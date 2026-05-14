/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.metrics.integration.helper;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentInstanceLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.metrics.model.AddNodeRequest;
import com.liferay.portal.workflow.metrics.model.AddProcessRequest;
import com.liferay.portal.workflow.metrics.model.AddTaskRequest;
import com.liferay.portal.workflow.metrics.model.AddTransitionRequest;
import com.liferay.portal.workflow.metrics.model.Assignment;
import com.liferay.portal.workflow.metrics.model.DeleteProcessRequest;
import com.liferay.portal.workflow.metrics.model.DeleteTransitionRequest;
import com.liferay.portal.workflow.metrics.model.RoleAssignment;
import com.liferay.portal.workflow.metrics.model.UpdateProcessRequest;
import com.liferay.portal.workflow.metrics.model.UserAssignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = IndexerHelper.class)
public class IndexerHelperImpl implements IndexerHelper {

	@Override
	public AddNodeRequest createAddNodeRequest(
		KaleoDefinitionVersion kaleoDefinitionVersion, KaleoNode kaleoNode) {

		AddNodeRequest.Builder builder = new AddNodeRequest.Builder();

		return builder.companyId(
			kaleoNode.getCompanyId()
		).createDate(
			kaleoNode.getCreateDate()
		).initial(
			kaleoNode.isInitial()
		).modifiedDate(
			kaleoNode.getModifiedDate()
		).name(
			kaleoNode.getName()
		).nodeId(
			kaleoNode.getKaleoNodeId()
		).processId(
			kaleoNode.getKaleoDefinitionId()
		).processVersion(
			kaleoDefinitionVersion.getVersion()
		).terminal(
			kaleoNode.isTerminal()
		).type(
			kaleoNode.getType()
		).build();
	}

	@Override
	public AddNodeRequest createAddNodeRequest(
		KaleoDefinitionVersion kaleoDefinitionVersion, KaleoTask kaleoTask) {

		AddNodeRequest.Builder builder = new AddNodeRequest.Builder();

		return builder.companyId(
			kaleoTask.getCompanyId()
		).createDate(
			kaleoTask.getCreateDate()
		).initial(
			false
		).modifiedDate(
			kaleoTask.getModifiedDate()
		).name(
			kaleoTask.getName()
		).nodeId(
			kaleoTask.getKaleoTaskId()
		).processId(
			kaleoTask.getKaleoDefinitionId()
		).processVersion(
			kaleoDefinitionVersion.getVersion()
		).terminal(
			false
		).type(
			NodeType.TASK.name()
		).build();
	}

