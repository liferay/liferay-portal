/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.lang.builder.internal.util;

import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.Validator;

/**
 * @author Andrea Di Giorgi
 */
public class StringUtil extends com.liferay.gradle.util.StringUtil {

	public static String merge(Iterable<?> iterable, String separator) {
		StringBuilder sb = new StringBuilder();

		boolean first = true;

		for (Object object : iterable) {
			if (!first) {
				sb.append(separator);
			}

			first = false;

			String s = GradleUtil.toString(object);

			if (Validator.isNull(s)) {
				continue;
			}

			sb.append(s);
		}

		return sb.toString();
	}

}