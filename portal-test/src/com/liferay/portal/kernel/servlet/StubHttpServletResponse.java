/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.Locale;

/**
 * @author Shuyang Zhou
 */
public class StubHttpServletResponse implements HttpServletResponse {

	@Override
	public void addCookie(Cookie cookie) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addDateHeader(String name, long value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsHeader(String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public String encodeRedirectUrl(String url) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public String encodeUrl(String string) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String encodeURL(String string) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flushBuffer() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBufferSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCharacterEncoding() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContentType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getHeader(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getHeaderNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> getHeaders(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCommitted() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetBuffer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendError(int status) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendError(int status, String message) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBufferSize(int bufferSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentLength(int contentLength) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentLengthLong(long contentLengthLong) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentType(String contentType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDateHeader(String name, long value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLocale(Locale locale) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatus(int status) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public void setStatus(int status, String message) {
		throw new UnsupportedOperationException();
	}

}