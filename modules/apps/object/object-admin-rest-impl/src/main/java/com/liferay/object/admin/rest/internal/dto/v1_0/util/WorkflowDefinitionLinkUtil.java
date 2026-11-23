/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.util;

import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.object.admin.rest.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alberto Sousa
 */
public class WorkflowDefinitionLinkUtil {

	public static List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
			toWorkflowDefinitionLinks(
				long companyId, GroupLocalService groupLocalService,
				KaleoDefinitionLocalService kaleoDefinitionLocalService,
				long userId,
				WorkflowDefinitionLinkLocalService
					workflowDefinitionLinkLocalService,
				WorkflowDefinitionLink[] workflowDefinitionLinks)
		throws Exception {

		List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
			serviceBuilderWorkflowDefinitionLinks = new ArrayList<>();

		if (workflowDefinitionLinks == null) {
			return serviceBuilderWorkflowDefinitionLinks;
		}

		for (WorkflowDefinitionLink workflowDefinitionLink :
				workflowDefinitionLinks) {

			com.liferay.portal.kernel.model.WorkflowDefinitionLink
				serviceBuilderWorkflowDefinitionLink =
					workflowDefinitionLinkLocalService.
						createWorkflowDefinitionLink(0L);

			if (Validator.isNotNull(
					workflowDefinitionLink.getGroupExternalReferenceCode())) {

				Group group =
					groupLocalService.fetchGroupByExternalReferenceCode(
						workflowDefinitionLink.getGroupExternalReferenceCode(),
						companyId);

				if (group == null) {
					throw new ObjectDefinitionScopeException(
						"An object definition can only be linked to a " +
							"workflow definition with an existing group");
				}

				serviceBuilderWorkflowDefinitionLink.setGroupId(
					group.getGroupId());
			}

			serviceBuilderWorkflowDefinitionLink.setUserId(userId);

			String workflowDefinitionName =
				workflowDefinitionLink.getWorkflowDefinitionName();

			if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-49856") &&
				ExportImportThreadLocal.isImportInProcess() &&
				Validator.isNotNull(
					workflowDefinitionLink.
						getWorkflowDefinitionExternalReferenceCode())) {

				ServiceContext serviceContext = new ServiceContext();

				serviceContext.setCompanyId(companyId);
				serviceContext.setUserId(userId);

				KaleoDefinition kaleoDefinition =
					kaleoDefinitionLocalService.
						fetchKaleoDefinitionByExternalReferenceCode(
							workflowDefinitionLink.
								getWorkflowDefinitionExternalReferenceCode(),
							companyId);

				if (kaleoDefinition == null) {
					kaleoDefinition =
						kaleoDefinitionLocalService.fetchKaleoDefinition(
							workflowDefinitionName, serviceContext);
				}

				if (kaleoDefinition == null) {
					try (SafeCloseable safeCloseable =
							LazyReferencingThreadLocal.
								setEnabledWithSafeCloseable(true)) {

						kaleoDefinition =
							kaleoDefinitionLocalService.
								getOrAddEmptyKaleoDefinition(
									workflowDefinitionLink.
										getWorkflowDefinitionExternalReferenceCode(),
									workflowDefinitionName,
									WorkflowDefinitionConstants.SCOPE_ALL,
									false, serviceContext);
					}
				}

				workflowDefinitionName = kaleoDefinition.getName();
			}

			serviceBuilderWorkflowDefinitionLink.setWorkflowDefinitionName(
				workflowDefinitionName);

			serviceBuilderWorkflowDefinitionLinks.add(
				serviceBuilderWorkflowDefinitionLink);
		}

		return serviceBuilderWorkflowDefinitionLinks;
	}

}