/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.service.base.WorkflowDefinitionLinkServiceBaseImpl;

import java.util.List;

/**
 * @author Jhosseph Gonzalez
 */
public class WorkflowDefinitionLinkServiceImpl
	extends WorkflowDefinitionLinkServiceBaseImpl {

	@Override
	public WorkflowDefinitionLink addWorkflowDefinitionLink(
			long userId, long companyId, long groupId, String className,
			long classPK, long typePK, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException {

		ModelResourcePermission<WorkflowDefinitionLink>
			modelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					"com.liferay.portal.workflow.kaleo.model.KaleoDefinition");

		modelResourcePermission.check(
			getPermissionChecker(), null, ActionKeys.ADD_DEFINITION);

		return workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			userId, companyId, groupId, className, classPK, typePK,
			workflowDefinitionName, workflowDefinitionVersion);
	}

	@Override
	public WorkflowDefinitionLink
			fetchWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowDefinitionLinkLocalService.
				fetchWorkflowDefinitionLinkByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (workflowDefinitionLink != null) {
			ModelResourcePermission<WorkflowDefinitionLink>
				modelResourcePermission =
					ModelResourcePermissionRegistryUtil.
						getModelResourcePermission(
							"com.liferay.portal.workflow.kaleo.model." +
								"KaleoDefinition");

			modelResourcePermission.check(
				getPermissionChecker(), null, ActionKeys.VIEW);
		}

		return workflowDefinitionLink;
	}

	@Override
	public List<WorkflowDefinitionLink> getWorkflowDefinitionLinks(
			long companyId, String workflowDefinitionName,
			int workflowDefinitionVersion)
		throws PortalException {

		ModelResourcePermission<WorkflowDefinitionLink>
			modelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					"com.liferay.portal.workflow.kaleo.model.KaleoDefinition");

		modelResourcePermission.check(
			getPermissionChecker(), null, ActionKeys.VIEW);

		return workflowDefinitionLinkLocalService.getWorkflowDefinitionLinks(
			companyId, workflowDefinitionName, workflowDefinitionVersion);
	}

}