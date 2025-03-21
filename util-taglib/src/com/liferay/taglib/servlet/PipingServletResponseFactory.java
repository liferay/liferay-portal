/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.servlet;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.io.WriterOutputStream;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletOutputStreamAdapter;
import com.liferay.portal.kernel.util.ServerDetector;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;

import java.io.IOException;

/**
 * @author Shuyang Zhou
 */
public class PipingServletResponseFactory {

	public static HttpServletResponse createPipingServletResponse(
		PageContext pageContext) {

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)pageContext.getResponse();

		JspWriter jspWriter = pageContext.getOut();

		if (ServerDetector.isWebLogic()) {

			// This optimization cannot be applied to WebLogic because WebLogic
			// relies on the WriterOutputStream bridging logic insde
			// getOutputStream().

			// WebLogic's weblogic.servlet.internal.DelegateChunkWriter#
			// getWriter() always builds its writer on top of
			// HttpServletResponse#getOutputStream() rather than relying on
			// the HttpServletResponse#getWriter().

			// In order to avoid the potential heavy
			// BufferCacheServletResponse#getBufferSize() call, we
			// preadapt JspWriter to ServletOutputStream using
			// JspWriter#getBufferSize() rather than the
			// HttpServletResponse#getBufferSize().

			return new PipingServletResponse(
				httpServletResponse,
				new ServletOutputStreamAdapter(
					new WriterOutputStream(
						jspWriter, httpServletResponse.getCharacterEncoding(),
						jspWriter.getBufferSize(), true)));
		}

		if (!(pageContext instanceof PageContextWrapper) ||
			(jspWriter instanceof BodyContent)) {

			// This optimization cannot be applied to a page context with a
			// pushed body

			return new PipingServletResponse(httpServletResponse, jspWriter);
		}

		if (!ServerDetector.isTomcat()) {
			try {
				jspWriter.flush();
			}
			catch (IOException ioException) {
				ReflectionUtil.throwException(ioException);
			}
		}

		return httpServletResponse;
	}

}