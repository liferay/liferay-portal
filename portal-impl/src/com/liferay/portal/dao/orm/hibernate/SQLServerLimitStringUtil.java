/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Minhchau Dang
 * @author Steven Cao
 */
public class SQLServerLimitStringUtil {

	public static String getLimitString(String sql, int offset, int limit) {
		if (_hasOrderBy(sql)) {
			return StringBundler.concat(
				sql, " offset ", offset, " rows fetch next ", limit - offset,
				" rows only");
		}

		String sqlLowerCase = StringUtil.toLowerCase(sql);

		int fromPos = sqlLowerCase.indexOf(" from ");

		String selectFrom = sql.substring(0, fromPos);

		int orderByPos = sqlLowerCase.lastIndexOf(" order by ");

		String selectFromWhere = null;

		String orderBy = StringPool.BLANK;

		if (orderByPos > 0) {
			selectFromWhere = sql.substring(fromPos, orderByPos);

			orderBy = sql.substring(orderByPos + 9);
		}
		else {
			selectFromWhere = sql.substring(fromPos);
		}

		String[] splitOrderBy = _splitOrderBy(selectFrom, orderBy);

		String innerOrderBy = splitOrderBy[0];
		String outerOrderBy = splitOrderBy[1];

		return StringBundler.concat(
			"select * from (select *, row_number() over (", outerOrderBy,
			") as _page_row_num from (",
			_getInnerSelectFrom(selectFrom, innerOrderBy, limit),
			selectFromWhere, innerOrderBy,
			" ) _temp_table_1 ) _temp_table_2 where _page_row_num between ",
			offset + 1, " and ", limit, " order by _page_row_num");
	}

	private static String _getInnerSelectFrom(
		String selectFrom, String innerOrderBy, int limit) {

		String innerSelectFrom = selectFrom;

		if (Validator.isNotNull(innerOrderBy)) {
			Matcher matcher = _selectPattern.matcher(innerSelectFrom);

			innerSelectFrom = matcher.replaceAll(
				StringBundler.concat("$1 top ", limit, StringPool.SPACE));
		}

		return innerSelectFrom;
	}

	private static boolean _hasOrderBy(String sql) {
		int depth = 0;

		for (int i = sql.length() - 1; i >= 0; i--) {
			char c = sql.charAt(i);

			if (c == CharPool.APOSTROPHE) {
				i = sql.lastIndexOf(CharPool.APOSTROPHE, i - 1);
			}
			else if (c == CharPool.CLOSE_PARENTHESIS) {
				depth++;
			}
			else if (c == CharPool.OPEN_PARENTHESIS) {
				depth--;
			}
			else if ((depth == 0) &&
					 sql.regionMatches(true, i, " order by ", 0, 10)) {

				return true;
			}
		}

		return false;
	}

	private static String[] _splitOrderBy(String selectFrom, String orderBy) {
		StringBundler innerOrderBySB = new StringBundler();
		StringBundler outerOrderBySB = new StringBundler();

		int depth = 0;
		List<String> orderByColumns = new ArrayList<>();
		int start = 0;

		for (int i = 0; i < orderBy.length(); i++) {
			char c = orderBy.charAt(i);

			if (c == CharPool.OPEN_PARENTHESIS) {
				depth++;
			}
			else if (c == CharPool.CLOSE_PARENTHESIS) {
				depth--;
			}
			else if ((c == CharPool.COMMA) && (depth == 0)) {
				String column = orderBy.substring(start, i);

				if (Validator.isNotNull(column)) {
					orderByColumns.add(column);
				}

				start = i + 1;
			}
		}

		if (start < orderBy.length()) {
			String column = orderBy.substring(start);

			if (Validator.isNotNull(column)) {
				orderByColumns.add(column);
			}
		}

		for (String orderByColumn : orderByColumns) {
			orderByColumn = orderByColumn.trim();

			String orderByColumnName = orderByColumn;

			String orderByType = "ASC";

			int spacePos = orderByColumn.lastIndexOf(CharPool.SPACE);

			if (spacePos != -1) {
				int parenPos = orderByColumn.indexOf(
					CharPool.OPEN_PARENTHESIS, spacePos);

				if (parenPos == -1) {
					orderByColumnName = orderByColumn.substring(0, spacePos);
					orderByType = orderByColumn.substring(spacePos + 1);
				}
			}

			String patternString = StringBundler.concat(
				"\\Q", orderByColumnName, "\\E as (\\w+)");

			Pattern pattern = Pattern.compile(
				patternString, Pattern.CASE_INSENSITIVE);

			Matcher matcher = pattern.matcher(selectFrom);

			if (matcher.find()) {
				orderByColumnName = matcher.group(1);
			}

			if (selectFrom.contains(orderByColumnName)) {
				if (outerOrderBySB.length() == 0) {
					outerOrderBySB.append(" order by ");
				}
				else {
					outerOrderBySB.append(StringPool.COMMA);
				}

				matcher = _qualifiedColumnPattern.matcher(orderByColumnName);

				orderByColumnName = matcher.replaceAll("$1");

				outerOrderBySB.append(orderByColumnName);

				outerOrderBySB.append(StringPool.SPACE);
				outerOrderBySB.append(orderByType);
			}
			else {
				if (innerOrderBySB.length() == 0) {
					innerOrderBySB.append(" order by ");
				}
				else {
					innerOrderBySB.append(StringPool.COMMA);
				}

				if (orderByColumnName.endsWith("ASC")) {
					orderByColumnName = orderByColumnName.substring(
						0, orderByColumnName.lastIndexOf("ASC"));
					orderByType = "ASC";
				}
				else if (orderByColumnName.endsWith("DESC")) {
					orderByColumnName = orderByColumnName.substring(
						0, orderByColumnName.lastIndexOf("DESC"));
					orderByType = "DESC";
				}

				innerOrderBySB.append(orderByColumnName);
				innerOrderBySB.append(StringPool.SPACE);
				innerOrderBySB.append(orderByType);
			}
		}

		if (outerOrderBySB.length() == 0) {
			outerOrderBySB.append(" order by CURRENT_TIMESTAMP");
		}

		return new String[] {
			innerOrderBySB.toString(), outerOrderBySB.toString()
		};
	}

	private static final Pattern _qualifiedColumnPattern = Pattern.compile(
		"\\w+\\.([\\w\\*]+)");
	private static final Pattern _selectPattern = Pattern.compile(
		"(SELECT(?: DISTINCT)?) ", Pattern.CASE_INSENSITIVE);

}