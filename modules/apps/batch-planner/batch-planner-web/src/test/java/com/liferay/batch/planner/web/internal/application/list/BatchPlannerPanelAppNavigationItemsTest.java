/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.application.list;

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
public class BatchPlannerPanelAppNavigationItemsTest
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
			_batchPlannerPanelApp, "_language", language);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		List<PanelAppNavigationItem> panelAppNavigationItems =
			_batchPlannerPanelApp.getPanelAppNavigationItems(
				httpServletRequest);

		assertCanonicalNames(
			panelAppNavigationItems, "import-and-export", "templates");

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(1);

		assertParameterValue(
			"/batch_planner/view_batch_planner_plan_templates",
			panelAppNavigationItem, "mvcRenderCommandName");
		assertParameterValue(
			"batch-planner-plan-templates", panelAppNavigationItem, "tabs1");
	}

	@Test
	public void testGetPanelAppNavigationItemsOmitsTheDefaultViewCommand()
		throws Exception {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_batchPlannerPanelApp.getPanelAppNavigationItems(
				httpServletRequest);

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		assertParameterValue(
			null, panelAppNavigationItem, "mvcRenderCommandName");
		assertParameterValue(
			"batch-planner-plans", panelAppNavigationItem, "tabs1");
	}

	private final BatchPlannerPanelApp _batchPlannerPanelApp =
		new BatchPlannerPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

}