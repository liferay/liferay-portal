/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.servlet;

import com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details;
import com.liferay.frontend.js.loader.modules.extender.internal.resolution.BrowserModulesResolution;
import com.liferay.frontend.js.loader.modules.extender.internal.resolution.BrowserModulesResolver;
import com.liferay.frontend.js.loader.modules.extender.internal.servlet.util.JSLoaderModulesUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistry;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rodolfo Roza Miranda
 */
@Component(
	configurationPid = "com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details",
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.frontend.js.loader.modules.extender.internal.servlet.JSResolveModulesServlet",
		"osgi.http.whiteboard.servlet.pattern=/js_resolve_modules",
		"service.ranking:Integer=" + Details.MAX_VALUE_LESS_1K
	},
	service = Servlet.class
)
public class JSResolveModulesServlet extends HttpServlet {

	@Activate
	@Modified
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_browserModulesResolver = new BrowserModulesResolver(
			_absolutePortalURLBuilderFactory, bundleContext, _jsonFactory,
			_npmRegistry, properties);

		_details = ConfigurableUtil.createConfigurable(
			Details.class, properties);
	}

	@Deactivate
	protected void deactivate() {
		_browserModulesResolver.destroy();
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		long maxRequestLength = _getMaxRequestLength();
		String queryString = httpServletRequest.getQueryString();

		if ((queryString != null) &&
			(queryString.getBytes(StandardCharsets.UTF_8).length >
				maxRequestLength)) {

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Module resolution query string exceeds the maximum ",
						"length of ", maxRequestLength, " bytes"));
			}

			httpServletResponse.setStatus(
				HttpServletResponse.SC_REQUEST_URI_TOO_LONG);

			return;
		}

		_writeResponse(
			httpServletRequest, httpServletResponse,
			Arrays.asList(
				ParamUtil.getStringValues(httpServletRequest, "modules")));
	}

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		long maxRequestLength = _getMaxRequestLength();

		if (httpServletRequest.getContentLengthLong() > maxRequestLength) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Module resolution request content length exceeds the ",
						"maximum length of ", maxRequestLength, " bytes"));
			}

			httpServletResponse.setStatus(
				HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);

			return;
		}

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		StreamUtil.transfer(
			httpServletRequest.getInputStream(), unsyncByteArrayOutputStream,
			StreamUtil.BUFFER_SIZE, false, maxRequestLength + 1);

		if (unsyncByteArrayOutputStream.size() > maxRequestLength) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Module resolution request body exceeds the maximum ",
						"length of ", maxRequestLength, " bytes"));
			}

			httpServletResponse.setStatus(
				HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);

			return;
		}

		if (unsyncByteArrayOutputStream.size() == 0) {
			_writeResponse(
				httpServletRequest, httpServletResponse,
				Collections.emptyList());

			return;
		}

		String body = unsyncByteArrayOutputStream.toString(StringPool.UTF8);

		if (!body.startsWith(_MODULES_PREFIX)) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Module resolution request body must start with \"" +
						_MODULES_PREFIX + "\"");
			}

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		try {
			body = URLDecoder.decode(
				body.substring(_MODULES_PREFIX.length()),
				StandardCharsets.UTF_8);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to decode module resolution request body",
					illegalArgumentException);
			}

			httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

			return;
		}

		_writeResponse(
			httpServletRequest, httpServletResponse,
			Arrays.asList(body.split(StringPool.COMMA)));
	}

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-48372")) {
			return;
		}

		if (JSLoaderModulesUtil.eTagEquals(
				httpServletRequest.getHeader(HttpHeaders.IF_NONE_MATCH))) {

			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

			return;
		}

		super.service(httpServletRequest, httpServletResponse);
	}

	private long _getMaxRequestLength() {
		long maxRequestLength = _details.maxRequestLength();

		if ((maxRequestLength <= 0) ||
			(maxRequestLength > _MAX_REQUEST_LENGTH)) {

			return _MAX_REQUEST_LENGTH;
		}

		return maxRequestLength;
	}

	private void _writeResponse(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, List<String> moduleNames)
		throws IOException {

		httpServletResponse.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		httpServletResponse.addHeader(
			HttpHeaders.ETAG, JSLoaderModulesUtil.getETag());
		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		BrowserModulesResolution browserModulesResolution =
			_browserModulesResolver.resolve(
				ListUtil.unique(moduleNames), httpServletRequest);

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(browserModulesResolution.toJSON());

		printWriter.close();
	}

	private List<String> _getModuleNames(HttpServletRequest httpServletRequest)
		throws IOException {

		String[] moduleNames = null;

		String method = httpServletRequest.getMethod();

		if (method.equals("GET")) {
			moduleNames = ParamUtil.getStringValues(
				httpServletRequest, "modules");
		}
		else {
			String body = StringUtil.read(httpServletRequest.getInputStream());

			body = URLDecoder.decode(
				body, httpServletRequest.getCharacterEncoding());

			if (!body.isEmpty()) {
				body = body.substring(8);

				moduleNames = body.split(StringPool.COMMA);
			}
		}

		if (moduleNames != null) {
			return Arrays.asList(moduleNames);
		}

		return Collections.emptyList();
	}

	private static final long _MAX_REQUEST_LENGTH = 1024 * 1024;

	private static final String _MODULES_PREFIX = "modules=";

	private static final Log _log = LogFactoryUtil.getLog(
		JSResolveModulesServlet.class);

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	private BrowserModulesResolver _browserModulesResolver;

	private volatile Details _details;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private NPMRegistry _npmRegistry;

}