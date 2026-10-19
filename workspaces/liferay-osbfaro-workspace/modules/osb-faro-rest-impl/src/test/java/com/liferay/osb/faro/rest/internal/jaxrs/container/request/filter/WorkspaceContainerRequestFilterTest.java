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

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Nilton Vieira
 */
public class WorkspaceContainerRequestFilterTest {

	@Before
	public void setUp() {
		_permissionChecker = PermissionThreadLocal.getPermissionChecker();

		FaroChannel allUsersFaroChannel = _mockFaroChannel(
			FaroChannelConstants.PERMISSION_ALL_USERS);
		FaroChannel selectUsersFaroChannel = _mockFaroChannel(
			FaroChannelConstants.PERMISSION_SELECT_USERS);

		Mockito.when(
			_faroChannelLocalService.fetchFaroChannel(
				_ALL_USERS_CHANNEL_ID, _GROUP_ID)
		).thenReturn(
			allUsersFaroChannel
		);

		Mockito.when(
			_faroChannelLocalService.fetchFaroChannel(
				_SELECT_USERS_CHANNEL_ID, _GROUP_ID)
		).thenReturn(
			selectUsersFaroChannel
		);

		Mockito.when(
			_faroProject.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_faroProject.isAllowedIPAddress(Mockito.any())
		).thenReturn(
			true
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			_faroProject
		);

		FaroUser faroUser = Mockito.mock(FaroUser.class);

		Mockito.when(
			faroUser.getRoleId()
		).thenReturn(
			_ROLE_ID
		);

		Mockito.when(
			faroUser.getStatus()
		).thenReturn(
			FaroUserConstants.STATUS_APPROVED
		);

		Mockito.when(
			_faroUserLocalService.fetchFaroUser(_GROUP_ID, _MEMBER_USER_ID)
		).thenReturn(
			faroUser
		);

		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			RoleConstants.SITE_MEMBER
		);

