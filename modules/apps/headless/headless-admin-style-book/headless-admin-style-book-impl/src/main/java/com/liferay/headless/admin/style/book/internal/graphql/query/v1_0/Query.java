/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.style.book.internal.graphql.query.v1_0;

import com.liferay.headless.admin.style.book.dto.v1_0.StyleBook;
import com.liferay.headless.admin.style.book.resource.v1_0.StyleBookResource;
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
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.constraints.NotEmpty;

import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public class Query {

	public static void setStyleBookResourceComponentServiceObjects(
		ComponentServiceObjects<StyleBookResource>
			styleBookResourceComponentServiceObjects) {

		_styleBookResourceComponentServiceObjects =
			styleBookResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryStyleBook(assetLibraryExternalReferenceCode: ___, styleBookExternalReferenceCode: ___){actions, creator, dateCreated, dateModified, defaultStyleBook, designLibraryExternalReferenceCode, designLibraryName, externalReferenceCode, frontendTokensValues, id, key, name, previewFileEntryExternalReferenceCode, themeId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves a specific style book of an asset library."
	)
	public StyleBook assetLibraryStyleBook(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("styleBookExternalReferenceCode") String
				styleBookExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> styleBookResource.getAssetLibraryStyleBook(
				assetLibraryExternalReferenceCode,
				styleBookExternalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryStyleBooks(aggregation: ___, assetLibraryExternalReferenceCode: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the style books of the asset library."
	)
	public StyleBookPage assetLibraryStyleBooks(
			@GraphQLName("assetLibraryExternalReferenceCode") @NotEmpty String
				assetLibraryExternalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> new StyleBookPage(
				styleBookResource.getAssetLibraryStyleBooksPage(
					assetLibraryExternalReferenceCode, search,
					_aggregationBiFunction.apply(
						styleBookResource, aggregations),
					_filterBiFunction.apply(styleBookResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(styleBookResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {styleBook(siteExternalReferenceCode: ___, styleBookExternalReferenceCode: ___){actions, creator, dateCreated, dateModified, defaultStyleBook, designLibraryExternalReferenceCode, designLibraryName, externalReferenceCode, frontendTokensValues, id, key, name, previewFileEntryExternalReferenceCode, themeId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a specific style book of a site.")
	public StyleBook styleBook(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("styleBookExternalReferenceCode") String
				styleBookExternalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> styleBookResource.getSiteStyleBook(
				siteExternalReferenceCode, styleBookExternalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {styleBooks(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteExternalReferenceCode: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the style books of the site.")
	public StyleBookPage styleBooks(
			@GraphQLName("siteExternalReferenceCode") @NotEmpty String
				siteExternalReferenceCode,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_styleBookResourceComponentServiceObjects,
			this::_populateResourceContext,
			styleBookResource -> new StyleBookPage(
				styleBookResource.getSiteStyleBooksPage(
					siteExternalReferenceCode, search,
					_aggregationBiFunction.apply(
						styleBookResource, aggregations),
					_filterBiFunction.apply(styleBookResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(styleBookResource, sortsString))));
	}

	@GraphQLName("StyleBookPage")
	public class StyleBookPage {

		public StyleBookPage(Page styleBookPage) {
			actions = styleBookPage.getActions();

			facets = styleBookPage.getFacets();

			items = styleBookPage.getItems();
			lastPage = styleBookPage.getLastPage();
			page = styleBookPage.getPage();
			pageSize = styleBookPage.getPageSize();
			totalCount = styleBookPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<StyleBook> items;

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

	private void _populateResourceContext(StyleBookResource styleBookResource)
		throws Exception {

		styleBookResource.setContextAcceptLanguage(_acceptLanguage);
		styleBookResource.setContextCompany(_company);
		styleBookResource.setContextHttpServletRequest(_httpServletRequest);
		styleBookResource.setContextHttpServletResponse(_httpServletResponse);
		styleBookResource.setContextUriInfo(_uriInfo);
		styleBookResource.setContextUser(_user);
		styleBookResource.setGroupLocalService(_groupLocalService);
		styleBookResource.setResourceActionLocalService(
			_resourceActionLocalService);
		styleBookResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		styleBookResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<StyleBookResource>
		_styleBookResourceComponentServiceObjects;

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
// LIFERAY-REST-BUILDER-HASH:-147149598