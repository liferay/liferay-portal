/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.integration.internal;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.DefaultWorkflowTransition;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowTransition;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;
import com.liferay.portal.lock.service.LockLocalService;
import com.liferay.portal.workflow.kaleo.KaleoWorkflowModelConverter;
import com.liferay.portal.workflow.kaleo.exception.NoSuchInstanceException;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.WorkflowEngine;
import com.liferay.portal.workflow.kaleo.runtime.integration.internal.util.WorkflowLockUtil;
import com.liferay.portal.workflow.kaleo.runtime.util.comparator.KaleoInstanceOrderByComparator;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceService;

import java.io.Serializable;

import java.util.ArrayList;
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
	public void deleteWorkflowInstance(long workflowInstanceId)
		throws PortalException {

		_workflowEngine.deleteWorkflowInstance(workflowInstanceId);
	}

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

			KaleoInstance kaleoInstance =
				_kaleoInstanceLocalService.getKaleoInstance(workflowInstanceId);

			KaleoInstanceToken rootKaleoInstanceToken =
				kaleoInstance.getRootKaleoInstanceToken(null, serviceContext);

			List<WorkflowTransition> workflowTransitions = new ArrayList<>();

			_getNextWorkflowTransitions(
				rootKaleoInstanceToken, workflowTransitions);

			return workflowTransitions;
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

		return _kaleoWorkflowModelConverter.toWorkflowInstance(
			_kaleoInstanceService.getKaleoInstance(workflowInstanceId));
	}

	@Override
	public WorkflowInstance getWorkflowInstance(
			long companyId, long workflowInstanceId)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		return _getWorkflowInstance(workflowInstanceId, serviceContext);
	}

	@Override
	public WorkflowInstance getWorkflowInstance(
			long companyId, long userId, long workflowInstanceId)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);
		serviceContext.setUserId(userId);

		return _getWorkflowInstance(workflowInstanceId, serviceContext);
	}

	@Override
	public int getWorkflowInstanceCount(
			long companyId, Long userId, String assetClassName,
			Long assetClassPK, Boolean completed)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		try {
			return _kaleoInstanceLocalService.getKaleoInstancesCount(
				userId, assetClassName, assetClassPK, completed,
				serviceContext);
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public int getWorkflowInstanceCount(
			long companyId, Long userId, String[] assetClassNames,
			Boolean completed)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		try {
			return _kaleoInstanceLocalService.getKaleoInstancesCount(
				userId, assetClassNames, completed, serviceContext);
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public int getWorkflowInstanceCount(
			long companyId, String workflowDefinitionName,
			Integer workflowDefinitionVersion, Boolean completed)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		try {
			return _kaleoInstanceLocalService.getKaleoInstancesCount(
				workflowDefinitionName, workflowDefinitionVersion, completed,
				serviceContext);
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, Long userId, String assetClassName,
			Long assetClassPK, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		try {
			return _toWorkflowInstances(
				_kaleoInstanceLocalService.getKaleoInstances(
					userId, assetClassName, assetClassPK, completed, start, end,
					KaleoInstanceOrderByComparator.getOrderByComparator(
						orderByComparator, _kaleoWorkflowModelConverter,
						serviceContext),
					serviceContext));
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, Long userId, String[] assetClassNames,
			Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		try {
			return _toWorkflowInstances(
				_kaleoInstanceLocalService.getKaleoInstances(
					userId, assetClassNames, completed, start, end,
					KaleoInstanceOrderByComparator.getOrderByComparator(
						orderByComparator, _kaleoWorkflowModelConverter,
						serviceContext),
					serviceContext));
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public List<WorkflowInstance> getWorkflowInstances(
			long companyId, String workflowDefinitionName,
			Integer workflowDefinitionVersion, Boolean completed, int start,
			int end, OrderByComparator<WorkflowInstance> orderByComparator)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		try {
			return _toWorkflowInstances(
				_kaleoInstanceLocalService.getKaleoInstances(
					workflowDefinitionName, workflowDefinitionVersion,
					completed, start, end,
					KaleoInstanceOrderByComparator.getOrderByComparator(
						orderByComparator, _kaleoWorkflowModelConverter,
						serviceContext),
					serviceContext));
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public WorkflowInstance liberalStartWorkflowInstance(
			long companyId, long groupId, long userId,
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			boolean waitForCompletion)
		throws WorkflowException {

		return _startWorkflowInstance(
			companyId, groupId, userId, workflowDefinitionName,
			workflowDefinitionVersion, transitionName, workflowContext, true,
			waitForCompletion);
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

		try {
			WorkflowModelSearchResult<WorkflowInstance>
				workflowModelSearchResult = _searchWorkflowInstances(
					userId, active, assetClassName, assetTitle,
					assetDescription, nodeName, kaleoDefinitionName, completed,
					false, start, end, orderByComparator, serviceContext);

			return workflowModelSearchResult.getWorkflowModels();
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public int searchCount(
			long companyId, Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed)
		throws WorkflowException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setCompanyId(companyId);

		try {
			return _kaleoInstanceLocalService.searchCount(
				userId, active, assetClassName, assetTitle, assetDescription,
				nodeName, kaleoDefinitionName, completed, serviceContext);
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
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

		return _searchWorkflowInstances(
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

		return _startWorkflowInstance(
			companyId, groupId, userId, workflowDefinitionName,
			workflowDefinitionVersion, transitionName, workflowContext, false,
			waitForCompletion);
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

	private <T> T _get(
			boolean liberal,
			UnsafeSupplier<T, PortalException> localServiceUnsafeSupplier,
			UnsafeSupplier<T, PortalException> serviceUnsafeSupplier)
		throws PortalException {

		if (liberal) {
			return localServiceUnsafeSupplier.get();
		}

		return serviceUnsafeSupplier.get();
	}

	private void _getNextWorkflowTransitions(
			KaleoInstanceToken kaleoInstanceToken,
			List<WorkflowTransition> workflowTransitions)
		throws Exception {

		if (kaleoInstanceToken.hasIncompleteChildrenKaleoInstanceToken()) {
			List<KaleoInstanceToken> incompleteChildrenKaleoInstanceTokens =
				kaleoInstanceToken.getIncompleteChildrenKaleoInstanceTokens();

			for (KaleoInstanceToken incompleteChildrenKaleoInstanceToken :
					incompleteChildrenKaleoInstanceTokens) {

				_getNextWorkflowTransitions(
					incompleteChildrenKaleoInstanceToken, workflowTransitions);
			}
		}
		else {
			KaleoNode kaleoNode = kaleoInstanceToken.getCurrentKaleoNode();

			for (KaleoTransition kaleoTransition :
					kaleoNode.getKaleoTransitions()) {

				workflowTransitions.add(
					new DefaultWorkflowTransition() {
						{
							setLabelMap(kaleoTransition.getLabelMap());
							setName(kaleoTransition.getName());
							setSourceNodeName(
								kaleoTransition.getSourceKaleoNodeName());
							setTargetNodeName(
								kaleoTransition.getTargetKaleoNodeName());
						}
					});
			}
		}
	}

	private WorkflowInstance _getWorkflowInstance(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException {

		try {
			KaleoInstance kaleoInstance = null;

			if (serviceContext.getUserId() > 0) {
				kaleoInstance = _kaleoInstanceLocalService.fetchKaleoInstance(
					workflowInstanceId, serviceContext.getCompanyId(),
					serviceContext.getUserId());
			}
			else {
				kaleoInstance = _kaleoInstanceLocalService.getKaleoInstance(
					workflowInstanceId);

				if (kaleoInstance.getCompanyId() !=
						serviceContext.getCompanyId()) {

					throw new NoSuchInstanceException(
						"No KaleoInstance exists with the primary key " +
							workflowInstanceId);
				}
			}

			if (kaleoInstance != null) {
				return _kaleoWorkflowModelConverter.toWorkflowInstance(
					kaleoInstance);
			}
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}

		return null;
	}

	private WorkflowModelSearchResult<WorkflowInstance>
			_searchWorkflowInstances(
				Long userId, Boolean active, String assetClassName,
				String assetTitle, String assetDescription, String nodeName,
				String kaleoDefinitionName, Boolean completed,
				boolean searchByActiveWorkflowHandlers, int start, int end,
				OrderByComparator<WorkflowInstance> orderByComparator,
				ServiceContext serviceContext)
		throws WorkflowException {

		try {
			BaseModelSearchResult<KaleoInstance> baseModelSearchResult =
				_kaleoInstanceLocalService.searchKaleoInstances(
					userId, active, assetClassName, assetTitle,
					assetDescription, nodeName, kaleoDefinitionName, completed,
					searchByActiveWorkflowHandlers, start, end,
					KaleoInstanceOrderByComparator.getOrderByComparator(
						orderByComparator, _kaleoWorkflowModelConverter,
						serviceContext),
					serviceContext);

			return new WorkflowModelSearchResult<>(
				_toWorkflowInstances(baseModelSearchResult.getBaseModels()),
				baseModelSearchResult.getLength());
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	private WorkflowInstance _startWorkflowInstance(
			long companyId, long groupId, long userId,
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			boolean liberal, boolean waitForCompletion)
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

		try {
			return _get(
				liberal,
				() -> _workflowEngine.liberalStartWorkflowInstance(
					workflowDefinitionName, workflowDefinitionVersion,
					transitionName, workflowContext, serviceContext,
					waitForCompletion),
				() -> _workflowEngine.startWorkflowInstance(
					workflowDefinitionName, workflowDefinitionVersion,
					transitionName, workflowContext, serviceContext,
					waitForCompletion));
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (PortalException portalException) {
			throw new WorkflowException(portalException);
		}
	}

	private List<WorkflowInstance> _toWorkflowInstances(
		List<KaleoInstance> kaleoInstances) {

		return TransformUtil.transform(
			kaleoInstances,
			kaleoInstance -> _kaleoWorkflowModelConverter.toWorkflowInstance(
				kaleoInstance));
	}

	@Reference
	private KaleoInstanceLocalService _kaleoInstanceLocalService;

	@Reference
	private KaleoInstanceService _kaleoInstanceService;

	@Reference
	private KaleoWorkflowModelConverter _kaleoWorkflowModelConverter;

	@Reference
	private LockLocalService _lockLocalService;

	@Reference
	private WorkflowEngine _workflowEngine;

}