/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.db;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

	public static Set<String> getModuleTableNames(Connection connection)
		throws Exception {

		Set<String> tableNames = new HashSet<>();

		DBInspector dbInspector = new DBInspector(connection);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (!symbolicName.startsWith("com.liferay") ||
				!BundleUtil.isLiferayServiceBundle(bundle)) {

				continue;
			}

			String tableSQL = getModuleTablesSQL(bundle);

			if (tableSQL == null) {
				continue;
			}

			tableNames.addAll(parseCreateTableSQL(dbInspector, tableSQL));
		}

		return tableNames;
	}

	public static Map<String, String[]> getModuleTablesPrimaryKeyColumnNames(
		Bundle bundle) {

		return _getTablesPrimaryKeyColumnNames(getModuleTablesSQL(bundle));
	}

	public static String getModuleTablesSQL(Bundle bundle) {
		return _read(bundle, "/META-INF/sql/tables.sql");
	}

	public static String getPortalIndexesSQL() {
		return StringUtil.read(
			DBResourceUtil.class,
			"/com/liferay/portal/tools/sql/dependencies/indexes.sql");
	}

	public static Set<String> getPortalTableNames(Connection connection)
		throws Exception {

		if (_portalTableNames != null) {
			return _portalTableNames;
		}

		DBInspector dbInspector = new DBInspector(connection);

		_portalTableNames = parseCreateTableSQL(
			dbInspector, getPortalTablesSQL());

		return _portalTableNames;
	}

	public static Map<String, String[]> getPortalTablesPrimaryKeyColumnNames() {
		return _getTablesPrimaryKeyColumnNames(getPortalTablesSQL());
	}

	public static String getPortalTablesSQL() {
		return StringUtil.read(
			DBResourceUtil.class,
			"/com/liferay/portal/tools/sql/dependencies/portal-tables.sql");
	}

	public static Set<String> getServiceComponentModuleTableNames(
			Connection connection)
		throws Exception {

		return _getServiceComponentTableNames(
			connection, "buildNamespace like 'com.liferay%'");
	}

	public static Set<String> getServiceComponentPortalTableNames(
			Connection connection)
		throws Exception {

		return _getServiceComponentTableNames(
			connection,
			"buildNamespace = '" +
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME + "'");
	}

	public static Set<String> parseCreateTableSQL(
			DBInspector dbInspector, String createTableSQL)
		throws SQLException {

		Set<String> tableNames = new HashSet<>();

		Matcher matcher = _createTablePattern.matcher(createTableSQL);

		while (matcher.find()) {
			tableNames.add(dbInspector.normalizeName(matcher.group(1)));
		}

		return tableNames;
	}

	private static Set<String> _getServiceComponentTableNames(
			Connection connection, String sqlCondition)
		throws Exception {

		Set<String> tableNames = new HashSet<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select data_ from ServiceComponent where buildNumber = ",
					"(select max(buildNumber) from ServiceComponent ",
					"TEMP_TABLE where ServiceComponent.buildNamespace = ",
					"TEMP_TABLE.buildNamespace) and ", sqlCondition))) {

			DBInspector dbInspector = new DBInspector(connection);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					tableNames.addAll(
						parseCreateTableSQL(
							dbInspector, resultSet.getString(1)));
				}
			}
		}

		return tableNames;
	}

	private static Map<String, String[]> _getTablesPrimaryKeyColumnNames(
		Pattern pattern, String sql) {

		Map<String, String[]> tablesPrimaryKeyColumnNames = new HashMap<>();

		Matcher matcher = pattern.matcher(sql);

		while (matcher.find()) {
			tablesPrimaryKeyColumnNames.put(
				matcher.group(1),
				StringUtil.split(
					StringUtil.removeChar(matcher.group(2), CharPool.SPACE)));
		}

		return tablesPrimaryKeyColumnNames;
	}

	private static Map<String, String[]> _getTablesPrimaryKeyColumnNames(
		String sql) {

		return HashMapBuilder.putAll(
			_getTablesPrimaryKeyColumnNames(_composedPrimaryKeyPattern, sql)
		).putAll(
			_getTablesPrimaryKeyColumnNames(_inlinedPrimaryKeyPattern, sql)
		).build();
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

	private static final Pattern _composedPrimaryKeyPattern = Pattern.compile(
		"create table\\s+(\\w+)\\s*\\((?:[^;]*?)?primary key\\s*\\(([^)]+)\\)",
		Pattern.DOTALL);
	private static final Pattern _createTablePattern = Pattern.compile(
		"create table (\\S*) \\(");
	private static final Pattern _inlinedPrimaryKeyPattern = Pattern.compile(
		"create table\\s+(\\w+)\\s*\\([^;]*?(\\w+)\\s+\\w+(?:\\([^)]*\\))?" +
			"(?:\\s+\\w+)*\\s+primary key\\b",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static volatile Set<String> _portalTableNames;

}