/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.common;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.sql.transformer.HQLToJPQLTransformerLogic;
import com.liferay.portal.dao.sql.transformer.JPQLToHQLTransformerLogic;
import com.liferay.portal.dao.sql.transformer.SQLTransformerFactory;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;

import java.util.function.Function;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Manuel de la Peña
 */
public class SQLTransformer {

	public static void reloadSQLTransformer() {
		_instance._reloadSQLTransformer();
	}

	public static String transform(String sql) {
		com.liferay.portal.dao.sql.transformer.SQLTransformer sqlTransformer =
			_instance._getSQLTransformer();

		return sqlTransformer.transform(sql);
	}

	public static String transformFromHQLToJQPL(String sql) {
		return _instance._transformFromHQLToJPQL(sql);
	}

	public static String transformFromJPQLToHQL(String sql) {
		return _instance._transformFromJPQLToHQL(sql);
	}

	private SQLTransformer() {
		_reloadSQLTransformer();
	}

	private com.liferay.portal.dao.sql.transformer.SQLTransformer
		_getSQLTransformer() {

		return _sqlTransformer;
	}

	private void _reloadSQLTransformer() {
		if (_transformedSQLsPortalCache == null) {
			_transformedSQLsPortalCache = PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				SQLTransformer.class.getName());
		}
		else {
			_transformedSQLsPortalCache.removeAll();
		}

		_sqlTransformer = SQLTransformerFactory.getSQLTransformer(
			DBManagerUtil.getDB());
	}

	private String _transformFromHQLToJPQL(String sql) {
		String newSQL = _transformedSQLsPortalCache.get(sql);

		if (newSQL != null) {
			return newSQL;
		}

		newSQL = _sqlTransformer.transform(sql);

		Function[] functions = {
			HQLToJPQLTransformerLogic.getPositionalParameterFunction(),
			HQLToJPQLTransformerLogic.getNotEqualsFunction(),
			HQLToJPQLTransformerLogic.getCompositeIdMarkerFunction()
		};

		for (Function<String, String> function : functions) {
			newSQL = function.apply(newSQL);
		}

		_transformedSQLsPortalCache.put(sql, newSQL);

		return newSQL;
	}

	private String _transformFromJPQLToHQL(String sql) {
		String newSQL = _transformedSQLsPortalCache.get(sql);

		if (newSQL != null) {
			return newSQL;
		}

		newSQL = _sqlTransformer.transform(sql);

		Function<String, String> countFunction =
			JPQLToHQLTransformerLogic.getCountFunction();

		newSQL = countFunction.apply(newSQL);

		if (newSQL.contains("?")) {
			StringBundler sb = new StringBundler();

			int counter = 1;

			for (int i = 0; i < newSQL.length(); i++) {
				char c = newSQL.charAt(i);

				if ((c == '?') &&
					((i == 0) || (newSQL.charAt(i - 1) == ' '))) {

					sb.append('?');
					sb.append(counter++);
				}
				else {
					sb.append(c);
				}
			}

			newSQL = sb.toString();
		}

		_transformedSQLsPortalCache.put(sql, newSQL);

		return newSQL;
	}

	private static final SQLTransformer _instance = new SQLTransformer();

	private com.liferay.portal.dao.sql.transformer.SQLTransformer
		_sqlTransformer;
	private PortalCache<String, String> _transformedSQLsPortalCache;

}