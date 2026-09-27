/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author James Lefeu
 * @author Peter Shin
 * @author Shuyang Zhou
 */
public class IndexMetadataFactoryUtil {

	public static IndexMetadata createIndexMetadata(
		boolean unique, String tableName, String... columnNames) {

		if (columnNames == null) {
			throw new NullPointerException("Column names are missing");
		}

		return new IndexMetadata(
			createIndexName(tableName, columnNames), tableName, unique,
			columnNames);
	}

	public static IndexMetadata createIndexMetadata(String createSQL) {
		boolean unique = createSQL.contains("create unique index ");

		int start = createSQL.indexOf("IX_");

		if (start < 0) {
			throw new IllegalArgumentException(
				"Unable to find index name start " + createSQL);
		}

		int end = createSQL.indexOf(CharPool.SPACE, start + 3);

		String indexName = createSQL.substring(start, end);

		start = createSQL.indexOf("on ", end + 1);

		if (start < 0) {
			throw new IllegalArgumentException(
				"Unable to find table name start " + createSQL);
		}

		start += 3;

		end = createSQL.indexOf(CharPool.SPACE, start + 1);

		if (end < 0) {
			throw new IllegalArgumentException(
				"Unable to find table name end " + createSQL);
		}

		String tableName = createSQL.substring(start, end);

		start = createSQL.indexOf(CharPool.OPEN_PARENTHESIS, end + 1);

		if (start < 0) {
			throw new IllegalArgumentException(
				"Unable to find column names start " + createSQL);
		}

		start += 1;

		end = createSQL.indexOf(CharPool.CLOSE_PARENTHESIS, start + 1);

		if (end < 0) {
			throw new IllegalArgumentException(
				"Unable to find column names end " + createSQL);
		}

		String[] columnNames = StringUtil.split(
			createSQL.substring(start, end), StringPool.COMMA_AND_SPACE);

		return new IndexMetadata(indexName, tableName, unique, columnNames);
	}

	public static String createIndexName(
		String tableName, String... columnNames) {

		StringBundler sb = new StringBundler(4 + (columnNames.length * 2));

		sb.append(tableName);
		sb.append(StringPool.SPACE);
		sb.append(StringPool.OPEN_PARENTHESIS);

		for (String columnName : columnNames) {
			sb.append(columnName);
			sb.append(StringPool.COMMA_AND_SPACE);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_PARENTHESIS);
		sb.append(StringPool.SEMICOLON);

		String specification = sb.toString();

		String specificationHash = StringUtil.toHexString(
			specification.hashCode());

		specificationHash = StringUtil.toUpperCase(specificationHash);

		return _INDEX_NAME_PREFIX.concat(specificationHash);
	}

	private static final String _INDEX_NAME_PREFIX = "IX_";

}