/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.ClassRule;
import org.junit.Rule;

/**
 * @author Manuel de la Peña
 */
public class SQLServerSQLTransformerLogicTest
	extends BaseSQLTransformerLogicTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	public SQLServerSQLTransformerLogicTest() {
		super(new TestSQLServerDB(1, 0));
	}

	@Override
	public String getDropTableIfExistsTextTransformedSQL() {
		return "IF OBJECT_ID('Foo', 'U') IS NOT NULL DROP TABLE Foo";
	}

	@Override
	protected String getBitwiseCheckTransformedSQL() {
		return "select (foo & bar) from Foo";
	}

	@Override
	protected String getBitwiseOrTransformedSQL() {
		return "select (foo | bar) from Foo";
	}

	@Override
	protected String getBooleanTransformedSQL() {
		return "select * from Foo where foo = 0 and bar = 1";
	}

	@Override
	protected String getCastClobTextTransformedSQL() {
		return StringBundler.concat(
			"select CAST(foo || (CAST(foo AS NVARCHAR(MAX)) || (bar || foo)) ",
			"AS NVARCHAR(MAX)), CAST(foo || (bar || foo) AS NVARCHAR(MAX)) ",
			"from Foo");
	}

	@Override
	protected String getCastTextTransformedSQL() {
		return StringBundler.concat(
			"select CAST(foo || (CAST(foo AS NVARCHAR(MAX)) || (bar || foo)) ",
			"AS NVARCHAR(MAX)), CAST(foo || (bar || foo) AS NVARCHAR(MAX)) ",
			"from Foo");
	}

	@Override
	protected String getIntegerDivisionTransformedSQL() {
		return "select foo / bar from Foo";
	}

	@Override
	protected String getModTransformedSQL() {
		return "select foo % bar from Foo";
	}

	@Override
	protected String getNullDateTransformedSQL() {
		return "select NULL from Foo";
	}

	@Override
	protected String getSubstrOriginalSQL() {
		return "select SUBSTR(foo, 1, 1) from Foo";
	}

	@Override
	protected String getSubstrTransformedSQL() {
		return "select SUBSTRING(foo, 1, 1) from Foo";
	}

	private static final class TestSQLServerDB extends TestDB {

		public TestSQLServerDB(int majorVersion, int minorVersion) {
			super(DBType.SQLSERVER, majorVersion, minorVersion);
		}

		@Override
		protected String[] getTemplate() {
			return new String[] {
				"NOOP", "1", "0", "NOOP", "NOOP", " NOOP", " NOOP", " NOOP",
				" NOOP", " NOOP", " NOOP", " NOOP", " NOOP", " NOOP", " NOOP",
				" NOOP", "NOOP", "NOOP"
			};
		}

	}

}