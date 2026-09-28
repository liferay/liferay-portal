/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.aui.web.internal.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
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
	public void testInclude() throws Exception {

		// Feature flag disabled

		_assertScripts(_include(false, false, false), false);
		_assertScripts(_include(false, false, true), false);
		_assertScripts(_include(false, true, false), false);
		_assertScripts(_include(false, true, true), false);

		// Feature flag enabled

		_assertScripts(_include(true, false, false), true);
		_assertScripts(_include(true, false, true), true);
		_assertScripts(_include(true, true, false), true);
		_assertScripts(_include(true, true, true), true);
	}

	@Test
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

	private static String _getScript(String url) {
		return StringBundler.concat(
			"<script data-senna-track=\"permanent\" src=\"", _CONTEXT_PATH, url,
			"\" type=\"text/javascript\"></script>");
	}

	private void _assertScripts(String content, boolean modulesDeprecated) {
		Assert.assertTrue(content.contains(_AUI_SANDBOX_SCRIPT));

		if (modulesDeprecated) {
			Assert.assertTrue(content.contains(_MODULES_DEPRECATED_SCRIPT));
			Assert.assertTrue(
				content.indexOf(_AUI_SANDBOX_SCRIPT) < content.indexOf(
					_MODULES_DEPRECATED_SCRIPT));
		}
		else {
			Assert.assertFalse(content.contains(_MODULES_DEPRECATED_SCRIPT));
		}
	}

	private String _include(
			boolean deprecationFeatureFlagEnabled, boolean enableAUIPreload,
			boolean themeJsBarebone)
		throws Exception {

		try (MockedStatic<ContentSecurityPolicyNonceProviderUtil>
				contentSecurityPolicyNonceProviderUtilMockedStatic =
					Mockito.mockStatic(
						ContentSecurityPolicyNonceProviderUtil.class);
			MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			contentSecurityPolicyNonceProviderUtilMockedStatic.when(
				() -> ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
					Mockito.any())
			).thenReturn(
				StringPool.BLANK
			);

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(
					Mockito.anyLong(), Mockito.eq("LPD-57347"))
			).thenReturn(
				deprecationFeatureFlagEnabled
			);

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

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

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

			mockHttpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			auiTopHeadJSDynamicInclude.include(
				mockHttpServletRequest, mockHttpServletResponse,
				"/html/common/themes/top_js.jspf#resources");

			return mockHttpServletResponse.getContentAsString();
		}
	}

	private static final String _AUI_SANDBOX_SCRIPT = _getScript(
		"/liferay/aui_sandbox.js");

	private static final String _CONTEXT_PATH = "/o/frontend-js-aui-web";

	private static final String _MODULES_DEPRECATED_SCRIPT = _getScript(
		"/liferay/modules_deprecated.js");

}