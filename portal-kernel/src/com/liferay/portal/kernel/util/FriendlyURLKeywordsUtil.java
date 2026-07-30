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

	public static String getLayoutFriendlyURLKeyword(String friendlyURL) {
		return _getFriendlyURLKeyword(
			friendlyURL, _LAYOUT_FRIENDLY_URL_KEYWORDS);
	}

	public static String getSiteFriendlyURLKeyword(String friendlyURL) {
		return _getFriendlyURLKeyword(friendlyURL, _SITE_FRIENDLY_URL_KEYWORDS);
	}

	public static boolean hasLayoutFriendlyURLKeyword(String friendlyURL) {
		return Validator.isNotNull(getLayoutFriendlyURLKeyword(friendlyURL));
	}

	public static boolean hasSiteFriendlyURLKeyword(String friendlyURL) {
		return Validator.isNotNull(getSiteFriendlyURLKeyword(friendlyURL));
	}

	private static String _getFriendlyURLKeyword(
		String friendlyURL, String[] friendlyURLKeywords) {

		friendlyURL = StringUtil.toLowerCase(friendlyURL);

		for (String friendlyURLKeyword : friendlyURLKeywords) {
			if (friendlyURL.startsWith(friendlyURLKeyword)) {
				return friendlyURLKeyword;
			}

			if (friendlyURLKeyword.equals(friendlyURL + StringPool.SLASH)) {
				return friendlyURL;
			}
		}

		return null;
	}

	private static String[] _toFriendlyURLKeywords(String[] keywords) {
		String[] friendlyURLKeywords = new String[keywords.length];

		for (int i = 0; i < keywords.length; i++) {
			String keyword = StringPool.SLASH + keywords[i];

			if (!keyword.contains(StringPool.PERIOD)) {
				if (keyword.endsWith(StringPool.STAR)) {
					keyword = keyword.substring(0, keyword.length() - 1);
				}
				else {
					keyword = keyword + StringPool.SLASH;
				}
			}

			friendlyURLKeywords[i] = StringUtil.toLowerCase(keyword);
		}

		return friendlyURLKeywords;
	}

	private static final String[] _LAYOUT_FRIENDLY_URL_KEYWORDS =
		_toFriendlyURLKeywords(PropsValues.LAYOUT_FRIENDLY_URL_KEYWORDS);

	private static final String[] _SITE_FRIENDLY_URL_KEYWORDS =
		_toFriendlyURLKeywords(PropsValues.SITES_FRIENDLY_URL_KEYWORDS);

}