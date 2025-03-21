/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.util;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletRequest;

import java.util.Map;

/**
 * @author Sergio González
 */
public class MBMailUtil {

	public static Map<String, String> getEmailDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Map<String, String> definitionTerms = LinkedHashMapBuilder.put(
			"[$CATEGORY_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-category-in-which-the-message-has-been-posted")
		).put(
			"[$COMPANY_ID$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-id-associated-with-the-message-board")
		).put(
			"[$COMPANY_MX$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-mx-associated-with-the-message-board")
		).put(
			"[$COMPANY_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-name-associated-with-the-message-board")
		).put(
			"[$FROM_ADDRESS$]", HtmlUtil.escape(emailFromAddress)
		).put(
			"[$FROM_NAME$]", HtmlUtil.escape(emailFromName)
		).put(
			"[$MAILING_LIST_ADDRESS$]",
			() -> {
				if (!PrefsPropsUtil.getBoolean(
						themeDisplay.getCompanyId(),
						PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED,
						PropsValues.POP_SERVER_NOTIFICATIONS_ENABLED)) {

					return null;
				}

				return LanguageUtil.get(
					themeDisplay.getLocale(),
					"the-email-address-of-the-mailing-list");
			}
		).put(
			"[$MESSAGE_BODY$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-message-body")
		).put(
			"[$MESSAGE_ID$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-message-id")
		).put(
			"[$MESSAGE_PARENT$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-message-body-of-the-parent-message")
		).put(
			"[$MESSAGE_SIBLINGS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-message-thread-of-messages-at-the-same-level")
		).put(
			"[$MESSAGE_SUBJECT$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-message-subject")
		).put(
			"[$MESSAGE_URL$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-message-url")
		).put(
			"[$MESSAGE_USER_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-email-address-of-the-user-who-added-the-message")
		).put(
			"[$MESSAGE_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-added-the-message")
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
		).put(
			"[$ROOT_MESSAGE_BODY$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-message-body-of-the-original-message")
		).put(
			"[$SITE_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-site-name-associated-with-the-message-board")
		).build();

		if (!PropsValues.MESSAGE_BOARDS_EMAIL_BULK) {
			definitionTerms.put(
				"[$TO_ADDRESS$]",
				LanguageUtil.get(
					themeDisplay.getLocale(),
					"the-address-of-the-email-recipient"));
			definitionTerms.put(
				"[$TO_NAME$]",
				LanguageUtil.get(
					themeDisplay.getLocale(),
					"the-name-of-the-email-recipient"));
		}

		return definitionTerms;
	}

	public static Map<String, String> getEmailFromDefinitionTerms(
		PortletRequest portletRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return LinkedHashMapBuilder.put(
			"[$COMPANY_ID$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-id-associated-with-the-message-board")
		).put(
			"[$COMPANY_MX$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-mx-associated-with-the-message-board")
		).put(
			"[$COMPANY_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-company-name-associated-with-the-message-board")
		).put(
			"[$MAILING_LIST_ADDRESS$]",
			() -> {
				if (!PrefsPropsUtil.getBoolean(
						themeDisplay.getCompanyId(),
						PropsKeys.POP_SERVER_NOTIFICATIONS_ENABLED,
						PropsValues.POP_SERVER_NOTIFICATIONS_ENABLED)) {

					return null;
				}

				return LanguageUtil.get(
					themeDisplay.getLocale(),
					"the-email-address-of-the-mailing-list");
			}
		).put(
			"[$MESSAGE_USER_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-email-address-of-the-user-who-added-the-message")
		).put(
			"[$MESSAGE_USER_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-user-who-added-the-message")
		).put(
			"[$PORTLET_NAME$]",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return HtmlUtil.escape(portletDisplay.getTitle());
			}
		).put(
			"[$SITE_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-site-name-associated-with-the-message-board")
		).build();
	}

}