/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.configuration.kernel.util.PortletConfigurationUtil;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jorge Avalos
 */
public class RenderResponseImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_portletConfigurationUtilMockedStatic.close();
	}

	@Test
	public void testSetTitle() {
		PortletRequestImpl portletRequestImpl = Mockito.mock(
			PortletRequestImpl.class);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLanguageId()
		).thenReturn(
			"es_ES"
		);

		PortletDisplay portletDisplay = Mockito.mock(PortletDisplay.class);

		Mockito.when(
			portletDisplay.getId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			themeDisplay.getPortletDisplay()
		).thenReturn(
			portletDisplay
		);

		Mockito.when(
			portletRequestImpl.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			themeDisplay
		);

		RenderResponseImpl renderResponseImpl = new RenderResponseImpl();

		ReflectionTestUtil.setFieldValue(
			renderResponseImpl, "portletRequestImpl", portletRequestImpl);

		String portletTitle = RandomTestUtil.randomString();

		_portletConfigurationUtilMockedStatic.when(
			() -> PortletConfigurationUtil.getPortletTitle(
				portletDisplay.getId(), portletDisplay.getPortletPreferences(),
				themeDisplay.getLanguageId())
		).thenReturn(
			portletTitle
		);

		renderResponseImpl.setTitle(StringPool.BLANK);

		Assert.assertEquals(portletTitle, renderResponseImpl.getTitle());

		_portletConfigurationUtilMockedStatic.verify(
			() -> PortletConfigurationUtil.getPortletTitle(
				portletDisplay.getId(), portletDisplay.getPortletPreferences(),
				themeDisplay.getLanguageId()),
			Mockito.times(1));
	}

	private static final MockedStatic<PortletConfigurationUtil>
		_portletConfigurationUtilMockedStatic = Mockito.mockStatic(
			PortletConfigurationUtil.class);

}