/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.exception.FormInstanceExpiredException;
import com.liferay.dynamic.data.mapping.exception.FormInstanceSubmissionLimitException;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializer;
import com.liferay.dynamic.data.mapping.form.builder.context.DDMFormContextDeserializerRequest;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util.AddFormInstanceRecordMVCCommandUtil;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 * @author Harlan Bruno
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"mvc.command.name=/dynamic_data_mapping_form/add_form_instance_record"
	},
	service = MVCResourceCommand.class
)
public class AddFormInstanceRecordMVCResourceCommand
	extends BaseMVCResourceCommand {

	protected DDMFormValues createDDMFormValues(
			DDMFormInstance ddmFormInstance, ResourceRequest resourceRequest)
		throws Exception {

		String serializedDDMFormValues = ParamUtil.getString(
			resourceRequest, "serializedDDMFormValues");

		if (Validator.isNull(serializedDDMFormValues)) {
			return null;
		}

		DDMFormContextDeserializerRequest ddmFormContextDeserializerRequest =
			DDMFormContextDeserializerRequest.with(
				getDDMForm(ddmFormInstance), serializedDDMFormValues);

		Locale currentLocale = LocaleUtil.fromLanguageId(
			_language.getLanguageId(resourceRequest));

		ddmFormContextDeserializerRequest.addProperty(
			"currentLocale", currentLocale);

		return _ddmFormBuilderContextToDDMFormValues.deserialize(
			ddmFormContextDeserializerRequest);
	}

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		boolean preview = ParamUtil.getBoolean(resourceRequest, "preview");

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		if (preview || user.isGuestUser()) {
			return;
		}

		long formInstanceId = ParamUtil.getLong(
			resourceRequest, "formInstanceId");

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceLocalService.getFormInstance(formInstanceId);

		try {
			AddFormInstanceRecordMVCCommandUtil.validateExpirationStatus(
				ddmFormInstance, resourceRequest);
			AddFormInstanceRecordMVCCommandUtil.validateSubmissionLimitStatus(
				ddmFormInstance, _ddmFormInstanceRecordVersionLocalService,
				resourceRequest);
		}
		catch (FormInstanceExpiredException formInstanceExpiredException) {
			if (_log.isDebugEnabled()) {
				_log.debug(formInstanceExpiredException);
			}

			return;
		}
		catch (FormInstanceSubmissionLimitException
					formInstanceSubmissionLimitException) {

			if (_log.isDebugEnabled()) {
				_log.debug(formInstanceSubmissionLimitException);
			}

			return;
		}

		DDMFormValues ddmFormValues = createDDMFormValues(
			ddmFormInstance, resourceRequest);

		if (ddmFormValues == null) {
			return;
		}

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			_ddmFormInstanceRecordVersionLocalService.
				fetchLatestFormInstanceRecordVersion(
					themeDisplay.getUserId(), formInstanceId,
					ddmFormInstance.getVersion(),
					WorkflowConstants.STATUS_DRAFT);

		ServiceContext serviceContext = _createServiceContext(resourceRequest);

		if (ddmFormInstanceRecordVersion == null) {
			_ddmFormInstanceRecordService.addFormInstanceRecord(
				ddmFormInstance.getGroupId(), formInstanceId, ddmFormValues,
				serviceContext);
		}
		else {
			_ddmFormInstanceRecordService.updateFormInstanceRecord(
				ddmFormInstanceRecordVersion.getFormInstanceRecordId(), false,
				ddmFormValues, serviceContext);
		}
	}

	protected DDMForm getDDMForm(DDMFormInstance ddmFormInstance)
		throws PortalException {

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		return ddmStructure.getDDMForm();
	}

	private ServiceContext _createServiceContext(
			ResourceRequest resourceRequest)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMFormInstanceRecord.class.getName(), resourceRequest);

		serviceContext.setAttribute("status", WorkflowConstants.STATUS_DRAFT);
		serviceContext.setAttribute("validateDDMFormValues", Boolean.FALSE);
		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		return serviceContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddFormInstanceRecordMVCResourceCommand.class);

	@Reference(
		target = "(dynamic.data.mapping.form.builder.context.deserializer.type=formValues)"
	)
	private DDMFormContextDeserializer<DDMFormValues>
		_ddmFormBuilderContextToDDMFormValues;

	@Reference
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

	@Reference
	private Language _language;

}