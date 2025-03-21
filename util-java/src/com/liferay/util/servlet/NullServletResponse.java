/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.servlet;

import com.liferay.petra.io.OutputStreamWriter;
import com.liferay.petra.io.unsync.UnsyncPrintWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.PrintWriter;

/**
 * @author Brian Wing Shun Chan
 */
public class NullServletResponse extends HttpServletResponseWrapper {

	public NullServletResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);

		_printWriter = new UnsyncPrintWriter(
			new OutputStreamWriter(
				_servletOutputStream, getCharacterEncoding(), true));
	}

	@Override
	public ServletOutputStream getOutputStream() {
		return _servletOutputStream;
	}

	@Override
	public PrintWriter getWriter() {
		return _printWriter;
	}

	private final PrintWriter _printWriter;
	private final ServletOutputStream _servletOutputStream =
		new NullServletOutputStream();

}