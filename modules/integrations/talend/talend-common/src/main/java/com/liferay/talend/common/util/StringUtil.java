/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.common.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 */
public class StringUtil {

	public static boolean isEmpty(String value) {
		if (value == null) {
			return true;
		}

		String trimmedValue = value.trim();

		return trimmedValue.isEmpty();
	}

	public static String removeQuotes(String s) {
		return s.replace("\"", "");
	}

	public static String replace(String pattern, String... tplArgs) {
		String replaced = pattern;

		for (int i = 0; i < tplArgs.length;) {
			replaced = replaced.replace(tplArgs[i], tplArgs[i + 1]);

			i = i + 2;
		}

		return replaced;
	}

	public static Set<String> stripPrefix(String prefix, Set<String> values) {
		Set<String> prefixs = new HashSet<>();

		for (String value : values) {
			if (value.startsWith(prefix)) {
				prefixs.add(value.substring(prefix.length()));
			}
			else {
				prefixs.add(value);
			}
		}

		return prefixs;
	}

	public static String toLowerCase(String value) {
		return value.toLowerCase(Locale.getDefault());
	}

	public static String toUpperCase(String value) {
		return value.toUpperCase(Locale.getDefault());
	}

	private StringUtil() {
	}

}