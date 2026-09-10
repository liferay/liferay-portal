/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.application.list;

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
public class DispatchPanelAppNavigationItemsTest
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
			_dispatchPanelApp, "_language", language);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		List<PanelAppNavigationItem> panelAppNavigationItems =
			_dispatchPanelApp.getPanelAppNavigationItems(httpServletRequest);

		assertCanonicalNames(
			panelAppNavigationItems, "dispatch-triggers", "scheduled-jobs");

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		assertParameterValue(
			"/dispatch/view_dispatch_trigger", panelAppNavigationItem,
			"mvcRenderCommandName");
		assertParameterValue(
			"dispatch-trigger", panelAppNavigationItem, "tabs1");

		panelAppNavigationItem = panelAppNavigationItems.get(1);

		assertParameterValue(
			"/dispatch/edit_scheduler_response", panelAppNavigationItem,
			"mvcRenderCommandName");
		assertParameterValue(
			"scheduler-response", panelAppNavigationItem, "tabs1");
	}

	private final DispatchPanelApp _dispatchPanelApp = new DispatchPanelApp() {

		@Override
		public PortletURL getPortletURL(HttpServletRequest httpServletRequest) {
			return _portletURL;
		}

		private final PortletURL _portletURL = new MockLiferayPortletURL();

	};

}