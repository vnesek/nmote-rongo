package com.nmote.rongo;

import java.util.Objects;

import com.mongodb.async.client.MongoIterable;

import reactor.core.publisher.Mono;

/**
 * Operations that allow asynchronous iteration over a collection view.
 *
 * @param <T>
 *            the result type
 * @param <K>
 *            iterable subtype
 */
public class RongoIterable<T, K extends MongoIterable<T>> {

    public RongoIterable(K iterable) {
        Objects.requireNonNull(this);
        this.iterable = iterable;
    }

    /**
     * Sets the number of documents to return per batch.
     *
     * @param batchSize
     *            the batch size
     * @return this
     * @mongodb.driver.manual reference/method/cursor.batchSize/#cursor.batchSize
     *                        Batch Size
     */
    public RongoIterable<T, K> batchSize(int batchSize) {
        // TODO
        return this;
    }

    /**
     * Helper to return the first item in the iterator or null.
     *
     * @param callback
     *            a callback that is passed the first item or null.
     */
    public Mono<T> first() {
        return Rongo.mono(iterable::first);
    }

    public K getIterable() {
        return iterable;
    }

    protected final K iterable;

}
