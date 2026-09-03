/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.cmp.project.util;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Marcos Martins
 */
public class CMPProjectUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetFilterStringWithCMPProjectIds() {
		Assert.assertEquals(
			"cmpProjects/id in ('39601')",
			CMPProjectUtil.getFilterString(new Long[] {39601L}, null));
		Assert.assertEquals(
			"cmpProjects/id in ('39601', '39602')",
			CMPProjectUtil.getFilterString(new Long[] {39601L, 39602L}, null));
	}

	@Test
	public void testGetFilterStringWithFilterString() {
		Assert.assertEquals(
			"(assetType eq 'blog') and cmpProjects/id in ('39601')",
			CMPProjectUtil.getFilterString(
				new Long[] {39601L}, "assetType eq 'blog'"));
	}

	@Test
	public void testGetFilterStringWithoutCMPProjectIds() {
		Assert.assertEquals(
			"assetType eq 'blog'",
			CMPProjectUtil.getFilterString(null, "assetType eq 'blog'"));
		Assert.assertNull(CMPProjectUtil.getFilterString(new Long[0], null));
		Assert.assertNull(CMPProjectUtil.getFilterString(null, null));
	}

}