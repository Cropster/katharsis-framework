package io.katharsis.queryspec;

import java.util.Map;

import io.katharsis.core.internal.repository.adapter.LinksSerializer;
import io.katharsis.core.internal.utils.StringUtils;
import io.katharsis.errorhandling.exception.BadRequestException;
import io.katharsis.repository.request.QueryAdapter;
import io.katharsis.resource.links.PagedLinksInformation;

/**
 * Extracts paging parameters from the http request. This allows you to configure and customize
 * the pagination parameters on your API.
 * <p>
 * The default configuration is {@link #DEFAULT_OFFSET_PARAMETER} and {@link #DEFAULT_LIMIT_PARAMETER}.
 * <p>
 * This class is based off of the the spring-data-commons PageableHandlerMethodArgumentResolver class which is
 * Apache 2.0 licensed, just like Katharsis (https://github.com/spring-projects/spring-data-commons)
 *
 * @author Casey Link
 *         created on 2017-03-10
 */
public class PagingParametersResolver {

	private static final String DEFAULT_OFFSET_PARAMETER = "offset";
	private static final String DEFAULT_LIMIT_PARAMETER = "limit";
	private static final long DEFAULT_MAX_LIMIT = 2000;

	private String offsetParameterName = DEFAULT_OFFSET_PARAMETER;
	private String limitParameterName = DEFAULT_LIMIT_PARAMETER;
	private long maxLimit = DEFAULT_MAX_LIMIT;
	private boolean oneIndexedParameters = false;

	public PagingParametersResolver() {
	}

	/**
	 * Configures whether to expose and assume 1-based page number indexes in the request parameters. Defaults to
	 * {@literal false}, meaning a page number of 0 in the request equals the first page. If this is set to
	 * {@literal true}, a page number of 1 in the request will be considered the first page.
	 *
	 * @param oneIndexedParameters
	 * 		the oneIndexedParameters to set
	 */
	public void setOneIndexedParameters(boolean oneIndexedParameters) {
		this.oneIndexedParameters = oneIndexedParameters;
	}

	/**
	 * Indicates whether to expose and assume 1-based page number indexes in the request parameters. Defaults to
	 * {@literal false}, meaning a page number of 0 in the request equals the first page. If this is set to
	 * {@literal true}, a page number of 1 in the request will be considered the first page.
	 *
	 * @return whether to assume 1-based page number indexes in the request parameters.
	 */
	protected boolean isOneIndexedParameters() {
		return this.oneIndexedParameters;
	}

	/**
	 * Configures the maximum limit to be accepted. This allows to put an upper boundary of the limit to prevent
	 * potential attacks trying to issue an {@link OutOfMemoryError}. Defaults to {@link #DEFAULT_MAX_LIMIT}.
	 *
	 * @param maxLimit
	 * 		the maxLimit to set
	 */
	public void setMaxLimit(int maxLimit) {
		this.maxLimit = maxLimit;
	}

	/**
	 * Retrieves the maximum limit to be accepted. This allows to put an upper boundary of the limit to prevent
	 * potential attacks trying to issue an {@link OutOfMemoryError}. Defaults to {@link #DEFAULT_MAX_LIMIT}.
	 *
	 * @return the maximum limit allowed.
	 */
	protected long getMaxLimit() {
		return this.maxLimit;
	}

	/**
	 * Configures the parameter name to be used to find the offset in the request. Defaults to {@code offset}.
	 *
	 * @param offsetParameterName
	 * 		the parameter name to be used, must not be {@literal null} or empty.
	 */
	public void setOffsetParameterName(String offsetParameterName) {
		if (StringUtils.isBlank(offsetParameterName)) {
			throw new IllegalArgumentException("Offset parameter name must not be null or empty!");
		}
		this.offsetParameterName = offsetParameterName;
	}

	/**
	 * Retrieves the parameter name to be used to find the offset in the request. Defaults to {@code offset}.
	 *
	 * @return the parameter name to be used, never {@literal null} or empty.
	 */
	protected String getOffsetParameterName() {
		return this.offsetParameterName;
	}

	/**
	 * Retrieves the parameter name to be used to find the limit in the request. Defaults to {@code limit}.
	 *
	 * @return the parameter name to be used, never {@literal null} or empty.
	 */
	public String getLimitParameterName() {
		return limitParameterName;
	}

	/**
	 * Configures the parameter name to be used to find the limit in the request. Defaults to {@code limit}.
	 *
	 * @param limitParameterName
	 * 		the parameter name to be used, must not be {@literal null} or empty.
	 */
	public void setLimitParameterName(String limitParameterName) {
		if (StringUtils.isBlank(offsetParameterName)) {
			throw new IllegalArgumentException("Limit parameter name must not be null or empty!");
		}
		this.limitParameterName = limitParameterName;
	}

	protected PagingSpec resolvePageParameters(Map<String, Long> pageParams) {
		Long limit;
		long offset = 0L;
		for (Map.Entry<String, Long> paramValue : pageParams.entrySet()) {
			if (getLimitParameterName().equalsIgnoreCase(paramValue.getKey())) {
				limit = paramValue.getValue();
				if (limit != null && limit > getMaxLimit()) {
					String error = String.format("%s parameter value %d is larger than the maximum allowed of " + "of" +
							" %d", getLimitParameterName(), limit, getMaxLimit());
					throw new BadRequestException(error);
				}
			} else if (getOffsetParameterName().equalsIgnoreCase(paramValue.getKey())) {
				offset = paramValue.getValue();
			}
		}
		return null;
	}

	protected PagedLinksInformation enrich(LinksSerializer linksSerializer,
	                                       PagedLinksInformation pagedLinksInformation,
	                                       QueryAdapter queryAdapter,
	                                       long total) {
		long pageSize = queryAdapter.getLimit().longValue();
		long offset = queryAdapter.getOffset();

		long currentPage = offset / pageSize;
		if (currentPage * pageSize != offset) {
			throw new IllegalArgumentException("offset " + offset + " is not a multiple of limit " + pageSize);
		}
		long totalPages = (total + pageSize - 1) / pageSize;

		QueryAdapter pageSpec = queryAdapter.duplicate();
		pageSpec.setLimit(pageSize);

		if (totalPages > 0) {
			pageSpec.setOffset(0);
			pagedLinksInformation.setFirst(linksSerializer.toUrl(pageSpec));

			pageSpec.setOffset((totalPages - 1) * pageSize);
			pagedLinksInformation.setLast(linksSerializer.toUrl(pageSpec));

			if (currentPage > 0) {
				pageSpec.setOffset((currentPage - 1) * pageSize);
				pagedLinksInformation.setPrev(linksSerializer.toUrl(pageSpec));
			}

			if (currentPage < totalPages - 1) {
				pageSpec.setOffset((currentPage + 1) * pageSize);
				pagedLinksInformation.setNext(linksSerializer.toUrl(pageSpec));
			}
		}

		return pagedLinksInformation;
	}
}
