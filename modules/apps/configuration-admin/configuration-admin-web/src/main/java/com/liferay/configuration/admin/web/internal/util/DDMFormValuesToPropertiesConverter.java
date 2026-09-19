/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.LocationVariableProtocol;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import org.osgi.service.metatype.AttributeDefinition;

/**
 * @author Kamesh Sampath
 * @author Raymond Augé
 * @author Marcellus Tavares
 */
public class DDMFormValuesToPropertiesConverter {

	public DDMFormValuesToPropertiesConverter(
		ConfigurationModel configurationModel, DDMFormValues ddmFormValues,
		JSONFactory jsonFactory, Locale locale) {

		this(configurationModel, ddmFormValues, jsonFactory, locale, null);
	}

	public DDMFormValuesToPropertiesConverter(
		ConfigurationModel configurationModel, DDMFormValues ddmFormValues,
		JSONFactory jsonFactory, Locale locale,
		LocationVariableResolver locationVariableResolver) {

		_configurationModel = configurationModel;
		_jsonFactory = jsonFactory;
		_locale = locale;
		_locationVariableResolver = locationVariableResolver;

		DDMForm ddmForm = ddmFormValues.getDDMForm();

		_defaultLocale = ddmForm.getDefaultLocale();
		_ddmFormFieldsMap = ddmForm.getDDMFormFieldsMap(false);

		_ddmFormFieldValuesMap = ddmFormValues.getDDMFormFieldValuesMap();
		_ddmFormFieldValuesReferencesMap =
			ddmFormValues.getDDMFormFieldValuesReferencesMap(false);
	}

	public Dictionary<String, Object> getProperties() {
		Dictionary<String, Object> properties = new Hashtable<>();

		AttributeDefinition[] attributeDefinitions =
			_configurationModel.getAttributeDefinitions(ConfigurationModel.ALL);

		for (AttributeDefinition attributeDefinition : attributeDefinitions) {
			Object value = null;

			List<DDMFormFieldValue> ddmFormFieldValues =
				_ddmFormFieldValuesMap.get(attributeDefinition.getID());

			if (ddmFormFieldValues == null) {
				ddmFormFieldValues = _ddmFormFieldValuesReferencesMap.get(
					attributeDefinition.getID());
			}

			if (attributeDefinition.getCardinality() == 0) {
				value = _toSimpleValue(ddmFormFieldValues.get(0));
			}
			else if (attributeDefinition.getCardinality() > 0) {
				value = _toArrayValue(ddmFormFieldValues);
			}
			else if (attributeDefinition.getCardinality() < 0) {
				value = _toVectorValue(ddmFormFieldValues);
			}

			String[] defaultValues = attributeDefinition.getDefaultValue();

			if ((ArrayUtil.getLength(defaultValues) == 1) &&
				_isDefaultResourceValue(
					defaultValues[0], attributeDefinition.getType(), value)) {

				value = defaultValues[0];
			}

			properties.put(attributeDefinition.getID(), value);
		}

		return properties;
	}

	protected String getDDMFormFieldDataType(String fieldName) {
		DDMFormField ddmFormField = _ddmFormFieldsMap.get(fieldName);

		return ddmFormField.getDataType();
	}

	protected String getDDMFormFieldType(String fieldName) {
		DDMFormField ddmFormField = _ddmFormFieldsMap.get(fieldName);

		return ddmFormField.getType();
	}

	private String _getDDMFormFieldValueString(
		DDMFormFieldValue ddmFormFieldValue) {

		Value value = ddmFormFieldValue.getValue();

		String valueString = value.getString(_locale);

		String type = getDDMFormFieldType(ddmFormFieldValue.getName());

		if (type.equals(DDMFormFieldType.SELECT)) {
			try {
				JSONArray jsonArray = _jsonFactory.createJSONArray(valueString);

				if (jsonArray.length() == 1) {
					valueString = jsonArray.getString(0);
				}
				else if (jsonArray.length() == 0) {
					valueString = StringPool.BLANK;
				}
			}
			catch (JSONException jsonException) {
				ReflectionUtil.throwException(jsonException);
			}
		}

		if (valueString.equals(StringPool.BLANK)) {
			String dataType = getDDMFormFieldDataType(
				ddmFormFieldValue.getName());

			valueString = _getDataTypeDefaultValue(dataType);
		}

		return valueString;
	}

	private String _getDataTypeDefaultValue(String dataType) {
		if (dataType.equals(FieldConstants.BOOLEAN)) {
			return "false";
		}
		else if (dataType.equals(FieldConstants.DOUBLE) ||
				 dataType.equals(FieldConstants.FLOAT)) {

			return "0.0";
		}
		else if (dataType.equals(FieldConstants.INTEGER) ||
				 dataType.equals(FieldConstants.LONG) ||
				 dataType.equals(FieldConstants.SHORT)) {

			return "0";
		}

		return StringPool.BLANK;
	}

	private boolean _isDefaultResourceValue(
		String defaultValue, int type, Object value) {

		if ((_locationVariableResolver == null) ||
			(!_locationVariableResolver.isLocationVariable(
				defaultValue, LocationVariableProtocol.LANGUAGE) &&
			 !_locationVariableResolver.isLocationVariable(
				 defaultValue, LocationVariableProtocol.RESOURCE))) {

			return false;
		}

		String resolvedDefaultValue = _locationVariableResolver.resolve(
			defaultValue);

		if (Objects.equals(resolvedDefaultValue, value)) {
			return true;
		}

		String stringValue = String.valueOf(value);

		if ((type == ExtendedAttributeDefinition.LOCALIZED_VALUES_MAP) &&
			JSONUtil.isJSONObject(stringValue)) {

			try {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					stringValue);

				if ((jsonObject.length() == 1) &&
					Objects.equals(
						jsonObject.get(LocaleUtil.toLanguageId(_defaultLocale)),
						resolvedDefaultValue)) {

					return true;
				}
			}
			catch (JSONException jsonException) {
				_log.error(jsonException);
			}
		}

		return false;
	}

	private Serializable _toArrayValue(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

		String dataType = getDDMFormFieldDataType(ddmFormFieldValue.getName());

		Vector<Serializable> values = _toVectorValue(ddmFormFieldValues);

		return FieldConstants.getSerializable(dataType, values);
	}

	private Serializable _toSimpleValue(DDMFormFieldValue ddmFormFieldValue) {
		String dataType = getDDMFormFieldDataType(ddmFormFieldValue.getName());

		String valueString = _getDDMFormFieldValueString(ddmFormFieldValue);

		return FieldConstants.getSerializable(dataType, valueString);
	}

	private Vector<Serializable> _toVectorValue(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		Vector<Serializable> values = new Vector<>();

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			Serializable simpleDDMFormFieldValue = _toSimpleValue(
				ddmFormFieldValue);

			if (!Validator.isBlank(simpleDDMFormFieldValue.toString())) {
				values.add(simpleDDMFormFieldValue);
			}
		}

		return values;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormValuesToPropertiesConverter.class);

	private final ConfigurationModel _configurationModel;
	private final Map<String, List<DDMFormFieldValue>> _ddmFormFieldValuesMap;
	private final Map<String, List<DDMFormFieldValue>>
		_ddmFormFieldValuesReferencesMap;
	private final Map<String, DDMFormField> _ddmFormFieldsMap;
	private final Locale _defaultLocale;
	private final JSONFactory _jsonFactory;
	private final Locale _locale;
	private LocationVariableResolver _locationVariableResolver;

}