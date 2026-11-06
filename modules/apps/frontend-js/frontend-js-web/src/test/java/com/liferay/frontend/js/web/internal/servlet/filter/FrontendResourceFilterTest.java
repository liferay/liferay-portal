/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.servlet.filter;

import com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration;
import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.test.util.FrontendJSWebTestUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
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
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Iván Zaera Avellón
 */
public class FrontendResourceFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ToolDependencies.wireCaches();
	}

	@Test
	public void testContent() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _send(
			null, RandomTestUtil.randomString(), "use-system-defaults", true,
			false, true, Deflater.NO_COMPRESSION);

		_assertContent(false, mockHttpServletResponse);

		String contentType = mockHttpServletResponse.getHeader("Content-Type");

		Assert.assertTrue(contentType.contains(_CONTENT_TYPE));
	}

	@Test
	public void testContentWhenAcceptEncodingIsNotGzip() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _send(
			"identity", RandomTestUtil.randomString(),
			"use-best-speed-compression", true, false, true,
			Deflater.NO_COMPRESSION);

		_assertContent(false, mockHttpServletResponse);

		Assert.assertNull(
			mockHttpServletResponse.getHeader("Content-Encoding"));
		Assert.assertEquals(
			"Accept-Encoding", mockHttpServletResponse.getHeader("Vary"));
	}

	@Test
	public void testContentWhenCompressionStrategyIsDoNotCompress()
		throws Exception {

		MockHttpServletResponse mockHttpServletResponse = _send(
			"gzip, deflate", RandomTestUtil.randomString(), "do-not-compress",
			true, false, true, Deflater.NO_COMPRESSION);

		_assertContent(false, mockHttpServletResponse);

		Assert.assertNull(
			mockHttpServletResponse.getHeader("Content-Encoding"));
		Assert.assertNull(mockHttpServletResponse.getHeader("Vary"));
	}

	@Test
	public void testContentWhenCompressionStrategyIsUseCompression()
		throws Exception {

		for (String compressionStrategy :
				new String[] {
					"use-best-speed-compression",
					"use-intermediate-compression", "use-maximum-compression"
				}) {

			MockHttpServletResponse mockHttpServletResponse = _send(
				"gzip, deflate", RandomTestUtil.randomString(),
				compressionStrategy, true, false, true,
				Deflater.NO_COMPRESSION);

			_assertContent(true, mockHttpServletResponse);

			Assert.assertEquals(
				"gzip", mockHttpServletResponse.getHeader("Content-Encoding"));

			String contentType = mockHttpServletResponse.getHeader(
				"Content-Type");

			Assert.assertTrue(contentType.contains(_CONTENT_TYPE));

			Assert.assertEquals(
				"Accept-Encoding", mockHttpServletResponse.getHeader("Vary"));
		}
	}

	@Test
	public void testContentWhenCompressionStrategyIsUseSystemDefaults()
		throws Exception {

		// System defaults disable compression

		MockHttpServletResponse mockHttpServletResponse = _send(
			"gzip, deflate", RandomTestUtil.randomString(),
			"use-system-defaults", true, false, true, Deflater.NO_COMPRESSION);

		_assertContent(false, mockHttpServletResponse);

		Assert.assertNull(
			mockHttpServletResponse.getHeader("Content-Encoding"));
		Assert.assertNull(mockHttpServletResponse.getHeader("Vary"));

		// System defaults enable compression

		mockHttpServletResponse = _send(
			"gzip, deflate", RandomTestUtil.randomString(),
			"use-system-defaults", true, false, true, Deflater.BEST_SPEED);

		_assertContent(true, mockHttpServletResponse);

		Assert.assertEquals(
			"gzip", mockHttpServletResponse.getHeader("Content-Encoding"));

		String contentType = mockHttpServletResponse.getHeader("Content-Type");

		Assert.assertTrue(contentType.contains(_CONTENT_TYPE));

		Assert.assertEquals(
			"Accept-Encoding", mockHttpServletResponse.getHeader("Vary"));
	}

	@Test
	public void testETag() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _send(
			null, RandomTestUtil.randomString(), "use-system-defaults", true,
			false, true, Deflater.NO_COMPRESSION);

		Assert.assertEquals(_E_TAG, mockHttpServletResponse.getHeader("ETag"));
	}

	@Test
	public void testGZipCache() throws Exception {
		String cacheKey = RandomTestUtil.randomString();

		_send(
			"gzip, deflate", cacheKey, "use-best-speed-compression", true,
			false, true, Deflater.NO_COMPRESSION);

		_send(
			"gzip, deflate", cacheKey, "use-best-speed-compression", true,
			false, true, Deflater.NO_COMPRESSION);

		Assert.assertEquals(1, _inputStreamsCount.get());
	}

	@Test
	public void testGZipCacheWhenCacheKeyChanges() throws Exception {
		_send(
			"gzip, deflate", RandomTestUtil.randomString(),
			"use-best-speed-compression", true, false, true,
			Deflater.NO_COMPRESSION);

		_send(
			"gzip, deflate", RandomTestUtil.randomString(),
			"use-best-speed-compression", true, false, true,
			Deflater.NO_COMPRESSION);

		Assert.assertEquals(2, _inputStreamsCount.get());
	}

	@Test
	public void testGZipCacheWhenCompressionLevelChanges() throws Exception {
		String cacheKey = RandomTestUtil.randomString();

		_send(
			"gzip, deflate", cacheKey, "use-best-speed-compression", true,
			false, true, Deflater.NO_COMPRESSION);

		_send(
			"gzip, deflate", cacheKey, "use-maximum-compression", true, false,
			true, Deflater.NO_COMPRESSION);

		Assert.assertEquals(2, _inputStreamsCount.get());
	}

	@Test
	public void testGZipCacheWhenFrontendResourceHasNoCacheKey()
		throws Exception {

		_send(
			"gzip, deflate", null, "use-best-speed-compression", true, false,
			true, Deflater.NO_COMPRESSION);

		_send(
			"gzip, deflate", null, "use-best-speed-compression", true, false,
			true, Deflater.NO_COMPRESSION);

		Assert.assertEquals(2, _inputStreamsCount.get());
	}

	@Test
	public void testImmutable() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _send(
			null, RandomTestUtil.randomString(), "use-system-defaults", true,
			false, true, Deflater.NO_COMPRESSION);

		String cacheControl = mockHttpServletResponse.getHeader(
			"Cache-Control");

		Assert.assertTrue(cacheControl.contains("immutable"));
		Assert.assertTrue(cacheControl.contains("max-age=31536000"));
		Assert.assertTrue(cacheControl.contains("public"));
	}

	@Test
	public void testMaxAge() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _send(
			null, RandomTestUtil.randomString(), "use-system-defaults", false,
			false, true, Deflater.NO_COMPRESSION);

		String cacheControl = mockHttpServletResponse.getHeader(
			"Cache-Control");

		Assert.assertTrue(cacheControl.contains("max-age=" + _MAX_AGE));
	}

	@Test
	public void testPrivate() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _send(
			null, RandomTestUtil.randomString(), "use-system-defaults", false,
			true, true, Deflater.NO_COMPRESSION);

		String cacheControl = mockHttpServletResponse.getHeader(
			"Cache-Control");

		Assert.assertTrue(cacheControl.contains("private"));
	}

	@Test
	public void testSendNoCache() throws Exception {
		MockHttpServletResponse mockHttpServletResponse = _send(
			null, RandomTestUtil.randomString(), "use-system-defaults", false,
			false, false, Deflater.NO_COMPRESSION);

		String cacheControl = mockHttpServletResponse.getHeader(
			"Cache-Control");

		Assert.assertTrue(cacheControl.contains("must-revalidate"));

		mockHttpServletResponse = _send(
			null, RandomTestUtil.randomString(), "use-system-defaults", false,
			false, true, Deflater.NO_COMPRESSION);

		cacheControl = mockHttpServletResponse.getHeader("Cache-Control");

		Assert.assertTrue(cacheControl.contains("no-cache"));
	}

	private void _assertContent(
			boolean gzipContent,
			MockHttpServletResponse mockHttpServletResponse)
		throws Exception {

		String content;

		if (gzipContent) {
			try (GZIPInputStream gzipInputStream = new GZIPInputStream(
					new ByteArrayInputStream(
						mockHttpServletResponse.getContentAsByteArray()))) {

				content = new String(
					StreamUtil.toByteArray(gzipInputStream),
					StandardCharsets.UTF_8);
			}
		}
		else {
			content = mockHttpServletResponse.getContentAsString();
		}

		Assert.assertEquals(_CONTENT, content);
	}

	private FrontendResourceFilter _createFrontendResourceFilter(
			String compressionStrategy, int systemCompressionLevel)
		throws Exception {

		FrontendResourceFilter frontendResourceFilter =
			new FrontendResourceFilter();

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		FrontendCachingConfiguration frontendCachingConfiguration =
			Mockito.mock(FrontendCachingConfiguration.class);

		Mockito.when(
			frontendCachingConfiguration.compressionStrategy()
		).thenReturn(
			compressionStrategy
		);

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				Mockito.eq(FrontendCachingConfiguration.class),
				Mockito.anyLong())
		).thenReturn(
			frontendCachingConfiguration
		);

		ReflectionTestUtil.setFieldValue(
			frontendResourceFilter, "_configurationProvider",
			configurationProvider);

		ReflectionTestUtil.setFieldValue(
			frontendResourceFilter, "_gZipBytesPortalCache",
			PortalCacheHelperUtil.getPortalCache(
				PortalCacheManagerNames.SINGLE_VM,
				FrontendResourceFilterTest.class.getName()));
		ReflectionTestUtil.setFieldValue(
			frontendResourceFilter, "_systemCompressionLevel",
			systemCompressionLevel);

		return frontendResourceFilter;
	}

	private FrontendResource _mockFrontendResource(
		String cacheKey, boolean immutable, boolean privateResource,
		boolean sendNoCache) {

		return new FrontendResource() {

			@Override
			public String getCacheKey() {
				return cacheKey;
			}

			@Override
			public String getContentType() {
				return _CONTENT_TYPE;
			}

			@Override
			public String getETag() {
				return _E_TAG;
			}

			@Override
			public InputStream getInputStream() {
				_inputStreamsCount.incrementAndGet();

				return new ByteArrayInputStream(
					_CONTENT.getBytes(StandardCharsets.UTF_8));
			}

			@Override
			public long getMaxAge() {
				return _MAX_AGE;
			}

			@Override
			public boolean isImmutable() {
				return immutable;
			}

			@Override
			public boolean isPrivate() {
				return privateResource;
			}

			@Override
			public boolean isSendNoCache() {
				return sendNoCache;
			}

		};
	}

	private MockHttpServletResponse _send(
			String acceptEncoding, String cacheKey, String compressionStrategy,
			boolean immutable, boolean privateResource, boolean sendNoCache,
			int systemCompressionLevel)
		throws Exception {

		FrontendResourceFilter frontendResourceFilter =
			_createFrontendResourceFilter(
				compressionStrategy, systemCompressionLevel);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		if (acceptEncoding != null) {
			mockHttpServletRequest.addHeader("Accept-Encoding", acceptEncoding);
		}

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		frontendResourceFilter.send(
			_mockFrontendResource(
				cacheKey, immutable, privateResource, sendNoCache),
			mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private static final String _CONTENT = RandomTestUtil.randomString();

	private static final String _CONTENT_TYPE = "application/javascript";

	private static final String _E_TAG =
		FrontendJSWebTestUtil.randomHashedFileHash();

	private static final long _MAX_AGE = RandomTestUtil.randomLong();

	private final AtomicInteger _inputStreamsCount = new AtomicInteger();

}