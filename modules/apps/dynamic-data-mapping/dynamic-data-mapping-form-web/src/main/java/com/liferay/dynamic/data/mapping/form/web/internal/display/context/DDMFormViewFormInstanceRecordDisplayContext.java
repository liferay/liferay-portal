/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.display.context;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.field.type.constants.DDMFormFieldTypeConstants;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderer;
import com.liferay.dynamic.data.mapping.form.renderer.DDMFormRenderingContext;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.constants.DDMFormWebKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.helper.DDMFormAdminRequestHelper;
import com.liferay.dynamic.data.mapping.form.web.internal.display.context.util.DDMFormDisplayContextUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormLayout;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormValuesMerger;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Marcellus Tavares
 */
public class DDMFormViewFormInstanceRecordDisplayContext {

	public DDMFormViewFormInstanceRecordDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DDMFormInstanceRecordLocalService ddmFormInstanceRecordLocalService,
		DDMFormInstanceVersionLocalService ddmFormInstanceVersionLocalService,
		DDMFormRenderer ddmFormRenderer,
		DDMFormValuesFactory ddmFormValuesFactory,
		DDMFormValuesMerger ddmFormValuesMerger) {

		_httpServletResponse = httpServletResponse;
		_ddmFormInstanceRecordLocalService = ddmFormInstanceRecordLocalService;
		_ddmFormInstanceVersionLocalService =
			ddmFormInstanceVersionLocalService;
		_ddmFormRenderer = ddmFormRenderer;
		_ddmFormValuesFactory = ddmFormValuesFactory;
		_ddmFormValuesMerger = ddmFormValuesMerger;

		_ddmFormAdminRequestHelper = new DDMFormAdminRequestHelper(
			httpServletRequest);
	}

	public Map<String, Object> getDDMFormContext(RenderRequest renderRequest)
		throws Exception {

		return getDDMFormContext(renderRequest, true);
	}

	public Map<String, Object> getDDMFormContext(
			RenderRequest renderRequest, boolean readOnly)
		throws Exception {

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_getDDMFormInstanceRecord();

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecord.getFormInstance();

		DDMFormInstanceVersion latestDDMFormInstanceVersion =
			_ddmFormInstanceVersionLocalService.getLatestFormInstanceVersion(
				ddmFormInstance.getFormInstanceId(),
				WorkflowConstants.STATUS_APPROVED);

		DDMStructureVersion latestDDMStructureVersion =
			latestDDMFormInstanceVersion.getStructureVersion();

		DDMForm latestDDMForm = latestDDMStructureVersion.getDDMForm();

		if (!readOnly) {
			DDMFormDisplayContextUtil.addCaptchaDDMFormField(
				latestDDMForm, ddmFormInstance.getSettingsModel(),
				renderRequest);

			DDMFormLayout latestDDMFormLayout =
				latestDDMStructureVersion.getDDMFormLayout();

			DDMFormDisplayContextUtil.addCaptchaDDMFormLayoutRow(
				ddmFormInstance.getSettingsModel(), latestDDMFormLayout);

			return _ddmFormRenderer.getDDMFormTemplateContext(
				latestDDMForm, latestDDMFormLayout,
				_createDDMFormRenderingContext(
					latestDDMForm, ddmFormInstanceRecord,
					_getDDMFormValues(
						renderRequest, latestDDMForm,
						ddmFormInstanceRecord.getDDMFormValues()),
					false));
		}

		DDMFormInstanceVersion currentDDMFormInstanceVersion =
			ddmFormInstance.getFormInstanceVersion(
				ddmFormInstanceRecord.getFormInstanceVersion());

		DDMStructureVersion currentDDMStructureVersion =
			currentDDMFormInstanceVersion.getStructureVersion();

		DDMForm currentDDMForm = currentDDMStructureVersion.getDDMForm();

		DDMFormRenderingContext ddmFormRenderingContext =
			_createDDMFormRenderingContext(
				currentDDMForm, ddmFormInstanceRecord,
				_getDDMFormValues(
					renderRequest, currentDDMForm,
					ddmFormInstanceRecord.getDDMFormValues()),
				true);

		if (!Objects.equals(currentDDMForm, latestDDMForm)) {
			_updateDDMFormFields(
				currentDDMForm.getDDMFormFieldsMap(true),
				latestDDMForm.getDDMFormFieldsMap(true),
				ddmFormRenderingContext.getLocale());
		}

		return _ddmFormRenderer.getDDMFormTemplateContext(
			currentDDMForm, currentDDMStructureVersion.getDDMFormLayout(),
			ddmFormRenderingContext);
	}

	private DDMFormRenderingContext _createDDMFormRenderingContext(
		DDMForm ddmForm, DDMFormInstanceRecord ddmFormInstanceRecord,
		DDMFormValues ddmFormValues, boolean readOnly) {

		DDMFormRenderingContext ddmFormRenderingContext =
			new DDMFormRenderingContext();

		ddmFormRenderingContext.addProperty(
			"ddmFormInstanceRecordId",
			ddmFormInstanceRecord.getFormInstanceRecordId());

		String redirectURL = ParamUtil.getString(
			_ddmFormAdminRequestHelper.getRequest(), "redirect");

		if (Validator.isNotNull(redirectURL)) {
			Locale locale = ddmForm.getDefaultLocale();

			Set<Locale> availableLocales = ddmForm.getAvailableLocales();

			if (availableLocales.contains(
					_ddmFormAdminRequestHelper.getLocale())) {

				locale = _ddmFormAdminRequestHelper.getLocale();
			}

			ddmFormRenderingContext.setCancelLabel(
				LanguageUtil.get(locale, "cancel"));
		}

		ddmFormRenderingContext.setContainerId(
			"ddmForm".concat(StringUtil.randomString()));
		ddmFormRenderingContext.setDDMFormInstanceId(
			ddmFormInstanceRecord.getFormInstanceId());
		ddmFormRenderingContext.setDDMFormValues(ddmFormValues);
		ddmFormRenderingContext.setHttpServletRequest(
			_ddmFormAdminRequestHelper.getRequest());
		ddmFormRenderingContext.setHttpServletResponse(_httpServletResponse);
		ddmFormRenderingContext.setLocale(ddmFormValues.getDefaultLocale());
		ddmFormRenderingContext.setPortletNamespace(
			PortalUtil.getPortletNamespace(
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN));
		ddmFormRenderingContext.setReadOnly(readOnly);

		if (Validator.isNotNull(redirectURL)) {
			ddmFormRenderingContext.setRedirectURL(redirectURL);
		}
		else {
			ddmFormRenderingContext.setShowCancelButton(false);
		}

		ddmFormRenderingContext.setViewMode(true);

		return ddmFormRenderingContext;
	}

	private DDMFormInstanceRecord _getDDMFormInstanceRecord() throws Exception {
		HttpServletRequest httpServletRequest =
			_ddmFormAdminRequestHelper.getRequest();

		long formInstanceRecordId = ParamUtil.getLong(
			httpServletRequest, "formInstanceRecordId");

		if (formInstanceRecordId > 0) {
			return _ddmFormInstanceRecordLocalService.fetchFormInstanceRecord(
				formInstanceRecordId);
		}

		return (DDMFormInstanceRecord)httpServletRequest.getAttribute(
			DDMFormWebKeys.DYNAMIC_DATA_MAPPING_FORM_INSTANCE_RECORD);
	}

	private DDMFormValues _getDDMFormValues(
			RenderRequest renderRequest, DDMForm ddmForm,
			DDMFormValues ddmFormValues)
		throws Exception {

		DDMFormValues mergedDDMFormValues = _ddmFormValuesMerger.merge(
			ddmFormValues,
			_ddmFormValuesFactory.create(renderRequest, ddmForm));

		mergedDDMFormValues.setAvailableLocales(
			ddmFormValues.getAvailableLocales());
		mergedDDMFormValues.setDefaultLocale(ddmFormValues.getDefaultLocale());

		return mergedDDMFormValues;
	}

	private void _updateDDMFormFields(
		Map<String, DDMFormField> currentDDMFormFieldsMap,
		Map<String, DDMFormField> latestDDMFormFielsMap, Locale locale) {

		for (DDMFormField ddmFormField : currentDDMFormFieldsMap.values()) {
			if (Objects.equals(
					DDMFormFieldTypeConstants.FIELDSET,
					ddmFormField.getType())) {

				continue;
			}

			ddmFormField.setProperty("requireConfirmation", false);

			if (latestDDMFormFielsMap.containsKey(ddmFormField.getName())) {
				continue;
			}

			ddmFormField.setReadOnly(true);

			LocalizedValue localizedValue = ddmFormField.getLabel();

			localizedValue.addString(
				locale,
				LanguageUtil.format(
					locale, "x-removed", localizedValue.getString(locale),
					false));
		}
	}

	private final DDMFormAdminRequestHelper _ddmFormAdminRequestHelper;
	private final DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;
	private final DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;
	private final DDMFormRenderer _ddmFormRenderer;
	private final DDMFormValuesFactory _ddmFormValuesFactory;
	private final DDMFormValuesMerger _ddmFormValuesMerger;
	private final HttpServletResponse _httpServletResponse;

}