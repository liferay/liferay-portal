/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

/**
 * @author Brian Wing Shun Chan
 */
public class LocaleUtil_IW {
	public static LocaleUtil_IW getInstance() {
		return _instance;
	}

	public boolean equals(java.util.Locale locale1, java.util.Locale locale2) {
		return LocaleUtil.equals(locale1, locale2);
	}

	public java.util.Locale fromLanguageId(java.lang.String languageId) {
		return LocaleUtil.fromLanguageId(languageId);
	}

	public java.util.Locale fromLanguageId(java.lang.String languageId,
		boolean validate) {
		return LocaleUtil.fromLanguageId(languageId, validate);
	}

	public java.util.Locale fromLanguageId(java.lang.String languageId,
		boolean validate, boolean useDefault) {
		return LocaleUtil.fromLanguageId(languageId, validate, useDefault);
	}

	public java.util.Locale[] fromLanguageIds(
		java.util.List<java.lang.String> languageIds) {
		return LocaleUtil.fromLanguageIds(languageIds);
	}

	public java.util.Locale[] fromLanguageIds(java.lang.String[] languageIds) {
		return LocaleUtil.fromLanguageIds(languageIds);
	}

	public java.util.Locale getDefault() {
		return LocaleUtil.getDefault();
	}

	public java.util.Map<java.lang.String, java.lang.String> getISOLanguages(
		java.util.Locale locale) {
		return LocaleUtil.getISOLanguages(locale);
	}

	public java.lang.String getLocaleDisplayName(
		java.util.Locale displayLocale, java.util.Locale locale) {
		return LocaleUtil.getLocaleDisplayName(displayLocale, locale);
	}

	public java.lang.String getLongDisplayName(java.util.Locale locale,
		java.util.Set<java.lang.String> duplicateLanguages) {
		return LocaleUtil.getLongDisplayName(locale, duplicateLanguages);
	}

	public java.util.Locale getMostRelevantLocale() {
		return LocaleUtil.getMostRelevantLocale();
	}

	public java.lang.String getShortDisplayName(java.util.Locale locale,
		java.util.Set<java.lang.String> duplicateLanguages) {
		return LocaleUtil.getShortDisplayName(locale, duplicateLanguages);
	}

	public java.util.Locale getSiteDefault() {
		return LocaleUtil.getSiteDefault();
	}

	public void setDefault(java.lang.String userLanguage,
		java.lang.String userCountry, java.lang.String userVariant) {
		LocaleUtil.setDefault(userLanguage, userCountry, userVariant);
	}

	public java.lang.String toBCP47LangTag(java.util.Locale locale) {
		return LocaleUtil.toBCP47LangTag(locale);
	}

	public java.lang.String toBCP47LanguageId(java.util.Locale locale) {
		return LocaleUtil.toBCP47LanguageId(locale);
	}

	public java.lang.String toBCP47LanguageId(java.lang.String languageId) {
		return LocaleUtil.toBCP47LanguageId(languageId);
	}

	public java.lang.String[] toBCP47LanguageIds(java.util.Locale[] locales) {
		return LocaleUtil.toBCP47LanguageIds(locales);
	}

	public java.lang.String[] toBCP47LanguageIds(java.lang.String[] languageIds) {
		return LocaleUtil.toBCP47LanguageIds(languageIds);
	}

	public java.lang.String[] toDisplayNames(
		java.util.Collection<java.util.Locale> locales, java.util.Locale locale) {
		return LocaleUtil.toDisplayNames(locales, locale);
	}

	public java.lang.String toJSON(java.util.Locale locale) {
		return LocaleUtil.toJSON(locale);
	}

	public java.lang.String toLanguageId(java.util.Locale locale) {
		return LocaleUtil.toLanguageId(locale);
	}

	public java.lang.String[] toLanguageIds(
		java.util.Collection<java.util.Locale> locales) {
		return LocaleUtil.toLanguageIds(locales);
	}

	public java.lang.String[] toLanguageIds(java.util.Locale[] locales) {
		return LocaleUtil.toLanguageIds(locales);
	}

	public java.util.Map<java.lang.String, java.lang.Object> toMap(
		java.util.Locale locale) {
		return LocaleUtil.toMap(locale);
	}

	public java.lang.String toW3cLanguageId(java.util.Locale locale) {
		return LocaleUtil.toW3cLanguageId(locale);
	}

	public java.lang.String toW3cLanguageId(java.lang.String languageId) {
		return LocaleUtil.toW3cLanguageId(languageId);
	}

	public java.lang.String[] toW3cLanguageIds(java.util.Locale[] locales) {
		return LocaleUtil.toW3cLanguageIds(locales);
	}

	public java.lang.String[] toW3cLanguageIds(java.lang.String[] languageIds) {
		return LocaleUtil.toW3cLanguageIds(languageIds);
	}

	private LocaleUtil_IW() {
	}

	private static LocaleUtil_IW _instance = new LocaleUtil_IW();
}