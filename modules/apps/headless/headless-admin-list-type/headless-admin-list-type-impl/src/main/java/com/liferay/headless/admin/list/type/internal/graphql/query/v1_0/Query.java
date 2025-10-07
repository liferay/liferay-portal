/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.list.type.internal.graphql.query.v1_0;

import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeEntryResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Gabriel Albuquerque
 * @generated
 */
@Generated("")
public class Query {

	public static void setListTypeDefinitionResourceComponentServiceObjects(
		ComponentServiceObjects<ListTypeDefinitionResource>
			listTypeDefinitionResourceComponentServiceObjects) {

		_listTypeDefinitionResourceComponentServiceObjects =
			listTypeDefinitionResourceComponentServiceObjects;
	}

	public static void setListTypeEntryResourceComponentServiceObjects(
		ComponentServiceObjects<ListTypeEntryResource>
			listTypeEntryResourceComponentServiceObjects) {

		_listTypeEntryResourceComponentServiceObjects =
			listTypeEntryResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {listTypeDefinition(listTypeDefinitionId: ___){actions, dateCreated, dateModified, defaultLanguageId, externalReferenceCode, id, listTypeEntries, name, name_i18n, system}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ListTypeDefinition listTypeDefinition(
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.getListTypeDefinition(
					listTypeDefinitionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {listTypeDefinitionByExternalReferenceCode(externalReferenceCode: ___){actions, dateCreated, dateModified, defaultLanguageId, externalReferenceCode, id, listTypeEntries, name, name_i18n, system}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ListTypeDefinition listTypeDefinitionByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource ->
				listTypeDefinitionResource.
					getListTypeDefinitionByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {listTypeDefinitions(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ListTypeDefinitionPage listTypeDefinitions(
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeDefinitionResource -> new ListTypeDefinitionPage(
				listTypeDefinitionResource.getListTypeDefinitionsPage(
					search,
					_aggregationBiFunction.apply(
						listTypeDefinitionResource, aggregations),
					_filterBiFunction.apply(
						listTypeDefinitionResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						listTypeDefinitionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {listTypeDefinitionByExternalReferenceCodeListTypeEntries(aggregation: ___, externalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ListTypeEntryPage
			listTypeDefinitionByExternalReferenceCodeListTypeEntries(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource -> new ListTypeEntryPage(
				listTypeEntryResource.
					getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
						externalReferenceCode, search,
						_aggregationBiFunction.apply(
							listTypeEntryResource, aggregations),
						_filterBiFunction.apply(
							listTypeEntryResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							listTypeEntryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {listTypeDefinitionListTypeEntries(aggregation: ___, filter: ___, listTypeDefinitionId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ListTypeEntryPage listTypeDefinitionListTypeEntries(
			@GraphQLName("listTypeDefinitionId") Long listTypeDefinitionId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource -> new ListTypeEntryPage(
				listTypeEntryResource.getListTypeDefinitionListTypeEntriesPage(
					listTypeDefinitionId, search,
					_aggregationBiFunction.apply(
						listTypeEntryResource, aggregations),
					_filterBiFunction.apply(
						listTypeEntryResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						listTypeEntryResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {listTypeEntry(listTypeEntryId: ___){actions, dateCreated, dateModified, externalReferenceCode, id, key, name, name_i18n, system, type}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ListTypeEntry listTypeEntry(
			@GraphQLName("listTypeEntryId") Long listTypeEntryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_listTypeEntryResourceComponentServiceObjects,
			this::_populateResourceContext,
			listTypeEntryResource -> listTypeEntryResource.getListTypeEntry(
				listTypeEntryId));
	}

	@GraphQLTypeExtension(ListTypeDefinition.class)
	public class
		GetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageTypeExtension {

		public GetListTypeDefinitionByExternalReferenceCodeListTypeEntriesPageTypeExtension(
			ListTypeDefinition listTypeDefinition) {

			_listTypeDefinition = listTypeDefinition;
		}

		@GraphQLField
		public ListTypeEntryPage byExternalReferenceCodeListTypeEntries(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_listTypeEntryResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				listTypeEntryResource -> new ListTypeEntryPage(
					listTypeEntryResource.
						getListTypeDefinitionByExternalReferenceCodeListTypeEntriesPage(
							_listTypeDefinition.getExternalReferenceCode(),
							search,
							_aggregationBiFunction.apply(
								listTypeEntryResource, aggregations),
							_filterBiFunction.apply(
								listTypeEntryResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								listTypeEntryResource, sortsString))));
		}

		private ListTypeDefinition _listTypeDefinition;

	}

	@GraphQLName("ListTypeDefinitionPage")
	public class ListTypeDefinitionPage {

		public ListTypeDefinitionPage(Page listTypeDefinitionPage) {
			actions = listTypeDefinitionPage.getActions();

			facets = listTypeDefinitionPage.getFacets();

			items = listTypeDefinitionPage.getItems();
			lastPage = listTypeDefinitionPage.getLastPage();
			page = listTypeDefinitionPage.getPage();
			pageSize = listTypeDefinitionPage.getPageSize();
			totalCount = listTypeDefinitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ListTypeDefinition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ListTypeEntryPage")
	public class ListTypeEntryPage {

		public ListTypeEntryPage(Page listTypeEntryPage) {
			actions = listTypeEntryPage.getActions();

			facets = listTypeEntryPage.getFacets();

			items = listTypeEntryPage.getItems();
			lastPage = listTypeEntryPage.getLastPage();
			page = listTypeEntryPage.getPage();
			pageSize = listTypeEntryPage.getPageSize();
			totalCount = listTypeEntryPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ListTypeEntry> items;

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
			ListTypeDefinitionResource listTypeDefinitionResource)
		throws Exception {

		listTypeDefinitionResource.setContextAcceptLanguage(_acceptLanguage);
		listTypeDefinitionResource.setContextCompany(_company);
		listTypeDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		listTypeDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		listTypeDefinitionResource.setContextUriInfo(_uriInfo);
		listTypeDefinitionResource.setContextUser(_user);
		listTypeDefinitionResource.setGroupLocalService(_groupLocalService);
		listTypeDefinitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		listTypeDefinitionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		listTypeDefinitionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ListTypeEntryResource listTypeEntryResource)
		throws Exception {

		listTypeEntryResource.setContextAcceptLanguage(_acceptLanguage);
		listTypeEntryResource.setContextCompany(_company);
		listTypeEntryResource.setContextHttpServletRequest(_httpServletRequest);
		listTypeEntryResource.setContextHttpServletResponse(
			_httpServletResponse);
		listTypeEntryResource.setContextUriInfo(_uriInfo);
		listTypeEntryResource.setContextUser(_user);
		listTypeEntryResource.setGroupLocalService(_groupLocalService);
		listTypeEntryResource.setResourceActionLocalService(
			_resourceActionLocalService);
		listTypeEntryResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		listTypeEntryResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<ListTypeDefinitionResource>
		_listTypeDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<ListTypeEntryResource>
		_listTypeEntryResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}