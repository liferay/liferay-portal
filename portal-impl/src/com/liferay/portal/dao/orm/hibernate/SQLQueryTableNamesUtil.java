/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class SQLQueryTableNamesUtil {

	public static String[] getTableNames(String sql) {
		String[] tableNames = _portalCache.get(sql);

		if (tableNames != null) {
			return tableNames;
		}

		String lowerCaseSQL = StringUtil.toLowerCase(sql);

		Set<String> tableNameSet = new HashSet<>();

		// Find table name from the "from" clause

		int index = 0;

		while ((index = lowerCaseSQL.indexOf(" from ", index)) != -1) {
			index += 6;

			int[] indexes = _getTableNameIndexes(lowerCaseSQL, index);

			if (indexes == null) {
				continue;
			}

			tableNameSet.add(sql.substring(indexes[0], indexes[1]));
		}

		// Find table name from the "join" clause

		index = 0;

		while ((index = lowerCaseSQL.indexOf(" join ", index)) != -1) {
			index += 6;

			int[] indexes = _getTableNameIndexes(lowerCaseSQL, index);

			if (indexes == null) {
				continue;
			}

			tableNameSet.add(sql.substring(indexes[0], indexes[1]));
		}

		tableNames = tableNameSet.toArray(new String[0]);

		_portalCache.put(sql, tableNames);

		return tableNames;
	}

	private static int[] _getTableNameIndexes(String sql, int index) {
		int start = -1;
		int end = sql.length();

		for (int i = index; i < sql.length(); i++) {
			char c = sql.charAt(i);

			if (c == CharPool.OPEN_PARENTHESIS) {

				// There is a subquery in the current clause, so no need to
				// parse for a table name

				break;
			}
			else if ((c == CharPool.SPACE) ||
					 (c == CharPool.CLOSE_PARENTHESIS)) {

				if (start != -1) {
					end = i;

					break;
				}
			}
			else if (start == -1) {
				start = i;
			}
		}

		if (start == -1) {
			return null;
		}

		return new int[] {start, end};
	}

	private static final PortalCache<String, String[]> _portalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM,
			SQLQueryTableNamesUtil.class.getName());

}