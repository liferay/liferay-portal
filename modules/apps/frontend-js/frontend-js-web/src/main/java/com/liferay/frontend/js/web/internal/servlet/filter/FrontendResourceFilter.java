/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.filter;

import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.internal.resource.handler.FrontendResourceRequestHandler;
import com.liferay.frontend.js.web.internal.resource.handler.HashedFileFrontendResourceRequestHandler;
import com.liferay.frontend.js.web.internal.resource.handler.JavaScriptFrontendResourceRequestHandler;
import com.liferay.frontend.js.web.internal.resource.handler.LanguageFrontendResourceRequestHandler;
import com.liferay.frontend.js.web.internal.resource.handler.StyleSheetFrontendResourceRequestHandler;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletConfigFactoryUtil;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.servlet.BrowserSnifferUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	configurationPid = "com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration",
	property = {
		"before-filter=Header Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Frontend Resource Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class FrontendResourceFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		List<FrontendResourceRequestHandler> frontendResourceRequestHandlers =
			_frontendResourceRequestHandlers.get();

		for (FrontendResourceRequestHandler frontendResourceRequestHandler :
				frontendResourceRequestHandlers) {

			if (frontendResourceRequestHandler.canHandleRequest(
					httpServletRequest)) {

				return true;
			}
		}

		return false;
	}

	@Activate
	protected void activate() {
		_gZipCompressionLevel = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.GZIP_COMPRESSION_LEVEL),
			Deflater.DEFAULT_COMPRESSION);
		_gZipEnabled = GetterUtil.getBoolean(PropsUtil.get(_GZIP_FILTER));

		List<FrontendResourceRequestHandler> frontendResourceRequestHandlers =
			new ArrayList<>();

		frontendResourceRequestHandlers.add(
			new HashedFileFrontendResourceRequestHandler(
				_configurationProvider, ContentTypes.APPLICATION_JSON, ".map",
				_hashedFilesRegistry, _portal));
		frontendResourceRequestHandlers.add(
			new JavaScriptFrontendResourceRequestHandler(
				_configurationProvider, _hashedFilesRegistry, _language,
				_portal, PortletConfigFactoryUtil.getPortletConfigFactory()));
		frontendResourceRequestHandlers.add(
			new LanguageFrontendResourceRequestHandler(
				_configurationProvider, _hashedFilesRegistry, _jsonFactory,
				_language, _portal));
		frontendResourceRequestHandlers.add(
			new StyleSheetFrontendResourceRequestHandler(
				_configurationProvider, _hashedFilesRegistry, _portal,
				_themeLocalService));

		_frontendResourceRequestHandlers.set(frontendResourceRequestHandlers);
	}

	@Deactivate
	protected void deactivate() {
		_frontendResourceRequestHandlers.set(Collections.emptyList());
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		List<FrontendResourceRequestHandler> frontendResourceRequestHandlers =
			_frontendResourceRequestHandlers.get();

		for (FrontendResourceRequestHandler frontendResourceRequestHandler :
				frontendResourceRequestHandlers) {

			if (frontendResourceRequestHandler.canHandleRequest(
					httpServletRequest)) {

				FrontendResource frontendResource =
					frontendResourceRequestHandler.handleRequest(
						httpServletRequest);

				send(frontendResource, httpServletRequest, httpServletResponse);

				return;
			}
		}

		super.processFilter(
			httpServletRequest, httpServletResponse, filterChain);
	}

	protected void send(
			FrontendResource frontendResource,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (frontendResource == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		String eTag = frontendResource.getETag();

		if (eTag != null) {
			if (StringUtil.equals(
					httpServletRequest.getHeader(HttpHeaders.IF_NONE_MATCH),
					eTag)) {

				httpServletResponse.setStatus(
					HttpServletResponse.SC_NOT_MODIFIED);

				return;
			}

			httpServletResponse.setHeader(HttpHeaders.ETAG, eTag);
		}

		if (frontendResource.isImmutable()) {
			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL,
				"immutable, max-age=31536000, public");
		}
		else {
			StringBuilder sb = new StringBuilder();

			sb.append("max-age=");
			sb.append(frontendResource.getMaxAge());

			if (frontendResource.isSendNoCache()) {
				sb.append(", no-cache");
			}
			else {
				sb.append(", must-revalidate");
			}

			if (frontendResource.isPrivate()) {
				sb.append(", private");
			}
			else {
				sb.append(", public");
			}

			httpServletResponse.setHeader(
				HttpHeaders.CACHE_CONTROL, sb.toString());
		}

		httpServletResponse.setCharacterEncoding(StringPool.UTF8);
		httpServletResponse.setContentType(frontendResource.getContentType());

		GZIPOutputStream gzipOutputStream = null;

		if (_gZipEnabled) {
			httpServletResponse.addHeader("Vary", HttpHeaders.ACCEPT_ENCODING);

			if (BrowserSnifferUtil.acceptsGzip(httpServletRequest)) {
				httpServletResponse.setHeader(
					HttpHeaders.CONTENT_ENCODING, "gzip");

				gzipOutputStream = _createGZipOutputStream(
					httpServletResponse.getOutputStream());
			}
		}

		try (InputStream inputStream = frontendResource.getInputStream()) {
			if (gzipOutputStream == null) {
				StreamUtil.transfer(
					inputStream, httpServletResponse.getOutputStream(), false);
			}
			else {
				StreamUtil.transfer(inputStream, gzipOutputStream, false);

				gzipOutputStream.finish();
			}
		}
	}

	private GZIPOutputStream _createGZipOutputStream(OutputStream outputStream)
		throws Exception {

		return new GZIPOutputStream(outputStream) {
			{
				def.setLevel(_gZipCompressionLevel);
			}
		};
	}

	private static final String _GZIP_FILTER =
		"com.liferay.portal.servlet.filters.gzip.GZipFilter";

	@Reference
	private ConfigurationProvider _configurationProvider;

	private final AtomicReference<List<FrontendResourceRequestHandler>>
		_frontendResourceRequestHandlers = new AtomicReference<>(
			Collections.emptyList());
	private int _gZipCompressionLevel;
	private boolean _gZipEnabled;

	@Reference
	private HashedFilesRegistry _hashedFilesRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ThemeLocalService _themeLocalService;

}