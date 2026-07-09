/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.langchain4j.agentic.internal;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.internal.agent.util.AgentUtil;
import com.liferay.ai.hub.internal.audit.constants.AIHubEventTypes;
import com.liferay.ai.hub.internal.constants.AIHubDestinationNames;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.internal.InternalAgent;
import dev.langchain4j.agentic.observability.AgentListener;
import dev.langchain4j.agentic.planner.AgentArgument;
import dev.langchain4j.agentic.planner.AgentInstance;
import dev.langchain4j.agentic.planner.AgenticSystemTopology;
import dev.langchain4j.agentic.planner.Planner;
import dev.langchain4j.agentic.scope.AgenticScopeAccess;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Feliphe Marinho
 */
public class InternalAgentImpl implements InternalAgent, InvocationHandler {

	public InternalAgentImpl(
		AgentContext agentContext, QuotaManager quotaManager,
		WorkflowDefinitionManager workflowDefinitionManager,
		WorkflowInstanceManager workflowInstanceManager) {

		_agentContext = agentContext;
		_quotaManager = quotaManager;
		_workflowDefinitionManager = workflowDefinitionManager;
		_workflowInstanceManager = workflowInstanceManager;
	}

	@Override
	public String agentId() {
		return _name;
	}

	@Override
	public boolean allowStreamingOutput() {
		return false;
	}

	@Override
	public void appendId(String idSuffix) {
	}

	@Override
	public List<AgentArgument> arguments() {
		return _agentArguments;
	}

	@Override
	public boolean async() {
		return _async;
	}

	@Override
	public String description() {
		return _description;
	}

	public Object invoke(Map<String, ?> inputObjects) {
		boolean invoked = false;
		String permit = null;

		try {
			permit = _quotaManager.acquireAgentInstancePermit(
				_agentContext.getUserId());

			Company company = CompanyLocalServiceUtil.getCompany(
				_agentContext.getCompanyId());

			Map<String, Serializable> workflowContext =
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_SERVICE_CONTEXT,
					_agentContext.getServiceContext()
				).put(
					"agentDefinitionExternalReferenceCode", _name
				).put(
					"agentInstancePermit", permit
				).put(
					"instructionDefinitionScope",
					_agentContext.getInstructionDefinitionScope()
				).put(
					"memoryId", () -> _memoryId
				).put(
					"oAuth2ApplicationId",
					_agentContext.getOAuth2ApplicationId()
				).put(
					"outBoundEventName", () -> _outBoundEventName
				).put(
					"sseEventSinkKey", _agentContext.getSseEventSinkKey()
				).put(
					"userToken",
					() -> {
						if (_agentContext.getUserToken() == null) {
							return null;
						}

						return EncryptorUtil.encrypt(
							company.getKeyObj(), _agentContext.getUserToken());
					}
				).build();

			for (AgentArgument agentArgument : arguments()) {
				String name = agentArgument.name();

				if (workflowContext.containsKey(name)) {
					continue;
				}

				workflowContext.put(
					name, MapUtil.getString(inputObjects, name));
			}

			Map<String, ?> input = _agentContext.getInput();

			if (input != null) {
				for (String key : input.keySet()) {
					if (StringUtil.equals(key, "message") ||
						workflowContext.containsKey(key)) {

						continue;
					}

					workflowContext.put(key, MapUtil.getString(input, key));
				}
			}

			Message message = new Message();

			message.put(
				"additionalInformation",
				JSONFactoryUtil.createJSONObject(inputObjects));
			message.put("createDate", new Date());
			message.put(
				"eventType", AIHubEventTypes.AI_HUB_AGENT_INSTANCE_START);
			message.put("userId", _agentContext.getUserId());

			WorkflowDefinition workflowDefinition =
				_workflowDefinitionManager.liberalGetLatestWorkflowDefinition(
					_agentContext.getCompanyId(), _workflowDefinitionName);

			WorkflowInstance workflowInstance =
				_workflowInstanceManager.startWorkflowInstance(
					_agentContext.getCompanyId(), _agentContext.getGroupId(),
					_agentContext.getUserId(), _workflowDefinitionName,
					workflowDefinition.getVersion(), null, workflowContext);

			message.put(
				"workflowInstanceId", workflowInstance.getWorkflowInstanceId());

			MessageBusUtil.sendMessage(
				AIHubDestinationNames.AI_HUB_AGENT_INSTANCE, message);

			invoked = true;

			if (async()) {
				return workflowInstance.getWorkflowInstanceId();
			}

			return AgentUtil.getOutput(workflowInstance);
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			throw unsupportedOperationException;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
		finally {
			if (!invoked && (permit != null)) {
				_quotaManager.releaseAgentInstancePermit(permit);
			}
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] arguments)
		throws Throwable {

		Class<?> declaringClass = method.getDeclaringClass();

		if ((declaringClass == AgentInstance.class) ||
			(declaringClass == InternalAgent.class) ||
			(declaringClass == Object.class)) {

			return method.invoke(
				ProxyUtil.getInvocationHandler(proxy), arguments);
		}

		if (declaringClass == AgenticScopeAccess.class) {
			if (Objects.equals(method.getName(), "evictAgenticScope")) {
				return false;
			}

			return null;
		}

		return invoke((Map<String, ?>)arguments[0]);
	}

	@Override
	public AgentListener listener() {
		return null;
	}

	@Override
	public String name() {
		return _name;
	}

	@Override
	public String outputKey() {
		return _outputKey;
	}

	@Override
	public Type outputType() {
		return String.class;
	}

	@Override
	public AgentInstance parent() {
		return _internalAgent;
	}

	@Override
	public Class<? extends Planner> plannerType() {
		return null;
	}

	@Override
	public void registerInheritedParentListener(AgentListener agentListener) {
	}

	public void setAgentArguments(List<AgentArgument> agentArguments) {
		_agentArguments = agentArguments;
	}

	public void setAsync(boolean async) {
		_async = async;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setMemoryId(String memoryId) {
		_memoryId = memoryId;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOutBoundEventName(String outBoundEventName) {
		_outBoundEventName = outBoundEventName;
	}

	public void setOutputKey(String outputKey) {
		_outputKey = outputKey;
	}

	@Override
	public void setParent(InternalAgent internalAgent) {
		_internalAgent = internalAgent;
	}

	public void setWorkflowDefinitionName(String workflowDefinitionName) {
		_workflowDefinitionName = workflowDefinitionName;
	}

	@Override
	public List<AgentInstance> subagents() {
		return List.of();
	}

	@Override
	public AgenticSystemTopology topology() {
		return AgenticSystemTopology.AI_AGENT;
	}

	@Override
	public Class<?> type() {
		return UntypedAgent.class;
	}

	private List<AgentArgument> _agentArguments;
	private final AgentContext _agentContext;
	private boolean _async;
	private String _description;
	private InternalAgent _internalAgent;
	private String _memoryId;
	private String _name;
	private String _outBoundEventName;
	private String _outputKey;
	private final QuotaManager _quotaManager;
	private final WorkflowDefinitionManager _workflowDefinitionManager;
	private String _workflowDefinitionName;
	private final WorkflowInstanceManager _workflowInstanceManager;

}