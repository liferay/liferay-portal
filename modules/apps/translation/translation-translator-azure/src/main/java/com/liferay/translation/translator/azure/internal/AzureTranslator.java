/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.azure.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.url.URLBuilder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.translation.translator.BaseTranslator;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.azure.internal.configuration.AzureTranslatorConfiguration;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@Component(service = Translator.class)
public class AzureTranslator extends BaseTranslator {

	@Override
	public boolean isEnabled(long companyId) throws ConfigurationException {
		AzureTranslatorConfiguration azureTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AzureTranslatorConfiguration.class, companyId);

		return azureTranslatorConfiguration.enabled();
	}

	@Override
	public TranslatorPacket translate(TranslatorPacket translatorPacket)
		throws PortalException {

		AzureTranslatorConfiguration azureTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				AzureTranslatorConfiguration.class,
				translatorPacket.getCompanyId());

		if (!azureTranslatorConfiguration.enabled()) {
			return translatorPacket;
		}

		Map<String, String> fieldsMap = translatorPacket.getFieldsMap();
		Map<String, Boolean> htmlMap = translatorPacket.getHTMLMap();

		Map<Boolean, List<String>> keysMap = MapUtil.toPartitionMap(
			new ArrayList<>(fieldsMap.keySet()),
			key -> GetterUtil.getBoolean(htmlMap.get(key)));

		Map<String, String> translatedFieldsMap = new HashMap<>();

		try {
			for (Map.Entry<Boolean, List<String>> entry : keysMap.entrySet()) {
				translatedFieldsMap.putAll(
					_translate(
						azureTranslatorConfiguration, entry.getKey(),
						entry.getValue(), translatorPacket));
			}
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}

		return new TranslatorPacket() {

			@Override
			public long getCompanyId() {
				return translatorPacket.getCompanyId();
			}

			@Override
			public Map<String, String> getFieldsMap() {
				return translatedFieldsMap;
			}

			@Override
			public Map<String, Boolean> getHTMLMap() {
				return translatorPacket.getHTMLMap();
			}

			@Override
			public String getSourceLanguageId() {
				return translatorPacket.getSourceLanguageId();
			}

			@Override
			public String getTargetLanguageId() {
				return translatorPacket.getTargetLanguageId();
			}

		};
	}

	private String _getLocation(
		boolean html, TranslatorPacket translatorPacket) {

		String textType = "plain";

		if (html) {
			textType = "html";
		}

		return URLBuilder.create(
			"https://api.cognitive.microsofttranslator.com/translate"
		).addParameter(
			"api-version", "3.0"
		).addParameter(
			"from", getLanguageCode(translatorPacket.getSourceLanguageId())
		).addParameter(
			"textType", textType
		).addParameter(
			"to", getLanguageCode(translatorPacket.getTargetLanguageId())
		).build();
	}

	private Map<String, String> _getTranslatedFieldsMap(
		JSONArray jsonArray, List<String> keys) {

		Map<String, String> translatedFieldsMap = new HashMap<>();

		for (int i = 0; i < keys.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray translationsJSONArray = jsonObject.getJSONArray(
				"translations");

			JSONObject translationJSONObject =
				translationsJSONArray.getJSONObject(0);

			translatedFieldsMap.put(
				keys.get(i), translationJSONObject.getString("text"));
		}

		return translatedFieldsMap;
	}

	private String _getUserAgent(String userAgent) {
		if (Validator.isNull(userAgent)) {
			return "Liferay";
		}

		return userAgent;
	}

	private JSONArray _invoke(
			AzureTranslatorConfiguration azureTranslatorConfiguration,
			long companyId, String location, JSONArray requestJSONArray)
		throws IOException, PortalException {

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
		options.addHeader(
			HttpHeaders.USER_AGENT,
			_getUserAgent(azureTranslatorConfiguration.userAgent()));
		options.addHeader(
			"Ocp-Apim-Subscription-Key",
			_secretResolver.resolve(
				companyId, azureTranslatorConfiguration.subscriptionKey()));
		options.addHeader(
			"Ocp-Apim-Subscription-Region",
			azureTranslatorConfiguration.resourceLocation());
		options.setBody(
			requestJSONArray.toString(), ContentTypes.APPLICATION_JSON,
			StringPool.UTF8);
		options.setLocation(location);
		options.setPost(true);

		String json = _http.URLtoString(options);

		Http.Response response = options.getResponse();

		if (response.getResponseCode() != 200) {
			throw new PortalException(
				"Response code " + response.getResponseCode());
		}

		return _jsonFactory.createJSONArray(json);
	}

	private Map<String, String> _translate(
			AzureTranslatorConfiguration azureTranslatorConfiguration,
			boolean html, List<String> keys, TranslatorPacket translatorPacket)
		throws IOException, PortalException {

		Map<String, String> fieldsMap = translatorPacket.getFieldsMap();

		JSONArray jsonArray = _invoke(
			azureTranslatorConfiguration, translatorPacket.getCompanyId(),
			_getLocation(html, translatorPacket),
			JSONUtil.toJSONArray(
				keys, key -> JSONUtil.put("Text", fieldsMap.get(key)), _log));

		return _getTranslatedFieldsMap(jsonArray, keys);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AzureTranslator.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SecretResolver _secretResolver;

}