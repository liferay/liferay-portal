/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Shuyang Zhou
 */
public class ServletInputStreamAdapter extends ServletInputStream {

	public ServletInputStreamAdapter(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public int available() throws IOException {
		return inputStream.available();
	}

	@Override
	public void close() throws IOException {
		inputStream.close();
	}

	@Override
	public boolean isFinished() {
		try {
			if (inputStream.available() == 0) {
				return true;
			}

			return false;
		}
		catch (IOException ioException) {
			return true;
		}
	}

	@Override
	public boolean isReady() {
		try {
			if (inputStream.available() > 0) {
				return true;
			}

			return false;
		}
		catch (IOException ioException) {
			return false;
		}
	}

	@Override
	public void mark(int readLimit) {
		inputStream.mark(readLimit);
	}

	@Override
	public boolean markSupported() {
		return inputStream.markSupported();
	}

	@Override
	public int read() throws IOException {
		return inputStream.read();
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		return inputStream.read(bytes);
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {
		return inputStream.read(bytes, offset, length);
	}

	@Override
	public void reset() throws IOException {
		inputStream.reset();
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long skip(long skip) throws IOException {
		return inputStream.skip(skip);
	}

	protected InputStream inputStream;

}