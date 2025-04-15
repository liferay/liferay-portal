/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.BadRequestException;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Víctor Galán
 */
public class LocalizedMapUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetI18nMap() {
		Map<String, String> i18nMap = LocalizedMapUtil.getI18nMap(
			HashMapBuilder.put(
				LocaleUtil.FRANCE, "bonjour"
			).put(
				LocaleUtil.US, "hello"
			).build());

		Assert.assertEquals(i18nMap.toString(), 2, i18nMap.size());
		Assert.assertEquals("hello", i18nMap.get("en-US"));
		Assert.assertEquals("bonjour", i18nMap.get("fr-FR"));

		i18nMap = LocalizedMapUtil.getI18nMap(
			false,
			HashMapBuilder.put(
				LocaleUtil.FRANCE, "bonjour"
			).put(
				LocaleUtil.US, "hello"
			).build());

		Assert.assertNull(i18nMap);

		Set<Locale> availableLocales = new HashSet<>();

		availableLocales.add(LocaleUtil.BRAZIL);
		availableLocales.add(LocaleUtil.FRANCE);
		availableLocales.add(LocaleUtil.US);

		i18nMap = LocalizedMapUtil.getI18nMap(
			true, availableLocales,
			HashMapBuilder.put(
				"en_US", "hello"
			).put(
				"fr_FR", "bonjour"
			).put(
				"hu_HU", "szia"
			).build());

