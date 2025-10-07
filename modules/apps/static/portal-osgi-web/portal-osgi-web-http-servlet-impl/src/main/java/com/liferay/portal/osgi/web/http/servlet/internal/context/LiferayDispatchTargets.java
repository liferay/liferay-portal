/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.context;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.EndpointRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.registration.FilterRegistration;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.LiferayHttpServletRequestWrapper;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.LiferayHttpServletResponseWrapper;
import com.liferay.portal.osgi.web.http.servlet.internal.servlet.ResponseStateHandler;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import java.net.URLDecoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dante Wang
 */
public class LiferayDispatchTargets {

	public LiferayDispatchTargets(
		EndpointRegistration<?> endpointRegistration,
		LiferayContextController liferayContextController,
		List<FilterRegistration> matchingFilterRegistrations, String pathInfo,
		String queryString, String requestURI, String servletName,
		String servletPath) {

		_endpointRegistration = endpointRegistration;
		_liferayContextController = liferayContextController;
		_matchingFilterRegistrations = matchingFilterRegistrations;
		_pathInfo = pathInfo;
		_queryString = queryString;
		_requestURI = requestURI;
		_servletName = servletName;
		_servletPath = GetterUtil.getString(servletPath);
	}

	public LiferayDispatchTargets(
		EndpointRegistration<?> endpointRegistration,
		LiferayContextController liferayContextController, String pathInfo,
		String queryString, String requestURI, String servletName,
		String servletPath) {

		this(
			endpointRegistration, liferayContextController,
			Collections.emptyList(), pathInfo, queryString, requestURI,
			servletName, servletPath);
	}

	public void addRequestParameters(HttpServletRequest httpServletRequest) {
		if (_queryString == null) {
			_parameterMap = httpServletRequest.getParameterMap();
			_queryString = httpServletRequest.getQueryString();

			return;
		}

		Map<String, String[]> parsedParameterMap = _parseParameterMap(
			_queryString);

		Map<String, String[]> requestParameterMap =
			httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry :
				requestParameterMap.entrySet()) {

			parsedParameterMap.put(
				entry.getKey(),
				_append(
					parsedParameterMap.get(entry.getKey()), entry.getValue()));
		}

