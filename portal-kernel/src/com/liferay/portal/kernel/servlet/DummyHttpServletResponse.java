/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.util.Collection;
import java.util.Locale;

/**
 * @author Cristina González Castellano
 * @author Eduardo Pérez
 */
public class DummyHttpServletResponse implements HttpServletResponse {

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
	public boolean containsHeader(String name) {
		return false;
	}

	public String encodeRedirectUrl(String url) {
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		return null;
	}

	public String encodeUrl(String url) {
		return null;
	}

	@Override
	public String encodeURL(String url) {
		return null;
	}

	@Override
	public void flushBuffer() {
	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public String getHeader(String name) {
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return null;
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() {
		return null;
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public PrintWriter getWriter() {
		return null;
	}

	@Override
	public boolean isCommitted() {
		return false;
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
	public void setDateHeader(String name, long value) {
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

	public void setStatus(int status, String message) {
	}

}