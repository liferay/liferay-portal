/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.translator.azure.internal;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.azure.internal.configuration.AzureTranslatorConfiguration;

import java.util.ArrayList;
import java.util.Collections;
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
	public void testTranslateWhenUserAgentIsConfigured() throws Exception {
		String userAgent = RandomTestUtil.randomString();

		_setUpAzureTranslator(userAgent);

		_azureTranslator.translate(_getTranslatorPacket("en_US", "ca_ES"));

		Assert.assertEquals(Collections.singletonList(userAgent), _userAgents);
	}

	@Test
	public void testTranslateWhenUserAgentIsNotConfigured() throws Exception {
		_azureTranslator.translate(_getTranslatorPacket("en_US", "ca_ES"));

		Assert.assertEquals(Collections.singletonList("Liferay"), _userAgents);
	}

	private String _getTranslationsJSON(String text) {
		return JSONUtil.putAll(
			JSONUtil.put(
				"translations", JSONUtil.put(JSONUtil.put("text", text)))
		).toString();
	}

	private TranslatorPacket _getTranslatorPacket(
		String sourceLanguage, String targetLanguage) {

		String text = RandomTestUtil.randomString();

		return new TranslatorPacket() {

			@Override
			public long getCompanyId() {
				return _COMPANY_ID;
			}

			@Override
			public Map<String, String> getFieldsMap() {
				return Map.of(_INFO_FIELD_NAME, text);
			}

			@Override
			public Map<String, Boolean> getHTMLMap() {
				return Map.of(_INFO_FIELD_NAME, false);
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

	private void _setUpAzureTranslator(String userAgent) throws Exception {
		ReflectionTestUtil.setFieldValue(
			_azureTranslator, "_configurationProvider",
			_setUpConfigurationProvider(_COMPANY_ID, userAgent));
		ReflectionTestUtil.setFieldValue(
			_azureTranslator, "_http", _setUpHttp());
		ReflectionTestUtil.setFieldValue(
			_azureTranslator, "_jsonFactory", new JSONFactoryImpl());
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

	private Http _setUpHttp() throws Exception {
		Http http = Mockito.mock(Http.class);

		Mockito.when(
			http.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocation -> {
				Http.Options options = invocation.getArgument(0);

				_userAgents.add(options.getHeader(HttpHeaders.USER_AGENT));

				Http.Response httpResponse = new Http.Response();

				httpResponse.setResponseCode(200);

				options.setResponse(httpResponse);

				return _getTranslationsJSON(RandomTestUtil.randomString());
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
			new String[] {
				"https://api.cognitive.microsofttranslator.com/translate", ""
			}
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _INFO_FIELD_NAME =
		"infoField--JournalArticle_title--0";

	private final AzureTranslator _azureTranslator = new AzureTranslator();
	private final List<String> _userAgents = new ArrayList<>();

}