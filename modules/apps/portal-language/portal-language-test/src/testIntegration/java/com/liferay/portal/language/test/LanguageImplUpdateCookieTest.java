/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class LanguageImplUpdateCookieTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpdateCookieAddsCookieOnFirstCall() {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_language.updateCookie(
			new MockHttpServletRequest(), mockHttpServletResponse,
			LocaleUtil.US);

		_assertGuestLanguageIdCookie(LocaleUtil.US, mockHttpServletResponse);
	}

	@Test
	public void testUpdateCookieOnlyAddsCookieOnLocaleUpdate() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setCookies(
			new Cookie(
				CookiesConstants.NAME_GUEST_LANGUAGE_ID,
				LocaleUtil.toLanguageId(LocaleUtil.US)));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_language.updateCookie(
			mockHttpServletRequest, mockHttpServletResponse, LocaleUtil.US);
		_language.updateCookie(
			mockHttpServletRequest, mockHttpServletResponse, LocaleUtil.SPAIN);

		_assertGuestLanguageIdCookie(LocaleUtil.SPAIN, mockHttpServletResponse);
	}

	private void _assertGuestLanguageIdCookie(
		Locale locale, MockHttpServletResponse mockHttpServletResponse) {

		Cookie validCookie = null;
		int total = 0;

		for (Cookie cookie : mockHttpServletResponse.getCookies()) {
			if (StringUtil.equals(
					cookie.getName(),
					CookiesConstants.NAME_GUEST_LANGUAGE_ID)) {

				validCookie = cookie;
				total++;
			}
		}

		Assert.assertEquals(1, total);
		Assert.assertNotEquals(0, validCookie.getMaxAge());
		Assert.assertEquals(
			LocaleUtil.toLanguageId(locale), validCookie.getValue());
	}

	@Inject
	private static Language _language;

}