/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration;
import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.internal.resource.HashedFileFrontendResource;
import com.liferay.frontend.js.web.internal.util.FrontendJSWebUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesUtil;
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
		ConfigurationProvider configurationProvider, String contentType,
		String fileExtension, HashedFilesRegistry hashedFilesRegistry,
		Portal portal) {

		_configurationProvider = configurationProvider;
		_contentType = contentType;
		_fileExtension = fileExtension;
		_hashedFilesRegistry = hashedFilesRegistry;
		_portal = portal;
	}

	@Override
	public boolean canHandleRequest(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		if (requestURI.endsWith(_fileExtension) &&
			(_hashedFilesRegistry.getResource(requestURI) != null)) {

			return true;
		}

		return false;
	}

	@Override
	public FrontendResource handleRequest(HttpServletRequest httpServletRequest)
		throws IOException, ServletException {

		String requestURI = httpServletRequest.getRequestURI();

		URL url = _hashedFilesRegistry.getResource(requestURI);

		if (url == null) {
			return null;
		}

		boolean immutable = false;

		if (HashedFilesUtil.getHash(requestURI) != null) {
			immutable = true;
		}

		long maxAge = 31536000;
		boolean sendNoCache = false;

		if (!immutable) {
			FrontendCachingConfiguration frontendCachingConfiguration =
				FrontendJSWebUtil.getFrontendCachingConfiguration(
					_portal.getCompanyId(httpServletRequest),
					_configurationProvider);

			maxAge = frontendCachingConfiguration.jsFilesMaxAge();
			sendNoCache = frontendCachingConfiguration.sendNoCacheForJSFiles();
		}

		return new HashedFileFrontendResource(
			_contentType, HashedFilesUtil.getHash(url.getFile()), immutable,
			maxAge, sendNoCache, url);
	}

	private final ConfigurationProvider _configurationProvider;
	private final String _contentType;
	private final String _fileExtension;
	private final HashedFilesRegistry _hashedFilesRegistry;
	private final Portal _portal;

}