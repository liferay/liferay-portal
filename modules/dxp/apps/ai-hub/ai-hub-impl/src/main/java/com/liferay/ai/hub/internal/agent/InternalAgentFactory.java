/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.agent;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.internal.langchain4j.agentic.internal.InternalAgentImpl;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.internal.InternalAgent;
import dev.langchain4j.agentic.planner.AgentArgument;

/**
 * @author Feliphe Marinho
 */
public class InternalAgentFactory {

	public InternalAgentFactory(
		AgentContext agentContext, QuotaManager quotaManager,
		WorkflowDefinitionManager workflowDefinitionManager,
		WorkflowInstanceManager workflowInstanceManager) {

		_agentContext = agentContext;
		_quotaManager = quotaManager;
		_workflowDefinitionManager = workflowDefinitionManager;
		_workflowInstanceManager = workflowInstanceManager;
	}

	public InternalAgent create(ObjectEntry objectEntry) {
		InternalAgentImpl internalAgentImpl = new InternalAgentImpl(
			_agentContext, _quotaManager, _workflowDefinitionManager,
			_workflowInstanceManager);

		internalAgentImpl.setAgentArguments(
			TransformUtil.transformToList(
				StringUtil.split(
					GetterUtil.getString(
						objectEntry.getPropertyValue("inputVariables"))),
				inputVariable -> new AgentArgument(
					String.class, inputVariable)));
		internalAgentImpl.setDescription(
			GetterUtil.getString(objectEntry.getPropertyValue("description")));
		internalAgentImpl.setMemoryId(_agentContext.getSseEventSinkKey());
		internalAgentImpl.setName(
			GetterUtil.getString(
				objectEntry.getPropertyValue("externalReferenceCode")));
		internalAgentImpl.setOutputKey(
			GetterUtil.getString(
				objectEntry.getPropertyValue("outputVariable")));
		internalAgentImpl.setWorkflowDefinitionName(
			GetterUtil.getString(
				objectEntry.getPropertyValue("workflowDefinitionName")));

		return (InternalAgent)ProxyUtil.newProxyInstance(
			UntypedAgent.class.getClassLoader(),
			new Class<?>[] {InternalAgent.class, UntypedAgent.class},
			internalAgentImpl);
	}

	private final AgentContext _agentContext;
	private final QuotaManager _quotaManager;
	private final WorkflowDefinitionManager _workflowDefinitionManager;
	private final WorkflowInstanceManager _workflowInstanceManager;

}