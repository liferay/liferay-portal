/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.test.BaseDBTestCase;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Pastor
 * @author Alberto Chaparro
 */
public class DB2DBTest extends BaseDBTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_defaultAlter = null;
		_nullableAlter = null;
	}

	@Test
	public void testGetLongDefaultValue() {
		Assert.assertEquals("10", db.getDefaultValue("10"));
	}

	@Test
	public void testGetVarcharDefaultValue() {
		Assert.assertEquals("test", db.getDefaultValue("'test'"));
	}

	@Test
	public void testRewordAlterColumnType() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName set data type " +
				"varchar(75);\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75);"));
		Assert.assertEquals(
			"alter table DLFolder alter column userName drop default;",
			_defaultAlter);
		Assert.assertNull(_nullableAlter);
	}

	@Test
	public void testRewordAlterColumnTypeBigDecimal() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userId set data type " +
				"decimal(30, 16);\n",
			buildSQL("alter_column_type DLFolder userId BIGDECIMAL;"));
		Assert.assertEquals(
			"alter table DLFolder alter column userId drop default;",
			_defaultAlter);
		Assert.assertNull(_nullableAlter);
	}

	@Test
	public void testRewordAlterColumnTypeNoSemicolon() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName set data type " +
				"varchar(75);\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75)"));
		Assert.assertEquals(
			"alter table DLFolder alter column userName drop default;",
			_defaultAlter);
		Assert.assertNull(_nullableAlter);
	}

	@Test
	public void testRewordAlterColumnTypeNotNull() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName set data type " +
				"varchar(75);\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) not null;"));
		Assert.assertEquals(
			"alter table DLFolder alter column userName drop default;",
			_defaultAlter);
		Assert.assertEquals(
			"alter table DLFolder alter column userName set not null;",
			_nullableAlter);
	}

	@Test
	public void testRewordAlterColumnTypeNotNullUpperCase() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName set data type " +
				"varchar(75);\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) NOT NULL;"));
		Assert.assertEquals(
			"alter table DLFolder alter column userName drop default;",
			_defaultAlter);
		Assert.assertEquals(
			"alter table DLFolder alter column userName set not null;",
			_nullableAlter);
	}

	@Test
	public void testRewordAlterColumnTypeNull() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName set data type " +
				"varchar(75);\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) null;"));
		Assert.assertEquals(
			"alter table DLFolder alter column userName drop default;",
			_defaultAlter);
		Assert.assertNull(_nullableAlter);
	}

	@Test
	public void testRewordAlterColumnTypeNullUpperCase() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName set data type " +
				"varchar(75);\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) NULL;"));
		Assert.assertEquals(
			"alter table DLFolder alter column userName drop default;",
			_defaultAlter);
		Assert.assertNull(_nullableAlter);
	}

	@Test
	public void testRewordAlterColumnTypeWithDefault() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName set data type " +
				"varchar(75);\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) default " +
					"'test test' not null;"));
		Assert.assertEquals(
			"alter table DLFolder alter column userName set default 'test " +
				"test';",
			_defaultAlter);
		Assert.assertEquals(
			"alter table DLFolder alter column userName set not null;",
			_nullableAlter);
	}

	@Test
	public void testRewordRenameTable() throws Exception {
		Assert.assertEquals(
			"rename table a to b;\n", buildSQL(RENAME_TABLE_QUERY));
	}

	@Test
	public void testTextColumnType() throws Exception {
		Assert.assertEquals(
			"create table TestTable (largeColumnValue clob(2G))\n",
			buildSQL("create table TestTable (largeColumnValue TEXT)"));
	}

	@Override
	protected DB getDB() {
		return new DB2DB(0, 0) {

			@Override
			public void runSQL(String template) {
				if (StringUtil.count(template, "default") > 0) {
					_defaultAlter = template;
				}

				if (StringUtil.count(template, "null") > 0) {
					_nullableAlter = template;
				}
			}

			@Override
			protected boolean isNullable(String tableName, String columnName) {
				return true;
			}

		};
	}

	private String _defaultAlter;
	private String _nullableAlter;

}