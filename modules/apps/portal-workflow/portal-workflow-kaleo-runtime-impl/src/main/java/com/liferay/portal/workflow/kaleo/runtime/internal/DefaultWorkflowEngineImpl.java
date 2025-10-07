/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.kernel.workflow.DefaultWorkflowTransition;
import com.liferay.portal.kernel.workflow.RequiredWorkflowDefinitionException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowDefinitionFileException;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowTransition;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;
import com.liferay.portal.workflow.kaleo.KaleoWorkflowModelConverter;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.ExecutionType;
import com.liferay.portal.workflow.kaleo.definition.deployment.WorkflowDeployer;
import com.liferay.portal.workflow.kaleo.definition.parser.WorkflowModelParser;
import com.liferay.portal.workflow.kaleo.definition.parser.WorkflowValidator;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.KaleoSignaler;
import com.liferay.portal.workflow.kaleo.runtime.WorkflowEngine;
import com.liferay.portal.workflow.kaleo.runtime.action.KaleoActionExecutor;
import com.liferay.portal.workflow.kaleo.runtime.assignment.AggregateKaleoTaskAssignmentSelector;
import com.liferay.portal.workflow.kaleo.runtime.notification.NotificationHelper;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.kaleo.runtime.util.comparator.KaleoInstanceOrderByComparator;
import com.liferay.portal.workflow.kaleo.service.KaleoLogLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskInstanceTokenLocalService;