		_parameterMap = Collections.unmodifiableMap(parsedParameterMap);
	}

	public boolean doDispatch(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String path,
			DispatcherType requestedDispatcherType)
		throws IOException, ServletException {

		setDispatcherType(requestedDispatcherType);

		RequestAttributeSetter requestAttributeSetter =
			new RequestAttributeSetter(httpServletRequest);

		if (_dispatcherType == DispatcherType.FORWARD) {
			if (!httpServletRequest.isAsyncStarted() &&
				!httpServletResponse.isCommitted()) {

				httpServletResponse.resetBuffer();
			}

			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_FORWARD_CONTEXT_PATH,
				httpServletRequest.getContextPath());
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_FORWARD_PATH_INFO,
				httpServletRequest.getPathInfo());
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_FORWARD_QUERY_STRING,
				httpServletRequest.getQueryString());
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_FORWARD_REQUEST_URI,
				httpServletRequest.getRequestURI());
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_FORWARD_SERVLET_PATH,
				httpServletRequest.getServletPath());
		}
		else if (_dispatcherType == DispatcherType.INCLUDE) {
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_CONTEXT_PATH,
				_liferayContextController.getFullContextPath());
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_PATH_INFO, getPathInfo());
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_QUERY_STRING,
				getQueryString());
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_REQUEST_URI,
				getRequestURI());
			requestAttributeSetter.setAttribute(
				JavaConstants.JAKARTA_SERVLET_INCLUDE_SERVLET_PATH,
				getServletPath());
		}

		HttpServletRequest newHttpServletRequest = httpServletRequest;

		LiferayHttpServletRequestWrapper liferayHttpServletRequestWrapper =
			LiferayHttpServletRequestWrapper.find(httpServletRequest);

		if (liferayHttpServletRequestWrapper == null) {
			liferayHttpServletRequestWrapper =
				new LiferayHttpServletRequestWrapper(httpServletRequest);

			newHttpServletRequest = liferayHttpServletRequestWrapper;

			httpServletResponse = new LiferayHttpServletResponseWrapper(
				httpServletResponse);
		}

		try {
			liferayHttpServletRequestWrapper.push(this);

			ResponseStateHandler responseStateHandler =
				new ResponseStateHandler(
					newHttpServletRequest, httpServletResponse, this);

			responseStateHandler.processRequest();

			if ((_dispatcherType == DispatcherType.FORWARD) &&
				!httpServletResponse.isCommitted() &&
				!newHttpServletRequest.isAsyncStarted()) {

				try {
					httpServletResponse.flushBuffer();

					Writer writer = httpServletResponse.getWriter();

					writer.close();
				}
				catch (IllegalStateException illegalStateException) {
					if (_log.isDebugEnabled()) {
						_log.debug(illegalStateException);
					}

					try {
						ServletOutputStream servletOutputStream =
							httpServletResponse.getOutputStream();

						servletOutputStream.close();
					}
					catch (IllegalStateException | IOException exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}
					}
				}
			}
		}
		finally {
			liferayHttpServletRequestWrapper.pop();

			requestAttributeSetter.close();
		}

		return true;
	}

	public LiferayContextController getContextController() {
		return _liferayContextController;
	}

	public DispatcherType getDispatcherType() {
		return _dispatcherType;
	}

	public List<FilterRegistration> getMatchingFilterRegistrations() {
		return _matchingFilterRegistrations;
	}

	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	public String getPathInfo() {
		return _pathInfo;
	}

	public String getQueryString() {
		return _queryString;
	}

	public String getRequestURI() {
		if (_requestURI == null) {
			return null;
		}

		return _liferayContextController.getFullContextPath() + _requestURI;
	}

	public String getServletName() {
		return _servletName;
	}

	public String getServletPath() {
		return _servletPath;
	}

	public EndpointRegistration<?> getServletRegistration() {
		return _endpointRegistration;
	}

	public Map<String, Object> getSpecialOverides() {
		return _specialOverrides;
	}

	public void setDispatcherType(DispatcherType dispatcherType) {
		_dispatcherType = dispatcherType;
	}

	@Override
	public String toString() {
		String value = _string;

		if (value == null) {
			String queryString = StringPool.BLANK;

			if (_queryString != null) {
				queryString = StringPool.QUESTION + _queryString;
			}

			value = StringBundler.concat(
				_SIMPLE_NAME, CharPool.OPEN_BRACKET,
				_liferayContextController.getFullContextPath(), _requestURI,
				queryString, CharPool.COMMA, CharPool.SPACE,
				_endpointRegistration, CharPool.CLOSE_BRACKET);

			_string = value;
		}

		return value;
	}

	private String[] _append(String[] params, String value) {
		if (params.length == 0) {
			return new String[] {value};
		}

		String[] newParams = new String[params.length + 1];

		System.arraycopy(params, 0, newParams, 0, params.length);

		newParams[params.length] = Objects.requireNonNullElse(
			value, StringPool.BLANK);

		return newParams;
	}

	private String[] _append(String[] params, String... values) {
		if (values == null) {
			values = new String[1];
		}

		String[] newParams = values;

		int length = 0;

		if (params != null) {
			length = params.length;

			newParams = new String[params.length + values.length];

			System.arraycopy(params, 0, newParams, 0, params.length);
		}

		for (int i = 0; i < values.length; ++i) {
			newParams[length + i] = Objects.requireNonNullElse(
				values[i], StringPool.BLANK);
		}

		return newParams;
	}

	private Map<String, String[]> _parseParameterMap(String queryString) {
		if ((queryString == null) || queryString.isEmpty()) {
			return new HashMap<>();
		}

		try {
			Map<String, String[]> parameterMap = new LinkedHashMap<>();

			String[] parameters = StringUtil.split(
				queryString, CharPool.AMPERSAND);

			for (String parameter : parameters) {
				int index = parameter.indexOf(CharPool.EQUAL);

				String name = parameter;

				if (index > 0) {
					name = URLDecoder.decode(
						parameter.substring(0, index), StringPool.UTF8);
				}

				String[] values = parameterMap.get(name);

				if (values == null) {
					values = new String[0];
				}

				String value = null;

				if ((index > 0) && (parameter.length() > (index + 1))) {
					value = URLDecoder.decode(
						parameter.substring(index + 1), StringPool.UTF8);
				}

				values = _append(values, value);

				parameterMap.put(name, values);
			}

			return parameterMap;
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException(unsupportedEncodingException);
		}
	}

	private static final String _SIMPLE_NAME =
		LiferayDispatchTargets.class.getSimpleName();

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayDispatchTargets.class);

	private DispatcherType _dispatcherType;
	private final EndpointRegistration<?> _endpointRegistration;
	private final LiferayContextController _liferayContextController;
	private final List<FilterRegistration> _matchingFilterRegistrations;
	private Map<String, String[]> _parameterMap;
	private final String _pathInfo;
	private String _queryString;
	private final String _requestURI;
	private final String _servletName;
	private final String _servletPath;
	private final Map<String, Object> _specialOverrides =
		new ConcurrentHashMap<>();
	private String _string;

	private static class RequestAttributeSetter implements Closeable {

		public RequestAttributeSetter(ServletRequest servletRequest) {
			_servletRequest = servletRequest;
		}

		public void close() {
			for (Map.Entry<String, Object> oldValue : _oldValues.entrySet()) {
				if (oldValue.getValue() == null) {
					_servletRequest.removeAttribute(oldValue.getKey());
				}
				else {
					_servletRequest.setAttribute(
						oldValue.getKey(), oldValue.getValue());
				}
			}
		}

		public void setAttribute(String name, Object value) {
			_oldValues.put(name, _servletRequest.getAttribute(name));

			_servletRequest.setAttribute(name, value);
		}

		private final Map<String, Object> _oldValues = new HashMap<>();
		private final ServletRequest _servletRequest;

	}

}