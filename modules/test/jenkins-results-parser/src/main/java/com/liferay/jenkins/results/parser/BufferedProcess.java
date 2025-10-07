/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Peter Yoo
 */
public class BufferedProcess extends Process {

	public BufferedProcess(Process process) {
		_process = process;

		_standardErrorInputStreamBuffer = new InputStreamBuffer(
			process.getErrorStream());

		_standardErrorInputStreamBuffer.start();

		_standardOutInputStreamBuffer = new InputStreamBuffer(
			process.getInputStream());

		_standardOutInputStreamBuffer.start();
	}

	@Override
	public void destroy() {
		_process.destroy();
	}

	@Override
	public int exitValue() {
		int exitValue = _process.exitValue();

		JenkinsResultsParserUtil.sleep(_MILLIS_EXECUTION_TIME_MIN);

		return exitValue;
	}

	@Override
	public InputStream getErrorStream() {
		return _standardErrorInputStreamBuffer.toInputStream();
	}

	@Override
	public InputStream getInputStream() {
		return _standardOutInputStreamBuffer.toInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		return _process.getOutputStream();
	}

	@Override
	public int waitFor() throws InterruptedException {
		JenkinsResultsParserUtil.sleep(_MILLIS_EXECUTION_TIME_MIN);

		return _process.waitFor();
	}

	private static final long _MILLIS_EXECUTION_TIME_MIN = 10;

	private final Process _process;
	private final InputStreamBuffer _standardErrorInputStreamBuffer;
	private final InputStreamBuffer _standardOutInputStreamBuffer;

	private class InputStreamBuffer extends Thread {

		public InputStreamBuffer(InputStream inputStream) {
			_inputStream = inputStream;
		}

		public void run() {
			try {
				byte[] bytes = new byte[256];

				int bytesRead = 0;

				while (bytesRead != -1) {
					bytesRead = _inputStream.read(bytes);

					if (bytesRead > 0) {
						synchronized (_byteArrayOutputStream) {
							_byteArrayOutputStream.write(bytes, 0, bytesRead);
						}
					}
				}
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		public InputStream toInputStream() {
			return new ByteArrayInputStream(
				_byteArrayOutputStream.toByteArray());
		}

		private final ByteArrayOutputStream _byteArrayOutputStream =
			new ByteArrayOutputStream();
		private final InputStream _inputStream;

	}

}