import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = AopService.class)
@CTAware
@Transactional(
	isolation = Isolation.PORTAL, propagation = Propagation.REQUIRED,
	rollbackFor = Exception.class
)
public class DefaultWorkflowEngineImpl
	extends BaseKaleoBean implements AopService, WorkflowEngine {

	@Override
	public void deleteWorkflowDefinition(
			String name, int version, ServiceContext serviceContext)
		throws WorkflowException {

		try {
			KaleoDefinition kaleoDefinition =
				kaleoDefinitionLocalService.fetchKaleoDefinition(
					name, serviceContext);

			if (kaleoDefinition != null) {
				List<WorkflowDefinitionLink> workflowDefinitionLinks =
					workflowDefinitionLinkLocalService.
						getWorkflowDefinitionLinks(
							serviceContext.getCompanyId(),
							kaleoDefinition.getName(),
							kaleoDefinition.getVersion());

				if (ListUtil.isNotEmpty(workflowDefinitionLinks)) {
					throw new RequiredWorkflowDefinitionException(
						workflowDefinitionLinks);
				}

				kaleoDefinitionLocalService.deleteKaleoDefinition(
					name, serviceContext);
			}
			else {
				kaleoDefinitionVersionLocalService.
					deleteKaleoDefinitionVersions(
						serviceContext.getCompanyId(), name);
			}
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public void deleteWorkflowInstance(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException {

		try {
			kaleoInstanceLocalService.deleteKaleoInstance(workflowInstanceId);
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public WorkflowDefinition deployWorkflowDefinition(
			String externalReferenceCode, String title, String name,
			String scope, InputStream inputStream,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			Definition definition = _workflowModelParser.parse(inputStream);

			if (_workflowValidator != null) {
				_workflowValidator.validate(definition);
			}

			String definitionName = _getDefinitionName(definition, name);

			KaleoDefinition kaleoDefinition =
				kaleoDefinitionLocalService.fetchKaleoDefinition(
					definitionName, serviceContext);

			WorkflowDefinition workflowDefinition = _workflowDeployer.deploy(
				externalReferenceCode, title, definitionName, scope, definition,
				serviceContext);

			_updateWorkflowDefinitionLinks(
				serviceContext.getCompanyId(), kaleoDefinition,
				workflowDefinition);

			return workflowDefinition;
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (PortalException portalException) {
			throw new WorkflowException(portalException);
		}
	}

	@Override
	public ExecutionContext executeTimerWorkflowInstance(
			long kaleoTimerInstanceTokenId, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws WorkflowException {

		String name = PrincipalThreadLocal.getName();
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			KaleoTimerInstanceToken kaleoTimerInstanceToken =
				kaleoTimerInstanceTokenLocalService.getKaleoTimerInstanceToken(
					kaleoTimerInstanceTokenId);

			KaleoInstanceToken kaleoInstanceToken =
				kaleoTimerInstanceToken.getKaleoInstanceToken();

			ExecutionContext executionContext = new ExecutionContext(
				kaleoInstanceToken, kaleoTimerInstanceToken, workflowContext,
				serviceContext);

			if (PrincipalThreadLocal.getUserId() == 0) {
				PrincipalThreadLocal.setName(serviceContext.getUserId());
			}

			if (permissionChecker == null) {
				PermissionThreadLocal.setPermissionChecker(
					_defaultPermissionCheckerFactory.create(
						_userLocalService.getUser(serviceContext.getUserId())));
			}

			executionContext.setKaleoTaskInstanceToken(
				kaleoTimerInstanceToken.getKaleoTaskInstanceToken());

			_executeTimer(executionContext);

			kaleoTimerInstanceToken =
				kaleoTimerInstanceTokenLocalService.getKaleoTimerInstanceToken(
					kaleoTimerInstanceTokenId);

			if (!kaleoTimerInstanceToken.isCompleted()) {
				kaleoTimerInstanceToken.setWorkflowContext(
					WorkflowContextUtil.convert(
						executionContext.getWorkflowContext()));

				kaleoTimerInstanceTokenLocalService.
					updateKaleoTimerInstanceToken(kaleoTimerInstanceToken);
			}

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					_kaleoSignaler.signalExecute(
						kaleoInstanceToken.getCurrentKaleoNode(),
						executionContext);

					return null;
				});

			return executionContext;
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
		finally {
			PrincipalThreadLocal.setName(name);

			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
	}

	@Override
	public List<String> getNextTransitionNames(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException {

		return TransformUtil.transform(
			getNextWorkflowTransitions(workflowInstanceId, serviceContext),
			WorkflowTransition::getName);
	}

	@Override
	public List<WorkflowTransition> getNextWorkflowTransitions(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException {

		try {
			KaleoInstance kaleoInstance =
				kaleoInstanceLocalService.getKaleoInstance(workflowInstanceId);

			KaleoInstanceToken rootKaleoInstanceToken =
				kaleoInstance.getRootKaleoInstanceToken(null, serviceContext);

			List<WorkflowTransition> workflowTransitions = new ArrayList<>();

			getNextWorkflowTransitions(
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
	public WorkflowInstance getWorkflowInstance(
			long workflowInstanceId, ServiceContext serviceContext)
		throws WorkflowException {

		try {
			KaleoInstance kaleoInstance = null;

			if (serviceContext.getUserId() > 0) {
				kaleoInstance = kaleoInstanceLocalService.fetchKaleoInstance(
					workflowInstanceId, serviceContext.getCompanyId(),
					serviceContext.getUserId());
			}
			else {
				kaleoInstance = kaleoInstanceLocalService.getKaleoInstance(
					workflowInstanceId);
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

	@Override
	public int getWorkflowInstanceCount(
			Long userId, String assetClassName, Long assetClassPK,
			Boolean completed, ServiceContext serviceContext)
		throws WorkflowException {

		try {
			return kaleoInstanceLocalService.getKaleoInstancesCount(
				userId, assetClassName, assetClassPK, completed,
				serviceContext);
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public int getWorkflowInstanceCount(
			Long userId, String[] assetClassNames, Boolean completed,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			return kaleoInstanceLocalService.getKaleoInstancesCount(
				userId, assetClassNames, completed, serviceContext);
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public int getWorkflowInstanceCount(
			String workflowDefinitionName, int workflowDefinitionVersion,
			boolean completed, ServiceContext serviceContext)
		throws WorkflowException {

		try {
			return kaleoInstanceLocalService.getKaleoInstancesCount(
				workflowDefinitionName, workflowDefinitionVersion, completed,
				serviceContext);
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public List<WorkflowInstance> getWorkflowInstances(
			Long userId, String assetClassName, Long assetClassPK,
			Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			return _toWorkflowInstances(
				kaleoInstanceLocalService.getKaleoInstances(
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
			Long userId, String[] assetClassNames, Boolean completed, int start,
			int end, OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			return _toWorkflowInstances(
				kaleoInstanceLocalService.getKaleoInstances(
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
			String workflowDefinitionName, int workflowDefinitionVersion,
			boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			return _toWorkflowInstances(
				kaleoInstanceLocalService.getKaleoInstances(
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
	public WorkflowDefinition saveWorkflowDefinition(
			String externalReferenceCode, String title, String name,
			String scope, byte[] bytes, ServiceContext serviceContext)
		throws WorkflowException {

		try {
			Definition definition = _getDefinition(bytes);

			String definitionName = _getDefinitionName(
				definition, name, serviceContext);

			KaleoDefinition kaleoDefinition =
				kaleoDefinitionLocalService.fetchKaleoDefinition(
					definitionName, serviceContext);

			WorkflowDefinition workflowDefinition = _workflowDeployer.save(
				externalReferenceCode, title, definitionName, scope, definition,
				serviceContext);

			_updateWorkflowDefinitionLinks(
				serviceContext.getCompanyId(), kaleoDefinition,
				workflowDefinition);

			return workflowDefinition;
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public List<WorkflowInstance> search(
			Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			WorkflowModelSearchResult<WorkflowInstance>
				workflowModelSearchResult = searchWorkflowInstances(
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
			Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			return kaleoInstanceLocalService.searchCount(
				userId, active, assetClassName, assetTitle, assetDescription,
				nodeName, kaleoDefinitionName, completed, serviceContext);
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public WorkflowModelSearchResult<WorkflowInstance> searchWorkflowInstances(
			Long userId, Boolean active, String assetClassName,
			String assetTitle, String assetDescription, String nodeName,
			String kaleoDefinitionName, Boolean completed,
			boolean searchByActiveWorkflowHandlers, int start, int end,
			OrderByComparator<WorkflowInstance> orderByComparator,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			BaseModelSearchResult<KaleoInstance> baseModelSearchResult =
				kaleoInstanceLocalService.searchKaleoInstances(
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

	@Override
	public WorkflowInstance signalWorkflowInstance(
			long workflowInstanceId, String transitionName,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws WorkflowException {

		return signalWorkflowInstance(
			workflowInstanceId, transitionName, workflowContext, serviceContext,
			false);
	}

	@Override
	public WorkflowInstance signalWorkflowInstance(
			long workflowInstanceId, String transitionName,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext, boolean waitForCompletion)
		throws WorkflowException {

		try {
			KaleoInstance kaleoInstance = _updateContext(
				workflowInstanceId, workflowContext);

			KaleoInstanceToken kaleoInstanceToken =
				kaleoInstance.getRootKaleoInstanceToken(serviceContext);

			if (Validator.isNotNull(transitionName)) {

				// Validate that the transition actually exists before moving
				// forward

				KaleoNode currentKaleoNode =
					kaleoInstanceToken.getCurrentKaleoNode();

				currentKaleoNode.getKaleoTransition(transitionName);
			}

			serviceContext.setScopeGroupId(kaleoInstanceToken.getGroupId());

			ExecutionContext executionContext = new ExecutionContext(
				kaleoInstanceToken, workflowContext, serviceContext);

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					try {
						_kaleoSignaler.signalExit(
							transitionName, executionContext,
							waitForCompletion);
					}
					catch (Exception exception) {
						throw new WorkflowException(
							"Unable to signal next transition", exception);
					}

					return null;
				});

			return _kaleoWorkflowModelConverter.toWorkflowInstance(
				kaleoInstance, workflowContext);
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public WorkflowInstance startWorkflowInstance(
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws WorkflowException {

		return startWorkflowInstance(
			workflowDefinitionName, workflowDefinitionVersion, transitionName,
			workflowContext, serviceContext, false);
	}

	@Override
	public WorkflowInstance startWorkflowInstance(
			String workflowDefinitionName, Integer workflowDefinitionVersion,
			String transitionName, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext, boolean waitForCompletion)
		throws WorkflowException {

		try {
			KaleoDefinition kaleoDefinition =
				kaleoDefinitionLocalService.getKaleoDefinition(
					workflowDefinitionName, serviceContext);

			if (!kaleoDefinition.isActive()) {
				throw new WorkflowException(
					StringBundler.concat(
						"Inactive workflow definition with name ",
						workflowDefinitionName, " and version ",
						workflowDefinitionVersion));
			}

			KaleoDefinitionVersion kaleoDefinitionVersion =
				kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
					serviceContext.getCompanyId(), workflowDefinitionName,
					_getVersion(workflowDefinitionVersion));

			KaleoNode kaleoStartNode =
				kaleoDefinitionVersion.getKaleoStartNode();

			if (Validator.isNotNull(transitionName)) {

				// Validate that the transition actually exists before moving
				// forward

				kaleoStartNode.getKaleoTransition(transitionName);
			}

			long scopeGroupId = serviceContext.getScopeGroupId();

			if (scopeGroupId != WorkflowConstants.DEFAULT_GROUP_ID) {
				Group group = _groupLocalService.getGroup(scopeGroupId);

				if (group.isLayout()) {
					group = _groupLocalService.getGroup(
						group.getParentGroupId());

					serviceContext.setScopeGroupId(group.getGroupId());
				}
			}

			KaleoInstance kaleoInstance =
				kaleoInstanceLocalService.addKaleoInstance(
					kaleoDefinition.getKaleoDefinitionId(),
					kaleoDefinitionVersion.getKaleoDefinitionVersionId(),
					kaleoDefinitionVersion.getName(),
					_getVersion(kaleoDefinitionVersion.getVersion()),
					workflowContext, serviceContext);

			KaleoInstanceToken rootKaleoInstanceToken =
				kaleoInstance.getRootKaleoInstanceToken(
					workflowContext, serviceContext);

			rootKaleoInstanceToken.setCurrentKaleoNode(kaleoStartNode);

			kaleoLogLocalService.addInstanceStartKaleoLog(
				rootKaleoInstanceToken, serviceContext);

			ExecutionContext executionContext = new ExecutionContext(
				rootKaleoInstanceToken, workflowContext, serviceContext);

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					try {
						_kaleoSignaler.signalEntry(
							transitionName, executionContext,
							waitForCompletion);
					}
					catch (Exception exception) {
						throw new WorkflowException(
							"Unable to start workflow", exception);
					}

					return null;
				});

			return _kaleoWorkflowModelConverter.toWorkflowInstance(
				kaleoInstance, workflowContext);
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public WorkflowInstance updateContext(
			long workflowInstanceId, Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws WorkflowException {

		try {
			KaleoInstance kaleoInstance = _updateContext(
				workflowInstanceId, workflowContext);

			return _kaleoWorkflowModelConverter.toWorkflowInstance(
				kaleoInstance);
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	@Override
	public WorkflowInstance updateWorkflowInstanceActive(
			long userId, long companyId, long workflowInstanceId,
			boolean active)
		throws WorkflowException {

		try {
			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setCompanyId(companyId);
			serviceContext.setUserId(userId);

			KaleoInstance kaleoInstance =
				kaleoInstanceLocalService.updateActive(
					userId, workflowInstanceId, active);

			return _kaleoWorkflowModelConverter.toWorkflowInstance(
				kaleoInstance);
		}
		catch (PortalException portalException) {
			throw new WorkflowException(portalException);
		}
	}

	@Override
	public void validateWorkflowDefinition(InputStream inputStream)
		throws WorkflowException {

		try {
			if (_workflowValidator != null) {
				Definition definition = _workflowModelParser.parse(inputStream);

				_workflowValidator.validate(definition);
			}
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		catch (Exception exception) {
			throw new WorkflowException(exception);
		}
	}

	protected void getNextWorkflowTransitions(
			KaleoInstanceToken kaleoInstanceToken,
			List<WorkflowTransition> workflowTransitions)
		throws Exception {

		if (kaleoInstanceToken.hasIncompleteChildrenKaleoInstanceToken()) {
			List<KaleoInstanceToken> incompleteChildrenKaleoInstanceTokens =
				kaleoInstanceToken.getIncompleteChildrenKaleoInstanceTokens();

			for (KaleoInstanceToken incompleteChildrenKaleoInstanceToken :
					incompleteChildrenKaleoInstanceTokens) {

				getNextWorkflowTransitions(
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

	private void _executeTimer(ExecutionContext executionContext)
		throws PortalException {

		KaleoTimerInstanceToken kaleoTimerInstanceToken =
			executionContext.getKaleoTimerInstanceToken();

		KaleoTimer kaleoTimer = kaleoTimerInstanceToken.getKaleoTimer();

		_kaleoActionExecutor.executeKaleoActions(
			KaleoTimer.class.getName(), kaleoTimer.getKaleoTimerId(),
			ExecutionType.ON_TIMER, executionContext);

		List<KaleoTaskAssignment> kaleoTaskReassignments =
			kaleoTimer.getKaleoTaskReassignments();

		if (ListUtil.isNotEmpty(kaleoTaskReassignments)) {
			_reassignKaleoTask(kaleoTaskReassignments, executionContext);
		}

		_notificationHelper.sendKaleoNotifications(
			KaleoTimer.class.getName(), kaleoTimer.getKaleoTimerId(),
			ExecutionType.ON_TIMER, executionContext);

		if (!kaleoTimer.isRecurring()) {
			kaleoTimerInstanceTokenLocalService.completeKaleoTimerInstanceToken(
				kaleoTimerInstanceToken.getKaleoTimerInstanceTokenId(),
				executionContext.getServiceContext());
		}
	}

	private Definition _getDefinition(byte[] bytes) throws WorkflowException {
		try {
			_workflowModelParser.setValidate(false);

			return _workflowModelParser.parse(
				new UnsyncByteArrayInputStream(bytes));
		}
		catch (WorkflowDefinitionFileException
					workflowDefinitionFileException) {

			if (_log.isDebugEnabled()) {
				_log.debug(workflowDefinitionFileException);
			}

			try {
				return new Definition(
					StringPool.BLANK, StringPool.BLANK,
					new String(bytes, "UTF-8"), 0);
			}
			catch (UnsupportedEncodingException unsupportedEncodingException) {
				throw new WorkflowException(unsupportedEncodingException);
			}
		}
		catch (WorkflowException workflowException) {
			throw workflowException;
		}
		finally {
			_workflowModelParser.setValidate(true);
		}
	}

	private String _getDefinitionName(Definition definition, String name) {
		if (Validator.isNotNull(name)) {
			return name;
		}

		if (Validator.isNotNull(definition.getName())) {
			return definition.getName();
		}

		return PortalUUIDUtil.generate();
	}

	private String _getDefinitionName(
		Definition definition, String name, ServiceContext serviceContext) {

		if (Validator.isNotNull(name)) {
			return name;
		}

		if (Validator.isNotNull(definition.getName())) {
			KaleoDefinition kaleoDefinition =
				kaleoDefinitionLocalService.fetchKaleoDefinition(
					definition.getName(), serviceContext);

			if ((kaleoDefinition != null) && kaleoDefinition.isActive()) {
				return PortalUUIDUtil.generate();
			}

			return definition.getName();
		}

		return PortalUUIDUtil.generate();
	}

	private String _getVersion(int version) {
		return version + StringPool.PERIOD + 0;
	}

	private int _getVersion(String version) {
		int[] versionParts = StringUtil.split(version, StringPool.PERIOD, 0);

		return versionParts[0];
	}

	private void _reassignKaleoTask(
			List<KaleoTaskAssignment> kaleoTaskAssignments,
			ExecutionContext executionContext)
		throws PortalException {

		KaleoTaskInstanceToken kaleoTaskInstanceToken =
			executionContext.getKaleoTaskInstanceToken();

		List<KaleoTaskAssignmentInstance> previousTaskAssignmentInstances =
			kaleoTaskInstanceToken.getKaleoTaskAssignmentInstances();

		kaleoTaskInstanceToken =
			_kaleoTaskInstanceTokenLocalService.assignKaleoTaskInstanceToken(
				kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId(),
				_aggregateKaleoTaskAssignmentSelector.getKaleoTaskAssignments(
					kaleoTaskAssignments, executionContext),
				executionContext.getWorkflowContext(),
				executionContext.getServiceContext());

		_kaleoLogLocalService.addTaskAssignmentKaleoLogs(
			previousTaskAssignmentInstances, kaleoTaskInstanceToken, null,
			executionContext.getWorkflowContext(),
			executionContext.getServiceContext());
	}

	private List<WorkflowInstance> _toWorkflowInstances(
		List<KaleoInstance> kaleoInstances) {

		return TransformUtil.transform(
			kaleoInstances,
			kaleoInstance -> _kaleoWorkflowModelConverter.toWorkflowInstance(
				kaleoInstance));
	}

	private KaleoInstance _updateContext(
			long workflowInstanceId, Map<String, Serializable> workflowContext)
		throws Exception {

		return kaleoInstanceLocalService.updateKaleoInstance(
			workflowInstanceId, workflowContext);
	}

	private void _updateWorkflowDefinitionLinks(
			long companyId, KaleoDefinition kaleoDefinition,
			WorkflowDefinition workflowDefinition)
		throws PortalException {

		if (kaleoDefinition == null) {
			return;
		}

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			workflowDefinitionLinkLocalService.getWorkflowDefinitionLinks(
				companyId, kaleoDefinition.getName(),
				kaleoDefinition.getVersion());

		for (WorkflowDefinitionLink workflowDefinitionLink :
				workflowDefinitionLinks) {

			workflowDefinitionLink.setWorkflowDefinitionVersion(
				workflowDefinition.getVersion());

			workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				workflowDefinitionLink);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultWorkflowEngineImpl.class);

	@Reference
	private AggregateKaleoTaskAssignmentSelector
		_aggregateKaleoTaskAssignmentSelector;

	@Reference
	private PermissionCheckerFactory _defaultPermissionCheckerFactory;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KaleoActionExecutor _kaleoActionExecutor;

	@Reference
	private KaleoLogLocalService _kaleoLogLocalService;

	@Reference
	private KaleoSignaler _kaleoSignaler;

	@Reference
	private KaleoTaskInstanceTokenLocalService
		_kaleoTaskInstanceTokenLocalService;

	@Reference
	private KaleoWorkflowModelConverter _kaleoWorkflowModelConverter;

	@Reference
	private NotificationHelper _notificationHelper;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowDeployer _workflowDeployer;

	@Reference
	private WorkflowModelParser _workflowModelParser;

	@Reference
	private WorkflowValidator _workflowValidator;

}