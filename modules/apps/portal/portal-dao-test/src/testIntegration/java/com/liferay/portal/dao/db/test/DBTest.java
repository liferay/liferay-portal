/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class DBTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		db = DBManagerUtil.getDB();

		dbInspector = new DBInspector(connection);

		Assume.assumeTrue(
			(db.getDBType() != DBType.ORACLE) &&
			(db.getDBType() != DBType.POSTGRESQL));
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		connection = DataAccess.getConnection();

		db = DBManagerUtil.getDB();

		dbInspector = new DBInspector(connection);

		for (int i = 0; i < _SYNC_TABLES_COLUMN_NAMES.length; i++) {
			_SYNC_TABLES_COLUMN_NAMES[i] = dbInspector.normalizeName(
				_SYNC_TABLES_COLUMN_NAMES[i]);
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		DataAccess.cleanUp(connection);
	}

	@Before
	public void setUp() throws Exception {
		_createTestTable(TABLE_NAME_1);
	}

	@After
	public void tearDown() throws Exception {
		db.runSQL("DROP_TABLE_IF_EXISTS(" + TABLE_NAME_1 + ")");
		db.runSQL("DROP_TABLE_IF_EXISTS(" + _TABLE_NAME_2 + ")");
		db.runSQL("DROP_TABLE_IF_EXISTS(" + _TABLE_NAME_3 + ")");
	}

	@Test
	public void testAlterColumnNameNoNullableChange() throws Exception {
		db.alterColumnName(
			connection, TABLE_NAME_1, "nilColumn",
			"nilColumnTest VARCHAR(75) null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "nilColumnTest", "VARCHAR(75) null"));

		db.alterColumnName(
			connection, TABLE_NAME_1, "notNilColumn",
			"notNilColumnTest VARCHAR(75) not null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "notNilColumnTest", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterColumnTypeAlterSize() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "notNilColumn", "VARCHAR(200) not null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "notNilColumn", "VARCHAR(200) not null"));
	}

	@Test
	public void testAlterColumnTypeChangeToDefaultNotNull() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "nilColumn",
			"VARCHAR(75) default 'test' not null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "nilColumn",
				"VARCHAR(75) default 'test' not null"));
	}

	@Test
	public void testAlterColumnTypeChangeToDefaultNull() throws Exception {
		try {
			db.alterColumnType(
				connection, TABLE_NAME_1, "notNilColumn",
				"VARCHAR(75) default 'test' null");

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Invalid alter column type statement",
				illegalArgumentException.getMessage());

			Assert.assertTrue(
				dbInspector.hasColumnType(
					TABLE_NAME_1, "notNilColumn", "VARCHAR(75) not null"));
		}
	}

	@Test
	public void testAlterColumnTypeChangeToNotNull() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "nilColumn", "VARCHAR(75) not null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "nilColumn", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterColumnTypeChangeToNull() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "notNilColumn", "VARCHAR(75) null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "notNilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testAlterColumnTypeChangeToText() throws Exception {
		db.alterColumnType(connection, TABLE_NAME_1, "typeString", "TEXT null");

		Assert.assertTrue(
			dbInspector.hasColumnType(TABLE_NAME_1, "typeString", "TEXT null"));
	}

	@Test
	public void testAlterColumnTypeChangeWithoutDefaultClause()
		throws Exception {

		db.alterColumnType(
			connection, TABLE_NAME_1, "typeVarcharDefault",
			"VARCHAR(10) not null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "typeVarcharDefault", "VARCHAR(10) not null"));
	}

	@Test
	public void testAlterColumnTypeChangeWithoutNullClause() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "notNilColumn", "VARCHAR(75)");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "notNilColumn", "VARCHAR(75) null"));

		db.alterColumnType(
			connection, TABLE_NAME_1, "nilColumn", "VARCHAR(75)");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "nilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testAlterColumnTypeDefaultWithData() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "nilColumn",
			"VARCHAR(75) default 'test' not null");

		db.runSQL(
			"insert into " + TABLE_NAME_1 +
				" (id, notNilColumn) values (1, '1')");

		db.runSQL(
			"insert into " + TABLE_NAME_1 +
				" (id, notNilColumn, nilColumn) values (2, '2', 'nil')");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "nilColumn",
				"VARCHAR(75) default 'test' not null"));

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select nilColumn from " + TABLE_NAME_1 + " order by id");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			Assert.assertEquals("test", resultSet.getString(1));

			resultSet.next();

			Assert.assertEquals("nil", resultSet.getString(1));
		}
	}

	@Test
	public void testAlterColumnTypeNoChangesNotNull() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "notNilColumn", "VARCHAR(75) not null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "notNilColumn", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterColumnTypeNoChangesNull() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "nilColumn", "VARCHAR(75) null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "nilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testAlterColumnTypeWithData() throws Exception {
		db.runSQL(
			"insert into " + TABLE_NAME_1 +
				" (id, notNilColumn, typeString) values (1, '1', 'testValue')");

		db.alterColumnType(connection, TABLE_NAME_1, "typeString", "TEXT null");

		Assert.assertTrue(
			dbInspector.hasColumnType(TABLE_NAME_1, "typeString", "TEXT null"));

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select typeString from " + TABLE_NAME_1);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			Assert.assertEquals("testValue", resultSet.getString(1));
		}
	}

	@Test
	public void testAlterIndexedColumnName() throws Exception {
		addIndex(new String[] {"typeVarchar", "typeBoolean"});

		db.alterColumnName(
			connection, TABLE_NAME_1, "typeVarchar",
			"typeVarcharTest VARCHAR(75) null");

		Assert.assertTrue(
			dbInspector.hasColumn(TABLE_NAME_1, "typeVarcharTest"));

		_validateIndex(
			new String[] {
				dbInspector.normalizeName("typeVarcharTest"),
				dbInspector.normalizeName("typeBoolean")
			});
	}

	@Test
	public void testAlterIndexedColumnType() throws Exception {
		addIndex(new String[] {"typeVarchar", "typeBoolean"});

		db.alterColumnType(
			connection, TABLE_NAME_1, "typeVarchar", "VARCHAR(50) null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "typeVarchar", "VARCHAR(50) null"));

		_validateIndex(
			new String[] {
				dbInspector.normalizeName("typeVarchar"),
				dbInspector.normalizeName("typeBoolean")
			});
	}

	@Test
	public void testAlterPrimaryKeyName() throws Exception {
		db.alterColumnName(
			connection, TABLE_NAME_1, "id", "idTest LONG not null");

		Assert.assertTrue(
			ArrayUtil.contains(
				db.getPrimaryKeyColumnNames(connection, TABLE_NAME_1),
				dbInspector.normalizeName("idTest")));
	}

	@Test
	public void testAlterPrimaryKeyType() throws Exception {
		db.alterColumnType(
			connection, TABLE_NAME_1, "id", "VARCHAR(75) not null");

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "id", "VARCHAR(75) not null"));
	}

	@Test
	public void testAlterTableAddColumn() throws Exception {
		db.alterTableAddColumn(
			connection, TABLE_NAME_1, "testColumn", "LONG null");

		Assert.assertTrue(dbInspector.hasColumn(TABLE_NAME_1, "testColumn"));
	}

	@Test
	public void testAlterTableAddColumnLongDefaultNotNull() throws Exception {
		db.alterTableAddColumn(
			connection, TABLE_NAME_1, "testColumn", "LONG default 2 not null");

		db.runSQL(
			"insert into " + TABLE_NAME_1 +
				" (id, notNilColumn) values (1, '1')");

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select testColumn from " + TABLE_NAME_1);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			Assert.assertEquals(2, resultSet.getLong(1));
		}

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "testColumn", "LONG default 2 not null"));
	}

	@Test
	public void testAlterTableAddColumnVarcharDefaultNotNull()
		throws Exception {

		db.alterTableAddColumn(
			connection, TABLE_NAME_1, "testColumn",
			"VARCHAR(40) default 'test value' not null");

		db.runSQL(
			"insert into " + TABLE_NAME_1 +
				" (id, notNilColumn) values (1, '1')");

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select testColumn from " + TABLE_NAME_1);
			ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();

			Assert.assertEquals("test value", resultSet.getString(1));
		}

		Assert.assertTrue(
			dbInspector.hasColumnType(
				TABLE_NAME_1, "testColumn",
				"VARCHAR(40) default 'test value' not null"));
	}

	@Test
	public void testAlterTableDropIndexedColumn() throws Exception {
		addIndex(new String[] {"typeVarchar", "typeBoolean"});

		db.alterTableDropColumn(connection, TABLE_NAME_1, "typeVarchar");

		Assert.assertFalse(dbInspector.hasColumn(TABLE_NAME_1, "typeVarchar"));
		Assert.assertFalse(dbInspector.hasIndex(TABLE_NAME_1, INDEX_NAME));
	}

	@Test
	public void testAlterTableDropIndexedColumnWithDuplicateValues()
		throws Exception {

		db.runSQL(
			"create table " + DBTest._TABLE_NAME_2 +
				" (id1 LONG not null, id2 LONG not null)");

		addIndex(_TABLE_NAME_2, new String[] {"id1", "id2"}, true);

		db.runSQL("INSERT into " + _TABLE_NAME_2 + " (id1, id2) values (1, 1)");
		db.runSQL("INSERT into " + _TABLE_NAME_2 + " (id1, id2) values (1, 2)");

		db.alterTableDropColumn(connection, _TABLE_NAME_2, "id2");

		Assert.assertFalse(dbInspector.hasColumn(_TABLE_NAME_2, "id2"));
		Assert.assertFalse(dbInspector.hasIndex(_TABLE_NAME_2, INDEX_NAME));
	}

	@Test
	public void testAlterTableName() throws Exception {
		db.runSQL(
			StringBundler.concat(
				"alter_table_name ", TABLE_NAME_1, StringPool.SPACE,
				_TABLE_NAME_2));

		Assert.assertTrue(dbInspector.hasTable(_TABLE_NAME_2));

		db.runSQL("DROP_TABLE_IF_EXISTS(" + _TABLE_NAME_2 + ")");

		Assert.assertFalse(dbInspector.hasTable(TABLE_NAME_1));
	}

	@Test
	public void testCopyTableRows() throws Exception {
		_createTestTable(_TABLE_NAME_2);

		db.runSQL(
			StringBundler.concat(
				"insert into ", TABLE_NAME_1,
				" (id, notNilColumn, typeString) values (1, '1', ",
				"'testTable1Value1')"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", TABLE_NAME_1,
				" (id, notNilColumn, typeString) values (2, '2', ",
				"'testTable1Value2')"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", TABLE_NAME_1,
				" (id, notNilColumn) values (3, '3')"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", _TABLE_NAME_2,
				" (id, notNilColumn, typeString) values (1, '1', ",
				"'testTable2Value1')"));

		Map<String, String> columnNamesMap = new HashMap<>();

		for (String columnName : _SYNC_TABLES_COLUMN_NAMES) {
			columnNamesMap.put(columnName, columnName);
		}

		db.copyTableRows(
			connection, TABLE_NAME_1, _TABLE_NAME_2, columnNamesMap,
			HashMapBuilder.put(
				dbInspector.normalizeName("typeString"), "'test'"
			).build());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from " + _TABLE_NAME_2 + " order by id asc");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(1, resultSet.getLong("id"));
			Assert.assertEquals("1", resultSet.getString("notNilColumn"));
			Assert.assertEquals(
				"testTable2Value1", resultSet.getString("typeString"));

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(2, resultSet.getLong("id"));
			Assert.assertEquals("2", resultSet.getString("notNilColumn"));
			Assert.assertEquals(
				"testTable1Value2", resultSet.getString("typeString"));

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(3, resultSet.getLong("id"));
			Assert.assertEquals("3", resultSet.getString("notNilColumn"));
			Assert.assertEquals("test", resultSet.getString("typeString"));

			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	public void testCopyTableRowsDifferentColumnNames() throws Exception {
		db.runSQL(
			StringBundler.concat(
				"create table ", _TABLE_NAME_2, " (id2 LONG not null primary ",
				"key, notNilColumn2 VARCHAR(75) not null, nilColumn2 ",
				"VARCHAR(75) null, typeBlob2 BLOB, typeBoolean2 BOOLEAN,",
				"typeDate2 DATE null, typeDouble2 DOUBLE, typeInteger2 ",
				"INTEGER, typeLong2 LONG null, typeSBlob2 SBLOB, typeString2 ",
				"STRING null, typeText2 TEXT null, typeVarchar2 VARCHAR(75) ",
				"null);"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", TABLE_NAME_1,
				" (id, notNilColumn, typeString) values (1, '1', ",
				"'testTable1Value1')"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", TABLE_NAME_1,
				" (id, notNilColumn, typeString) values (2, '2', ",
				"'testTable1Value2')"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", TABLE_NAME_1,
				" (id, notNilColumn) values (3, '3')"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", _TABLE_NAME_2,
				" (id2, notNilColumn2, typeString2) values (1, '1', ",
				"'testTable2Value1')"));

		Map<String, String> columnNamesMap = new HashMap<>();

		for (String columnName : _SYNC_TABLES_COLUMN_NAMES) {
			columnNamesMap.put(columnName, columnName + "2");
		}

		db.copyTableRows(
			connection, TABLE_NAME_1, _TABLE_NAME_2, columnNamesMap,
			HashMapBuilder.put(
				dbInspector.normalizeName("typeString2"), "'test'"
			).build());

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from " + _TABLE_NAME_2 + " order by id2 asc");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(1, resultSet.getLong("id2"));
			Assert.assertEquals("1", resultSet.getString("notNilColumn2"));
			Assert.assertEquals(
				"testTable2Value1", resultSet.getString("typeString2"));

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(2, resultSet.getLong("id2"));
			Assert.assertEquals("2", resultSet.getString("notNilColumn2"));
			Assert.assertEquals(
				"testTable1Value2", resultSet.getString("typeString2"));

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(3, resultSet.getLong("id2"));
			Assert.assertEquals("3", resultSet.getString("notNilColumn2"));
			Assert.assertEquals("test", resultSet.getString("typeString2"));

			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	public void testCopyTableStructure() throws Exception {
		String[] indexColumnNames = {"typeVarchar", "typeBoolean"};

		addIndex(indexColumnNames);

		db.copyTableStructure(connection, TABLE_NAME_1, _TABLE_NAME_2);

		boolean supportsDuplicatedIndexName = ReflectionTestUtil.invoke(
			db, "isSupportsDuplicatedIndexName", new Class<?>[0]);

		Assert.assertTrue(dbInspector.hasTable(_TABLE_NAME_2));
		Assert.assertFalse(dbInspector.hasRows(_TABLE_NAME_2));
		Assert.assertFalse(dbInspector.isNullable(_TABLE_NAME_2, "id"));
		Assert.assertFalse(
			dbInspector.isNullable(_TABLE_NAME_2, "notNilColumn"));

		String indexNamePrefix = StringPool.BLANK;

		if (!supportsDuplicatedIndexName) {
			indexNamePrefix = "TMP_";
		}

		Assert.assertTrue(
			dbInspector.hasIndex(_TABLE_NAME_2, indexNamePrefix + INDEX_NAME));

		Assert.assertArrayEquals(
			new String[] {dbInspector.normalizeName("id")},
			db.getPrimaryKeyColumnNames(connection, _TABLE_NAME_2));
	}

	@Test
	public void testGetIndexMetadatas() throws Exception {
		addIndex(new String[] {"typeVarchar", "typeBoolean"});

		List<IndexMetadata> indexMetadatas = ReflectionTestUtil.invoke(
			db, "getIndexMetadatas",
			new Class<?>[] {
				Connection.class, String.class, String.class, boolean.class
			},
			connection, TABLE_NAME_1, "typeVarchar", false);

		for (IndexMetadata indexMetadata : indexMetadatas) {
			Assert.assertEquals(
				dbInspector.normalizeName(INDEX_NAME),
				indexMetadata.getIndexName());
		}
	}

	@Test
	public void testGetPrimaryKeyColumnNames() throws Exception {
		db.runSQL(_SQL_CREATE_TABLE_2);

		Assert.assertArrayEquals(
			new String[] {
				dbInspector.normalizeName("id2"),
				dbInspector.normalizeName("id1")
			},
			db.getPrimaryKeyColumnNames(connection, _TABLE_NAME_2));
	}

	@Test
	public void testGetPrimaryKeyColumnNamesIncorrectOrder() throws Exception {
		db.runSQL(_SQL_CREATE_TABLE_2);

		Assert.assertFalse(
			Arrays.equals(
				new String[] {
					dbInspector.normalizeName("id1"),
					dbInspector.normalizeName("id2")
				},
				db.getPrimaryKeyColumnNames(connection, _TABLE_NAME_2)));
	}

	@Test
	public void testRenameTables() throws Exception {
		db.runSQL(_SQL_CREATE_TABLE_2);

		db.renameTables(
			connection, new ObjectValuePair<>(TABLE_NAME_1, _TABLE_NAME_3),
			new ObjectValuePair<>(_TABLE_NAME_2, TABLE_NAME_1),
			new ObjectValuePair<>(_TABLE_NAME_3, _TABLE_NAME_2));

		Assert.assertTrue(dbInspector.hasTable(TABLE_NAME_1));
		Assert.assertTrue(dbInspector.hasTable(_TABLE_NAME_2));

		Assert.assertTrue(dbInspector.hasColumn(TABLE_NAME_1, "id1"));
		Assert.assertTrue(dbInspector.hasColumn(_TABLE_NAME_2, "id"));
	}

	@Test
	public void testRenameTablesRollback() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.dao.db.BaseDB", LoggerTestUtil.OFF)) {

			db.renameTables(
				connection, new ObjectValuePair<>(TABLE_NAME_1, _TABLE_NAME_3),
				new ObjectValuePair<>(_TABLE_NAME_2, TABLE_NAME_1));

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(dbInspector.hasTable(TABLE_NAME_1));
			Assert.assertFalse(dbInspector.hasTable(_TABLE_NAME_2));
			Assert.assertFalse(dbInspector.hasTable(_TABLE_NAME_3));
		}
	}

	@Test
	public void testSyncTables() throws Exception {
		db.runSQL(
			StringBundler.concat(
				"insert into ", TABLE_NAME_1,
				" (id, notNilColumn, typeString) values (1, '1', ",
				"'testValueA')"));

		_createTestTable(_TABLE_NAME_2);

		db.runSQL(
			StringBundler.concat(
				"insert into ", _TABLE_NAME_2,
				" (id, notNilColumn, typeString) values (1, '1', ",
				"'testValueA')"));

		Map<String, String> columnNamesMap = new HashMap<>();

		for (String columnName : _SYNC_TABLES_COLUMN_NAMES) {
			columnNamesMap.put(columnName, columnName);
		}

		try (AutoCloseable autoCloseable = db.syncTables(
				connection, TABLE_NAME_1, _TABLE_NAME_2, columnNamesMap,
				HashMapBuilder.put(
					dbInspector.normalizeName("typeString"), "'test'"
				).build())) {

			db.runSQL(
				StringBundler.concat(
					"insert into ", TABLE_NAME_1,
					" (id, notNilColumn, typeString) values (2, '2', ",
					"'testValueB')"));

			db.runSQL(
				StringBundler.concat(
					"insert into ", TABLE_NAME_1,
					" (id, notNilColumn) values (3, '3')"));

			db.runSQL("delete from " + TABLE_NAME_1 + " where id = 1");

			db.runSQL(
				"update " + TABLE_NAME_1 +
					" set typeString = NULL where id = 2");
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from " + _TABLE_NAME_2 + " order by id");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(2, resultSet.getLong("id"));
			Assert.assertEquals("2", resultSet.getString("notNilColumn"));
			Assert.assertEquals("test", resultSet.getString("typeString"));

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(3, resultSet.getLong("id"));
			Assert.assertEquals("3", resultSet.getString("notNilColumn"));
			Assert.assertEquals("test", resultSet.getString("typeString"));

			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	public void testSyncTablesDifferentColumnNames() throws Exception {
		db.runSQL(
			StringBundler.concat(
				"create table ", _TABLE_NAME_2, " (id2 LONG not null primary ",
				"key, notNilColumn2 VARCHAR(75) not null, nilColumn2 ",
				"VARCHAR(75) null, typeBlob2 BLOB, typeBoolean2 BOOLEAN,",
				"typeDate2 DATE null, typeDouble2 DOUBLE, typeInteger2 ",
				"INTEGER, typeLong2 LONG null, typeLongDefault2 LONG default ",
				"10 not null, typeSBlob2 SBLOB, typeString2 STRING null, ",
				"typeText2 TEXT null, typeVarchar2 VARCHAR(75) null,",
				"typeVarcharDefault2 VARCHAR(10) default 'testValue' not ",
				"null);"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", TABLE_NAME_1,
				" (id, notNilColumn, typeString) values (1, '1', ",
				"'testValueA')"));

		db.runSQL(
			StringBundler.concat(
				"insert into ", _TABLE_NAME_2,
				" (id2, notNilColumn2, typeString2) values (1, '1', ",
				"'testValueA')"));

		Map<String, String> columnNamesMap = new HashMap<>();

		for (String columnName : _SYNC_TABLES_COLUMN_NAMES) {
			columnNamesMap.put(columnName, columnName + "2");
		}

		try (AutoCloseable autoCloseable = db.syncTables(
				connection, TABLE_NAME_1, _TABLE_NAME_2, columnNamesMap,
				HashMapBuilder.put(
					dbInspector.normalizeName("typeString2"), "'test'"
				).build())) {

			db.runSQL(
				StringBundler.concat(
					"insert into ", TABLE_NAME_1,
					" (id, notNilColumn, typeString) values (2, '2', ",
					"'testValueB')"));

			db.runSQL(
				StringBundler.concat(
					"insert into ", TABLE_NAME_1,
					" (id, notNilColumn) values (3, '3')"));

			db.runSQL("delete from " + TABLE_NAME_1 + " where id = 1");

			db.runSQL(
				"update " + TABLE_NAME_1 +
					" set typeString = NULL where id = 2");
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select * from " + _TABLE_NAME_2 + " order by id2");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(2, resultSet.getLong("id2"));
			Assert.assertEquals("2", resultSet.getString("notNilColumn2"));
			Assert.assertEquals("test", resultSet.getString("typeString2"));

			Assert.assertTrue(resultSet.next());
			Assert.assertEquals(3, resultSet.getLong("id2"));
			Assert.assertEquals("3", resultSet.getString("notNilColumn2"));
			Assert.assertEquals("test", resultSet.getString("typeString2"));

			Assert.assertFalse(resultSet.next());
		}
	}

	@Test
	public void testUpdatePrimaryKeyAddsMissingPrimaryKey() throws Exception {
		db.runSQL("create table DBTest (id INTEGER not null, column1 TEXT)");

		try (Connection connection = DataAccess.getConnection()) {
			db.updatePrimaryKey(connection, "DBTest", new String[] {"id"});

			Assert.assertTrue(
				ArrayUtil.equalsIgnoreCase(
					new String[] {"id"},
					db.getPrimaryKeyColumnNames(connection, "DBTest")));
		}
		finally {
			db.runSQL("DROP_TABLE_IF_EXISTS(DBTest)");
		}
	}

	@Test
	public void testUpdatePrimaryKeyChangesPrimaryKey() throws Exception {
		db.runSQL(
			"create table DBTest (id INTEGER not null, column1 LONG not null " +
				"primary key, column2 INTEGER not null)");

		try (Connection connection = DataAccess.getConnection()) {
			db.updatePrimaryKey(
				connection, "DBTest", new String[] {"id", "column2"});

			Assert.assertTrue(
				ArrayUtil.equalsIgnoreCase(
					new String[] {"id", "column2"},
					db.getPrimaryKeyColumnNames(connection, "DBTest")));
		}
		finally {
			db.runSQL("DROP_TABLE_IF_EXISTS(DBTest)");
		}
	}

	@Test
	public void testUpdatePrimaryKeyWithCTCollectionIdAddsPrimaryKey()
		throws Exception {

		db.runSQL(
			"create table DBTest (id INTEGER not null, ctCollectionId LONG " +
				"not null)");

		try (Connection connection = DataAccess.getConnection()) {
			db.updatePrimaryKey(
				connection, "DBTest", new String[] {"id", "ctCollectionId"});

			Assert.assertTrue(
				ArrayUtil.equalsIgnoreCase(
					new String[] {"id", "ctCollectionId"},
					db.getPrimaryKeyColumnNames(connection, "DBTest")));
		}
		finally {
			db.runSQL("DROP_TABLE_IF_EXISTS(DBTest)");
		}
	}

	@Test
	public void testUpdatePrimaryKeyWithoutCTCollectionIdAddsPrimaryKey()
		throws Exception {

		db.runSQL("create table DBTest (id INTEGER not null, column1 TEXT)");

		try (Connection connection = DataAccess.getConnection()) {
			db.updatePrimaryKey(
				connection, "DBTest", new String[] {"id", "ctCollectionId"});

			Assert.assertTrue(
				ArrayUtil.equalsIgnoreCase(
					new String[] {"id"},
					db.getPrimaryKeyColumnNames(connection, "DBTest")));
		}
		finally {
			db.runSQL("DROP_TABLE_IF_EXISTS(DBTest)");
		}
	}

	protected void addIndex(
		String tableName, String[] columnNames, boolean unique) {

		List<IndexMetadata> indexMetadatas = Arrays.asList(
			new IndexMetadata(INDEX_NAME, tableName, unique, columnNames));

		ReflectionTestUtil.invoke(
			db, "addIndexes", new Class<?>[] {Connection.class, List.class},
			connection, indexMetadatas);
	}

	protected void addIndex(String[] columnNames) {
		addIndex(TABLE_NAME_1, columnNames, false);
	}

	protected static final String INDEX_NAME = "IX_TEMP";

	protected static final String TABLE_NAME_1 = "DBTest1";

	protected static Connection connection;
	protected static DB db;
	protected static DBInspector dbInspector;

	private void _createTestTable(String tableName) throws Exception {
		db.runSQL(
			StringBundler.concat(
				"create table ", tableName, " (id LONG not null primary key, ",
				"notNilColumn VARCHAR(75) not null, nilColumn VARCHAR(75) ",
				"null , typeBlob BLOB, typeBoolean BOOLEAN, typeDate DATE ",
				"null, typeDouble DOUBLE, typeInteger INTEGER, typeLong LONG ",
				"null, typeLongDefault LONG default 10 not null, typeSBlob ",
				"SBLOB, typeString STRING null, typeText TEXT null, ",
				"typeVarchar VARCHAR(75) null, typeVarcharDefault VARCHAR(10) ",
				"default 'testValue' not null);"));
	}

	private List<IndexMetadata> _getIndexMetadatas(
		String tableName, String[] columnNames) {

		return ReflectionTestUtil.invoke(
			db, "getIndexMetadatas",
			new Class<?>[] {
				Connection.class, String.class, String.class, boolean.class
			},
			connection, tableName, columnNames[0], false);
	}

	private void _validateIndex(String[] columnNames) throws Exception {
		List<IndexMetadata> indexMetadatas = _getIndexMetadatas(
			TABLE_NAME_1, columnNames);

		Assert.assertEquals(
			indexMetadatas.toString(), 1, indexMetadatas.size());

		IndexMetadata indexMetadata = indexMetadatas.get(0);

		Assert.assertEquals(
			dbInspector.normalizeName(INDEX_NAME),
			indexMetadata.getIndexName());

		Assert.assertArrayEquals(
			ArrayUtil.sortedUnique(columnNames),
			ArrayUtil.sortedUnique(indexMetadata.getColumnNames()));
	}

	private static final String _SQL_CREATE_TABLE_2 =
		"create table " + DBTest._TABLE_NAME_2 +
			" (id1 LONG not null, id2 LONG not null, primary key (id2, id1))";

	private static final String[] _SYNC_TABLES_COLUMN_NAMES = {
		"id", "notNilColumn", "nilColumn", "typeBlob", "typeBoolean",
		"typeDate", "typeDouble", "typeInteger", "typeLong", "typeSBlob",
		"typeString", "typeText", "typeVarchar"
	};

	private static final String _TABLE_NAME_2 = "DBTest2";

	private static final String _TABLE_NAME_3 = "DBTest3";

}