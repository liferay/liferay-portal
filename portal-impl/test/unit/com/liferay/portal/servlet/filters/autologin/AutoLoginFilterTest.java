/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.autologin;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.BaseAutoLogin;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Leon Chi
 */
public class AutoLoginFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDoFilter() throws IOException, ServletException {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		boolean[] calledLogin = {false};

		ServiceRegistration<AutoLogin> serviceRegistration =
			bundleContext.registerService(
				AutoLogin.class,
				new BaseAutoLogin() {

					@Override
					protected String[] doLogin(
						HttpServletRequest httpServletRequest,
						HttpServletResponse httpServletResponse) {

						calledLogin[0] = true;

						return null;
					}

				},
				null);

		AutoLoginFilter autoLoginFilter = new AutoLoginFilter();

		autoLoginFilter.doFilter(
			new HttpServletRequestWrapper(
				ProxyFactory.newDummyInstance(HttpServletRequest.class)) {

				@Override
				public String getRequestURI() {
					return StringPool.BLANK;
				}

				@Override
				public HttpSession getSession() {
					return ProxyFactory.newDummyInstance(HttpSession.class);
				}

			},
			null, ProxyFactory.newDummyInstance(FilterChain.class));

		try {
			Assert.assertTrue("Login method should be invoked", calledLogin[0]);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

}