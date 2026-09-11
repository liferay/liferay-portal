/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.spa.web.internal.servlet.taglib;

import com.liferay.frontend.js.spa.web.internal.configuration.SPAConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.PortletData;
import com.liferay.portal.kernel.servlet.taglib.aui.PortletDataRenderer;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.ESModuleAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Iván Zaera Avellón
 */
public class SPATopHeadJSPDynamicIncludeTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			ScriptData.class, "_portletDataRenderer",
			(PortletDataRenderer)(portletDatas, writer) -> {
				for (PortletData portletData : portletDatas) {
					for (JSFragment jsFragment : portletData.getJSFragments()) {
						writer.write(jsFragment.getCode());
					}
				}
			});

		_resourceBundleLoaderUtilMockedStatic.when(
			() ->
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByServletContextName(
						"frontend-js-spa-web")
		).thenReturn(
			Mockito.mock(ResourceBundleLoader.class)
		);
	}

	@After
	public void tearDown() {
		_resourceBundleLoaderUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-104833")
	public void testInclude() throws Exception {

		// The clear cache request attribute is set

		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);

		JSONObject configJSONObject = _include(mockHttpServletRequest);

		Assert.assertTrue(configJSONObject.getBoolean("clearScreensCache"));

		_assertHttpSessionNotCreated(mockHttpServletRequest);

		// The clear cache request attribute is not set

		mockHttpServletRequest = _getMockHttpServletRequest();

		configJSONObject = _include(mockHttpServletRequest);

		Assert.assertFalse(configJSONObject.getBoolean("clearScreensCache"));

		_assertHttpSessionNotCreated(mockHttpServletRequest);
	}

	private void _assertHttpSessionNotCreated(
		MockHttpServletRequest mockHttpServletRequest) {

		Mockito.verify(
			mockHttpServletRequest, Mockito.never()
		).getSession();

		Assert.assertNull(mockHttpServletRequest.getSession(false));
	}

	private AbsolutePortalURLBuilderFactory
		_getAbsolutePortalURLBuilderFactory() {

		AbsolutePortalURLBuilderFactory absolutePortalURLBuilderFactory =
			Mockito.mock(AbsolutePortalURLBuilderFactory.class);

		AbsolutePortalURLBuilder absolutePortalURLBuilder = Mockito.mock(
			AbsolutePortalURLBuilder.class);

		ESModuleAbsolutePortalURLBuilder esModuleAbsolutePortalURLBuilder =
			Mockito.mock(ESModuleAbsolutePortalURLBuilder.class);

		Mockito.when(
			esModuleAbsolutePortalURLBuilder.build()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			absolutePortalURLBuilder.forESModule(
				Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			esModuleAbsolutePortalURLBuilder
		);

		Mockito.when(
			absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				Mockito.any())
		).thenReturn(
			absolutePortalURLBuilder
		);

		return absolutePortalURLBuilderFactory;
	}

	private ConfigurationProvider _getConfigurationProvider() throws Exception {
		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		SPAConfiguration spaConfiguration = Mockito.mock(
			SPAConfiguration.class);

		Mockito.when(
			spaConfiguration.enabled()
		).thenReturn(
			true
		);

		Mockito.when(
			spaConfiguration.navigationExceptionSelectors()
		).thenReturn(
			new String[0]
		);

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				Mockito.eq(SPAConfiguration.class), Mockito.anyLong())
		).thenReturn(
			spaConfiguration
		);

		return configurationProvider;
	}

	private MockHttpServletRequest _getMockHttpServletRequest() {
		MockHttpServletRequest mockHttpServletRequest = Mockito.spy(
			new MockHttpServletRequest());

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getCompanyId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		return mockHttpServletRequest;
	}

	private Portal _getPortal() {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getCurrentURL(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			StringPool.BLANK
		);

		return portal;
	}

	private SPATopHeadJSPDynamicInclude _getSPATopHeadJSPDynamicInclude()
		throws Exception {

		SPATopHeadJSPDynamicInclude spaTopHeadJSPDynamicInclude =
			new SPATopHeadJSPDynamicInclude();

		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_absolutePortalURLBuilderFactory",
			_getAbsolutePortalURLBuilderFactory());
		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_configurationProvider",
			_getConfigurationProvider());
		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_globalNavigationExceptionSelectors",
			new ArrayList<String>());
		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_jsonFactory",
			JSONFactoryUtil.getJSONFactory());
		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_language",
			Mockito.mock(Language.class));
		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_portal", _getPortal());
		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_portletLocalService",
			Mockito.mock(PortletLocalService.class));
		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_redirectParamName",
			RandomTestUtil.randomString());
		ReflectionTestUtil.setFieldValue(
			spaTopHeadJSPDynamicInclude, "_validStatusCodesJSONArray",
			JSONFactoryUtil.createJSONArray());

		return spaTopHeadJSPDynamicInclude;
	}

	private JSONObject _include(MockHttpServletRequest mockHttpServletRequest)
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		SPATopHeadJSPDynamicInclude spaTopHeadJSPDynamicInclude =
			_getSPATopHeadJSPDynamicInclude();

		spaTopHeadJSPDynamicInclude.include(
			mockHttpServletRequest, mockHttpServletResponse,
			"/html/common/themes/top_head.jsp#post");

		String contentAsString = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(contentAsString.startsWith("init("));

		return JSONFactoryUtil.createJSONObject(
			contentAsString.substring(5, contentAsString.lastIndexOf(");")));
	}

	private final MockedStatic<ResourceBundleLoaderUtil>
		_resourceBundleLoaderUtilMockedStatic = Mockito.mockStatic(
			ResourceBundleLoaderUtil.class);

}