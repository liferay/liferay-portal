/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.language.override.internal.graphql.query.v1_0;

import com.liferay.headless.admin.language.override.dto.v1_0.LanguageOverride;
import com.liferay.headless.admin.language.override.resource.v1_0.LanguageOverrideResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
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
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public class Query {

	public static void setLanguageOverrideResourceComponentServiceObjects(
		ComponentServiceObjects<LanguageOverrideResource>
			languageOverrideResourceComponentServiceObjects) {

		_languageOverrideResourceComponentServiceObjects =
			languageOverrideResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {languageOverrideByExternalReferenceCode(externalReferenceCode: ___){creator, dateCreated, dateModified, externalReferenceCode, id, key, languageId, value}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public LanguageOverride languageOverrideByExternalReferenceCode(
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageOverrideResource ->
				languageOverrideResource.
					getLanguageOverrideByExternalReferenceCode(
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {languageOverrides(page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public LanguageOverridePage languageOverrides(
			@GraphQLName("search") String search,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageOverrideResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageOverrideResource -> new LanguageOverridePage(
				languageOverrideResource.getLanguageOverridesPage(
					search, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						languageOverrideResource, sortsString))));
	}

	@GraphQLName("LanguageOverridePage")
	public class LanguageOverridePage {

		public LanguageOverridePage(Page languageOverridePage) {
			actions = languageOverridePage.getActions();

			items = languageOverridePage.getItems();
			lastPage = languageOverridePage.getLastPage();
			page = languageOverridePage.getPage();
			pageSize = languageOverridePage.getPageSize();
			totalCount = languageOverridePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<LanguageOverride> items;

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
			LanguageOverrideResource languageOverrideResource)
		throws Exception {

		languageOverrideResource.setContextAcceptLanguage(_acceptLanguage);
		languageOverrideResource.setContextCompany(_company);
		languageOverrideResource.setContextHttpServletRequest(
			_httpServletRequest);
		languageOverrideResource.setContextHttpServletResponse(
			_httpServletResponse);
		languageOverrideResource.setContextUriInfo(_uriInfo);
		languageOverrideResource.setContextUser(_user);
		languageOverrideResource.setGroupLocalService(_groupLocalService);
		languageOverrideResource.setResourceActionLocalService(
			_resourceActionLocalService);
		languageOverrideResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		languageOverrideResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<LanguageOverrideResource>
		_languageOverrideResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
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
// LIFERAY-REST-BUILDER-HASH:-2035292836