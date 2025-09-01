/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.frontend.resource.handler;

import com.liferay.frontend.js.web.internal.frontend.resource.FrontendResource;
import com.liferay.frontend.js.web.internal.frontend.resource.HashedFileFrontendResource;
import com.liferay.frontend.js.web.internal.hashed.files.HashedFilesRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.List;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFileFrontendResourceRequestHandler
	implements FrontendResourceRequestHandler {

	public HashedFileFrontendResourceRequestHandler(
		String contentType, String fileExtension,
		HashedFilesRegistry hashedFilesRegistry, long maxAge, String maxAgeKey,
		Portal portal, boolean sendNoCache, String sendNoCacheKey,
		ServiceTrackerMap<String, ServletContext> serviceTrackerMap) {

		_contentType = contentType;
		_fileExtension = fileExtension;
		_hashedFilesRegistry = hashedFilesRegistry;
		_maxAge = maxAge;
		_maxAgeKey = maxAgeKey;
		_portal = portal;
		_sendNoCache = sendNoCache;
		_sendNoCacheKey = sendNoCacheKey;
		_serviceTrackerMap = serviceTrackerMap;
	}

	@Override
	public boolean canHandleRequest(HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public FrontendResource handleRequest(HttpServletRequest httpServletRequest)
		throws IOException, ServletException {

		String requestURI = httpServletRequest.getRequestURI();

		String hash = _getHash(requestURI);

		if (hash != null) {
			return _createFrontendResource(
				hash, true, 31536000, requestURI, false);
		}

		long maxAge = _maxAge;
		boolean sendNoCache = _sendNoCache;

		try {
			Settings settings = FallbackKeysSettingsUtil.getSettings(
				new CompanyServiceSettingsLocator(
					_portal.getCompanyId(httpServletRequest),
					"com.liferay.frontend.js.web.internal.configuration." +
						"FrontendCachingConfiguration",
					"com.liferay.frontend.js.web.internal.configuration." +
						"FrontendCachingConfiguration"));

			maxAge = Long.valueOf(
				settings.getValue(_maxAgeKey, String.valueOf(_maxAge)));
			sendNoCache = Boolean.valueOf(
				settings.getValue(
					_sendNoCacheKey, String.valueOf(_sendNoCache)));
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get frontend caching configuration, using " +
						"reasonable defaults instead",
					exception);
			}
		}

		String hashedFileURI = _hashedFilesRegistry.get(requestURI);

		if (hashedFileURI == null) {
			return _createFrontendResource(
				null, false, maxAge, requestURI, sendNoCache);
		}

		return _createFrontendResource(
			_getHash(hashedFileURI), false, maxAge, hashedFileURI, sendNoCache);
	}

	private FrontendResource _createFrontendResource(
			String eTag, boolean immutable, long maxAge, String resourceURI,
			boolean sendNoCache)
		throws MalformedURLException {

		List<String> resourceURIParts = Arrays.asList(
			resourceURI.split(StringPool.SLASH));

		ServletContext servletContext = _serviceTrackerMap.getService(
			StringUtil.merge(resourceURIParts.subList(0, 3), StringPool.SLASH));

		if (servletContext == null) {
			return null;
		}

		String resourcePath = StringUtil.merge(
			resourceURIParts.subList(3, resourceURIParts.size()),
			StringPool.SLASH);

		resourcePath = StringPool.SLASH + resourcePath;

		URL url = servletContext.getResource(resourcePath);

		if (url == null) {
			return null;
		}

		return new HashedFileFrontendResource(
			_contentType, eTag, immutable, maxAge, sendNoCache, url);
	}

	private String _getHash(String uri) {
		int i = uri.lastIndexOf(".(");

		if (i == -1) {
			return null;
		}

		int j = uri.lastIndexOf(").");

		if (j == -1) {
			return null;
		}

		return uri.substring(i + 2, j);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HashedFileFrontendResourceRequestHandler.class);

	private final String _contentType;
	private final String _fileExtension;
	private final HashedFilesRegistry _hashedFilesRegistry;
	private final long _maxAge;
	private final String _maxAgeKey;
	private final Portal _portal;
	private final boolean _sendNoCache;
	private final String _sendNoCacheKey;
	private final ServiceTrackerMap<String, ServletContext> _serviceTrackerMap;

}