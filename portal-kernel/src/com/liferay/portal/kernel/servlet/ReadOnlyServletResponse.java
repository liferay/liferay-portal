/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.util.Locale;

/**
 * @author Shuyang Zhou
 */
public class ReadOnlyServletResponse extends HttpServletResponseWrapper {

	public ReadOnlyServletResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}

	@Override
	public void addCookie(Cookie cookie) {
	}

	@Override
	public void addDateHeader(String name, long value) {
	}

	@Override
	public void addHeader(String name, String value) {
	}

	@Override
	public void addIntHeader(String name, int value) {
	}

	@Override
	public void flushBuffer() {
	}

	@Override
	public void reset() {
	}

	@Override
	public void resetBuffer() {
	}

	@Override
	public void sendError(int status) {
	}

	@Override
	public void sendError(int status, String message) {
	}

	@Override
	public void sendRedirect(String location) {
	}

	@Override
	public void setBufferSize(int bufferSize) {
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
	}

	@Override
	public void setContentLength(int contentLength) {
	}

	@Override
	public void setContentLengthLong(long contentLengthLong) {
	}

	@Override
	public void setContentType(String contentType) {
	}

	@Override
	public void setDateHeader(String name, long date) {
	}

	@Override
	public void setHeader(String name, String value) {
	}

	@Override
	public void setIntHeader(String name, int value) {
	}

	@Override
	public void setLocale(Locale locale) {
	}

	@Override
	public void setStatus(int status) {
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public void setStatus(int status, String message) {
	}

}