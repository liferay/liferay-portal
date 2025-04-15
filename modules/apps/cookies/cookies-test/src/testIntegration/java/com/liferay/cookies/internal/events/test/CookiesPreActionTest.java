/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.events.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carol Alonso
 * @author Olivér Kecskeméty
 */
@RunWith(Arquillian.class)
public class CookiesPreActionTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDeleteUserConsentCookieWhenAnyOptionalConsentCookiesAreMissing()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, new ThemeDisplay());

		List<Cookie> mockCookies = new ArrayList<Cookie>() {
			{
				add(
					new Cookie(
						CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL, "true"));
				add(
					new Cookie(
						CookiesConstants.NAME_CONSENT_TYPE_NECESSARY, "true"));
				add(
					new Cookie(
						CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE,
						"false"));
				add(
					new Cookie(
						CookiesConstants.NAME_USER_CONSENT_CONFIGURED, "true"));
			}
		};

		mockHttpServletRequest.setCookies(mockCookies.toArray(new Cookie[0]));

		_cookiesPreAction.run(mockHttpServletRequest, mockHttpServletResponse);

		Cookie[] cookies = mockHttpServletResponse.getCookies();

		Assert.assertEquals(Arrays.toString(cookies), 1, cookies.length);

		Cookie userConsentConfiguredCookie = mockHttpServletResponse.getCookie(
			CookiesConstants.NAME_USER_CONSENT_CONFIGURED);

		Assert.assertNotNull(userConsentConfiguredCookie);
		Assert.assertEquals(0, userConsentConfiguredCookie.getMaxAge());
		Assert.assertEquals("", userConsentConfiguredCookie.getValue());
	}

	@Inject(
		filter = "component.name=com.liferay.cookies.internal.events.CookiesPreAction",
		type = LifecycleAction.class
	)
	private Action _cookiesPreAction;

}