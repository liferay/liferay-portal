/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.langchain4j.observability.api.listener;

import com.liferay.ai.hub.internal.audit.AuditRouterUtil;
import com.liferay.ai.hub.internal.audit.constants.AIHubEventTypes;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.constants.WorkflowInstanceDestinationNames;

import dev.langchain4j.guardrail.GuardrailResult;

import java.time.Duration;

import java.util.Date;
import java.util.List;

/**
 * @author Pedro Leite
 */
public abstract class BaseGuardrailExecutedListener {

	public BaseGuardrailExecutedListener(ExecutionContext executionContext) {
		_executionContext = executionContext;
	}

	protected void completeExceptionally(
		String content, Duration duration, GuardrailResult<?> guardrailResult,
		String guardrailType) {

		List<GuardrailResult.Failure> failures = guardrailResult.failures();

		GuardrailResult.Failure failure = failures.get(0);

		String failureMessage = failure.message();

		try {
			KaleoInstanceToken kaleoInstanceToken =
				_executionContext.getKaleoInstanceToken();

			AuditRouterUtil.route(
				WorkflowInstance.class.getName(),
				kaleoInstanceToken.getKaleoInstanceId(), new Date(),
				AIHubEventTypes.AI_HUB_GUARDRAIL_VIOLATION,
				JSONUtil.put(
					"agentDefinitionExternalReferenceCode",
					MapUtil.getString(
						_executionContext.getWorkflowContext(),
						"agentDefinitionExternalReferenceCode")
				).put(
					"content", content
				).put(
					"duration", duration.toMillis()
				).put(
					"guardrailType", guardrailType
				).put(
					"violation", failureMessage
				).put(
					"workflowInstanceId",
					kaleoInstanceToken.getKaleoInstanceId()
				),
				kaleoInstanceToken.getUserId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		try {
			Message message = new Message();

			KaleoInstanceToken kaleoInstanceToken =
				_executionContext.getKaleoInstanceToken();

			message.put("companyId", kaleoInstanceToken.getCompanyId());

			message.put("createDate", new Date());
			message.put(
				"exception", new IllegalArgumentException(failureMessage));
			message.put("userId", kaleoInstanceToken.getUserId());
			message.put(
				"workflowInstanceId", kaleoInstanceToken.getKaleoInstanceId());

			MessageBusUtil.sendMessage(
				WorkflowInstanceDestinationNames.WORKFLOW_INSTANCE, message);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseGuardrailExecutedListener.class);

	private final ExecutionContext _executionContext;

}