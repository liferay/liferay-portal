/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.internal.deployment;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.kaleo.KaleoWorkflowModelConverter;
import com.liferay.portal.workflow.kaleo.definition.Condition;
import com.liferay.portal.workflow.kaleo.definition.Definition;
import com.liferay.portal.workflow.kaleo.definition.Node;
import com.liferay.portal.workflow.kaleo.definition.State;
import com.liferay.portal.workflow.kaleo.definition.Task;
import com.liferay.portal.workflow.kaleo.definition.Transition;
import com.liferay.portal.workflow.kaleo.definition.deployment.WorkflowDeployer;
import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.service.KaleoConditionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTransitionLocalService;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = WorkflowDeployer.class)
public class DefaultWorkflowDeployer implements WorkflowDeployer {

	@Override
	public WorkflowDefinition deploy(
			String externalReferenceCode, String title, String name,
			String scope, boolean system, Definition definition,
			ServiceContext serviceContext)
		throws PortalException {

		_checkPermissions(serviceContext);

		KaleoDefinition kaleoDefinition = _addOrUpdateKaleoDefinition(
			externalReferenceCode, title, name, scope, system, definition,
			serviceContext);

		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.
				fetchLatestKaleoDefinitionVersion(
					kaleoDefinition.getCompanyId(), kaleoDefinition.getName());

		long kaleoDefinitionVersionId =
			kaleoDefinitionVersion.getKaleoDefinitionVersionId();

		Map<String, KaleoNode> kaleoNodesMap = new HashMap<>();

		for (Node node : definition.getNodes()) {
			KaleoNode kaleoNode = _kaleoNodeLocalService.addKaleoNode(
				kaleoDefinition.getKaleoDefinitionId(),
				kaleoDefinitionVersionId, node, serviceContext);

			kaleoNodesMap.put(node.getName(), kaleoNode);

			if (node instanceof Condition condition) {
				_kaleoConditionLocalService.addKaleoCondition(
					kaleoDefinition.getKaleoDefinitionId(),
					kaleoDefinitionVersionId, kaleoNode.getKaleoNodeId(),
					condition, serviceContext);
			}
			else if (node instanceof Task task) {
				_kaleoTaskLocalService.addKaleoTask(
					kaleoDefinition.getKaleoDefinitionId(),
					kaleoDefinitionVersionId, kaleoNode.getKaleoNodeId(), task,
					serviceContext);
			}
		}

		for (Node node : definition.getNodes()) {
			KaleoNode kaleoNode = kaleoNodesMap.get(node.getName());

			for (Transition transition : node.getOutgoingTransitionsList()) {
				Node sourceNode = transition.getSourceNode();

				KaleoNode sourceKaleoNode = kaleoNodesMap.get(
					sourceNode.getName());

				if (sourceKaleoNode == null) {
					throw new KaleoDefinitionValidationException.
						MustSetSourceNode(sourceNode.getDefaultLabel());
				}

				Node targetNode = transition.getTargetNode();

				KaleoNode targetKaleoNode = kaleoNodesMap.get(
					targetNode.getName());

				if (targetKaleoNode == null) {
					throw new KaleoDefinitionValidationException.
						MustSetTargetNode(targetNode.getDefaultLabel());
				}

				_kaleoTransitionLocalService.addKaleoTransition(
					kaleoNode.getKaleoDefinitionId(),
					kaleoNode.getKaleoDefinitionVersionId(),
					kaleoNode.getKaleoNodeId(), transition, sourceKaleoNode,
					targetKaleoNode, serviceContext);
			}
		}

		State initialState = definition.getInitialState();

		if (initialState == null) {
			throw new KaleoDefinitionValidationException.
				MustSetInitialStateNode();
		}

		KaleoNode kaleoNode = kaleoNodesMap.get(initialState.getName());

		_kaleoDefinitionLocalService.activateKaleoDefinition(
			kaleoDefinition.getKaleoDefinitionId(), kaleoDefinitionVersionId,
			kaleoNode.getKaleoNodeId(), serviceContext);

		kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.fetchKaleoDefinitionVersion(
				kaleoDefinitionVersionId);

		kaleoDefinitionVersion.getKaleoNodeKaleoActions(0L);
		kaleoDefinitionVersion.getKaleoNodeKaleoNotifications(0L);
		kaleoDefinitionVersion.getKaleoNodeKaleoTransitions(0L);

		return _kaleoWorkflowModelConverter.toWorkflowDefinition(
			_kaleoDefinitionLocalService.getKaleoDefinition(
				kaleoDefinition.getKaleoDefinitionId()));
	}

	@Override
	public WorkflowDefinition save(
			String externalReferenceCode, String title, String name,
			String scope, boolean system, Definition definition,
			ServiceContext serviceContext)
		throws PortalException {

		KaleoDefinition kaleoDefinition = _addOrUpdateKaleoDefinition(
			externalReferenceCode, title, name, scope, system, definition,
			serviceContext);

		return _kaleoWorkflowModelConverter.toWorkflowDefinition(
			kaleoDefinition);
	}

	private KaleoDefinition _addOrUpdateKaleoDefinition(
			String externalReferenceCode, String title, String name,
			String scope, boolean system, Definition definition,
			ServiceContext serviceContext)
		throws PortalException {

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.fetchKaleoDefinition(
				name, serviceContext);

		if (kaleoDefinition == null) {
			kaleoDefinition = _kaleoDefinitionService.addKaleoDefinition(
				externalReferenceCode, name, title, definition.getDescription(),
				definition.getContent(), scope, system, 1, serviceContext);
		}
		else {
			kaleoDefinition = _kaleoDefinitionService.updateKaleoDefinition(
				externalReferenceCode, kaleoDefinition.getKaleoDefinitionId(),
				title, definition.getDescription(), definition.getContent(),
				system, serviceContext);
		}

		return kaleoDefinition;
	}

	private void _checkPermissions(ServiceContext serviceContext)
		throws PrincipalException {

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if ((permissionChecker == null) ||
			!GetterUtil.getBoolean(
				serviceContext.getAttribute("checkPermission"), true)) {

			return;
		}

		_portletResourcePermission.check(
			permissionChecker, serviceContext.getScopeGroupId(),
			ActionKeys.ADD_DEFINITION);
	}

	@Reference
	private KaleoConditionLocalService _kaleoConditionLocalService;

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference
	private KaleoDefinitionService _kaleoDefinitionService;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Reference
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Reference
	private KaleoTransitionLocalService _kaleoTransitionLocalService;

	@Reference
	private KaleoWorkflowModelConverter _kaleoWorkflowModelConverter;

	@Reference(
		target = "(resource.name=" + WorkflowConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}