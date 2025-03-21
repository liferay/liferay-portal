/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.click.to.chat.web.internal.events.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class LogoutPreActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_setUpMockHttpServletRequest();
	}

	@Test
	public void testLogoutWithClickToChatDisabled() throws Exception {
		_assertCookiesExist();

		AuthenticatedSessionManagerUtil.logout(
			_mockHttpServletRequest, new MockHttpServletResponse());

		_assertCookiesExist();
	}

	@Test
	public void testLogoutWithClickToChatEnabled() throws Exception {
		_assertCookiesExist();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.click.to.chat.web.internal.configuration." +
						"ClickToChatConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"chatProviderId", "intercom"
					).put(
						"enabled", true
					).build())) {

			AuthenticatedSessionManagerUtil.logout(
				_mockHttpServletRequest, new MockHttpServletResponse());
		}

		Assert.assertNull(
			CookiesManagerUtil.getCookieValue(
				"intercom-id-test", _mockHttpServletRequest));
		Assert.assertNull(
			CookiesManagerUtil.getCookieValue(
				"intercom-session-test", _mockHttpServletRequest));
		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				"test-cookie", _mockHttpServletRequest));
	}

	private void _assertCookiesExist() {
		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				"intercom-id-test", _mockHttpServletRequest));
		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				"intercom-session-test", _mockHttpServletRequest));
		Assert.assertNotNull(
			CookiesManagerUtil.getCookieValue(
				"test-cookie", _mockHttpServletRequest));
	}

	private void _setUpMockHttpServletRequest() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setUser(TestPropsValues.getUser());

		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_mockHttpServletRequest.setCookies(
			new Cookie("intercom-id-test", RandomTestUtil.randomString()),
			new Cookie("intercom-session-test", RandomTestUtil.randomString()),
			new Cookie("test-cookie", RandomTestUtil.randomString()));
		_mockHttpServletRequest.setPathInfo(StringPool.BLANK);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	private final MockHttpServletRequest _mockHttpServletRequest =
		new MockHttpServletRequest();

}