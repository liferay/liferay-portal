/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alberto Chaparro
 */
@RunWith(Arquillian.class)
public class DBInspectorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_connection = DataAccess.getConnection();

		_dbInspector = new DBInspector(_connection);

		_db = DBManagerUtil.getDB();

		_db.runSQL(
			StringBundler.concat(
				"create table ", _TABLE_NAME, " (id LONG not null primary ",
				"key, notNilColumn VARCHAR(75) not null, nilColumn ",
				"VARCHAR(75) null, typeBigDecimal BIGDECIMAL, typeBlob BLOB, ",
				"typeBoolean BOOLEAN, typeDate DATE null, typeDouble DOUBLE, ",
				"typeInteger INTEGER, typeLong LONG null, typeLongDefault ",
				"LONG default 10 not null, typeSBlob SBLOB, typeString STRING ",
				"null, typeText TEXT null, typeVarchar VARCHAR(75) null, ",
				"typeVarcharDefault VARCHAR(10) default 'testValue' not ",
				"null)"));
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_db.runSQL("drop table " + _TABLE_NAME);

		DataAccess.cleanUp(_connection);
	}

	@Test
	public void testHasColumn() throws Exception {
		Assert.assertTrue(_dbInspector.hasColumn(_TABLE_NAME, _COLUMN_NAME));
	}

	@Test
	public void testHasColumnLowerCase() throws Exception {
		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		Assume.assumeTrue(databaseMetaData.storesLowerCaseIdentifiers());

		Assert.assertTrue(
			_dbInspector.hasColumn(
				_TABLE_NAME, StringUtil.toLowerCase(_COLUMN_NAME)));
	}

	@Test
	public void testHasColumnNonexistent() throws Exception {
		Assert.assertTrue(
			!_dbInspector.hasColumn(_TABLE_NAME, _COLUMN_NAME_NONEXISTING));
	}

	@Test
	public void testHasColumnTypeBigDecimal() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeBigDecimal", "BIGDECIMAL"));
	}

	@Test
	public void testHasColumnTypeBlob() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeBlob", "BLOB null"));
	}

	@Test
	public void testHasColumnTypeBoolean() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeBoolean", "BOOLEAN null"));
	}

	@Test
	public void testHasColumnTypeDate() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeDate", "DATE null"));
	}

	@Test
	public void testHasColumnTypeDouble() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeDouble", "DOUBLE null"));
	}

	@Test
	public void testHasColumnTypeInteger() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeInteger", "INTEGER null"));
	}

	@Test
	public void testHasColumnTypeLong() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeLong", "LONG null"));
	}

	@Test
	public void testHasColumnTypeLongDefaultNotNull() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeLongDefault", "LONG default 10 not null"));
		Assert.assertFalse(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeLongDefault", "LONG default 15 not null"));
	}

	@Test
	public void testHasColumnTypeSBlob() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeSBlob", "SBLOB null"));
	}

	@Test
	public void testHasColumnTypeString() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeString", "STRING null"));
	}

	@Test
	public void testHasColumnTypeText() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(_TABLE_NAME, "typeText", "TEXT null"));
	}

	@Test
	public void testHasColumnTypeVarchar() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeVarchar", "VARCHAR(75) null"));
	}

	@Test
	public void testHasColumnTypeVarcharDefaultNotNull() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeVarcharDefault",
				"VARCHAR(10) default 'testValue' not null"));

		Assert.assertFalse(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeVarcharDefault",
				"VARCHAR(10) default 'notTestValue' not null"));
	}

	@Test
	public void testHasColumnUpperCase() throws Exception {
		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		Assume.assumeTrue(databaseMetaData.storesUpperCaseIdentifiers());

		Assert.assertTrue(
			_dbInspector.hasColumn(
				_TABLE_NAME, StringUtil.toUpperCase(_COLUMN_NAME)));
	}

	@Test
	public void testHasNotNullColumnTypeNotNull() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "notNilColumn", "VARCHAR(75) not null"));
	}

	@Test
	public void testHasNotNullColumnTypeNull() throws Exception {
		Assert.assertFalse(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "notNilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testHasNullColumnTypeNotNull() throws Exception {
		Assert.assertFalse(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "nilColumn", "VARCHAR(75) not null"));
	}

	@Test
	public void testHasNullColumnTypeNull() throws Exception {
		Assert.assertTrue(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "nilColumn", "VARCHAR(75) null"));
	}

	@Test
	public void testHasTable() throws Exception {
		Assert.assertTrue(_dbInspector.hasTable(_TABLE_NAME));
	}

	@Test
	public void testHasTableLowerCase() throws Exception {
		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		Assume.assumeTrue(databaseMetaData.storesLowerCaseIdentifiers());

		Assert.assertTrue(
			_dbInspector.hasTable(StringUtil.toLowerCase(_TABLE_NAME)));
	}

	@Test
	public void testHasTableNonexistent() throws Exception {
		Assert.assertFalse(_dbInspector.hasTable(_TABLE_NAME_NONEXISTING));
	}

	@Test
	public void testHasTableUpperCase() throws Exception {
		DatabaseMetaData databaseMetaData = _connection.getMetaData();

		Assume.assumeTrue(databaseMetaData.storesUpperCaseIdentifiers());

		Assert.assertTrue(
			_dbInspector.hasTable(StringUtil.toUpperCase(_TABLE_NAME)));
	}

	@Test
	public void testIsNotNullColumnNullable() throws Exception {
		Assert.assertTrue(_dbInspector.isNullable(_TABLE_NAME, "nilColumn"));
	}

	@Test
	public void testIsNullableColumnNullable() throws Exception {
		Assert.assertFalse(
			_dbInspector.isNullable(_TABLE_NAME, "notNilColumn"));
	}

	@Test
	public void testNotHasColumnTypeString() throws Exception {
		Assert.assertFalse(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeVarchar", "STRING null"));
	}

	@Test
	public void testNotHasColumnTypeText() throws Exception {
		Assert.assertFalse(
			_dbInspector.hasColumnType(
				_TABLE_NAME, "typeVarchar", "TEXT null"));
	}

	private static final String _COLUMN_NAME = "id";

	private static final String _COLUMN_NAME_NONEXISTING = "nonexistentColumn";

	private static final String _TABLE_NAME = "DBInspectorTest";

	private static final String _TABLE_NAME_NONEXISTING = "NonexistentTable";

	private static Connection _connection;
	private static DB _db;
	private static DBInspector _dbInspector;

}