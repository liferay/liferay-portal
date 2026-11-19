/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.forms.internal.workflow;

import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess;
import com.liferay.portal.workflow.kaleo.forms.service.KaleoProcessLocalService;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.portal.workflow.kaleo.forms.model.KaleoProcess",
	service = WorkflowHandler.class
)
public class KaleoProcessWorkflowHandler
	extends BaseWorkflowHandler<KaleoProcess> {

	@Override
	public String getClassName() {
		return KaleoProcess.class.getName();
	}

	@Override
	public String getTitle(long classPK, Locale locale) {
		try {
			DDLRecord ddlRecord = _ddlRecordLocalService.getDDLRecord(classPK);

			KaleoProcess kaleoProcess =
				_kaleoProcessLocalService.getDDLRecordSetKaleoProcess(
					ddlRecord.getRecordSetId());

			DDLRecordSet ddlRecordSet = kaleoProcess.getDDLRecordSet();

			return ddlRecordSet.getName(locale);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		return null;
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		DDLRecord ddlRecord = _ddlRecordLocalService.getRecord(classPK);

		KaleoProcess kaleoProcess =
			_kaleoProcessLocalService.getDDLRecordSetKaleoProcess(
				ddlRecord.getRecordSetId());

		return _workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
			companyId, groupId, getClassName(),
			kaleoProcess.getKaleoProcessId(), 0);
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public KaleoProcess updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));

		long ddlRecordId = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		DDLRecord record = _ddlRecordLocalService.getRecord(ddlRecordId);

		DDLRecordVersion recordVersion = record.getRecordVersion();

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		DDLRecord ddlRecord = _ddlRecordLocalService.updateStatus(
			userId, recordVersion.getRecordVersionId(), status, serviceContext);

		return _kaleoProcessLocalService.getDDLRecordSetKaleoProcess(
			ddlRecord.getRecordSetId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoProcessWorkflowHandler.class);

	@Reference
	private DDLRecordLocalService _ddlRecordLocalService;

	@Reference
	private KaleoProcessLocalService _kaleoProcessLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}