/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.graphql.mutation.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderValidationResult;
import com.liferay.portal.search.rest.dto.v1_0.SearchRequestBody;
import com.liferay.portal.search.rest.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorConfiguration;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorResults;
import com.liferay.portal.search.rest.resource.v1_0.EmbeddingProviderValidationResultResource;
import com.liferay.portal.search.rest.resource.v1_0.SearchResultResource;
import com.liferay.portal.search.rest.resource.v1_0.SuggestionResource;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class Mutation {

	public static void
		setEmbeddingProviderValidationResultResourceComponentServiceObjects(
			ComponentServiceObjects<EmbeddingProviderValidationResultResource>
				embeddingProviderValidationResultResourceComponentServiceObjects) {

		_embeddingProviderValidationResultResourceComponentServiceObjects =
			embeddingProviderValidationResultResourceComponentServiceObjects;
	}

	public static void setSearchResultResourceComponentServiceObjects(
		ComponentServiceObjects<SearchResultResource>
			searchResultResourceComponentServiceObjects) {

		_searchResultResourceComponentServiceObjects =
			searchResultResourceComponentServiceObjects;
	}

	public static void setSuggestionResourceComponentServiceObjects(
		ComponentServiceObjects<SuggestionResource>
			suggestionResourceComponentServiceObjects) {

		_suggestionResourceComponentServiceObjects =
			suggestionResourceComponentServiceObjects;
	}

	@GraphQLField
	public EmbeddingProviderValidationResult
			createEmbeddingValidateProviderConfiguration(
				@GraphQLName("embeddingProviderConfiguration")
					EmbeddingProviderConfiguration
						embeddingProviderConfiguration)
		throws Exception {

		return _applyComponentServiceObjects(
			_embeddingProviderValidationResultResourceComponentServiceObjects,
			this::_populateResourceContext,
			embeddingProviderValidationResultResource ->
				embeddingProviderValidationResultResource.
					postEmbeddingValidateProviderConfiguration(
						embeddingProviderConfiguration));
	}

	@GraphQLField(
		description = "Search the company index for matching content. This endpoint requires setting the portal property 'feature.flag.LPS-179669' to true or enabling via Instance Settings > Feature Flags: Release."
	)
	public java.util.Collection<SearchResult> createSearchPage(
			@GraphQLName("entryClassNames") String entryClassNames,
			@GraphQLName("scope") String scope,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("searchRequestBody") SearchRequestBody
				searchRequestBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_searchResultResourceComponentServiceObjects,
			this::_populateResourceContext,
			searchResultResource -> {
				Page paginationPage = searchResultResource.postSearchPage(
					entryClassNames, scope, search,
					_filterBiFunction.apply(searchResultResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(searchResultResource, sortsString),
					searchRequestBody);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public java.util.Collection<SuggestionsContributorResults>
			createSuggestionsPage(
				@GraphQLName("currentURL") String currentURL,
				@GraphQLName("destinationFriendlyURL") String
					destinationFriendlyURL,
				@GraphQLName("groupId") Long groupId,
				@GraphQLName("keywordsParameterName") String
					keywordsParameterName,
				@GraphQLName("plid") Long plid,
				@GraphQLName("scope") String scope,
				@GraphQLName("search") String search,
				@GraphQLName("suggestionsContributorConfigurations")
					SuggestionsContributorConfiguration[]
						suggestionsContributorConfigurations)
		throws Exception {

		return _applyComponentServiceObjects(
			_suggestionResourceComponentServiceObjects,
			this::_populateResourceContext,
			suggestionResource -> {
				Page paginationPage = suggestionResource.postSuggestionsPage(
					currentURL, destinationFriendlyURL, groupId,
					keywordsParameterName, plid, scope, search,
					suggestionsContributorConfigurations);

				return paginationPage.getItems();
			});
	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			EmbeddingProviderValidationResultResource
				embeddingProviderValidationResultResource)
		throws Exception {

		embeddingProviderValidationResultResource.setContextAcceptLanguage(
			_acceptLanguage);
		embeddingProviderValidationResultResource.setContextCompany(_company);
		embeddingProviderValidationResultResource.setContextHttpServletRequest(
			_httpServletRequest);
		embeddingProviderValidationResultResource.setContextHttpServletResponse(
			_httpServletResponse);
		embeddingProviderValidationResultResource.setContextUriInfo(_uriInfo);
		embeddingProviderValidationResultResource.setContextUser(_user);
		embeddingProviderValidationResultResource.setGroupLocalService(
			_groupLocalService);
		embeddingProviderValidationResultResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			SearchResultResource searchResultResource)
		throws Exception {

		searchResultResource.setContextAcceptLanguage(_acceptLanguage);
		searchResultResource.setContextCompany(_company);
		searchResultResource.setContextHttpServletRequest(_httpServletRequest);
		searchResultResource.setContextHttpServletResponse(
			_httpServletResponse);
		searchResultResource.setContextUriInfo(_uriInfo);
		searchResultResource.setContextUser(_user);
		searchResultResource.setGroupLocalService(_groupLocalService);
		searchResultResource.setRoleLocalService(_roleLocalService);

		searchResultResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		searchResultResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(SuggestionResource suggestionResource)
		throws Exception {

		suggestionResource.setContextAcceptLanguage(_acceptLanguage);
		suggestionResource.setContextCompany(_company);
		suggestionResource.setContextHttpServletRequest(_httpServletRequest);
		suggestionResource.setContextHttpServletResponse(_httpServletResponse);
		suggestionResource.setContextUriInfo(_uriInfo);
		suggestionResource.setContextUser(_user);
		suggestionResource.setGroupLocalService(_groupLocalService);
		suggestionResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects
		<EmbeddingProviderValidationResultResource>
			_embeddingProviderValidationResultResourceComponentServiceObjects;
	private static ComponentServiceObjects<SearchResultResource>
		_searchResultResourceComponentServiceObjects;
	private static ComponentServiceObjects<SuggestionResource>
		_suggestionResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}