/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal.servlet;

import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMFormRule;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Marcellus Tavares
 */
public class DDMFormTemplateContextProcessor {

	public DDMFormTemplateContextProcessor(
		JSONObject jsonObject, String languageId) {

		_jsonObject = jsonObject;

		_ddmFormValues = new DDMFormValues(_ddmForm);

		_locale = LocaleUtil.fromLanguageId(languageId);

		_initModels();

		process();
	}

	public DDMForm getDDMForm() {
		return _ddmForm;
	}

	public long getDDMFormInstanceId() {
		return _ddmFormInstanceId;
	}

	public DDMFormLayout getDDMFormLayout() {
		return _ddmFormLayout;
	}

	public DDMFormValues getDDMFormValues() {
		return _ddmFormValues;
	}

	public long getGroupId() {
		return _groupId;
	}

	protected DDMFormField getDDMFormField(JSONObject jsonObject) {
		String name = jsonObject.getString("fieldName");
		String type = jsonObject.getString("type");

		DDMFormField ddmFormField = new DDMFormField(name, type);

		_setDDMFormFieldConfirmationErrorMessage(
			jsonObject.getString("confirmationErrorMessage"), ddmFormField);
		_setDDMFormFieldConfirmationLabel(
			jsonObject.getString("confirmationLabel"), ddmFormField);
		_setDDMFormFieldCustomProperties(jsonObject, ddmFormField);
		_setDDMFormFieldDataType(
			jsonObject.getString("dataType"), ddmFormField);
		_setDDMFormFieldFieldName(
			jsonObject.getString("fieldName"), ddmFormField);
		_setDDMFormFieldFieldReference(
			jsonObject.getString("fieldReference"), ddmFormField);
		_setDDMFormFieldInputMaskFormat(
			jsonObject.getString("inputMaskFormat"), ddmFormField);
		_setDDMFormFieldLabel(jsonObject.getString("label"), ddmFormField);
		_setDDMFormFieldLayout(ddmFormField, jsonObject.getString("layout"));
		_setDDMFormFieldLocalizable(
			jsonObject.getBoolean("localizable", false), ddmFormField);
		_setDDMFormFieldMultiple(
			jsonObject.getBoolean("multiple"), ddmFormField);
		_setDDMFormFieldNumericInputMask(
			jsonObject.getString("numericInputMask"), ddmFormField);
		_setDDMFormFieldOptions(
			jsonObject.getJSONArray("options"), ddmFormField);
		_setDDMFormFieldPlaceholder(
			jsonObject.getString("placeholder"), ddmFormField);
		_setDDMFormFieldPredefinedValue(
			jsonObject.getString("predefinedValue"), ddmFormField);
		_setDDMFormFieldProperty(
			ddmFormField, "buttonLabel", jsonObject.getString("buttonLabel"));
		_setDDMFormFieldProperty(
			ddmFormField, "title", jsonObject.getString("title"));
		_setDDMFormFieldPropertyDDMStructureId(jsonObject, ddmFormField);
		_setDDMFormFieldPropertyDDMStructureLayoutId(jsonObject, ddmFormField);
		_setDDMFormFieldPropertyMessage(
			ddmFormField, jsonObject.getString("message"));
		_setDDMFormFieldPropertyOptions(jsonObject, ddmFormField, "columns");
		_setDDMFormFieldPropertyRows(jsonObject, ddmFormField);
		_setDDMFormFieldPropertyUpgradedStructure(jsonObject, ddmFormField);
		_setDDMFormFieldReadOnly(
			jsonObject.getBoolean("readOnly", false), ddmFormField);
		_setDDMFormFieldRepeatable(
			jsonObject.getBoolean("repeatable", false), ddmFormField);
		_setDDMFormFieldRequired(
			jsonObject.getBoolean("required", false), ddmFormField);
		_setDDMFormFieldRequiredErrorMessage(
			_getLocalizedValue(jsonObject.getString("requiredErrorMessage")),
			ddmFormField);
		_setDDMFormFieldText(jsonObject.getString("text"), ddmFormField);
		_setDDMFormFieldTooltip(jsonObject.getString("tooltip"), ddmFormField);
		_setDDMFormFieldValid(
			jsonObject.getBoolean("valid", true), ddmFormField);
		_setDDMFormFieldValidation(
			jsonObject.getJSONObject("validation"), ddmFormField);
		_setDDMFormFieldVisibilityExpression(
			jsonObject.getString("visibilityExpression"), ddmFormField);
		_setDDMFormFieldVisibleFields(
			ddmFormField, jsonObject.getString("visibleFields"));

		_setDDMFormFieldNestedFields(
			jsonObject.getJSONArray("nestedFields"), ddmFormField);

		return ddmFormField;
	}

