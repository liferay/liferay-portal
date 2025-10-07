/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.internal.dto.v1_0.util;

import com.liferay.object.admin.rest.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alberto Sousa
 */
public class WorkflowDefinitionLinkUtil {

	public static List<com.liferay.portal.kernel.model.WorkflowDefinitionLink>
			toWorkflowDefinitionLinks(
				long companyId, GroupLocalService groupLocalService,
				long userId,
				WorkflowDefinitionLinkLocalService
					workflowDefinitionLinkLocalService,
				WorkflowDefinitionLink[] workflowDefinitionLinks)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-17564")) {
			return Collections.emptyList();
		}

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
			serviceBuilderWorkflowDefinitionLink.setWorkflowDefinitionName(
				workflowDefinitionLink.getWorkflowDefinitionName());

			serviceBuilderWorkflowDefinitionLinks.add(
				serviceBuilderWorkflowDefinitionLink);
		}

		return serviceBuilderWorkflowDefinitionLinks;
	}

}