/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.aui.web.internal.servlet.taglib;

import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.ServletContext;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Iván Zaera Avellón
 */
public class AUITopHeadJSDynamicIncludeTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-104702")
	public void testIncludeWhenAUIPreloadIsEnabled() throws Exception {
		Assert.assertTrue(
			_include(
				true, true, true
			).contains(
				_getScriptURL(_URL_AUI_SANDBOX) +
					_getScriptURL(_URL_MODULES_DEPRECATED)
			));
		Assert.assertTrue(
			_include(
				true, true, false
			).contains(
				_getScriptURL(_URL_AUI_SANDBOX) +
					_getScriptURL(_URL_MODULES_DEPRECATED)
			));
	}

	@Test
	@TestInfo("LPD-104702")
	public void testIncludeWhenDeprecationFeatureFlagIsDisabled()
		throws Exception {

		String content = _include(false, false, false);

		Assert.assertTrue(content.contains(_getScriptURL(_URL_AUI_SANDBOX)));
		Assert.assertFalse(content.contains(_URL_MODULES_DEPRECATED));
	}

	@Test
	@TestInfo("LPD-104702")
	public void testIncludeWhenDeprecationFeatureFlagIsEnabled()
		throws Exception {

		Assert.assertTrue(
			_include(
				true, false, false
			).contains(
				_getScriptURL(_URL_AUI_SANDBOX) +
					_getScriptURL(_URL_MODULES_DEPRECATED)
			));
	}

	@Test
	@TestInfo("LPD-104702")
	public void testRegister() {
		AUITopHeadJSDynamicInclude auiTopHeadJSDynamicInclude =
			new AUITopHeadJSDynamicInclude();

		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry =
			Mockito.mock(DynamicInclude.DynamicIncludeRegistry.class);

		auiTopHeadJSDynamicInclude.register(dynamicIncludeRegistry);

		Mockito.verify(
			dynamicIncludeRegistry
		).register(
			"/html/common/themes/top_js.jspf#resources"
		);
	}

	private String _getScriptURL(String url) {
		return "<script data-senna-track=\"permanent\" src=\"" + url +
			"\" type=\"text/javascript\"></script>\n";
	}

	private String _include(
			boolean deprecationFeatureFlagEnabled, boolean enableAUIPreload,
			boolean themeJsBarebone)
		throws Exception {

		AUITopHeadJSDynamicInclude auiTopHeadJSDynamicInclude =
			new AUITopHeadJSDynamicInclude();

		ServletContext servletContext = Mockito.mock(ServletContext.class);

		Mockito.when(
			servletContext.getContextPath()
		).thenReturn(
			_CONTEXT_PATH
		);

		ReflectionTestUtil.setFieldValue(
			auiTopHeadJSDynamicInclude, "_servletContext", servletContext);

		ReflectionTestUtil.invoke(
			auiTopHeadJSDynamicInclude, "_setJSResourcePaths",
			new Class<?>[] {boolean.class}, enableAUIPreload);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			themeDisplay.isThemeJsBarebone()
		).thenReturn(
			themeJsBarebone
		);

		Mockito.when(
			themeDisplay.isThemeJsFastLoad()
		).thenReturn(
			false
		);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		try (MockedStatic<FeatureFlagManagerUtil> mockedStatic =
				Mockito.mockStatic(FeatureFlagManagerUtil.class)) {

			mockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-57347"))
			).thenReturn(
				deprecationFeatureFlagEnabled
			);

			auiTopHeadJSDynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				"/html/common/themes/top_js.jspf#resources");
		}

		return mockHttpServletResponse.getContentAsString();
	}

	private static final String _CONTEXT_PATH = "/o/frontend-js-aui-web";

	private static final String _URL_AUI_SANDBOX =
		_CONTEXT_PATH + "/liferay/aui_sandbox.js";

	private static final String _URL_MODULES_DEPRECATED =
		_CONTEXT_PATH + "/liferay/modules_deprecated.js";

}