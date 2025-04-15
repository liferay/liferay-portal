/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet;

import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.portal.kernel.io.OutputStreamWriter;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.servlet.ServletOutputStreamAdapter;
import com.liferay.util.servlet.NullServletOutputStream;

import jakarta.portlet.ActionResponse;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletServletResponse extends HttpServletResponseWrapper {

	public PortletServletResponse(
		HttpServletResponse httpServletResponse,
		PortletResponse portletResponse, boolean include) {

		super(httpServletResponse);

		_portletResponse = portletResponse;
		_include = include;

		LiferayPortletResponse liferayPortletResponse =
			LiferayPortletUtil.getLiferayPortletResponse(portletResponse);

		_lifecycle = liferayPortletResponse.getLifecycle();
	}

	@Override
	public void addCookie(Cookie cookie) {
		if (!_include) {
			_portletResponse.addProperty(cookie);
		}
	}

	@Override
	public void addDateHeader(String name, long value) {
		addHeader(name, String.valueOf(value));
	}

	@Override
	public void addHeader(String name, String value) {
		if (!_include &&
			(_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			 _lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			 _lifecycle.equals(PortletRequest.RESOURCE_PHASE))) {

			MimeResponse mimeResponse = _getMimeResponse();

			mimeResponse.setProperty(name, value);
		}
	}

	@Override
	public void addIntHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}

	@Override
	public boolean containsHeader(String name) {
		return false;
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public String encodeRedirectUrl(String url) {
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		return null;
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public String encodeUrl(String url) {
		return _portletResponse.encodeURL(url);
	}

	@Override
	public String encodeURL(String url) {
		return _portletResponse.encodeURL(url);
	}

	@Override
	public void flushBuffer() throws IOException {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			mimeResponse.flushBuffer();
		}
	}

	@Override
	public int getBufferSize() {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			return mimeResponse.getBufferSize();
		}

		return 0;
	}

	@Override
	public String getCharacterEncoding() {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			return mimeResponse.getCharacterEncoding();
		}

		return null;
	}

	@Override
	public String getContentType() {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			return mimeResponse.getContentType();
		}

		return null;
	}

	@Override
	public Locale getLocale() {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			return mimeResponse.getLocale();
		}

		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			return new ServletOutputStreamAdapter(
				mimeResponse.getPortletOutputStream());
		}

		return new NullServletOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			return mimeResponse.getWriter();
		}

		return new UnsyncPrintWriter(
			new OutputStreamWriter(
				new NullServletOutputStream(), getCharacterEncoding(), true));
	}

	@Override
	public boolean isCommitted() {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			return mimeResponse.isCommitted();
		}
		else if (!_include) {
			return false;
		}

		return true;
	}

	@Override
	public void reset() {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			mimeResponse.reset();
		}
	}

	@Override
	public void resetBuffer() {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			mimeResponse.resetBuffer();
		}
	}

	@Override
	public void sendError(int status) {
	}

	@Override
	public void sendError(int status, String message) {
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		if (!_include && _lifecycle.equals(PortletRequest.ACTION_PHASE)) {
			ActionResponse actionResponse = _getActionResponse();

			actionResponse.sendRedirect(location);
		}
	}

	@Override
	public void setBufferSize(int bufferSize) {
		if (_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			_lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			_lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {

			MimeResponse mimeResponse = _getMimeResponse();

			mimeResponse.setBufferSize(bufferSize);
		}
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		if (!_include && _lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			ResourceResponse resourceResponse = _getResourceResponse();

			resourceResponse.setCharacterEncoding(characterEncoding);
		}
	}

	@Override
	public void setContentLength(int contentLength) {
		if (!_include && _lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			ResourceResponse resourceResponse = _getResourceResponse();

			resourceResponse.setContentLength(contentLength);
		}
	}

	@Override
	public void setContentLengthLong(long contentLengthLong) {
		setContentLength(Math.toIntExact(contentLengthLong));
	}

	@Override
	public void setContentType(String contentType) {
		if (!_include &&
			(_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			 _lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			 _lifecycle.equals(PortletRequest.RESOURCE_PHASE))) {

			MimeResponse mimeResponse = _getMimeResponse();

			mimeResponse.setContentType(contentType);
		}
	}

	@Override
	public void setDateHeader(String name, long date) {
		setHeader(name, String.valueOf(date));
	}

	@Override
	public void setHeader(String name, String value) {
		if (!_include &&
			(_lifecycle.equals(PortletRequest.HEADER_PHASE) ||
			 _lifecycle.equals(PortletRequest.RENDER_PHASE) ||
			 _lifecycle.equals(PortletRequest.RESOURCE_PHASE))) {

			MimeResponse mimeResponse = _getMimeResponse();

			mimeResponse.setProperty(name, value);
		}
	}

	@Override
	public void setIntHeader(String name, int value) {
		setHeader(name, String.valueOf(value));
	}

	@Override
	public void setLocale(Locale locale) {
		if (!_include && _lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			ResourceResponse resourceResponse = _getResourceResponse();

			resourceResponse.setLocale(locale);
		}
	}

	@Override
	public void setStatus(int status) {
		if (!_include && _lifecycle.equals(PortletRequest.RESOURCE_PHASE)) {
			ResourceResponse resourceResponse = _getResourceResponse();

			resourceResponse.setProperty(
				ResourceResponse.HTTP_STATUS_CODE, String.valueOf(status));
		}
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x)
	 */
	@Deprecated
	public void setStatus(int status, String message) {
		setStatus(status);
	}

	private ActionResponse _getActionResponse() {
		return (ActionResponse)_portletResponse;
	}

	private MimeResponse _getMimeResponse() {
		return (MimeResponse)_portletResponse;
	}

	private ResourceResponse _getResourceResponse() {
		return (ResourceResponse)_portletResponse;
	}

	private final boolean _include;
	private final String _lifecycle;
	private final PortletResponse _portletResponse;

}