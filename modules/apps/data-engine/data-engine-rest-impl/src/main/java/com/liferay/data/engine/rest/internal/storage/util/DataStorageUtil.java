/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.internal.storage.util;

import com.liferay.data.engine.rest.dto.v2_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.data.engine.rest.strategy.util.DataRecordValueKeyUtil;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Jeyvison Nascimento
 * @author Leonardo Barros
 */
public class DataStorageUtil {

	public static Map<String, Object> toDataRecordValues(
			DDMFormValues ddmFormValues, DDMStructure ddmStructure)
		throws PortalException {

		if (ddmFormValues == null) {
			return Collections.emptyMap();
		}

		_addMissingDDMFormFieldValues(
			ddmStructure.getFullHierarchyDDMFormFieldsMap(false),
			ddmFormValues);

		Map<String, Object> values = new HashMap<>();

		String previousDDMFormFieldValueName = StringPool.BLANK;
		Integer repeatableIndex = 0;

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			String ddmFormFieldValueName = ddmFormFieldValue.getName();

			repeatableIndex = _updateRepeatableIndex(
				ddmFormFieldValueName, previousDDMFormFieldValueName,
				repeatableIndex);

			previousDDMFormFieldValueName = ddmFormFieldValueName;

			_addValues(
				ddmStructure.getFullHierarchyDDMFormFieldsMap(true),
				ddmFormFieldValue, StringPool.BLANK, repeatableIndex, values);
		}

