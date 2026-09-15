/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.depot.util.DepotRoleUtil;
import com.liferay.portal.kernel.service.UserGroupGroupRoleService;
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
public class DepotUserGroupGroupRoleServiceWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddUserGroupGroupRoles() throws Exception {
		DepotUserGroupGroupRoleServiceWrapper
			depotUserGroupGroupRoleServiceWrapper =
				new DepotUserGroupGroupRoleServiceWrapper();

		UserGroupGroupRoleService userGroupGroupRoleService = Mockito.mock(
			UserGroupGroupRoleService.class);

		depotUserGroupGroupRoleServiceWrapper.setWrappedService(
			userGroupGroupRoleService);

		try (MockedStatic<DepotRoleUtil> depotRoleUtilMockedStatic =
				Mockito.mockStatic(DepotRoleUtil.class)) {

			long groupId = RandomTestUtil.randomLong();
			long[] roleIds = {RandomTestUtil.randomLong()};
			long userGroupId = RandomTestUtil.randomLong();

			depotUserGroupGroupRoleServiceWrapper.addUserGroupGroupRoles(
				userGroupId, groupId, roleIds);

			depotRoleUtilMockedStatic.verify(
				() -> DepotRoleUtil.validate(groupId, roleIds));

			Mockito.verify(
				userGroupGroupRoleService
			).addUserGroupGroupRoles(
				userGroupId, groupId, roleIds
			);

			long roleId = RandomTestUtil.randomLong();
			long[] userGroupIds = {userGroupId};

			depotUserGroupGroupRoleServiceWrapper.addUserGroupGroupRoles(
				userGroupIds, groupId, roleId);

			depotRoleUtilMockedStatic.verify(
				() -> DepotRoleUtil.validate(groupId, new long[] {roleId}));

			Mockito.verify(
				userGroupGroupRoleService
			).addUserGroupGroupRoles(
				userGroupIds, groupId, roleId
			);
		}
	}

}