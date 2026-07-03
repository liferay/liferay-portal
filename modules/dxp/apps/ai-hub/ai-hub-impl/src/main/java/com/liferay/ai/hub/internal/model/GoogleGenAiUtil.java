/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.model;

import com.google.genai.types.HarmBlockThreshold;
import com.google.genai.types.HarmCategory;
import com.google.genai.types.SafetySetting;

import com.liferay.ai.hub.configuration.VertexAIConfiguration;
import com.liferay.ai.hub.internal.langchain4j.model.chat.listener.AIHubChatModelListenerImpl;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.ServiceContext;

import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiStreamingChatModel;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @author Feliphe Marinho
 * @author Iliyan Peychev
 */
public class GoogleGenAiUtil {

	public static GoogleGenAiChatModel createGoogleGenAiChatModel(
			QuotaManager quotaManager, ServiceContext serviceContext)
		throws ConfigurationException {

		VertexAIConfiguration vertexAIConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				VertexAIConfiguration.class, serviceContext.getCompanyId());

		return GoogleGenAiChatModel.builder(
		).listeners(
			Collections.singletonList(
				new AIHubChatModelListenerImpl(quotaManager, serviceContext))
		).location(
			vertexAIConfiguration.location()
		).modelName(
			vertexAIConfiguration.modelName()
		).projectId(
			vertexAIConfiguration.projectId()
		).safetySettings(
			_safetySettings
		).build();
	}

	public static GoogleGenAiStreamingChatModel
			createGoogleGenAiStreamingChatModel(
				ExecutorService executorService, QuotaManager quotaManager,
				ServiceContext serviceContext)
		throws ConfigurationException {

		VertexAIConfiguration vertexAIConfiguration =
			ConfigurationProviderUtil.getCompanyConfiguration(
				VertexAIConfiguration.class, serviceContext.getCompanyId());

		return GoogleGenAiStreamingChatModel.builder(
		).executor(
			executorService
		).listeners(
			Collections.singletonList(
				new AIHubChatModelListenerImpl(quotaManager, serviceContext))
		).location(
			vertexAIConfiguration.location()
		).modelName(
			vertexAIConfiguration.modelName()
		).projectId(
			vertexAIConfiguration.projectId()
		).safetySettings(
			_safetySettings
		).build();
	}

	private static SafetySetting _createSafetySetting(
		HarmCategory.Known harmCategoryKnown) {

		return SafetySetting.builder(
		).category(
			harmCategoryKnown
		).threshold(
			HarmBlockThreshold.Known.BLOCK_MEDIUM_AND_ABOVE
		).build();
	}

	private static final List<SafetySetting> _safetySettings = List.of(
		_createSafetySetting(
			HarmCategory.Known.HARM_CATEGORY_DANGEROUS_CONTENT),
		_createSafetySetting(HarmCategory.Known.HARM_CATEGORY_HARASSMENT),
		_createSafetySetting(HarmCategory.Known.HARM_CATEGORY_HATE_SPEECH),
		_createSafetySetting(
			HarmCategory.Known.HARM_CATEGORY_SEXUALLY_EXPLICIT));

}