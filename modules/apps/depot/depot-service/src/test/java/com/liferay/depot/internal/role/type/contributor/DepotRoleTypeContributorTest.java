/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.role.type.contributor;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.internal.roles.admin.role.type.contributor.DepotRoleTypeContributor;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Cristina González
 * @author Thiago Buarque
 */
public class DepotRoleTypeContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetExcludedRoleNames() {
		Assert.assertArrayEquals(
			new String[] {
				DepotRolesConstants.ASSET_LIBRARY_OWNER,
				DepotRolesConstants.DESIGN_LIBRARY_OWNER
			},
			_depotRoleTypeContributor.getExcludedRoleNames());
	}

	@Test
	public void testGetSubtypes() {
		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-17564"))
			).thenReturn(
				false
			);

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-58677"))
			).thenReturn(
				false
			);

			Assert.assertArrayEquals(
				new String[0], _depotRoleTypeContributor.getSubtypes());

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-58677"))
			).thenReturn(
				true
			);

			Assert.assertArrayEquals(
				new String[] {DepotRolesConstants.SUBTYPE_PROJECT},
				_depotRoleTypeContributor.getSubtypes());

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-17564"))
			).thenReturn(
				true
			);

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-58677"))
			).thenReturn(
				false
			);

			Assert.assertArrayEquals(
				new String[] {DepotRolesConstants.SUBTYPE_SPACE},
				_depotRoleTypeContributor.getSubtypes());

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-58677"))
			).thenReturn(
				true
			);

			Assert.assertArrayEquals(
				new String[] {
					DepotRolesConstants.SUBTYPE_PROJECT,
					DepotRolesConstants.SUBTYPE_SPACE
				},
				_depotRoleTypeContributor.getSubtypes());

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-57283"))
			).thenReturn(
				true
			);

			Assert.assertArrayEquals(
				new String[] {
					DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
					DepotRolesConstants.SUBTYPE_PROJECT,
					DepotRolesConstants.SUBTYPE_SPACE
				},
				_depotRoleTypeContributor.getSubtypes());
		}
	}

	@Test
	public void testIsAllowAssignMembers() {
		_testIsAllowAssignMembersWithAdministrator();
		_testIsAllowAssignMembersWithMember();
		_testIsAllowAssignMembersWithOwner();
	}

	@Test
	public void testIsAllowDefinePermissions() {
		_testIsAllowDefinePermissionsWithAdministrator();
		_testIsAllowDefinePermissionsWithMember();
		_testIsAllowDefinePermissionsWithOwner();
	}

	@Test
	public void testIsAllowDelete() {
		_testIsAllowDeleteWithAdministrator();
		_testIsAllowDeleteWithDesignLibraryAdministrator();
		_testIsAllowDeleteWithDesignLibraryContentReviewer();
		_testIsAllowDeleteWithDesignLibraryMember();
		_testIsAllowDeleteWithDesignLibraryOwner();
		_testIsAllowDeleteWithMember();
		_testIsAllowDeleteWithOwner();
	}

	@Test
	public void testIsAutomaticallyAssignedWithDesignLibraryMember() {
		Assert.assertTrue(
			_depotRoleTypeContributor.isAutomaticallyAssigned(
				_mockRole(DepotRolesConstants.DESIGN_LIBRARY_MEMBER)));
	}

	private Role _mockRole(String name) {
		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			name
		);

		return role;
	}

	private void _testIsAllowAssignMembersWithAdministrator() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowAssignMembers(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR)));
	}

	private void _testIsAllowAssignMembersWithMember() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowAssignMembers(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_MEMBER)));
	}

	private void _testIsAllowAssignMembersWithOwner() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowAssignMembers(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_OWNER)));
	}

	private void _testIsAllowDefinePermissionsWithAdministrator() {
		Assert.assertTrue(
			_depotRoleTypeContributor.isAllowDefinePermissions(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR)));
	}

	private void _testIsAllowDefinePermissionsWithMember() {
		Assert.assertTrue(
			_depotRoleTypeContributor.isAllowDefinePermissions(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_MEMBER)));
	}

	private void _testIsAllowDefinePermissionsWithOwner() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowDefinePermissions(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_OWNER)));
	}

	private void _testIsAllowDeleteWithAdministrator() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowDelete(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR)));
	}

	private void _testIsAllowDeleteWithDesignLibraryAdministrator() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowDelete(
				_mockRole(DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR)));
	}

	private void _testIsAllowDeleteWithDesignLibraryContentReviewer() {
		Assert.assertTrue(
			_depotRoleTypeContributor.isAllowDelete(
				_mockRole(
					DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER)));
	}

	private void _testIsAllowDeleteWithDesignLibraryMember() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowDelete(
				_mockRole(DepotRolesConstants.DESIGN_LIBRARY_MEMBER)));
	}

	private void _testIsAllowDeleteWithDesignLibraryOwner() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowDelete(
				_mockRole(DepotRolesConstants.DESIGN_LIBRARY_OWNER)));
	}

	private void _testIsAllowDeleteWithMember() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowDelete(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_MEMBER)));
	}

	private void _testIsAllowDeleteWithOwner() {
		Assert.assertFalse(
			_depotRoleTypeContributor.isAllowDelete(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_OWNER)));
	}

	private final DepotRoleTypeContributor _depotRoleTypeContributor =
		new DepotRoleTypeContributor();

}