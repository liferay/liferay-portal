/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.vulcan.internal.configuration.HeadlessApiCacheConfiguration;

import jakarta.annotation.Priority;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * PoC for LPD-92464: applies an administrator-configured Cache-Control
 * header to public Headless API responses whose path matches a configured
 * pattern. Registered against the same "Liferay.Vulcan" extension selector
 * as {@link CacheContainerResponseFilter}, so it runs against every Vulcan
 * based headless application, not just Headless Admin Taxonomy. It runs
 * after {@link CacheContainerResponseFilter} (lower {@link Priority} value,
 * and response filters execute in descending priority order), so a matching
 * request's Cache-Control value overrides that filter's default
 * "no-cache, no-store".
 *
 * @author Jan Brychta
 * @review This is a proof of concept, not production code. See the module's
 *         write-up in LPD-92464 for known gaps (single flat company scoped
 *         configuration instead of Documents and Media's full per-company
 *         ManagedServiceFactory pattern, no localized Language keys, no
 *         automated test coverage).
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_SELECT + "=(osgi.jaxrs.extension.select=\\(osgi.jaxrs.name=Liferay.Vulcan\\))",
		JaxrsWhiteboardConstants.JAX_RS_EXTENSION + "=true",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=Liferay.Headless.Api.Cache"
	},
	service = ContainerResponseFilter.class
)
@Priority(Priorities.USER - 20)
@Provider
public class HeadlessApiCacheContainerResponseFilter
	implements ContainerResponseFilter {

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		HeadlessApiCacheConfiguration headlessApiCacheConfiguration =
			_getHeadlessApiCacheConfiguration();

		if ((headlessApiCacheConfiguration == null) ||
			!headlessApiCacheConfiguration.enabled()) {

			return;
		}

		String cacheControlValue = _getCacheControlValue(
			_getPath(containerRequestContext.getUriInfo()),
			headlessApiCacheConfiguration.cacheableEndpoints());

		if (cacheControlValue == null) {
			return;
		}

		MultivaluedMap<String, Object> headers =
			containerResponseContext.getHeaders();

		headers.putSingle("Cache-Control", cacheControlValue);
	}

	private String _getCacheControlValue(
		String path, String[] cacheableEndpoints) {

		for (String cacheableEndpoint : cacheableEndpoints) {
			String[] parts = cacheableEndpoint.split(";", 2);

			if (parts.length != 2) {
				continue;
			}

			if (_matches(path, parts[0])) {
				return parts[1];
			}
		}

		return null;
	}

	private HeadlessApiCacheConfiguration _getHeadlessApiCacheConfiguration() {
		try {
			return _configurationProvider.getCompanyConfiguration(
				HeadlessApiCacheConfiguration.class,
				CompanyThreadLocal.getCompanyId());
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);

			return null;
		}
	}

	private String _getPath(UriInfo uriInfo) {
		String basePath = uriInfo.getBaseUri(
		).getPath();

		if (basePath.endsWith("/")) {
			return basePath + uriInfo.getPath();
		}

		return basePath + "/" + uriInfo.getPath();
	}

	private boolean _matches(String path, String pattern) {
		String[] pathParts = path.split("/");
		String[] patternParts = pattern.split("/");

		if (pathParts.length != patternParts.length) {
			return false;
		}

		for (int i = 0; i < pathParts.length; i++) {
			if (!Objects.equals(patternParts[i], "*") &&
				!Objects.equals(pathParts[i], patternParts[i])) {

				return false;
			}
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HeadlessApiCacheContainerResponseFilter.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

}