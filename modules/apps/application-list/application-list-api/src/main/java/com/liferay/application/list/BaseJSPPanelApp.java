/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list;

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
 * Provides a skeletal implementation of the {@link PanelApp} with JSP support
 * to minimize the effort required to implement this interface.
 *
 * <p>
 * To implement a JSP application, this class should be extended and {@link
 * #getJspPath()} should be implemented, which returns a path for the main JSP
 * application view in the current servlet context.
 * </p>
 *
 * <p>
 * JSP applications are included within JSP application categories defined by
 * {@link BaseJSPPanelCategory} implementations.
 * </p>
 *
 * @author Julio Camarero
 * @see    BasePanelApp
 * @see    PanelApp
 */
public abstract class BaseJSPPanelApp extends BasePanelApp {

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
			_log.error("Unable to include " + jspPath, servletException);

			return false;
		}

		return true;
	}

	protected abstract ServletContext getServletContext();

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJSPPanelApp.class);

}