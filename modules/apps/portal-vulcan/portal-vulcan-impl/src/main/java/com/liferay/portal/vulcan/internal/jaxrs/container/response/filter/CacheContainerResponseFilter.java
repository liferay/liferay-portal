/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter;

import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.internal.configuration.admin.service.HeadlessAPICacheManagedServiceFactory;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

import java.net.URI;

/**
 * @author Alejandro Tardín
 */
@Provider
public class CacheContainerResponseFilter implements ContainerResponseFilter {

	public CacheContainerResponseFilter(
		HeadlessAPICacheManagedServiceFactory
			headlessAPICacheManagedServiceFactory) {

		_headlessAPICacheManagedServiceFactory =
			headlessAPICacheManagedServiceFactory;
	}

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		MultivaluedMap<String, Object> headers =
			containerResponseContext.getHeaders();

		UriInfo uriInfo = containerRequestContext.getUriInfo();

		URI baseURI = uriInfo.getBaseUri();

		String basePath = StringUtil.removeFirst(
			baseURI.getPath(), Portal.PATH_MODULE);

		if (!basePath.endsWith("/")) {
			basePath += "/";
		}

		String cacheControl =
			_headlessAPICacheManagedServiceFactory.getCacheControl(
				CompanyThreadLocal.getCompanyId(),
				basePath + uriInfo.getPath());

		if (cacheControl == null) {
			headers.putSingle("Cache-Control", "no-cache, no-store");

			return;
		}

		headers.putSingle("Cache-Control", cacheControl);
	}

	private final HeadlessAPICacheManagedServiceFactory
		_headlessAPICacheManagedServiceFactory;

}