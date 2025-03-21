/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.rest.internal.graphql.query.v1_0;

import com.liferay.dispatch.rest.dto.v1_0.DispatchTrigger;
import com.liferay.dispatch.rest.resource.v1_0.DispatchTriggerResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
public class Query {

	public static void setDispatchTriggerResourceComponentServiceObjects(
		ComponentServiceObjects<DispatchTriggerResource>
			dispatchTriggerResourceComponentServiceObjects) {

		_dispatchTriggerResourceComponentServiceObjects =
			dispatchTriggerResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {dispatchTriggers{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DispatchTriggerPage dispatchTriggers() throws Exception {
		return _applyComponentServiceObjects(
			_dispatchTriggerResourceComponentServiceObjects,
			this::_populateResourceContext,
			dispatchTriggerResource -> new DispatchTriggerPage(
				dispatchTriggerResource.getDispatchTriggersPage()));
	}

	@GraphQLName("DispatchTriggerPage")
	public class DispatchTriggerPage {

		public DispatchTriggerPage(Page dispatchTriggerPage) {
			actions = dispatchTriggerPage.getActions();

			items = dispatchTriggerPage.getItems();
			lastPage = dispatchTriggerPage.getLastPage();
			page = dispatchTriggerPage.getPage();
			pageSize = dispatchTriggerPage.getPageSize();
			totalCount = dispatchTriggerPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<DispatchTrigger> items;

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
			DispatchTriggerResource dispatchTriggerResource)
		throws Exception {

		dispatchTriggerResource.setContextAcceptLanguage(_acceptLanguage);
		dispatchTriggerResource.setContextCompany(_company);
		dispatchTriggerResource.setContextHttpServletRequest(
			_httpServletRequest);
		dispatchTriggerResource.setContextHttpServletResponse(
			_httpServletResponse);
		dispatchTriggerResource.setContextUriInfo(_uriInfo);
		dispatchTriggerResource.setContextUser(_user);
		dispatchTriggerResource.setGroupLocalService(_groupLocalService);
		dispatchTriggerResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<DispatchTriggerResource>
		_dispatchTriggerResourceComponentServiceObjects;

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