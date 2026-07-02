/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.select;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Victor Kammerer
 */
public class SelectDDMFormFieldValueRendererTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRender() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			"Select", "Select", "select", "string", false, false, false);

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		ddmFormFieldOptions.addOptionLabel(
			"value 1", LocaleUtil.US, "option with &");

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"Select", new UnlocalizedValue("[\"value 1\"]"));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		SelectDDMFormFieldValueRenderer selectDDMFormFieldValueRenderer =
			new SelectDDMFormFieldValueRenderer();

		Assert.assertEquals(
			"option with &amp;",
			selectDDMFormFieldValueRenderer.render(
				ddmFormFieldValue, LocaleUtil.US));

		ddmForm = DDMFormTestUtil.createDDMForm();

		ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);

		ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue(
			"Select", new UnlocalizedValue("[\"value 1\"]"));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		Assert.assertNull(ddmFormFieldValue.getDDMFormField());

		Assert.assertEquals(
			"value 1",
			selectDDMFormFieldValueRenderer.render(
				ddmFormFieldValue, LocaleUtil.US));
	}

}