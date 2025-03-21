/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.internal.graphql.query.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingModel;
import com.liferay.portal.search.rest.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.resource.v1_0.EmbeddingModelResource;
import com.liferay.portal.search.rest.resource.v1_0.SearchResultResource;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class Query {

	public static void setEmbeddingModelResourceComponentServiceObjects(
		ComponentServiceObjects<EmbeddingModelResource>
			embeddingModelResourceComponentServiceObjects) {

		_embeddingModelResourceComponentServiceObjects =
			embeddingModelResourceComponentServiceObjects;
	}

	public static void setSearchResultResourceComponentServiceObjects(
		ComponentServiceObjects<SearchResultResource>
			searchResultResourceComponentServiceObjects) {

		_searchResultResourceComponentServiceObjects =
			searchResultResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {embeddingEmbeddingModels(page: ___, pageSize: ___, provider: ___, search: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public EmbeddingModelPage embeddingEmbeddingModels(
			@GraphQLName("provider") String provider,
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_embeddingModelResourceComponentServiceObjects,
			this::_populateResourceContext,
			embeddingModelResource -> new EmbeddingModelPage(
				embeddingModelResource.getEmbeddingEmbeddingModelsPage(
					provider, search, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {search(blueprintExternalReferenceCode: ___, emptySearch: ___, entryClassNames: ___, filter: ___, page: ___, pageSize: ___, scope: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Search the company index for matching content. This endpoint is development and requires setting the portal property 'feature.flag.LPD-11232' to true or enabling via Instance Settings > Feature Flags: Developer."
	)
	public SearchResultPage search(
			@GraphQLName("blueprintExternalReferenceCode") String
				blueprintExternalReferenceCode,
			@GraphQLName("emptySearch") Boolean emptySearch,
			@GraphQLName("entryClassNames") String entryClassNames,
			@GraphQLName("scope") String scope,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_searchResultResourceComponentServiceObjects,
			this::_populateResourceContext,
			searchResultResource -> new SearchResultPage(
				searchResultResource.getSearchPage(
					blueprintExternalReferenceCode, emptySearch,
					entryClassNames, scope, search,
					_filterBiFunction.apply(searchResultResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						searchResultResource, sortsString))));
	}

	@GraphQLName("EmbeddingModelPage")
	public class EmbeddingModelPage {

		public EmbeddingModelPage(Page embeddingModelPage) {
			actions = embeddingModelPage.getActions();

			items = embeddingModelPage.getItems();
			lastPage = embeddingModelPage.getLastPage();
			page = embeddingModelPage.getPage();
			pageSize = embeddingModelPage.getPageSize();
			totalCount = embeddingModelPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<EmbeddingModel> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SearchResultPage")
	public class SearchResultPage {

		public SearchResultPage(Page searchResultPage) {
			actions = searchResultPage.getActions();

			items = searchResultPage.getItems();
			lastPage = searchResultPage.getLastPage();
			page = searchResultPage.getPage();
			pageSize = searchResultPage.getPageSize();
			totalCount = searchResultPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SearchResult> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

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

	private void _populateResourceContext(
			EmbeddingModelResource embeddingModelResource)
		throws Exception {

		embeddingModelResource.setContextAcceptLanguage(_acceptLanguage);
		embeddingModelResource.setContextCompany(_company);
		embeddingModelResource.setContextHttpServletRequest(
			_httpServletRequest);
		embeddingModelResource.setContextHttpServletResponse(
			_httpServletResponse);
		embeddingModelResource.setContextUriInfo(_uriInfo);
		embeddingModelResource.setContextUser(_user);
		embeddingModelResource.setGroupLocalService(_groupLocalService);
		embeddingModelResource.setRoleLocalService(_roleLocalService);
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
	}

	private static ComponentServiceObjects<EmbeddingModelResource>
		_embeddingModelResourceComponentServiceObjects;
	private static ComponentServiceObjects<SearchResultResource>
		_searchResultResourceComponentServiceObjects;

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

}