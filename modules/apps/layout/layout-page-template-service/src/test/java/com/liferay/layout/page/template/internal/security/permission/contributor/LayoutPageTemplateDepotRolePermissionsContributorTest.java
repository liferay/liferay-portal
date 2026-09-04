/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.security.permission.contributor;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.security.permission.contributor.DepotRolePermission;
import com.liferay.layout.page.template.constants.LayoutPageTemplateActionKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Georgel Pop
 */
public class LayoutPageTemplateDepotRolePermissionsContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-104558")
	public void testGetDepotRolePermissions() {
		LayoutPageTemplateDepotRolePermissionsContributor
			layoutPageTemplateDepotRolePermissionsContributor =
				new LayoutPageTemplateDepotRolePermissionsContributor();

		List<DepotRolePermission> depotRolePermissions =
			layoutPageTemplateDepotRolePermissionsContributor.
				getDepotRolePermissions();

		Assert.assertEquals(
			depotRolePermissions.toString(), 12, depotRolePermissions.size());

		Iterator<DepotRolePermission> iterator =
			depotRolePermissions.iterator();

		for (String roleName :
				List.of(
					DepotRolesConstants.DESIGN_LIBRARY_ADMINISTRATOR,
					DepotRolesConstants.DESIGN_LIBRARY_CONTENT_REVIEWER,
					DepotRolesConstants.DESIGN_LIBRARY_OWNER)) {

			_assertDepotRolePermission(
				iterator.next(), roleName,
				LayoutPageTemplateConstants.RESOURCE_NAME,
				LayoutPageTemplateActionKeys.
					ADD_LAYOUT_PAGE_TEMPLATE_COLLECTION,
				LayoutPageTemplateActionKeys.ADD_LAYOUT_PAGE_TEMPLATE_ENTRY);
			_assertDepotRolePermission(
				iterator.next(), roleName,
				LayoutPageTemplateCollection.class.getName(), ActionKeys.DELETE,
				ActionKeys.UPDATE);
			_assertDepotRolePermission(
				iterator.next(), roleName,
				LayoutPageTemplateEntry.class.getName(), ActionKeys.DELETE,
				ActionKeys.UPDATE);
			_assertDepotRolePermission(
				iterator.next(), roleName, Layout.class.getName(),
				ActionKeys.UPDATE);
		}
	}

	private void _assertDepotRolePermission(
		DepotRolePermission depotRolePermission, String roleName,
		String resourceName, String... actionKeys) {

		Assert.assertEquals(roleName, depotRolePermission.getRoleName());
		Assert.assertEquals(
			resourceName, depotRolePermission.getResourceName());
		Assert.assertArrayEquals(
			actionKeys, depotRolePermission.getActionKeys());
	}

}