/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.login.web.internal.portlet.util;

import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Map;

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

		return LinkedHashMapBuilder.put(
			"[$FROM_ADDRESS$]", HtmlUtil.escape(emailFromAddress)
		).put(
			"[$FROM_NAME$]", HtmlUtil.escape(emailFromName)
		).put(
			"[$PASSWORD_RESET_URL$]",
			() -> {
				if (!showPasswordTerms) {
					return null;
				}

				return LanguageUtil.get(
					themeDisplay.getLocale(), "the-password-reset-url");
			}
		).put(
			"[$PORTAL_URL$]",
			() -> {
				Company company = themeDisplay.getCompany();

				return company.getVirtualHostname();
			}
		).put(
			"[$REMOTE_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-browser's-remote-address")
		).put(
			"[$REMOTE_HOST$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-browser's-remote-host")
		).put(
			"[$TO_ADDRESS$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-address-of-the-email-recipient")
		).put(
			"[$TO_NAME$]",
			LanguageUtil.get(
				themeDisplay.getLocale(), "the-name-of-the-email-recipient")
		).put(
			"[$USER_ID$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-user-id")
		).put(
			"[$USER_SCREENNAME$]",
			LanguageUtil.get(themeDisplay.getLocale(), "the-user-screen-name")
		).build();
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

	public static String getEmailTemplateXML(
		PortletPreferences portletPreferences, PortletRequest portletRequest,
		long companyId, String portletPreferencesTemplateKey,
		String companyPortletPreferencesTemplateKey,
		String portalPropertiesTemplateKey) {

		String xml = LocalizationUtil.getLocalizationXmlFromPreferences(
			portletPreferences, portletRequest, portletPreferencesTemplateKey,
			"preferences", null);

		if (xml == null) {
			PortletPreferences companyPortletPreferences =
				PrefsPropsUtil.getPreferences(companyId);

			String defaultContent = null;

			try {
				defaultContent = StringUtil.read(
					PortalClassLoaderUtil.getClassLoader(),
					PropsUtil.get(portalPropertiesTemplateKey));
			}
			catch (IOException ioException) {
				_log.error(
					"Unable to read the content for " +
						PropsUtil.get(portalPropertiesTemplateKey),
					ioException);
			}

			xml = LocalizationUtil.getLocalizationXmlFromPreferences(
				companyPortletPreferences, portletRequest,
				companyPortletPreferencesTemplateKey, "settings",
				defaultContent);
		}

		return xml;
	}

	public static String getLogin(
		HttpServletRequest httpServletRequest, String paramName,
		Company company) {

		String login = httpServletRequest.getParameter(paramName);

		if ((login == null) || login.equals(StringPool.NULL)) {
			login = CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_LOGIN, httpServletRequest, false);

			String authType = company.getAuthType();

			if (PropsValues.COMPANY_LOGIN_PREPOPULATE_DOMAIN &&
				Validator.isNull(login) &&
				authType.equals(CompanyConstants.AUTH_TYPE_EA)) {

				login = "@".concat(company.getMx());
			}
		}

		return login;
	}

	public static PortletURL getLoginURL(
			HttpServletRequest httpServletRequest, long plid)
		throws PortletModeException, WindowStateException {

		String portletName = LoginPortletKeys.LOGIN;

		if (FeatureFlagManagerUtil.isEnabled("LPD-6378")) {
			PortletConfig portletConfig =
				(PortletConfig)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_CONFIG);

			portletName = portletConfig.getPortletName();

			if (!portletName.equals(LoginPortletKeys.CREATE_ACCOUNT) &&
				!portletName.equals(LoginPortletKeys.LOGIN) &&
				!portletName.equals(LoginPortletKeys.FORGOT_PASSWORD)) {

				portletName = LoginPortletKeys.LOGIN;
			}
		}

		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, portletName, plid,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/login/login"
		).setParameter(
			"saveLastPath", false
		).setPortletMode(
			PortletMode.VIEW
		).setWindowState(
			WindowState.MAXIMIZED
		).buildPortletURL();
	}

	public static void sendEmailUserCreationAttempt(
			ActionRequest actionRequest, String fromName, String fromAddress,
			String toAddress, String subject, String body)
		throws Exception {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(actionRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		UserLocalServiceUtil.sendEmailUserCreationAttempt(
			company.getCompanyId(), toAddress, fromName, fromAddress, subject,
			body, serviceContext);
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

	public static void sendPasswordLockout(
			ActionRequest actionRequest, String fromName, String fromAddress,
			String toAddress, String subject, String body)
		throws Exception {

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(actionRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Company company = themeDisplay.getCompany();

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			User.class.getName(), actionRequest);

		UserLocalServiceUtil.sendPasswordLockout(
			company.getCompanyId(), toAddress, fromName, fromAddress, subject,
			body, serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(LoginUtil.class);

}