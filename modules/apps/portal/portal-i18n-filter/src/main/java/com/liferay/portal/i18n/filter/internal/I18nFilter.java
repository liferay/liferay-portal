/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.i18n.filter.internal;

import com.liferay.friendly.url.configuration.FriendlyURLRedirectionConfiguration;
import com.liferay.friendly.url.configuration.FriendlyURLRedirectionConfigurationProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.cookies.constants.CookiesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 * @author Daniel Mijarra
 */
@Component(
	property = {
		"before-filter=GZip Filter", "dispatcher=FORWARD", "dispatcher=REQUEST",
		"servlet-context-name=", "servlet-filter-name=I18n Filter",
		"url-pattern=/group/*", "url-pattern=/user/*", "url-pattern=/web/*"
	},
	service = Filter.class
)
public class I18nFilter extends BasePortalFilter {

	public static final String SKIP_FILTER =
		I18nFilter.class.getName() + "#SKIP_FILTER";

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!isAlreadyFiltered(httpServletRequest) &&
			!isForwardedByI18nServlet(httpServletRequest) &&
			!isWidget(httpServletRequest)) {

			return true;
		}

		return false;
	}

	protected String getDefaultLanguageId(
		HttpServletRequest httpServletRequest) {

		String defaultLanguageId = getSiteDefaultLanguageId(httpServletRequest);

		if (Validator.isNull(defaultLanguageId)) {
			defaultLanguageId = LocaleUtil.toLanguageId(
				LocaleUtil.getDefault());
		}

		return defaultLanguageId;
	}

	protected String getFriendlyURL(HttpServletRequest httpServletRequest) {
		String friendlyURL = StringPool.BLANK;

		String pathInfo = httpServletRequest.getPathInfo();

		if (Validator.isNotNull(pathInfo)) {
			String[] pathInfoElements = pathInfo.split("/");

			if ((pathInfoElements != null) && (pathInfoElements.length > 1)) {
				friendlyURL = StringPool.SLASH + pathInfoElements[1];
			}
		}

		return friendlyURL;
	}

	protected String getRedirect(HttpServletRequest httpServletRequest)
		throws Exception {

		int localePrependFriendlyURLStyle = PrefsPropsUtil.getInteger(
			_portal.getCompanyId(httpServletRequest),
			PropsKeys.LOCALE_PREPEND_FRIENDLY_URL_STYLE);

		if (localePrependFriendlyURLStyle == 0) {
			String virtualHostLanguageId =
				(String)httpServletRequest.getAttribute(
					WebKeys.VIRTUAL_HOST_LANGUAGE_ID);

			if (Validator.isNotNull(virtualHostLanguageId)) {
				httpServletRequest.setAttribute(
					WebKeys.I18N_LANGUAGE_ID, virtualHostLanguageId);
			}

			return null;
		}

		String method = httpServletRequest.getMethod();

		if (method.equals(HttpMethods.POST)) {
			return null;
		}

		String contextPath = _portal.getPathContext();

		String requestURI = httpServletRequest.getRequestURI();

		if (Validator.isNotNull(contextPath) &&
			requestURI.startsWith(contextPath)) {

			requestURI = requestURI.substring(contextPath.length());
		}

		String i18nLanguageId = prependI18nLanguageId(
			httpServletRequest, localePrependFriendlyURLStyle);

		if (i18nLanguageId == null) {
			return null;
		}

		Locale locale = LocaleUtil.fromLanguageId(i18nLanguageId);

		if (!_language.isAvailableLocale(locale)) {
			return null;
		}

		requestURI = StringUtil.replace(
			requestURI, StringPool.DOUBLE_SLASH, StringPool.SLASH);

		String i18nPath = StringPool.SLASH.concat(
			_portal.getI18nPathLanguageId(locale, i18nLanguageId));

		if (requestURI.contains(i18nPath.concat(StringPool.SLASH))) {
			return null;
		}

		String redirect = contextPath + i18nPath + requestURI;

		int[] groupFriendlyURLIndex = _portal.getGroupFriendlyURLIndex(
			requestURI);

		String groupFriendlyURL = StringPool.BLANK;

		int friendlyURLEnd = 0;

		if (groupFriendlyURLIndex != null) {
			int friendlyURLStart = groupFriendlyURLIndex[0];
			friendlyURLEnd = groupFriendlyURLIndex[1];

			groupFriendlyURL = requestURI.substring(
				friendlyURLStart, friendlyURLEnd);
		}

		Group friendlyURLGroup = _groupLocalService.fetchFriendlyURLGroup(
			_portal.getCompanyId(httpServletRequest), groupFriendlyURL);

		if ((friendlyURLGroup != null) &&
			!_language.isAvailableLocale(
				friendlyURLGroup.getGroupId(), i18nLanguageId)) {

			return null;
		}

		LayoutSet layoutSet = (LayoutSet)httpServletRequest.getAttribute(
			WebKeys.VIRTUAL_HOST_LAYOUT_SET);

		if ((layoutSet != null) && !layoutSet.isPrivateLayout() &&
			requestURI.startsWith(
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING)) {

			Group group = layoutSet.getGroup();

			if (groupFriendlyURL.equals(group.getFriendlyURL())) {
				String layoutURL = requestURI.substring(friendlyURLEnd);

				if (Validator.isNull(layoutURL)) {
					redirect = contextPath + i18nPath + StringPool.SLASH;
				}
				else {
					redirect = contextPath + i18nPath + layoutURL;
				}
			}
		}

		String queryString = httpServletRequest.getQueryString();

		if (Validator.isNull(queryString)) {
			queryString = (String)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_FORWARD_QUERY_STRING);
		}

		if (Validator.isNotNull(queryString)) {
			redirect += StringPool.QUESTION + queryString;
		}

		return redirect;
	}

	protected String getRequestedLanguageId(
		HttpServletRequest httpServletRequest, String userLanguageId) {

		String requestedLanguageId = null;

		HttpSession httpSession = httpServletRequest.getSession();

		Locale locale = (Locale)httpSession.getAttribute(WebKeys.LOCALE);

		if (locale != null) {
			requestedLanguageId = LocaleUtil.toLanguageId(locale);
		}

		if (Validator.isNull(requestedLanguageId)) {
			requestedLanguageId = userLanguageId;
		}

		if (Validator.isNull(requestedLanguageId)) {
			requestedLanguageId = CookiesManagerUtil.getCookieValue(
				CookiesConstants.NAME_GUEST_LANGUAGE_ID, httpServletRequest,
				false);
		}

		if (Validator.isNull(requestedLanguageId)) {
			requestedLanguageId = (String)httpServletRequest.getAttribute(
				WebKeys.VIRTUAL_HOST_LANGUAGE_ID);
		}

		if (Validator.isNull(requestedLanguageId) &&
			PropsValues.LOCALE_DEFAULT_REQUEST) {

			Enumeration<Locale> enumeration = httpServletRequest.getLocales();

			while (enumeration.hasMoreElements()) {
				Locale requestLocale = enumeration.nextElement();

				if (Validator.isNull(requestLocale.getCountry())) {
					requestLocale = _language.getLocale(
						requestLocale.getLanguage());
				}

				if (_language.isAvailableLocale(requestLocale)) {
					requestedLanguageId = LocaleUtil.toLanguageId(
						requestLocale);

					break;
				}
			}
		}

		return requestedLanguageId;
	}

	protected String getSiteDefaultLanguageId(
		HttpServletRequest httpServletRequest) {

		String friendlyURL = getFriendlyURL(httpServletRequest);

		long companyId = _portal.getCompanyId(httpServletRequest);

		try {
			Group group = _groupLocalService.getFriendlyURLGroup(
				companyId, friendlyURL);

			return LocaleUtil.toLanguageId(
				_portal.getSiteDefaultLocale(group.getGroupId()));
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return StringPool.BLANK;
		}
	}

	protected boolean isAlreadyFiltered(HttpServletRequest httpServletRequest) {
		if (httpServletRequest.getAttribute(SKIP_FILTER) != null) {
			return true;
		}

		return false;
	}

	protected boolean isForwardedByI18nServlet(
		HttpServletRequest httpServletRequest) {

		if ((httpServletRequest.getAttribute(WebKeys.I18N_LANGUAGE_ID) !=
				null) ||
			(httpServletRequest.getAttribute(WebKeys.I18N_PATH) != null)) {

			return true;
		}

		return false;
	}

	protected boolean isWidget(HttpServletRequest httpServletRequest) {
		if (httpServletRequest.getAttribute(WebKeys.WIDGET) != null) {
			return true;
		}

		return false;
	}

	protected String prependI18nLanguageId(
		HttpServletRequest httpServletRequest, int prependFriendlyUrlStyle) {

		User user = (User)httpServletRequest.getAttribute(WebKeys.USER);

		String userLanguageId = null;

		if (user != null) {
			userLanguageId = user.getLanguageId();
		}

		String requestedLanguageId = getRequestedLanguageId(
			httpServletRequest, userLanguageId);

		String defaultLanguageId = getDefaultLanguageId(httpServletRequest);

		if (Validator.isNull(requestedLanguageId)) {
			requestedLanguageId = defaultLanguageId;
		}

		if (prependFriendlyUrlStyle == 1) {
			return prependIfRequestedLocaleDiffersFromDefaultLocale(
				defaultLanguageId, requestedLanguageId);
		}
		else if (prependFriendlyUrlStyle == 2) {
			return requestedLanguageId;
		}
		else if (prependFriendlyUrlStyle == 3) {
			if (user != null) {
				if (userLanguageId.equals(requestedLanguageId)) {
					return null;
				}

				return requestedLanguageId;
			}

			return prependIfRequestedLocaleDiffersFromDefaultLocale(
				defaultLanguageId, requestedLanguageId);
		}

		return null;
	}

	protected String prependIfRequestedLocaleDiffersFromDefaultLocale(
		String defaultLanguageId, String guestLanguageId) {

		if (defaultLanguageId.equals(guestLanguageId)) {
			return null;
		}

		return guestLanguageId;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		httpServletRequest.setAttribute(SKIP_FILTER, Boolean.TRUE);

		String redirect = getRedirect(httpServletRequest);

		if (redirect == null) {
			processFilter(
				I18nFilter.class.getName(), httpServletRequest,
				httpServletResponse, filterChain);

			return;
		}

		if (_isPermanentRedirect(CompanyThreadLocal.getCompanyId())) {
			httpServletResponse.setHeader("Location", redirect);
			httpServletResponse.setStatus(
				HttpServletResponse.SC_MOVED_PERMANENTLY);

			if (_log.isDebugEnabled()) {
				_log.debug(
					HttpServletResponse.SC_MOVED_PERMANENTLY +
						" Moved Permanently to Location " + redirect);
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug("Redirect " + redirect);
			}

			httpServletResponse.sendRedirect(redirect);
		}
	}

	private String _getFriendlyURLRedirectionType(long companyId) {
		FriendlyURLRedirectionConfiguration
			friendlyURLRedirectionConfiguration =
				_friendlyURLRedirectionConfigurationProvider.
					getCompanyFriendlyURLRedirectionConfiguration(companyId);

		return friendlyURLRedirectionConfiguration.redirectionType();
	}

	private boolean _isPermanentRedirect(long companyId) {
		return Objects.equals(
			_getFriendlyURLRedirectionType(companyId), "permanent");
	}

	private static final Log _log = LogFactoryUtil.getLog(I18nFilter.class);

	@Reference
	private FriendlyURLRedirectionConfigurationProvider
		_friendlyURLRedirectionConfigurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}