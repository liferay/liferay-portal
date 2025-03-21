/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.eclipse.equinox.http.servlet.internal.HttpServletEndpointController;
import org.eclipse.equinox.http.servlet.internal.context.DispatchTargets;
import org.eclipse.equinox.http.servlet.internal.servlet.HttpServletRequestWrapperImpl;

/**
 * @author Dante Wang
 */
public class HttpServletEndpointServlet extends HttpServlet {

	public HttpServletEndpointServlet(
		HttpServletEndpointController httpServletEndpointController,
		ServletConfig servletConfig) {

		_httpServletEndpointController = httpServletEndpointController;
		_servletConfig = servletConfig;
	}

	@Override
	public ServletConfig getServletConfig() {
		return _servletConfig;
	}

	@Override
	public void init(ServletConfig servletConfig) {
	}

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		String dispatchPathInfo =
			HttpServletRequestWrapperImpl.getDispatchPathInfo(
				httpServletRequest);

		if (dispatchPathInfo == null) {
			dispatchPathInfo = StringPool.SLASH;
		}

		DispatchTargets dispatchTargets =
			_httpServletEndpointController.getDispatchTargets(dispatchPathInfo);

		if ((dispatchTargets != null) &&
			dispatchTargets.doDispatch(
				httpServletRequest, httpServletResponse, dispatchPathInfo,
				httpServletRequest.getDispatcherType())) {

			return;
		}

		PortalUtil.sendError(
			HttpServletResponse.SC_NOT_FOUND, new NoSuchLayoutException(),
			httpServletRequest, httpServletResponse);
	}

	private final HttpServletEndpointController _httpServletEndpointController;
	private final ServletConfig _servletConfig;

}