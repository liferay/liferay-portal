/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.check.comparator.PropertyNameComparator;
import com.liferay.source.formatter.check.util.SourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alan Huang
 */
public class PropertiesTestFileCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (absolutePath.contains("/dependencies/") ||
			!fileName.endsWith("/test.properties")) {

			return content;
		}

		String rootDirName = SourceUtil.getRootDirName(absolutePath);

		if (!rootDirName.equals(StringPool.BLANK) &&
			absolutePath.equals(rootDirName + "/test.properties")) {

			_checkTestPropertiesOrder(fileName, content);
		}
		else {
			content = _sortProperties(content);
		}

		content = _formatSQLQuery(content);

		if (isAttributeValue(
				_CHECK_TESTRAY_MAIN_COMPONENT_NAME_KEY, absolutePath)) {

			_checkTestrayMainComponentName(fileName, absolutePath, content);
		}

		return content;
	}

	private String _addParenthesis(String sqlClauses) throws IOException {
		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(sqlClauses))) {

			String line = StringPool.BLANK;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = StringUtil.trimLeading(line);

				if (line.startsWith("(") || line.startsWith(")")) {
					sb.append(line);
					sb.append("\n");

					continue;
				}

				int x = StringUtil.indexOfAny(
					line, new String[] {" AND \\", " OR \\"});

				if (x == -1) {
					x = line.lastIndexOf("\\");
				}

				if (x == -1) {
					sb.append("(");
					sb.append(line);
					sb.append(")");
					sb.append("\n");

					continue;
				}

				sb.append("(");
				sb.append(line.substring(0, x));
				sb.append(")");
				sb.append(line.substring(x));
				sb.append("\n");
			}
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private String _checkIndentation(String sqlClauses) throws IOException {
		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(sqlClauses))) {

			int level = 1;

			String line = StringPool.BLANK;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.replaceFirst("^\\( *(\\(.+\\)) *\\)$", "$1");

				sb.append(_fixIndentation(line, level));

				sb.append("\n");

				level += getLevel(line, "(", ")");
			}
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private void _checkTestPropertiesOrder(String fileName, String content) {
		String commentCategory = null;
		String commentPrefix = null;
		String previousLine = null;
		String previousPropertyKey = null;

		String[] lines = content.split("\n");

		int lineNumber = 0;

		for (String line : lines) {
			lineNumber++;

			if ((line.startsWith("##") || line.startsWith("    #")) &&
				!line.contains("=")) {

				if (commentPrefix == null) {
					commentCategory = line;
					commentPrefix = line;

					continue;
				}

				if (line.startsWith(commentPrefix) && !line.contains("=")) {
					commentCategory += "\n" + line;

					continue;
				}
			}

			if ((commentCategory != null) &&
				commentCategory.startsWith(commentPrefix + "\n") &&
				commentCategory.endsWith("\n" + commentPrefix)) {

				commentCategory = null;
				commentPrefix = null;
				previousLine = null;
				previousPropertyKey = null;
			}

			if ((commentCategory != null) && !Validator.isBlank(line) &&
				!line.startsWith(StringPool.FOUR_SPACES)) {

				addMessage(
					fileName, "Incorrect indentation on line " + lineNumber);

				return;
			}

			int x = line.indexOf('=');

			if ((x == -1) ||
				((previousLine != null) && previousLine.endsWith("\\"))) {

				previousLine = line;

				continue;
			}

			String propertyKey = StringUtil.trimLeading(line.substring(0, x));

			if (propertyKey.startsWith(StringPool.POUND)) {
				propertyKey = propertyKey.substring(1);
			}

			if (Validator.isNull(previousPropertyKey)) {
				previousLine = line;
				previousPropertyKey = propertyKey;

				continue;
			}

			PropertyNameComparator propertyNameComparator =
				new PropertyNameComparator();

			int compare = propertyNameComparator.compare(
				previousPropertyKey, propertyKey);

			if (compare > 0) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Incorrect order of properties: \"", propertyKey,
						"\" should come before \"", previousPropertyKey, "\""),
					lineNumber);
			}

			previousLine = line;
			previousPropertyKey = propertyKey;
		}
	}

	private void _checkTestrayMainComponentName(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!isModulesFile(absolutePath) ||
			absolutePath.contains("/modules/apps/archived/")) {

			return;
		}

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		String testrayMainComponentName = properties.getProperty(
			"testray.main.component.name");

		if (testrayMainComponentName == null) {
			return;
		}

		List<String> testrayAllTeamsComponentNames =
			_getTestrayAllTeamsComponentNames();

		if (!testrayAllTeamsComponentNames.contains(testrayMainComponentName)) {
			addMessage(
				fileName,
				StringBundler.concat(
					"Property value \"", testrayMainComponentName,
					"\" does not exist in \"testray.team.*.component.names\" ",
					"in ", SourceUtil.getRootDirName(absolutePath),
					"/test.properties"));
		}
	}

	private int _compareTo(String sqlClause, String nextSQLClause) {
		if (sqlClause.endsWith("\")") && nextSQLClause.endsWith("\")")) {
			sqlClause = sqlClause.substring(0, sqlClause.length() - 2);
			nextSQLClause = nextSQLClause.substring(
				0, nextSQLClause.length() - 2);
		}

		return sqlClause.compareTo(nextSQLClause);
	}

	private String _fixIndentation(String line, int level) {
		String trimmedLine = StringUtil.trim(line);

		if (Validator.isNull(trimmedLine)) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		for (int i = 0; i < level; i++) {
			if ((i == (level - 1)) && trimmedLine.startsWith(")")) {
				break;
			}

			sb.append(StringPool.FOUR_SPACES);
		}

		sb.append(trimmedLine);

		return sb.toString();
	}

	private String _formatSQLQuery(String content) throws IOException {
		StringBuffer sb = new StringBuffer();

		Matcher matcher = _sqlPattern1.matcher(content);

		outerLoop:
		while (matcher.find()) {
			String originalSqlClauses = matcher.group(2);

			String sqlClauses = originalSqlClauses.replaceAll("\\\\\n *", "");

			sqlClauses = sqlClauses.replaceAll("\\( +\\(", "((");
			sqlClauses = sqlClauses.replaceAll("\\) +\\)", "))");

			int x = sqlClauses.indexOf("(");

			if (x == -1) {
				continue;
			}

			int y = x;
			String s = StringPool.BLANK;

			while (true) {
				y = sqlClauses.indexOf(")", y + 1);

				if (y == -1) {
					continue outerLoop;
				}

				s = sqlClauses.substring(x, y + 1);

				int level = getLevel(s);

				if (level != 0) {
					continue;
				}

				sqlClauses = StringUtil.replaceFirst(
					sqlClauses, s, _removeRedundantParenthesis(s), x);

				x = sqlClauses.indexOf("(", x + 1);

				if (x == -1) {
					break;
				}

				y = x;
			}

			sqlClauses = StringUtil.replace(sqlClauses, " AND ", " AND \\\n");
			sqlClauses = StringUtil.replace(sqlClauses, " OR ", " OR \\\n");

			sqlClauses = _addParenthesis(sqlClauses);

			sqlClauses = _checkIndentation(sqlClauses);

			sqlClauses = _sortSQLClauses(sqlClauses);

			sqlClauses = "\\\n" + sqlClauses;

			if (originalSqlClauses.endsWith("\\\n")) {
				sqlClauses = sqlClauses + "\n";
			}

			if (!sqlClauses.equals(originalSqlClauses)) {
				String replacement = StringUtil.replaceFirst(
					matcher.group(), matcher.group(2), sqlClauses);

				matcher.appendReplacement(
					sb, Matcher.quoteReplacement(replacement));
			}
		}

		if (sb.length() > 0) {
			matcher.appendTail(sb);

			return sb.toString();
		}

		return content;
	}

	private synchronized String _getRootTestPropertiesContent() {
		if (_rootTestPropertiesContent != null) {
			return _rootTestPropertiesContent;
		}

		File portalDir = getPortalDir();

		if (portalDir == null) {
			return null;
		}

		File file = new File(portalDir + "/test.properties");

		if (!file.exists()) {
			return null;
		}

		_rootTestPropertiesContent = FileUtil.read(file);

		return _rootTestPropertiesContent;
	}

	private String _getSQLClause(String line) {
		Matcher matcher = _sqlPattern2.matcher(line);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private synchronized List<String> _getTestrayAllTeamsComponentNames()
		throws IOException {

		if (_testrayAllTeamsComponentNames != null) {
			return _testrayAllTeamsComponentNames;
		}

		_testrayAllTeamsComponentNames = new ArrayList<>();

		File file = new File(getPortalDir(), "test.properties");

		if (!file.exists()) {
			return _testrayAllTeamsComponentNames;
		}

		Properties properties = new Properties();

		properties.load(new FileInputStream(file));

		List<String> testrayAvailableComponentNames = ListUtil.fromString(
			properties.getProperty("testray.available.component.names"),
			StringPool.COMMA);

		for (String testrayAvailableComponentName :
				testrayAvailableComponentNames) {

			if (!testrayAvailableComponentName.startsWith("${") &&
				!testrayAvailableComponentName.endsWith("}")) {

				continue;
			}

			String testrayTeamComponentName =
				testrayAvailableComponentName.substring(
					2, testrayAvailableComponentName.length() - 1);

			List<String> testrayTeamComponentNames = ListUtil.fromString(
				properties.getProperty(testrayTeamComponentName),
				StringPool.COMMA);

			if (ListUtil.isEmpty(testrayTeamComponentNames)) {
				continue;
			}

			_testrayAllTeamsComponentNames.addAll(testrayTeamComponentNames);
		}

		return _testrayAllTeamsComponentNames;
	}

	private String _mergeProperties(Map<String, String> propertiesMap) {
		StringBundler sb = new StringBundler();

		for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
			String key = entry.getKey();

			boolean multiLine = false;

			String[] values = StringUtil.split(entry.getValue());

			if ((values.length == 1) && !StringUtil.equals(values[0], "**")) {
				int lineLength = 1 + key.length() + values[0].length();

				if (lineLength > getMaxLineLength()) {
					multiLine = true;
				}
			}
			else if (values.length > 1) {
				multiLine = true;
			}

			if (multiLine && !StringUtil.endsWith(sb.toString(), "\n\n")) {
				sb.append("\n");
			}

			sb.append(key);
			sb.append("=");

			if (multiLine) {
				for (String value : values) {
					sb.append("\\\n    ");
					sb.append(value);
					sb.append(",");
				}

				sb.setIndex(sb.index() - 1);

				sb.append("\n\n");
			}
			else {
				sb.append(values);

				sb.append("\n");
			}
		}

		return StringUtil.trim(sb.toString());
	}

	private String _removeRedundantParenthesis(String sqlClause) {
		int x = -1;

		while (true) {
			x = StringUtil.indexOfAny(
				sqlClause, new String[] {" AND ", " OR "}, x + 1);

			if (x == -1) {
				break;
			}

			int level1 = getLevel(sqlClause.substring(0, x));
			int level2 = getLevel(sqlClause.substring(x));

			if ((level1 == 1) && (level2 == -1)) {
				sqlClause = StringUtil.insert(
					sqlClause, "\\\n", sqlClause.length() - 1);
				sqlClause = StringUtil.insert(sqlClause, "\\\n", 1);

				return sqlClause;
			}
		}

		if (sqlClause.startsWith("((")) {
			return _removeRedundantParenthesis(
				sqlClause.substring(1, sqlClause.length() - 1));
		}

		return sqlClause;
	}

	private String _sortProperties(String content) throws IOException {
		String rootTestPropertiesContent = _getRootTestPropertiesContent();

		if (rootTestPropertiesContent == null) {
			return content;
		}

		Map<String, String> propertiesMap1 = new TreeMap<>(
			new NaturalOrderStringComparator());
		Map<String, String> propertiesMap2 = new TreeMap<>(
			new NaturalOrderStringComparator());

		Properties properties = new Properties();

		properties.load(new StringReader(content));

		Enumeration<String> enumeration =
			(Enumeration<String>)properties.propertyNames();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			String keyPrefix = key;

			int x = keyPrefix.indexOf("[");

			if (x != -1) {
				keyPrefix = keyPrefix.substring(0, x);
			}

			if (rootTestPropertiesContent.contains(keyPrefix + "=") ||
				rootTestPropertiesContent.contains(keyPrefix + "[")) {

				propertiesMap1.put(key, properties.getProperty(key));
			}
			else {
				propertiesMap2.put(key, properties.getProperty(key));
			}
		}

		String mergedProperties = _mergeProperties(propertiesMap1);

		if (Validator.isBlank(mergedProperties)) {
			return _mergeProperties(propertiesMap2);
		}

		return StringUtil.trimTrailing(
			mergedProperties + "\n\n" + _mergeProperties(propertiesMap2));
	}

	private String _sortSQLClauses(String sqlClauses) {
		Matcher matcher = _sqlPattern2.matcher(sqlClauses);

		while (matcher.find()) {
			int lineNumber = getLineNumber(sqlClauses, matcher.start());

			if (Validator.isNull(matcher.group(4))) {
				continue;
			}

			String nextSQLClause = _getSQLClause(
				SourceUtil.getLine(sqlClauses, lineNumber + 1));

			if (nextSQLClause == null) {
				continue;
			}

			String sqlClause = matcher.group(1);

			if (_compareTo(sqlClause, nextSQLClause) > 0) {
				sqlClauses = StringUtil.replaceFirst(
					sqlClauses, nextSQLClause, sqlClause,
					getLineStartPos(sqlClauses, lineNumber + 1));

				return StringUtil.replaceFirst(
					sqlClauses, sqlClause, nextSQLClause,
					getLineStartPos(sqlClauses, lineNumber));
			}
		}

		return sqlClauses;
	}

	private static final String _CHECK_TESTRAY_MAIN_COMPONENT_NAME_KEY =
		"checkTestrayMainComponentName";

	private static final Pattern _sqlPattern1 = Pattern.compile(
		"(?<=\\A|\n)test\\.batch\\.run\\.property(\\.global)?\\.query.+]=" +
			"([\\s\\S]*?[^\\\\])(?=(\\Z|\n))");
	private static final Pattern _sqlPattern2 = Pattern.compile(
		"\\s(\\(.* ([!=]=|~) .+\\))( (AND|OR) )?(\\\\)?");

	private String _rootTestPropertiesContent;
	private List<String> _testrayAllTeamsComponentNames;

}