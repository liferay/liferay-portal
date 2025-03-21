/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.secure;

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
public class SecureFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSecureFilterIsEnabledIfDisabled() {
		PropsUtil.set(SecureFilter.class.getName(), "false");

		SecureFilter secureFilter = new SecureFilter();

		secureFilter.init(_filterConfig);

		Assert.assertTrue(secureFilter.isFilterEnabled());
	}

	@Test
	public void testSecureFilterIsEnabledIfEnabled() {
		PropsUtil.set(SecureFilter.class.getName(), "true");

		SecureFilter secureFilter = new SecureFilter();

		secureFilter.init(_filterConfig);

		Assert.assertTrue(secureFilter.isFilterEnabled());
	}

	private final FilterConfig _filterConfig = ProxyFactory.newDummyInstance(
		FilterConfig.class);

}