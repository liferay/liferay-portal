/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.workflow;

import com.liferay.portal.kernel.exception.PortalException;
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
 */
public interface WorkflowInstanceManager {

	public void deleteWorkflowInstance(long companyId, long workflowInstanceId)
		throws WorkflowException;

	public List<String> getNextTransitionNames(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException;

	public List<WorkflowTransition> getNextWorkflowTransitions(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException;

	public default WorkflowInstance getWorkflowInstance(long workflowInstanceId)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	public WorkflowInstance getWorkflowInstance(
			long companyId, long workflowInstanceId)
		throws WorkflowException;

	public default WorkflowInstance getWorkflowInstance(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public int getWorkflowInstanceCount(
			long companyId, Long userId, String assetClassName,
			Long assetClassPK, Boolean completed)
		throws WorkflowException;

	public int getWorkflowInstanceCount(
			long companyId, Long userId, String[] assetClassNames,
			Boolean completed)
		throws WorkflowException;

	public int getWorkflowInstanceCount(
			long companyId, String workflowDefinitionName,
			Integer workflowDefinitionVersion, Boolean completed)
		throws WorkflowException;

	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, Long userId, String assetClassName,
			Long assetClassPK, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException;

	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, Long userId, String[] assetClassNames,
			Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException;

	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, String workflowDefinitionName,
			Integer workflowDefinitionVersion, Boolean completed, int start,
			int end, OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException;

	public default List<WorkflowInstance> search(
			long companyId, Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default int searchCount(
			long companyId, Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowModelSearchResult<WorkflowInstance>
			searchWorkflowInstances(
				long companyId, Long userId, Boolean active,
				String assetClassName, String assetTitle,
				String assetDescription, String nodeName,
				String kaleoDefinitionName, Boolean completed,
				boolean searchByActiveWorkflowHandlers, int start, int end,
				OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public WorkflowInstance signalWorkflowInstance(
			long companyId, long userId, long workflowInstanceId,
			String transitionName, Map<String, Serializable> workflowContext)
		throws WorkflowException;

	public default WorkflowInstance signalWorkflowInstance(
			long companyId, long userId, long workflowInstanceId,
			String transitionName, Map<String, Serializable> workflowContext,
			boolean waitForCompletion)
		throws WorkflowException {

		if (waitForCompletion) {
			throw new UnsupportedOperationException();
		}

		return signalWorkflowInstance(
			companyId, userId, workflowInstanceId, transitionName,
			workflowContext);
	}

	public WorkflowInstance startWorkflowInstance(
			long companyId, long groupId, long userId,
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext)
		throws WorkflowException;

	public default WorkflowInstance startWorkflowInstance(
			long companyId, long groupId, long userId,
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			boolean waitForCompletion)
		throws WorkflowException {

		if (waitForCompletion) {
			throw new UnsupportedOperationException();
		}

		return startWorkflowInstance(
			companyId, groupId, userId, workflowDefinitionName,
			workflowDefinitionVersion, transitionName, workflowContext);
	}

	public default WorkflowInstance updateActive(
			long userId, long companyId, long workflowInstanceId,
			boolean active)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowInstance updateContext(
			long workflowInstanceId, Map<String, Serializable> workflowContext)
		throws PortalException {

		throw new UnsupportedOperationException();
	}

	public WorkflowInstance updateWorkflowContext(
			long companyId, long workflowInstanceId,
			Map<String, Serializable> workflowContext)
		throws WorkflowException;

}