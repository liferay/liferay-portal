/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.validator;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.exception.FragmentEntryConfigurationException;
import com.liferay.fragment.exception.FragmentEntryFieldTypesException;
import com.liferay.fragment.exception.FragmentEntryTypeOptionsException;
import com.liferay.fragment.validator.FragmentEntryValidator;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.json.validator.JSONValidator;
import com.liferay.portal.json.validator.JSONValidatorException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(service = FragmentEntryValidator.class)
public class FragmentEntryValidatorImpl implements FragmentEntryValidator {

	@Override
	public void validateConfiguration(JSONObject configurationJSONObject)
		throws FragmentEntryConfigurationException {

		validateConfigurationValues(configurationJSONObject, null);
	}

	@Override
	public void validateConfigurationValues(
			JSONObject configurationJSONObject, JSONObject valuesJSONObject)
		throws FragmentEntryConfigurationException {

		if (configurationJSONObject == null) {
			return;
		}

		try {
			_validateConfigurationValues(
				configurationJSONObject, valuesJSONObject);
		}
		catch (Exception exception) {
			throw new FragmentEntryConfigurationException(
				_getMessage(exception.getMessage()), exception);
		}
	}

	@Override
	public void validateTypeOptions(int fragmentEntryType, String typeOptions)
		throws FragmentEntryTypeOptionsException {

		if (Validator.isNull(typeOptions)) {
			return;
		}

		try {
			_typeOptionsJSONValidator.validate(typeOptions);

			JSONObject configurationJSONObject = _jsonFactory.createJSONObject(
				typeOptions);

			JSONArray fieldTypesJSONArray =
				configurationJSONObject.getJSONArray("fieldTypes");

			if (!Objects.equals(
					FragmentConstants.TYPE_INPUT, fragmentEntryType)) {

				if (!JSONUtil.isEmpty(fieldTypesJSONArray)) {
					throw new FragmentEntryFieldTypesException(
						"Only fragment type input can have field types");
				}

				return;
			}

			if (JSONUtil.isEmpty(fieldTypesJSONArray)) {
				throw new FragmentEntryFieldTypesException(
					"Fragment type input must have at least one field type");
			}

			if ((fieldTypesJSONArray.length() > 1) &&
				JSONUtil.hasValue(fieldTypesJSONArray, "captcha")) {

				throw new FragmentEntryFieldTypesException(
					"Captcha field type cannot be mixed with other field " +
						"types");
			}

			if ((fieldTypesJSONArray.length() > 1) &&
				JSONUtil.hasValue(fieldTypesJSONArray, "stepper")) {

				throw new FragmentEntryFieldTypesException(
					"Stepper field type cannot be mixed with other field " +
						"types");
			}
		}
		catch (JSONException jsonException) {
			throw new FragmentEntryTypeOptionsException(
				_getMessage(jsonException.getMessage()), jsonException);
		}
		catch (JSONValidatorException jsonValidatorException) {
			throw new FragmentEntryTypeOptionsException(jsonValidatorException);
		}
	}

	private boolean _checkValidationRules(
		String value, JSONObject validationJSONObject) {

		if (Validator.isNull(value) || (validationJSONObject == null)) {
			return true;
		}

		String type = validationJSONObject.getString("type");

		if (Objects.equals(type, "email")) {
			return Validator.isEmailAddress(value);
		}
		else if (Objects.equals(type, "number")) {
			long max = validationJSONObject.getLong("max", Long.MAX_VALUE);
			long min = validationJSONObject.getLong("min", Long.MIN_VALUE);

			boolean valid = false;

			if (Validator.isNumber(value) &&
				(GetterUtil.getLong(value) <= max) &&
				(GetterUtil.getLong(value) >= min)) {

				valid = true;
			}

			return valid;
		}
		else if (Objects.equals(type, "pattern")) {
			String regexp = validationJSONObject.getString("regexp");

			return value.matches(regexp);
		}
		else if (Objects.equals(type, "url")) {
			return Validator.isUrl(value);
		}

		long maxLength = validationJSONObject.getLong(
			"maxLength", Long.MAX_VALUE);
		long minLength = validationJSONObject.getLong(
			"minLength", Long.MIN_VALUE);

		if ((value.length() <= maxLength) && (value.length() >= minLength)) {
			return true;
		}

		return false;
	}

