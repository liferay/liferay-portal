/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.common;

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
		_transformedSQLsPortalCache.removeAll();

		_sqlTransformer = SQLTransformerFactory.getSQLTransformer(
			DBManagerUtil.getDB());
	}

	public static String transform(String sql) {
		return _sqlTransformer.transform(sql);
	}

	public static String transformFromJPQLToHQL(String sql) {
		String newSQL = _transformedSQLsPortalCache.get(sql);

		if (newSQL != null) {
			return newSQL;
		}

		newSQL = _sqlTransformer.transform(sql);

		Function<String, String> countFunction =
			JPQLToHQLTransformerLogic.getCountFunction();

		newSQL = countFunction.apply(newSQL);

		_transformedSQLsPortalCache.put(sql, newSQL);

		return newSQL;
	}

	private static volatile
		com.liferay.portal.dao.sql.transformer.SQLTransformer _sqlTransformer =
			SQLTransformerFactory.getSQLTransformer(DBManagerUtil.getDB());
	private static final PortalCache<String, String>
		_transformedSQLsPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM, SQLTransformer.class.getName());

}