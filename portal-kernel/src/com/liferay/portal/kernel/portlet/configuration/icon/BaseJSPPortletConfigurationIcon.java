/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet.configuration.icon;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Eduardo García
 */
public abstract class BaseJSPPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	public abstract String getJspPath();

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String jspPath = getJspPath();

		if (Validator.isNull(jspPath)) {
			return false;
		}

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(jspPath);

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			_log.error("Unable to include JSP " + jspPath, servletException);

			return false;
		}

		return true;
	}

	protected abstract ServletContext getServletContext();

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJSPPortletConfigurationIcon.class);

}