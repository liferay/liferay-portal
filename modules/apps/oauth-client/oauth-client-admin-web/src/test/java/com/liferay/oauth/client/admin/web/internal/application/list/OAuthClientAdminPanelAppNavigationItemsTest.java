/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.application.list;

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
public class OAuthClientAdminPanelAppNavigationItemsTest
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
			_oAuthClientAdminPanelApp, "_language", language);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		List<PanelAppNavigationItem> panelAppNavigationItems =
			_oAuthClientAdminPanelApp.getPanelAppNavigationItems(
				httpServletRequest);

		String[] expectedNavigations = {
			"oauth-clients", "oauth-client-as-local-metadata",
			"oauth-client-pr-local-metadata"
		};

		assertCanonicalNames(panelAppNavigationItems, expectedNavigations);

		String[] expectedMVCRenderCommandNames = {
			"/oauth_client_admin/view_oauth_client_entries",
			"/oauth_client_admin/view_oauth_client_as_local_metadata",
			"/oauth_client_admin/view_oauth_client_pr_local_metadata"
		};

		for (int i = 0; i < expectedNavigations.length; i++) {
			PanelAppNavigationItem panelAppNavigationItem =
				panelAppNavigationItems.get(i);

			assertParameterValue(
				expectedMVCRenderCommandNames[i], panelAppNavigationItem,
				"mvcRenderCommandName");
			assertParameterValue(
				expectedNavigations[i], panelAppNavigationItem, "navigation");
		}
	}

	private final OAuthClientAdminPanelApp _oAuthClientAdminPanelApp =
		new OAuthClientAdminPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

}