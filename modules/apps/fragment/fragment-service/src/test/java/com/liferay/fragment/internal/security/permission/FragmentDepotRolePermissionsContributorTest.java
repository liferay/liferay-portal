/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.security.permission;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.role.contributor.DepotRolePermission;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Thiago Buarque
 */
public class FragmentDepotRolePermissionsContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetDepotRolePermissions() {
		FragmentDepotRolePermissionsContributor
			fragmentDepotRolePermissionsContributor =
				new FragmentDepotRolePermissionsContributor();

		List<DepotRolePermission> depotRolePermissions =
			fragmentDepotRolePermissionsContributor.getDepotRolePermissions();

		Assert.assertEquals(
			depotRolePermissions.toString(), 3, depotRolePermissions.size());

		Map<String, DepotRolePermission> depotRolePermissionsMap =
			new HashMap<>();

		for (DepotRolePermission depotRolePermission : depotRolePermissions) {
			depotRolePermissionsMap.put(
				depotRolePermission.getRoleName(), depotRolePermission);
		}

		for (String roleName :
				List.of(
					DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
					DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER,
					DepotRolesConstants.DESIGN_LIBRARY_OWNER)) {

			DepotRolePermission depotRolePermission =
				depotRolePermissionsMap.get(roleName);

			Assert.assertNotNull(roleName, depotRolePermission);
			Assert.assertEquals(
				FragmentConstants.RESOURCE_NAME,
				depotRolePermission.getResourceName());
			Assert.assertArrayEquals(
				new String[] {FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES},
				depotRolePermission.getActionKeys());
		}
	}

}