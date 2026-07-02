/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.workflow.kaleo.runtime.node.util;

import com.liferay.ai.hub.internal.mcp.tool.provider.MCPToolProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.runtime.constants.WorkflowInstanceDestinationNames;

import dev.langchain4j.model.vertexai.gemini.VertexAiGeminiStreamingChatModel;

import java.util.Date;
import java.util.function.Consumer;

/**
 * @author Feliphe Marinho
 */
public class OnErrorConsumerUtil {

	public static Consumer<Throwable> create(
		KaleoInstanceToken kaleoInstanceToken, String sseEventSinkKey,
		VertexAiGeminiStreamingChatModel vertexAiGeminiStreamingChatModel) {

		return throwable -> {
			MCPToolProviderUtil.close(sseEventSinkKey);

			vertexAiGeminiStreamingChatModel.close();

			Message message = new Message();

			message.put("companyId", kaleoInstanceToken.getCompanyId());

			message.put("createDate", new Date());
			message.put("exception", throwable);
			message.put("userId", kaleoInstanceToken.getUserId());
			message.put(
				"workflowInstanceId", kaleoInstanceToken.getKaleoInstanceId());

			MessageBusUtil.sendMessage(
				WorkflowInstanceDestinationNames.WORKFLOW_INSTANCE, message);

			_log.error(throwable);
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OnErrorConsumerUtil.class);

}