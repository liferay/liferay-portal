/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.assistant.handler;

import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiStreamingChatModel;
import dev.langchain4j.observability.api.listener.AiServiceListener;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.service.tool.ToolProvider;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Feliphe Marinho
 */
public class AssistantHandlerContext {

	public static AssistantHandlerContext.Builder builder() {
		return new AssistantHandlerContext.Builder();
	}

	public AssistantHandlerContext(AssistantHandlerContext.Builder builder) {
		_aiServiceListeners = builder._aiServiceListeners;
		_googleGenAiStreamingChatModel = builder._googleGenAiStreamingChatModel;
		_inputGuardrails = builder._inputGuardrails;
		_invocationParameters = builder._invocationParameters;
		_memoryId = builder._memoryId;
		_onCompleteResponseConsumer = builder._onCompleteResponseConsumer;
		_onErrorConsumer = builder._onErrorConsumer;
		_outputGuardrails = builder._outputGuardrails;
		_retrievalAugmentor = builder._retrievalAugmentor;
		_systemMessageProviderFunction = builder._systemMessageProviderFunction;
		_tools = builder._tools;
		_toolProvider = builder._toolProvider;
		_userMessage = builder._userMessage;
	}

	public List<AiServiceListener<?>> getAiServiceListeners() {
		return _aiServiceListeners;
	}

	public GoogleGenAiStreamingChatModel getGoogleGenAiStreamingChatModel() {
		return _googleGenAiStreamingChatModel;
	}

	public List<InputGuardrail> getInputGuardrails() {
		return _inputGuardrails;
	}

	public InvocationParameters getInvocationParameters() {
		return _invocationParameters;
	}

	public String getMemoryId() {
		return _memoryId;
	}

	public Consumer<ChatResponse> getOnCompleteResponseConsumer() {
		return _onCompleteResponseConsumer;
	}

	public Consumer<Throwable> getOnErrorConsumer() {
		return _onErrorConsumer;
	}

	public List<OutputGuardrail> getOutputGuardrails() {
		return _outputGuardrails;
	}

	public RetrievalAugmentor getRetrievalAugmentor() {
		return _retrievalAugmentor;
	}

	public Function<Object, String> getSystemMessageProviderFunction() {
		return _systemMessageProviderFunction;
	}

	public ToolProvider getToolProvider() {
		return _toolProvider;
	}

	public Object[] getTools() {
		return _tools;
	}

	public String getUserMessage() {
		return _userMessage;
	}

	public static class Builder {

		public Builder aiServiceListeners(
			List<AiServiceListener<?>> aiServiceListeners) {

			_aiServiceListeners = aiServiceListeners;

			return this;
		}

		public AssistantHandlerContext build() {
			return new AssistantHandlerContext(this);
		}

		public Builder googleGenAiStreamingChatModel(
			GoogleGenAiStreamingChatModel googleGenAiStreamingChatModel) {

			_googleGenAiStreamingChatModel = googleGenAiStreamingChatModel;

			return this;
		}

		public Builder inputGuardrails(List<InputGuardrail> inputGuardrails) {
			_inputGuardrails = inputGuardrails;

			return this;
		}

		public Builder invocationParameters(
			InvocationParameters invocationParameters) {

			_invocationParameters = invocationParameters;

			return this;
		}

		public Builder memoryId(String memoryId) {
			_memoryId = memoryId;

			return this;
		}

		public Builder onCompleteResponseConsumer(
			Consumer<ChatResponse> onCompleteResponseConsumer) {

			_onCompleteResponseConsumer = onCompleteResponseConsumer;

			return this;
		}

		public Builder onErrorConsumer(Consumer<Throwable> onErrorConsumer) {
			_onErrorConsumer = onErrorConsumer;

			return this;
		}

		public Builder outputGuardrails(
			List<OutputGuardrail> outputGuardrails) {

			_outputGuardrails = outputGuardrails;

			return this;
		}

		public Builder retrievalAugmentor(
			RetrievalAugmentor retrievalAugmentor) {

			_retrievalAugmentor = retrievalAugmentor;

			return this;
		}

		public Builder systemMessageProviderFunction(
			Function<Object, String> systemMessageProviderFunction) {

			_systemMessageProviderFunction = systemMessageProviderFunction;

			return this;
		}

		public Builder toolProvider(ToolProvider toolProvider) {
			if (toolProvider != null) {
				_toolProvider = toolProvider;
			}

			return this;
		}

		public Builder tools(Object... tools) {
			_tools = tools;

			return this;
		}

		public Builder userMessage(String userMessage) {
			_userMessage = userMessage;

			return this;
		}

		private List<AiServiceListener<?>> _aiServiceListeners;
		private GoogleGenAiStreamingChatModel _googleGenAiStreamingChatModel;
		private List<InputGuardrail> _inputGuardrails;
		private InvocationParameters _invocationParameters;
		private String _memoryId;
		private Consumer<ChatResponse> _onCompleteResponseConsumer;
		private Consumer<Throwable> _onErrorConsumer;
		private List<OutputGuardrail> _outputGuardrails;
		private RetrievalAugmentor _retrievalAugmentor;
		private Function<Object, String> _systemMessageProviderFunction;
		private ToolProvider _toolProvider;
		private Object[] _tools = new Object[0];
		private String _userMessage;

	}

	private final List<AiServiceListener<?>> _aiServiceListeners;
	private final GoogleGenAiStreamingChatModel _googleGenAiStreamingChatModel;
	private final List<InputGuardrail> _inputGuardrails;
	private final InvocationParameters _invocationParameters;
	private final String _memoryId;
	private final Consumer<ChatResponse> _onCompleteResponseConsumer;
	private final Consumer<Throwable> _onErrorConsumer;
	private final List<OutputGuardrail> _outputGuardrails;
	private final RetrievalAugmentor _retrievalAugmentor;
	private final Function<Object, String> _systemMessageProviderFunction;
	private final ToolProvider _toolProvider;
	private final Object[] _tools;
	private final String _userMessage;

}