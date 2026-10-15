/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.response.filter;

import com.liferay.osb.faro.util.FaroThreadLocal;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Faro.Rest)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Faro.Rest.Origin.Container.Response.Filter"
	},
	scope = ServiceScope.PROTOTYPE, service = ContainerResponseFilter.class
)
public class OriginContainerResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(
		ContainerRequestContext containerRequestContext,
		ContainerResponseContext containerResponseContext) {

		FaroThreadLocal.setOrigin(null);
	}

}