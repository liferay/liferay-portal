/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.container.request.filter;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * @author Ivica Cardic
 */
@Provider
public class LogContainerRequestFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext)
		throws IOException {

		if (_log.isDebugEnabled()) {
			StringBundler sb = new StringBundler(7);

			sb.append("{headers: ");
			sb.append(MapUtil.toString(containerRequestContext.getHeaders()));
			sb.append(", method: ");
			sb.append(containerRequestContext.getMethod());
			sb.append(", uri: ");

			UriInfo uriInfo = containerRequestContext.getUriInfo();

			sb.append(uriInfo.getRequestUri());

			sb.append("}");

			_log.debug(sb.toString());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LogContainerRequestFilter.class);

}