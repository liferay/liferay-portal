/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.dao.db.DBManagerImpl;
import com.liferay.portal.dao.db.HypersonicDB;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class HibernateSQLTransformerLogicTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		DBManagerImpl dbManagerImpl = new DBManagerImpl();

		dbManagerImpl.setDB(new HypersonicDB(1, 0));

		DBManagerUtil.setDBManager(dbManagerImpl);

		SQLTransformer.reloadSQLTransformer();
	}

	@Test
	public void testReplaceBoolean() {
		Assert.assertEquals(
			"select * from Foo where foo = false and bar = true",
			SQLTransformer.transformForHibernate(
				"select * from Foo where foo = [$FALSE$] and bar = [$TRUE$]"));
	}

	@Test
	public void testReplaceCastClobText() {
		Assert.assertEquals(
			"select SUBSTRING(foo || (SUBSTRING(foo, 1, 4000) || (bar || " +
				"foo)), 1, 4000), SUBSTRING(foo || (bar || foo), 1, 4000) " +
					"from Foo",
			SQLTransformer.transformForHibernate(
				"select CAST_CLOB_TEXT(foo || (CAST_CLOB_TEXT(foo) || (bar " +
					"|| foo))), CAST_CLOB_TEXT(foo || (bar || foo)) from Foo"));
	}

	@Test
	public void testReplaceCastDecimal() {
		Assert.assertEquals(
			"select CAST(1 + (CAST(foo AS big_decimal) - (bar x 2)) AS " +
				"big_decimal), CAST(foo + (bar x 3) AS big_decimal) from Foo",
			SQLTransformer.transformForHibernate(
				"select CAST_DECIMAL(1 + (CAST_DECIMAL(foo) - (bar x 2))), " +
					"CAST_DECIMAL(foo + (bar x 3)) from Foo"));
	}

	@Test
	public void testReplaceCastLong() {
		Assert.assertEquals(
			"select CAST(1 + (CAST(foo AS long) - (bar x 2)) AS long), " +
				"CAST(foo + (bar x 3) AS long) from Foo",
			SQLTransformer.transformForHibernate(
				"select CAST_LONG(1 + (CAST_LONG(foo) - (bar x 2))), " +
					"CAST_LONG(foo + (bar x 3)) from Foo"));
	}

	@Test
	public void testReplaceCastText() {
		Assert.assertEquals(
			"select CAST(foo || (CAST(foo AS string) || (bar || foo)) AS " +
				"string), CAST(foo || (bar || foo) AS string) from Foo",
			SQLTransformer.transformForHibernate(
				"select CAST_TEXT(foo || (CAST_TEXT(foo) || (bar || foo))), " +
					"CAST_TEXT(foo || (bar || foo)) from Foo"));
	}

	@Test
	public void testReplaceCount() {
		Assert.assertEquals(
			"SELECT COUNT(*) FROM Foo foo",
			SQLTransformer.transformForHibernate(
				"SELECT COUNT(foo) FROM Foo foo"));
	}

	@Test
	public void testReplaceCountWithIncorrectAlias() {
		String sql = "SELECT COUNT(bar) FROM Foo foo";

		Assert.assertEquals(sql, SQLTransformer.transformForHibernate(sql));
	}

	@Test
	public void testReplaceCountWithNoCount() {
		String sql = "SELECT * FROM Foo where foo != 1";

		Assert.assertEquals(sql, SQLTransformer.transformForHibernate(sql));
	}

	@Test
	public void testReplaceInstr() {
		Assert.assertEquals(
			"select LOCATE('fooText', foo) from Foo",
			SQLTransformer.transformForHibernate(
				"select INSTR(foo, 'fooText') from Foo"));
	}

	@Test
	public void testReplaceInstrWithPostColumnModificator() {
		Assert.assertEquals(
			"select LOCATE(CHR(10), foo COLLATE Latin1_General_100_BIN2) " +
				"from Foo",
			SQLTransformer.transformForHibernate(
				"select INSTR(foo COLLATE Latin1_General_100_BIN2, CHR(10)) " +
					"from Foo"));
	}

	@Test
	public void testReplaceInstrWithPreColumnModificator() {
		Assert.assertEquals(
			"select LOCATE(CHAR(10), BINARY foo) from Foo",
			SQLTransformer.transformForHibernate(
				"select INSTR(BINARY foo, CHAR(10)) from Foo"));
	}

	@Test
	public void testReplaceNullDate() {
		Assert.assertEquals(
			"select NULL from Foo",
			SQLTransformer.transformForHibernate(
				"select [$NULL_DATE$] from Foo"));
	}

	@Test
	public void testReplacePositionalParameters() {
		Assert.assertEquals(
			"select * from Foo where a = ?1 and b = ?2 and c = ?3",
			SQLTransformer.transformForHibernate(
				"select * from Foo where a = ? and b = ? and c = ?"));
	}

	@Test
	public void testReplacePositionalParametersWithNoParameters() {
		String sql = "select * from Foo where a = 1";

		Assert.assertEquals(sql, SQLTransformer.transformForHibernate(sql));
	}

	@Test
	public void testReplacePositionalParametersWithQuotedQuestionMark() {
		Assert.assertEquals(
			"select * from Foo where a = '?' and b = ?1",
			SQLTransformer.transformForHibernate(
				"select * from Foo where a = '?' and b = ?"));
	}

	@Test
	public void testReplaceSubstr() {
		Assert.assertEquals(
			"select SUBSTRING(foo, 1, 10) from Foo",
			SQLTransformer.transformForHibernate(
				"select SUBSTR(foo, 1, 10) from Foo"));
	}

	@Test
	public void testUnsupportedAggregation() {
		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> SQLTransformer.transformForHibernate(
				"select foo from Foo order by AGGREGATION_STRING_MIN(foo)"));
	}

	@Test
	public void testUnsupportedBitwiseCheck() {
		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> SQLTransformer.transformForHibernate(
				"select BITAND(foo, bar) from Foo"));
	}

	@Test
	public void testUnsupportedBitwiseOr() {
		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> SQLTransformer.transformForHibernate(
				"select BITOR(foo, bar) from Foo"));
	}

	@Test
	public void testUnsupportedDropTableIfExists() {
		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> SQLTransformer.transformForHibernate(
				"DROP_TABLE_IF_EXISTS(Foo)"));
	}

	@Test
	public void testUnsupportedIntegerDivision() {
		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> SQLTransformer.transformForHibernate(
				"select INTEGER_DIV(foo, bar) from Foo"));
	}

	@Test
	public void testUnsupportedTruncateTable() {
		Assert.assertThrows(
			UnsupportedOperationException.class,
			() -> SQLTransformer.transformForHibernate("truncate table Foo"));
	}

}