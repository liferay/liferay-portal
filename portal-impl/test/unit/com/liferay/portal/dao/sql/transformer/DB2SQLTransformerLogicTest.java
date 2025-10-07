/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class DB2SQLTransformerLogicTest
	extends BaseSQLTransformerLogicTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	public DB2SQLTransformerLogicTest() {
		super(new TestDB(DBType.DB2, 1, 0));
	}

	@Override
	public String getDropTableIfExistsTextTransformedSQL() {
		return "BEGIN\nDECLARE CONTINUE HANDLER FOR SQLSTATE '42704'\nBEGIN " +
			"END;\nEXECUTE IMMEDIATE 'DROP TABLE Foo';\nEND";
	}

	@Override
	@Test
	public void testReplaceBitwiseCheckWithExtraWhitespace() {
		Assert.assertEquals(
			getBitwiseCheckTransformedSQL(),
			sqlTransformer.transform(getBitwiseCheckOriginalSQL()));
	}

	@Test
	public void testReplaceConcat() {
		Assert.assertEquals(
			"select * from Foo where foo LIKE CAST(bar AS VARCHAR(2000)) " +
				"CONCAT ?",
			sqlTransformer.transform(
				"select * from Foo where foo LIKE CONCAT(CAST_TEXT(bar),?)"));
	}

	@Override
	@Test
	public void testReplaceModWithExtraWhitespace() {
		Assert.assertEquals(
			getModTransformedSQL(),
			sqlTransformer.transform(getModOriginalSQL()));
	}

	@Test
	public void testReplaceQuestionMark() {
		_testReplaceQuestionMark("select foo from Foo where foo LIKE ?");
		_testReplaceQuestionMark("select foo, ?, bar, ? from Foo");
		_testReplaceQuestionMark(
			"select * from Foo where case when foo = ? then ? else ? end");
		_testReplaceQuestionMark(
			"select bar, ?, case when foo = ? then ? else ? end as columnA " +
				"from Foo");

		Assert.assertEquals(
			"select * from Foo where foo = ? And bar = ?",
			sqlTransformer.transform(
				"select * from Foo where foo = ? And bar = ?"));
		Assert.assertEquals(
			"select * from Foo where foo = \" ?\"",
			sqlTransformer.transform("select * from Foo where foo = \" ?\""));
		Assert.assertEquals(
			"select * from Foo where foo = \' ?\'",
			sqlTransformer.transform("select * from Foo where foo = \' ?\'"));
	}

	@Override
	protected String getBooleanTransformedSQL() {
		return "select * from Foo where foo = FALSE and bar = TRUE";
	}

	@Override
	protected String getCastClobTextTransformedSQL() {
		return StringBundler.concat(
			"select CAST(foo || (CAST(foo AS VARCHAR(2000)) || (bar || foo)) ",
			"AS VARCHAR(2000)), CAST(foo || (bar || foo) AS VARCHAR(2000)) ",
			"from Foo");
	}

	@Override
	protected String getCastTextTransformedSQL() {
		return StringBundler.concat(
			"select CAST(foo || (CAST(foo AS VARCHAR(2000)) || (bar || foo)) ",
			"AS VARCHAR(2000)), CAST(foo || (bar || foo) AS VARCHAR(2000)) ",
			"from Foo");
	}

	@Override
	protected String getIntegerDivisionTransformedSQL() {
		return "select foo / bar from Foo";
	}

	@Override
	protected String getNullDateTransformedSQL() {
		return "select NULL from Foo";
	}

	@Override
	protected String getTruncateTableTransformedSQL() {
		return super.getTruncateTableTransformedSQL() + " IMMEDIATE";
	}

	private void _testReplaceQuestionMark(String sql) {
		Assert.assertEquals(
			StringUtil.replace(
				sql, CharPool.QUESTION,
				"COALESCE(CAST(? AS VARCHAR(2000)),'')"),
			sqlTransformer.transform(sql));
	}

}