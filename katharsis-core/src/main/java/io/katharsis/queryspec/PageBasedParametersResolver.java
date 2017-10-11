package io.katharsis.queryspec;

/**
 * An alternative implementation of the default {@link PagingParametersResolver} that uses the names
 * "number" and "size" for the offset and limit parameter names respectively.
 *
 * @author Casey Link
 *         created on 2017-03-10
 */
public class PageBasedParametersResolver extends PagingParametersResolver {

	public PageBasedParametersResolver() {
		setOffsetParameterName("number");
		setLimitParameterName("size");
	}
}
