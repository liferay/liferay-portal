/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.file;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Michael Hashimoto
 */
public class JSTestClassFile extends BaseTestClassFile {

	@Override
	public List<TestClassFileMethod> getTestClassFileMethods()
		throws IOException {

		if (_testClassFileMethods == null) {
			_testClassFileMethods = new ArrayList<>();

			_parse();
		}

		return Collections.unmodifiableList(_testClassFileMethods);
	}

	protected JSTestClassFile(File file, TestPackage testPackage) {
		super(file, testPackage);
	}

	private static int _skipString(String content, int index) {
		char quote = content.charAt(index);

		index++;

		while (index < content.length()) {
			char c = content.charAt(index);

			if (c == '\\') {
				index = index + 2;

				continue;
			}

			if (c == quote) {
				return index + 1;
			}

			if ((quote == '`') && content.startsWith("${", index)) {
				index = _skipTemplateExpression(content, index + 2);

				continue;
			}

			index++;
		}

		return index;
	}

	private static int _skipTemplateExpression(String content, int index) {
		int depth = 1;

		while (index < content.length()) {
			char c = content.charAt(index);

			if ((c == '\'') || (c == '\"') || (c == '`')) {
				index = _skipString(content, index);

				continue;
			}

			if (c == '{') {
				depth++;
			}
			else if (c == '}') {
				depth--;

				if (depth == 0) {
					return index + 1;
				}
			}

			index++;
		}

		return index;
	}

	private String _expandTitle(List<String> row, int rowIndex, String title) {
		StringBuilder sb = new StringBuilder();

		int argIndex = 0;
		int length = title.length();

		for (int i = 0; i < length; i++) {
			char c = title.charAt(i);

			if ((c != '%') || ((i + 1) >= length)) {
				sb.append(c);

				continue;
			}

			char nextChar = title.charAt(i + 1);

			if (nextChar == '%') {
				sb.append('%');

				i++;

				continue;
			}

			if (nextChar == '#') {
				sb.append(rowIndex);

				i++;

				continue;
			}

			if (_PLACEHOLDERS.indexOf(nextChar) == -1) {
				sb.append(c);

				continue;
			}

			if (argIndex < row.size()) {
				sb.append(row.get(argIndex));

				argIndex++;
			}
			else {
				sb.append(c);
				sb.append(nextChar);
			}

			i++;
		}

		return sb.toString();
	}

	private Map<String, String> _getBindings(
		Map<String, String> bindings, List<String> parameters,
		List<String> row) {

		Map<String, String> rowBindings = new HashMap<>(bindings);

		for (int i = 0; i < parameters.size(); i++) {
			if (i >= row.size()) {
				break;
			}

			rowBindings.put(parameters.get(i), row.get(i));
		}

		return rowBindings;
	}

	private int _getBodyIndex(String content, int index) {
		int length = content.length();

		while (index < length) {
			char c = content.charAt(index);

			if (c == '{') {
				return index;
			}

			if (c == ';') {
				return -1;
			}

			if (c == '(') {
				index = _skipBalanced(content, index);

				continue;
			}

			if ((c == '/') && ((index + 1) < length)) {
				char nextChar = content.charAt(index + 1);

				if ((nextChar == '/') || (nextChar == '*')) {
					index = _skipComment(content, index);

					continue;
				}

				if (_isRegexStart(content, index)) {
					index = _skipRegex(content, index);

					continue;
				}
			}

			if ((c == '\'') || (c == '\"') || (c == '`')) {
				index = _skipString(content, index);

				continue;
			}

			index++;
		}

		return -1;
	}

	private List<List<String>> _getEachRows(String content, int index) {
		if (!_isEach(content, index)) {
			return null;
		}

		int length = content.length();

		while ((index < length) && (content.charAt(index) == '.')) {
			index++;

			while ((index < length) &&
				   Character.isJavaIdentifierPart(content.charAt(index))) {

				index++;
			}
		}

		index = _skipWhitespaceAndComments(content, index);

		if ((index >= length) || (content.charAt(index) != '(')) {
			return null;
		}

		index = _skipWhitespaceAndComments(content, index + 1);

		if ((index >= length) || (content.charAt(index) != '[')) {
			return null;
		}

		List<List<String>> rows = new ArrayList<>();

		index = _skipWhitespaceAndComments(content, index + 1);

		while (index < length) {
			char c = content.charAt(index);

			if (c == ']') {
				return rows;
			}

			if (c == ',') {
				index = _skipWhitespaceAndComments(content, index + 1);

				continue;
			}

			List<String> row = new ArrayList<>();

			if (c == '[') {
				index = _skipWhitespaceAndComments(content, index + 1);

				while (index < length) {
					c = content.charAt(index);

					if (c == ']') {
						index = _skipWhitespaceAndComments(content, index + 1);

						break;
					}

					if (c == ',') {
						index = _skipWhitespaceAndComments(content, index + 1);

						continue;
					}

					Title title = _getRowTitle(content, index);

					if ((title == null) || title.dynamic) {
						return null;
					}

					row.add(title.value);

					index = _skipWhitespaceAndComments(content, title.index);
				}
			}
			else {
				Title title = _getRowTitle(content, index);

				if ((title == null) || title.dynamic) {
					return null;
				}

				row.add(title.value);

				index = _skipWhitespaceAndComments(content, title.index);
			}

			rows.add(row);
		}

		return null;
	}

