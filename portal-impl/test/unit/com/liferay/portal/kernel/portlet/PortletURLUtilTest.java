/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Juanjo Fernández
 */
public class PortletURLUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	@Test
	public void testGetRefreshURLEncodesReflectedParameters() {
		String portletId = RandomTestUtil.randomString();

		Portlet portlet = Mockito.mock(Portlet.class);

		Mockito.when(
			portlet.getPortletId()
		).thenReturn(
			portletId
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, RandomTestUtil.randomString());
		mockHttpServletRequest.setAttribute(WebKeys.RENDER_PORTLET, portlet);

		String layoutMode = "constructor[prototype][headerJavaScriptPaths][]=x";
		String settingsScope = "constructor[prototype][dataType]=TEXT";

		mockHttpServletRequest.setParameter("p_l_mode", layoutMode);
		mockHttpServletRequest.setParameter("p_p_id", portletId);
		mockHttpServletRequest.setParameter("settingsScope", settingsScope);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setLifecycle(RandomTestUtil.randomString());
		themeDisplay.setPathMain(RandomTestUtil.randomString());
		themeDisplay.setStatePopUp(true);

		String refreshURL = PortletURLUtil.getRefreshURL(
			mockHttpServletRequest, themeDisplay);

		Assert.assertTrue(
			refreshURL.contains("&p_l_mode=" + URLCodec.encodeURL(layoutMode)));
		Assert.assertTrue(
			refreshURL.contains(
				"&settingsScope=" + URLCodec.encodeURL(settingsScope)));
		Assert.assertFalse(refreshURL.contains("constructor[prototype]"));
	}

}