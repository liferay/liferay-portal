/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.sharepoint;

import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PropsUtil;

import jakarta.servlet.FilterConfig;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mariano Álvaro Sáiz
 */
public class SharepointFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSharepointFilterIsDisabled() {
		PropsUtil.set(SharepointFilter.class.getName(), "false");

		SharepointFilter sharepointFilter = new SharepointFilter();

		sharepointFilter.init(_filterConfig);

		Assert.assertFalse(sharepointFilter.isFilterEnabled());
	}

	@Test
	public void testSharepointFilterIsEnabled() {
		PropsUtil.set(SharepointFilter.class.getName(), "true");

		SharepointFilter sharepointFilter = new SharepointFilter();

		sharepointFilter.init(_filterConfig);

		Assert.assertTrue(sharepointFilter.isFilterEnabled());
	}

	private final FilterConfig _filterConfig = ProxyFactory.newDummyInstance(
		FilterConfig.class);

}