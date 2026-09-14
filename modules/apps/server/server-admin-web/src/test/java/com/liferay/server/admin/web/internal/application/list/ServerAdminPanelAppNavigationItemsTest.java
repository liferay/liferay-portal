/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.test.util.BasePanelAppNavigationItemsTestCase;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Mario Leandro
 */
public class ServerAdminPanelAppNavigationItemsTest
	extends BasePanelAppNavigationItemsTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ReflectionTestUtil.setFieldValue(
			_serverAdminPanelApp, "_language", language);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		List<PanelAppNavigationItem> panelAppNavigationItems =
			_serverAdminPanelApp.getPanelAppNavigationItems(httpServletRequest);
		String[] expectedTabs1Names = {
			"resources", "log-levels", "properties", "database-migration",
			"document-migration", "external-services", "friendly-urls",
			"script", "shutdown", "production-readiness"
		};

		assertCanonicalNames(panelAppNavigationItems, expectedTabs1Names);

		for (int i = 0; i < expectedTabs1Names.length; i++) {
			PanelAppNavigationItem panelAppNavigationItem =
				panelAppNavigationItems.get(i);

			assertParameterValue(
				"/server_admin/view", panelAppNavigationItem,
				"mvcRenderCommandName");
			assertParameterValue(
				expectedTabs1Names[i], panelAppNavigationItem, "tabs1");
		}
	}

	private final ServerAdminPanelApp _serverAdminPanelApp =
		new ServerAdminPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

}