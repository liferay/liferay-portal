/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;

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

		return StringBundler.concat(
			"select * from (select *, row_number() over ( order by ",
			"CURRENT_TIMESTAMP) as _page_row_num from (", sql,
			" ) _temp_table_1 ) _temp_table_2 where _page_row_num between ",
			offset + 1, " and ", limit, " order by _page_row_num");
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

}