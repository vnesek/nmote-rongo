package com.nmote.rongo;

import java.util.List;
import java.util.Objects;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.MongoNamespace;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import reactor.core.publisher.Mono;

public class RongoCollection<T> {

    public RongoCollection(MongoCollection<T> collection) {
        Objects.requireNonNull(collection);
        this.collection = collection;
    }

    public Mono<Long> count() {
        return Rongo.mono(collection::count);
    }

    public Mono<Long> count(Bson filter) {
        return Rongo.mono(c -> collection.count(filter, c));
    }

    public Mono<Long> count(CountOptions options, Bson filter) {
        return Rongo.mono(c -> collection.count(filter, options, c));
    }

    public Mono<DeleteResult> deleteMany(Bson filter) {
        return Rongo.mono(c -> collection.deleteMany(filter, c));
    }

    public Mono<DeleteResult> deleteMany(Bson filter, DeleteOptions options) {
        return Rongo.mono(c -> collection.deleteMany(filter, options, c));
    }

    public Mono<DeleteResult> deleteOne(Bson filter) {
        return Rongo.mono(c -> collection.deleteOne(filter, c));
    }

    public Mono<DeleteResult> deleteOne(Bson filter, DeleteOptions options) {
        return Rongo.mono(c -> collection.deleteOne(filter, options, c));
    }

    public RongoFindIterable<T> find() {
        return new RongoFindIterable<>(collection.find());
    }

    public RongoFindIterable<T> find(Bson filter) {
        return new RongoFindIterable<>(collection.find(filter));
    }

    public Mono<T> findOneAndDelete(Bson filter) {
        return Rongo.mono(c -> collection.findOneAndDelete(filter, c));
    }

    public Mono<T> findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
        return Rongo.mono(c -> collection.findOneAndDelete(filter, options, c));
    }

    public Mono<T> findOneAndReplace(Bson filter, T replacement) {
        return Rongo.mono(c -> collection.findOneAndReplace(filter, replacement, c));
    }

    public Mono<T> findOneAndReplace(Bson filter, T replacement, FindOneAndReplaceOptions options) {
        return Rongo.mono(c -> collection.findOneAndReplace(filter, replacement, options, c));
    }

    public Mono<T> findOneAndUpdate(Bson filter, Bson update) {
        return Rongo.mono(c -> collection.findOneAndUpdate(filter, update, c));
    }

    public Mono<T> findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
        return Rongo.mono(c -> collection.findOneAndUpdate(filter, update, options, c));
    }

    public CodecRegistry getCodecRegistry() {
        return collection.getCodecRegistry();
    }

    public MongoCollection<T> getCollection() {
        return collection;
    }

    public Class<T> getDocumentClass() {
        return collection.getDocumentClass();
    }

    public MongoNamespace getNamespace() {
        return collection.getNamespace();
    }

    public ReadConcern getReadConcern() {
        return collection.getReadConcern();
    }

    public ReadPreference getReadPreference() {
        return collection.getReadPreference();
    }

    public WriteConcern getWriteConcern() {
        return collection.getWriteConcern();
    }

    public Mono<Void> insertMany(InsertManyOptions options, List<T> documents) {
        return Rongo.mono(c -> collection.insertMany(documents, options, c));
    }

    public Mono<Void> insertMany(List<T> documents) {
        return Rongo.mono(c -> collection.insertMany(documents, c));
    }

    public Mono<Void> insertOne(InsertOneOptions options, T document) {
        return Rongo.mono(c -> collection.insertOne(document, options, c));
    }

    public Mono<Void> insertOne(T document) {
        return Rongo.mono(c -> collection.insertOne(document, c));
    }

    public Mono<UpdateResult> replaceOne(Bson filter, T replacement) {
        return Rongo.mono(c -> collection.replaceOne(filter, replacement, c));
    }

    public Mono<UpdateResult> replaceOne(Bson filter, T replacement, UpdateOptions options) {
        return Rongo.mono(c -> collection.replaceOne(filter, replacement, options, c));
    }

    public Mono<UpdateResult> updateMany(Bson filter, Bson update) {
        return Rongo.mono(c -> collection.updateMany(filter, update, c));
    }

    public Mono<UpdateResult> updateMany(Bson filter, Bson update, UpdateOptions options) {
        return Rongo.mono(c -> collection.updateMany(filter, update, options, c));
    }

    public Mono<UpdateResult> updateOne(Bson filter, Bson update) {
        return Rongo.mono(c -> collection.updateOne(filter, update, c));
    }

    public Mono<UpdateResult> updateOne(Bson filter, Bson update, UpdateOptions options) {
        return Rongo.mono(c -> collection.updateOne(filter, update, options, c));
    }

    public RongoCollection<T> withJacksonCodecRegistry() {
        return withCodecRegistry(Rongo.codecRegistry(collection.getCodecRegistry()));
    }

    public RongoCollection<T> withCodecRegistry(CodecRegistry codecRegistry) {
        return Rongo.from(collection.withCodecRegistry(codecRegistry));
    }

    public <S> RongoCollection<S> withDocumentClass(Class<S> newDocumentClass) {
        return Rongo.from(collection.withDocumentClass(newDocumentClass));
    }

    public RongoCollection<T> withReadConcern(ReadConcern readConcern) {
        return Rongo.from(collection.withReadConcern(readConcern));
    }

    public RongoCollection<T> withReadPreference(ReadPreference readPreference) {
        return Rongo.from(collection.withReadPreference(readPreference));
    }

    public RongoCollection<T> withWriteConcern(WriteConcern writeConcern) {
        return Rongo.from(collection.withWriteConcern(writeConcern));
    }

    private final MongoCollection<T> collection;
}
