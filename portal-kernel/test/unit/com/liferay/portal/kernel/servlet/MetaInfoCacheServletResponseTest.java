/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class MetaInfoCacheServletResponseTest {

	@ClassRule
	public static final CodeCoverageAssertor codeCoverageAssertor =
		CodeCoverageAssertor.INSTANCE;

	@Test
	public void testAddCookie() {
		final List<Cookie> cookies = new ArrayList<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public void addCookie(Cookie cookie) {
					cookies.add(cookie);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Map<String, Set<Header>> headers =
			metaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headers.toString(), 0, headers.size());

		Collection<String> headerNames =
			metaInfoCacheServletResponse.getHeaderNames();

		Assert.assertEquals(headerNames.toString(), 0, headerNames.size());

		Assert.assertNull(
			metaInfoCacheServletResponse.getHeader(HttpHeaders.SET_COOKIE));

		Collection<String> setCookieHeaders =
			metaInfoCacheServletResponse.getHeaders(HttpHeaders.SET_COOKIE);

		Assert.assertEquals(
			setCookieHeaders.toString(), 0, setCookieHeaders.size());

		// First add

		Cookie cookie1 = new Cookie("testCookieName1", "testCookieValue1");

		metaInfoCacheServletResponse.addCookie(cookie1);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(
			metaInfoCacheServletResponse.containsHeader(
				HttpHeaders.SET_COOKIE));

		Header cookieHeader1 = new Header(cookie1);

		Set<Header> cookieHeaders = headers.get(HttpHeaders.SET_COOKIE);

		Assert.assertEquals(cookieHeaders.toString(), 1, cookieHeaders.size());
		Assert.assertTrue(
			cookieHeaders.toString(), cookieHeaders.contains(cookieHeader1));

		Assert.assertEquals(cookies.toString(), 1, cookies.size());
		Assert.assertEquals(cookie1, cookies.get(0));

		setCookieHeaders = metaInfoCacheServletResponse.getHeaders(
			HttpHeaders.SET_COOKIE);

		Assert.assertEquals(
			setCookieHeaders.toString(), 1, setCookieHeaders.size());
		Assert.assertTrue(
			setCookieHeaders.toString(),
			setCookieHeaders.contains(cookieHeader1.toString()));

		Assert.assertEquals(
			cookieHeader1.toString(),
			metaInfoCacheServletResponse.getHeader(HttpHeaders.SET_COOKIE));
		Assert.assertTrue(
			headerNames.toString(),
			headerNames.contains(HttpHeaders.SET_COOKIE));

		// Second add

		Cookie cookie2 = new Cookie("testCookieName2", "testCookieValue2");

		metaInfoCacheServletResponse.addCookie(cookie2);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(
			metaInfoCacheServletResponse.containsHeader(
				HttpHeaders.SET_COOKIE));

		Header cookieHeader2 = new Header(cookie2);

		cookieHeaders = headers.get(HttpHeaders.SET_COOKIE);

		Assert.assertEquals(cookieHeaders.toString(), 2, cookieHeaders.size());
		Assert.assertTrue(
			cookieHeaders.toString(), cookieHeaders.contains(cookieHeader2));

		Assert.assertEquals(cookies.toString(), 2, cookies.size());
		Assert.assertEquals(cookie2, cookies.get(1));

		setCookieHeaders = metaInfoCacheServletResponse.getHeaders(
			HttpHeaders.SET_COOKIE);

		Assert.assertEquals(
			setCookieHeaders.toString(), 2, setCookieHeaders.size());
		Assert.assertTrue(
			setCookieHeaders.toString(),
			setCookieHeaders.contains(cookieHeader2.toString()));
	}

	@Test
	public void testAddDateHeader() {
		final List<ObjectValuePair<String, Long>> objectValuePairs =
			new ArrayList<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public void addDateHeader(String name, long value) {
					objectValuePairs.add(
						new ObjectValuePair<String, Long>(name, value));
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Map<String, Set<Header>> headers =
			metaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headers.toString(), 0, headers.size());

		// First add

		metaInfoCacheServletResponse.addDateHeader("date1", 1);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("date1"));

		Set<Header> dateHeaders1 = headers.get("date1");

		Assert.assertEquals(dateHeaders1.toString(), 1, dateHeaders1.size());
		Assert.assertTrue(
			dateHeaders1.toString(), dateHeaders1.contains(new Header(1L)));

		Assert.assertEquals(
			objectValuePairs.toString(), 1, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Long>("date1", 1L),
			objectValuePairs.get(0));

		// Second add

		metaInfoCacheServletResponse.addDateHeader("date1", 2);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("date1"));

		dateHeaders1 = headers.get("date1");

		Assert.assertEquals(dateHeaders1.toString(), 2, dateHeaders1.size());
		Assert.assertTrue(
			dateHeaders1.toString(), dateHeaders1.contains(new Header(2L)));

		Assert.assertEquals(
			objectValuePairs.toString(), 2, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Long>("date1", 2L),
			objectValuePairs.get(1));

		// Third add

		metaInfoCacheServletResponse.addDateHeader("date2", 1);

		Assert.assertEquals(headers.toString(), 2, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("date2"));

		Set<Header> dateHeaders2 = headers.get("date2");

		Assert.assertEquals(dateHeaders2.toString(), 1, dateHeaders2.size());
		Assert.assertTrue(
			dateHeaders2.toString(), dateHeaders2.contains(new Header(1L)));

		Assert.assertEquals(
			objectValuePairs.toString(), 3, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Long>("date2", 1L),
			objectValuePairs.get(2));
	}

	@Test
	public void testAddHeader() {
		final AtomicReference<String> contentTypeReference =
			new AtomicReference<>();
		final List<ObjectValuePair<String, String>> objectValuePairs =
			new ArrayList<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public void addHeader(String name, String value) {
					objectValuePairs.add(
						new ObjectValuePair<String, String>(name, value));
				}

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setContentType(String contentType) {
					contentTypeReference.set(contentType);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Map<String, Set<Header>> headersMap =
			metaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headersMap.toString(), 0, headersMap.size());

		// Add content type

		metaInfoCacheServletResponse.addHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.TEXT_HTML);

		Assert.assertEquals(headersMap.toString(), 0, headersMap.size());
		Assert.assertEquals(
			ContentTypes.TEXT_HTML,
			metaInfoCacheServletResponse.getContentType());
		Assert.assertEquals(ContentTypes.TEXT_HTML, contentTypeReference.get());

		// First add

		metaInfoCacheServletResponse.addHeader("name1", "value1");

		Assert.assertEquals(headersMap.toString(), 1, headersMap.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name1"));

		Set<Header> headers1 = headersMap.get("name1");

		Assert.assertEquals(headers1.toString(), 1, headers1.size());
		Assert.assertTrue(
			headers1.toString(), headers1.contains(new Header("value1")));

		Assert.assertEquals(
			objectValuePairs.toString(), 1, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, String>("name1", "value1"),
			objectValuePairs.get(0));

		// Second add

		metaInfoCacheServletResponse.addHeader("name1", "value2");

		Assert.assertEquals(headersMap.toString(), 1, headersMap.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name1"));

		headers1 = headersMap.get("name1");

		Assert.assertEquals(headers1.toString(), 2, headers1.size());
		Assert.assertTrue(
			headers1.toString(), headers1.contains(new Header("value2")));

		Assert.assertEquals(
			objectValuePairs.toString(), 2, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, String>("name1", "value2"),
			objectValuePairs.get(1));

		// Third add

		metaInfoCacheServletResponse.addHeader("name2", "value1");

		Assert.assertEquals(headersMap.toString(), 2, headersMap.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name2"));

		Set<Header> headers2 = headersMap.get("name2");

		Assert.assertEquals(headers2.toString(), 1, headers2.size());
		Assert.assertTrue(
			headers2.toString(), headers2.contains(new Header("value1")));

		Assert.assertEquals(
			objectValuePairs.toString(), 3, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, String>("name2", "value1"),
			objectValuePairs.get(2));
	}

	@Test
	public void testAddIntHeader() {
		final List<ObjectValuePair<String, Integer>> objectValuePairs =
			new ArrayList<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public void addIntHeader(String name, int value) {
					objectValuePairs.add(
						new ObjectValuePair<String, Integer>(name, value));
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Map<String, Set<Header>> headers =
			metaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headers.toString(), 0, headers.size());

		// First add

		metaInfoCacheServletResponse.addIntHeader("name1", 1);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name1"));

		Set<Header> intHeaders1 = headers.get("name1");

		Assert.assertEquals(intHeaders1.toString(), 1, intHeaders1.size());
		Assert.assertTrue(
			intHeaders1.toString(), intHeaders1.contains(new Header(1)));

		Assert.assertEquals(
			objectValuePairs.toString(), 1, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Integer>("name1", 1),
			objectValuePairs.get(0));

		// Second add

		metaInfoCacheServletResponse.addIntHeader("name1", 2);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name1"));

		intHeaders1 = headers.get("name1");

		Assert.assertEquals(intHeaders1.toString(), 2, intHeaders1.size());
		Assert.assertTrue(
			intHeaders1.toString(), intHeaders1.contains(new Header(2)));

		Assert.assertEquals(
			objectValuePairs.toString(), 2, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Integer>("name1", 2),
			objectValuePairs.get(1));

		// Third add

		metaInfoCacheServletResponse.addIntHeader("name2", 1);

		Assert.assertEquals(headers.toString(), 2, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name2"));

		Set<Header> intHeaders2 = headers.get("name2");

		Assert.assertEquals(intHeaders2.toString(), 1, intHeaders2.size());
		Assert.assertTrue(
			intHeaders2.toString(), intHeaders2.contains(new Header(1)));

		Assert.assertEquals(
			objectValuePairs.toString(), 3, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Integer>("name2", 1),
			objectValuePairs.get(2));
	}

	@Test
	public void testConstructor() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Assert.assertSame(
			stubHttpServletResponse,
			metaInfoCacheServletResponse.getResponse());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testFinishResponse() throws IOException {
		final AtomicLong contentLengthReference = new AtomicLong();
		final AtomicReference<String> locationReference =
			new AtomicReference<>();
		final AtomicReference<String> messageReference =
			new AtomicReference<>();
		final AtomicInteger statusReference = new AtomicInteger();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public void addHeader(String name, String value) {
				}

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void reset() {
				}

				@Override
				public void resetBuffer() {
				}

				@Override
				public void sendError(int status) {
					statusReference.set(status);
				}

				@Override
				public void sendError(int status, String errorMessage) {
					statusReference.set(status);
					messageReference.set(errorMessage);
				}

				@Override
				public void sendRedirect(String location) {
					locationReference.set(location);
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
				}

				@Override
				public void setContentLength(int contentLength) {
					contentLengthReference.set(contentLength);
				}

				@Override
				public void setContentLengthLong(long contentLength) {
					contentLengthReference.set(contentLength);
				}

				@Override
				public void setContentType(String contentType) {
				}

				@Override
				public void setHeader(String name, String value) {
				}

				@Override
				public void setLocale(Locale locale) {
				}

				@Override
				public void setStatus(int status) {
					statusReference.set(status);
				}

				/**
				 * @deprecated As of Judson (7.1.x)
				 */
				@Deprecated
				@Override
				public void setStatus(int status, String message) {
					statusReference.set(status);
					messageReference.set(message);
				}

			};

		// Clean

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		metaInfoCacheServletResponse.finishResponse();

		// Transfer headers

		MetaInfoCacheServletResponse innerMetaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		MetaInfoCacheServletResponse outerMetaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(innerMetaInfoCacheServletResponse);

		innerMetaInfoCacheServletResponse.addHeader("name1", "value3");
		innerMetaInfoCacheServletResponse.addHeader("name2", "value3");

		outerMetaInfoCacheServletResponse.addHeader("name1", "value1");
		outerMetaInfoCacheServletResponse.addHeader("name1", "value2");
		outerMetaInfoCacheServletResponse.addHeader("name2", "value1");

		outerMetaInfoCacheServletResponse.finishResponse();

		Map<String, Set<Header>> headersMap =
			innerMetaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headersMap.toString(), 2, headersMap.size());

		Set<Header> headers1 = headersMap.get("name1");

		Assert.assertEquals(headers1.toString(), 2, headers1.size());
		Assert.assertTrue(
			headers1.toString(), headers1.contains(new Header("value1")));
		Assert.assertTrue(
			headers1.toString(), headers1.contains(new Header("value2")));

		Set<Header> headers2 = headersMap.get("name2");

		Assert.assertEquals(headers2.toString(), 1, headers2.size());
		Assert.assertTrue(
			headers2.toString(), headers2.contains(new Header("value1")));

		outerMetaInfoCacheServletResponse.finishResponse();

		headersMap = innerMetaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headersMap.toString(), 2, headersMap.size());

		headers1 = headersMap.get("name1");

		Assert.assertEquals(headers1.toString(), 2, headers1.size());
		Assert.assertTrue(
			headers1.toString(), headers1.contains(new Header("value1")));
		Assert.assertTrue(
			headers1.toString(), headers1.contains(new Header("value2")));

		headers2 = headersMap.get("name2");

		Assert.assertEquals(headers2.toString(), 1, headers2.size());
		Assert.assertTrue(
			headers2.toString(), headers2.contains(new Header("value1")));

		// Send redirect

		MetaInfoCacheServletResponse fromMetaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		fromMetaInfoCacheServletResponse.sendRedirect("testURL");

		MetaInfoCacheServletResponse toMetaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		fromMetaInfoCacheServletResponse.setResponse(
			toMetaInfoCacheServletResponse);

		locationReference.set(null);

		fromMetaInfoCacheServletResponse.finishResponse();

		Assert.assertEquals("testURL", locationReference.get());

		// Send error

		fromMetaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		fromMetaInfoCacheServletResponse.sendError(400, "Bad Page");

		toMetaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		fromMetaInfoCacheServletResponse.setResponse(
			toMetaInfoCacheServletResponse);

		messageReference.set(null);
		statusReference.set(0);

		fromMetaInfoCacheServletResponse.finishResponse();

		Assert.assertEquals("Bad Page", messageReference.get());
		Assert.assertEquals(400, statusReference.get());

		// Normal

		fromMetaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		fromMetaInfoCacheServletResponse.setContentLength(2048);
		fromMetaInfoCacheServletResponse.setContentType(
			ContentTypes.TEXT_HTML_UTF8);
		fromMetaInfoCacheServletResponse.setLocale(LocaleUtil.US);
		fromMetaInfoCacheServletResponse.setStatus(302, "moved");

		toMetaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		fromMetaInfoCacheServletResponse.setResponse(
			toMetaInfoCacheServletResponse);

		contentLengthReference.set(0);
		messageReference.set(null);
		statusReference.set(0);

		fromMetaInfoCacheServletResponse.finishResponse();

		Assert.assertEquals(
			StringPool.UTF8,
			toMetaInfoCacheServletResponse.getCharacterEncoding());
		Assert.assertEquals(
			ContentTypes.TEXT_HTML,
			toMetaInfoCacheServletResponse.getContentType());
		Assert.assertEquals(
			LocaleUtil.US, toMetaInfoCacheServletResponse.getLocale());
		Assert.assertEquals(2048, contentLengthReference.get());
		Assert.assertNull(messageReference.get());
		Assert.assertEquals(302, statusReference.get());

		// Finish response after commit

		fromMetaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		fromMetaInfoCacheServletResponse.sendRedirect("testURL");

		toMetaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		fromMetaInfoCacheServletResponse.setResponse(
			toMetaInfoCacheServletResponse);

		locationReference.set(null);

		toMetaInfoCacheServletResponse.flushBuffer();

		fromMetaInfoCacheServletResponse.finishResponse();

		Assert.assertNull(locationReference.get());
	}

	@Test
	public void testFlushBuffer() throws IOException {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Assert.assertFalse(metaInfoCacheServletResponse.isCommitted());

		metaInfoCacheServletResponse.flushBuffer();

		Assert.assertTrue(metaInfoCacheServletResponse.isCommitted());
	}

	@SuppressWarnings("cast")
	@Test
	public void testGetMetaInfoDataBag() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse();

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		MetaInfoCacheServletResponse.MetaData metaData =
			metaInfoCacheServletResponse.getMetaData();

		Assert.assertNotNull(metaData);
		Assert.assertTrue(metaData instanceof Serializable);
	}

	@Test
	public void testGetOutputStream() throws IOException {
		final AtomicBoolean calledGetOutputStreamReference =
			new AtomicBoolean();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public ServletOutputStream getOutputStream() {
					calledGetOutputStreamReference.set(true);

					return null;
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Assert.assertFalse(metaInfoCacheServletResponse.calledGetOutputStream);

		Assert.assertFalse(calledGetOutputStreamReference.get());

		metaInfoCacheServletResponse.getOutputStream();

		Assert.assertTrue(metaInfoCacheServletResponse.calledGetOutputStream);
		Assert.assertTrue(calledGetOutputStreamReference.get());
	}

	@Test
	public void testGetSetBufferSize() throws IOException {
		final AtomicInteger bufferSizeReference = new AtomicInteger();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setBufferSize(int bufferSzie) {
					bufferSizeReference.set(bufferSzie);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Get default

		Assert.assertEquals(0, metaInfoCacheServletResponse.getBufferSize());

		// Normal set

		metaInfoCacheServletResponse.setBufferSize(1024);

		Assert.assertEquals(1024, metaInfoCacheServletResponse.getBufferSize());

		Assert.assertEquals(1024, bufferSizeReference.get());

		// Set after commit

		metaInfoCacheServletResponse.flushBuffer();

		try {
			metaInfoCacheServletResponse.setBufferSize(2048);
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testGetSetCharacterEncoding() throws IOException {
		final AtomicReference<String> characterEncodingReference =
			new AtomicReference<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public PrintWriter getWriter() {
					return null;
				}

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
					characterEncodingReference.set(characterEncoding);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Get default

		Assert.assertEquals(
			StringPool.DEFAULT_CHARSET_NAME,
			metaInfoCacheServletResponse.getCharacterEncoding());

		// Null set

		metaInfoCacheServletResponse.setCharacterEncoding(null);

		Assert.assertEquals(
			StringPool.DEFAULT_CHARSET_NAME,
			metaInfoCacheServletResponse.getCharacterEncoding());

		Assert.assertNull(characterEncodingReference.get());

		// Normal set

		metaInfoCacheServletResponse.setCharacterEncoding(
			StringPool.ISO_8859_1);

		Assert.assertEquals(
			StringPool.ISO_8859_1,
			metaInfoCacheServletResponse.getCharacterEncoding());
		Assert.assertEquals(
			StringPool.ISO_8859_1, characterEncodingReference.get());

		characterEncodingReference.set(null);

		// Set after get writer

		metaInfoCacheServletResponse.getWriter();

		metaInfoCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		Assert.assertEquals(
			StringPool.ISO_8859_1,
			metaInfoCacheServletResponse.getCharacterEncoding());
		Assert.assertNull(characterEncodingReference.get());

		// Set after commit

		metaInfoCacheServletResponse.flushBuffer();

		metaInfoCacheServletResponse.setCharacterEncoding(StringPool.UTF8);

		Assert.assertEquals(
			StringPool.ISO_8859_1,
			metaInfoCacheServletResponse.getCharacterEncoding());
		Assert.assertNull(characterEncodingReference.get());
	}

	@Test
	public void testGetSetContentType() throws IOException {
		final AtomicReference<String> characterEncodingReference =
			new AtomicReference<>();
		final AtomicReference<String> contentTypeReference =
			new AtomicReference<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setCharacterEncoding(String characterEncoding) {
					characterEncodingReference.set(characterEncoding);
				}

				@Override
				public void setContentType(String contentType) {
					contentTypeReference.set(contentType);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Get default

		Assert.assertNull(metaInfoCacheServletResponse.getContentType());

		// Null set

		metaInfoCacheServletResponse.setContentType(null);

		Assert.assertNull(metaInfoCacheServletResponse.getContentType());

		Assert.assertNull(contentTypeReference.get());

		// Set with character encoding

		metaInfoCacheServletResponse.setContentType(
			ContentTypes.TEXT_HTML_UTF8);

		Assert.assertEquals(
			StringPool.UTF8,
			metaInfoCacheServletResponse.getCharacterEncoding());
		Assert.assertEquals(StringPool.UTF8, characterEncodingReference.get());
		Assert.assertEquals(
			ContentTypes.TEXT_HTML_UTF8,
			metaInfoCacheServletResponse.getContentType());

		characterEncodingReference.set(null);

		// Set without character encoding

		metaInfoCacheServletResponse.setContentType(ContentTypes.TEXT_HTML);

		Assert.assertEquals(
			StringPool.DEFAULT_CHARSET_NAME,
			metaInfoCacheServletResponse.getCharacterEncoding());
		Assert.assertNull(characterEncodingReference.get());
		Assert.assertEquals(
			ContentTypes.TEXT_HTML,
			metaInfoCacheServletResponse.getContentType());
		Assert.assertEquals(ContentTypes.TEXT_HTML, contentTypeReference.get());

		// Set with broken character encoding

		metaInfoCacheServletResponse.setContentType(
			"text/html; charset0=UTF-8");

		Assert.assertEquals(
			StringPool.DEFAULT_CHARSET_NAME,
			metaInfoCacheServletResponse.getCharacterEncoding());
		Assert.assertNull(characterEncodingReference.get());
		Assert.assertEquals(
			ContentTypes.TEXT_HTML,
			metaInfoCacheServletResponse.getContentType());
		Assert.assertEquals(
			"text/html; charset0=UTF-8", contentTypeReference.get());

		characterEncodingReference.set(null);
		contentTypeReference.set(null);

		// Set after commit

		metaInfoCacheServletResponse.flushBuffer();

		metaInfoCacheServletResponse.setContentType(
			ContentTypes.TEXT_HTML_UTF8);

		Assert.assertEquals(
			StringPool.DEFAULT_CHARSET_NAME,
			metaInfoCacheServletResponse.getCharacterEncoding());
		Assert.assertEquals(
			ContentTypes.TEXT_HTML,
			metaInfoCacheServletResponse.getContentType());
		Assert.assertNull(characterEncodingReference.get());
		Assert.assertNull(contentTypeReference.get());
	}

	@Test
	public void testGetSetLocale() throws IOException {
		final AtomicReference<Locale> localeReference = new AtomicReference<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setLocale(Locale locale) {
					localeReference.set(locale);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Normal set

		metaInfoCacheServletResponse.setLocale(LocaleUtil.US);

		Assert.assertEquals(
			LocaleUtil.US, metaInfoCacheServletResponse.getLocale());

		Assert.assertEquals(LocaleUtil.US, localeReference.get());

		localeReference.set(null);

		// Set after commit

		metaInfoCacheServletResponse.flushBuffer();

		metaInfoCacheServletResponse.setLocale(LocaleUtil.FRENCH);

		Assert.assertEquals(
			LocaleUtil.US, metaInfoCacheServletResponse.getLocale());
		Assert.assertNull(localeReference.get());
	}

	@Test
	public void testGetWriter() throws IOException {
		final AtomicBoolean calledGetWriter = new AtomicBoolean();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public PrintWriter getWriter() {
					calledGetWriter.set(true);

					return null;
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Assert.assertFalse(metaInfoCacheServletResponse.calledGetWriter);

		Assert.assertFalse(calledGetWriter.get());

		metaInfoCacheServletResponse.getWriter();

		Assert.assertTrue(metaInfoCacheServletResponse.calledGetWriter);
		Assert.assertTrue(calledGetWriter.get());
	}

	@Test
	public void testIsCommitted() throws IOException {
		final AtomicBoolean committed = new AtomicBoolean();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return committed.get();
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Assert.assertFalse(metaInfoCacheServletResponse.isCommitted());

		// Commit wrapped instance

		committed.set(true);

		Assert.assertTrue(metaInfoCacheServletResponse.isCommitted());

		committed.set(false);

		// Commit wrapper

		metaInfoCacheServletResponse.flushBuffer();

		Assert.assertTrue(metaInfoCacheServletResponse.isCommitted());

		// Commit both

		committed.set(true);

		Assert.assertTrue(metaInfoCacheServletResponse.isCommitted());
	}

	@Test
	public void testReset() throws IOException {
		final AtomicBoolean calledResetReference = new AtomicBoolean();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void reset() {
					calledResetReference.set(true);
				}

				@Override
				public void resetBuffer() {
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Normal reset buffer

		metaInfoCacheServletResponse.reset();

		Assert.assertTrue(calledResetReference.get());

		// Reset buffer after commit

		metaInfoCacheServletResponse.flushBuffer();

		try {
			metaInfoCacheServletResponse.reset();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testResetBuffer() throws IOException {
		final AtomicBoolean calledResetBufferReference = new AtomicBoolean();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void resetBuffer() {
					calledResetBufferReference.set(true);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Normal reset buffer

		metaInfoCacheServletResponse.resetBuffer();

		Assert.assertTrue(calledResetBufferReference.get());

		// Reset buffer after commit

		metaInfoCacheServletResponse.flushBuffer();

		try {
			metaInfoCacheServletResponse.resetBuffer();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testSendError() throws IOException {
		final AtomicReference<String> messageReference =
			new AtomicReference<>();
		final AtomicInteger statusReference = new AtomicInteger();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void resetBuffer() {
				}

				@Override
				public void sendError(int status) {
					statusReference.set(status);
				}

				@Override
				public void sendError(int status, String message) {
					statusReference.set(status);
					messageReference.set(message);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Set status and message

		metaInfoCacheServletResponse.sendError(400, "Bad Page");

		Assert.assertEquals("Bad Page", messageReference.get());
		Assert.assertTrue(metaInfoCacheServletResponse.isCommitted());
		Assert.assertEquals(400, metaInfoCacheServletResponse.getStatus());
		Assert.assertEquals(400, statusReference.get());

		messageReference.set(null);
		statusReference.set(0);

		// Set status

		metaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		metaInfoCacheServletResponse.sendError(404);

		Assert.assertNull(messageReference.get());
		Assert.assertTrue(metaInfoCacheServletResponse.isCommitted());
		Assert.assertEquals(404, metaInfoCacheServletResponse.getStatus());
		Assert.assertEquals(404, statusReference.get());

		messageReference.set(null);
		statusReference.set(0);

		// Set status and message after commit

		metaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		metaInfoCacheServletResponse.flushBuffer();

		try {
			metaInfoCacheServletResponse.sendError(500, "After commit");

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}

		// Set status after commit

		metaInfoCacheServletResponse = new MetaInfoCacheServletResponse(
			stubHttpServletResponse);

		metaInfoCacheServletResponse.flushBuffer();

		try {
			metaInfoCacheServletResponse.sendError(500);

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testSendRedirect() throws IOException {
		final AtomicReference<String> locationReference =
			new AtomicReference<>();
		final AtomicReference<String> messageReference =
			new AtomicReference<>();
		final AtomicInteger statusReference = new AtomicInteger();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void resetBuffer() {
				}

				@Override
				public void sendRedirect(String location) {
					locationReference.set(location);
				}

				@Override
				public void setStatus(int status) {
					statusReference.set(status);
				}

				/**
				 * @deprecated As of Judson (7.1.x)
				 */
				@Deprecated
				@Override
				public void setStatus(int status, String message) {
					statusReference.set(status);
					messageReference.set(message);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Normal send

		metaInfoCacheServletResponse.sendRedirect("testURL");

		Assert.assertEquals("testURL", locationReference.get());

		// Send after commit

		metaInfoCacheServletResponse.flushBuffer();

		try {
			metaInfoCacheServletResponse.sendRedirect("testURL");

			Assert.fail();
		}
		catch (IllegalStateException illegalStateException) {
		}
	}

	@Test
	public void testSetContentLength() throws IOException {
		final AtomicLong contentLengthReference = new AtomicLong();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setContentLength(int i) {
					contentLengthReference.set(i);
				}

				public void setContentLengthLong(long l) {
					contentLengthReference.set(l);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Normal set

		metaInfoCacheServletResponse.setContentLength(1024);

		Assert.assertEquals(1024, contentLengthReference.get());

		contentLengthReference.set(0);

		// set after commit

		metaInfoCacheServletResponse.flushBuffer();

		metaInfoCacheServletResponse.setContentLength(2048);

		Assert.assertEquals(0, contentLengthReference.get());
	}

	@Test
	public void testSetContentLengthLong() throws IOException {
		final AtomicLong contentLengthReference = new AtomicLong();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setContentLength(int i) {
					contentLengthReference.set(i);
				}

				public void setContentLengthLong(long l) {
					contentLengthReference.set(l);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Normal set

		metaInfoCacheServletResponse.setContentLengthLong(1024);

		Assert.assertEquals(1024, contentLengthReference.get());

		contentLengthReference.set(0);

		// set after commit

		metaInfoCacheServletResponse.flushBuffer();

		metaInfoCacheServletResponse.setContentLengthLong(2048);

		Assert.assertEquals(0, contentLengthReference.get());
	}

	@Test
	public void testSetDateHeader() {
		final List<ObjectValuePair<String, Long>> objectValuePairs =
			new ArrayList<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public void setDateHeader(String name, long value) {
					objectValuePairs.add(
						new ObjectValuePair<String, Long>(name, value));
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Map<String, Set<Header>> headers =
			metaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headers.toString(), 0, headers.size());

		// First set

		metaInfoCacheServletResponse.setDateHeader("date1", 1);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("date1"));

		Set<Header> dateHeaders1 = headers.get("date1");

		Assert.assertEquals(dateHeaders1.toString(), 1, dateHeaders1.size());
		Assert.assertTrue(
			dateHeaders1.toString(), dateHeaders1.contains(new Header(1L)));

		Assert.assertEquals(
			objectValuePairs.toString(), 1, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Long>("date1", 1L),
			objectValuePairs.get(0));

		// Second set

		metaInfoCacheServletResponse.setDateHeader("date1", 2);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("date1"));

		dateHeaders1 = headers.get("date1");

		Assert.assertEquals(dateHeaders1.toString(), 1, dateHeaders1.size());
		Assert.assertTrue(
			dateHeaders1.toString(), dateHeaders1.contains(new Header(2L)));

		Assert.assertEquals(
			objectValuePairs.toString(), 2, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Long>("date1", 2L),
			objectValuePairs.get(1));

		// Third set

		metaInfoCacheServletResponse.setDateHeader("date2", 1);

		Assert.assertEquals(headers.toString(), 2, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("date2"));

		Set<Header> dateHeaders2 = headers.get("date2");

		Assert.assertEquals(dateHeaders2.toString(), 1, dateHeaders2.size());
		Assert.assertTrue(
			dateHeaders2.toString(), dateHeaders2.contains(new Header(1L)));

		Assert.assertEquals(
			objectValuePairs.toString(), 3, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Long>("date2", 1L),
			objectValuePairs.get(2));
	}

	@Test
	public void testSetGetStatus() throws IOException {
		final AtomicReference<String> messageReference =
			new AtomicReference<>();
		final AtomicInteger statusReference = new AtomicInteger();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setStatus(int status) {
					statusReference.set(status);
				}

				/**
				 * @deprecated As of Judson (7.1.x)
				 */
				@Deprecated
				@Override
				public void setStatus(int status, String message) {
					statusReference.set(status);
					messageReference.set(message);
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		// Set status and message

		metaInfoCacheServletResponse.setStatus(400, "Bad Page");

		Assert.assertNull(messageReference.get());
		Assert.assertEquals(400, metaInfoCacheServletResponse.getStatus());
		Assert.assertEquals(400, statusReference.get());

		messageReference.set(null);
		statusReference.set(0);

		// Set status

		metaInfoCacheServletResponse.setStatus(404);

		Assert.assertNull(messageReference.get());
		Assert.assertEquals(404, metaInfoCacheServletResponse.getStatus());
		Assert.assertEquals(404, statusReference.get());

		messageReference.set(null);
		statusReference.set(0);

		// Set status and message after commit

		metaInfoCacheServletResponse.flushBuffer();

		metaInfoCacheServletResponse.setStatus(500, "After commit");

		Assert.assertNull(messageReference.get());
		Assert.assertEquals(404, metaInfoCacheServletResponse.getStatus());
		Assert.assertEquals(0, statusReference.get());

		// Set status after commit

		metaInfoCacheServletResponse.flushBuffer();

		metaInfoCacheServletResponse.setStatus(500);

		Assert.assertNull(messageReference.get());
		Assert.assertEquals(404, metaInfoCacheServletResponse.getStatus());
		Assert.assertEquals(0, statusReference.get());
	}

	@Test
	public void testSetHeader() {
		final AtomicReference<String> contentTypeReference =
			new AtomicReference<>();
		final List<ObjectValuePair<String, String>> objectValuePairs =
			new ArrayList<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

				@Override
				public void setContentType(String contentType) {
					contentTypeReference.set(contentType);
				}

				@Override
				public void setHeader(String name, String value) {
					objectValuePairs.add(
						new ObjectValuePair<String, String>(name, value));
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Map<String, Set<Header>> headersMap =
			metaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headersMap.toString(), 0, headersMap.size());

		// Set content type

		metaInfoCacheServletResponse.setHeader(
			HttpHeaders.CONTENT_TYPE, ContentTypes.TEXT_HTML);

		Assert.assertEquals(headersMap.toString(), 0, headersMap.size());
		Assert.assertEquals(
			ContentTypes.TEXT_HTML,
			metaInfoCacheServletResponse.getContentType());
		Assert.assertEquals(ContentTypes.TEXT_HTML, contentTypeReference.get());

		// First set

		metaInfoCacheServletResponse.setHeader("name1", "value1");

		Assert.assertEquals(headersMap.toString(), 1, headersMap.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name1"));

		Set<Header> headers1 = headersMap.get("name1");

		Assert.assertEquals(headers1.toString(), 1, headers1.size());
		Assert.assertTrue(
			headers1.toString(), headers1.contains(new Header("value1")));

		Assert.assertEquals(
			objectValuePairs.toString(), 1, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, String>("name1", "value1"),
			objectValuePairs.get(0));

		// Second add

		metaInfoCacheServletResponse.setHeader("name1", "value2");

		Assert.assertEquals(headersMap.toString(), 1, headersMap.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name1"));

		headers1 = headersMap.get("name1");

		Assert.assertEquals(headers1.toString(), 1, headers1.size());
		Assert.assertTrue(
			headers1.toString(), headers1.contains(new Header("value2")));

		Assert.assertEquals(
			objectValuePairs.toString(), 2, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, String>("name1", "value2"),
			objectValuePairs.get(1));

		// Third add

		metaInfoCacheServletResponse.setHeader("name2", "value1");

		Assert.assertEquals(headersMap.toString(), 2, headersMap.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name2"));

		Set<Header> headers2 = headersMap.get("name2");

		Assert.assertEquals(headers2.toString(), 1, headers2.size());
		Assert.assertTrue(
			headers2.toString(), headers2.contains(new Header("value1")));

		Assert.assertEquals(
			objectValuePairs.toString(), 3, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, String>("name2", "value1"),
			objectValuePairs.get(2));
	}

	@Test
	public void testSetIntHeader() {
		final List<ObjectValuePair<String, Integer>> objectValuePairs =
			new ArrayList<>();

		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public void setIntHeader(String name, int value) {
					objectValuePairs.add(
						new ObjectValuePair<String, Integer>(name, value));
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Map<String, Set<Header>> headers =
			metaInfoCacheServletResponse.getHeaders();

		Assert.assertEquals(headers.toString(), 0, headers.size());

		// First set

		metaInfoCacheServletResponse.setIntHeader("name1", 1);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name1"));

		Set<Header> intHeaders1 = headers.get("name1");

		Assert.assertEquals(intHeaders1.toString(), 1, intHeaders1.size());
		Assert.assertTrue(
			intHeaders1.toString(), intHeaders1.contains(new Header(1)));

		Assert.assertEquals(
			objectValuePairs.toString(), 1, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Integer>("name1", 1),
			objectValuePairs.get(0));

		// Second set

		metaInfoCacheServletResponse.setIntHeader("name1", 2);

		Assert.assertEquals(headers.toString(), 1, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name1"));

		intHeaders1 = headers.get("name1");

		Assert.assertEquals(intHeaders1.toString(), 1, intHeaders1.size());
		Assert.assertTrue(
			intHeaders1.toString(), intHeaders1.contains(new Header(2)));

		Assert.assertEquals(
			objectValuePairs.toString(), 2, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Integer>("name1", 2),
			objectValuePairs.get(1));

		// Third set

		metaInfoCacheServletResponse.setIntHeader("name2", 1);

		Assert.assertEquals(headers.toString(), 2, headers.size());
		Assert.assertTrue(metaInfoCacheServletResponse.containsHeader("name2"));

		Set<Header> intHeaders2 = headers.get("name2");

		Assert.assertEquals(intHeaders2.toString(), 1, intHeaders2.size());
		Assert.assertTrue(
			intHeaders2.toString(), intHeaders2.contains(new Header(1)));

		Assert.assertEquals(
			objectValuePairs.toString(), 3, objectValuePairs.size());
		Assert.assertEquals(
			new ObjectValuePair<String, Integer>("name2", 1),
			objectValuePairs.get(2));
	}

	@Test
	public void testToString() {
		StubHttpServletResponse stubHttpServletResponse =
			new StubHttpServletResponse() {

				@Override
				public boolean isCommitted() {
					return false;
				}

			};

		MetaInfoCacheServletResponse metaInfoCacheServletResponse =
			new MetaInfoCacheServletResponse(stubHttpServletResponse);

		Assert.assertEquals(
			StringBundler.concat(
				"{bufferSize=0, charsetName=null, committed=false, ",
				"contentLength=-1, contentType=null, error=false, ",
				"errorMessage=null, headers={}, location=null, locale=null, ",
				"status=200}"),
			metaInfoCacheServletResponse.toString());
	}

}