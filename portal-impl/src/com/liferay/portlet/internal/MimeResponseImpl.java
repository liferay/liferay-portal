/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.CacheControl;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.util.Locale;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public abstract class MimeResponseImpl
	extends PortletResponseImpl implements MimeResponse {

	@Override
	public void flushBuffer() throws IOException {
		httpServletResponse.flushBuffer();

		_calledFlushBuffer = true;
	}

	@Override
	public int getBufferSize() {
		return httpServletResponse.getBufferSize();
	}

	@Override
	public CacheControl getCacheControl() {
		Portlet portlet = getPortlet();

		int expirationTime = 0;

		Integer expCache = portlet.getExpCache();

		if (expCache != null) {
			expirationTime = expCache;
		}

		return new CacheControlImpl(null, expirationTime, false, false, this);
	}

	@Override
	public String getCharacterEncoding() {
		return httpServletResponse.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return _contentType;
	}

	@Override
	public Locale getLocale() {
		return portletRequestImpl.getLocale();
	}

	@Override
	public OutputStream getPortletOutputStream()
		throws IllegalStateException, IOException {

		if (_calledGetWriter) {
			throw new IllegalStateException(
				"Unable to obtain OutputStream because Writer is already in " +
					"use");
		}

		if (_contentType == null) {
			setContentType(portletRequestImpl.getResponseContentType());
		}

		_calledGetPortletOutputStream = true;

		return httpServletResponse.getOutputStream();
	}

	@Override
	public PrintWriter getWriter() throws IllegalStateException, IOException {
		if (_calledGetPortletOutputStream) {
			throw new IllegalStateException(
				"Unable to obtain Writer because OutputStream is already in " +
					"use");
		}

		if (_contentType == null) {
			setContentType(portletRequestImpl.getResponseContentType());
		}

		_calledGetWriter = true;

		return httpServletResponse.getWriter();
	}

	public boolean isCalledFlushBuffer() {
		return _calledFlushBuffer;
	}

	public boolean isCalledGetPortletOutputStream() {
		return _calledGetPortletOutputStream;
	}

	public boolean isCalledGetWriter() {
		return _calledGetWriter;
	}

	@Override
	public boolean isCommitted() {
		return httpServletResponse.isCommitted();
	}

	@Override
	public void reset() {
		if (_calledFlushBuffer) {
			throw new IllegalStateException(
				"Unable to reset a buffer that has been flushed");
		}

		httpServletResponse.reset();

		clearHeaders();
	}

	@Override
	public void resetBuffer() {
		if (_calledFlushBuffer) {
			throw new IllegalStateException(
				"Unable to reset a buffer that has been flushed");
		}

		httpServletResponse.resetBuffer();
	}

	@Override
	public void setBufferSize(int bufferSize) {
		httpServletResponse.setBufferSize(bufferSize);
	}

	@Override
	public void setContentType(String contentType) {
		if (_calledGetPortletOutputStream || _calledGetWriter) {
			return;
		}

		if (Validator.isNull(contentType)) {
			throw new IllegalArgumentException("Content type is null");
		}

		String lifecycle = getLifecycle();
		WindowState windowState = portletRequestImpl.getWindowState();

		if (!contentType.startsWith(
				portletRequestImpl.getResponseContentType()) &&
			!lifecycle.equals(PortletRequest.RESOURCE_PHASE) &&
			!windowState.equals(LiferayWindowState.EXCLUSIVE)) {

			throw new IllegalArgumentException(
				contentType + " is an unsupported content type");
		}

		_contentType = contentType;

		httpServletResponse.setContentType(contentType);
	}

	private boolean _calledFlushBuffer;
	private boolean _calledGetPortletOutputStream;
	private boolean _calledGetWriter;
	private String _contentType;

}