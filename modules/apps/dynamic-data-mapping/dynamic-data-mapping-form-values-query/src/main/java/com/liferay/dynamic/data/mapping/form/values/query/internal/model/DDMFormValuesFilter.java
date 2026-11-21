/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.values.query.internal.model;

import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author Pablo Carvalho
 */
public class DDMFormValuesFilter {

	public List<DDMFormFieldValue> filter(DDMFormValues ddmFormValues) {
		return filter(ddmFormValues.getDDMFormFieldValues());
	}

	public List<DDMFormFieldValue> filter(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		if (!_greedy) {
			return ListUtil.filter(
				ddmFormFieldValues,
				ddmFormFieldValue -> _ddmFormFieldValueMatcher.matches(
					ddmFormFieldValue));
		}

		List<DDMFormFieldValue> filteredDDMFormFieldValues = new ArrayList<>();

		Queue<DDMFormFieldValue> queue = new ArrayDeque<>(ddmFormFieldValues);

		DDMFormFieldValue ddmFormFieldValue = null;

		while ((ddmFormFieldValue = queue.poll()) != null) {
			queue.addAll(ddmFormFieldValue.getNestedDDMFormFieldValues());

			if (!_ddmFormFieldValueMatcher.matches(ddmFormFieldValue)) {
				continue;
			}

			filteredDDMFormFieldValues.add(ddmFormFieldValue);
		}

		return filteredDDMFormFieldValues;
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

	private DDMFormFieldValueMatcher _ddmFormFieldValueMatcher;
	private boolean _greedy;

}