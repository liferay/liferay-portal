/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.java.parser.util;

import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 * @author Charles May
 * @author Alexander Chow
 * @author Harry Mark
 * @author Tariq Dweik
 * @author Glenn Powell
 * @author Raymond Augé
 * @author Prashant Dighe
 * @author Shuyang Zhou
 * @author James Lefeu
 * @author Miguel Pastor
 * @author Cody Hoag
 * @author James Hinkey
 * @author Hugo Huijser
 */
public class ToolsUtil {

	public static int getLevel(String s) {
		return getLevel(
			s, new String[] {StringPool.OPEN_PARENTHESIS},
			new String[] {StringPool.CLOSE_PARENTHESIS}, 0);
	}

	public static int getLevel(
		String s, String increaseLevelString, String decreaseLevelString) {

		return getLevel(
			s, new String[] {increaseLevelString},
			new String[] {decreaseLevelString}, 0);
	}

	public static int getLevel(
		String s, String[] increaseLevelStrings,
		String[] decreaseLevelStrings) {

		return getLevel(s, increaseLevelStrings, decreaseLevelStrings, 0);
	}

	public static int getLevel(
		String s, String[] increaseLevelStrings, String[] decreaseLevelStrings,
		int startLevel) {

		int level = startLevel;

		for (String increaseLevelString : increaseLevelStrings) {
			level = _adjustLevel(level, s, increaseLevelString, 1);
		}

		for (String decreaseLevelString : decreaseLevelStrings) {
			level = _adjustLevel(level, s, decreaseLevelString, -1);
		}

		return level;
	}

	public static String getPackagePath(File file) {
		String fileName = StringUtil.replace(
			file.toString(), CharPool.BACK_SLASH, CharPool.SLASH);

		return getPackagePath(fileName);
	}

	public static String getPackagePath(String fileName) {
		int x = Math.max(
			fileName.lastIndexOf("/com/"), fileName.lastIndexOf("/org/"));
		int y = fileName.lastIndexOf(CharPool.SLASH);

		String packagePath = fileName.substring(x + 1, y);

		return StringUtil.replace(packagePath, CharPool.SLASH, CharPool.PERIOD);
	}

	public static boolean isInsideQuotes(String s, int pos) {
		return isInsideQuotes(s, pos, true);
	}

	public static boolean isInsideQuotes(
		String s, int pos, boolean allowEscapedQuotes) {

		int start = s.lastIndexOf(CharPool.NEW_LINE, pos);

		if (start == -1) {
			start = 0;
		}

		int end = s.indexOf(CharPool.NEW_LINE, pos);

		if (end == -1) {
			end = s.length();
		}

		String line = s.substring(start, end);

		pos -= start;

		char delimiter = CharPool.SPACE;
		boolean insideQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (insideQuotes) {
				if (c == delimiter) {
					if (!allowEscapedQuotes) {
						insideQuotes = false;
					}
					else {
						int precedingBackSlashCount = 0;

						for (int j = i - 1; j >= 0; j--) {
							if (line.charAt(j) == CharPool.BACK_SLASH) {
								precedingBackSlashCount += 1;
							}
							else {
								break;
							}
						}

						if ((precedingBackSlashCount == 0) ||
							((precedingBackSlashCount % 2) == 0)) {

							insideQuotes = false;
						}
					}
				}
			}
			else if ((c == CharPool.APOSTROPHE) || (c == CharPool.QUOTE)) {
				delimiter = c;
				insideQuotes = true;
			}

			if (pos == i) {
				return insideQuotes;
			}
		}

