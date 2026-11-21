/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.values.query.internal.model;

import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

/**
 * @author Pablo Carvalho
 */
public class DDMFormValuesFilter {

	public List<DDMFormFieldValue> filter(DDMFormValues ddmFormValues) {
		List<DDMFormFieldValue> ddmFormFieldValues = ListUtil.copy(
			ddmFormValues.getDDMFormFieldValues());

		return filter(ddmFormFieldValues);
	}

	public List<DDMFormFieldValue> filter(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		if (_greedy) {
			_addNestedDDMFormFieldValues(ddmFormFieldValues);
		}

		return ListUtil.filter(
			ddmFormFieldValues,
			ddmFormFieldValue -> _ddmFormFieldValueMatcher.matches(
				ddmFormFieldValue));
	}

	public boolean isGreedy() {
		return _greedy;
	}

	public void setDDMFormFieldValueMatcher(
		DDMFormFieldValueMatcher ddmFormFieldValueMatcher) {

		_ddmFormFieldValueMatcher = ddmFormFieldValueMatcher;
	}

	public void setGreedy(boolean greedy) {
		_greedy = greedy;
	}

	private void _addNestedDDMFormFieldValues(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		for (int i = 0; i < ddmFormFieldValues.size(); i++) {
			DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(i);

			ddmFormFieldValues.addAll(
				ddmFormFieldValue.getNestedDDMFormFieldValues());
		}
	}

	private DDMFormFieldValueMatcher _ddmFormFieldValueMatcher;
	private boolean _greedy;

}