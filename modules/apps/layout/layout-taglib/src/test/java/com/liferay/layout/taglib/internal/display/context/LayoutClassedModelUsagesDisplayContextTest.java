/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Víctor Galán
 */
public class LayoutClassedModelUsagesDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_portalUtilMockedStatic.close();
	}

	@Test
	public void testGetUsagesData() {
		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		String pathMain = RandomTestUtil.randomString();
		long plid = RandomTestUtil.randomLong();

		themeDisplay.setPathMain(pathMain);
		themeDisplay.setPlid(plid);

		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		HttpServletRequest httpServletRequest =
			mockLiferayPortletRenderRequest.getHttpServletRequest();

		Mockito.when(
			PortalUtil.getHttpServletRequest(mockLiferayPortletRenderRequest)
		).thenReturn(
			httpServletRequest
		);

		String portalURL = RandomTestUtil.randomString();

		Mockito.when(
			PortalUtil.getPortalURL(httpServletRequest)
		).thenReturn(
			portalURL
		);

		String className = RandomTestUtil.randomString();
		long classPK = RandomTestUtil.randomLong();

		LayoutClassedModelUsagesDisplayContext
			layoutClassedModelUsagesDisplayContext =
				new LayoutClassedModelUsagesDisplayContext(
					httpServletRequest, mockLiferayPortletRenderRequest,
					new MockLiferayPortletRenderResponse(), className, classPK);

		Map<String, Object> usagesData =
			layoutClassedModelUsagesDisplayContext.getUsagesData();

		Assert.assertEquals(
			StringBundler.concat(
				portalURL, pathMain,
				"/portal/get_layout_classed_model_usages?p_l_id=", plid,
				"&className=", className, "&classPK=", classPK),
			usagesData.get("getUsagesURL"));
	}

	private static final MockedStatic<PortalUtil> _portalUtilMockedStatic =
		Mockito.mockStatic(PortalUtil.class);

}