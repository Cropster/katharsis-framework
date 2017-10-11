package io.katharsis.queryspec;

/**
 * @author Casey Link
 *         created on 2017-03-10
 */
public interface PagingSpec {

	long getOffset();

	long getLimit();

	long getPageNumber();

	PagingSpec first();

	PagingSpec previous();

	PagingSpec next();

	PagingSpec last();

	boolean hasPrevious();

	PagingSpec previousOrFirst();

	PagingSpec duplicate();

	PagingSpec withOffset(long offset);

	PagingSpec withLimit(long limit);
}
