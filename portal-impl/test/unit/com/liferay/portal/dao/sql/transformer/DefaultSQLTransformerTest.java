/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.sql.transformer;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Manuel de la Peña
 */
public class DefaultSQLTransformerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testTransformWithMultipleFunctions() {
		SQLTransformer sqlTransformer = new DefaultSQLTransformer(
			() -> new Function[] {_toUpperCaseFunction, _trimFunction});

		String sql = sqlTransformer.transform(" select * from Table ");

		Assert.assertEquals("SELECT * FROM TABLE", sql);
	}

	@Test
	public void testTransformWithNoFunctions() {
		String sql = "select * from Foo";

		SQLTransformer sqlTransformer = new DefaultSQLTransformer(
			() -> new Function[0]);

		Assert.assertEquals(sql, sqlTransformer.transform(sql));
	}

	@Test
	public void testTransformWithNullFunction() {
		String sql = "select * from Foo";

		SQLTransformer sqlTransformer = new DefaultSQLTransformer(() -> null);

		Assert.assertEquals(sql, sqlTransformer.transform(sql));
	}

	@Test
	public void testTransformWithOneFunction() {
		SQLTransformer sqlTransformer = new DefaultSQLTransformer(
			() -> new Function[] {_dummyFunction});

		Assert.assertNull(sqlTransformer.transform(null));
	}

	private final Function<String, String> _dummyFunction = (String sql) -> sql;
	private final Function<String, String> _toUpperCaseFunction =
		(String sql) -> StringUtil.toUpperCase(sql);
	private final Function<String, String> _trimFunction =
		(String sql) -> sql.trim();

}