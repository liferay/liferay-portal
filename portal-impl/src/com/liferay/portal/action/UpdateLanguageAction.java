/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLResolverRegistryUtil;
import com.liferay.portal.kernel.portlet.LayoutFriendlyURLSeparatorComposite;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

		String layoutURL = redirect;

		String friendlyURLSeparatorPart = StringPool.BLANK;
		String queryString = StringPool.BLANK;

		int questionIndex = redirect.indexOf(StringPool.QUESTION);

		if (questionIndex != -1) {
			queryString = redirect.substring(questionIndex);
			layoutURL = redirect.substring(0, questionIndex);
		}

		String friendlyURLSeparator = StringPool.BLANK;
		int friendlyURLSeparatorIndex = -1;

		for (String urlSeparator :
				FriendlyURLResolverRegistryUtil.getURLSeparators()) {

			if (VirtualLayoutConstants.CANONICAL_URL_SEPARATOR.equals(
					urlSeparator)) {

				continue;
			}

			friendlyURLSeparatorIndex = layoutURL.indexOf(urlSeparator);

			if (friendlyURLSeparatorIndex != -1) {
				friendlyURLSeparator = urlSeparator;

				break;
			}
		}

		Layout layout = themeDisplay.getLayout();

		if (friendlyURLSeparatorIndex != -1) {
			friendlyURLSeparatorPart = layoutURL.substring(
				friendlyURLSeparatorIndex);

			try {
				LayoutFriendlyURLSeparatorComposite
					layoutFriendlyURLSeparatorComposite =
						PortalUtil.getLayoutFriendlyURLSeparatorComposite(
							layout.getGroupId(), layout.isPrivateLayout(),
							friendlyURLSeparatorPart,
							httpServletRequest.getParameterMap(),
							HashMapBuilder.<String, Object>put(
								"request", httpServletRequest
							).build());

				friendlyURLSeparatorPart =
					layoutFriendlyURLSeparatorComposite.getFriendlyURL();
			}
			catch (NoSuchLayoutException noSuchLayoutException) {
				if (!Portal.FRIENDLY_URL_SEPARATOR.equals(
						friendlyURLSeparator)) {

					if (_log.isDebugEnabled()) {
						_log.debug(noSuchLayoutException);
					}

					throw noSuchLayoutException;
				}
			}

			layoutURL = layoutURL.substring(0, friendlyURLSeparatorIndex);
		}

		Locale currentLocale = themeDisplay.getLocale();

		String mappingPart = StringPool.BLANK;

		String currentLayoutFriendlyURL = layout.getFriendlyURL(currentLocale);

		int currentLayoutFriendlyURLIndex = -1;

		if (Validator.isNotNull(currentLayoutFriendlyURL)) {
			currentLayoutFriendlyURLIndex = layoutURL.indexOf(
				currentLayoutFriendlyURL);
		}

		if (currentLayoutFriendlyURLIndex != -1) {
			mappingPart = _getMappingPart(
				currentLayoutFriendlyURLIndex +
					currentLayoutFriendlyURL.length(),
				layoutURL);
		}
		else {
			Group group = layout.getGroup();

			String groupFriendlyURL = group.getFriendlyURL();

			int groupFriendlyURLIndex = layoutURL.indexOf(groupFriendlyURL);

			if (groupFriendlyURLIndex != -1) {
				mappingPart = _getMappingPart(
					groupFriendlyURLIndex + groupFriendlyURL.length(),
					layoutURL);
			}
		}

		if (themeDisplay.isI18n()) {
			String i18nPath = themeDisplay.getI18nPath();

			String currentLocalePath =
				StringPool.SLASH + currentLocale.toLanguageTag();

			if (layoutURL.startsWith(currentLocalePath)) {
				layoutURL = layoutURL.substring(currentLocalePath.length());
			}
			else if (layoutURL.startsWith(i18nPath)) {
				layoutURL = layoutURL.substring(i18nPath.length());
			}
		}

		int localePrependFriendlyURLStyle = PrefsPropsUtil.getInteger(
			PortalUtil.getCompanyId(httpServletRequest),
			PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);

		if (!Validator.isBlank(themeDisplay.getPathMain()) &&
			layoutURL.startsWith(themeDisplay.getPathMain())) {

			redirect = layoutURL;
		}
		else if (isFriendlyURLResolver(layoutURL) ||
				 layout.isTypeControlPanel()) {

			redirect = layoutURL + friendlyURLSeparatorPart;
		}
		else if (isGroupFriendlyURL(
					layout.getGroup(), layout, layoutURL, currentLocale)) {

			if (localePrependFriendlyURLStyle == 0) {
				redirect = layoutURL;
			}
			else {
				redirect = PortalUtil.getGroupFriendlyURL(
					layout.getLayoutSet(), themeDisplay, locale);
			}

			if (!redirect.endsWith(StringPool.SLASH) &&
				!friendlyURLSeparatorPart.startsWith(StringPool.SLASH)) {

				redirect += StringPool.SLASH;
			}

			if (Validator.isNotNull(friendlyURLSeparatorPart)) {
				redirect += friendlyURLSeparatorPart;
			}
		}
		else {
			if (localePrependFriendlyURLStyle == 0) {
				redirect = PortalUtil.getLayoutURL(
					layout, themeDisplay, locale);
			}
			else {
				redirect = PortalUtil.getLayoutFriendlyURL(
					layout, themeDisplay, locale);
			}

			if (Validator.isNotNull(friendlyURLSeparatorPart)) {
				redirect += friendlyURLSeparatorPart;
			}

			if (Validator.isNotNull(mappingPart)) {
				redirect += mappingPart;
			}
		}

		if (Validator.isNotNull(queryString)) {
			redirect = redirect + queryString;
		}

		redirect = _prependI18nPath(
			redirect, locale, themeDisplay, layout,
			localePrependFriendlyURLStyle);

		if (Validator.isNotNull(themeDisplay.getDoAsUserId())) {
			return HttpComponentsUtil.setParameter(
				PortalUtil.addPreservedParameters(
					themeDisplay, layout, redirect, true),
				"doAsUserLanguageId", LocaleUtil.toLanguageId(locale));
		}

		return redirect;
	}

	protected boolean isFriendlyURLResolver(String layoutURL) {
		String[] urlSeparators =
			FriendlyURLResolverRegistryUtil.getURLSeparators();

		for (String urlSeparator : urlSeparators) {
			if (layoutURL.contains(urlSeparator)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isGroupFriendlyURL(
		Group group, Layout layout, String layoutURL, Locale locale) {

		if (Validator.isNull(layoutURL) ||
			Objects.equals(layoutURL, StringPool.SLASH)) {

			return true;
		}

		if ((layoutURL.length() > 1) && layoutURL.endsWith(StringPool.SLASH)) {
			layoutURL = layoutURL.substring(0, layoutURL.length() - 1);
		}

		if (PortalUtil.isGroupFriendlyURL(
				layoutURL, group.getFriendlyURL(),
				layout.getFriendlyURL(locale))) {

			return true;
		}

		int index = layoutURL.indexOf(StringPool.SLASH);

		String string = layoutURL.substring(index + 1);

		index = string.indexOf(CharPool.SLASH);

		Locale layoutURLLocale = LocaleUtil.fromLanguageId(
			string.substring(index + 1), true, false);

		if (layoutURLLocale != null) {
			return true;
		}

		return false;
	}

	private String _getMappingPart(int fromIndex, String url) {
		String mappingPart = StringPool.BLANK;

		List<FriendlyURLMapper> friendlyURLMappers =
			PortletLocalServiceUtil.getFriendlyURLMappers();

		for (FriendlyURLMapper friendlyURLMapper : friendlyURLMappers) {
			if (friendlyURLMapper.isCheckMappingWithPrefix()) {
				continue;
			}

			String mappingPath =
				StringPool.SLASH + friendlyURLMapper.getMapping();

			int mappingIndex = url.indexOf(mappingPath, fromIndex);

			if (mappingIndex == -1) {
				continue;
			}

			int mappingEndIndex = mappingIndex + mappingPath.length();

			if ((mappingEndIndex == url.length()) ||
				(url.charAt(mappingEndIndex) == CharPool.SLASH)) {

				mappingPart = url.substring(mappingIndex);
			}
		}

		return mappingPart;
	}

	private String _prependI18nPath(
			String redirect, Locale locale, ThemeDisplay themeDisplay,
			Layout layout, int localePrependFriendlyURLStyle)
		throws PortalException {

		if (localePrependFriendlyURLStyle == 0) {
			return redirect;
		}

		if ((localePrependFriendlyURLStyle != 2) &&
			locale.equals(LocaleUtil.getDefault())) {

			return redirect;
		}

		if (layout.isTypeControlPanel()) {
			return redirect;
		}

		int pathStart = 0;

		if (redirect.startsWith(Http.HTTP_WITH_SLASH) ||
			redirect.startsWith(Http.HTTPS_WITH_SLASH)) {

			pathStart = redirect.indexOf(
				CharPool.SLASH, redirect.indexOf("://") + 3);

			if (pathStart == -1) {
				return redirect;
			}
		}

		String pathContext = themeDisplay.getPathContext();

		if (Validator.isNotNull(pathContext) &&
			!pathContext.equals(StringPool.SLASH) &&
			redirect.startsWith(pathContext, pathStart)) {

			pathStart += pathContext.length();
		}

		String path = redirect.substring(pathStart);

		if (!Validator.isBlank(themeDisplay.getPathMain()) &&
			path.startsWith(themeDisplay.getPathMain() + StringPool.SLASH)) {

			return redirect;
		}

		String i18nPath =
			StringPool.SLASH +
				PortalUtil.getI18nPathLanguageId(
					locale, LocaleUtil.toLanguageId(locale));

		if (path.startsWith(i18nPath + StringPool.SLASH) ||
			path.equals(i18nPath)) {

			return redirect;
		}

		return redirect.substring(0, pathStart) + i18nPath + path;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateLanguageAction.class);

}