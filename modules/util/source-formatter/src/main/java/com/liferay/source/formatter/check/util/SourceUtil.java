/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ToolsUtil;
import com.liferay.portal.xml.SAXReaderFactory;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

/**
 * @author Hugo Huijser
 */
public class SourceUtil {

	public static boolean containsUnquoted(String s, String text) {
		int x = -1;

		while (true) {
			x = s.indexOf(text, x + 1);

			if (x == -1) {
				return false;
			}

			if (!ToolsUtil.isInsideQuotes(s, x)) {
				return true;
			}
		}
	}

	public static String getAbsolutePath(File file) {
		return getAbsolutePath(file.toPath());
	}

	public static String getAbsolutePath(Path filePath) {
		filePath = filePath.toAbsolutePath();

		filePath = filePath.normalize();

		return StringUtil.replace(
			filePath.toString(), CharPool.BACK_SLASH, CharPool.SLASH);
	}

	public static String getAbsolutePath(String fileName) {
		return getAbsolutePath(Paths.get(fileName));
	}

	public static Map<String, String> getAnnotationMemberValuePair(
		String annotation) {

		Map<String, String> annotationMemberValuePair = new HashMap<>();

		Matcher matcher = _annotationMemberValuePairPattern.matcher(annotation);

		while (matcher.find()) {
			annotationMemberValuePair.put(
				matcher.group(1), StringUtil.unquote(matcher.group(2)));
		}

		return annotationMemberValuePair;
	}

	public static List<String> getAnnotationsBlocks(String content) {
		List<String> annotationsBlocks = new ArrayList<>();

		Matcher matcher = _modifierPattern.matcher(content);

		while (matcher.find()) {
			int lineNumber = getLineNumber(content, matcher.end());

			String annotationsBlock = StringPool.BLANK;

			for (int i = lineNumber - 1;; i--) {
				String line = getLine(content, i);

				if (Validator.isNull(line) ||
					line.matches("\t*(private|public|protected| \\*/).*")) {

					if (Validator.isNotNull(annotationsBlock)) {
						annotationsBlocks.add(annotationsBlock);
					}

					break;
				}

				annotationsBlock = line + "\n" + annotationsBlock;
			}
		}

		return annotationsBlocks;
	}

