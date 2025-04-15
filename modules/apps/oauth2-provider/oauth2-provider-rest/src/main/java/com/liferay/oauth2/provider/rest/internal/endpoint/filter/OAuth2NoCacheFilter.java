/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.OAuth2.Application)",
		"osgi.jaxrs.extension=true", "osgi.jaxrs.name=OAuth2NoCacheFilter"
	},
	service = ContainerResponseFilter.class
)
@Provider
public class OAuth2NoCacheFilter implements ContainerResponseFilter {

	@Override
	public void filter(
		ContainerRequestContext containerRequestContext,
		ContainerResponseContext containerResponseContext) {

		MultivaluedMap<String, Object> headers =
			containerResponseContext.getHeaders();

		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Expires", "0");
		headers.add("Pragma", "no-cache");
	}

}