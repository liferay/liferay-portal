/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.BaseDDMTestCase;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Carolina Barbosa
 */
public class DDMFormValuesConverterUtilTest extends BaseDDMTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddMissingNestedDDMFormFieldValues() {
		DDMFormField fieldset1DDMFormField = new DDMFormField(
			"Fieldset1", DDMFormFieldTypeConstants.FIELDSET);

		addNestedTextDDMFormFields(fieldset1DDMFormField, "Text1", "Text2");

		DDMFormField fieldset2DDMFormField = new DDMFormField(
			"Fieldset2", DDMFormFieldTypeConstants.FIELDSET);

		addNestedTextDDMFormFields(fieldset2DDMFormField, "Text3");

		fieldset1DDMFormField.addNestedDDMFormField(fieldset2DDMFormField);

		DDMFormFieldValue text1DDMFormFieldValue = createDDMFormFieldValue(
			"Text1");
		DDMFormFieldValue text3DDMFormFieldValue = createDDMFormFieldValue(
			"Text3");

		List<DDMFormFieldValue> ddmFormFieldValues =
			DDMFormValuesConverterUtil.addMissingDDMFormFieldValues(
				Collections.singletonList(fieldset1DDMFormField),
				HashMapBuilder.<String, List<DDMFormFieldValue>>put(
					"Text1", Collections.singletonList(text1DDMFormFieldValue)
				).put(
					"Text3", Collections.singletonList(text3DDMFormFieldValue)
				).build());

		Assert.assertEquals(
			ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());

		_assertDDMFormFieldValueName(ddmFormFieldValues.get(0), "Fieldset1");

		List<DDMFormFieldValue> fieldset1NestedDDMFormFieldValues =
			_getNestedDDMFormFieldValues(ddmFormFieldValues.get(0));

		Assert.assertEquals(
			fieldset1NestedDDMFormFieldValues.toString(), 3,
			fieldset1NestedDDMFormFieldValues.size());

		_assertDDMFormFieldValueName(
			fieldset1NestedDDMFormFieldValues.get(0), "Text1");
		_assertDDMFormFieldValueName(
			fieldset1NestedDDMFormFieldValues.get(1), "Text2");
		_assertDDMFormFieldValueName(
			fieldset1NestedDDMFormFieldValues.get(2), "Fieldset2");

		Assert.assertEquals(
			fieldset1NestedDDMFormFieldValues.get(0), text1DDMFormFieldValue);

		List<DDMFormFieldValue> fieldset2NestedDDMFormFieldValues =
			_getNestedDDMFormFieldValues(
				fieldset1NestedDDMFormFieldValues.get(2));

		Assert.assertEquals(
			fieldset2NestedDDMFormFieldValues.toString(), 1,
			fieldset2NestedDDMFormFieldValues.size());

		_assertDDMFormFieldValueName(
			fieldset2NestedDDMFormFieldValues.get(0), "Text3");

		Assert.assertEquals(
			fieldset2NestedDDMFormFieldValues.get(0), text3DDMFormFieldValue);
	}

	@Test
	public void testRemoveExtraNestedDDMFormFieldValues() {
		DDMFormField ddmFormField = new DDMFormField("Fieldset", "fieldset");

		addNestedTextDDMFormFields(ddmFormField, "Text");

		DDMFormFieldValue dateDDMFormFieldValue = createDDMFormFieldValue(
			"Date");
		DDMFormFieldValue numericDDMFormFieldValue = createDDMFormFieldValue(
			"Numeric");

		DDMFormFieldValue nestedFieldsetDDMFormFieldValue =
			_getDDMFormFieldValue(
				"NestedFieldset", dateDDMFormFieldValue,
				numericDDMFormFieldValue);

		DDMFormFieldValue textDDMFormFieldValue = createDDMFormFieldValue(
			"Text");

		DDMFormFieldValue fieldsetDDMFormFieldValue = _getDDMFormFieldValue(
			"Fieldset", nestedFieldsetDDMFormFieldValue, textDDMFormFieldValue);

		List<DDMFormFieldValue> ddmFormFieldValues =
			DDMFormValuesConverterUtil.addMissingDDMFormFieldValues(
				ListUtil.fromArray(ddmFormField),
				HashMapBuilder.<String, List<DDMFormFieldValue>>put(
					"Date", ListUtil.fromArray(dateDDMFormFieldValue)
				).put(
					"Fieldset", ListUtil.fromArray(fieldsetDDMFormFieldValue)
				).put(
					"NestedFieldset",
					ListUtil.fromArray(nestedFieldsetDDMFormFieldValue)
				).put(
					"Numeric", ListUtil.fromArray(numericDDMFormFieldValue)
				).put(
					"Text", ListUtil.fromArray(textDDMFormFieldValue)
				).build());

		Assert.assertEquals(
			ddmFormFieldValues.toString(), 1, ddmFormFieldValues.size());

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		Assert.assertEquals("Fieldset", ddmFormFieldValue.getName());

		List<DDMFormFieldValue> nestedDDMFormFieldValues =
			ddmFormFieldValue.getNestedDDMFormFieldValues();

		Assert.assertEquals(
			nestedDDMFormFieldValues.toString(), 1,
			nestedDDMFormFieldValues.size());
		Assert.assertEquals(
			nestedDDMFormFieldValues.get(0), textDDMFormFieldValue);
	}

	private void _assertDDMFormFieldValueName(
		DDMFormFieldValue ddmFormFieldValue, String expectedName) {

		Assert.assertEquals(expectedName, ddmFormFieldValue.getName());
	}

	private DDMFormFieldValue _getDDMFormFieldValue(
		String name, DDMFormFieldValue... nestedDDMFormFieldValues) {

		DDMFormFieldValue ddmFormFieldValue = createDDMFormFieldValue(name);

		for (DDMFormFieldValue nestedDDMFormFieldValue :
				nestedDDMFormFieldValues) {

			ddmFormFieldValue.addNestedDDMFormFieldValue(
				nestedDDMFormFieldValue);
		}

		return ddmFormFieldValue;
	}

	private List<DDMFormFieldValue> _getNestedDDMFormFieldValues(
		DDMFormFieldValue ddmFormFieldValue) {

		return ddmFormFieldValue.getNestedDDMFormFieldValues();
	}

}