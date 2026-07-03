/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.constants;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Stefano Motta
 */
public class DepotRolesConstantsTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetSubtype() {
		Assert.assertNull(
			DepotRolesConstants.getSubtype(DepotConstants.TYPE_ASSET_LIBRARY));
		Assert.assertEquals(
			DepotRolesConstants.SUBTYPE_CATALOG,
			DepotRolesConstants.getSubtype(DepotConstants.TYPE_CATALOG));
		Assert.assertEquals(
			DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY,
			DepotRolesConstants.getSubtype(DepotConstants.TYPE_DESIGN_LIBRARY));
		Assert.assertEquals(
			DepotRolesConstants.SUBTYPE_PROJECT,
			DepotRolesConstants.getSubtype(DepotConstants.TYPE_PROJECT));
		Assert.assertEquals(
			DepotRolesConstants.SUBTYPE_SPACE,
			DepotRolesConstants.getSubtype(DepotConstants.TYPE_SPACE));
		Assert.assertNull(DepotRolesConstants.getSubtype(0));
	}

}