		Mockito.when(
			_roleLocalService.fetchRole(_ROLE_ID)
		).thenReturn(
			role
		);

		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(
				_MEMBER_USER_ID, _CHANNEL_GROUP_ID)
		).thenReturn(
			Collections.emptyList()
		);

		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_faroChannelLocalService",
			_faroChannelLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_faroUserLocalService",
			_faroUserLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_httpServletRequest",
			Mockito.mock(HttpServletRequest.class));
		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_roleLocalService",
			_roleLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_userGroupRoleLocalService",
			_userGroupRoleLocalService);
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_permissionChecker);
	}

	@Test
	public void testFilter() {
		Assert.assertNull(_filter(null, null, _MEMBER_USER_ID));
		Assert.assertNull(_filter(_GROUP_ID, null, _MEMBER_USER_ID));
		Assert.assertNull(
			_filter(_GROUP_ID, _ALL_USERS_CHANNEL_ID, _MEMBER_USER_ID));
		Assert.assertNull(_filter(_GROUP_ID, null, _OMNIADMIN_USER_ID));

		_assertStatus(
			Response.Status.NOT_FOUND, _filter(999L, null, _MEMBER_USER_ID));
		_assertStatus(
			Response.Status.UNAUTHORIZED,
			_filter(_GROUP_ID, null, _GUEST_USER_ID));
		_assertStatus(
			Response.Status.FORBIDDEN,
			_filter(_GROUP_ID, null, _OUTSIDER_USER_ID));
		_assertStatus(
			Response.Status.NOT_FOUND,
			_filter(_GROUP_ID, "unknown", _MEMBER_USER_ID));
		_assertStatus(
			Response.Status.FORBIDDEN,
			_filter(_GROUP_ID, _SELECT_USERS_CHANNEL_ID, _MEMBER_USER_ID));

		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(
				_MEMBER_USER_ID, _CHANNEL_GROUP_ID)
		).thenReturn(
			List.of(Mockito.mock(UserGroupRole.class))
		);

		Assert.assertNull(
			_filter(_GROUP_ID, _SELECT_USERS_CHANNEL_ID, _MEMBER_USER_ID));

		Mockito.when(
			_faroProject.isAllowedIPAddress(Mockito.any())
		).thenReturn(
			false
		);

		_assertStatus(
			Response.Status.FORBIDDEN,
			_filter(_GROUP_ID, null, _MEMBER_USER_ID));
		Assert.assertNull(_filter(_GROUP_ID, null, _OMNIADMIN_USER_ID));
	}

	private void _assertStatus(Response.Status status, Response response) {
		Assert.assertNotNull(response);
		Assert.assertEquals(status.getStatusCode(), response.getStatus());
	}

	private Response _filter(Long groupId, String channelId, long userId) {
		User user = Mockito.mock(User.class);

		Mockito.when(
			user.isGuestUser()
		).thenReturn(
			userId == _GUEST_USER_ID
		);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.getUser()
		).thenReturn(
			user
		);

		Mockito.when(
			permissionChecker.getUserId()
		).thenReturn(
			userId
		);

		Mockito.when(
			permissionChecker.isOmniadmin()
		).thenReturn(
			userId == _OMNIADMIN_USER_ID
		);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		MultivaluedMap<String, String> pathParameters =
			new MultivaluedHashMap<>();

		if (groupId != null) {
			pathParameters.putSingle("groupId", String.valueOf(groupId));
		}

		if (channelId != null) {
			pathParameters.putSingle("channelId", channelId);
		}

		UriInfo uriInfo = Mockito.mock(UriInfo.class);

		Mockito.when(
			uriInfo.getPathParameters()
		).thenReturn(
			pathParameters
		);

		Mockito.when(
			uriInfo.getQueryParameters()
		).thenReturn(
			new MultivaluedHashMap<>()
		);

		ContainerRequestContext containerRequestContext = Mockito.mock(
			ContainerRequestContext.class);

		Mockito.when(
			containerRequestContext.getUriInfo()
		).thenReturn(
			uriInfo
		);

		_workspaceContainerRequestFilter.filter(containerRequestContext);

		ArgumentCaptor<Response> argumentCaptor = ArgumentCaptor.forClass(
			Response.class);

		Mockito.verify(
			containerRequestContext, Mockito.atMost(1)
		).abortWith(
			argumentCaptor.capture()
		);

		List<Response> responses = argumentCaptor.getAllValues();

		if (responses.isEmpty()) {
			return null;
		}

		return responses.get(0);
	}

	private FaroChannel _mockFaroChannel(int permissionType) {
		FaroChannel faroChannel = Mockito.mock(FaroChannel.class);

		Mockito.when(
			faroChannel.getGroupId()
		).thenReturn(
			_CHANNEL_GROUP_ID
		);

		Mockito.when(
			faroChannel.getPermissionType()
		).thenReturn(
			permissionType
		);

		return faroChannel;
	}

	private static final String _ALL_USERS_CHANNEL_ID = "100";

	private static final long _CHANNEL_GROUP_ID = 200L;

	private static final long _GROUP_ID = 300L;

	private static final long _GUEST_USER_ID = 1L;

	private static final long _MEMBER_USER_ID = 2L;

	private static final long _OMNIADMIN_USER_ID = 4L;

	private static final long _OUTSIDER_USER_ID = 3L;

	private static final long _ROLE_ID = 400L;

	private static final String _SELECT_USERS_CHANNEL_ID = "500";

	private final FaroChannelLocalService _faroChannelLocalService =
		Mockito.mock(FaroChannelLocalService.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final FaroUserLocalService _faroUserLocalService = Mockito.mock(
		FaroUserLocalService.class);
	private PermissionChecker _permissionChecker;
	private final RoleLocalService _roleLocalService = Mockito.mock(
		RoleLocalService.class);
	private final UserGroupRoleLocalService _userGroupRoleLocalService =
		Mockito.mock(UserGroupRoleLocalService.class);
	private final WorkspaceContainerRequestFilter
		_workspaceContainerRequestFilter =
			new WorkspaceContainerRequestFilter();

}