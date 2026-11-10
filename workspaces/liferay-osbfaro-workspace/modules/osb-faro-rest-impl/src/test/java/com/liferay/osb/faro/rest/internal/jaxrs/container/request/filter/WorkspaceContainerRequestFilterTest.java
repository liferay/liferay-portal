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
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Nilton Vieira
 */
public class WorkspaceContainerRequestFilterTest {

	@Before
	public void setUp() {
		FaroChannel allUsersFaroChannel = _mockFaroChannel(
			FaroChannelConstants.PERMISSION_ALL_USERS);
		FaroChannel selectUsersFaroChannel = _mockFaroChannel(
			FaroChannelConstants.PERMISSION_SELECT_USERS);

		Mockito.when(
			_faroChannelLocalService.fetchFaroChannel(
				_CHANNEL_ID_ALL_USERS, _GROUP_ID_WORKSPACE)
		).thenReturn(
			allUsersFaroChannel
		);

		Mockito.when(
			_faroChannelLocalService.fetchFaroChannel(
				_CHANNEL_ID_SELECT_USERS, _GROUP_ID_WORKSPACE)
		).thenReturn(
			selectUsersFaroChannel
		);

		Mockito.when(
			_faroProject.isAllowedIPAddress(Mockito.any())
		).thenReturn(
			true
		);

		Mockito.when(
			_faroProject.getGroupId()
		).thenReturn(
			_GROUP_ID_WORKSPACE
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(
				_GROUP_ID_WORKSPACE)
		).thenReturn(
			_faroProject
		);

		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_faroChannelLocalService",
			_faroChannelLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspaceContainerRequestFilter, "_httpServletRequest",
			Mockito.mock(HttpServletRequest.class));

		_mockFaroUser(RoleConstants.SITE_ADMINISTRATOR, _USER_ID_ADMINISTRATOR);
		_mockFaroUser(RoleConstants.SITE_MEMBER, _USER_ID_MEMBER);
		_mockFaroUser(RoleConstants.SITE_OWNER, _USER_ID_OWNER);

