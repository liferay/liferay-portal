/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db;

import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBResourceUtil {

	public static String getModuleIndexesSQL(Bundle bundle) {
		return _read(bundle, "/META-INF/sql/indexes.sql");
	}

	public static String getModuleSequencesSQL(Bundle bundle) {
		return _read(bundle, "/META-INF/sql/sequences.sql");
	}

	public static String getModuleTablesSQL(Bundle bundle) {
		return _read(bundle, "/META-INF/sql/tables.sql");
	}

	public static String getPortalIndexesSQL() {
		return StringUtil.read(
			DBResourceUtil.class,
			"/com/liferay/portal/tools/sql/dependencies/indexes.sql");
	}

	public static Set<String> getPortalTableNames() throws Exception {
		if (_portalTableNames != null) {
			return _portalTableNames;
		}

		Matcher matcher = _createTablePattern.matcher(getPortalTablesSQL());

		Set<String> tableNames = new HashSet<>();

		while (matcher.find()) {
			String match = matcher.group(1);

			tableNames.add(StringUtil.toLowerCase(match));
		}

		_portalTableNames = tableNames;

		return tableNames;
	}

	public static String getPortalTablesSQL() {
		return StringUtil.read(
			DBResourceUtil.class,
			"/com/liferay/portal/tools/sql/dependencies/portal-tables.sql");
	}

	public static Set<String> getPreupgradedServiceComponentTables(Connection connection)
		throws Exception {

		Set<String> tableNames = new HashSet<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select data_ from ServiceComponent where buildNamespace " +
					"like ?")) {

			preparedStatement.setString(1, "com.liferay%");

			DBInspector dbInspector = new DBInspector(connection);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Matcher matcher = _createTablePattern.matcher(
						resultSet.getString(1));

					while (matcher.find()) {
						tableNames.add(
							dbInspector.normalizeName(matcher.group(1)));
					}
				}
			}
		}

		return tableNames;
	}

	public static Set<String> getModuleTables(Connection connection)
		throws Exception {

		Set<String> tableNames = new HashSet<>();

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		DBInspector dbInspector = new DBInspector(connection);

		for (Bundle bundle : bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (!symbolicName.startsWith("com.liferay") ||
				!symbolicName.contains("service")) {

				continue;
			}

			String tableSQL = getModuleTablesSQL(bundle);

			if (tableSQL == null) {
				continue;
			}

			Matcher matcher = _createTablePattern.matcher(tableSQL);

			while (matcher.find()) {
				tableNames.add(dbInspector.normalizeName(matcher.group(1)));
			}
		}

		return tableNames;
	}

	public static boolean isPortalTableName(String tableName) throws Exception {
		Set<String> portalTableNames = getPortalTableNames();

		return portalTableNames.contains(StringUtil.toLowerCase(tableName));
	}

	private static String _read(Bundle bundle, String path) {
		URL resource = bundle.getResource(path);

		if (resource == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to locate SQL file " + path);
			}

			return null;
		}

		try (InputStream inputStream = resource.openStream()) {
			return StringUtil.read(inputStream);
		}
		catch (IOException ioException) {
			_log.error("Unable to read SQL file " + path, ioException);

			return null;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(DBResourceUtil.class);

	private static final Pattern _createTablePattern = Pattern.compile(
		"create table (\\S*) \\(");
	private static Set<String> _portalTableNames;

}