	@Override
	public AddProcessRequest createAddProcessRequest(
		long companyId, KaleoDefinition kaleoDefinition) {

		AddProcessRequest.Builder builder = new AddProcessRequest.Builder();

		builder.active(
			kaleoDefinition.isActive()
		).companyId(
			kaleoDefinition.getCompanyId()
		).createDate(
			kaleoDefinition.getCreateDate()
		).description(
			kaleoDefinition.getDescription()
		).modifiedDate(
			kaleoDefinition.getModifiedDate()
		).name(
			kaleoDefinition.getName()
		).processId(
			kaleoDefinition.getKaleoDefinitionId()
		).title(
			kaleoDefinition.getTitle(
				_localization.getDefaultLanguageId(kaleoDefinition.getTitle()))
		).titleMap(
			kaleoDefinition.getTitleMap()
		);

		String version = StringBundler.concat(
			kaleoDefinition.getVersion(), CharPool.PERIOD, 0);

		builder.version(version);

		try {
			List<KaleoDefinitionVersion> kaleoDefinitionVersions =
				_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersions(
					companyId, kaleoDefinition.getName());

			if (kaleoDefinitionVersions != null) {
				return builder.versions(
					TransformUtil.transformToArray(
						kaleoDefinitionVersions,
						KaleoDefinitionVersion::getVersion, String.class)
				).build();
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		return builder.versions(
			new String[] {version}
		).build();
	}

	@Override
	public AddTaskRequest createAddTaskRequest(
		KaleoInstance kaleoInstance,
		KaleoTaskInstanceToken kaleoTaskInstanceToken, String processVersion) {

		AddTaskRequest.Builder builder = new AddTaskRequest.Builder();

		builder.assetTitleMap(
			createAssetTitleLocalizationMap(
				kaleoTaskInstanceToken.getClassName(),
				kaleoTaskInstanceToken.getClassPK(),
				kaleoTaskInstanceToken.getGroupId())
		).assetTypeMap(
			createAssetTypeLocalizationMap(
				kaleoTaskInstanceToken.getClassName(),
				kaleoTaskInstanceToken.getGroupId())
		).assignments(
			() -> toAssignments(
				_kaleoTaskAssignmentInstanceLocalService.
					getKaleoTaskAssignmentInstances(
						kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId()))
		).className(
			kaleoTaskInstanceToken.getClassName()
		).classPK(
			kaleoTaskInstanceToken.getClassPK()
		).companyId(
			kaleoTaskInstanceToken.getCompanyId()
		).completed(
			kaleoTaskInstanceToken.isCompleted()
		).completionDate(
			kaleoTaskInstanceToken.getCompletionDate()
		).completionUserId(
			kaleoTaskInstanceToken.getCompletionUserId()
		).createDate(
			kaleoTaskInstanceToken.getCreateDate()
		);

		if (kaleoInstance != null) {
			builder.instanceCompleted(
				kaleoInstance.isCompleted()
			).instanceCompletionDate(
				kaleoInstance.getCompletionDate()
			);
		}

		return builder.instanceId(
			kaleoTaskInstanceToken.getKaleoInstanceId()
		).modifiedDate(
			kaleoTaskInstanceToken.getModifiedDate()
		).name(
			kaleoTaskInstanceToken.getKaleoTaskName()
		).nodeId(
			kaleoTaskInstanceToken.getKaleoTaskId()
		).processId(
			kaleoTaskInstanceToken.getKaleoDefinitionId()
		).processVersion(
			processVersion
		).taskId(
			kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId()
		).userId(
			kaleoTaskInstanceToken.getUserId()
		).build();
	}

	@Override
	public AddTransitionRequest createAddTransitionRequest(
			KaleoTransition kaleoTransition, String processVersion)
		throws PortalException {

		AddTransitionRequest.Builder builder =
			new AddTransitionRequest.Builder();

		return builder.companyId(
			kaleoTransition.getCompanyId()
		).createDate(
			kaleoTransition.getCreateDate()
		).modifiedDate(
			kaleoTransition.getModifiedDate()
		).name(
			kaleoTransition.getName()
		).nodeId(
			_getNodeId(kaleoTransition.getKaleoNodeId())
		).processId(
			kaleoTransition.getKaleoDefinitionId()
		).processVersion(
			processVersion
		).sourceNodeId(
			_getNodeId(kaleoTransition.getSourceKaleoNodeId())
		).sourceNodeName(
			kaleoTransition.getSourceKaleoNodeName()
		).targetNodeId(
			_getNodeId(kaleoTransition.getTargetKaleoNodeId())
		).targetNodeName(
			kaleoTransition.getTargetKaleoNodeName()
		).transitionId(
			kaleoTransition.getKaleoTransitionId()
		).userId(
			kaleoTransition.getUserId()
		).build();
	}

	@Override
	public Map<Locale, String> createAssetTitleLocalizationMap(
		String className, long classPK, long groupId) {

		AssetRenderer<?> assetRenderer = _getAssetRenderer(className, classPK);

		if (assetRenderer != null) {
			AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
				assetRenderer.getClassName(), assetRenderer.getClassPK());

			if (assetEntry != null) {
				return _localization.populateLocalizationMap(
					assetEntry.getTitleMap(), assetEntry.getDefaultLanguageId(),
					assetEntry.getGroupId());
			}
		}

		WorkflowHandler<?> workflowHandler =
			WorkflowHandlerRegistryUtil.getWorkflowHandler(className);

		if (workflowHandler != null) {
			Map<Locale, String> localizationMap = new HashMap<>();

			for (Locale availableLocale :
					_language.getAvailableLocales(groupId)) {

				localizationMap.put(
					availableLocale,
					workflowHandler.getTitle(classPK, availableLocale));
			}

			return localizationMap;
		}

		return Collections.emptyMap();
	}

	@Override
	public Map<Locale, String> createAssetTypeLocalizationMap(
		String className, long groupId) {

		Map<Locale, String> localizationMap = new HashMap<>();

		for (Locale availableLocale : _language.getAvailableLocales(groupId)) {
			localizationMap.put(
				availableLocale,
				ResourceActionsUtil.getModelResource(
					availableLocale, className));
		}

		return localizationMap;
	}

	@Override
	public DeleteProcessRequest createDeleteProcessRequest(
		KaleoDefinition kaleoDefinition) {

		DeleteProcessRequest.Builder builder =
			new DeleteProcessRequest.Builder();

		return builder.companyId(
			kaleoDefinition.getCompanyId()
		).processId(
			kaleoDefinition.getKaleoDefinitionId()
		).build();
	}

	@Override
	public DeleteTransitionRequest createDeleteTransitionRequest(
		KaleoTransition kaleoTransition) {

		DeleteTransitionRequest.Builder builder =
			new DeleteTransitionRequest.Builder();

		return builder.companyId(
			kaleoTransition.getCompanyId()
		).transitionId(
			kaleoTransition.getKaleoTransitionId()
		).build();
	}

	@Override
	public UpdateProcessRequest createUpdateProcessRequest(
		KaleoDefinition kaleoDefinition) {

		UpdateProcessRequest.Builder builder =
			new UpdateProcessRequest.Builder();

		return builder.active(
			kaleoDefinition.isActive()
		).companyId(
			kaleoDefinition.getCompanyId()
		).description(
			kaleoDefinition.getDescription()
		).modifiedDate(
			kaleoDefinition.getModifiedDate()
		).processId(
			kaleoDefinition.getKaleoDefinitionId()
		).title(
			kaleoDefinition.getTitle(
				_localization.getDefaultLanguageId(kaleoDefinition.getTitle()))
		).titleMap(
			kaleoDefinition.getTitleMap()
		).version(
			StringBundler.concat(
				kaleoDefinition.getVersion(), CharPool.PERIOD, 0)
		).build();
	}

	@Override
	public List<Assignment> toAssignments(
		List<KaleoTaskAssignmentInstance> kaleoTaskAssignmentInstances) {

		if (ListUtil.isEmpty(kaleoTaskAssignmentInstances)) {
			return Collections.emptyList();
		}

		KaleoTaskAssignmentInstance firstKaleoTaskAssignmentInstance =
			kaleoTaskAssignmentInstances.get(0);

		if (Objects.equals(
				firstKaleoTaskAssignmentInstance.getAssigneeClassName(),
				User.class.getName())) {

			User user = _userLocalService.fetchUser(
				firstKaleoTaskAssignmentInstance.getAssigneeClassPK());

			return Collections.singletonList(
				new UserAssignment(
					firstKaleoTaskAssignmentInstance.getAssigneeClassPK(),
					user.getFullName()));
		}

		Map<Long, List<Long>> assigneeClassPKGroupIds = new HashMap<>();

		for (KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance :
				kaleoTaskAssignmentInstances) {

			List<Long> groupIds = assigneeClassPKGroupIds.computeIfAbsent(
				kaleoTaskAssignmentInstance.getAssigneeClassPK(),
				key -> new ArrayList<>());

			groupIds.add(kaleoTaskAssignmentInstance.getGroupId());
		}

		return TransformUtil.transform(
			assigneeClassPKGroupIds.entrySet(),
			entry -> new RoleAssignment(entry.getKey(), entry.getValue()));
	}

	private AssetRenderer<?> _getAssetRenderer(String className, long classPK) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory != null) {
			try {
				return assetRendererFactory.getAssetRenderer(classPK);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}
		}

		return null;
	}

	private long _getNodeId(long kaleoNodeId) throws PortalException {
		KaleoNode kaleoNode = _kaleoNodeLocalService.fetchKaleoNode(
			kaleoNodeId);

		if ((kaleoNode == null) ||
			!Objects.equals(kaleoNode.getType(), NodeType.TASK.name())) {

			return kaleoNodeId;
		}

		KaleoTask kaleoTask = _kaleoTaskLocalService.getKaleoNodeKaleoTask(
			kaleoNode.getKaleoNodeId());

		return kaleoTask.getKaleoTaskId();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerHelperImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Reference
	private KaleoNodeLocalService _kaleoNodeLocalService;

	@Reference
	private KaleoTaskAssignmentInstanceLocalService
		_kaleoTaskAssignmentInstanceLocalService;

	@Reference
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private UserLocalService _userLocalService;

}