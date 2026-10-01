/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * @author Micha Kiener
 * @author Shuyang Zhou
 * @author Brian Wing Shun Chan
 * @author Marcellus Tavares
 * @author Raymond Augé
 */
public class WorkflowInstanceManagerUtil {

	public static void deleteWorkflowInstance(
			long companyId, long workflowInstanceId)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		workflowInstanceManager.deleteWorkflowInstance(
			companyId, workflowInstanceId);
	}

	public static List<String> getNextTransitionNames(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getNextTransitionNames(
			companyId, userId, workflowInstanceId);
	}

	public static WorkflowInstance getWorkflowInstance(long workflowInstanceId)
		throws PortalException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstance(workflowInstanceId);
	}

	public static WorkflowInstance getWorkflowInstance(
			long companyId, long workflowInstanceId)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstance(
			companyId, workflowInstanceId);
	}

	public static WorkflowInstance getWorkflowInstance(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstance(
			companyId, userId, workflowInstanceId);
	}

	public static int getWorkflowInstanceCount(
			long companyId, Long userId, String assetClassName,
			Long assetClassPK, Boolean completed)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstanceCount(
			companyId, userId, assetClassName, assetClassPK, completed);
	}

	public static int getWorkflowInstanceCount(
			long companyId, Long userId, String[] assetClassNames,
			Boolean completed)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstanceCount(
			companyId, userId, assetClassNames, completed);
	}

	public static int getWorkflowInstanceCount(
			long companyId, String workflowDefinitionName,
			Integer workflowDefinitionVersion, Boolean completed)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstanceCount(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			completed);
	}

	public static List<WorkflowInstance> getWorkflowInstances(
			long companyId, Long userId, String assetClassName,
			Long assetClassPK, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstances(
			companyId, userId, assetClassName, assetClassPK, completed, start,
			end, orderByComparator);
	}

	public static List<WorkflowInstance> getWorkflowInstances(
			long companyId, Long userId, String[] assetClassNames,
			Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstances(
			companyId, userId, assetClassNames, completed, start, end,
			orderByComparator);
	}

	public static List<WorkflowInstance> getWorkflowInstances(
			long companyId, String workflowDefinitionName,
			Integer workflowDefinitionVersion, Boolean completed, int start,
			int end, OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.getWorkflowInstances(
			companyId, workflowDefinitionName, workflowDefinitionVersion,
			completed, start, end, orderByComparator);
	}

	public static List<WorkflowInstance> search(
			long companyId, Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.search(
			companyId, userId, active, assetClassName, assetTitle,
			assetDescription, nodeName, kaleoDefinitionName, completed, start,
			end, orderByComparator);
	}

	public static int searchCount(
			long companyId, Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.searchCount(
			companyId, userId, active, assetClassName, assetTitle,
			assetDescription, nodeName, kaleoDefinitionName, completed);
	}

	public static WorkflowModelSearchResult<WorkflowInstance>
			searchWorkflowInstances(
				long companyId, Long userId, Boolean active,
				String assetClassName, String assetTitle,
				String assetDescription, String nodeName,
				String kaleoDefinitionName, Boolean completed,
				boolean searchByActiveWorkflowHandlers, int start, int end,
				OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.searchWorkflowInstances(
			companyId, userId, active, assetClassName, assetTitle,
			assetDescription, nodeName, kaleoDefinitionName, completed,
			searchByActiveWorkflowHandlers, start, end, orderByComparator);
	}

	public static WorkflowInstance signalWorkflowInstance(
			long companyId, long userId, long workflowInstanceId,
			String transitionName, Map<String, Serializable> workflowContext)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.signalWorkflowInstance(
			companyId, userId, workflowInstanceId, transitionName,
			workflowContext);
	}

	public static WorkflowInstance signalWorkflowInstance(
			long companyId, long userId, long workflowInstanceId,
			String transitionName, Map<String, Serializable> workflowContext,
			boolean waitForCompletion)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.signalWorkflowInstance(
			companyId, userId, workflowInstanceId, transitionName,
			workflowContext, waitForCompletion);
	}

	public static WorkflowInstance startWorkflowInstance(
			long companyId, long groupId, long userId,
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.startWorkflowInstance(
			companyId, groupId, userId, workflowDefinitionName,
			workflowDefinitionVersion, transitionName, workflowContext);
	}

	public static WorkflowInstance startWorkflowInstance(
			long companyId, long groupId, long userId,
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			boolean waitForCompletion)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.startWorkflowInstance(
			companyId, groupId, userId, workflowDefinitionName,
			workflowDefinitionVersion, transitionName, workflowContext,
			waitForCompletion);
	}

	public static WorkflowInstance updateWorkflowContext(
			long companyId, long workflowInstanceId,
			Map<String, Serializable> workflowContext)
		throws WorkflowException {

		WorkflowInstanceManager workflowInstanceManager =
			_workflowInstanceManagerSnapshot.get();

		return workflowInstanceManager.updateWorkflowContext(
			companyId, workflowInstanceId, workflowContext);
	}

	private static final Snapshot<WorkflowInstanceManager>
		_workflowInstanceManagerSnapshot = new Snapshot<>(
			WorkflowInstanceManagerUtil.class, WorkflowInstanceManager.class);

}