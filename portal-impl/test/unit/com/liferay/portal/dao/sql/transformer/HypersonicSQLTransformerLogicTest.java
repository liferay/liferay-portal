/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.dao.db.HypersonicDB;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 * @author Brian Wing Shun Chan
 */
public class HypersonicSQLTransformerLogicTest
	extends BaseSQLTransformerLogicTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	public HypersonicSQLTransformerLogicTest() {
		super(new HypersonicDB(1, 0));
	}

	@Override
	public String getDropTableIfExistsTextTransformedSQL() {
		return "DROP TABLE Foo IF EXISTS";
	}

	@Override
	@Test
	public void testReplaceBitwiseCheckWithExtraWhitespace() {
		Assert.assertEquals(
			getBitwiseCheckTransformedSQL(),
			SQLTransformer.transform(getBitwiseCheckOriginalSQL()));
	}

	@Override
	@Test
	public void testReplaceModWithExtraWhitespace() {
		Assert.assertEquals(
			getModTransformedSQL(),
			SQLTransformer.transform(getModOriginalSQL()));
	}

	@Override
	protected String getCastClobTextTransformedSQL() {
		return StringBundler.concat(
			"select CONVERT(foo || (CONVERT(foo, SQL_VARCHAR) || (bar || ",
			"foo)), SQL_VARCHAR), CONVERT(foo || (bar || foo), SQL_VARCHAR) ",
			"from Foo");
	}

	@Override
	protected String getCastLongTransformedSQL() {
		return "select CONVERT(1 + (CONVERT(foo, SQL_BIGINT) - (bar x 2)), " +
			"SQL_BIGINT), CONVERT(foo + (bar x 3), SQL_BIGINT) from Foo";
	}

	@Override
	protected String getCastTextTransformedSQL() {
		return StringBundler.concat(
			"select CONVERT(foo || (CONVERT(foo, SQL_VARCHAR) || (bar || ",
			"foo)), SQL_VARCHAR), CONVERT(foo || (bar || foo), SQL_VARCHAR) ",
			"from Foo");
	}

	@Override
	protected String getIntegerDivisionTransformedSQL() {
		return "select foo / bar from Foo";
	}

	@Override
	protected String getNullDateTransformedSQL() {
		return "select CAST(NULL AS DATE) from Foo";
	}

}