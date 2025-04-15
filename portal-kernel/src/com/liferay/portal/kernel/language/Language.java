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

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface Language {

	public String format(
		HttpServletRequest httpServletRequest, String pattern,
		LanguageWrapper argument);

	public String format(
		HttpServletRequest httpServletRequest, String pattern,
		LanguageWrapper argument, boolean translateArguments);

	public String format(
		HttpServletRequest httpServletRequest, String pattern,
		LanguageWrapper[] arguments);

	public String format(
		HttpServletRequest httpServletRequest, String pattern,
		LanguageWrapper[] arguments, boolean translateArguments);

	public String format(
		HttpServletRequest httpServletRequest, String pattern, Object argument);

	public String format(
		HttpServletRequest httpServletRequest, String pattern, Object argument,
		boolean translateArguments);

	public String format(
		HttpServletRequest httpServletRequest, String pattern,
		Object[] arguments);

	public String format(
		HttpServletRequest httpServletRequest, String pattern,
		Object[] arguments, boolean translateArguments);

	public String format(Locale locale, String pattern, List<Object> arguments);

	public String format(Locale locale, String pattern, Object argument);

	public String format(
		Locale locale, String pattern, Object argument,
		boolean translateArguments);

	public String format(Locale locale, String pattern, Object[] arguments);

	public String format(
		Locale locale, String pattern, Object[] arguments,
		boolean translateArguments);

	public String format(
		ResourceBundle resourceBundle, String pattern, Object argument);

	public String format(
		ResourceBundle resourceBundle, String pattern, Object argument,
		boolean translateArguments);

	public String format(
		ResourceBundle resourceBundle, String pattern, Object[] arguments);

	public String format(
		ResourceBundle resourceBundle, String pattern, Object[] arguments,
		boolean translateArguments);

	public String formatStorageSize(double size, Locale locale);

	public String get(
		HttpServletRequest httpServletRequest, ResourceBundle resourceBundle,
		String key);

	public String get(
		HttpServletRequest httpServletRequest, ResourceBundle resourceBundle,
		String key, String defaultValue);

	public String get(HttpServletRequest httpServletRequest, String key);

	public String get(
		HttpServletRequest httpServletRequest, String key, String defaultValue);

	public String get(Locale locale, String key);

	public String get(Locale locale, String key, String defaultValue);

	public String get(ResourceBundle resourceBundle, String key);

	public String get(
		ResourceBundle resourceBundle, String key, String defaultValue);

	public Set<Locale> getAvailableLocales();

	public Set<Locale> getAvailableLocales(long groupId);

	public String getBCP47LangTag(Locale locale);

	public String getBCP47LanguageId(HttpServletRequest httpServletRequest);

	public String getBCP47LanguageId(Locale locale);

	public String getBCP47LanguageId(PortletRequest portletRequest);

	public Set<Locale> getCompanyAvailableLocales(long companyId);

	public String getLanguageId(HttpServletRequest httpServletRequest);

	public String getLanguageId(Locale locale);

	public String getLanguageId(PortletRequest portletRequest);

	public default long getLastModified() {
		return System.currentTimeMillis();
	}

	public Locale getLocale(long groupId, String languageCode);

	public Locale getLocale(String languageCode);

	public ResourceBundleLoader getResourceBundleLoader();

	public Set<Locale> getSupportedLocales();

	public String getTimeDescription(
		HttpServletRequest httpServletRequest, long milliseconds);

	public String getTimeDescription(
		HttpServletRequest httpServletRequest, long milliseconds,
		boolean approximate);

	public String getTimeDescription(
		HttpServletRequest httpServletRequest, Long milliseconds);

	public String getTimeDescription(Locale locale, long milliseconds);

	public String getTimeDescription(
		Locale locale, long milliseconds, boolean approximate);

	public String getTimeDescription(Locale locale, Long milliseconds);

	public void init();

	public boolean isAvailableLanguageCode(String languageCode);

	public boolean isAvailableLocale(Locale locale);

	public boolean isAvailableLocale(long groupId, Locale locale);

	public boolean isAvailableLocale(long groupId, String languageId);

	public boolean isAvailableLocale(String languageId);

	public boolean isBetaLocale(Locale locale);

	public boolean isDuplicateLanguageCode(String languageCode);

	public boolean isInheritLocales(long groupId) throws PortalException;

	public boolean isSameLanguage(Locale locale1, Locale locale2);

	public String process(
		Supplier<ResourceBundle> resourceBundleSupplier, Locale locale,
		String content);

	public void resetAvailableGroupLocales(long groupId);

	public void resetAvailableLocales(long companyId);

	public void updateCookie(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, Locale locale);

}