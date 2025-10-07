/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Nathaly Gomes
 */
public class DDMFormFieldUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSortNestedDDMFormFields() throws Exception {
		DDMFormField ddmFormField = new DDMFormField(
			DDMFormFieldTypeConstants.FIELDSET,
			DDMFormFieldTypeConstants.FIELDSET);

		DDMFormTestUtil.addNestedTextDDMFormFields(
			ddmFormField, "Field3", "Field2", "Field1");

		ddmFormField.setProperty(
			"rows",
			JSONUtil.putAll(
				JSONUtil.put(
					"columns",
					JSONUtil.putAll(
						JSONUtil.put(
							"fields", JSONUtil.putAll("Field1")
						).put(
							"size", 12
						))),
				JSONUtil.put(
					"columns",
					JSONUtil.putAll(
						JSONUtil.put(
							"fields", JSONUtil.putAll("Field2")
						).put(
							"size", 12
						))),
				JSONUtil.put(
					"columns",
					JSONUtil.putAll(
						JSONUtil.put(
							"fields", JSONUtil.putAll("Field3")
						).put(
							"size", 12
						)))));

		_assertNestedDDMFormFields(
			ddmFormField, new String[] {"Field3", "Field2", "Field1"});

		DDMFormFieldUtil.sortNestedDDMFormFields(
			Collections.singletonList(ddmFormField));

		_assertNestedDDMFormFields(
			ddmFormField, new String[] {"Field1", "Field2", "Field3"});
	}

	@Test
	public void testSortNestedDDMFormFieldsWithDeletedField() throws Exception {
		DDMFormField ddmFormField = new DDMFormField(
			DDMFormFieldTypeConstants.FIELDSET,
			DDMFormFieldTypeConstants.FIELDSET);

		DDMFormTestUtil.addNestedTextDDMFormFields(
			ddmFormField, "Field3", "Field1");

		ddmFormField.setProperty(
			"rows",
			JSONUtil.putAll(
				JSONUtil.put(
					"columns",
					JSONUtil.putAll(
						JSONUtil.put(
							"fields", JSONUtil.putAll("Field1")
						).put(
							"size", 12
						))),
				JSONUtil.put(
					"columns",
					JSONUtil.putAll(
						JSONUtil.put(
							"fields", JSONUtil.putAll("Field2")
						).put(
							"size", 12
						))),
				JSONUtil.put(
					"columns",
					JSONUtil.putAll(
						JSONUtil.put(
							"fields", JSONUtil.putAll("Field3")
						).put(
							"size", 12
						)))));

		DDMFormFieldUtil.sortNestedDDMFormFields(
			Collections.singletonList(ddmFormField));

		List<DDMFormField> nestedDDMFormFields =
			ddmFormField.getNestedDDMFormFields();

		Assert.assertEquals(
			nestedDDMFormFields.toString(), 2, nestedDDMFormFields.size());

		_assertNestedDDMFormFieldsWithDeletedField(
			ddmFormField, new String[] {"Field1", "Field3"});
	}

	private void _assertNestedDDMFormFields(
		DDMFormField ddmFormField, String[] nestedDDMFormFieldNames) {

		List<DDMFormField> nestedDDMFormFields =
			ddmFormField.getNestedDDMFormFields();

		Assert.assertEquals(
			nestedDDMFormFieldNames[0],
			_getDDMFormFieldName(nestedDDMFormFields.get(0)));
		Assert.assertEquals(
			nestedDDMFormFieldNames[1],
			_getDDMFormFieldName(nestedDDMFormFields.get(1)));
		Assert.assertEquals(
			nestedDDMFormFieldNames[2],
			_getDDMFormFieldName(nestedDDMFormFields.get(2)));
	}

	private void _assertNestedDDMFormFieldsWithDeletedField(
		DDMFormField ddmFormField, String[] nestedDDMFormFieldNames) {

		List<DDMFormField> nestedDDMFormFields =
			ddmFormField.getNestedDDMFormFields();

		Assert.assertEquals(
			nestedDDMFormFieldNames[0],
			_getDDMFormFieldName(nestedDDMFormFields.get(0)));
		Assert.assertEquals(
			nestedDDMFormFieldNames[1],
			_getDDMFormFieldName(nestedDDMFormFields.get(1)));
	}

	private String _getDDMFormFieldName(DDMFormField ddmFormField) {
		return ddmFormField.getName();
	}

}