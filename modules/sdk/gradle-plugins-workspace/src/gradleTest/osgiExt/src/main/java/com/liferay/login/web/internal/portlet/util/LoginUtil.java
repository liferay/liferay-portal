/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.util;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 * @author Scott Lee
 */
public class LoginUtil {

	public static Map<String, String> getEmailDefinitionTerms(
		PortletRequest portletRequest, String emailFromAddress,
		String emailFromName, boolean showPasswordTerms) {

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Map<String, String> definitionTerms = new LinkedHashMap<>();

		definitionTerms.put(
			"[$FROM_ADDRESS$]", HtmlUtil.escape(emailFromAddress));
		definitionTerms.put("[$FROM_NAME$]", HtmlUtil.escape(emailFromName));

		if (showPasswordTerms) {
			definitionTerms.put(
				"[$PASSWORD_RESET_URL$]",
				LanguageUtil.get(
					themeDisplay.getLocale(), "the-password-reset-url"));
		}

		Company company = themeDisplay.getCompany();

		definitionTerms.put("[$PORTAL_URL$]", company.getVirtualHostname());

		definitionTerms.put(
			"[$REMOTE_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-browser's-remote-address"));
		definitionTerms.put(
			"[$REMOTE_HOST$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-browser's-remote-host"));
		definitionTerms.put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-address-of-the-email-recipient"));
		definitionTerms.put(
			"[$TO_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-name-of-the-email-recipient"));
		definitionTerms.put(
			"[$USER_ID$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-user-id"));

		definitionTerms.put(
			"[$USER_SCREENNAME$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-user-screen-name"));

		return definitionTerms;
	}

	public static String getEmailFromAddress(
		PortletPreferences portletPreferences, long companyId) {

		return PortalUtil.getEmailFromAddress(
			portletPreferences, companyId,
			PropsValues.LOGIN_EMAIL_FROM_ADDRESS);
	}

	public static String getEmailFromName(
		PortletPreferences portletPreferences, long companyId) {

		return PortalUtil.getEmailFromName(
			portletPreferences, companyId, PropsValues.LOGIN_EMAIL_FROM_NAME);
	}

	public static String getLogin(
		HttpServletRequest httpServletRequest, String paramName,
		Company company) {

		String login = httpServletRequest.getParameter(paramName);

		if ((login == null) || login.equals("null")) {
			login = CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_LOGIN, httpServletRequest, false);

			if (PropsValues.COMPANY_LOGIN_PREPOPULATE_DOMAIN &&
				Validator.isNull(login) &&
				CompanyConstants.AUTH_TYPE_EA.equals(company.getAuthType())) {

				login = "@".concat(company.getMx());
			}
		}

		return login;
	}

	public static PortletURL getLoginURL(
			HttpServletRequest httpServletRequest, long plid)
		throws PortletModeException, WindowStateException {

		PortletURL portletURL = PortletURLFactoryUtil.create(
			httpServletRequest, LoginPortletKeys.LOGIN, plid,
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("saveLastPath", Boolean.FALSE.toString());
		portletURL.setParameter("mvcRenderCommandName", "/login/login");
		portletURL.setPortletMode(PortletMode.VIEW);
		portletURL.setWindowState(WindowState.MAXIMIZED);

		return portletURL;
	}

	public static String sayHello() {
		return "Hello World";
	}

	public static void sendPassword(ActionRequest actionRequest)
		throws Exception {

		String toAddress = ParamUtil.getString(actionRequest, "emailAddress");

		sendPassword(actionRequest, null, null, toAddress, null, null);
	}

	public static void sendPassword(
			ActionRequest actionRequest, String fromName, String fromAddress,
			String toAddress, String subject, String body)
		throws Exception {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(actionRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		if (!company.isSendPasswordResetLink()) {
			return;
		}

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		UserLocalServiceUtil.sendPassword(
			company.getCompanyId(), toAddress, fromName, fromAddress, subject,
			body, serviceContext);
	}

}