/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.ws.rs.BadRequestException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class LocalizedMapUtil {

	public static Map<String, String> getI18nMap(
		boolean acceptAllLanguages, Map<Locale, String> localizedMap) {

		if (!acceptAllLanguages) {
			return null;
		}

		Map<String, String> i18nMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : localizedMap.entrySet()) {
			Locale locale = entry.getKey();

			i18nMap.put(LocaleUtil.toBCP47LanguageId(locale), entry.getValue());
		}

		return i18nMap;
	}

	public static Map<String, String> getI18nMap(
		boolean acceptAllLanguages, Set<Locale> availableLocales,
		Map<String, String> localizedMap) {

		if (!acceptAllLanguages) {
			return null;
		}

		Map<String, String> i18nMap = new HashMap<>();

		for (Locale locale : availableLocales) {
			String languageId = LocaleUtil.toLanguageId(locale);

			if (localizedMap.containsKey(languageId)) {
				i18nMap.put(languageId, localizedMap.get(languageId));
			}
		}

		return i18nMap;
	}

	public static Map<String, String> getI18nMap(
		Map<Locale, String> localizedMap) {

		return getI18nMap(true, localizedMap);
	}

	public static Map<String, String> getLanguageIdMap(
		Map<Locale, String> localizedMap) {

		Map<String, String> languageIdMap = new HashMap<>();

		localizedMap.forEach(
			(locale, value) -> languageIdMap.put(
				LocaleUtil.toLanguageId(locale), value));

		return Collections.unmodifiableMap(languageIdMap);
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #getI18nMap(boolean, Map)}
	 */
	@Deprecated
	public static Map<String, String> getLocalizedMap(
		boolean acceptAllLanguages, Map<Locale, String> localizedMap) {

		return getI18nMap(acceptAllLanguages, localizedMap);
	}

	public static Map<Locale, String> getLocalizedMap(
		Locale defaultLocale, String defaultValue,
		Map<String, String> i18nMap) {

		Map<Locale, String> localizedMap = getLocalizedMap(i18nMap);

		if (defaultValue != null) {
			localizedMap.put(defaultLocale, defaultValue);
		}

		return localizedMap;
	}

	public static Map<Locale, String> getLocalizedMap(
		Locale defaultLocale, String defaultValue, Map<String, String> i18nMap,
		Map<Locale, String> fallbackLocalizedMap) {

		Map<Locale, String> localizedMap = null;

		if (i18nMap != null) {
			localizedMap = getLocalizedMap(i18nMap);
		}
		else if (defaultValue != null) {
			localizedMap = new HashMap<>(fallbackLocalizedMap);
		}
		else {
			localizedMap = new HashMap<>();
		}

		if (defaultValue != null) {
			localizedMap.put(defaultLocale, defaultValue);
		}

		return localizedMap;
	}

	public static Map<Locale, String> getLocalizedMap(
		Map<String, String> i18nMap) {

		return getLocalizedMap(i18nMap, false);
	}

	public static Map<Locale, String> getLocalizedMap(
		Map<String, String> i18nMap, boolean useDefault) {

		Map<Locale, String> localizedMap = new HashMap<>();

		if (i18nMap == null) {
			return localizedMap;
		}

		for (Map.Entry<String, String> entry : i18nMap.entrySet()) {
			Locale locale = _getLocale(entry.getKey(), useDefault);
			String value = entry.getValue();

			if ((locale != null) && (value != null)) {
				localizedMap.put(locale, value);
			}
		}

		return localizedMap;
	}

	public static Map<Locale, String> getLocalizedMap(String label) {
		return Collections.singletonMap(LocaleUtil.getDefault(), label);
	}

	public static Map<String, String> mergeI18nMap(
		Map<String, String> i18nMap, String locale, String value) {

		if (Validator.isNull(locale)) {
			return i18nMap;
		}

		if (i18nMap == null) {
			return Collections.singletonMap(locale, value);
		}

		if (Validator.isNotNull(value)) {
			i18nMap.put(locale, value);
		}
		else {
			i18nMap.remove(locale);
		}

		return i18nMap;
	}

	public static Map<Locale, String> mergeLocalizedMap(
		Map<Locale, String> localizedMap, Locale locale, String value) {

		if (locale == null) {
			return localizedMap;
		}

		if (localizedMap == null) {
			return Collections.singletonMap(locale, value);
		}

		if (value != null) {
			localizedMap.put(locale, value);
		}
		else {
			localizedMap.remove(locale);
		}

		return localizedMap;
	}

	public static Map<Locale, String> mergeLocalizedMap(
		Map<Locale, String> localizedMap, Map.Entry<Locale, String> entry) {

		if (entry == null) {
			return localizedMap;
		}

		return mergeLocalizedMap(
			localizedMap, entry.getKey(), entry.getValue());
	}

	public static Map<Locale, String> patchLocalizedMap(
		Map<Locale, String> localizedMap, Locale locale, String value) {

		if (value != null) {
			localizedMap.put(locale, value);
		}

		return localizedMap;
	}

	public static Map<Locale, String> patchLocalizedMap(
		Map<Locale, String> localizedMap, Locale defaultLocale,
		String defaultValue, Map<String, String> i18nMap) {

		Map<Locale, String> resultLocalizedMap = new HashMap<>();

		if (localizedMap != null) {
			resultLocalizedMap.putAll(localizedMap);
		}

		resultLocalizedMap = patchLocalizedMap(
			resultLocalizedMap, defaultLocale, defaultValue);

		if (i18nMap == null) {
			return resultLocalizedMap;
		}

		for (Map.Entry<String, String> entry : i18nMap.entrySet()) {
			Locale locale = _getLocale(entry.getKey(), false);

			if (locale != null) {
				resultLocalizedMap = patchLocalizedMap(
					resultLocalizedMap, locale, entry.getValue());
			}
		}

		return resultLocalizedMap;
	}

	public static Map<String, String> populateI18nMap(
		String defaultLanguageId, Map<String, String> i18nMap,
		String siteDefaultValue) {

		String siteDefaultLanguageId = LocaleUtil.toLanguageId(
			LocaleUtil.getSiteDefault());

		if (MapUtil.isEmpty(i18nMap)) {
			return HashMapBuilder.put(
				siteDefaultLanguageId, siteDefaultValue
			).build();
		}

		Map<String, String> newI18nMap = new HashMap<>();

		for (Map.Entry<String, String> entry : i18nMap.entrySet()) {
			newI18nMap.put(
				StringUtil.replace(
					entry.getKey(), CharPool.MINUS, CharPool.UNDERLINE),
				entry.getValue());
		}

		if (!newI18nMap.containsKey(defaultLanguageId) &&
			newI18nMap.containsKey("en_US")) {

			defaultLanguageId = "en_US";
		}

		if ((defaultLanguageId == null) && (siteDefaultValue == null)) {
			return newI18nMap;
		}

		newI18nMap.putIfAbsent(
			siteDefaultLanguageId,
			MapUtil.getString(newI18nMap, defaultLanguageId, siteDefaultValue));

		return newI18nMap;
	}

	public static Map<Locale, String> populateLocalizedMap(
		Map<String, String> i18nMap) {

		return populateLocalizedMap(null, i18nMap, null);
	}

	public static Map<Locale, String> populateLocalizedMap(
		String defaultLanguageId, Map<String, String> i18nMap) {

		return populateLocalizedMap(defaultLanguageId, i18nMap, null);
	}

	public static Map<Locale, String> populateLocalizedMap(
		String defaultLanguageId, Map<String, String> i18nMap,
		String siteDefaultValue) {

		return getLocalizedMap(
			populateI18nMap(defaultLanguageId, i18nMap, siteDefaultValue));
	}

	public static void validateI18n(
		boolean add, Locale defaultLocale, String entityName,
		Map<Locale, String> localizedMap, Set<Locale> notFoundLocales) {

		if ((add && localizedMap.isEmpty()) ||
			!localizedMap.containsKey(defaultLocale)) {

			throw new BadRequestException(
				entityName + " must include the default language " +
					LocaleUtil.toW3cLanguageId(defaultLocale));
		}

		notFoundLocales.removeAll(localizedMap.keySet());

		if (!notFoundLocales.isEmpty()) {
			StringBundler sb = new StringBundler(
				(notFoundLocales.size() * 2) + 2);

			sb.append(entityName);
			sb.append(" title missing in the languages: ");

			for (Locale locale : notFoundLocales) {
				sb.append(LocaleUtil.toW3cLanguageId(locale));
				sb.append(",");
			}

			sb.setIndex(sb.index() - 1);

			throw new BadRequestException(sb.toString());
		}
	}

	private static Locale _getLocale(String languageId, boolean useDefault) {
		return LocaleUtil.fromLanguageId(languageId, true, useDefault);
	}

}