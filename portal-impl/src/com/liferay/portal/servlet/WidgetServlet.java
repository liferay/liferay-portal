/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 */
public class WidgetServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			String redirect = getRedirect(httpServletRequest);

			if ((redirect == null) || !PortalUtil.isValidResourceId(redirect)) {
				PortalUtil.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					new NoSuchLayoutException(), httpServletRequest,
					httpServletResponse);
			}
			else {
				httpServletRequest.setAttribute(WebKeys.WIDGET, Boolean.TRUE);

				ServletContext servletContext = getServletContext();

				RequestDispatcher requestDispatcher =
					servletContext.getRequestDispatcher(redirect);

				requestDispatcher.forward(
					httpServletRequest, httpServletResponse);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			PortalUtil.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception,
				httpServletRequest, httpServletResponse);
		}
	}

	protected String getRedirect(HttpServletRequest httpServletRequest) {
		String path = GetterUtil.getString(httpServletRequest.getPathInfo());

		if (Validator.isNull(path)) {
			return null;
		}

		String ppid = ParamUtil.getString(httpServletRequest, "p_p_id");

		int pos = path.indexOf(Portal.FRIENDLY_URL_SEPARATOR);

		if (Validator.isNull(ppid) && (pos == -1)) {
			return null;
		}

		return path;
	}

	private static final Log _log = LogFactoryUtil.getLog(WidgetServlet.class);

}