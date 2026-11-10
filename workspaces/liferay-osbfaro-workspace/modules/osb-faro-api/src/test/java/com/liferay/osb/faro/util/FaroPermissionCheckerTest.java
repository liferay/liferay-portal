/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.util;

import com.liferay.osb.faro.constants.FaroChannelConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.service.FaroUserLocalServiceUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class FaroPermissionCheckerTest {

	@Before
	public void setUp() {
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
	public void testHasFaroChannelAccessPermissionWithAllUsersFaroChannel() {
		FaroChannel faroChannel = _mockFaroChannel(
			FaroChannelConstants.PERMISSION_ALL_USERS);

		_setPermissionChecker(_USER_ID_MEMBER);

		Assert.assertTrue(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));

		_setPermissionChecker(_USER_ID_OUTSIDER);

		Assert.assertTrue(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));
	}

	@Test
	public void testHasFaroChannelAccessPermissionWithSelectUsersFaroChannel() {
		FaroChannel faroChannel = _mockFaroChannel(
			FaroChannelConstants.PERMISSION_SELECT_USERS);

		_setPermissionChecker(_USER_ID_ADMINISTRATOR);

		Assert.assertTrue(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));

		_setPermissionChecker(_USER_ID_MEMBER);

		Assert.assertFalse(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));

		_setPermissionChecker(_USER_ID_OMNIADMIN);

		Assert.assertTrue(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));

		_setPermissionChecker(_USER_ID_OUTSIDER);

		Assert.assertFalse(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));

		_setPermissionChecker(_USER_ID_OWNER);

		Assert.assertTrue(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));

		_setPermissionChecker(_USER_ID_PENDING_ADMINISTRATOR);

		Assert.assertFalse(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));

		_userGroupRoleLocalServiceUtilMockedStatic.when(
			() -> UserGroupRoleLocalServiceUtil.getUserGroupRoles(
				_USER_ID_MEMBER, _GROUP_ID_CHANNEL)
		).thenReturn(
			List.of(Mockito.mock(UserGroupRole.class))
		);

		_setPermissionChecker(_USER_ID_MEMBER);

		Assert.assertTrue(
			FaroPermissionChecker.hasFaroChannelAccessPermission(faroChannel));
	}

	@Test
	public void testIsGroupMember() {
		Assert.assertTrue(
			FaroPermissionChecker.isGroupMember(
				_GROUP_ID_WORKSPACE,
				_mockPermissionChecker(_USER_ID_ADMINISTRATOR)));
		Assert.assertTrue(
			FaroPermissionChecker.isGroupMember(
				_GROUP_ID_WORKSPACE, _mockPermissionChecker(_USER_ID_MEMBER)));
		Assert.assertTrue(
			FaroPermissionChecker.isGroupMember(
				_GROUP_ID_WORKSPACE,
				_mockPermissionChecker(_USER_ID_OMNIADMIN)));
		Assert.assertFalse(
			FaroPermissionChecker.isGroupMember(
				_GROUP_ID_WORKSPACE,
				_mockPermissionChecker(_USER_ID_OUTSIDER)));
		Assert.assertTrue(
			FaroPermissionChecker.isGroupMember(
				_GROUP_ID_WORKSPACE, _mockPermissionChecker(_USER_ID_OWNER)));
		Assert.assertFalse(
			FaroPermissionChecker.isGroupMember(
				_GROUP_ID_WORKSPACE,
				_mockPermissionChecker(_USER_ID_PENDING_ADMINISTRATOR)));
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

	private FaroUser _mockFaroUser(String roleName, int status, long userId) {
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

	private void _setPermissionChecker(long userId) {
		PermissionThreadLocal.setPermissionChecker(
			_mockPermissionChecker(userId));
	}

	private static final long _GROUP_ID_CHANNEL = RandomTestUtil.randomLong();

	private static final long _GROUP_ID_WORKSPACE = RandomTestUtil.randomLong();

	private static final long _USER_ID_ADMINISTRATOR =
		RandomTestUtil.randomLong();

	private static final long _USER_ID_MEMBER = RandomTestUtil.randomLong();

	private static final long _USER_ID_OMNIADMIN = RandomTestUtil.randomLong();

	private static final long _USER_ID_OUTSIDER = RandomTestUtil.randomLong();

	private static final long _USER_ID_OWNER = RandomTestUtil.randomLong();

	private static final long _USER_ID_PENDING_ADMINISTRATOR =
		RandomTestUtil.randomLong();

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

}