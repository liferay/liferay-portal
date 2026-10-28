/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.report.util;

import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.math.BigDecimal;

import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Akhash Ramprakash
 */
public class NumericDDMFormFieldTypeReportUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetValueBigDecimal() {
		Assert.assertEquals(
			new BigDecimal("3.5"),
			NumericDDMFormFieldTypeReportUtil.getValueBigDecimal(
				_mockDDMFormFieldValue(LocaleUtil.BRAZIL, "3,5")));
		Assert.assertEquals(
			new BigDecimal("4.5"),
			NumericDDMFormFieldTypeReportUtil.getValueBigDecimal(
				_mockDDMFormFieldValue(LocaleUtil.US, "4.5")));
	}

	private DDMFormFieldValue _mockDDMFormFieldValue(
		Locale defaultLocale, String fieldValue) {

		DDMFormFieldValue ddmFormFieldValue = Mockito.mock(
			DDMFormFieldValue.class);

		Value value = new LocalizedValue(defaultLocale);

		value.addString(value.getDefaultLocale(), fieldValue);

		Mockito.when(
			ddmFormFieldValue.getValue()
		).thenReturn(
			value
		);

		return ddmFormFieldValue;
	}

}