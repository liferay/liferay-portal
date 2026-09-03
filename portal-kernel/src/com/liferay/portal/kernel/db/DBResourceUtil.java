/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.db;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DBResourceUtil {

	public static void disableCache() {
		_cacheEnabled = false;

		_moduleTableNamesDCLSingleton.destroy(null);
	}

	public static void enableCache() {
		_cacheEnabled = true;
	}

	public static Map<String, String>
			getHistoricalServiceComponentTablesServletContextNames(
				Connection connection)
		throws Exception {

		Map<String, String>
			historicalServiceComponentTablesServletContextNames = new TreeMap<>(
				String.CASE_INSENSITIVE_ORDER);

		Map<String, String> tablesServletContextNames =
			getTablesServletContextNames();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				_SQL_HISTORICAL_SERVICE_COMPONENT);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String buildNamespace = resultSet.getString(1);

				for (String tableName :
						parseCreateTableSQL(resultSet.getString(2))) {

					if (tablesServletContextNames.containsKey(tableName)) {
						continue;
					}

					historicalServiceComponentTablesServletContextNames.put(
						tableName, buildNamespace);
				}
			}
		}

		return historicalServiceComponentTablesServletContextNames;
	}

	public static Set<String> getLiferayTableNames(Connection connection)
		throws Exception {

		Set<String> liferayTableNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		liferayTableNames.addAll(getModuleTableNames());
		liferayTableNames.addAll(getPortalTableNames());
		liferayTableNames.addAll(
			getServiceComponentModuleTableNames(connection));
		liferayTableNames.addAll(
			getServiceComponentPortalTableNames(connection));

		return liferayTableNames;
	}

	public static String getModuleIndexesSQL(Bundle bundle) {
		return _read(bundle, "/META-INF/sql/indexes.sql");
	}

	public static String getModuleSequencesSQL(Bundle bundle) {
		return _read(bundle, "/META-INF/sql/sequences.sql");
	}

	public static Set<String> getModuleTableNames() {
		if (_cacheEnabled) {
			Set<String> moduleTableNames = new TreeSet<>(
				String.CASE_INSENSITIVE_ORDER);

			moduleTableNames.addAll(
				_moduleTableNamesDCLSingleton.getSingleton(
					DBResourceUtil::_getModuleTableNames));

			return moduleTableNames;
		}

		return _getModuleTableNames();
	}

	public static Map<String, String[]> getModuleTablesPrimaryKeyColumnNames(
		Bundle bundle) {

		return _getTablesPrimaryKeyColumnNames(getModuleTablesSQL(bundle));
	}

	public static String getModuleTablesSQL(Bundle bundle) {
		return _read(bundle, "/META-INF/sql/tables.sql");
	}

	public static Map<String, String[]>
			getNonserviceBuilderPrimaryKeyColumnNames(long companyId)
		throws PortalException {

		Map<String, String[]> nonserviceBuildPrimaryKeyColumnNames =
			new HashMap<>();

		ServiceTrackerList<DBResourceProvider> serviceTrackerList =
			_serviceTrackerListDCLSingleton.getSingleton(
				() -> ServiceTrackerListFactory.open(
					SystemBundleUtil.getBundleContext(),
					DBResourceProvider.class));

		for (DBResourceProvider dbResourceProvider : serviceTrackerList) {
			nonserviceBuildPrimaryKeyColumnNames.putAll(
				dbResourceProvider.getTablesPrimaryKeyColumnNames(companyId));
		}

		return nonserviceBuildPrimaryKeyColumnNames;
	}

	public static List<String> getNonserviceBuilderTableNames(long companyId)
		throws PortalException {

		List<String> nonserviceBuildTableNames = new ArrayList<>();

		ServiceTrackerList<DBResourceProvider> serviceTrackerList =
			_serviceTrackerListDCLSingleton.getSingleton(
				() -> ServiceTrackerListFactory.open(
					SystemBundleUtil.getBundleContext(),
					DBResourceProvider.class));

		for (DBResourceProvider dbResourceProvider : serviceTrackerList) {
			nonserviceBuildTableNames.addAll(
				dbResourceProvider.getTableNames(companyId));
		}

		return nonserviceBuildTableNames;
	}

	public static String getPortalIndexesSQL() {
		return StringUtil.read(
			DBResourceUtil.class,
			"/com/liferay/portal/tools/sql/dependencies/indexes.sql");
	}

	public static Set<String> getPortalTableNames() {
		Set<String> portalTableNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

		portalTableNames.addAll(
			_portalTableNamesDCLSingleton.getSingleton(
				() -> parseCreateTableSQL(getPortalTablesSQL())));

		return portalTableNames;
	}

	public static Map<String, String[]> getPortalTablesPrimaryKeyColumnNames() {
		return _getTablesPrimaryKeyColumnNames(getPortalTablesSQL());
	}

	public static String getPortalTablesSQL() {
		return StringUtil.read(
			DBResourceUtil.class,
			"/com/liferay/portal/tools/sql/dependencies/portal-tables.sql");
	}

	public static Map<String, List<String>>
			getServiceComponentModuleColumnDefinitionsMap(Connection connection)
		throws Exception {

		return _getServiceComponentColumnDefinitionsMap(
			connection, "buildNamespace like 'com.liferay%'");
	}

	public static Set<String> getServiceComponentModuleTableNames(
			Connection connection)
		throws Exception {

		return _getServiceComponentTableNames(
			connection, "buildNamespace like 'com.liferay%'");
	}

	public static Map<String, List<String>>
			getServiceComponentPortalColumnDefinitionsMap(Connection connection)
		throws Exception {

		return _getServiceComponentColumnDefinitionsMap(
			connection,
			"buildNamespace = '" +
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME + "'");
	}

	public static Set<String> getServiceComponentPortalTableNames(
			Connection connection)
		throws Exception {

		return _getServiceComponentTableNames(
			connection,
			"buildNamespace = '" +
				ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME + "'");
	}

	public static Map<String, String> getTablesServletContextNames() {
		Map<String, String> tablesServletContextNames = new TreeMap<>(
			String.CASE_INSENSITIVE_ORDER);

		for (String portalTableName : getPortalTableNames()) {
			tablesServletContextNames.put(
				portalTableName, ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME);
		}

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		for (Bundle bundle : bundleContext.getBundles()) {
			String symbolicName = bundle.getSymbolicName();

			if (!symbolicName.startsWith("com.liferay") ||
				(!BundleUtil.isLiferayRequireSchemaVersionBundle(bundle) &&
				 !BundleUtil.isLiferayServiceBundle(bundle))) {

				continue;
			}

			for (String tableName :
					parseCreateTableSQL(getModuleTablesSQL(bundle))) {

				tablesServletContextNames.put(tableName, symbolicName);
			}
		}

		return tablesServletContextNames;
	}

	public static Set<String> parseCreateTableSQL(String createTableSQL) {
		Set<String> tableNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

		if (createTableSQL == null) {
			return tableNames;
		}

		Matcher matcher = _createTablePattern.matcher(createTableSQL);

		while (matcher.find()) {
			tableNames.add(matcher.group(1));
		}

		return tableNames;
	}

	private static Set<String> _getModuleTableNames() {
		Set<String> moduleTableNames = new TreeSet<>(
			String.CASE_INSENSITIVE_ORDER);

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

			moduleTableNames.addAll(parseCreateTableSQL(tableSQL));
		}

		return moduleTableNames;
	}

	private static Map<String, List<String>>
			_getServiceComponentColumnDefinitionsMap(
				Connection connection, String sqlCondition)
		throws Exception {

		Map<String, List<String>> columnDefinitionsMap = new HashMap<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				_SQL_SERVICE_COMPONENT + sqlCondition);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				columnDefinitionsMap.putAll(
					_parseColumnDefinitionsMap(resultSet.getString(1)));
			}
		}

		return columnDefinitionsMap;
	}

	private static Set<String> _getServiceComponentTableNames(
			Connection connection, String sqlCondition)
		throws Exception {

		Set<String> tableNames = new HashSet<>();

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				_SQL_SERVICE_COMPONENT + sqlCondition);

			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				tableNames.addAll(parseCreateTableSQL(resultSet.getString(1)));
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

		if (sql == null) {
			return Collections.emptyMap();
		}

		return HashMapBuilder.putAll(
			_getTablesPrimaryKeyColumnNames(_composedPrimaryKeyPattern, sql)
		).putAll(
			_getTablesPrimaryKeyColumnNames(_inlinedPrimaryKeyPattern, sql)
		).build();
	}

	private static Map<String, List<String>> _parseColumnDefinitionsMap(
		String sql) {

		Map<String, List<String>> columnDefinitionsMap = new TreeMap<>();

		if (sql == null) {
			return columnDefinitionsMap;
		}

		String tableName = null;

		for (String line : StringUtil.splitLines(sql)) {
			line = line.trim();

			if (line.contains("create table ")) {
				String createTableSQL = line.substring(
					line.indexOf("create table "));

				tableName =
					StringUtil.split(createTableSQL, StringPool.SPACE)[2];

				columnDefinitionsMap.put(tableName, new ArrayList<>());

				continue;
			}

			if (line.isEmpty() || line.startsWith(")") ||
				line.startsWith(";") || line.startsWith("<") ||
				line.startsWith("]]>") || line.startsWith("create ") ||
				line.startsWith("primary key")) {

				continue;
			}

			line = StringUtil.replaceLast(
				line, CharPool.COMMA, StringPool.BLANK);
			line = StringUtil.removeSubstring(line, "primary key");

			columnDefinitionsMap.get(
				tableName
			).add(
				line.trim()
			);
		}

		return columnDefinitionsMap;
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

	private static final String _SQL_HISTORICAL_SERVICE_COMPONENT =
		StringBundler.concat(
			"select buildNamespace, data_ from ServiceComponent where ",
			"buildNamespace like 'com.liferay%' or buildNamespace = '",
			ReleaseConstants.DEFAULT_SERVLET_CONTEXT_NAME,
			"' order by serviceComponentId");

	private static final String _SQL_SERVICE_COMPONENT = StringBundler.concat(
		"select data_ from ServiceComponent where buildNumber = (select ",
		"max(buildNumber) from ServiceComponent ServiceComponent2 where ",
		"ServiceComponent.buildNamespace = ServiceComponent2.buildNamespace) ",
		"and ");

	private static final Log _log = LogFactoryUtil.getLog(DBResourceUtil.class);

	private static volatile boolean _cacheEnabled;
	private static final Pattern _composedPrimaryKeyPattern = Pattern.compile(
		"create table\\s+(\\w+)\\s*\\((?:[^;]*?)?primary key\\s*\\(([^)]+)\\)",
		Pattern.DOTALL);
	private static final Pattern _createTablePattern = Pattern.compile(
		"create table (\\S*) \\(");
	private static final Pattern _inlinedPrimaryKeyPattern = Pattern.compile(
		"create table\\s+(\\w+)\\s*\\([^;]*?(\\w+)\\s+\\w+(?:\\([^)]*\\))?" +
			"(?:\\s+\\w+)*\\s+primary key\\b",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final DCLSingleton<Set<String>>
		_moduleTableNamesDCLSingleton = new DCLSingleton<>();
	private static final DCLSingleton<Set<String>>
		_portalTableNamesDCLSingleton = new DCLSingleton<>();
	private static final DCLSingleton<ServiceTrackerList<DBResourceProvider>>
		_serviceTrackerListDCLSingleton = new DCLSingleton<>();

}