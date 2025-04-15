/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.testray.rest.internal.graphql.query.v1_0;

import com.liferay.osb.testray.rest.dto.v1_0.CompareRuns;
import com.liferay.osb.testray.rest.resource.v1_0.CompareRunsResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Map;
import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class Query {

	public static void setCompareRunsResourceComponentServiceObjects(
		ComponentServiceObjects<CompareRunsResource>
			compareRunsResourceComponentServiceObjects) {

		_compareRunsResourceComponentServiceObjects =
			compareRunsResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {compareRuns(idRunA: ___, idRunB: ___){dueStatuses, values}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CompareRuns compareRuns(
			@GraphQLName("idRunA") Long idRunA,
			@GraphQLName("idRunB") Long idRunB)
		throws Exception {

		return _applyComponentServiceObjects(
			_compareRunsResourceComponentServiceObjects,
			this::_populateResourceContext,
			compareRunsResource -> compareRunsResource.getCompareRuns(
				idRunA, idRunB));
	}

	@GraphQLName("CompareRunsPage")
	public class CompareRunsPage {

		public CompareRunsPage(Page compareRunsPage) {
			actions = compareRunsPage.getActions();

			items = compareRunsPage.getItems();
			lastPage = compareRunsPage.getLastPage();
			page = compareRunsPage.getPage();
			pageSize = compareRunsPage.getPageSize();
			totalCount = compareRunsPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<CompareRuns> items;

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
			CompareRunsResource compareRunsResource)
		throws Exception {

		compareRunsResource.setContextAcceptLanguage(_acceptLanguage);
		compareRunsResource.setContextCompany(_company);
		compareRunsResource.setContextHttpServletRequest(_httpServletRequest);
		compareRunsResource.setContextHttpServletResponse(_httpServletResponse);
		compareRunsResource.setContextUriInfo(_uriInfo);
		compareRunsResource.setContextUser(_user);
		compareRunsResource.setGroupLocalService(_groupLocalService);
		compareRunsResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<CompareRunsResource>
		_compareRunsResourceComponentServiceObjects;

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