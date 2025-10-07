/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mule.internal.util;

/**
 * @author Igor Beslic
 */
public class StringUtil {

	public static String replace(String pattern, String... tplArgs) {
		String replaced = pattern;

		for (int i = 0; i < tplArgs.length; i = i + 2) {
			replaced = replaced.replace(tplArgs[i], tplArgs[i + 1]);
		}

		return replaced;
	}

	private StringUtil() {
	}

}