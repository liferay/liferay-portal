/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Eduardo Lundgren
 */
public class LocaleUtil {

	public static final Locale BRAZIL = new Locale("pt", "BR");

	public static final Locale CANADA = Locale.CANADA;

	public static final Locale CANADA_FRENCH = Locale.CANADA_FRENCH;

	public static final Locale CHINA = Locale.CHINA;

	public static final Locale CHINESE = Locale.CHINESE;

	public static final Locale ENGLISH = Locale.ENGLISH;

	public static final Locale FRANCE = Locale.FRANCE;

	public static final Locale FRENCH = Locale.FRENCH;

	public static final Locale GERMAN = Locale.GERMAN;

	public static final Locale GERMANY = Locale.GERMANY;

	public static final Locale HUNGARY = new Locale("hu", "HU");

	public static final Locale ITALIAN = Locale.ITALIAN;

	public static final Locale ITALY = Locale.ITALY;

	public static final Locale JAPAN = Locale.JAPAN;

	public static final Locale JAPANESE = Locale.JAPANESE;

	public static final Locale KOREA = Locale.KOREA;

	public static final Locale KOREAN = Locale.KOREAN;

	public static final Locale NETHERLANDS = new Locale("nl", "NL");

	public static final Locale PORTUGAL = new Locale("pt", "PT");

	public static final Locale PRC = Locale.PRC;

	public static final Locale ROOT = Locale.ROOT;

	public static final Locale SIMPLIFIED_CHINESE = Locale.SIMPLIFIED_CHINESE;

	public static final Locale SPAIN = new Locale("es", "ES");

	public static final Locale TAIWAN = Locale.TAIWAN;

	public static final Locale TRADITIONAL_CHINESE = Locale.TRADITIONAL_CHINESE;

	public static final Locale UK = Locale.UK;

	public static final Locale US = Locale.US;

	public static boolean equals(Locale locale1, Locale locale2) {
		String languageId1 = toLanguageId(locale1);
		String languageId2 = toLanguageId(locale2);

		return StringUtil.equalsIgnoreCase(languageId1, languageId2);
	}

	public static Locale fromLanguageId(String languageId) {
		return fromLanguageId(languageId, true);
	}

	public static Locale fromLanguageId(String languageId, boolean validate) {
		return fromLanguageId(languageId, validate, true);
	}

	public static Locale fromLanguageId(
		String languageId, boolean validate, boolean useDefault) {

		if (languageId == null) {
			if (useDefault) {
				return getDefault();
			}

			return null;
		}

		Locale locale = _fromLanguageId(languageId);

		if (validate) {
			boolean languageCode = false;

			if ((languageId.indexOf(CharPool.UNDERLINE) < 0) &&
				(languageId.indexOf(CharPool.MINUS) < 0)) {

				languageCode = true;
			}

			if ((languageCode &&
				 !LanguageUtil.isAvailableLanguageCode(languageId)) ||
				(!languageCode && !LanguageUtil.isAvailableLocale(locale))) {

				if (_log.isWarnEnabled()) {
					_log.warn(languageId + " is not a valid language id");
				}

				if (useDefault) {
					return getDefault();
				}

				return null;
			}
		}

		return locale;
	}

	public static Locale[] fromLanguageIds(List<String> languageIds) {
		Locale[] locales = new Locale[languageIds.size()];

		for (int i = 0; i < languageIds.size(); i++) {
			locales[i] = fromLanguageId(languageIds.get(i), true);
		}

		return locales;
	}

	public static Locale[] fromLanguageIds(String[] languageIds) {
		Locale[] locales = new Locale[languageIds.length];

		for (int i = 0; i < languageIds.length; i++) {
			locales[i] = fromLanguageId(languageIds[i], true);
		}

		return locales;
	}

	public static Locale getDefault() {
		Locale locale = LocaleThreadLocal.getDefaultLocale();

		if (locale != null) {
			return locale;
		}

		return _locale;
	}

