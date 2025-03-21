/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Shuyang Zhou
 */
public class ServletOutputStreamAdapter extends ServletOutputStream {

	public ServletOutputStreamAdapter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}

	@Override
	public void flush() throws IOException {
		outputStream.flush();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		outputStream.write(bytes, 0, bytes.length);
	}

	@Override
	public void write(byte[] bytes, int offset, int length) throws IOException {
		outputStream.write(bytes, offset, length);
	}

	@Override
	public void write(int b) throws IOException {
		outputStream.write(b);
	}

	protected OutputStream outputStream;

}