		return false;
	}

	public static String stripFullyQualifiedClassNames(
			String content, String imports, String packagePath)
		throws IOException {

		if (Validator.isNull(content) || Validator.isNull(imports)) {
			return content;
		}

		String afterImportsContent = null;

		int pos = content.lastIndexOf("\nimport ");

		if ((pos == -1) && !content.startsWith("import ")) {
			afterImportsContent = content;
		}
		else {
			pos = content.indexOf("\n", pos + 1);

			afterImportsContent = content.substring(pos);
		}

		afterImportsContent = _stripFullyQualifiedClassNames(
			imports, afterImportsContent, packagePath);
		afterImportsContent = _stripFullyQualifiedClassNames(
			imports, afterImportsContent, "java.lang");

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(imports))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				int x = line.indexOf("import ");

				if (x == -1) {
					continue;
				}

				String importPackageAndClassName = line.substring(
					x + 7, line.lastIndexOf(StringPool.SEMICOLON));

				if (importPackageAndClassName.contains(StringPool.STAR)) {
					continue;
				}

				Pattern pattern = Pattern.compile(
					StringBundler.concat(
						"[^\\w.](",
						com.liferay.portal.kernel.util.StringUtil.replace(
							importPackageAndClassName, CharPool.PERIOD,
							"\\.\\s*"),
						")\\W"));

				outerLoop:
				while (true) {
					Matcher matcher = pattern.matcher(afterImportsContent);

					while (matcher.find()) {
						x = matcher.start();

						int y = afterImportsContent.lastIndexOf(
							CharPool.NEW_LINE, x);

						if (y == -1) {
							y = 0;
						}

						String s = afterImportsContent.substring(y, x + 1);

						if (isInsideQuotes(s, x - y)) {
							continue;
						}

						s = com.liferay.portal.kernel.util.StringUtil.trim(s);

						if (s.startsWith("//")) {
							continue;
						}

						int z = importPackageAndClassName.lastIndexOf(
							StringPool.PERIOD);

						afterImportsContent =
							com.liferay.portal.kernel.util.StringUtil.
								replaceFirst(
									afterImportsContent, matcher.group(1),
									importPackageAndClassName.substring(z + 1),
									x);

						continue outerLoop;
					}

					break;
				}
			}

			if (pos == -1) {
				return afterImportsContent;
			}

			return content.substring(0, pos) + afterImportsContent;
		}
	}

	private static int _adjustLevel(
		int level, String text, String s, int diff) {

		boolean multiLineComment = false;

		forLoop:
		for (String line : StringUtil.splitLines(text)) {
			line = StringUtil.trim(line);

			if (line.startsWith("/*")) {
				multiLineComment = true;
			}

			if (multiLineComment) {
				if (line.endsWith("*/")) {
					multiLineComment = false;
				}

				continue;
			}

			if (line.startsWith("//") || line.startsWith("*")) {
				continue;
			}

			int x = -1;

			while (true) {
				x = line.indexOf(s, x + 1);

				if (x == -1) {
					continue forLoop;
				}

				if (!isInsideQuotes(line, x)) {
					level += diff;
				}
			}
		}

		return level;
	}

	private static String _stripFullyQualifiedClassNames(
		String imports, String afterImportsContent, String packagePath) {

		if (Validator.isNull(packagePath)) {
			return afterImportsContent;
		}

		Pattern pattern1 = Pattern.compile(
			StringBundler.concat(
				"\n(.*)(",
				com.liferay.portal.kernel.util.StringUtil.replace(
					packagePath, CharPool.PERIOD, "\\.\\s*"),
				"\\.\\s*)([A-Z]\\w+)\\W"));

		outerLoop:
		while (true) {
			Matcher matcher1 = pattern1.matcher(afterImportsContent);

			while (matcher1.find()) {
				String lineStart =
					com.liferay.portal.kernel.util.StringUtil.trimLeading(
						matcher1.group(1));

				if (lineStart.contains("//") || lineStart.startsWith("*") ||
					isInsideQuotes(afterImportsContent, matcher1.start(2))) {

					continue;
				}

				String className = matcher1.group(3);

				Pattern pattern2 = Pattern.compile(
					"import [\\w.]+\\." + className + ";");

				Matcher matcher2 = pattern2.matcher(imports);

				if (matcher2.find()) {
					continue;
				}

				afterImportsContent =
					com.liferay.portal.kernel.util.StringUtil.replaceFirst(
						afterImportsContent, matcher1.group(2),
						StringPool.BLANK, matcher1.start());

				continue outerLoop;
			}

			break;
		}

		return afterImportsContent;
	}

}