	protected DDMFormFieldOptions getDDMFormFieldOptions(JSONArray jsonArray) {
		DDMFormFieldOptions ddmFormFieldOptions = new DDMFormFieldOptions();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String value = jsonObject.getString("value");

			JSONObject labelMapJSONObject = jsonObject.getJSONObject(
				"labelMap");

			if (labelMapJSONObject != null) {
				Map<String, String> labelMap = JSONUtil.toStringMap(
					labelMapJSONObject);

				for (Map.Entry<String, String> entry : labelMap.entrySet()) {
					ddmFormFieldOptions.addOptionLabel(
						value, LocaleUtil.fromLanguageId(entry.getKey()),
						entry.getValue());
				}
			}
			else {
				ddmFormFieldOptions.addOptionLabel(
					value, _locale, jsonObject.getString("label"));
			}

			ddmFormFieldOptions.addOptionReference(
				value, jsonObject.getString("reference"));
		}

		return ddmFormFieldOptions;
	}

	protected DDMFormFieldValue getDDMFormFieldValue(JSONObject jsonObject) {
		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setConfirmationValue(
			jsonObject.get("confirmationValue"));
		ddmFormFieldValue.setFieldReference(
			jsonObject.getString("fieldReference"));
		ddmFormFieldValue.setName(jsonObject.getString("fieldName"));
		ddmFormFieldValue.setInstanceId(jsonObject.getString("instanceId"));

		_setDDMFormFieldValueValue(
			jsonObject.getString("value"),
			jsonObject.getBoolean("localizable", false), ddmFormFieldValue);

		_setDDMFormFieldValueNestedFieldValues(
			jsonObject.getJSONArray("nestedFields"), ddmFormFieldValue);

		return ddmFormFieldValue;
	}

	protected void process() {
		_ddmFormLayout.setNextPage(_jsonObject.getInt("nextPage"));
		_ddmFormLayout.setPreviousPage(_jsonObject.getInt("previousPage"));

		_traversePages(_jsonObject.getJSONArray("pages"));
	}

	protected void setDDMFormInstanceId() {
		_ddmFormInstanceId = _jsonObject.getLong("formId", 0);
	}

	protected void setGroupId() {
		_groupId = _jsonObject.getLong("groupId", 0);
	}

	private void _addDDMFormDDMFormField(JSONObject jsonObject) {
		Map<String, DDMFormField> ddmFormFields = _ddmForm.getDDMFormFieldsMap(
			true);

		String fieldName = jsonObject.getString("fieldName");

		if (ddmFormFields.containsKey(fieldName)) {
			return;
		}

		_ddmForm.addDDMFormField(getDDMFormField(jsonObject));
	}

	private void _addDDMFormValuesDDMFormFieldValue(JSONObject jsonObject) {
		_ddmFormValues.addDDMFormFieldValue(getDDMFormFieldValue(jsonObject));
	}

	private DDMFormRule _getDDMFormRule(JSONObject jsonObject) {
		List<String> actions = _getDDMFormRuleActions(
			jsonObject.getJSONArray("actions"));

		return new DDMFormRule(actions, jsonObject.getString("condition"));
	}

	private List<String> _getDDMFormRuleActions(JSONArray jsonArray) {
		List<String> actions = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			actions.add(jsonArray.getString(i));
		}

		return actions;
	}

	private List<DDMFormRule> _getDDMFormRules(JSONArray jsonArray) {
		List<DDMFormRule> ddmFormRules = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormRules.add(_getDDMFormRule(jsonArray.getJSONObject(i)));
		}

		return ddmFormRules;
	}

	private LocalizedValue _getLocalizedValue(String value) {
		LocalizedValue localizedValue = new LocalizedValue(_locale);

		localizedValue.addString(_locale, value);

		return localizedValue;
	}

	private void _initModels() {
		_setDDMFormDefaultLocale();
		setDDMFormInstanceId();
		_setDDMFormRules();
		_setDDMFormValuesAvailableLocales();
		_setDDMFormValuesDefaultLocale();
		setGroupId();
		_setObjectFieldsJSONArray();
	}

	private void _setDDMFormDefaultLocale() {
		_ddmForm.setDefaultLocale(_locale);
	}

	private void _setDDMFormFieldConfirmationErrorMessage(
		String confirmationErrorMessage, DDMFormField ddmFormField) {

		ddmFormField.setProperty(
			"confirmationErrorMessage",
			_getLocalizedValue(GetterUtil.getString(confirmationErrorMessage)));
	}

	private void _setDDMFormFieldConfirmationLabel(
		String confirmationLabel, DDMFormField ddmFormField) {

		ddmFormField.setProperty(
			"confirmationLabel",
			_getLocalizedValue(GetterUtil.getString(confirmationLabel)));
	}

	private void _setDDMFormFieldCustomProperties(
		JSONObject jsonObject, DDMFormField ddmFormField) {

		Iterator<String> iterator = jsonObject.keys();

		Map<String, Object> properties = ddmFormField.getProperties();

		while (iterator.hasNext()) {
			String key = iterator.next();

			if (!properties.containsKey(key) && !key.equals("dataSourceType")) {
				ddmFormField.setProperty(key, jsonObject.get(key));
			}
		}
	}

	private void _setDDMFormFieldDataType(
		String dataType, DDMFormField ddmFormField) {

		ddmFormField.setDataType(GetterUtil.getString(dataType));
	}

	private void _setDDMFormFieldFieldName(
		String fieldName, DDMFormField ddmFormField) {

		ddmFormField.setName(GetterUtil.getString(fieldName));
	}

	private void _setDDMFormFieldFieldReference(
		String fieldReference, DDMFormField ddmFormField) {

		ddmFormField.setFieldReference(GetterUtil.getString(fieldReference));
	}

	private void _setDDMFormFieldInputMaskFormat(
		String inputMaskFormat, DDMFormField ddmFormField) {

		ddmFormField.setProperty(
			"inputMaskFormat",
			_getLocalizedValue(GetterUtil.getString(inputMaskFormat)));
	}

	private void _setDDMFormFieldLabel(
		String label, DDMFormField ddmFormField) {

		ddmFormField.setLabel(_getLocalizedValue(GetterUtil.getString(label)));
	}

	private void _setDDMFormFieldLayout(
		DDMFormField ddmFormField, String layout) {

		ddmFormField.setProperty(
			"layout", _getLocalizedValue(GetterUtil.getString(layout)));
	}

	private void _setDDMFormFieldLocalizable(
		boolean localizable, DDMFormField ddmFormField) {

		ddmFormField.setLocalizable(localizable);
	}

	private void _setDDMFormFieldMultiple(
		boolean multiple, DDMFormField ddmFormField) {

		ddmFormField.setMultiple(multiple);
	}

	private void _setDDMFormFieldNestedFields(
		JSONArray jsonArray, DDMFormField ddmFormField) {

		if (jsonArray == null) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			DDMFormField nestedDDMFormField = getDDMFormField(
				jsonArray.getJSONObject(i));

			ddmFormField.addNestedDDMFormField(nestedDDMFormField);
		}
	}

	private void _setDDMFormFieldNumericInputMask(
		String numericInputMask, DDMFormField ddmFormField) {

		ddmFormField.setProperty(
			"numericInputMask",
			_getLocalizedValue(GetterUtil.getString(numericInputMask)));
	}

	private void _setDDMFormFieldOptions(
		JSONArray jsonArray, DDMFormField ddmFormField) {

		if (jsonArray == null) {
			return;
		}

		ddmFormField.setDDMFormFieldOptions(getDDMFormFieldOptions(jsonArray));
	}

	private void _setDDMFormFieldPlaceholder(
		String placeholder, DDMFormField ddmFormField) {

		ddmFormField.setProperty(
			"placeholder",
			_getLocalizedValue(GetterUtil.getString(placeholder)));
	}

	private void _setDDMFormFieldPredefinedValue(
		String predefinedValue, DDMFormField ddmFormField) {

		ddmFormField.setProperty(
			"predefinedValue",
			_getLocalizedValue(GetterUtil.getString(predefinedValue)));
	}

	private void _setDDMFormFieldProperty(
		DDMFormField ddmFormField, String propertyName, String propertyValue) {

		if (!Objects.equals(ddmFormField.getType(), "redirect_button")) {
			return;
		}

		ddmFormField.setProperty(
			propertyName, new Object[] {_getLocalizedValue(propertyValue)});
	}

	private void _setDDMFormFieldPropertyDDMStructureId(
		JSONObject jsonObject, DDMFormField ddmFormField) {

		if (!Objects.equals(ddmFormField.getType(), "fieldset")) {
			return;
		}

		ddmFormField.setProperty(
			"ddmStructureId", jsonObject.getLong("ddmStructureId"));
	}

	private void _setDDMFormFieldPropertyDDMStructureLayoutId(
		JSONObject jsonObject, DDMFormField ddmFormField) {

		if (!Objects.equals(ddmFormField.getType(), "fieldset")) {
			return;
		}

		ddmFormField.setProperty(
			"ddmStructureLayoutId", jsonObject.getLong("ddmStructureLayoutId"));
	}

	private void _setDDMFormFieldPropertyFieldSetRows(
		JSONObject jsonObject, DDMFormField ddmFormField) {

		JSONArray jsonArray = jsonObject.getJSONArray("rows");

		if (jsonArray == null) {
			return;
		}

		ddmFormField.setProperty("rows", jsonArray.toString());
	}

	private void _setDDMFormFieldPropertyMessage(
		DDMFormField ddmFormField, String message) {

		if (!Objects.equals(ddmFormField.getType(), "redirect_button")) {
			return;
		}

		ddmFormField.setProperty("message", message);
	}

	private void _setDDMFormFieldPropertyOptions(
		JSONObject jsonObject, DDMFormField ddmFormField, String property) {

		JSONArray jsonArray = jsonObject.getJSONArray(property);

		if (jsonArray == null) {
			return;
		}

		ddmFormField.setProperty(property, getDDMFormFieldOptions(jsonArray));
	}

	private void _setDDMFormFieldPropertyRows(
		JSONObject jsonObject, DDMFormField ddmFormField) {

		String type = jsonObject.getString("type");

		if (type.equals("grid")) {
			_setDDMFormFieldPropertyOptions(jsonObject, ddmFormField, "rows");
		}
		else if (type.equals("fieldset")) {
			_setDDMFormFieldPropertyFieldSetRows(jsonObject, ddmFormField);
		}
	}

	private void _setDDMFormFieldPropertyUpgradedStructure(
		JSONObject jsonObject, DDMFormField ddmFormField) {

		if (!Objects.equals(ddmFormField.getType(), "fieldset")) {
			return;
		}

		ddmFormField.setProperty(
			"upgradedStructure", jsonObject.getBoolean("upgradedStructure"));
	}

	private void _setDDMFormFieldReadOnly(
		boolean readOnly, DDMFormField ddmFormField) {

		ddmFormField.setReadOnly(readOnly);
	}

	private void _setDDMFormFieldRepeatable(
		boolean repeatable, DDMFormField ddmFormField) {

		ddmFormField.setRepeatable(repeatable);
	}

	private void _setDDMFormFieldRequired(
		boolean required, DDMFormField ddmFormField) {

		ddmFormField.setRequired(required);
	}

	private void _setDDMFormFieldRequiredErrorMessage(
		LocalizedValue requiredErrorMessage, DDMFormField ddmFormField) {

		ddmFormField.setRequiredErrorMessage(requiredErrorMessage);
	}

	private void _setDDMFormFieldText(String text, DDMFormField ddmFormField) {
		ddmFormField.setProperty(
			"text", _getLocalizedValue(GetterUtil.getString(text)));
	}

	private void _setDDMFormFieldTooltip(
		String tooltip, DDMFormField ddmFormField) {

		ddmFormField.setProperty(
			"tooltip", _getLocalizedValue(GetterUtil.getString(tooltip)));
	}

	private void _setDDMFormFieldValid(
		boolean valid, DDMFormField ddmFormField) {

		ddmFormField.setProperty("valid", valid);
	}

	private void _setDDMFormFieldValidation(
		JSONObject jsonObject, DDMFormField ddmFormField) {

		if ((jsonObject == null) || !jsonObject.has("expression")) {
			return;
		}

		DDMFormFieldValidation ddmFormFieldValidation =
			new DDMFormFieldValidation();

		ddmFormFieldValidation.setErrorMessageLocalizedValue(
			_getLocalizedValue(jsonObject.getString("errorMessage")));

		JSONObject expressionJSONObject = jsonObject.getJSONObject(
			"expression");

		if (expressionJSONObject != null) {
			ddmFormFieldValidation.setDDMFormFieldValidationExpression(
				new DDMFormFieldValidationExpression() {
					{
						setName(expressionJSONObject.getString("name"));
						setValue(expressionJSONObject.getString("value"));
					}
				});
		}
		else {
			ddmFormFieldValidation.setDDMFormFieldValidationExpression(
				new DDMFormFieldValidationExpression() {
					{
						setValue(jsonObject.getString("expression"));
					}
				});
		}

		ddmFormFieldValidation.setParameterLocalizedValue(
			_getLocalizedValue(jsonObject.getString("parameter")));

		ddmFormField.setDDMFormFieldValidation(ddmFormFieldValidation);
	}

	private void _setDDMFormFieldValueNestedFieldValues(
		JSONArray jsonArray, DDMFormFieldValue ddmFormFieldValue) {

		if (jsonArray == null) {
			return;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			DDMFormFieldValue nestedDDMFormFieldValue = getDDMFormFieldValue(
				jsonArray.getJSONObject(i));

			ddmFormFieldValue.addNestedDDMFormFieldValue(
				nestedDDMFormFieldValue);
		}
	}

	private void _setDDMFormFieldValueValue(
		String value, boolean localizable,
		DDMFormFieldValue ddmFormFieldValue) {

		if (localizable) {
			ddmFormFieldValue.setValue(_getLocalizedValue(value));
		}
		else {
			ddmFormFieldValue.setValue(new UnlocalizedValue(value));
		}
	}

	private void _setDDMFormFieldVisibilityExpression(
		String visibilityExpression, DDMFormField ddmFormField) {

		ddmFormField.setVisibilityExpression(
			GetterUtil.getString(visibilityExpression));
	}

	private void _setDDMFormFieldVisibleFields(
		DDMFormField ddmFormField, String visibleFields) {

		ddmFormField.setProperty(
			"visibleFields",
			_getLocalizedValue(GetterUtil.getString(visibleFields)));
	}

	private void _setDDMFormRules() {
		_ddmForm.setDDMFormRules(
			_getDDMFormRules(_jsonObject.getJSONArray("rules")));
	}

	private void _setDDMFormValuesAvailableLocales() {
		_ddmFormValues.addAvailableLocale(_locale);
	}

	private void _setDDMFormValuesDefaultLocale() {
		_ddmFormValues.setDefaultLocale(_locale);
	}

	private void _setObjectFieldsJSONArray() {
		_ddmForm.setObjectFieldsJSONArray(
			_jsonObject.getJSONArray("objectFields"));
	}

	private void _traverseColumns(
		JSONArray jsonArray, DDMFormLayoutRow ddmFormLayoutRow) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			DDMFormLayoutColumn ddmFormLayoutColumn = new DDMFormLayoutColumn(
				jsonObject.getInt("size"));

			_traverseFields(
				jsonObject.getJSONArray("fields"), ddmFormLayoutColumn);

			ddmFormLayoutRow.addDDMFormLayoutColumn(ddmFormLayoutColumn);
		}
	}

	private void _traverseFields(
		JSONArray jsonArray, DDMFormLayoutColumn ddmFormLayoutColumn) {

		Set<String> ddmFormFieldNames = new LinkedHashSet<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			_addDDMFormDDMFormField(jsonObject);
			_addDDMFormValuesDDMFormFieldValue(jsonObject);

			ddmFormFieldNames.add(jsonObject.getString("fieldName"));
		}

		ddmFormLayoutColumn.setDDMFormFieldNames(
			ListUtil.fromCollection(ddmFormFieldNames));
	}

	private void _traversePages(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			DDMFormLayoutPage ddmFormLayoutPage = new DDMFormLayoutPage();

			ddmFormLayoutPage.setDescription(
				_getLocalizedValue(jsonObject.getString("description")));
			ddmFormLayoutPage.setTitle(
				_getLocalizedValue(jsonObject.getString("title")));

			_traverseRows(jsonObject.getJSONArray("rows"), ddmFormLayoutPage);

			_ddmFormLayout.addDDMFormLayoutPage(ddmFormLayoutPage);
		}
	}

	private void _traverseRows(
		JSONArray jsonArray, DDMFormLayoutPage ddmFormLayoutPage) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

			_traverseColumns(
				jsonObject.getJSONArray("columns"), ddmFormLayoutRow);

			ddmFormLayoutPage.addDDMFormLayoutRow(ddmFormLayoutRow);
		}
	}

	private final DDMForm _ddmForm = new DDMForm();
	private long _ddmFormInstanceId;
	private final DDMFormLayout _ddmFormLayout = new DDMFormLayout();
	private final DDMFormValues _ddmFormValues;
	private long _groupId;
	private final JSONObject _jsonObject;
	private final Locale _locale;

}