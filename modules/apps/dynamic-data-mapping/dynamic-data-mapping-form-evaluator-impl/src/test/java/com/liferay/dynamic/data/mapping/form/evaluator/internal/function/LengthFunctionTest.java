/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rodrigo Paulino
 */
public class LengthFunctionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testApplyWithText() {
		LengthFunction lengthFunction = new LengthFunction();

		Assert.assertEquals(
			Integer.valueOf(8), lengthFunction.apply("12345678"));
	}

	@Test
	public void testApplyWithoutText() {
		LengthFunction lengthFunction = new LengthFunction();

		Assert.assertEquals(Integer.valueOf(0), lengthFunction.apply(null));
		Assert.assertEquals(
			Integer.valueOf(0), lengthFunction.apply(StringPool.BLANK));
	}

}