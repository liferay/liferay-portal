/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.security.permission.contributor;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.security.permission.contributor.DepotRolePermission;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookConstants;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Thiago Buarque
 */
public class StyleBookDepotRolePermissionsContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-96528")
	public void testGetDepotRolePermissions() {
		StyleBookDepotRolePermissionsContributor
			styleBookDepotRolePermissionsContributor =
				new StyleBookDepotRolePermissionsContributor();

		List<String> expectedRoleNames = ListUtil.fromArray(
			DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
			DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER,
			DepotRolesConstants.DESIGN_LIBRARY_OWNER);

		for (DepotRolePermission depotRolePermission :
				styleBookDepotRolePermissionsContributor.
					getDepotRolePermissions()) {

			Assert.assertTrue(
				depotRolePermission.getRoleName(),
				expectedRoleNames.remove(depotRolePermission.getRoleName()));
			Assert.assertEquals(
				StyleBookConstants.RESOURCE_NAME,
				depotRolePermission.getResourceName());
			Assert.assertArrayEquals(
				new String[] {StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES},
				depotRolePermission.getActionKeys());
		}

		Assert.assertTrue(
			expectedRoleNames.toString(), expectedRoleNames.isEmpty());
	}

}