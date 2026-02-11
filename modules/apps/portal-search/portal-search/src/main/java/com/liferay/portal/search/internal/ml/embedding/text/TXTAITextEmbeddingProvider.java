/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.ml.embedding.text;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.internal.ml.embedding.text.util.ConfigurationValidationUtil;
import com.liferay.portal.search.rest.dto.v1_0.EmbeddingProviderConfiguration;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	property = "provider.name=TxtAIText", service = TextEmbeddingProvider.class
)
public class TXTAITextEmbeddingProvider implements TextEmbeddingProvider {

	@Override
	public Double[] getEmbedding(
		EmbeddingProviderConfiguration embeddingProviderConfiguration,
		String text) {

		Map<String, Object> attributes =
			(Map<String, Object>)embeddingProviderConfiguration.getAttributes();

		if (!ConfigurationValidationUtil.validateAttributes(
				attributes, new String[] {"hostAddress"})) {

			return new Double[0];
		}

		return _getEmbedding(attributes, text);
	}

	private Double[] _getEmbedding(
		Map<String, Object> attributes, String text) {

		try {
			String responseJSON = _http.URLtoString(
				_getOptions(attributes, text));

			if (!JSONUtil.isJSONArray(responseJSON)) {
				throw new IllegalArgumentException(responseJSON);
			}

			List<Double> list = JSONUtil.toDoubleList(
				_jsonFactory.createJSONArray(responseJSON));

			return list.toArray(new Double[0]);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private String _getLocation(String hostAddress, String text) {
		if (!hostAddress.endsWith("/")) {
			hostAddress += "/";
		}

		return StringBundler.concat(
			hostAddress, "transform?text=", URLCodec.encodeURL(text, false));
	}

	private Http.Options _getOptions(
		Map<String, Object> attributes, String text) {

		Http.Options options = new Http.Options();

		String hostAddress = MapUtil.getString(attributes, "hostAddress");

		String basicAuthUsername = MapUtil.getString(
			attributes, "basicAuthUsername");

		if (!Validator.isBlank(basicAuthUsername)) {
			options.setAuth(
				HttpComponentsUtil.getDomain(hostAddress), -1, null,
				basicAuthUsername,
				MapUtil.getString(
					attributes, "basicAuthPassword", StringPool.BLANK));
		}

		options.setLocation(_getLocation(hostAddress, text));

		return options;
	}

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}