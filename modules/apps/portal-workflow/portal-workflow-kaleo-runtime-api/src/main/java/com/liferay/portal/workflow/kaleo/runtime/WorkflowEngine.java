/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowTransition;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;

import java.io.InputStream;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Michael C. Han
 * @author Marcellus Tavares
 */
@ProviderType
public interface WorkflowEngine {

	public void deleteWorkflowDefinition(
			String name, int version, ServiceContext serviceContext)
		throws WorkflowException;

	public void deleteWorkflowInstance(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException;

	public WorkflowDefinition deployWorkflowDefinition(
			String externalReferenceCode, String title, String name,
			String scope, boolean system, InputStream inputStream,
			ServiceContext serviceContext)
		throws WorkflowException;

	public ExecutionContext executeTimerWorkflowInstance(
			long kaleoTimerInstanceTokenId, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws WorkflowException;

	public List<String> getNextTransitionNames(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException;

	public List<WorkflowTransition> getNextWorkflowTransitions(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException;

	public WorkflowInstance getWorkflowInstance(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException;

	public int getWorkflowInstanceCount(
			Long userId, String assetClassName, Long assetClassPK,
			Boolean completed, ServiceContext serviceContext)
		throws WorkflowException;

	public int getWorkflowInstanceCount(
			Long userId, String[] assetClassNames, Boolean completed,
			ServiceContext serviceContext)
		throws WorkflowException;

	public int getWorkflowInstanceCount(
			String workflowDefinitionName, int workflowDefinitionVersion,
			boolean completed, ServiceContext serviceContext)
		throws WorkflowException;

	public List<WorkflowInstance> getWorkflowInstances(
			Long userId, String assetClassName, Long assetClassPK,
			Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException;

	public List<WorkflowInstance> getWorkflowInstances(
			Long userId, String[] assetClassNames, Boolean completed, int start,
			int end, OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException;

	public List<WorkflowInstance> getWorkflowInstances(
			String workflowDefinitionName, int workflowDefinitionVersion,
			boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException;

	public WorkflowDefinition saveWorkflowDefinition(
			String externalReferenceCode, String title, String name,
			String scope, boolean system, byte[] bytes,
			ServiceContext serviceContext)
		throws WorkflowException;

	public default List<WorkflowInstance> search(
			Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default int searchCount(
			Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed,
			ServiceContext serviceContext)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public default WorkflowModelSearchResult<WorkflowInstance>
			searchWorkflowInstances(
				Long userId, Boolean active, String assetClassName,
				String assetTitle, String assetDescription, String nodeName,
				String kaleoDefinitionName, Boolean completed,
				boolean searchByActiveWorkflowHandlers, int start, int end,
				OrderByComparator<WorkflowInstance> orderByComparator,
				ServiceContext serviceContext)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public WorkflowInstance signalWorkflowInstance(
			long workflowInstanceId, String transitionName,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws WorkflowException;

	public WorkflowInstance signalWorkflowInstance(
			long workflowInstanceId, String transitionName,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext, boolean waitForCompletion)
		throws WorkflowException;

	public WorkflowInstance startWorkflowInstance(
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws WorkflowException;

	public WorkflowInstance startWorkflowInstance(
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext, boolean waitForCompletion)
		throws WorkflowException;

	public WorkflowInstance updateContext(
			long workflowInstanceId, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws WorkflowException;

	public default WorkflowInstance updateWorkflowInstanceActive(
			long userId, long companyId, long workflowInstanceId,
			boolean active)
		throws WorkflowException {

		throw new UnsupportedOperationException();
	}

	public void validateWorkflowDefinition(InputStream inputStream)
		throws WorkflowException;

}