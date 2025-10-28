/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.BaseDBProcess;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class BaseDBProcessTest extends BaseDBProcess {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		_db = DBManagerUtil.getDB();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		DataAccess.cleanUp(_connection);
	}

	@Before
	public void setUp() throws Exception {
		connection = _connection;

		runSQL(
			StringBundler.concat(
				"create table ", _TABLE_NAME, " (id LONG not null primary ",
				"key, notNilColumn VARCHAR(75) not null, nilColumn ",
				"VARCHAR(75) null, typeBlob BLOB, typeBoolean BOOLEAN,",
				"typeDate DATE null, typeDouble DOUBLE, typeInteger INTEGER, ",
				"typeLong LONG null, typeLongDefault LONG default 10 not null,",
				"typeSBlob SBLOB, typeString STRING null, typeText TEXT null, ",
				"typeVarchar VARCHAR(75) null, typeVarcharDefault VARCHAR(10) ",
				"default 'testValue' not null)"));
	}

	@After
	public void tearDown() throws Exception {
		runSQL("DROP_TABLE_IF_EXISTS(" + _TABLE_NAME + ")");
	}

	@Test
	public void testAlterColumnNameChangeType() throws Exception {
		try {
			alterColumnName(_TABLE_NAME, "typeBoolean", "typeChanged VARCHAR");

			Assert.fail();
		}
		catch (SQLException sqlException) {
			Assert.assertFalse(
				_dbInspector.hasColumn(_TABLE_NAME, "typeChanged"));
		}
	}

	@Test
	public void testAlterColumnNameNoNeeded() throws Exception {
		alterColumnName(_TABLE_NAME, "deletedColumn", "typeBoolean BOOLEAN");

		Assert.assertFalse(
			_dbInspector.hasColumn(_TABLE_NAME, "deletedColumn"));

		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeBoolean", "BOOLEAN"));
	}

	@Test
	public void testAlterColumnNameNoNeededOldColumnExist() throws Exception {
		try {
			alterColumnName(_TABLE_NAME, "typeInteger", "typeBoolean BOOLEAN");

			Assert.fail();
		}
		catch (SQLException sqlException) {
			Assert.assertTrue(
				_dbInspector.hasColumn(_TABLE_NAME, "typeInteger"));

			Assert.assertTrue(
				_dbInspector.hasColumnType(
					_TABLE_NAME, "typeBoolean", "BOOLEAN"));
		}
	}

	@Test
	public void testAlterColumnNameNoNeededWithAlterType() throws Exception {
		try {
			alterColumnName(
				_TABLE_NAME, "deletedColumn", "typeBoolean VARCHAR");

			Assert.fail();
		}
		catch (SQLException sqlException) {
			Assert.assertFalse(
				_dbInspector.hasColumn(_TABLE_NAME, "deletedColumn"));

			Assert.assertFalse(
				_dbInspector.hasColumnType(
					_TABLE_NAME, "typeBoolean", "VARCHAR"));
		}
	}

	@Test
	public void testAlterColumnNameNoNeededWithAlterTypeAndOldColumnExist()
		throws Exception {

		try {
			alterColumnName(_TABLE_NAME, "typeInteger", "typeBoolean VARCHAR");

			Assert.fail();
		}
		catch (SQLException sqlException) {
			Assert.assertTrue(
				_dbInspector.hasColumn(_TABLE_NAME, "typeInteger"));

			Assert.assertFalse(
				_dbInspector.hasColumnType(
					_TABLE_NAME, "typeBoolean", "VARCHAR"));
		}
	}

	@Test
	public void testAlterColumnNameNonexistentColumn() throws Exception {
		try {
			alterColumnName(
				_TABLE_NAME, "nonexistentColumn",
				"newNonexistentColumn LONG null");

			Assert.fail();
		}
		catch (SQLException sqlException) {
			Assert.assertFalse(
				_dbInspector.hasColumn(_TABLE_NAME, "nonexistentColumn"));
		}
	}

	@Test
	public void testAlterColumnNameNoNullableChange() throws Exception {
		alterColumnName(
			_TABLE_NAME, "nilColumn", "nilColumnTest VARCHAR(75) null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "nilColumnTest", "VARCHAR(75) null"));

		alterColumnName(
			_TABLE_NAME, "notNilColumn",
			"notNilColumnTest VARCHAR(75) not null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "notNilColumnTest", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterColumnNameSameColumnDifferentCase() throws Exception {
		alterColumnName(_TABLE_NAME, "typeInteger", "TypeInteger INTEGER");

		if (StringUtil.equals(
				_dbInspector.normalizeName("typeInteger"),
				_dbInspector.normalizeName("TypeInteger"))) {

			Assert.assertTrue(
				_dbInspector.hasColumn(_TABLE_NAME, "typeInteger"));
		}
		else {
			Assert.assertTrue(
				_dbInspector.hasColumn(_TABLE_NAME, "TypeInteger"));
		}
	}

	@Test
	public void testAlterColumnTypeAlterSize() throws Exception {
		alterColumnType(_TABLE_NAME, "notNilColumn", "VARCHAR(200) not null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "notNilColumn", "VARCHAR(200) not null"));
	}

	@Test
	public void testAlterColumnTypeChangeToDefaultNotNull() throws Exception {
		alterColumnType(_TABLE_NAME, "nilColumn", "LONG default 2 not null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "nilColumn", "LONG default 2 not null"));
	}

	@Test
	public void testAlterColumnTypeChangeToNotNull() throws Exception {
		alterColumnType(_TABLE_NAME, "nilColumn", "VARCHAR(75) not null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "nilColumn", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterColumnTypeChangeToNull() throws Exception {
		alterColumnType(_TABLE_NAME, "notNilColumn", "VARCHAR(75) null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "notNilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testAlterColumnTypeChangeToText() throws Exception {
		alterColumnType(_TABLE_NAME, "typeString", "TEXT null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeString", "TEXT null"));
	}

	@Test
	public void testAlterColumnTypeChangeWithoutDefaultClause()
		throws Exception {

		_db.alterColumnType(
			_connection, _TABLE_NAME, "typeVarcharDefault",
			"VARCHAR(10) not null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeVarcharDefault", "VARCHAR(10) not null"));
	}

	@Test
	public void testAlterColumnTypeChangeWithoutNullClause() throws Exception {
		_db.alterColumnType(
			_connection, _TABLE_NAME, "notNilColumn", "VARCHAR(75)");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "notNilColumn", "VARCHAR(75) null"));

		alterColumnType(_TABLE_NAME, "nilColumn", "VARCHAR(75)");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "nilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testAlterColumnTypeNoChangesNotNull() throws Exception {
		alterColumnType(_TABLE_NAME, "notNilColumn", "VARCHAR(75) not null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "notNilColumn", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterColumnTypeNoChangesNull() throws Exception {
		alterColumnType(_TABLE_NAME, "nilColumn", "VARCHAR(75) null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "nilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testAlterColumnTypeNoNeeded() throws Exception {
		alterColumnType(_TABLE_NAME, "typeText", "TEXT null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeText", "TEXT null"));
	}

	@Test
	public void testAlterColumnTypeNonexistentColumn() throws Exception {
		try {
			alterColumnType(_TABLE_NAME, "nonexistentColumn", "TEXT not null");

			Assert.fail();
		}
		catch (SQLException sqlException) {
			Assert.assertFalse(
				_dbInspector.hasColumnType(
					_TABLE_NAME, "nonexistentColumn", "TEXT not null"));
		}
	}

	@Test
	public void testAlterColumnTypeWithData() throws Exception {
		runSQL(
			"insert into " + _TABLE_NAME +
				" (id, notNilColumn, typeString) values (1, '1', 'testValue')");

		alterColumnType(_TABLE_NAME, "typeString", "TEXT null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeString", "TEXT null"));

		try (PreparedStatement preparedStatement = _connection.prepareStatement(
				"select typeString from " + _TABLE_NAME);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			Assert.assertEquals("testValue", resultSet.getString(1));
		}
	}

	@Test
	public void testAlterIndexedColumnName() throws Exception {
		_addIndex(new String[] {"typeVarchar", "typeBoolean"});

		alterColumnName(
			_TABLE_NAME, "typeVarchar", "typeVarcharTest VARCHAR(75) null");

		Assert.assertTrue(
			_dbInspector.hasColumn(_TABLE_NAME, "typeVarcharTest"));

		_validateIndex(
			new String[] {
				_dbInspector.normalizeName("typeVarcharTest"),
				_dbInspector.normalizeName("typeBoolean")
			});
	}

	@Test
	public void testAlterIndexedColumnType() throws Exception {
		_addIndex(new String[] {"typeVarchar", "typeBoolean"});

		alterColumnType(_TABLE_NAME, "typeVarchar", "VARCHAR(50) null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeVarchar", "VARCHAR(50) null"));

		_validateIndex(
			new String[] {
				_dbInspector.normalizeName("typeVarchar"),
				_dbInspector.normalizeName("typeBoolean")
			});
	}

	@Test
	public void testAlterPrimaryKeyName() throws Exception {
		alterColumnName(_TABLE_NAME, "id", "idTest LONG not null");

		String[] primaryKeyColumnNames = ReflectionTestUtil.invoke(
			_db, "getPrimaryKeyColumnNames",
			new Class<?>[] {Connection.class, String.class}, _connection,
			_TABLE_NAME);

		Assert.assertTrue(
			ArrayUtil.contains(
				primaryKeyColumnNames, _dbInspector.normalizeName("idTest")));
	}

	@Test
	public void testAlterPrimaryKeyType() throws Exception {
		alterColumnType(_TABLE_NAME, "id", "VARCHAR(75) not null");

		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "id", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterTableAddColumn() throws Exception {
		alterTableAddColumn(_TABLE_NAME, "testColumn", "LONG null");

		Assert.assertTrue(_dbInspector.hasColumn(_TABLE_NAME, "testColumn"));
	}

	@Test
	public void testAlterTableAddExistingColumn() throws Exception {
		alterTableAddColumn(_TABLE_NAME, "typeDouble", "DOUBLE");
	}

	@Test
	public void testAlterTableAddExistingColumnWithDifferentType()
		throws Exception {

		try {
			alterTableAddColumn(_TABLE_NAME, "typeDouble", "VARCHAR");

			Assert.fail();
		}
		catch (SQLException sqlException) {
			Assert.assertFalse(
				hasColumnType(_TABLE_NAME, "typeDouble", "VARCHAR"));
		}
	}

	@Test
	public void testAlterTableDropIndexedColumn() throws Exception {
		_addIndex(new String[] {"typeVarchar", "typeBoolean"});

		alterTableDropColumn(_TABLE_NAME, "typeVarchar");

		Assert.assertFalse(_dbInspector.hasColumn(_TABLE_NAME, "typeVarchar"));

		List<IndexMetadata> indexMetadatas = ReflectionTestUtil.invoke(
			_db, "getIndexMetadatas",
			new Class<?>[] {
				Connection.class, String.class, String.class, boolean.class
			},
			_connection, _TABLE_NAME, "typeVarchar", false);

		Assert.assertEquals(
			indexMetadatas.toString(), 0, indexMetadatas.size());
	}

	@Test
	public void testAlterTableDropNonexistentColumn() throws Exception {
		alterTableDropColumn(_TABLE_NAME, "nonexistentColumn");
	}

	@Test
	public void testDropIndexes() throws Exception {
		_addIndex(new String[] {"typeVarchar", "typeBoolean"});

		Assert.assertTrue(hasIndex(_TABLE_NAME, _INDEX_NAME));

		dropIndexes(Collections.singletonList(_INDEX_NAME), _TABLE_NAME);

		Assert.assertFalse(hasIndex(_TABLE_NAME, _INDEX_NAME));
	}

	@Test
	public void testDropNonexistentTable() throws Exception {
		dropTable("nonexistentTable");
	}

	@Test
	public void testDropTable() throws Exception {
		dropTable(_TABLE_NAME);

		Assert.assertFalse(_dbInspector.hasTable(_TABLE_NAME));
	}

	@Test
	public void testProcessConcurrently() throws Exception {
		_validateProcessConcurrently(
			threadIds -> processConcurrently(
				"select id from " + _TABLE_NAME,
				resultSet -> new Object[] {resultSet.getInt("id")},
				values -> {
					Thread currentThread = Thread.currentThread();

					threadIds.add(currentThread.getId());

					int value = (int)values[0];

					runSQL(
						StringBundler.concat(
							"update ", _TABLE_NAME, " set typeInteger = ",
							value, " where id = ", value));
				},
				null));
	}

	@Test
	public void testProcessConcurrentlyShutdown() throws Exception {
		List<Integer> values = new ArrayList<>();

		for (int i = 1; i <= _PROCESS_CONCURRENTLY_COUNT; i++) {
			values.add(i);
		}

		List<Future<Void>> futures = new ArrayList<>();

		ExecutorService executorService = Executors.newWorkStealingPool();

		for (int i = 0; i <= 10; i++) {
			Future<Void> future = executorService.submit(
				() -> {
					processConcurrently(
						values.toArray(new Integer[0]),
						value -> Thread.sleep(1000), "An exception was thrown");

					return null;
				});

			futures.add(future);
		}

		executorService.shutdown();

		for (Future<Void> future : futures) {
			future.get();
		}
	}

	@Test
	public void testProcessConcurrentlyWithBatch() throws Exception {
		_validateProcessConcurrently(
			threadIds -> processConcurrently(
				"select id from " + _TABLE_NAME,
				"update " + _TABLE_NAME + " set typeInteger = ? where id = ?",
				resultSet -> new Object[] {resultSet.getInt("id")},
				(values, preparedStatement) -> {
					Thread currentThread = Thread.currentThread();

					threadIds.add(currentThread.getId());

					int value = (int)values[0];

					preparedStatement.setInt(1, value);
					preparedStatement.setInt(2, value);

					preparedStatement.addBatch();
				},
				null));
	}

	@Test
	public void testProcessConcurrentlyWithList() throws Exception {
		List<Integer> values = new ArrayList<>();

		for (int i = 1; i <= _PROCESS_CONCURRENTLY_COUNT; i++) {
			values.add(i);
		}

		_validateProcessConcurrently(
			threadIds -> processConcurrently(
				values.toArray(new Integer[0]),
				value -> {
					Thread currentThread = Thread.currentThread();

					threadIds.add(currentThread.getId());

					runSQL(
						StringBundler.concat(
							"update ", _TABLE_NAME, " set typeInteger = ",
							value, " where id = ", value));
				},
				null));
	}

	private void _addIndex(String[] columnNames) {
		List<IndexMetadata> indexMetadatas = Arrays.asList(
			new IndexMetadata(_INDEX_NAME, _TABLE_NAME, false, columnNames));

		ReflectionTestUtil.invoke(
			_db, "addIndexes", new Class<?>[] {Connection.class, List.class},
			_connection, indexMetadatas);
	}

	private void _populateTable() throws Exception {
		for (int i = 1; i <= _PROCESS_CONCURRENTLY_COUNT; i++) {
			runSQL(
				StringBundler.concat(
					"insert into ", _TABLE_NAME, " (id, notNilColumn) values (",
					i, ", '1')"));
		}
	}

	private void _validateIndex(String[] columnNames) throws Exception {
		List<IndexMetadata> indexMetadatas = ReflectionTestUtil.invoke(
			_db, "getIndexMetadatas",
			new Class<?>[] {
				Connection.class, String.class, String.class, boolean.class
			},
			_connection, _TABLE_NAME, columnNames[0], false);

		Assert.assertEquals(
			indexMetadatas.toString(), 1, indexMetadatas.size());

		IndexMetadata indexMetadata = indexMetadatas.get(0);

		Assert.assertEquals(
			_dbInspector.normalizeName(_INDEX_NAME),
			indexMetadata.getIndexName());

		Assert.assertArrayEquals(
			ArrayUtil.sortedUnique(columnNames),
			ArrayUtil.sortedUnique(indexMetadata.getColumnNames()));
	}

	private void _validateProcessConcurrently(
			UnsafeConsumer<Set<Long>, Exception> unsafeConsumer)
		throws Exception {

		_populateTable();

		Set<Long> threadIds = Collections.synchronizedSet(new HashSet<>());

		unsafeConsumer.accept(threadIds);

		Assert.assertTrue(threadIds.size() > 1);

		_validateTableContent();
	}

	private void _validateTableContent() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(1) from " + _TABLE_NAME +
					" where id >= 1 and id <= ? and typeInteger = id")) {

			preparedStatement.setInt(1, _PROCESS_CONCURRENTLY_COUNT);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();

				Assert.assertEquals(
					_PROCESS_CONCURRENTLY_COUNT, resultSet.getInt(1));
			}
		}
	}

	private static final String _INDEX_NAME = "IX_TEMP";

	private static final int _PROCESS_CONCURRENTLY_COUNT = 100;

	private static final String _TABLE_NAME = "BaseDBProcessTest";

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;

}