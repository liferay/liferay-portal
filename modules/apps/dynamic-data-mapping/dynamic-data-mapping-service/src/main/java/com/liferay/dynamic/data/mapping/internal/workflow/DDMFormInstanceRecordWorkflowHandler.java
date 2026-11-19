/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.workflow;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
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
 * @author Leonardo Barros
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord",
	service = WorkflowHandler.class
)
public class DDMFormInstanceRecordWorkflowHandler
	extends BaseWorkflowHandler<DDMFormInstanceRecord> {

	@Override
	public AssetRenderer<DDMFormInstanceRecord> getAssetRenderer(long classPK)
		throws PortalException {

		AssetRendererFactory<DDMFormInstanceRecord> assetRendererFactory =
			getAssetRendererFactory();

		if (assetRendererFactory == null) {
			return null;
		}

		DDMFormInstanceRecordVersion formInstanceRecordVersion =
			_ddmFormInstanceRecordVersionLocalService.
				getFormInstanceRecordVersion(classPK);

		return assetRendererFactory.getAssetRenderer(
			formInstanceRecordVersion.getFormInstanceRecordId(),
			AssetRendererFactory.TYPE_LATEST);
	}

	@Override
	public String getClassName() {
		return DDMFormInstanceRecord.class.getName();
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public WorkflowDefinitionLink getWorkflowDefinitionLink(
			long companyId, long groupId, long classPK)
		throws PortalException {

		DDMFormInstanceRecordVersion formInstanceRecordVersion =
			_ddmFormInstanceRecordVersionLocalService.
				getFormInstanceRecordVersion(classPK);

		DDMFormInstanceRecord formInstanceRecord =
			formInstanceRecordVersion.getFormInstanceRecord();

		return _workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
			companyId, groupId, DDMFormInstance.class.getName(),
			formInstanceRecord.getFormInstanceId(), 0);
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public DDMFormInstanceRecord updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));
		long classPK = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			"serviceContext");

		return _ddmFormInstanceRecordLocalService.updateStatus(
			userId, classPK, status, serviceContext);
	}

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

}