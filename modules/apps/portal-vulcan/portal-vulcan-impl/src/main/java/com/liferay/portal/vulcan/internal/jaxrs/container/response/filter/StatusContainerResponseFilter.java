/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.response.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

/**
 * @author Zoltán Takács
 */
public class StatusContainerResponseFilter implements ContainerResponseFilter {

	public StatusContainerResponseFilter(Response.Status status) {
		_status = status;
	}

	@Override
	public void filter(
			ContainerRequestContext containerRequestContext,
			ContainerResponseContext containerResponseContext)
		throws IOException {

		containerResponseContext.setStatus(_status.getStatusCode());
	}

	private final Response.Status _status;

}