		Assert.assertEquals(i18nMap.toString(), 2, i18nMap.size());
		Assert.assertEquals("hello", i18nMap.get("en_US"));
		Assert.assertEquals("bonjour", i18nMap.get("fr_FR"));
	}

	@Test
	public void testMergeI18nMap() {
		Map<String, String> map = LocalizedMapUtil.mergeI18nMap(
			HashMapBuilder.put(
				"en_US", "Brazil"
			).build(),
			null, "Brasil");

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertEquals("Brazil", map.get("en_US"));

		map = LocalizedMapUtil.mergeI18nMap(null, "pt_BR", "Brasil");

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertEquals("Brasil", map.get("pt_BR"));

		map = LocalizedMapUtil.mergeI18nMap(
			HashMapBuilder.put(
				"pt_BR", "Brasil"
			).build(),
			"en_US", "Brazil");

		Assert.assertEquals(map.toString(), 2, map.size());
		Assert.assertEquals("Brazil", map.get("en_US"));
		Assert.assertEquals("Brasil", map.get("pt_BR"));

		map = LocalizedMapUtil.mergeI18nMap(
			HashMapBuilder.put(
				"en_US", "Brazil"
			).build(),
			"en_US", null);

		Assert.assertEquals(map.toString(), 0, map.size());
		Assert.assertNull(map.get("en_US"));
	}

	@Test
	public void testMergeLocalizedMap() {

		// Null map

		Map<Locale, String> map = LocalizedMapUtil.mergeLocalizedMap(
			null, new AbstractMap.SimpleEntry<>(LocaleUtil.US, "hello"));

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertEquals("hello", map.get(LocaleUtil.US));

		// Null entry

		map = LocalizedMapUtil.mergeLocalizedMap(
			HashMapBuilder.put(
				LocaleUtil.US, "hello"
			).build(),
			null);

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertEquals("hello", map.get(LocaleUtil.US));

		// Entry hello null

		map = LocalizedMapUtil.mergeLocalizedMap(
			HashMapBuilder.put(
				LocaleUtil.US, "hello"
			).build(),
			new AbstractMap.SimpleEntry<>(LocaleUtil.US, null));

		Assert.assertEquals(map.toString(), 0, map.size());
		Assert.assertNull(map.get(LocaleUtil.US));

		// Merge map

		map = LocalizedMapUtil.mergeLocalizedMap(
			HashMapBuilder.put(
				LocaleUtil.US, "hello"
			).build(),
			new AbstractMap.SimpleEntry<>(LocaleUtil.FRANCE, "bonjour"));

		Assert.assertEquals(map.toString(), 2, map.size());
		Assert.assertEquals("bonjour", map.get(LocaleUtil.FRANCE));
		Assert.assertEquals("hello", map.get(LocaleUtil.US));
	}

	@Test
	public void testPopulateI18nMap() {

		// Do not populate international map if both default language and site
		// default value are undefined

		Map<String, String> i18nMap = HashMapBuilder.put(
			"pt_BR", RandomTestUtil.randomString()
		).build();

		Assert.assertEquals(
			i18nMap, LocalizedMapUtil.populateI18nMap(null, i18nMap, null));

		// Do not populate international map if site default value is already
		// defined

		i18nMap = HashMapBuilder.put(
			"en_US", RandomTestUtil.randomString()
		).build();

		Assert.assertEquals(
			i18nMap,
			LocalizedMapUtil.populateI18nMap(
				RandomTestUtil.randomString(), i18nMap,
				RandomTestUtil.randomString()));

		// Populate international map with default language value

		String defaultValue = RandomTestUtil.randomString();

		Assert.assertEquals(
			HashMapBuilder.put(
				"en_US", defaultValue
			).put(
				"pt_BR", defaultValue
			).build(),
			LocalizedMapUtil.populateI18nMap(
				"pt_BR",
				HashMapBuilder.put(
					"pt-BR", defaultValue
				).build(),
				RandomTestUtil.randomString()));
		Assert.assertEquals(
			HashMapBuilder.put(
				"en_US", defaultValue
			).put(
				"pt_BR", defaultValue
			).build(),
			LocalizedMapUtil.populateI18nMap(
				"pt_BR",
				HashMapBuilder.put(
					"pt_BR", defaultValue
				).build(),
				RandomTestUtil.randomString()));

		// Populate international map with english value when default language
		// value is undefined

		Locale locale = LocaleUtil.getDefault();

		try {
			_setDefaultLocale(LocaleUtil.BRAZIL);

			String englishValue = RandomTestUtil.randomString();

			Assert.assertEquals(
				HashMapBuilder.put(
					"en_US", englishValue
				).put(
					"pt_BR", englishValue
				).build(),
				LocalizedMapUtil.populateI18nMap(
					null,
					HashMapBuilder.put(
						"en_US", englishValue
					).build(),
					null));
		}
		finally {
			_setDefaultLocale(locale);
		}

		// Populate international map with site default value when default
		// language is undefined

		String siteDefaultValue = RandomTestUtil.randomString();

		Assert.assertEquals(
			HashMapBuilder.put(
				"en_US", siteDefaultValue
			).build(),
			LocalizedMapUtil.populateI18nMap("pt_BR", null, siteDefaultValue));
		Assert.assertEquals(
			HashMapBuilder.put(
				"en_US", siteDefaultValue
			).build(),
			LocalizedMapUtil.populateI18nMap(
				"pt_BR", Collections.emptyMap(), siteDefaultValue));
	}

	@Test
	public void testValidateI18n() {
		String randomEntityName = RandomTestUtil.randomString();

		Set<Locale> notFoundLocales = new HashSet<Locale>() {
			{
				add(LocaleUtil.CHINESE);
				add(LocaleUtil.GERMAN);
			}
		};

		try {
			LocalizedMapUtil.validateI18n(
				false, LocaleUtil.ENGLISH, randomEntityName,
				HashMapBuilder.put(
					LocaleUtil.ENGLISH, RandomTestUtil.randomString()
				).build(),
				notFoundLocales);

			Assert.fail();
		}
		catch (BadRequestException badRequestException) {
			String message = badRequestException.getMessage();

			List<Locale> missingNotFoundLocales = new ArrayList<>();

			for (Locale notFoundLocale : notFoundLocales) {
				if (!message.contains(notFoundLocale.toString())) {
					missingNotFoundLocales.add(notFoundLocale);
				}
			}

			Assert.assertTrue(missingNotFoundLocales.isEmpty());
		}
	}

	private void _setDefaultLocale(Locale locale) {
		LocaleUtil.setDefault(
			locale.getLanguage(), locale.getCountry(), locale.getVariant());
	}

}