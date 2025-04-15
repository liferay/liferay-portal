/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cookies;

import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Tamas Molnar
 */
public class CookiesManagerUtil {

	public static boolean addCookie(
		Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.addCookie(
			cookie, httpServletRequest, httpServletResponse);
	}

	public static boolean addCookie(
		Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean secure) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.addCookie(
			cookie, httpServletRequest, httpServletResponse, secure);
	}

	public static boolean addCookie(
		int consentType, Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.addCookie(
			consentType, cookie, httpServletRequest, httpServletResponse);
	}

	public static boolean addCookie(
		int consentType, Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean secure) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.addCookie(
			consentType, cookie, httpServletRequest, httpServletResponse,
			secure);
	}

	public static boolean addSupportCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.addSupportCookie(
			httpServletRequest, httpServletResponse);
	}

	public static boolean deleteCookies(
		String domain, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String... cookieNames) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.deleteCookies(
			domain, httpServletRequest, httpServletResponse, cookieNames);
	}

	public static String getCookieValue(
		String cookieName, HttpServletRequest httpServletRequest) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.getCookieValue(cookieName, httpServletRequest);
	}

	public static String getCookieValue(
		String cookieName, HttpServletRequest httpServletRequest,
		boolean toUpperCase) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.getCookieValue(
			cookieName, httpServletRequest, toUpperCase);
	}

	public static String getDomain(HttpServletRequest httpServletRequest) {
		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.getDomain(httpServletRequest);
	}

	public static String getDomain(String host) {
		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.getDomain(host);
	}

	public static boolean hasConsentType(
		int consentType, HttpServletRequest httpServletRequest) {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.hasConsentType(consentType, httpServletRequest);
	}

	public static boolean hasSessionId(HttpServletRequest httpServletRequest) {
		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.hasSessionId(httpServletRequest);
	}

	public static boolean isEncodedCookie(String cookieName) {
		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		return cookiesManager.isEncodedCookie(cookieName);
	}

	public static void validateSupportCookie(
			HttpServletRequest httpServletRequest)
		throws UnsupportedCookieException {

		CookiesManager cookiesManager = _cookiesManagerSnapshot.get();

		cookiesManager.validateSupportCookie(httpServletRequest);
	}

	private static final Snapshot<CookiesManager> _cookiesManagerSnapshot =
		new Snapshot<>(CookiesManagerUtil.class, CookiesManager.class);

}