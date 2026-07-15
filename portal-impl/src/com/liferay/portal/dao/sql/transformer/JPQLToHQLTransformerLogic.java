/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Manuel de la Peña
 */
public class JPQLToHQLTransformerLogic implements SQLTransformerLogic {

	@Override
	public Function<String, String>[] getFunctions() {
		return new Function[] {_getCountFunction()};
	}

	private Function<String, String> _getCountFunction() {
		return (String sql) -> {
			Matcher matcher = _jpqlCountPattern.matcher(sql);

			if (matcher.find()) {
				String countExpression = matcher.group(1);
				String entityAlias = matcher.group(3);

				if (entityAlias.equals(countExpression)) {
					return matcher.replaceFirst(_HQL_COUNT_SQL);
				}
			}

			return sql;
		};
	}

	private static final String _HQL_COUNT_SQL = "SELECT COUNT(*) FROM $2 $3";

	private static final Pattern _jpqlCountPattern = Pattern.compile(
		"SELECT COUNT\\((\\S+)\\) FROM (\\S+) (\\S+)");

}