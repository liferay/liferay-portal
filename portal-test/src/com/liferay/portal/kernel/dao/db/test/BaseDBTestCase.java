/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.db.test;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DB;

import java.io.IOException;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Miguel Pastor
 * @author László Csontos
 */
public abstract class BaseDBTestCase {

	@Test
	public void testReplaceTemplate() throws IOException, SQLException {
		Assert.assertEquals(
			StringBundler.concat(
				"select * from SomeTable where someColumn1 = ",
				db.getTemplateFalse(), " and someColumn2 = ",
				db.getTemplateTrue(), StringPool.NEW_LINE),
			buildSQL(_BOOLEAN_LITERAL_QUERY));

		Assert.assertEquals(
			_BOOLEAN_PATTERN_QUERY + StringPool.NEW_LINE,
			buildSQL(_BOOLEAN_PATTERN_QUERY));
	}

	@Test
	public void testRewordAlterColumnNameWithType() throws Exception {
		try {
			buildSQL(
				"alter_column_name DLFolder userName userNameTest " +
					"VARCHAR(75) not null;");

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Invalid alter column name statement",
				illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testRewordAlterColumnTypeWithDefaultNull() throws Exception {
		try {
			buildSQL(
				"alter_column_type DLFolder userName VARCHAR(75) default " +
					"'test test' null;");

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals(
				"Invalid alter column type statement",
				illegalArgumentException.getMessage());
		}
	}

	protected String buildSQL(String query) throws IOException, SQLException {
		return db.buildSQL(query);
	}

	protected abstract DB getDB();

	protected static final String RENAME_TABLE_QUERY = "alter_table_name a b";

	protected final DB db = getDB();

	private static final String _BOOLEAN_LITERAL_QUERY =
		"select * from SomeTable where someColumn1 = FALSE and someColumn2 = " +
			"TRUE";

	private static final String _BOOLEAN_PATTERN_QUERY =
		"select * from SomeTable where someColumn1 = [$FALSE$] and " +
			"someColumn2 = [$TRUE$]";

}