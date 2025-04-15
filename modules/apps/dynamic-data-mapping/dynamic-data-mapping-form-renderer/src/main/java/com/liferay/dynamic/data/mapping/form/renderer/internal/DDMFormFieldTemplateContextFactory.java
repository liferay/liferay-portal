/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.renderer.internal;

import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorFieldContextKey;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueAccessor;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.constants.DDMFormRendererConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.model.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.language.constants.LanguageConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Marcellus Tavares
 */
public class DDMFormFieldTemplateContextFactory {

	public DDMFormFieldTemplateContextFactory(
		DDMFormEvaluator ddmFormEvaluator, String ddmFormFieldName,
		Map<String, DDMFormField> ddmFormFieldsMap,
		Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
			ddmFormFieldsPropertyChanges,
		List<DDMFormFieldValue> ddmFormFieldValues,
		DDMFormRenderingContext ddmFormRenderingContext,
		DDMStructureLayoutLocalService ddmStructureLayoutLocalService,
		DDMStructureLocalService ddmStructureLocalService,
		GroupLocalService groupLocalService, HtmlParser htmlParser,
		JSONFactory jsonFactory, boolean pageEnabled,
		DDMFormLayout parentDDMFormLayout) {

		_ddmFormEvaluator = ddmFormEvaluator;
		_ddmFormFieldName = ddmFormFieldName;
		_ddmFormFieldsMap = ddmFormFieldsMap;
		_ddmFormFieldsPropertyChanges = ddmFormFieldsPropertyChanges;
		_ddmFormFieldValues = ddmFormFieldValues;
		_ddmFormRenderingContext = ddmFormRenderingContext;
		_ddmStructureLayoutLocalService = ddmStructureLayoutLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
		_groupLocalService = groupLocalService;
		_htmlParser = htmlParser;
		_jsonFactory = jsonFactory;
		_pageEnabled = pageEnabled;
		_parentDDMFormLayout = parentDDMFormLayout;

		_locale = ddmFormRenderingContext.getLocale();
	}

	public List<Object> create() {
		return _createDDMFormFieldTemplateContexts(
			_ddmFormFieldValues, StringPool.BLANK);
	}

	protected void setDDMFormFieldTypeServicesRegistry(
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry) {

		_ddmFormFieldTypeServicesRegistry = ddmFormFieldTypeServicesRegistry;
	}

	private boolean _addProperty(
		Map<String, Object> changedProperties, String propertyName) {

		if (_ddmFormRenderingContext.isReturnFullContext() ||
			changedProperties.containsKey(propertyName)) {

			return true;
		}

		return false;
	}

	private DDMFormFieldRenderingContext _createDDDMFormFieldRenderingContext(
		Map<String, Object> changedProperties,
		Map<String, Object> ddmFormFieldTemplateContext) {

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setDDMFormInstanceId(
			_ddmFormRenderingContext.getDDMFormInstanceId());
		ddmFormFieldRenderingContext.setHttpServletRequest(
			_ddmFormRenderingContext.getHttpServletRequest());
		ddmFormFieldRenderingContext.setHttpServletResponse(
			_ddmFormRenderingContext.getHttpServletResponse());
		ddmFormFieldRenderingContext.setLocale(_locale);
		ddmFormFieldRenderingContext.setPortletNamespace(
			_ddmFormRenderingContext.getPortletNamespace());
		ddmFormFieldRenderingContext.setProperties(ddmFormFieldTemplateContext);
		ddmFormFieldRenderingContext.setProperty(
			"changedProperties", changedProperties);

		Long ddmFormInstanceRecordId = _ddmFormRenderingContext.getProperty(
			"ddmFormInstanceRecordId");

		if ((ddmFormInstanceRecordId != null) &&
			(ddmFormInstanceRecordId > 0)) {

			ddmFormFieldRenderingContext.setProperty(
				"ddmFormInstanceRecordId", ddmFormInstanceRecordId);
		}

		long groupId = _ddmFormRenderingContext.getGroupId();

		if (groupId == 0) {
			HttpServletRequest httpServletRequest =
				ddmFormFieldRenderingContext.getHttpServletRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			groupId = themeDisplay.getScopeGroupId();
		}

		ddmFormFieldRenderingContext.setProperty("groupId", groupId);
		ddmFormFieldRenderingContext.setReturnFullContext(
			_ddmFormRenderingContext.isReturnFullContext());
		ddmFormFieldRenderingContext.setViewMode(
			_ddmFormRenderingContext.isViewMode());

		return ddmFormFieldRenderingContext;
	}

