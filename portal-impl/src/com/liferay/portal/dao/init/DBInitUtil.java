/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.init;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.jdbc.util.DynamicDataSource;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.spring.hibernate.DialectDetector;
import com.liferay.portal.upgrade.PortalUpgradeProcess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Date;
import java.util.Objects;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.dialect.Dialect;

import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

/**
 * @author Preston Crary
 */
public class DBInitUtil {

	public static DataSource getDataSource() {
		return _dataSource;
	}

	public static void init() throws Exception {
		_readDataSource = _initDataSource("jdbc.read.");

		_writeDataSource = _initDataSource("jdbc.write.");

		if ((_readDataSource != null) && (_writeDataSource != null)) {
			_dataSource = new DynamicDataSource(
				_readDataSource, _writeDataSource);
		}
		else {
			_dataSource = _initDataSource("jdbc.default.");
		}

		if (_dataSource == null) {
			throw new IllegalStateException("Data source is null");
		}

		DBPartitionUtil.checkDatabasePartitionSchemaNamePrefix();

		_dataSource = DBPartitionUtil.wrapDataSource(_dataSource);

		try (Connection connection = _dataSource.getConnection()) {
			_init(DBManagerUtil.getDB(), connection);
		}

		_dataSource = new LazyConnectionDataSourceProxy(_dataSource);
	}

	private static boolean _checkDefaultRelease(Connection connection) {
		try {
			if (!PortalUpgradeProcess.hasPortalRelease(connection)) {
				PortalUpgradeProcess.createPortalRelease(connection);

				_setDBNew();
			}

			Date currentBuildDate = PortalUpgradeProcess.getCurrentBuildDate(
				connection);

			StartupHelperUtil.setNewRelease(
				(currentBuildDate == null) ? true :
					currentBuildDate.before(ReleaseInfo.getBuildDate()));

			return true;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private static void _checkSQLServer(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select name, is_read_committed_snapshot_on from " +
					"sys.databases where name = db_name()");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			if (!resultSet.next() ||
				resultSet.getBoolean("is_read_committed_snapshot_on") ||
				!_log.isWarnEnabled()) {

				return;
			}

			String name = resultSet.getString("name");

			_log.warn(
				StringBundler.concat(
					"SQL Server may have deadlocks because ",
					"\"read_committed_snapshot\" is disabled for database \"",
					name, "\". To enable, execute: alter database ", name,
					" set read_committed_snapshot on"));
		}
		catch (Exception exception) {
			_log.error("Unable to check SQL Server", exception);
		}
	}

	private static void _createTablesAndPopulate(DB db, Connection connection)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Create tables and populate with default data");
		}

		ClassLoader classLoader = DBInitUtil.class.getClassLoader();

		_runSQLFile(db, connection, classLoader, "portal-tables.sql");
		_runSQLFile(db, connection, classLoader, "portal-data-counter.sql");
		_runSQLFile(db, connection, classLoader, "indexes.sql");
		_runSQLFile(db, connection, classLoader, "sequences.sql");

		PortalUpgradeProcess.createPortalRelease(connection);

		_setDBNew();
	}

	private static void _init(DB db, Connection connection) throws Exception {
		try {
			DBPartitionUtil.setDefaultCompanyId(connection);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (_checkDefaultRelease(connection)) {
			_setSupportsStringCaseSensitiveQuery(db, connection);

			return;
		}

		try {
			db.runSQL(
				connection,
				"alter table Release_ add mvccVersion LONG default 0 not null");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		try {
			db.runSQL(
				connection,
				"alter table Release_ add schemaVersion VARCHAR(75) null");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		try {
			db.runSQL(
				connection,
				"alter table Release_ add versionDisplayName VARCHAR(75) null");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		try {
			db.runSQL(connection, "alter table Release_ add state_ INTEGER");
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if (_checkDefaultRelease(connection)) {
			_setSupportsStringCaseSensitiveQuery(db, connection);

			return;
		}

		// Create tables and populate with default data

		if (GetterUtil.getBoolean(
				PropsUtil.get(PropsKeys.SCHEMA_RUN_ENABLED))) {

			_createTablesAndPopulate(db, connection);

			_setSupportsStringCaseSensitiveQuery(db, connection);

			DBPartitionUtil.setDefaultCompanyId(connection);
		}
	}

	private static DataSource _initDataSource(String prefix) throws Exception {
		Properties properties = PropsUtil.getProperties(prefix, true);

		if ((properties == null) || properties.isEmpty()) {
			return null;
		}

		DataSource dataSource = DataSourceFactoryUtil.initDataSource(
			properties);

		Dialect dialect = DialectDetector.getDialect(dataSource);

		DBType dbType = DBManagerUtil.getDBType(dialect);

		if (dbType == DBType.SQLSERVER) {
			_checkSQLServer(dataSource);
		}

		DBManagerUtil.setDB(dialect, dataSource);

		return dataSource;
	}

	private static void _runSQLFile(
			DB db, Connection connection, ClassLoader classLoader, String path)
		throws Exception {

		db.runSQLTemplate(
			connection,
			StreamUtil.toString(
				classLoader.getResourceAsStream(
					"com/liferay/portal/tools/sql/dependencies/".concat(path))),
			false);
	}

	private static void _setDBNew() {
		StartupHelperUtil.setDBNew(true);

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				StartupHelperUtil.setDBNew(false);

				return null;
			});
	}

	private static void _setSupportsStringCaseSensitiveQuery(
			DB db, Connection connection)
		throws Exception {

		if (!Objects.equals(
				PortalUpgradeProcess.getCurrentTestString(connection),
				ReleaseConstants.TEST_STRING)) {

			throw new SystemException(
				"Release_ table was not initialized properly");
		}

		db.setSupportsStringCaseSensitiveQuery(
			PortalUpgradeProcess.isSupportsStringCaseSensitiveQuery(
				connection));
	}

	private DBInitUtil() {
	}

	private static final Log _log = LogFactoryUtil.getLog(DBInitUtil.class);

	private static DataSource _dataSource;
	private static DataSource _readDataSource;
	private static DataSource _writeDataSource;

}