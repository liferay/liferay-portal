/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.token.internal.events;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Jorge García Jiménez
 */
public class CookieLogoutProcessorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			CookiesManagerUtil.class, "_cookiesManagerSnapshot",
			new Snapshot<CookiesManager>(
				CookiesManagerUtil.class, CookiesManager.class) {

				@Override
				public CookiesManager get() {
					return _cookiesManager;
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			CookiesManagerUtil.class, "_cookiesManagerSnapshot",
			_cookiesManagerSnapshot);
	}

	@Test
	public void testLogout() {
		_testLogout();
		_testLogoutWhenDomainIsNull();
	}

	private void _assertCookie(Cookie cookie, String domain, String name) {
		Assert.assertEquals(domain, cookie.getDomain());
		Assert.assertEquals(0, cookie.getMaxAge());
		Assert.assertEquals(name, cookie.getName());
		Assert.assertEquals(StringPool.BLANK, cookie.getValue());
	}

	private List<Cookie> _captureCookies(int count) {
		ArgumentCaptor<Cookie> argumentCaptor = ArgumentCaptor.forClass(
			Cookie.class);

		Mockito.verify(
			_cookiesManager, Mockito.times(count)
		).addCookie(
			Mockito.eq(CookiesConstants.CONSENT_TYPE_FUNCTIONAL),
			argumentCaptor.capture(), Mockito.eq(_httpServletRequest),
			Mockito.eq(_httpServletResponse)
		);

		return argumentCaptor.getAllValues();
	}

	private void _testLogout() {
		String domain = StringUtil.toLowerCase(RandomTestUtil.randomString());

		Mockito.when(
			_cookiesManager.getDomain(_httpServletRequest)
		).thenReturn(
			domain
		);

		String name1 = RandomTestUtil.randomString();
		String name2 = RandomTestUtil.randomString();

		_cookieLogoutProcessor.logout(
			_httpServletRequest, _httpServletResponse, name1, name2);

		List<Cookie> cookies = _captureCookies(2);

		_assertCookie(cookies.get(0), domain, name1);
		_assertCookie(cookies.get(1), domain, name2);
	}

	private void _testLogoutWhenDomainIsNull() {
		Mockito.reset(_cookiesManager);

		String name = RandomTestUtil.randomString();

		_cookieLogoutProcessor.logout(
			_httpServletRequest, _httpServletResponse, name);

		List<Cookie> cookies = _captureCookies(1);

		_assertCookie(cookies.get(0), null, name);
	}

	private final CookieLogoutProcessor _cookieLogoutProcessor =
		new CookieLogoutProcessor();
	private final CookiesManager _cookiesManager = Mockito.mock(
		CookiesManager.class);
	private final Snapshot<CookiesManager> _cookiesManagerSnapshot =
		ReflectionTestUtil.getFieldValue(
			CookiesManagerUtil.class, "_cookiesManagerSnapshot");
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final HttpServletResponse _httpServletResponse = Mockito.mock(
		HttpServletResponse.class);

}