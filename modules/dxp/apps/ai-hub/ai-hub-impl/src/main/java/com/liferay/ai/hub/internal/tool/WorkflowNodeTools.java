/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.tool;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.workflow.WorkflowNodeManager;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.invocation.InvocationParameters;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Feliphe Marinho
 */
public class WorkflowNodeTools {

	public WorkflowNodeTools(WorkflowNodeManager workflowNodeManager) {
		_completeWorkflowNodeCallable =
			new CompanyInheritableThreadLocalCallable<>(
				() -> {
					ExecutionContext executionContext =
						_invocationParameters.get("executionContext");

					KaleoInstanceToken kaleoInstanceToken =
						executionContext.getKaleoInstanceToken();

					Map<String, Serializable> workflowContext =
						executionContext.getWorkflowContext();

					workflowContext.put("reason", _reason);

					workflowNodeManager.completeWorkflowNode(
						kaleoInstanceToken.getCompanyId(),
						kaleoInstanceToken.getUserId(),
						kaleoInstanceToken.getKaleoInstanceTokenId(),
						_transitionName, workflowContext, false);

					return null;
				});
	}

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

		_invocationParameters = invocationParameters;
		_reason = reason;
		_transitionName = transitionName;

		try {
			_completeWorkflowNodeCallable.call();
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	private final Callable<Void> _completeWorkflowNodeCallable;
	private InvocationParameters _invocationParameters;
	private String _reason;
	private String _transitionName;
}
