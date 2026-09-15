/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.portal.kernel.service.UserGroupRoleService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lianne Louie
 */
public class DepotUserGroupRoleServiceWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddUserGroupRoles() throws Exception {
		DepotUserGroupRoleServiceWrapper depotUserGroupRoleServiceWrapper =
			new DepotUserGroupRoleServiceWrapper();

		UserGroupRoleService userGroupRoleService = Mockito.mock(
			UserGroupRoleService.class);

		depotUserGroupRoleServiceWrapper.setWrappedService(
			userGroupRoleService);

		try (MockedStatic<DepotRoleUtil> depotRoleUtilMockedStatic =
				Mockito.mockStatic(DepotRoleUtil.class)) {

			long groupId = RandomTestUtil.randomLong();
			long[] roleIds = {RandomTestUtil.randomLong()};
			long userId = RandomTestUtil.randomLong();

			depotUserGroupRoleServiceWrapper.addUserGroupRoles(
				userId, groupId, roleIds);

			depotRoleUtilMockedStatic.verify(
				() -> DepotRoleUtil.validate(groupId, roleIds));

			Mockito.verify(
				userGroupRoleService
			).addUserGroupRoles(
				userId, groupId, roleIds
			);

			long roleId = RandomTestUtil.randomLong();
			long[] userIds = {userId};

			depotUserGroupRoleServiceWrapper.addUserGroupRoles(
				userIds, groupId, roleId);

			depotRoleUtilMockedStatic.verify(
				() -> DepotRoleUtil.validate(groupId, new long[] {roleId}));

			Mockito.verify(
				userGroupRoleService
			).addUserGroupRoles(
				userIds, groupId, roleId
			);
		}
	}

	@Test
	public void testUpdateUserGroupRoles() throws Exception {
		DepotUserGroupRoleServiceWrapper depotUserGroupRoleServiceWrapper =
			new DepotUserGroupRoleServiceWrapper();

		UserGroupRoleService userGroupRoleService = Mockito.mock(
			UserGroupRoleService.class);

		depotUserGroupRoleServiceWrapper.setWrappedService(
			userGroupRoleService);

		try (MockedStatic<DepotRoleUtil> depotRoleUtilMockedStatic =
				Mockito.mockStatic(DepotRoleUtil.class)) {

			long[] addedRoleIds = {RandomTestUtil.randomLong()};
			long[] deletedRoleIds = {RandomTestUtil.randomLong()};
			long groupId = RandomTestUtil.randomLong();
			long userId = RandomTestUtil.randomLong();

			depotUserGroupRoleServiceWrapper.updateUserGroupRoles(
				userId, groupId, addedRoleIds, deletedRoleIds);

			depotRoleUtilMockedStatic.verify(
				() -> DepotRoleUtil.validate(groupId, addedRoleIds));

			Mockito.verify(
				userGroupRoleService
			).updateUserGroupRoles(
				userId, groupId, addedRoleIds, deletedRoleIds
			);
		}
	}

}