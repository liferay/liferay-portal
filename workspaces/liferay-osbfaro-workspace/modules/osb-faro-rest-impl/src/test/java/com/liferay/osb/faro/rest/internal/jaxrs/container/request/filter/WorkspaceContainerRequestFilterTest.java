/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.request.filter;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class WorkspaceContainerRequestFilterTest {

	@Test
	public void testFilter() throws Exception {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			_testFilterWithAuthorizedRequests();
			_testFilterWithUnauthorizedRequests();
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	private Response _filter(
		String channelId, boolean pathParameter, String groupId,
		boolean omniadmin, long userId) {

		PermissionThreadLocal.setPermissionChecker(
			_proxy(
				PermissionChecker.class,
				HashMapBuilder.<String, Object>put(
					"getUser",
					_proxy(
						User.class,
						HashMapBuilder.<String, Object>put(
							"isGuestUser", userId == _GUEST_USER_ID
						).build())
				).put(
					"getUserId", userId
				).put(
					"isOmniadmin", omniadmin
				).build()));

		MultivaluedMap<String, String> pathParameters =
			new MultivaluedHashMap<>();
		MultivaluedMap<String, String> queryParameters =
			new MultivaluedHashMap<>();

		if (groupId != null) {
			pathParameters.putSingle("groupId", groupId);
		}

		if (channelId != null) {
			if (pathParameter) {
				pathParameters.putSingle("channelId", channelId);
			}
			else {
				queryParameters.putSingle("channelId", channelId);
			}
		}

		AtomicReference<Response> responseAtomicReference =
			new AtomicReference<>();

		WorkspaceContainerRequestFilter workspaceContainerRequestFilter =
			new WorkspaceContainerRequestFilter();

		ReflectionTestUtil.setFieldValue(
			workspaceContainerRequestFilter, "_faroChannelLocalService",
			_proxy(
				FaroChannelLocalService.class,
				HashMapBuilder.<String, Object>put(
					"fetchFaroChannel",
					(UnsafeFunction)arguments -> _faroChannels.get(arguments[0])
				).build()));
		ReflectionTestUtil.setFieldValue(
			workspaceContainerRequestFilter, "_faroProjectLocalService",
			_proxy(
				FaroProjectLocalService.class,
				HashMapBuilder.<String, Object>put(
					"fetchFaroProjectByGroupId",
					(UnsafeFunction)arguments -> {
						if ((long)arguments[0] == _GROUP_ID) {
							return _faroProject;
						}

						return null;
					}
				).build()));
		ReflectionTestUtil.setFieldValue(
			workspaceContainerRequestFilter, "_faroUserLocalService",
			_proxy(
				FaroUserLocalService.class,
				HashMapBuilder.<String, Object>put(
					"fetchFaroUser",
					(UnsafeFunction)arguments -> {
						if (((long)arguments[1] != _CHANNEL_MEMBER_USER_ID) &&
							((long)arguments[1] != _MEMBER_USER_ID)) {

							return null;
						}

						return _proxy(
							FaroUser.class,
							HashMapBuilder.<String, Object>put(
								"getRoleId", (Object)_ROLE_ID
							).put(
								"getStatus",
								(Object)FaroUserConstants.STATUS_APPROVED
							).build());
					}
				).build()));
		ReflectionTestUtil.setFieldValue(
			workspaceContainerRequestFilter, "_httpServletRequest",
			_proxy(
				HttpServletRequest.class,
				HashMapBuilder.<String, Object>put(
					"getRemoteAddr", _REMOTE_ADDR
				).build()));
		ReflectionTestUtil.setFieldValue(
			workspaceContainerRequestFilter, "_roleLocalService",
			_proxy(
				RoleLocalService.class,
				HashMapBuilder.<String, Object>put(
					"fetchRole",
					_proxy(
						Role.class,
						HashMapBuilder.<String, Object>put(
							"getName", RoleConstants.SITE_MEMBER
						).build())
				).build()));
		ReflectionTestUtil.setFieldValue(
			workspaceContainerRequestFilter, "_userGroupRoleLocalService",
			_proxy(
				UserGroupRoleLocalService.class,
				HashMapBuilder.<String, Object>put(
					"getUserGroupRoles",
					(UnsafeFunction)arguments -> {
						if (((long)arguments[0] == _CHANNEL_MEMBER_USER_ID) &&
							((long)arguments[1] == _CHANNEL_GROUP_ID)) {

							return Collections.singletonList(
								_proxy(UserGroupRole.class, new HashMap<>()));
						}

						return Collections.emptyList();
					}
				).build()));

		workspaceContainerRequestFilter.filter(
			_proxy(
				ContainerRequestContext.class,
				HashMapBuilder.<String, Object>put(
					"abortWith",
					(UnsafeFunction)arguments -> {
						responseAtomicReference.set((Response)arguments[0]);

						return null;
					}
				).put(
					"getUriInfo",
					_proxy(
						UriInfo.class,
						HashMapBuilder.<String, Object>put(
							"getPathParameters", pathParameters
						).put(
							"getQueryParameters", queryParameters
						).build())
				).build()));

		return responseAtomicReference.get();
	}

	private Object _getDefaultValue(Method method) {
		Class<?> returnType = method.getReturnType();

		if (returnType == boolean.class) {
			return false;
		}

		if (returnType == int.class) {
			return 0;
		}

		if (returnType == long.class) {
			return 0L;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T _proxy(Class<T> clazz, Map<String, Object> values) {
		return (T)ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), new Class<?>[] {clazz},
			(proxy, method, arguments) -> {
				Object value = values.get(method.getName());

				if (value instanceof UnsafeFunction) {
					UnsafeFunction unsafeFunction = (UnsafeFunction)value;

					value = unsafeFunction.apply(arguments);
				}

				if (value == null) {
					return _getDefaultValue(method);
				}

				return value;
			});
	}

	private void _setFaroChannels() {
		_faroChannels.put(
			_ALL_USERS_CHANNEL_ID,
			_proxy(
				FaroChannel.class,
				HashMapBuilder.<String, Object>put(
					"getGroupId", _CHANNEL_GROUP_ID
				).put(
					"getPermissionType",
					FaroChannelConstants.PERMISSION_ALL_USERS
				).build()));
		_faroChannels.put(
			_SELECT_USERS_CHANNEL_ID,
			_proxy(
				FaroChannel.class,
				HashMapBuilder.<String, Object>put(
					"getGroupId", _CHANNEL_GROUP_ID
				).put(
					"getPermissionType",
					FaroChannelConstants.PERMISSION_SELECT_USERS
				).build()));
	}

	private void _setFaroProject(boolean allowedIPAddress) {
		_faroProject = _proxy(
			FaroProject.class,
			HashMapBuilder.<String, Object>put(
				"getGroupId", _GROUP_ID
			).put(
				"isAllowedIPAddress", allowedIPAddress
			).build());
	}

	private void _testFilterWithAuthorizedRequests() {
		_setFaroChannels();
		_setFaroProject(true);

		Assert.assertNull(_filter(null, false, null, false, _MEMBER_USER_ID));
		Assert.assertNull(
			_filter(
				null, false, String.valueOf(_GROUP_ID), false,
				_MEMBER_USER_ID));
		Assert.assertNull(
			_filter(
				_ALL_USERS_CHANNEL_ID, true, String.valueOf(_GROUP_ID), false,
				_MEMBER_USER_ID));
		Assert.assertNull(
			_filter(
				_ALL_USERS_CHANNEL_ID, false, String.valueOf(_GROUP_ID), false,
				_MEMBER_USER_ID));
		Assert.assertNull(
			_filter(
				_SELECT_USERS_CHANNEL_ID, true, String.valueOf(_GROUP_ID),
				false, _CHANNEL_MEMBER_USER_ID));

		_setFaroProject(false);

		Assert.assertNull(
			_filter(
				null, false, String.valueOf(_GROUP_ID), true,
				_OUTSIDER_USER_ID));
	}

	private void _testFilterWithUnauthorizedRequests() {
		_setFaroChannels();
		_setFaroProject(true);

		_assertStatus(
			Response.Status.NOT_FOUND,
			_filter(null, false, "999", false, _MEMBER_USER_ID));
		_assertStatus(
			Response.Status.UNAUTHORIZED,
			_filter(
				null, false, String.valueOf(_GROUP_ID), false, _GUEST_USER_ID));
		_assertStatus(
			Response.Status.FORBIDDEN,
			_filter(
				null, false, String.valueOf(_GROUP_ID), false,
				_OUTSIDER_USER_ID));
		_assertStatus(
			Response.Status.FORBIDDEN,
			_filter(
				_ALL_USERS_CHANNEL_ID, true, String.valueOf(_GROUP_ID), false,
				_OUTSIDER_USER_ID));
		_assertStatus(
			Response.Status.NOT_FOUND,
			_filter(
				"unknown", true, String.valueOf(_GROUP_ID), false,
				_MEMBER_USER_ID));
		_assertStatus(
			Response.Status.FORBIDDEN,
			_filter(
				_SELECT_USERS_CHANNEL_ID, true, String.valueOf(_GROUP_ID),
				false, _MEMBER_USER_ID));
		_assertStatus(
			Response.Status.FORBIDDEN,
			_filter(
				_SELECT_USERS_CHANNEL_ID, false, String.valueOf(_GROUP_ID),
				false, _MEMBER_USER_ID));

		_setFaroProject(false);

		_assertStatus(
			Response.Status.FORBIDDEN,
			_filter(
				null, false, String.valueOf(_GROUP_ID), false,
				_MEMBER_USER_ID));
	}

	private void _assertStatus(Response.Status status, Response response) {
		Assert.assertNotNull(response);
		Assert.assertEquals(status.getStatusCode(), response.getStatus());
	}

	private static final String _ALL_USERS_CHANNEL_ID = "100";

	private static final long _CHANNEL_GROUP_ID = 200L;

	private static final long _CHANNEL_MEMBER_USER_ID = 4L;

	private static final long _GROUP_ID = 300L;

	private static final long _GUEST_USER_ID = 1L;

	private static final long _MEMBER_USER_ID = 2L;

	private static final long _OUTSIDER_USER_ID = 3L;

	private static final long _ROLE_ID = 400L;

	private static final String _SELECT_USERS_CHANNEL_ID = "500";

	private final Map<String, FaroChannel> _faroChannels = new HashMap<>();
	private FaroProject _faroProject;

	private static final String _REMOTE_ADDR = "127.0.0.1";

	@FunctionalInterface
	private interface UnsafeFunction {

		public Object apply(Object[] arguments) throws Exception;

	}

}