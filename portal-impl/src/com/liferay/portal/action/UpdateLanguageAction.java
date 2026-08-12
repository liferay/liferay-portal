/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class UpdateLanguageAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String languageId = ParamUtil.getString(
			httpServletRequest, "languageId");

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		if (LanguageUtil.isAvailableLocale(
				themeDisplay.getSiteGroupId(), locale)) {

			boolean persistState = ParamUtil.getBoolean(
				httpServletRequest, "persistState", true);

			if (themeDisplay.isSignedIn() && persistState) {
				UserServiceUtil.updateLanguageId(
					themeDisplay.getUserId(), languageId);
			}

			if (Validator.isNull(themeDisplay.getDoAsUserId())) {
				HttpSession httpSession = httpServletRequest.getSession();

				httpSession.setAttribute(WebKeys.LOCALE, locale);

				LanguageUtil.updateCookie(
					httpServletRequest, httpServletResponse, locale);
			}
		}

		// Send redirect

		try {
			httpServletResponse.sendRedirect(
				getRedirect(httpServletRequest, themeDisplay, locale));
		}
		catch (IllegalArgumentException | NoSuchLayoutException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			httpServletResponse.sendError(
				HttpServletResponse.SC_BAD_REQUEST,
				httpServletRequest.getRequestURI());
		}

		return null;
	}

	public String getRedirect(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
			Locale locale)
		throws PortalException {

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			redirect = PortalUtil.escapeRedirect(redirect);

			if (Validator.isNull(redirect)) {
				throw new IllegalArgumentException();
			}
		}

		String contextPath = httpServletRequest.getContextPath();

		if (Validator.isNotNull(contextPath) &&
			!contextPath.equals(StringPool.SLASH)) {

			redirect = redirect.substring(contextPath.length());
		}

		redirect = PortalUtil.getChangeLanguageURL(
			redirect, httpServletRequest, locale, themeDisplay);

		if (Validator.isNotNull(themeDisplay.getDoAsUserId())) {
			return HttpComponentsUtil.setParameter(
				PortalUtil.addPreservedParameters(
					themeDisplay, themeDisplay.getLayout(), redirect, true),
				"doAsUserLanguageId", LocaleUtil.toLanguageId(locale));
		}

		return redirect;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateLanguageAction.class);

}