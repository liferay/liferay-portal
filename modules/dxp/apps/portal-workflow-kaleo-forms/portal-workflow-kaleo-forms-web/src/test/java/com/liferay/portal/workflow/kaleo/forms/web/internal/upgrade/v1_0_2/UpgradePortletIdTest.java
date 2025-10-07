/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.web.internal.upgrade.v1_0_2;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Inácio Nery
 */
public class UpgradePortletIdTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_upgradePortletId = new UpgradePortletId();
	}

	@Test
	public void testOldTypeSettingsShouldBeUpdate1() {
		String oldTypeSettings =
			"column-1=1_WAR_kaleoformsportlet,\ncolumn-2=" + _PORTLET_KEY;

		String newTypeSettings = _upgradePortletId.getNewTypeSettings(
			oldTypeSettings, "1_WAR_kaleoformsportlet");

		String expectedTypeSettings = "column-2=" + _PORTLET_KEY + "\n";

		Assert.assertEquals(expectedTypeSettings, newTypeSettings);
	}

	@Test
	public void testOldTypeSettingsShouldBeUpdate2() {
		String oldTypeSettings =
			"column-1=" + _PORTLET_KEY + ",\ncolumn-2=1_WAR_kaleoformsportlet";

		String newTypeSettings = _upgradePortletId.getNewTypeSettings(
			oldTypeSettings, "1_WAR_kaleoformsportlet");

		String expectedTypeSettings = "column-1=" + _PORTLET_KEY + ",\n";

		Assert.assertEquals(expectedTypeSettings, newTypeSettings);
	}

	private static final String _PORTLET_KEY = RandomTestUtil.randomString();

	private UpgradePortletId _upgradePortletId;

}