/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.Transition;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskIds;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskTransition;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskTransitions;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.TransitionUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowTaskTransitionsResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.kernel.workflow.WorkflowTransition;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Inácio Nery
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workflow-task-transitions.properties",
	scope = ServiceScope.PROTOTYPE,
	service = WorkflowTaskTransitionsResource.class
)
@CTAware
public class WorkflowTaskTransitionsResourceImpl
	extends BaseWorkflowTaskTransitionsResourceImpl {

	@Override
	public WorkflowTaskTransitions postWorkflowTaskTransition(
			WorkflowTaskIds workflowTaskIds)
		throws Exception {

		return new WorkflowTaskTransitions() {
			{
				setWorkflowTaskTransitions(
					() -> {
						List<WorkflowTaskTransition> workflowTaskTransitions =
							new ArrayList<>();

						for (Long workflowTaskId :
								workflowTaskIds.getWorkflowTaskIds()) {

							workflowTaskTransitions.add(
								_createWorkflowTaskTransition(
									_workflowTaskManager.getWorkflowTask(
										workflowTaskId),
									_workflowTaskManager.
										liberalGetNextWorkflowTransitions(
											workflowTaskId)));
						}

						return workflowTaskTransitions.toArray(
							new WorkflowTaskTransition[0]);
					});
			}
		};
	}

	private WorkflowTaskTransition _createWorkflowTaskTransition(
		WorkflowTask workflowTask,
		List<WorkflowTransition> workflowTransitions) {

		WorkflowTaskTransition workflowTaskTransition =
			new WorkflowTaskTransition();

		workflowTaskTransition.setTransitions(
			() -> transformToArray(
				workflowTransitions,
				workflowTransition -> TransitionUtil.toTransition(
					contextAcceptLanguage.getPreferredLocale(),
					workflowTransition),
				Transition.class));
		workflowTaskTransition.setWorkflowDefinitionVersion(
			() -> String.valueOf(workflowTask.getWorkflowDefinitionVersion()));
		workflowTaskTransition.setWorkflowTaskLabel(
			() -> _language.get(
				ResourceBundleUtil.getModuleAndPortalResourceBundle(
					contextAcceptLanguage.getPreferredLocale(),
					WorkflowTaskTransitionsResourceImpl.class),
				workflowTask.getName()));
		workflowTaskTransition.setWorkflowTaskName(workflowTask::getName);

		return workflowTaskTransition;
	}

	@Reference
	private Language _language;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}