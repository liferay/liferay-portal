/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

/**
 * @author Peter Shin
 */
public class GradleIndentationCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		return _checkIndentation(content);
	}

	private String _checkIndentation(String content) throws IOException {
		boolean insideIfStatement = false;
		boolean insideQuotes = false;
		int tabCount = 0;

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (!insideIfStatement && !insideQuotes &&
					!line.matches("^\\s*['\"].*['\"].*[\\)\\+]$")) {

					line = _checkIndentation(line, tabCount);
				}

				sb.append(line);

				sb.append("\n");

				String trimmedLine = StringUtil.trim(line);

				if ((trimmedLine.startsWith("else if (") ||
					 trimmedLine.startsWith("if (")) &&
					!line.endsWith("{")) {

					insideIfStatement = true;
				}
				else if (insideIfStatement) {
					if (line.endsWith("{")) {
						insideIfStatement = false;

						tabCount++;
					}
				}
				else {
					tabCount = _getTabCount(line, insideQuotes, tabCount);
				}

				if (line.indexOf(_getQuoteString(line)) != -1) {
					insideQuotes = !insideQuotes;
				}
			}
		}

		if (sb.length() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private String _checkIndentation(String line, int expectedTabCount) {
		if (Validator.isNull(line)) {
			return line;
		}

		int leadingTabCount = getLeadingTabCount(line);

		if (line.matches("\t*[\\}\\]\\)].*")) {
			expectedTabCount = expectedTabCount - 1;
		}

		if (leadingTabCount == expectedTabCount) {
			return line;
		}

		StringBundler sb = new StringBundler();

		for (int i = 0; i < expectedTabCount; i++) {
			sb.append(StringPool.TAB);
		}

		sb.append(StringUtil.trimLeading(line));

		return sb.toString();
	}

	private String _getQuoteString(String line) {
		if (line.contains("'''")) {
			return "'''";
		}

		return "\"\"\"";
	}

	private int _getTabCount(String line, boolean insideQuotes, int tabCount) {
		String quoteString = _getQuoteString(line);
		String text = line;

		if (line.contains(quoteString)) {
			if (insideQuotes) {
				int x = quoteString.length();

				text = text.substring(line.indexOf(quoteString) + x);
			}
			else {
				text = text.substring(0, line.indexOf(quoteString));
			}
		}

		if (insideQuotes && line.equals(text)) {
			return tabCount;
		}

		if (text.matches("^\\s*Matcher\\s+.*") ||
			text.matches("^\\s*Pattern\\s+.*")) {

			return tabCount;
		}

		if (text.contains(" ==~ /")) {
			int x = text.indexOf(" ==~ /");

			int y = text.indexOf("/", x + 6);

			if (y != -1) {
				text = text.substring(0, x) + text.substring(y + 1);
			}
		}

		tabCount += getLevel(text, "([{", "}])");

		text = StringUtil.removeSubstrings(text, "([{", "}])");

		tabCount += getLevel(text, "[{", "}]");

		text = StringUtil.removeSubstrings(text, "[{", "}]");

		String trimmedText = StringUtil.trim(text);

		if (trimmedText.endsWith("([")) {
			tabCount++;
			trimmedText = trimmedText.substring(0, trimmedText.length() - 2);
		}
		else if (trimmedText.equals("])")) {
			tabCount--;
			trimmedText = trimmedText.substring(0, trimmedText.length() - 2);
		}

		return getLevel(
			trimmedText, new String[] {"{", "[", "("},
			new String[] {"}", "]", ")"}, tabCount);
	}

}