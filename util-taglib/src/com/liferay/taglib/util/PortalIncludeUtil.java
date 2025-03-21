/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.portal.kernel.servlet.DirectRequestDispatcherFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.servlet.PipingServletResponseFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class PortalIncludeUtil {

	public static void include(
			PageContext pageContext, HTMLRenderer htmlRenderer)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		htmlRenderer.renderHTML(
			httpServletRequest,
			PipingServletResponseFactory.createPipingServletResponse(
				pageContext));
	}

	public static void include(PageContext pageContext, String path)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		ServletContext servletContext =
			(ServletContext)httpServletRequest.getAttribute(WebKeys.CTX);

		RequestDispatcher requestDispatcher =
			DirectRequestDispatcherFactoryUtil.getRequestDispatcher(
				servletContext, path);

		requestDispatcher.include(
			httpServletRequest,
			PipingServletResponseFactory.createPipingServletResponse(
				pageContext));
	}

	public interface HTMLRenderer {

		public void renderHTML(
				HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse)
			throws IOException, ServletException;

	}

}