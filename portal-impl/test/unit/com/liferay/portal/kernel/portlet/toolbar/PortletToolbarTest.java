/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.toolbar;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.toolbar.contributor.locator.PortletToolbarContributorLocator;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.servlet.taglib.ui.Menu;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Leon Chi
 */
public class PortletToolbarTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetPortletTitleMenus() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Menu testMenu = new Menu();

		ServiceRegistration<PortletToolbarContributorLocator>
			serviceRegistration = bundleContext.registerService(
				PortletToolbarContributorLocator.class,
				(portletId, portletRequest) -> Collections.singletonList(
					(portletRequest1, portletResponse) ->
						Collections.singletonList(testMenu)),
				null);

		PortletRequest portletRequest = new MockPortletRequest();

		portletRequest.setAttribute(
			PortletServlet.PORTLET_SERVLET_REQUEST,
			new MockHttpServletRequest());

		List<Menu> menus = PortletToolbar.INSTANCE.getPortletTitleMenus(
			RandomTestUtil.randomString(), portletRequest,
			ProxyFactory.newDummyInstance(PortletResponse.class));

		Assert.assertTrue(
			"Unable to find " + testMenu,
			menus.removeIf(menu -> testMenu == menu));

		serviceRegistration.unregister();
	}

}