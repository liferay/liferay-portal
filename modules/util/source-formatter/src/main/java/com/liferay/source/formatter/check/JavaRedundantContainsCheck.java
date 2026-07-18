/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.source.formatter.parser.JavaTerm;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Shuyang Zhou
 */
public class JavaRedundantContainsCheck extends BaseJavaTermCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
		String fileName, String absolutePath, JavaTerm javaTerm,
		String fileContent) {

		String content = javaTerm.getContent();

		if (_isAllowedFileName(
				absolutePath,
				getAttributeValues(_ALLOWED_FILE_NAMES_KEY, absolutePath))) {

			return content;
		}

		String blankedContent = _blankComments(content);

		Matcher matcher = _containsPattern.matcher(blankedContent);

		while (matcher.find()) {
			boolean negated = false;

			if (matcher.group(1) != null) {
				negated = true;
			}

			String receiver = matcher.group(2);

			boolean map = false;

			if (matcher.group(3) != null) {
				map = true;
			}

			int[] keyPositions = _getBalancedPositions(
				blankedContent, matcher.end());

			if (keyPositions == null) {
				continue;
			}

			String key = blankedContent.substring(
				keyPositions[0], keyPositions[1]);

			if (_hasTopLevelComma(key)) {
				continue;
			}

			int i = keyPositions[1] + 1;

			if ((i >= blankedContent.length()) ||
				(blankedContent.charAt(i) != ')')) {

				continue;
			}

			i++;

			while ((i < blankedContent.length()) &&
				   Character.isWhitespace(blankedContent.charAt(i))) {

				i++;
			}

			if ((i >= blankedContent.length()) ||
				(blankedContent.charAt(i) != '{')) {

				continue;
			}

			String firstStatement = _getFirstStatement(blankedContent, i + 1);

			if (firstStatement == null) {
				continue;
			}

			String operation = null;
			String suggestion = null;

			if (map && negated) {
				operation = "put";
				suggestion = "a single \"putIfAbsent\" or \"computeIfAbsent\"";
			}
			else if (map) {
				if (_hasOperation(
						firstStatement, receiver, "get", key, false)) {

					operation = "get";
					suggestion = "a single \"get\" with a null check";
				}
				else if (_hasOperation(
							firstStatement, receiver, "remove", key, false)) {

					operation = "remove";
					suggestion = "a single \"remove\" with a null check";
				}
			}
			else if (negated) {
				if (!_isSetTypedReceiver(fileContent, receiver)) {
					continue;
				}

				operation = "add";
				suggestion = "the boolean result of a single \"add\"";
			}
			else {
				operation = "remove";
				suggestion = "the boolean result of a single \"remove\"";
			}

			if (operation == null) {
				continue;
			}

			if ((map && !negated) ||
				_hasOperation(
					firstStatement, receiver, operation, key,
					operation.equals("put"))) {

				addMessage(
					fileName,
					StringBundler.concat(
						"Combine the \"contains", map ? "Key" : "",
						"\" check on \"", receiver, "\" and the following \"",
						operation, "\" into ", suggestion),
					javaTerm.getLineNumber(matcher.start()));
			}
		}

		return content;
	}

	@Override
	protected String[] getCheckableJavaTermNames() {
		return new String[] {JAVA_METHOD};
	}

	private String _blankComments(String content) {
		char[] chars = content.toCharArray();

		int i = 0;

		while (i < chars.length) {
			char c = chars[i];

			if ((c == CharPool.QUOTE) || (c == CharPool.APOSTROPHE)) {
				i++;

				while ((i < chars.length) && (chars[i] != c)) {
					if (chars[i] == CharPool.BACK_SLASH) {
						i++;
					}

					i++;
				}

				i++;
			}
			else if ((c == CharPool.SLASH) && ((i + 1) < chars.length) &&
					 (chars[i + 1] == CharPool.SLASH)) {

				while ((i < chars.length) && (chars[i] != CharPool.NEW_LINE)) {
					chars[i] = CharPool.SPACE;

					i++;
				}
			}
			else if ((c == CharPool.SLASH) && ((i + 1) < chars.length) &&
					 (chars[i + 1] == CharPool.STAR)) {

				while (i < chars.length) {
					if ((chars[i] == CharPool.STAR) &&
						((i + 1) < chars.length) &&
						(chars[i + 1] == CharPool.SLASH)) {

						chars[i] = CharPool.SPACE;
						chars[i + 1] = CharPool.SPACE;

						i += 2;

						break;
					}

					if (chars[i] != CharPool.NEW_LINE) {
						chars[i] = CharPool.SPACE;
					}

					i++;
				}
			}
			else {
				i++;
			}
		}

		return new String(chars);
	}

	private int[] _getBalancedPositions(String content, int openParenPos) {
		int level = 1;

		for (int i = openParenPos; i < content.length(); i++) {
			char c = content.charAt(i);

			if (c == '(') {
				level++;
			}
			else if (c == ')') {
				level--;

				if (level == 0) {
					return new int[] {openParenPos, i};
				}
			}
		}

		return null;
	}

	private String _getFirstStatement(String content, int blockStartPos) {
		int level = 0;

		for (int i = blockStartPos; i < content.length(); i++) {
			char c = content.charAt(i);

			if ((c == '(') || (c == '{')) {
				level++;
			}
			else if ((c == ')') || (c == '}')) {
				if ((c == '}') && (level == 0)) {
					return content.substring(blockStartPos, i);
				}

				level--;
			}
			else if ((c == ';') && (level == 0)) {
				return content.substring(blockStartPos, i + 1);
			}
		}

		return null;
	}

	private boolean _hasOperation(
		String statement, String receiver, String operation, String key,
		boolean keyIsFirstArgument) {

		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				Pattern.quote(receiver), "\\s*\\.\\s*", operation, "\\s*\\("));

		Matcher matcher = pattern.matcher(statement);

		while (matcher.find()) {
			int[] argsPositions = _getBalancedPositions(
				statement, matcher.end());

			if (argsPositions == null) {
				return false;
			}

			String args = statement.substring(
				argsPositions[0], argsPositions[1]);

			if (keyIsFirstArgument) {
				int level = 0;

				for (int i = 0; i < args.length(); i++) {
					char c = args.charAt(i);

					if ((c == '(') || (c == '<') || (c == '[')) {
						level++;
					}
					else if ((c == ')') || (c == '>') || (c == ']')) {
						level--;
					}
					else if ((c == ',') && (level == 0)) {
						args = args.substring(0, i);

						break;
					}
				}
			}

			if (Objects.equals(_normalize(args), _normalize(key))) {
				return true;
			}
		}

		return false;
	}

	private boolean _hasTopLevelComma(String s) {
		int level = 0;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if ((c == '(') || (c == '<') || (c == '[')) {
				level++;
			}
			else if ((c == ')') || (c == '>') || (c == ']')) {
				level--;
			}
			else if ((c == ',') && (level == 0)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isAllowedFileName(
		String absolutePath, List<String> allowedFileNames) {

		for (String allowedFileName : allowedFileNames) {
			if (absolutePath.endsWith(allowedFileName)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isSetTypedReceiver(String fileContent, String receiver) {
		Pattern pattern = Pattern.compile(
			StringBundler.concat(
				"\\b\\w*Set\\s*(<[^<>]*(<[^<>]*>)?[^<>]*>)?\\s+",
				Pattern.quote(receiver), "\\b"));

		Matcher matcher = pattern.matcher(fileContent);

		if (matcher.find()) {
			return true;
		}

		pattern = Pattern.compile(
			Pattern.quote(receiver) + "\\s*=\\s*new\\s+\\w*Set\\s*[<(]");

		matcher = pattern.matcher(fileContent);

		return matcher.find();
	}

	private String _normalize(String s) {
		return s.replaceAll("\\s+", "");
	}

	private static final String _ALLOWED_FILE_NAMES_KEY = "allowedFileNames";

	private static final Pattern _containsPattern = Pattern.compile(
		"if \\((!)?(\\w+)\\.contains(Key)?\\(");

}