	private Map<String, Object> _createDDMFormFieldTemplateContext(
		DDMFormField ddmFormField, String ddmFormFieldParameterName) {

		Map<String, Object> ddmFormFieldTemplateContext =
			HashMapBuilder.<String, Object>put(
				"type", ddmFormField.getType()
			).build();

		_setDDMFormFieldTemplateContextFieldName(
			ddmFormFieldTemplateContext, ddmFormField.getName());
		_setDDMFormFieldTemplateContextFieldReference(
			ddmFormFieldTemplateContext, ddmFormField.getFieldReference());
		_setDDMFormFieldTemplateContextLocalizedValue(
			ddmFormFieldTemplateContext, "label", ddmFormField.getLabel());
		_setDDMFormFieldTemplateContextName(
			ddmFormFieldTemplateContext, ddmFormFieldParameterName);

		return ddmFormFieldTemplateContext;
	}

	private Map<String, Object> _createDDMFormFieldTemplateContext(
		DDMFormFieldValue ddmFormFieldValue,
		Map<String, Object> changedProperties, int index,
		String parentDDMFormFieldParameterName) {

		DDMFormField ddmFormField = _ddmFormFieldsMap.get(
			ddmFormFieldValue.getName());

		String ddmFormFieldParameterName = _getDDMFormFieldParameterName(
			ddmFormFieldValue.getName(), ddmFormFieldValue.getInstanceId(),
			index, parentDDMFormFieldParameterName);

		Map<String, Object> ddmFormFieldTemplateContext =
			_createDDMFormFieldTemplateContext(
				ddmFormField, ddmFormFieldParameterName);

		if (_ddmFormRenderingContext.isReturnFullContext()) {
			_setProperties(
				ddmFormFieldTemplateContext, ddmFormField, ddmFormFieldValue);
		}

		_setPropertiesChangeableByRule(
			ddmFormFieldTemplateContext, changedProperties, ddmFormField,
			ddmFormFieldValue);

		_setDDMFormFieldTemplateContextNestedTemplateContexts(
			ddmFormFieldTemplateContext,
			_createNestedDDMFormFieldTemplateContext(
				ddmFormFieldValue, ddmFormFieldParameterName));

		// Contributed template parameters

		_setDDMFormFieldTemplateContextContributedParameters(
			changedProperties, ddmFormFieldTemplateContext, ddmFormField);

		return ddmFormFieldTemplateContext;
	}

	private List<Object> _createDDMFormFieldTemplateContexts(
		List<DDMFormFieldValue> ddmFormFieldValues,
		String parentDDMFormFieldParameterName) {

		List<Object> ddmFormFieldTemplateContexts = new ArrayList<>();

		int index = 0;

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			DDMFormField ddmFormField = _ddmFormFieldsMap.get(
				ddmFormFieldValue.getName());

			if (ddmFormField == null) {
				continue;
			}

			Map<String, Object> changedProperties = _getChangedProperties(
				ddmFormField, ddmFormFieldValue);

			if (!_ddmFormRenderingContext.isReturnFullContext() &&
				changedProperties.isEmpty() && !ddmFormField.isRequired()) {

				continue;
			}

			ddmFormFieldTemplateContexts.add(
				_createDDMFormFieldTemplateContext(
					ddmFormFieldValue, changedProperties, index++,
					parentDDMFormFieldParameterName));
		}

