/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Inácio Nery
 */
@Component(service = DDMFormValuesMerger.class)
public class DDMFormValuesMergerImpl implements DDMFormValuesMerger {

	@Override
	public DDMFormValues merge(
		DDMForm ddmForm, DDMFormValues newDDMFormValues,
		DDMFormValues existingDDMFormValues) {

		List<DDMFormFieldValue> newDDMFormFieldValues = new ArrayList<>(
			newDDMFormValues.getDDMFormFieldValues());

		for (DDMFormFieldValue ddmFormFieldValue :
				newDDMFormValues.getDDMFormFieldValues()) {

			newDDMFormFieldValues.addAll(
				ddmFormFieldValue.getNestedDDMFormFieldValues());
		}

		List<DDMFormFieldValue> mergedDDMFormFieldValues =
			_mergeDDMFormFieldValues(
				ddmForm, newDDMFormFieldValues,
				existingDDMFormValues.getDDMFormFieldValues());

		existingDDMFormValues.setDDMFormFieldValues(mergedDDMFormFieldValues);

		return existingDDMFormValues;
	}

	@Override
	public DDMFormValues merge(
		DDMFormValues newDDMFormValues, DDMFormValues existingDDMFormValues) {

		return merge(null, newDDMFormValues, existingDDMFormValues);
	}

	private DDMFormFieldValue _getDDMFormFieldValueByName(
		List<DDMFormFieldValue> ddmFormFieldValues, String name) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (name.equals(ddmFormFieldValue.getName())) {
				return ddmFormFieldValue;
			}
		}

		return null;
	}

	private List<DDMFormFieldValue> _mergeDDMFormFieldValues(
		DDMForm ddmForm, List<DDMFormFieldValue> newDDMFormFieldValues,
		List<DDMFormFieldValue> existingDDMFormFieldValues) {

		List<DDMFormFieldValue> mergedDDMFormFieldValues = new ArrayList<>(
			existingDDMFormFieldValues);

		for (DDMFormFieldValue newDDMFormFieldValue : newDDMFormFieldValues) {
			DDMFormValues ddmFormValues =
				newDDMFormFieldValue.getDDMFormValues();

			DDMFormFieldValue actualDDMFormFieldValue =
				_getDDMFormFieldValueByName(
					existingDDMFormFieldValues, newDDMFormFieldValue.getName());

			if (actualDDMFormFieldValue != null) {
				if (ddmForm == null) {
					ddmForm = ddmFormValues.getDDMForm();
				}

				_mergeValue(
					newDDMFormFieldValue.getValue(),
					actualDDMFormFieldValue.getValue());

				List<DDMFormFieldValue> mergedNestedDDMFormFieldValues =
					_mergeDDMFormFieldValues(
						null,
						newDDMFormFieldValue.getNestedDDMFormFieldValues(),
						actualDDMFormFieldValue.getNestedDDMFormFieldValues());

				newDDMFormFieldValue.setNestedDDMFormFields(
					mergedNestedDDMFormFieldValues);

				existingDDMFormFieldValues.remove(actualDDMFormFieldValue);
				mergedDDMFormFieldValues.remove(actualDDMFormFieldValue);
			}

			mergedDDMFormFieldValues.add(newDDMFormFieldValue);
		}

		return mergedDDMFormFieldValues;
	}

	private void _mergeValue(Value newValue, Value existingValue) {
		if ((newValue == null) || (existingValue == null)) {
			return;
		}

		for (Locale locale : existingValue.getAvailableLocales()) {
			String value = newValue.getString(locale);

			if (value != null) {
				continue;
			}

			newValue.addString(locale, existingValue.getString(locale));
		}
	}

}