/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.kaleo.runtime.node;

import com.liferay.ai.hub.internal.assistant.handler.AssistantHandlerContext;
import com.liferay.ai.hub.internal.assistant.handler.AssistantHandlerUtil;
import com.liferay.ai.hub.internal.mcp.tool.provider.MCPToolProviderUtil;
import com.liferay.ai.hub.internal.model.VertexAiGeminiUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.KaleoLogUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.PromptUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.QuotaUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.RetrievalAugmentorUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.ToolsUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.VariablesUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.highlight.FieldConfigBuilderFactory;
import com.liferay.portal.search.highlight.HighlightBuilderFactory;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.workflow.kaleo.definition.NodeType;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoNodeSetting;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.graph.PathElement;
import com.liferay.portal.workflow.kaleo.runtime.node.BaseNodeExecutor;
import com.liferay.portal.workflow.kaleo.runtime.node.NodeExecutor;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeSettingLocalService;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.model.vertexai.gemini.VertexAiGeminiStreamingChatModel;

import java.io.Serializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author João Victor Alves
 */
@Component(service = NodeExecutor.class)
public class AIDecisionNodeExecutor extends BaseNodeExecutor {

	@Override
	public NodeType getNodeType() {
		return NodeType.AI_DECISION;
	}

	public class Tools {

		@Tool(
			"Complete the workflow node by proceeding to the chosen transition"
		)
		public void completeWorkflowNode(
				InvocationParameters invocationParameters,
				@P(
					"A brief, one-sentence justification for the chosen transition."
				)
				String reason,
				@P("Transition name") String transitionName)
			throws PortalException {

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						invocationParameters.get("companyId"))) {

				PermissionThreadLocal.setPermissionChecker(
					invocationParameters.get("permissionChecker"));

				ExecutionContext executionContext = invocationParameters.get(
					"executionContext");

				KaleoInstanceToken kaleoInstanceToken =
					executionContext.getKaleoInstanceToken();

				Map<String, Serializable> workflowContext =
					executionContext.getWorkflowContext();

				workflowContext.put("reason", reason);

				_workflowNodeManager.completeWorkflowNode(
					kaleoInstanceToken.getCompanyId(),
					kaleoInstanceToken.getUserId(),
					kaleoInstanceToken.getKaleoInstanceTokenId(),
					transitionName, workflowContext, false);
			}
			finally {
				PermissionThreadLocal.setPermissionChecker(permissionChecker);
			}
		}

	}

	@Override
	protected boolean doEnter(
		KaleoNode currentKaleoNode, ExecutionContext executionContext) {

		return true;
	}

	@Override
	protected void doExecute(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			List<PathElement> remainingPathElements)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		Map<String, String> kaleoNodeSettingValues = new HashMap<>();

		List<KaleoNodeSetting> kaleoNodeSettings =
			_kaleoNodeSettingLocalService.getKaleoNodeSettings(
				currentKaleoNode.getKaleoNodeId());

		for (KaleoNodeSetting kaleoNodeSetting : kaleoNodeSettings) {
			kaleoNodeSettingValues.put(
				kaleoNodeSetting.getName(), kaleoNodeSetting.getValue());
		}

		String prompt = PromptUtil.composePrompt(
			kaleoInstanceToken.getCompanyId(), _dtoConverterRegistry,
			executionContext, kaleoNodeSettingValues, _objectEntryManager);
		String userMessage = VariablesUtil.applyInputVariables(
			executionContext, "userMessage", kaleoNodeSettingValues);

		ServiceContext serviceContext = executionContext.getServiceContext();

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		QuotaUtil.checkUsage(
			serviceContext.getCompanyId(), currentKaleoNode.getName(),
			prompt + "\n" + userMessage, workflowContext,
			kaleoInstanceToken.getKaleoInstanceId(),
			serviceContext.getUserId());

		VertexAiGeminiStreamingChatModel vertexAiGeminiStreamingChatModel =
			VertexAiGeminiUtil.createVertexAiGeminiStreamingChatModel(
				serviceContext.getCompanyId());

		String sseEventSinkKey = GetterUtil.getString(
			workflowContext.get("sseEventSinkKey"));

		AssistantHandlerUtil.handle(
			AssistantHandlerContext.builder(
			).invocationParameters(
				InvocationParameters.from(
					Map.of(
						"companyId", CompanyThreadLocal.getCompanyId(),
						"executionContext", executionContext,
						"permissionChecker",
						PermissionThreadLocal.getPermissionChecker()))
			).memoryId(
				GetterUtil.getString(workflowContext.get("memoryId"))
			).onCompleteResponseConsumer(
				response -> {
					MCPToolProviderUtil.close(sseEventSinkKey);

					vertexAiGeminiStreamingChatModel.close();

					KaleoLogUtil.addNodeUsageKaleoLog(
						response, kaleoInstanceToken,
						GetterUtil.getString(workflowContext.get("reason")),
						prompt, executionContext.getServiceContext(),
						userMessage);

					QuotaUtil.updateUsage(response, serviceContext);
				}
			).onErrorConsumer(
				throwable -> {
					MCPToolProviderUtil.close(sseEventSinkKey);

					vertexAiGeminiStreamingChatModel.close();

					_log.error(throwable);
				}
			).retrievalAugmentor(
				RetrievalAugmentorUtil.createRetrievalAugmentor(
					kaleoInstanceToken.getCompanyId(), _dtoConverterRegistry,
					_fieldConfigBuilderFactory, _highlightBuilderFactory,
					kaleoNodeSettingValues, serviceContext.getLocale(),
					_objectEntryManager, _searchEngineAdapter,
					serviceContext.getUserId(), workflowContext)
			).systemMessageProviderFunction(
				memoryId -> prompt
			).tools(
				new Tools()
			).toolProvider(
				MCPToolProviderUtil.create(
					kaleoInstanceToken.getCompanyId(), _dtoConverterRegistry,
					kaleoInstanceToken.getGroupId(), serviceContext.getLocale(),
					ToolsUtil.getMCPServerExternalReferenceCodes(
						_jsonFactory, kaleoNodeSettingValues),
					_objectEntryManager, sseEventSinkKey,
					serviceContext.getUserId(), workflowContext)
			).userMessage(
				userMessage
			).vertexAiGeminiStreamingChatModel(
				vertexAiGeminiStreamingChatModel
			).build());
	}

	@Override
	protected void doExit(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			List<PathElement> remainingPathElements)
		throws PortalException {

		KaleoTransition kaleoTransition = null;

		if (Validator.isNull(executionContext.getTransitionName())) {
			kaleoTransition = currentKaleoNode.getDefaultKaleoTransition();
		}
		else {
			kaleoTransition = currentKaleoNode.getKaleoTransition(
				executionContext.getTransitionName());
		}

		remainingPathElements.add(
			new PathElement(
				null, kaleoTransition.getTargetKaleoNode(),
				new ExecutionContext(
					executionContext.getKaleoInstanceToken(),
					executionContext.getWorkflowContext(),
					executionContext.getServiceContext())));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AIDecisionNodeExecutor.class);

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FieldConfigBuilderFactory _fieldConfigBuilderFactory;

	@Reference
	private HighlightBuilderFactory _highlightBuilderFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private KaleoNodeSettingLocalService _kaleoNodeSettingLocalService;

	@Reference(
		target = "(object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}