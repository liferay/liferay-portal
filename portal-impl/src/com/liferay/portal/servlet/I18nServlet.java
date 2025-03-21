/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.AsyncPortletServletRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 */
public class I18nServlet extends HttpServlet {

	public static Set<String> getLanguageIds() {
		return new HashSet<>(_languageIds.values());
	}

	public static Map<String, String> getLanguageIdsMap() {
		return _languageIds;
	}

	public static void setLanguageIds(Element root) {
		Map<String, String> languageIds = new HashMap<>();

		List<Element> rootElements = root.elements("servlet-mapping");

		for (Element element : rootElements) {
			String servletName = element.elementText("servlet-name");

			if (servletName.equals("I18n Servlet")) {
				String urlPattern = element.elementText("url-pattern");

				String languageId = urlPattern.substring(
					0, urlPattern.lastIndexOf(CharPool.SLASH));

				languageIds.put(StringUtil.toLowerCase(languageId), languageId);
			}
		}

		_languageIds = Collections.unmodifiableMap(languageIds);
	}

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			I18nData i18nData = getI18nData(httpServletRequest);

			if ((i18nData == null) ||
				!PortalUtil.isValidResourceId(i18nData.getPath())) {

				PortalUtil.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					new NoSuchLayoutException(), httpServletRequest,
					httpServletResponse);

