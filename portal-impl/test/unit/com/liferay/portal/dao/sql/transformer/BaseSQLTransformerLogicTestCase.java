/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.dao.db.DBManagerImpl;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 * @author Brian Wing Shun Chan
 */
public abstract class BaseSQLTransformerLogicTestCase {

	public BaseSQLTransformerLogicTestCase(DB db) {
		_db = db;
	}

	@Before
	public void setUp() {
		DBManagerImpl dbManagerImpl = new DBManagerImpl();

		dbManagerImpl.setDB(_db);

		DBManagerUtil.setDBManager(dbManagerImpl);

		SQLTransformer.reloadSQLTransformer();
	}

	@Test
	public void testReplaceAggregation() {
		Assert.assertEquals(
			getAggregationTransformedSQL(),
			SQLTransformer.transform(getAggregationOriginalSQL()));
	}

	@Test
	public void testReplaceBitwiseCheck() {
		Assert.assertEquals(
			getBitwiseCheckTransformedSQL(),
			SQLTransformer.transform(getBitwiseCheckOriginalSQL()));
	}

	@Test
	public void testReplaceBitwiseCheckWithExtraWhitespace() {
		Assert.assertEquals(
			getBitwiseCheckTransformedSQL(),
			SQLTransformer.transform(
				_addExtraWhitespaceFunction.apply(
					getBitwiseCheckOriginalSQL())));
	}

	@Test
	public void testReplaceBitwiseOr() {
		Assert.assertEquals(
			getBitwiseOrTransformedSQL(),
			SQLTransformer.transform(getBitwiseOrOriginalSQL()));
	}

	@Test
	public void testReplaceBoolean() {
		Assert.assertEquals(
			getBooleanTransformedSQL(),
			SQLTransformer.transform(getBooleanOriginalSQL()));
	}

	@Test
	public void testReplaceCastClobText() {
		Assert.assertEquals(
			getCastClobTextTransformedSQL(),
			SQLTransformer.transform(getCastClobTextOriginalSQL()));
	}

	@Test
	public void testReplaceCastDecimal() {
		Assert.assertEquals(
			getCastDecimalTransformedSQL(),
			SQLTransformer.transform(getCastDecimalOriginalSQL()));
	}

	@Test
	public void testReplaceCastLong() {
		Assert.assertEquals(
			getCastLongTransformedSQL(),
			SQLTransformer.transform(getCastLongOriginalSQL()));
	}

	@Test
	public void testReplaceCastText() {
		Assert.assertEquals(
			getCastTextTransformedSQL(),
			SQLTransformer.transform(getCastTextOriginalSQL()));
	}

	@Test
	public void testReplaceCrossJoin() {
		Assert.assertEquals(
			getCrossJoinTransformedSQL(),
			SQLTransformer.transform(getCrossJoinOriginalSQL()));
	}

	@Test
	public void testReplaceDropTableIfExistsText() {
		Assert.assertEquals(
			getDropTableIfExistsTextTransformedSQL(),
			SQLTransformer.transform(getDropTableIfExistsTextOriginalSQL()));
	}

	@Test
	public void testReplaceInstr() {
		Assert.assertEquals(
			getInstrTransformedSQL(),
			SQLTransformer.transform(getInstrOriginalSQL()));
	}

	@Test
	public void testReplaceInstrWithPostColumnModificator() {
		Assert.assertEquals(
			getInstrWithPostColumnModificatorTransformedSQL(),
			SQLTransformer.transform(
				getInstrWithPostColumnModificatorOriginalSQL()));
	}

	@Test
	public void testReplaceInstrWithPreColumnModificator() {
		Assert.assertEquals(
			getInstrWithPreColumnModificatorTransformedSQL(),
			SQLTransformer.transform(
				getInstrWithPreColumnModificatorOriginalSQL()));
	}

	@Test
	public void testReplaceIntegerDivision() {
		Assert.assertEquals(
			getIntegerDivisionTransformedSQL(),
			SQLTransformer.transform(getIntegerDivisionOriginalSQL()));
	}

	@Test
	public void testReplaceIntegerDivisionWithExtraWhitespace() {
		Assert.assertEquals(
			getIntegerDivisionTransformedSQL(),
			SQLTransformer.transform(
				_addExtraWhitespaceFunction.apply(
					getIntegerDivisionOriginalSQL())));
	}

	@Test
	public void testReplaceMod() {
		Assert.assertEquals(
			getModTransformedSQL(),
			SQLTransformer.transform(getModOriginalSQL()));
	}

	@Test
	public void testReplaceModWithExtraWhitespace() {
		Assert.assertEquals(
			getModTransformedSQL(),
			SQLTransformer.transform(
				_addExtraWhitespaceFunction.apply(getModOriginalSQL())));
	}

	@Test
	public void testReplaceNullDate() {
		Assert.assertEquals(
			getNullDateTransformedSQL(),
			SQLTransformer.transform(getNullDateOriginalSQL()));
	}

	@Test
	public void testReplaceReplace() {
		Assert.assertEquals(
			getReplaceTransformedSQL(),
			SQLTransformer.transform(getReplaceOriginalSQL()));
	}

	@Test
	public void testReplaceSubstr() {
		Assert.assertEquals(
			getSubstrTransformedSQL(),
			SQLTransformer.transform(getSubstrOriginalSQL()));
	}

	@Test
	public void testReplaceSubstrWithExtraWhitespace() {
		Assert.assertEquals(
			getSubstrTransformedSQL(),
			SQLTransformer.transform(
				_addExtraWhitespaceFunction.apply(getSubstrOriginalSQL())));
	}

