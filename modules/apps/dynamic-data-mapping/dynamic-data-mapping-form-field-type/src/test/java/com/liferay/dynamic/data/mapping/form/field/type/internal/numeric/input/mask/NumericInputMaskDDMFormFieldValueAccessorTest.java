/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.numeric.input.mask;

import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carolina Barbosa
 */
public class NumericInputMaskDDMFormFieldValueAccessorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetValue() {
		Assert.assertEquals(
			StringPool.BLANK,
			_numericInputMaskDDMFormFieldValueAccessor.getValue(
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"NumericInputMask", null),
				LocaleUtil.US));
		Assert.assertEquals(
			JSONUtil.put(
				"append", ""
			).put(
				"appendType", "prefix"
			).put(
				"decimalPlaces", 2
			).put(
				"symbols",
				JSONUtil.put(
					"decimalSymbol", "."
				).put(
					"thousandsSeparator", "none"
				)
			).toString(),
			_numericInputMaskDDMFormFieldValueAccessor.getValue(
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"NumericInputMask",
					DDMFormValuesTestUtil.createLocalizedValue(
						StringPool.BLANK, LocaleUtil.US)),
				LocaleUtil.US));

		String value = JSONUtil.put(
			"append", "%"
		).put(
			"appendType", "suffix"
		).put(
			"decimalPlaces", 3
		).put(
			"symbols",
			JSONUtil.put(
				"decimalSymbol", ","
			).put(
				"thousandsSeparator", "\'"
			)
		).toString();

		Assert.assertEquals(
			value,
			_numericInputMaskDDMFormFieldValueAccessor.getValue(
				DDMFormValuesTestUtil.createDDMFormFieldValue(
					"NumericInputMask",
					DDMFormValuesTestUtil.createLocalizedValue(
						value, LocaleUtil.US)),
				LocaleUtil.US));
	}

	private final NumericInputMaskDDMFormFieldValueAccessor
		_numericInputMaskDDMFormFieldValueAccessor =
			new NumericInputMaskDDMFormFieldValueAccessor();

}