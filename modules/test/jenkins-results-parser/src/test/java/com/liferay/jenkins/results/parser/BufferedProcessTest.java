/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Peter Yoo
 */
public class BufferedProcessTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testOutputOverThreshold() throws Exception {
		byte[] standardOut = _newBytes(11 * 1024 * 1024);

		BufferedProcess bufferedProcess = new BufferedProcess(
			new StubProcess(standardOut));

		Assert.assertArrayEquals(
			standardOut, _readStandardOut(bufferedProcess, standardOut.length));

		Assert.assertTrue(
			bufferedProcess.getInputStream() instanceof FileInputStream);
	}

	@Test
	public void testOutputUnderThreshold() throws Exception {
		byte[] standardOut = _newBytes(1024);

		BufferedProcess bufferedProcess = new BufferedProcess(
			new StubProcess(standardOut));

		Assert.assertArrayEquals(
			standardOut, _readStandardOut(bufferedProcess, standardOut.length));

		Assert.assertTrue(
			bufferedProcess.getInputStream() instanceof ByteArrayInputStream);
	}

	private byte[] _newBytes(int size) {
		byte[] bytes = new byte[size];

		for (int i = 0; i < size; i++) {
			bytes[i] = (byte)('a' + (i % 26));
		}

		return bytes;
	}

	private byte[] _readInputStream(InputStream inputStream) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		byte[] bytes = new byte[8192];

		int bytesRead = inputStream.read(bytes);

		while (bytesRead != -1) {
			byteArrayOutputStream.write(bytes, 0, bytesRead);

			bytesRead = inputStream.read(bytes);
		}

		return byteArrayOutputStream.toByteArray();
	}

	private byte[] _readStandardOut(
			BufferedProcess bufferedProcess, int expectedLength)
		throws Exception {

		byte[] read = new byte[0];

		for (int i = 0; i < 1000; i++) {
			read = _readInputStream(bufferedProcess.getInputStream());

			if (read.length >= expectedLength) {
				break;
			}

			JenkinsResultsParserUtil.sleep(10);
		}

		return read;
	}

	private static class StubProcess extends Process {

		public StubProcess(byte[] standardOut) {
			_standardOut = standardOut;
		}

		@Override
		public void destroy() {
		}

		@Override
		public int exitValue() {
			return 0;
		}

		@Override
		public InputStream getErrorStream() {
			return new ByteArrayInputStream(new byte[0]);
		}

		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(_standardOut);
		}

		@Override
		public OutputStream getOutputStream() {
			return new ByteArrayOutputStream();
		}

		@Override
		public int waitFor() {
			return 0;
		}

		private final byte[] _standardOut;

	}

}