/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.context;

import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializerRequest;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextVisitor;
import com.liferay.dynamic.data.mapping.form.builder.rule.DDMFormRuleDeserializer;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "dynamic.data.mapping.form.builder.context.deserializer.type=form",
	service = DDMFormContextDeserializer.class
)
public class DDMFormContextToDDMForm
	implements DDMFormContextDeserializer<DDMForm> {

	@Override
	public DDMForm deserialize(
			DDMFormContextDeserializerRequest ddmFormContextDeserializerRequest)
		throws PortalException {

		String serializedFormContext =
			ddmFormContextDeserializerRequest.getProperty(
				"serializedFormContext");

		if (Validator.isNull(serializedFormContext)) {
			throw new IllegalStateException(
				"The property \"serializedFormContext\" is required");
		}

		return deserialize(serializedFormContext);
	}

	protected LocalizedValue createLocalizedValue(
		JSONObject jsonObject, Locale defaultLocale) {

		LocalizedValue localizedValue = new LocalizedValue(defaultLocale);

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String languageId = iterator.next();

			localizedValue.addString(
				LocaleUtil.fromLanguageId(languageId),
				jsonObject.getString(languageId));
		}

		return localizedValue;
	}

	protected DDMForm deserialize(String serializedFormContext)
		throws PortalException {

		DDMForm ddmForm = new DDMForm();

		JSONObject jsonObject = jsonFactory.createJSONObject(
			serializedFormContext);

		_setDDMFormAvailableLocales(
			jsonObject.getJSONArray("availableLanguageIds"), ddmForm);
		_setDDMFormDefaultLocale(
			jsonObject.getString("defaultLanguageId"), ddmForm);
		_setDDMFormFields(jsonObject.getJSONArray("pages"), ddmForm);
		_setDDMFormRules(jsonObject.getJSONArray("rules"), ddmForm);
		_setDDMFormSuccessPageSettings(
			jsonObject.getJSONObject("successPageSettings"), ddmForm);

		return ddmForm;
	}

	protected Set<Locale> getAvailableLocales(JSONArray jsonArray) {
		Set<Locale> availableLocales = new HashSet<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			availableLocales.add(
				LocaleUtil.fromLanguageId(jsonArray.getString(i)));
		}

		return availableLocales;
	}

	protected DDMFormFieldValidation getDDMFormFieldValidation(
			Set<Locale> availableLocales, String dataType, Locale defaultLocale,
			String serializedValue)
		throws PortalException {

		if (Validator.isNull(serializedValue)) {
			return null;
		}

		JSONObject jsonObject = jsonFactory.createJSONObject(serializedValue);

		JSONObject expressionJSONObject = jsonObject.getJSONObject(
			"expression");

		if (!StringUtil.equals(dataType, DDMFormFieldTypeConstants.DATE) &&
			Validator.isNull(expressionJSONObject.getString("value"))) {

			return null;
		}

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		if (expressionJSONObject != null) {
			ddmFormFieldValidation.setDDMFormFieldValidationExpression(
				new DDMFormFieldValidationExpression() {
					{
						setName(expressionJSONObject.getString("name"));
						setValue(expressionJSONObject.getString("value"));
					}
				});
		}

		LocalizedValue errorMessageLocalizedValue = null;

		JSONObject errorMessageJSONObject = jsonObject.getJSONObject(
			"errorMessage");

		if (errorMessageJSONObject == null) {
			errorMessageLocalizedValue = new LocalizedValue();

			errorMessageLocalizedValue.addString(
				defaultLocale, jsonObject.getString("errorMessage"));
		}
		else {
			errorMessageLocalizedValue = getLocalizedValue(
				errorMessageJSONObject, availableLocales, defaultLocale);
		}

		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			errorMessageLocalizedValue);

		LocalizedValue parameterLocalizedValue = null;

		JSONObject parameterJSONObject = jsonObject.getJSONObject("parameter");

		if (parameterJSONObject == null) {
			parameterLocalizedValue = new LocalizedValue();

			parameterLocalizedValue.addString(
				defaultLocale, jsonObject.getString("parameter"));
		}
		else {
			parameterLocalizedValue = getParameterLocalizedValue(
				parameterJSONObject, availableLocales, dataType, defaultLocale);
		}

		ddmFormFieldValidation.setParameterLocalizedValue(
			parameterLocalizedValue);

		return ddmFormFieldValidation;
	}

	protected LocalizedValue getLocalizedValue(
		JSONObject jsonObject, Set<Locale> availableLocales,
		Locale defaultLocale) {

		LocalizedValue localizedValue = new LocalizedValue(defaultLocale);

		if (jsonObject == null) {
			return localizedValue;
		}

		String defaultValueString = jsonObject.getString(
			LocaleUtil.toLanguageId(defaultLocale));

		for (Locale availableLocale : availableLocales) {
			localizedValue.addString(
				availableLocale,
				jsonObject.getString(
					LocaleUtil.toLanguageId(availableLocale),
					defaultValueString));
		}

		return localizedValue;
	}

	protected LocalizedValue getLocalizedValue(
			String serializedValue, String type, Set<Locale> availableLocales,
			Locale defaultLocale)
		throws PortalException {

		LocalizedValue localizedValue = new LocalizedValue(defaultLocale);

		JSONObject jsonObject = jsonFactory.createJSONObject(serializedValue);

		String defaultValueString = jsonObject.getString(
			LocaleUtil.toLanguageId(defaultLocale));

		for (Locale availableLocale : availableLocales) {
			String valueString = jsonObject.getString(
				LocaleUtil.toLanguageId(availableLocale), defaultValueString);

			valueString = String.valueOf(
				getValueFromValueAccessor(type, valueString, defaultLocale));

			localizedValue.addString(availableLocale, valueString);
		}

		return localizedValue;
	}

	protected LocalizedValue getParameterLocalizedValue(
		JSONObject jsonObject, Set<Locale> availableLocales, String dataType,
		Locale defaultLocale) {

		if (!StringUtil.equals(dataType, DDMFormFieldTypeConstants.DATE)) {
			return getLocalizedValue(
				jsonObject, availableLocales, defaultLocale);
		}

		LocalizedValue parameterLocalizedValue = new LocalizedValue(
			defaultLocale);

		String value = jsonObject.getString(
			LocaleUtil.toLanguageId(defaultLocale));

		if (Validator.isNull(value)) {
			value = jsonObject.getString(
				LocaleUtil.toLanguageId(
					LocaleThreadLocal.getThemeDisplayLocale()));
		}

		for (Locale availableLocale : availableLocales) {
			parameterLocalizedValue.addString(availableLocale, value);
		}

		return parameterLocalizedValue;
	}

	protected Object getValueFromValueAccessor(
		String type, String serializedValue, Locale defaultLocale) {

		DDMFormFieldValueAccessor<?> ddmFormFieldValueAccessor =
			ddmFormFieldTypeServicesRegistry.getDDMFormFieldValueAccessor(type);

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setValue(new UnlocalizedValue(serializedValue));

		if (ddmFormFieldValueAccessor != null) {
			Object value = ddmFormFieldValueAccessor.getValue(
				ddmFormFieldValue, defaultLocale);

			if (!(value instanceof BigDecimal)) {
				return value;
			}
		}

		return serializedValue;
	}

	@Reference
	protected DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry;

	@Reference
	protected DDMFormRuleDeserializer ddmFormRuleDeserializer;

	@Reference
	protected JSONFactory jsonFactory;

	private Object _getBooleanValue(
		String type, String serializedValue, Locale defaultLocale) {

		Object value = getValueFromValueAccessor(
			type, serializedValue, defaultLocale);

		if (!(value instanceof Boolean)) {
			value = Boolean.valueOf(serializedValue);
		}

		return value;
	}

	private DDMFormFieldOptions _getDDMFormFieldOptions(
		JSONObject jsonObject, Set<Locale> availableLocales,
		Locale defaultLocale) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		for (Locale availableLocale : availableLocales) {
			JSONArray jsonArray = jsonObject.getJSONArray(
				LocaleUtil.toLanguageId(availableLocale));

			if (jsonArray == null) {
				jsonArray = jsonObject.getJSONArray(defaultLanguageId);
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject optionJSONObject = jsonArray.getJSONObject(i);

				ddmFormFieldOptions.addOptionLabel(
					optionJSONObject.getString("value"), availableLocale,
					optionJSONObject.getString("label"));
				ddmFormFieldOptions.addOptionReference(
					optionJSONObject.getString("value"),
					optionJSONObject.getString("reference"));
			}
		}

		ddmFormFieldOptions.setDefaultLocale(defaultLocale);

		return ddmFormFieldOptions;
	}

	private DDMFormFieldOptions _getDDMFormFieldOptions(
			String serializedValue, Set<Locale> availableLocales,
			Locale defaultLocale)
		throws PortalException {

		if (Validator.isNull(serializedValue)) {
			return new DDMFormFieldOptions();
		}

		JSONObject jsonObject = jsonFactory.createJSONObject(serializedValue);

		return _getDDMFormFieldOptions(
			jsonObject, availableLocales, defaultLocale);
	}

	private Object _getDDMFormFieldPropertyValue(
			String serializedValue, boolean localizable, String dataType,
			String type, Set<Locale> availableLocales, Locale defaultLocale)
		throws PortalException {

		if (Objects.equals(dataType, "ddm-options")) {
			return _getDDMFormFieldOptions(
				serializedValue, availableLocales, defaultLocale);
		}
		else if (Objects.equals(dataType, "boolean")) {
			return _getBooleanValue(type, serializedValue, defaultLocale);
		}
		else if (Objects.equals(type, "validation")) {
			return getDDMFormFieldValidation(
				availableLocales, dataType, defaultLocale, serializedValue);
		}
		else if (localizable) {
			return getLocalizedValue(
				serializedValue, type, availableLocales, defaultLocale);
		}
		else if (Objects.equals(type, "select")) {
			return _getSelectValue(type, serializedValue, defaultLocale);
		}

		return serializedValue;
	}

	private Object _getSelectValue(
			String type, String serializedValue, Locale defaultLocale)
		throws JSONException {

		Object value = getValueFromValueAccessor(
			type, serializedValue, defaultLocale);

		if (!(value instanceof JSONArray)) {
			value = jsonFactory.createJSONArray(serializedValue);
		}

		return value;
	}

	private void _setDDMFormAvailableLocales(
		JSONArray jsonArray, DDMForm ddmForm) {

		ddmForm.setAvailableLocales(getAvailableLocales(jsonArray));
	}

	private void _setDDMFormDefaultLocale(
		String defaultLanguageId, DDMForm ddmForm) {

		ddmForm.setDefaultLocale(LocaleUtil.fromLanguageId(defaultLanguageId));
	}

	private void _setDDMFormFieldSettings(
		JSONObject jsonObject, DDMForm ddmForm, DDMFormField ddmFormField) {

		DDMFormContextVisitor ddmFormTemplateContextVisitor =
			new DDMFormContextVisitor(jsonObject.getJSONArray("pages"));

		ddmFormTemplateContextVisitor.onVisitField(
			new Consumer<JSONObject>() {

				@Override
				public void accept(JSONObject jsonObject) {
					boolean localizable = jsonObject.getBoolean(
						"localizable", false);

					String propertyName = jsonObject.getString("fieldName");

					String value = jsonObject.getString(
						getValueProperty(localizable));

					String dataType = jsonObject.getString("dataType");
					String type = jsonObject.getString("type");

					try {
						Object propertyValue = _getDDMFormFieldPropertyValue(
							value, localizable, dataType, type,
							ddmForm.getAvailableLocales(),
							ddmForm.getDefaultLocale());

						ddmFormField.setProperty(propertyName, propertyValue);
					}
					catch (PortalException portalException) {
						_log.error(
							String.format(
								"Unable to set the property \"%s\" of the " +
									"field \"%s\"",
								propertyName, ddmFormField.getName()),
							portalException);
					}
				}

				protected String getValueProperty(boolean localizable) {
					if (localizable) {
						return "localizedValue";
					}

					return "value";
				}

			});

		ddmFormTemplateContextVisitor.visit();
	}

	private void _setDDMFormFields(JSONArray jsonArray, DDMForm ddmForm) {
		DDMFormContextVisitor ddmFormTemplateContextVisitor =
			new DDMFormContextVisitor(jsonArray);

		ddmFormTemplateContextVisitor.onVisitField(
			new Consumer<JSONObject>() {

				@Override
				public void accept(JSONObject jsonObject) {
					DDMFormField ddmFormField = createDDMFormField(jsonObject);

					ddmForm.addDDMFormField(ddmFormField);
				}

				protected DDMFormField createDDMFormField(
					JSONObject jsonObject) {

					String name = jsonObject.getString("name");
					String type = jsonObject.getString("type");

					DDMFormField ddmFormField = new DDMFormField(name, type);

					if (jsonObject.has("nestedFields")) {
						JSONArray nestedFieldsJSONArray =
							jsonObject.getJSONArray("nestedFields");

						for (int i = 0; i < nestedFieldsJSONArray.length();
							 i++) {

							DDMFormField nestedDDMFormField =
								createDDMFormField(
									nestedFieldsJSONArray.getJSONObject(i));

							ddmFormField.addNestedDDMFormField(
								nestedDDMFormField);
						}
					}

					_setDDMFormFieldSettings(
						jsonObject.getJSONObject("settingsContext"), ddmForm,
						ddmFormField);

					return ddmFormField;
				}

			});

		ddmFormTemplateContextVisitor.visit();
	}

	private void _setDDMFormRules(JSONArray jsonArray, DDMForm ddmForm)
		throws PortalException {

		ddmForm.setDDMFormRules(
			ddmFormRuleDeserializer.deserialize(ddmForm, jsonArray));
	}

	private void _setDDMFormSuccessPageSettings(
		JSONObject jsonObject, DDMForm ddmForm) {

		if (jsonObject == null) {
			return;
		}

		DDMFormSuccessPageSettings ddmFormSuccessPageSettings =
			new DDMFormSuccessPageSettings();

		Locale defaultLocale = ddmForm.getDefaultLocale();

		ddmFormSuccessPageSettings.setBody(
			createLocalizedValue(
				jsonObject.getJSONObject("body"), defaultLocale));
		ddmFormSuccessPageSettings.setTitle(
			createLocalizedValue(
				jsonObject.getJSONObject("title"), defaultLocale));

		ddmFormSuccessPageSettings.setEnabled(jsonObject.getBoolean("enabled"));

		ddmForm.setDDMFormSuccessPageSettings(ddmFormSuccessPageSettings);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormContextToDDMForm.class);

}