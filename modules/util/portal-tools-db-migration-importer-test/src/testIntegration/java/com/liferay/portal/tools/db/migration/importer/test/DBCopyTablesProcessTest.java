/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.jdbc.postgresql.PostgreSQLJDBCUtil;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBFactory;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.db.migration.importer.DBCopyTablesProcess;
import com.liferay.portal.tools.db.migration.importer.jdbc.AutoBatchPreparedStatementUtil;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class DBCopyTablesProcessTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		Assume.assumeTrue(
			System.getProperty("database.postgresql.driver") != null);

		AutoBatchPreparedStatementUtil.start();

		_targetDataSource = DataSourceFactoryUtil.initDataSource(
			System.getProperty("database.postgresql.driver"),
			System.getProperty("database.postgresql.url"),
			System.getProperty("database.postgresql.username"),
			System.getProperty("database.postgresql.password"),
			StringPool.BLANK);

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			_sourceTableName = dbInspector.normalizeName("TestTable");
		}

		try (Connection connection = _targetDataSource.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			_targetTableName = dbInspector.normalizeName("TestTable");
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		if (System.getProperty("database.postgresql.driver") == null) {
			return;
		}

		AutoBatchPreparedStatementUtil.stop();

		DataSourceFactoryUtil.destroyDataSource(_targetDataSource);
	}

	@After
	public void tearDown() throws Exception {
		_sourceDB.runSQL("DROP_TABLE_IF_EXISTS(TestTable)");

		try (Connection connection = _targetDataSource.getConnection()) {
			_targetDB.runSQL(connection, "DROP TABLE IF EXISTS TestTable");
		}
	}

	@Test
	public void testBigDecimalColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), GetterUtil::getDouble, "BIGDECIMAL",
			() -> RandomTestUtil.nextDouble() + 0.5);
	}

	@Test
	public void testBlobColumn() throws Exception {
		_createTable("BLOB");

		Object[] values = new Object[_TABLE_SIZE];

		for (int i = 0; i < _TABLE_SIZE; i++) {
			values[i] = RandomTestUtil.randomBytes();
		}

		_insertValues(values);

		_copyTable();

		int total = 0;

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select testColumn from " + _targetTableName +
					" order by id ASC")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Assert.assertArrayEquals(
						(byte[])values[total++],
						PostgreSQLJDBCUtil.getLargeObject(
							resultSet, "testColumn"));
				}
			}
		}

		Assert.assertEquals(values.length, total);
	}

	@Test
	public void testBooleanColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), Function.identity(), "BOOLEAN",
			RandomTestUtil::randomBoolean);
	}

	@Test
	@TestInfo("LPD-110960")
	public void testCopyTableWithTargetCheckConstraint() throws Exception {
		_createTable(
			"create table TestTable (id INTEGER, testColumn VARCHAR(75))",
			StringBundler.concat(
				"create table TestTable (id INTEGER, testColumn VARCHAR(75), ",
				"check (id <> ", RandomTestUtil.randomInt(1, _TABLE_SIZE),
				"))"));

		_insertValues(RandomTestUtil.randomStrings(_TABLE_SIZE));

		try {
			_copyTable();

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof BatchUpdateException);
		}
	}

	@Test
	@TestInfo("LPD-110096")
	public void testCopyTableWithTargetOnlyColumn() throws Exception {
		_createTable(
			"create table TestTable (id INTEGER, testColumn VARCHAR(75))",
			"create table TestTable (id INTEGER, aColumn VARCHAR(75), " +
				"testColumn VARCHAR(75))");

		String[] values = RandomTestUtil.randomStrings(_TABLE_SIZE);

		_insertValues(values);

		_copyTable();

		_assertValues(
			"aColumn", Function.identity(), new Object[_TABLE_SIZE],
			Function.identity());
		_assertValues(
			"testColumn", Function.identity(), values, Function.identity());
	}

	@Test
	public void testDateColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), Function.identity(), "DATE",
			RandomTestUtil::nextTimestamp);
	}

	@Test
	public void testDoubleColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), GetterUtil::getDouble, "DOUBLE",
			() -> RandomTestUtil.nextDouble() + 0.5);
	}

	@Test
	public void testIntegerColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), Function.identity(), "INTEGER",
			RandomTestUtil::nextInt);
	}

	@Test
	public void testLongColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), Function.identity(), "LONG",
			RandomTestUtil::nextLong);
	}

	@Test
	public void testNullChar() throws Exception {
		_testColumnTypeOf(
			object -> StringUtil.removeSubstring(
				(String)object, StringPool.NULL_CHAR),
			Function.identity(), "VARCHAR(20)", () -> "Test\\u0000Test");
	}

	@Test
	@TestInfo({"LPD-110097", "LPD-110098"})
	public void testRunWithCustomColumns() throws Exception {
		DBType dbType = _sourceDB.getDBType();

		if (dbType == DBType.DB2) {
			_testRunWithCustomColumns(
				"bytea", value -> Arrays.toString((byte[])value),
				value -> Arrays.toString((byte[])value), "varbinary(16)",
				RandomTestUtil::randomBytes);
			_testRunWithCustomColumns(
				"double precision",
				value -> GetterUtil.getDouble(String.valueOf(value)),
				Function.identity(), "real",
				() -> RandomTestUtil.randomInt(0, 99999) / 100F);
			_testRunWithCustomColumns(
				"text", Function.identity(), Function.identity(), "dbclob",
				() -> StringBundler.concat(
					RandomTestUtil.randomString(), StringPool.NEW_LINE,
					RandomTestUtil.randomString()));
			_testRunWithCustomColumns(
				"time without time zone", Function.identity(),
				Function.identity(), "time",
				() -> Time.valueOf(
					LocalTime.ofSecondOfDay(
						RandomTestUtil.randomInt(0, 86399))));
			_testRunWithCustomColumns(
				"timestamp without time zone", Function.identity(),
				Function.identity(), "date",
				() -> Date.valueOf(
					LocalDate.ofEpochDay(RandomTestUtil.randomInt(0, 36524))));
		}
		else if ((dbType == DBType.MARIADB) || (dbType == DBType.MYSQL)) {
			_testRunWithCustomColumns(
				"bytea", value -> Arrays.toString((byte[])value),
				value -> Arrays.toString((byte[])value), "varbinary(16)",
				RandomTestUtil::randomBytes);
			_testRunWithCustomColumns(
				"double precision",
				value -> GetterUtil.getDouble(String.valueOf(value)),
				Function.identity(), "float",
				() -> RandomTestUtil.randomInt(0, 99999) / 100F);
			_testRunWithCustomColumns(
				"time without time zone", Function.identity(),
				Function.identity(), "time",
				() -> Time.valueOf(
					LocalTime.ofSecondOfDay(
						RandomTestUtil.randomInt(0, 86399))));
			_testRunWithCustomColumns(
				"timestamp without time zone", Function.identity(),
				Function.identity(), "date",
				() -> Date.valueOf(
					LocalDate.ofEpochDay(RandomTestUtil.randomInt(0, 36524))));
		}
		else if (dbType == DBType.ORACLE) {
			_testRunWithCustomColumns(
				"bytea", value -> Arrays.toString((byte[])value),
				value -> Arrays.toString((byte[])value), "raw(16)",
				RandomTestUtil::randomBytes);
			_testRunWithCustomColumns(
				"character varying(10)", Function.identity(),
				Function.identity(), "nchar(10)",
				() -> RandomTestUtil.randomString(10));
			_testRunWithCustomColumns(
				"double precision",
				value -> GetterUtil.getDouble(String.valueOf(value)),
				Function.identity(), "binary_float",
				() -> RandomTestUtil.randomInt(0, 99999) / 100F);
			_testRunWithCustomColumns(
				"text", Function.identity(), Function.identity(), "nclob",
				() -> StringBundler.concat(
					RandomTestUtil.randomString(), StringPool.RETURN_NEW_LINE,
					RandomTestUtil.randomString()));
			_testRunWithCustomColumns(
				"timestamp with time zone",
				value -> Timestamp.from(Instant.from((OffsetDateTime)value)),
				Function.identity(), "timestamp with local time zone",
				() -> OffsetDateTime.ofInstant(
					Instant.ofEpochSecond(RandomTestUtil.randomInt()),
					ZoneOffset.ofHours(RandomTestUtil.randomInt(-12, 14))));
			_testRunWithCustomColumns(
				"timestamp with time zone",
				value -> Timestamp.from(Instant.from((OffsetDateTime)value)),
				Function.identity(), "timestamp with time zone",
				() -> OffsetDateTime.ofInstant(
					Instant.ofEpochSecond(RandomTestUtil.randomInt()),
					ZoneOffset.ofHours(RandomTestUtil.randomInt(-12, 14))));
			_testRunWithCustomColumns(
				"timestamp without time zone", Function.identity(),
				Function.identity(), "date",
				() -> Date.valueOf(
					LocalDate.ofEpochDay(RandomTestUtil.randomInt(0, 36524))));
		}
		else if (dbType == DBType.SQLSERVER) {
			_testRunWithCustomColumns(
				"bytea", value -> Arrays.toString((byte[])value),
				value -> Arrays.toString((byte[])value), "varbinary(16)",
				RandomTestUtil::randomBytes);
			_testRunWithCustomColumns(
				"character varying(10)", Function.identity(),
				Function.identity(), "nchar(10)",
				() -> RandomTestUtil.randomString(10));
			_testRunWithCustomColumns(
				"double precision",
				value -> GetterUtil.getDouble(String.valueOf(value)),
				Function.identity(), "real",
				() -> RandomTestUtil.randomInt(0, 99999) / 100F);
			_testRunWithCustomColumns(
				"text", Function.identity(), Function.identity(), "ntext",
				() -> StringBundler.concat(
					RandomTestUtil.randomString(), StringPool.RETURN_NEW_LINE,
					RandomTestUtil.randomString()));
			_testRunWithCustomColumns(
				"time without time zone", Function.identity(),
				Function.identity(), "time",
				() -> Time.valueOf(
					LocalTime.ofSecondOfDay(
						RandomTestUtil.randomInt(0, 86399))));
			_testRunWithCustomColumns(
				"timestamp with time zone",
				value -> Timestamp.from(Instant.from((OffsetDateTime)value)),
				Function.identity(), "datetimeoffset",
				() -> OffsetDateTime.ofInstant(
					Instant.ofEpochSecond(RandomTestUtil.randomInt()),
					ZoneOffset.ofHours(RandomTestUtil.randomInt(-12, 14))));
			_testRunWithCustomColumns(
				"timestamp without time zone", Function.identity(),
				Function.identity(), "date",
				() -> Date.valueOf(
					LocalDate.ofEpochDay(RandomTestUtil.randomInt(0, 36524))));
		}

		_testRunWithCustomColumns(
			"character varying(10)",
			value -> {
				if ((dbType == DBType.MARIADB) || (dbType == DBType.MYSQL)) {
					return value;
				}

				return value + "     ";
			},
			Function.identity(), "char(10)",
			() -> RandomTestUtil.randomString(5));

		_testRunWithCustomColumns();
	}

	@Test
	@TestInfo("LPD-110097")
	public void testRunWithCustomColumnsInPartition() throws Exception {
		String partitionName = "test" + RandomTestUtil.randomInt();

		try (Connection connection = _targetDataSource.getConnection()) {
			_targetDB.runSQL(connection, "create schema " + partitionName);
		}

		DataSource targetDataSource = _targetDataSource;

		_targetDataSource = DataSourceFactoryUtil.initDataSource(
			System.getProperty("database.postgresql.driver"),
			System.getProperty("database.postgresql.url") + "?currentSchema=" +
				partitionName,
			System.getProperty("database.postgresql.username"),
			System.getProperty("database.postgresql.password"),
			StringPool.BLANK);

		try {
			_testRunWithCustomColumns();
		}
		finally {
			DataSourceFactoryUtil.destroyDataSource(_targetDataSource);

			_targetDataSource = targetDataSource;

			try (Connection connection = _targetDataSource.getConnection()) {
				_targetDB.runSQL(
					connection, "drop schema " + partitionName + " cascade");
			}
		}
	}

	@Test
	public void testSblobColumn() throws Exception {
		_createTable("SBLOB");

		Object[] values = new Object[_TABLE_SIZE];

		for (int i = 0; i < _TABLE_SIZE; i++) {
			values[i] = RandomTestUtil.randomBytes();
		}

		_insertValues(values);

		_copyTable();

		int total = 0;

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select testColumn from " + _targetTableName +
					" order by id ASC")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Assert.assertArrayEquals(
						(byte[])values[total++],
						resultSet.getBytes("testColumn"));
				}
			}
		}

		Assert.assertEquals(values.length, total);
	}

	@Test
	public void testStringColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), Function.identity(), "STRING",
			RandomTestUtil::randomString);
	}

	@Test
	@TestInfo("LPD-110961")
	public void testTextColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), Function.identity(), "TEXT",
			() -> StringBundler.concat(
				StringPool.NEW_LINE, StringPool.NEW_LINE,
				RandomTestUtil.randomString(), StringPool.RETURN_NEW_LINE,
				RandomTestUtil.randomString(), StringPool.RETURN,
				RandomTestUtil.randomString(), StringPool.NEW_LINE));
	}

	@Test
	public void testVarcharColumn() throws Exception {
		_testColumnTypeOf(
			Function.identity(), Function.identity(), "VARCHAR(10)",
			() -> RandomTestUtil.randomString(10));
	}

	private void _assertColumnType(String columnName, String expectedColumnType)
		throws Exception {

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select format_type(atttypid, atttypmod) as columnType from " +
					"pg_attribute where attrelid = ?::regclass and attname = " +
						"?")) {

			preparedStatement.setString(1, _targetTableName);
			preparedStatement.setString(2, StringUtil.toLowerCase(columnName));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());
				Assert.assertEquals(
					expectedColumnType, resultSet.getString("columnType"));
			}
		}
	}

	private void _assertValues(
			String columnName, Function<Object, Object> expectedFunction,
			Object[] expectedValues, Function<Object, Object> getFunction)
		throws Exception {

		int total = 0;

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select ", columnName, " from ", _targetTableName,
					" order by id ASC"))) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Assert.assertEquals(
						expectedFunction.apply(expectedValues[total++]),
						getFunction.apply(resultSet.getObject(columnName)));
				}
			}
		}

		Assert.assertEquals(expectedValues.length, total);
	}

	private void _copyTable() {
		DBCopyTablesProcess dbCopyTablesProcess = new DBCopyTablesProcess(
			InfrastructureUtil.getDataSource(), _targetDataSource);

		ReflectionTestUtil.invoke(
			dbCopyTablesProcess, "_loadColumnsMetadata", new Class<?>[0]);

		ReflectionTestUtil.invoke(
			dbCopyTablesProcess, "_copyTable",
			new Class<?>[] {String.class, String.class}, _sourceTableName,
			_targetTableName);
	}

	private void _createTable(String columnType) throws Exception {
		String createTableSQL =
			"create table TestTable (id INTEGER, testColumn " + columnType +
				")";

		_createTable(createTableSQL, createTableSQL);
	}

	private void _createTable(
			String sourceCreateTableSQL, String targetCreateTableSQL)
		throws Exception {

		_sourceDB.runSQL(sourceCreateTableSQL);

		try (Connection connection = _targetDataSource.getConnection()) {
			_targetDB.runSQL(connection, targetCreateTableSQL);
		}
	}

	private DB _getDB() {
		ServiceLoader<DBFactory> serviceLoader = ServiceLoader.load(
			DBFactory.class, DBFactory.class.getClassLoader());

		for (DBFactory dbFactory : serviceLoader) {
			if (dbFactory.getDBType() == DBType.POSTGRESQL) {
				return dbFactory.create(0, 0);
			}
		}

		throw new IllegalStateException(
			"Unable to load database type " + DBType.POSTGRESQL);
	}

	private void _insertValues(Object[] values) throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement =
				com.liferay.portal.kernel.dao.jdbc.
					AutoBatchPreparedStatementUtil.autoBatch(
						connection,
						"insert into " + _sourceTableName +
							"(id, testColumn) values(?, ?)")) {

			int id = 0;

			for (Object value : values) {
				preparedStatement.setInt(1, ++id);

				if (value instanceof byte[]) {
					preparedStatement.setBytes(2, (byte[])value);
				}
				else {
					preparedStatement.setObject(2, value);
				}

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		}
	}

	private void _testColumnTypeOf(
			Function<Object, Object> expectedFunction,
			Function<Object, Object> getFunction, String type,
			Supplier<Object> valueSupplier)
		throws Exception {

		_createTable(type);

		Object[] values = new Object[_TABLE_SIZE];

		for (int i = 0; i < _TABLE_SIZE; i++) {
			values[i] = valueSupplier.get();
		}

		_insertValues(values);

		_copyTable();

		_assertValues("testColumn", expectedFunction, values, getFunction);
	}

	private void _testRunWithCustomColumns() throws Exception {
		_createTable(
			"create table TestTable (id INTEGER, charColumn CHAR(10), " +
				"longColumn LONG, testColumn VARCHAR(75))",
			"create table TestTable (id INTEGER, testColumn VARCHAR(75))");

		_insertValues(RandomTestUtil.randomStrings(_TABLE_SIZE));

		String charColumnValue = RandomTestUtil.randomString(10);
		long longColumnValue = RandomTestUtil.randomLong();

		_sourceDB.runSQL(
			StringBundler.concat(
				"update TestTable set charColumn = '", charColumnValue,
				"', longColumn = ", longColumnValue));

		new DBCopyTablesProcess(
			InfrastructureUtil.getDataSource(), _targetDataSource
		).run();

		_assertColumnType("charColumn", "character varying(10)");

		if (_sourceDB.getDBType() == DBType.ORACLE) {
			_assertColumnType("longColumn", "numeric(30,0)");
		}
		else {
			_assertColumnType("longColumn", "bigint");
		}

		_assertValues(
			"charColumn", value -> charColumnValue, new Object[_TABLE_SIZE],
			Function.identity());
		_assertValues(
			"longColumn", value -> longColumnValue, new Object[_TABLE_SIZE],
			GetterUtil::getLong);
	}

	private void _testRunWithCustomColumns(
			String expectedColumnType,
			Function<Object, Object> expectedFunction,
			Function<Object, Object> getFunction, String type,
			Supplier<Object> valueSupplier)
		throws Exception {

		_createTable(
			"create table TestTable (id INTEGER, testColumn " + type + ")",
			"create table TestTable (id INTEGER)");

		Object[] values = new Object[_TABLE_SIZE];

		for (int i = 0; i < _TABLE_SIZE; i++) {
			values[i] = valueSupplier.get();
		}

		_insertValues(values);

		new DBCopyTablesProcess(
			InfrastructureUtil.getDataSource(), _targetDataSource
		).run();

		_assertColumnType("testColumn", expectedColumnType);

		_assertValues("testColumn", expectedFunction, values, getFunction);

		tearDown();
	}

	private static final int _TABLE_SIZE = 5000;

	private static String _sourceTableName;
	private static DataSource _targetDataSource;
	private static String _targetTableName;

	private final DB _sourceDB = DBManagerUtil.getDB();
	private final DB _targetDB = _getDB();

}