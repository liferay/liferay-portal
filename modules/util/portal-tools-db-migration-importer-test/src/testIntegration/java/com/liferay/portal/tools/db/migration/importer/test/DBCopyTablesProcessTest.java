/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tools.db.migration.importer.DBCopyTablesProcess;
import com.liferay.portal.tools.db.migration.importer.jdbc.AutoBatchPreparedStatementUtil;

import java.io.PrintStream;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
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

		DataSource dataSource = InfrastructureUtil.getDataSource();

		_sourceDataSource = ProxyUtil.newDelegateProxyInstance(
			DataSource.class.getClassLoader(), DataSource.class,
			new Object() {

				public Connection getConnection() throws SQLException {
					Connection connection = dataSource.getConnection();

					DatabaseMetaData databaseMetaData =
						connection.getMetaData();

					DatabaseMetaData proxyDatabaseMetaData =
						ProxyUtil.newDelegateProxyInstance(
							DatabaseMetaData.class.getClassLoader(),
							DatabaseMetaData.class,
							new Object() {

								public ResultSet getColumns(
										String catalog, String schemaPattern,
										String tableNamePattern,
										String columnNamePattern)
									throws SQLException {

									return databaseMetaData.getColumns(
										catalog, schemaPattern,
										"%" + _sourceTableName + "%",
										columnNamePattern);
								}

							},
							databaseMetaData);

					return ProxyUtil.newDelegateProxyInstance(
						Connection.class.getClassLoader(), Connection.class,
						new Object() {

							public DatabaseMetaData getMetaData() {
								return proxyDatabaseMetaData;
							}

						},
						connection);
				}

			},
			dataSource);

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

	@Before
	public void setUp() throws Exception {
		_sourceTableName = _normalizeName("TestTable");
	}

	@After
	public void tearDown() throws Exception {
		_sourceDB.runSQL("DROP_TABLE_IF_EXISTS(" + _sourceTableName + ")");

		try (Connection connection = _targetDataSource.getConnection()) {
			_targetDB.runSQL(
				connection,
				"drop table if exists \"" +
					StringUtil.toLowerCase(_sourceTableName) + "\"");
		}
	}

	@Test
	@TestInfo("LPD-110216")
	public void testAddCustomPrimaryKeysAndIndexes() throws Exception {
		_sourceDB.runSQL(
			"create table TestTable (id INTEGER not null primary key, " +
				"charColumn VARCHAR(75), longColumn VARCHAR(700), testColumn " +
					"VARCHAR(75), textColumn VARCHAR(75))");

		_sourceDB.runSQL(
			new String[] {
				"create index IX_" + RandomTestUtil.randomString(61) +
					" on TestTable (textColumn, charColumn)",
				"create index IX_LEFT_MATCH on TestTable (id, testColumn)",
				"create index IX_LONG_COLUMN on TestTable (longColumn)",
				"create index TestTable on TestTable (charColumn)",
				"create unique index IX_DUPLICATE on TestTable (testColumn)",
				"create unique index IX_UNIQUE on TestTable (testColumn, id)",
				"create unique index IX_UNIQUE_MISMATCH on TestTable " +
					"(charColumn, id)"
			});

		List<String> expectedIndexDefinitions = ListUtil.fromArray(
			"CREATE INDEX ix_906fecd9 ON testtable USING btree (textcolumn, " +
				"charcolumn)",
			"CREATE INDEX ix_e1d669e2 ON testtable USING btree (charcolumn)",
			"CREATE INDEX ix_left_match ON testtable USING btree (id, " +
				"\"left\"((testcolumn)::text, 676))",
			"CREATE INDEX ix_long_column ON testtable USING btree " +
				"(\"left\"((longcolumn)::text, 676))",
			"CREATE INDEX ix_unique_mismatch ON testtable USING btree " +
				"(charcolumn, id)",
			"CREATE UNIQUE INDEX ix_unique ON testtable USING btree " +
				"(testcolumn, id)",
			"CREATE UNIQUE INDEX testtable_pkey ON testtable USING btree (id)");
		List<String> expectedMessages = ListUtil.fromArray(
			"created index ix_long_column on table testtable in the default " +
				"schema as a btree index on (left(\"longcolumn\", 676))",
			"unable to create unique index ix_unique_mismatch on table " +
				"testtable in the default schema because index " +
					"ix_unique_mismatch already exists on the same columns");

		DBType dbType = _sourceDB.getDBType();

		if ((dbType == DBType.MARIADB) || (dbType == DBType.MYSQL)) {
			_sourceDB.runSQL(
				"create fulltext index IX_TYPED on TestTable (charColumn, " +
					"longColumn)");

			if (dbType == DBType.MYSQL) {
				_sourceDB.runSQL(
					"create index IX_EXPRESSION on TestTable " +
						"((upper(testColumn)))");

				expectedMessages.add(
					"unable to create index ix_expression on table testtable " +
						"in the default schema because one of its columns is " +
							"an expression");
			}

			expectedIndexDefinitions.add(
				"CREATE INDEX ix_typed ON testtable USING btree (charcolumn, " +
					"\"left\"((longcolumn)::text, 676))");
			expectedMessages.add(
				"created fulltext index ix_typed on table testtable in the " +
					"default schema as a btree index on (\"charcolumn\", " +
						"left(\"longcolumn\", 676))");
		}
		else if (dbType == DBType.ORACLE) {
			_sourceDB.runSQL(
				new String[] {
					"create index IX_EXPRESSION on TestTable " +
						"(upper(testColumn))",
					"create index IX_TYPED on TestTable (textColumn) " +
						"indextype is ctxsys.context"
				});

			expectedIndexDefinitions.add(
				"CREATE INDEX ix_typed ON testtable USING btree (textcolumn)");
			expectedMessages.add(
				"created ctxsys.context index ix_typed on table testtable in " +
					"the default schema as a btree index on (\"textcolumn\")");
			expectedMessages.add(
				"unable to create index ix_expression on table testtable in " +
					"the default schema because one of its columns is an " +
						"expression");
		}
		else if (dbType == DBType.SQLSERVER) {
			_sourceDB.runSQL(
				"create unique index IX_TYPED on TestTable (textColumn) " +
					"where textColumn is not null");

			expectedIndexDefinitions.add(
				"CREATE INDEX ix_typed ON testtable USING btree (textcolumn)");
			expectedMessages.add(
				"created filtered index ix_typed on table testtable in the " +
					"default schema as a btree index on (\"textcolumn\")");
		}

		String[] values = RandomTestUtil.randomStrings(_TABLE_SIZE);

		_insertValues(values);

		DBCopyTablesProcess dbCopyTablesProcess = new DBCopyTablesProcess(
			_sourceDataSource, _targetDataSource);

		dbCopyTablesProcess.run();

		try (Connection connection = _targetDataSource.getConnection()) {
			_targetDB.runSQL(
				connection,
				new String[] {
					"create index ix_left_match on testtable (id, " +
						"left(testcolumn, 676))",
					"create index ix_unique_mismatch on testtable " +
						"(charcolumn, id)",
					StringBundler.concat(
						"insert into testtable (id, testcolumn) values (",
						_TABLE_SIZE + 1, ", '", values[0], "')")
				});
		}

		PrintStream printStream = System.out;

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		System.setOut(new PrintStream(unsyncByteArrayOutputStream, true));

		try {
			dbCopyTablesProcess.addCustomPrimaryKeysAndIndexes();
		}
		finally {
			System.setOut(printStream);
		}

		Assert.assertEquals(
			ListUtil.sort(expectedMessages),
			ListUtil.sort(
				TransformUtil.transformToList(
					StringUtil.splitLines(
						unsyncByteArrayOutputStream.toString()),
					message -> {
						if (message.startsWith("Renaming ") ||
							message.contains(": ")) {

							return null;
						}

						return StringUtil.toLowerCase(message);
					})));

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select replace(indexdef, current_schema() || '.', '') as " +
					"indexDefinition from pg_indexes where schemaname = " +
						"current_schema() and tablename = ?")) {

			preparedStatement.setString(1, _targetTableName);

			List<String> indexDefinitions = new ArrayList<>();

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					indexDefinitions.add(
						resultSet.getString("indexDefinition"));
				}
			}

			Assert.assertEquals(
				ListUtil.sort(expectedIndexDefinitions),
				ListUtil.sort(indexDefinitions));
		}
	}

	@Test
	@TestInfo("LPD-110216")
	public void testAddCustomPrimaryKeysAndIndexesInPartition()
		throws Exception {

		_testInPartition(this::testAddCustomPrimaryKeysAndIndexes);
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
		_testInPartition(this::_testRunWithCustomColumns);
	}

	@Test
	@TestInfo("LPD-110099")
	public void testRunWithCustomTables() throws Exception {
		_testRunWithCustomTables();
	}

	@Test
	@TestInfo("LPD-110099")
	public void testRunWithCustomTablesInPartition() throws Exception {
		_testInPartition(this::_testRunWithCustomTables);
	}

	@Test
	@TestInfo("LPD-110099")
	public void testRunWithLongTableName() throws Exception {
		String tableName =
			"TestTableWithANameLongerThanTheSixtyThreeBytesPostgreSQLAllowsXX";

		_sourceTableName = _normalizeName(tableName);

		_sourceDB.runSQL("create table " + tableName + " (id INTEGER)");

		_runDBCopyTablesProcess();

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(*) as count_ from pg_class where relname = ?")) {

			preparedStatement.setString(
				1, StringUtil.toLowerCase(tableName.substring(0, 63)));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());
				Assert.assertEquals(0, resultSet.getInt("count_"));
			}
		}
	}

	@Test
	@TestInfo("LPD-110099")
	public void testRunWithReservedWords() throws Exception {
		_sourceTableName = _normalizeName("verbose");

		_sourceDB.runSQL("create table verbose (id INTEGER, freeze INTEGER)");
		_sourceDB.runSQL("insert into verbose (id, freeze) values (1, 2)");

		_runDBCopyTablesProcess();

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select \"id\", \"freeze\" from \"verbose\"");

			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(2, resultSet.getInt("freeze"));
			Assert.assertEquals(1, resultSet.getInt("id"));
			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	@TestInfo("LPD-110099")
	public void testRunWithToolTables() throws Exception {
		Assume.assumeTrue(_sourceDB.getDBType() == DBType.ORACLE);

		_sourceDB.runSQL(
			"create table TestTable (id INTEGER, testColumn VARCHAR(75))");
		_sourceDB.runSQL(
			"create index TestTable_Text on TestTable (testColumn) indextype " +
				"is ctxsys.context");
		_sourceDB.runSQL(
			"create materialized view log on TestTable with rowid");

		_runDBCopyTablesProcess();

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select relname from pg_class where relname like ? order by " +
					"relname")) {

			preparedStatement.setString(1, "%testtable%");

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());
				Assert.assertEquals(
					"testtable", resultSet.getString("relname"));
				Assert.assertFalse(resultSet.next());
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
				"select format_type(atttypid, atttypmod) || case when " +
					"attnotnull then ' not null' else '' end as columnType " +
						"from pg_attribute where attrelid = ?::regclass and " +
							"attname = ?")) {

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
			_sourceDataSource, _targetDataSource);

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

	private String _normalizeName(String name) throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.normalizeName(name);
		}
	}

	private void _runDBCopyTablesProcess() throws Exception {
		DBCopyTablesProcess dbCopyTablesProcess = new DBCopyTablesProcess(
			_sourceDataSource, _targetDataSource);

		dbCopyTablesProcess.run();
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

	private void _testInPartition(UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

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
			unsafeRunnable.run();
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

		_runDBCopyTablesProcess();

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

		_runDBCopyTablesProcess();

		_assertColumnType("testColumn", expectedColumnType);

		_assertValues("testColumn", expectedFunction, values, getFunction);

		tearDown();
	}

	private void _testRunWithCustomTables() throws Exception {
		_sourceDB.runSQL(
			"create table TestTable (id INTEGER, charColumn CHAR(10), " +
				"testColumn VARCHAR(75) not null)");

		String[] values = RandomTestUtil.randomStrings(_TABLE_SIZE);

		_insertValues(values);

		_runDBCopyTablesProcess();

		_assertColumnType("charColumn", "character varying(10)");

		if (_sourceDB.getDBType() == DBType.ORACLE) {
			_assertColumnType("id", "numeric(30,0)");
		}
		else {
			_assertColumnType("id", "integer");
		}

		_assertColumnType("testColumn", "character varying(75) not null");

		try (Connection connection = _targetDataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select attname from pg_attribute where attrelid = " +
					"?::regclass and attnum > 0 order by attnum")) {

			preparedStatement.setString(1, _targetTableName);

			List<String> columnNames = new ArrayList<>();

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					columnNames.add(resultSet.getString("attname"));
				}
			}

			Assert.assertEquals(
				Arrays.asList("id", "charcolumn", "testcolumn"), columnNames);
		}

		_assertValues(
			"charColumn", value -> null, new Object[_TABLE_SIZE],
			Function.identity());
		_assertValues(
			"testColumn", Function.identity(), values, Function.identity());
	}

	private static final int _TABLE_SIZE = 5000;

	private static DataSource _sourceDataSource;
	private static String _sourceTableName;
	private static DataSource _targetDataSource;
	private static String _targetTableName;

	private final DB _sourceDB = DBManagerUtil.getDB();
	private final DB _targetDB = _getDB();

}