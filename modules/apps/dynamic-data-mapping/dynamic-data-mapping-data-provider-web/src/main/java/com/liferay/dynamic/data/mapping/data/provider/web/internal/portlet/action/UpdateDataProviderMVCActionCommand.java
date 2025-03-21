/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.data.provider.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.web.internal.util.DDMDataProviderPortletUtil;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_DATA_PROVIDER,
		"mvc.command.name=/dynamic_data_mapping_data_provider/update_data_provider"
	},
	service = MVCActionCommand.class
)
public class UpdateDataProviderMVCActionCommand
	extends AddDataProviderMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long dataProviderInstanceId = ParamUtil.getLong(
			actionRequest, "dataProviderInstanceId");

		DDMFormValues ddmFormValues = getDDMFormValues(
			actionRequest, actionResponse);

		_restorePasswordDDMFormFieldValues(
			dataProviderInstanceId, ddmFormValues);

		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMDataProviderInstance.class.getName(), actionRequest);

		ddmDataProviderInstanceService.updateDataProviderInstance(
			dataProviderInstanceId,
			getLocalizedMap(themeDisplay.getSiteDefaultLocale(), name),
			getLocalizedMap(themeDisplay.getSiteDefaultLocale(), description),
			ddmFormValues, serviceContext);
	}

	@Reference(target = "(ddm.form.values.deserializer.type=json)")
	protected DDMFormValuesDeserializer jsonDDMFormValuesDeserializer;

	private DDMFormValues _deserialize(String content, DDMForm ddmForm) {
		DDMFormValuesDeserializerDeserializeRequest.Builder builder =
			DDMFormValuesDeserializerDeserializeRequest.Builder.newBuilder(
				content, ddmForm);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				jsonDDMFormValuesDeserializer.deserialize(builder.build());

		return ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();
	}

	private DDMFormFieldValue _findStoredDDMFormFieldValue(
		String name, String instanceId, DDMFormValues storedDDMFormValues) {

		Set<DDMFormFieldValue> storedDDMFormFieldValues = new HashSet<>();

		_flattenDDMFormFieldValues(
			storedDDMFormValues.getDDMFormFieldValues(),
			storedDDMFormFieldValues);

		for (DDMFormFieldValue ddmFormFieldValue : storedDDMFormFieldValues) {
			if (Objects.equals(ddmFormFieldValue.getName(), name) &&
				Objects.equals(ddmFormFieldValue.getInstanceId(), instanceId)) {

				return ddmFormFieldValue;
			}
		}

		return null;
	}

	private void _flattenDDMFormFieldValues(
		List<DDMFormFieldValue> storedDDMFormFieldValues,
		Set<DDMFormFieldValue> collectedDDMFormFieldValues) {

		for (DDMFormFieldValue storedDDMFormFieldValue :
				storedDDMFormFieldValues) {

			collectedDDMFormFieldValues.add(storedDDMFormFieldValue);

			_flattenDDMFormFieldValues(
				storedDDMFormFieldValue.getNestedDDMFormFieldValues(),
				collectedDDMFormFieldValues);
		}
	}

	private DDMForm _getDataProviderInstanceSettingsDDMForm(
		DDMDataProviderInstance dataProviderInstance) {

		DDMDataProvider ddmDataProvider =
			ddmDataProviderRegistry.getDDMDataProvider(
				dataProviderInstance.getType());

		Class<?> clazz = ddmDataProvider.getSettings();

		return DDMFormFactory.create(clazz);
	}

	private DDMFormValues _getStoredDDMFormValues(
		DDMForm dataProviderInstanceSettingsDDMForm,
		String dataProviderInstanceDefinition) {

		return _deserialize(
			dataProviderInstanceDefinition,
			dataProviderInstanceSettingsDDMForm);
	}

	private void _restoreDDMFormFieldValue(
		DDMFormFieldValue ddmFormFieldValue,
		DDMFormFieldValue storedDDMFormFieldValue) {

		Value value = ddmFormFieldValue.getValue();

		Value storedValue = storedDDMFormFieldValue.getValue();

		for (Locale availableLocale : value.getAvailableLocales()) {
			if (Objects.equals(
					value.getString(availableLocale),
					Portal.TEMP_OBFUSCATION_VALUE)) {

				value.addString(
					availableLocale, storedValue.getString(availableLocale));
			}
		}
	}

	private void _restoreDDMFormFieldValue(
		DDMFormFieldValue ddmFormFieldValue,
		DDMFormValues storedDDMFormValues) {

		DDMFormFieldValue storedFormFieldValue = _findStoredDDMFormFieldValue(
			ddmFormFieldValue.getName(), ddmFormFieldValue.getInstanceId(),
			storedDDMFormValues);

		if (storedFormFieldValue != null) {
			_restoreDDMFormFieldValue(ddmFormFieldValue, storedFormFieldValue);
		}
	}

	private void _restoreDDMFormFieldValues(
		Set<String> ddmFormFieldNamesToBeRestored,
		List<DDMFormFieldValue> ddmFormFieldValues,
		DDMFormValues storedDDMFormValues) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (ddmFormFieldNamesToBeRestored.contains(
					ddmFormFieldValue.getName())) {

				_restoreDDMFormFieldValue(
					ddmFormFieldValue, storedDDMFormValues);
			}

			_restoreDDMFormFieldValues(
				ddmFormFieldNamesToBeRestored,
				ddmFormFieldValue.getNestedDDMFormFieldValues(),
				storedDDMFormValues);
		}
	}

	private void _restorePasswordDDMFormFieldValues(
			long dataProviderInstanceId, DDMFormValues ddmFormValues)
		throws Exception {

		DDMDataProviderInstance dataProviderInstance =
			ddmDataProviderInstanceService.getDataProviderInstance(
				dataProviderInstanceId);

		DDMForm dataProviderInstanceSettingsDDMForm =
			_getDataProviderInstanceSettingsDDMForm(dataProviderInstance);

		Set<String> passwordDDMFormFieldNames =
			DDMDataProviderPortletUtil.getDDMFormFieldNamesByType(
				dataProviderInstanceSettingsDDMForm, "password");

		_restoreDDMFormFieldValues(
			passwordDDMFormFieldNames, ddmFormValues.getDDMFormFieldValues(),
			_getStoredDDMFormValues(
				dataProviderInstanceSettingsDDMForm,
				dataProviderInstance.getDefinition()));
	}

}