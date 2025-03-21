/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.language;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

/**
 * @author Eduardo Lundgren
 * @author Shuyang Zhou
 */
public class LanguageFilter extends BasePortalFilter {

	@Override
	public void init(FilterConfig filterConfig) {
		super.init(filterConfig);
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		httpServletResponse.setHeader(
			HttpHeaders.CACHE_CONTROL, "private, no-cache");

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		processFilter(
			LanguageFilter.class.getName(),
			new NoCacheHttpServletRequestWrapper(httpServletRequest),
			bufferCacheServletResponse, filterChain);

		if (_log.isDebugEnabled()) {
			String completeURL = HttpComponentsUtil.getCompleteURL(
				httpServletRequest);

			_log.debug("Translating response " + completeURL);
		}

		String content = bufferCacheServletResponse.getString();

		content = translateResponse(httpServletRequest, content);

		String eTag =
			StringPool.QUOTE + DigesterUtil.digest("SHA-1", content) +
				StringPool.QUOTE;

		httpServletResponse.setHeader(HttpHeaders.ETAG, eTag);

		String ifNoneMatch = httpServletRequest.getHeader(
			HttpHeaders.IF_NONE_MATCH);

		if ((ifNoneMatch != null) && ifNoneMatch.equals(eTag)) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

			return;
		}

		ServletResponseUtil.write(httpServletResponse, content);
	}

	protected String translateResponse(
		HttpServletRequest httpServletRequest, String content) {

		Locale locale = LocaleUtil.fromLanguageId(
			LanguageUtil.getLanguageId(httpServletRequest));

		PortletConfig portletConfig = null;

		Enumeration<String> enumeration =
			httpServletRequest.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String parameterName = enumeration.nextElement();

			int index = parameterName.indexOf(CharPool.COLON);

			if (index > 0) {
				portletConfig = PortletConfigFactoryUtil.get(
					parameterName.substring(0, index));
			}
		}

		PortletConfig finalPortletConfig = portletConfig;

		return LanguageUtil.process(
			() -> {
				ResourceBundle resourceBundle =
					LanguageResources.getResourceBundle(locale);

				if (finalPortletConfig != null) {
					resourceBundle = new AggregateResourceBundle(
						finalPortletConfig.getResourceBundle(locale),
						resourceBundle);
				}

				return resourceBundle;
			},
			locale, content);
	}

	private static final Log _log = LogFactoryUtil.getLog(LanguageFilter.class);

	private static class NoCacheHttpServletRequestWrapper
		extends HttpServletRequestWrapper {

		public NoCacheHttpServletRequestWrapper(
			HttpServletRequest httpServletRequest) {

			super(httpServletRequest);

			_httpServletRequest = httpServletRequest;
		}

		public long getDateHeader(String name) {
			if (StringUtil.equalsIgnoreCase(name, "If-Modified-Since") ||
				StringUtil.equalsIgnoreCase(name, "If-None-Match")) {

				return -1;
			}

			return _httpServletRequest.getDateHeader(name);
		}

		public String getHeader(String name) {
			if (StringUtil.equalsIgnoreCase(name, "If-Modified-Since") ||
				StringUtil.equalsIgnoreCase(name, "If-None-Match")) {

				return null;
			}

			return _httpServletRequest.getHeader(name);
		}

		public Enumeration<String> getHeaderNames() {
			List<String> headerNames = new ArrayList<>();

			Enumeration<String> enumeration =
				_httpServletRequest.getHeaderNames();

			while (enumeration.hasMoreElements()) {
				String name = enumeration.nextElement();

				if (StringUtil.equalsIgnoreCase(name, "If-Modified-Since") ||
					StringUtil.equalsIgnoreCase(name, "If-None-Match")) {

					continue;
				}

				headerNames.add(name);
			}

			return new Enumeration<String>() {

				@Override
				public boolean hasMoreElements() {
					if (_nextIndex < headerNames.size()) {
						return true;
					}

					return false;
				}

				@Override
				public String nextElement() {
					if (!hasMoreElements()) {
						throw new NoSuchElementException();
					}

					_nextIndex++;

					return headerNames.get(_nextIndex - 1);
				}

				private int _nextIndex;

			};
		}

		public Enumeration<String> getHeaders(String name) {
			if (StringUtil.equalsIgnoreCase(name, "If-Modified-Since") ||
				StringUtil.equalsIgnoreCase(name, "If-None-Match")) {

				return null;
			}

			return _httpServletRequest.getHeaders(name);
		}

		public int getIntHeader(String name) {
			if (StringUtil.equalsIgnoreCase(name, "If-Modified-Since") ||
				StringUtil.equalsIgnoreCase(name, "If-None-Match")) {

				return -1;
			}

			return _httpServletRequest.getIntHeader(name);
		}

		private final HttpServletRequest _httpServletRequest;

	}

}