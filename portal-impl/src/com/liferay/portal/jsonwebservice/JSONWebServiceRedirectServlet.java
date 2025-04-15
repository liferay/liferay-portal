/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.jsonwebservice;

import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Rafael Praxedes
 */
public class JSONWebServiceRedirectServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			httpServletRequest.getRequestDispatcher(
				Portal.PATH_MODULE + "/portal/api/jsonws");

		if (requestDispatcher == null) {
			PortalUtil.sendError(
				HttpServletResponse.SC_SERVICE_UNAVAILABLE,
				new ServletException(
					Portal.PATH_MODULE + "/portal/api/jsonws is unavailable"),
				httpServletRequest, httpServletResponse);

			return;
		}

		requestDispatcher.forward(httpServletRequest, httpServletResponse);
	}

}