/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webserver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.BaseWebServerTestCase;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.webserver.WebServerServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class RangeWebServerTest extends BaseWebServerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testBasic() throws Exception {
		testRange(null);
	}

	@Test
	public void testMultipartRange() throws Exception {
		String[] ranges = {"0-9", "25-25", "30-49", "70-79"};

		String rangeHeader = "bytes=";

		for (int i = 0; i < ranges.length; i++) {
			rangeHeader += ranges[i];

			if (i != (ranges.length - 1)) {
				rangeHeader += StringPool.COMMA;
			}
		}

		MockHttpServletResponse mockHttpServletResponse = testRange(
			rangeHeader);

		String contentType = mockHttpServletResponse.getContentType();

		Assert.assertTrue(contentType.startsWith(_BOUNDARY_PREFACE));

		String boundary = contentType.substring(
			_BOUNDARY_PREFACE.length(),
			contentType.lastIndexOf(CharPool.SEMICOLON));

		String responseBody = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			responseBody.startsWith("\r\n--" + boundary + "\r\n"));
		Assert.assertTrue(responseBody.endsWith("--" + boundary + "--\r\n"));

		String[] responseBodies = StringUtil.split(responseBody, boundary);

		Assert.assertEquals(
			Arrays.toString(responseBodies), ranges.length + 2,
			responseBodies.length);
		Assert.assertEquals(StringPool.DOUBLE_DASH, responseBodies[0]);
		Assert.assertEquals(
			StringPool.DOUBLE_DASH, responseBodies[ranges.length + 1]);

		for (int i = 0; i < ranges.length; i++) {
			String[] lines = StringUtil.split(
				responseBodies[i + 1], StringPool.RETURN_NEW_LINE);

			Assert.assertEquals("Content-Type: text/plain", lines[0]);
			Assert.assertEquals(
				"Content-Range: bytes " + ranges[i] + "/80", lines[1]);
			Assert.assertTrue(Validator.isNull(lines[2]));

			String[] rangePair = StringUtil.split(ranges[i], StringPool.DASH);

			int start = GetterUtil.getInteger(rangePair[0]);
			int end = GetterUtil.getInteger(rangePair[1]);

			byte[] bytes = ArrayUtil.subset(
				_SAMPLE_DATA.getBytes(), start, end + 1);

			Assert.assertArrayEquals(bytes, lines[3].getBytes("UTF-8"));

			Assert.assertEquals(StringPool.DOUBLE_DASH, lines[4]);
		}
	}

	@Test
	public void testSingleRangeByte() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = testRange(
			"bytes=10-10");

		Assert.assertEquals(
			"1", mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_LENGTH));
		Assert.assertEquals(
			"bytes 10-10/80",
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_RANGE));
		Assert.assertEquals("B", mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testSingleRangeFirst() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = testRange(
			"bytes=0-9");

		Assert.assertEquals(
			"10",
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_LENGTH));
		Assert.assertEquals(
			"bytes 0-9/80",
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_RANGE));
		Assert.assertEquals(
			"A123456789", mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testSingleRangeLast() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = testRange(
			"bytes=70-79");

		Assert.assertEquals(
			"10",
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_LENGTH));
		Assert.assertEquals(
			"bytes 70-79/80",
			mockHttpServletResponse.getHeader(HttpHeaders.CONTENT_RANGE));
		Assert.assertArrayEquals(
			_UNICODE_DATA.getBytes(),
			mockHttpServletResponse.getContentAsByteArray());
	}

	@Override
	protected HttpServlet getServlet() {
		return new WebServerServlet() {

			@Override
			protected boolean isSupportsRangeHeader(String contentType) {
				return true;
			}

		};
	}

	protected MockHttpServletResponse testRange(String rangeHeader)
		throws Exception {

		String fileName = "Test Range.txt";

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), group.getGroupId(),
			parentFolder.getFolderId(), fileName, ContentTypes.TEXT_PLAIN,
			_SAMPLE_DATA.getBytes(), null, null, null,
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), TestPropsValues.getUserId()));

		String path = StringBundler.concat(
			"/", fileEntry.getGroupId(), "/", fileEntry.getFolderId(), "/",
			fileEntry.getTitle());

		Map<String, String> headers = new HashMap<>();

		if (Validator.isNotNull(rangeHeader)) {
			headers.put(HttpHeaders.RANGE, rangeHeader);
		}

		MockHttpServletResponse mockHttpServletResponse = service(
			Method.GET, path, headers, null, null, null);

		int status = mockHttpServletResponse.getStatus();

		Assert.assertTrue(
			mockHttpServletResponse.containsHeader(HttpHeaders.ACCEPT_RANGES));

		if (Validator.isNotNull(rangeHeader)) {
			Assert.assertEquals(HttpServletResponse.SC_PARTIAL_CONTENT, status);
		}
		else {
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
		}

		String contentType = mockHttpServletResponse.getContentType();

		if (Validator.isNotNull(rangeHeader) &&
			rangeHeader.contains(StringPool.COMMA)) {

			Assert.assertTrue(contentType.startsWith("multipart/byteranges"));
		}
		else {
			Assert.assertTrue(contentType.startsWith(ContentTypes.TEXT_PLAIN));
		}

		return mockHttpServletResponse;
	}

	private static final String _BOUNDARY_PREFACE =
		"multipart/byteranges; boundary=";

	private static final String _SAMPLE_DATA =
		"A123456789B123456789C123456789D123456789" +
			"E123456789F123456789G123456789" + RangeWebServerTest._UNICODE_DATA;

	private static final String _UNICODE_DATA = "H\u4e2d\u00e9\u00fc\u00f1";

	@Inject
	private static DLAppLocalService _dlAppLocalService;

}