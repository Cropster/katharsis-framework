package io.katharsis.core.internal.repository.adapter;

import io.katharsis.repository.request.QueryAdapter;

/**
 * @author Casey Link
 *         created on 2017-03-10
 */
public interface LinksSerializer {

	public String toUrl(QueryAdapter queryAdapter);
}
