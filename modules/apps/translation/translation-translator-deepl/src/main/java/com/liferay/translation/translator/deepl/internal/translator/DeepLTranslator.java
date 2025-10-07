/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.deepl.internal.translator;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.translation.exception.TranslatorException;
import com.liferay.translation.translator.BaseTranslator;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.deepl.internal.configuration.DeepLTranslatorConfiguration;

import jakarta.ws.rs.core.Response;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yasuyuki Takeo
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.translation.translator.deepl.internal.configuration.DeepLTranslatorConfiguration",
	service = Translator.class
)
public class DeepLTranslator extends BaseTranslator {

	@Override
	public boolean isEnabled(long companyId) throws ConfigurationException {
		DeepLTranslatorConfiguration deepLTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				DeepLTranslatorConfiguration.class, companyId);

		return deepLTranslatorConfiguration.enabled();
	}

	@Override
	public TranslatorPacket translate(TranslatorPacket translatorPacket)
		throws PortalException {

		DeepLTranslatorConfiguration deepLTranslatorConfiguration =
			_configurationProvider.getCompanyConfiguration(
				DeepLTranslatorConfiguration.class,
				translatorPacket.getCompanyId());

		if (!deepLTranslatorConfiguration.enabled()) {
			return translatorPacket;
		}

		List<String> supportedLanguageCodes = _getSupportedLanguageCodes(
			deepLTranslatorConfiguration);

		String targetLanguageCode = StringUtil.toUpperCase(
			_getTargetLanguageCode(translatorPacket.getTargetLanguageId()));

		if (!supportedLanguageCodes.contains(targetLanguageCode)) {
			throw new TranslatorException(
				StringBundler.concat(
					"Target language code ", targetLanguageCode,
					" is not among the supported language codes: ",
					StringUtil.merge(
						supportedLanguageCodes, StringPool.COMMA_AND_SPACE)));
		}

		Map<String, String> translatedFieldsMap = _translate(
			deepLTranslatorConfiguration, translatorPacket.getFieldsMap(),
			translatorPacket.getHTMLMap(),
			StringUtil.toUpperCase(
				getLanguageCode(translatorPacket.getSourceLanguageId())),
			targetLanguageCode);

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

	@Override
	protected String getLanguageCode(String languageId) {
		if (StringUtil.endsWith(languageId, "ES")) {
			return "es";
		}

		return super.getLanguageCode(languageId);
	}

	private List<String> _getSupportedLanguageCodes(
			DeepLTranslatorConfiguration deepLTranslatorConfiguration)
		throws PortalException {

		Http.Options options = new Http.Options();

		options.setMethod(Http.Method.GET);

		return JSONUtil.toList(
			_jsonFactory.createJSONArray(
				_invoke(
					deepLTranslatorConfiguration.authKey(), options,
					URLBuilder.create(
						deepLTranslatorConfiguration.validateLanguageURL()
					).addParameter(
						"type", "target"
					).build())),
			jsonObject -> jsonObject.getString("language"), _log);
	}

	private String _getTargetLanguageCode(String languageId) {
		if (StringUtil.startsWith(languageId, "en") ||
			StringUtil.startsWith(languageId, "pt")) {

			return StringUtil.replace(
				languageId, CharPool.UNDERLINE, CharPool.DASH);
		}

		if (StringUtil.startsWith(languageId, "zh")) {
			if (StringUtil.endsWith(languageId, "TW")) {
				return "zh-HANT";
			}

			return "zh-HANS";
		}

		return getLanguageCode(languageId);
	}

	private String _invoke(String authKey, Http.Options options, String url)
		throws PortalException {

		String json = null;

		options.addHeader(
			HttpHeaders.AUTHORIZATION, "DeepL-Auth-Key " + authKey);
		options.setLocation(url);

		try {
			json = _http.URLtoString(options);
		}
		catch (IOException ioException) {
			throw new TranslatorException(ioException);
		}

		Http.Response response = options.getResponse();

		Response.Status status = Response.Status.fromStatusCode(
			response.getResponseCode());

		if (status == Response.Status.OK) {
			return json;
		}

		throw new TranslatorException("HTTP response status " + status);
	}

	private Map<String, String> _translate(
			DeepLTranslatorConfiguration deepLTranslatorConfiguration,
			Map<String, String> fieldsMap, Map<String, Boolean> htmlMap,
			String sourceLanguageCode, String targetLanguageCode)
		throws PortalException {

		Map<String, String> translatedFieldsMap = new HashMap<>();

		for (Map.Entry<String, String> entry : fieldsMap.entrySet()) {
			Boolean html = htmlMap.get(entry.getKey());

			translatedFieldsMap.put(
				entry.getKey(),
				_translate(
					deepLTranslatorConfiguration, sourceLanguageCode,
					targetLanguageCode, entry.getValue(), html));
		}

		return translatedFieldsMap;
	}

	private String _translate(
			DeepLTranslatorConfiguration deepLTranslatorConfiguration,
			String sourceLanguageCode, String targetLanguageCode, String text,
			Boolean html)
		throws PortalException {

		if (Validator.isBlank(text)) {
			return text;
		}

		JSONObject requestJSONObject = _jsonFactory.createJSONObject();

		if ((html != null) && html) {
			requestJSONObject.put("tag_handling", "html");
		}

		requestJSONObject.put(
			"source_lang", sourceLanguageCode
		).put(
			"target_lang", targetLanguageCode
		).put(
			"text", new String[] {text}
		);

		Http.Body body = new Http.Body(
			requestJSONObject.toString(), ContentTypes.APPLICATION_JSON,
			"UTF-8");

		Http.Options options = new Http.Options();

		options.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);

		options.setBody(body);
		options.setMethod(Http.Method.POST);

		JSONObject jsonObject = _jsonFactory.createJSONObject(
			_invoke(
				deepLTranslatorConfiguration.authKey(), options,
				deepLTranslatorConfiguration.url()));

		JSONArray jsonArray = jsonObject.getJSONArray("translations");

		JSONObject translationJSONObject = jsonArray.getJSONObject(0);

		return translationJSONObject.getString("text");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeepLTranslator.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}