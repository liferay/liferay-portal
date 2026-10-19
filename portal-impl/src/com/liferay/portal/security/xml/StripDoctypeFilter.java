/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.xml;

import com.liferay.portal.kernel.util.ArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author Tomas Polesovsky
 */
public class StripDoctypeFilter {

	public StripDoctypeFilter(InputStream inputStream) {
		this(inputStream, null);
	}

	public StripDoctypeFilter(Reader reader) {
		this(null, reader);
	}

	public int read() throws IOException {
		if (_bufferLength > 0) {
			return readFromBuffer();
		}

		int c = readFromSource();

		if (_documentStarted) {
			return c;
		}

		if (c == '<') {
			int[] buffer = new int[2];

			buffer[0] = readFromSource();
			buffer[1] = readFromSource();

			if ((buffer[0] == '?') ||
				((buffer[0] == '!') && (buffer[1] == '-'))) {

				setBuffer(buffer);

				return c;
			}

			if ((buffer[0] == '!') && (buffer[1] == 'D')) {
				while (true) {
					int doctypeContent = readFromSource();

					if (doctypeContent == -1) {
						return -1;
					}

					if (doctypeContent == '[') {
						_entityDeclaration = true;
					}
					else if (doctypeContent == ']') {
						_entityDeclaration = false;
					}
					else if (doctypeContent == '>') {
						if (!_entityDeclaration) {
							_documentStarted = true;

							return readFromSource();
						}
					}
				}
			}

			setBuffer(buffer);

			_documentStarted = true;
		}

		return c;
	}

	public int read(byte[] bytes, int offset, int length) throws IOException {
		if (_documentStarted && (length > _bufferLength)) {
			int bufferLength = _bufferLength;

			for (int i = 0; i < bufferLength; i++) {
				bytes[offset++] = (byte)(readFromBuffer() & 0xFF);
			}

			return _inputStream.read(bytes, offset, length - bufferLength) +
				bufferLength;
		}

		int read = 0;

		for (read = 0; read < length; read++) {
			int c = read();

			if (c == -1) {
				if (read == 0) {
					return -1;
				}

				return read;
			}

			bytes[offset + read] = (byte)(c & 0xFF);
		}

		return read;
	}

	public int read(char[] chars, int offset, int length) throws IOException {
		if (_documentStarted && (length > _bufferLength)) {
			int bufferLength = _bufferLength;

			for (int i = 0; i < bufferLength; i++) {
				chars[offset++] = (char)readFromBuffer();
			}

			return _reader.read(chars, offset, length - bufferLength) +
				bufferLength;
		}

		int read = 0;

		for (read = 0; read < length; read++) {
			int c = read();

			if (c == -1) {
				if (read == 0) {
					return -1;
				}

				return read;
			}

			chars[offset + read] = (char)c;
		}

		return read;
	}

	protected StripDoctypeFilter(InputStream inputStream, Reader reader) {
		_inputStream = inputStream;
		_reader = reader;
	}

	protected int readFromBuffer() {
		_bufferLength--;

		return _buffer[_bufferLength];
	}

	protected int readFromSource() throws IOException {
		if (_inputStream != null) {
			return _inputStream.read();
		}

		if (_reader != null) {
			return _reader.read();
		}

		throw new IllegalStateException("No underlying source available");
	}

	protected void setBuffer(int[] buffer) {
		_buffer = buffer;

		ArrayUtil.reverse(_buffer);

		_bufferLength = _buffer.length;
	}

	private int[] _buffer;
	private int _bufferLength;
	private boolean _documentStarted;
	private boolean _entityDeclaration;
	private final InputStream _inputStream;
	private final Reader _reader;

}