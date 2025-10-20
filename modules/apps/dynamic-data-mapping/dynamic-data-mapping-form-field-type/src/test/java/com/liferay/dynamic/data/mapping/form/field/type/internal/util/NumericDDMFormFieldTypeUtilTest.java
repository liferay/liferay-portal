/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.test.util.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carolina Barbosa
 */
public class NumericDDMFormFieldTypeUtilTest
	extends BaseDDMFormFieldTypeSettingsTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetInputMaskParametersDouble() {
		DDMFormField ddmFormField = new DDMFormField("field", "numeric");

		ddmFormField.setProperty("inputMask", true);
		ddmFormField.setProperty(
			"numericInputMask",
			DDMFormValuesTestUtil.createLocalizedValue(
				StringPool.BLANK, LocaleUtil.US));

		_assertInputMaskParametersDouble(
			NumericDDMFormFieldTypeUtil.getParameters(
				"double", ddmFormField, new DDMFormFieldRenderingContext()));

		ddmFormField.setProperty(
			"numericInputMask",
			DDMFormValuesTestUtil.createLocalizedValue(
				JSONUtil.put(
					"append", "$"
				).put(
					"appendType", "prefix"
				).put(
					"decimalPlaces", 2
				).put(
					"symbols",
					JSONUtil.put(
						"decimalSymbol", ","
					).put(
						"thousandsSeparator", "\'"
					)
				).toString(),
				LocaleUtil.US));

		_assertInputMaskParametersDouble(
			NumericDDMFormFieldTypeUtil.getParameters(
				"double", ddmFormField, new DDMFormFieldRenderingContext()));
	}

	@Test
	public void testGetInputMaskParametersInteger() {
		DDMFormField ddmFormField = new DDMFormField("field", "numeric");

		ddmFormField.setProperty("inputMask", true);

		Map<String, Object> parameters =
			NumericDDMFormFieldTypeUtil.getParameters(
				"integer", ddmFormField, new DDMFormFieldRenderingContext());

		Assert.assertEquals(parameters.toString(), 2, parameters.size());
		Assert.assertTrue(parameters.containsKey("inputMask"));
		Assert.assertTrue(parameters.containsKey("inputMaskFormat"));
	}

	@Test
	public void testGetParametersDouble() {
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setLocale(LocaleUtil.US);

		Map<String, Object> parameters =
			NumericDDMFormFieldTypeUtil.getParameters(
				"double", new DDMFormField("field", "numeric"),
				ddmFormFieldRenderingContext);

		Assert.assertEquals(parameters.toString(), 2, parameters.size());
		Assert.assertEquals(
			HashMapBuilder.put(
				"en_US",
				HashMapBuilder.put(
					"decimalSymbol", "."
				).put(
					"thousandsSeparator", ","
				).build()
			).put(
				"pt_BR",
				HashMapBuilder.put(
					"decimalSymbol", ","
				).put(
					"thousandsSeparator", "."
				).build()
			).build(),
			parameters.get("localizedSymbols"));
		Assert.assertEquals(
			HashMapBuilder.put(
				"decimalSymbol", "."
			).put(
				"thousandsSeparator", ","
			).build(),
			parameters.get("symbols"));
	}

	@Test
	public void testGetParametersInteger() {
		Map<String, Object> parameters =
			NumericDDMFormFieldTypeUtil.getParameters(
				"integer", new DDMFormField("field", "numeric"),
				new DDMFormFieldRenderingContext());

		Assert.assertTrue(parameters.isEmpty());
	}

	private void _assertInputMaskParametersDouble(
		Map<String, Object> parameters) {

		Assert.assertEquals(parameters.toString(), 6, parameters.size());

		Assert.assertTrue(parameters.containsKey("append"));
		Assert.assertTrue(parameters.containsKey("appendType"));
		Assert.assertTrue(parameters.containsKey("decimalPlaces"));
		Assert.assertTrue(parameters.containsKey("inputMask"));
		Assert.assertTrue(parameters.containsKey("numericInputMask"));
		Assert.assertTrue(parameters.containsKey("symbols"));
	}

}