/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.exception.FormInstanceNotPublishedException;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluator;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateRequest;
import com.liferay.dynamic.data.mapping.form.evaluator.DDMFormEvaluatorEvaluateResponse;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldOptionsFactory;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.constants.DDMFormWebKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util.AddFormInstanceRecordMVCCommandUtil;
import com.liferay.dynamic.data.mapping.form.web.internal.util.DDMLayoutUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletSession;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/add_form_instance_record"
	},
	service = MVCActionCommand.class
)
public class AddFormInstanceRecordMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		PortletSession portletSession = actionRequest.getPortletSession();

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		if (groupId == 0) {
			groupId = GetterUtil.getLong(
				portletSession.getAttribute(DDMFormWebKeys.GROUP_ID));
		}

		long formInstanceId = ParamUtil.getLong(
			actionRequest, "formInstanceId");

		if (formInstanceId == 0) {
			formInstanceId = GetterUtil.getLong(
				portletSession.getAttribute(
					DDMFormWebKeys.DYNAMIC_DATA_MAPPING_FORM_INSTANCE_ID));
		}

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceLocalService.getFormInstance(formInstanceId);

		AddFormInstanceRecordMVCCommandUtil.validateExpirationStatus(
			ddmFormInstance, actionRequest);
		AddFormInstanceRecordMVCCommandUtil.validateSubmissionLimitStatus(
			ddmFormInstance, _ddmFormInstanceRecordVersionLocalService,
			actionRequest);

		_validatePublishStatus(actionRequest, ddmFormInstance);

		_validateCaptcha(actionRequest, ddmFormInstance);

		DDMForm ddmForm = getDDMForm(ddmFormInstance);

		DDMFormValues ddmFormValues = _ddmFormValuesFactory.create(
			actionRequest, ddmForm);

		String languageId = ParamUtil.getString(
			actionRequest, "defaultLanguageId");

		if (Validator.isNull(languageId)) {
			languageId = _language.getLanguageId(actionRequest);
		}

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		_createDDMFormFieldOptionsFromDataProvider(
			actionRequest, ddmForm, locale);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DDMFormEvaluatorEvaluateResponse ddmFormEvaluatorEvaluateResponse =
			_ddmFormEvaluator.evaluate(
				DDMFormEvaluatorEvaluateRequest.Builder.newBuilder(
					ddmForm, ddmFormValues, locale
				).withCompanyId(
					_portal.getCompanyId(actionRequest)
				).withDDMFormInstanceId(
					ParamUtil.getLong(actionRequest, "formInstanceId")
				).withGroupId(
					ParamUtil.getLong(actionRequest, "groupId")
				).withTimeZoneId(
					_getTimeZoneId(themeDisplay)
				).withUserId(
					_portal.getUserId(actionRequest)
				).build());

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		AddFormInstanceRecordMVCCommandUtil.updateNonevaluableDDMFormFields(
			ddmForm.getDDMFormFieldsMap(true),
			ddmFormEvaluatorEvaluateResponse.getDDMFormFieldsPropertyChanges(),
			ddmFormValues.getDDMFormFieldValuesMap(true),
			ddmStructure.getDDMFormLayout(),
			ddmFormEvaluatorEvaluateResponse.getDisabledPagesIndexes());

		AddFormInstanceRecordMVCCommandUtil.updateReadOnlyDDMFormFields(
			ddmForm.getDDMFormFieldsMap(true),
			ddmFormEvaluatorEvaluateResponse.getDDMFormFieldsPropertyChanges());

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMFormInstanceRecord.class.getName(), actionRequest);

		serviceContext.setRequest(_portal.getHttpServletRequest(actionRequest));

		_updateFormInstanceRecord(
			actionRequest, ddmFormInstance, ddmFormValues, groupId,
			serviceContext, themeDisplay.getUserId());

		if (!SessionErrors.isEmpty(actionRequest)) {
			return;
		}

		if (SessionMessages.contains(
				actionRequest,
				_portal.getPortletId(actionRequest) +
					SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE)) {

			SessionMessages.clear(actionRequest);
		}

		SessionMessages.add(actionRequest, "formInstanceRecordAdded");

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		String ddmFormInstanceSettingsRedirectURL =
			ddmFormInstanceSettings.redirectURL();

		if (Validator.isNotNull(ddmFormInstanceSettingsRedirectURL)) {
			hideDefaultSuccessMessage(actionRequest);
		}

		portletSession.setAttribute(
			DDMFormWebKeys.DYNAMIC_DATA_MAPPING_FORM_INSTANCE_ID,
			formInstanceId);
		portletSession.setAttribute(DDMFormWebKeys.GROUP_ID, groupId);

		sendRedirect(
			actionRequest, actionResponse,
			ParamUtil.getString(
				actionRequest, "redirect", ddmFormInstanceSettingsRedirectURL));
	}

	protected DDMForm getDDMForm(DDMFormInstance ddmFormInstance)
		throws PortalException {

		DDMFormInstanceVersion latestDDMFormInstanceVersion =
			_ddmFormInstanceVersionLocalService.getLatestFormInstanceVersion(
				ddmFormInstance.getFormInstanceId(),
				WorkflowConstants.STATUS_APPROVED);

		DDMStructureVersion ddmStructureVersion =
			latestDDMFormInstanceVersion.getStructureVersion();

		return ddmStructureVersion.getDDMForm();
	}

	private void _createDDMFormFieldOptionsFromDataProvider(
		ActionRequest actionRequest, DDMForm ddmForm, Locale locale) {

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			_portal.getHttpServletRequest(actionRequest));
		ddmFormFieldRenderingContext.setLocale(locale);

		Map<String, DDMFormField> ddmFormFieldsMap =
			ddmForm.getDDMFormFieldsMap(true);

		for (DDMFormField ddmFormField : ddmFormFieldsMap.values()) {
			if (Objects.equals(ddmFormField.getType(), "select") &&
				Objects.equals(
					ddmFormField.getDataSourceType(), "data-provider")) {

				ddmFormField.setProperty(
					"options",
					_ddmFormFieldOptionsFactory.create(
						ddmFormField, ddmFormFieldRenderingContext));
			}
		}
	}

	private String _getTimeZoneId(ThemeDisplay themeDisplay) {
		if (themeDisplay == null) {
			return StringPool.BLANK;
		}

		User user = themeDisplay.getUser();

		return user.getTimeZoneId();
	}

	private void _updateFormInstanceRecord(
			ActionRequest actionRequest, DDMFormInstance ddmFormInstance,
			DDMFormValues ddmFormValues, long groupId,
			ServiceContext serviceContext, long userId)
		throws Exception {

		long ddmFormInstanceRecordId = ParamUtil.getLong(
			actionRequest, "formInstanceRecordId");

		if (ddmFormInstanceRecordId != 0) {
			_ddmFormInstanceRecordService.updateFormInstanceRecord(
				ddmFormInstanceRecordId, false, ddmFormValues, serviceContext);
		}
		else {
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
				_ddmFormInstanceRecordVersionLocalService.
					fetchLatestFormInstanceRecordVersion(
						userId, ddmFormInstance.getFormInstanceId(),
						ddmFormInstance.getVersion(),
						WorkflowConstants.STATUS_DRAFT);

			if (ddmFormInstanceRecordVersion == null) {
				_ddmFormInstanceRecordService.addFormInstanceRecord(
					groupId, ddmFormInstance.getFormInstanceId(), ddmFormValues,
					serviceContext);
			}
			else {
				_ddmFormInstanceRecordService.updateFormInstanceRecord(
					ddmFormInstanceRecordVersion.getFormInstanceRecordId(),
					false, ddmFormValues, serviceContext);
			}
		}
	}

	private void _validateCaptcha(
			ActionRequest actionRequest, DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMFormInstanceSettings formInstanceSettings =
			ddmFormInstance.getSettingsModel();

		if (formInstanceSettings.requireCaptcha()) {
			CaptchaUtil.check(actionRequest);
		}
	}

	private void _validatePublishStatus(
			ActionRequest actionRequest, DDMFormInstance ddmFormInstance)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String currentURL = ParamUtil.getString(actionRequest, "currentURL");

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		if (StringUtil.startsWith(
				currentURL, DDMLayoutUtil.getFormLayoutURL(themeDisplay)) &&
			!ddmFormInstanceSettings.published()) {

			throw new FormInstanceNotPublishedException(
				"Form instance " + ddmFormInstance.getFormInstanceId() +
					" is not published");
		}
	}

	@Reference
	private DDMFormEvaluator _ddmFormEvaluator;

	@Reference
	private DDMFormFieldOptionsFactory _ddmFormFieldOptionsFactory;

	@Reference
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

	@Reference
	private DDMFormInstanceVersionLocalService
		_ddmFormInstanceVersionLocalService;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}