	private Title _getExpressionTitle(String content, int index) {
		int startIndex = index;

		int depth = 0;
		int length = content.length();

		while (index < length) {
			char c = content.charAt(index);

			if ((c == '/') && ((index + 1) < length)) {
				char nextChar = content.charAt(index + 1);

				if ((nextChar == '/') || (nextChar == '*')) {
					index = _skipComment(content, index);

					continue;
				}
			}

			if ((c == '\'') || (c == '\"') || (c == '`')) {
				index = _skipString(content, index);

				continue;
			}

			if ((c == '(') || (c == '[') || (c == '{')) {
				depth++;
			}
			else if ((c == ']') || (c == '}')) {
				depth--;
			}
			else if (c == ')') {
				if (depth == 0) {
					break;
				}

				depth--;
			}
			else if ((c == ',') && (depth == 0)) {
				break;
			}

			index++;
		}

		String expression = content.substring(startIndex, index);

		expression = expression.trim();

		if (expression.isEmpty() || expression.contains("=>")) {
			return null;
		}

		Title title = new Title();

		title.dynamic = true;
		title.index = index;
		title.value = "${" + expression + "}";

		return title;
	}

	private List<String> _getParameters(String content, int index) {
		List<String> parameters = new ArrayList<>();

		int bodyIndex = _getBodyIndex(content, index);

		if (bodyIndex == -1) {
			return parameters;
		}

		int parametersIndex = content.lastIndexOf('(', bodyIndex);

		if (parametersIndex < index) {
			int endIndex = content.lastIndexOf("=>", bodyIndex);

			if (endIndex < index) {
				return parameters;
			}

			String parameter = content.substring(index, endIndex);

			parameter = parameter.trim();

			if (parameter.startsWith(",")) {
				parameter = parameter.substring(1);

				parameter = parameter.trim();
			}

			if (!parameter.isEmpty()) {
				parameters.add(parameter);
			}

			return parameters;
		}

		int endIndex = content.indexOf(')', parametersIndex);

		if (endIndex == -1) {
			return parameters;
		}

		String parametersString = content.substring(
			parametersIndex + 1, endIndex);

		for (String parameter : parametersString.split(",")) {
			parameter = parameter.trim();

			if (!parameter.isEmpty()) {
				parameters.add(parameter);
			}
		}

		return parameters;
	}

	private Title _getRowTitle(String content, int index) {
		int length = content.length();

		char c = content.charAt(index);

		if ((c == '\'') || (c == '\"') || (c == '`')) {
			int endIndex = _skipString(content, index);

			Title stringValue = new Title();

			stringValue.index = endIndex;
			stringValue.value = _unescape(
				content.substring(index + 1, endIndex - 1));

			return stringValue;
		}

		int endIndex = index;

		while ((endIndex < length) &&
			   ((content.charAt(endIndex) == '.') ||
				(content.charAt(endIndex) == '-') ||
				Character.isLetterOrDigit(content.charAt(endIndex)))) {

			endIndex++;
		}

		if (endIndex == index) {
			return null;
		}

		Title value = new Title();

		value.index = endIndex;
		value.value = content.substring(index, endIndex);

		value.dynamic = !_isLiteral(value.value);

		return value;
	}

