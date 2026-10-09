/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class SessionKeysCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		return _fixSessionKeys(content, getPatterns());
	}

	protected List<Pattern> getPatterns() {
		return Arrays.asList(sessionKeyPattern);
	}

	protected final Pattern sessionKeyPattern = Pattern.compile(
		"SessionErrors.(?:add|contains|get)\\([^;%&|!]+|" +
			"SessionMessages.(?:add|contains|get)\\([^;%&|!]+",
		Pattern.MULTILINE);

	private String _fixSessionKey(String content, Pattern pattern) {
		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return content;
		}

		String newContent = content;

		do {
			String match = matcher.group();

			String s = null;

			if (pattern.equals(sessionKeyPattern)) {
				s = StringPool.COMMA;
			}
			else {
				s = "key=";
			}

			int x = match.indexOf(s);

			if (x == -1) {
				continue;
			}

			x = x + s.length();

			String substring = StringUtil.trim(match.substring(x));

			String quote = StringPool.BLANK;

			if (substring.startsWith(StringPool.APOSTROPHE)) {
				quote = StringPool.APOSTROPHE;
			}
			else if (substring.startsWith(StringPool.QUOTE)) {
				quote = StringPool.QUOTE;
			}
			else {
				continue;
			}

			int y = match.indexOf(quote, x);

			int z = match.indexOf(quote, y + 1);

			if ((y == -1) || (z == -1)) {
				continue;
			}

			String prefix = match.substring(0, y + 1);
			String suffix = match.substring(z);

			String oldKey = match.substring(y + 1, z);
			String oldKeySuffix = StringPool.BLANK;

			for (String allowedSuffix : _ALLOWED_SUFFIXES) {
				if (oldKey.endsWith(allowedSuffix)) {
					oldKey = oldKey.substring(
						0, oldKey.lastIndexOf(allowedSuffix));
					oldKeySuffix = allowedSuffix;

					break;
				}
			}

			boolean alphaNumericKey = true;

			for (char c : oldKey.toCharArray()) {
				if (Validator.isChar(c) || Validator.isDigit(c) ||
					(c == CharPool.DASH) || (c == CharPool.UNDERLINE)) {

					continue;
				}

				alphaNumericKey = false;
			}

			if (!alphaNumericKey) {
				continue;
			}

			String newKey = TextFormatter.format(oldKey, TextFormatter.O);

			newKey = TextFormatter.format(newKey, TextFormatter.M);

			if (newKey.equals(oldKey)) {
				continue;
			}

			if (Validator.isNotNull(oldKeySuffix)) {
				newKey = newKey + oldKeySuffix;
				oldKey = oldKey + oldKeySuffix;
			}

			String oldSub = StringBundler.concat(prefix, oldKey, suffix);
			String newSub = StringBundler.concat(prefix, newKey, suffix);

			newContent = StringUtil.replaceFirst(newContent, oldSub, newSub);
		}
		while (matcher.find());

		return newContent;
	}

	private String _fixSessionKeys(String content, List<Pattern> patterns) {
		for (Pattern pattern : patterns) {
			content = _fixSessionKey(content, pattern);
		}

		return content;
	}

	private static final String[] _ALLOWED_SUFFIXES = {
		"_requestProcessedSuccess", "_requestProcessedWarning"
	};

}