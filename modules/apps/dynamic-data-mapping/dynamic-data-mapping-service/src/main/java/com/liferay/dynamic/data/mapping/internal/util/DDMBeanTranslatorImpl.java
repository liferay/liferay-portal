/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMBeanTranslator;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leonardo Barros
 */
@Component(service = DDMBeanTranslator.class)
public class DDMBeanTranslatorImpl implements DDMBeanTranslator {

	@Override
	public DDMForm translate(
		com.liferay.dynamic.data.mapping.kernel.DDMForm ddmForm) {

		if (ddmForm == null) {
			return null;
		}

		DDMForm translatedDDMForm = new DDMForm();

		translatedDDMForm.setAvailableLocales(ddmForm.getAvailableLocales());
		translatedDDMForm.setDefaultLocale(ddmForm.getDefaultLocale());

		for (com.liferay.dynamic.data.mapping.kernel.DDMFormField ddmFormField :
				ddmForm.getDDMFormFields()) {

			DDMFormField translatedDDMFormField = translate(ddmFormField);

			translatedDDMFormField.setDDMForm(translatedDDMForm);

			translatedDDMForm.addDDMFormField(translatedDDMFormField);
		}

		return translatedDDMForm;
	}

	@Override
	public com.liferay.dynamic.data.mapping.kernel.DDMForm translate(
		DDMForm ddmForm) {

		if (ddmForm == null) {
			return null;
		}

		com.liferay.dynamic.data.mapping.kernel.DDMForm translatedDDMForm =
			new com.liferay.dynamic.data.mapping.kernel.DDMForm();

		translatedDDMForm.setAvailableLocales(ddmForm.getAvailableLocales());
		translatedDDMForm.setDefaultLocale(ddmForm.getDefaultLocale());

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			com.liferay.dynamic.data.mapping.kernel.DDMFormField
				translatedDDMFormField = translate(ddmFormField);

			translatedDDMFormField.setDDMForm(translatedDDMForm);

			translatedDDMForm.addDDMFormField(translatedDDMFormField);
		}

