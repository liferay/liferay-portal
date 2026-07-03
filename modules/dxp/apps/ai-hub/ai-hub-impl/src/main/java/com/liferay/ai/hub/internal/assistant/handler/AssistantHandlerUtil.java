/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.assistant.handler;

import com.liferay.ai.hub.internal.memory.ChatMemoryProviderUtil;
import com.liferay.portal.kernel.util.Validator;

import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.memory.ChatMemoryAccess;

/**
 * @author Feliphe Marinho
 */
public class AssistantHandlerUtil {

	public static void handle(AssistantHandlerContext assistantHandlerContext) {
		TokenStream tokenStream = null;

		AiServices<? extends Assistant> aiServices = AiServices.builder(
			Assistant.class);

		if (Validator.isNotNull(assistantHandlerContext.getMemoryId())) {
			aiServices = AiServices.builder(ChatMemoryAccessAssistant.class);

			aiServices.chatMemoryProvider(ChatMemoryProviderUtil::provide);
		}

		aiServices.registerListeners(
			assistantHandlerContext.getAiServiceListeners());

		if (assistantHandlerContext.getRetrievalAugmentor() != null) {
			aiServices.retrievalAugmentor(
				assistantHandlerContext.getRetrievalAugmentor());
		}

		Assistant assistant = aiServices.inputGuardrails(
			assistantHandlerContext.getInputGuardrails()
		).maxToolCallingRoundTrips(
			7
		).outputGuardrails(
			assistantHandlerContext.getOutputGuardrails()
		).streamingChatModel(
			assistantHandlerContext.getGoogleGenAiStreamingChatModel()
		).systemMessageProvider(
			assistantHandlerContext.getSystemMessageProviderFunction()
		).toolProvider(
			assistantHandlerContext.getToolProvider()
		).tools(
			assistantHandlerContext.getTools()
		).build();

		if (assistant instanceof
				ChatMemoryAccessAssistant chatMemoryAccessAssistant) {

			tokenStream = chatMemoryAccessAssistant.invoke(
				assistantHandlerContext.getInvocationParameters(),
				assistantHandlerContext.getMemoryId(),
				assistantHandlerContext.getUserMessage());
		}
		else {
			tokenStream = assistant.invoke(
				assistantHandlerContext.getInvocationParameters(),
				assistantHandlerContext.getUserMessage());
		}

		tokenStream.onCompleteResponse(
			assistantHandlerContext.getOnCompleteResponseConsumer()
		).onError(
			assistantHandlerContext.getOnErrorConsumer()
		).start();
	}

	public interface Assistant {

		public TokenStream invoke(
			InvocationParameters invocationParameters,
			@UserMessage String userMessage);

	}

	public interface ChatMemoryAccessAssistant
		extends Assistant, ChatMemoryAccess {

		public TokenStream invoke(
			InvocationParameters invocationParameters,
			@MemoryId String memoryId, @UserMessage String userMessage);

	}

}