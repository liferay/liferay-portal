/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.data.mask.internal.engine;

import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jose Luis Navarro
 */
public class DataMaskEngineUtil {

	public static void evict(String regex) {
		if (regex != null) {
			_patterns.remove(regex);
		}
	}

	public static String redact(
		String detectionRegex, String replacementRegex, String replacementValue,
		String text) {

		if (Validator.isNull(text)) {
			return text;
		}

		Pattern detectionPattern = _getPattern(detectionRegex);

		if (detectionPattern == null) {
			return text;
		}

		Pattern replacementPattern = _getPattern(replacementRegex);

		Matcher matcher = detectionPattern.matcher(text);

		StringBuffer sb = new StringBuffer();

		boolean found = false;

		while (matcher.find()) {
			found = true;

			if (replacementPattern == null) {
				matcher.appendReplacement(
					sb, Matcher.quoteReplacement(replacementValue));
			}
			else {
				matcher.appendReplacement(
					sb,
					Matcher.quoteReplacement(
						replacementPattern.matcher(
							matcher.group()
						).replaceAll(
							replacementValue
						)));
			}
		}

		if (!found) {
			return text;
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static Pattern _getPattern(String regex) {
		if (Validator.isNull(regex)) {
			return null;
		}

		return _patterns.computeIfAbsent(regex, Pattern::compile);
	}

	private static final Map<String, Pattern> _patterns =
		new ConcurrentHashMap<>();

}