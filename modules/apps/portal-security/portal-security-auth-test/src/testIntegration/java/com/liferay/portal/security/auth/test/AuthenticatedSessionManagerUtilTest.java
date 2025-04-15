/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.model.RememberMeToken;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RememberMeTokenLocalService;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;

import jakarta.servlet.http.Cookie;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Manuele Castro
 * @author Stian Sigvartsen
 */
@RunWith(Arquillian.class)
public class AuthenticatedSessionManagerUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			_PASSWORD,
			RandomTestUtil.randomString() + RandomTestUtil.nextLong() +
				"@liferay.com",
			RandomTestUtil.randomString(
				NumericStringRandomizerBumper.INSTANCE,
				UniqueStringRandomizerBumper.INSTANCE),
			LocaleUtil.getDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			new long[] {TestPropsValues.getGroupId()},
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testRememberMeLogin() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_cookiesManager.addSupportCookie(
			mockHttpServletRequest, mockHttpServletResponse);

		AuthenticatedSessionManagerUtil.login(
			mockHttpServletRequest, mockHttpServletResponse, _user.getLogin(),
			_PASSWORD, true, null);

		Assert.assertNotNull(
			_cookiesManager.getCookieValue(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID,
				mockHttpServletRequest, false));
		Assert.assertNotNull(
			_cookiesManager.getCookieValue(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE,
				mockHttpServletRequest, false));
	}

	@Test
	public void testRememberMeLogout() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

		_cookiesManager.addCookie(
			CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
			_createCookie(
				CookiesConstants.NAME_REMEMBER_ME, Boolean.TRUE.toString(), 1),
			mockHttpServletRequest, mockHttpServletResponse);

		Cookie cookie = _createCookie(
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE, StringPool.BLANK, 1);

		RememberMeToken rememberMeToken =
			_rememberMeTokenLocalService.addRememberMeToken(
				_user.getCompanyId(), _user.getUserId(),
				new Date(System.currentTimeMillis()), cookie::setValue);

		_cookiesManager.addCookie(
			CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
			_createCookie(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID,
				String.valueOf(rememberMeToken.getRememberMeTokenId()), 1),
			mockHttpServletRequest, mockHttpServletResponse);

		_cookiesManager.addCookie(
			CookiesConstants.CONSENT_TYPE_FUNCTIONAL, cookie,
			mockHttpServletRequest, mockHttpServletResponse);

		mockHttpServletRequest.setCookies(mockHttpServletResponse.getCookies());

		mockHttpServletRequest.setPathInfo(StringPool.SLASH);

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		themeDisplay.setSiteGroupId(TestPropsValues.getGroupId());
		themeDisplay.setUser(_user);

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		AuthenticatedSessionManagerUtil.logout(
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertNull(
			_cookiesManager.getCookieValue(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID,
				mockHttpServletRequest, false));
		Assert.assertNull(
			_cookiesManager.getCookieValue(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE,
				mockHttpServletRequest, false));
		Assert.assertNull(
			_rememberMeTokenLocalService.fetchRememberMeToken(
				rememberMeToken.getRememberMeTokenId()));
	}

	private Cookie _createCookie(String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);

		cookie.setDomain("");
		cookie.setMaxAge(maxAge);

		return cookie;
	}

	private static final String _PASSWORD = RandomTestUtil.randomString();

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CookiesManager _cookiesManager;

	@Inject
	private RememberMeTokenLocalService _rememberMeTokenLocalService;

	@DeleteAfterTestRun
	private User _user;

}