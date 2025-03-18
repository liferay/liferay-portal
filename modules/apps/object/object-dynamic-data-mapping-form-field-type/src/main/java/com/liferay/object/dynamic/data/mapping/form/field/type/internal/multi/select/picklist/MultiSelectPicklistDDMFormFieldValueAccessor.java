/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.dynamic.data.mapping.form.field.type.internal.multi.select.picklist;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldValueUtil;
import com.liferay.object.dynamic.data.mapping.form.field.type.constants.ObjectDDMFormFieldTypeConstants;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pedro Leite
 */
@Component(
	property = "ddm.form.field.type.name=" + ObjectDDMFormFieldTypeConstants.MULTISELECT_PICKLIST,
	service = DDMFormFieldValueAccessor.class
)
public class MultiSelectPicklistDDMFormFieldValueAccessor
	implements DDMFormFieldValueAccessor<JSONArray> {

	@Override
	public JSONArray getValue(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		return DDMFormFieldValueUtil.getOptionsValuesJSONArray(
			ddmFormFieldValue, locale);
	}

	@Override
	public JSONArray getValueForEvaluation(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		return getValue(ddmFormFieldValue, locale);
	}

	@Override
	public boolean isEmpty(DDMFormFieldValue ddmFormFieldValue, Locale locale) {
		JSONArray jsonArray = getValue(ddmFormFieldValue, locale);

		if (JSONUtil.isEmpty(jsonArray)) {
			return true;
		}

		return false;
	}

}