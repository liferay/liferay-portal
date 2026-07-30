/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.StringPool;

/**
 * @author David Truong
 */
public class FriendlyURLKeywordsUtil {

	public static String getSiteFriendlyURLKeyword(String friendlyURL) {
		friendlyURL = StringUtil.toLowerCase(friendlyURL);

		for (String keyword : _SITE_FRIENDLY_URL_KEYWORDS) {
			if (friendlyURL.startsWith(keyword)) {
				return keyword;
			}

			if (keyword.equals(friendlyURL + StringPool.SLASH)) {
				return friendlyURL;
			}
		}

		return null;
	}

	public static boolean hasSiteFriendlyURLKeyword(String friendlyURL) {
		String keyword = getSiteFriendlyURLKeyword(friendlyURL);

		return Validator.isNotNull(keyword);
	}

	private static final String[] _SITE_FRIENDLY_URL_KEYWORDS;

	static {
		_SITE_FRIENDLY_URL_KEYWORDS =
			new String[PropsValues.SITES_FRIENDLY_URL_KEYWORDS.length];

		for (int i = 0; i < PropsValues.SITES_FRIENDLY_URL_KEYWORDS.length;
			 i++) {

			String keyword = PropsValues.SITES_FRIENDLY_URL_KEYWORDS[i];

			keyword = StringPool.SLASH + keyword;

			if (!keyword.contains(StringPool.PERIOD)) {
				if (keyword.endsWith(StringPool.STAR)) {
					keyword = keyword.substring(0, keyword.length() - 1);
				}
				else {
					keyword = keyword + StringPool.SLASH;
				}
			}

			_SITE_FRIENDLY_URL_KEYWORDS[i] = StringUtil.toLowerCase(keyword);
		}
	}

}