/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.internal.resource.HashedFileFrontendResource;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.net.URL;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFileFrontendResourceRequestHandler
	implements FrontendResourceRequestHandler {

	public HashedFileFrontendResourceRequestHandler(
		String contentType, String fileExtension,
		HashedFilesRegistry hashedFilesRegistry, long maxAge, String maxAgeKey,
		Portal portal, boolean sendNoCache, String sendNoCacheKey) {

		_contentType = contentType;
		_fileExtension = fileExtension;
		_hashedFilesRegistry = hashedFilesRegistry;
		_maxAge = maxAge;
		_maxAgeKey = maxAgeKey;
		_portal = portal;
		_sendNoCache = sendNoCache;
		_sendNoCacheKey = sendNoCacheKey;
	}

	@Override
	public boolean canHandleRequest(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		if (!requestURI.endsWith(_fileExtension)) {
			return false;
		}

		if (HashedFilesUtil.containsHash(requestURI)) {
			return true;
		}

		// LPD-52709

		if (true) {
			return false;
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

		if (requestHash != null) {
			return _createFrontendResource(
				_portal.getCompanyId(httpServletRequest), requestHash, true,
				requestURI);
		}

		String hashedFileURI = _hashedFilesRegistry.getHashedFileURI(
			requestURI);

		if (hashedFileURI == null) {
			return _createFrontendResource(
				_portal.getCompanyId(httpServletRequest), null, false,
				requestURI);
		}

		return _createFrontendResource(
			_portal.getCompanyId(httpServletRequest),
			HashedFilesUtil.getHash(hashedFileURI), false, hashedFileURI);
	}

	private FrontendResource _createFrontendResource(
		long companyId, String eTag, boolean immutable, String resourceURI) {

		long maxAge = 31536000;
		boolean sendNoCache = false;

		if (!immutable) {
			maxAge = _maxAge;
			sendNoCache = _sendNoCache;

			try {
				Settings settings = FallbackKeysSettingsUtil.getSettings(
					new CompanyServiceSettingsLocator(
						companyId,
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
		}

		URL resourceURL = _hashedFilesRegistry.getResource(resourceURI);

		if (resourceURL == null) {
			return null;
		}

		return new HashedFileFrontendResource(
			_contentType, eTag, immutable, maxAge, sendNoCache, resourceURL);
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

}