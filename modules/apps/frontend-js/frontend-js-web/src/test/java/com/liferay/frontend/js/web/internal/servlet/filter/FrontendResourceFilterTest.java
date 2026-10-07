/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.filter;

import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.test.util.FrontendJSWebTestUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.ToolDependencies;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Iván Zaera Avellón
 */
public class FrontendResourceFilterTest {

	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		ToolDependencies.wireCaches();
	}

	@Test
	public void testContent() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		frontendResourceFilter.send(
			_mockFrontendResource(
				FrontendJSWebTestUtil.randomHashedFileHash(), true,
				RandomTestUtil.randomLong(), false, true),
			new MockHttpServletRequest(), mockHttpServletResponse);

		String contentType = mockHttpServletResponse.getHeader("Content-Type");

		Assert.assertTrue(contentType.contains(_CONTENT_TYPE));

		Assert.assertEquals(
			"import React from 'react';",
			mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testETag() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		String eTag = FrontendJSWebTestUtil.randomHashedFileHash();

		frontendResourceFilter.send(
			_mockFrontendResource(
				eTag, true, RandomTestUtil.randomLong(), false, true),
			new MockHttpServletRequest(), mockHttpServletResponse);

		Assert.assertEquals(eTag, mockHttpServletResponse.getHeader("ETag"));
	}

	@Test
	public void testGZip() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();

		_enableGZip(frontendResourceFilter);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader("Accept-Encoding", "gzip, deflate");

		FrontendResource frontendResource = _mockFrontendResource(
			FrontendJSWebTestUtil.randomHashedFileHash(), true,
			RandomTestUtil.randomLong(), false, true);
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		frontendResourceFilter.send(
			frontendResource, mockHttpServletRequest, mockHttpServletResponse);

		byte[] bytes = mockHttpServletResponse.getContentAsByteArray();

		Assert.assertEquals(
			bytes.length, mockHttpServletResponse.getContentLength());

		Assert.assertEquals(
			"gzip", mockHttpServletResponse.getHeader("Content-Encoding"));
		Assert.assertEquals(
			"Accept-Encoding", mockHttpServletResponse.getHeader("Vary"));

		try (GZIPInputStream gzipInputStream = new GZIPInputStream(
				new ByteArrayInputStream(bytes))) {

			Assert.assertEquals(
				_CONTENT,
				new String(
					StreamUtil.toByteArray(gzipInputStream),
					StandardCharsets.UTF_8));
		}

		mockHttpServletResponse = new MockHttpServletResponse();

		frontendResourceFilter.send(
			frontendResource, mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertArrayEquals(
			bytes, mockHttpServletResponse.getContentAsByteArray());

		Assert.assertEquals(1, _inputStreamCount.get());
	}

	@Test
	public void testGZipDisabled() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader("Accept-Encoding", "gzip");

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		frontendResourceFilter.send(
			_mockFrontendResource(
				FrontendJSWebTestUtil.randomHashedFileHash(), true,
				RandomTestUtil.randomLong(), false, true),
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertNull(
			mockHttpServletResponse.getHeader("Content-Encoding"));
		Assert.assertNull(mockHttpServletResponse.getHeader("Vary"));
		Assert.assertEquals(
			_CONTENT, mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testGZipNotAccepted() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();

		_enableGZip(frontendResourceFilter);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		frontendResourceFilter.send(
			_mockFrontendResource(
				FrontendJSWebTestUtil.randomHashedFileHash(), true,
				RandomTestUtil.randomLong(), false, true),
			new MockHttpServletRequest(), mockHttpServletResponse);

		Assert.assertNull(
			mockHttpServletResponse.getHeader("Content-Encoding"));
		Assert.assertEquals(
			"Accept-Encoding", mockHttpServletResponse.getHeader("Vary"));
		Assert.assertEquals(
			_CONTENT, mockHttpServletResponse.getContentAsString());
	}

	@Test
	public void testGZipNotCachedWithoutETag() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();

		_enableGZip(frontendResourceFilter);

		FrontendResource frontendResource = _mockFrontendResource(
			null, false, RandomTestUtil.randomLong(), false, true);

		for (int i = 0; i < 2; i++) {
			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.addHeader("Accept-Encoding", "gzip");

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			frontendResourceFilter.send(
				frontendResource, mockHttpServletRequest,
				mockHttpServletResponse);

			Assert.assertEquals(
				"gzip", mockHttpServletResponse.getHeader("Content-Encoding"));
		}

		Assert.assertEquals(2, _inputStreamCount.get());
	}

	@Test
	public void testImmutable() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		frontendResourceFilter.send(
			_mockFrontendResource(
				FrontendJSWebTestUtil.randomHashedFileHash(), true,
				RandomTestUtil.randomLong(), false, true),
			new MockHttpServletRequest(), mockHttpServletResponse);

		String cacheControl = mockHttpServletResponse.getHeader(
			"Cache-Control");

		Assert.assertTrue(cacheControl.contains("immutable"));
		Assert.assertTrue(cacheControl.contains("max-age=31536000"));
		Assert.assertTrue(cacheControl.contains("public"));
	}

	@Test
	public void testMaxAge() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		long maxAge = RandomTestUtil.randomLong();

		frontendResourceFilter.send(
			_mockFrontendResource(
				FrontendJSWebTestUtil.randomHashedFileHash(), false, maxAge,
				false, true),
			new MockHttpServletRequest(), mockHttpServletResponse);

		String cacheControl = mockHttpServletResponse.getHeader(
			"Cache-Control");

		Assert.assertTrue(cacheControl.contains("max-age=" + maxAge));
	}

	@Test
	public void testPrivate() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		frontendResourceFilter.send(
			_mockFrontendResource(
				FrontendJSWebTestUtil.randomHashedFileHash(), false,
				RandomTestUtil.randomLong(), true, true),
			new MockHttpServletRequest(), mockHttpServletResponse);

		String cacheControl = mockHttpServletResponse.getHeader(
			"Cache-Control");

		Assert.assertTrue(cacheControl.contains("private"));
	}

	@Test
	public void testSendNoCache() throws Exception {
		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		frontendResourceFilter.send(
			_mockFrontendResource(
				FrontendJSWebTestUtil.randomHashedFileHash(), false,
				RandomTestUtil.randomLong(), false, false),
			new MockHttpServletRequest(), mockHttpServletResponse);

		String cacheControl = mockHttpServletResponse.getHeader(
			"Cache-Control");

		Assert.assertTrue(cacheControl.contains("must-revalidate"));

		mockHttpServletResponse = new MockHttpServletResponse();

		frontendResourceFilter.send(
			_mockFrontendResource(
				FrontendJSWebTestUtil.randomHashedFileHash(), false,
				RandomTestUtil.randomLong(), false, true),
			new MockHttpServletRequest(), mockHttpServletResponse);

		cacheControl = mockHttpServletResponse.getHeader("Cache-Control");

		Assert.assertTrue(cacheControl.contains("no-cache"));
	}

	private void _enableGZip(FrontendResourceFilter frontendResourceFilter) {
		ReflectionTestUtil.setFieldValue(
			frontendResourceFilter, "_gZipBytesPortalCache",
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				FrontendResourceFilterTest.class.getName()));
		ReflectionTestUtil.setFieldValue(
			frontendResourceFilter, "_gZipEnabled", true);
	}

	private FrontendResource _mockFrontendResource(
		String eTag, boolean immutable, long maxAge, boolean isPrivate,
		boolean sendNoCache) {

		return new FrontendResource() {

			@Override
			public String getContentType() {
				return _CONTENT_TYPE;
			}

			@Override
			public String getETag() {
				return eTag;
			}

			@Override
			public InputStream getInputStream() {
				_inputStreamCount.incrementAndGet();

				return new ByteArrayInputStream(
					_CONTENT.getBytes(StandardCharsets.UTF_8));
			}

			@Override
			public long getMaxAge() {
				return maxAge;
			}

			@Override
			public boolean isImmutable() {
				return immutable;
			}

			@Override
			public boolean isPrivate() {
				return isPrivate;
			}

			@Override
			public boolean isSendNoCache() {
				return sendNoCache;
			}

		};
	}

	private static final String _CONTENT = "import React from 'react';";

	private static final String _CONTENT_TYPE = "application/javascript";

	private final AtomicInteger _inputStreamCount = new AtomicInteger();

}