/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration;
import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.internal.resource.StyleSheetFrontendResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.TreeMapBuilder;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.net.URL;

import java.util.SortedMap;

/**
 * @author Iván Zaera Avellón
 */
public class StyleSheetFrontendResourceRequestHandler
	implements FrontendResourceRequestHandler {

	public StyleSheetFrontendResourceRequestHandler(
		FrontendCachingConfiguration frontendCachingConfiguration,
		HashedFilesRegistry hashedFilesRegistry, Portal portal,
		ThemeLocalService themeLocalService) {

		_frontendCachingConfiguration = frontendCachingConfiguration;
		_hashedFilesRegistry = hashedFilesRegistry;
		_portal = portal;
		_themeLocalService = themeLocalService;
	}

	@Override
	public boolean canHandleRequest(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		if (!requestURI.endsWith(".css")) {
			return false;
		}

		if (HashedFilesUtil.containsHash(requestURI)) {
			return true;
		}

		String hashedFileURI = _hashedFilesRegistry.getHashedFileURI(
			requestURI);

		if (hashedFileURI != null) {
			return true;
		}

		URL resourceURL = _hashedFilesRegistry.getResource(requestURI);

		if (resourceURL != null) {
			return true;
		}

		return false;
	}

	@Override
	public FrontendResource handleRequest(HttpServletRequest httpServletRequest)
		throws IOException, ServletException {

		String requestURI = httpServletRequest.getRequestURI();

		String requestHash = HashedFilesUtil.getHash(requestURI);

		SortedMap<String, String> tokens = _getTokens(httpServletRequest);

		if (requestHash != null) {
			return _createFrontendResource(
				requestHash, tokens == null, requestURI, tokens);
		}

		String hashedFileURI = _hashedFilesRegistry.getHashedFileURI(
			requestURI);

		if (hashedFileURI == null) {
			return _createFrontendResource(null, false, requestURI, tokens);
		}

		return _createFrontendResource(
			HashedFilesUtil.getHash(hashedFileURI), false, hashedFileURI,
			tokens);
	}

	private FrontendResource _createFrontendResource(
		String eTag, boolean immutable, String resourceURI,
		SortedMap<String, String> tokens) {

		long maxAge = 31536000;
		boolean sendNoCache = false;

		if (!immutable) {
			maxAge = _frontendCachingConfiguration.cssStyleSheetsMaxAge();
			sendNoCache =
				_frontendCachingConfiguration.sendNoCacheForCSSStyleSheets();
		}

		URL resourceURL = _hashedFilesRegistry.getResource(resourceURI);

		if (resourceURL == null) {
			return null;
		}

		return new StyleSheetFrontendResource(
			eTag, immutable, maxAge, sendNoCache, tokens, resourceURL);
	}

	private SortedMap<String, String> _getTokens(
		HttpServletRequest httpServletRequest) {

		SortedMap<String, String> tokens = null;

		if (Boolean.valueOf(httpServletRequest.getParameter("tokenize"))) {
			String proxyPath = _portal.getPathProxy();

			ServletContext servletContext =
				httpServletRequest.getServletContext();

			String baseURL = proxyPath.concat(servletContext.getContextPath());

			if (baseURL.endsWith(StringPool.SLASH)) {
				baseURL = baseURL.substring(0, baseURL.length() - 1);
			}

			String themeImagePath;

			try {
				Theme theme = _themeLocalService.getTheme(
					_portal.getCompanyId(httpServletRequest),
					httpServletRequest.getParameter("themeId"));

				themeImagePath =
					_portal.getCDNHost(httpServletRequest) +
						theme.getStaticResourcePath() + theme.getImagesPath();
			}
			catch (PortalException portalException) {
				throw new RuntimeException(portalException);
			}

			tokens = TreeMapBuilder.put(
				"base_url", baseURL
			).put(
				"portal_ctx", _portal.getPathContext()
			).put(
				"theme_image_path", themeImagePath
			).build();
		}

		return tokens;
	}

	private final FrontendCachingConfiguration _frontendCachingConfiguration;
	private final HashedFilesRegistry _hashedFilesRegistry;
	private final Portal _portal;
	private final ThemeLocalService _themeLocalService;

}