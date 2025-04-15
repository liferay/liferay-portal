/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpOnlyCookieServletResponse;
import com.liferay.portal.kernel.servlet.NonSerializableObjectRequestWrapper;
import com.liferay.portal.kernel.servlet.SanitizedServletResponse;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class InvokerFilter implements Filter {

	@Override
	public void destroy() {
		ServletContext servletContext = _filterConfig.getServletContext();

		InvokerFilterHelper invokerFilterHelper =
			(InvokerFilterHelper)servletContext.getAttribute(
				InvokerFilterHelper.class.getName());

		if (invokerFilterHelper != null) {
			servletContext.removeAttribute(InvokerFilterHelper.class.getName());

			invokerFilterHelper.destroy();
		}

		if (_INVOKER_FILTER_CHAIN_ENABLED) {
			PortalCacheHelperUtil.removePortalCache(
				PortalCacheManagerNames.SINGLE_VM, _getPortalCacheName());
		}
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)servletResponse;

		String originalURI = getOriginalRequestURI(httpServletRequest);

		if (!handleLongRequestURL(
				httpServletRequest, httpServletResponse, originalURI)) {

			return;
		}

		httpServletRequest = handleNonSerializableRequest(httpServletRequest);

		httpServletResponse =
			HttpOnlyCookieServletResponse.getHttpOnlyCookieServletResponse(
				httpServletResponse);

		httpServletResponse = secureResponseHeaders(
			httpServletRequest, httpServletResponse);

		String uri = getURI(originalURI);

		httpServletRequest.setAttribute(WebKeys.INVOKER_FILTER_URI, uri);

		try {
			InvokerFilterChain invokerFilterChain = getInvokerFilterChain(
				httpServletRequest, uri, filterChain);

			Thread currentThread = Thread.currentThread();

			invokerFilterChain.setContextClassLoader(
				currentThread.getContextClassLoader());

			invokerFilterChain.doFilter(
				httpServletRequest, httpServletResponse);
		}
		finally {
			httpServletRequest.removeAttribute(WebKeys.INVOKER_FILTER_URI);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		_filterConfig = filterConfig;

		ServletContext servletContext = _filterConfig.getServletContext();

		_contextPath = servletContext.getContextPath();

		if (_INVOKER_FILTER_CHAIN_ENABLED) {
			_filterChainsPortalCache = PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM, _getPortalCacheName());
		}

		InvokerFilterHelper invokerFilterHelper =
			(InvokerFilterHelper)servletContext.getAttribute(
				InvokerFilterHelper.class.getName());

		if (invokerFilterHelper == null) {
			invokerFilterHelper = new InvokerFilterHelper();

			servletContext.setAttribute(
				InvokerFilterHelper.class.getName(), invokerFilterHelper);

			invokerFilterHelper.init(_filterConfig);
		}

		_invokerFilterHelper = invokerFilterHelper;

		_invokerFilterHelper.addInvokerFilter(this);

		_dispatcher = Dispatcher.valueOf(
			_filterConfig.getInitParameter("dispatcher"));
	}

	protected void clearFilterChainsCache() {
		if (_filterChainsPortalCache != null) {
			_filterChainsPortalCache.removeAll();
		}
	}

	protected InvokerFilterChain getInvokerFilterChain(
		HttpServletRequest httpServletRequest, String uri,
		FilterChain filterChain) {

		if (_filterChainsPortalCache == null) {
			return _invokerFilterHelper.createInvokerFilterChain(
				httpServletRequest, _dispatcher, uri, filterChain);
		}

		String key = uri;

		String queryString = httpServletRequest.getQueryString();

		if (Validator.isNotNull(queryString) &&
			!_skipQueryStringURIs.contains(uri)) {

			key = StringBundler.concat(
				key, StringPool.QUESTION, _scrubQueryString(queryString));
		}

		InvokerFilterChain invokerFilterChain = _filterChainsPortalCache.get(
			key);

		if (invokerFilterChain == null) {
			invokerFilterChain = _invokerFilterHelper.createInvokerFilterChain(
				httpServletRequest, _dispatcher, uri, filterChain);

			_filterChainsPortalCache.put(key, invokerFilterChain);
		}

		return invokerFilterChain.clone(filterChain);
	}

	protected String getOriginalRequestURI(
		HttpServletRequest httpServletRequest) {

		String uri = null;

		if (_dispatcher == Dispatcher.ERROR) {
			uri = (String)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_SERVLET_ERROR_REQUEST_URI);
		}
		else if (_dispatcher == Dispatcher.INCLUDE) {
			uri = (String)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_SERVLET_INCLUDE_REQUEST_URI);
		}
		else {
			uri = httpServletRequest.getRequestURI();
		}

		return uri;
	}

	protected String getURI(String originalURI) {
		if (Validator.isNotNull(_contextPath) &&
			!_contextPath.equals(StringPool.SLASH) &&
			originalURI.startsWith(_contextPath)) {

			originalURI = originalURI.substring(_contextPath.length());
		}

		return HttpComponentsUtil.normalizePath(originalURI);
	}

	protected boolean handleLongRequestURL(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String originalURI)
		throws IOException {

		String queryString = httpServletRequest.getQueryString();

		int length = originalURI.length();

		if (queryString != null) {
			length += queryString.length();
		}

		if (length <= _INVOKER_FILTER_URI_MAX_LENGTH) {
			return true;
		}

		httpServletResponse.sendError(
			HttpServletResponse.SC_REQUEST_URI_TOO_LONG);

		if (_log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Rejected ",
					StringUtil.shorten(
						originalURI, _INVOKER_FILTER_URI_MAX_LENGTH),
					" because it has more than ",
					_INVOKER_FILTER_URI_MAX_LENGTH, " characters"));
		}

		return false;
	}

	protected HttpServletRequest handleNonSerializableRequest(
		HttpServletRequest httpServletRequest) {

		if (ServerDetector.isWebLogic() &&
			!NonSerializableObjectRequestWrapper.isWrapped(
				httpServletRequest)) {

			httpServletRequest = new NonSerializableObjectRequestWrapper(
				httpServletRequest);
		}

		return httpServletRequest;
	}

	protected HttpServletResponse secureResponseHeaders(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (Boolean.FALSE.equals(
				httpServletRequest.getAttribute(_SECURE_RESPONSE))) {

			return httpServletResponse;
		}

		httpServletRequest.setAttribute(_SECURE_RESPONSE, Boolean.FALSE);

		return SanitizedServletResponse.getSanitizedServletResponse(
			httpServletRequest, httpServletResponse);
	}

	private String _getPortalCacheName() {
		ServletContext servletContext = _filterConfig.getServletContext();

		String servletContextName = servletContext.getContextPath();

		if (Validator.isNull(servletContextName)) {
			return _filterConfig.getFilterName();
		}

		return StringBundler.concat(
			servletContextName, StringPool.DASH, _filterConfig.getFilterName());
	}

	private String _scrubQueryString(String queryString) {
		String[] parameters = StringUtil.split(queryString, CharPool.AMPERSAND);

		for (int i = 0; i < parameters.length; i++) {
			String parameter = parameters[i];

			int index = parameter.indexOf(CharPool.EQUAL);

			if ((index != -1) &&
				_queryStringIgnoredKeys.contains(
					parameter.substring(0, index))) {

				parameters[i] = StringPool.BLANK;
			}
		}

		Arrays.sort(parameters);

		StringBundler sb = new StringBundler();

		for (String parameter : parameters) {
			if (!parameter.isEmpty()) {
				sb.append(parameter);
				sb.append(StringPool.AMPERSAND);
			}
		}

		if (sb.index() != 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private static final boolean _INVOKER_FILTER_CHAIN_ENABLED =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.INVOKER_FILTER_CHAIN_ENABLED));

	private static final int _INVOKER_FILTER_URI_MAX_LENGTH =
		GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.INVOKER_FILTER_URI_MAX_LENGTH));

	private static final String _SECURE_RESPONSE =
		InvokerFilter.class.getName() + "SECURE_RESPONSE";

	private static final Log _log = LogFactoryUtil.getLog(InvokerFilter.class);

	private static final Set<String> _queryStringIgnoredKeys = new HashSet<>(
		Arrays.asList(
			PropsUtil.getArray(
				PropsKeys.
					INVOKER_FILTER_CHAIN_CACHE_QUERY_STRING_IGNORED_KEYS)));
	private static final Set<String> _skipQueryStringURIs = new HashSet<>(
		Arrays.asList(
			PropsUtil.getArray(
				PropsKeys.INVOKER_FILTER_CHAIN_CACHE_SKIP_QUERY_STRING_URIS)));

	private String _contextPath;
	private Dispatcher _dispatcher;
	private PortalCache<String, InvokerFilterChain> _filterChainsPortalCache;
	private FilterConfig _filterConfig;
	private InvokerFilterHelper _invokerFilterHelper;

}