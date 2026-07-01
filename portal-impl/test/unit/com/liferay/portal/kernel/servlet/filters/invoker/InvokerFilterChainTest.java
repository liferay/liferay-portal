/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eric Yan
 */
public class InvokerFilterChainTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDoFilterWithLiferayFilterWhenIsFilterEnabledThrowsException()
		throws Exception {

		InvokerFilterChain invokerFilterChain = new InvokerFilterChain(
			new MockFilterChain());

		invokerFilterChain.addFilter(
			new BasePortalFilter() {
			});

		RuntimeException runtimeException = new RuntimeException();

		invokerFilterChain.addFilter(
			new BasePortalFilter() {

				@Override
				public boolean isFilterEnabled(
					HttpServletRequest httpServletRequest,
					HttpServletResponse httpServletResponse) {

					throw runtimeException;
				}

			});

		try {
			invokerFilterChain.doFilter(
				new MockHttpServletRequest(), new MockHttpServletResponse());

			Assert.fail();
		}
		catch (ServletException servletException) {
			Assert.assertSame(
				runtimeException, servletException.getRootCause());
		}
	}

}