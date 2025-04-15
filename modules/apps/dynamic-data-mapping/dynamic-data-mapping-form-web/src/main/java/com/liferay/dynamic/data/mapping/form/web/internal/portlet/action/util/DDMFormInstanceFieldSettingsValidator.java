/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util;

import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextVisitor;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateRequest;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateResponse;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.web.internal.FormInstanceFieldSettingsException;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = DDMFormInstanceFieldSettingsValidator.class)
public class DDMFormInstanceFieldSettingsValidator {

	public void validate(PortletRequest portletRequest, DDMForm ddmForm)
		throws PortalException {

		Map<String, Set<String>> fieldNamePropertiesMap = _evaluate(
			portletRequest, ddmForm);

		if (!fieldNamePropertiesMap.isEmpty()) {
			throw new FormInstanceFieldSettingsException.
				MustSetValidValueForProperties(fieldNamePropertiesMap);
		}
	}

	private DDMFormValues _createDDMFormFieldFormValues(
		JSONObject jsonObject, DDMForm fieldSettingsDDMForm,
		Set<Locale> availableLocales, Locale defaultLocale) {

		DDMFormValues fieldSettingsDDMFormValues = new DDMFormValues(
			fieldSettingsDDMForm);

		fieldSettingsDDMFormValues.setAvailableLocales(availableLocales);
		fieldSettingsDDMFormValues.setDefaultLocale(defaultLocale);

		DDMFormContextVisitor ddmFormContextVisitor = new DDMFormContextVisitor(
			jsonObject.getJSONArray("pages"));

		ddmFormContextVisitor.onVisitField(
			new Consumer<JSONObject>() {

				@Override
				public void accept(JSONObject jsonObject) {
					DDMFormFieldValue ddmFormFieldValue =
						new DDMFormFieldValue();

					ddmFormFieldValue.setDDMFormValues(
						fieldSettingsDDMFormValues);
					ddmFormFieldValue.setFieldReference(
						jsonObject.getString("fieldReference"));
					ddmFormFieldValue.setInstanceId(
						jsonObject.getString("instanceId"));
					ddmFormFieldValue.setName(
						jsonObject.getString("fieldName"));
					ddmFormFieldValue.setValue(getValue(jsonObject));

					fieldSettingsDDMFormValues.addDDMFormFieldValue(
						ddmFormFieldValue);
				}

				protected LocalizedValue getLocalizedValue(
					String serializedValue, Set<Locale> availableLocales,
					Locale defaultLocale) {

					LocalizedValue localizedValue = new LocalizedValue(
						defaultLocale);

					try {
						JSONObject jsonObject = _jsonFactory.createJSONObject(
							serializedValue);

						String defaultValueString = jsonObject.getString(
							LocaleUtil.toLanguageId(defaultLocale));

						for (Locale availableLocale : availableLocales) {
							String valueString = jsonObject.getString(
								LocaleUtil.toLanguageId(availableLocale),
								defaultValueString);

							localizedValue.addString(
								availableLocale, valueString);
						}
					}
					catch (Exception exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}
					}

					return localizedValue;
				}

				protected Value getValue(JSONObject jsonObject) {
					boolean localizable = jsonObject.getBoolean(
						"localizable", false);

					if (localizable) {
						return getLocalizedValue(
							jsonObject.getString("localizedValue"),
							availableLocales, defaultLocale);
					}
					else if (StringUtil.equals(
								jsonObject.getString("type"),
								DDMFormFieldTypeConstants.OPTIONS)) {

						try {
							JSONObject optionsJSONObject =
								_jsonFactory.createJSONObject(
									jsonObject.getString("value"));

							JSONArray defaultJSONArray =
								optionsJSONObject.getJSONArray(
									LocaleUtil.toLanguageId(defaultLocale));

							for (Locale availableLocale : availableLocales) {
								JSONArray jsonArray =
									optionsJSONObject.getJSONArray(
										LocaleUtil.toLanguageId(
											availableLocale));

								if (jsonArray != null) {
									continue;
								}

								optionsJSONObject.put(
									LocaleUtil.toLanguageId(availableLocale),
									defaultJSONArray);
							}

							return new UnlocalizedValue(
								optionsJSONObject.toString());
						}
						catch (JSONException jsonException) {
							if (_log.isDebugEnabled()) {
								_log.debug(jsonException);
							}
						}
					}

					return new UnlocalizedValue(jsonObject.getString("value"));
				}

			});

		ddmFormContextVisitor.visit();

