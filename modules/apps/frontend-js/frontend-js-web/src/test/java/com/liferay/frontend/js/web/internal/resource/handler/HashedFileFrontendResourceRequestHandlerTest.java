/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.test.util.FrontendJSWebTestUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.settings.FallbackKeysSettingsUtil;
import com.liferay.portal.kernel.settings.Settings;
import com.liferay.portal.kernel.settings.SettingsLocator;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;

import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public class HashedFileFrontendResourceRequestHandlerTest {

	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_hashedFilePath = StringUtil.replace(
			_UNHASHED_FILE_PATH, ".js", ".(" + _HASH + ").js");
	}

	@After
	public void tearDown() {
		if (_fallbackKeysSettingsUtilMockedStatic != null) {
			_fallbackKeysSettingsUtilMockedStatic.close();

			_fallbackKeysSettingsUtilMockedStatic = null;
		}
	}

	@Ignore
	@Test
	public void testCanHandleRequest() throws Exception {

		// LPD-52709

		_mockFallbackKeysSettingsUtil(
			HashMapBuilder.<String, Object>put(
				"maxAgeKey", RandomTestUtil.randomLong()
			).put(
				"sendNoCacheKey", false
			).build());

		HashedFileFrontendResourceRequestHandler
			hashedFileFrontendResourceRequestHandler =
				new HashedFileFrontendResourceRequestHandler(
					ContentTypes.TEXT_JAVASCRIPT, ".js",
					_mockHashedFilesRegistry(true), RandomTestUtil.randomLong(),
					"maxAgeKey", Mockito.mock(Portal.class), false,
					"sendNoCacheKey");

		Assert.assertTrue(
			hashedFileFrontendResourceRequestHandler.canHandleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _UNHASHED_FILE_PATH)));

		Assert.assertTrue(
			hashedFileFrontendResourceRequestHandler.canHandleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _hashedFilePath)));

		Assert.assertFalse(
			hashedFileFrontendResourceRequestHandler.canHandleRequest(
				_mockHttpServletRequest("/nonsense/request/index.js")));
	}

	@Test
	public void testHandleRequestWithHash() throws Exception {
		_mockFallbackKeysSettingsUtil(
			HashMapBuilder.<String, Object>put(
				"maxAgeKey", RandomTestUtil.randomLong()
			).put(
				"sendNoCacheKey", false
			).build());

		HashedFileFrontendResourceRequestHandler
			hashedFileFrontendResourceRequestHandler =
				new HashedFileFrontendResourceRequestHandler(
					ContentTypes.TEXT_JAVASCRIPT, ".js",
					_mockHashedFilesRegistry(true), RandomTestUtil.randomLong(),
					"maxAgeKey", Mockito.mock(Portal.class), true,
					"sendNoCacheKey");

		FrontendResource frontendResource =
			hashedFileFrontendResourceRequestHandler.handleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _hashedFilePath));

		Assert.assertEquals(
			ContentTypes.TEXT_JAVASCRIPT, frontendResource.getContentType());
		Assert.assertEquals(_HASH, frontendResource.getETag());
		Assert.assertEquals(
			"export default x;",
			StreamUtil.toString(frontendResource.getInputStream()));
		Assert.assertEquals(31536000L, frontendResource.getMaxAge());
		Assert.assertTrue(frontendResource.isImmutable());
		Assert.assertFalse(frontendResource.isSendNoCache());
	}

	@Ignore
	@Test
	public void testHandleRequestWithNoConfiguration() throws Exception {

		// LPD-52709

		_mockFallbackKeysSettingsUtil(null);

		long maxAge = RandomTestUtil.randomLong();

		HashedFileFrontendResourceRequestHandler
			hashedFileFrontendResourceRequestHandler =
				new HashedFileFrontendResourceRequestHandler(
					ContentTypes.TEXT_JAVASCRIPT, ".js",
					_mockHashedFilesRegistry(true), maxAge, "maxAgeKey",
					Mockito.mock(Portal.class), true, "sendNoCacheKey");

		FrontendResource frontendResource =
			hashedFileFrontendResourceRequestHandler.handleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _UNHASHED_FILE_PATH));

		Assert.assertEquals(maxAge, frontendResource.getMaxAge());
		Assert.assertTrue(frontendResource.isSendNoCache());
	}

	@Test
	public void testHandleRequestWithoutHashForHashedFile() throws Exception {
		long maxAge = RandomTestUtil.randomLong();

		_mockFallbackKeysSettingsUtil(
			HashMapBuilder.<String, Object>put(
				"maxAgeKey", maxAge
			).put(
				"sendNoCacheKey", false
			).build());

		HashedFileFrontendResourceRequestHandler
			hashedFileFrontendResourceRequestHandler =
				new HashedFileFrontendResourceRequestHandler(
					ContentTypes.TEXT_JAVASCRIPT, ".js",
					_mockHashedFilesRegistry(true), RandomTestUtil.randomLong(),
					"maxAgeKey", Mockito.mock(Portal.class), true,
					"sendNoCacheKey");

		FrontendResource frontendResource =
			hashedFileFrontendResourceRequestHandler.handleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _UNHASHED_FILE_PATH));

		Assert.assertEquals(
			ContentTypes.TEXT_JAVASCRIPT, frontendResource.getContentType());
		Assert.assertEquals(_HASH, frontendResource.getETag());
		Assert.assertEquals(
			"export default x;",
			StreamUtil.toString(frontendResource.getInputStream()));
		Assert.assertEquals(maxAge, frontendResource.getMaxAge());
		Assert.assertFalse(frontendResource.isImmutable());
		Assert.assertFalse(frontendResource.isSendNoCache());
	}

	@Test
	public void testHandleRequestWithoutHashForUnhashedFile() throws Exception {
		long maxAge = RandomTestUtil.randomLong();

		_mockFallbackKeysSettingsUtil(
			HashMapBuilder.<String, Object>put(
				"maxAgeKey", maxAge
			).put(
				"sendNoCacheKey", false
			).build());

		HashedFileFrontendResourceRequestHandler
			hashedFileFrontendResourceRequestHandler =
				new HashedFileFrontendResourceRequestHandler(
					ContentTypes.TEXT_JAVASCRIPT, ".js",
					_mockHashedFilesRegistry(false),
					RandomTestUtil.randomLong(), "maxAgeKey",
					Mockito.mock(Portal.class), true, "sendNoCacheKey");

		FrontendResource frontendResource =
			hashedFileFrontendResourceRequestHandler.handleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _UNHASHED_FILE_PATH));

		Assert.assertEquals(
			ContentTypes.TEXT_JAVASCRIPT, frontendResource.getContentType());
		Assert.assertNull(frontendResource.getETag());
		Assert.assertEquals(
			"export default x;",
			StreamUtil.toString(frontendResource.getInputStream()));
		Assert.assertEquals(maxAge, frontendResource.getMaxAge());
		Assert.assertFalse(frontendResource.isImmutable());
		Assert.assertFalse(frontendResource.isSendNoCache());
	}

	private void _mockFallbackKeysSettingsUtil(Map<String, Object> map) {
		if (_fallbackKeysSettingsUtilMockedStatic != null) {
			_fallbackKeysSettingsUtilMockedStatic.close();
		}

		_fallbackKeysSettingsUtilMockedStatic = Mockito.mockStatic(
			FallbackKeysSettingsUtil.class);

		Settings settings = null;

		if (map != null) {
			settings = Mockito.mock(Settings.class);

			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Mockito.when(
					settings.getValue(
						Mockito.eq(entry.getKey()), Mockito.anyString())
				).thenReturn(
					String.valueOf(entry.getValue())
				);
			}
		}

		_fallbackKeysSettingsUtilMockedStatic.when(
			() -> FallbackKeysSettingsUtil.getSettings(
				Mockito.any(SettingsLocator.class))
		).thenReturn(
			settings
		);
	}

	private HashedFilesRegistry _mockHashedFilesRegistry(boolean hashedFile)
		throws Exception {

		HashedFilesRegistry hashedFilesRegistry = Mockito.mock(
			HashedFilesRegistry.class);

		if (hashedFile) {
			Mockito.when(
				hashedFilesRegistry.getHashedFileURI(
					Mockito.eq("/o/frontend-js-web" + _UNHASHED_FILE_PATH))
			).thenReturn(
				"/o/frontend-js-web" + _hashedFilePath
			);
		}

		URL url = Mockito.mock(URL.class);

		Mockito.when(
			url.openStream()
		).thenReturn(
			new ByteArrayInputStream(
				"export default x;".getBytes(StandardCharsets.UTF_8))
		);

		Mockito.when(
			hashedFilesRegistry.getResource(
				Mockito.eq(
					"/o/frontend-js-web" +
						(hashedFile ? _hashedFilePath : _UNHASHED_FILE_PATH)))
		).thenReturn(
			url
		);

		return hashedFilesRegistry;
	}

	private HttpServletRequest _mockHttpServletRequest(String requestURI) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setRequestURI(requestURI);

		return mockHttpServletRequest;
	}

	private static final String _HASH =
		FrontendJSWebTestUtil.randomHashedFileHash();

	private static final String _UNHASHED_FILE_PATH = "/__liferay__/index.js";

	private MockedStatic<FallbackKeysSettingsUtil>
		_fallbackKeysSettingsUtilMockedStatic;
	private String _hashedFilePath;

}