	public static int getColumnIndex(
		String tablesSQLContent, String tableName, String columnName) {

		String tableSQL = _getTableSQL(tablesSQLContent, tableName);

		if (tableSQL == null) {
			return -1;
		}

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"(?i)\n\\s*", columnName, "_?\\s+([\\w\\(\\)]+)[\\s,]"));

		Matcher matcher = pattern.matcher(tableSQL);

		if (matcher.find()) {
			return matcher.start();
		}

		return -1;
	}

	public static String getIndent(String s) {
		StringBundler sb = new StringBundler(s.length());

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != CharPool.TAB) {
				break;
			}

			sb.append(CharPool.TAB);
		}

		return sb.toString();
	}

	public static String getLeadingSpaces(String line) {
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) != CharPool.SPACE) {
				return line.substring(0, i);
			}
		}

		return line;
	}

	public static String getLine(String content, int lineNumber) {
		int nextLineStartPos = getLineStartPos(content, lineNumber);

		if (nextLineStartPos == -1) {
			return null;
		}

		int nextLineEndPos = content.indexOf(
			CharPool.NEW_LINE, nextLineStartPos);

		if (nextLineEndPos == -1) {
			return content.substring(nextLineStartPos);
		}

		return content.substring(nextLineStartPos, nextLineEndPos);
	}

	public static int getLineNumber(String content, int pos) {
		return StringUtil.count(content, 0, pos, CharPool.NEW_LINE) + 1;
	}

	public static int getLineStartPos(String content, int lineNumber) {
		if (lineNumber <= 0) {
			return -1;
		}

		if (lineNumber == 1) {
			return 0;
		}

		int x = -1;

		for (int i = 1; i < lineNumber; i++) {
			x = content.indexOf(CharPool.NEW_LINE, x + 1);

			if (x == -1) {
				return x;
			}
		}

		return x + 1;
	}

	public static int[] getMultiLinePositions(
		String content, Pattern multiLinePattern) {

		List<Integer> multiLinePositions = new ArrayList<>();

		Matcher matcher = multiLinePattern.matcher(content);

		while (matcher.find()) {
			multiLinePositions.add(getLineNumber(content, matcher.start()));
			multiLinePositions.add(getLineNumber(content, matcher.end() - 1));
		}

		return ArrayUtil.toIntArray(multiLinePositions);
	}

	public static String getRootDirName(String absolutePath) {
		while (true) {
			int x = absolutePath.lastIndexOf(CharPool.SLASH);

			if (x == -1) {
				return StringPool.BLANK;
			}

			absolutePath = absolutePath.substring(0, x);

			File file = new File(absolutePath + "/portal-impl");

			if (file.exists()) {
				return absolutePath;
			}
		}
	}

	public static boolean hasTypo(String s1, String s2) {
		if (Validator.isNull(s1) || Validator.isNull(s2) || s1.equals(s2) ||
			(s1.charAt(0) != s2.charAt(0)) ||
			(s1.charAt(s1.length() - 1) != s2.charAt(s2.length() - 1))) {

			return false;
		}

		int min = Math.min(s1.length(), s2.length());
		int diff = Math.abs(s1.length() - s2.length());

		if ((min < 5) || (diff > 1)) {
			return false;
		}

		int i = StringUtil.startsWithWeight(s1, s2);

		s1 = s1.substring(i);

		if (s1.startsWith(StringPool.UNDERLINE)) {
			return false;
		}

		s2 = s2.substring(i);

		if (s2.startsWith(StringPool.UNDERLINE)) {
			return false;
		}

		for (int j = 1;; j++) {
			if ((j > s1.length()) || (j > s2.length())) {
				return true;
			}

			if (s1.charAt(s1.length() - j) == s2.charAt(s2.length() - j)) {
				continue;
			}

			char[] chars1 = s1.toCharArray();
			char[] chars2 = s2.toCharArray();

			Arrays.sort(chars1);
			Arrays.sort(chars2);

			return Arrays.equals(chars1, chars2);
		}
	}

	public static boolean isInsideMultiLines(
		int lineNumber, int[] multiLinePositions) {

		for (int i = 0; i < (multiLinePositions.length - 1); i += 2) {
			if (lineNumber < multiLinePositions[i]) {
				return false;
			}

			if (lineNumber <= multiLinePositions[i + 1]) {
				return true;
			}
		}

		return false;
	}

	public static boolean isLiteralString(String s) {
		if ((s == null) || (s.length() < 2)) {
			return false;
		}

		if ((s.charAt(0) == CharPool.QUOTE) &&
			(s.charAt(s.length() - 1) == CharPool.QUOTE)) {

			return true;
		}

		return false;
	}

	public static boolean isXML(String content) {
		if (readXML(content) != null) {
			return true;
		}

		return false;
	}

	public static Document readXML(File file) {
		SAXReader saxReader = SAXReaderFactory.getSAXReader(null, false, false);

		try {
			return saxReader.read(file);
		}
		catch (DocumentException documentException) {
			return null;
		}
	}

	public static Document readXML(String content) {
		SAXReader saxReader = SAXReaderFactory.getSAXReader(null, false, false);

		try {
			return saxReader.read(new UnsyncStringReader(content));
		}
		catch (DocumentException documentException) {
			return null;
		}
	}

	public static List<String> splitAnnotations(
			String annotationsBlock, String indent)
		throws IOException {

		List<String> annotations = new ArrayList<>();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(
					new UnsyncStringReader(annotationsBlock))) {

			String annotation = null;

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				if (annotation == null) {
					if (line.startsWith(indent + StringPool.AT)) {
						annotation = line + "\n";
					}

					continue;
				}

				String lineIndent = getIndent(line);

				if (lineIndent.length() < indent.length()) {
					annotations.add(annotation);

					annotation = null;
				}
				else if (line.startsWith(indent + StringPool.AT)) {
					annotations.add(annotation);

					annotation = line + "\n";
				}
				else {
					annotation += line + "\n";
				}
			}

			if (Validator.isNotNull(annotation)) {
				annotations.add(annotation);
			}
		}

		return annotations;
	}

	public static String stripQuotes(String s) {
		return stripQuotes(s, CharPool.APOSTROPHE, CharPool.QUOTE);
	}

	public static String stripQuotes(String s, char... delimiters) {
		List<Character> delimitersList = ListUtil.fromArray(delimiters);

		char delimiter = CharPool.SPACE;
		boolean insideQuotes = false;

		StringBundler sb = new StringBundler();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (insideQuotes) {
				if (c == delimiter) {
					int precedingBackSlashCount = 0;

					for (int j = i - 1; j >= 0; j--) {
						if (s.charAt(j) == CharPool.BACK_SLASH) {
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
			else if (delimitersList.contains(c)) {
				delimiter = c;
				insideQuotes = true;
			}
			else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private static String _getTableSQL(
		String tablesSQLContent, String tableName) {

		Pattern pattern = Pattern.compile("create table " + tableName + "_? ");

		Matcher matcher = pattern.matcher(tablesSQLContent);

		if (!matcher.find()) {
			return null;
		}

		int x = tablesSQLContent.indexOf(");", matcher.start());

		if (x == -1) {
			return null;
		}

		return tablesSQLContent.substring(matcher.start(), x + 1);
	}

	private static final Pattern _annotationMemberValuePairPattern =
		Pattern.compile("(\\w+) = (\".*?\"|.*(?=[,\\)\\s]))");
	private static final Pattern _modifierPattern = Pattern.compile(
		"[^\n]\n(\t*)(public|protected|private)");

}