	public static Map<String, String> getISOLanguages(Locale locale) {
		Map<String, String> isoLanguages = new TreeMap<>(
			String.CASE_INSENSITIVE_ORDER);

		for (String isoLanguageId : Locale.getISOLanguages()) {
			Locale isoLocale = fromLanguageId(isoLanguageId, true);

			isoLanguages.put(
				isoLocale.getDisplayLanguage(locale), isoLanguageId);
		}

		return isoLanguages;
	}

	public static String getLocaleDisplayName(
		Locale displayLocale, Locale locale) {

		String key = "language." + displayLocale.getLanguage();

		String displayName = LanguageUtil.get(locale, key);

		if (displayName.equals(key)) {
			return displayLocale.getDisplayName(locale);
		}

		String country = _getDisplayCountry(displayLocale, locale);

		if (Validator.isNull(country)) {
			return displayName;
		}

		return StringBundler.concat(
			displayName, StringPool.SPACE, StringPool.OPEN_PARENTHESIS, country,
			StringPool.CLOSE_PARENTHESIS);
	}

	public static String getLongDisplayName(
		Locale locale, Set<String> duplicateLanguages) {

		return _getDisplayName(
			locale.getDisplayLanguage(locale),
			_getDisplayCountry(locale, locale), locale, duplicateLanguages);
	}

	public static Locale getMostRelevantLocale() {
		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		if (locale == null) {
			locale = getDefault();
		}

		return locale;
	}

	public static String getShortDisplayName(
		Locale locale, Set<String> duplicateLanguages) {

		String language = locale.getDisplayLanguage(locale);

		if (language.length() > 3) {
			language = locale.getLanguage();
			language = StringUtil.toUpperCase(language);
		}

		return _getDisplayName(
			language, StringUtil.toUpperCase(locale.getCountry()), locale,
			duplicateLanguages);
	}

	public static Locale getSiteDefault() {
		Locale locale = LocaleThreadLocal.getSiteDefaultLocale();

		if (locale != null) {
			return locale;
		}

		return getDefault();
	}

	public static void setDefault(
		String userLanguage, String userCountry, String userVariant) {

		if (Validator.isNotNull(userLanguage) &&
			Validator.isNull(userCountry) && Validator.isNull(userVariant)) {

			_locale = new Locale(userLanguage);
		}
		else if (Validator.isNotNull(userLanguage) &&
				 Validator.isNotNull(userCountry) &&
				 Validator.isNull(userVariant)) {

			_locale = new Locale(userLanguage, userCountry);
		}
		else if (Validator.isNotNull(userLanguage) &&
				 Validator.isNotNull(userCountry) &&
				 Validator.isNotNull(userVariant)) {

			_locale = new Locale(userLanguage, userCountry, userVariant);
		}

		LocaleThreadLocal.setDefaultLocale(_locale);
	}

	public static String toBCP47LangTag(Locale locale) {
		String languageId = toLanguageId(locale);

		if (languageId.equals("zh_CN")) {
			return "zh-Hans";
		}
		else if (languageId.equals("zh_TW")) {
			return "zh-Hant";
		}

		return toBCP47LanguageId(languageId);
	}

	public static String toBCP47LanguageId(Locale locale) {
		return toBCP47LanguageId(toLanguageId(locale));
	}

	public static String toBCP47LanguageId(String languageId) {
		if (languageId.equals("zh_CN")) {
			return "zh-Hans-CN";
		}
		else if (languageId.equals("zh_TW")) {
			return "zh-Hant-TW";
		}

		return StringUtil.replace(
			languageId, CharPool.UNDERLINE, CharPool.MINUS);
	}

	public static String[] toBCP47LanguageIds(Locale[] locales) {
		return toBCP47LanguageIds(toLanguageIds(locales));
	}

	public static String[] toBCP47LanguageIds(String[] languageIds) {
		String[] bcp47LanguageIds = new String[languageIds.length];

		for (int i = 0; i < languageIds.length; i++) {
			bcp47LanguageIds[i] = toBCP47LanguageId(languageIds[i]);
		}

		return bcp47LanguageIds;
	}

