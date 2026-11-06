/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.security.permission;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class WorkspacePermissionTest {

	@Before
	public void setUp() {
		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(
				Mockito.anyLong(), Mockito.eq(_CHANNEL_GROUP_ID))
		).thenReturn(
			Collections.emptyList()
		);

		ReflectionTestUtil.setFieldValue(
			_workspacePermission, "_faroUserLocalService",
			_faroUserLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspacePermission, "_roleLocalService", _roleLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspacePermission, "_userGroupRoleLocalService",
			_userGroupRoleLocalService);

		_mockFaroUser(
			RoleConstants.SITE_ADMINISTRATOR, FaroUserConstants.STATUS_APPROVED,
			_USER_ID_ADMINISTRATOR);
		_mockFaroUser(
			RoleConstants.SITE_ADMINISTRATOR, FaroUserConstants.STATUS_PENDING,
			_USER_ID_PENDING_ADMINISTRATOR);
		_mockFaroUser(
			RoleConstants.SITE_MEMBER, FaroUserConstants.STATUS_APPROVED,
			_USER_ID_MEMBER);
		_mockFaroUser(
			RoleConstants.SITE_OWNER, FaroUserConstants.STATUS_APPROVED,
			_USER_ID_OWNER);
	}

	@Test
	public void testHasChannelPermissionWithAllUsersFaroChannel() {
		FaroChannel faroChannel = _mockFaroChannel(
			FaroChannelConstants.PERMISSION_ALL_USERS);

		Assert.assertTrue(
			_workspacePermission.hasChannelPermission(
				faroChannel, _mockPermissionChecker(_USER_ID_MEMBER)));
		Assert.assertTrue(
			_workspacePermission.hasChannelPermission(
				faroChannel, _mockPermissionChecker(_USER_ID_OUTSIDER)));
	}

	@Test
	public void testHasChannelPermissionWithSelectUsersFaroChannel() {
		FaroChannel faroChannel = _mockFaroChannel(
			FaroChannelConstants.PERMISSION_SELECT_USERS);

		Assert.assertTrue(
			_workspacePermission.hasChannelPermission(
				faroChannel, _mockPermissionChecker(_USER_ID_ADMINISTRATOR)));
		Assert.assertFalse(
			_workspacePermission.hasChannelPermission(
				faroChannel, _mockPermissionChecker(_USER_ID_MEMBER)));
		Assert.assertTrue(
			_workspacePermission.hasChannelPermission(
				faroChannel, _mockPermissionChecker(_USER_ID_OMNIADMIN)));
		Assert.assertFalse(
			_workspacePermission.hasChannelPermission(
				faroChannel, _mockPermissionChecker(_USER_ID_OUTSIDER)));
		Assert.assertTrue(
			_workspacePermission.hasChannelPermission(
				faroChannel, _mockPermissionChecker(_USER_ID_OWNER)));
		Assert.assertFalse(
			_workspacePermission.hasChannelPermission(
				faroChannel,
				_mockPermissionChecker(_USER_ID_PENDING_ADMINISTRATOR)));

		Mockito.when(
			_userGroupRoleLocalService.getUserGroupRoles(
				_USER_ID_MEMBER, _CHANNEL_GROUP_ID)
		).thenReturn(
			List.of(Mockito.mock(UserGroupRole.class))
		);

		Assert.assertTrue(
			_workspacePermission.hasChannelPermission(
				faroChannel, _mockPermissionChecker(_USER_ID_MEMBER)));
	}

	@Test
	public void testIsWorkspaceMember() {
		Assert.assertTrue(
			_workspacePermission.isWorkspaceMember(
				_GROUP_ID, _mockPermissionChecker(_USER_ID_ADMINISTRATOR)));
		Assert.assertTrue(
			_workspacePermission.isWorkspaceMember(
				_GROUP_ID, _mockPermissionChecker(_USER_ID_MEMBER)));
		Assert.assertFalse(
			_workspacePermission.isWorkspaceMember(
				_GROUP_ID, _mockPermissionChecker(_USER_ID_OUTSIDER)));
		Assert.assertTrue(
			_workspacePermission.isWorkspaceMember(
				_GROUP_ID, _mockPermissionChecker(_USER_ID_OWNER)));
		Assert.assertFalse(
			_workspacePermission.isWorkspaceMember(
				_GROUP_ID,
				_mockPermissionChecker(_USER_ID_PENDING_ADMINISTRATOR)));
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

		Mockito.when(
			faroChannel.getWorkspaceGroupId()
		).thenReturn(
			_GROUP_ID
		);

		return faroChannel;
	}

	private void _mockFaroUser(String roleName, int status, long userId) {
		FaroUser faroUser = Mockito.mock(FaroUser.class);

		Mockito.when(
			faroUser.getRoleId()
		).thenReturn(
			userId
		);

		Mockito.when(
			faroUser.getStatus()
		).thenReturn(
			status
		);

		Mockito.when(
			_faroUserLocalService.fetchFaroUser(_GROUP_ID, userId)
		).thenReturn(
			faroUser
		);

		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			roleName
		);

		Mockito.when(
			_roleLocalService.fetchRole(userId)
		).thenReturn(
			role
		);
	}

	private PermissionChecker _mockPermissionChecker(long userId) {
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
			permissionChecker.getUserId()
		).thenReturn(
			userId
		);

		return permissionChecker;
	}

	private static final long _CHANNEL_GROUP_ID = 100;

	private static final long _GROUP_ID = 200;

	private static final long _USER_ID_ADMINISTRATOR = 1;

	private static final long _USER_ID_MEMBER = 2;

	private static final long _USER_ID_OMNIADMIN = 3;

	private static final long _USER_ID_OUTSIDER = 4;

	private static final long _USER_ID_OWNER = 5;

	private static final long _USER_ID_PENDING_ADMINISTRATOR = 6;

	private final FaroUserLocalService _faroUserLocalService = Mockito.mock(
		FaroUserLocalService.class);
	private final RoleLocalService _roleLocalService = Mockito.mock(
		RoleLocalService.class);
	private final UserGroupRoleLocalService _userGroupRoleLocalService =
		Mockito.mock(UserGroupRoleLocalService.class);
	private final WorkspacePermission _workspacePermission =
		new WorkspacePermission();

}