	private Title _getTitle(
		Map<String, String> bindings, String content, int index) {

		int length = content.length();

		index = _skipWhitespaceAndComments(content, index);

		if (index >= length) {
			return null;
		}

		char c = content.charAt(index);

		if ((c != '\'') && (c != '\"') && (c != '`')) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		boolean dynamic = false;

		while (true) {
			int endIndex = _skipString(content, index);

			String literal = content.substring(index + 1, endIndex - 1);

			if (content.charAt(index) == '`') {
				literal = _resolve(bindings, literal);

				if (literal.contains("${")) {
					dynamic = true;
				}
			}

			sb.append(_unescape(literal));

			index = _skipWhitespaceAndComments(content, endIndex);

			if ((index >= length) || (content.charAt(index) != '+')) {
				break;
			}

			index = _skipWhitespaceAndComments(content, index + 1);

			if (index >= length) {
				break;
			}

			c = content.charAt(index);

			if ((c != '\'') && (c != '\"') && (c != '`')) {
				break;
			}
		}

		Title title = new Title();

		title.dynamic = dynamic;
		title.index = index;
		title.value = sb.toString();

		return title;
	}

	private int _getTitleIndex(String content, int index) {
		boolean each = false;

		int length = content.length();

		while ((index < length) && (content.charAt(index) == '.')) {
			int endIndex = index + 1;

			while ((endIndex < length) &&
				   Character.isJavaIdentifierPart(content.charAt(endIndex))) {

				endIndex++;
			}

			if (content.startsWith("each", index + 1)) {
				each = true;
			}

			index = endIndex;
		}

		index = _skipWhitespaceAndComments(content, index);

		if (index >= length) {
			return -1;
		}

		if (each) {
			char c = content.charAt(index);

			if (c == '`') {
				index = _skipString(content, index);
			}
			else if (c == '(') {
				index = _skipBalanced(content, index);
			}
			else {
				return -1;
			}

			index = _skipWhitespaceAndComments(content, index);
		}

		if ((index >= length) || (content.charAt(index) != '(')) {
			return -1;
		}

		return index + 1;
	}