		return fieldSettingsDDMFormValues;
	}

	private Map<String, Set<String>> _evaluate(
			PortletRequest portletRequest, DDMForm ddmForm)
		throws JSONException {

		Map<String, Set<String>> fieldNamePropertiesMap = new HashMap<>();

		String serializedFormBuilderContext = ParamUtil.getString(
			portletRequest, "serializedFormBuilderContext");

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			serializedFormBuilderContext);

		DDMFormContextVisitor ddmFormContextVisitor = new DDMFormContextVisitor(
			jsonObject.getJSONArray("pages"));

		ddmFormContextVisitor.onVisitField(
			new FieldJSONObjectConsumer(
				ddmForm, fieldNamePropertiesMap, portletRequest));

		ddmFormContextVisitor.visit();

		return fieldNamePropertiesMap;
	}

	private DDMFormEvaluatorEvaluateResponse _evaluate(
		PortletRequest portletRequest, DDMForm ddmForm,
		DDMFormValues ddmFormValues, Locale locale) {

		DDMFormEvaluatorEvaluateRequest.Builder builder =
			DDMFormEvaluatorEvaluateRequest.Builder.newBuilder(
				ddmForm, ddmFormValues, locale);

		builder.withCompanyId(
			_portal.getCompanyId(portletRequest)
		).withDDMFormInstanceId(
			ParamUtil.getLong(portletRequest, "formInstanceId")
		).withGroupId(
			ParamUtil.getLong(portletRequest, "groupId")
		).withUserId(
			_portal.getUserId(portletRequest)
		);

		return _ddmFormEvaluator.evaluate(builder.build());
	}

	private String _getFieldLabel(DDMFormField ddmFormField, Locale locale) {
		LocalizedValue label = ddmFormField.getLabel();

		return label.getString(locale);
	}

	private Set<String> _getInvalidDDMFormFields(
		DDMForm fieldDDMForm,
		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse,
		Locale locale) {

		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges =
				ddmFormEvaluatorEvaluateResponse.
					getDDMFormFieldsPropertyChanges();

		if (MapUtil.isEmpty(ddmFormFieldsPropertyChanges)) {
			return Collections.emptySet();
		}

		Set<String> ddmFormFields = new HashSet<>();

		Map<String, DDMFormField> ddmFormFieldsMap =
			fieldDDMForm.getDDMFormFieldsMap(true);

		for (Map.Entry<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
				entry : ddmFormFieldsPropertyChanges.entrySet()) {

			if (!MapUtil.getBoolean(entry.getValue(), "valid", true)) {
				DDMFormEvaluatorFieldContextKey ddmFormFieldContextKey =
					entry.getKey();

				DDMFormField propertyFormField = ddmFormFieldsMap.get(
					ddmFormFieldContextKey.getName());

				ddmFormFields.add(_getFieldLabel(propertyFormField, locale));
			}
		}

		return ddmFormFields;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceFieldSettingsValidator.class);

	@Reference
	private DDMFormEvaluator _ddmFormEvaluator;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	private class FieldJSONObjectConsumer implements Consumer<JSONObject> {

		public FieldJSONObjectConsumer(
			DDMForm ddmForm, Map<String, Set<String>> fieldNamePropertiesMap,
			PortletRequest portletRequest) {

			_ddmForm = ddmForm;
			_fieldNamePropertiesMap = fieldNamePropertiesMap;
			_portletRequest = portletRequest;
		}

		@Override
		public void accept(JSONObject jsonObject) {
			_evaluateDDMFormField(jsonObject);
		}

		private void _evaluateDDMFormField(JSONObject jsonObject) {
			if (jsonObject.has("nestedFields")) {
				JSONArray nestedFieldsJSONArray = jsonObject.getJSONArray(
					"nestedFields");

				for (int i = 0; i < nestedFieldsJSONArray.length(); i++) {
					_evaluateDDMFormField(
						nestedFieldsJSONArray.getJSONObject(i));
				}
			}

			Map<String, DDMFormField> ddmFormFieldsMap =
				_ddmForm.getDDMFormFieldsMap(true);

			DDMFormField ddmFormField = ddmFormFieldsMap.get(
				jsonObject.getString("fieldName"));

			if (ddmFormField == null) {
				return;
			}

			DDMFormFieldType ddmFormFieldType =
				_ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
					ddmFormField.getType());

			DDMForm ddmFormFieldTypeSettingsDDMForm = DDMFormFactory.create(
				ddmFormFieldType.getDDMFormFieldTypeSettings());

			if (StringUtil.equals(ddmFormField.getDataType(), "integer") &&
				GetterUtil.getBoolean(ddmFormField.getProperty("inputMask"))) {

				Map<String, DDMFormField>
					ddmFormFieldTypeSettingsDDMFormFieldsMap =
						ddmFormFieldTypeSettingsDDMForm.getDDMFormFieldsMap(
							false);

				DDMFormField predefinedValueDDMFormField =
					ddmFormFieldTypeSettingsDDMFormFieldsMap.get(
						"predefinedValue");

				predefinedValueDDMFormField.setDataType("integer");
				predefinedValueDDMFormField.setProperty("inputMask", true);
				predefinedValueDDMFormField.setProperty(
					"inputMaskFormat",
					ddmFormField.getProperty("inputMaskFormat"));
			}

			DDMFormValues ddmFormFieldTypeSettingsDDMFormValues =
				_createDDMFormFieldFormValues(
					jsonObject.getJSONObject("settingsContext"),
					ddmFormFieldTypeSettingsDDMForm,
					_ddmForm.getAvailableLocales(),
					_ddmForm.getDefaultLocale());

			for (Locale availableLocale : _ddmForm.getAvailableLocales()) {
				DDMFormEvaluatorEvaluateResponse
					ddmFormEvaluatorEvaluateResponse = _evaluate(
						_portletRequest, ddmFormFieldTypeSettingsDDMForm,
						ddmFormFieldTypeSettingsDDMFormValues, availableLocale);

				Set<String> invalidDDMFormFields = _getInvalidDDMFormFields(
					ddmFormFieldTypeSettingsDDMForm,
					ddmFormEvaluatorEvaluateResponse,
					ddmFormFieldTypeSettingsDDMForm.getDefaultLocale());

				if (!invalidDDMFormFields.isEmpty()) {
					_fieldNamePropertiesMap.put(
						_getFieldLabel(ddmFormField, availableLocale),
						invalidDDMFormFields);

					break;
				}
			}
		}

		private final DDMForm _ddmForm;
		private final Map<String, Set<String>> _fieldNamePropertiesMap;
		private final PortletRequest _portletRequest;

	}

}