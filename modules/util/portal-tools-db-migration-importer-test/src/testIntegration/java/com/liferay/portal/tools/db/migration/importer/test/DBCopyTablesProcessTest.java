/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.db.migration.importer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.tools.db.migration.importer.DBCopyTablesProcess;
import com.liferay.portal.tools.db.migration.importer.jdbc.AutoBatchPreparedStatementUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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
			"BIGDECIMAL", RandomTestUtil::nextDouble, GetterUtil::getDouble);
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
			"BOOLEAN", RandomTestUtil::randomBoolean, Function.identity());
	}

	@Test
	public void testDateColumn() throws Exception {
		_testColumnTypeOf(
			"DATE", RandomTestUtil::nextTimestamp, Function.identity());
	}

	@Test
	public void testDoubleColumn() throws Exception {
		_testColumnTypeOf(
			"DOUBLE", RandomTestUtil::nextDouble, GetterUtil::getDouble);
	}

	@Test
	public void testIntegerColumn() throws Exception {
		_testColumnTypeOf(
			"INTEGER", RandomTestUtil::nextInt, Function.identity());
	}

	@Test
	public void testLongColumn() throws Exception {
		_testColumnTypeOf(
			"LONG", RandomTestUtil::nextLong, Function.identity());
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
						(byte[])values[total++], resultSet.getBytes(1));
				}
			}
		}

		Assert.assertEquals(values.length, total);
	}

	@Test
	public void testStringColumn() throws Exception {
		_testColumnTypeOf(
			"STRING", RandomTestUtil::randomString, Function.identity());
	}

	@Test
	public void testTextColumn() throws Exception {
		_testColumnTypeOf(
			"TEXT", RandomTestUtil::randomString, Function.identity());
	}

	@Test
	public void testVarcharColumn() throws Exception {
		_testColumnTypeOf(
			"VARCHAR(10)", () -> RandomTestUtil.randomString(10),
			Function.identity());
	}

	private void _assertValues(
			Object[] expectedValues, Function<Object, Object> function)
		throws Exception {

		int total = 0;

		try (Connection connection = _targetDataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				"select testColumn from " + _targetTableName +
					" order by id ASC")) {

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					Assert.assertEquals(
						expectedValues[total++],
						function.apply(resultSet.getObject(1)));
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

		_sourceDB.runSQL(createTableSQL);

		try (Connection connection = _targetDataSource.getConnection()) {
			_targetDB.runSQL(connection, createTableSQL);
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
			String type, Supplier<Object> valueSupplier,
			Function<Object, Object> getFunction)
		throws Exception {

		_createTable(type);

		Object[] values = new Object[_TABLE_SIZE];

		for (int i = 0; i < _TABLE_SIZE; i++) {
			values[i] = valueSupplier.get();
		}

		_insertValues(values);

		_copyTable();

		_assertValues(values, getFunction);
	}

	private static final int _TABLE_SIZE = 5000;

	private static String _sourceTableName;
	private static DataSource _targetDataSource;
	private static String _targetTableName;

	private final DB _sourceDB = DBManagerUtil.getDB();
	private final DB _targetDB = _getDB();

}