/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.web.internal.client;

import com.liferay.ai.creator.openai.web.internal.exception.AICreatorOpenAIClientException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.net.HttpURLConnection;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import org.osgi.service.component.annotations.Component;

/**
 * @author Lourdes Fernández Besada
 * @author Roberto Díaz
 */
@Component(
	enabled = false, property = "service.ranking:Integer=200",
	service = AICreatorOpenAIClient.class
)
public class MockAICreatorOpenAIClient implements AICreatorOpenAIClient {

	@Override
	public String getCompletion(
		String apiKey, String content, Locale locale, String tone, int words) {

		if (Objects.equals(apiKey, "VALID_API_KEY") &&
			Objects.equals(content, "USER_CONTENT")) {

			return _getSampleCompletion(0);
		}

		if (Validator.isNotNull(content) &&
			content.startsWith(_USER_CONTENT_SLEEP_MILLIS_PREFIX)) {

			return _getSampleCompletion(
				GetterUtil.getLong(
					content.substring(
						_USER_CONTENT_SLEEP_MILLIS_PREFIX.length())));
		}

		throw _getAICreatorOpenAIClientException(content);
	}

	@Override
	public String[] getGenerations(
		String apiKey, String model, int numberOfImages, String prompt,
		String size) {

		if (Objects.equals(apiKey, "VALID_API_KEY") &&
			Objects.equals(prompt, "USER_IMAGES")) {

			String[] generations = new String[numberOfImages];

			Arrays.fill(generations, "data:image/png;base64," + _IMAGE);

			return generations;
		}

		throw _getAICreatorOpenAIClientException(prompt);
	}

	@Override
	public void validateAPIKey(String apiKey, String model) {
		if (!Objects.equals(apiKey, "VALID_API_KEY")) {
			throw _getAICreatorOpenAIClientException(apiKey);
		}

		if (Validator.isNotNull(model) && model.startsWith("OPENAI_API_")) {
			throw _getAICreatorOpenAIClientException(model);
		}
	}

	private AICreatorOpenAIClientException _getAICreatorOpenAIClientException(
		String key) {

		if (Objects.equals(key, "OPENAI_API_IOEXCEPTION")) {
			return new AICreatorOpenAIClientException(new IOException());
		}

		String errorMessage = StringUtils.substringBetween(
			key, "OPENAI_API_", "_ERROR_MESSAGE");

		if (Validator.isNotNull(errorMessage)) {
			return new AICreatorOpenAIClientException(
				"openai-api-error-code", errorMessage,
				HttpURLConnection.HTTP_INTERNAL_ERROR);
		}

		return new AICreatorOpenAIClientException(
			new UnsupportedOperationException("Unsupported key: " + key));
	}

	private String _getSampleCompletion(long sleepMillis) {
		if ((sleepMillis <= 0) || (sleepMillis > 10000)) {
			return "OPENAI_API_COMPLETION_RESPONSE_CONTENT";
		}

		try {
			Thread.sleep(sleepMillis);
		}
		catch (InterruptedException interruptedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(interruptedException);
			}
		}

		return "OPENAI_API_COMPLETION_RESPONSE_CONTENT";
	}

	private static final String _IMAGE = StringBundler.concat(
		"iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAIAAAAlC+aJAAAAT0lEQVR42u3PQQkA",
		"AAgEsPsb0v4pNIJvYbACS/W8FgEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB",
		"AQEBAQEBAQEBAQEBAQEBAQGBywLV/pFLvCMoJQAAAABJRU5ErkJggg==");

	private static final String _USER_CONTENT_SLEEP_MILLIS_PREFIX =
		"USER_CONTENT_SLEEP_MILLIS_";

	private static final Log _log = LogFactoryUtil.getLog(
		MockAICreatorOpenAIClient.class);

}