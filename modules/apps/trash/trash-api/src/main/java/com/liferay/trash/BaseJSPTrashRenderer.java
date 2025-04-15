/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Alexander Chow
 */
public abstract class BaseJSPTrashRenderer extends BaseTrashRenderer {

	public abstract String getJspPath(
		HttpServletRequest httpServletRequest, String template);

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		String jspPath = getJspPath(httpServletRequest, template);

		if (Validator.isNull(jspPath)) {
			return false;
		}

		ServletContext servletContext = getServletContext(httpServletRequest);

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(jspPath);

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);

			return true;
		}
		catch (ServletException servletException) {
			_log.error("Unable to include JSP " + jspPath, servletException);

			throw new IOException(
				"Unable to include " + jspPath, servletException);
		}
	}

	public void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	protected ServletContext getServletContext(
		HttpServletRequest httpServletRequest) {

		if (_servletContext != null) {
			return _servletContext;
		}

		return (ServletContext)httpServletRequest.getAttribute(WebKeys.CTX);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJSPTrashRenderer.class);

	private ServletContext _servletContext;

}