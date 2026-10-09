/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.io;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "ddm.form.values.deserializer.type=json",
	service = DDMFormValuesDeserializer.class
)
public class DDMFormValuesJSONDeserializer
	implements DDMFormValuesDeserializer {

	@Override
	public DDMFormValuesDeserializerDeserializeResponse deserialize(
		DDMFormValuesDeserializerDeserializeRequest
			ddmFormValuesDeserializerDeserializeRequest) {

		DDMForm ddmForm =
			ddmFormValuesDeserializerDeserializeRequest.getDDMForm();

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormValuesDeserializerDeserializeResponse.Builder builder =
			DDMFormValuesDeserializerDeserializeResponse.Builder.newBuilder(
				ddmFormValues);

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				ddmFormValuesDeserializerDeserializeRequest.getContent());

			_setDDMFormValuesAvailableLocales(
				jsonObject.getJSONArray("availableLanguageIds"), ddmFormValues);
			_setDDMFormValuesDefaultLocale(
				jsonObject.getString("defaultLanguageId"), ddmFormValues);
			setDDMFormFieldValues(
				jsonObject.getJSONArray("fieldValues"), ddmForm, ddmFormValues);

			setDDMFormLocalizedValuesDefaultLocale(ddmFormValues);

			return builder.build();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			builder = builder.exception(exception);
		}

		return builder.build();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DDMFormFieldValueJSONDeserializer.class,
			"ddm.form.field.type.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	protected Set<Locale> getAvailableLocales(JSONArray jsonArray) {
		Set<Locale> availableLocales = new HashSet<>();

		if (jsonArray == null) {
			return availableLocales;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			Locale availableLocale = LocaleUtil.fromLanguageId(
				jsonArray.getString(i));

			availableLocales.add(availableLocale);
		}

		return availableLocales;
	}

	protected List<DDMFormFieldValue> getDDMFormFieldValues(
		JSONArray jsonArray, Map<String, DDMFormField> ddmFormFieldsMap) {

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormFieldValues.add(
				_getDDMFormFieldValue(
					jsonArray.getJSONObject(i), ddmFormFieldsMap));
		}

		return ddmFormFieldValues;
	}

	protected Value getValue(DDMFormField ddmFormField, JSONObject jsonObject) {
		DDMFormFieldValueJSONDeserializer ddmFormFieldValueJSONDeserializer =
			_getDDMFormFieldValueJSONDeserializer(ddmFormField);

		if (ddmFormFieldValueJSONDeserializer != null) {
			return ddmFormFieldValueJSONDeserializer.deserialize(
				ddmFormField, String.valueOf(jsonObject.get("value")));
		}

		JSONObject valueJSONObject = jsonObject.getJSONObject("value");

		if (isLocalized(valueJSONObject)) {
			return _getLocalizedValue(valueJSONObject);
		}

		return new UnlocalizedValue(jsonObject.getString("value"));
	}

	protected boolean isLocalized(JSONObject jsonObject) {
		if (jsonObject == null) {
			return false;
		}

		for (String key : jsonObject.keySet()) {
			if (_isInvalidLocale(key)) {
				return false;
			}
		}

		return true;
	}

	protected void setDDMFormFieldValues(
		JSONArray jsonArray, DDMForm ddmForm, DDMFormValues ddmFormValues) {

		ddmFormValues.setDDMFormFieldValues(
			getDDMFormFieldValues(
				jsonArray, ddmForm.getDDMFormFieldsMap(true)));
	}

	protected void setDDMFormLocalizedValuesDefaultLocale(
		DDMFormValues ddmFormValues) {

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			_setDDMFormFieldValueLocalizedValueDefaultLocale(
				ddmFormFieldValue, ddmFormValues.getDefaultLocale());
		}
	}

	private DDMFormFieldValue _getDDMFormFieldValue(
		JSONObject jsonObject, Map<String, DDMFormField> ddmFormFieldsMap) {

		DDMFormFieldValue ddmFormFieldValue = null;

		String instanceId = jsonObject.getString("instanceId");

		if (instanceId.matches("[a-zA-Z0-9]*")) {
			ddmFormFieldValue = new DDMFormFieldValue(instanceId);
		}
		else {
			ddmFormFieldValue = new DDMFormFieldValue();
		}

		ddmFormFieldValue.setFieldReference(
			jsonObject.getString("fieldReference"));

		ddmFormFieldValue.setName(jsonObject.getString("name"));

		_setDDMFormFieldValueValue(
			jsonObject, ddmFormFieldsMap.get(jsonObject.getString("name")),
			ddmFormFieldValue);

		_setNestedDDMFormFieldValues(
			jsonObject.getJSONArray("nestedFieldValues"), ddmFormFieldsMap,
			ddmFormFieldValue);

		return ddmFormFieldValue;
	}

	private DDMFormFieldValueJSONDeserializer
		_getDDMFormFieldValueJSONDeserializer(DDMFormField ddmFormField) {

		if (ddmFormField == null) {
			return null;
		}

		return _serviceTrackerMap.getService(ddmFormField.getType());
	}

	private LocalizedValue _getLocalizedValue(JSONObject jsonObject) {
		LocalizedValue localizedValue = new LocalizedValue();

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String languageId = iterator.next();

			if (!_language.isAvailableLocale(languageId)) {
				continue;
			}

			localizedValue.addString(
				LocaleUtil.fromLanguageId(languageId),
				jsonObject.getString(languageId));
		}

		return localizedValue;
	}

	private boolean _isInvalidLocale(String languageId) {
		if (LocaleUtil.fromLanguageId(languageId, true, false) == null) {
			return true;
		}

		return false;
	}

	private void _setDDMFormFieldValueLocalizedValueDefaultLocale(
		DDMFormFieldValue ddmFormFieldValue, Locale defaultLocale) {

		Value value = ddmFormFieldValue.getValue();

		if ((value != null) && value.isLocalized()) {
			value.setDefaultLocale(defaultLocale);
		}

		for (DDMFormFieldValue nestedDDMFormFieldValue :
				ddmFormFieldValue.getNestedDDMFormFieldValues()) {

			_setDDMFormFieldValueLocalizedValueDefaultLocale(
				nestedDDMFormFieldValue, defaultLocale);
		}
	}

	private void _setDDMFormFieldValueValue(
		JSONObject jsonObject, DDMFormField ddmFormField,
		DDMFormFieldValue ddmFormFieldValue) {

		if ((ddmFormField != null) && ddmFormField.isTransient()) {
			return;
		}

		String valueString = jsonObject.getString("value", null);

		if (valueString == null) {
			return;
		}

		ddmFormFieldValue.setValue(getValue(ddmFormField, jsonObject));
	}

	private void _setDDMFormValuesAvailableLocales(
		JSONArray jsonArray, DDMFormValues ddmFormValues) {

		ddmFormValues.setAvailableLocales(getAvailableLocales(jsonArray));
	}

	private void _setDDMFormValuesDefaultLocale(
		String defaultLanguageId, DDMFormValues ddmFormValues) {

		Locale defaultLocale = LocaleUtil.fromLanguageId(defaultLanguageId);

		ddmFormValues.setDefaultLocale(defaultLocale);

		Set<Locale> availableLocales = ddmFormValues.getAvailableLocales();

		if ((availableLocales != null) &&
			!availableLocales.contains(defaultLocale)) {

			availableLocales.add(defaultLocale);
		}
	}

	private void _setNestedDDMFormFieldValues(
		JSONArray jsonArray, Map<String, DDMFormField> ddmFormFieldsMap,
		DDMFormFieldValue ddmFormFieldValue) {

		if ((jsonArray == null) || (jsonArray.length() == 0)) {
			return;
		}

		List<DDMFormFieldValue> nestedDDMFormFieldValues =
			getDDMFormFieldValues(jsonArray, ddmFormFieldsMap);

		ddmFormFieldValue.setNestedDDMFormFields(nestedDDMFormFieldValues);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormValuesJSONDeserializer.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	private ServiceTrackerMap<String, DDMFormFieldValueJSONDeserializer>
		_serviceTrackerMap;

}