		return ddmFormFieldTemplateContexts;
	}

	private List<Object> _createNestedDDMFormFieldTemplateContext(
		DDMFormFieldValue parentDDMFormFieldValue,
		String parentDDMFormFieldParameterName) {

		List<Object> nestedDDMFormFieldTemplateContexts = new ArrayList<>();

		Map<String, List<DDMFormFieldValue>> nestedDDMFormFieldValuesMap =
			parentDDMFormFieldValue.getNestedDDMFormFieldValuesMap();

		Set<String> ddmFormFieldValueNames = new HashSet<>();

		for (DDMFormFieldValue ddmFormFieldValue :
				parentDDMFormFieldValue.getNestedDDMFormFieldValues()) {

			ddmFormFieldValueNames.add(ddmFormFieldValue.getName());
		}

		for (String name : ddmFormFieldValueNames) {
			nestedDDMFormFieldTemplateContexts.addAll(
				_createDDMFormFieldTemplateContexts(
					nestedDDMFormFieldValuesMap.get(name),
					parentDDMFormFieldParameterName));
		}

		return nestedDDMFormFieldTemplateContexts;
	}

	private List<Map<String, String>> _createOptions(
		DDMFormFieldOptions ddmFormFieldOptions) {

		List<Map<String, String>> list = new ArrayList<>();

		if (ddmFormFieldOptions == null) {
			return list;
		}

		Map<String, LocalizedValue> options = ddmFormFieldOptions.getOptions();

		Map<String, String> optionsReferences =
			ddmFormFieldOptions.getOptionsReferences();

		for (Map.Entry<String, LocalizedValue> entry : options.entrySet()) {
			Map<String, String> option = new HashMap<>();

			LocalizedValue localizedValue = entry.getValue();

			String value = localizedValue.getString(_locale);

			if (value == null) {
				value = localizedValue.getString(LocaleUtil.getDefault());
			}

			option.put("label", value);

			option.put("reference", optionsReferences.get(entry.getKey()));
			option.put("value", entry.getKey());

			list.add(option);
		}

		return list;
	}

	private List<Map<String, String>> _createOptions(
		List<KeyValuePair> keyValuePairs) {

		return TransformUtil.transform(
			keyValuePairs,
			keyValuePair -> HashMapBuilder.put(
				"label", keyValuePair.getValue()
			).put(
				"value", keyValuePair.getKey()
			).build());
	}

	private String _getAffixedDDMFormFieldParameterName(
		String ddmFormFieldParameterName) {

		return StringBundler.concat(
			_ddmFormRenderingContext.getPortletNamespace(),
			DDMFormRendererConstants.DDM_FORM_FIELD_NAME_PREFIX,
			ddmFormFieldParameterName,
			DDMFormRendererConstants.DDM_FORM_FIELD_LANGUAGE_ID_SEPARATOR,
			LocaleUtil.toLanguageId(_locale));
	}

	private Map<String, Object> _getChangedProperties(
		DDMFormField ddmFormField, DDMFormFieldValue ddmFormFieldValue) {

		Map<String, Object> changedProperties =
			_ddmFormFieldsPropertyChanges.get(
				new DDMFormEvaluatorFieldContextKey(
					ddmFormFieldValue.getName(),
					ddmFormFieldValue.getInstanceId()));

		if (changedProperties == null) {
			changedProperties = new HashMap<>();
		}

		if (Objects.equals(
				DDMFormFieldTypeConstants.FIELDSET, ddmFormField.getType())) {

			changedProperties.put("editOnlyInDefaultLanguage", false);
		}

		changedProperties.put("enabled", _pageEnabled);

		if (_ddmFormRenderingContext.isReadOnly()) {
			changedProperties.put("readOnly", true);
		}

		return changedProperties;
	}

	private String _getDDMFormFieldParameterName(
		String ddmFormFieldName, String instanceId, int index,
		String parentDDMFormFieldParameterName) {

		StringBundler sb = new StringBundler(7);

		if (Validator.isNotNull(parentDDMFormFieldParameterName)) {
			sb.append(parentDDMFormFieldParameterName);
			sb.append(DDMFormRendererConstants.DDM_FORM_FIELDS_SEPARATOR);
		}

		sb.append(ddmFormFieldName);
		sb.append(DDMFormRendererConstants.DDM_FORM_FIELD_PARTS_SEPARATOR);
		sb.append(instanceId);
		sb.append(DDMFormRendererConstants.DDM_FORM_FIELD_PARTS_SEPARATOR);
		sb.append(index);

		return sb.toString();
	}

	private DDMFormLayout _getDDMFormLayout(long ddmStructureLayoutId) {
		try {
			return _ddmStructureLayoutLocalService.
				getStructureLayoutDDMFormLayout(
					_ddmStructureLayoutLocalService.getStructureLayout(
						ddmStructureLayoutId));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return new DDMFormLayout();
	}

	private long _getDefaultDDMFormLayoutId(long ddmStructureId) {
		DDMStructure ddmStructure = _ddmStructureLocalService.fetchDDMStructure(
			ddmStructureId);

		return ddmStructure.getDefaultDDMStructureLayoutId();
	}

	private List<Map<String, Object>> _getNestedFieldsContext(
		List<Object> pages) {

		if (ListUtil.isEmpty(pages)) {
			return new ArrayList<>();
		}

		for (Object page : pages) {
			Map<String, Object> pageContext = (Map<String, Object>)page;

			List<Map<String, Object>> rows =
				(List<Map<String, Object>>)pageContext.get("rows");

			if (rows == null) {
				return null;
			}

			for (Map<String, Object> row : rows) {
				List<Map<String, Object>> columns =
					(List<Map<String, Object>>)row.get("columns");

				if (columns == null) {
					return null;
				}

				for (Map<String, Object> column : columns) {
					return (List<Map<String, Object>>)column.get("fields");
				}
			}
		}

		return null;
	}

	private boolean _isFieldSetField(DDMFormField ddmFormField) {
		return StringUtil.equals(ddmFormField.getType(), "fieldset");
	}

	private void _setDDMFormFieldFieldSetTemplateContextContributedParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		Map<String, Object> properties =
			ddmFormFieldRenderingContext.getProperties();

		if (Validator.isNotNull(ddmFormField.getProperty("ddmStructureId")) &&
			!properties.containsKey("nestedFields")) {

			long ddmStructureLayoutId = GetterUtil.getLong(
				ddmFormField.getProperty("ddmStructureLayoutId"));

			if (ddmStructureLayoutId == 0) {
				ddmStructureLayoutId = _getDefaultDDMFormLayoutId(
					GetterUtil.getLong(
						ddmFormField.getProperty("ddmStructureId")));
			}

			DDMFormLayout ddmFormLayout = _getDDMFormLayout(
				ddmStructureLayoutId);

			ddmFormLayout.setDDMFormRules(
				_parentDDMFormLayout.getDDMFormRules());

			String rows = MapUtil.getString(
				ddmFormField.getProperties(), "rows");

			if (Validator.isNotNull(rows)) {
				_updateDDMFormLayoutRows(ddmFormLayout, rows);
			}

			DDMFormPagesTemplateContextFactory
				ddmFormPagesTemplateContextFactory =
					new DDMFormPagesTemplateContextFactory(
						ddmFormField.getDDMForm(), ddmFormLayout,
						_ddmFormRenderingContext,
						_ddmStructureLayoutLocalService,
						_ddmStructureLocalService, _groupLocalService,
						_htmlParser, _jsonFactory);

			ddmFormPagesTemplateContextFactory.setDDMFormEvaluator(
				_ddmFormEvaluator);
			ddmFormPagesTemplateContextFactory.
				setDDMFormFieldTypeServicesRegistry(
					_ddmFormFieldTypeServicesRegistry);

			ddmFormFieldRenderingContext.setProperty(
				"nestedFields",
				_getNestedFieldsContext(
					ddmFormPagesTemplateContextFactory.create()));
		}
	}

	private void _setDDMFormFieldTemplateContextContributedParameters(
		Map<String, Object> changedProperties,
		Map<String, Object> ddmFormFieldTemplateContext,
		DDMFormField ddmFormField) {

		DDMFormFieldTemplateContextContributor
			ddmFormFieldTemplateContextContributor =
				_ddmFormFieldTypeServicesRegistry.
					getDDMFormFieldTemplateContextContributor(
						ddmFormField.getType());

		if (ddmFormFieldTemplateContextContributor == null) {
			return;
		}

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			_createDDDMFormFieldRenderingContext(
				changedProperties, ddmFormFieldTemplateContext);

		if (_isFieldSetField(ddmFormField)) {
			_setDDMFormFieldFieldSetTemplateContextContributedParameters(
				ddmFormField, ddmFormFieldRenderingContext);
		}
		else if (StringUtil.equals(
					ddmFormField.getType(), "object-relationship")) {

			ddmFormFieldRenderingContext.setProperty(
				"objectEntryId",
				_ddmFormRenderingContext.getProperty("objectEntryId"));
		}

		Map<String, Object> contributedParameters =
			ddmFormFieldTemplateContextContributor.getParameters(
				ddmFormField, ddmFormFieldRenderingContext);

		if ((contributedParameters == null) ||
			contributedParameters.isEmpty()) {

			return;
		}

		ddmFormFieldTemplateContext.putAll(contributedParameters);
	}

	private void _setDDMFormFieldTemplateContextDataType(
		Map<String, Object> ddmFormFieldTemplateContext, String dataType) {

		ddmFormFieldTemplateContext.put("dataType", dataType);
	}

	private void _setDDMFormFieldTemplateContextDir(
		Map<String, Object> ddmFormFieldTemplateContext) {

		ddmFormFieldTemplateContext.put(
			"dir", LanguageUtil.get(_locale, LanguageConstants.KEY_DIR));
	}

	private void _setDDMFormFieldTemplateContextEditOnlyInDefaultLanguage(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties,
		boolean editOnlyInDefaultLanguage) {

		ddmFormFieldTemplateContext.put(
			"editOnlyInDefaultLanguage",
			MapUtil.getBoolean(
				changedProperties, "editOnlyInDefaultLanguage",
				editOnlyInDefaultLanguage));
	}

	private void _setDDMFormFieldTemplateContextEnabled(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties, boolean defaultValue) {

		if (!_addProperty(changedProperties, "enabled")) {
			return;
		}

		ddmFormFieldTemplateContext.put(
			"enabled",
			MapUtil.getBoolean(changedProperties, "enabled", defaultValue));
	}

	private void _setDDMFormFieldTemplateContextEvaluable(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties, Object evaluable) {

		if (changedProperties.containsKey("required")) {
			ddmFormFieldTemplateContext.put("evaluable", true);

			return;
		}

		if (evaluable == null) {
			return;
		}

		ddmFormFieldTemplateContext.put("evaluable", evaluable);
	}

	private void _setDDMFormFieldTemplateContextFieldName(
		Map<String, Object> ddmFormFieldTemplateContext, String fieldName) {

		ddmFormFieldTemplateContext.put("fieldName", fieldName);
	}

	private void _setDDMFormFieldTemplateContextFieldReference(
		Map<String, Object> ddmFormFieldTemplateContext,
		String fieldReference) {

		ddmFormFieldTemplateContext.put("fieldReference", fieldReference);
	}

	private void _setDDMFormFieldTemplateContextInputMaskProperties(
		Map<String, Object> changedProperties,
		Map<String, Object> ddmFormFieldTemplateContext) {

		if (!_addProperty(changedProperties, "inputMask")) {
			return;
		}

		ddmFormFieldTemplateContext.put(
			"inputMask",
			MapUtil.getBoolean(changedProperties, "inputMask", false));
		ddmFormFieldTemplateContext.put(
			"inputMaskFormat", changedProperties.get("inputMaskFormat"));
		ddmFormFieldTemplateContext.put(
			"numericInputMask", changedProperties.get("numericInputMask"));
	}

	private void _setDDMFormFieldTemplateContextInstanceId(
		Map<String, Object> ddmFormFieldTemplateContext, String instanceId) {

		ddmFormFieldTemplateContext.put("instanceId", instanceId);
	}

	private void _setDDMFormFieldTemplateContextLocale(
		Map<String, Object> ddmFormFieldTemplateContext) {

		ddmFormFieldTemplateContext.put(
			"locale", LocaleUtil.toLanguageId(_locale));
	}

	private void _setDDMFormFieldTemplateContextLocalizable(
		Map<String, Object> ddmFormFieldTemplateContext, boolean localizable) {

		ddmFormFieldTemplateContext.put("localizable", localizable);
	}

	private void _setDDMFormFieldTemplateContextLocalizedValue(
		Map<String, Object> ddmFormFieldTemplateContext, String propertyName,
		LocalizedValue localizedValue) {

		if (localizedValue == null) {
			return;
		}

		String propertyValue = GetterUtil.getString(
			localizedValue.getString(_locale));

		ddmFormFieldTemplateContext.put(propertyName, propertyValue);
	}

	private void _setDDMFormFieldTemplateContextName(
		Map<String, Object> ddmFormFieldTemplateContext,
		String ddmFormFieldParameterName) {

		String name = _getAffixedDDMFormFieldParameterName(
			ddmFormFieldParameterName);

		ddmFormFieldTemplateContext.put("name", name);
	}

	private void _setDDMFormFieldTemplateContextNestedTemplateContexts(
		Map<String, Object> ddmFormFieldRenderingContext,
		List<Object> nestedDDMFormFieldTemplateContexts) {

		if (nestedDDMFormFieldTemplateContexts.isEmpty()) {
			return;
		}

		ddmFormFieldRenderingContext.put(
			"nestedFields", nestedDDMFormFieldTemplateContexts);
	}

	private void _setDDMFormFieldTemplateContextOptions(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties,
		DDMFormFieldOptions ddmFormFieldOptions) {

		List<KeyValuePair> keyValuePairs =
			(List<KeyValuePair>)changedProperties.get("options");

		if (keyValuePairs != null) {
			ddmFormFieldTemplateContext.put(
				"options", _createOptions(keyValuePairs));
		}
		else if (_addProperty(changedProperties, "options")) {
			ddmFormFieldTemplateContext.put(
				"options", _createOptions(ddmFormFieldOptions));
		}
	}

	private void _setDDMFormFieldTemplateContextReadOnly(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties, boolean defaultValue) {

		if (!_addProperty(changedProperties, "readOnly")) {
			return;
		}

		boolean readOnly = MapUtil.getBoolean(
			changedProperties, "readOnly", defaultValue);

		ddmFormFieldTemplateContext.put("readOnly", readOnly);
	}

	private void _setDDMFormFieldTemplateContextRepeatable(
		Map<String, Object> ddmFormFieldTemplateContext, boolean repeatable) {

		ddmFormFieldTemplateContext.put("repeatable", repeatable);
	}

	private void _setDDMFormFieldTemplateContextRequired(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties, boolean defaultValue) {

		if (!_addProperty(changedProperties, "required")) {
			return;
		}

		ddmFormFieldTemplateContext.put(
			"required",
			MapUtil.getBoolean(changedProperties, "required", defaultValue));
	}

	private void _setDDMFormFieldTemplateContextShowLabel(
		Map<String, Object> ddmFormFieldTemplateContext, boolean showLabel) {

		ddmFormFieldTemplateContext.put("showLabel", showLabel);
	}

	private void _setDDMFormFieldTemplateContextTransient(
		Map<String, Object> ddmFormFieldTemplateContext, boolean isTransient) {

		ddmFormFieldTemplateContext.put("transient", isTransient);
	}

	private void _setDDMFormFieldTemplateContextType(
		Map<String, Object> ddmFormFieldTemplateContext, String type) {

		ddmFormFieldTemplateContext.put("type", type);
	}

	private void _setDDMFormFieldTemplateContextValid(
		Map<String, Object> changedProperties,
		Map<String, Object> ddmFormFieldTemplateContext, boolean defaultValue) {

		if (_addProperty(changedProperties, "errorMessage")) {
			ddmFormFieldTemplateContext.put(
				"errorMessage",
				MapUtil.getString(changedProperties, "errorMessage"));
		}

		if (_addProperty(changedProperties, "valid")) {
			ddmFormFieldTemplateContext.put(
				"valid",
				MapUtil.getBoolean(changedProperties, "valid", defaultValue));
		}
	}

	private void _setDDMFormFieldTemplateContextValidation(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties,
		DDMFormFieldValidation ddmFormFieldValidation) {

		if (ddmFormFieldValidation == null) {
			return;
		}

		LocalizedValue errorMessageLocalizedValue =
			ddmFormFieldValidation.getErrorMessageLocalizedValue();

		String errorMessage = StringPool.BLANK;

		if (errorMessageLocalizedValue != null) {
			errorMessage = GetterUtil.getString(
				errorMessageLocalizedValue.getString(_locale));
		}

		ddmFormFieldTemplateContext.put(
			"validation",
			HashMapBuilder.<String, Object>put(
				"dataType",
				GetterUtil.getString(
					changedProperties.get("validationDataType"),
					MapUtil.getString(changedProperties, "dataType"))
			).put(
				"errorMessage", errorMessage
			).put(
				"expression",
				() -> {
					DDMFormFieldValidationExpression
						ddmFormFieldValidationExpression =
							ddmFormFieldValidation.
								getDDMFormFieldValidationExpression();

					return HashMapBuilder.put(
						"name",
						GetterUtil.getString(
							ddmFormFieldValidationExpression.getName())
					).put(
						"value",
						GetterUtil.getString(
							ddmFormFieldValidationExpression.getValue())
					).build();
				}
			).put(
				"fieldName",
				GetterUtil.getString(
					changedProperties.get("validationFieldName"))
			).put(
				"parameter",
				() -> {
					LocalizedValue parameterLocalizedValue =
						ddmFormFieldValidation.getParameterLocalizedValue();

					if (parameterLocalizedValue != null) {
						return GetterUtil.getString(
							parameterLocalizedValue.getString(_locale));
					}

					return StringPool.BLANK;
				}
			).build());
	}

	private void _setDDMFormFieldTemplateContextValue(
		Map<String, Object> changedProperties,
		Map<String, Object> ddmFormFieldTemplateContext, Value value) {

		if (changedProperties.get("value") != null) {
			ddmFormFieldTemplateContext.put(
				"value", changedProperties.get("value"));

			ddmFormFieldTemplateContext.put("valueChanged", true);
		}
		else if (value != null) {
			ddmFormFieldTemplateContext.put("value", value.getString(_locale));
		}
	}

	private void _setDDMFormFieldTemplateContextValueLocalizableValue(
		Map<String, Object> ddmFormFieldTemplateContext,
		DDMFormFieldValue ddmFormFieldValue) {

		if (ddmFormFieldValue == null) {
			return;
		}

		Value value = ddmFormFieldValue.getValue();

		if (!(value instanceof LocalizedValue)) {
			return;
		}

		DDMFormField ddmFormField = _ddmFormFieldsMap.get(
			ddmFormFieldValue.getName());

		DDMFormFieldValueAccessor<?> ddmFormFieldValueAccessor =
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldValueAccessor(
				ddmFormField.getType());

		Map<String, Object> localizedValues = new HashMap<>();

		for (Locale availableLocale : value.getAvailableLocales()) {
			String languageId = LanguageUtil.getLanguageId(availableLocale);

			Object localizedValue = value.getString(availableLocale);

			if (ddmFormFieldValueAccessor != null) {
				Object ddmFormFieldValueAccessorValue =
					ddmFormFieldValueAccessor.getValue(
						ddmFormFieldValue, availableLocale);

				if (!(ddmFormFieldValueAccessorValue instanceof BigDecimal)) {
					localizedValue = ddmFormFieldValueAccessor.getValue(
						ddmFormFieldValue, availableLocale);
				}
			}

			if (localizedValue instanceof JSONObject) {
				localizedValue = localizedValue.toString();
			}

			localizedValues.put(
				languageId,
				GetterUtil.getObject(localizedValue, StringPool.BLANK));
		}

		ddmFormFieldTemplateContext.put("localizedValue", localizedValues);
	}

	private void _setDDMFormFieldTemplateContextValueLocalizableValueEdited(
		Map<String, Object> ddmFormFieldTemplateContext,
		DDMFormFieldValue ddmFormFieldValue) {

		boolean persisted = GetterUtil.getBoolean(
			(Object)_ddmFormRenderingContext.getProperty("persisted"));

		if (!persisted || (ddmFormFieldValue == null)) {
			return;
		}

		Value value = ddmFormFieldValue.getValue();

		if (!(value instanceof LocalizedValue)) {
			return;
		}

		Set<Locale> availableLocales = value.getAvailableLocales();

		Map<String, Object> localizedValueEdited = new HashMap<>();

		availableLocales.forEach(
			availableLocale -> localizedValueEdited.put(
				LanguageUtil.getLanguageId(availableLocale), true));

		ddmFormFieldTemplateContext.put(
			"localizedValueEdited", localizedValueEdited);
	}

	private void _setDDMFormFieldTemplateContextVisibilityExpression(
		Map<String, Object> ddmFormFieldTemplateContext,
		String visibilityExpression) {

		ddmFormFieldTemplateContext.put(
			"visibilityExpression", visibilityExpression);
	}

	private void _setDDMFormFieldTemplateContextVisible(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties, boolean defaultValue) {

		if (!_addProperty(changedProperties, "visible")) {
			return;
		}

		ddmFormFieldTemplateContext.put(
			"visible",
			MapUtil.getBoolean(changedProperties, "visible", defaultValue));
	}

	private void _setDDMFormFieldTemplateContextVisualProperty(
		Map<String, Object> ddmFormFieldTemplateContext,
		boolean visualProperty) {

		ddmFormFieldTemplateContext.put("visualProperty", visualProperty);
	}

	private void _setProperties(
		Map<String, Object> ddmFormFieldTemplateContext,
		DDMFormField ddmFormField, DDMFormFieldValue ddmFormFieldValue) {

		_setDDMFormFieldTemplateContextDataType(
			ddmFormFieldTemplateContext, ddmFormField.getDataType());
		_setDDMFormFieldTemplateContextDir(ddmFormFieldTemplateContext);
		_setDDMFormFieldTemplateContextInstanceId(
			ddmFormFieldTemplateContext, ddmFormFieldValue.getInstanceId());
		_setDDMFormFieldTemplateContextLocale(ddmFormFieldTemplateContext);
		_setDDMFormFieldTemplateContextLocalizable(
			ddmFormFieldTemplateContext, ddmFormField.isLocalizable());
		_setDDMFormFieldTemplateContextLocalizedValue(
			ddmFormFieldTemplateContext, "tip", ddmFormField.getTip());
		_setDDMFormFieldTemplateContextRepeatable(
			ddmFormFieldTemplateContext, ddmFormField.isRepeatable());
		_setDDMFormFieldTemplateContextShowLabel(
			ddmFormFieldTemplateContext, ddmFormField.isShowLabel());
		_setDDMFormFieldTemplateContextTransient(
			ddmFormFieldTemplateContext, ddmFormField.isTransient());
		_setDDMFormFieldTemplateContextType(
			ddmFormFieldTemplateContext, ddmFormField.getType());
		_setDDMFormFieldTemplateContextVisibilityExpression(
			ddmFormFieldTemplateContext,
			ddmFormField.getVisibilityExpression());
		_setDDMFormFieldTemplateContextVisualProperty(
			ddmFormFieldTemplateContext,
			GetterUtil.getBoolean(ddmFormField.getProperty("visualProperty")));
	}

	private void _setPropertiesChangeableByRule(
		Map<String, Object> ddmFormFieldTemplateContext,
		Map<String, Object> changedProperties, DDMFormField ddmFormField,
		DDMFormFieldValue ddmFormFieldValue) {

		_setDDMFormFieldTemplateContextEditOnlyInDefaultLanguage(
			ddmFormFieldTemplateContext, changedProperties,
			_ddmFormRenderingContext.isEditOnlyInDefaultLanguage());
		_setDDMFormFieldTemplateContextEnabled(
			ddmFormFieldTemplateContext, changedProperties, true);
		_setDDMFormFieldTemplateContextEvaluable(
			ddmFormFieldTemplateContext, changedProperties,
			ddmFormField.getProperty("evaluable"));
		_setDDMFormFieldTemplateContextInputMaskProperties(
			changedProperties, ddmFormFieldTemplateContext);
		_setDDMFormFieldTemplateContextLocalizedValue(
			ddmFormFieldTemplateContext, "requiredErrorMessage",
			ddmFormField.getRequiredErrorMessage());
		_setDDMFormFieldTemplateContextOptions(
			ddmFormFieldTemplateContext, changedProperties,
			ddmFormField.getDDMFormFieldOptions());
		_setDDMFormFieldTemplateContextReadOnly(
			ddmFormFieldTemplateContext, changedProperties,
			ddmFormField.isReadOnly());
		_setDDMFormFieldTemplateContextRequired(
			ddmFormFieldTemplateContext, changedProperties,
			ddmFormField.isRequired());
		_setDDMFormFieldTemplateContextValid(
			changedProperties, ddmFormFieldTemplateContext, true);
		_setDDMFormFieldTemplateContextValue(
			changedProperties, ddmFormFieldTemplateContext,
			ddmFormFieldValue.getValue());
		_setDDMFormFieldTemplateContextValueLocalizableValue(
			ddmFormFieldTemplateContext, ddmFormFieldValue);
		_setDDMFormFieldTemplateContextValueLocalizableValueEdited(
			ddmFormFieldTemplateContext, ddmFormFieldValue);
		_setDDMFormFieldTemplateContextValidation(
			ddmFormFieldTemplateContext, changedProperties,
			ddmFormField.getDDMFormFieldValidation());
		_setDDMFormFieldTemplateContextVisible(
			ddmFormFieldTemplateContext, changedProperties, true);
	}

	private DDMFormLayoutColumn _toDDMFormLayoutColumn(JSONObject jsonObject) {
		DDMFormLayoutColumn ddmFormLayoutColumn = new DDMFormLayoutColumn();

		ddmFormLayoutColumn.setDDMFormFieldNames(
			JSONUtil.toStringList(jsonObject.getJSONArray("fields")));
		ddmFormLayoutColumn.setSize(jsonObject.getInt("size"));

		return ddmFormLayoutColumn;
	}

	private List<DDMFormLayoutColumn> _toDDMFormLayoutColumns(
		JSONArray jsonArray) {

		List<DDMFormLayoutColumn> ddmFormLayoutColumns = new ArrayList<>(
			jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormLayoutColumns.add(
				_toDDMFormLayoutColumn(jsonArray.getJSONObject(i)));
		}

		return ddmFormLayoutColumns;
	}

	private DDMFormLayoutRow _toDDMFormLayoutRow(JSONObject jsonObject) {
		DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

		ddmFormLayoutRow.setDDMFormLayoutColumns(
			_toDDMFormLayoutColumns(jsonObject.getJSONArray("columns")));

		return ddmFormLayoutRow;
	}

	private List<DDMFormLayoutRow> _toDDMFormLayoutRows(JSONArray jsonArray) {
		List<DDMFormLayoutRow> ddmFormLayoutRows = new ArrayList<>(
			jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			ddmFormLayoutRows.add(
				_toDDMFormLayoutRow(jsonArray.getJSONObject(i)));
		}

		return ddmFormLayoutRows;
	}

	private void _updateDDMFormLayoutRows(
		DDMFormLayout ddmFormLayout, String rowsJSON) {

		try {
			DDMFormLayoutPage ddmFormLayoutPage =
				ddmFormLayout.getDDMFormLayoutPage(0);

			ddmFormLayoutPage.setDDMFormLayoutRows(
				_toDDMFormLayoutRows(_jsonFactory.createJSONArray(rowsJSON)));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFieldTemplateContextFactory.class);

	private final DDMFormEvaluator _ddmFormEvaluator;
	private final String _ddmFormFieldName;
	private final Map<String, DDMFormField> _ddmFormFieldsMap;
	private final Map<DDMFormEvaluatorFieldContextKey, Map<String, Object>>
		_ddmFormFieldsPropertyChanges;
	private DDMFormFieldTypeServicesRegistry _ddmFormFieldTypeServicesRegistry;
	private final List<DDMFormFieldValue> _ddmFormFieldValues;
	private final DDMFormRenderingContext _ddmFormRenderingContext;
	private final DDMStructureLayoutLocalService
		_ddmStructureLayoutLocalService;
	private final DDMStructureLocalService _ddmStructureLocalService;
	private final GroupLocalService _groupLocalService;
	private final HtmlParser _htmlParser;
	private final JSONFactory _jsonFactory;
	private final Locale _locale;
	private final boolean _pageEnabled;
	private final DDMFormLayout _parentDDMFormLayout;

}