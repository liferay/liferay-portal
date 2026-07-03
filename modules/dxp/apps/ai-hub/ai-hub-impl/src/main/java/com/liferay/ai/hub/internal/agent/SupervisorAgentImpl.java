/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.agent;

import com.liferay.ai.hub.agent.AgentContext;
import com.liferay.ai.hub.agent.SupervisorAgent;
import com.liferay.ai.hub.internal.exception.ContentInjectorException;
import com.liferay.ai.hub.internal.langchain4j.model.chat.GoogleGenAiChatModelWrapper;
import com.liferay.ai.hub.internal.memory.ChatMemoryProviderUtil;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.ai.hub.rest.resource.v1_0.util.SseUtil;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.internal.InternalAgent;
import dev.langchain4j.agentic.scope.AgentInvocation;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorContextStrategy;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;

import java.lang.reflect.InvocationTargetException;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Feliphe Marinho
 * @author João Victor Alves
 */
@Component(service = SupervisorAgent.class)
public class SupervisorAgentImpl implements SupervisorAgent {

	@Override
	public void invoke(AgentContext agentContext) {
		InternalAgent[] internalAgents = _createInternalAgents(agentContext);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_noticeableExecutorService.submit(
			new CompanyInheritableThreadLocalCallable<>(
				() -> {
					PermissionChecker originalPermissionChecker =
						PermissionThreadLocal.getPermissionChecker();

					try {
						PermissionThreadLocal.setPermissionChecker(
							permissionChecker);

						_invoke(
							agentContext,
							new GoogleGenAiChatModelWrapper(
								_quotaManager,
								agentContext.getServiceContext()),
							internalAgents);
					}
					catch (Exception exception) {
						_handleException(agentContext, exception);
					}
					finally {
						PermissionThreadLocal.setPermissionChecker(
							originalPermissionChecker);
					}

					return null;
				}));
	}

