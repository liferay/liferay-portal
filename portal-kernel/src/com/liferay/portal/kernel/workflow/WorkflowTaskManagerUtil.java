/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Micha Kiener
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 * @author Raymond Augé
 */
public class WorkflowTaskManagerUtil {

	public static WorkflowTask assignWorkflowTaskToRole(
			long companyId, long userId, long workflowTaskId, long roleId,
			String comment, Date dueDate,
			Map<String, Serializable> workflowContext)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.assignWorkflowTaskToRole(
			companyId, userId, workflowTaskId, roleId, comment, dueDate,
			workflowContext);
	}

	public static WorkflowTask assignWorkflowTaskToUser(
			long companyId, long userId, long workflowTaskId,
			long assigneeUserId, String comment, Date dueDate,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.assignWorkflowTaskToUser(
			companyId, userId, workflowTaskId, assigneeUserId, comment, dueDate,
			workflowContext);
	}

	public static WorkflowTask completeWorkflowTask(
			long companyId, long userId, long workflowTaskId,
			String transitionName, String comment,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.completeWorkflowTask(
			companyId, userId, workflowTaskId, transitionName, comment,
			workflowContext);
	}

	public static WorkflowTask completeWorkflowTask(
			long companyId, long userId, long workflowTaskId,
			String transitionName, String comment,
			Map<String, Serializable> workflowContext,
			boolean waitForCompletion)
		throws PortalException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.completeWorkflowTask(
			companyId, userId, workflowTaskId, transitionName, comment,
			workflowContext, waitForCompletion);
	}

	public static WorkflowTask fetchWorkflowTask(long workflowTaskId)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.fetchWorkflowTask(workflowTaskId);
	}

	public static List<User> getAssignableUsers(long workflowTaskId)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getAssignableUsers(workflowTaskId);
	}

	public static List<String> getNextTransitionNames(
			long companyId, long userId, long workflowTaskId)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getNextTransitionNames(
			userId, workflowTaskId);
	}

	public static List<User> getNotifiableUsers(long workflowTaskId)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getNotifiableUsers(workflowTaskId);
	}

	public static WorkflowTask getWorkflowTask(
			long companyId, long workflowTaskId)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTask(workflowTaskId);
	}

	public static int getWorkflowTaskCount(long companyId, Boolean completed)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTaskCount(companyId, completed);
	}

	public static int getWorkflowTaskCountByRole(
			long companyId, long roleId, Boolean completed)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTaskCountByRole(
			companyId, roleId, completed);
	}

	public static int getWorkflowTaskCountBySubmittingUser(
			long companyId, long userId, Boolean completed)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTaskCountBySubmittingUser(
			companyId, userId, completed);
	}

	public static int getWorkflowTaskCountByUser(
			long companyId, long userId, Boolean completed)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTaskCountByUser(
			companyId, userId, completed);
	}

	public static int getWorkflowTaskCountByUserRoles(
			long companyId, long userId, Boolean completed)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTaskCountByUserRoles(
			companyId, userId, completed);
	}

	public static int getWorkflowTaskCountByUserRoles(
			long companyId, long userId, long workflowInstanceId,
			Boolean completed)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTaskCountByUserRoles(
			companyId, userId, workflowInstanceId, completed);
	}

	public static int getWorkflowTaskCountByWorkflowInstance(
			long companyId, Long userId, long workflowInstanceId,
			Boolean completed)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTaskCountByWorkflowInstance(
			companyId, userId, workflowInstanceId, completed);
	}

	public static List<WorkflowTransition> getWorkflowTaskWorkflowTransitions(
			long workflowTaskId)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTaskWorkflowTransitions(
			workflowTaskId);
	}

	public static List<WorkflowTask> getWorkflowTasks(
			long companyId, Boolean completed, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTasks(
			companyId, completed, start, end, orderByComparator);
	}

	public static List<WorkflowTask> getWorkflowTasksByRole(
			long companyId, long roleId, Boolean completed, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTasksByRole(
			companyId, roleId, completed, start, end, orderByComparator);
	}

	public static List<WorkflowTask> getWorkflowTasksBySubmittingUser(
			long companyId, long userId, Boolean completed, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTasksBySubmittingUser(
			companyId, userId, completed, start, end, orderByComparator);
	}

	public static List<WorkflowTask> getWorkflowTasksByUser(
			long companyId, long userId, Boolean completed, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTasksByUser(
			companyId, userId, completed, start, end, orderByComparator);
	}

	public static List<WorkflowTask> getWorkflowTasksByUserRoles(
			long companyId, long userId, Boolean completed, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTasksByUserRoles(
			companyId, userId, completed, start, end, orderByComparator);
	}

	public static List<WorkflowTask> getWorkflowTasksByWorkflowInstance(
			long companyId, Long userId, long workflowInstanceId,
			Boolean completed, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.getWorkflowTasksByWorkflowInstance(
			companyId, userId, workflowInstanceId, completed, start, end,
			orderByComparator);
	}

	public static boolean hasAssignableUsers(
			long companyId, long workflowTaskId)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.hasAssignableUsers(workflowTaskId);
	}

	public static boolean isNotifiableUser(long userId, long workflowTaskId)
		throws PortalException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.isNotifiableUser(userId, workflowTaskId);
	}

	public static List<WorkflowTask> search(
			long companyId, long userId, String assetTitle, String[] taskNames,
			String[] assetTypes, Long[] assetPrimaryKeys,
			String assigneeClassName, Long[] assigneeUserIds, Date dueDateGT,
			Date dueDateLT, Boolean completed, Boolean searchByUserRoles,
			Long workflowDefinitionId, Long[] workflowInstanceIds,
			Boolean andOperator, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.search(
			companyId, userId, assetTitle, taskNames, assetTypes,
			assetPrimaryKeys, assigneeClassName, assigneeUserIds, dueDateGT,
			dueDateLT, completed, searchByUserRoles, workflowDefinitionId,
			workflowInstanceIds, andOperator, start, end, orderByComparator);
	}

	public static int searchCount(
			long companyId, long userId, String assetTitle, String[] taskNames,
			String[] assetTypes, Long[] assetPrimaryKeys,
			String assigneeClassName, Long[] assigneeUserIds, Date dueDateGT,
			Date dueDateLT, Boolean completed, Boolean searchByUserRoles,
			Long workflowDefinitionId, Long[] workflowInstanceIds,
			Boolean andOperator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.searchCount(
			companyId, userId, assetTitle, taskNames, assetTypes,
			assetPrimaryKeys, assigneeClassName, assigneeUserIds, dueDateGT,
			dueDateLT, completed, searchByUserRoles, workflowDefinitionId,
			workflowInstanceIds, andOperator);
	}

	public static WorkflowModelSearchResult<WorkflowTask> searchWorkflowTasks(
			long companyId, long userId, String assetTitle, String[] taskNames,
			String[] assetTypes, Long[] assetPrimaryKeys,
			String assigneeClassName, Long[] assigneeUserIds, Date dueDateGT,
			Date dueDateLT, Boolean completed,
			Boolean searchByActivatedWorkflowHandlers,
			Boolean searchByUserRoles, Long workflowDefinitionId,
			Long[] workflowInstanceIds, Boolean andOperator, int start, int end,
			OrderByComparator<WorkflowTask> orderByComparator)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.searchWorkflowTasks(
			companyId, userId, assetTitle, taskNames, assetTypes,
			assetPrimaryKeys, assigneeClassName, assigneeUserIds, dueDateGT,
			dueDateLT, completed, searchByActivatedWorkflowHandlers,
			searchByUserRoles, workflowDefinitionId, workflowInstanceIds,
			andOperator, start, end, orderByComparator);
	}

	public static WorkflowTask updateDueDate(
			long companyId, long userId, long workflowTaskId, String comment,
			Date dueDate)
		throws WorkflowException {

		WorkflowTaskManager workflowTaskManager =
			_workflowTaskManagerSnapshot.get();

		return workflowTaskManager.updateDueDate(
			companyId, userId, workflowTaskId, comment, dueDate);
	}

	private static final Snapshot<WorkflowTaskManager>
		_workflowTaskManagerSnapshot = new Snapshot<>(
			WorkflowTaskManagerUtil.class, WorkflowTaskManager.class);

}