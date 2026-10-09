/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeFormatter;

import java.net.URLDecoder;
import java.net.URLEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class JS {

	public static String decodeURIComponent(String s) {

		// Get rid of all unicode

		Matcher matcher = _pattern.matcher(s);

		s = matcher.replaceAll(StringPool.BLANK);

		// Adjust for JavaScript specific annoyances

		s = StringUtil.replace(s, '+', "%2B");
		s = StringUtil.replace(s, "%20", "+");

		// Decode URL

		try {
			s = URLDecoder.decode(s, StringPool.UTF8);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return s;
	}

	public static String encodeURIComponent(String s) {

		// Encode URL

		try {
			s = URLEncoder.encode(s, StringPool.UTF8);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		// Adjust for JavaScript specific annoyances

		s = StringUtil.replace(s, '+', "%20");
		s = StringUtil.replace(s, "%2B", "+");

		return s;
	}

	public static String getSafeName(String name) {
		if (name == null) {
			return null;
		}

		StringBuilder sb = null;

		int index = 0;

		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);

			if ((c == CharPool.DASH) || (c == CharPool.PERIOD) ||
				(c == CharPool.SPACE)) {

				if (sb == null) {
					sb = new StringBuilder(name.length() - 1);

					sb.append(name, index, i);
				}
			}
			else if (sb != null) {
				sb.append(c);
			}
		}

		if (sb == null) {
			return name;
		}

		return sb.toString();
	}

	public static String toScript(String[] array) {
		StringBundler sb = new StringBundler((array.length * 4) + 2);

		sb.append(StringPool.OPEN_BRACKET);

		for (int i = 0; i < array.length; i++) {
			sb.append(StringPool.APOSTROPHE);
			sb.append(UnicodeFormatter.toString(array[i]));
			sb.append(StringPool.APOSTROPHE);

			if ((i + 1) >= array.length) {
				continue;
			}

			sb.append(StringPool.COMMA);
		}

		sb.append(StringPool.CLOSE_BRACKET);

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(JS.class);

	private static final Pattern _pattern = Pattern.compile("%u[0-9a-fA-F]{4}");

}