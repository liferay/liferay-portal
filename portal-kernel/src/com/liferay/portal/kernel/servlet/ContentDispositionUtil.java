/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Daniel Raposo
 */
public class ContentDispositionUtil {

	public static String getContentDispositionHeaderValue(String fileName) {
		return getContentDispositionHeaderValue(
			HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT, fileName);
	}

	public static String getContentDispositionHeaderValue(
		String contentDispositionType, String fileName) {

		// If necessary for non-ASCII characters, encode based on RFC 2184.
		// However, not all browsers support RFC 2184. See LEP-3127.

		for (int i = 0; i < fileName.length(); i++) {
			char c = fileName.charAt(i);

			if (!Validator.isAscii(c) || (c == CharPool.BACK_SLASH) ||
				(c == CharPool.QUOTE)) {

				return StringBundler.concat(
					contentDispositionType, "; filename*=UTF-8''",
					URLCodec.encodeURL(fileName, true));
			}
		}

		return StringBundler.concat(
			contentDispositionType, "; filename=\"", fileName, "\"");
	}

}