/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.application.list;

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
public class AnnouncementsPanelAppNavigationItemsTest
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
			_announcementsPanelApp, "_language", language);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		List<PanelAppNavigationItem> panelAppNavigationItems =
			_announcementsPanelApp.getPanelAppNavigationItems(
				httpServletRequest);

		assertCanonicalNames(
			panelAppNavigationItems, "announcements", "alerts");

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		assertParameterValue(
			"announcements", panelAppNavigationItem, "navigation");

		panelAppNavigationItem = panelAppNavigationItems.get(1);

		assertParameterValue("alerts", panelAppNavigationItem, "navigation");
	}

	private final AnnouncementsPanelApp _announcementsPanelApp =
		new AnnouncementsPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

}