	private boolean _isDescribeWord(String word) {
		for (String describeWord : _DESCRIBE_WORDS) {
			if (Objects.equals(word, describeWord)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isEach(String content, int index) {
		int length = content.length();

		while ((index < length) && (content.charAt(index) == '.')) {
			int endIndex = index + 1;

			while ((endIndex < length) &&
				   Character.isJavaIdentifierPart(content.charAt(endIndex))) {

				endIndex++;
			}

			if (Objects.equals(
					content.substring(index + 1, endIndex), "each")) {

				return true;
			}

			index = endIndex;
		}

		return false;
	}

	private boolean _isLiteral(String value) {
		for (String literal : _LITERALS) {
			if (Objects.equals(value, literal)) {
				return true;
			}
		}

		char c = value.charAt(0);

		if ((c == '-') || Character.isDigit(c)) {
			return true;
		}

		return false;
	}

	private boolean _isRegexStart(String content, int index) {
		int length = content.length();

		if (((index + 1) < length) && (content.charAt(index + 1) == '>')) {
			return false;
		}

		int i = index - 1;

		while ((i >= 0) && Character.isWhitespace(content.charAt(i))) {
			i--;
		}

		if (i < 0) {
			return true;
		}

		char c = content.charAt(i);

		if (Character.isJavaIdentifierPart(c)) {
			int endIndex = i + 1;

			int startIndex = endIndex;

			while ((startIndex > 0) &&
				   Character.isJavaIdentifierPart(
					   content.charAt(startIndex - 1))) {

				startIndex--;
			}

			String word = content.substring(startIndex, endIndex);

			for (String regexKeyword : _REGEX_KEYWORDS) {
				if (Objects.equals(word, regexKeyword)) {
					return true;
				}
			}

			return false;
		}

		if ((c == ')') || (c == ']') || (c == '<')) {
			return false;
		}

		return true;
	}

	private boolean _isTestWord(String word) {
		for (String testWord : _TEST_WORDS) {
			if (Objects.equals(word, testWord)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isUncertainTitle(String title) {
		for (String placeholder : _UNCERTAIN_PLACEHOLDERS) {
			if (title.contains(placeholder)) {
				return true;
			}
		}

		return false;
	}

	private List<String> _merge(List<String> first, List<String> second) {
		List<String> merged = new ArrayList<>(first);

		merged.addAll(second);

		return merged;
	}

	private void _parse() throws IOException {
		_parse(new HashMap<>(), getContent(), new ArrayList<>());
	}

	private void _parse(
		Map<String, String> bindings, String content,
		List<String> parentDescribeNames) {

		List<String> describeNames = new ArrayList<>();
		List<Integer> describeDepths = new ArrayList<>();

		int index = 0;

		String pendingDescribeName = null;
		int pendingDescribeBodyIndex = -1;

		int depth = 0;
		int length = content.length();

		while (index < length) {
			char c = content.charAt(index);

			if ((c == '/') && ((index + 1) < length)) {
				char nextChar = content.charAt(index + 1);

				if ((nextChar == '/') || (nextChar == '*')) {
					index = _skipComment(content, index);

					continue;
				}

				if (_isRegexStart(content, index)) {
					index = _skipRegex(content, index);

					continue;
				}
			}

			if ((c == '\'') || (c == '\"') || (c == '`')) {
				index = _skipString(content, index);

				continue;
			}

			if (c == '{') {
				depth++;

				if ((pendingDescribeName != null) &&
					(pendingDescribeBodyIndex == index)) {

					describeNames.add(pendingDescribeName);
					describeDepths.add(depth);

					pendingDescribeBodyIndex = -1;
					pendingDescribeName = null;
				}

				index++;

				continue;
			}

			if (c == '}') {
				depth--;

				while (!describeDepths.isEmpty()) {
					int lastIndex = describeDepths.size() - 1;

					if (describeDepths.get(lastIndex) <= depth) {
						break;
					}

					describeDepths.remove(lastIndex);
					describeNames.remove(lastIndex);
				}

				index++;

				continue;
			}

			if (!Character.isJavaIdentifierStart(c)) {
				index++;

				continue;
			}

			int wordEndIndex = index;

			while ((wordEndIndex < length) &&
				   Character.isJavaIdentifierPart(
					   content.charAt(wordEndIndex))) {

				wordEndIndex++;
			}

			String word = content.substring(index, wordEndIndex);

			if (((index > 0) && (content.charAt(index - 1) == '.')) ||
				(!_isDescribeWord(word) && !_isTestWord(word))) {

				index = wordEndIndex;

				continue;
			}

			int titleIndex = _getTitleIndex(content, wordEndIndex);

			if (titleIndex == -1) {
				index = wordEndIndex;

				continue;
			}

			Title title = _getTitle(bindings, content, titleIndex);

			if (title == null) {
				title = _getExpressionTitle(content, titleIndex);
			}

			if (title == null) {
				index = wordEndIndex;

				continue;
			}

			if (_isDescribeWord(word)) {
				List<List<String>> describeRows = _getEachRows(
					content, wordEndIndex);

				if ((describeRows != null) && !describeRows.isEmpty() &&
					!title.dynamic) {

					int bodyIndex = _getBodyIndex(content, title.index);

					if (bodyIndex != -1) {
						List<String> parameters = _getParameters(
							content, title.index);

						int bodyEndIndex = _skipBlock(content, bodyIndex);

						String body = content.substring(
							bodyIndex + 1, bodyEndIndex - 1);

						List<String> describes = _merge(
							parentDescribeNames, describeNames);

						for (int i = 0; i < describeRows.size(); i++) {
							List<String> rowDescribes = new ArrayList<>(
								describes);

							List<String> row = describeRows.get(i);

							rowDescribes.add(_expandTitle(row, i, title.value));

							_parse(
								_getBindings(bindings, parameters, row), body,
								rowDescribes);
						}

						index = bodyEndIndex;

						continue;
					}
				}

				pendingDescribeBodyIndex = _getBodyIndex(content, title.index);
				pendingDescribeName = title.value;
			}
			else {
				List<List<String>> rows = _getEachRows(content, wordEndIndex);

				boolean uncertain = _isUncertainTitle(title.value);

				if ((rows == null) || rows.isEmpty() || title.dynamic ||
					uncertain) {

					_testClassFileMethods.add(
						TestClassFileMethodFactory.newTestClassFileMethod(
							title.value,
							_merge(parentDescribeNames, describeNames), this));
				}
				else {
					for (int i = 0; i < rows.size(); i++) {
						_testClassFileMethods.add(
							TestClassFileMethodFactory.newTestClassFileMethod(
								_expandTitle(rows.get(i), i, title.value),
								_merge(parentDescribeNames, describeNames),
								this));
					}
				}
			}

			index = title.index;
		}
	}

	private String _resolve(Map<String, String> bindings, String literal) {
		if (bindings.isEmpty() || !literal.contains("${")) {
			return literal;
		}

		for (Map.Entry<String, String> entry : bindings.entrySet()) {
			literal = literal.replace(
				"${" + entry.getKey() + "}", entry.getValue());
		}

		return literal;
	}

	private int _skipBalanced(String content, int index) {
		int depth = 0;
		int length = content.length();

		while (index < length) {
			char c = content.charAt(index);

			if ((c == '/') && ((index + 1) < length)) {
				char nextChar = content.charAt(index + 1);

				if ((nextChar == '/') || (nextChar == '*')) {
					index = _skipComment(content, index);

					continue;
				}

				if (_isRegexStart(content, index)) {
					index = _skipRegex(content, index);

					continue;
				}
			}

			if ((c == '\'') || (c == '\"') || (c == '`')) {
				index = _skipString(content, index);

				continue;
			}

			if (c == '(') {
				depth++;
			}
			else if (c == ')') {
				depth--;

				if (depth == 0) {
					return index + 1;
				}
			}

			index++;
		}

		return index;
	}

	private int _skipBlock(String content, int index) {
		int depth = 0;
		int length = content.length();

		while (index < length) {
			char c = content.charAt(index);

			if ((c == '/') && ((index + 1) < length)) {
				char nextChar = content.charAt(index + 1);

				if ((nextChar == '/') || (nextChar == '*')) {
					index = _skipComment(content, index);

					continue;
				}

				if (_isRegexStart(content, index)) {
					index = _skipRegex(content, index);

					continue;
				}
			}

			if ((c == '\'') || (c == '\"') || (c == '`')) {
				index = _skipString(content, index);

				continue;
			}

			if (c == '{') {
				depth++;
			}
			else if (c == '}') {
				depth--;

				if (depth == 0) {
					return index + 1;
				}
			}

			index++;
		}

		return index;
	}

	private int _skipComment(String content, int index) {
		int length = content.length();

		if (content.startsWith("//", index)) {
			while ((index < length) && (content.charAt(index) != '\n')) {
				index++;
			}

			return index;
		}

		index = index + 2;

		while (index < length) {
			if (content.startsWith("*/", index)) {
				return index + 2;
			}

			index++;
		}

		return index;
	}

	private int _skipRegex(String content, int index) {
		int length = content.length();

		boolean characterClass = false;

		index++;

		while (index < length) {
			char c = content.charAt(index);

			if (c == '\\') {
				index = index + 2;

				continue;
			}

			if (c == '\n') {
				return index;
			}

			if (c == '[') {
				characterClass = true;
			}
			else if (c == ']') {
				characterClass = false;
			}
			else if ((c == '/') && !characterClass) {
				index++;

				while ((index < length) &&
					   Character.isLetter(content.charAt(index))) {

					index++;
				}

				return index;
			}

			index++;
		}

		return index;
	}

	private int _skipWhitespaceAndComments(String content, int index) {
		int length = content.length();

		while (index < length) {
			char c = content.charAt(index);

			if (Character.isWhitespace(c)) {
				index++;

				continue;
			}

			if ((c == '/') && ((index + 1) < length)) {
				char nextChar = content.charAt(index + 1);

				if ((nextChar == '/') || (nextChar == '*')) {
					index = _skipComment(content, index);

					continue;
				}

				if (_isRegexStart(content, index)) {
					index = _skipRegex(content, index);

					continue;
				}
			}

			break;
		}

		return index;
	}

	private String _unescape(String literal) {
		StringBuilder sb = new StringBuilder();

		int length = literal.length();

		for (int i = 0; i < length; i++) {
			char c = literal.charAt(i);

			if ((c != '\\') || ((i + 1) >= length)) {
				sb.append(c);

				continue;
			}

			i++;

			char escapedChar = literal.charAt(i);

			if (escapedChar == 'n') {
				sb.append('\n');
			}
			else if (escapedChar == 't') {
				sb.append('\t');
			}
			else {
				sb.append(escapedChar);
			}
		}

		return sb.toString();
	}

	private static final String[] _DESCRIBE_WORDS = {
		"describe", "fdescribe", "xdescribe"
	};

	private static final String[] _LITERALS = {
		"false", "null", "true", "undefined"
	};

	private static final String _PLACEHOLDERS = "difjops";

	private static final String[] _REGEX_KEYWORDS = {
		"case", "delete", "do", "else", "in", "instanceof", "new", "of",
		"return", "typeof", "void", "yield"
	};

	private static final String[] _TEST_WORDS = {
		"fit", "ftest", "it", "test", "xit", "xtest"
	};

	private static final String[] _UNCERTAIN_PLACEHOLDERS = {"%j", "%o", "%p"};

	private List<TestClassFileMethod> _testClassFileMethods;

	private static class Title {

		public boolean dynamic;
		public int index;
		public String value;

	}

}