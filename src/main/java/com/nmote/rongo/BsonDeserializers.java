/*
 * Copyright (C) 2011 Benoit GUEROUT <bguerout at gmail dot com> and Yves AMSELLEM <amsellem dot yves at gmail dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmote.rongo;

import java.io.IOException;
import java.util.Date;

import org.bson.types.Binary;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

class BsonDeserializers extends SimpleDeserializers {

    private static final long serialVersionUID = 1L;

    public BsonDeserializers() {
        //addDeserializer(Bson.class, new BsonDeserializer());
        addDeserializer(Date.class, new DateDeserializer());
        addDeserializer(MinKey.class, new MinKeyDeserializer());
        addDeserializer(MaxKey.class, new MaxKeyDeserializer());
        addDeserializer(Binary.class, new BinaryDeserializer());
        //addDeserializer(DBObject.class, new NativeDeserializer<DBObject>());
        addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        //addDeserializer(BSONTimestamp.class, new BSONTimestampDeserializer());
    }

    private static class DateDeserializer extends JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            Object deserialized = jp.getEmbeddedObject();
            if (deserialized instanceof Long) {
                return getDateFromBackwardFormat((Long) deserialized);
            }
            return (Date) deserialized;
        }

        private Date getDateFromBackwardFormat(Long deserialized) {
            return new Date(deserialized);
        }
    }

    private static class MinKeyDeserializer extends JsonDeserializer<MinKey> {
        @Override
        public MinKey deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            TreeNode tree = jp.getCodec().readTree(jp);
            if (tree.isObject()) {
                int value = ((ValueNode) tree.get("$minKey")).asInt();
                if (value == 1) {
                    return new MinKey();
                }
            } else if (tree instanceof POJONode) {
                return (MinKey) ((POJONode) tree).getPojo();
            } else if (tree instanceof TextNode) {
                return new MinKey();
            }
            return (MinKey) ctxt.handleUnexpectedToken(MinKey.class, jp);
        }
    }

    private static class MaxKeyDeserializer extends JsonDeserializer<MaxKey> {
        @Override
        public MaxKey deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            TreeNode tree = jp.getCodec().readTree(jp);
            if (tree.isObject()) {
                int value = ((ValueNode) tree.get("$maxKey")).asInt();
                if (value == 1) {
                    return new MaxKey();
                }
            } else if (tree instanceof POJONode) {
                return (MaxKey) ((POJONode) tree).getPojo();
            } else if (tree instanceof TextNode) {
                return new MaxKey();
            }
            return (MaxKey) ctxt.handleUnexpectedToken(MaxKey.class, jp);
        }
    }

    /*
    private static class NativeDeserializer<T> extends JsonDeserializer<T> {
        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String asString = jp.readValueAsTree().toString();
            return (T) JSON.parse(asString);
        }
    }
    */

    /*
    private static class BsonDeserializer extends JsonDeserializer<Bson> {
        @Override
        public Bson deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            Map<?, ?> map = jp.readValueAs(Map.class);
            return new BasicDBObject(map);
        }
    }
    */

    private static class BinaryDeserializer extends JsonDeserializer<Binary> {
        @Override
        public Binary deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            TreeNode tree = jp.getCodec().readTree(jp);
            if (tree.isObject()) {
                byte[] binary = Base64Variants.MIME_NO_LINEFEEDS.decode(((ValueNode) tree.get("$binary")).asText());
                byte type = Integer.valueOf(((ValueNode) tree.get("$type")).asText().toLowerCase(), 16).byteValue();
                return new Binary(type, binary);
            } else if (tree instanceof POJONode) {
                return (Binary) ((POJONode) tree).getPojo();
            } else if (tree instanceof BinaryNode) {
                return new Binary(((BinaryNode) tree).binaryValue());
            }
            return (Binary) ctxt.handleUnexpectedToken(Binary.class, jp);
        }
    }

    private static class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

        @Override
        public ObjectId deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            TreeNode tree = jp.getCodec().readTree(jp);
            if (tree.isObject()) {
                String hexString = ((ValueNode) tree.get("$oid")).asText();
                return new ObjectId(hexString);
            } else if (tree instanceof POJONode) {
                return (ObjectId) ((POJONode) tree).getPojo();
            }
            return (ObjectId) ctxt.handleUnexpectedToken(ObjectId.class, jp);
        }

    }

    /*
    private static class BSONTimestampDeserializer extends JsonDeserializer<BSONTimestamp> {
        @Override
        public BSONTimestamp deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            TreeNode tree = jp.getCodec().readTree(jp);
            if (tree.isObject()) {
                TreeNode timestamp = tree.get("$timestamp");
                int time = ((ValueNode) timestamp.get("t")).asInt();
                int inc = ((ValueNode) timestamp.get("i")).asInt();
                return new BSONTimestamp(time, inc);
            } else if (tree instanceof POJONode) {
                return (BSONTimestamp) ((POJONode) tree).getPojo();
            }
            return (BSONTimestamp) ctxt.handleUnexpectedToken(BSONTimestamp.class, jp);
        }
    }
    */
}
