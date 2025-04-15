/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.util;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Gavin Wan
 * @author Brian Wing Shun Chan
 */
public class EmailConfigurationUtil {

	public static Map<String, String> getEmailDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Locale locale = themeDisplay.getLocale();

		ResourceBundle resourceBundle = getResourceBundle(locale);

		return LinkedHashMapBuilder.put(
			"[$FROM_ADDRESS$]", HtmlUtil.escape(emailFromAddress)
		).put(
			"[$FROM_NAME$]", HtmlUtil.escape(emailFromName)
		).put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(locale, "the-address-of-the-email-recipient")
		).put(
			"[$TO_NAME$]",
			LanguageUtil.get(locale, "the-name-of-the-email-recipient")
		).put(
			"[$PAGE_URL$]", LanguageUtil.get(resourceBundle, "the-report-url")
		).put(
			"[$REPORT_NAME$]",
			LanguageUtil.get(resourceBundle, "the-name-of-the-report")
		).put(
			"[$PORTAL_URL$]",
			() -> {
				Company company = themeDisplay.getCompany();

				return company.getVirtualHostname();
			}
		).put(
			"[$PORTLET_NAME$]",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return HtmlUtil.escape(portletDisplay.getTitle());
			}
		).build();
	}

	public static ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			locale, EmailConfigurationUtil.class);
	}

}