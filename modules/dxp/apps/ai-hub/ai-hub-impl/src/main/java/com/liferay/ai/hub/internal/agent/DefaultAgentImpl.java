/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.agent;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.agent.DefaultAgent;
import com.liferay.ai.hub.internal.langchain4j.agentic.internal.InternalAgentImpl;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import dev.langchain4j.agentic.planner.AgentArgument;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Feliphe Marinho
 */
@Component(service = DefaultAgent.class)
public class DefaultAgentImpl implements DefaultAgent {

	@Override
	public Object invoke(AgentContext agentContext) {
		InternalAgentImpl internalAgentImpl = new InternalAgentImpl(
			agentContext, _quotaManager, _workflowDefinitionManager,
			_workflowInstanceManager);

		internalAgentImpl.setAgentArguments(
			TransformUtil.transform(
				agentContext.getInputVariableNames(),
				inputVariableName -> new AgentArgument(
					String.class, inputVariableName)));
		internalAgentImpl.setAsync(agentContext.isAsynchronous());
		internalAgentImpl.setName(
			agentContext.getAgentDefinitionExternalReferenceCode());
		internalAgentImpl.setOutBoundEventName(
			agentContext.getAgentDefinitionExternalReferenceCode());
		internalAgentImpl.setWorkflowDefinitionName(
			agentContext.getWorkflowDefinitionName());

		return internalAgentImpl.invoke(agentContext.getInput());
	}

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private QuotaManager _quotaManager;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}