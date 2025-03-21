/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.servlet.filter;

import com.liferay.layout.seo.web.internal.configuration.LayoutSEODynamicRenderingConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.redirect.matcher.UserAgentMatcher;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jamie Sammons
 */
@Component(
	configurationPid = "com.liferay.layout.seo.web.internal.configuration.LayoutSEODynamicRenderingConfiguration",
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = {
		"dispatcher=FORWARD", "dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Layout SEO Dynamic Rendering Filter",
		"url-pattern=/*"
	},
	service = Filter.class
)
public class LayoutSEODynamicRenderingFilter extends BaseFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			if (!_layoutSEODynamicRenderingConfiguration.enabled() ||
				!_userAgentMatcher.isCrawlerUserAgent(
					httpServletRequest.getHeader(HttpHeaders.USER_AGENT))) {

				return false;
			}

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			String requestURI = serviceContext.getCurrentURL();

			for (String ignoredExtension :
					_layoutSEODynamicRenderingConfiguration.
						ignoredExtensions()) {

				if (requestURI.endsWith(ignoredExtension)) {
					return false;
				}
			}

			if (!_isIncludedPath(
					requestURI,
					_layoutSEODynamicRenderingConfiguration.includedPaths())) {

				return false;
			}

			Map<String, String[]> parameterMap =
				httpServletRequest.getParameterMap();

			if (parameterMap.containsKey("_escaped_fragment_")) {
				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return true;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_layoutSEODynamicRenderingConfiguration =
			ConfigurableUtil.createConfigurable(
				LayoutSEODynamicRenderingConfiguration.class, properties);
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws IOException {

		Http.Options options = new Http.Options();

		Map<String, String> headers = _getHeaders(httpServletRequest);

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			options.addHeader(entry.getKey(), entry.getValue());
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		options.setLocation(
			StringBundler.concat(
				_layoutSEODynamicRenderingConfiguration.serviceURL(),
				StringPool.SLASH, serviceContext.getPortalURL(),
				serviceContext.getCurrentURL()));

		options.setNormalizeURI(false);

		_write(_http.URLtoString(options), httpServletResponse);
	}

	private Map<String, String> _getHeaders(
		HttpServletRequest httpServletRequest) {

		Map<String, String> headers = new HashMap<>();

		Enumeration<String> enumeration = httpServletRequest.getHeaderNames();

		while (enumeration.hasMoreElements()) {
			String headerName = enumeration.nextElement();

			if (!_hopByHopHeaderNames.contains(headerName) &&
				!headerName.equals("content-length")) {

				headers.put(
					headerName, httpServletRequest.getHeader(headerName));
			}
		}

		return headers;
	}

	private boolean _isIncludedPath(String requestURI, String[] includedPaths) {
		for (String includedPath : includedPaths) {
			if (requestURI.contains(StringUtil.toLowerCase(includedPath))) {
				return true;
			}
		}

		return false;
	}

	private void _write(String html, HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setContentType("text/html; charset=UTF-8");

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(html);

		printWriter.flush();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSEODynamicRenderingFilter.class);

	private final Set<String> _hopByHopHeaderNames = SetUtil.fromArray(
		"connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
		"te", "trailers", "transfer-encoding", "upgrade");

	@Reference
	private Http _http;

	private LayoutSEODynamicRenderingConfiguration
		_layoutSEODynamicRenderingConfiguration;

	@Reference
	private UserAgentMatcher _userAgentMatcher;

}