/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.internal.configuration.HeadlessApiCacheConfiguration;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

import java.net.URI;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 */
@Provider
public class CacheContainerResponseFilter implements ContainerResponseFilter {

	public CacheContainerResponseFilter(
		ConfigurationProvider configurationProvider) {

		_configurationProvider = configurationProvider;
	}

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		MultivaluedMap<String, Object> headers =
			containerResponseContext.getHeaders();

		String cacheControl = null;

		try {
			HeadlessApiCacheConfiguration headlessApiCacheConfiguration =
				_configurationProvider.getCompanyConfiguration(
					HeadlessApiCacheConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			if (headlessApiCacheConfiguration.enabled()) {
				UriInfo uriInfo = containerRequestContext.getUriInfo();

				URI baseURI = uriInfo.getBaseUri();

				String basePath = baseURI.getPath();

				if (!basePath.endsWith("/")) {
					basePath += "/";
				}

				String[] pathParts = StringUtil.split(
					basePath + uriInfo.getPath(), CharPool.SLASH);

				for (String cacheableEndpoint :
						headlessApiCacheConfiguration.cacheableEndpoints()) {

					String[] parts = StringUtil.split(
						cacheableEndpoint, CharPool.SEMICOLON);

					if (parts.length != 2) {
						continue;
					}

					String[] patternParts = StringUtil.split(
						parts[0], CharPool.SLASH);

					if (patternParts.length != pathParts.length) {
						continue;
					}

					boolean matches = true;

					for (int i = 0; i < pathParts.length; i++) {
						if (!Objects.equals(patternParts[i], "*") &&
							!Objects.equals(pathParts[i], patternParts[i])) {

							matches = false;

							break;
						}
					}

					if (matches) {
						cacheControl = parts[1];

						break;
					}
				}
			}
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		if (cacheControl == null) {
			headers.putSingle("Cache-Control", "no-cache, no-store");

			return;
		}

		headers.putSingle("Cache-Control", cacheControl);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CacheContainerResponseFilter.class);

	private final ConfigurationProvider _configurationProvider;

}