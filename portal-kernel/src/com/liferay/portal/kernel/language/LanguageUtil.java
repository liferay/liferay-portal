/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.language;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Brian Wing Shun Chan
 */
public class LanguageUtil {

	public static String format(
		HttpServletRequest httpServletRequest, String pattern,
		LanguageWrapper argument) {

		return _language.format(httpServletRequest, pattern, argument);
	}

	public static String format(
		HttpServletRequest httpServletRequest, String pattern,
		LanguageWrapper argument, boolean translateArguments) {

		return _language.format(
			httpServletRequest, pattern, argument, translateArguments);
	}

	public static String format(
		HttpServletRequest httpServletRequest, String pattern,
		LanguageWrapper[] arguments) {

		return _language.format(httpServletRequest, pattern, arguments);
	}

	public static String format(
		HttpServletRequest httpServletRequest, String pattern,
		LanguageWrapper[] arguments, boolean translateArguments) {

		return _language.format(
			httpServletRequest, pattern, arguments, translateArguments);
	}

	public static String format(
		HttpServletRequest httpServletRequest, String pattern,
		Object argument) {

		return _language.format(httpServletRequest, pattern, argument);
	}

	public static String format(
		HttpServletRequest httpServletRequest, String pattern, Object argument,
		boolean translateArguments) {

		return _language.format(
			httpServletRequest, pattern, argument, translateArguments);
	}

	public static String format(
		HttpServletRequest httpServletRequest, String pattern,
		Object[] arguments) {

		return _language.format(httpServletRequest, pattern, arguments);
	}

	public static String format(
		HttpServletRequest httpServletRequest, String pattern,
		Object[] arguments, boolean translateArguments) {

		return _language.format(
			httpServletRequest, pattern, arguments, translateArguments);
	}

	public static String format(
		Locale locale, String pattern, List<Object> arguments) {

		return _language.format(locale, pattern, arguments);
	}

	public static String format(
		Locale locale, String pattern, Object argument) {

		return _language.format(locale, pattern, argument);
	}

	public static String format(
		Locale locale, String pattern, Object argument,
		boolean translateArguments) {

		return _language.format(locale, pattern, argument, translateArguments);
	}

	public static String format(
		Locale locale, String pattern, Object[] arguments) {

		return _language.format(locale, pattern, arguments);
	}

	public static String format(
		Locale locale, String pattern, Object[] arguments,
		boolean translateArguments) {

		return _language.format(locale, pattern, arguments, translateArguments);
	}

	public static String format(
		ResourceBundle resourceBundle, String pattern, Object argument) {

		return _language.format(resourceBundle, pattern, argument);
	}

	public static String format(
		ResourceBundle resourceBundle, String pattern, Object argument,
		boolean translateArguments) {

		return _language.format(
			resourceBundle, pattern, argument, translateArguments);
	}

	public static String format(
		ResourceBundle resourceBundle, String pattern, Object[] arguments) {

		return _language.format(resourceBundle, pattern, arguments);
	}

	public static String format(
		ResourceBundle resourceBundle, String pattern, Object[] arguments,
		boolean translateArguments) {

		return _language.format(
			resourceBundle, pattern, arguments, translateArguments);
	}

	public static String formatStorageSize(double size, Locale locale) {
		return _language.formatStorageSize(size, locale);
	}

	public static String get(
		HttpServletRequest httpServletRequest, ResourceBundle resourceBundle,
		String key) {

		return _language.get(httpServletRequest, resourceBundle, key);
	}

	public static String get(
		HttpServletRequest httpServletRequest, ResourceBundle resourceBundle,
		String key, String defaultValue) {

		return _language.get(
			httpServletRequest, resourceBundle, key, defaultValue);
	}

	public static String get(
		HttpServletRequest httpServletRequest, String key) {

		return _language.get(httpServletRequest, key);
	}

	public static String get(
		HttpServletRequest httpServletRequest, String key,
		String defaultValue) {

		return _language.get(httpServletRequest, key, defaultValue);
	}

	public static String get(Locale locale, String key) {
		return _language.get(locale, key);
	}

	public static String get(Locale locale, String key, String defaultValue) {
		return _language.get(locale, key, defaultValue);
	}

	public static String get(ResourceBundle resourceBundle, String key) {
		return _language.get(resourceBundle, key);
	}

	public static String get(
		ResourceBundle resourceBundle, String key, String defaultValue) {

		return _language.get(resourceBundle, key, defaultValue);
	}

