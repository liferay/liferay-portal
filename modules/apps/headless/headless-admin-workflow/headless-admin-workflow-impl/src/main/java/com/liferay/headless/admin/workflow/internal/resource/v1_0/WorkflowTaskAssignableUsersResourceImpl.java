/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.Assignee;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignableUser;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignableUsers;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskIds;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.AssigneeUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowTaskAssignableUsersResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workflow-task-assignable-users.properties",
	scope = ServiceScope.PROTOTYPE,
	service = WorkflowTaskAssignableUsersResource.class
)
@CTAware
public class WorkflowTaskAssignableUsersResourceImpl
	extends BaseWorkflowTaskAssignableUsersResourceImpl {

	@Override
	public WorkflowTaskAssignableUsers postWorkflowTaskAssignableUser(
			WorkflowTaskIds workflowTaskIds)
		throws Exception {

		return new WorkflowTaskAssignableUsers() {
			{
				setWorkflowTaskAssignableUsers(
					() -> {
						List<WorkflowTaskAssignableUser>
							workflowTaskAssignableUsers = new ArrayList<>();

						Set<User> commonAssignableUsers = null;

						for (Long workflowTaskId :
								workflowTaskIds.getWorkflowTaskIds()) {

							List<User> assignableUsers =
								_workflowTaskManager.getAssignableUsers(
									workflowTaskId);

							if (commonAssignableUsers == null) {
								commonAssignableUsers = new TreeSet<>(
									UserFirstNameComparator.getInstance(true));

								commonAssignableUsers.addAll(assignableUsers);
							}
							else {
								commonAssignableUsers.retainAll(
									assignableUsers);
							}

							workflowTaskAssignableUsers.add(
								_createWorkflowTaskAssignableUser(
									assignableUsers, workflowTaskId));
						}

						workflowTaskAssignableUsers.add(
							_createWorkflowTaskAssignableUser(
								commonAssignableUsers,
								_COMMON_WORKFLOW_TASK_ID));

						return workflowTaskAssignableUsers.toArray(
							new WorkflowTaskAssignableUser[0]);
					});
			}
		};
	}

	private WorkflowTaskAssignableUser _createWorkflowTaskAssignableUser(
		Collection<User> assignableUsers, Long workflowTaskId) {

		WorkflowTaskAssignableUser workflowTaskAssignableUser =
			new WorkflowTaskAssignableUser();

		workflowTaskAssignableUser.setAssignableUsers(
			() -> transformToArray(
				assignableUsers, AssigneeUtil::toAssignee, Assignee.class));
		workflowTaskAssignableUser.setWorkflowTaskId(() -> workflowTaskId);

		return workflowTaskAssignableUser;
	}

	private static final Long _COMMON_WORKFLOW_TASK_ID = 0L;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}