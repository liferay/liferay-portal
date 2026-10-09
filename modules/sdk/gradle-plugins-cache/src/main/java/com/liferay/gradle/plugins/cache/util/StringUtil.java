/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.cache.util;

/**
 * @author Andrea Di Giorgi
 */
public class StringUtil extends com.liferay.gradle.util.StringUtil {

	public static String getCommonPrefix(char separator, String... strings) {
		String firstString = strings[0];

		int index = 0;
		boolean match = true;

		while (match) {
			int nextIndex = firstString.indexOf(separator, index + 1);

			if (nextIndex == -1) {
				break;
			}

			for (int i = 1; i < strings.length; i++) {
				if (!firstString.regionMatches(
						index, strings[i], index, nextIndex - index + 1)) {

					match = false;

					break;
				}
			}

			if (!match) {
				continue;
			}

			index = nextIndex;
		}

		if (index == 0) {
			return null;
		}

		return firstString.substring(0, index);
	}

}