	@Activate
	protected void activate() {
		_noticeableExecutorService = _portalExecutorManager.getPortalExecutor(
			SupervisorAgentImpl.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_noticeableExecutorService.shutdown();
	}

	private InternalAgent[] _createInternalAgents(AgentContext agentContext) {
		try {
			Page<ObjectEntry> page = _objectEntryManager.getObjectEntries(
				agentContext.getCompanyId(),
				_objectDefinitionLocalService.getObjectDefinition(
					agentContext.getCompanyId(), "AIHubAgentDefinition"),
				null, null, agentContext.getDTOConverterContext(),
				_getFilterString(agentContext), Pagination.of(1, 20), null,
				null);

			InternalAgentFactory internalAgentFactory =
				new InternalAgentFactory(
					agentContext, _quotaManager, _workflowDefinitionManager,
					_workflowInstanceManager);

			return TransformUtil.transformToArray(
				page.getItems(), internalAgentFactory::create,
				InternalAgent.class);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return new InternalAgent[0];
	}

	private String _getFilterString(AgentContext agentContext)
		throws Exception {

		if (Validator.isNull(agentContext.getChatbotExternalReferenceCode())) {
			return "(active eq true)";
		}

		NestedFieldsContext nestedFieldsContext =
			NestedFieldsContextThreadLocal.getAndSetNestedFieldsContext(
				new NestedFieldsContext(
					1, List.of("agentDefinitionsToChatbots")));

		try {
			ObjectEntry chatbotObjectEntry = _objectEntryManager.getObjectEntry(
				agentContext.getCompanyId(),
				agentContext.getDTOConverterContext(),
				agentContext.getChatbotExternalReferenceCode(),
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						"L_AI_HUB_CHATBOT", agentContext.getCompanyId()),
				null);

			ObjectEntry[] agentDefinitionObjectEntries =
				(ObjectEntry[])chatbotObjectEntry.getPropertyValue(
					"agentDefinitionsToChatbots");

			if (ArrayUtil.isEmpty(agentDefinitionObjectEntries)) {
				return "(active eq true)";
			}

			String agentDefinitionIds = StringUtil.merge(
				TransformUtil.transformToArray(
					List.of(agentDefinitionObjectEntries),
					objectEntry -> "'" + objectEntry.getId() + "'",
					String.class),
				",");

			return "(active eq true) and (id in (" + agentDefinitionIds + "))";
		}
		finally {
			NestedFieldsContextThreadLocal.setNestedFieldsContext(
				nestedFieldsContext);
		}
	}

	private void _handleException(
		AgentContext agentContext, Exception exception) {

		_log.error(exception);

		DTOConverterContext dtoConverterContext =
			agentContext.getDTOConverterContext();

		if (exception instanceof UnsupportedOperationException) {
			SseUtil.send(
				_language.get(
					dtoConverterContext.getLocale(),
					"you-have-exceeded-your-quota"),
				"Chat Message Sent", null, agentContext.getSseEventSinkKey());

			return;
		}

		if (!(exception.getCause() instanceof
				InvocationTargetException invocationTargetException)) {

			SseUtil.send(
				_language.get(
					dtoConverterContext.getLocale(),
					"i-cannot-fulfill-this-request"),
				"Chat Message Sent", null, agentContext.getSseEventSinkKey());

			return;
		}

		if ((invocationTargetException.getCause() instanceof
				RuntimeException runtimeException) &&
			(runtimeException.getCause() instanceof
				ContentInjectorException contentInjectorException)) {

			SseUtil.send(
				_language.get(
					dtoConverterContext.getLocale(),
					contentInjectorException.getMessageKey()),
				"Chat Message Sent", null, agentContext.getSseEventSinkKey());
		}

		if (invocationTargetException.getCause() instanceof
				UnsupportedOperationException) {

			SseUtil.send(
				_language.get(
					dtoConverterContext.getLocale(),
					"you-have-exceeded-your-quota"),
				"Chat Message Sent", null, agentContext.getSseEventSinkKey());
		}
	}

	private void _invoke(
			AgentContext agentContext, ChatModel chatModel,
			InternalAgent[] internalAgents)
		throws PortalException {

		_quotaManager.checkTokensUsage(
			agentContext.getCompanyId(), agentContext.getUserId());

		String[] agentDefinitionExternalReferenceCodes = null;

		dev.langchain4j.agentic.supervisor.SupervisorAgent supervisorAgent =
			AgenticServices.supervisorBuilder(
			).chatMemoryProvider(
				memoryId -> ChatMemoryProviderUtil.provide(
					agentContext.getSseEventSinkKey())
			).chatModel(
				chatModel
			).contextGenerationStrategy(
				SupervisorContextStrategy.CHAT_MEMORY_AND_SUMMARIZATION
			).maxAgentsInvocations(
				5
			).subAgents(
				(Object[])internalAgents
			).responseStrategy(
				SupervisorResponseStrategy.SCORED
			).build();

		ResultWithAgenticScope<String> resultWithAgenticScope =
			supervisorAgent.invokeWithAgenticScope(
				MapUtil.getString(agentContext.getInput(), "message"));

		AgenticScope agenticScope = resultWithAgenticScope.agenticScope();

		if ((agenticScope != null) &&
			(agenticScope.agentInvocations() != null)) {

			agentDefinitionExternalReferenceCodes = ArrayUtil.distinct(
				TransformUtil.transformToArray(
					agenticScope.agentInvocations(), AgentInvocation::agentName,
					String.class));
		}

		String data = resultWithAgenticScope.result();

		if (Validator.isBlank(data)) {
			DTOConverterContext dtoConverterContext =
				agentContext.getDTOConverterContext();

			data = _language.get(
				dtoConverterContext.getLocale(),
				"i-cannot-fulfill-this-request");
		}

		SseUtil.send(
			agentDefinitionExternalReferenceCodes, data, "Chat Message Sent",
			null, agentContext.getSseEventSinkKey());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SupervisorAgentImpl.class);

	@Reference
	private Language _language;

	private NoticeableExecutorService _noticeableExecutorService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference(target = "(object.entry.manager.storage.type=default)")
	private ObjectEntryManager _objectEntryManager;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	@Reference(policyOption = ReferencePolicyOption.GREEDY)
	private QuotaManager _quotaManager;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

}