/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.context.helper;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldType;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormTemplateContextFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormLayoutFactory;
import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolver;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.servlet.taglib.aui.ESImport;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Marcellus Tavares
 */
public class DDMFormBuilderContextFactoryHelper {

	public DDMFormBuilderContextFactoryHelper(
		DDMStructure ddmStructure, DDMStructureVersion ddmStructureVersion,
		DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
		DDMFormTemplateContextFactory ddmFormTemplateContextFactory,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, JSONFactory jsonFactory,
		Locale locale, NPMResolver npmResolver, String portletNamespace,
		boolean readOnly) {

		_ddmStructure = ddmStructure;
		_ddmStructureVersion = ddmStructureVersion;
		_ddmFormFieldTypeServicesRegistry = ddmFormFieldTypeServicesRegistry;
		_ddmFormTemplateContextFactory = ddmFormTemplateContextFactory;
		_httpServletRequest = httpServletRequest;
		_httpServletResponse = httpServletResponse;
		_jsonFactory = jsonFactory;
		_locale = locale;
		_npmResolver = npmResolver;
		_portletNamespace = portletNamespace;
		_readOnly = readOnly;
	}

	public Map<String, Object> create() {
		if (_ddmStructure != null) {
			Map<String, Object> context = _createFormContext(_ddmStructure);

			if (context != null) {
				return context;
			}
		}

		if (_ddmStructureVersion != null) {
			Map<String, Object> context = _createFormContext(
				_ddmStructureVersion);

			if (context != null) {
				return context;
			}
		}

		return _createEmptyStateContext();
	}

	private Map<String, Object> _createDDMFormFieldSettingContext(
			DDMFormField ddmFormField)
		throws PortalException {

		DDMFormFieldType ddmFormFieldType =
			_ddmFormFieldTypeServicesRegistry.getDDMFormFieldType(
				ddmFormField.getType());

		DDMForm ddmForm = DDMFormFactory.create(
			ddmFormFieldType.getDDMFormFieldTypeSettings());

		DDMFormLayout ddmFormLayout = DDMFormLayoutFactory.create(
			ddmFormFieldType.getDDMFormFieldTypeSettings());

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setContainerId("settings");
		ddmFormRenderingContext.setDDMFormValues(
			_createDDMFormFieldSettingContextDDMFormValues(
				ddmForm, ddmFormField));

		if (_ddmStructureVersion != null) {
			ddmFormRenderingContext.setGroupId(
				_ddmStructureVersion.getGroupId());
		}

		ddmFormRenderingContext.setHttpServletRequest(_httpServletRequest);
		ddmFormRenderingContext.setHttpServletResponse(_httpServletResponse);
		ddmFormRenderingContext.setLocale(_locale);
		ddmFormRenderingContext.setPortletNamespace(_portletNamespace);

		return _ddmFormTemplateContextFactory.create(
			ddmForm, ddmFormLayout, ddmFormRenderingContext);
	}