	public static Set<Locale> getAvailableLocales() {
		return _language.getAvailableLocales();
	}

	public static Set<Locale> getAvailableLocales(long groupId) {
		return _language.getAvailableLocales(groupId);
	}

	public static String getBCP47LanguageId(
		HttpServletRequest httpServletRequest) {

		return _language.getBCP47LanguageId(httpServletRequest);
	}

	public static String getBCP47LanguageId(Locale locale) {
		return _language.getBCP47LanguageId(locale);
	}

	public static String getBCP47LanguageId(PortletRequest portletRequest) {
		return _language.getBCP47LanguageId(portletRequest);
	}

	public static Set<Locale> getCompanyAvailableLocales(long companyId) {
		return _language.getCompanyAvailableLocales(companyId);
	}

	public static Language getLanguage() {
		return _language;
	}

	public static String getLanguageId(HttpServletRequest httpServletRequest) {
		return _language.getLanguageId(httpServletRequest);
	}

	public static String getLanguageId(Locale locale) {
		return _language.getLanguageId(locale);
	}

	public static String getLanguageId(PortletRequest portletRequest) {
		return _language.getLanguageId(portletRequest);
	}

	public static long getLastModified() {
		return _language.getLastModified();
	}

	public static Locale getLocale(long groupId, String languageCode) {
		return _language.getLocale(groupId, languageCode);
	}

	public static Locale getLocale(String languageCode) {
		return _language.getLocale(languageCode);
	}

	public static ResourceBundleLoader getResourceBundleLoader() {
		return _language.getResourceBundleLoader();
	}

	public static Set<Locale> getSupportedLocales() {
		return _language.getSupportedLocales();
	}

	public static String getTimeDescription(
		HttpServletRequest httpServletRequest, long milliseconds) {

		return _language.getTimeDescription(httpServletRequest, milliseconds);
	}

	public static String getTimeDescription(
		HttpServletRequest httpServletRequest, long milliseconds,
		boolean approximate) {

		return _language.getTimeDescription(
			httpServletRequest, milliseconds, approximate);
	}

	public static String getTimeDescription(
		HttpServletRequest httpServletRequest, Long milliseconds) {

		return _language.getTimeDescription(httpServletRequest, milliseconds);
	}

	public static String getTimeDescription(Locale locale, long milliseconds) {
		return _language.getTimeDescription(locale, milliseconds);
	}

	public static String getTimeDescription(
		Locale locale, long milliseconds, boolean approximate) {

		return _language.getTimeDescription(locale, milliseconds, approximate);
	}

	public static String getTimeDescription(Locale locale, Long milliseconds) {
		return _language.getTimeDescription(locale, milliseconds);
	}

	public static void init() {
		_language.init();
	}

	public static boolean isAvailableLanguageCode(String languageCode) {
		return _language.isAvailableLanguageCode(languageCode);
	}

	public static boolean isAvailableLocale(Locale locale) {
		return _language.isAvailableLocale(locale);
	}

	public static boolean isAvailableLocale(long groupId, Locale locale) {
		return _language.isAvailableLocale(groupId, locale);
	}

	public static boolean isAvailableLocale(long groupId, String languageId) {
		return _language.isAvailableLocale(groupId, languageId);
	}

	public static boolean isAvailableLocale(String languageId) {
		return _language.isAvailableLocale(languageId);
	}

	public static boolean isBetaLocale(Locale locale) {
		return _language.isBetaLocale(locale);
	}

	public static boolean isDuplicateLanguageCode(String languageCode) {
		return _language.isDuplicateLanguageCode(languageCode);
	}

	public static boolean isInheritLocales(long groupId)
		throws PortalException {

		return _language.isInheritLocales(groupId);
	}

	public static boolean isSameLanguage(Locale locale1, Locale locale2) {
		return _language.isSameLanguage(locale1, locale2);
	}

	public static String process(
		Supplier<ResourceBundle> resourceBundleSupplier, Locale locale,
		String content) {

		return _language.process(resourceBundleSupplier, locale, content);
	}

	public static void resetAvailableGroupLocales(long groupId) {
		_language.resetAvailableGroupLocales(groupId);
	}

	public static void resetAvailableLocales(long companyId) {
		_language.resetAvailableLocales(companyId);
	}

	public static void updateCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Locale locale) {

		_language.updateCookie(httpServletRequest, httpServletResponse, locale);
	}

	public void setLanguage(Language language) {
		_language = language;
	}

	private static Language _language;

}