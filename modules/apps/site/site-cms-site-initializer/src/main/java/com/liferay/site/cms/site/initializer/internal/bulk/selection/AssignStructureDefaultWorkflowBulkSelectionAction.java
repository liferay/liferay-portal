/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Attila Bakay
 */
@Component(
	property = "bulk.selection.action.key=assign.structure.default.workflow.object.definition",
	service = BulkSelectionAction.class
)
public class AssignStructureDefaultWorkflowBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		if (object instanceof ObjectDefinition) {
			ObjectDefinition objectDefinition = (ObjectDefinition)object;

			List<WorkflowDefinitionLink> workflowDefinitionLinkList =
				new ArrayList<>();

			String workflow = (String)inputMap.get("workflow");

			if (!workflow.equals(StringPool.BLANK)) {
				WorkflowDefinition workflowDefinition =
					_workflowDefinitionManager.getLatestWorkflowDefinition(
						objectDefinition.getCompanyId(), workflow);

				workflowDefinitionLinkList.add(
					_workflowDefinitionLinkLocalService.
						updateWorkflowDefinitionLink(
							user.getUserId(), objectDefinition.getCompanyId(),
							0, objectDefinition.getClassName(), 0, 0,
							workflowDefinition.getName(),
							workflowDefinition.getVersion()));
			}

			_objectDefinitionLocalService.addOrUpdateWorkflowDefinitionLinks(
				objectDefinition, workflowDefinitionLinkList);
		}
		else {
			throw new IllegalArgumentException("Unsupported object " + object);
		}
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}