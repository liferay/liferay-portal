/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.portlet.MockPortletRequest;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.SAXReader;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.LocalizationImpl;
import com.liferay.portlet.PortletPreferencesImpl;

import jakarta.portlet.PortletPreferences;

import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Connor McKay
 * @author Péter Borkuti
 */
@RunWith(Arquillian.class)
public class LocalizationImplTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_cache = LocalizationImpl.class.getDeclaredField("_cache");

		_cache.setAccessible(true);

		_defaultLocale = LocaleUtil.getDefault();

		_localization = LocalizationUtil.getLocalization();
	}

	@AfterClass
	public static void tearDownClass() {
		LocaleUtil.setDefault(
			_defaultLocale.getLanguage(), _defaultLocale.getCountry(),
			_defaultLocale.getVariant());
	}

	@Before
	public void setUp() throws Exception {
		StringBundler sb = new StringBundler(10);

		sb.append("<?xml version=\"1.0\"?>");

		sb.append("<root available-locales=\"en_US,es_ES\" ");
		sb.append("default-locale=\"en_US\">");
		sb.append("<static-content language-id=\"es_ES\">");
		sb.append("foo&amp;bar");
		sb.append("</static-content>");
		sb.append("<static-content language-id=\"en_US\">");
		sb.append("<![CDATA[Example in English]]>");
		sb.append("</static-content>");
		sb.append("</root>");

		_xml = sb.toString();

		_cache.set(
			_localization,
			new ConcurrentReferenceKeyHashMap<>(
				FinalizeManager.SOFT_REFERENCE_FACTORY));
	}

	@Test
	public void testChunkedText() {
		String translation = LocalizationUtil.getLocalization(_xml, "es_ES");

		Assert.assertNotNull(translation);
		Assert.assertEquals("foo&bar", translation);
		Assert.assertEquals(7, translation.length());

		translation = LocalizationUtil.getLocalization(_xml, "en_US");

		Assert.assertNotNull(translation);
		Assert.assertEquals(18, translation.length());
	}

	@Test
	public void testGetAvailableLanguageIds() throws DocumentException {
		String[] documentAvailableLanguageIds =
			LocalizationUtil.getAvailableLanguageIds(_saxReader.read(_xml));

		Assert.assertEquals(
			Arrays.toString(documentAvailableLanguageIds), 2,
			documentAvailableLanguageIds.length);

		String[] xmlAvailableLanguageIds =
			LocalizationUtil.getAvailableLanguageIds(_xml);

		Assert.assertEquals(
			Arrays.toString(xmlAvailableLanguageIds), 2,
			xmlAvailableLanguageIds.length);

		Arrays.sort(documentAvailableLanguageIds);
		Arrays.sort(xmlAvailableLanguageIds);

		Assert.assertTrue(
			"Available language IDs between the document and XML do not match",
			Arrays.equals(
				documentAvailableLanguageIds, xmlAvailableLanguageIds));
	}

	@Test
	public void testGetDefaultLanguageId() throws DocumentException {
		Assert.assertEquals(
			"The default language ids from Document and XML do not match",
			LocalizationUtil.getDefaultLanguageId(_saxReader.read(_xml)),
			LocalizationUtil.getDefaultLanguageId(_xml));
	}

	@Test
	public void testGetLocalizationMapWithNoValues() {
		Map<Locale, String> map = LocalizationUtil.getMap(
			new LocalizedValuesMap("defaultValue"));

		Assert.assertEquals(map.toString(), 1, map.size());
		Assert.assertEquals("defaultValue", map.get(LocaleUtil.getDefault()));
	}

	@Test
	public void testGetLocalizationMapWithTwoValues() {
		LocalizedValuesMap localizedValuesMap = new LocalizedValuesMap(
			"defaultValue");

		localizedValuesMap.put(LocaleUtil.GERMANY, _GERMAN_HELLO);
		localizedValuesMap.put(LocaleUtil.US, _ENGLISH_HELLO);

		Set<Locale> locales = new HashSet<>(
			Arrays.asList(
				LocaleUtil.getDefault(), LocaleUtil.GERMANY, LocaleUtil.US));

		Map<Locale, String> map = LocalizationUtil.getMap(localizedValuesMap);

		Assert.assertEquals(map.toString(), locales.size(), map.size());
		Assert.assertEquals(_GERMAN_HELLO, map.get(LocaleUtil.GERMANY));
		Assert.assertEquals(_ENGLISH_HELLO, map.get(LocaleUtil.US));
	}

	@Test
	public void testGetLocalizationXml() {
		String xml = LocalizationUtil.getXml(
			new LocalizedValuesMap("defaultValue"), "key");

		for (Locale locale : _language.getAvailableLocales()) {
			Assert.assertTrue(
				"Key for " + locale + "included in XML",
				xml.contains(
					"<key language-id=\"" + locale + "\">defaultValue</key>"));
		}

		Assert.assertTrue(
			"Default locale included in XML",
			xml.contains("default-locale=\"" + LocaleUtil.getDefault() + "\""));
	}

	@Test
	public void testGetLocalizationXmlFromPreferences() throws Exception {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		LocalizationUtil.setPreferencesValue(
			portletPreferences, "test", _ENGLISH_LANGUAGE_ID, "changedValue");

		String xml = LocalizationUtil.getLocalizationXmlFromPreferences(
			portletPreferences, new MockPortletRequest(), "test", "testValue");

		Assert.assertTrue(
			"Portlet preferences were not properly applied to XML: " + xml,
			xml.contains(
				"<test language-id=\"" + _ENGLISH_LANGUAGE_ID +
					"\">changedValue</test>"));
	}

	@Test
	public void testGetLocalizationXmlFromPreferencesWithEmptyPreferences() {
		String xml = LocalizationUtil.getLocalizationXmlFromPreferences(
			new PortletPreferencesImpl(), new MockPortletRequest(), "test",
			"testValue");

		Assert.assertTrue(
			"Default values were not included in XML: " + xml,
			xml.contains(
				"<test language-id=\"" + LocaleUtil.getDefault() +
					"\">testValue</test>"));
	}

	@Test
	public void testGetPreferencesValue() throws Exception {
		String key = RandomTestUtil.randomString();

		LocaleUtil.setDefault(
			LocaleUtil.US.getLanguage(), LocaleUtil.US.getCountry(),
			LocaleUtil.US.getVariant());

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		LocalizationUtil.setPreferencesValue(
			portletPreferences, key, _ENGLISH_LANGUAGE_ID, "A");
		LocalizationUtil.setPreferencesValue(
			portletPreferences, key, _GERMAN_LANGUAGE_ID, "B");

		Assert.assertEquals(
			"A",
			LocalizationUtil.getPreferencesValue(
				portletPreferences, key, _ENGLISH_LANGUAGE_ID));
		Assert.assertEquals(
			"B",
			LocalizationUtil.getPreferencesValue(
				portletPreferences, key, _GERMAN_LANGUAGE_ID));
		Assert.assertEquals(
			"A",
			LocalizationUtil.getPreferencesValue(
				portletPreferences, key, _SPANISH_LANGUAGE_ID));

		LocaleUtil.setDefault(
			LocaleUtil.GERMANY.getLanguage(), LocaleUtil.GERMANY.getCountry(),
			LocaleUtil.GERMANY.getVariant());

		Assert.assertEquals(
			"A",
			LocalizationUtil.getPreferencesValue(
				portletPreferences, key, _ENGLISH_LANGUAGE_ID));
		Assert.assertEquals(
			"B",
			LocalizationUtil.getPreferencesValue(
				portletPreferences, key, _GERMAN_LANGUAGE_ID));
		Assert.assertEquals(
			"B",
			LocalizationUtil.getPreferencesValue(
				portletPreferences, key, _SPANISH_LANGUAGE_ID));
	}

	@Test
	public void testLocalizationsXML() {
		String xml = StringPool.BLANK;

		xml = LocalizationUtil.updateLocalization(
			xml, "greeting", _ENGLISH_HELLO, _ENGLISH_LANGUAGE_ID,
			_ENGLISH_LANGUAGE_ID);
		xml = LocalizationUtil.updateLocalization(
			xml, "greeting", _GERMAN_HELLO, _GERMAN_LANGUAGE_ID,
			_ENGLISH_LANGUAGE_ID);

		Assert.assertEquals(
			_ENGLISH_HELLO,
			LocalizationUtil.getLocalization(xml, _ENGLISH_LANGUAGE_ID));
		Assert.assertEquals(
			_GERMAN_HELLO,
			LocalizationUtil.getLocalization(xml, _GERMAN_LANGUAGE_ID));
	}

	@Test
	public void testLocalizationsXMLDefaultValue() {
		String xml = StringPool.BLANK;

		xml = LocalizationUtil.updateLocalization(
			xml, "greeting", _ENGLISH_HELLO, _ENGLISH_LANGUAGE_ID,
			_ENGLISH_LANGUAGE_ID);
		xml = LocalizationUtil.updateLocalization(
			xml, "greeting", _GERMAN_HELLO, _GERMAN_LANGUAGE_ID,
			_ENGLISH_LANGUAGE_ID);

		String defaultValue = "Default Value";

		Assert.assertEquals(
			defaultValue,
			LocalizationUtil.getLocalization(
				xml, _SPANISH_LANGUAGE_ID, false, defaultValue));
		Assert.assertEquals(
			_ENGLISH_HELLO,
			LocalizationUtil.getLocalization(
				xml, _SPANISH_LANGUAGE_ID, true, defaultValue));
	}

	@Test
	public void testLocalizationsXMLUseDefault() {
		String xml = StringPool.BLANK;

		xml = LocalizationUtil.updateLocalization(
			xml, "greeting", _ENGLISH_HELLO, _ENGLISH_LANGUAGE_ID,
			_ENGLISH_LANGUAGE_ID);
		xml = LocalizationUtil.updateLocalization(
			xml, "greeting", _GERMAN_HELLO, _GERMAN_LANGUAGE_ID,
			_ENGLISH_LANGUAGE_ID);

		Assert.assertEquals(
			_ENGLISH_HELLO,
			LocalizationUtil.getLocalization(xml, _SPANISH_LANGUAGE_ID, true));
	}

	@Test
	public void testPreferencesLocalization() throws Exception {
		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		LocalizationUtil.setPreferencesValue(
			portletPreferences, "greeting", _ENGLISH_LANGUAGE_ID,
			_ENGLISH_HELLO);
		LocalizationUtil.setPreferencesValue(
			portletPreferences, "greeting", _GERMAN_LANGUAGE_ID, _GERMAN_HELLO);

		Assert.assertEquals(
			_ENGLISH_HELLO,
			LocalizationUtil.getPreferencesValue(
				portletPreferences, "greeting", _ENGLISH_LANGUAGE_ID));
		Assert.assertEquals(
			_GERMAN_HELLO,
			LocalizationUtil.getPreferencesValue(
				portletPreferences, "greeting", _GERMAN_LANGUAGE_ID));
	}

	@Test
	public void testSetLocalizedPreferencesValues() throws Exception {
		MockPortletRequest request = new MockPortletRequest();

		request.setParameter(
			"greeting_" + _ENGLISH_LANGUAGE_ID, _ENGLISH_HELLO);
		request.setParameter("greeting_" + _GERMAN_LANGUAGE_ID, _GERMAN_HELLO);

		PortletPreferences portletPreferences = new PortletPreferencesImpl();

		LocalizationUtil.setLocalizedPreferencesValues(
			request, portletPreferences, "greeting");

		Assert.assertEquals(
			_ENGLISH_HELLO,
			LocalizationUtil.getPreferencesValue(
				portletPreferences, "greeting", _ENGLISH_LANGUAGE_ID));
		Assert.assertEquals(
			_GERMAN_HELLO,
			LocalizationUtil.getPreferencesValue(
				portletPreferences, "greeting", _GERMAN_LANGUAGE_ID));
	}

	@Test
	public void testUpdateLocalization() {
		Map<Locale, String> localizationMap = HashMapBuilder.put(
			LocaleUtil.US, _ENGLISH_HELLO
		).build();

		StringBundler sb = new StringBundler(10);

		sb.append("<?xml version='1.0' encoding='UTF-8'?>");
		sb.append("<root available-locales=\"en_US,de_DE\" ");
		sb.append("default-locale=\"en_US\">");
		sb.append("<greeting language-id=\"de_DE\">");
		sb.append("<![CDATA[Beispiel auf Deutsch]]>");
		sb.append("</greeting>");
		sb.append("<greeting language-id=\"en_US\">");
		sb.append("<![CDATA[Example in English]]>");
		sb.append("</greeting>");
		sb.append("</root>");

		String xml = LocalizationUtil.updateLocalization(
			localizationMap, sb.toString(), "greeting",
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()));

		Assert.assertEquals(
			_ENGLISH_HELLO,
			LocalizationUtil.getLocalization(xml, _ENGLISH_LANGUAGE_ID, false));
		Assert.assertEquals(
			StringPool.BLANK,
			LocalizationUtil.getLocalization(xml, _GERMAN_LANGUAGE_ID, false));
	}

	@Test
	public void testUpdateLocalizationWithAmpersand() {
		String englishValue = "foo&bar";
		String spanishValue = "bar&foo";

		String xml = LocalizationUtil.updateLocalization(
			HashMapBuilder.put(
				LocaleUtil.SPAIN, spanishValue
			).put(
				LocaleUtil.US, englishValue
			).build(),
			_xml, "static-content", "en_US");

		Assert.assertEquals(
			spanishValue,
			LocalizationUtil.getLocalization(xml, _SPANISH_LANGUAGE_ID));
		Assert.assertEquals(
			englishValue,
			LocalizationUtil.getLocalization(xml, _ENGLISH_LANGUAGE_ID));
	}

	@Test
	public void testUpdateLocalizationWithInvalidXmlCharacter() {
		Assert.assertEquals(
			_ENGLISH_HELLO,
			LocalizationUtil.getLocalization(
				LocalizationUtil.updateLocalization(
					StringPool.BLANK, "greeting", _INVALID_ENGLISH_HELLO,
					_ENGLISH_LANGUAGE_ID, _ENGLISH_LANGUAGE_ID),
				_ENGLISH_LANGUAGE_ID));
	}

	@Test
	public void testUpdateLocalizationWithUTF8() {
		String xml = LocalizationUtil.updateLocalization(
			StringPool.BLANK, "greeting", _HELLO_SURFER_UTF8,
			_ENGLISH_LANGUAGE_ID, _ENGLISH_LANGUAGE_ID);

		Assert.assertEquals(
			_HELLO_SURFER_UTF8,
			LocalizationUtil.getLocalization(xml, _ENGLISH_LANGUAGE_ID));
	}

	private static final String _ENGLISH_HELLO = "Hello World";

	private static final String _ENGLISH_LANGUAGE_ID = LocaleUtil.toLanguageId(
		LocaleUtil.US);

	private static final String _GERMAN_HELLO = "Hallo Welt";

	private static final String _GERMAN_LANGUAGE_ID = LocaleUtil.toLanguageId(
		LocaleUtil.GERMANY);

	private static final String _HELLO_SURFER_UTF8 = "Hello \uD83C\uDFC4";

	private static final String _INVALID_ENGLISH_HELLO = "Hello\u0008 World";

	private static final String _SPANISH_LANGUAGE_ID = LocaleUtil.toLanguageId(
		LocaleUtil.SPAIN);

	private static Field _cache;
	private static Locale _defaultLocale;
	private static Localization _localization;

	@Inject
	private Language _language;

	@Inject
	private SAXReader _saxReader;

	private String _xml;

}