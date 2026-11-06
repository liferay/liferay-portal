/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.metatype.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Jorge Ferrer
 */
public class ParameterMapUtilWhenSettingAParameterMapWithPrefixesTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws ConfigurationException {
		_testBean = ParameterMapUtil.setParameterMap(
			ParameterMapUtilTestUtil.TestBean.class,
			ParameterMapUtilTestUtil.getTestBean(),
			HashMapBuilder.put(
				"prefix--testBigDecimal1--",
				new String[] {
					String.valueOf(
						ParameterMapUtilTestUtil.PARAMETER_MAP_BIG_DECIMAL)
				}
			).put(
				"prefix--testBoolean1--", new String[] {"false"}
			).put(
				"prefix--testEnum1--",
				new String[] {
					String.valueOf(
						ParameterMapUtilTestUtil.TestEnum.PARAMETER_MAP)
				}
			).put(
				"prefix--testString1--",
				new String[] {ParameterMapUtilTestUtil.PARAMETER_MAP_STRING}
			).put(
				"prefix--testStringArray1--",
				ParameterMapUtilTestUtil.PARAMETER_MAP_STRING_ARRAY
			).build(),
			"prefix--", StringPool.DOUBLE_DASH);
	}

	@Test
	public void testValuesInTheParameterMapAreReadFirst() {
		Assert.assertEquals(
			ParameterMapUtilTestUtil.PARAMETER_MAP_BIG_DECIMAL,
			_testBean.testBigDecimal1());
		Assert.assertFalse(_testBean.testBoolean1());
		Assert.assertEquals(
			ParameterMapUtilTestUtil.TestEnum.PARAMETER_MAP,
			_testBean.testEnum1());
		Assert.assertEquals(
			ParameterMapUtilTestUtil.PARAMETER_MAP_STRING,
			_testBean.testString1());
		Assert.assertArrayEquals(
			ParameterMapUtilTestUtil.PARAMETER_MAP_STRING_ARRAY,
			_testBean.testStringArray1());
	}

	@Test
	public void testValuesNotInTheParameterMapAreReadFromBean() {
		Assert.assertTrue(_testBean.testBoolean2());
		Assert.assertEquals(
			ParameterMapUtilTestUtil.TEST_BEAN_STRING, _testBean.testString2());
		Assert.assertArrayEquals(
			ParameterMapUtilTestUtil.TEST_BEAN_STRING_ARRAY,
			_testBean.testStringArray2());
	}

	private ParameterMapUtilTestUtil.TestBean _testBean;

}