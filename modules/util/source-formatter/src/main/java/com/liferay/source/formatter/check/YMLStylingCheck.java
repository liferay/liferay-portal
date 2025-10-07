/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

/**
 * @author Alan Huang
 */
public class YMLStylingCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		content = content.trim();

		if (content.endsWith("\n---")) {
			content = content.substring(0, content.length() - 4);
		}

		if (content.startsWith("---\n")) {
			content = content.substring(4);
		}

		content = content.replaceAll(
			"(\\A|\n)( *)(description:) (?!\\|-)(.+)(\\Z|\n)",
			"$1$2$3\n    $2$4$5");
		content = content.replaceAll("(\\A|\n) *description:\n +\"\"", "");
		content = content.replaceAll(
			"(\\A|\n)( *#)@? ?(review)(\\Z|\n)", "$1$2 @$3$4");

		return _formatQuotes(content);
	}

	private String _fixBooleanValue(String s) {
		if (_isBooleanFalse(s)) {
			return "false";
		}

		if (_isBooleanTrue(s)) {
			return "true";
		}

		return s;
	}

	private String _fixQuotes(String s) {
		if (Validator.isNull(s) || (s.length() == 1)) {
			return s;
		}

		if ((s.charAt(0) == CharPool.APOSTROPHE) &&
			(s.charAt(s.length() - 1) == CharPool.APOSTROPHE)) {

			if (s.length() == 2) {
				return StringPool.QUOTE + StringPool.QUOTE;
			}

			String unquotedValue = s.substring(1, s.length() - 1);

			unquotedValue = StringUtil.replace(unquotedValue, "''", "'");
			unquotedValue = StringUtil.replace(unquotedValue, "\"", "\\\"");

			s = CharPool.QUOTE + unquotedValue + CharPool.QUOTE;
		}

		if ((s.charAt(0) == CharPool.QUOTE) &&
			(s.charAt(s.length() - 1) == CharPool.QUOTE)) {

			if (s.length() == 2) {
				return s;
			}

			String unquotedValue = s.substring(1, s.length() - 1);

			if (unquotedValue.contains("\\") ||
				unquotedValue.matches("\\d+(\\.\\d*)?") ||
				unquotedValue.startsWith("!") ||
				unquotedValue.startsWith("#") ||
				unquotedValue.startsWith("&") ||
				unquotedValue.startsWith("*") ||
				unquotedValue.startsWith("[") ||
				unquotedValue.startsWith("{") ||
				_isBooleanValue(unquotedValue)) {

				return s;
			}

			return s.substring(1, s.length() - 1);
		}

		return s;
	}

	private String _formatQuotes(String content) throws IOException {
		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;
			int lineNumber = 0;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				lineNumber++;

				String trimmedLine = StringUtil.trimLeading(line);

				int x = trimmedLine.indexOf(": ");

				if (x == -1) {
					continue;
				}

				String key = trimmedLine.substring(0, x);

				String newKey = _fixQuotes(key);

				if (!key.equals(newKey)) {
					return StringUtil.replaceFirst(
						content, key, newKey,
						getLineStartPos(content, lineNumber));
				}

				String value = trimmedLine.substring(x + 2);

				String newValue = _fixQuotes(value);

				newValue = _fixBooleanValue(newValue);

				if (value.equals(newValue)) {
					continue;
				}

				return StringUtil.replaceFirst(
					content, ": " + value, ": " + newValue,
					getLineStartPos(content, lineNumber));
			}
		}

		return content;
	}

	private boolean _isBooleanFalse(String s) {
		if (StringUtil.equalsIgnoreCase(s, "false") ||
			StringUtil.equalsIgnoreCase(s, "no") ||
			StringUtil.equalsIgnoreCase(s, "off")) {

			return true;
		}

		return false;
	}

	private boolean _isBooleanTrue(String s) {
		if (StringUtil.equalsIgnoreCase(s, "on") ||
			StringUtil.equalsIgnoreCase(s, "true") ||
			StringUtil.equalsIgnoreCase(s, "yes")) {

			return true;
		}

		return false;
	}

	private boolean _isBooleanValue(String s) {
		if (_isBooleanFalse(s) || _isBooleanTrue(s)) {
			return true;
		}

		return false;
	}

}