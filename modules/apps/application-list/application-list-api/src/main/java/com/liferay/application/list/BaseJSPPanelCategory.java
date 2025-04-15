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
 * Provides a skeletal implementation of the {@link PanelCategory} with JSP
 * support to minimize the effort required to implement this interface.
 *
 * <p>
 * To implement a JSP application category, this class should be extended and
 * {@link #getJspPath()} should be implemented, which returns a path for the
 * main JSP application category view in the current servlet context. {@link
 * #getServletContext()} should be implemented, which returns the appropriate
 * servlet context for JSP pages. If the servlet context is not set, {@link
 * #include(HttpServletRequest, HttpServletResponse)} will throw a
 * <code>NullPointerException</code>.
 * </p>
 *
 * <p>
 * JSP application categories include JSP applications defined by {@link
 * BaseJSPPanelApp} implementations.
 * </p>
 *
 * @author Eudaldo Alonso
 * @see    BasePanelCategory
 * @see    PanelCategory
 */
public abstract class BaseJSPPanelCategory extends BasePanelCategory {

	public String getHeaderJspPath() {
		return null;
	}

	public abstract String getJspPath();

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		return includeJSP(
			httpServletRequest, httpServletResponse, getJspPath());
	}

	@Override
	public boolean includeHeader(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		return includeJSP(
			httpServletRequest, httpServletResponse, getHeaderJspPath());
	}

	protected abstract ServletContext getServletContext();

	protected boolean includeJSP(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String jspPath)
		throws IOException {

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

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJSPPanelCategory.class);

}