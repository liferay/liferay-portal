/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Sampsa Sohlman
 */
public class SQLServerLimitStringUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testDistinct() throws Exception {
		String sql = SQLServerLimitStringUtil.getLimitString(
			"SELECT DISTINCT JournalArticle.* FROM JournalArticle ORDER BY " +
				"userName ASC",
			10, 30);

		Assert.assertFalse(sql.contains("SELECT top DISTINCT"));
		Assert.assertTrue(sql.contains("SELECT DISTINCT top"));
	}

	@Test
	public void testInnerOrderBy() throws Exception {
		String sql = SQLServerLimitStringUtil.getLimitString(
			"SELECT articleId, userName FROM JournalArticle ORDER BY " +
				"modifiedDate ASC",
			10, 30);

		Assert.assertTrue(sql.indexOf("30") > 0);
		Assert.assertTrue(sql.indexOf("11") > 0);
		Assert.assertTrue(sql.indexOf("top") > 0);
	}

	@Test
	public void testNoInnerOrderBy() throws Exception {
		String sql = SQLServerLimitStringUtil.getLimitString(
			"SELECT articleId, userName FROM JournalArticle ORDER BY " +
				"userName ASC",
			10, 30);

		Assert.assertTrue(sql.indexOf("30") > 0);
		Assert.assertTrue(sql.indexOf("11") > 0);
		Assert.assertTrue(sql.indexOf("top") != 0);
	}

	@Test
	public void testOrderByWithFunctionExpression() throws Exception {
		String sql = SQLServerLimitStringUtil.getLimitString(
			StringBundler.concat(
				"select tempgroup.groupid from (select distinct ",
				"group_.groupid, group_.name from group_ where ",
				"group_.companyid = ?) tempgroup order by ",
				"replace(tempgroup.name, ?, ?) asc"),
			0, 20);

		Assert.assertTrue(
			sql, sql.contains("replace(tempgroup.name, ?, ?) asc"));
	}

	@Test
	public void testOrderByWithTextCast() throws Exception {
		String sql = SQLServerLimitStringUtil.getLimitString(
			StringBundler.concat(
				"select DDMStructure.* from DDMStructure where ",
				"DDMStructure.companyId = ? order by CAST(DDMStructure.name ",
				"AS NVARCHAR(MAX)) ASC"),
			20, 40);

		Assert.assertTrue(
			sql.contains(
				"order by CAST(DDMStructure.name AS NVARCHAR(MAX)) ASC"));
		Assert.assertTrue(sql.contains("select top 40 DDMStructure.*"));
	}

	@Test
	public void testUnionWithFieldsQuery() throws Exception {
		String sql = SQLServerLimitStringUtil.getLimitString(
			"( SELECT articleId, userName FROM JournalArticle ) UNION ALL ( " +
				"SELECT articleId, userName FROM JournalArticle )",
			10, 30);

		Assert.assertTrue(sql.indexOf("30") > 0);
		Assert.assertTrue(sql.indexOf("11") > 0);
		Assert.assertTrue(sql.indexOf("top") != 0);
	}

}