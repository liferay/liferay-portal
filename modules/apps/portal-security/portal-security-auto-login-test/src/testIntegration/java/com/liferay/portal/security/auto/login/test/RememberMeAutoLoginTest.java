/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auto.login.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.model.RememberMeToken;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auto.login.AutoLogin;
import com.liferay.portal.kernel.security.auto.login.AutoLoginException;
import com.liferay.portal.kernel.service.RememberMeTokenLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

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
 */
@RunWith(Arquillian.class)
public class RememberMeAutoLoginTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testRememberMeAutoLogin()
		throws AutoLoginException, PwdEncryptorException {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _user.getCompanyId());

		_cookiesManager.addCookie(
			CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
			_createCookie(
				CookiesConstants.NAME_REMEMBER_ME, Boolean.TRUE.toString(),
				PropsValues.COMPANY_SECURITY_AUTO_LOGIN_MAX_AGE),
			mockHttpServletRequest, mockHttpServletResponse);

		Cookie cookie = _createCookie(
			CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE, StringPool.BLANK,
			PropsValues.COMPANY_SECURITY_AUTO_LOGIN_MAX_AGE);

		long loginMaxAgeMillis =
			(long)PropsValues.COMPANY_SECURITY_AUTO_LOGIN_MAX_AGE * 1000;

		RememberMeToken rememberMeToken =
			_rememberMeTokenLocalService.addRememberMeToken(
				_user.getCompanyId(), _user.getUserId(),
				new Date(System.currentTimeMillis() + loginMaxAgeMillis),
				cookie::setValue);

		_cookiesManager.addCookie(
			CookiesConstants.CONSENT_TYPE_FUNCTIONAL,
			_createCookie(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID,
				String.valueOf(rememberMeToken.getRememberMeTokenId()),
				PropsValues.COMPANY_SECURITY_AUTO_LOGIN_MAX_AGE),
			mockHttpServletRequest, mockHttpServletResponse);

		_cookiesManager.addCookie(
			CookiesConstants.CONSENT_TYPE_FUNCTIONAL, cookie,
			mockHttpServletRequest, mockHttpServletResponse);

		String[] credentials = _rememberMeAutoLogin.login(
			mockHttpServletRequest, mockHttpServletResponse);

		String[] expectedCredentials = new String[3];

		expectedCredentials[0] = String.valueOf(_user.getUserId());
		expectedCredentials[1] = _user.getPassword();
		expectedCredentials[2] = String.valueOf(_user.isPasswordEncrypted());

		Assert.assertArrayEquals(expectedCredentials, credentials);
	}

	@Test
	public void testRemoveExpiredRememberMeCookie()
		throws AutoLoginException, PwdEncryptorException {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _user.getCompanyId());

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

		_rememberMeAutoLogin.login(
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

	@Test
	public void testRemoveInvalidRememberMeCookie() throws AutoLoginException {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, _user.getCompanyId());

		mockHttpServletRequest.setCookies(
			new Cookie(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID,
				RandomTestUtil.randomString()),
			new Cookie(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE,
				RandomTestUtil.randomString()));

		_rememberMeAutoLogin.login(
			mockHttpServletRequest, new MockHttpServletResponse());

		Assert.assertNull(
			_cookiesManager.getCookieValue(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_ID,
				mockHttpServletRequest, false));
		Assert.assertNull(
			_cookiesManager.getCookieValue(
				CookiesConstants.NAME_REMEMBER_ME_TOKEN_VALUE,
				mockHttpServletRequest, false));
	}

	private Cookie _createCookie(String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);

		cookie.setDomain("localhost");
		cookie.setMaxAge(maxAge);

		return cookie;
	}

	@Inject
	private CookiesManager _cookiesManager;

	@Inject(
		filter = "component.name=com.liferay.portal.security.auto.login.remember.me.RememberMeAutoLogin"
	)
	private AutoLogin _rememberMeAutoLogin;

	@Inject
	private RememberMeTokenLocalService _rememberMeTokenLocalService;

	@DeleteAfterTestRun
	private User _user;

}