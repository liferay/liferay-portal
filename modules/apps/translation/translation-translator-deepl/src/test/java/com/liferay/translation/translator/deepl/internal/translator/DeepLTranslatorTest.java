/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.deepl.internal.translator;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.deepl.internal.configuration.DeepLTranslatorConfiguration;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Gergely Szalay
 */
public class DeepLTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpDeepLTranslator();
		_setUpPortalUtil();
	}

	@Test
	public void testTranslationHandlesLanguage() throws Exception {
		TranslatorPacket translatorPacket = _deepLTranslator.translate(
			_getTranslatorPocket(
				Map.of("infoField--JournalArticle_title--0", false), "en_US",
				"ca_ES"));

		Assert.assertEquals(
			Map.of("infoField--JournalArticle_title--0", "¡Hola, mundo!"),
			translatorPacket.getFieldsMap());
	}

	@Test
	public void testTranslationHandlesLanguageVariant() throws Exception {
		TranslatorPacket translatorPacket = _deepLTranslator.translate(
			_getTranslatorPocket(
				Map.of("infoField--JournalArticle_title--0", false), "en_US",
				"pt_BR"));

		Assert.assertEquals(
			Map.of("infoField--JournalArticle_title--0", "Olá, mundo!"),
			translatorPacket.getFieldsMap());
	}

	@Test
	public void testTranslationHandlesTraditionalChinese()
		throws PortalException {

		TranslatorPacket translatorPacket = _deepLTranslator.translate(
			_getTranslatorPocket(
				Map.of("infoField--JournalArticle_title--0", false), "en_US",
				"zh_TW"));

		Assert.assertEquals(
			Map.of("infoField--JournalArticle_title--0", "哈囉，世界！"),
			translatorPacket.getFieldsMap());
	}

	private String _getTranslationsJSON(String text) {
		return JSONUtil.put(
			"translations",
			JSONUtil.put(
				JSONUtil.put(
					"detected_source_language", "EN"
				).put(
					"text", text
				))
		).toString();
	}

	private TranslatorPacket _getTranslatorPocket(
		Map<String, Boolean> htmlMap, String sourceLanguage,
		String targetLanguage) {

		return new TranslatorPacket() {

			@Override
			public long getCompanyId() {
				return _COMPANY_ID;
			}

			@Override
			public Map<String, String> getFieldsMap() {
				return Map.of(
					"infoField--JournalArticle_title--0", "Hello, world!");
			}

			@Override
			public Map<String, Boolean> getHTMLMap() {
				return htmlMap;
			}

			@Override
			public String getSourceLanguageId() {
				return sourceLanguage;
			}

			@Override
			public String getTargetLanguageId() {
				return targetLanguage;
			}

		};
	}

	private ConfigurationProvider _setUpConfigurationProvider(long companyId)
		throws Exception {

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		DeepLTranslatorConfiguration deepLTranslatorConfiguration =
			Mockito.mock(DeepLTranslatorConfiguration.class);

		Mockito.when(
			deepLTranslatorConfiguration.authKey()
		).thenReturn(
			"DEEPL_authkey"
		);

		Mockito.when(
			deepLTranslatorConfiguration.enabled()
		).thenReturn(
			true
		);

		Mockito.when(
			deepLTranslatorConfiguration.url()
		).thenReturn(
			"https://api-free.deepl.com/v2/translate"
		);

		Mockito.when(
			deepLTranslatorConfiguration.validateLanguageURL()
		).thenReturn(
			"https://api-free.deepl.com/v2/languages"
		);

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				DeepLTranslatorConfiguration.class, companyId)
		).thenReturn(
			deepLTranslatorConfiguration
		);

		return configurationProvider;
	}

	private void _setUpDeepLTranslator() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_deepLTranslator, "_configurationProvider",
			_setUpConfigurationProvider(_COMPANY_ID));
		ReflectionTestUtil.setFieldValue(
			_deepLTranslator, "_http", _setUpHttp());
		ReflectionTestUtil.setFieldValue(
			_deepLTranslator, "_jsonFactory", new JSONFactoryImpl());
	}

	private Http _setUpHttp() throws Exception {
		Http http = Mockito.mock(Http.class);

		Mockito.when(
			http.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocation -> {
				Http.Options options = invocation.getArgument(0);

				Http.Response httpResponse = new Http.Response();

				httpResponse.setResponseCode(200);

				options.setResponse(httpResponse);

				if (options.isPost()) {
					Http.Body body = options.getBody();

					JSONObject payloadJSONObject =
						JSONFactoryUtil.createJSONObject(body.getContent());

					String targetLanguage = JSONUtil.getValue(
						payloadJSONObject, "Object/target_lang"
					).toString();

					if (targetLanguage.equals("ES")) {
						return _getTranslationsJSON("¡Hola, mundo!");
					}
					else if (targetLanguage.equals("PT-BR")) {
						return _getTranslationsJSON("Olá, mundo!");
					}
					else if (targetLanguage.equals("ZH-HANT")) {
						return _getTranslationsJSON("哈囉，世界！");
					}
				}

				return JSONUtil.putAll(
					JSONUtil.put("language", "ES"),
					JSONUtil.put("language", "PT-BR"),
					JSONUtil.put("language", "ZH-HANT")
				).toString();
			}
		);

		return http;
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));

		Mockito.when(
			PortalUtil.stripURLAnchor(Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			new String[] {"https://api-free.deepl.com/v2/languages", ""}
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final DeepLTranslator _deepLTranslator = new DeepLTranslator();

}