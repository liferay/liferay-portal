/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Petteri Karttunen
 */
public class WordUtil {

	public static boolean isPlural(String value) {
		if (Validator.isBlank(value)) {
			return false;
		}

		return !Objects.equals(toSingular(value), StringUtil.trim(value));
	}

	public static String normalize(String value) {
		if (Validator.isBlank(value)) {
			return StringPool.BLANK;
		}

		Matcher matcher = _nonalphanumericPattern.matcher(value);

		return StringUtil.toLowerCase(matcher.replaceAll(StringPool.BLANK));
	}

	public static String toPhrase(String value) {
		if (Validator.isBlank(value)) {
			return StringPool.BLANK;
		}

		// Capitalize the first letter so a leading lowercased acronym stays
		// one word. Example: "cMSBlogId" becomes "cms blog id" instead of
		// "c ms blog id"

		return TextFormatter.format(
			StringUtil.upperCaseFirstLetter(value), TextFormatter.H);
	}

	public static String toPlural(String value) {
		if (Validator.isBlank(value)) {
			return StringPool.BLANK;
		}

		value = StringUtil.trim(value);

		if (isPlural(value) ||
			_uninflectedWords.contains(_getLastWord(value))) {

			return value;
		}

		return TextFormatter.formatPlural(value);
	}

	public static String toSingular(String value) {
		if (Validator.isBlank(value)) {
			return StringPool.BLANK;
		}

		value = StringUtil.trim(value);

		String lastWord = _getLastWord(value);

		if (lastWord.endsWith("ss") || _uninflectedWords.contains(lastWord) ||
			_singularWordsEndingInS.contains(lastWord)) {

			return value;
		}

		if (lastWord.endsWith("es") &&
			_singularWordsEndingInS.contains(
				lastWord.substring(0, lastWord.length() - 2))) {

			return value.substring(0, value.length() - 2);
		}

		if (lastWord.endsWith("ies")) {
			return value.substring(0, value.length() - 3) + "y";
		}

		if (lastWord.endsWith("ches") || lastWord.endsWith("shes") ||
			lastWord.endsWith("sses") || lastWord.endsWith("xes") ||
			lastWord.endsWith("zes")) {

			return value.substring(0, value.length() - 2);
		}

		if (lastWord.endsWith("s")) {
			return value.substring(0, value.length() - 1);
		}

		return value;
	}

	public static String[] toWords(String value) {
		if (Validator.isBlank(value)) {
			return new String[0];
		}

		Matcher matcher = _possessivePattern.matcher(
			StringUtil.toLowerCase(value));

		matcher = _punctuationPattern.matcher(
			matcher.replaceAll(StringPool.BLANK));

		return StringUtil.split(
			StringUtil.trim(matcher.replaceAll(StringPool.SPACE)),
			CharPool.SPACE);
	}

	private static String _getLastWord(String value) {
		String[] words = _wordSeparatorPattern.split(value);

		if (words.length == 0) {
			return StringPool.BLANK;
		}

		return normalize(words[words.length - 1]);
	}

	private static final Pattern _nonalphanumericPattern = Pattern.compile(
		"[^A-Za-z0-9]");
	private static final Pattern _possessivePattern = Pattern.compile(
		"['\u2019]s\\b");
	private static final Pattern _punctuationPattern = Pattern.compile(
		"[^a-z0-9-]+");
	private static final Set<String> _singularWordsEndingInS =
		SetUtil.fromArray("status");
	private static final Set<String> _uninflectedWords = SetUtil.fromArray(
		"analysis", "analytics", "cms", "news", "series", "statistics");
	private static final Pattern _wordSeparatorPattern = Pattern.compile(
		"[\\s-]+");

}