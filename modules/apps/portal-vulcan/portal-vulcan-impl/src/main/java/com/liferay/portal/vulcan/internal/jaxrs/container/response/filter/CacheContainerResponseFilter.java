/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.internal.configuration.admin.service.HeadlessAPICacheManagedServiceFactory;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
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

		Response.StatusType statusType =
			containerResponseContext.getStatusInfo();

		String cacheControl = null;

		if (Objects.equals(
				containerRequestContext.getMethod(), HttpMethod.GET) &&
			(statusType.getFamily() == Response.Status.Family.SUCCESSFUL) &&
			(_company != null) && (_user != null) && _user.isGuestUser()) {

			UriInfo uriInfo = containerRequestContext.getUriInfo();

			URI baseURI = uriInfo.getBaseUri();

			String basePath = StringUtil.removeFirst(
				baseURI.getPath(), Portal.PATH_MODULE);

			if (!basePath.endsWith("/")) {
				basePath += "/";
			}

			cacheControl =
				_headlessAPICacheManagedServiceFactory.getCacheControl(
					_company.getCompanyId(), basePath + uriInfo.getPath());
		}

		if (cacheControl == null) {
			headers.putSingle("Cache-Control", "no-cache, no-store");

			return;
		}

		headers.putSingle("Cache-Control", cacheControl);
	}

	@Context
	private Company _company;

	private final HeadlessAPICacheManagedServiceFactory
		_headlessAPICacheManagedServiceFactory;

	@Context
	private User _user;

}