/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.http;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.servlet.PortalSessionThreadLocal;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.vulcan.internal.constants.VulcanConstants;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Alejandro Tardín
 */
public class VulcanRequestForwarderHttpServletRequestWrapper
	extends PersistentHttpServletRequestWrapper {

	public VulcanRequestForwarderHttpServletRequestWrapper(
		byte[] body, String contentType, Map<String, String> headers,
		HttpServletRequest httpServletRequest, String method, String pathInfo,
		User user) {

		super(httpServletRequest);

		_attributes = HashMapBuilder.<String, Object>put(
			VulcanRequestForwarderHttpServletRequestWrapper.class.getName(),
			true
		).put(
			WebKeys.USER_ID, (user != null) ? user.getUserId() : null
		).build();

		_headers = HashMapBuilder.put(
			HttpHeaders.ACCEPT, ContentTypes.APPLICATION_JSON
		).put(
			HttpHeaders.CONTENT_LENGTH,
			(body != null) ? String.valueOf(body.length) : null
		).put(
			HttpHeaders.CONTENT_TYPE, contentType
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

		if (headers != null) {
			_headers.putAll(headers);
		}

		_body = body;
		_contentType = contentType;
		_httpServletRequest = httpServletRequest;
		_method = method;
		_pathInfo = pathInfo;

		int index = pathInfo.indexOf('?');

		_queryString = (index > -1) ? pathInfo.substring(index + 1) : null;

		_parameterMap = HttpComponentsUtil.getParameterMap(_queryString);
	}

	@Override
	public Object getAttribute(String name) {
		Object attributeValue = _attributes.get(name);

		if (attributeValue != null) {
			return attributeValue;
		}

		if (name.startsWith("jakarta.servlet.include.") ||
			VulcanConstants.TRANSACTION_CLEAN_UP_MESSAGE_OBSERVER.equals(
				name)) {

			return null;
		}

		return _httpServletRequest.getAttribute(name);
	}

	@Override
	public int getContentLength() {
		if (_body == null) {
			return super.getContentLength();
		}

		return _body.length;
	}

	@Override
	public long getContentLengthLong() {
		return getContentLength();
	}

	@Override
	public String getContentType() {
		return _contentType;
	}

	@Override
	public DispatcherType getDispatcherType() {
		return DispatcherType.FORWARD;
	}

	@Override
	public String getHeader(String name) {
		return _headers.get(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		List<String> headerNames = new ArrayList<>();

		for (Map.Entry<String, String> entry : _headers.entrySet()) {
			if (entry.getValue() != null) {
				headerNames.add(entry.getKey());
			}
		}

		return Collections.enumeration(headerNames);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		String value = _headers.get(name);

		if (Validator.isNotNull(value)) {
			return Collections.enumeration(Arrays.asList(value));
		}

		return Collections.emptyEnumeration();
	}

	@Override
	public ServletInputStream getInputStream() {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
			(_body != null) ? _body : new byte[0]);

		return new ServletInputStream() {

			@Override
			public boolean isFinished() {
				if (_byteArrayInputStream.available() == 0) {
					return true;
				}

				return false;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public int read() {
				return _byteArrayInputStream.read();
			}

			@Override
			public int read(byte[] bytes, int offset, int length) {
				return _byteArrayInputStream.read(bytes, offset, length);
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				throw new UnsupportedOperationException();
			}

			private final ByteArrayInputStream _byteArrayInputStream =
				byteArrayInputStream;

		};
	}

	@Override
	public String getMethod() {
		return _method;
	}

	@Override
	public String getParameter(String name) {
		String[] value = _parameterMap.get(name);

		return ((value != null) && (value.length > 0)) ? value[0] : null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(_parameterMap.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return _parameterMap.get(name);
	}

	@Override
	public String getPathInfo() {
		return _pathInfo;
	}

	@Override
	public String getQueryString() {
		return _queryString;
	}

	@Override
	public void removeAttribute(String name) {
		_attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object object) {
		_attributes.put(name, object);
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
	}

	private final Map<String, Object> _attributes;
	private final byte[] _body;
	private final String _contentType;
	private final Map<String, String> _headers;
	private final HttpServletRequest _httpServletRequest;
	private final String _method;
	private final Map<String, String[]> _parameterMap;
	private final String _pathInfo;
	private final String _queryString;

}