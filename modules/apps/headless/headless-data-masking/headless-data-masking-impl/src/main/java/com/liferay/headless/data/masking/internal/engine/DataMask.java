/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.masking.internal.engine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jose Luis Navarro
 */
public class DataMask {

	public DataMask(
		Pattern detectionPattern, String name, Pattern replacementPattern,
		String replacementValue) {

		_detectionPattern = detectionPattern;
		_name = name;
		_replacementPattern = replacementPattern;
		_replacementValue = replacementValue;
	}

	public String apply(String text) {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _detectionPattern.matcher(text);

		boolean found = false;

		while (matcher.find()) {
			found = true;

			if (_replacementPattern == null) {
				matcher.appendReplacement(
					sb, Matcher.quoteReplacement(_replacementValue));
			}
			else {
				String replaced = _replacementPattern.matcher(
					matcher.group()
				).replaceAll(
					_replacementValue
				);

				matcher.appendReplacement(
					sb, Matcher.quoteReplacement(replaced));
			}
		}

		if (!found) {
			return text;
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	public String getName() {
		return _name;
	}

	private final Pattern _detectionPattern;
	private final String _name;
	private final Pattern _replacementPattern;
	private final String _replacementValue;

}