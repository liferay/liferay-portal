/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.user.notification.internal.graphql.mutation.v1_0;

import com.liferay.headless.user.notification.resource.v1_0.UserNotificationResource;
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

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Carlos Correa
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setUserNotificationResourceComponentServiceObjects(
		ComponentServiceObjects<UserNotificationResource>
			userNotificationResourceComponentServiceObjects) {

		_userNotificationResourceComponentServiceObjects =
			userNotificationResourceComponentServiceObjects;
	}

	@GraphQLField(description = "Mark the user notification as read.")
	public boolean updateUserNotificationRead(
			@GraphQLName("userNotificationId") Long userNotificationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userNotificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			userNotificationResource ->
				userNotificationResource.putUserNotificationRead(
					userNotificationId));

		return true;
	}

	@GraphQLField(description = "Mark the user notification as unread.")
	public boolean updateUserNotificationUnread(
			@GraphQLName("userNotificationId") Long userNotificationId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_userNotificationResourceComponentServiceObjects,
			this::_populateResourceContext,
			userNotificationResource ->
				userNotificationResource.putUserNotificationUnread(
					userNotificationId));

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
			UserNotificationResource userNotificationResource)
		throws Exception {

		userNotificationResource.setContextAcceptLanguage(_acceptLanguage);
		userNotificationResource.setContextCompany(_company);
		userNotificationResource.setContextHttpServletRequest(
			_httpServletRequest);
		userNotificationResource.setContextHttpServletResponse(
			_httpServletResponse);
		userNotificationResource.setContextUriInfo(_uriInfo);
		userNotificationResource.setContextUser(_user);
		userNotificationResource.setGroupLocalService(_groupLocalService);
		userNotificationResource.setRoleLocalService(_roleLocalService);

		userNotificationResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		userNotificationResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<UserNotificationResource>
		_userNotificationResourceComponentServiceObjects;

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