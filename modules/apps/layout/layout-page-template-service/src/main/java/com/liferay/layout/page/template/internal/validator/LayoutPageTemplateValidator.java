/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.internal.validator;

/**
 * @author Mariano Álvaro Sáiz
 */
public class LayoutPageTemplateValidator {

	public static boolean hasBlacklistedChar(String string) {
		for (char c : _BLACKLIST_CHAR) {
			if (string.indexOf(c) >= 0) {
				return true;
			}
		}

		return false;
	}

	public static boolean isBlacklistedChar(char c) {
		for (char blacklistedChar : _BLACKLIST_CHAR) {
			if (c == blacklistedChar) {
				return true;
			}
		}

		return false;
	}

	public static Character getBlacklistCharacter(
			String string)  {

		for (char c : _BLACKLIST_CHAR) {
			if (string.indexOf(c) >= 0) {
				return c;
			}
		}

		return null;
	}

	private static final char[] _BLACKLIST_CHAR = {
		';', '/', '?', ':', '@', '=', '&', '\"', '<', '>', '#', '%', '{', '}',
		'|', '\\', '^', '~', '[', ']', '`'
	};

}