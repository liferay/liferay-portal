/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.db;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.test.BaseDBTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Miguel Pastor
 * @author Alberto Chaparro
 */
public class HypersonicDBTest extends BaseDBTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRewordAlterColumnType() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);alter " +
				"table DLFolder alter column userName set default null;alter " +
					"table DLFolder alter column userName set null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75);"));
	}

	@Test
	public void testRewordAlterColumnTypeBigDecimal() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userId decimal(30, 16);alter " +
				"table DLFolder alter column userId set default null;alter " +
					"table DLFolder alter column userId set null;\n",
			buildSQL("alter_column_type DLFolder userId BIGDECIMAL;"));
	}

	@Test
	public void testRewordAlterColumnTypeNoSemicolon() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);alter " +
				"table DLFolder alter column userName set default null;alter " +
					"table DLFolder alter column userName set null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75)"));
	}

	@Test
	public void testRewordAlterColumnTypeNotNull() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);alter " +
				"table DLFolder alter column userName set default null;alter " +
					"table DLFolder alter column userName set not null;\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) not null;"));
	}

	@Test
	public void testRewordAlterColumnTypeNotNullUpperCase() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);alter " +
				"table DLFolder alter column userName set default null;alter " +
					"table DLFolder alter column userName set not null;\n",
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) NOT NULL;"));
	}

	@Test
	public void testRewordAlterColumnTypeNull() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);alter " +
				"table DLFolder alter column userName set default null;alter " +
					"table DLFolder alter column userName set null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) null;"));
	}

	@Test
	public void testRewordAlterColumnTypeNullUpperCase() throws Exception {
		Assert.assertEquals(
			"alter table DLFolder alter column userName varchar(75);alter " +
				"table DLFolder alter column userName set default null;alter " +
					"table DLFolder alter column userName set null;\n",
			buildSQL("alter_column_type DLFolder userName VARCHAR(75) NULL;"));
	}

	@Test
	public void testRewordAlterColumnTypeWithDefault() throws Exception {
		Assert.assertEquals(
			StringBundler.concat(
				"alter table DLFolder alter column userName varchar(75);alter ",
				"table DLFolder alter column userName set default 'test test';",
				"alter table DLFolder alter column userName set not null;\n"),
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) default " +
					"'test test' not null;"));
	}

	@Test
	public void testRewordRenameTable() throws Exception {
		Assert.assertEquals(
			"alter table a rename to b;\n", buildSQL(RENAME_TABLE_QUERY));
	}

	@Override
	protected DB getDB() {
		return new HypersonicDB(0, 0);
	}

}