/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.template.servlet;

import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.internal.constants.VulcanConstants;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class RESTClientHttpRequestDelegate {

	public RESTClientHttpRequestDelegate(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest, String pathInfo) {

		_attributes = HashMapBuilder.<String, Object>put(
			RESTClientHttpRequestDelegate.class.getName(), true
		).put(
			WebKeys.USER,
			() -> {
				if (contextObjects.containsKey("user")) {
					return contextObjects.get("user");
				}

				return null;
			}
		).build();
		_headers = HashMapBuilder.put(
			HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON
		).put(
			"Accept-Language",
			() -> {
				Locale locale = PortalUtil.getLocale(httpServletRequest);

				return locale.toLanguageTag();
			}
		).put(
			"X-CSRF-Token",
			() -> {
				HttpSession httpSession =
					PortalSessionThreadLocal.getHttpSession();

				if (httpSession == null) {
					return null;
				}

				String csrfToken = (String)httpSession.getAttribute(
					WebKeys.AUTHENTICATION_TOKEN + "#CSRF");

				if (csrfToken == null) {
					return null;
				}

				httpSession = httpServletRequest.getSession(false);

				if (httpSession != null) {
					httpSession.setAttribute(
						WebKeys.AUTHENTICATION_TOKEN + "#CSRF", csrfToken);
				}

				return csrfToken;
			}
		).build();
		_httpServletRequest = httpServletRequest;
		_pathInfo = pathInfo;
	}

	public Object getAttribute(String name) {
		Object attributeValue = _attributes.get(name);

		if (attributeValue != null) {
			return attributeValue;
		}

		if (VulcanConstants.TRANSACTION_CLEAN_UP_MESSAGE_OBSERVER.equals(
				name)) {

			return null;
		}

		return _httpServletRequest.getAttribute(name);
	}

	public DispatcherType getDispatcherType() {
		return DispatcherType.FORWARD;
	}

	public String getHeader(String name) {
		return _headers.get(name);
	}

	public Enumeration<String> getHeaders(String name) {
		String value = _headers.get(name);

		if (Validator.isNotNull(value)) {
			return Collections.enumeration(Arrays.asList(value));
		}

		return Collections.emptyEnumeration();
	}

	public String getMethod() {
		return HttpMethods.GET;
	}

	public String getPathInfo() {
		return _pathInfo;
	}

	public void removeAttribute(String name) {
		_attributes.remove(name);
	}

	public void setAttribute(String name, Object object) {
		_attributes.put(name, object);
	}

	public void setCharacterEncoding(String characterEncoding) {
	}

	private final Map<String, Object> _attributes;
	private final Map<String, String> _headers;
	private final HttpServletRequest _httpServletRequest;
	private final String _pathInfo;

}