	public static String[] toDisplayNames(
		Collection<Locale> locales, Locale locale) {

		String[] displayNames = new String[locales.size()];

		int i = 0;

		for (Locale curLocale : locales) {
			displayNames[i++] = curLocale.getDisplayName(locale);
		}

		return displayNames;
	}

	public static String toJSONString(Locale locale) {
		Set<Character> extensionKeys = locale.getExtensionKeys();
		Set<String> unicodeLocaleAttributes =
			locale.getUnicodeLocaleAttributes();
		Set<String> unicodeLocaleKeys = locale.getUnicodeLocaleKeys();

		StringBundler sb = new StringBundler(
			29 + (extensionKeys.size() * 3) +
				(unicodeLocaleAttributes.size() * 3) +
					(unicodeLocaleKeys.size() * 3));

		sb.append("{\"country\": \"");
		sb.append(locale.getCountry());
		sb.append("\", \"displayCountry\": \"");
		sb.append(locale.getDisplayCountry());
		sb.append("\", \"displayLanguage\": \"");
		sb.append(locale.getDisplayLanguage());
		sb.append("\", \"displayName\": \"");
		sb.append(locale.getDisplayName());
		sb.append("\", \"displayScript\": \"");
		sb.append(locale.getDisplayScript());
		sb.append("\", \"displayVariant\": \"");
		sb.append(locale.getDisplayVariant());
		sb.append("\", \"extensionKeys\":");

		_putValues(sb, extensionKeys);

		sb.append(",\"ISO3Country\": \"");
		sb.append(locale.getISO3Country());
		sb.append("\", \"ISO3Language\": \"");
		sb.append(locale.getISO3Language());
		sb.append("\", \"language\": \"");
		sb.append(locale.getLanguage());
		sb.append("\", \"script\": \"");
		sb.append(locale.getScript());
		sb.append("\", \"unicodeLocaleAttributes\":");

		_putValues(sb, unicodeLocaleAttributes);

		sb.append(", \"unicodeLocaleKeys\":");

		_putValues(sb, unicodeLocaleKeys);

		sb.append(", \"variant\": \"");
		sb.append(locale.getVariant());

		sb.append("\"}");

		return sb.toString();
	}

	public static String toLanguageId(Locale locale) {
		if (locale == null) {
			locale = _locale;
		}

		String languageId = _languageIds.get(locale);

		if (languageId != null) {
			return languageId;
		}

		String country = locale.getCountry();

		boolean hasCountry = false;

		if (country.length() != 0) {
			hasCountry = true;
		}

		String variant = locale.getVariant();

		boolean hasVariant = false;

		if (variant.length() != 0) {
			hasVariant = true;
		}

		if (!hasCountry && !hasVariant) {
			languageId = locale.getLanguage();

			_languageIds.put(locale, languageId);

			return languageId;
		}

		int length = 3;

		if (hasCountry && hasVariant) {
			length = 5;
		}

		StringBundler sb = new StringBundler(length);

		sb.append(locale.getLanguage());

		if (hasCountry) {
			sb.append(StringPool.UNDERLINE);
			sb.append(country);
		}

		if (hasVariant) {
			sb.append(StringPool.UNDERLINE);
			sb.append(variant);
		}

		languageId = sb.toString();

		_languageIds.put(locale, languageId);

		return languageId;
	}

	public static String[] toLanguageIds(Collection<Locale> locales) {
		String[] languageIds = new String[locales.size()];

		int i = 0;

		for (Locale locale : locales) {
			languageIds[i++] = toLanguageId(locale);
		}

		return languageIds;
	}

	public static String[] toLanguageIds(Locale[] locales) {
		String[] languageIds = new String[locales.length];

		for (int i = 0; i < locales.length; i++) {
			languageIds[i] = toLanguageId(locales[i]);
		}

		return languageIds;
	}

