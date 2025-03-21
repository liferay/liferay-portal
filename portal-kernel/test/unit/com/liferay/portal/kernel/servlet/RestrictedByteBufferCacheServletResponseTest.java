/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.io.DummyOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;

import jakarta.servlet.ServletOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class RestrictedByteBufferCacheServletResponseTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testConstructor() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		RestrictedByteBufferCacheServletResponse
			restrictedByteBufferCacheServletResponse =
				new RestrictedByteBufferCacheServletResponse(
					stubHttpServletResponse, 1024);

		Assert.assertSame(
			stubHttpServletResponse,
			restrictedByteBufferCacheServletResponse.getResponse());

		Assert.assertFalse(
			restrictedByteBufferCacheServletResponse.isOverflowed());
	}

	@Test
	public void testGetBufferSize() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public ServletOutputStream getOutputStream() {
					return new ServletOutputStreamAdapter(
						new UnsyncByteArrayOutputStream());
				}

				@Override
				public boolean isCommitted() {
					return false;
				}

			};

		RestrictedByteBufferCacheServletResponse
			restrictedByteBufferCacheServletResponse =
				new RestrictedByteBufferCacheServletResponse(
					stubHttpServletResponse, 1024);

		Assert.assertEquals(
			1024, restrictedByteBufferCacheServletResponse.getBufferSize());

		OutputStream outputStream =
			restrictedByteBufferCacheServletResponse.getOutputStream();

		Assert.assertEquals(
			1024, restrictedByteBufferCacheServletResponse.getBufferSize());

		outputStream.flush();

		Assert.assertEquals(
			0, restrictedByteBufferCacheServletResponse.getBufferSize());
	}

	@Test
	public void testGetByteBuffer() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public ServletOutputStream getOutputStream() {
					return new ServletOutputStreamAdapter(
						new UnsyncByteArrayOutputStream());
				}

				@Override
				public boolean isCommitted() {
					return false;
				}

			};

		RestrictedByteBufferCacheServletResponse
			restrictedByteBufferCacheServletResponse =
				new RestrictedByteBufferCacheServletResponse(
					stubHttpServletResponse, 1024);

		ByteBuffer emptyByteBuffer = ReflectionTestUtil.getFieldValue(
			restrictedByteBufferCacheServletResponse, "_emptyByteBuffer");

		Assert.assertSame(
			emptyByteBuffer,
			restrictedByteBufferCacheServletResponse.getByteBuffer());

		OutputStream outputStream =
			restrictedByteBufferCacheServletResponse.getOutputStream();

		ByteBuffer byteBuffer =
			restrictedByteBufferCacheServletResponse.getByteBuffer();

		Assert.assertNotSame(
			emptyByteBuffer,
			restrictedByteBufferCacheServletResponse.getByteBuffer());

		Assert.assertEquals(0, byteBuffer.remaining());

		outputStream.flush();

		try {
			restrictedByteBufferCacheServletResponse.getByteBuffer();

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertEquals(
				"Cache overflowed", illegalStateException.getMessage());
		}

		Assert.assertTrue(
			restrictedByteBufferCacheServletResponse.isOverflowed());
	}

	@Test
	public void testGetOutputStream() throws IOException {

		// Two gets

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public ServletOutputStream getOutputStream() {
					return new ServletOutputStreamAdapter(
						new UnsyncByteArrayOutputStream());
				}

			};

		RestrictedByteBufferCacheServletResponse
			restrictedByteBufferCacheServletResponse =
				new RestrictedByteBufferCacheServletResponse(
					stubHttpServletResponse, 1024);

		ServletOutputStream servletOutputStream1 =
			restrictedByteBufferCacheServletResponse.getOutputStream();
		ServletOutputStream servletOutputStream2 =
			restrictedByteBufferCacheServletResponse.getOutputStream();

		Assert.assertSame(servletOutputStream1, servletOutputStream2);

		// Get servlet output stream after getting print writer

		restrictedByteBufferCacheServletResponse =
			new RestrictedByteBufferCacheServletResponse(
				stubHttpServletResponse, 1024);

		restrictedByteBufferCacheServletResponse.getWriter();

		try {
			restrictedByteBufferCacheServletResponse.getOutputStream();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testGetWriter() throws IOException {

		// Two gets

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public ServletOutputStream getOutputStream() {
					return new ServletOutputStreamAdapter(
						new UnsyncByteArrayOutputStream());
				}

			};

		RestrictedByteBufferCacheServletResponse
			restrictedByteBufferCacheServletResponse =
				new RestrictedByteBufferCacheServletResponse(
					stubHttpServletResponse, 1024);

		PrintWriter printWriter1 =
			restrictedByteBufferCacheServletResponse.getWriter();
		PrintWriter printWriter2 =
			restrictedByteBufferCacheServletResponse.getWriter();

		Assert.assertSame(printWriter1, printWriter2);

		// Get print writer after getting servlet output stream

		restrictedByteBufferCacheServletResponse =
			new RestrictedByteBufferCacheServletResponse(
				stubHttpServletResponse, 1024);

		restrictedByteBufferCacheServletResponse.getOutputStream();

		try {
			restrictedByteBufferCacheServletResponse.getWriter();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testResetBuffer() throws IOException {

		// Null out servlet output stream

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public ServletOutputStream getOutputStream() {
					return new ServletOutputStreamAdapter(
						new UnsyncByteArrayOutputStream());
				}

				@Override
				public boolean isCommitted() {
					return false;
				}

			};

		RestrictedByteBufferCacheServletResponse
			restrictedByteBufferCacheServletResponse =
				new RestrictedByteBufferCacheServletResponse(
					stubHttpServletResponse, 1024);

		OutputStream outputStream =
			restrictedByteBufferCacheServletResponse.getOutputStream();

		Assert.assertTrue(
			restrictedByteBufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(
			restrictedByteBufferCacheServletResponse.calledGetWriter);
		Assert.assertSame(
			outputStream,
			restrictedByteBufferCacheServletResponse.getOutputStream());

		restrictedByteBufferCacheServletResponse.resetBuffer(true);

		Assert.assertFalse(
			restrictedByteBufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(
			restrictedByteBufferCacheServletResponse.calledGetWriter);
		Assert.assertNotSame(
			outputStream,
			restrictedByteBufferCacheServletResponse.getOutputStream());

		// Null out print writer

		restrictedByteBufferCacheServletResponse =
			new RestrictedByteBufferCacheServletResponse(
				stubHttpServletResponse, 1024);

		PrintWriter printWriter =
			restrictedByteBufferCacheServletResponse.getWriter();

		Assert.assertFalse(
			restrictedByteBufferCacheServletResponse.calledGetOutputStream);
		Assert.assertTrue(
			restrictedByteBufferCacheServletResponse.calledGetWriter);
		Assert.assertSame(
			printWriter, restrictedByteBufferCacheServletResponse.getWriter());

		restrictedByteBufferCacheServletResponse.resetBuffer(true);

		Assert.assertFalse(
			restrictedByteBufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(
			restrictedByteBufferCacheServletResponse.calledGetWriter);
		Assert.assertNotSame(
			printWriter, restrictedByteBufferCacheServletResponse.getWriter());

		// Reset buffer

		restrictedByteBufferCacheServletResponse =
			new RestrictedByteBufferCacheServletResponse(
				stubHttpServletResponse, 1024);

		restrictedByteBufferCacheServletResponse.flushCache();
		restrictedByteBufferCacheServletResponse.resetBuffer(false);

		outputStream =
			restrictedByteBufferCacheServletResponse.getOutputStream();

		byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

		outputStream.write(bytes);

		ByteBuffer byteBuffer =
			restrictedByteBufferCacheServletResponse.getByteBuffer();

		Assert.assertEquals(ByteBuffer.wrap(bytes), byteBuffer);

		restrictedByteBufferCacheServletResponse.resetBuffer(false);

		byteBuffer = restrictedByteBufferCacheServletResponse.getByteBuffer();

		Assert.assertEquals(0, byteBuffer.remaining());

		restrictedByteBufferCacheServletResponse.flushCache();

		try {
			restrictedByteBufferCacheServletResponse.resetBuffer(false);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testSetBufferSize() throws IOException {

		// Setting smaller buffer size has no affect

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public int getBufferSize() {
					return _bufferSize;
				}

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setBufferSize(int bufferSize) {
					_bufferSize = bufferSize;
				}

				private int _bufferSize;

			};

		RestrictedByteBufferCacheServletResponse
			restrictedByteBufferCacheServletResponse =
				new RestrictedByteBufferCacheServletResponse(
					stubHttpServletResponse, 1024);

		restrictedByteBufferCacheServletResponse.setBufferSize(512);

		Assert.assertFalse(
			restrictedByteBufferCacheServletResponse.isOverflowed());
		Assert.assertEquals(
			1024, restrictedByteBufferCacheServletResponse.getBufferSize());

		// Setting a larger buffer size causes overflow

		restrictedByteBufferCacheServletResponse.setBufferSize(2048);

		Assert.assertTrue(
			restrictedByteBufferCacheServletResponse.isOverflowed());
		Assert.assertEquals(
			2048, restrictedByteBufferCacheServletResponse.getBufferSize());

		// Set after commit

		restrictedByteBufferCacheServletResponse.flushBuffer();

		try {
			restrictedByteBufferCacheServletResponse.setBufferSize(2048);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}

		// Setting a larger buffer size causes overflow with a failure in
		// flushing

		IOException ioException = new IOException();

		stubHttpServletResponse = new StubHttpServletResponse() {

			@Override
			public int getBufferSize() {
				return _bufferSize;
			}

			@Override
			public ServletOutputStream getOutputStream() {
				return new ServletOutputStreamAdapter(
					new DummyOutputStream() {

						@Override
						public void write(
							byte[] bytes, int offset, int length) {

							ReflectionUtil.throwException(ioException);
						}

					});
			}

			@Override
			public boolean isCommitted() {
				return false;
			}

			@Override
			public void setBufferSize(int bufferSize) {
				_bufferSize = bufferSize;
			}

			private int _bufferSize;

		};

		restrictedByteBufferCacheServletResponse =
			new RestrictedByteBufferCacheServletResponse(
				stubHttpServletResponse, 1024);

		Assert.assertNotNull(
			restrictedByteBufferCacheServletResponse.getOutputStream());

		try {
			restrictedByteBufferCacheServletResponse.setBufferSize(2048);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
			Assert.assertSame(ioException, illegalStateException.getCause());
		}
	}

}