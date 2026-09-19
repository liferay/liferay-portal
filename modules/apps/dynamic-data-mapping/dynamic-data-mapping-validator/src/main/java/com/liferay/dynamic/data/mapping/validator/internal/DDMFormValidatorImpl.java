/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.validator.internal;

import com.liferay.dynamic.data.mapping.constants.DDMConstants;
import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueValidationException;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustNotDuplicateFieldName;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustNotDuplicateFieldReference;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetAvailableLocales;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetDefaultLocale;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetDefaultLocaleAsAvailableLocale;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetFieldType;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetFieldsForForm;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetOptionsForField;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidAvailableLocalesForProperty;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidCharactersForFieldName;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidCharactersForFieldType;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidDefaultLocaleForProperty;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidIndexType;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidType;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidValidationExpression;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidationException.MustSetValidVisibilityExpression;
import com.liferay.dynamic.data.mapping.validator.DDMFormValidator;
import com.liferay.dynamic.data.mapping.validator.internal.util.DDMFormRuleValidatorUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanProperties;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = DDMFormValidator.class)
public class DDMFormValidatorImpl implements DDMFormValidator {

	@Override
	public void validate(DDMForm ddmForm)
		throws DDMFormFieldValueValidationException,
			   DDMFormValidationException {

		DDMFormRuleValidatorUtil.validateDDMFormRules(
			_ddmExpressionFactory, ddmForm.getDDMFormRules());

		_validateDDMFormLocales(ddmForm);

		List<DDMFormField> ddmFormFields = ddmForm.getDDMFormFields();

		if (ddmFormFields.isEmpty()) {
			throw new MustSetFieldsForForm();
		}

		_validateDDMFormFieldNames(ddmFormFields);

		Set<String> duplicatedDDMFormFieldReferences = new HashSet<>();

		_addDuplicatedDDMFormFieldReferences(
			ddmFormFields, new HashSet<>(), duplicatedDDMFormFieldReferences);

		if (SetUtil.isNotEmpty(duplicatedDDMFormFieldReferences)) {
			throw new MustNotDuplicateFieldReference(
				duplicatedDDMFormFieldReferences);
		}

		_validateDDMFormFields(
			ddmFormFields, new HashSet<String>(),
			ddmForm.allowInvalidAvailableLocalesForProperty(),
			ddmForm.getAvailableLocales(), ddmForm.getDefaultLocale());
	}

	protected void validateDDMFormFieldValidationExpression(
			DDMFormField ddmFormField, Set<Locale> locales)
		throws DDMFormValidationException {

		DDMFormFieldValidation ddmFormFieldValidation =
			ddmFormField.getDDMFormFieldValidation();

		if (ddmFormFieldValidation == null) {
			return;
		}

		DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
			ddmFormFieldValidation.getDDMFormFieldValidationExpression();

		if ((ddmFormFieldValidationExpression == null) ||
			Validator.isNull(ddmFormFieldValidationExpression.getValue())) {

			return;
		}

		try {
			if (ddmFormFieldValidation.getParameterLocalizedValue() == null) {
				_ddmExpressionFactory.createExpression(
					CreateExpressionRequest.Builder.newBuilder(
						ddmFormFieldValidationExpression.getValue()
					).build());
			}
			else {
				LocalizedValue parameterLocalizedValue =
					ddmFormFieldValidation.getParameterLocalizedValue();

				for (Locale locale : locales) {
					_ddmExpressionFactory.createExpression(
						CreateExpressionRequest.Builder.newBuilder(
							ddmFormFieldValidationExpression.getExpression(
								null, parameterLocalizedValue.getString(locale),
								null)
						).build());
				}
			}
		}
		catch (DDMExpressionException ddmExpressionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ddmExpressionException);
			}

