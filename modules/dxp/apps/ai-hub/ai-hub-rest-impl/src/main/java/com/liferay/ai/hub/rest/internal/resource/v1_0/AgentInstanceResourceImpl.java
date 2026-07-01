/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.internal.resource.v1_0;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.agent.DefaultAgent;
import com.liferay.ai.hub.rest.dto.v1_0.AgentDefinition;
import com.liferay.ai.hub.rest.dto.v1_0.AgentInstance;
import com.liferay.ai.hub.rest.dto.v1_0.Variable;
import com.liferay.ai.hub.rest.internal.util.OAuth2ApplicationIdResolverUtil;
import com.liferay.ai.hub.rest.manager.v1_0.AgentDefinitionManager;
import com.liferay.ai.hub.rest.resource.v1_0.AgentInstanceResource;
import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Feliphe Marinho
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/agent-instance.properties",
	scope = ServiceScope.PROTOTYPE, service = AgentInstanceResource.class
)
public class AgentInstanceResourceImpl extends BaseAgentInstanceResourceImpl {

	@Override
	public void getAgentInstanceSubscribe(SseEventSink sseEventSink) {
		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		SseUtil.initialize(_sse, sseEventSink);
	}

	@Override
	public AgentInstance postAgentInstance(AgentInstance agentInstance)
		throws Exception {

		if (!FeatureFlagManagerUtil.isEnabled(
				contextCompany.getCompanyId(), "LPD-62272")) {

			throw new UnsupportedOperationException();
		}

		AgentDefinition agentDefinition =
			_agentDefinitionManager.getAgentDefinition(
				contextCompany.getCompanyId(),
				new DefaultDTOConverterContext(
					contextAcceptLanguage.isAcceptAllLanguages(), null,
					_dtoConverterRegistry, contextHttpServletRequest, null,
					contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
					contextUser),
				agentInstance.getAgentDefinitionExternalReferenceCode());

		AgentContext agentContext = AgentContext.builder(
		).agentDefinitionExternalReferenceCode(
			agentDefinition.getExternalReferenceCode()
		).asynchronous(
			GetterUtil.getBoolean(agentInstance.getAsynchronous(), true)
		).companyId(
			contextCompany.getCompanyId()
		).groupId(
			AccountEntryUtil.getUserAccountEntryGroupId(contextUser.getUserId())
		).input(
			agentInstance.getContext()
		).inputVariableNames(
			transformToList(
				agentDefinition.getInputVariables(), Variable::getName)
		).instructionDefinitionScope(
			agentInstance.getInstructionDefinitionScopeAsString()
		).oAuth2ApplicationId(
			OAuth2ApplicationIdResolverUtil.resolve(contextHttpServletRequest)
		).serviceContext(
			ServiceContextFactory.getInstance(contextHttpServletRequest)
		).sseEventSinkKey(
			agentInstance.getSseEventSinkKey()
		).userId(
			contextUser.getUserId()
		).userToken(
			contextHttpServletRequest.getHeader(
				"Liferay-AI-Hub-Cell-On-Behalf-Of")
		).workflowDefinitionName(
			agentDefinition.getWorkflowDefinitionName()
		).build();

		Object result = _defaultAgent.invoke(agentContext);

		return new AgentInstance() {
			{
				setAgentDefinitionExternalReferenceCode(
					agentDefinition::getExternalReferenceCode);
				setExternalReferenceCode(
					() -> {
						if (agentContext.isAsynchronous()) {
							return String.valueOf(result);
						}

						return null;
					});
				setOutput(
					() -> {
						if (agentContext.isAsynchronous()) {
							return null;
						}

						return String.valueOf(result);
					});
			}
		};
	}

	@Reference
	private AgentDefinitionManager _agentDefinitionManager;

	@Reference
	private DefaultAgent _defaultAgent;

	@Reference
	private DTOConverterRegistry _dtoConverterRegistry;

	@Context
	private Sse _sse;

}