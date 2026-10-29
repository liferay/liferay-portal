/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.util;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class DepotRolesUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_rolePermissionUtilMockedStatic = Mockito.mockStatic(
			RolePermissionUtil.class);
	}

	@After
	public void tearDown() {
		_rolePermissionUtilMockedStatic.close();
	}

	@Test
	public void testFilterGroupRoles() throws Exception {
		_testFilterGroupRolesWithCompanyAdmin();
		_testFilterGroupRolesWithoutCompanyAdmin();
	}

	private PermissionChecker _mockPermissionChecker(boolean companyAdmin) {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.isCompanyAdmin()
		).thenReturn(
			companyAdmin
		);

		return permissionChecker;
	}

	private Role _mockRole(String name) {
		Role role = Mockito.mock(Role.class);

		Mockito.when(
			role.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			role.getRoleId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		return role;
	}

	private void _mockRolePermission(Role role, boolean assignMembers) {
		long roleId = role.getRoleId();

		_rolePermissionUtilMockedStatic.when(
			() -> RolePermissionUtil.contains(
				Mockito.any(PermissionChecker.class), Mockito.eq(_GROUP_ID),
				Mockito.eq(roleId), Mockito.eq(ActionKeys.ASSIGN_MEMBERS))
		).thenReturn(
			assignMembers
		);
	}

	private Role _mockRoleWithAssignMembers(String name) {
		Role role = _mockRole(name);

		_mockRolePermission(role, true);

		return role;
	}

	private void _testFilterGroupRolesWithCompanyAdmin() throws Exception {
		List<Role> roles = DepotRolesUtil.filterGroupRoles(
			_mockPermissionChecker(true), _GROUP_ID,
			Arrays.asList(
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR),
				_mockRole(
					DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER),
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER),
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_MEMBER),
				_mockRole(DepotRolesConstants.ASSET_LIBRARY_OWNER)));

		Assert.assertEquals(
			Arrays.asList(
				DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR,
				DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER,
				DepotRolesConstants.ASSET_LIBRARY_OWNER),
			ListUtil.toList(roles, Role::getName));
	}

	private void _testFilterGroupRolesWithoutCompanyAdmin() throws Exception {
		Role role = _mockRole(RandomTestUtil.randomString());

		_mockRolePermission(role, false);

		List<Role> roles = DepotRolesUtil.filterGroupRoles(
			_mockPermissionChecker(false), _GROUP_ID,
			Arrays.asList(
				_mockRoleWithAssignMembers(
					DepotRolesConstants.ASSET_LIBRARY_ADMINISTRATOR),
				_mockRoleWithAssignMembers(
					DepotRolesConstants.ASSET_LIBRARY_CONNECTED_SITE_MEMBER),
				_mockRoleWithAssignMembers(
					DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER),
				_mockRoleWithAssignMembers(
					DepotRolesConstants.ASSET_LIBRARY_MEMBER),
				_mockRoleWithAssignMembers(
					DepotRolesConstants.ASSET_LIBRARY_OWNER),
				_mockRoleWithAssignMembers(
					DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR),
				_mockRoleWithAssignMembers(
					DepotRolesConstants.DESIGN_LIBRARY_MEMBER),
				_mockRoleWithAssignMembers(
					DepotRolesConstants.DESIGN_LIBRARY_OWNER),
				_mockRoleWithAssignMembers(DepotRolesConstants.PROJECT_MEMBER),
				role));

		Assert.assertEquals(
			Arrays.asList(DepotRolesConstants.ASSET_LIBRARY_CONTENT_REVIEWER),
			ListUtil.toList(roles, Role::getName));
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private MockedStatic<RolePermissionUtil> _rolePermissionUtilMockedStatic;

}