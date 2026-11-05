/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.azure.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.azure.internal.configuration.AzureTranslatorConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Ben Demetrius
 */
public class AzureTranslatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpAzureTranslator(null);
		_setUpPortalUtil();
	}

	@Test
	public void testTranslateWhenFieldsAreEmpty() throws Exception {
		TranslatorPacket translatorPacket = _azureTranslator.translate(
			_getTranslatorPacket(
				Collections.emptyMap(), Collections.emptyMap()));

		Assert.assertEquals(
			Collections.emptyMap(), translatorPacket.getFieldsMap());

		Assert.assertEquals(Collections.emptyMap(), _texts);
		Assert.assertEquals(Collections.emptyList(), _userAgents);
	}

	@Test
	public void testTranslateWhenFieldsAreHTML() throws Exception {
		String fieldName = RandomTestUtil.randomString();
		String text = StringBundler.concat(
			"<p>", RandomTestUtil.randomString(), "</p>");

		TranslatorPacket translatorPacket = _azureTranslator.translate(
			_getTranslatorPacket(
				Map.of(fieldName, text), Map.of(fieldName, true)));

		Assert.assertEquals(
			Map.of(fieldName, text), translatorPacket.getFieldsMap());

		Assert.assertEquals(
			Map.of("html", Collections.singletonList(text)), _texts);
	}

	@Test
	public void testTranslateWhenFieldsAreHTMLAndPlainText() throws Exception {
		String htmlFieldName = RandomTestUtil.randomString();
		String htmlText = StringBundler.concat(
			"<a class=\"", RandomTestUtil.randomString(), "\" href=\"/",
			RandomTestUtil.randomString(), "\">", RandomTestUtil.randomString(),
			"</a>");
		String plainFieldName = RandomTestUtil.randomString();
		String plainText = RandomTestUtil.randomString();

		Map<String, String> fieldsMap = HashMapBuilder.put(
			htmlFieldName, htmlText
		).put(
			plainFieldName, plainText
		).build();

		TranslatorPacket translatorPacket = _azureTranslator.translate(
			_getTranslatorPacket(
				fieldsMap,
				HashMapBuilder.put(
					htmlFieldName, true
				).put(
					plainFieldName, false
				).build()));

		Assert.assertEquals(fieldsMap, translatorPacket.getFieldsMap());

		Assert.assertEquals(
			HashMapBuilder.put(
				"html", Collections.singletonList(htmlText)
			).put(
				"plain", Collections.singletonList(plainText)
			).build(),
			_texts);
	}

	@Test
	public void testTranslateWhenFieldsAreInterleaved() throws Exception {
		String htmlFieldName1 = RandomTestUtil.randomString();
		String htmlFieldName2 = RandomTestUtil.randomString();
		String htmlText1 = StringBundler.concat(
			"<b>", RandomTestUtil.randomString(), "</b>");
		String htmlText2 = StringBundler.concat(
			"<b>", RandomTestUtil.randomString(), "</b>");
		String plainFieldName1 = RandomTestUtil.randomString();
		String plainFieldName2 = RandomTestUtil.randomString();
		String plainText1 = RandomTestUtil.randomString();
		String plainText2 = RandomTestUtil.randomString();

		Map<String, String> fieldsMap = LinkedHashMapBuilder.put(
			htmlFieldName1, htmlText1
		).put(
			plainFieldName1, plainText1
		).put(
			htmlFieldName2, htmlText2
		).put(
			plainFieldName2, plainText2
		).build();

		Map<String, Boolean> htmlMap = LinkedHashMapBuilder.put(
			htmlFieldName1, true
		).put(
			plainFieldName1, false
		).put(
			htmlFieldName2, true
		).put(
			plainFieldName2, false
		).build();

		TranslatorPacket translatorPacket = _azureTranslator.translate(
			_getTranslatorPacket(fieldsMap, htmlMap));

		Assert.assertEquals(fieldsMap, translatorPacket.getFieldsMap());
		Assert.assertEquals(htmlMap, translatorPacket.getHTMLMap());
		Assert.assertEquals("en_US", translatorPacket.getSourceLanguageId());
		Assert.assertEquals("ca_ES", translatorPacket.getTargetLanguageId());

		Assert.assertEquals(
			HashMapBuilder.put(
				"html", List.of(htmlText1, htmlText2)
			).put(
				"plain", List.of(plainText1, plainText2)
			).build(),
			_texts);

		Assert.assertEquals(List.of("Liferay", "Liferay"), _userAgents);
	}

	@Test
	public void testTranslateWhenHTMLFlagsAreNull() throws Exception {
		String htmlFieldName = RandomTestUtil.randomString();
		String htmlText = StringBundler.concat(
			"<p>", RandomTestUtil.randomString(), "</p>");
		String plainText = RandomTestUtil.randomString();

		Map<String, String> fieldsMap = LinkedHashMapBuilder.put(
			htmlFieldName, htmlText
		).put(
			RandomTestUtil.randomString(), plainText
		).build();

		TranslatorPacket translatorPacket = _azureTranslator.translate(
			_getTranslatorPacket(
				fieldsMap, Collections.singletonMap(htmlFieldName, null)));

		Assert.assertEquals(fieldsMap, translatorPacket.getFieldsMap());

		Assert.assertEquals(
			Map.of("plain", List.of(htmlText, plainText)), _texts);
	}

	@Test
	public void testTranslateWhenResponseCodeIsNotOK() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_azureTranslator, "_http", _setUpHttp(500));

		try {
			_azureTranslator.translate(_getTranslatorPacket());

			Assert.fail();
		}
		catch (PortalException portalException) {
			Assert.assertEquals(
				"Response code 500", portalException.getMessage());
		}
	}

	@Test
	public void testTranslateWhenUserAgentIsConfigured() throws Exception {
		String userAgent = RandomTestUtil.randomString();

		_setUpAzureTranslator(userAgent);

		_azureTranslator.translate(_getTranslatorPacket());

		Assert.assertEquals(Collections.singletonList(userAgent), _userAgents);
	}

	@Test
	public void testTranslateWhenUserAgentIsNotConfigured() throws Exception {
		_azureTranslator.translate(_getTranslatorPacket());

		Assert.assertEquals(Collections.singletonList("Liferay"), _userAgents);
	}

	private TranslatorPacket _getTranslatorPacket() {
		return _getTranslatorPacket(
			Map.of(_INFO_FIELD_NAME, RandomTestUtil.randomString()),
			Map.of(_INFO_FIELD_NAME, false));
	}

	private TranslatorPacket _getTranslatorPacket(
		Map<String, String> fieldsMap, Map<String, Boolean> htmlMap) {

		return new TranslatorPacket() {

			@Override
			public long getCompanyId() {
				return _COMPANY_ID;
			}

			@Override
			public Map<String, String> getFieldsMap() {
				return fieldsMap;
			}

			@Override
			public Map<String, Boolean> getHTMLMap() {
				return htmlMap;
			}

			@Override
			public String getSourceLanguageId() {
				return "en_US";
			}

			@Override
			public String getTargetLanguageId() {
				return "ca_ES";
			}

		};
	}

	private void _setUpAzureTranslator(String userAgent) throws Exception {
		ReflectionTestUtil.setFieldValue(
			_azureTranslator, "_configurationProvider",
			_setUpConfigurationProvider(_COMPANY_ID, userAgent));
		ReflectionTestUtil.setFieldValue(
			_azureTranslator, "_http", _setUpHttp(200));
		ReflectionTestUtil.setFieldValue(
			_azureTranslator, "_jsonFactory", _jsonFactoryImpl);

		SecretResolver secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			secretResolver.resolve(Mockito.anyLong(), Mockito.any())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);

		ReflectionTestUtil.setFieldValue(
			_azureTranslator, "_secretResolver", secretResolver);
	}

	private ConfigurationProvider _setUpConfigurationProvider(
			long companyId, String userAgent)
		throws Exception {

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		AzureTranslatorConfiguration azureTranslatorConfiguration =
			Mockito.mock(AzureTranslatorConfiguration.class);

		Mockito.when(
			azureTranslatorConfiguration.enabled()
		).thenReturn(
			true
		);

		Mockito.when(
			azureTranslatorConfiguration.resourceLocation()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			azureTranslatorConfiguration.subscriptionKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			azureTranslatorConfiguration.userAgent()
		).thenReturn(
			userAgent
		);

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				AzureTranslatorConfiguration.class, companyId)
		).thenReturn(
			azureTranslatorConfiguration
		);

		return configurationProvider;
	}

	private Http _setUpHttp(int responseCode) throws Exception {
		Http http = Mockito.mock(Http.class);

		Mockito.when(
			http.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocation -> {
				Http.Options options = invocation.getArgument(0);

				_userAgents.add(options.getHeader(HttpHeaders.USER_AGENT));

				Http.Response httpResponse = new Http.Response();

				httpResponse.setResponseCode(responseCode);

				options.setResponse(httpResponse);

				Http.Body body = options.getBody();

				JSONArray requestJSONArray = _jsonFactoryImpl.createJSONArray(
					body.getContent());

				List<String> requestTexts = JSONUtil.toStringList(
					requestJSONArray, "Text");

				List<String> texts = _texts.computeIfAbsent(
					HttpComponentsUtil.getParameter(
						options.getLocation(), "textType", false),
					textType -> new ArrayList<>());

				texts.addAll(requestTexts);

				JSONArray responseJSONArray = JSONUtil.toJSONArray(
					requestTexts,
					text -> JSONUtil.put(
						"translations",
						JSONUtil.put(JSONUtil.put("text", text))));

				return responseJSONArray.toString();
			}
		);

		return http;
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));

		Mockito.when(
			PortalUtil.stripURLAnchor(Mockito.anyString(), Mockito.anyString())
		).thenAnswer(
			invocation -> new String[] {invocation.getArgument(0), ""}
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _INFO_FIELD_NAME =
		"infoField--JournalArticle_title--0";

	private final AzureTranslator _azureTranslator = new AzureTranslator();
	private final JSONFactoryImpl _jsonFactoryImpl = new JSONFactoryImpl();
	private final Map<String, List<String>> _texts = new HashMap<>();
	private final List<String> _userAgents = new ArrayList<>();

}