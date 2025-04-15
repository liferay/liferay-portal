/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.cookies;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tamas Molnar
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface CookiesManager {

	public boolean addCookie(
		Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public boolean addCookie(
		Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean secure);

	public boolean addCookie(
		int consentType, Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public boolean addCookie(
		int consentType, Cookie cookie, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, boolean secure);

	public boolean addSupportCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public boolean deleteCookies(
		String domain, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String... cookieNames);

	public String getCookieValue(
		String cookieName, HttpServletRequest httpServletRequest);

	public String getCookieValue(
		String cookieName, HttpServletRequest httpServletRequest,
		boolean toUpperCase);

	public String getDomain(HttpServletRequest httpServletRequest);

	public String getDomain(String host);

	public boolean hasConsentType(
		int consentType, HttpServletRequest httpServletRequest);

	public boolean hasSessionId(HttpServletRequest httpServletRequest);

	public boolean isEncodedCookie(String cookieName);

	public void validateSupportCookie(HttpServletRequest httpServletRequest)
		throws UnsupportedCookieException;

}