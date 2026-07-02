/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.grid;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Pedro Queiroz
 */
public class GridDDMFormFieldValueRendererTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testRender() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			"Grid", "Grid", "grid", "string", false, false, false);

		DDMFormFieldOptions ddmFormFieldRows = new DDMFormFieldOptions();

		ddmFormFieldRows.addOptionLabel(
			"rowValue 1", LocaleUtil.US, "rowLabel 1");
		ddmFormFieldRows.addOptionLabel(
			"rowValue 2", LocaleUtil.US, "rowLabel 2");

		ddmFormField.setProperty("rows", ddmFormFieldRows);

		DDMFormFieldOptions ddmFormFieldColumns = new DDMFormFieldOptions();

		ddmFormFieldColumns.addOptionLabel(
			"columnValue 1", LocaleUtil.US, "columnLabel 1");
		ddmFormFieldColumns.addOptionLabel(
			"columnValue 2", LocaleUtil.US, "columnLabel 2");

		ddmFormField.setProperty("columns", ddmFormFieldColumns);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"Grid",
				new UnlocalizedValue("{\"rowValue 1\":\"columnValue 1\"}"));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		GridDDMFormFieldValueRenderer gridDDMFormFieldValueRenderer =
			_createGridDDMFormFieldValueRenderer();

		Assert.assertEquals(
			"rowLabel 1: columnLabel 1",
			gridDDMFormFieldValueRenderer.render(
				ddmFormFieldValue, LocaleUtil.US));

		ddmForm = DDMFormTestUtil.createDDMForm();

		ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(ddmForm);

		ddmFormFieldValue = DDMFormValuesTestUtil.createDDMFormFieldValue(
			"Grid", new UnlocalizedValue("{\"rowValue 1\":\"columnValue 1\"}"));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		Assert.assertNull(ddmFormFieldValue.getDDMFormField());

		Assert.assertEquals(
			StringPool.BLANK,
			gridDDMFormFieldValueRenderer.render(
				ddmFormFieldValue, LocaleUtil.US));
	}

	@Test
	public void testRenderWithTwoRows() throws Exception {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm();

		DDMFormField ddmFormField = DDMFormTestUtil.createDDMFormField(
			"Grid", "Grid", "grid", "string", false, false, false);

		DDMFormFieldOptions ddmFormFieldRows = new DDMFormFieldOptions();

		ddmFormFieldRows.addOptionLabel(
			"rowValue 1", LocaleUtil.US, "rowLabel 1");
		ddmFormFieldRows.addOptionLabel(
			"rowValue 2", LocaleUtil.US, "rowLabel 2");

		ddmFormField.setProperty("rows", ddmFormFieldRows);

		DDMFormFieldOptions ddmFormFieldColumns = new DDMFormFieldOptions();

		ddmFormFieldColumns.addOptionLabel(
			"columnValue 1", LocaleUtil.US, "columnLabel 1");
		ddmFormFieldColumns.addOptionLabel(
			"columnValue 2", LocaleUtil.US, "columnLabel 2");

		ddmFormField.setProperty("columns", ddmFormFieldColumns);

		ddmForm.addDDMFormField(ddmFormField);

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		String value =
			"{\"rowValue 2\":\"columnValue 2\", \"rowValue 1\":\"" +
				"columnValue 1\"}";

		DDMFormFieldValue ddmFormFieldValue =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"Grid", new UnlocalizedValue(value));

		ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);

		GridDDMFormFieldValueRenderer gridDDMFormFieldValueRenderer =
			_createGridDDMFormFieldValueRenderer();

		Assert.assertEquals(
			"rowLabel 1: columnLabel 1, rowLabel 2: columnLabel 2",
			gridDDMFormFieldValueRenderer.render(
				ddmFormFieldValue, LocaleUtil.US));
	}

	private GridDDMFormFieldValueAccessor
		_createGridDDMFormFieldValueAccessor() {

		GridDDMFormFieldValueAccessor gridDDMFormFieldValueAccessor =
			new GridDDMFormFieldValueAccessor();

		gridDDMFormFieldValueAccessor.jsonFactory = new JSONFactoryImpl();

		return gridDDMFormFieldValueAccessor;
	}

	private GridDDMFormFieldValueRenderer _createGridDDMFormFieldValueRenderer()
		throws Exception {

		GridDDMFormFieldValueRenderer gridDDMFormFieldValueRenderer =
			new GridDDMFormFieldValueRenderer();

		gridDDMFormFieldValueRenderer.gridDDMFormFieldValueAccessor =
			_createGridDDMFormFieldValueAccessor();

		return gridDDMFormFieldValueRenderer;
	}

}