/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.language;

/**
 * @author Brian Wing Shun Chan
 */
public class UnicodeLanguageUtil_IW {
	public static UnicodeLanguageUtil_IW getInstance() {
		return _instance;
	}

	public java.lang.String format(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String pattern,
		com.liferay.portal.kernel.language.LanguageWrapper argument) {
		return UnicodeLanguageUtil.format(httpServletRequest, pattern, argument);
	}

	public java.lang.String format(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String pattern,
		com.liferay.portal.kernel.language.LanguageWrapper argument,
		boolean translateArguments) {
		return UnicodeLanguageUtil.format(httpServletRequest, pattern,
			argument, translateArguments);
	}

	public java.lang.String format(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String pattern,
		com.liferay.portal.kernel.language.LanguageWrapper[] arguments) {
		return UnicodeLanguageUtil.format(httpServletRequest, pattern, arguments);
	}

	public java.lang.String format(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String pattern,
		com.liferay.portal.kernel.language.LanguageWrapper[] arguments,
		boolean translateArguments) {
		return UnicodeLanguageUtil.format(httpServletRequest, pattern,
			arguments, translateArguments);
	}

	public java.lang.String format(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String pattern, java.lang.Object argument) {
		return UnicodeLanguageUtil.format(httpServletRequest, pattern, argument);
	}

	public java.lang.String format(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String pattern, java.lang.Object argument,
		boolean translateArguments) {
		return UnicodeLanguageUtil.format(httpServletRequest, pattern,
			argument, translateArguments);
	}

	public java.lang.String format(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String pattern, java.lang.Object[] arguments) {
		return UnicodeLanguageUtil.format(httpServletRequest, pattern, arguments);
	}

	public java.lang.String format(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String pattern, java.lang.Object[] arguments,
		boolean translateArguments) {
		return UnicodeLanguageUtil.format(httpServletRequest, pattern,
			arguments, translateArguments);
	}

	public java.lang.String format(java.util.Locale locale,
		java.lang.String pattern, java.lang.Object argument) {
		return UnicodeLanguageUtil.format(locale, pattern, argument);
	}

	public java.lang.String format(java.util.Locale locale,
		java.lang.String pattern, java.lang.Object argument,
		boolean translateArguments) {
		return UnicodeLanguageUtil.format(locale, pattern, argument,
			translateArguments);
	}

	public java.lang.String format(java.util.Locale locale,
		java.lang.String pattern, java.lang.Object[] arguments) {
		return UnicodeLanguageUtil.format(locale, pattern, arguments);
	}

	public java.lang.String format(java.util.Locale locale,
		java.lang.String pattern, java.lang.Object[] arguments,
		boolean translateArguments) {
		return UnicodeLanguageUtil.format(locale, pattern, arguments,
			translateArguments);
	}

	public java.lang.String format(java.util.ResourceBundle resourceBundle,
		java.lang.String pattern, java.lang.Object argument) {
		return UnicodeLanguageUtil.format(resourceBundle, pattern, argument);
	}

	public java.lang.String format(java.util.ResourceBundle resourceBundle,
		java.lang.String pattern, java.lang.Object argument,
		boolean translateArguments) {
		return UnicodeLanguageUtil.format(resourceBundle, pattern, argument,
			translateArguments);
	}

	public java.lang.String format(java.util.ResourceBundle resourceBundle,
		java.lang.String pattern, java.lang.Object[] arguments) {
		return UnicodeLanguageUtil.format(resourceBundle, pattern, arguments);
	}

	public java.lang.String format(java.util.ResourceBundle resourceBundle,
		java.lang.String pattern, java.lang.Object[] arguments,
		boolean translateArguments) {
		return UnicodeLanguageUtil.format(resourceBundle, pattern, arguments,
			translateArguments);
	}

	public java.lang.String get(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String key) {
		return UnicodeLanguageUtil.get(httpServletRequest, key);
	}

	public java.lang.String get(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.String key, java.lang.String defaultValue) {
		return UnicodeLanguageUtil.get(httpServletRequest, key, defaultValue);
	}

	public java.lang.String get(java.util.Locale locale, java.lang.String key) {
		return UnicodeLanguageUtil.get(locale, key);
	}

	public java.lang.String get(java.util.Locale locale, java.lang.String key,
		java.lang.String defaultValue) {
		return UnicodeLanguageUtil.get(locale, key, defaultValue);
	}

	public java.lang.String get(java.util.ResourceBundle resourceBundle,
		java.lang.String key) {
		return UnicodeLanguageUtil.get(resourceBundle, key);
	}

	public java.lang.String get(java.util.ResourceBundle resourceBundle,
		java.lang.String key, java.lang.String defaultValue) {
		return UnicodeLanguageUtil.get(resourceBundle, key, defaultValue);
	}

	public java.lang.String getTimeDescription(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		long milliseconds) {
		return UnicodeLanguageUtil.getTimeDescription(httpServletRequest,
			milliseconds);
	}

	public java.lang.String getTimeDescription(
		jakarta.servlet.http.HttpServletRequest httpServletRequest,
		java.lang.Long milliseconds) {
		return UnicodeLanguageUtil.getTimeDescription(httpServletRequest,
			milliseconds);
	}

	private UnicodeLanguageUtil_IW() {
	}

	private static UnicodeLanguageUtil_IW _instance = new UnicodeLanguageUtil_IW();
}