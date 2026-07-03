/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.langchain4j.model.chat.listener;

import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.ai.hub.quota.Source;
import com.liferay.ai.hub.quota.Usage;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;

import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.output.TokenUsage;

/**
 * @author Carolina Barbosa
 */
public class AIHubChatModelListenerImpl implements ChatModelListener {

	public AIHubChatModelListenerImpl(
		QuotaManager quotaManager, ServiceContext serviceContext) {

		_quotaManager = quotaManager;

		_companyId = serviceContext.getCompanyId();
		_userId = serviceContext.getUserId();
	}

	@Override
	public void onResponse(ChatModelResponseContext chatModelResponseContext) {
		ChatResponse chatResponse = chatModelResponseContext.chatResponse();

		if (chatResponse == null) {
			return;
		}

		TokenUsage tokenUsage = chatResponse.tokenUsage();

		if (tokenUsage == null) {
			return;
		}

		try {
			_quotaManager.updateUsage(
				_companyId,
				Usage.builder(
				).source(
					Source.VERTEX_INPUT
				).tokenCount(
					GetterUtil.getLong(tokenUsage.inputTokenCount())
				).build(),
				_userId);
			_quotaManager.updateUsage(
				_companyId,
				Usage.builder(
				).source(
					Source.VERTEX_OUTPUT
				).tokenCount(
					GetterUtil.getLong(tokenUsage.outputTokenCount())
				).build(),
				_userId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AIHubChatModelListenerImpl.class);

	private final long _companyId;
	private final QuotaManager _quotaManager;
	private final long _userId;

}