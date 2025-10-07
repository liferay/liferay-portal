/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Daniel Raposo
 */
public class FieldsUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-60157")
	public void test() {
		Assert.assertEquals(List.of("."), FieldsUtil.expand("."));
		Assert.assertEquals(List.of("..", "."), FieldsUtil.expand(".."));
		Assert.assertEquals(List.of("..a", "."), FieldsUtil.expand("..a"));
		Assert.assertEquals(List.of(".a"), FieldsUtil.expand(".a"));
		Assert.assertEquals(List.of(".a.b", ".a"), FieldsUtil.expand(".a.b"));
		Assert.assertEquals(List.of("a.", "a"), FieldsUtil.expand("a."));
		Assert.assertEquals(
			List.of("a..", "a.", "a"), FieldsUtil.expand("a.."));
		Assert.assertEquals(
			List.of("a..b", "a.", "a"), FieldsUtil.expand("a..b"));
		Assert.assertEquals(List.of("a.b", "a"), FieldsUtil.expand("a.b"));
		Assert.assertEquals(
			List.of("a.b.", "a.b", "a"), FieldsUtil.expand("a.b."));
		Assert.assertEquals(
			List.of("a.b.c", "a.b", "a"), FieldsUtil.expand("a.b.c"));
		Assert.assertEquals(List.of("abc"), FieldsUtil.expand("abc"));
	}

}