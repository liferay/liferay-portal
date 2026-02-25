/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.text.embeddings;

import com.liferay.petra.reflect.ReflectionUtil;
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

import java.net.HttpURLConnection;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(enabled = false, service = TextEmbeddingProvider.class)
public class HuggingFaceInferenceAPITextEmbeddingProvider
	implements TextEmbeddingProvider {

	@Override
	public Double[] getEmbedding(
		EmbeddingProviderConfiguration embeddingProviderConfiguration,
		String text) {

		Map<String, Object> attributes =
			(Map<String, Object>)embeddingProviderConfiguration.getAttributes();

		if (!ConfigurationValidationUtil.validateAttributes(
				attributes, new String[] {"accessToken", "model"})) {

			return new Double[0];
		}

		return _getEmbedding(attributes, text);
	}

	@Override
	public String getProviderName() {
		return "hugging-face-inference-api";
	}

	private Double[] _getEmbedding(
		Map<String, Object> attributes, String text) {

		try {
			Http.Options options = _getOptions(attributes, text);

			String responseJSON = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() ==
					HttpURLConnection.HTTP_UNAVAILABLE) {

				options.addHeader("x-wait-for-model", "true");
				options.setTimeout(
					MapUtil.getInteger(attributes, "modelTimeout", 30) * 1000);

				responseJSON = _http.URLtoString(options);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("Response: " + responseJSON);
			}

			if (!JSONUtil.isJSONArray(responseJSON)) {
				throw new IllegalArgumentException(responseJSON);
			}

			JSONArray jsonArray = _jsonFactory.createJSONArray(responseJSON);

			List<Double> list = JSONUtil.toDoubleList(jsonArray);

			return list.toArray(new Double[0]);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private Http.Options _getOptions(
		Map<String, Object> attributes, String text) {

		Http.Options options = new Http.Options();

		JSONObject jsonObject = JSONUtil.put("inputs", text);

		options.addHeader(
			HttpHeaders.AUTHORIZATION,
			"Bearer " + MapUtil.getString(attributes, "accessToken"));
		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.setBody(
			jsonObject.toString(), ContentTypes.APPLICATION_JSON,
			StringPool.UTF8);
		options.setCookieSpec(Http.CookieSpec.STANDARD);
		options.setLocation(
			"https://router.huggingface.co/hf-inference/models/" +
				MapUtil.getString(attributes, "model"));
		options.setPost(true);

		return options;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HuggingFaceInferenceAPITextEmbeddingProvider.class);

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}