			throw new MustSetValidValidationExpression(
				ddmFormField.getName(),
				ddmFormFieldValidationExpression.getValue());
		}
	}

	private void _addDuplicatedDDMFormFieldReferences(
		List<DDMFormField> ddmFormFields, Set<String> ddmFormFieldReferences,
		Set<String> duplicatedDDMFormFieldReferences) {

		for (DDMFormField ddmFormField : ddmFormFields) {
			if (!ddmFormFieldReferences.add(
					StringUtil.toLowerCase(ddmFormField.getFieldReference()))) {

				duplicatedDDMFormFieldReferences.add(
					ddmFormField.getFieldReference());
			}

			_addDuplicatedDDMFormFieldReferences(
				ddmFormField.getNestedDDMFormFields(), ddmFormFieldReferences,
				duplicatedDDMFormFieldReferences);
		}
	}

	private void _validateDDMFormAvailableLocales(
			Set<Locale> availableLocales, Locale defaultLocale)
		throws DDMFormValidationException {

		if ((availableLocales == null) || availableLocales.isEmpty()) {
			throw new MustSetAvailableLocales();
		}

		if (!availableLocales.contains(defaultLocale)) {
			throw new MustSetDefaultLocaleAsAvailableLocale(defaultLocale);
		}
	}

	private void _validateDDMFormFieldIndexType(DDMFormField ddmFormField)
		throws DDMFormValidationException {

		if (!ArrayUtil.contains(
				_DDM_FORM_FIELD_INDEX_TYPES, ddmFormField.getIndexType())) {

			throw new MustSetValidIndexType(ddmFormField.getName());
		}
	}

	private void _validateDDMFormFieldName(
			DDMFormField ddmFormField, Set<String> ddmFormFieldNames)
		throws DDMFormValidationException {

		Matcher matcher = _ddmFormFieldNamePattern.matcher(
			ddmFormField.getName());

		if (!matcher.matches()) {
			throw new MustSetValidCharactersForFieldName(
				ddmFormField.getName());
		}

		if (ddmFormFieldNames.contains(
				StringUtil.toLowerCase(ddmFormField.getName()))) {

			throw new MustNotDuplicateFieldName(ddmFormField.getName());
		}

		ddmFormFieldNames.add(StringUtil.toLowerCase(ddmFormField.getName()));
	}

	private void _validateDDMFormFieldNames(List<DDMFormField> ddmFormFields)
		throws DDMFormValidationException {

		Set<String> duplicatedDDMFieldNames = new HashSet<>();

		Set<String> ddmFormFieldNames = new HashSet<>();

		for (DDMFormField ddmFormField : ddmFormFields) {
			if (!ddmFormFieldNames.add(ddmFormField.getName())) {
				duplicatedDDMFieldNames.add(ddmFormField.getName());
			}
		}

		if (SetUtil.isNotEmpty(duplicatedDDMFieldNames)) {
			throw new MustNotDuplicateFieldName(duplicatedDDMFieldNames);
		}
	}

	private void _validateDDMFormFieldOptions(
			boolean allowInvalidAvailableLocalesForProperty,
			DDMFormField ddmFormField, Set<Locale> ddmFormAvailableLocales,
			Locale ddmFormDefaultLocale)
		throws DDMFormValidationException {

		try {
			_validateDDMFormFieldOptions(
				ddmFormField, ddmFormAvailableLocales, ddmFormDefaultLocale);
		}
		catch (DDMFormValidationException ddmFormValidationException) {
			if ((ddmFormValidationException instanceof
					MustSetValidAvailableLocalesForProperty) &&
				allowInvalidAvailableLocalesForProperty) {

				return;
			}

			throw ddmFormValidationException;
		}
	}

	private void _validateDDMFormFieldOptions(
			DDMFormField ddmFormField, Set<Locale> ddmFormAvailableLocales,
			Locale ddmFormDefaultLocale)
		throws DDMFormValidationException {

		String fieldType = ddmFormField.getType();

		if (fieldType.equals(DDMFormFieldType.GRID)) {
			_validateDDMFormFieldOptionsProperties(
				ddmFormField, "columns", ddmFormAvailableLocales,
				ddmFormDefaultLocale);
			_validateDDMFormFieldOptionsProperties(
				ddmFormField, "rows", ddmFormAvailableLocales,
				ddmFormDefaultLocale);
		}

		if (!fieldType.equals(DDMFormFieldType.CHECKBOX_MULTIPLE) &&
			!fieldType.equals(DDMFormFieldType.RADIO) &&
			!fieldType.equals(DDMFormFieldType.SELECT)) {

			return;
		}

		if (!Validator.isBlank(ddmFormField.getDataSourceType()) &&
			!Objects.equals(ddmFormField.getDataSourceType(), "manual")) {

			return;
		}

		_validateDDMFormFieldOptionsProperties(
			ddmFormField, "options", ddmFormAvailableLocales,
			ddmFormDefaultLocale);
	}

	private void _validateDDMFormFieldOptionsProperties(
			DDMFormField ddmFormField, String propertyName,
			Set<Locale> ddmFormAvailableLocales, Locale ddmFormDefaultLocale)
		throws DDMFormValidationException {

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		if (!propertyName.equals("options")) {
			ddmFormFieldOptions = (DDMFormFieldOptions)ddmFormField.getProperty(
				propertyName);
		}

		Set<String> optionsValues = Collections.emptySet();

		if (ddmFormFieldOptions != null) {
			optionsValues = ddmFormFieldOptions.getOptionsValues();
		}

		if (optionsValues.isEmpty()) {
			LocalizedValue localizedValue = ddmFormField.getLabel();

			throw new MustSetOptionsForField(
				localizedValue.getString(ddmFormDefaultLocale),
				ddmFormField.getName());
		}

		for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
			LocalizedValue localizedValue = ddmFormFieldOptions.getOptionLabels(
				optionValue);

			_validateDDMFormFieldPropertyValue(
				ddmFormField.getName(), propertyName, localizedValue,
				ddmFormAvailableLocales, ddmFormDefaultLocale);
		}
	}

	private void _validateDDMFormFieldPropertyValue(
			String fieldName, String propertyName, LocalizedValue propertyValue,
			Set<Locale> ddmFormAvailableLocales, Locale ddmFormDefaultLocale)
		throws DDMFormValidationException {

		if (!ddmFormDefaultLocale.equals(propertyValue.getDefaultLocale())) {
			throw new MustSetValidDefaultLocaleForProperty(
				fieldName, propertyName);
		}

		if (!ddmFormAvailableLocales.equals(
				propertyValue.getAvailableLocales())) {

			throw new MustSetValidAvailableLocalesForProperty(
				fieldName, propertyName);
		}
	}

	private void _validateDDMFormFieldType(DDMFormField ddmFormField)
		throws DDMFormValidationException {

		if (Validator.isNull(ddmFormField.getType())) {
			throw new MustSetFieldType(ddmFormField.getName());
		}

		Matcher matcher = _ddmFormFieldTypePattern.matcher(
			ddmFormField.getType());

		if (!matcher.matches()) {
			throw new MustSetValidCharactersForFieldType(
				ddmFormField.getType());
		}

		Set<String> ddmFormFieldTypeNames = new HashSet<>(
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldTypeNames());

		ddmFormFieldTypeNames.addAll(
			SetUtil.fromArray(DDMConstants.SUPPORTED_DDM_FORM_FIELD_TYPES));

		if (!ddmFormFieldTypeNames.contains(ddmFormField.getType())) {
			throw new MustSetValidType(ddmFormField.getType());
		}
	}

	private void _validateDDMFormFieldVisibilityExpression(
			DDMFormField ddmFormField)
		throws DDMFormValidationException {

		String visibilityExpression = ddmFormField.getVisibilityExpression();

		if (Validator.isNull(visibilityExpression)) {
			return;
		}

		try {
			_ddmExpressionFactory.createExpression(
				CreateExpressionRequest.Builder.newBuilder(
					visibilityExpression
				).build());
		}
		catch (DDMExpressionException ddmExpressionException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ddmExpressionException);
			}

			throw new MustSetValidVisibilityExpression(
				ddmFormField.getName(), visibilityExpression);
		}
	}

	private void _validateDDMFormFields(
			List<DDMFormField> ddmFormFields, Set<String> ddmFormFieldNames,
			boolean allowInvalidAvailableLocalesForProperty,
			Set<Locale> ddmFormAvailableLocales, Locale ddmFormDefaultLocale)
		throws DDMFormValidationException {

		for (DDMFormField ddmFormField : ddmFormFields) {
			_validateDDMFormFieldName(ddmFormField, ddmFormFieldNames);

			_validateDDMFormFieldType(ddmFormField);

			_validateDDMFormFieldIndexType(ddmFormField);

			_validateDDMFormFieldOptions(
				allowInvalidAvailableLocalesForProperty, ddmFormField,
				ddmFormAvailableLocales, ddmFormDefaultLocale);

			_validateOptionalDDMFormFieldLocalizedProperty(
				ddmFormField, "label", allowInvalidAvailableLocalesForProperty,
				ddmFormAvailableLocales, ddmFormDefaultLocale);

			_validateOptionalDDMFormFieldLocalizedProperty(
				ddmFormField, "tip", allowInvalidAvailableLocalesForProperty,
				ddmFormAvailableLocales, ddmFormDefaultLocale);

			validateDDMFormFieldValidationExpression(
				ddmFormField, ddmFormAvailableLocales);
			_validateDDMFormFieldVisibilityExpression(ddmFormField);

			_validateDDMFormFields(
				ddmFormField.getNestedDDMFormFields(), ddmFormFieldNames,
				allowInvalidAvailableLocalesForProperty,
				ddmFormAvailableLocales, ddmFormDefaultLocale);
		}
	}

	private void _validateDDMFormLocales(DDMForm ddmForm)
		throws DDMFormValidationException {

		Locale defaultLocale = ddmForm.getDefaultLocale();

		if (defaultLocale == null) {
			throw new MustSetDefaultLocale();
		}

		_validateDDMFormAvailableLocales(
			ddmForm.getAvailableLocales(), defaultLocale);
	}

	private void _validateOptionalDDMFormFieldLocalizedProperty(
			DDMFormField ddmFormField, String propertyName,
			boolean allowInvalidAvailableLocalesForProperty,
			Set<Locale> ddmFormAvailableLocales, Locale ddmFormDefaultLocale)
		throws DDMFormValidationException {

		LocalizedValue propertyValue =
			(LocalizedValue)_beanProperties.getObject(
				ddmFormField, propertyName);

		if ((propertyValue == null) ||
			MapUtil.isEmpty(propertyValue.getValues())) {

			return;
		}

		try {
			_validateDDMFormFieldPropertyValue(
				ddmFormField.getName(), propertyName, propertyValue,
				ddmFormAvailableLocales, ddmFormDefaultLocale);
		}
		catch (DDMFormValidationException ddmFormValidationException) {
			if ((ddmFormValidationException instanceof
					MustSetValidAvailableLocalesForProperty) &&
				allowInvalidAvailableLocalesForProperty) {

				return;
			}

			throw ddmFormValidationException;
		}
	}

	private static final String[] _DDM_FORM_FIELD_INDEX_TYPES = {
		StringPool.BLANK, "keyword", "none", "text"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormValidatorImpl.class);

	private static final Pattern _ddmFormFieldNamePattern = Pattern.compile(
		"([^\\p{Punct}|\\p{Space}$]|_)+");
	private static final Pattern _ddmFormFieldTypePattern = Pattern.compile(
		"([^\\p{Punct}|\\p{Space}$]|[-_])+");

	@Reference
	private BeanProperties _beanProperties;

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;

}