	@Test
	public void testTransform() {
		String sql = "select * from Foo";

		Assert.assertEquals(sql, SQLTransformer.transform(sql));
	}

	@Test
	public void testTruncateTable() {
		Assert.assertEquals(
			getTruncateTableTransformedSQL(),
			SQLTransformer.transform(getTruncateTableOriginalSQL()));
	}

	protected String getAggregationOriginalSQL() {
		return "select foo from Foo order by AGGREGATION_STRING_MIN(foo)";
	}

	protected String getAggregationTransformedSQL() {
		return "select foo from Foo order by MIN(foo)";
	}

	protected String getBitwiseCheckOriginalSQL() {
		return "select BITAND(foo, bar) from Foo";
	}

	protected String getBitwiseCheckTransformedSQL() {
		return getBitwiseCheckOriginalSQL();
	}

	protected String getBitwiseOrOriginalSQL() {
		return "select BITOR(foo, bar) from Foo";
	}

	protected String getBitwiseOrTransformedSQL() {
		return getBitwiseOrOriginalSQL();
	}

	protected String getBooleanOriginalSQL() {
		return "select * from Foo where foo = [$FALSE$] and bar = [$TRUE$]";
	}

	protected String getBooleanTransformedSQL() {
		return "select * from Foo where foo = false and bar = true";
	}

	protected String getCastClobTextOriginalSQL() {
		return "select CAST_CLOB_TEXT(foo || (CAST_CLOB_TEXT(foo) || (bar || " +
			"foo))), CAST_CLOB_TEXT(foo || (bar || foo)) from Foo";
	}

	protected String getCastClobTextTransformedSQL() {
		return "select foo || (foo || (bar || foo)), foo || (bar || foo) " +
			"from Foo";
	}

	protected String getCastDecimalOriginalSQL() {
		return "select CAST_DECIMAL(1 + (CAST_DECIMAL(foo) - (bar x 2))), " +
			"CAST_DECIMAL(foo + (bar x 3)) from Foo";
	}

	protected String getCastDecimalTransformedSQL() {
		return "select CAST(1 + (CAST(foo AS DECIMAL(31, 2)) - (bar x 2)) AS " +
			"DECIMAL(31, 2)), CAST(foo + (bar x 3) AS DECIMAL(31, 2)) from Foo";
	}

	protected String getCastLongOriginalSQL() {
		return "select CAST_LONG(1 + (CAST_LONG(foo) - (bar x 2))), " +
			"CAST_LONG(foo + (bar x 3)) from Foo";
	}

	protected String getCastLongTransformedSQL() {
		return "select 1 + (foo - (bar x 2)), foo + (bar x 3) from Foo";
	}

	protected String getCastTextOriginalSQL() {
		return "select CAST_TEXT(foo || (CAST_TEXT(foo) || (bar || foo))), " +
			"CAST_TEXT(foo || (bar || foo)) from Foo";
	}

	protected String getCastTextTransformedSQL() {
		return "select foo || (foo || (bar || foo)), foo || (bar || foo) " +
			"from Foo";
	}

	protected String getCrossJoinOriginalSQL() {
		return "select * from Foo CROSS JOIN Bar";
	}

	protected String getCrossJoinTransformedSQL() {
		return getCrossJoinOriginalSQL();
	}

	protected String getDropTableIfExistsTextOriginalSQL() {
		return "DROP_TABLE_IF_EXISTS(Foo)";
	}

	protected abstract String getDropTableIfExistsTextTransformedSQL();

	protected String getInstrOriginalSQL() {
		return "select INSTR(foo, 'fooText') from Foo";
	}

	protected String getInstrTransformedSQL() {
		return getInstrOriginalSQL();
	}

	protected String getInstrWithPostColumnModificatorOriginalSQL() {
		return "select INSTR(foo COLLATE Latin1_General_100_BIN2, CHR(10)) " +
			"from Foo";
	}

	protected String getInstrWithPostColumnModificatorTransformedSQL() {
		return getInstrWithPostColumnModificatorOriginalSQL();
	}

	protected String getInstrWithPreColumnModificatorOriginalSQL() {
		return "select INSTR(BINARY foo, CHAR(10)) from Foo";
	}

	protected String getInstrWithPreColumnModificatorTransformedSQL() {
		return getInstrWithPreColumnModificatorOriginalSQL();
	}

	protected String getIntegerDivisionOriginalSQL() {
		return "select INTEGER_DIV(foo, bar) from Foo";
	}

	protected abstract String getIntegerDivisionTransformedSQL();

	protected String getModOriginalSQL() {
		return "select MOD(foo, bar) from Foo";
	}

	protected String getModTransformedSQL() {
		return getModOriginalSQL();
	}

	protected String getNullDateOriginalSQL() {
		return "select [$NULL_DATE$] from Foo";
	}

	protected abstract String getNullDateTransformedSQL();

	protected String getReplaceOriginalSQL() {
		return "select replace(foo) from Foo";
	}

	protected String getReplaceTransformedSQL() {
		return getReplaceOriginalSQL();
	}

	protected String getSubstrOriginalSQL() {
		return "select SUBSTR(foo) from Foo";
	}

	protected String getSubstrTransformedSQL() {
		return getSubstrOriginalSQL();
	}

	protected String getTruncateTableOriginalSQL() {
		return "truncate table Foo";
	}

	protected String getTruncateTableTransformedSQL() {
		return "TRUNCATE TABLE Foo";
	}

	private final Function<String, String> _addExtraWhitespaceFunction =
		(String sql) -> StringUtil.replace(sql, CharPool.COMMA, "   ,   ");
	private final DB _db;

}