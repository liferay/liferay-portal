/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.definition.ConfigurationFieldOptionsProvider;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.storage.constants.FieldConstants;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Kamesh Sampath
 * @author Raymond Augé
 * @author Marcellus Tavares
 */
public class ConfigurationModelToDDMFormConverter {

	public static final String NUMBER_TYPE_VALUE_VALIDATION_EXPRESSION_NAME =
		"numberTypeValueValidation";

	public ConfigurationModelToDDMFormConverter(
		ConfigurationModel configurationModel, Locale locale,
		ResourceBundle resourceBundle) {

		_configurationModel = configurationModel;
		_locale = locale;
		_resourceBundle = resourceBundle;
	}

	public DDMForm getDDMForm() {
		DDMForm ddmForm = getConfigurationDDMForm();

		if (ddmForm == null) {
			ddmForm = new DDMForm();
		}

		ddmForm.addAvailableLocale(_locale);
		ddmForm.setDefaultLocale(_locale);

		_addRequiredDDMFormFields(ddmForm);
		_addOptionalDDMFormFields(ddmForm);

		if (_configurationModel.isReadOnly()) {
			for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
				ddmFormField.setReadOnly(true);
			}
		}

		return ddmForm;
	}

	protected DDMForm getConfigurationDDMForm() {
		Class<?> formClass =
			ConfigurationDDMFormDeclarationUtil.getConfigurationDDMFormClass(
				_configurationModel);

		if (formClass == null) {
			return null;
		}

		try {
			return DDMFormFactory.create(formClass);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalArgumentException);
			}
		}

		return null;
	}

	protected ConfigurationFieldOptionsProvider
		getConfigurationFieldOptionsProvider(
			AttributeDefinition attributeDefinition) {

		String pid = _configurationModel.getBaseID();

		if (_configurationModel.isFactory()) {
			pid = _configurationModel.getFactoryPid();
		}

		return _serviceTrackerMap.getService(
			_getKey(pid, attributeDefinition.getID()));
	}

	protected String getDDMFormFieldDataType(
		AttributeDefinition attributeDefinition) {

		int type = attributeDefinition.getType();

		if (type == AttributeDefinition.BOOLEAN) {
			return FieldConstants.BOOLEAN;
		}
		else if (type == AttributeDefinition.DOUBLE) {
			return FieldConstants.DOUBLE;
		}
		else if (type == AttributeDefinition.FLOAT) {
			return FieldConstants.FLOAT;
		}
		else if (type == AttributeDefinition.INTEGER) {
			return FieldConstants.INTEGER;
		}
		else if (type == AttributeDefinition.LONG) {
			return FieldConstants.LONG;
		}
		else if (type == AttributeDefinition.SHORT) {
			return FieldConstants.SHORT;
		}

		return FieldConstants.STRING;
	}

	protected String getDDMFormFieldType(
		AttributeDefinition attributeDefinition,
		DDMFormFieldOptions ddmFormFieldOptions) {

		int type = attributeDefinition.getType();

		if (type == AttributeDefinition.BOOLEAN) {
			if (SetUtil.isEmpty(ddmFormFieldOptions.getOptionsValues())) {
				return DDMFormFieldType.CHECKBOX;
			}

			return DDMFormFieldType.RADIO;
		}
		else if (type == AttributeDefinition.INTEGER) {
			return DDMFormFieldType.NUMERIC;
		}
		else if (type == AttributeDefinition.LONG) {
			return DDMFormFieldType.NUMERIC;
		}
		else if (type == AttributeDefinition.PASSWORD) {
			return DDMFormFieldType.PASSWORD;
		}
		else if (type == ExtendedAttributeDefinition.LOCALIZED_VALUES_MAP) {
			return DDMFormFieldType.LOCALIZABLE_TEXT;
		}

		ConfigurationFieldOptionsProvider configurationFieldOptionsProvider =
			getConfigurationFieldOptionsProvider(attributeDefinition);

		if (SetUtil.isNotEmpty(ddmFormFieldOptions.getOptionsValues()) ||
			(configurationFieldOptionsProvider != null)) {

			return DDMFormFieldType.SELECT;
		}

		return DDMFormFieldType.TEXT;
	}

	private static String _getKey(String configurationPid, String fieldName) {
		return StringBundler.concat(
			configurationPid, StringPool.POUND, fieldName);
	}

	private static Collection<String> _getPropertyValues(
		ServiceReference<?> serviceReference, String name) {

		Object propertyValue = serviceReference.getProperty(name);

		if (propertyValue == null) {
			return Collections.emptyList();
		}

		if (propertyValue instanceof Collection) {
			return (Collection<String>)propertyValue;
		}

		if (propertyValue instanceof Object[]) {
			return Arrays.asList((String[])propertyValue);
		}

		return Arrays.asList((String)propertyValue);
	}

	private void _addDDMFormFields(
		AttributeDefinition[] attributeDefinitions, DDMForm ddmForm,
		boolean required) {

		if (attributeDefinitions == null) {
			return;
		}

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(false);

		for (AttributeDefinition attributeDefinition : attributeDefinitions) {
			if (!ddmFormFieldsMap.containsKey(attributeDefinition.getID())) {
				ddmForm.addDDMFormField(
					_getDDMFormField(attributeDefinition, required));
			}
		}
	}

	private void _addOptionalDDMFormFields(DDMForm ddmForm) {
		AttributeDefinition[] optionalAttributeDefinitions = ArrayUtil.filter(
			_configurationModel.getAttributeDefinitions(
				ObjectClassDefinition.OPTIONAL),
			_requiredInputPredicate.negate());

		_addDDMFormFields(optionalAttributeDefinitions, ddmForm, false);
	}

	private void _addRequiredDDMFormFields(DDMForm ddmForm) {
		AttributeDefinition[] requiredAttributeDefinitions = ArrayUtil.append(
			_configurationModel.getAttributeDefinitions(
				ObjectClassDefinition.REQUIRED),
			ArrayUtil.filter(
				_configurationModel.getAttributeDefinitions(
					ObjectClassDefinition.OPTIONAL),
				_requiredInputPredicate));

		_addDDMFormFields(requiredAttributeDefinitions, ddmForm, true);
	}

	private DDMFormFieldOptions _getDDMFieldOptions(
		AttributeDefinition attributeDefinition) {

		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		ConfigurationFieldOptionsProvider configurationFieldOptionsProvider =
			getConfigurationFieldOptionsProvider(attributeDefinition);

		if (configurationFieldOptionsProvider != null) {
			for (ConfigurationFieldOptionsProvider.Option option :
					configurationFieldOptionsProvider.getOptions()) {

				ddmFormFieldOptions.addOptionLabel(
					option.getValue(), _locale, option.getLabel(_locale));
			}

			return ddmFormFieldOptions;
		}

		String[] optionLabels = attributeDefinition.getOptionLabels();
		String[] optionValues = attributeDefinition.getOptionValues();

		if ((optionLabels == null) || (optionValues == null)) {
			return ddmFormFieldOptions;
		}

		for (int i = 0; i < optionLabels.length; i++) {
			ddmFormFieldOptions.addOptionLabel(
				optionValues[i], _locale, _translate(optionLabels[i]));
		}

		return ddmFormFieldOptions;
	}

	private DDMFormField _getDDMFormField(
		AttributeDefinition attributeDefinition, boolean required) {

		DDMFormFieldOptions ddmFormFieldOptions = _getDDMFieldOptions(
			attributeDefinition);

		String type = getDDMFormFieldType(
			attributeDefinition, ddmFormFieldOptions);

		DDMFormField ddmFormField = new DDMFormField(
			DDMFormFieldNameUtil.normalizeFieldName(
				attributeDefinition.getID()),
			type);

		_setDDMFormFieldDataType(attributeDefinition, ddmFormField);
		_setDDMFormFieldLabel(attributeDefinition, ddmFormField);
		_setDDMFormFieldOptions(ddmFormField, ddmFormFieldOptions);
		_setDDMFormFieldPredefinedValue(attributeDefinition, ddmFormField);
		_setDDMFormFieldReadOnly(attributeDefinition, ddmFormField);
		_setDDMFormFieldRequired(ddmFormField, required);
		_setDDMFormFieldTip(attributeDefinition, ddmFormField);
		_setDDMFormFieldValidation(ddmFormField);
		_setDDMFormFieldVisibilityExpression(attributeDefinition, ddmFormField);

		ddmFormField.setFieldReference(attributeDefinition.getID());
		ddmFormField.setLocalizable(true);
		ddmFormField.setShowLabel(true);

		_setDDMFormFieldRepeatable(attributeDefinition, ddmFormField);

		_setDDMFormFieldDisplayStyle(ddmFormField);

		return ddmFormField;
	}

	private String _getDDMFormFieldPredefinedValue(
		AttributeDefinition attributeDefinition) {

		String dataType = getDDMFormFieldDataType(attributeDefinition);

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

	private Map<String, String> _getExtensionAttributes(
		AttributeDefinition attributeDefinition) {

		ExtendedAttributeDefinition extendedAttributeDefinition =
			_configurationModel.getExtendedAttributeDefinition(
				attributeDefinition.getID());

		return extendedAttributeDefinition.getExtensionAttributes(
			com.liferay.portal.configuration.metatype.annotations.
				ExtendedAttributeDefinition.XML_NAMESPACE);
	}

	private void _setDDMFormFieldDataType(
		AttributeDefinition attributeDefinition, DDMFormField ddmFormField) {

		ddmFormField.setDataType(getDDMFormFieldDataType(attributeDefinition));
	}

	private void _setDDMFormFieldDisplayStyle(DDMFormField ddmFormField) {
		if (Objects.equals(ddmFormField.getDataType(), FieldConstants.STRING)) {
			ddmFormField.setProperty("displayStyle", "multiline");
		}
	}

	private void _setDDMFormFieldLabel(
		AttributeDefinition attributeDefinition, DDMFormField ddmFormField) {

		LocalizedValue label = new LocalizedValue(_locale);

		Map<String, String> extensionAttributes = _getExtensionAttributes(
			attributeDefinition);

		List<String> nameArguments = StringUtil.split(
			extensionAttributes.get("name-arguments"));

		label.addString(
			_locale, _translate(attributeDefinition.getName(), nameArguments));

		ddmFormField.setLabel(label);
	}

	private void _setDDMFormFieldOptions(
		DDMFormField ddmFormField, DDMFormFieldOptions ddmFormFieldOptions) {

		ddmFormField.setDDMFormFieldOptions(ddmFormFieldOptions);
	}

	private void _setDDMFormFieldPredefinedValue(
		AttributeDefinition attributeDefinition, DDMFormField ddmFormField) {

		String type = ddmFormField.getType();

		String predefinedValueString = _getDDMFormFieldPredefinedValue(
			attributeDefinition);

		if (type.equals(DDMFormFieldType.SELECT)) {
			predefinedValueString = "[\"" + predefinedValueString + "\"]";
		}

		LocalizedValue predefinedValue = new LocalizedValue(_locale);

		predefinedValue.addString(_locale, predefinedValueString);

		ddmFormField.setPredefinedValue(predefinedValue);
	}

	private void _setDDMFormFieldReadOnly(
		AttributeDefinition attributeDefinition, DDMFormField ddmFormField) {

		if (_configurationModel.hasConfigurationOverrideProperty(
				attributeDefinition.getID())) {

			ddmFormField.setReadOnly(true);
		}
	}

	private void _setDDMFormFieldRepeatable(
		AttributeDefinition attributeDefinition, DDMFormField ddmFormField) {

		if (attributeDefinition.getCardinality() == 0) {
			return;
		}

		ddmFormField.setRepeatable(true);
	}

	private void _setDDMFormFieldRequired(
		DDMFormField ddmFormField, boolean required) {

		if (DDMFormFieldType.CHECKBOX.equals(ddmFormField.getType())) {
			return;
		}

		ddmFormField.setRequired(required);
	}

	private void _setDDMFormFieldTip(
		AttributeDefinition attributeDefinition, DDMFormField ddmFormField) {

		LocalizedValue tip = new LocalizedValue(_locale);

		StringBundler sb = new StringBundler(3);

		Map<String, String> extensionAttributes = _getExtensionAttributes(
			attributeDefinition);

		String description = _translate(
			attributeDefinition.getDescription(),
			StringUtil.split(extensionAttributes.get("description-arguments")));

		if (Validator.isNotNull(description)) {
			sb.append(description);
		}

		if (_configurationModel.hasConfigurationOverrideProperty(
				attributeDefinition.getID())) {

			if (sb.length() > 0) {
				sb.append(StringPool.SPACE);
			}

			sb.append(
				LanguageUtil.get(
					_locale,
					"this-field-has-been-set-by-a-portal-property-and-cannot-" +
						"be-changed-here"));
		}

		tip.addString(_locale, sb.toString());

		ddmFormField.setTip(tip);
	}

	private void _setDDMFormFieldValidation(DDMFormField ddmFormField) {
		String dataType = ddmFormField.getDataType();

		String maxNumericValue = null;

		if (dataType.equals(FieldConstants.DOUBLE)) {
			maxNumericValue = String.valueOf(Double.MAX_VALUE);
		}
		else if (dataType.equals(FieldConstants.FLOAT)) {
			maxNumericValue = String.valueOf(Float.MAX_VALUE);
		}
		else if (dataType.equals(FieldConstants.INTEGER)) {
			maxNumericValue = String.valueOf(Integer.MAX_VALUE);
		}
		else if (dataType.equals(FieldConstants.LONG)) {
			maxNumericValue = String.valueOf(Long.MAX_VALUE);
		}
		else if (dataType.equals(FieldConstants.SHORT)) {
			maxNumericValue = String.valueOf(Short.MAX_VALUE);
		}

		if (maxNumericValue == null) {
			return;
		}

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		LocalizedValue errorMessageLocalizedValue = new LocalizedValue();

		errorMessageLocalizedValue.addString(
			_locale,
			LanguageUtil.format(
				_locale, "please-enter-a-value-less-than-or-equal-to-x",
				maxNumericValue));

		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			errorMessageLocalizedValue);

		DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
			new DDMFormFieldValidationExpression();

		ddmFormFieldValidationExpression.setName(
			NUMBER_TYPE_VALUE_VALIDATION_EXPRESSION_NAME);
		ddmFormFieldValidationExpression.setValue(
			StringBundler.concat(
				"(", ddmFormField.getName(), "<=", maxNumericValue, ")"));

		ddmFormFieldValidation.setDDMFormFieldValidationExpression(
			ddmFormFieldValidationExpression);

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);
	}

	private void _setDDMFormFieldVisibilityExpression(
		AttributeDefinition attributeDefinition, DDMFormField ddmFormField) {

		String[] hiddenFieldKeys = {
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			ExtendedObjectClassDefinition.Scope.COMPANY.getValue()
		};

		if (ArrayUtil.contains(hiddenFieldKeys, attributeDefinition.getID()) ||
			ArrayUtil.contains(
				hiddenFieldKeys, attributeDefinition.getName())) {

			ddmFormField.setVisibilityExpression("FALSE");

			return;
		}

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			_configurationModel.getConfigurationScopeDisplayContext();

		Map<String, String> extensionAttributes = _getExtensionAttributes(
			attributeDefinition);

		if ((configurationScopeDisplayContext != null) &&
			(!ConfigurationVisibilityUtil.isVisibleByFeatureFlagKey(
				extensionAttributes.get("feature.flag.key"),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK()) ||
			 !ConfigurationVisibilityUtil.isVisibleByVisibilityControllerKey(
				 extensionAttributes.get("visibility-controller-key"),
				 configurationScopeDisplayContext.getScope(),
				 configurationScopeDisplayContext.getScopePK()))) {

			ddmFormField.setVisibilityExpression("FALSE");
		}
	}

	private String _translate(String key) {
		return _translate(key, Collections.emptyList());
	}

	private String _translate(String key, List<String> arguments) {
		if ((_resourceBundle == null) || (key == null)) {
			return key;
		}

		String value = null;

		if (ListUtil.isEmpty(arguments)) {
			value = LanguageUtil.get(_resourceBundle, key);
		}
		else {
			value = LanguageUtil.format(
				_resourceBundle, key, arguments.toArray(new String[0]));
		}

		if (value == null) {
			return key;
		}

		return value;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ConfigurationModelToDDMFormConverter.class);

	private static final ServiceTrackerMap
		<String, ConfigurationFieldOptionsProvider> _serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ConfigurationModelToDDMFormConverter.class);

		_serviceTrackerMap =
			(ServiceTrackerMap<String, ConfigurationFieldOptionsProvider>)
				(ServiceTrackerMap)ServiceTrackerMapFactory.openSingleValueMap(
					bundle.getBundleContext(),
					ConfigurationFieldOptionsProvider.class, null,
					(serviceReference, emitter) -> {
						for (String configurationPid :
								_getPropertyValues(
									serviceReference, "configuration.pid")) {

							for (String fieldName :
									_getPropertyValues(
										serviceReference,
										"configuration.field.name")) {

								emitter.emit(
									_getKey(configurationPid, fieldName));
							}
						}
					});
	}

	private final ConfigurationModel _configurationModel;
	private final Locale _locale;

	private final Predicate<AttributeDefinition> _requiredInputPredicate =
		attributeDefinition -> {
			Map<String, String> extensionAttributes = _getExtensionAttributes(
				attributeDefinition);

			return Boolean.valueOf(extensionAttributes.get("required-input"));
		};

	private final ResourceBundle _resourceBundle;

}