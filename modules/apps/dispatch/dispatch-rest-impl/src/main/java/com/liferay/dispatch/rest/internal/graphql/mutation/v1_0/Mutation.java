/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.rest.internal.graphql.mutation.v1_0;

import com.liferay.dispatch.rest.dto.v1_0.DispatchTrigger;
import com.liferay.dispatch.rest.resource.v1_0.DispatchTriggerResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import java.util.function.BiFunction;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setDispatchTriggerResourceComponentServiceObjects(
		ComponentServiceObjects<DispatchTriggerResource>
			dispatchTriggerResourceComponentServiceObjects) {

		_dispatchTriggerResourceComponentServiceObjects =
			dispatchTriggerResourceComponentServiceObjects;
	}

	@GraphQLField
	public Response createDispatchTriggersPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_dispatchTriggerResourceComponentServiceObjects,
			this::_populateResourceContext,
			dispatchTriggerResource ->
				dispatchTriggerResource.postDispatchTriggersPageExportBatch(
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public DispatchTrigger createDispatchTrigger(
			@GraphQLName("dispatchTrigger") DispatchTrigger dispatchTrigger)
		throws Exception {

		return _applyComponentServiceObjects(
			_dispatchTriggerResourceComponentServiceObjects,
			this::_populateResourceContext,
			dispatchTriggerResource ->
				dispatchTriggerResource.postDispatchTrigger(dispatchTrigger));
	}

	@GraphQLField
	public Response createDispatchTriggerBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_dispatchTriggerResourceComponentServiceObjects,
			this::_populateResourceContext,
			dispatchTriggerResource ->
				dispatchTriggerResource.postDispatchTriggerBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean createDispatchTriggerRun(
			@GraphQLName("dispatchTriggerId") Long dispatchTriggerId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_dispatchTriggerResourceComponentServiceObjects,
			this::_populateResourceContext,
			dispatchTriggerResource ->
				dispatchTriggerResource.postDispatchTriggerRun(
					dispatchTriggerId));

		return true;
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

		dispatchTriggerResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		dispatchTriggerResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<DispatchTriggerResource>
		_dispatchTriggerResourceComponentServiceObjects;

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