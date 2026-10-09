/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class SQLLongNamesCheck extends BaseFileCheck {

	@Override
	protected String doProcess(
		String fileName, String absolutePath, String content) {

		_checkTables(fileName, content);

		return content;
	}

	private void _checkColumns(
		String fileName, String tableContent, int startLineNumber) {

		Matcher matcher = _columnPattern.matcher(tableContent);

		while (matcher.find()) {
			String columnName = matcher.group(1);

			if (columnName.length() <= _MAX_NAME_LENGTH) {
				continue;
			}

			addMessage(
				fileName,
				StringBundler.concat(
					"Column name \"", columnName, "\" should not exceed ",
					_MAX_NAME_LENGTH, " characters"),
				startLineNumber + getLineNumber(tableContent, matcher.start()));
		}
	}

	private void _checkTables(String fileName, String content) {
		Matcher matcher = _createTablePattern.matcher(content);

		while (matcher.find()) {
			String tableName = matcher.group(1);

			if (tableName.length() > _MAX_NAME_LENGTH) {
				addMessage(
					fileName,
					StringBundler.concat(
						"Table name \"", tableName, "\" should not exceed ",
						_MAX_NAME_LENGTH, " characters"),
					getLineNumber(content, matcher.start()));
			}

			int x = matcher.end();

			while (true) {
				x = content.indexOf(")", x + 1);

				if (x == -1) {
					return;
				}

				String tableContent = content.substring(matcher.end(), x);

				if (getLevel(tableContent) == 0) {
					_checkColumns(
						fileName, tableContent,
						getLineNumber(content, matcher.end()));

					break;
				}
			}
		}
	}

	private static final int _MAX_NAME_LENGTH = 30;

	private static final Pattern _columnPattern = Pattern.compile(
		"\n\t*(\\w+) ");
	private static final Pattern _createTablePattern = Pattern.compile(
		"create table (\\w+) \\(");

}