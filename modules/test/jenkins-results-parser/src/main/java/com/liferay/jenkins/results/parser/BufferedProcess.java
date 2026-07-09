/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

		_standardErrorInputStreamBuffer._close();
		_standardOutInputStreamBuffer._close();
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

	private static final int _MEMORY_THRESHOLD = 10 * 1024 * 1024;

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
						_write(bytes, bytesRead);
					}
				}
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		public synchronized InputStream toInputStream() {
			if (_closed) {
				return new ByteArrayInputStream(new byte[0]);
			}

			if (_overflowFile == null) {
				return new ByteArrayInputStream(
					_byteArrayOutputStream.toByteArray());
			}

			try {
				_overflowOutputStream.flush();

				return new FileInputStream(_overflowFile);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		private synchronized void _close() {
			if (_closed) {
				return;
			}

			_closed = true;

			if (_overflowOutputStream != null) {
				try {
					_overflowOutputStream.close();
				}
				catch (IOException ioException) {
				}
			}

			if (_overflowFile != null) {
				_overflowFile.delete();
			}
		}

		private synchronized void _write(byte[] bytes, int length) {
			if (_closed) {
				return;
			}

			try {
				if (_overflowOutputStream != null) {
					_overflowOutputStream.write(bytes, 0, length);

					return;
				}

				if ((_byteArrayOutputStream.size() + length) <=
						_MEMORY_THRESHOLD) {

					_byteArrayOutputStream.write(bytes, 0, length);

					return;
				}

				_overflowFile = File.createTempFile(
					"buffered-process-overflow-", ".tmp");

				_overflowFile.deleteOnExit();

				_overflowOutputStream = new BufferedOutputStream(
					new FileOutputStream(_overflowFile));

				_byteArrayOutputStream.writeTo(_overflowOutputStream);

				_byteArrayOutputStream = null;

				_overflowOutputStream.write(bytes, 0, length);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		private ByteArrayOutputStream _byteArrayOutputStream =
			new ByteArrayOutputStream();
		private boolean _closed;
		private final InputStream _inputStream;
		private File _overflowFile;
		private OutputStream _overflowOutputStream;

	}

}