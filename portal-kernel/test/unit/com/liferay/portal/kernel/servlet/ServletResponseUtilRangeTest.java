/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Tomas Polesovsky
 */
public class ServletResponseUtilRangeTest {

	@BeforeClass
	public static void setUpClass() {
		FileUtil fileUtil = new FileUtil();

		com.liferay.portal.kernel.util.File file = Mockito.mock(
			com.liferay.portal.kernel.util.File.class);

		fileUtil.setFile(file);

		Mockito.when(
			file.createTempFile()
		).thenAnswer(
			(Answer<File>)invocation -> {
				String name = String.valueOf(System.currentTimeMillis());

				return File.createTempFile(name, null);
			}
		);

		Mockito.when(
			file.delete(Mockito.any(File.class))
		).thenAnswer(
			(Answer<Boolean>)invocation -> {
				Object[] args = invocation.getArguments();

				File arg = (File)args[0];

				return arg.delete();
			}
		);

		PropsTestUtil.setProps(
			PropsKeys.WEB_SERVER_SERVLET_MAX_RANGE_FIELDS, "10");

		MimeTypes mimeTypes = Mockito.mock(MimeTypes.class);

		Mockito.when(
			mimeTypes.getExtensions(Mockito.anyString())
		).thenReturn(
			Collections.emptySet()
		);

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			MimeTypes.class, mimeTypes, null);
	}

	@AfterClass
	public static void tearDownClass() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testGetMultipleRanges() throws IOException {
		setUpRange(_httpServletRequest, "bytes=1-3,3-8,9-11,12-12,30-");

		List<Range> ranges = ReflectionTestUtil.invoke(
			ServletResponseUtil.class, "_getRanges",
			new Class<?>[] {HttpServletRequest.class, long.class},
			_httpServletRequest, 1000);

		Assert.assertEquals(ranges.toString(), 5, ranges.size());
		assertRange(ranges.get(0), 1, 3, 3);
		assertRange(ranges.get(1), 3, 8, 6);
		assertRange(ranges.get(2), 9, 11, 3);
		assertRange(ranges.get(3), 12, 12, 1);
		assertRange(ranges.get(4), 30, 999, 970);
	}

	@Test
	public void testGetRangesPerSpec() throws IOException {

		// https://tools.ietf.org/html/rfc7233#section-2.1

		long length = 10000;

		// The final 500 bytes (byte offsets 9500-9999, inclusive): bytes=-500
		// or bytes=9500-

		setUpRange(_httpServletRequest, "bytes=-500");

		List<Range> ranges = ReflectionTestUtil.invoke(
			ServletResponseUtil.class, "_getRanges",
			new Class<?>[] {HttpServletRequest.class, long.class},
			_httpServletRequest, length);

		Assert.assertEquals(ranges.toString(), 1, ranges.size());
		assertRange(ranges.get(0), 9500, 9999, 500);

		setUpRange(_httpServletRequest, "bytes=9500-");

		ranges = ReflectionTestUtil.invoke(
			ServletResponseUtil.class, "_getRanges",
			new Class<?>[] {HttpServletRequest.class, long.class},
			_httpServletRequest, length);

		Assert.assertEquals(ranges.toString(), 1, ranges.size());
		assertRange(ranges.get(0), 9500, 9999, 500);

		// The first and last bytes only (bytes 0 and 9999): bytes=0-0,-1

		setUpRange(_httpServletRequest, "bytes=0-0,-1");

		ranges = ReflectionTestUtil.invoke(
			ServletResponseUtil.class, "_getRanges",
			new Class<?>[] {HttpServletRequest.class, long.class},
			_httpServletRequest, length);

		Assert.assertEquals(ranges.toString(), 2, ranges.size());
		assertRange(ranges.get(0), 0, 0, 1);
		assertRange(ranges.get(1), 9999, 9999, 1);

		// Other valid (but not canonical) specifications of the second 500
		// bytes (byte offsets 500-999, inclusive): bytes=500-600,601-999 or
		// bytes=500-700,601-999

		setUpRange(_httpServletRequest, "bytes=500-600,601-999");

		ranges = ReflectionTestUtil.invoke(
			ServletResponseUtil.class, "_getRanges",
			new Class<?>[] {HttpServletRequest.class, long.class},
			_httpServletRequest, length);

		Assert.assertEquals(ranges.toString(), 2, ranges.size());
		assertRange(ranges.get(0), 500, 600, 101);
		assertRange(ranges.get(1), 601, 999, 399);

		setUpRange(_httpServletRequest, "bytes=500-700,601-999");

		ranges = ReflectionTestUtil.invoke(
			ServletResponseUtil.class, "_getRanges",
			new Class<?>[] {HttpServletRequest.class, long.class},
			_httpServletRequest, length);

		Assert.assertEquals(ranges.toString(), 2, ranges.size());
		assertRange(ranges.get(0), 500, 700, 201);
		assertRange(ranges.get(1), 601, 999, 399);
	}

	@Test
	public void testGetRangesSimple() throws IOException {
		setUpRange(_httpServletRequest, "bytes=0-999");

		List<Range> ranges = ReflectionTestUtil.invoke(
			ServletResponseUtil.class, "_getRanges",
			new Class<?>[] {HttpServletRequest.class, long.class},
			_httpServletRequest, 1000);

		Assert.assertEquals(ranges.toString(), 1, ranges.size());
		assertRange(ranges.get(0), 0, 999, 1000);
	}

	@Test
	public void testWriteWithRanges() throws IOException {
		byte[] content = new byte[1000];

		Arrays.fill(content, (byte)48);

		testWriteWith(new ByteArrayInputStream(content), content);

		File tempFile = FileUtil.createTempFile();

		try {
			try (FileOutputStream fileOutputStream = new FileOutputStream(
					tempFile)) {

				fileOutputStream.write(content);
			}

			testWriteWith(new FileInputStream(tempFile), content);
		}
		finally {
			tempFile.delete();
		}

		testWriteWith(
			new BufferedInputStream(new ByteArrayInputStream(content)),
			content);
	}

	protected void assertRange(Range range, long start, long end, long length) {
		Assert.assertEquals(range.getStart(), start);
		Assert.assertEquals(range.getEnd(), end);
		Assert.assertEquals(range.getLength(), length);
	}

	protected void setUpRange(
		HttpServletRequest httpServletRequest, String rangeHeader) {

		Mockito.when(
			httpServletRequest.getHeader(HttpHeaders.RANGE)
		).thenReturn(
			rangeHeader
		);
	}

	protected void testWriteWith(InputStream inputStream, byte[] content)
		throws IOException {

		setUpRange(_httpServletRequest, "bytes=0-9,980-989,980-999,990-999");

		List<Range> ranges = ReflectionTestUtil.invoke(
			ServletResponseUtil.class, "_getRanges",
			new Class<?>[] {HttpServletRequest.class, long.class},
			_httpServletRequest, content.length);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		mockHttpServletResponse.setCharacterEncoding(StringPool.UTF8);

		ServletResponseUtil.sendFileWithRangeHeader(
			_httpServletRequest, mockHttpServletResponse, "filename.txt",
			inputStream, content.length, "text/plain");

		String contentType = mockHttpServletResponse.getContentType();

		Assert.assertTrue(
			contentType.startsWith(_CONTENT_TYPE_BOUNDARY_PREFACE));

		String boundary = contentType.substring(
			_CONTENT_TYPE_BOUNDARY_PREFACE.length(),
			contentType.lastIndexOf(CharPool.SEMICOLON));

		String responseBody = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			responseBody.startsWith("\r\n--" + boundary + "\r\n"));
		Assert.assertTrue(responseBody.endsWith("--" + boundary + "--\r\n"));

		String[] responseBodies = StringUtil.split(responseBody, boundary);

		Assert.assertEquals(
			Arrays.toString(responseBodies), ranges.size() + 2,
			responseBodies.length);
		Assert.assertEquals(StringPool.DOUBLE_DASH, responseBodies[0]);
		Assert.assertEquals(
			StringPool.DOUBLE_DASH, responseBodies[ranges.size() + 1]);

		for (int i = 0; i < ranges.size(); i++) {
			Range range = ranges.get(i);

			String[] lines = StringUtil.split(
				responseBodies[i + 1], StringPool.RETURN_NEW_LINE);

			Assert.assertEquals("Content-Type: text/plain", lines[0]);
			Assert.assertEquals(
				"Content-Range: " + range.getContentRange(), lines[1]);
			Assert.assertTrue(Validator.isNull(lines[2]));

			int start = (int)range.getStart();
			int end = (int)range.getEnd();

			byte[] bytes = ArrayUtil.subset(content, start, end + 1);

			Assert.assertArrayEquals(bytes, lines[3].getBytes("UTF-8"));

			Assert.assertEquals(StringPool.DOUBLE_DASH, lines[4]);
		}
	}

	private static final String _CONTENT_TYPE_BOUNDARY_PREFACE =
		"multipart/byteranges; boundary=";

	private static ServiceRegistration<MimeTypes> _serviceRegistration;

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);

}