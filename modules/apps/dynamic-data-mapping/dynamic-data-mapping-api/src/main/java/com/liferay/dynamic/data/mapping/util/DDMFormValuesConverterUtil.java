/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Mateus Santana
 */
public class DDMFormValuesConverterUtil {

	public static List<DDMFormFieldValue> addMissingDDMFormFieldValues(
		Collection<DDMFormField> ddmFormFields,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap) {

		List<DDMFormFieldValue> newDDMFormFieldValues = new ArrayList<>();

		for (DDMFormField ddmFormField : ddmFormFields) {
			List<DDMFormFieldValue> ddmFormFieldValues =
				ddmFormFieldValuesMap.get(ddmFormField.getName());

			if (ddmFormFieldValues == null) {
				DDMFormFieldValue ddmFormFieldValue =
					_createDefaultDDMFormFieldValue(ddmFormField);

				_populateNestedValues(
					ddmFormField, ddmFormFieldValue, ddmFormFieldValuesMap);

				newDDMFormFieldValues.add(ddmFormFieldValue);
			}
			else {
				for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
					_populateNestedValues(
						ddmFormField, ddmFormFieldValue, ddmFormFieldValuesMap);

					newDDMFormFieldValues.add(ddmFormFieldValue);
				}
			}
		}

		return newDDMFormFieldValues;
	}

	private static DDMFormFieldValue _createDefaultDDMFormFieldValue(
		DDMFormField ddmFormField) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setInstanceId(StringUtil.randomString());
		ddmFormFieldValue.setName(ddmFormField.getName());

		if (ddmFormField.isLocalizable()) {
			ddmFormFieldValue.setValue(new LocalizedValue());
		}
		else {
			ddmFormFieldValue.setValue(new UnlocalizedValue((String)null));
		}

		return ddmFormFieldValue;
	}

	private static void _populateNestedValues(
		DDMFormField ddmFormField, DDMFormFieldValue ddmFormFieldValue,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap) {

		if (!StringUtil.equals(
				ddmFormField.getType(), DDMFormFieldTypeConstants.FIELDSET)) {

			return;
		}

		Set<String> currentNames = new HashSet<>();

		for (DDMFormFieldValue currentDDMFormFieldValue :
				ddmFormFieldValue.getNestedDDMFormFieldValues()) {

			currentNames.add(currentDDMFormFieldValue.getName());
		}

		Set<String> expectedNames = new HashSet<>();

		for (DDMFormField nestedDDMFormField :
				ddmFormField.getNestedDDMFormFields()) {

			expectedNames.add(nestedDDMFormField.getName());

			List<DDMFormFieldValue> nestedDDMFormFieldValues =
				ddmFormFieldValuesMap.get(nestedDDMFormField.getName());

			if (nestedDDMFormFieldValues == null) {
				DDMFormFieldValue nestedDDMFormFieldValue =
					_createDefaultDDMFormFieldValue(nestedDDMFormField);

				ddmFormFieldValue.addNestedDDMFormFieldValue(
					nestedDDMFormFieldValue);

				_populateNestedValues(
					nestedDDMFormField, nestedDDMFormFieldValue,
					ddmFormFieldValuesMap);
			}
			else {
				for (DDMFormFieldValue nestedDDMFormFieldValue :
						nestedDDMFormFieldValues) {

					if (!currentNames.contains(
							nestedDDMFormFieldValue.getName())) {

						ddmFormFieldValue.addNestedDDMFormFieldValue(
							nestedDDMFormFieldValue);

						_populateNestedValues(
							nestedDDMFormField, nestedDDMFormFieldValue,
							ddmFormFieldValuesMap);
					}
				}
			}
		}

		List<DDMFormFieldValue> currentDDMFormFieldValues =
			ddmFormFieldValue.getNestedDDMFormFieldValues();

		Iterator<DDMFormFieldValue> iterator =
			currentDDMFormFieldValues.iterator();

		while (iterator.hasNext()) {
			DDMFormFieldValue currentDDMFormFieldValue = iterator.next();

			if (!expectedNames.contains(currentDDMFormFieldValue.getName())) {
				iterator.remove();
			}
		}
	}

}