/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.internal.graphql.query.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.segments.asah.rest.dto.v1_0.Experiment;
import com.liferay.segments.asah.rest.resource.v1_0.ExperimentResource;

import java.util.Map;
import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Query {

	public static void setExperimentResourceComponentServiceObjects(
		ComponentServiceObjects<ExperimentResource>
			experimentResourceComponentServiceObjects) {

		_experimentResourceComponentServiceObjects =
			experimentResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {experiment(experimentId: ___){dateCreated, dateModified, description, id, name, siteId, status, winnerVariantId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Experiment experiment(
			@GraphQLName("experimentId") String experimentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_experimentResourceComponentServiceObjects,
			this::_populateResourceContext,
			experimentResource -> experimentResource.getExperiment(
				experimentId));
	}

	@GraphQLName("ExperimentPage")
	public class ExperimentPage {

		public ExperimentPage(Page experimentPage) {
			actions = experimentPage.getActions();

			items = experimentPage.getItems();
			lastPage = experimentPage.getLastPage();
			page = experimentPage.getPage();
			pageSize = experimentPage.getPageSize();
			totalCount = experimentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Experiment> items;

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

	private void _populateResourceContext(ExperimentResource experimentResource)
		throws Exception {

		experimentResource.setContextAcceptLanguage(_acceptLanguage);
		experimentResource.setContextCompany(_company);
		experimentResource.setContextHttpServletRequest(_httpServletRequest);
		experimentResource.setContextHttpServletResponse(_httpServletResponse);
		experimentResource.setContextUriInfo(_uriInfo);
		experimentResource.setContextUser(_user);
		experimentResource.setGroupLocalService(_groupLocalService);
		experimentResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<ExperimentResource>
		_experimentResourceComponentServiceObjects;

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