	private String _getMessage(String message) {
		return StringBundler.concat(
			_language.get(
				LocaleUtil.getDefault(), "fragment-configuration-is-invalid"),
			System.lineSeparator(), message);
	}

	private void _validateConfigurationValues(
			JSONObject configurationJSONObject, JSONObject valuesJSONObject)
		throws Exception {

		_configurationJSONValidator.validate(
			_jsonFactory.toString(configurationJSONObject));

		Set<String> fieldNames = new HashSet<>();

		JSONArray fieldSetsJSONArray = configurationJSONObject.getJSONArray(
			"fieldSets");

		for (int i = 0; i < fieldSetsJSONArray.length(); i++) {
			JSONObject fieldSetJSONObject = fieldSetsJSONArray.getJSONObject(i);

			JSONArray fieldsJSONArray = fieldSetJSONObject.getJSONArray(
				"fields");

			Map<String, JSONObject> fieldJSONObjects = new HashMap<>(
				fieldsJSONArray.length());

			for (int j = 0; j < fieldsJSONArray.length(); j++) {
				JSONObject fieldJSONObject = fieldsJSONArray.getJSONObject(j);

				String fieldName = fieldJSONObject.getString("name");

				if (fieldNames.contains(fieldName)) {
					throw new FragmentEntryConfigurationException(
						"Field names must be unique");
				}

				fieldNames.add(fieldName);

				fieldJSONObjects.put(fieldName, fieldJSONObject);
			}

			for (Map.Entry<String, JSONObject> entry :
					fieldJSONObjects.entrySet()) {

				JSONObject fieldJSONObject = entry.getValue();

				JSONObject typeOptionsJSONObject =
					fieldJSONObject.getJSONObject("typeOptions");

				if (typeOptionsJSONObject == null) {
					continue;
				}

				String fieldName = entry.getKey();

				String defaultValue = fieldJSONObject.getString("defaultValue");

				if (!_checkValidationRules(
						defaultValue,
						typeOptionsJSONObject.getJSONObject("validation"))) {

					throw new FragmentEntryConfigurationException(
						"Invalid default configuration value for field \"" +
							fieldName + "\"");
				}

				if (valuesJSONObject != null) {
					String value = valuesJSONObject.getString(fieldName);

					if (!_checkValidationRules(
							value,
							typeOptionsJSONObject.getJSONObject(
								"validation"))) {

						throw new FragmentEntryConfigurationException(
							"Invalid configuration value for field \"" +
								fieldName + "\"");
					}
				}

				JSONObject dependencyJSONObject =
					typeOptionsJSONObject.getJSONObject("dependency");

				if (dependencyJSONObject == null) {
					continue;
				}

				for (String key : dependencyJSONObject.keySet()) {
					if (key.equals(fieldName)) {
						throw new FragmentEntryConfigurationException(
							"Dependency field cannot reference itself");
					}

					if (!fieldJSONObjects.containsKey(key)) {
						throw new FragmentEntryConfigurationException(
							"Dependency field cannot depend on field \"" + key +
								"\" that does not exist");
					}

					JSONObject dependencyFieldJSONObject = fieldJSONObjects.get(
						key);

					if (!_allowedDependencyTypes.contains(
							dependencyFieldJSONObject.getString("type"))) {

						throw new FragmentEntryConfigurationException(
							"Dependency field type should be checkbox, " +
								"select, or text");
					}
				}
			}
		}
	}

	private static final Set<String> _allowedDependencyTypes =
		SetUtil.fromArray("checkbox", "select", "text");
	private static final JSONValidator _configurationJSONValidator =
		new JSONValidator(
			FragmentEntryValidatorImpl.class.getResource(
				"dependencies/configuration-json-schema.json"));
	private static final JSONValidator _typeOptionsJSONValidator =
		new JSONValidator(
			FragmentEntryValidatorImpl.class.getResource(
				"dependencies/type-options-json-schema.json"));

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

}