/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.kaleo.runtime.node;

import com.liferay.ai.hub.guardrail.ModelArmorHandler;
import com.liferay.ai.hub.internal.assistant.handler.AssistantHandlerContext;
import com.liferay.ai.hub.internal.assistant.handler.AssistantHandlerUtil;
import com.liferay.ai.hub.internal.langchain4j.observability.api.listener.AiServiceErrorListenerImpl;
import com.liferay.ai.hub.internal.langchain4j.observability.api.listener.InputGuardrailExecutedListenerImpl;
import com.liferay.ai.hub.internal.langchain4j.observability.api.listener.OutputGuardrailExecutedListenerImpl;
import com.liferay.ai.hub.internal.mcp.tool.provider.MCPToolProviderUtil;
import com.liferay.ai.hub.internal.model.GoogleGenAiUtil;
import com.liferay.ai.hub.internal.tool.WorkflowNodeTools;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.GuardrailsUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.KaleoNodeSettingUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.MessageUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.OnErrorConsumerUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.PromptUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.QuotaUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.RetrievalAugmentorUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.ToolsUtil;
import com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util.VariablesUtil;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
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
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.graph.PathElement;
import com.liferay.portal.workflow.kaleo.runtime.node.BaseNodeExecutor;
import com.liferay.portal.workflow.kaleo.runtime.node.NodeExecutor;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.invocation.InvocationParameters;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author João Victor Alves
 */
@Component(service = NodeExecutor.class)
public class AIDecisionNodeExecutor extends BaseNodeExecutor {

	@Override
	public NodeType getNodeType() {
		return NodeType.AI_DECISION;
	}

	@Activate
	protected void activate() {
		_noticeableExecutorService = _portalExecutorManager.getPortalExecutor(
			AIDecisionNodeExecutor.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_noticeableExecutorService.shutdown();
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

		ServiceContext serviceContext = executionContext.getServiceContext();

		Map<String, Serializable> workflowContext =
			executionContext.getWorkflowContext();

		if (QuotaUtil.hasExceededQuota(
				serviceContext.getCompanyId(), currentKaleoNode.getName(),
				_quotaManager, serviceContext.getUserId(), workflowContext,
				kaleoInstanceToken.getKaleoInstanceId())) {

			return;
		}

		Map<String, String> kaleoNodeSettingValues =
			KaleoNodeSettingUtil.getKaleoNodeSettingValuesMap(
				currentKaleoNode.getKaleoNodeId());

		String prompt = PromptUtil.composePrompt(
			kaleoInstanceToken.getCompanyId(), _dtoConverterRegistry,
			executionContext, kaleoNodeSettingValues, _objectEntryManager);
		String userMessage = VariablesUtil.applyInputVariables(
			executionContext, "userMessage", kaleoNodeSettingValues);

		String sseEventSinkKey = GetterUtil.getString(
			workflowContext.get("sseEventSinkKey"));

		List<InputGuardrail> inputGuardrails = new ArrayList<>();
		List<OutputGuardrail> outputGuardrails = new ArrayList<>();

		GuardrailsUtil.populate(
			_dtoConverterRegistry, inputGuardrails, _modelArmorHandler,
			_objectEntryManager, outputGuardrails, _quotaManager,
			serviceContext, workflowContext);

		Consumer<Throwable> onErrorConsumer = OnErrorConsumerUtil.create(
			sseEventSinkKey);

		AssistantHandlerUtil.handle(
			AssistantHandlerContext.builder(
			).aiServiceListeners(
				List.of(
					new AiServiceErrorListenerImpl(onErrorConsumer),
					new InputGuardrailExecutedListenerImpl(executionContext),
					new OutputGuardrailExecutedListenerImpl(executionContext))
			).googleGenAiStreamingChatModel(
				GoogleGenAiUtil.createGoogleGenAiStreamingChatModel(
					_noticeableExecutorService, _quotaManager, serviceContext)
			).inputGuardrails(
				inputGuardrails
			).invocationParameters(
				InvocationParameters.from(
					Map.of("executionContext", executionContext))
			).memoryId(
				GetterUtil.getString(workflowContext.get("memoryId"))
			).onCompleteResponseConsumer(
				chatResponse -> {
					MCPToolProviderUtil.close(sseEventSinkKey);

					MessageUtil.sendMessage(
						chatResponse, kaleoInstanceToken, prompt,
						executionContext.getServiceContext(), userMessage);
				}
			).onErrorConsumer(
				onErrorConsumer
			).outputGuardrails(
				outputGuardrails
			).retrievalAugmentor(
				RetrievalAugmentorUtil.createRetrievalAugmentor(
					kaleoInstanceToken.getCompanyId(), _dtoConverterRegistry,
					_fieldConfigBuilderFactory, _highlightBuilderFactory,
					kaleoNodeSettingValues, serviceContext.getLocale(),
					_objectEntryManager, _searchEngineAdapter,
					serviceContext.getUserId(), workflowContext,
					kaleoInstanceToken.getKaleoInstanceId())
			).systemMessageProviderFunction(
				memoryId -> prompt
			).tools(
				new WorkflowNodeTools(_workflowNodeManager)
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

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Reference
	private FieldConfigBuilderFactory _fieldConfigBuilderFactory;

	@Reference
	private HighlightBuilderFactory _highlightBuilderFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ModelArmorHandler _modelArmorHandler;

	private NoticeableExecutorService _noticeableExecutorService;

	@Reference(
		target = "(object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT + ")"
	)
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private QuotaManager _quotaManager;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private WorkflowNodeManager _workflowNodeManager;

}