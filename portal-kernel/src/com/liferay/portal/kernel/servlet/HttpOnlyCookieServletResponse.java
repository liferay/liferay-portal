/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.util.SystemProperties;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Samuel Kong
 */
public class HttpOnlyCookieServletResponse extends HttpServletResponseWrapper {

	public static HttpServletResponse getHttpOnlyCookieServletResponse(
		HttpServletResponse httpServletResponse) {

		HttpServletResponse wrappedHttpServletResponse = httpServletResponse;

		while (wrappedHttpServletResponse instanceof
					HttpServletResponseWrapper) {

			if (wrappedHttpServletResponse instanceof
					HttpOnlyCookieServletResponse) {

				return httpServletResponse;
			}

			HttpServletResponseWrapper httpServletResponseWrapper =
				(HttpServletResponseWrapper)wrappedHttpServletResponse;

			wrappedHttpServletResponse =
				(HttpServletResponse)httpServletResponseWrapper.getResponse();
		}

		return new HttpOnlyCookieServletResponse(httpServletResponse);
	}

	public HttpOnlyCookieServletResponse(
		HttpServletResponse httpServletResponse) {

		super(httpServletResponse);
	}

	@Override
	public void addCookie(Cookie cookie) {
		if (!_httpOnlyCookieNames.contains(cookie.getName())) {
			cookie.setHttpOnly(true);
		}

		super.addCookie(cookie);
	}

	private static final Set<String> _httpOnlyCookieNames =
		new HashSet<String>() {
			{
				add(CookiesConstants.NAME_CONSENT_TYPE_FUNCTIONAL);
				add(CookiesConstants.NAME_CONSENT_TYPE_NECESSARY);
				add(CookiesConstants.NAME_CONSENT_TYPE_PERFORMANCE);
				add(CookiesConstants.NAME_CONSENT_TYPE_PERSONALIZATION);
				add(CookiesConstants.NAME_USER_CONSENT_CONFIGURED);

				for (String cookieName :
						SystemProperties.getArray(
							"cookie.http.only.names.excludes")) {

					add(cookieName);
				}
			}
		};

}