package io.katharsis.core.internal.repository.adapter;

import io.katharsis.core.internal.utils.JsonApiUrlBuilder;
import io.katharsis.module.ModuleRegistry;
import io.katharsis.repository.request.QueryAdapter;
import io.katharsis.repository.request.RepositoryRequestSpec;
import io.katharsis.resource.information.ResourceField;
import io.katharsis.resource.information.ResourceInformation;

/**
 * @author Casey Link
 *         created on 2017-03-10
 */
public class DefaultLinksSerializer implements LinksSerializer {

	private final RepositoryRequestSpec requestSpec;
	private final ModuleRegistry moduleRegistry;

	public DefaultLinksSerializer(RepositoryRequestSpec requestSpec, ModuleRegistry moduleRegistry) {
		this.requestSpec = requestSpec;
		this.moduleRegistry = moduleRegistry;
	}

	@Override
	public String toUrl(QueryAdapter queryAdapter) {
		JsonApiUrlBuilder urlBuilder = new JsonApiUrlBuilder(moduleRegistry.getResourceRegistry());
		Object relationshipSourceId = requestSpec.getId();
		ResourceField relationshipField = requestSpec.getRelationshipField();
		ResourceInformation rootInfo;
		if (relationshipField == null) {
			rootInfo = queryAdapter.getResourceInformation();
		} else {
			rootInfo = relationshipField.getParentResourceInformation();
		}
		return urlBuilder.buildUrl(rootInfo, relationshipSourceId, queryAdapter, relationshipField != null ? relationshipField.getJsonName() : null);
	}
}
