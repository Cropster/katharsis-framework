package io.katharsis.queryspec;

public class DefaultPagingSpec implements PagingSpec {

	private final long offset;
	private final long limit;
	private final boolean isZeroIndexed;

	public DefaultPagingSpec(long offset, long limit) {
		this(offset, limit, true);
	}

	public DefaultPagingSpec(long offset, long limit, boolean isZeroIndexed) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must not be less than zero!");
		}

		if (limit < 1) {
			throw new IllegalArgumentException("Limit must not be less than one!");
		}

		this.offset = offset;
		this.limit = limit;
		this.isZeroIndexed = isZeroIndexed;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public long getLimit() {
		return limit;
	}

	@Override
	public long getPageNumber() {
		if (offset < limit || limit == 0)
			return isZeroIndexed ? 0 : 1;

		long page = (long) Math.ceil((double) offset / (double) limit);
		if (isZeroIndexed) {
			return page;
		}

		return page + 1;
	}

	@Override
	public PagingSpec first() {
		return null;
	}

	@Override
	public PagingSpec previous() {
		return null;
	}

	@Override
	public PagingSpec next() {
		return null;
	}

	@Override
	public PagingSpec last() {
		return null;
	}

	@Override
	public boolean hasPrevious() {
		return offset > 0;
	}

	@Override
	public PagingSpec previousOrFirst() {
		return hasPrevious() ? previous() : first();
	}

	@Override
	public PagingSpec duplicate() {
		return new DefaultPagingSpec(offset, limit, isZeroIndexed);
	}

	@Override
	public PagingSpec withOffset(long offset) {
		return new DefaultPagingSpec(offset, this.limit, this.isZeroIndexed);
	}

	@Override
	public PagingSpec withLimit(long limit) {
		return new DefaultPagingSpec(this.offset, limit, this.isZeroIndexed);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultPagingSpec that = (DefaultPagingSpec) o;

		if (offset != that.offset) return false;
		if (limit != that.limit) return false;
		return isZeroIndexed == that.isZeroIndexed;
	}

	@Override
	public int hashCode() {
		int result = (int) (offset ^ (offset >>> 32));
		result = 31 * result + (int) (limit ^ (limit >>> 32));
		result = 31 * result + (isZeroIndexed ? 1 : 0);
		return result;
	}
}
