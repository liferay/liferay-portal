/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Adolfo Pérez
 */
public class URLUtil {

	public static String[] splitURL(String url) {
		Set<String> terms = new HashSet<>();

		int pos = url.indexOf("://");

		if (pos == -1) {
			pos = 0;
		}
		else {
			pos += 3;
		}

		String[] parts = _pattern.split(url.substring(pos));

		Collections.addAll(terms, parts);

		if (terms.isEmpty()) {
			return new String[0];
		}

		String domain = parts[0];

		terms.addAll(StringUtil.split(domain, CharPool.PERIOD));

		pos = domain.length();

		while (pos != -1) {
			pos = domain.lastIndexOf(StringPool.PERIOD, pos - 1);

			if (pos <= 0) {
				continue;
			}

			terms.add(domain.substring(pos + 1));
		}

		return terms.toArray(new String[0]);
	}

	private static final Pattern _pattern = Pattern.compile("[/?=&]+");

}