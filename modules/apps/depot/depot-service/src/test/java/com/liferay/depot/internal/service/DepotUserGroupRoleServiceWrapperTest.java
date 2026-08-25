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
import com.liferay.portal.kernel.service.UserGroupRoleService;
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
public class DepotUserGroupRoleServiceWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_depotUserGroupRoleServiceWrapper =
			new DepotUserGroupRoleServiceWrapper();

		_depotUserGroupRoleServiceWrapper.setWrappedService(
			Mockito.mock(UserGroupRoleService.class));
	}

	@Test
	public void testAddUserGroupRoles() throws Exception {
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

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic, _ASSET_LIBRARY_GROUP_ID,
				DepotConstants.TYPE_ASSET_LIBRARY);

			long designLibraryRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic, RoleConstants.TYPE_DEPOT,
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY);

			_assertAddUserGroupRoles(
				_ASSET_LIBRARY_GROUP_ID, designLibraryRoleId, true);

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic,
				_DESIGN_LIBRARY_GROUP_ID, DepotConstants.TYPE_DESIGN_LIBRARY);

			long projectRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic, RoleConstants.TYPE_DEPOT,
				DepotRolesConstants.SUBTYPE_PROJECT);

			_assertAddUserGroupRoles(
				_DESIGN_LIBRARY_GROUP_ID, designLibraryRoleId, true);
			_assertAddUserGroupRoles(
				_DESIGN_LIBRARY_GROUP_ID, projectRoleId, false);

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic, _PROJECT_GROUP_ID,
				DepotConstants.TYPE_PROJECT);

			long siteRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic, RoleConstants.TYPE_SITE,
				null);
			long untaggedRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic, RoleConstants.TYPE_DEPOT,
				null);

			_assertAddUserGroupRoles(_PROJECT_GROUP_ID, projectRoleId, true);
			_assertAddUserGroupRoles(
				_PROJECT_GROUP_ID, designLibraryRoleId, false);
			_assertAddUserGroupRoles(_PROJECT_GROUP_ID, siteRoleId, true);
			_assertAddUserGroupRoles(_PROJECT_GROUP_ID, untaggedRoleId, false);

			_mockDepotEntry(
				depotEntryLocalServiceUtilMockedStatic, _SPACE_GROUP_ID,
				DepotConstants.TYPE_SPACE);

			long spaceRoleId = _mockRole(
				roleLocalServiceUtilMockedStatic, RoleConstants.TYPE_DEPOT,
				DepotRolesConstants.SUBTYPE_SPACE);

			_assertAddUserGroupRoles(_SPACE_GROUP_ID, spaceRoleId, true);

			_assertAddUserGroupRoles(_SPACE_GROUP_ID, projectRoleId, false);
			_assertAddUserGroupRoles(_SPACE_GROUP_ID, untaggedRoleId, true);

			_assertAddUserGroupRoles(_SITE_GROUP_ID, designLibraryRoleId, true);

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-96750"))
			).thenReturn(
				false
			);

			_assertAddUserGroupRoles(
				_PROJECT_GROUP_ID, designLibraryRoleId, true);
		}
	}

	private void _assertAddUserGroupRoles(
			long groupId, long roleId, boolean valid)
		throws Exception {

		try {
			_depotUserGroupRoleServiceWrapper.addUserGroupRoles(
				_USER_ID, groupId, new long[] {roleId});

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
			_depotUserGroupRoleServiceWrapper.addUserGroupRoles(
				new long[] {_USER_ID}, groupId, roleId);

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
			_depotUserGroupRoleServiceWrapper.updateUserGroupRoles(
				_USER_ID, groupId, new long[] {roleId}, new long[0]);

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

	private void _mockDepotEntry(
		MockedStatic<DepotEntryLocalServiceUtil> mockedStatic, long groupId,
		int type) {

		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			type
		);

		mockedStatic.when(
			() -> DepotEntryLocalServiceUtil.fetchGroupDepotEntry(groupId)
		).thenReturn(
			depotEntry
		);
	}

	private long _mockRole(
		MockedStatic<RoleLocalServiceUtil> mockedStatic, int type,
		String subtype) {

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
			type
		);

		mockedStatic.when(
			() -> RoleLocalServiceUtil.getRole(roleId)
		).thenReturn(
			role
		);

		return roleId;
	}

	private static final long _ASSET_LIBRARY_GROUP_ID =
		RandomTestUtil.randomLong();

	private static final long _DESIGN_LIBRARY_GROUP_ID =
		RandomTestUtil.randomLong();

	private static final long _PROJECT_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _SITE_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _SPACE_GROUP_ID = RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private DepotUserGroupRoleServiceWrapper _depotUserGroupRoleServiceWrapper;

}