		return translatedDDMForm;
	}

	@Override
	public DDMFormField translate(
		com.liferay.dynamic.data.mapping.kernel.DDMFormField ddmFormField) {

		if (ddmFormField == null) {
			return null;
		}

		DDMFormField translatedDDMFormField = new DDMFormField(
			ddmFormField.getName(), ddmFormField.getType());

		translatedDDMFormField.setDataType(ddmFormField.getDataType());
		translatedDDMFormField.setFieldNamespace(
			ddmFormField.getFieldNamespace());

		String fieldReference = ddmFormField.getFieldReference();

		if (Validator.isNotNull(fieldReference)) {
			translatedDDMFormField.setFieldReference(fieldReference);
		}

		translatedDDMFormField.setIndexType(ddmFormField.getIndexType());
		translatedDDMFormField.setDDMFormFieldOptions(
			translate(ddmFormField.getDDMFormFieldOptions()));
		translatedDDMFormField.setLabel(
			_translateLocalizedValue(ddmFormField.getLabel()));
		translatedDDMFormField.setLocalizable(ddmFormField.isLocalizable());
		translatedDDMFormField.setMultiple(ddmFormField.isMultiple());
		translatedDDMFormField.setPredefinedValue(
			_translateLocalizedValue(ddmFormField.getPredefinedValue()));
		translatedDDMFormField.setReadOnly(ddmFormField.isReadOnly());
		translatedDDMFormField.setRepeatable(ddmFormField.isRepeatable());
		translatedDDMFormField.setRequired(ddmFormField.isRequired());
		translatedDDMFormField.setShowLabel(ddmFormField.isShowLabel());
		translatedDDMFormField.setStyle(
			_translateLocalizedValue(ddmFormField.getStyle()));
		translatedDDMFormField.setTip(
			_translateLocalizedValue(ddmFormField.getTip()));

		for (com.liferay.dynamic.data.mapping.kernel.DDMFormField
				nestedDDMFormField : ddmFormField.getNestedDDMFormFields()) {

			translatedDDMFormField.addNestedDDMFormField(
				translate(nestedDDMFormField));
		}

		_setProperties(translatedDDMFormField, ddmFormField.getProperties());

		return translatedDDMFormField;
	}

	@Override
	public com.liferay.dynamic.data.mapping.kernel.DDMFormField translate(
		DDMFormField ddmFormField) {

		if (ddmFormField == null) {
			return null;
		}

		com.liferay.dynamic.data.mapping.kernel.DDMFormField
			translatedDDMFormField =
				new com.liferay.dynamic.data.mapping.kernel.DDMFormField(
					ddmFormField.getName(), ddmFormField.getType());

		translatedDDMFormField.setDataType(ddmFormField.getDataType());
		translatedDDMFormField.setFieldNamespace(
			ddmFormField.getFieldNamespace());
		translatedDDMFormField.setIndexType(ddmFormField.getIndexType());
		translatedDDMFormField.setDDMFormFieldOptions(
			translate(ddmFormField.getDDMFormFieldOptions()));
		translatedDDMFormField.setLabel(
			_translateLocalizedValue(ddmFormField.getLabel()));
		translatedDDMFormField.setLocalizable(ddmFormField.isLocalizable());
		translatedDDMFormField.setMultiple(ddmFormField.isMultiple());
		translatedDDMFormField.setPredefinedValue(
			_translateLocalizedValue(ddmFormField.getPredefinedValue()));
		translatedDDMFormField.setReadOnly(ddmFormField.isReadOnly());
		translatedDDMFormField.setRepeatable(ddmFormField.isRepeatable());
		translatedDDMFormField.setRequired(ddmFormField.isRequired());
		translatedDDMFormField.setShowLabel(ddmFormField.isShowLabel());
		translatedDDMFormField.setStyle(
			_translateLocalizedValue(ddmFormField.getStyle()));
		translatedDDMFormField.setTip(
			_translateLocalizedValue(ddmFormField.getTip()));

		for (DDMFormField nestedDDMFormField :
				ddmFormField.getNestedDDMFormFields()) {

			translatedDDMFormField.addNestedDDMFormField(
				translate(nestedDDMFormField));
		}

		_setProperties(translatedDDMFormField, ddmFormField.getProperties());

		return translatedDDMFormField;
	}

	@Override
	public DDMFormValues translate(
		com.liferay.dynamic.data.mapping.kernel.DDMFormValues ddmFormValues) {

		if (ddmFormValues == null) {
			return null;
		}

		DDMFormValues translatedDDMFormValues = new DDMFormValues(
			translate(ddmFormValues.getDDMForm()));

		translatedDDMFormValues.setAvailableLocales(
			ddmFormValues.getAvailableLocales());
		translatedDDMFormValues.setDefaultLocale(
			ddmFormValues.getDefaultLocale());

		for (com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue
				ddmFormFieldValue : ddmFormValues.getDDMFormFieldValues()) {

			translatedDDMFormValues.addDDMFormFieldValue(
				translate(ddmFormFieldValue));
		}

		return translatedDDMFormValues;
	}

	@Override
	public com.liferay.dynamic.data.mapping.kernel.DDMFormValues translate(
		DDMFormValues ddmFormValues) {

		if (ddmFormValues == null) {
			return null;
		}

		com.liferay.dynamic.data.mapping.kernel.DDMForm translatedDDMForm =
			translate(ddmFormValues.getDDMForm());

		com.liferay.dynamic.data.mapping.kernel.DDMFormValues
			translatedDDMFormValues =
				new com.liferay.dynamic.data.mapping.kernel.DDMFormValues(
					translatedDDMForm);

		translatedDDMFormValues.setAvailableLocales(
			ddmFormValues.getAvailableLocales());
		translatedDDMFormValues.setDefaultLocale(
			ddmFormValues.getDefaultLocale());

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			translatedDDMFormValues.addDDMFormFieldValue(
				translate(ddmFormFieldValue));
		}

		return translatedDDMFormValues;
	}

	protected DDMFormFieldOptions translate(
		com.liferay.dynamic.data.mapping.kernel.DDMFormFieldOptions
			ddmFormFieldOptions) {

		if (ddmFormFieldOptions == null) {
			return null;
		}

		DDMFormFieldOptions translatedDDMFormFieldOptions =
			new DDMFormFieldOptions();

		translatedDDMFormFieldOptions.setDefaultLocale(
			ddmFormFieldOptions.getDefaultLocale());

		Map<String, com.liferay.dynamic.data.mapping.kernel.LocalizedValue>
			options = ddmFormFieldOptions.getOptions();

		for (Map.Entry
				<String, com.liferay.dynamic.data.mapping.kernel.LocalizedValue>
					entry : options.entrySet()) {

			com.liferay.dynamic.data.mapping.kernel.LocalizedValue
				localizedValue = entry.getValue();

			for (Locale locale : localizedValue.getAvailableLocales()) {
				translatedDDMFormFieldOptions.addOptionLabel(
					entry.getKey(), locale, localizedValue.getString(locale));
			}
		}

		return translatedDDMFormFieldOptions;
	}

	protected com.liferay.dynamic.data.mapping.kernel.DDMFormFieldOptions
		translate(DDMFormFieldOptions ddmFormFieldOptions) {

		if (ddmFormFieldOptions == null) {
			return null;
		}

		com.liferay.dynamic.data.mapping.kernel.DDMFormFieldOptions
			translatedDDMFormFieldOptions =
				new com.liferay.dynamic.data.mapping.kernel.
					DDMFormFieldOptions();

		translatedDDMFormFieldOptions.setDefaultLocale(
			ddmFormFieldOptions.getDefaultLocale());

		Map<String, LocalizedValue> options = ddmFormFieldOptions.getOptions();

		for (Map.Entry<String, LocalizedValue> entry : options.entrySet()) {
			LocalizedValue localizedValue = entry.getValue();

			for (Locale locale : localizedValue.getAvailableLocales()) {
				translatedDDMFormFieldOptions.addOptionLabel(
					entry.getKey(), locale, localizedValue.getString(locale));
			}
		}

		return translatedDDMFormFieldOptions;
	}

	protected DDMFormFieldValue translate(
		com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue
			ddmFormFieldValue) {

		if (ddmFormFieldValue == null) {
			return null;
		}

		DDMFormFieldValue translatedDDMFormFieldValue = new DDMFormFieldValue(
			ddmFormFieldValue.getInstanceId());

		translatedDDMFormFieldValue.setName(ddmFormFieldValue.getName());
		translatedDDMFormFieldValue.setValue(
			translate(ddmFormFieldValue.getValue()));

		for (com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue
				nestedFormFieldValue :
					ddmFormFieldValue.getNestedDDMFormFieldValues()) {

			translatedDDMFormFieldValue.addNestedDDMFormFieldValue(
				translate(nestedFormFieldValue));
		}

		return translatedDDMFormFieldValue;
	}

	protected com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue
		translate(DDMFormFieldValue ddmFormFieldValue) {

		if (ddmFormFieldValue == null) {
			return null;
		}

		com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue
			translatedDDMFormFieldValue =
				new com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue(
					ddmFormFieldValue.getInstanceId());

		translatedDDMFormFieldValue.setName(ddmFormFieldValue.getName());
		translatedDDMFormFieldValue.setValue(
			translate(ddmFormFieldValue.getValue()));

		for (DDMFormFieldValue nestedFormFieldValue :
				ddmFormFieldValue.getNestedDDMFormFieldValues()) {

			translatedDDMFormFieldValue.addNestedDDMFormFieldValue(
				translate(nestedFormFieldValue));
		}

		return translatedDDMFormFieldValue;
	}

	protected Value translate(
		com.liferay.dynamic.data.mapping.kernel.Value value) {

		if (value == null) {
			return null;
		}

		if (value.isLocalized()) {
			return _translateLocalizedValue(value);
		}

		return _translateUnlocalizedValue(value);
	}

	protected com.liferay.dynamic.data.mapping.kernel.Value translate(
		Value value) {

		if (value == null) {
			return null;
		}

		if (value.isLocalized()) {
			return _translateLocalizedValue(value);
		}

		return _translateUnlocalizedValue(value);
	}

	private void _setProperties(
		com.liferay.dynamic.data.mapping.kernel.DDMFormField ddmFormField,
		Map<String, Object> properties) {

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			Object property = ddmFormField.getProperty(entry.getKey());

			if (property != null) {
				continue;
			}

			ddmFormField.setProperty(entry.getKey(), entry.getValue());
		}
	}

	private void _setProperties(
		DDMFormField ddmFormField, Map<String, Object> properties) {

		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			Object property = ddmFormField.getProperty(entry.getKey());

			if (property != null) {
				continue;
			}

			ddmFormField.setProperty(entry.getKey(), entry.getValue());
		}
	}

	private LocalizedValue _translateLocalizedValue(
		com.liferay.dynamic.data.mapping.kernel.Value value) {

		if (value == null) {
			return null;
		}

		LocalizedValue translatedLocalizedValue = new LocalizedValue();

		translatedLocalizedValue.setDefaultLocale(value.getDefaultLocale());

		Map<Locale, String> values = translatedLocalizedValue.getValues();

		values.putAll(value.getValues());

		return translatedLocalizedValue;
	}

	private com.liferay.dynamic.data.mapping.kernel.LocalizedValue
		_translateLocalizedValue(Value value) {

		if (value == null) {
			return null;
		}

		com.liferay.dynamic.data.mapping.kernel.LocalizedValue
			translatedLocalizedValue =
				new com.liferay.dynamic.data.mapping.kernel.LocalizedValue();

		translatedLocalizedValue.setDefaultLocale(value.getDefaultLocale());

		Map<Locale, String> values = translatedLocalizedValue.getValues();

		values.putAll(value.getValues());

		return translatedLocalizedValue;
	}

	private UnlocalizedValue _translateUnlocalizedValue(
		com.liferay.dynamic.data.mapping.kernel.Value value) {

		if (value == null) {
			return null;
		}

		return new UnlocalizedValue(value.getString(value.getDefaultLocale()));
	}

	private com.liferay.dynamic.data.mapping.kernel.UnlocalizedValue
		_translateUnlocalizedValue(Value value) {

		if (value == null) {
			return null;
		}

		return new com.liferay.dynamic.data.mapping.kernel.UnlocalizedValue(
			value.getString(value.getDefaultLocale()));
	}

}