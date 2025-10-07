/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.dao.db.PostgreSQLDB;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class PostgreSQLTransformerLogicTest
	extends BaseSQLTransformerLogicTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	public PostgreSQLTransformerLogicTest() {
		super(new PostgreSQLDB(1, 0));
	}

	@Override
	public String getDropTableIfExistsTextTransformedSQL() {
		return "DROP TABLE IF EXISTS Foo";
	}

	@Test
	public void testReplaceBooleanAggregation() {
		Assert.assertEquals(
			"select foo from Foo order by CASE WHEN MIN(CAST(foo AS " +
				"INTEGER)) = 1 THEN 1 ELSE 0 END",
			sqlTransformer.transform(
				"select foo from Foo order by AGGREGATION_BOOLEAN_MIN(foo)"));
	}

	@Override
	@Test
	public void testReplaceModWithExtraWhitespace() {
		Assert.assertEquals(
			getModTransformedSQL(),
			sqlTransformer.transform(getModOriginalSQL()));
	}

	@Test
	public void testReplaceNegativeComparison() {
		Assert.assertEquals(
			"select * from Foo where foo != (-1)",
			sqlTransformer.transform("select * from Foo where foo != -1"));
		Assert.assertEquals(
			"select * from Foo where foo != (-1) and bar != (-1)",
			sqlTransformer.transform(
				"select * from Foo where foo != -1 and bar != -1"));
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
	protected String getCastClobTextTransformedSQL() {
		return "select CAST(foo || (CAST(foo AS TEXT) || (bar || foo)) AS " +
			"TEXT), CAST(foo || (bar || foo) AS TEXT) from Foo";
	}

	@Override
	protected String getCastLongTransformedSQL() {
		return "select CAST(1 + (CAST(foo AS BIGINT) - (bar x 2)) AS " +
			"BIGINT), CAST(foo + (bar x 3) AS BIGINT) from Foo";
	}

	@Override
	protected String getCastTextTransformedSQL() {
		return "select CAST(foo || (CAST(foo AS TEXT) || (bar || foo)) AS " +
			"TEXT), CAST(foo || (bar || foo) AS TEXT) from Foo";
	}

	@Override
	protected String getIntegerDivisionTransformedSQL() {
		return "select foo / bar from Foo";
	}

	@Override
	protected String getNullDateTransformedSQL() {
		return "select CAST(NULL AS TIMESTAMP) from Foo";
	}

}