/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.model;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.types.HarmBlockThreshold;
import com.google.genai.types.HarmCategory;
import com.google.genai.types.SafetySetting;

import com.liferay.ai.hub.configuration.VertexAIConfiguration;
import com.liferay.ai.hub.quota.QuotaManager;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiStreamingChatModel;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Iliyan Peychev
 */
public class GoogleGenAiUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_configurationProviderUtilMockedStatic.close();
		_googleCredentialsMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_vertexAIConfiguration.location()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_vertexAIConfiguration.modelName()
		).thenReturn(
			_MODEL_NAME
		);

		Mockito.when(
			_vertexAIConfiguration.projectId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		_configurationProviderUtilMockedStatic.when(
			() -> ConfigurationProviderUtil.getCompanyConfiguration(
				Mockito.eq(VertexAIConfiguration.class), Mockito.anyLong())
		).thenReturn(
			_vertexAIConfiguration
		);

		GoogleCredentials googleCredentials = Mockito.mock(
			GoogleCredentials.class);

		Mockito.when(
			googleCredentials.createScoped((String[])Mockito.any())
		).thenReturn(
			googleCredentials
		);

		_googleCredentialsMockedStatic.when(
			GoogleCredentials::getApplicationDefault
		).thenReturn(
			googleCredentials
		);
	}

	@Test
	public void testCreateGoogleGenAiChatModel() throws Exception {
		GoogleGenAiChatModel googleGenAiChatModel =
			GoogleGenAiUtil.createGoogleGenAiChatModel(
				_quotaManager, _serviceContext);

		ChatRequestParameters defaultRequestParameters =
			googleGenAiChatModel.defaultRequestParameters();

		Assert.assertEquals(_MODEL_NAME, defaultRequestParameters.modelName());
		Assert.assertEquals(
			ResponseFormat.JSON, defaultRequestParameters.responseFormat());

		_assertSafetySettings(
			ReflectionTestUtil.getFieldValue(
				googleGenAiChatModel, "safetySettings"));
	}

	@Test
	public void testCreateGoogleGenAiStreamingChatModel() throws Exception {
		GoogleGenAiStreamingChatModel googleGenAiStreamingChatModel =
			GoogleGenAiUtil.createGoogleGenAiStreamingChatModel(
				Mockito.mock(ExecutorService.class), _quotaManager,
				_serviceContext);

		ChatRequestParameters defaultRequestParameters =
			googleGenAiStreamingChatModel.defaultRequestParameters();

		Assert.assertEquals(_MODEL_NAME, defaultRequestParameters.modelName());
		Assert.assertNull(defaultRequestParameters.responseFormat());

		_assertSafetySettings(
			ReflectionTestUtil.getFieldValue(
				googleGenAiStreamingChatModel, "safetySettings"));
	}

	private void _assertSafetySettings(List<SafetySetting> safetySettings) {
		Assert.assertEquals(
			List.of(
				_createSafetySetting(
					HarmCategory.Known.HARM_CATEGORY_DANGEROUS_CONTENT),
				_createSafetySetting(
					HarmCategory.Known.HARM_CATEGORY_HARASSMENT),
				_createSafetySetting(
					HarmCategory.Known.HARM_CATEGORY_HATE_SPEECH),
				_createSafetySetting(
					HarmCategory.Known.HARM_CATEGORY_SEXUALLY_EXPLICIT)),
			safetySettings);
	}

	private SafetySetting _createSafetySetting(
		HarmCategory.Known harmCategoryKnown) {

		return SafetySetting.builder(
		).category(
			harmCategoryKnown
		).threshold(
			HarmBlockThreshold.Known.BLOCK_MEDIUM_AND_ABOVE
		).build();
	}

	private static final String _MODEL_NAME = "configured-model";

	private static final MockedStatic<ConfigurationProviderUtil>
		_configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	private static final MockedStatic<GoogleCredentials>
		_googleCredentialsMockedStatic = Mockito.mockStatic(
			GoogleCredentials.class);

	private final QuotaManager _quotaManager = Mockito.mock(QuotaManager.class);
	private final ServiceContext _serviceContext = new ServiceContext();
	private final VertexAIConfiguration _vertexAIConfiguration = Mockito.mock(
		VertexAIConfiguration.class);

}