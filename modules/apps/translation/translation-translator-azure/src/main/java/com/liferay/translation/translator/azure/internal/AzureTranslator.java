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
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.url.URLBuilder;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.translation.translator.BaseTranslator;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.azure.internal.configuration.AzureTranslatorConfiguration;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.translation.translator.azure.internal.configuration.AzureTranslatorConfiguration",
	service = Translator.class
)
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

		try {
			Http.Options options = new Http.Options();

			options.addHeader(
				"Ocp-Apim-Subscription-Key",
				azureTranslatorConfiguration.subscriptionKey());
			options.addHeader(
				"Ocp-Apim-Subscription-Region",
				azureTranslatorConfiguration.resourceLocation());
			options.addHeader(
				HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
			options.addHeader(
				HttpHeaders.USER_AGENT,
				_getUserAgent(azureTranslatorConfiguration.userAgent()));
			options.setBody(
				_getTranslatorPacketPayload(translatorPacket),
				ContentTypes.APPLICATION_JSON, StringPool.UTF8);
			options.setLocation(
				URLBuilder.create(
					"https://api.cognitive.microsofttranslator.com/translate"
				).addParameter(
					"api-version", "3.0"
				).addParameter(
					"from",
					getLanguageCode(translatorPacket.getSourceLanguageId())
				).addParameter(
					"to",
					getLanguageCode(translatorPacket.getTargetLanguageId())
				).build());
			options.setPost(true);

			String json = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			if (response.getResponseCode() != 200) {
				throw new PortalException(
					"Response code " + response.getResponseCode());
			}

			Map<String, String> translatedFieldsMap = _getTranslatedFieldsMap(
				translatorPacket.getFieldsMap(),
				_jsonFactory.createJSONArray(json));

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
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	private Map<String, String> _getTranslatedFieldsMap(
		Map<String, String> fieldsMap, JSONArray jsonArray) {

		Map<String, String> translatedFieldsMap = new HashMap<>();

		int i = 0;

		for (String key : fieldsMap.keySet()) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			JSONArray translationsJSONArray = jsonObject.getJSONArray(
				"translations");

			JSONObject translationJSONObject =
				translationsJSONArray.getJSONObject(0);

			translatedFieldsMap.put(
				key, translationJSONObject.getString("text"));

			i++;
		}

		return translatedFieldsMap;
	}

	private String _getTranslatorPacketPayload(
		TranslatorPacket translatorPacket) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		Map<String, String> fields = translatorPacket.getFieldsMap();

		fields.forEach(
			(key, value) -> jsonArray.put(JSONUtil.put("Text", value)));

		return jsonArray.toString();
	}

	private String _getUserAgent(String userAgent) {
		if (Validator.isNull(userAgent)) {
			return "Liferay";
		}

		return userAgent;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}