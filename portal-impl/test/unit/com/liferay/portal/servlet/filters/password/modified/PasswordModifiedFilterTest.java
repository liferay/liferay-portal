/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.password.modified;

import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Christopher Kian
 */
public class PasswordModifiedFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testProcessFilter() throws Exception {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(
			new PortalImpl() {

				@Override
				public String getPathContext() {
					return "/test";
				}

			});

		PasswordModifiedFilter passwordModifiedFilter =
			new PasswordModifiedFilter();

		FilterChain filterChain = Mockito.mock(FilterChain.class);

		passwordModifiedFilter.processFilter(
			new HttpServletRequestWrapper(
				ProxyFactory.newDummyInstance(HttpServletRequest.class)) {

				@Override
				public String getContextPath() {
					return "/c/bad/context/path";
				}

				@Override
				public String getRequestURI() {
					return "/test/path";
				}

			},
			null, filterChain);

		Mockito.verify(
			filterChain
		).doFilter(
			Mockito.any(), Mockito.any()
		);
	}

	@Test
	public void testProcessFilterWithPortalProxyPath() throws Exception {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(
			new PortalImpl() {

				@Override
				public String getPathContext() {
					return "/proxy/test";
				}

				@Override
				public String getPathProxy() {
					return "/proxy";
				}

			});

		PasswordModifiedFilter passwordModifiedFilter =
			new PasswordModifiedFilter();

		FilterChain filterChain = Mockito.mock(FilterChain.class);

		passwordModifiedFilter.processFilter(
			new HttpServletRequestWrapper(
				ProxyFactory.newDummyInstance(HttpServletRequest.class)) {

				@Override
				public String getContextPath() {
					return "/c/bad/context/path";
				}

				@Override
				public String getRequestURI() {
					return "/test/path";
				}

			},
			null, filterChain);

		Mockito.verify(
			filterChain
		).doFilter(
			Mockito.any(), Mockito.any()
		);
	}

}