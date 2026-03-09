/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.data.cleanup.DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Avalos
 */
@RunWith(Arquillian.class)
public class DatabaseTableAndColumnCaseDataCleanupPreupgradeProcessTest
	extends DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		DBType dbType = DBManagerUtil.getDBType();

		Assume.assumeTrue(
			(dbType == DBType.MYSQL) || (dbType == DBType.MARIADB) ||
			(dbType == DBType.SQLSERVER));
	}

	@Before
	public void setUp() throws Exception {
		_connection = DataAccess.getConnection();
		_db = DBManagerUtil.getDB();
	}

	@After
	public void tearDown() throws Exception {
		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testUpgradeWithLongServiceComponentTableName()
		throws Exception {

		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		String invalidColumnName = _getName(
			"testCOLUMN", databaseMetaData.getMaxColumnNameLength());
		String invalidTableName = _getName(
			"testTABLE", databaseMetaData.getMaxTableNameLength());
		String testColumnName = _getName(
			"testColumn", databaseMetaData.getMaxColumnNameLength());
		String testTableName = _getName(
			"TestTable", databaseMetaData.getMaxTableNameLength());

		_testUpgradeWithServiceComponentTable(
			invalidColumnName, invalidTableName, testColumnName, testTableName);
	}

	@Test
	public void testUpgradeWithObjectDefinitionDBTable() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		String objectDefinitionDBTableName = objectDefinition.getDBTableName();

		String invalidObjectDefinitionDBTableName = StringUtil.toUpperCase(
			objectDefinitionDBTableName);

		try (Connection connection = DataAccess.getConnection();
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.class.
					getName(),
				LoggerTestUtil.INFO)) {

			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					"DROP_TABLE_IF_EXISTS(" + objectDefinitionDBTableName +
						")"));

			DBType dbType = DBManagerUtil.getDBType();

			if (dbType == DBType.SQLSERVER) {
				DBPartitionUtil.forEachCompanyId(
					companyId -> _db.runSQL(
						StringBundler.concat(
							"create table [",
							invalidObjectDefinitionDBTableName,
							"] ([testColumn] LONG)")));
			}
			else {
				DBPartitionUtil.forEachCompanyId(
					companyId -> _db.runSQL(
						StringBundler.concat(
							"create table `",
							invalidObjectDefinitionDBTableName,
							"` (`testColumn` LONG)")));
			}

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 1, messages.size());

			Assert.assertTrue(
				messages.toString(),
				messages.contains(
					StringBundler.concat(
						"Table ", invalidObjectDefinitionDBTableName,
						" was renamed to ", objectDefinitionDBTableName,
						" because it was incorrectly cased")));
		}
		finally {
			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);

			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					"DROP_TABLE_IF_EXISTS(" +
						invalidObjectDefinitionDBTableName + ")"));
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					"DROP_TABLE_IF_EXISTS(" + objectDefinitionDBTableName +
						")"));
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					StringBundler.concat(
						"DROP_TABLE_IF_EXISTS(", objectDefinitionDBTableName,
						_TEMP_SUFFIX, ")")));
		}
	}

	@Test
	public void testUpgradeWithServiceComponentTable() throws Exception {
		_testUpgradeWithServiceComponentTable(
			"testCOLUMN", "testTABLE", "testColumn", "TestTable");
	}

	@Test
	public void testValidateColumnNamesCasingCompanyTable() throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			this.connection = connection;

			String columnName = "testColumn";
			String columnType = "LONG";
			String invalidColumnName = "testCOLUMN";
			String tableName = "Company";

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.
						class.getName(),
					LoggerTestUtil.INFO);
				SafeCloseable safeCloseable =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"DATABASE_PARTITION_ENABLED", false)) {

				alterTableAddColumn(tableName, invalidColumnName, columnType);

				_testValidateColumnNamesCasing(
					invalidColumnName, columnName, columnType, tableName);

				List<String> messages = logCapture.getMessages();

				Assert.assertEquals(messages.toString(), 1, messages.size());

				String message = messages.get(0);

				Assert.assertEquals(
					message,
					StringBundler.concat(
						"Table ", tableName, ", column ", invalidColumnName,
						" was renamed to ", columnName,
						" because it was incorrectly cased"),
					message);
			}
			finally {
				alterTableDropColumn(tableName, columnName);
				alterTableDropColumn(tableName, invalidColumnName);
			}

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.
						class.getName(),
					LoggerTestUtil.INFO);
				SafeCloseable safeCloseable =
					PropsValuesTestUtil.swapWithSafeCloseable(
						"DATABASE_PARTITION_ENABLED", true)) {

				alterTableAddColumn(tableName, invalidColumnName, columnType);

				_testValidateColumnNamesCasing(
					invalidColumnName, columnName, columnType, tableName);

				List<String> messages = logCapture.getMessages();

				Assert.assertEquals(messages.toString(), 1, messages.size());

				String message = messages.get(0);

				Assert.assertEquals(
					message,
					StringBundler.concat(
						"Column ", tableName, StringPool.PERIOD,
						invalidColumnName,
						" is incorrectly cased, must be manually renamed to ",
						tableName, StringPool.PERIOD, columnName),
					message);
			}
			finally {
				alterTableDropColumn(tableName, columnName);
				alterTableDropColumn(tableName, invalidColumnName);
			}
		}
		finally {
			this.connection = null;
		}
	}

	@Test
	public void testValidateColumnNamesCasingMysqlWithLowerCaseTableNamesTableConfiguration()
		throws Exception {

		Assume.assumeTrue(
			(_db.getDBType() == DBType.MARIADB) ||
			(_db.getDBType() == DBType.MYSQL));

		String columnName = "testColumn";
		String tableName = "TestTable";

		try (Connection connection = DataAccess.getConnection();
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.class.
					getName(),
				LoggerTestUtil.INFO)) {

			_createTestTable(columnName, tableName);

			// When mysql is configured with lower_case_table_names = 1
			// DatabaseMetaData.storesLowerCaseIdentifiers returns true

			DatabaseMetaData proxyDatabaseMetaData =
				ProxyUtil.newDelegateProxyInstance(
					DatabaseMetaData.class.getClassLoader(),
					DatabaseMetaData.class,
					new Object() {

						public boolean storesLowerCaseIdentifiers() {
							return true;
						}

					},
					connection.getMetaData());

			Connection proxyConnection = ProxyUtil.newDelegateProxyInstance(
				Connection.class.getClassLoader(), Connection.class,
				new Object() {

					public DatabaseMetaData getMetaData() {
						return proxyDatabaseMetaData;
					}

				},
				connection);

			this.connection = proxyConnection;

			_testValidateColumnNamesCasing(
				columnName, columnName, "LONG", tableName);

			this.connection = null;

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 0, messages.size());
		}
		finally {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					"DROP_TABLE_IF_EXISTS(" + tableName + ")"));
		}
	}

	private void _createTestTable(String columnName, String tableName)
		throws Exception {

		DBType dbType = DBManagerUtil.getDBType();

		if (dbType == DBType.SQLSERVER) {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					StringBundler.concat(
						"create table [", tableName, "] ([", columnName,
						"] LONG)")));
		}
		else {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					StringBundler.concat(
						"create table `", tableName, "` (`", columnName,
						"` LONG)")));
		}
	}

	private String _getName(String baseName, int targetLength) {
		if (baseName.length() >= targetLength) {
			return baseName.substring(0, targetLength);
		}

		return baseName + "a".repeat(targetLength - baseName.length());
	}

	private void _testUpgradeWithServiceComponentTable(
			String invalidColumnName, String invalidTableName,
			String testColumnName, String testTableName)
		throws Exception {

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				RandomTestUtil.nextLong());

		serviceComponent.setMvccVersion(0);
		serviceComponent.setBuildNamespace("com.liferay.test.service.impl");

		serviceComponent.setData(
			StringBundler.concat(
				"<![CDATA[create table ", testTableName, " (	 \n",
				testColumnName, " LONG"));

		_serviceComponentLocalService.addServiceComponent(serviceComponent);

		try (Connection connection = DataAccess.getConnection();
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.class.
					getName(),
				LoggerTestUtil.INFO)) {

			_createTestTable(invalidColumnName, invalidTableName);

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 2, messages.size());

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", invalidTableName, " was renamed to ",
						testTableName, " because it was incorrectly cased")));

			Assert.assertTrue(
				messages.toString(),
				messages.contains(
					StringBundler.concat(
						"Table ", testTableName, ", column ", invalidColumnName,
						" was renamed to ", testColumnName,
						" because it was incorrectly cased")));

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			DBInspector dbInspector = new DBInspector(_connection);

			try (ResultSet resultSet = databaseMetaData.getColumns(
					dbInspector.getCatalog(), dbInspector.getSchema(),
					testTableName, "%")) {

				while (resultSet.next()) {
					String tableName = resultSet.getString("TABLE_NAME");
					String columnName = resultSet.getString("COLUMN_NAME");

					Assert.assertEquals(tableName, testTableName, tableName);
					Assert.assertEquals(columnName, testColumnName, columnName);
				}
			}
		}
		finally {
			_serviceComponentLocalService.deleteServiceComponent(
				serviceComponent);

			DatabaseMetaData databaseMetaData = _connection.getMetaData();

			int maxTableNameLength = databaseMetaData.getMaxTableNameLength();

			String tempTableName;

			if ((maxTableNameLength > 0) &&
				((testTableName.length() + _TEMP_SUFFIX.length()) >
					maxTableNameLength)) {

				String truncatedTableName = testTableName.substring(
					0, maxTableNameLength - _TEMP_SUFFIX.length());

				tempTableName = truncatedTableName + _TEMP_SUFFIX;
			}
			else {
				tempTableName = testTableName + _TEMP_SUFFIX;
			}

			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					"DROP_TABLE_IF_EXISTS(" + invalidTableName + ")"));
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					"DROP_TABLE_IF_EXISTS(" + tempTableName + ")"));
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					"DROP_TABLE_IF_EXISTS(" + testTableName + ")"));
		}
	}

	private void _testValidateColumnNamesCasing(
			String existingColumnName, String expectedColumnName,
			String expectedColumnType, String tableName)
		throws Exception {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		Map<String, String> existingColumnNames =
			TreeMapBuilder.<String, String>create(
				String.CASE_INSENSITIVE_ORDER
			).put(
				existingColumnName, existingColumnName
			).build();

		List<String> expectedColumnDefinitions = Collections.singletonList(
			expectedColumnName + StringPool.SPACE + expectedColumnType);

		ReflectionTestUtil.invoke(
			this, "_validateColumnNamesCasing",
			new Class<?>[] {
				DBInspector.class, List.class, Map.class, int.class,
				String.class
			},
			new DBInspector(connection), expectedColumnDefinitions,
			existingColumnNames, databaseMetaData.getMaxColumnNameLength(),
			tableName);
	}

	private static final String _TEMP_SUFFIX = "_temp";

	private Connection _connection;
	private DB _db;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}