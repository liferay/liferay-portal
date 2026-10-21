/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.internal.graphql.mutation.v2_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.tools.rest.builder.test.resource.v2_0.FeatureFlagApplicationTestEntityResource;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@com.liferay.portal.vulcan.feature.flag.FeatureFlag("APPLICATION-123")
@Generated("")
public class Mutation {

	public static void
		setFeatureFlagApplicationTestEntityResourceComponentServiceObjects(
			ComponentServiceObjects<FeatureFlagApplicationTestEntityResource>
				featureFlagApplicationTestEntityResourceComponentServiceObjects) {

		_featureFlagApplicationTestEntityResourceComponentServiceObjects =
			featureFlagApplicationTestEntityResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response createFeatureFlagApplicationTestEntitiesPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_featureFlagApplicationTestEntityResourceComponentServiceObjects,
			this::_populateResourceContext,
			featureFlagApplicationTestEntityResource ->
				featureFlagApplicationTestEntityResource.
					postFeatureFlagApplicationTestEntitiesPageExportBatch(
						callbackURL, contentType, fieldNames));
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
		featureFlagApplicationTestEntityResource.setRoleLocalService(
			_roleLocalService);

		featureFlagApplicationTestEntityResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		featureFlagApplicationTestEntityResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects
		<FeatureFlagApplicationTestEntityResource>
			_featureFlagApplicationTestEntityResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
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
// LIFERAY-REST-BUILDER-HASH:387690909