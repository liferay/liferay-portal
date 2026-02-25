/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.text.embeddings;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.search.ml.embedding.util.ConfigurationValidationUtil;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;
import com.liferay.portal.search.rest.text.embeddings.configuration.TextEmbeddingProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(enabled = false, service = TextEmbeddingProvider.class)
public class VertexAITextEmbeddingProvider implements TextEmbeddingProvider {

	@Override
	public Double[] getEmbedding(
		EmbeddingProviderConfiguration embeddingProviderConfiguration,
		String text) {

		Map<String, Object> attributes =
			(Map<String, Object>)embeddingProviderConfiguration.getAttributes();

		if (!ConfigurationValidationUtil.validateAttributes(
				attributes, new String[] {"location", "model", "projectId"})) {

			return new Double[0];
		}

		return _getEmbedding(attributes, text);
	}

	@Override
	public String getProviderName() {
		return "vertex-ai";
	}

	private String _getAuthenticationToken() throws Exception {
		String authenticationToken = StringPool.BLANK;

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(
				_getAuthenticationTokenCommand);

			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();

			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()))) {

				authenticationToken = bufferedReader.readLine();
			}
			catch (IOException ioException) {
				_log.error(ioException);
			}

			if (process.exitValue() != 0) {
				throw new Exception(
					StringBundler.concat(
						_getAuthenticationTokenCommand,
						" failed with exit status ", process.exitValue()));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return authenticationToken;
	}

	private Double[] _getEmbedding(
		Map<String, Object> attributes, String text) {

		try {
			JSONObject responseJSONObject = _jsonFactory.createJSONObject(
				_http.URLtoString(_getOptions(attributes, text)));

			JSONArray predictionsJSONArray = responseJSONObject.getJSONArray(
				"predictions");

			if (JSONUtil.isEmpty(predictionsJSONArray)) {
				throw new IllegalArgumentException(
					responseJSONObject.toString());
			}

			JSONObject predictionJSONObject =
				predictionsJSONArray.getJSONObject(0);

			JSONObject embeddingsJSONObject =
				predictionJSONObject.getJSONObject("embeddings");

			List<Double> list = JSONUtil.toDoubleList(
				_getJSONArray(embeddingsJSONObject.getJSONArray("values")));

			return list.toArray(new Double[0]);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private JSONArray _getJSONArray(JSONArray jsonArray1) {
		JSONArray jsonArray2 = jsonArray1.getJSONArray(0);

		if (jsonArray2 != null) {
			return _getJSONArray(jsonArray2);
		}

		return jsonArray1;
	}

	private String _getLocation(Map<String, Object> attributes) {
		return StringBundler.concat(
			"https://", MapUtil.getString(attributes, "location"),
			"-aiplatform.googleapis.com/v1/projects/",
			MapUtil.getString(attributes, "projectId"), "/locations/",
			MapUtil.getString(attributes, "location"),
			"/publishers/google/models/",
			MapUtil.getString(attributes, "model"), ":predict");
	}

	private Http.Options _getOptions(
			Map<String, Object> attributes, String text)
		throws Exception {

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.AUTHORIZATION, "Bearer " + _getAuthenticationToken());
		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.setBody(
			_getRequestBody(attributes, text), ContentTypes.APPLICATION_JSON,
			StringPool.UTF8);
		options.setCookieSpec(Http.CookieSpec.STANDARD);
		options.setLocation(_getLocation(attributes));
		options.setPost(true);

		return options;
	}

	private String _getRequestBody(
		Map<String, Object> attributes, String text) {

		return String.valueOf(
			JSONUtil.put(
				"instances",
				_jsonFactory.createJSONArray(
				).put(
					JSONUtil.put("content", text)
				)
			).put(
				"parameters",
				JSONUtil.put(
					"autoTruncate",
					MapUtil.getBoolean(attributes, "autoTruncate", true))
			));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VertexAITextEmbeddingProvider.class);

	private static final List<String> _getAuthenticationTokenCommand =
		new ArrayList<String>() {
			{
				add("gcloud");
				add("auth");
				add("print-access-token");
			}
		};

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}