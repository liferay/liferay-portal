/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.servlet;

import com.liferay.petra.io.OutputStreamWriter;
import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.io.WriterOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletOutputStreamAdapter;
import com.liferay.portal.kernel.util.ServerDetector;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author Shuyang Zhou
 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link com.liferay.portal.kernel.servlet.PipingServletResponse} and {@link PipingServletResponseFactory}
 */
@Deprecated
public class PipingServletResponse extends HttpServletResponseWrapper {

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

	public PipingServletResponse(
		HttpServletResponse httpServletResponse, OutputStream outputStream) {

		super(httpServletResponse);

		if (outputStream == null) {
			throw new NullPointerException("Output stream is null");
		}

		_servletOutputStream = new ServletOutputStreamAdapter(outputStream);
	}

	public PipingServletResponse(
		HttpServletResponse httpServletResponse, PrintWriter printWriter) {

		super(httpServletResponse);

		if (printWriter == null) {
			throw new NullPointerException("Print writer is null");
		}

		_printWriter = printWriter;
	}

	public PipingServletResponse(
		HttpServletResponse httpServletResponse,
		ServletOutputStream servletOutputStream) {

		super(httpServletResponse);

		if (servletOutputStream == null) {
			throw new NullPointerException("Servlet output stream is null");
		}

		_servletOutputStream = servletOutputStream;
	}

	public PipingServletResponse(
		HttpServletResponse httpServletResponse, Writer writer) {

		super(httpServletResponse);

		if (writer == null) {
			throw new NullPointerException("Writer is null");
		}

		_printWriter = new UnsyncPrintWriter(writer);
	}

	@Override
	public ServletOutputStream getOutputStream() {
		if (_servletOutputStream == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Getting an output stream when a writer is available is " +
						"not recommended because it is slow");
			}

			_servletOutputStream = new ServletOutputStreamAdapter(
				new WriterOutputStream(
					_printWriter, getCharacterEncoding(), getBufferSize(),
					true));
		}

		return _servletOutputStream;
	}

	@Override
	public PrintWriter getWriter() {
		if (_printWriter == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Getting a writer when an output stream is available is " +
						"not recommended because it is slow");
			}

			_printWriter = new UnsyncPrintWriter(
				new OutputStreamWriter(
					_servletOutputStream, getCharacterEncoding(), true));
		}

		return _printWriter;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PipingServletResponse.class);

	private PrintWriter _printWriter;
	private ServletOutputStream _servletOutputStream;

}