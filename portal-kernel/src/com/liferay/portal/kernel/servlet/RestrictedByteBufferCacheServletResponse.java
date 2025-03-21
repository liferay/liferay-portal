/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.io.OutputStreamWriter;
import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.portal.kernel.internal.servlet.RestrictedByteArrayCacheOutputStream;
import com.liferay.portal.kernel.internal.servlet.RestrictedByteArrayCacheOutputStream.FlushPreAction;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.nio.ByteBuffer;

/**
 * @author Shuyang Zhou
 */
public class RestrictedByteBufferCacheServletResponse
	extends MetaInfoCacheServletResponse {

	public RestrictedByteBufferCacheServletResponse(
		HttpServletResponse httpServletResponse, int cacheCapacity) {

		super(httpServletResponse);

		_cacheCapacity = cacheCapacity;
	}

	public void flushCache() throws IOException {
		if (_restrictedByteArrayCacheOutputStream != null) {
			_restrictedByteArrayCacheOutputStream.flush();
		}
	}

	@Override
	public int getBufferSize() {
		if (isOverflowed()) {
			return super.getBufferSize();
		}

		if (_restrictedByteArrayCacheOutputStream == null) {
			return _cacheCapacity;
		}

		return _restrictedByteArrayCacheOutputStream.getCacheCapacity();
	}

	public ByteBuffer getByteBuffer() {
		if (_restrictedByteArrayCacheOutputStream == null) {
			return _emptyByteBuffer;
		}

		return _restrictedByteArrayCacheOutputStream.unsafeGetByteBuffer();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (calledGetWriter) {
			throw new IllegalStateException(
				"Unable to obtain OutputStream because Writer is already in " +
					"use");
		}

		if (_servletOutputStream != null) {
			return _servletOutputStream;
		}

		_restrictedByteArrayCacheOutputStream =
			new RestrictedByteArrayCacheOutputStream(
				super.getOutputStream(), _cacheCapacity,
				new FinishResponseFlushPreAction());

		_servletOutputStream = new ServletOutputStreamAdapter(
			_restrictedByteArrayCacheOutputStream);

		calledGetOutputStream = true;

		return _servletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (calledGetOutputStream) {
			throw new IllegalStateException(
				"Unable to obtain Writer because OutputStream is already in " +
					"use");
		}

		if (_printWriter != null) {
			return _printWriter;
		}

		ServletResponse servletResponse = getResponse();

		_restrictedByteArrayCacheOutputStream =
			new RestrictedByteArrayCacheOutputStream(
				servletResponse.getOutputStream(), _cacheCapacity,
				new FinishResponseFlushPreAction());

		_printWriter = new UnsyncPrintWriter(
			new OutputStreamWriter(
				_restrictedByteArrayCacheOutputStream, getCharacterEncoding(),
				true));

		calledGetWriter = true;

		return _printWriter;
	}

	public boolean isOverflowed() {
		if (_restrictedByteArrayCacheOutputStream == null) {
			if (_cacheCapacity >= super.getBufferSize()) {
				return false;
			}

			return true;
		}

		return _restrictedByteArrayCacheOutputStream.isOverflowed();
	}

	@Override
	public void setBufferSize(int bufferSize) {
		if (isCommitted()) {
			throw new IllegalStateException("Set buffer size after commit");
		}

		if (bufferSize > getBufferSize()) {
			super.setBufferSize(bufferSize);

			try {
				flushCache();
			}
			catch (IOException ioException) {
				throw new IllegalStateException(
					"Unable to transfer restricted byte buffer to underneath" +
						"response's buffer",
					ioException);
			}
		}
	}

	@Override
	protected void resetBuffer(boolean nullOutReferences) {
		if (nullOutReferences) {
			calledGetOutputStream = false;
			calledGetWriter = false;

			_printWriter = null;
			_servletOutputStream = null;
			_restrictedByteArrayCacheOutputStream = null;
		}
		else if (_restrictedByteArrayCacheOutputStream != null) {
			_restrictedByteArrayCacheOutputStream.reset();
		}
	}

	private static final ByteBuffer _emptyByteBuffer = ByteBuffer.allocate(0);

	private final int _cacheCapacity;
	private PrintWriter _printWriter;
	private RestrictedByteArrayCacheOutputStream
		_restrictedByteArrayCacheOutputStream;
	private ServletOutputStream _servletOutputStream;

	private class FinishResponseFlushPreAction implements FlushPreAction {

		@Override
		public void beforeFlush() throws IOException {
			flushBuffer();
		}

	}

}