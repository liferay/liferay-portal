/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.base.WorkflowDefinitionLinkServiceBaseImpl;

import java.util.List;

/**
 * @author Jhosseph Gonzalez
 */
public class WorkflowDefinitionLinkServiceImpl
	extends WorkflowDefinitionLinkServiceBaseImpl {

	@Override
	public WorkflowDefinitionLink addWorkflowDefinitionLink(
			String externalReferenceCode, long userId, long companyId,
			long groupId, String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws PortalException {

		_checkPermission(ActionKeys.ADD_DEFINITION, companyId, groupId, userId);

		return workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			externalReferenceCode, userId, companyId, groupId, className,
			classPK, typePK, workflowDefinitionName, workflowDefinitionVersion);
	}

	@Override
	public WorkflowDefinitionLink
			fetchWorkflowDefinitionLinkByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowDefinitionLinkPersistence.fetchByERC_G(
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

	@Override
	public WorkflowDefinitionLink updateWorkflowDefinitionLink(
			String externalReferenceCode, long userId, long companyId,
			long groupId, String className, long classPK, long typePK,
			String workflowDefinitionName, int workflowDefinitionVersion)
		throws PortalException {

		_checkPermission(ActionKeys.UPDATE, companyId, groupId, userId);

		return workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			externalReferenceCode, userId, companyId, groupId, className,
			classPK, typePK, workflowDefinitionName, workflowDefinitionVersion);
	}

	private void _checkPermission(
			String actionId, long companyId, long groupId, long userId)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		ModelResourcePermission<WorkflowDefinitionLink>
			modelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					"com.liferay.portal.workflow.kaleo.model.KaleoDefinition");

		modelResourcePermission.check(permissionChecker, null, actionId);

		if (!permissionChecker.isOmniadmin() &&
			(companyId != permissionChecker.getCompanyId())) {

			throw new PrincipalException(
				StringBundler.concat(
					"User ", permissionChecker.getUserId(),
					" must be an omniadmin to act in company ", companyId));
		}

		if (userId != permissionChecker.getUserId()) {
			throw new PrincipalException(
				StringBundler.concat(
					"User ", permissionChecker.getUserId(),
					" cannot act on behalf of user ", userId));
		}

		if (groupId == WorkflowConstants.DEFAULT_GROUP_ID) {
			if (!permissionChecker.isCompanyAdmin(companyId)) {
				throw new PrincipalException.MustBeCompanyAdmin(
					permissionChecker);
			}

			return;
		}

		Group group = _groupLocalService.getGroup(groupId);

		if (companyId != group.getCompanyId()) {
			throw new PrincipalException(
				StringBundler.concat(
					"Group ", groupId, " does not belong to company ",
					companyId));
		}

		GroupPermissionUtil.check(permissionChecker, group, ActionKeys.UPDATE);
	}

	@BeanReference(type = GroupLocalService.class)
	private GroupLocalService _groupLocalService;

}