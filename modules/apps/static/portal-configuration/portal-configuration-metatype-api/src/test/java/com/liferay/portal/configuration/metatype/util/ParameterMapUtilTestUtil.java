/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.metatype.util;

import java.math.BigDecimal;

/**
 * @author Jorge Ferrer
 */
public class ParameterMapUtilTestUtil {

	public static final BigDecimal PARAMETER_MAP_BIG_DECIMAL = new BigDecimal(
		"1.5");

	public static final String PARAMETER_MAP_STRING = "PARAMETER_MAP";

	public static final String[] PARAMETER_MAP_STRING_ARRAY = {
		"PARAMETER_MAP1", "PARAMETER_MAP2"
	};

	public static final BigDecimal TEST_BEAN_BIG_DECIMAL = new BigDecimal(
		"2.5");

	public static final String TEST_BEAN_STRING = "TEST_BEAN";

	public static final String[] TEST_BEAN_STRING_ARRAY = {
		"TEST_BEAN1", "TEST_BEAN2"
	};

	protected static TestBean getTestBean() {
		return new TestBean() {

			@Override
			public BigDecimal testBigDecimal1() {
				return TEST_BEAN_BIG_DECIMAL;
			}

			@Override
			public boolean testBoolean1() {
				return true;
			}

			@Override
			public boolean testBoolean2() {
				return true;
			}

			@Override
			public TestEnum testEnum1() {
				return TestEnum.TEST_BEAN;
			}

			@Override
			public String testString1() {
				return TEST_BEAN_STRING;
			}

			@Override
			public String testString2() {
				return TEST_BEAN_STRING;
			}

			@Override
			public String[] testStringArray1() {
				return TEST_BEAN_STRING_ARRAY;
			}

			@Override
			public String[] testStringArray2() {
				return TEST_BEAN_STRING_ARRAY;
			}

		};
	}

	protected interface TestBean {

		public BigDecimal testBigDecimal1();

		public boolean testBoolean1();

		public boolean testBoolean2();

		public TestEnum testEnum1();

		public String testString1();

		public String testString2();

		public String[] testStringArray1();

		public String[] testStringArray2();

	}

	protected enum TestEnum {

		PARAMETER_MAP, TEST_BEAN

	}

}