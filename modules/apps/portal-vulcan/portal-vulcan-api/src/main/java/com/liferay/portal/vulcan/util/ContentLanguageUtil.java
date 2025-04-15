/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.HttpHeaders;

import java.util.Locale;

/**
 * @author Víctor Galán
 */
public class ContentLanguageUtil {

	public static void addContentLanguageHeader(
		String[] availableLocaleIds, String defaultLocaleId,
		HttpServletResponse httpServletResponse, Locale requestedLocale) {

		if (httpServletResponse == null) {
			return;
		}

		Locale contentLocale = null;

		for (String availableLocaleId : availableLocaleIds) {
			Locale locale = LocaleUtil.fromLanguageId(availableLocaleId);

			if (LocaleUtil.equals(locale, requestedLocale)) {
				contentLocale = locale;

				break;
			}
		}

		if (contentLocale == null) {
			contentLocale = LocaleUtil.fromLanguageId(defaultLocaleId);
		}

		httpServletResponse.addHeader(
			HttpHeaders.CONTENT_LANGUAGE,
			LocaleUtil.toW3cLanguageId(contentLocale));
	}

}