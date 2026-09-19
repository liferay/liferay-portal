/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.language;

import com.liferay.batch.engine.configuration.BatchEngineTaskCompanyConfiguration;
import com.liferay.batch.engine.language.LanguageKeyResolver;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Vendel Töreki
 */
public class LanguageKeyResolverImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_language = Mockito.mock(Language.class);

		Set<Locale> locales = new LinkedHashSet<>();

		locales.add(LocaleUtil.BRAZIL);
		locales.add(LocaleUtil.SPAIN);
		locales.add(LocaleUtil.US);

		Mockito.when(
			_language.getAvailableLocales()
		).thenReturn(
			locales
		);

		_configurationProvider = Mockito.mock(ConfigurationProvider.class);

		_batchEngineTaskCompanyConfiguration = Mockito.mock(
			BatchEngineTaskCompanyConfiguration.class);

		Mockito.when(
			_configurationProvider.getCompanyConfiguration(
				BatchEngineTaskCompanyConfiguration.class, _COMPANY_ID)
		).thenReturn(
			_batchEngineTaskCompanyConfiguration
		);

		Mockito.when(
			_batchEngineTaskCompanyConfiguration.languageKeyResolutionEnabled()
		).thenReturn(
			true
		);

		_languageKeyResolver = new LanguageKeyResolverImpl();

		ReflectionTestUtil.setFieldValue(
			_languageKeyResolver, "_configurationProvider",
			_configurationProvider);
		ReflectionTestUtil.setFieldValue(
			_languageKeyResolver, "_language", _language);
	}

	@Test
	public void testExpandExcludesLocalesWithoutTranslation() throws Exception {
		_whenTranslation(LocaleUtil.BRAZIL, "welcome-to-liferay", "Bem-vindo");
		_whenTranslation(LocaleUtil.SPAIN, "welcome-to-liferay", null);
		_whenTranslation(LocaleUtil.US, "welcome-to-liferay", "Welcome");

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			_FOR_EACH_LANGUAGE_ID, "welcome-to-liferay"
		).build();

		_languageKeyResolver.expand(_COMPANY_ID, map);

		Assert.assertEquals(map.toString(), 2, map.size());
		Assert.assertEquals("Welcome", map.get("en_US"));
		Assert.assertEquals("Bem-vindo", map.get("pt_BR"));
		Assert.assertFalse(map.containsKey("es_ES"));
	}

	@Test
	public void testExpandFullLocaleMap() throws Exception {
		_whenTranslation(LocaleUtil.BRAZIL, "welcome-to-liferay", "Bem-vindo");
		_whenTranslation(LocaleUtil.SPAIN, "welcome-to-liferay", "Bienvenido");
		_whenTranslation(LocaleUtil.US, "welcome-to-liferay", "Welcome");

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			_FOR_EACH_LANGUAGE_ID, "welcome-to-liferay"
		).build();

		_languageKeyResolver.expand(_COMPANY_ID, map);

		Assert.assertFalse(map.containsKey(_FOR_EACH_LANGUAGE_ID));
		Assert.assertEquals(map.toString(), 3, map.size());
		Assert.assertEquals("Welcome", map.get("en_US"));
		Assert.assertEquals("Bienvenido", map.get("es_ES"));
		Assert.assertEquals("Bem-vindo", map.get("pt_BR"));
	}

	@Test
	public void testExpandJSONObject() throws Exception {
		_whenTranslation(LocaleUtil.BRAZIL, "welcome-to-liferay", "Bem-vindo");
		_whenTranslation(LocaleUtil.SPAIN, "welcome-to-liferay", "Bienvenido");
		_whenTranslation(LocaleUtil.US, "welcome-to-liferay", "Welcome");

		JSONObject jsonObject = JSONUtil.put(
			"value_i18n",
			JSONUtil.put(_FOR_EACH_LANGUAGE_ID, "welcome-to-liferay"));

		_languageKeyResolver.expand(_COMPANY_ID, jsonObject);

		JSONObject expandedJSONObject = jsonObject.getJSONObject("value_i18n");

		Assert.assertFalse(expandedJSONObject.has(_FOR_EACH_LANGUAGE_ID));
		Assert.assertEquals("Welcome", expandedJSONObject.getString("en_US"));
		Assert.assertEquals(
			"Bienvenido", expandedJSONObject.getString("es_ES"));
		Assert.assertEquals("Bem-vindo", expandedJSONObject.getString("pt_BR"));
	}

	@Test
	public void testExpandNestedMaps() throws Exception {
		_whenTranslation(LocaleUtil.BRAZIL, "welcome-to-liferay", "Bem-vindo");
		_whenTranslation(LocaleUtil.SPAIN, "welcome-to-liferay", "Bienvenido");
		_whenTranslation(LocaleUtil.US, "welcome-to-liferay", "Welcome");

		Map<String, Object> valueI18nMap =
			LinkedHashMapBuilder.<String, Object>put(
				_FOR_EACH_LANGUAGE_ID, "welcome-to-liferay"
			).build();

		_languageKeyResolver.expand(
			_COMPANY_ID,
			LinkedHashMapBuilder.<String, Object>put(
				"fragmentFields",
				List.<Object>of(
					LinkedHashMapBuilder.<String, Object>put(
						"value",
						LinkedHashMapBuilder.<String, Object>put(
							"text", valueI18nMap
						).build()
					).build())
			).build());

		Assert.assertEquals(valueI18nMap.toString(), 3, valueI18nMap.size());
		Assert.assertEquals("Welcome", valueI18nMap.get("en_US"));
		Assert.assertEquals("Bienvenido", valueI18nMap.get("es_ES"));
		Assert.assertEquals("Bem-vindo", valueI18nMap.get("pt_BR"));
	}

	@Test
	public void testExpandPreservesExistingLocaleEntries() throws Exception {
		_whenTranslation(LocaleUtil.BRAZIL, "welcome-to-liferay", "Bem-vindo");
		_whenTranslation(LocaleUtil.SPAIN, "welcome-to-liferay", "Bienvenido");
		_whenTranslation(LocaleUtil.US, "welcome-to-liferay", "Welcome");

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"en_US", "Custom"
		).put(
			_FOR_EACH_LANGUAGE_ID, "welcome-to-liferay"
		).build();

		_languageKeyResolver.expand(_COMPANY_ID, map);

		Assert.assertEquals("Custom", map.get("en_US"));
		Assert.assertEquals("Bienvenido", map.get("es_ES"));
		Assert.assertEquals("Bem-vindo", map.get("pt_BR"));
	}

	@Test
	public void testExpandUnknownKeyLeavesMapEmpty() throws Exception {
		_whenTranslation(LocaleUtil.BRAZIL, "missing-key", null);
		_whenTranslation(LocaleUtil.SPAIN, "missing-key", null);
		_whenTranslation(LocaleUtil.US, "missing-key", null);

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			_FOR_EACH_LANGUAGE_ID, "missing-key"
		).build();

		_languageKeyResolver.expand(_COMPANY_ID, map);

		Assert.assertTrue(map.toString(), map.isEmpty());
	}

	@Test
	public void testExpandWithResolutionDisabledIsNoOp() throws Exception {
		Mockito.when(
			_batchEngineTaskCompanyConfiguration.languageKeyResolutionEnabled()
		).thenReturn(
			false
		);

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			_FOR_EACH_LANGUAGE_ID, "welcome-to-liferay"
		).build();

		_languageKeyResolver.expand(_COMPANY_ID, map);

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertEquals(
			"welcome-to-liferay", map.get(_FOR_EACH_LANGUAGE_ID));
	}

	@Test
	public void testExpandWithoutPlaceholderIsNoOp() throws Exception {
		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"en_US", "Welcome"
		).build();

		_languageKeyResolver.expand(_COMPANY_ID, map);

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertEquals("Welcome", map.get("en_US"));
	}

	private void _whenTranslation(
		Locale locale, String key, String translation) {

		Mockito.when(
			_language.get(locale, key, null)
		).thenReturn(
			translation
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _FOR_EACH_LANGUAGE_ID =
		"[$FOR_EACH_LANGUAGE_ID$]";

	private BatchEngineTaskCompanyConfiguration
		_batchEngineTaskCompanyConfiguration;
	private ConfigurationProvider _configurationProvider;
	private Language _language;
	private LanguageKeyResolver _languageKeyResolver;

}