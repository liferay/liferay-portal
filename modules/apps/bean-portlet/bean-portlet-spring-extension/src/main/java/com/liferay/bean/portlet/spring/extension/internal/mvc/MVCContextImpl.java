/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import com.liferay.bean.portlet.extension.CSRFLiferayPortletURL;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.mvc.MvcContext;
import jakarta.mvc.locale.LocaleResolver;
import jakarta.mvc.locale.LocaleResolverContext;
import jakarta.mvc.security.Csrf;
import jakarta.mvc.security.Encoders;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.Cookie;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Neil Griffin
 */
public class MVCContextImpl implements MvcContext {

	public MVCContextImpl(
		Configuration configuration, Encoders encoders,
		List<LocaleResolver> localeResolvers, PortletContext portletContext,
		PortletRequest portletRequest) {

		_configuration = configuration;
		_encoders = encoders;
		_portletContext = portletContext;
		_portletRequest = portletRequest;

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		CSRFLiferayPortletURL csrfLiferayPortletURL = new CSRFLiferayPortletURL(
			portletDisplay.getId());

		AuthTokenUtil.addCSRFToken(
			themeDisplay.getRequest(), csrfLiferayPortletURL);

		_csrf = new CsrfImpl(
			"p_auth", csrfLiferayPortletURL.getParameter("p_auth"));

		Map<String, jakarta.ws.rs.core.Cookie> cookieMap = new HashMap<>();

		Cookie[] cookies = portletRequest.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookieMap.put(
					cookie.getName(),
					new jakarta.ws.rs.core.Cookie(
						cookie.getName(), cookie.getValue()));
			}
		}

		Map<String, String> headerMap = new HashMap<>();

		Enumeration<String> enumeration = portletRequest.getPropertyNames();

		while (enumeration.hasMoreElements()) {
			String header = enumeration.nextElement();

			headerMap.put(header, portletRequest.getProperty(header));
		}

		LocaleResolverContext localeResolverContext =
			new LocaleResolverContextImpl(
				Collections.list(portletRequest.getLocales()), _configuration,
				cookieMap, headerMap, new UriInfoImpl());

		Locale locale = null;

		for (LocaleResolver localeResolver : localeResolvers) {
			locale = localeResolver.resolveLocale(localeResolverContext);

			if (locale != null) {
				break;
			}
		}

		_locale = locale;
	}

	@Override
	public String getBasePath() {
		return _portletContext.getContextPath();
	}

	@Override
	public Configuration getConfig() {
		return _configuration;
	}

	@Override
	public Csrf getCsrf() {
		return _csrf;
	}

	@Override
	public Encoders getEncoders() {
		return _encoders;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public URI uri(String identifier) {
		throw new UnsupportedOperationException();
	}

	@Override
	public URI uri(String identifier, Map<String, Object> parameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public UriBuilder uriBuilder(String identifier) {
		throw new UnsupportedOperationException();
	}

	private final Configuration _configuration;
	private final Csrf _csrf;
	private final Encoders _encoders;
	private final Locale _locale;
	private final PortletContext _portletContext;
	private final PortletRequest _portletRequest;

}