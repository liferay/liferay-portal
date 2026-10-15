/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.request.filter;

import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Faro.Rest)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Faro.Rest.Origin.Container.Request.Filter"
	},
	scope = ServiceScope.PROTOTYPE, service = ContainerRequestFilter.class
)
public class OriginContainerRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		if (GetterUtil.getBoolean(
				_httpServletRequest.getAttribute(
					"com.liferay.portal.vulcan.internal.http." +
						"VulcanRequestForwarderHttpServletRequestWrapper"))) {

			FaroThreadLocal.setOrigin("mcp");
		}
		else {
			FaroThreadLocal.setOrigin("rest-client");
		}
	}

	@Context
	private HttpServletRequest _httpServletRequest;

}