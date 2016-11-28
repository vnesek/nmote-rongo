package com.nmote.rongo;

import java.util.concurrent.TimeUnit;

import org.bson.conversions.Bson;

import com.mongodb.CursorType;
import com.mongodb.async.client.FindIterable;
import com.mongodb.client.model.Collation;

import reactor.core.publisher.Flux;

/**
 * Iterable for find.
 *
 * @param <T>
 *            The type of the result.
 */
public class RongoFindIterable<T> extends RongoIterable<T, FindIterable<T>> {

    public RongoFindIterable(FindIterable<T> iterable) {
        super(iterable);
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
    public RongoFindIterable<T> batchSize(int batchSize) {
        super.batchSize(batchSize);
        return this;
    }

    /**
     * Sets the collation options
     *
     * <p>
     * A null value represents the server default.
     * </p>
     *
     * @param collation
     *            the collation options to use
     * @return this
     * @since 3.4
     * @mongodb.server.release 3.4
     */
    public RongoFindIterable<T> collation(Collation collation) {
        iterable.collation(collation);
        return this;
    }

    /**
     * Sets the cursor type.
     *
     * @param cursorType
     *            the cursor type
     * @return this
     */
    public RongoFindIterable<T> cursorType(CursorType cursorType) {
        iterable.cursorType(cursorType);
        return this;
    }

    /**
     * Sets the query filter to apply to the query.
     *
     * @param filter
     *            the filter, which may be null.
     * @return this
     * @mongodb.driver.manual reference/method/db.collection.find/ Filter
     */
    public RongoFindIterable<T> filter(Bson filter) {
        iterable.filter(filter);
        return this;
    }

    public Flux<T> flux() {
        return Rongo.flux(iterable);
    }

    /**
     * Sets the limit to apply.
     *
     * @param limit
     *            the limit, which may be null
     * @return this
     * @mongodb.driver.manual reference/method/cursor.limit/#cursor.limit Limit
     */
    public RongoFindIterable<T> limit(int limit) {
        iterable.limit(limit);
        return this;
    }

    /**
     * The maximum amount of time for the server to wait on new documents to
     * satisfy a tailable cursor query. This only applies to a TAILABLE_AWAIT
     * cursor. When the cursor is not a TAILABLE_AWAIT cursor, this option is
     * ignored.
     *
     * On servers &gt;= 3.2, this option will be specified on the getMore
     * command as "maxTimeMS". The default is no value: no "maxTimeMS" is sent
     * to the server with the getMore command.
     *
     * On servers &lt; 3.2, this option is ignored, and indicates that the
     * driver should respect the server's default value
     *
     * A zero value will be ignored.
     *
     * @param maxAwaitTime
     *            the max await time
     * @param timeUnit
     *            the time unit to return the result in
     * @return the maximum await execution time in the given time unit
     * @mongodb.driver.manual reference/method/cursor.maxTimeMS/#cursor.maxTimeMS
     *                        Max Time
     * @since 3.2
     */
    public RongoFindIterable<T> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
        iterable.maxAwaitTime(maxAwaitTime, timeUnit);
        return this;
    }

    /**
     * Sets the maximum execution time on the server for this operation.
     *
     * @param maxTime
     *            the max time
     * @param timeUnit
     *            the time unit, which may not be null
     * @return this
     * @mongodb.driver.manual reference/method/cursor.maxTimeMS/#cursor.maxTimeMS
     *                        Max Time
     */
    public RongoFindIterable<T> maxTime(long maxTime, TimeUnit timeUnit) {
        iterable.maxTime(maxTime, timeUnit);
        return this;
    }

    /**
     * Sets the query modifiers to apply to this operation.
     *
     * @param modifiers
     *            the query modifiers to apply, which may be null.
     * @return this
     * @mongodb.driver.manual reference/operator/query-modifier/ Query Modifiers
     */
    public RongoFindIterable<T> modifiers(Bson modifiers) {
        iterable.modifiers(modifiers);
        return this;
    }

    /**
     * The server normally times out idle cursors after an inactivity period (10
     * minutes) to prevent excess memory use. Set this option to prevent that.
     *
     * @param noCursorTimeout
     *            true if cursor timeout is disabled
     * @return this
     */
    public RongoFindIterable<T> noCursorTimeout(boolean noCursorTimeout) {
        iterable.noCursorTimeout(noCursorTimeout);
        return this;
    }

    /**
     * Users should not set this under normal circumstances.
     *
     * @param oplogReplay
     *            if oplog replay is enabled
     * @return this
     */
    public RongoFindIterable<T> oplogReplay(boolean oplogReplay) {
        iterable.oplogReplay(oplogReplay);
        return this;
    }

    /**
     * Get partial results from a sharded cluster if one or more shards are
     * unreachable (instead of throwing an error).
     *
     * @param partial
     *            if partial results for sharded clusters is enabled
     * @return this
     */
    public RongoFindIterable<T> partial(boolean partial) {
        iterable.partial(partial);
        return this;
    }

    /**
     * Sets a document describing the fields to return for all matching
     * documents.
     *
     * @param projection
     *            the project document, which may be null.
     * @return this
     * @mongodb.driver.manual reference/method/db.collection.find/ Projection
     */
    public RongoFindIterable<T> projection(Bson projection) {
        iterable.projection(projection);
        return this;
    }

    /**
     * Sets the number of documents to skip.
     *
     * @param skip
     *            the number of documents to skip
     * @return this
     * @mongodb.driver.manual reference/method/cursor.skip/#cursor.skip Skip
     */
    public RongoFindIterable<T> skip(int skip) {
        iterable.skip(skip);
        return this;
    }

    /**
     * Sets the sort criteria to apply to the query.
     *
     * @param sort
     *            the sort criteria, which may be null.
     * @return this
     * @mongodb.driver.manual reference/method/cursor.sort/ Sort
     */
    public RongoFindIterable<T> sort(Bson sort) {
        iterable.sort(sort);
        return this;
    }

}
