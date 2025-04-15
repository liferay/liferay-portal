/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.mvc.locale.LocaleResolverContext;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Neil Griffin
 */
public class LocaleResolverContextImpl implements LocaleResolverContext {

	public LocaleResolverContextImpl(
		List<Locale> acceptableLanguages, Configuration configuration,
		Map<String, Cookie> cookies, Map<String, String> headers,
		UriInfo uriInfo) {

		_acceptableLanguages = acceptableLanguages;
		_configuration = configuration;
		_cookies = cookies;
		_headers = headers;
		_uriInfo = uriInfo;
	}

	@Override
	public List<Locale> getAcceptableLanguages() {
		return _acceptableLanguages;
	}

	@Override
	public Configuration getConfiguration() {
		return _configuration;
	}

	@Override
	public Cookie getCookie(String name) {
		return _cookies.get(name);
	}

	@Override
	public String getHeaderString(String name) {
		return _headers.get(name);
	}

	@Override
	public Request getRequest() {
		return new RequestImpl();
	}

	@Override
	public UriInfo getUriInfo() {
		return _uriInfo;
	}

	private final List<Locale> _acceptableLanguages;
	private final Configuration _configuration;
	private final Map<String, Cookie> _cookies;
	private final Map<String, String> _headers;
	private final UriInfo _uriInfo;

}