/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.security.RandomUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Carolina Barbosa
 */
public class DDMFormFieldUtil {

	public static String getDDMFormFieldName(String ddmFormFieldName) {
		for (int i = 0; i < _DDM_FORM_FIELD_NAME_RANDOM_NUMBERS_LENGTH; i++) {
			ddmFormFieldName = ddmFormFieldName.concat(
				String.valueOf(RandomUtil.nextInt(10)));
		}

		return StringUtil.removeChar(ddmFormFieldName, CharPool.SPACE);
	}

	public static String getLegacyDDMFormFieldName(String ddmFormFieldName) {
		int index = ddmFormFieldName.length() - 8;

		if ((index >= 0) &&
			Validator.isNumber(ddmFormFieldName.substring(index))) {

			return ddmFormFieldName.substring(0, index);
		}

		return ddmFormFieldName;
	}

	public static void sortNestedDDMFormFields(List<DDMFormField> ddmFormFields)
		throws Exception {

		for (DDMFormField ddmFormField : ddmFormFields) {
			if (!StringUtil.equals(
					ddmFormField.getType(),
					DDMFormFieldTypeConstants.FIELDSET)) {

				continue;
			}

			JSONArray rowsJSONArray = null;

			if (ddmFormField.getProperty("rows") instanceof String) {
				rowsJSONArray = JSONFactoryUtil.createJSONArray(
					GetterUtil.getString(ddmFormField.getProperty("rows")));
			}
			else {
				rowsJSONArray = JSONFactoryUtil.createJSONArray(
					JSONFactoryUtil.looseSerializeDeep(
						ddmFormField.getProperty("rows")));
			}

			Map<String, DDMFormField> nestedDDMFormFieldsMap =
				ddmFormField.getNestedDDMFormFieldsMap();

			if (nestedDDMFormFieldsMap.isEmpty()) {
				continue;
			}

			List<DDMFormField> sortedNestedDDMFormFields = new ArrayList<>();

			for (int i = 0; i < rowsJSONArray.length(); i++) {
				JSONObject rowJSONObject = rowsJSONArray.getJSONObject(i);

				if (rowJSONObject == null) {
					rowJSONObject = JSONFactoryUtil.createJSONObject(
						(String)rowsJSONArray.get(i));
				}

				JSONArray columnsJSONArray = rowJSONObject.getJSONArray(
					"columns");

				for (int j = 0; j < columnsJSONArray.length(); j++) {
					JSONObject columnJSONObject =
						columnsJSONArray.getJSONObject(j);

					for (String fieldName :
							JSONUtil.toStringList(
								columnJSONObject.getJSONArray("fields"))) {

						DDMFormField nestedDDMFormField =
							nestedDDMFormFieldsMap.get(fieldName);

						if (nestedDDMFormField == null) {
							continue;
						}

						if (StringUtil.equals(
								nestedDDMFormField.getType(),
								DDMFormFieldTypeConstants.FIELDSET)) {

							sortNestedDDMFormFields(
								ListUtil.toList(nestedDDMFormField));
						}

						sortedNestedDDMFormFields.add(nestedDDMFormField);
					}
				}
			}

			ddmFormField.setNestedDDMFormFields(sortedNestedDDMFormFields);
		}
	}

	private static final int _DDM_FORM_FIELD_NAME_RANDOM_NUMBERS_LENGTH = 8;

}