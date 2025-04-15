/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.graphql.servlet.v1_0;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.search.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.portal.search.rest.internal.graphql.query.v1_0.Query;
import com.liferay.portal.search.rest.internal.resource.v1_0.EmbeddingModelResourceImpl;
import com.liferay.portal.search.rest.internal.resource.v1_0.EmbeddingProviderValidationResultResourceImpl;
import com.liferay.portal.search.rest.internal.resource.v1_0.SearchResultResourceImpl;
import com.liferay.portal.search.rest.internal.resource.v1_0.SuggestionResourceImpl;
import com.liferay.portal.search.rest.resource.v1_0.EmbeddingModelResource;
import com.liferay.portal.search.rest.resource.v1_0.EmbeddingProviderValidationResultResource;
import com.liferay.portal.search.rest.resource.v1_0.SearchResultResource;
import com.liferay.portal.search.rest.resource.v1_0.SuggestionResource;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.
			setEmbeddingProviderValidationResultResourceComponentServiceObjects(
				_embeddingProviderValidationResultResourceComponentServiceObjects);
		Mutation.setSearchResultResourceComponentServiceObjects(
			_searchResultResourceComponentServiceObjects);
		Mutation.setSuggestionResourceComponentServiceObjects(
			_suggestionResourceComponentServiceObjects);

		Query.setEmbeddingModelResourceComponentServiceObjects(
			_embeddingModelResourceComponentServiceObjects);
		Query.setSearchResultResourceComponentServiceObjects(
			_searchResultResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Portal.Search.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/search-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#createEmbeddingValidateProviderConfiguration",
						new ObjectValuePair<>(
							EmbeddingProviderValidationResultResourceImpl.class,
							"postEmbeddingValidateProviderConfiguration"));
					put(
						"mutation#createSearchPage",
						new ObjectValuePair<>(
							SearchResultResourceImpl.class, "postSearchPage"));
					put(
						"mutation#createSuggestionsPage",
						new ObjectValuePair<>(
							SuggestionResourceImpl.class,
							"postSuggestionsPage"));

					put(
						"query#embeddingEmbeddingModels",
						new ObjectValuePair<>(
							EmbeddingModelResourceImpl.class,
							"getEmbeddingEmbeddingModelsPage"));
					put(
						"query#search",
						new ObjectValuePair<>(
							SearchResultResourceImpl.class, "getSearchPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<EmbeddingProviderValidationResultResource>
		_embeddingProviderValidationResultResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SearchResultResource>
		_searchResultResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SuggestionResource>
		_suggestionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<EmbeddingModelResource>
		_embeddingModelResourceComponentServiceObjects;

}