/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.graphql.query.v2_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.tools.rest.builder.test.dto.v2_0.FeatureFlagApplicationTestEntity;
import com.liferay.portal.tools.rest.builder.test.resource.v2_0.FeatureFlagApplicationTestEntityResource;
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
 * @author Alejandro Tardín
 * @generated
 */
@com.liferay.portal.vulcan.feature.flag.FeatureFlag("APPLICATION-123")
@Generated("")
public class Query {

	public static void
		setFeatureFlagApplicationTestEntityResourceComponentServiceObjects(
			ComponentServiceObjects<FeatureFlagApplicationTestEntityResource>
				featureFlagApplicationTestEntityResourceComponentServiceObjects) {

		_featureFlagApplicationTestEntityResourceComponentServiceObjects =
			featureFlagApplicationTestEntityResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {featureFlagApplicationTestEntities{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public FeatureFlagApplicationTestEntityPage
			featureFlagApplicationTestEntities()
		throws Exception {

		return _applyComponentServiceObjects(
			_featureFlagApplicationTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			featureFlagApplicationTestEntityResource ->
				new FeatureFlagApplicationTestEntityPage(
					featureFlagApplicationTestEntityResource.
						getFeatureFlagApplicationTestEntitiesPage()));
	}

	@GraphQLName("FeatureFlagApplicationTestEntityPage")
	public class FeatureFlagApplicationTestEntityPage {

		public FeatureFlagApplicationTestEntityPage(
			Page featureFlagApplicationTestEntityPage) {

			actions = featureFlagApplicationTestEntityPage.getActions();

			items = featureFlagApplicationTestEntityPage.getItems();
			lastPage = featureFlagApplicationTestEntityPage.getLastPage();
			page = featureFlagApplicationTestEntityPage.getPage();
			pageSize = featureFlagApplicationTestEntityPage.getPageSize();
			totalCount = featureFlagApplicationTestEntityPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<FeatureFlagApplicationTestEntity> items;

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
			FeatureFlagApplicationTestEntityResource
				featureFlagApplicationTestEntityResource)
		throws Exception {

		featureFlagApplicationTestEntityResource.setContextAcceptLanguage(
			_acceptLanguage);
		featureFlagApplicationTestEntityResource.setContextCompany(_company);
		featureFlagApplicationTestEntityResource.setContextHttpServletRequest(
			_httpServletRequest);
		featureFlagApplicationTestEntityResource.setContextHttpServletResponse(
			_httpServletResponse);
		featureFlagApplicationTestEntityResource.setContextUriInfo(_uriInfo);
		featureFlagApplicationTestEntityResource.setContextUser(_user);
		featureFlagApplicationTestEntityResource.setGroupLocalService(
			_groupLocalService);
		featureFlagApplicationTestEntityResource.setResourceActionLocalService(
			_resourceActionLocalService);
		featureFlagApplicationTestEntityResource.
			setResourcePermissionLocalService(_resourcePermissionLocalService);
		featureFlagApplicationTestEntityResource.setRoleLocalService(
			_roleLocalService);
	}

	private static ComponentServiceObjects
		<FeatureFlagApplicationTestEntityResource>
			_featureFlagApplicationTestEntityResourceComponentServiceObjects;

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
// LIFERAY-REST-BUILDER-HASH:-971583743