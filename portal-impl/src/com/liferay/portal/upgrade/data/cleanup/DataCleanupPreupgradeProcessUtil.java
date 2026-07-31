/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.db.DBResourceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Field;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Luis Ortiz
 */
public class DataCleanupPreupgradeProcessUtil {

	public static void disableCache() {
		_cacheEnabled = false;

		_primaryKeyColumnNames.clear();
		_tableNames.clear();

		DBInspector.disableCache();
		DBResourceUtil.disableCache();
	}

	public static void enableCache() {
		_cacheEnabled = true;

		DBInspector.enableCache();
		DBResourceUtil.enableCache();
	}

	public static String getPrimaryKeyColumnName(
			Connection connection, DBInspector dbInspector, String tableName)
		throws Exception {

		if (_cacheEnabled) {
			String primaryKeyColumnName = _primaryKeyColumnNames.get(tableName);

			if (primaryKeyColumnName != null) {
				if (primaryKeyColumnName.equals(
						_PRIMARY_KEY_COLUMN_NAME_NOT_FOUND)) {

					return null;
				}

				return primaryKeyColumnName;
			}
		}

		DB db = DBManagerUtil.getDB();

		List<String> primaryKeyColumnNames = new ArrayList<>(
			Arrays.asList(db.getPrimaryKeyColumnNames(connection, tableName)));

		if (primaryKeyColumnNames.size() > 1) {
			primaryKeyColumnNames.remove(
				dbInspector.normalizeName("ctCollectionId"));
		}

		if (primaryKeyColumnNames.size() != 1) {
			if (_cacheEnabled) {
				_primaryKeyColumnNames.putIfAbsent(
					tableName, _PRIMARY_KEY_COLUMN_NAME_NOT_FOUND);
			}

			return null;
		}

		String primaryKeyColumnName = primaryKeyColumnNames.get(0);

		if (_cacheEnabled) {
			_primaryKeyColumnNames.putIfAbsent(tableName, primaryKeyColumnName);
		}

		return primaryKeyColumnName;
	}

	public static String getTableName(
			Connection connection, DBInspector dbInspector,
			String fullyQualifiedName)
		throws Exception {

		String cacheKey = StringBundler.concat(
			Objects.toString(dbInspector.getCatalog(), StringPool.BLANK), ".",
			Objects.toString(dbInspector.getSchema(), StringPool.BLANK), ".",
			fullyQualifiedName);

		if (_cacheEnabled) {
			String tableName = _tableNames.get(cacheKey);

			if (tableName != null) {
				if (tableName.equals(_TABLE_NAME_NOT_FOUND)) {
					return null;
				}

				return tableName;
			}
		}

		String tableName = _getTableName(connection, fullyQualifiedName);

		if (_cacheEnabled) {
			_tableNames.putIfAbsent(
				cacheKey,
				(tableName != null) ? tableName : _TABLE_NAME_NOT_FOUND);
		}

		return tableName;
	}

	private static String _getTableName(
			Connection connection, String fullyQualifiedName)
		throws Exception {

		if (StringUtil.startsWith(
				fullyQualifiedName,
				"com.liferay.object.model.ObjectDefinition#")) {

			try (PreparedStatement preparedStatement =
					connection.prepareStatement(
						"select dbTableName from ObjectDefinition where " +
							"className = ?")) {

				preparedStatement.setString(1, fullyQualifiedName);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						return resultSet.getString("dbTableName");
					}
				}
			}

			return null;
		}

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			try {
				Class<?> clazz = bundle.loadClass(fullyQualifiedName);

				ImplementationClassName implementationClassName =
					clazz.getAnnotation(ImplementationClassName.class);

				if (implementationClassName == null) {
					return null;
				}

				clazz = bundle.loadClass(implementationClassName.value());

				Field field = clazz.getField("TABLE_NAME");

				return (String)field.get(null);
			}
			catch (ClassNotFoundException classNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(classNotFoundException);
				}
			}
		}

		return null;
	}

	private static final String _PRIMARY_KEY_COLUMN_NAME_NOT_FOUND =
		"PRIMARY_KEY_COLUMN_NAME_NOT_FOUND";

	private static final String _TABLE_NAME_NOT_FOUND = "TABLE_NAME_NOT_FOUND";

	private static final Log _log = LogFactoryUtil.getLog(
		DataCleanupPreupgradeProcessUtil.class);

	private static volatile boolean _cacheEnabled;
	private static final Map<String, String> _primaryKeyColumnNames =
		new ConcurrentHashMap<>();
	private static final Map<String, String> _tableNames =
		new ConcurrentHashMap<>();

}