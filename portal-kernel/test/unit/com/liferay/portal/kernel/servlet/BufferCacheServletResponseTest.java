/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.DummyOutputStream;
import com.liferay.portal.kernel.io.DummyWriter;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.test.util.PropsTestUtil;

import jakarta.servlet.ServletOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class BufferCacheServletResponseTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testConstructor() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		Assert.assertSame(
			stubHttpServletResponse, bufferCacheServletResponse.getResponse());
	}

	@Test
	public void testGetBufferSize() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
				}

			};

		// Clean

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		Assert.assertEquals(0, bufferCacheServletResponse.getBufferSize());

		// Byte buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setByteBuffer(ByteBuffer.wrap(_TEST_BYTES));

		Assert.assertEquals(
			_TEST_BYTES.length, bufferCacheServletResponse.getBufferSize());

		// Character buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		CharBuffer charBuffer = CharBuffer.wrap(_TEST_STRING);

		bufferCacheServletResponse.setCharBuffer(CharBuffer.wrap(_TEST_STRING));

		Assert.assertEquals(
			_TEST_STRING.length(), bufferCacheServletResponse.getBufferSize());
		Assert.assertEquals(0, charBuffer.position());
		Assert.assertEquals(_TEST_STRING.length(), charBuffer.limit());
		Assert.assertEquals(_TEST_STRING.length(), charBuffer.capacity());

		// Servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		ServletOutputStream servletOutputStream =
			bufferCacheServletResponse.getOutputStream();

		servletOutputStream.write(_TEST_BYTES);

		Assert.assertEquals(
			_TEST_BYTES.length, bufferCacheServletResponse.getBufferSize());

		// Print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		PrintWriter printWriter = bufferCacheServletResponse.getWriter();

		printWriter.print(_TEST_STRING);

		Assert.assertEquals(
			_TEST_STRING.length(), bufferCacheServletResponse.getBufferSize());

		// Exception handling

		OutputStream failFlushOutputStream = new UnsyncByteArrayOutputStream() {

			@Override
			public void flush() throws IOException {
				throw new IOException("Forced IOException");
			}

		};

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		servletOutputStream = bufferCacheServletResponse.getOutputStream();

		ReflectionTestUtil.setFieldValue(
			servletOutputStream, "outputStream", failFlushOutputStream);

		try {
			bufferCacheServletResponse.getBufferSize();

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Throwable throwable = runtimeException.getCause();

			Assert.assertTrue(throwable instanceof IOException);
			Assert.assertEquals("Forced IOException", throwable.getMessage());
		}
	}

	@Test
	public void testGetByteBuffer() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
				}

			};

		// Clean

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		ByteBuffer byteBuffer = bufferCacheServletResponse.getByteBuffer();

		Assert.assertEquals(0, byteBuffer.limit());

		// Byte buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		byteBuffer = ByteBuffer.wrap(_TEST_BYTES);

		bufferCacheServletResponse.setByteBuffer(byteBuffer);

		Assert.assertSame(
			byteBuffer, bufferCacheServletResponse.getByteBuffer());

		ServletOutputStreamAdapter servletOutputStreamAdapter =
			(ServletOutputStreamAdapter)
				bufferCacheServletResponse.getOutputStream();

		Assert.assertTrue(
			servletOutputStreamAdapter.outputStream instanceof
				DummyOutputStream);

		Assert.assertTrue(bufferCacheServletResponse.calledGetOutputStream);

		bufferCacheServletResponse.setByteBuffer(null);

		Assert.assertFalse(bufferCacheServletResponse.calledGetOutputStream);

		servletOutputStreamAdapter =
			(ServletOutputStreamAdapter)
				bufferCacheServletResponse.getOutputStream();

		Assert.assertTrue(
			servletOutputStreamAdapter.outputStream instanceof
				UnsyncByteArrayOutputStream);

		Assert.assertTrue(bufferCacheServletResponse.calledGetOutputStream);

		// Char buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		CharBuffer charBuffer = CharBuffer.wrap(_TEST_STRING);

		bufferCacheServletResponse.setCharBuffer(charBuffer);

		Assert.assertEquals(
			ByteBuffer.wrap(_TEST_BYTES),
			bufferCacheServletResponse.getByteBuffer());

		Assert.assertEquals(0, charBuffer.position());
		Assert.assertEquals(_TEST_STRING.length(), charBuffer.limit());
		Assert.assertEquals(_TEST_STRING.length(), charBuffer.capacity());

		UnsyncPrintWriter unsyncPrintWriter =
			(UnsyncPrintWriter)bufferCacheServletResponse.getWriter();

		Object writer = ReflectionTestUtil.getFieldValue(
			unsyncPrintWriter, "_writer");

		Assert.assertTrue(writer instanceof DummyWriter);

		Assert.assertTrue(bufferCacheServletResponse.calledGetWriter);

		bufferCacheServletResponse.setCharBuffer(null);

		Assert.assertFalse(bufferCacheServletResponse.calledGetWriter);

		unsyncPrintWriter =
			(UnsyncPrintWriter)bufferCacheServletResponse.getWriter();

		writer = ReflectionTestUtil.getFieldValue(unsyncPrintWriter, "_writer");

		Assert.assertTrue(writer instanceof UnsyncStringWriter);

		Assert.assertTrue(bufferCacheServletResponse.calledGetWriter);

		// Servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		ServletOutputStream servletOutputStream =
			bufferCacheServletResponse.getOutputStream();

		servletOutputStream.write(_TEST_BYTES);

		Assert.assertEquals(
			ByteBuffer.wrap(_TEST_BYTES),
			bufferCacheServletResponse.getByteBuffer());

		// Print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		PrintWriter printWriter = bufferCacheServletResponse.getWriter();

		printWriter.write(_TEST_STRING);

		Assert.assertEquals(
			ByteBuffer.wrap(_TEST_BYTES),
			bufferCacheServletResponse.getByteBuffer());
	}

	@Test
	public void testGetCharBuffer() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
				}

			};

		// Clean

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		CharBuffer charBuffer = bufferCacheServletResponse.getCharBuffer();

		Assert.assertEquals(0, charBuffer.limit());

		// Character buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharBuffer(CharBuffer.wrap(_TEST_STRING));

		Assert.assertEquals(
			_TEST_STRING,
			String.valueOf(bufferCacheServletResponse.getCharBuffer()));

		// Byte buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		ByteBuffer byteBuffer = ByteBuffer.wrap(_TEST_BYTES);

		bufferCacheServletResponse.setByteBuffer(byteBuffer);

		Assert.assertEquals(
			_TEST_STRING,
			String.valueOf(bufferCacheServletResponse.getCharBuffer()));

		Assert.assertEquals(0, byteBuffer.position());
		Assert.assertEquals(_TEST_BYTES.length, byteBuffer.limit());
		Assert.assertEquals(_TEST_BYTES.length, byteBuffer.capacity());

		// Print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		PrintWriter printWriter = bufferCacheServletResponse.getWriter();

		printWriter.print(_TEST_STRING);

		Assert.assertEquals(
			_TEST_STRING,
			String.valueOf(bufferCacheServletResponse.getCharBuffer()));

		// Servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		ServletOutputStream servletOutputStream =
			bufferCacheServletResponse.getOutputStream();

		servletOutputStream.write(_TEST_BYTES);

		Assert.assertEquals(
			_TEST_STRING,
			String.valueOf(bufferCacheServletResponse.getCharBuffer()));
	}

	@Test
	public void testGetOutputStream() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		// Two gets

		ServletOutputStream servletOutputStream1 =
			bufferCacheServletResponse.getOutputStream();
		ServletOutputStream servletOutputStream2 =
			bufferCacheServletResponse.getOutputStream();

		Assert.assertSame(servletOutputStream1, servletOutputStream2);

		// Get servlet output stream after getting print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.getWriter();

		try {
			bufferCacheServletResponse.getOutputStream();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testGetString() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
				}

			};

		// Clean

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		Assert.assertEquals(
			StringPool.BLANK, bufferCacheServletResponse.getString());

		// Character buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharBuffer(CharBuffer.wrap(_TEST_STRING));

		Assert.assertEquals(
			_TEST_STRING, bufferCacheServletResponse.getString());

		// Byte buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		ByteBuffer byteBuffer = ByteBuffer.wrap(_TEST_BYTES);

		bufferCacheServletResponse.setByteBuffer(byteBuffer);

		Assert.assertEquals(
			_TEST_STRING, bufferCacheServletResponse.getString());
		Assert.assertEquals(0, byteBuffer.position());
		Assert.assertEquals(_TEST_BYTES.length, byteBuffer.limit());
		Assert.assertEquals(_TEST_BYTES.length, byteBuffer.capacity());

		// Print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		PrintWriter printWriter = bufferCacheServletResponse.getWriter();

		printWriter.print(_TEST_STRING);

		Assert.assertEquals(
			_TEST_STRING, bufferCacheServletResponse.getString());

		// Servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		ServletOutputStream servletOutputStream =
			bufferCacheServletResponse.getOutputStream();

		servletOutputStream.write(_TEST_BYTES);

		Assert.assertEquals(
			_TEST_STRING, bufferCacheServletResponse.getString());
	}

	@Test
	public void testGetStringBundler() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
				}

			};

		// Clean

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		StringBundler sb = bufferCacheServletResponse.getStringBundler();

		Assert.assertEquals(1, sb.capacity());
		Assert.assertEquals(0, sb.index());

		// Character buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharBuffer(CharBuffer.wrap(_TEST_STRING));

		sb = bufferCacheServletResponse.getStringBundler();

		Assert.assertEquals(1, sb.capacity());
		Assert.assertEquals(1, sb.index());
		Assert.assertEquals(_TEST_STRING, sb.toString());

		// Byte buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		ByteBuffer byteBuffer = ByteBuffer.wrap(_TEST_BYTES);

		bufferCacheServletResponse.setByteBuffer(byteBuffer);

		sb = bufferCacheServletResponse.getStringBundler();

		Assert.assertEquals(1, sb.capacity());
		Assert.assertEquals(1, sb.index());
		Assert.assertEquals(_TEST_STRING, sb.toString());

		Assert.assertEquals(0, byteBuffer.position());
		Assert.assertEquals(_TEST_BYTES.length, byteBuffer.limit());
		Assert.assertEquals(_TEST_BYTES.length, byteBuffer.capacity());

		// Print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		PrintWriter printWriter = bufferCacheServletResponse.getWriter();

		printWriter.print(_TEST_STRING);

		sb = bufferCacheServletResponse.getStringBundler();

		Assert.assertEquals(10, sb.capacity());
		Assert.assertEquals(1, sb.index());
		Assert.assertEquals(_TEST_STRING, sb.toString());

		// Servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		ServletOutputStream servletOutputStream =
			bufferCacheServletResponse.getOutputStream();

		servletOutputStream.write(_TEST_BYTES);

		sb = bufferCacheServletResponse.getStringBundler();

		Assert.assertEquals(1, sb.capacity());
		Assert.assertEquals(1, sb.index());
		Assert.assertEquals(_TEST_STRING, sb.toString());
	}

	@Test
	public void testGetWriter() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		// Two gets

		PrintWriter printWriter1 = bufferCacheServletResponse.getWriter();
		PrintWriter printWriter2 = bufferCacheServletResponse.getWriter();

		Assert.assertSame(printWriter1, printWriter2);

		// Get printWriter after getting servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.getOutputStream();

		try {
			bufferCacheServletResponse.getWriter();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testIsByteCharMode() throws Exception {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
				}

			};

		// Clean

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		Assert.assertFalse(bufferCacheServletResponse.isByteMode());
		Assert.assertFalse(bufferCacheServletResponse.isCharMode());

		// Byte buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setByteBuffer(ByteBuffer.wrap(_TEST_BYTES));

		Assert.assertTrue(bufferCacheServletResponse.isByteMode());
		Assert.assertFalse(bufferCacheServletResponse.isCharMode());

		// Character buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.setCharBuffer(CharBuffer.wrap(_TEST_STRING));

		Assert.assertFalse(bufferCacheServletResponse.isByteMode());
		Assert.assertTrue(bufferCacheServletResponse.isCharMode());

		// Servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.getOutputStream();

		Assert.assertTrue(bufferCacheServletResponse.isByteMode());
		Assert.assertFalse(bufferCacheServletResponse.isCharMode());

		// Print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		bufferCacheServletResponse.getWriter();

		Assert.assertFalse(bufferCacheServletResponse.isByteMode());
		Assert.assertTrue(bufferCacheServletResponse.isCharMode());
	}

	@Test
	public void testOutputBuffer() throws Exception {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

			};

		PropsTestUtil.setProps(Collections.emptyMap());

		// Clean

		BufferCacheServletResponse toResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		BufferCacheServletResponse fromResponse =
			new BufferCacheServletResponse(toResponse);

		fromResponse.outputBuffer();

		ByteBuffer newByteBuffer = toResponse.getByteBuffer();

		Assert.assertEquals(0, newByteBuffer.limit());

		// Byte buffer

		toResponse = new BufferCacheServletResponse(stubHttpServletResponse);

		fromResponse = new BufferCacheServletResponse(toResponse);

		ByteBuffer byteBuffer = ByteBuffer.wrap(_TEST_BYTES);

		fromResponse.setByteBuffer(byteBuffer);

		fromResponse.outputBuffer();

		newByteBuffer = toResponse.getByteBuffer();

		Assert.assertSame(byteBuffer, newByteBuffer);

		// Character buffer

		toResponse = new BufferCacheServletResponse(stubHttpServletResponse);

		fromResponse = new BufferCacheServletResponse(toResponse);

		CharBuffer charBuffer = CharBuffer.wrap(_TEST_STRING);

		fromResponse.setCharBuffer(charBuffer);

		fromResponse.outputBuffer();

		CharBuffer newCharBuffer = toResponse.getCharBuffer();

		Assert.assertSame(charBuffer, newCharBuffer);

		// Servlet output stream

		toResponse = new BufferCacheServletResponse(stubHttpServletResponse);

		fromResponse = new BufferCacheServletResponse(toResponse);

		ServletOutputStream servletOutputStream =
			fromResponse.getOutputStream();

		servletOutputStream.write(_TEST_BYTES);

		fromResponse.outputBuffer();

		newByteBuffer = toResponse.getByteBuffer();

		Assert.assertEquals(ByteBuffer.wrap(_TEST_BYTES), newByteBuffer);

		// Print writer

		toResponse = new BufferCacheServletResponse(stubHttpServletResponse);

		fromResponse = new BufferCacheServletResponse(toResponse);

		PrintWriter printWriter = fromResponse.getWriter();

		printWriter.write(_TEST_STRING);

		fromResponse.outputBuffer();

		newCharBuffer = toResponse.getCharBuffer();

		Assert.assertEquals(_TEST_STRING, newCharBuffer.toString());
	}

	@Test
	public void testResetBuffer() throws Exception {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		// Null out byte buffer

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		ByteBuffer byteBuffer = ByteBuffer.wrap(_TEST_BYTES);

		bufferCacheServletResponse.setByteBuffer(byteBuffer);

		Assert.assertSame(
			byteBuffer, bufferCacheServletResponse.getByteBuffer());

		bufferCacheServletResponse.resetBuffer(true);

		ByteBuffer newByteBuffer = bufferCacheServletResponse.getByteBuffer();

		Assert.assertNotSame(byteBuffer, newByteBuffer);
		Assert.assertEquals(0, newByteBuffer.capacity());

		// Null out character buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		CharBuffer charBuffer = CharBuffer.wrap(_TEST_STRING);

		bufferCacheServletResponse.setCharBuffer(charBuffer);

		Assert.assertSame(
			charBuffer, bufferCacheServletResponse.getCharBuffer());

		bufferCacheServletResponse.resetBuffer(true);

		CharBuffer newCharBuffer = bufferCacheServletResponse.getCharBuffer();

		Assert.assertNotSame(charBuffer, newCharBuffer);
		Assert.assertEquals(0, newCharBuffer.capacity());

		// Null out servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		ServletOutputStream servletOutputStream =
			bufferCacheServletResponse.getOutputStream();

		Assert.assertSame(
			servletOutputStream, bufferCacheServletResponse.getOutputStream());

		Assert.assertTrue(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(bufferCacheServletResponse.calledGetWriter);

		bufferCacheServletResponse.resetBuffer(true);

		Assert.assertFalse(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(bufferCacheServletResponse.calledGetWriter);
		Assert.assertNotSame(
			servletOutputStream, bufferCacheServletResponse.getOutputStream());
		Assert.assertTrue(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(bufferCacheServletResponse.calledGetWriter);

		// Null out print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		PrintWriter printWriter = bufferCacheServletResponse.getWriter();

		Assert.assertSame(printWriter, bufferCacheServletResponse.getWriter());

		Assert.assertFalse(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertTrue(bufferCacheServletResponse.calledGetWriter);

		bufferCacheServletResponse.resetBuffer(true);

		Assert.assertFalse(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(bufferCacheServletResponse.calledGetWriter);
		Assert.assertNotSame(
			printWriter, bufferCacheServletResponse.getWriter());
		Assert.assertFalse(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertTrue(bufferCacheServletResponse.calledGetWriter);

		// Reset byte buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		byteBuffer = ByteBuffer.wrap(_TEST_BYTES);

		bufferCacheServletResponse.setByteBuffer(byteBuffer);

		Assert.assertSame(
			byteBuffer, bufferCacheServletResponse.getByteBuffer());

		bufferCacheServletResponse.resetBuffer(false);

		newByteBuffer = bufferCacheServletResponse.getByteBuffer();

		Assert.assertNotSame(byteBuffer, newByteBuffer);
		Assert.assertEquals(0, newByteBuffer.capacity());

		// Reset character buffer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		charBuffer = CharBuffer.wrap(_TEST_STRING);

		bufferCacheServletResponse.setCharBuffer(charBuffer);

		Assert.assertSame(
			charBuffer, bufferCacheServletResponse.getCharBuffer());

		bufferCacheServletResponse.resetBuffer(false);

		newCharBuffer = bufferCacheServletResponse.getCharBuffer();

		Assert.assertNotSame(charBuffer, newCharBuffer);
		Assert.assertEquals(0, newCharBuffer.capacity());

		// Reset servlet output stream

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		servletOutputStream = bufferCacheServletResponse.getOutputStream();

		Assert.assertSame(
			servletOutputStream, bufferCacheServletResponse.getOutputStream());

		Assert.assertTrue(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(bufferCacheServletResponse.calledGetWriter);

		servletOutputStream.write(_TEST_BYTES);

		byteBuffer = bufferCacheServletResponse.getByteBuffer();

		Assert.assertEquals(ByteBuffer.wrap(_TEST_BYTES), byteBuffer);

		bufferCacheServletResponse.resetBuffer(false);

		Assert.assertSame(
			servletOutputStream, bufferCacheServletResponse.getOutputStream());
		Assert.assertTrue(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertFalse(bufferCacheServletResponse.calledGetWriter);

		byteBuffer = bufferCacheServletResponse.getByteBuffer();

		Assert.assertEquals(0, byteBuffer.limit());

		// Reset print writer

		bufferCacheServletResponse = new BufferCacheServletResponse(
			stubHttpServletResponse);

		printWriter = bufferCacheServletResponse.getWriter();

		Assert.assertSame(printWriter, bufferCacheServletResponse.getWriter());

		Assert.assertFalse(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertTrue(bufferCacheServletResponse.calledGetWriter);

		printWriter.write(_TEST_STRING);

		charBuffer = bufferCacheServletResponse.getCharBuffer();

		Assert.assertEquals(_TEST_STRING, charBuffer.toString());

		bufferCacheServletResponse.resetBuffer(false);

		Assert.assertSame(printWriter, bufferCacheServletResponse.getWriter());
		Assert.assertFalse(bufferCacheServletResponse.calledGetOutputStream);
		Assert.assertTrue(bufferCacheServletResponse.calledGetWriter);

		charBuffer = bufferCacheServletResponse.getCharBuffer();

		Assert.assertEquals(0, charBuffer.limit());
	}

	@Test
	public void testSetBufferSize() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

			};

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		// Normal

		bufferCacheServletResponse.setBufferSize(1024);

		// Set after commit

		bufferCacheServletResponse.flushBuffer();

		try {
			bufferCacheServletResponse.setBufferSize(2048);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testSetContentLength() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		bufferCacheServletResponse.setContentLength(1024);
	}

	@Test
	public void testSetContentLengthLong() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		bufferCacheServletResponse.setContentLengthLong(1024);
	}

	@Test
	public void testSetString() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(stubHttpServletResponse);

		bufferCacheServletResponse.setString(_TEST_STRING);

		Assert.assertEquals(
			_TEST_STRING, bufferCacheServletResponse.getString());
	}

	private static final byte[] _TEST_BYTES = {'a', 'b', 'c'};

	private static final String _TEST_STRING = "abc";

}