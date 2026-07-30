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
		String detectionRegex, String replacementRegex, String replacementValue,
		String text) {

		return redact(
			detectionRegex, replacementRegex, replacementValue, text,
			newDeadline());
	}

	public static String redact(
		String detectionRegex, String replacementRegex, String replacementValue,
		String text, long deadline) {

		return _redact(
			detectionRegex, replacementRegex, replacementValue, text, deadline,
			true);
	}

	public static String redactWithoutCaching(
		String detectionRegex, String replacementRegex, String replacementValue,
		String text) {

		return _redact(
			detectionRegex, replacementRegex, replacementValue, text,
			newDeadline(), false);
	}

	private static Pattern _getPattern(String regex, boolean cache) {
		if (Validator.isNull(regex)) {
			return null;
		}

		if (!cache) {
			return Pattern.compile(regex);
		}

		return _patterns.computeIfAbsent(regex, Pattern::compile);
	}

	private static String _getReplacement(
		Matcher matcher, Pattern replacementPattern, String replacementValue,
		long deadline) {

		if (replacementPattern == null) {
			return replacementValue;
		}

		Matcher replacementMatcher = replacementPattern.matcher(
			new DeadlineCharSequence(matcher.group(), deadline));

		return replacementMatcher.replaceAll(replacementValue);
	}

	private static String _redact(
		String detectionRegex, String replacementRegex, String replacementValue,
		String text, long deadline, boolean cache) {

		if (text == null) {
			return text;
		}

		Pattern detectionPattern = _getPattern(detectionRegex, cache);

		if (detectionPattern == null) {
			return text;
		}

		StringBuffer sb = new StringBuffer();

		Matcher matcher = detectionPattern.matcher(
			new DeadlineCharSequence(text, deadline));

		Pattern replacementPattern = _getPattern(replacementRegex, cache);

		boolean found = false;

		while (matcher.find()) {
			found = true;

			matcher.appendReplacement(
				sb,
				Matcher.quoteReplacement(
					_getReplacement(
						matcher, replacementPattern, replacementValue,
						deadline)));
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

				throw new RedactTimeoutException(_TIMEOUT);
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