		_permissionChecker = PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_permissionChecker);

		_faroUserLocalServiceUtilMockedStatic.close();
		_roleLocalServiceUtilMockedStatic.close();
		_userGroupRoleLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testFilter() {
		Assert.assertNull(
			_filter(
				_CHANNEL_ID_ALL_USERS, _GROUP_ID_WORKSPACE, _USER_ID_MEMBER));
		Assert.assertNull(_filter(null, _GROUP_ID_WORKSPACE, _USER_ID_MEMBER));
		Assert.assertNull(
			_filter(null, _GROUP_ID_WORKSPACE, _USER_ID_OMNIADMIN));
		Assert.assertNull(_filter(null, null, _USER_ID_MEMBER));

		_assertResponseStatus(
			_filter(
				_CHANNEL_ID_SELECT_USERS, _GROUP_ID_WORKSPACE, _USER_ID_MEMBER),
			Response.Status.FORBIDDEN);
		_assertResponseStatus(
			_filter(null, _GROUP_ID_WORKSPACE, _USER_ID_GUEST),
			Response.Status.UNAUTHORIZED);
		_assertResponseStatus(
			_filter(null, _GROUP_ID_WORKSPACE, _USER_ID_OUTSIDER),
			Response.Status.FORBIDDEN);

		_userGroupRoleLocalServiceUtilMockedStatic.when(
			() -> UserGroupRoleLocalServiceUtil.getUserGroupRoles(
				_USER_ID_MEMBER, _GROUP_ID_CHANNEL)
		).thenReturn(
			List.of(Mockito.mock(UserGroupRole.class))
		);

		Assert.assertNull(
			_filter(
				_CHANNEL_ID_SELECT_USERS, _GROUP_ID_WORKSPACE,
				_USER_ID_MEMBER));

		Mockito.when(
			_faroProject.isAllowedIPAddress(Mockito.any())
		).thenReturn(
			false
		);

		_assertResponseStatus(
			_filter(null, _GROUP_ID_WORKSPACE, _USER_ID_MEMBER),
			Response.Status.FORBIDDEN);
		Assert.assertNull(
			_filter(null, _GROUP_ID_WORKSPACE, _USER_ID_OMNIADMIN));
	}

	@Test
	public void testFilterWithAllUsersFaroChannel() {
		Assert.assertNull(
			_filter(
				_CHANNEL_ID_ALL_USERS, _GROUP_ID_WORKSPACE,
				_USER_ID_ADMINISTRATOR));
		Assert.assertNull(
			_filter(
				_CHANNEL_ID_ALL_USERS, _GROUP_ID_WORKSPACE, _USER_ID_MEMBER));
		Assert.assertNull(
			_filter(
				_CHANNEL_ID_ALL_USERS, _GROUP_ID_WORKSPACE,
				_USER_ID_OMNIADMIN));
		Assert.assertNull(
			_filter(
				_CHANNEL_ID_ALL_USERS, _GROUP_ID_WORKSPACE, _USER_ID_OWNER));
	}

	@Test
	public void testFilterWithOutsiderUser() {
		_assertResponse(
			"Unable to access the workspace with group ID " +
				_GROUP_ID_WORKSPACE,
			_filter(null, _GROUP_ID_WORKSPACE, _USER_ID_OUTSIDER),
			Response.Status.FORBIDDEN);
	}

	@Test
	public void testFilterWithSelectUsersFaroChannel() {
		Assert.assertNull(
			_filter(
				_CHANNEL_ID_SELECT_USERS, _GROUP_ID_WORKSPACE,
				_USER_ID_ADMINISTRATOR));
		Assert.assertNull(
			_filter(
				_CHANNEL_ID_SELECT_USERS, _GROUP_ID_WORKSPACE,
				_USER_ID_OMNIADMIN));
		Assert.assertNull(
			_filter(
				_CHANNEL_ID_SELECT_USERS, _GROUP_ID_WORKSPACE, _USER_ID_OWNER));

		_assertResponse(
			"Unable to access the property with channel ID " +
				_CHANNEL_ID_SELECT_USERS,
			_filter(
				_CHANNEL_ID_SELECT_USERS, _GROUP_ID_WORKSPACE, _USER_ID_MEMBER),
			Response.Status.FORBIDDEN);

		_userGroupRoleLocalServiceUtilMockedStatic.when(
			() -> UserGroupRoleLocalServiceUtil.getUserGroupRoles(
				_USER_ID_MEMBER, _GROUP_ID_CHANNEL)
		).thenReturn(
			List.of(Mockito.mock(UserGroupRole.class))
		);

		Assert.assertNull(
			_filter(
				_CHANNEL_ID_SELECT_USERS, _GROUP_ID_WORKSPACE,
				_USER_ID_MEMBER));
	}

	private void _assertResponse(
		String expectedTitle, Response response, Response.Status status) {

		_assertResponseStatus(response, status);

		Problem problem = (Problem)response.getEntity();

		Assert.assertEquals(expectedTitle, problem.getTitle());
		Assert.assertNull(problem.getType());
	}

	private void _assertResponseStatus(
		Response response, Response.Status status) {

		Assert.assertEquals(status.getStatusCode(), response.getStatus());
	}

	private Response _filter(String channelId, Long groupId, long userId) {
		User user = Mockito.mock(User.class);

		boolean guestUser = false;

		if (userId == _USER_ID_GUEST) {
			guestUser = true;
		}

		Mockito.when(
			user.isGuestUser()
		).thenReturn(
			guestUser
		);

		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		boolean omniadmin = false;

		if (userId == _USER_ID_OMNIADMIN) {
			omniadmin = true;
		}

		Mockito.when(
			permissionChecker.isOmniadmin()
		).thenReturn(
			omniadmin
		);

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

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		MultivaluedMap<String, String> pathParameters =
			new MultivaluedHashMap<>();

		if (channelId != null) {
			pathParameters.putSingle("channelId", channelId);
		}

		if (groupId != null) {
			pathParameters.putSingle("groupId", String.valueOf(groupId));
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
			_GROUP_ID_CHANNEL
		);

		Mockito.when(
			faroChannel.getPermissionType()
		).thenReturn(
			permissionType
		);

		Mockito.when(
			faroChannel.getWorkspaceGroupId()
		).thenReturn(
			_GROUP_ID_WORKSPACE
		);

		return faroChannel;
	}

	private FaroUser _mockFaroUser(String roleName, long userId) {
		FaroUser faroUser = Mockito.mock(FaroUser.class);

		Mockito.when(
			faroUser.getRoleId()
		).thenReturn(
			userId
		);

		Mockito.when(
			faroUser.getStatus()
		).thenReturn(
			FaroUserConstants.STATUS_APPROVED
		);

		_faroUserLocalServiceUtilMockedStatic.when(
			() -> FaroUserLocalServiceUtil.fetchFaroUser(
				_GROUP_ID_WORKSPACE, userId)
		).thenReturn(
			faroUser
		);

		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			roleName
		);

		_roleLocalServiceUtilMockedStatic.when(
			() -> RoleLocalServiceUtil.fetchRole(userId)
		).thenReturn(
			role
		);

		return faroUser;
	}

	private static final String _CHANNEL_ID_ALL_USERS =
		RandomTestUtil.randomString();

	private static final String _CHANNEL_ID_SELECT_USERS =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID_CHANNEL = RandomTestUtil.randomLong();

	private static final long _GROUP_ID_WORKSPACE = RandomTestUtil.randomLong();

	private static final long _USER_ID_ADMINISTRATOR =
		RandomTestUtil.randomLong();

	private static final long _USER_ID_GUEST = RandomTestUtil.randomLong();

	private static final long _USER_ID_MEMBER = RandomTestUtil.randomLong();

	private static final long _USER_ID_OMNIADMIN = RandomTestUtil.randomLong();

	private static final long _USER_ID_OUTSIDER = RandomTestUtil.randomLong();

	private static final long _USER_ID_OWNER = RandomTestUtil.randomLong();

	private final FaroChannelLocalService _faroChannelLocalService =
		Mockito.mock(FaroChannelLocalService.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final MockedStatic<FaroUserLocalServiceUtil>
		_faroUserLocalServiceUtilMockedStatic = Mockito.mockStatic(
			FaroUserLocalServiceUtil.class);
	private PermissionChecker _permissionChecker;
	private final MockedStatic<RoleLocalServiceUtil>
		_roleLocalServiceUtilMockedStatic = Mockito.mockStatic(
			RoleLocalServiceUtil.class);
	private final MockedStatic<UserGroupRoleLocalServiceUtil>
		_userGroupRoleLocalServiceUtilMockedStatic = Mockito.mockStatic(
			UserGroupRoleLocalServiceUtil.class);
	private final WorkspaceContainerRequestFilter
		_workspaceContainerRequestFilter =
			new WorkspaceContainerRequestFilter();

}