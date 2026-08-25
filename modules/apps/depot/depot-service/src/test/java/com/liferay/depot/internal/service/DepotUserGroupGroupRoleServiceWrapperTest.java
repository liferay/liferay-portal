/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.exception.RoleSubtypeException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupGroupRoleService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() {
		_depotUserGroupGroupRoleServiceWrapper =
			new DepotUserGroupGroupRoleServiceWrapper();

		_depotUserGroupGroupRoleServiceWrapper.setWrappedService(
			Mockito.mock(UserGroupGroupRoleService.class));
	}

	@Test
	public void testAddUserGroupGroupRoles() throws Exception {
		try (MockedStatic<DepotEntryLocalServiceUtil>
				depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
					DepotEntryLocalServiceUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class);
			MockedStatic<RoleLocalServiceUtil>
				roleLocalServiceUtilMockedStatic = Mockito.mockStatic(
					RoleLocalServiceUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				true
			);

			DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

			Mockito.when(
				depotEntry.getType()
			).thenReturn(
				DepotConstants.TYPE_PROJECT
			);

			depotEntryLocalServiceUtilMockedStatic.when(
				() -> DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
					_PROJECT_GROUP_ID)
			).thenReturn(
				depotEntry
			);

			long designLibraryRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic,
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY);

			_assertAddUserGroupGroupRoles(designLibraryRoleId, false);

			long projectRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic,
				DepotRolesConstants.SUBTYPE_PROJECT);

			_assertAddUserGroupGroupRoles(projectRoleId, true);

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				false
			);

			_assertAddUserGroupGroupRoles(designLibraryRoleId, true);
		}
	}

	private void _assertAddUserGroupGroupRoles(long roleId, boolean valid)
		throws Exception {

		try {
			_depotUserGroupGroupRoleServiceWrapper.addUserGroupGroupRoles(
				_USER_GROUP_ID, _PROJECT_GROUP_ID, new long[] {roleId});

			if (!valid) {
				Assert.fail();
			}
		}
		catch (RoleSubtypeException roleSubtypeException) {
			if (valid) {
				throw roleSubtypeException;
			}
		}

		try {
			_depotUserGroupGroupRoleServiceWrapper.addUserGroupGroupRoles(
				new long[] {_USER_GROUP_ID}, _PROJECT_GROUP_ID, roleId);

			if (!valid) {
				Assert.fail();
			}
		}
		catch (RoleSubtypeException roleSubtypeException) {
			if (valid) {
				throw roleSubtypeException;
			}
		}
	}

	private long _mockRole(
		MockedStatic<RoleLocalServiceUtil> mockedStatic, String subtype) {

		long roleId = RandomTestUtil.randomLong();

		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getRoleId()
		).thenReturn(
			roleId
		);

		Mockito.when(
			role.getSubtype()
		).thenReturn(
			subtype
		);

		Mockito.when(
			role.getType()
		).thenReturn(
			RoleConstants.TYPE_DEPOT
		);

		mockedStatic.when(
			() -> RoleLocalServiceUtil.getRole(roleId)
		).thenReturn(
			role
		);

		return roleId;
	}

	private static final long _PROJECT_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _USER_GROUP_ID = RandomTestUtil.randomLong();

	private DepotUserGroupGroupRoleServiceWrapper
		_depotUserGroupGroupRoleServiceWrapper;

}