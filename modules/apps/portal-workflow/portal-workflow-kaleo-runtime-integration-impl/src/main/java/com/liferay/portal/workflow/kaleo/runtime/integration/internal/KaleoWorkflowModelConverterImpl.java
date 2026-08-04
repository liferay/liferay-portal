/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.integration.internal;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.DefaultWorkflowDefinition;
import com.liferay.portal.kernel.workflow.DefaultWorkflowInstance;
import com.liferay.portal.kernel.workflow.DefaultWorkflowLog;
import com.liferay.portal.kernel.workflow.DefaultWorkflowNode;
import com.liferay.portal.kernel.workflow.DefaultWorkflowNodeSetting;
import com.liferay.portal.kernel.workflow.DefaultWorkflowTask;
import com.liferay.portal.kernel.workflow.DefaultWorkflowTransition;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowLog;
import com.liferay.portal.kernel.workflow.WorkflowNode;
import com.liferay.portal.kernel.workflow.WorkflowNodeSetting;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskAssignee;
import com.liferay.portal.kernel.workflow.WorkflowTransition;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.kaleo.KaleoWorkflowModelConverter;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.definition.export.DefinitionExporter;
import com.liferay.portal.workflow.kaleo.definition.util.KaleoLogUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoLog;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoNodeSetting;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.integration.internal.util.LazyWorkflowTaskAssigneeList;
import com.liferay.portal.workflow.kaleo.runtime.integration.internal.util.WorkflowTaskAssigneesSupplier;
import com.liferay.portal.workflow.kaleo.runtime.util.WorkflowContextUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoInstanceTokenLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeSettingLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTransitionLocalService;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = KaleoWorkflowModelConverter.class)
public class KaleoWorkflowModelConverterImpl
	implements KaleoWorkflowModelConverter {

	@Override
	public List<WorkflowTaskAssignee> getWorkflowTaskAssignees(
		KaleoTaskInstanceToken kaleoTaskInstanceToken) {

		WorkflowTaskAssigneesSupplier workflowTaskAssigneesSupplier =
			new WorkflowTaskAssigneesSupplier(kaleoTaskInstanceToken);

		return workflowTaskAssigneesSupplier.get();
	}

	@Override
	public WorkflowDefinition toWorkflowDefinition(
		KaleoDefinition kaleoDefinition) {

		DefaultWorkflowDefinition defaultWorkflowDefinition =
			new DefaultWorkflowDefinition();

		defaultWorkflowDefinition.setActive(kaleoDefinition.isActive());
		defaultWorkflowDefinition.setCompanyId(kaleoDefinition.getCompanyId());

		String content = kaleoDefinition.getContent();

		if (Validator.isNull(content)) {
			try {
				content = _definitionExporter.export(
					kaleoDefinition.getKaleoDefinitionId());

				kaleoDefinition.setContent(content);

				kaleoDefinition =
					_kaleoDefinitionLocalService.updateKaleoDefinition(
						kaleoDefinition);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to export definition to string", exception);
				}
			}
		}

		defaultWorkflowDefinition.setContent(content);

		try {
			KaleoDefinitionVersion firstKaleoDefinitionVersion =
				_kaleoDefinitionVersionLocalService.
					getFirstKaleoDefinitionVersion(
						kaleoDefinition.getCompanyId(),
						kaleoDefinition.getName());

			defaultWorkflowDefinition.setCreateDate(
				firstKaleoDefinitionVersion.getCreateDate());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		defaultWorkflowDefinition.setContentAsXML(
			kaleoDefinition.getContentAsXML());
		defaultWorkflowDefinition.setDescription(
			kaleoDefinition.getDescription());
		defaultWorkflowDefinition.setExternalReferenceCode(
			kaleoDefinition.getExternalReferenceCode());

		if (kaleoDefinition.getGroupId() != 0) {
			Group group = _groupLocalService.fetchGroup(
				kaleoDefinition.getGroupId());

			defaultWorkflowDefinition.setGroupExternalReferenceCode(
				group.getExternalReferenceCode());
			defaultWorkflowDefinition.setGroupId(group.getGroupId());
		}

		defaultWorkflowDefinition.setModifiedDate(
			kaleoDefinition.getModifiedDate());
		defaultWorkflowDefinition.setName(kaleoDefinition.getName());
		defaultWorkflowDefinition.setScope(kaleoDefinition.getScope());
		defaultWorkflowDefinition.setSystem(kaleoDefinition.isSystem());
		defaultWorkflowDefinition.setTitle(kaleoDefinition.getTitle());
		defaultWorkflowDefinition.setUserId(kaleoDefinition.getUserId());
		defaultWorkflowDefinition.setVersion(kaleoDefinition.getVersion());
		defaultWorkflowDefinition.setWorkflowDefinitionId(
			kaleoDefinition.getKaleoDefinitionId());

		try {
			KaleoDefinitionVersion kaleoDefinitionVersion =
				_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
					kaleoDefinition.getCompanyId(), kaleoDefinition.getName(),
					kaleoDefinition.getVersion() + StringPool.PERIOD + 0);

			defaultWorkflowDefinition.setWorkflowNodes(
				_getWorkflowNodes(
					kaleoDefinitionVersion.getKaleoDefinitionVersionId()));
			defaultWorkflowDefinition.setWorkflowTransitions(
				_getWorkflowTransitions(
					kaleoDefinitionVersion.getKaleoDefinitionVersionId()));
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return defaultWorkflowDefinition;
	}

	@Override
	public WorkflowDefinition toWorkflowDefinition(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		DefaultWorkflowDefinition defaultWorkflowDefinition =
			new DefaultWorkflowDefinition();

		try {
			KaleoDefinition kaleoDefinition =
				kaleoDefinitionVersion.getKaleoDefinition();

			defaultWorkflowDefinition.setActive(kaleoDefinition.isActive());
			defaultWorkflowDefinition.setScope(kaleoDefinition.getScope());
			defaultWorkflowDefinition.setSystem(kaleoDefinition.isSystem());
			defaultWorkflowDefinition.setWorkflowDefinitionId(
				kaleoDefinition.getKaleoDefinitionId());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			defaultWorkflowDefinition.setActive(false);
			defaultWorkflowDefinition.setScope(
				WorkflowDefinitionConstants.SCOPE_ALL);
		}

		String content = kaleoDefinitionVersion.getContent();

		if (Validator.isNull(content)) {
			try {
				content = _definitionExporter.export(
					kaleoDefinitionVersion.getCompanyId(),
					kaleoDefinitionVersion.getName(),
					getVersion(kaleoDefinitionVersion.getVersion()));

				kaleoDefinitionVersion.setContent(content);

				kaleoDefinitionVersion =
					_kaleoDefinitionVersionLocalService.
						updateKaleoDefinitionVersion(kaleoDefinitionVersion);
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to export definition to string",
						portalException);
				}
			}
		}

		defaultWorkflowDefinition.setContent(content);

		defaultWorkflowDefinition.setContentAsXML(
			kaleoDefinitionVersion.getContentAsXML());
		defaultWorkflowDefinition.setCreateDate(
			kaleoDefinitionVersion.getCreateDate());
		defaultWorkflowDefinition.setDescription(
			kaleoDefinitionVersion.getDescription());

		if (kaleoDefinitionVersion.getGroupId() != 0) {
			Group group = _groupLocalService.fetchGroup(
				kaleoDefinitionVersion.getGroupId());

			defaultWorkflowDefinition.setGroupExternalReferenceCode(
				group.getExternalReferenceCode());
			defaultWorkflowDefinition.setGroupId(group.getGroupId());
		}

		defaultWorkflowDefinition.setModifiedDate(
			kaleoDefinitionVersion.getModifiedDate());
		defaultWorkflowDefinition.setName(kaleoDefinitionVersion.getName());
		defaultWorkflowDefinition.setTitle(kaleoDefinitionVersion.getTitle());
		defaultWorkflowDefinition.setUserId(kaleoDefinitionVersion.getUserId());
		defaultWorkflowDefinition.setVersion(
			getVersion(kaleoDefinitionVersion.getVersion()));
		defaultWorkflowDefinition.setWorkflowNodes(
			_getWorkflowNodes(
				kaleoDefinitionVersion.getKaleoDefinitionVersionId()));
		defaultWorkflowDefinition.setWorkflowTransitions(
			_getWorkflowTransitions(
				kaleoDefinitionVersion.getKaleoDefinitionVersionId()));

		return defaultWorkflowDefinition;
	}

	@Override
	public WorkflowInstance toWorkflowInstance(KaleoInstance kaleoInstance) {
		return toWorkflowInstance(kaleoInstance, null);
	}

	@Override
	public WorkflowInstance toWorkflowInstance(
		KaleoInstance kaleoInstance,
		Map<String, Serializable> workflowContext) {

		DefaultWorkflowInstance defaultWorkflowInstance =
			new DefaultWorkflowInstance();

		defaultWorkflowInstance.setActive(kaleoInstance.isActive());
		defaultWorkflowInstance.setCurrentWorkflowNodesSupplier(
			() -> TransformUtil.transform(
				_kaleoInstanceTokenLocalService.getKaleoInstanceTokens(
					kaleoInstance.getKaleoInstanceId()),
				kaleoInstanceToken -> {
					KaleoNode kaleoNode = _kaleoNodeLocalService.fetchKaleoNode(
						kaleoInstanceToken.getCurrentKaleoNodeId());

					if ((kaleoNode == null) ||
						Objects.equals(
							kaleoNode.getType(), NodeType.FORK.name())) {

						return null;
					}

					return _toWorkflowNode(kaleoNode);
				}));
		defaultWorkflowInstance.setEndDate(kaleoInstance.getCompletionDate());
		defaultWorkflowInstance.setGroupId(kaleoInstance.getGroupId());
		defaultWorkflowInstance.setStartDate(kaleoInstance.getCreateDate());

		if (workflowContext != null) {
			defaultWorkflowInstance.setWorkflowContext(workflowContext);
		}
		else {
			defaultWorkflowInstance.setWorkflowContext(
				WorkflowContextUtil.convert(
					kaleoInstance.getWorkflowContext()));
		}

		defaultWorkflowInstance.setWorkflowDefinitionName(
			kaleoInstance.getKaleoDefinitionName());
		defaultWorkflowInstance.setWorkflowDefinitionVersion(
			kaleoInstance.getKaleoDefinitionVersion());
		defaultWorkflowInstance.setWorkflowInstanceId(
			kaleoInstance.getKaleoInstanceId());

		return defaultWorkflowInstance;
	}

	@Override
	public WorkflowLog toWorkflowLog(KaleoLog kaleoLog) {
		DefaultWorkflowLog defaultWorkflowLog = new DefaultWorkflowLog();

		defaultWorkflowLog.setAuditUserId(kaleoLog.getUserId());
		defaultWorkflowLog.setComment(kaleoLog.getComment());
		defaultWorkflowLog.setCreateDate(kaleoLog.getCreateDate());
		defaultWorkflowLog.setPreviousWorkflowNode(
			_getWorkflowNode(kaleoLog.getPreviousKaleoNodeId()));

		long previousAssigneeClassPK = kaleoLog.getPreviousAssigneeClassPK();

		if (previousAssigneeClassPK > 0) {
			String previousAssigneeClassName =
				kaleoLog.getPreviousAssigneeClassName();

			if (previousAssigneeClassName.equals(Role.class.getName())) {
				defaultWorkflowLog.setPreviousRoleId(previousAssigneeClassPK);
			}
			else {
				defaultWorkflowLog.setPreviousUserId(previousAssigneeClassPK);
			}
		}

		long currentAssigneeClassPK = kaleoLog.getCurrentAssigneeClassPK();

		if (currentAssigneeClassPK > 0) {
			String currentAssigneeClassName =
				kaleoLog.getCurrentAssigneeClassName();

			if (currentAssigneeClassName.equals(Role.class.getName())) {
				defaultWorkflowLog.setRoleId(currentAssigneeClassPK);
			}
			else {
				defaultWorkflowLog.setUserId(currentAssigneeClassPK);
			}
		}

		defaultWorkflowLog.setCurrentWorkflowNode(
			_getWorkflowNode(kaleoLog.getKaleoClassPK()));
		defaultWorkflowLog.setType(KaleoLogUtil.convert(kaleoLog.getType()));
		defaultWorkflowLog.setWorkflowContext(kaleoLog.getWorkflowContext());
		defaultWorkflowLog.setWorkflowLogId(kaleoLog.getKaleoLogId());
		defaultWorkflowLog.setWorkflowTaskId(
			kaleoLog.getKaleoTaskInstanceTokenId());

		return defaultWorkflowLog;
	}

	@Override
	public WorkflowTask toWorkflowTask(
			KaleoTaskInstanceToken kaleoTaskInstanceToken,
			Map<String, Serializable> workflowContext)
		throws PortalException {

		DefaultWorkflowTask defaultWorkflowTask = new DefaultWorkflowTask();

		defaultWorkflowTask.setCreateDate(
			kaleoTaskInstanceToken.getCreateDate());
		defaultWorkflowTask.setCompletionDate(
			kaleoTaskInstanceToken.getCompletionDate());

		KaleoTask kaleoTask = kaleoTaskInstanceToken.getKaleoTask();

		defaultWorkflowTask.setDescription(kaleoTask.getDescription());

		defaultWorkflowTask.setDueDate(kaleoTaskInstanceToken.getDueDate());

		KaleoNode kaleoNode = kaleoTask.getKaleoNode();

		defaultWorkflowTask.setLabelMap(kaleoNode.getLabelMap());

		defaultWorkflowTask.setName(kaleoTask.getName());

		if (workflowContext != null) {
			defaultWorkflowTask.setOptionalAttributes(workflowContext);
		}
		else {
			defaultWorkflowTask.setOptionalAttributes(
				WorkflowContextUtil.convert(
					kaleoTaskInstanceToken.getWorkflowContext()));
		}

		defaultWorkflowTask.setUserName(kaleoTaskInstanceToken.getUserName());

		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				kaleoTaskInstanceToken.getKaleoDefinitionVersionId());

		KaleoDefinition kaleoDefinition =
			kaleoDefinitionVersion.getKaleoDefinition();

		defaultWorkflowTask.setWorkflowDefinitionId(
			kaleoDefinition.getKaleoDefinitionId());

		KaleoInstanceToken kaleoInstanceToken =
			kaleoTaskInstanceToken.getKaleoInstanceToken();

		KaleoInstance kaleoInstance = kaleoInstanceToken.getKaleoInstance();

		defaultWorkflowTask.setWorkflowDefinitionName(
			kaleoInstance.getKaleoDefinitionName());
		defaultWorkflowTask.setWorkflowDefinitionVersion(
			kaleoInstance.getKaleoDefinitionVersion());
		defaultWorkflowTask.setWorkflowInstanceId(
			kaleoInstance.getKaleoInstanceId());

		List<WorkflowTaskAssignee> workflowTaskAssignees =
			new LazyWorkflowTaskAssigneeList(
				kaleoTaskInstanceToken,
				_kaleoTaskAssignmentInstanceLocalService);

		defaultWorkflowTask.setWorkflowTaskAssignees(workflowTaskAssignees);

		defaultWorkflowTask.setWorkflowTaskId(
			kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId());

		return defaultWorkflowTask;
	}

	protected int getVersion(String version) {
		int[] versionParts = StringUtil.split(version, StringPool.PERIOD, 0);

		return versionParts[0];
	}

	private WorkflowNode _getWorkflowNode(long kaleoNodeId) {
		if (kaleoNodeId == 0) {
			return null;
		}

		try {
			return _toWorkflowNode(
				_kaleoNodeLocalService.getKaleoNode(kaleoNodeId));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		return null;
	}

	private List<WorkflowNode> _getWorkflowNodes(
		long kaleoDefinitionVersionId) {

		return TransformUtil.transform(
			_kaleoNodeLocalService.getKaleoDefinitionVersionKaleoNodes(
				kaleoDefinitionVersionId),
			this::_toWorkflowNode);
	}

	private List<WorkflowTransition> _getWorkflowTransitions(
		long kaleoDefinitionVersionId) {

		return TransformUtil.transform(
			_kaleoTransitionLocalService.
				getKaleoDefinitionVersionKaleoTransitions(
					kaleoDefinitionVersionId),
			kaleoTransition -> new DefaultWorkflowTransition() {
				{
					setLabelMap(kaleoTransition.getLabelMap());
					setName(kaleoTransition.getName());
					setSourceNodeName(kaleoTransition.getSourceKaleoNodeName());
					setTargetNodeName(kaleoTransition.getTargetKaleoNodeName());
				}
			});
	}

	private WorkflowNode _toWorkflowNode(KaleoNode kaleoNode) {
		DefaultWorkflowNode defaultWorkflowNode = new DefaultWorkflowNode();

		defaultWorkflowNode.setLabelMap(kaleoNode.getLabelMap());
		defaultWorkflowNode.setName(kaleoNode.getName());

		WorkflowNode.Type workflowNodeType = WorkflowNode.Type.valueOf(
			kaleoNode.getType());

		if (Objects.equals(workflowNodeType, WorkflowNode.Type.STATE)) {
			if (kaleoNode.isInitial()) {
				workflowNodeType = WorkflowNode.Type.INITIAL_STATE;
			}
			else if (kaleoNode.isTerminal()) {
				workflowNodeType = WorkflowNode.Type.TERMINAL_STATE;
			}
		}

		defaultWorkflowNode.setType(workflowNodeType);

		defaultWorkflowNode.setWorkflowNodeSettings(
			TransformUtil.transform(
				_kaleoNodeSettingLocalService.getKaleoNodeSettings(
					kaleoNode.getKaleoNodeId()),
				this::_toWorkflowNodeSetting));

		return defaultWorkflowNode;
	}

	private WorkflowNodeSetting _toWorkflowNodeSetting(
		KaleoNodeSetting kaleoNodeSetting) {

		DefaultWorkflowNodeSetting defaultWorkflowNodeSetting =
			new DefaultWorkflowNodeSetting();

		defaultWorkflowNodeSetting.setName(kaleoNodeSetting.getName());
		defaultWorkflowNodeSetting.setValue(kaleoNodeSetting.getValue());

		return defaultWorkflowNodeSetting;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoWorkflowModelConverterImpl.class);

	@Reference
	private DefinitionExporter _definitionExporter;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference
	private KaleoInstanceTokenLocalService _kaleoInstanceTokenLocalService;

	@Reference
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Reference
	private KaleoNodeSettingLocalService _kaleoNodeSettingLocalService;

	@Reference
	private KaleoTaskAssignmentInstanceLocalService
		_kaleoTaskAssignmentInstanceLocalService;

	@Reference
	private KaleoTransitionLocalService _kaleoTransitionLocalService;

}