/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Leonardo Barros
 */
public class IsEmptyFunctionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testApply() {
		IsEmptyFunction isEmptyFunction = new IsEmptyFunction();

		Assert.assertFalse(
			isEmptyFunction.apply(new String[] {"  ", "not empty "}));
		Assert.assertFalse(
			isEmptyFunction.apply(
				HashMapBuilder.put(
					RandomTestUtil.randomString(), StringPool.BLANK
				).put(
					RandomTestUtil.randomString(), RandomTestUtil.randomString()
				).build()));
		Assert.assertTrue(isEmptyFunction.apply(" "));
		Assert.assertTrue(isEmptyFunction.apply(null));
		Assert.assertTrue(
			isEmptyFunction.apply(
				Collections.singletonMap(
					RandomTestUtil.randomString(), StringPool.BLANK)));
	}

}