/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.validator;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;

/**
 * @author Mariano Álvaro Sáiz
 */
public class LayoutValidator {

	public static Character getBlacklistCharacter(String string) {
		for (char c : _BLACKLIST_CHARS) {
			if (string.indexOf(c) >= 0) {
				return c;
			}
		}

		return null;
	}

	public static boolean hasBlacklistedChar(String string) {
		for (char c : _BLACKLIST_CHARS) {
			if (string.indexOf(c) >= 0) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBlacklistedChar(char c) {
		for (char blacklistedChar : _BLACKLIST_CHARS) {
			if (c == blacklistedChar) {
				return true;
			}
		}

		return false;
	}

	public static String replaceBlacklistedChars(String string) {
		return StringUtil.replace(string, _BLACKLIST_CHARS, _REPLACEMENT_CHARS);
	}

	private static final char[] _BLACKLIST_CHARS;

	private static final char[] _REPLACEMENT_CHARS;

	static {
		_BLACKLIST_CHARS = new char[] {
			';', '/', '?', ':', '@', '=', '&', '\"', '<', '>', '#', '%', '{',
			'}', '|', '\\', '^', '~', '[', ']', '`'
		};

		_REPLACEMENT_CHARS = new char[_BLACKLIST_CHARS.length];

		Arrays.fill(_REPLACEMENT_CHARS, CharPool.DASH);
	}

}