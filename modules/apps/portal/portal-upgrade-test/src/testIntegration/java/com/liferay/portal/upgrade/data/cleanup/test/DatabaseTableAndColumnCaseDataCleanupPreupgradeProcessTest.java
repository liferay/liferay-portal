/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.db.partition.util.DBPartitionUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceComponentLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PropsValues;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
	public void testUpgradeModuleTableColumnCasedInServiceComponent()
		throws Exception {

		long buildNumber = 0;

		for (ServiceComponent serviceComponent :
				_serviceComponentLocalService.getLatestServiceComponents()) {

			if (!Objects.equals(
					serviceComponent.getBuildNamespace(),
					"com.liferay.wiki.service")) {

				continue;
			}

			buildNumber = serviceComponent.getBuildNumber();

			break;
		}

		_testUpgradeWithTable(
			"com.liferay.wiki.service", buildNumber + 1, "nodeId",
			"CREATEDATE DATE null,\n\t", "WikiNode");
	}

	@Test
	public void testUpgradePortalTableColumnCasedInServiceComponent()
		throws Exception {

		_testUpgradeWithTable(
			"com.liferay.test.service.impl", 0, "passwordTrackerId",
			"CREATEDATE DATE null,\n\t", "PasswordTracker");
	}

	@Test
	public void testUpgradePortalTableColumnMissingFromServiceComponent()
		throws Exception {

		_testUpgradeWithTable(
			"com.liferay.test.service.impl", 0, "passwordTrackerId",
			StringPool.BLANK, "PasswordTracker");
	}

	@Test
	public void testUpgradePortalTableNameCasedInServiceComponent()
		throws Exception {

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				RandomTestUtil.nextLong());

		serviceComponent.setMvccVersion(0);
		serviceComponent.setBuildNamespace("com.liferay.test.service.impl");
		serviceComponent.setData(
			"<![CDATA[create table passwordtracker (\n\ttestColumn LONG,\n\t" +
				"passwordTrackerId LONG not null primary key\n);]]>");

		_serviceComponentLocalService.addServiceComponent(serviceComponent);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.class.
					getName(),
				LoggerTestUtil.INFO)) {

			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					"alter table PasswordTracker add TESTCOLUMN LONG"));

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					"Table PasswordTracker, column TESTCOLUMN was renamed to " +
						"testColumn because it was incorrectly cased"));

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					try (Connection connection = DataAccess.getConnection()) {
						Assert.assertEquals(
							"testColumn",
							_getColumnName(
								"testColumn", connection, "PasswordTracker"));
					}
				});
		}
		finally {
			_serviceComponentLocalService.deleteServiceComponent(
				serviceComponent);

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					try (Connection connection = DataAccess.getConnection()) {
						for (String columnName :
								new String[] {
									"testColumn", "testColumn" + _TEMP_SUFFIX
								}) {

							String actualColumnName = _getColumnName(
								columnName, connection, "PasswordTracker");

							if (actualColumnName == null) {
								continue;
							}

							_db.runSQL(
								connection,
								"alter table PasswordTracker drop column " +
									actualColumnName);
						}
					}
				});
		}
	}

	@Test
	public void testUpgradeServiceComponentTableColumnType() throws Exception {
		_testUpgradeWithServiceComponentTable(
			"VARCHAR(75)", "testCOLUMN", "testTABLE", "testColumn",
			"TestTable");
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
			"LONG", invalidColumnName, invalidTableName, testColumnName,
			testTableName);
	}

	@Test
	public void testUpgradeWithObjectDefinitionDBTable() throws Exception {
		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		String objectDefinitionDBTableName = objectDefinition.getDBTableName();

		String invalidObjectDefinitionDBTableName = StringUtil.toLowerCase(
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
						"Table ",
						_prependPartitionName(
							invalidObjectDefinitionDBTableName),
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
			"LONG", "testCOLUMN", "testTABLE", "testColumn", "TestTable");
	}

	@Test
	public void testValidateColumnNamesCasingCompanyTableWithDatabasePartitionDisabled()
		throws Exception {

		Assume.assumeFalse(PropsValues.DATABASE_PARTITION_ENABLED);

		try (Connection connection = DataAccess.getConnection()) {
			this.connection = connection;

			String columnName = "testColumn";
			String invalidColumnName = "testCOLUMN";
			String tableName = "Company";

			_testValidateColumnNameCasing(
				columnName,
				StringBundler.concat(
					"Table ", tableName, ", column ", invalidColumnName,
					" was renamed to ", columnName, " because it was ",
					"incorrectly cased"),
				invalidColumnName, tableName);
		}
		finally {
			this.connection = null;
		}
	}

	@Test
	public void testValidateColumnNamesCasingCompanyTableWithDatabasePartitionEnabled()
		throws Exception {

		Assume.assumeTrue(PropsValues.DATABASE_PARTITION_ENABLED);

		try (Connection connection = DataAccess.getConnection()) {
			this.connection = connection;

			String columnName = "testColumn";
			String invalidColumnName = "testCOLUMN";
			String tableName = "Company";

			_testValidateColumnNameCasing(
				columnName,
				StringBundler.concat(
					"Table ", tableName, ", column ", invalidColumnName,
					" must be manually renamed to ", columnName, " because it ",
					"is incorrectly cased"),
				invalidColumnName, tableName);
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

			_createTestTable(columnName, "LONG", tableName);

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

			_testValidateColumnNamesCasing(columnName, columnName, tableName);

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

	private void _createTestTable(
			String columnName, String columnType, String tableName)
		throws Exception {

		DBType dbType = DBManagerUtil.getDBType();

		if (dbType == DBType.SQLSERVER) {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					StringBundler.concat(
						"create table [", tableName, "] ([", columnName, "] ",
						columnType, ")")));
		}
		else {
			DBPartitionUtil.forEachCompanyId(
				companyId -> _db.runSQL(
					StringBundler.concat(
						"create table `", tableName, "` (`", columnName, "` ",
						columnType, ")")));
		}
	}

	private String _getColumnName(
			String columnName, Connection connection, String tableName)
		throws Exception {

		DatabaseMetaData databaseMetaData = connection.getMetaData();
		DBInspector dbInspector = new DBInspector(connection);

		try (ResultSet resultSet = databaseMetaData.getColumns(
				dbInspector.getCatalog(), dbInspector.getSchema(), tableName,
				"%")) {

			while (resultSet.next()) {
				String actualColumnName = resultSet.getString("COLUMN_NAME");

				if (StringUtil.equalsIgnoreCase(actualColumnName, columnName)) {
					return actualColumnName;
				}
			}
		}

		return null;
	}

	private String _getName(String baseName, int targetLength) {
		if (baseName.length() >= targetLength) {
			return baseName.substring(0, targetLength);
		}

		return baseName + "a".repeat(targetLength - baseName.length());
	}

	private String _prependPartitionName(String tableName) {
		if (!PropsValues.DATABASE_PARTITION_ENABLED) {
			return tableName;
		}

		long companyId = CompanyThreadLocal.getNonsystemCompanyId();

		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			return tableName;
		}

		return StringBundler.concat(
			PropsValues.DATABASE_PARTITION_SCHEMA_NAME_PREFIX, companyId,
			StringPool.PERIOD, tableName);
	}

	private void _renameColumn(
			String columnName, Connection connection, String newColumnName,
			String tableName)
		throws Exception {

		_db.alterColumnName(
			connection, tableName, columnName, columnName + _TEMP_SUFFIX);
		_db.alterColumnName(
			connection, tableName, columnName + _TEMP_SUFFIX, newColumnName);
	}

	private void _testUpgradeWithServiceComponentTable(
			String columnType, String invalidColumnName,
			String invalidTableName, String testColumnName,
			String testTableName)
		throws Exception {

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				RandomTestUtil.nextLong());

		serviceComponent.setMvccVersion(0);
		serviceComponent.setBuildNamespace("com.liferay.test.service.impl");

		serviceComponent.setData(
			StringBundler.concat(
				"<![CDATA[create table ", testTableName, " (\n\t",
				testColumnName, " LONG\n);]]>"));

		_serviceComponentLocalService.addServiceComponent(serviceComponent);

		try (Connection connection = DataAccess.getConnection();
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.class.
					getName(),
				LoggerTestUtil.INFO)) {

			_createTestTable(invalidColumnName, columnType, invalidTableName);

			upgrade();

			List<String> messages = logCapture.getMessages();

			long partitionCount = 1;

			if (PropsValues.DATABASE_PARTITION_ENABLED) {
				partitionCount = PortalInstancePool.getCompanyIds().length;
			}

			Assert.assertEquals(
				messages.toString(), 2 * partitionCount, messages.size());

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

			Assert.assertTrue(
				dbInspector.hasColumnType(
					testTableName, testColumnName, columnType));
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

	private void _testUpgradeWithTable(
			String buildNamespace, long buildNumber,
			String primaryKeyColumnName,
			String serviceComponentColumnDefinition, String tableName)
		throws Exception {

		ServiceComponent serviceComponent =
			_serviceComponentLocalService.createServiceComponent(
				RandomTestUtil.nextLong());

		serviceComponent.setMvccVersion(0);
		serviceComponent.setBuildNamespace(buildNamespace);
		serviceComponent.setBuildNumber(buildNumber);
		serviceComponent.setData(
			StringBundler.concat(
				"<![CDATA[create table ", tableName, " (\n\t",
				serviceComponentColumnDefinition, primaryKeyColumnName,
				" LONG not null primary key\n);]]>"));

		_serviceComponentLocalService.addServiceComponent(serviceComponent);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.class.
					getName(),
				LoggerTestUtil.INFO)) {

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					try (Connection connection = DataAccess.getConnection()) {
						_renameColumn(
							"createDate", connection, "CREATEDATE", tableName);
					}
				});

			upgrade();

			List<String> messages = logCapture.getMessages();

			Assert.assertTrue(
				messages.contains(
					StringBundler.concat(
						"Table ", tableName, ", column CREATEDATE was renamed ",
						"to createDate because it was incorrectly cased")));

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					try (Connection connection = DataAccess.getConnection()) {
						Assert.assertEquals(
							"createDate",
							_getColumnName(
								"createDate", connection, tableName));
					}
				});
		}
		finally {
			_serviceComponentLocalService.deleteServiceComponent(
				serviceComponent);

			DBPartitionUtil.forEachCompanyId(
				companyId -> {
					try (Connection connection = DataAccess.getConnection()) {
						String columnName = _getColumnName(
							"createDate", connection, tableName);

						if (columnName == null) {
							String tempColumnName = _getColumnName(
								"createDate" + _TEMP_SUFFIX, connection,
								tableName);

							if (tempColumnName != null) {
								_db.alterColumnName(
									connection, tableName, tempColumnName,
									"createDate");
							}
						}
						else if (!columnName.equals("createDate")) {
							_renameColumn(
								columnName, connection, "createDate",
								tableName);
						}
					}
				});
		}
	}

	private void _testValidateColumnNameCasing(
			String columnName, String expectedLogMessage,
			String invalidColumnName, String tableName)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				DatabaseTableAndColumnCaseDataCleanupPreupgradeProcess.class.
					getName(),
				LoggerTestUtil.INFO)) {

			String columnType = "LONG";

			alterTableAddColumn(tableName, invalidColumnName, columnType);

			_testValidateColumnNamesCasing(
				invalidColumnName, columnName, tableName);

			List<String> messages = logCapture.getMessages();

			Assert.assertEquals(messages.toString(), 1, messages.size());

			String message = messages.get(0);

			Assert.assertEquals(message, expectedLogMessage, message);
		}
		finally {
			alterTableDropColumn(tableName, columnName);
			alterTableDropColumn(tableName, invalidColumnName);
		}
	}

	private void _testValidateColumnNamesCasing(
			String existingColumnName, String expectedColumnName,
			String tableName)
		throws Exception {

		DatabaseMetaData databaseMetaData = connection.getMetaData();

		ReflectionTestUtil.invoke(
			this, "_validateColumnNamesCasing",
			new Class<?>[] {
				Map.class, DBInspector.class, Collection.class, int.class,
				String.class
			},
			TreeMapBuilder.<String, String>create(
				String.CASE_INSENSITIVE_ORDER
			).put(
				existingColumnName, existingColumnName
			).build(),
			new DBInspector(connection),
			Collections.singletonList(expectedColumnName),
			databaseMetaData.getMaxColumnNameLength(), tableName);
	}

	private static final String _TEMP_SUFFIX = "_temp";

	private Connection _connection;
	private DB _db;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ServiceComponentLocalService _serviceComponentLocalService;

}