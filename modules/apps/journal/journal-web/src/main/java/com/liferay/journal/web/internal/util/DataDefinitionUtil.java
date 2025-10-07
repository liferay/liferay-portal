/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.util;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutColumn;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutPage;
import com.liferay.data.engine.rest.dto.v2_0.DataLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.util.DDMFormFieldUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Attila Bakay
 */
public class DataDefinitionUtil {

	public static boolean isValidFieldName(String fieldName) {
		int index = fieldName.length() - 8;

		if ((index >= 0) && Validator.isNumber(fieldName.substring(index))) {
			return true;
		}

		return false;
	}

	public static void updateDataDefinitionFields(
		DataDefinition dataDefinition, DDMStructure ddmStructure) {

		for (DataDefinitionField dataDefinitionField :
				dataDefinition.getDataDefinitionFields()) {

			String oldFieldName = dataDefinitionField.getName();

			String newFieldName = _getFieldName(
				dataDefinitionField, ddmStructure, oldFieldName);

			if (StringUtil.equals(newFieldName, oldFieldName)) {
				continue;
			}

			dataDefinitionField.setName(() -> newFieldName);

			DataLayout dataLayout = dataDefinition.getDefaultDataLayout();

			_updateDataLayoutFieldName(dataLayout, newFieldName, oldFieldName);
		}
	}

	private static String _getExistingFieldName(
		DataDefinitionField dataDefinitionField, DDMStructure ddmStructure) {

		Map<String, Object> customProperties =
			dataDefinitionField.getCustomProperties();

		if (customProperties == null) {
			return null;
		}

		String fieldReference = MapUtil.getString(
			customProperties, "fieldReference");

		if (Validator.isNull(fieldReference)) {
			return null;
		}

		DDMForm ddmForm = ddmStructure.getDDMForm();

		Map<String, DDMFormField> ddmFormFieldsReferencesMap =
			ddmForm.getDDMFormFieldsReferencesMap(true);

		if (ddmFormFieldsReferencesMap.containsKey(fieldReference)) {
			DDMFormField ddmFormField = ddmFormFieldsReferencesMap.get(
				fieldReference);

			return ddmFormField.getName();
		}

		return null;
	}

	private static String _getFieldName(
		DataDefinitionField dataDefinitionField, DDMStructure ddmStructure,
		String fieldName) {

		if (ddmStructure != null) {
			String existingFieldName = _getExistingFieldName(
				dataDefinitionField, ddmStructure);

			if (existingFieldName != null) {
				return existingFieldName;
			}
		}

		if (isValidFieldName(fieldName)) {
			return fieldName;
		}

		return DDMFormFieldUtil.getDDMFormFieldName(fieldName);
	}

	private static void _updateDataLayoutFieldName(
		DataLayout dataLayout, String newFieldName, String oldFieldName) {

		for (DataLayoutPage dataLayoutPage : dataLayout.getDataLayoutPages()) {
			for (DataLayoutRow dataLayoutRow :
					dataLayoutPage.getDataLayoutRows()) {

				for (DataLayoutColumn dataLayoutColumn :
						dataLayoutRow.getDataLayoutColumns()) {

					String[] dataLayoutColumnFieldNames =
						dataLayoutColumn.getFieldNames();

					for (int i = 0; i < dataLayoutColumnFieldNames.length;
						 i++) {

						if (dataLayoutColumnFieldNames[i].equals(
								oldFieldName)) {

							dataLayoutColumnFieldNames[i] = newFieldName;

							return;
						}
					}
				}
			}
		}
	}

}