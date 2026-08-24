/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.web.internal.servlet.taglib;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Riccardo Ferrari
 */
public class AnalyticsTopHeadJSPDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_analyticsConfiguration.liferayAnalyticsDataSourceId()
		).thenReturn(
			_DATA_SOURCE_ID
		);

		Mockito.when(
			_analyticsConfiguration.liferayAnalyticsEndpointURL()
		).thenReturn(
			_ENDPOINT_URL
		);

		Mockito.when(
			_analyticsConfiguration.liferayAnalyticsFaroBackendURL()
		).thenReturn(
			_FARO_BACKEND_URL
		);

		Mockito.when(
			_analyticsConfiguration.liferayAnalyticsProjectId()
		).thenReturn(
			_PROJECT_ID
		);
	}

	@Test
	public void testGetAnalyticsCloudClientConfig() {
		Map<String, String> analyticsCloudClientConfig =
			_getAnalyticsCloudClientConfig(_COOKIE_DOMAIN);

		Assert.assertEquals(
			_COOKIE_DOMAIN, analyticsCloudClientConfig.get("cookieDomain"));
		Assert.assertEquals(
			_DATA_SOURCE_ID, analyticsCloudClientConfig.get("dataSourceId"));
		Assert.assertEquals(
			_ENDPOINT_URL, analyticsCloudClientConfig.get("endpointUrl"));
		Assert.assertEquals(
			_FARO_BACKEND_URL,
			analyticsCloudClientConfig.get("faroBackendUrl"));
		Assert.assertEquals(
			_PROJECT_ID, analyticsCloudClientConfig.get("projectId"));
	}

	@Test
	public void testGetAnalyticsCloudClientConfigWithBlankCookieDomain() {
		Map<String, String> analyticsCloudClientConfig =
			_getAnalyticsCloudClientConfig("");

		Assert.assertFalse(
			analyticsCloudClientConfig.containsKey("cookieDomain"));
		Assert.assertEquals(
			_PROJECT_ID, analyticsCloudClientConfig.get("projectId"));
	}

	@Test
	public void testGetAnalyticsCloudClientConfigWithInvalidHost() {
		CookiesManager cookiesManager = Mockito.mock(CookiesManager.class);

		Mockito.when(
			cookiesManager.getDomain(_SERVER_NAME)
		).thenThrow(
			new IllegalArgumentException()
		);

		Map<String, String> analyticsCloudClientConfig =
			_getAnalyticsCloudClientConfig(
				cookiesManager, _createThemeDisplay());

		Assert.assertFalse(
			analyticsCloudClientConfig.containsKey("cookieDomain"));
		Assert.assertEquals(
			_PROJECT_ID, analyticsCloudClientConfig.get("projectId"));
	}

	@Test
	public void testGetAnalyticsCloudClientConfigWithIPAddressCookieDomain() {
		Map<String, String> analyticsCloudClientConfig =
			_getAnalyticsCloudClientConfig("10.0.0.5");

		Assert.assertFalse(
			analyticsCloudClientConfig.containsKey("cookieDomain"));
		Assert.assertEquals(
			_PROJECT_ID, analyticsCloudClientConfig.get("projectId"));
	}

	@Test
	public void testGetAnalyticsCloudClientConfigWithNullCookieDomain() {
		Map<String, String> analyticsCloudClientConfig =
			_getAnalyticsCloudClientConfig(null);

		Assert.assertFalse(
			analyticsCloudClientConfig.containsKey("cookieDomain"));
		Assert.assertEquals(
			_PROJECT_ID, analyticsCloudClientConfig.get("projectId"));
	}

	private ThemeDisplay _createThemeDisplay() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getServerName()
		).thenReturn(
			_SERVER_NAME
		);

		return themeDisplay;
	}

	private Map<String, String> _getAnalyticsCloudClientConfig(
		CookiesManager cookiesManager, ThemeDisplay themeDisplay) {

		AnalyticsTopHeadJSPDynamicInclude analyticsTopHeadJSPDynamicInclude =
			new AnalyticsTopHeadJSPDynamicInclude();

		ReflectionTestUtil.setFieldValue(
			analyticsTopHeadJSPDynamicInclude, "_cookiesManager",
			cookiesManager);

		return ReflectionTestUtil.invoke(
			analyticsTopHeadJSPDynamicInclude, "_getAnalyticsCloudClientConfig",
			new Class<?>[] {AnalyticsConfiguration.class, ThemeDisplay.class},
			_analyticsConfiguration, themeDisplay);
	}

	private Map<String, String> _getAnalyticsCloudClientConfig(
		String cookieDomain) {

		CookiesManager cookiesManager = Mockito.mock(CookiesManager.class);

		Mockito.when(
			cookiesManager.getDomain(_SERVER_NAME)
		).thenReturn(
			cookieDomain
		);

		return _getAnalyticsCloudClientConfig(
			cookiesManager, _createThemeDisplay());
	}

	private static final String _COOKIE_DOMAIN = RandomTestUtil.randomString();

	private static final String _DATA_SOURCE_ID = RandomTestUtil.randomString();

	private static final String _ENDPOINT_URL = RandomTestUtil.randomString();

	private static final String _FARO_BACKEND_URL =
		RandomTestUtil.randomString();

	private static final String _PROJECT_ID = RandomTestUtil.randomString();

	private static final String _SERVER_NAME = RandomTestUtil.randomString();

	private final AnalyticsConfiguration _analyticsConfiguration = Mockito.mock(
		AnalyticsConfiguration.class);

}