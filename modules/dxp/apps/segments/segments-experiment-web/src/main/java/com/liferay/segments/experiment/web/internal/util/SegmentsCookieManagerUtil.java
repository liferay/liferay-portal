/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.util;

import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

/**
 * @author Regisson Aguiar
 */
public class SegmentsCookieManagerUtil {

	public static void cleanCookieLogoutAction(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		Cookie[] cookies = httpServletRequest.getCookies();

		if (ArrayUtil.isEmpty(cookies)) {
			return;
		}

		for (Cookie cookie : cookies) {
			if (StringUtil.startsWith(
					cookie.getName(), _AB_TEST_VARIANT_ID_COOKIE_PREFIX)) {

				CookiesManagerUtil.deleteCookies(
					CookiesManagerUtil.getDomain(httpServletRequest),
					httpServletRequest, httpServletResponse, cookie.getName());
			}
		}
	}

	public static Cookie getCookie(
		HttpServletRequest httpServletRequest, long plid) {

		Cookie[] cookies = httpServletRequest.getCookies();

		if (ArrayUtil.isEmpty(cookies)) {
			return null;
		}

		for (Cookie cookie : cookies) {
			if (Objects.equals(
					cookie.getName(),
					_AB_TEST_VARIANT_ID_COOKIE_PREFIX + plid)) {

				return cookie;
			}
		}

		return null;
	}

	public static void setCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long plid,
		String segmentsExperienceKey) {

		Cookie abTestVariantIdCookie = new Cookie(
			_AB_TEST_VARIANT_ID_COOKIE_PREFIX + plid, segmentsExperienceKey);

		String domain = CookiesManagerUtil.getDomain(httpServletRequest);

		if (Validator.isNotNull(domain)) {
			abTestVariantIdCookie.setDomain(domain);
		}

		abTestVariantIdCookie.setMaxAge(CookiesConstants.MAX_AGE);

		CookiesManagerUtil.addCookie(
			CookiesConstants.CONSENT_TYPE_PERSONALIZATION,
			abTestVariantIdCookie, httpServletRequest, httpServletResponse);
	}

	public static void unsetCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, long plid) {

		Cookie cookie = getCookie(httpServletRequest, plid);

		if (cookie == null) {
			return;
		}

		CookiesManagerUtil.deleteCookies(
			CookiesManagerUtil.getDomain(httpServletRequest),
			httpServletRequest, httpServletResponse, cookie.getName());
	}

	private static final String _AB_TEST_VARIANT_ID_COOKIE_PREFIX =
		"ab_test_variant_id_";

}