	public static Map<String, Object> toMap(Locale locale) {
		return HashMapBuilder.<String, Object>put(
			"country", locale.getCountry()
		).put(
			"displayCountry", locale.getDisplayCountry()
		).put(
			"displayLanguage", locale.getDisplayLanguage()
		).put(
			"displayName", locale.getDisplayName()
		).put(
			"displayScript", locale.getDisplayScript()
		).put(
			"displayVariant", locale.getDisplayVariant()
		).put(
			"extensionKeys", locale.getExtensionKeys()
		).put(
			"ISO3Country", locale.getISO3Country()
		).put(
			"ISO3Language", locale.getISO3Language()
		).put(
			"language", locale.getLanguage()
		).put(
			"script", locale.getScript()
		).put(
			"unicodeLocaleAttributes", locale.getUnicodeLocaleAttributes()
		).put(
			"unicodeLocaleKeys", locale.getUnicodeLocaleKeys()
		).put(
			"variant", locale.getVariant()
		).build();
	}

	public static String toW3cLanguageId(Locale locale) {
		return toW3cLanguageId(toLanguageId(locale));
	}

	public static String toW3cLanguageId(String languageId) {
		return StringUtil.replace(
			languageId, CharPool.UNDERLINE, CharPool.MINUS);
	}

	public static String[] toW3cLanguageIds(Locale[] locales) {
		return toW3cLanguageIds(toLanguageIds(locales));
	}

	public static String[] toW3cLanguageIds(String[] languageIds) {
		String[] w3cLanguageIds = new String[languageIds.length];

		for (int i = 0; i < languageIds.length; i++) {
			w3cLanguageIds[i] = toW3cLanguageId(languageIds[i]);
		}

		return w3cLanguageIds;
	}

	private static Locale _fromLanguageId(String languageId) {
		Locale locale = _locales.get(languageId);

		if (locale != null) {
			return locale;
		}

		if (languageId.equals("zh-Hans-CN")) {
			languageId = "zh_CN";
		}
		else if (languageId.equals("zh-Hant-TW")) {
			languageId = "zh_TW";
		}
		else {
			languageId = StringUtil.replace(
				languageId, CharPool.MINUS, CharPool.UNDERLINE);
		}

		String[] languageIdParts = StringUtil.split(
			languageId, CharPool.UNDERLINE);

		if (languageIdParts.length < 2) {
			locale = new Locale(languageId);
		}
		else {
			String languageCode = languageIdParts[0];
			String countryCode = languageIdParts[1];

			String variant = null;

			if (languageIdParts.length > 2) {
				variant = languageIdParts[2];
			}

			if (Validator.isNotNull(variant)) {
				locale = new Locale(languageCode, countryCode, variant);
			}
			else {
				locale = new Locale(languageCode, countryCode);
			}
		}

		_locales.put(languageId, locale);

		return locale;
	}

	private static String _getDisplayCountry(
		Locale displayLocale, Locale locale) {

		String country = displayLocale.getDisplayCountry(locale);
		String variant = displayLocale.getDisplayVariant(locale);

		if (Validator.isNull(variant)) {
			return country;
		}

		return StringUtil.merge(
			new String[] {country, variant}, StringPool.COMMA_AND_SPACE);
	}

	private static String _getDisplayName(
		String language, String country, Locale locale,
		Set<String> duplicateLanguages) {

		String displayName = null;

		if (duplicateLanguages.contains(locale.getLanguage()) &&
			Validator.isNotNull(country)) {

			displayName = StringUtil.appendParentheticalSuffix(
				language, country);
		}
		else {
			displayName = language;
		}

		if (LanguageUtil.isBetaLocale(locale)) {
			displayName = displayName.concat(_BETA_SUFFIX);
		}

		return displayName;
	}

	private static void _putValues(StringBundler sb, Collection<?> values) {
		if (values.isEmpty()) {
			sb.append("[]");

			return;
		}

		sb.append("[");

		for (Object value : values) {
			sb.append("\"");
			sb.append(value);
			sb.append("\",");
		}

		sb.setIndex(sb.length() - 1);

		sb.append("\"]");
	}

	private static final String _BETA_SUFFIX = " [Beta]";

	private static final Log _log = LogFactoryUtil.getLog(LocaleUtil.class);

	private static final Map<Locale, String> _languageIds =
		new ConcurrentHashMap<>();
	private static Locale _locale = new Locale("en", "US");
	private static final Map<String, Locale> _locales =
		new ConcurrentHashMap<>();

}