				return;
			}

			String i18nLanguageId = httpServletRequest.getServletPath();

			if (i18nLanguageId.contains(StringPool.UNDERLINE)) {
				sendRedirect(httpServletRequest, httpServletResponse, i18nData);
			}
			else {
				_processI18nData(
					httpServletRequest, httpServletResponse, i18nData);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			PortalUtil.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception,
				httpServletRequest, httpServletResponse);
		}
	}

	protected I18nData getI18nData(HttpServletRequest httpServletRequest)
		throws PortalException {

		String i18nLanguageId = httpServletRequest.getServletPath();

		int pos = i18nLanguageId.lastIndexOf(CharPool.SLASH);

		i18nLanguageId = StringUtil.replace(
			i18nLanguageId.substring(pos + 1), CharPool.DASH,
			CharPool.UNDERLINE);

		if (_log.isDebugEnabled()) {
			_log.debug("Language ID " + i18nLanguageId);
		}

		if (Validator.isNull(i18nLanguageId)) {
			return null;
		}

		pos = i18nLanguageId.indexOf(CharPool.UNDERLINE);

		String i18nLanguageCode = i18nLanguageId;

		if (pos > 0) {
			i18nLanguageCode = i18nLanguageId.substring(0, pos);
		}

		Locale targetLocale = LanguageUtil.getLocale(i18nLanguageCode);

		Group siteGroup = null;

		String path = GetterUtil.getString(httpServletRequest.getPathInfo());

		Locale i18nLocale = LocaleUtil.fromLanguageId(i18nLanguageId);

		String i18nPath = StringPool.SLASH + i18nLocale.toLanguageTag();

		if (Validator.isNull(path)) {
			path = "/";
		}
		else {
			int[] friendlyURLIndices = PortalUtil.getGroupFriendlyURLIndex(
				path);

			if (friendlyURLIndices != null) {
				String friendlyURL = path.substring(
					friendlyURLIndices[0], friendlyURLIndices[1]);

				siteGroup = GroupLocalServiceUtil.fetchFriendlyURLGroup(
					PortalInstances.getCompanyId(httpServletRequest),
					friendlyURL);

				if (siteGroup == null) {
					httpServletRequest.setAttribute(
						WebKeys.I18N_ERROR_PATH, i18nPath);

					return null;
				}

				targetLocale = LanguageUtil.getLocale(
					siteGroup.getGroupId(), i18nLanguageCode);

				if ((targetLocale == null) &&
					PortalUtil.isGroupControlPanelPath(path)) {

					targetLocale = LanguageUtil.getLocale(i18nLanguageCode);
				}
			}
		}

		if (targetLocale == null) {
			if (PropsValues.LOCALE_USE_DEFAULT_IF_NOT_AVAILABLE) {
				targetLocale = PortalUtil.getSiteDefaultLocale(siteGroup);

				i18nLanguageCode = targetLocale.getLanguage();

				i18nPath = StringPool.SLASH + i18nLanguageCode;

				i18nLanguageId = LocaleUtil.toLanguageId(targetLocale);
			}
			else {
				return null;
			}
		}
		else {
			String siteDefaultLanguageId = LocaleUtil.toLanguageId(
				targetLocale);

			if (siteDefaultLanguageId.startsWith(i18nLanguageId)) {
				i18nPath = StringPool.SLASH + i18nLanguageCode;

				i18nLanguageId = siteDefaultLanguageId;
			}
		}

		String redirect = path;

		if (path.equals(HttpComponentsUtil.decodePath(path))) {
			redirect = HttpComponentsUtil.encodePath(path);
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Redirect " + redirect);
		}

		return new I18nData(
			i18nPath, i18nLanguageCode, i18nLanguageId, redirect);
	}

	protected I18nData getI18nData(Locale locale) throws PortalException {
		String languageId = LocaleUtil.toLanguageId(locale);

		String i18nPath = StringPool.SLASH + locale.toLanguageTag();

		Locale defaultLocale = LanguageUtil.getLocale(locale.getLanguage());

		if (LocaleUtil.equals(defaultLocale, locale)) {
			i18nPath = StringPool.SLASH + defaultLocale.getLanguage();
		}

		return new I18nData(
			i18nPath, locale.getLanguage(), languageId, StringPool.SLASH);
	}

	protected void sendRedirect(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, I18nData i18nData) {

		_setRequestAttributes(
			httpServletRequest, httpServletResponse, i18nData);

		String languageId = LocaleUtil.toW3cLanguageId(
			i18nData.getLanguageId());

		httpServletResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);

		ServletContext servletContext = getServletContext();

		httpServletResponse.setHeader(
			"Location",
			StringBundler.concat(
				servletContext.getContextPath(), StringPool.SLASH, languageId,
				i18nData.getPath()));
	}

	protected class I18nData {

		public I18nData(
			String i18nPath, String languageCode, String languageId,
			String path) {

			_i18nPath = i18nPath;
			_languageCode = languageCode;
			_languageId = languageId;
			_path = path;
		}

		@Override
		public boolean equals(Object object) {
			if (this == object) {
				return true;
			}

			if (!(object instanceof I18nData)) {
				return false;
			}

			I18nData i18nData = (I18nData)object;

			if (Objects.equals(getI18nPath(), i18nData.getI18nPath()) &&
				Objects.equals(getLanguageCode(), i18nData.getLanguageCode()) &&
				Objects.equals(getLanguageId(), i18nData.getLanguageId()) &&
				Objects.equals(getPath(), i18nData.getPath())) {

				return true;
			}

			return false;
		}

		public String getI18nPath() {
			return _i18nPath;
		}

		public String getLanguageCode() {
			return _languageCode;
		}

		public String getLanguageId() {
			return _languageId;
		}

		public String getPath() {
			return _path;
		}

		@Override
		public int hashCode() {
			int hash = HashUtil.hash(0, getI18nPath());

			hash = HashUtil.hash(hash, getLanguageCode());
			hash = HashUtil.hash(hash, getLanguageId());

			return HashUtil.hash(hash, getPath());
		}

		private final String _i18nPath;
		private final String _languageCode;
		private final String _languageId;
		private final String _path;

	}

	private void _processI18nData(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, I18nData i18nData)
		throws Exception {

		_setRequestAttributes(
			httpServletRequest, httpServletResponse, i18nData);

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(i18nData.getPath());

		if (httpServletRequest.isAsyncSupported()) {
			AsyncPortletServletRequest asyncPortletServletRequest =
				AsyncPortletServletRequest.getAsyncPortletServletRequest(
					httpServletRequest);

			if (asyncPortletServletRequest != null) {
				asyncPortletServletRequest.update(
					servletContext.getContextPath(), i18nData.getPath());
			}
		}

		requestDispatcher.forward(httpServletRequest, httpServletResponse);
	}

	private void _setRequestAttributes(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, I18nData i18nData) {

		httpServletRequest.setAttribute(
			WebKeys.I18N_LANGUAGE_CODE, i18nData.getLanguageCode());
		httpServletRequest.setAttribute(
			WebKeys.I18N_LANGUAGE_ID, i18nData.getLanguageId());
		httpServletRequest.setAttribute(
			WebKeys.I18N_PATH, i18nData.getI18nPath());

		Locale locale = LocaleUtil.fromLanguageId(
			i18nData.getLanguageId(), false, false);

		HttpSession httpSession = httpServletRequest.getSession();

		httpSession.setAttribute(WebKeys.LOCALE, locale);

		LanguageUtil.updateCookie(
			httpServletRequest, httpServletResponse, locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(I18nServlet.class);

	private static Map<String, String> _languageIds;

}