	private DDMFormValues _createDDMFormFieldSettingContextDDMFormValues(
		DDMForm ddmFormFieldTypeSettingsDDMForm, DDMFormField ddmFormField) {

		Map<String, Object> ddmFormFieldProperties =
			ddmFormField.getProperties();

		DDMFormValues ddmFormValues = new DDMFormValues(
			ddmFormFieldTypeSettingsDDMForm);

		for (DDMFormField ddmFormFieldTypeSetting :
				ddmFormFieldTypeSettingsDDMForm.getDDMFormFields()) {

			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setFieldReference(
				ddmFormFieldTypeSetting.getFieldReference());

			String propertyName = ddmFormFieldTypeSetting.getName();

			ddmFormFieldValue.setName(propertyName);

			DDMForm ddmForm = ddmFormField.getDDMForm();

			ddmFormFieldValue.setValue(
				_createDDMFormFieldValue(
					ddmFormFieldTypeSetting,
					ddmFormFieldProperties.get(propertyName),
					ddmForm.getAvailableLocales()));

			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return ddmFormValues;
	}

	private Value _createDDMFormFieldValue(
		DDMFormField ddmFormFieldTypeSetting, Object propertyValue,
		Set<Locale> availableLocales) {

		if (ddmFormFieldTypeSetting.isLocalizable()) {
			return (LocalizedValue)propertyValue;
		}

		if (Objects.equals(
				ddmFormFieldTypeSetting.getDataType(), "ddm-options")) {

			return _createDDMFormFieldValue(
				(DDMFormFieldOptions)propertyValue, availableLocales);
		}

		if (Objects.equals(
				ddmFormFieldTypeSetting.getName(), "requiredDescription") &&
			(propertyValue == null)) {

			return new UnlocalizedValue(Boolean.TRUE.toString());
		}

		if (Objects.equals(ddmFormFieldTypeSetting.getType(), "validation")) {
			return _createDDMFormFieldValue(
				availableLocales, (DDMFormFieldValidation)propertyValue);
		}

		return new UnlocalizedValue(String.valueOf(propertyValue));
	}

	private Value _createDDMFormFieldValue(
		DDMFormFieldOptions ddmFormFieldOptions, Set<Locale> availableLocales) {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (Locale availableLocale : availableLocales) {
			jsonObject.put(
				LocaleUtil.toLanguageId(availableLocale),
				_createOptions(ddmFormFieldOptions, availableLocale));
		}

		return new UnlocalizedValue(jsonObject.toString());
	}

	private Value _createDDMFormFieldValue(
		Set<Locale> availableLocales,
		DDMFormFieldValidation ddmFormFieldValidation) {

		if (ddmFormFieldValidation == null) {
			return null;
		}

		JSONObject errorMessageJSONObject = _jsonFactory.createJSONObject();
		JSONObject parameterJSONObject = _jsonFactory.createJSONObject();

		for (Locale availableLocale : availableLocales) {
			LocalizedValue errorMessageLocalizedValue =
				ddmFormFieldValidation.getErrorMessageLocalizedValue();

			errorMessageJSONObject.put(
				LocaleUtil.toLanguageId(availableLocale),
				errorMessageLocalizedValue.getString(availableLocale));

			LocalizedValue parameterLocalizedValue =
				ddmFormFieldValidation.getParameterLocalizedValue();

			parameterJSONObject.put(
				LocaleUtil.toLanguageId(availableLocale),
				parameterLocalizedValue.getString(availableLocale));
		}

		DDMFormFieldValidationExpression ddmFormFieldValidationExpression =
			ddmFormFieldValidation.getDDMFormFieldValidationExpression();

		JSONObject expressionJSONObject = _jsonFactory.createJSONObject();

		expressionJSONObject.put(
			"name",
			GetterUtil.getString(ddmFormFieldValidationExpression.getName())
		).put(
			"value",
			GetterUtil.getString(ddmFormFieldValidationExpression.getValue())
		);

		return new UnlocalizedValue(
			JSONUtil.put(
				"errorMessage", errorMessageJSONObject
			).put(
				"expression", expressionJSONObject
			).put(
				"parameter", parameterJSONObject
			).toString());
	}

	private Map<String, Object> _createEmptyStateContext() {
		return HashMapBuilder.<String, Object>put(
			"pages", new ArrayList<>()
		).put(
			"rules", new ArrayList<>()
		).put(
			"sidebarPanels", _getSidebarPanels()
		).build();
	}

	private Map<String, Object> _createFormContext(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout)
		throws PortalException {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.setHttpServletRequest(_httpServletRequest);
		ddmFormRenderingContext.setHttpServletResponse(_httpServletResponse);
		ddmFormRenderingContext.setLocale(_locale);
		ddmFormRenderingContext.setPortletNamespace(_portletNamespace);
		ddmFormRenderingContext.setReadOnly(_readOnly);

		Map<String, Object> ddmFormTemplateContext =
			_ddmFormTemplateContextFactory.create(
				ddmForm, ddmFormLayout, ddmFormRenderingContext);

		_populateDDMFormFieldSettingsContext(
			ddmFormTemplateContext, ddmForm.getDDMFormFieldsMap(true));

		return ddmFormTemplateContext;
	}

	private Map<String, Object> _createFormContext(DDMStructure ddmStructure) {
		try {
			return _doCreateFormContext(ddmStructure);
		}
		catch (PortalException portalException) {
			_log.error("Unable to create form context", portalException);
		}

		return _createEmptyStateContext();
	}

	private Map<String, Object> _createFormContext(
		DDMStructureVersion ddmStructureVersion) {

		try {
			return _doCreateFormContext(ddmStructureVersion);
		}
		catch (PortalException portalException) {
			_log.error("Unable to create form context", portalException);
		}

		return _createEmptyStateContext();
	}

	private JSONArray _createOptions(
		DDMFormFieldOptions ddmFormFieldOptions, Locale locale) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			LocalizedValue label = ddmFormFieldOptions.getOptionLabels(
				optionValue);

			jsonObject.put(
				"label", label.getString(locale)
			).put(
				"reference", ddmFormFieldOptions.getOptionReference(optionValue)
			).put(
				"value", optionValue
			);

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private Map<String, Object> _doCreateFormContext(
			DDMForm ddmForm, DDMFormLayout ddmFormLayout)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"pages",
			() -> {
				Map<String, Object> formContext = _createFormContext(
					ddmForm, ddmFormLayout);

				return formContext.get("pages");
			}
		).put(
			"paginationMode", ddmFormLayout.getPaginationMode()
		).put(
			"rules", new ArrayList<>()
		).put(
			"sidebarPanels", _getSidebarPanels()
		).put(
			"successPageSettings",
			() -> {
				DDMFormSuccessPageSettings ddmFormSuccessPageSettings =
					ddmForm.getDDMFormSuccessPageSettings();

				return HashMapBuilder.<String, Object>put(
					"body", _toMap(ddmFormSuccessPageSettings.getBody())
				).put(
					"enabled", ddmFormSuccessPageSettings.isEnabled()
				).put(
					"title", _toMap(ddmFormSuccessPageSettings.getTitle())
				).build();
			}
		).build();
	}

	private Map<String, Object> _doCreateFormContext(DDMStructure ddmStructure)
		throws PortalException {

		return _doCreateFormContext(
			ddmStructure.getDDMForm(), ddmStructure.getDDMFormLayout());
	}

	private Map<String, Object> _doCreateFormContext(
			DDMStructureVersion ddmStructureVersion)
		throws PortalException {

		return _doCreateFormContext(
			ddmStructureVersion.getDDMForm(),
			ddmStructureVersion.getDDMFormLayout());
	}

	private Map<String, Object> _getSidebarPanels() {
		return LinkedHashMapBuilder.<String, Object>put(
			"fields",
			HashMapBuilder.<String, Object>put(
				"icon", "forms"
			).put(
				"isLink", false
			).put(
				"label", LanguageUtil.get(_httpServletRequest, "builder")
			).put(
				"pluginEntryPoint",
				() -> {
					ESImport esImport = ESImportUtil.getESImport(
						_absolutePortalURLBuilderFactorySnapshot.get(
						).getAbsolutePortalURLBuilder(
							_httpServletRequest
						),
						"{FieldsSidebar} from data-engine-taglib");

					return StringBundler.concat(
						"{", esImport.getSymbol(), "} from ",
						esImport.getModule());
				}
			).put(
				"sidebarPanelId", "fields"
			).build()
		).build();
	}

	private void _populateDDMFormFieldSettingsContext(
		Map<String, Object> ddmFormTemplateContext,
		Map<String, DDMFormField> ddmFormFieldsMap) {

		DDMFormBuilderContextFieldVisitor ddmFormBuilderContextFieldVisitor =
			new DDMFormBuilderContextFieldVisitor(
				ddmFormTemplateContext,
				new Consumer<Map<String, Object>>() {

					@Override
					public void accept(Map<String, Object> fieldContext) {
						String fieldName = MapUtil.getString(
							fieldContext, "fieldName");

						try {
							fieldContext.put(
								"settingsContext",
								_createDDMFormFieldSettingContext(
									ddmFormFieldsMap.get(fieldName)));
						}
						catch (PortalException portalException) {
							_log.error(
								"Unable to create field settings context",
								portalException);
						}
					}

				});

		ddmFormBuilderContextFieldVisitor.visit();
	}

	private Map<String, Object> _toMap(LocalizedValue localizedValue) {
		Map<String, Object> map = new HashMap<>();

		Map<Locale, String> values = localizedValue.getValues();

		for (Map.Entry<Locale, String> entry : values.entrySet()) {
			map.put(
				LanguageUtil.getLanguageId(entry.getKey()), entry.getValue());
		}

		return map;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormBuilderContextFactoryHelper.class);

	private static final Snapshot<AbsolutePortalURLBuilderFactory>
		_absolutePortalURLBuilderFactorySnapshot = new Snapshot<>(
			DDMFormBuilderContextFactoryHelper.class,
			AbsolutePortalURLBuilderFactory.class);

	private final DDMFormFieldTypeServicesRegistry
		_ddmFormFieldTypeServicesRegistry;
	private final DDMFormTemplateContextFactory _ddmFormTemplateContextFactory;
	private final DDMStructure _ddmStructure;
	private final DDMStructureVersion _ddmStructureVersion;
	private final HttpServletRequest _httpServletRequest;
	private final HttpServletResponse _httpServletResponse;
	private final JSONFactory _jsonFactory;
	private final Locale _locale;
	private final NPMResolver _npmResolver;
	private final String _portletNamespace;
	private final boolean _readOnly;

	private static class DDMFormBuilderContextFieldVisitor {

		public DDMFormBuilderContextFieldVisitor(
			Map<String, Object> ddmFormBuilderContext,
			Consumer<Map<String, Object>> fieldConsumer) {

			_ddmFormBuilderContext = ddmFormBuilderContext;
			_fieldConsumer = fieldConsumer;
		}

		public void visit() {
			traversePages(
				(List<Map<String, Object>>)_ddmFormBuilderContext.get("pages"));
		}

		protected void traverseColumns(List<Map<String, Object>> columns) {
			for (Map<String, Object> column : columns) {
				traverseFields((List<Map<String, Object>>)column.get("fields"));
			}
		}

		protected void traverseFields(List<Map<String, Object>> fields) {
			for (Map<String, Object> field : fields) {
				_fieldConsumer.accept(field);

				if (field.containsKey("nestedFields")) {
					traverseFields(
						(List<Map<String, Object>>)field.get("nestedFields"));
				}
			}
		}

		protected void traversePages(List<Map<String, Object>> pages) {
			for (Map<String, Object> page : pages) {
				traverseRows((List<Map<String, Object>>)page.get("rows"));
			}
		}

		protected void traverseRows(List<Map<String, Object>> rows) {
			for (Map<String, Object> row : rows) {
				traverseColumns((List<Map<String, Object>>)row.get("columns"));
			}
		}

		private final Map<String, Object> _ddmFormBuilderContext;
		private final Consumer<Map<String, Object>> _fieldConsumer;

	}

}