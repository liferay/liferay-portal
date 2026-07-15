/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.common;

import com.liferay.portal.dao.sql.transformer.HibernateSQLTransformerLogic;
import com.liferay.portal.dao.sql.transformer.SQLTransformerLogic;
import com.liferay.portal.dao.sql.transformer.SQLTransformerLogicFactory;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.function.Function;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 * @author Manuel de la Peña
 */
public class SQLTransformer {

	public static void reloadSQLTransformer() {
		_hibernateTransformedSQLsPortalCache.removeAll();

		_transformedSQLsPortalCache.removeAll();

		_sqlTransformerLogic =
			SQLTransformerLogicFactory.getSQLTransformerLogic(
				DBManagerUtil.getDB());
	}

	public static String transform(String sql) {
		return _transform(
			_transformedSQLsPortalCache, _sqlTransformerLogic, sql);
	}

	public static String transformForHibernate(String sql) {
		return _transform(
			_hibernateTransformedSQLsPortalCache, _hibernateSQLTransformerLogic,
			sql);
	}

	private static String _transform(
		PortalCache<String, String> portalCache,
		SQLTransformerLogic sqlTransformerLogic, String sql) {

		String newSQL = portalCache.get(sql);

		if (newSQL != null) {
			return newSQL;
		}

		Function<String, String>[] functions =
			sqlTransformerLogic.getFunctions();

		if ((functions == null) || (sql == null)) {
			return sql;
		}

		String transformedSQL = sql;

		for (Function<String, String> function : functions) {
			transformedSQL = function.apply(transformedSQL);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Original SQL: " + sql);
			_log.debug("Transformed SQL: " + transformedSQL);
		}

		portalCache.put(sql, transformedSQL);

		return transformedSQL;
	}

	private static final Log _log = LogFactoryUtil.getLog(SQLTransformer.class);

	private static final SQLTransformerLogic _hibernateSQLTransformerLogic =
		new HibernateSQLTransformerLogic();
	private static final PortalCache<String, String>
		_hibernateTransformedSQLsPortalCache =
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				SQLTransformer.class.getName() + ".hibernate");
	private static volatile SQLTransformerLogic _sqlTransformerLogic =
		SQLTransformerLogicFactory.getSQLTransformerLogic(
			DBManagerUtil.getDB());
	private static final PortalCache<String, String>
		_transformedSQLsPortalCache = PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM, SQLTransformer.class.getName());

}