package com.nmote.rongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.mongodb.async.AsyncBatchCursor;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class Rongo {

    private static class CursorEmitter<T> implements Consumer<FluxSink<T>> {

        public CursorEmitter(AsyncBatchCursor<T> cursor) {
            Objects.requireNonNull(cursor);
            this.cursor = cursor;
        }

        @Override
        public void accept(FluxSink<T> emitter) {
            synchronized (this) {
                if (this.emitter == null) {
                    this.emitter = emitter;
                    emitter.setCancellation(cursor::close);
                    nextBatch();
                } else {
                    emitter.complete();
                }
            }
        }

        private void nextBatch() {
            if (emitter.isCancelled()) {
                // System.out.println("cancel");
                return;
            }
            long requested = emitter.requestedFromDownstream();
            int rate = (int) Math.max(Math.min(requested, 10000), 50);
            // System.out.println("next " + requested + " rate " + rate);
            cursor.setBatchSize(rate);
            cursor.next((x, t) -> {
                if (t != null) {
                    emitter.error(t);
                } else if (x != null) {
                    x.forEach(emitter::next);
                    // TODO Adjust batch size based on
                    // emitter::requestedFromDownstream
                    nextBatch();
                } else {
                    emitter.complete();
                }
            });
        }

        private final AsyncBatchCursor<T> cursor;

        private FluxSink<T> emitter;

    }

    private static class MonoCallbackSink<T> implements SingleResultCallback<T>, Consumer<MonoSink<T>> {

        public MonoCallbackSink(Consumer<SingleResultCallback<T>> action) {
            this.action = action;
        }

        @Override
        public void accept(MonoSink<T> sink) {
            this.sink = sink;
            action.accept(this);
        }

        @Override
        public void onResult(T result, Throwable t) {
            if (t != null) {
                sink.error(t);
            } else {
                sink.success(result);
            }
        }

        private MonoSink<T> sink;
        private final Consumer<SingleResultCallback<T>> action;
    }

    private static class MonoCallbackVoid implements SingleResultCallback<Void>, Consumer<MonoSink<Boolean>> {

        public MonoCallbackVoid(Consumer<SingleResultCallback<Void>> action) {
            this.action = action;
        }

        @Override
        public void accept(MonoSink<Boolean> sink) {
            this.sink = sink;
            action.accept(this);
        }

        @Override
        public void onResult(Void result, Throwable t) {
            if (t != null) {
                sink.error(t);
            } else {
                sink.success(Boolean.TRUE);
            }
        }

        private MonoSink<Boolean> sink;
        private final Consumer<SingleResultCallback<Void>> action;
    }

    public static Document bind(Map<String, Object> template, Map<String, Object> parameters) {
        Document result = new Document();
        bindInternal(template, parameters, result);
        return result;
    }

    public static Document bind(Map<String, Object> template, Object... args) {
        Map<String, Object> parameters = new HashMap<String, Object>(args.length);
        for (int i = args.length; i > 0; --i) {
            parameters.put(Integer.toString(i), args[i - 1]);
        }
        Document result = new Document();
        bindInternal(template, parameters, result);
        return result;
    }

    public static Document bind(String doc, Map<String, Object> parameters) {
        return bind(Document.parse(doc), parameters);
    }

    public static Document bind(String doc, Object... parameters) {
        Document result =  bind(Document.parse(doc), parameters);
        return result;
    }

    public static CodecRegistry codecRegistry(CodecRegistry... registry) {
        return codecRegistry(null, registry);
    }

    public static CodecRegistry codecRegistry(Consumer<ObjectMapper> mapperModifier, CodecRegistry... registry) {
        List<CodecRegistry> regs = new ArrayList<>();
        if (registry.length == 0) {
            regs.add(MongoClients.getDefaultCodecRegistry());
        } else {
            for (CodecRegistry reg : registry) {
                regs.add(reg);
            }
        }
        regs.add(codecRegistry(createObjectMapper(mapperModifier)));
        return CodecRegistries.fromRegistries(regs);
    }

    public static CodecRegistry codecRegistry(ObjectMapper mapper) {
        return CodecRegistries.fromProviders(new JacksonCodecProvider(mapper));
    }

    public static ObjectMapper createObjectMapper(Consumer<ObjectMapper> mapperModifier) {
        ObjectMapper mapper = new ObjectMapper(new RongoBsonFactory());
        mapper.registerModule(new RongoBsonModule());

        // Setup introspector to handle @MongoId and @MongoObjectId
        AnnotationIntrospector jongoIntrospector = new RongoAnnotationIntrospector();
        AnnotationIntrospector defaultIntrospector = mapper.getSerializationConfig().getAnnotationIntrospector();
        AnnotationIntrospector pair = new AnnotationIntrospectorPair(jongoIntrospector, defaultIntrospector);
        mapper.setAnnotationIntrospector(pair);

        if (mapperModifier != null) {
            mapperModifier.accept(mapper);
        }
        return mapper;
    }

    public static <T> Flux<T> flux(AsyncBatchCursor<T> cursor) {
        return Flux.create(new CursorEmitter<>(cursor));
    }

    public static <T> Flux<T> flux(FindIterable<T> iterable) {
        return mono(iterable::batchCursor).flatMap(Rongo::flux);
    }

    public static <T> RongoCollection<T> from(MongoCollection<T> instances) {
        return new RongoCollection<>(instances);
    }

    public static <T> Mono<T> mono(Consumer<SingleResultCallback<T>> action) {
        return Mono.create(new MonoCallbackSink<>(action));
    }

    public static Mono<Boolean> monoVoid(Consumer<SingleResultCallback<Void>> action) {
        return Mono.create(new MonoCallbackVoid(action));
    }

    private static void bindInternal(Map<String, Object> template, Map<String, Object> parameters,
            Map<String, Object> result) {
        for (Map.Entry<String, Object> e : template.entrySet()) {
            result.put(e.getKey(), bindInternal(e.getValue(), parameters));
        }
    }

    @SuppressWarnings("unchecked")
    private static Object bindInternal(Object value, Map<String, Object> parameters) {
        Object result;
        if (value != null) {
            if (value instanceof String) {
                result = bindInternal((String) value, parameters);
            } else if (value instanceof Map) {
                Map<String, Object> map = new LinkedHashMap<>();
                bindInternal((Map<String, Object>) value, parameters, map);
                result = map;
            } else if (value instanceof Iterable) {
                List<Object> list = new ArrayList<>();
                for (Object value2 : (Iterable<Object>) value) {
                    list.add(bindInternal(value2, parameters));
                }
                result = list;
            } else {
                result = value;
            }
        } else {
            result = null;
        }
        return result;
    }

    private static Object bindInternal(String value, Map<String, Object> parameters) {
        Object result;
        if (value.length() > 1 && value.charAt(0) == ':') {
            String key = value.substring(1);
            Object bindValue = parameters.get(key);
            if (bindValue == null && !parameters.containsKey(key)) {
                throw new IllegalStateException(String.format("parameter :%s not bound", key));
            }
            result = bindValue;
        } else {
            result = value;
        }
        return result;
    }
}
