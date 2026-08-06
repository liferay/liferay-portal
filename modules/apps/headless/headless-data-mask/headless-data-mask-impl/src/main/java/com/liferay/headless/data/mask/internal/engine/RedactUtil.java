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
 * @author Alejandro Tardín
 */
public class RedactUtil {

	public static void evict(String regex) {
		_patterns.remove(regex);
	}

	public static long newDeadline() {
		return System.currentTimeMillis() + _TIMEOUT;
	}

	public static String redact(
		long deadline, String detectionRegex, String replacementRegex,
		String replacementValue, String text) {

		return _redact(
			true, deadline, detectionRegex, replacementRegex, replacementValue,
			text);
	}

	public static String redact(
		String detectionRegex, String replacementRegex, String replacementValue,
		String text) {

		return redact(
			newDeadline(), detectionRegex, replacementRegex, replacementValue,
			text);
	}

	public static String redactWithoutCaching(
		String detectionRegex, String replacementRegex, String replacementValue,
		String text) {

		return _redact(
			false, newDeadline(), detectionRegex, replacementRegex,
			replacementValue, text);
	}

	private static Pattern _getPattern(boolean cache, String regex) {
		if (Validator.isNull(regex)) {
			return null;
		}

		if (!cache) {
			return Pattern.compile(regex);
		}

		return _patterns.computeIfAbsent(regex, Pattern::compile);
	}

	private static String _getReplacement(
		long deadline, Matcher matcher, Pattern replacementPattern,
		String replacementValue) {

		if (replacementPattern == null) {
			return replacementValue;
		}

		Matcher replacementMatcher = replacementPattern.matcher(
			new DeadlineCharSequence(matcher.group(), deadline));

		return replacementMatcher.replaceAll(replacementValue);
	}

	private static String _redact(
		boolean cache, long deadline, String detectionRegex,
		String replacementRegex, String replacementValue, String text) {

		if (text == null) {
			return text;
		}

		Pattern detectionPattern = _getPattern(cache, detectionRegex);

		if (detectionPattern == null) {
			return text;
		}

		try {
			return _redact(
				deadline, detectionPattern,
				_getPattern(cache, replacementRegex), replacementValue, text);
		}
		catch (StackOverflowError stackOverflowError) {
			throw new RedactException(
				"Redaction overflowed the stack", stackOverflowError);
		}
	}

	private static String _redact(
		long deadline, Pattern detectionPattern, Pattern replacementPattern,
		String replacementValue, String text) {

		StringBuffer sb = new StringBuffer();

		Matcher matcher = detectionPattern.matcher(
			new DeadlineCharSequence(text, deadline));

		boolean found = false;

		while (matcher.find()) {
			found = true;

			matcher.appendReplacement(
				sb,
				Matcher.quoteReplacement(
					_getReplacement(
						deadline, matcher, replacementPattern,
						replacementValue)));
		}

		if (!found) {
			return text;
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final int _DEADLINE_CHECK_INTERVAL = 1024;

	private static final long _TIMEOUT = 1000;

	private static final Map<String, Pattern> _patterns =
		new ConcurrentHashMap<>();

	private static class DeadlineCharSequence implements CharSequence {

		public DeadlineCharSequence(CharSequence charSequence, long deadline) {
			_charSequence = charSequence;
			_deadline = deadline;
		}

		@Override
		public char charAt(int index) {
			if (((++_charAtCount % _DEADLINE_CHECK_INTERVAL) == 0) &&
				(System.currentTimeMillis() > _deadline)) {

				throw new RedactException(
					"Redaction exceeded the timeout of " + _TIMEOUT +
						" milliseconds");
			}

			return _charSequence.charAt(index);
		}

		@Override
		public int length() {
			return _charSequence.length();
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return new DeadlineCharSequence(
				_charSequence.subSequence(start, end), _deadline);
		}

		@Override
		public String toString() {
			return _charSequence.toString();
		}

		private int _charAtCount;
		private final CharSequence _charSequence;
		private final long _deadline;

	}

}