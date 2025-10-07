/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.internal.dto.v2_0.util;

import com.liferay.data.engine.field.type.util.LocalizedValueUtil;
import com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesRegistry;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidationExpression;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMStructureLayoutLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.util.SettingsDDMFormFieldsUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ServicePreAction;
import com.liferay.portal.events.ThemeServicePreAction;
import com.liferay.portal.kernel.editor.configuration.EditorConfiguration;
import com.liferay.portal.kernel.editor.configuration.EditorConfigurationFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class DataDefinitionFieldUtil {

	public static Object getEditorConfig(
			String ddmFormFieldType, HttpServletRequest httpServletRequest)
		throws Exception {

		if (httpServletRequest == null) {
			return null;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			ServicePreAction servicePreAction = new ServicePreAction();

			HttpServletResponse httpServletResponse =
				new DummyHttpServletResponse();

			servicePreAction.servicePre(
				httpServletRequest, httpServletResponse, false);

			ThemeServicePreAction themeServicePreAction =
				new ThemeServicePreAction();

			themeServicePreAction.run(httpServletRequest, httpServletResponse);

			themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

			if (themeDisplay == null) {
				return null;
			}
		}

		EditorConfiguration editorConfiguration =
			EditorConfigurationFactoryUtil.getEditorConfiguration(
				StringPool.BLANK, ddmFormFieldType,
				FeatureFlagManagerUtil.isEnabled("LPD-11235") ?
					"ckeditor5_classic" : "ckeditor_classic",
				new HashMap<>(), themeDisplay,
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest));

		Map<String, Object> data = editorConfiguration.getData();

		return data.get("editorConfig");
	}

	public static DataDefinitionField toDataDefinitionField(
			DDMFormField ddmFormField,
			DDMFormFieldTypeServicesRegistry ddmFormFieldTypeServicesRegistry,
			DDMStructureLayoutLocalService ddmStructureLayoutLocalService,
			DDMStructureLocalService ddmStructureLocalService,
			HttpServletRequest httpServletRequest)
		throws Exception {

		return new DataDefinitionField() {
			{
				setCustomProperties(
					() -> _getCustomProperties(
						ddmFormField.getProperties(), ddmFormField.getType(),
						ddmFormField.getProperty("ddmStructureLayoutId"),
						ddmStructureLayoutLocalService,
						ddmStructureLocalService, httpServletRequest,
						SettingsDDMFormFieldsUtil.getSettingsDDMFormFields(
							ddmFormFieldTypeServicesRegistry,
							ddmFormField.getType())));
				setDefaultValue(
					() -> LocalizedValueUtil.toLocalizedValuesMap(
						ddmFormField.getPredefinedValue()));
				setFieldType(ddmFormField::getType);
				setIndexable(
					() -> Validator.isNotNull(ddmFormField.getIndexType()));
				setIndexType(
					() -> DataDefinitionField.IndexType.create(
						ddmFormField.getIndexType()));
				setLabel(
					() -> LocalizedValueUtil.toLocalizedValuesMap(
						ddmFormField.getLabel()));
				setLocalizable(ddmFormField::isLocalizable);
				setName(ddmFormField::getName);
				setNestedDataDefinitionFields(
					() -> TransformUtil.transformToArray(
						ddmFormField.getNestedDDMFormFields(),
						ddmFormField -> toDataDefinitionField(
							ddmFormField, ddmFormFieldTypeServicesRegistry,
							ddmStructureLayoutLocalService,
							ddmStructureLocalService, httpServletRequest),
						DataDefinitionField.class));
				setReadOnly(ddmFormField::isReadOnly);
				setRepeatable(ddmFormField::isRepeatable);
				setRequired(ddmFormField::isRequired);
				setShowLabel(ddmFormField::isShowLabel);
				setTip(
					() -> LocalizedValueUtil.toLocalizedValuesMap(
						ddmFormField.getTip()));
			}
		};
	}

	private static Map<String, Object> _getCustomProperties(
			Map<String, Object> ddmFormFieldProperties, String ddmFormFieldType,
			Object ddmStructureLayoutId,
			DDMStructureLayoutLocalService ddmStructureLayoutLocalService,
			DDMStructureLocalService ddmStructureLocalService,
			HttpServletRequest httpServletRequest,
			Map<String, DDMFormField> settingsDDMFormFieldsMap)
		throws Exception {

		Map<String, Object> customProperties = new HashMap<>();

		for (Map.Entry<String, Object> entry :
				ddmFormFieldProperties.entrySet()) {

			if (ArrayUtil.contains(_PREDEFINED_PROPERTIES, entry.getKey())) {
				continue;
			}

			DDMFormField settingsDDMFormField = settingsDDMFormFieldsMap.get(
				entry.getKey());

			if (settingsDDMFormField == null) {
				continue;
			}

			if (settingsDDMFormField.isLocalizable()) {
				customProperties.put(
					entry.getKey(),
					LocalizedValueUtil.toLocalizedValuesMap(
						(LocalizedValue)entry.getValue()));
			}
			else if (Objects.equals(
						settingsDDMFormField.getDataType(), "boolean")) {

				customProperties.put(
					entry.getKey(), GetterUtil.getBoolean(entry.getValue()));
			}
			else if (Objects.equals(
						settingsDDMFormField.getDataType(), "ddm-options")) {

				DDMFormFieldOptions ddmFormFieldOptions =
					(DDMFormFieldOptions)entry.getValue();

				if ((ddmFormFieldOptions == null) ||
					SetUtil.isEmpty(ddmFormFieldOptions.getOptionsValues())) {

					customProperties.put(
						entry.getKey(), Collections.emptyMap());

					continue;
				}

				Map<String, List<JSONObject>> options = new HashMap<>();

				for (String optionValue :
						ddmFormFieldOptions.getOptionsValues()) {

					LocalizedValue localizedValue =
						ddmFormFieldOptions.getOptionLabels(optionValue);

					for (Locale locale : localizedValue.getAvailableLocales()) {
						List<JSONObject> jsonObjects = options.computeIfAbsent(
							LanguageUtil.getLanguageId(locale),
							languageId -> new ArrayList<>());

						jsonObjects.add(
							JSONUtil.put(
								"label", localizedValue.getString(locale)
							).put(
								"reference",
								ddmFormFieldOptions.getOptionReference(
									optionValue)
							).put(
								"value", optionValue
							));
					}
				}

				customProperties.put(entry.getKey(), options);
			}
			else if (Objects.equals(settingsDDMFormField.getType(), "select") ||
					 (Objects.equals(
						 ddmFormFieldType,
						 DDMFormFieldTypeConstants.FIELDSET) &&
					  Objects.equals(settingsDDMFormField.getName(), "rows"))) {

				JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

				try {
					jsonArray = JSONFactoryUtil.createJSONArray(
						String.valueOf(entry.getValue()));
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}

				customProperties.put(entry.getKey(), jsonArray);
			}
			else if (Objects.equals(
						settingsDDMFormField.getType(), "validation")) {

				DDMFormFieldValidation ddmFormFieldValidation =
					(DDMFormFieldValidation)entry.getValue();

				if (ddmFormFieldValidation == null) {
					customProperties.put(
						entry.getKey(), Collections.emptyMap());

					continue;
				}

				customProperties.put(
					entry.getKey(),
					HashMapBuilder.<String, Object>put(
						"errorMessage",
						LocalizedValueUtil.toLocalizedValuesMap(
							ddmFormFieldValidation.
								getErrorMessageLocalizedValue())
					).put(
						"expression",
						() -> {
							DDMFormFieldValidationExpression
								ddmFormFieldValidationExpression =
									ddmFormFieldValidation.
										getDDMFormFieldValidationExpression();

							if (ddmFormFieldValidationExpression == null) {
								return Collections.emptyMap();
							}

							return HashMapBuilder.<String, Object>put(
								"name",
								ddmFormFieldValidationExpression.getName()
							).put(
								"value",
								ddmFormFieldValidationExpression.getValue()
							).build();
						}
					).put(
						"parameter",
						LocalizedValueUtil.toLocalizedValuesMap(
							ddmFormFieldValidation.getParameterLocalizedValue())
					).build());
			}
			else {
				customProperties.put(entry.getKey(), entry.getValue());
			}
		}

		String ddmStructureId = GetterUtil.getString(
			customProperties.get("ddmStructureId"));

		if (Validator.isNotNull(ddmStructureId)) {
			DDMStructure ddmStructure =
				ddmStructureLocalService.fetchDDMStructure(
					GetterUtil.getLong(ddmStructureId));

			if (ddmStructure != null) {
				customProperties.put(
					"ddmStructureKey", ddmStructure.getStructureKey());
				customProperties.put(
					"externalReferenceCode",
					ddmStructure.getExternalReferenceCode());
			}
		}

		if (Validator.isNotNull(ddmStructureLayoutId)) {
			String rows = StringPool.BLANK;

			try {
				DDMStructureLayout ddmStructureLayout =
					ddmStructureLayoutLocalService.getStructureLayout(
						GetterUtil.getLong(ddmStructureLayoutId));

				rows = String.valueOf(
					JSONUtil.getValueAsJSONArray(
						JSONFactoryUtil.createJSONObject(
							StringUtil.replace(
								ddmStructureLayout.getDefinition(),
								"fieldNames", "fields")),
						"JSONArray/pages", "Object/0", "JSONArray/rows"));
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}

			customProperties.put("rows", rows);
		}

		if (StringUtil.equals(
				ddmFormFieldType, DDMFormFieldTypeConstants.RICH_TEXT)) {

			customProperties.put(
				"editorConfig",
				getEditorConfig(ddmFormFieldType, httpServletRequest));
		}

		return customProperties;
	}

	private static final String[] _PREDEFINED_PROPERTIES = {
		"indexType", "label", "localizable", "name", "predefinedValue",
		"readOnly", "repeatable", "required", "showLabel", "tip", "type"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		DataDefinitionFieldUtil.class);

}