		return values;
	}

	public static String toJSON(
		DataDefinition dataDefinition, Map<String, ?> dataRecordValues) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Map<String, DataDefinitionField> dataDefinitionFields = new HashMap<>();

		for (DataDefinitionField dataDefinitionField :
				dataDefinition.getDataDefinitionFields()) {

			dataDefinitionFields.put(
				dataDefinitionField.getName(), dataDefinitionField);
		}

		for (Map.Entry<String, DataDefinitionField> entry :
				dataDefinitionFields.entrySet()) {

			if (!dataRecordValues.containsKey(entry.getKey())) {
				continue;
			}

			DataDefinitionField dataDefinitionField = entry.getValue();

			if (dataDefinitionField.getRepeatable()) {
				jsonObject.put(
					entry.getKey(),
					JSONFactoryUtil.createJSONArray(
						(List<Object>)dataRecordValues.get(entry.getKey())));
			}
			else {
				jsonObject.put(
					entry.getKey(), dataRecordValues.get(entry.getKey()));
			}
		}

		return jsonObject.toString();
	}

	private static void _addMissingDDMFormFieldValues(
		Map<String, DDMFormField> ddmFormFields, DDMFormValues ddmFormValues) {

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap(false);

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		for (DDMFormField ddmFormField : ddmFormFields.values()) {
			ddmFormFieldValues.addAll(
				_getDDMFormFieldValues(
					ddmFormField,
					ddmFormFieldValuesMap.get(ddmFormField.getName()),
					ddmFormValues.getDefaultLocale()));
		}

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);
	}

	private static void _addValues(
		Map<String, DDMFormField> ddmFormFields,
		DDMFormFieldValue ddmFormFieldValue, String parentDataRecordValueKey,
		Integer repeatableIndex, Map<String, Object> values) {

		DDMFormField ddmFormField = ddmFormFields.get(
			ddmFormFieldValue.getName());

		if (ddmFormField == null) {
			return;
		}

		String dataRecordValueKey =
			DataRecordValueKeyUtil.createDataRecordValueKey(
				ddmFormField.getName(), ddmFormFieldValue.getInstanceId(),
				parentDataRecordValueKey, repeatableIndex);

		if (StringUtil.equals(
				ddmFormField.getType(), DDMFormFieldTypeConstants.FIELDSET)) {

			values.put(dataRecordValueKey, StringPool.BLANK);

			String previousNestedDDMFormFieldValueName = StringPool.BLANK;
			Integer nestedRepeatableIndex = 0;

			for (DDMFormFieldValue nestedDDMFormFieldValue :
					ddmFormFieldValue.getNestedDDMFormFieldValues()) {

				String nestedDDMFormFieldValueName =
					nestedDDMFormFieldValue.getName();

				nestedRepeatableIndex = _updateRepeatableIndex(
					nestedDDMFormFieldValueName,
					previousNestedDDMFormFieldValueName, nestedRepeatableIndex);

				previousNestedDDMFormFieldValueName =
					nestedDDMFormFieldValueName;

				_addValues(
					ddmFormFields, nestedDDMFormFieldValue, dataRecordValueKey,
					nestedRepeatableIndex, values);
			}
		}
		else {
			Value value = ddmFormFieldValue.getValue();

			if (value == null) {
				values.put(dataRecordValueKey, null);
			}
			else {
				if (ddmFormField.isLocalizable() &&
					!ddmFormField.isTransient()) {

					values.put(
						dataRecordValueKey,
						_toLocalizedMap(
							ddmFormField.getType(), (LocalizedValue)value));
				}
				else {
					values.put(
						dataRecordValueKey,
						GetterUtil.getString(
							value.getString(value.getDefaultLocale())));
				}
			}
		}
	}

	private static DDMFormFieldValue _createDDMFormFieldValue(
		DDMFormField ddmFormField, Locale locale) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue() {
			{
				setName(ddmFormField.getName());
			}
		};

		if (ddmFormField.isLocalizable() && !ddmFormField.isTransient()) {
			Value value = new LocalizedValue(locale);

			value.addString(locale, StringPool.BLANK);

			ddmFormFieldValue.setValue(value);
		}
		else {
			ddmFormFieldValue.setValue(new UnlocalizedValue(StringPool.BLANK));
		}

		return ddmFormFieldValue;
	}

	private static List<DDMFormFieldValue> _getDDMFormFieldValues(
		DDMFormField ddmFormField, List<DDMFormFieldValue> ddmFormFieldValues,
		Locale locale) {

		if (ListUtil.isEmpty(ddmFormFieldValues)) {
			ddmFormFieldValues = Arrays.asList(
				_createDDMFormFieldValue(ddmFormField, locale));
		}

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (!StringUtil.equals(
					ddmFormField.getType(),
					DDMFormFieldTypeConstants.FIELDSET)) {

				continue;
			}

			List<DDMFormFieldValue> nestedDDMFormFieldValues =
				new ArrayList<>();

			for (DDMFormField nestedDDMFormField :
					ddmFormField.getNestedDDMFormFields()) {

				Map<String, List<DDMFormFieldValue>>
					nestedDDMFormFieldValuesMap =
						ddmFormFieldValue.getNestedDDMFormFieldValuesMap();

				nestedDDMFormFieldValues.addAll(
					_getDDMFormFieldValues(
						nestedDDMFormField,
						nestedDDMFormFieldValuesMap.get(
							nestedDDMFormField.getName()),
						locale));
			}

			ddmFormFieldValue.setNestedDDMFormFields(nestedDDMFormFieldValues);
		}

		return ddmFormFieldValues;
	}

	private static Map<String, Object> _toLocalizedMap(
		String fieldType, LocalizedValue localizedValue) {

		Map<String, Object> localizedMap = new HashMap<>();

		Function<Locale, Object> function = localizedValue::getString;

		if (fieldType.equals(DDMFormFieldType.CHECKBOX_MULTIPLE) ||
			fieldType.equals(DDMFormFieldType.SELECT)) {

			function = locale -> _toStringList(locale, localizedValue);
		}

		for (Locale locale : localizedValue.getAvailableLocales()) {
			localizedMap.put(
				LanguageUtil.getLanguageId(locale), function.apply(locale));
		}

		return localizedMap;
	}

	private static List<String> _toStringList(
		Locale locale, LocalizedValue localizedValue) {

		try {
			return JSONUtil.toStringList(
				JSONFactoryUtil.createJSONArray(
					localizedValue.getString(locale)));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return Collections.emptyList();
		}
	}

	private static Integer _updateRepeatableIndex(
		String ddmFormFieldValueName, String previousDDMFormFieldValueName,
		Integer repeatableIndex) {

		if (!StringUtil.equals(
				ddmFormFieldValueName, previousDDMFormFieldValueName)) {

			return 0;
		}

		return ++repeatableIndex;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DataStorageUtil.class);

}