/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.test.BaseDBTestCase;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shinn Lok
 * @author Alberto Chaparro
 */
public class OracleDBTest extends BaseDBTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testApplyMaxStringIndexLengthLimitation() throws Exception {
		DB db = getDB();

		Method method = ReflectionTestUtil.getMethod(
			db.getClass(), "replaceTemplate", String.class);

		PropsUtil.set(
			PropsKeys.DATABASE_STRING_INDEX_MAX_LENGTH + "[oracle]", "-1");

		Assert.assertEquals(
			"create index IX on Test (cola);",
			method.invoke(db, "create index IX on Test (cola);"));

		Assert.assertEquals(
			"create index IX on Test (cola);",
			method.invoke(
				db, "create index IX on Test (cola[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(db, "create index IX on Test (cola, colb);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(db, "create index IX on Test (cola, colb, colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb, " +
					"colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$], " +
					"colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb, " +
					"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$], colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb, " +
					"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$], " +
						"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$], " +
					"colc[$COLUMN_LENGTH:4000$]);"));

		PropsUtil.set(
			PropsKeys.DATABASE_STRING_INDEX_MAX_LENGTH + "[oracle]", "256");

		Assert.assertEquals(
			"create index IX on Test (cola);",
			method.invoke(db, "create index IX on Test (cola);"));

		Assert.assertEquals(
			"create index IX on Test (substr(cola, 1, 256));",
			method.invoke(
				db, "create index IX on Test (cola[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(db, "create index IX on Test (cola, colb);"));

		Assert.assertEquals(
			"create index IX on Test (substr(cola, 1, 256), colb);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb);"));

		Assert.assertEquals(
			"create index IX on Test (cola, substr(colb, 1, 256));",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola,substr(colb, 1, 256));",
			method.invoke(
				db,
				"create index IX on Test (cola,colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (substr(cola, 1, 256), substr(colb, 1, " +
				"256));",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (substr(cola, 1, 256),substr(colb, 1, " +
				"256));",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$]," +
					"colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(db, "create index IX on Test (cola, colb, colc);"));

		Assert.assertEquals(
			"create index IX on Test (substr(cola, 1, 256), colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb, " +
					"colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, substr(colb, 1, 256), colc);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$], " +
					"colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, substr(colc, 1, 256));",
			method.invoke(
				db,
				"create index IX on Test (cola, colb, " +
					"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (substr(cola, 1, 256), substr(colb, 1, " +
				"256), colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$], colc);"));

		Assert.assertEquals(
			"create index IX on Test (substr(cola, 1, 256), colb, " +
				"substr(colc, 1, 256));",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb, " +
					"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (substr(cola, 1, 256), substr(colb, 1, " +
				"256), substr(colc, 1, 256));",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$], " +
						"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, substr(colb, 1, 256), " +
				"substr(colc, 1, 256));",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$], " +
					"colc[$COLUMN_LENGTH:4000$]);"));

		PropsUtil.set(
			PropsKeys.DATABASE_STRING_INDEX_MAX_LENGTH + "[oracle]", "4000");

		Assert.assertEquals(
			"create index IX on Test (cola);",
			method.invoke(db, "create index IX on Test (cola);"));

		Assert.assertEquals(
			"create index IX on Test (cola);",
			method.invoke(
				db, "create index IX on Test (cola[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(db, "create index IX on Test (cola, colb);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola,colb);",
			method.invoke(
				db,
				"create index IX on Test (cola,colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola,colb);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$]," +
					"colb[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(db, "create index IX on Test (cola, colb, colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb, " +
					"colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$], " +
					"colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb, " +
					"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$], colc);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], colb, " +
					"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola[$COLUMN_LENGTH:4000$], " +
					"colb[$COLUMN_LENGTH:4000$], " +
						"colc[$COLUMN_LENGTH:4000$]);"));

		Assert.assertEquals(
			"create index IX on Test (cola, colb, colc);",
			method.invoke(
				db,
				"create index IX on Test (cola, colb[$COLUMN_LENGTH:4000$], " +
					"colc[$COLUMN_LENGTH:4000$]);"));
	}

	@Test
	public void testGetLongDefaultValue() {
		Assert.assertEquals("10", db.getDefaultValue("10 "));
	}

	@Test
	public void testGetVarcharDefaultValue() {
		Assert.assertEquals("test", db.getDefaultValue("'test'"));
	}

	@Test
	public void testRewordAlterColumnType() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75);"));
	}

	@Test
	public void testRewordAlterColumnTypeBigDecimal() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder modify userId decimal(30, 16) default null " +
				"null;\n",
			buildSQL("alter_column_type DLFolder userId BIGDECIMAL;"));
	}

	@Test
	public void testRewordAlterColumnTypeLowerCase() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null null;\n",
			buildSQL("alter_column_type DLFolder userName varchar(75);"));
	}

	@Test
	public void testRewordAlterColumnTypeNoSemicolon() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75)"));
	}

	@Test
	public void testRewordAlterColumnTypeNotNullWhenNotNull() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null;\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) not null;"));
	}

	@Test
	public void testRewordAlterColumnTypeNotNullWhenNotNullUpperCase()
		throws Exception {

		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null;\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) NOT NULL;"));
	}

	@Test
	public void testRewordAlterColumnTypeNotNullWhenNull() throws Exception {
		_nullable = true;

		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null not null;\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) not null;"));
	}

	@Test
	public void testRewordAlterColumnTypeNotNullWhenNullUpperCase()
		throws Exception {

		_nullable = true;

		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null not null;\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) NOT NULL;"));
	}

	@Test
	public void testRewordAlterColumnTypeNullWhenNotNull() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) null;"));
	}

	@Test
	public void testRewordAlterColumnTypeNullWhenNotNullUpperCase()
		throws Exception {

		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) NULL;"));
	}

	@Test
	public void testRewordAlterColumnTypeNullWhenNull() throws Exception {
		_nullable = true;

		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) null;"));
	}

	@Test
	public void testRewordAlterColumnTypeNullWhenNullUpperCase()
		throws Exception {

		_nullable = true;

		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) NULL;"));
	}

	@Test
	public void testRewordAlterColumnTypeString() throws Exception {
		Assert.assertEquals(
			"alter table BlogsEntry modify description VARCHAR2(4000 CHAR) " +
				"default null null;\n",
			buildSQL("alter_column_type BlogsEntry description STRING;"));
	}

	@Test
	public void testRewordAlterColumnTypeWithDefaultWhenNotNull()
		throws Exception {

		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"'test test';\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) default " +
					"'test test' not null;"));
	}

	@Test
	public void testRewordAlterColumnTypeWithDefaultWhenNull()
		throws Exception {

		_nullable = true;

		Assert.assertEquals(
			"alter table DLFolder modify userName VARCHAR2(75 CHAR) default " +
				"'test test' not null;\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) default " +
					"'test test' not null;"));
	}

	@Override
	protected DB getDB() {
		return new OracleDB(0, 0) {

			@Override
			protected boolean isNullable(String tableName, String columnName) {
				return _nullable;
			}

		};
	}

	private boolean _nullable;

}