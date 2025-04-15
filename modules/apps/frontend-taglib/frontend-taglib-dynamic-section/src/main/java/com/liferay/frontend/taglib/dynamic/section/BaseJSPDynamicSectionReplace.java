/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.dynamic.section;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.PipingServletResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

/**
 * @author Matthew Tambara
 */
public abstract class BaseJSPDynamicSectionReplace
	implements DynamicSectionReplace {

	@Override
	public String replace(PageContext pageContext)
		throws IOException, ServletException {

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(getJspPath());

		requestDispatcher.include(
			pageContext.getRequest(),
			new PipingServletResponse(
				(HttpServletResponse)pageContext.getResponse(),
				pageContext.getOut()));

		return StringPool.BLANK;
	}

	protected abstract String getJspPath();

	protected abstract ServletContext getServletContext();

}