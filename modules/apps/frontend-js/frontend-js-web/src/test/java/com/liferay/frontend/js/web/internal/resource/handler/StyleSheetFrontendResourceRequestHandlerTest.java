/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.configuration.FrontendCachingConfiguration;
import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.test.util.FrontendJSWebTestUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;

import java.net.URL;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public class StyleSheetFrontendResourceRequestHandlerTest {

	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_hashedFilePath = StringUtil.replace(
			_UNHASHED_FILE_PATH, ".css", ".(" + _HASH + ").css");
	}

	@Ignore
	@Test
	public void testCanHandleRequest() throws Exception {

		// LPD-57326

		StyleSheetFrontendResourceRequestHandler
			styleSheetFrontendResourceRequestHandler =
				new StyleSheetFrontendResourceRequestHandler(
					_mockFrontendCachingConfiguration(86400, false),
					_mockHashedFilesRegistry(true, false), _mockPortal(),
					_mockThemeLocalService());

		Assert.assertFalse(
			styleSheetFrontendResourceRequestHandler.canHandleRequest(
				_mockHttpServletRequest("/nonsense/request/main.css", false)));
		Assert.assertTrue(
			styleSheetFrontendResourceRequestHandler.canHandleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _UNHASHED_FILE_PATH, false)));
		Assert.assertTrue(
			styleSheetFrontendResourceRequestHandler.canHandleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _hashedFilePath, false)));
	}

	@Test
	public void testHandleRequestForTokenizedFile() throws Exception {
		long maxAge = RandomTestUtil.randomLong();
		boolean sendNoCache = true;

		StyleSheetFrontendResourceRequestHandler
			styleSheetFrontendResourceRequestHandler =
				new StyleSheetFrontendResourceRequestHandler(
					_mockFrontendCachingConfiguration(maxAge, sendNoCache),
					_mockHashedFilesRegistry(true, true), _mockPortal(),
					_mockThemeLocalService());

		FrontendResource frontendResource =
			styleSheetFrontendResourceRequestHandler.handleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _hashedFilePath, true));

		Assert.assertEquals(
			ContentTypes.TEXT_CSS, frontendResource.getContentType());
		Assert.assertEquals(_HASH + "-44795a18", frontendResource.getETag());
		Assert.assertEquals(
			StringUtil.replace(
				_TOKENIZED_CSS_CONTENT, "@theme_image_path@",
				"/o/classic-theme/images"),
			StreamUtil.toString(frontendResource.getInputStream()));
		Assert.assertEquals(maxAge, frontendResource.getMaxAge());
		Assert.assertFalse(frontendResource.isImmutable());
		Assert.assertEquals(sendNoCache, frontendResource.isSendNoCache());
	}

	@Test
	public void testHandleRequestWithHash() throws Exception {
		StyleSheetFrontendResourceRequestHandler
			styleSheetFrontendResourceRequestHandler =
				new StyleSheetFrontendResourceRequestHandler(
					_mockFrontendCachingConfiguration(86400, true),
					_mockHashedFilesRegistry(true, false), _mockPortal(),
					_mockThemeLocalService());

		FrontendResource frontendResource =
			styleSheetFrontendResourceRequestHandler.handleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _hashedFilePath, false));

		Assert.assertEquals(
			ContentTypes.TEXT_CSS, frontendResource.getContentType());
		Assert.assertEquals(_HASH, frontendResource.getETag());
		Assert.assertEquals(
			_CSS_CONTENT,
			StreamUtil.toString(frontendResource.getInputStream()));
		Assert.assertEquals(31536000L, frontendResource.getMaxAge());
		Assert.assertTrue(frontendResource.isImmutable());
		Assert.assertFalse(frontendResource.isSendNoCache());
	}

	@Test
	public void testHandleRequestWithoutHashForHashedFile() throws Exception {
		long maxAge = RandomTestUtil.randomLong();
		boolean sendNoCache = true;

		StyleSheetFrontendResourceRequestHandler
			styleSheetFrontendResourceRequestHandler =
				new StyleSheetFrontendResourceRequestHandler(
					_mockFrontendCachingConfiguration(maxAge, sendNoCache),
					_mockHashedFilesRegistry(true, false), _mockPortal(),
					_mockThemeLocalService());

		FrontendResource frontendResource =
			styleSheetFrontendResourceRequestHandler.handleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _UNHASHED_FILE_PATH, false));

		Assert.assertEquals(
			ContentTypes.TEXT_CSS, frontendResource.getContentType());
		Assert.assertEquals(_HASH, frontendResource.getETag());
		Assert.assertEquals(
			_CSS_CONTENT,
			StreamUtil.toString(frontendResource.getInputStream()));
		Assert.assertEquals(maxAge, frontendResource.getMaxAge());
		Assert.assertFalse(frontendResource.isImmutable());
		Assert.assertEquals(sendNoCache, frontendResource.isSendNoCache());
	}

	@Test
	public void testHandleRequestWithoutHashForUnhashedFile() throws Exception {
		long maxAge = RandomTestUtil.randomLong();
		boolean sendNoCache = true;

		StyleSheetFrontendResourceRequestHandler
			styleSheetFrontendResourceRequestHandler =
				new StyleSheetFrontendResourceRequestHandler(
					_mockFrontendCachingConfiguration(maxAge, sendNoCache),
					_mockHashedFilesRegistry(false, false), _mockPortal(),
					_mockThemeLocalService());

		FrontendResource frontendResource =
			styleSheetFrontendResourceRequestHandler.handleRequest(
				_mockHttpServletRequest(
					"/o/frontend-js-web" + _UNHASHED_FILE_PATH, false));

		Assert.assertEquals(
			ContentTypes.TEXT_CSS, frontendResource.getContentType());
		Assert.assertNull(frontendResource.getETag());
		Assert.assertEquals(
			_CSS_CONTENT,
			StreamUtil.toString(frontendResource.getInputStream()));
		Assert.assertEquals(maxAge, frontendResource.getMaxAge());
		Assert.assertFalse(frontendResource.isImmutable());
		Assert.assertEquals(sendNoCache, frontendResource.isSendNoCache());
	}

	private FrontendCachingConfiguration _mockFrontendCachingConfiguration(
		long cssStyleSheetsMaxAge, boolean sendNoCacheForCSSStyleSheets) {

		FrontendCachingConfiguration frontendCachingConfiguration =
			Mockito.mock(FrontendCachingConfiguration.class);

		Mockito.when(
			frontendCachingConfiguration.cssStyleSheetsMaxAge()
		).thenReturn(
			cssStyleSheetsMaxAge
		);

		Mockito.when(
			frontendCachingConfiguration.sendNoCacheForCSSStyleSheets()
		).thenReturn(
			sendNoCacheForCSSStyleSheets
		);

		return frontendCachingConfiguration;
	}

	private HashedFilesRegistry _mockHashedFilesRegistry(
			boolean hashedFile, boolean tokenizedFile)
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

		String content = tokenizedFile ? _TOKENIZED_CSS_CONTENT : _CSS_CONTENT;

		Mockito.when(
			url.openStream()
		).thenReturn(
			new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))
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

	private HttpServletRequest _mockHttpServletRequest(
		String requestURI, boolean tokenizedRequest) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setRequestURI(requestURI);

		if (tokenizedRequest) {
			mockHttpServletRequest.addParameter("tokenize", "true");
			mockHttpServletRequest.addParameter(
				"themeId", "classic_WAR_classictheme");
		}

		return mockHttpServletRequest;
	}

	private Portal _mockPortal() throws Exception {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getCDNHost(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			portal.getPathContext()
		).thenReturn(
			StringPool.BLANK
		);

		Mockito.when(
			portal.getPathProxy()
		).thenReturn(
			StringPool.BLANK
		);

		return portal;
	}

	private ThemeLocalService _mockThemeLocalService() {
		ThemeLocalService themeLocalService = Mockito.mock(
			ThemeLocalService.class);

		Theme theme = Mockito.mock(Theme.class);

		Mockito.when(
			theme.getImagesPath()
		).thenReturn(
			"/images"
		);

		Mockito.when(
			theme.getStaticResourcePath()
		).thenReturn(
			"/o/classic-theme"
		);

		Mockito.when(
			themeLocalService.getTheme(0, "classic_WAR_classictheme")
		).thenReturn(
			theme
		);

		return themeLocalService;
	}

	private static final String _CSS_CONTENT = "body {font-weight: bold;}";

	private static final String _HASH =
		FrontendJSWebTestUtil.randomHashedFileHash();

	private static final String _TOKENIZED_CSS_CONTENT =
		"body {background-image: url(@theme_image_path@/an_image.png)}";

	private static final String _UNHASHED_FILE_PATH = "/css/main.css";

	private String _hashedFilePath;

}