/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.facet;

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
 * @author Eudaldo Alonso
 */
public abstract class BaseJSPSearchFacet extends BaseSearchFacet {

	public abstract String getConfigurationJspPath();

	public abstract String getDisplayJspPath();

	@Override
	public void includeConfiguration(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (Validator.isNull(getConfigurationJspPath())) {
			return;
		}

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(getConfigurationJspPath());

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			_log.error(
				"Unable to include JSP " + getDisplayJspPath(),
				servletException);

			throw new IOException(
				"Unable to include " + getDisplayJspPath(), servletException);
		}
	}

	@Override
	public void includeView(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (Validator.isNull(getDisplayJspPath())) {
			return;
		}

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(getDisplayJspPath());

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			_log.error("Unable to include JSP", servletException);

			throw new IOException(
				"Unable to include " + getDisplayJspPath(), servletException);
		}
	}

	protected abstract ServletContext getServletContext();

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJSPSearchFacet.class);

}