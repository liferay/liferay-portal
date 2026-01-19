/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.resource.v1_0;

import com.liferay.ai.hub.rest.dto.v1_0.TaskDefinition;
import com.liferay.ai.hub.rest.resource.v1_0.TaskDefinitionResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Feliphe Marinho
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/task-definition.properties",
	scope = ServiceScope.PROTOTYPE, service = TaskDefinitionResource.class
)
public class TaskDefinitionResourceImpl extends BaseTaskDefinitionResourceImpl {

	@Override
	public void deleteTaskDefinition(Long taskDefinitionId) throws Exception {
		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.getWorkflowDefinition(taskDefinitionId);

		_workflowDefinitionManager.updateActive(
			contextCompany.getCompanyId(), contextUser.getUserId(),
			workflowDefinition.getName(), workflowDefinition.getVersion(),
			false);

		_workflowDefinitionManager.undeployWorkflowDefinition(
			contextCompany.getCompanyId(), contextUser.getUserId(),
			workflowDefinition.getName(), workflowDefinition.getVersion());
	}

	@Override
	public Page<TaskDefinition> getTaskDefinitionsPage(
			String search, Filter filter, Pagination pagination, Sort[] sorts)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		return SearchUtil.search(
			HashMapBuilder.put(
				"get",
				addAction(
					ActionKeys.VIEW, "getTaskDefinitionsPage",
					WorkflowConstants.RESOURCE_NAME, null)
			).build(),
			booleanQuery -> {
				BooleanFilter booleanFilter =
					booleanQuery.getPreBooleanFilter();

				booleanFilter.addRequiredTerm("latest", Boolean.TRUE);
				booleanFilter.addRequiredTerm("scope", "ai");
			},
			filter, KaleoDefinitionVersion.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.NAME, Field.VERSION),
			searchContext -> searchContext.setCompanyId(
				contextCompany.getCompanyId()),
			sorts,
			document -> _toTaskDefinition(
				_workflowDefinitionManager.getWorkflowDefinition(
					contextCompany.getCompanyId(), document.get(Field.NAME),
					GetterUtil.getInteger(document.get(Field.VERSION)))));
	}

	private TaskDefinition _toTaskDefinition(
			WorkflowDefinition workflowDefinition)
		throws PortalException {

		return new TaskDefinition() {
			{
				setActions(
					() -> HashMapBuilder.put(
						"delete",
						() -> {
							if (ArrayUtil.contains(
									WorkflowDefinitionConstants.
										SYSTEM_WORKFLOW_DEFINITION_NAMES,
									workflowDefinition.getName())) {

								return null;
							}

							return addAction(
								ActionKeys.DELETE,
								workflowDefinition.getWorkflowDefinitionId(),
								"deleteTaskDefinition",
								_workflowDefinitionModelResourcePermission);
						}
					).build());
				setDescription(workflowDefinition::getDescription);
				setId(workflowDefinition::getWorkflowDefinitionId);
				setName(workflowDefinition::getName);
				setVersion(workflowDefinition::getVersion);
			}
		};
	}

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoDefinition)"
	)
	private ModelResourcePermission<?>
		_workflowDefinitionModelResourcePermission;

}