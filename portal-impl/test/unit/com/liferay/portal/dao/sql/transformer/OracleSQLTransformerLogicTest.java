/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBType;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class OracleSQLTransformerLogicTest
	extends BaseSQLTransformerLogicTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	public OracleSQLTransformerLogicTest() {
		super(new TestDB(DBType.ORACLE, 1, 0));
	}

	@Override
	public String getDropTableIfExistsTextTransformedSQL() {
		return "BEGIN\nEXECUTE IMMEDIATE 'DROP TABLE Foo';\nEXCEPTION\nWHEN " +
			"OTHERS THEN\nIF SQLCODE != -942 THEN\nRAISE;\nEND IF;\nEND;\n/";
	}

	@Override
	@Test
	public void testReplaceBitwiseCheckWithExtraWhitespace() {
		Assert.assertEquals(
			getBitwiseCheckTransformedSQL(),
			sqlTransformer.transform(getBitwiseCheckOriginalSQL()));
	}

	@Test
	public void testReplaceEscape() {
		Assert.assertEquals(
			"select foo from Foo where foo LIKE ? ESCAPE '\\'",
			sqlTransformer.transform("select foo from Foo where foo LIKE ?"));
	}

	@Override
	@Test
	public void testReplaceModWithExtraWhitespace() {
		Assert.assertEquals(
			getModTransformedSQL(),
			sqlTransformer.transform(getModOriginalSQL()));
	}

	@Test
	public void testReplaceNotEqualsBlankString() {
		Assert.assertEquals(
			"select * from Foo where foo IS NOT NULL",
			sqlTransformer.transform("select * from Foo where foo != ''"));
	}

	@Override
	protected String getBitwiseOrTransformedSQL() {
		return "select (foo + bar - BITAND(foo, bar)) from Foo";
	}

	@Override
	protected String getBooleanTransformedSQL() {
		return "select * from Foo where foo = FALSE and bar = TRUE";
	}

	@Override
	protected String getCastClobTextTransformedSQL() {
		return StringBundler.concat(
			"select DBMS_LOB.SUBSTR(foo || (DBMS_LOB.SUBSTR(foo, 4000, 1) || ",
			"(bar || foo)), 4000, 1), DBMS_LOB.SUBSTR(foo || (bar || foo), ",
			"4000, 1) from Foo");
	}

	@Override
	protected String getCastTextTransformedSQL() {
		return StringBundler.concat(
			"select CAST(foo || (CAST(foo AS VARCHAR(4000)) || (bar || foo)) ",
			"AS VARCHAR(4000)), CAST(foo || (bar || foo) AS VARCHAR(4000)) ",
			"from Foo");
	}

	@Override
	protected String getIntegerDivisionTransformedSQL() {
		return "select TRUNC(foo / bar) from Foo";
	}

	@Override
	protected String getNullDateTransformedSQL() {
		return "select NULL from Foo";
	}

}