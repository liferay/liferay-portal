/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.integration.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowTransition;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;
import com.liferay.portal.lock.service.LockLocalService;
import com.liferay.portal.workflow.kaleo.runtime.WorkflowEngine;
import com.liferay.portal.workflow.kaleo.runtime.integration.internal.util.WorkflowLockUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 * @author Marcellus Tavares
 */
@Component(service = WorkflowInstanceManager.class)
public class WorkflowInstanceManagerImpl implements WorkflowInstanceManager {

	@Override
	public void deleteWorkflowInstance(long companyId, long workflowInstanceId)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		_workflowEngine.deleteWorkflowInstance(
			workflowInstanceId, serviceContext);
	}

	@Override
	public List<String> getNextTransitionNames(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException {

		return TransformUtil.transform(
			getNextWorkflowTransitions(companyId, userId, workflowInstanceId),
			WorkflowTransition::getName);
	}

	@Override
	public List<WorkflowTransition> getNextWorkflowTransitions(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException {

		try {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(companyId);
			serviceContext.setUserId(userId);

			return _workflowEngine.getNextWorkflowTransitions(
				workflowInstanceId, serviceContext);
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public WorkflowInstance getWorkflowInstance(long workflowInstanceId)
		throws PortalException {

		return _workflowEngine.getWorkflowInstance(workflowInstanceId);
	}

	@Override
	public WorkflowInstance getWorkflowInstance(
			long companyId, long workflowInstanceId)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.getWorkflowInstance(
			workflowInstanceId, serviceContext);
	}

	@Override
	public WorkflowInstance getWorkflowInstance(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return _workflowEngine.getWorkflowInstance(
			workflowInstanceId, serviceContext);
	}

	@Override
	public int getWorkflowInstanceCount(
			long companyId, Long userId, String assetClassName,
			Long assetClassPK, Boolean completed)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.getWorkflowInstanceCount(
			userId, assetClassName, assetClassPK, completed, serviceContext);
	}

	@Override
	public int getWorkflowInstanceCount(
			long companyId, Long userId, String[] assetClassNames,
			Boolean completed)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.getWorkflowInstanceCount(
			userId, assetClassNames, completed, serviceContext);
	}

	@Override
	public int getWorkflowInstanceCount(
			long companyId, String workflowDefinitionName,
			Integer workflowDefinitionVersion, Boolean completed)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.getWorkflowInstanceCount(
			workflowDefinitionName, workflowDefinitionVersion, completed,
			serviceContext);
	}

	@Override
	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, Long userId, String assetClassName,
			Long assetClassPK, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.getWorkflowInstances(
			userId, assetClassName, assetClassPK, completed, start, end,
			orderByComparator, serviceContext);
	}

	@Override
	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, Long userId, String[] assetClassNames,
			Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.getWorkflowInstances(
			userId, assetClassNames, completed, start, end, orderByComparator,
			serviceContext);
	}

	@Override
	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, String workflowDefinitionName,
			Integer workflowDefinitionVersion, Boolean completed, int start,
			int end, OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.getWorkflowInstances(
			workflowDefinitionName, workflowDefinitionVersion, completed, start,
			end, orderByComparator, serviceContext);
	}

	@Override
	public List<WorkflowInstance> search(
			long companyId, Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.search(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed, start, end,
			orderByComparator, serviceContext);
	}

	@Override
	public int searchCount(
			long companyId, Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.searchCount(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed, serviceContext);
	}

	@Override
	public WorkflowModelSearchResult<WorkflowInstance> searchWorkflowInstances(
			long companyId, Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed,
			boolean searchByActiveWorkflowHandlers, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.searchWorkflowInstances(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed,
			searchByActiveWorkflowHandlers, start, end, orderByComparator,
			serviceContext);
	}

	@Override
	public WorkflowInstance signalWorkflowInstance(
			long companyId, long userId, long workflowInstanceId,
			String transitionName, Map<String, Serializable> workflowContext)
		throws WorkflowException {

		return signalWorkflowInstance(
			companyId, userId, workflowInstanceId, transitionName,
			workflowContext, false);
	}

	@Override
	public WorkflowInstance signalWorkflowInstance(
			long companyId, long userId, long workflowInstanceId,
			String transitionName, Map<String, Serializable> workflowContext,
			boolean waitForCompletion)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return _workflowEngine.signalWorkflowInstance(
			workflowInstanceId, transitionName, workflowContext, serviceContext,
			waitForCompletion);
	}

	@Override
	public WorkflowInstance startWorkflowInstance(
			long companyId, long groupId, long userId,
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext)
		throws WorkflowException {

		return startWorkflowInstance(
			companyId, groupId, userId, workflowDefinitionName,
			workflowDefinitionVersion, transitionName, workflowContext, false);
	}

	@Override
	public WorkflowInstance startWorkflowInstance(
			long companyId, long groupId, long userId,
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			boolean waitForCompletion)
		throws WorkflowException {

		String className = WorkflowDefinition.class.getName();
		String key = WorkflowLockUtil.encodeKey(
			workflowDefinitionName, workflowDefinitionVersion);

		if (_lockLocalService.isLocked(className, key)) {
			throw new WorkflowException(
				StringBundler.concat(
					"Workflow definition name ", workflowDefinitionName,
					" and version ", workflowDefinitionVersion,
					" is being undeployed"));
		}

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		serviceContext.setCompanyId(companyId);
		serviceContext.setScopeGroupId(groupId);
		serviceContext.setUserId(userId);

		return _workflowEngine.startWorkflowInstance(
			workflowDefinitionName, workflowDefinitionVersion, transitionName,
			workflowContext, serviceContext, waitForCompletion);
	}

	@Override
	public WorkflowInstance updateActive(
			long userId, long companyId, long workflowInstanceId,
			boolean active)
		throws WorkflowException {

		return _workflowEngine.updateWorkflowInstanceActive(
			userId, companyId, workflowInstanceId, active);
	}

	@Override
	public WorkflowInstance updateContext(
			long workflowInstanceId, Map<String, Serializable> workflowContext)
		throws PortalException {

		return _workflowEngine.updateContext(
			workflowInstanceId, workflowContext);
	}

	@Override
	public WorkflowInstance updateWorkflowContext(
			long companyId, long workflowInstanceId,
			Map<String, Serializable> workflowContext)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _workflowEngine.updateContext(
			workflowInstanceId, workflowContext, serviceContext);
	}

	@Reference
	private LockLocalService _lockLocalService;

	@Reference
	private WorkflowEngine _workflowEngine;

}