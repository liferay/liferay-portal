/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.internal.workflow;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordVersionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.lists.model.DDLRecord",
	service = WorkflowHandler.class
)
public class DDLRecordWorkflowHandler extends BaseWorkflowHandler<DDLRecord> {

	@Override
	public AssetRenderer<DDLRecord> getAssetRenderer(long classPK)
		throws PortalException {

		AssetRendererFactory<DDLRecord> assetRendererFactory =
			getAssetRendererFactory();

		if (assetRendererFactory == null) {
			return null;
		}

		DDLRecordVersion recordVersion =
			_ddlRecordVersionLocalService.getRecordVersion(classPK);

		return assetRendererFactory.getAssetRenderer(
			recordVersion.getRecordId(), AssetRendererFactory.TYPE_LATEST);
	}

	@Override
	public String getClassName() {
		return DDLRecord.class.getName();
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		DDLRecordVersion recordVersion =
			_ddlRecordVersionLocalService.getRecordVersion(classPK);

		DDLRecord record = recordVersion.getRecord();

		return _workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
			companyId, groupId, DDLRecordSet.class.getName(),
			record.getRecordSetId(), 0);
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public DDLRecord updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));
		long classPK = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		return _ddlRecordLocalService.updateStatus(
			userId, classPK, status, serviceContext);
	}

	@Reference
	private DDLRecordLocalService _ddlRecordLocalService;

	@Reference
	private DDLRecordVersionLocalService _ddlRecordVersionLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}