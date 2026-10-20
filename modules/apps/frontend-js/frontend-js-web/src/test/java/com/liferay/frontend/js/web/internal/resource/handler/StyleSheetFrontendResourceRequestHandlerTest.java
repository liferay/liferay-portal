/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.test.util.FrontendJSWebTestUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URL;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.mockito.Mockito;

/**
 * @author Iván Zaera Avellón
 */
@RunWith(Parameterized.class)
public class StyleSheetFrontendResourceRequestHandlerTest
	extends BaseFrontendResourceRequestHandlerTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Parameterized.Parameters(
		name = "{0}: deployHashed={1}, deployTokenizable={2}, requestURL={3}, requestHasThemeId={4}"
	)
	public static Collection<Object[]> data() {
		return Arrays.asList(
			new Object[][] {
				{0, false, false, "/o/frontend-js-web/css/main.nocss", false},
				{1, false, false, "/nonsense/request/main.css", false},
				{2, false, false, _VALID_URL, false},
				{3, false, false, _VALID_URL, true},
				{4, false, false, _VALID_HASHED_URL, false},
				{5, false, false, _VALID_HASHED_URL, true},
				{6, false, true, _VALID_URL, false},
				{7, false, true, _VALID_URL, true},
				{8, false, true, _VALID_HASHED_URL, false},
				{9, false, true, _VALID_HASHED_URL, true},
				{10, true, false, _VALID_URL, false},
				{11, true, false, _VALID_URL, true},
				{12, true, false, _VALID_HASHED_URL, false},
				{13, true, false, _VALID_HASHED_URL, true},
				{14, true, true, _VALID_URL, false},
				{15, true, true, _VALID_URL, true},
				{16, true, true, _VALID_HASHED_URL, false},
				{17, true, true, _VALID_HASHED_URL, true}
			});
	}

	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void test() throws Exception {
		HashedFilesRegistry hashedFilesRegistry = Mockito.mock(
			HashedFilesRegistry.class);

		mockDeployedFile(
			hashedFilesRegistry, deployHashed ? _VALID_HASHED_URL : _VALID_URL,
			deployTokenizable ? _TOKENIZABLE_CSS_CONTENT : _CSS_CONTENT);

		StyleSheetFrontendResourceRequestHandler
			styleSheetFrontendResourceRequestHandler =
				new StyleSheetFrontendResourceRequestHandler(
					mockConfigurationProvider(
						HashMapBuilder.<String, Object>put(
							"cssStyleSheetsMaxAge", _CSS_STYLE_SHEETS_MAX_AGE
						).put(
							"sendNoCacheForCSSStyleSheets",
							_SEND_NO_CACHE_FOR_CSS_STYLE_SHEETS
						).put(
							"sendNoCacheForTokenizedCSSStyleSheets",
							_SEND_NO_CACHE_FOR_TOKENIZED_CSS_STYLE_SHEETS
						).put(
							"tokenizedCSSStyleSheetsMaxAge",
							_TOKENIZED_CSS_STYLE_SHEETS_MAX_AGE
						).build()),
					hashedFilesRegistry, mockPortal(),
					_mockThemeLocalService());

		HttpServletRequest httpServletRequest;

		if (requestHasThemeId) {
			httpServletRequest = mockHttpServletRequest(
				requestURL,
				HashMapBuilder.put(
					"themeId", _THEME_ID
				).build());
		}
		else {
			httpServletRequest = mockHttpServletRequest(requestURL, null);
		}

		Assert.assertEquals(
			_EXPECTED_CAN_HANDLE_REQUEST[index],
			styleSheetFrontendResourceRequestHandler.canHandleRequest(
				httpServletRequest));

		FrontendResource frontendResource =
			styleSheetFrontendResourceRequestHandler.handleRequest(
				httpServletRequest);

		if (!_EXPECTED_FRONTEND_RESOURCE[index]) {
			Assert.assertNull(frontendResource);

			return;
		}

		Assert.assertNotNull(frontendResource);
		Assert.assertEquals(
			ContentTypes.TEXT_CSS, frontendResource.getContentType());

		URL url = hashedFilesRegistry.getResource(
			deployHashed ? _VALID_HASHED_URL : _VALID_URL);

		if (_EXPECTED_CACHE_KEY[index] == ExpectedCacheKey.LAST_MODIFIED) {
			Assert.assertEquals(
				url + "#" + LAST_MODIFIED, frontendResource.getCacheKey());
		}
		else if (_EXPECTED_CACHE_KEY[index] ==
					ExpectedCacheKey.LAST_MODIFIED_TOKENIZED) {

			Assert.assertEquals(
				StringBundler.concat(
					url, "#", LAST_MODIFIED,
					"#base_url=#portal_ctx=#theme_image_path=",
					_THEME_STATIC_RESOURCE_PATH, _THEME_IMAGES_PATH),
				frontendResource.getCacheKey());
		}
		else if (_EXPECTED_CACHE_KEY[index] == ExpectedCacheKey.URL) {
			Assert.assertEquals(url.toString(), frontendResource.getCacheKey());
		}
		else if (_EXPECTED_CACHE_KEY[index] == ExpectedCacheKey.URL_TOKENIZED) {
			Assert.assertEquals(
				StringBundler.concat(
					url, "#base_url=#portal_ctx=#theme_image_path=",
					_THEME_STATIC_RESOURCE_PATH, _THEME_IMAGES_PATH),
				frontendResource.getCacheKey());
		}

		if (_EXPECTED_E_TAG[index] == ExpectedETag.HASH) {
			Assert.assertEquals(_HASH, frontendResource.getETag());
		}
		else if (_EXPECTED_E_TAG[index] == ExpectedETag.HASH_PLUS_TOKENS_HASH) {
			Assert.assertEquals(
				_HASH + "-44795a18", frontendResource.getETag());
		}
		else if (_EXPECTED_E_TAG[index] == ExpectedETag.NULL) {
			Assert.assertNull(frontendResource.getETag());
		}

		Assert.assertEquals(
			_EXPECTED_IMMUTABLE[index], frontendResource.isImmutable());

		if (_EXPECTED_MAX_AGE[index] == ExpectedMaxAge.CSS_STYLE_SHEETS) {
			Assert.assertEquals(
				_CSS_STYLE_SHEETS_MAX_AGE, frontendResource.getMaxAge());
		}
		else if (_EXPECTED_MAX_AGE[index] == ExpectedMaxAge.INFINITE) {
			Assert.assertEquals(31536000L, frontendResource.getMaxAge());
		}
		else if (_EXPECTED_MAX_AGE[index] ==
					ExpectedMaxAge.TOKENIZED_CSS_STYLE_SHEETS) {

			Assert.assertEquals(
				_TOKENIZED_CSS_STYLE_SHEETS_MAX_AGE,
				frontendResource.getMaxAge());
		}

		Assert.assertFalse(frontendResource.isPrivate());

		if (_EXPECTED_SEND_NO_CACHE[index] ==
				ExpectedSendNoCache.CSS_STYLE_SHEETS) {

			Assert.assertEquals(
				_SEND_NO_CACHE_FOR_CSS_STYLE_SHEETS,
				frontendResource.isSendNoCache());
		}
		else if (_EXPECTED_SEND_NO_CACHE[index] == ExpectedSendNoCache.FALSE) {
			Assert.assertFalse(frontendResource.isSendNoCache());
		}
		else if (_EXPECTED_SEND_NO_CACHE[index] ==
					ExpectedSendNoCache.TOKENIZED_CSS_STYLE_SHEETS) {

			Assert.assertEquals(
				_SEND_NO_CACHE_FOR_TOKENIZED_CSS_STYLE_SHEETS,
				frontendResource.isSendNoCache());
		}

		if (_EXPECTED_CONTENT[index] == ExpectedContent.CSS) {
			Assert.assertEquals(
				_CSS_CONTENT,
				StreamUtil.toString(frontendResource.getInputStream()));
		}
		else if (_EXPECTED_CONTENT[index] == ExpectedContent.TOKENIZABLE_CSS) {
			Assert.assertEquals(
				_TOKENIZABLE_CSS_CONTENT,
				StreamUtil.toString(frontendResource.getInputStream()));
		}
		else if (_EXPECTED_CONTENT[index] == ExpectedContent.TOKENIZED_CSS) {
			Assert.assertEquals(
				StringUtil.replace(
					_TOKENIZABLE_CSS_CONTENT, "@theme_image_path@",
					_THEME_STATIC_RESOURCE_PATH + _THEME_IMAGES_PATH),
				StreamUtil.toString(frontendResource.getInputStream()));
		}
	}

	@Parameterized.Parameter(1)
	public boolean deployHashed;

	@Parameterized.Parameter(2)
	public boolean deployTokenizable;

	@Parameterized.Parameter
	public int index;

	@Parameterized.Parameter(4)
	public boolean requestHasThemeId;

	@Parameterized.Parameter(3)
	public String requestURL;

	private ThemeLocalService _mockThemeLocalService() {
		ThemeLocalService themeLocalService = Mockito.mock(
			ThemeLocalService.class);

		Theme theme = Mockito.mock(Theme.class);

		Mockito.when(
			theme.getImagesPath()
		).thenReturn(
			_THEME_IMAGES_PATH
		);

		Mockito.when(
			theme.getStaticResourcePath()
		).thenReturn(
			_THEME_STATIC_RESOURCE_PATH
		);

		Mockito.when(
			themeLocalService.getTheme(COMPANY_ID, _THEME_ID)
		).thenReturn(
			theme
		);

		return themeLocalService;
	}

	private static final String _CSS_CONTENT = "body {font-weight: bold;}";

	private static final long _CSS_STYLE_SHEETS_MAX_AGE = 1000;

	private static final ExpectedCacheKey[] _EXPECTED_CACHE_KEY = {
		null, null, ExpectedCacheKey.LAST_MODIFIED,
		ExpectedCacheKey.LAST_MODIFIED, null, null,
		ExpectedCacheKey.LAST_MODIFIED,
		ExpectedCacheKey.LAST_MODIFIED_TOKENIZED, null, null,
		ExpectedCacheKey.URL, ExpectedCacheKey.URL, ExpectedCacheKey.URL,
		ExpectedCacheKey.URL, ExpectedCacheKey.URL,
		ExpectedCacheKey.URL_TOKENIZED, ExpectedCacheKey.URL,
		ExpectedCacheKey.URL_TOKENIZED
	};

	private static final boolean[] _EXPECTED_CAN_HANDLE_REQUEST = {
		false, false, true, true, false, false, true, true, false, false, true,
		true, true, true, true, true, true, true
	};

	private static final ExpectedContent[] _EXPECTED_CONTENT = {
		null, null, ExpectedContent.CSS, ExpectedContent.CSS, null, null,
		ExpectedContent.TOKENIZABLE_CSS, ExpectedContent.TOKENIZED_CSS, null,
		null, ExpectedContent.CSS, ExpectedContent.CSS, ExpectedContent.CSS,
		ExpectedContent.CSS, ExpectedContent.TOKENIZABLE_CSS,
		ExpectedContent.TOKENIZED_CSS, ExpectedContent.TOKENIZABLE_CSS,
		ExpectedContent.TOKENIZED_CSS
	};

	private static final ExpectedETag[] _EXPECTED_E_TAG = {
		null, null, ExpectedETag.NULL, ExpectedETag.NULL, ExpectedETag.NULL,
		ExpectedETag.NULL, ExpectedETag.NULL, ExpectedETag.NULL,
		ExpectedETag.NULL, ExpectedETag.NULL, ExpectedETag.HASH,
		ExpectedETag.HASH, ExpectedETag.HASH, ExpectedETag.HASH,
		ExpectedETag.HASH, ExpectedETag.HASH_PLUS_TOKENS_HASH,
		ExpectedETag.HASH, ExpectedETag.HASH_PLUS_TOKENS_HASH
	};

	private static final boolean[] _EXPECTED_FRONTEND_RESOURCE = {
		false, false, true, true, false, false, true, true, false, false, true,
		true, true, true, true, true, true, true
	};

	private static final boolean[] _EXPECTED_IMMUTABLE = {
		false, false, false, false, false, false, false, false, false, false,
		false, false, true, true, false, false, true, false
	};

	private static final ExpectedMaxAge[] _EXPECTED_MAX_AGE = {
		null, null, ExpectedMaxAge.CSS_STYLE_SHEETS,
		ExpectedMaxAge.CSS_STYLE_SHEETS, null, null,
		ExpectedMaxAge.CSS_STYLE_SHEETS,
		ExpectedMaxAge.TOKENIZED_CSS_STYLE_SHEETS, null, null,
		ExpectedMaxAge.CSS_STYLE_SHEETS, ExpectedMaxAge.CSS_STYLE_SHEETS,
		ExpectedMaxAge.INFINITE, ExpectedMaxAge.INFINITE,
		ExpectedMaxAge.CSS_STYLE_SHEETS,
		ExpectedMaxAge.TOKENIZED_CSS_STYLE_SHEETS, ExpectedMaxAge.INFINITE,
		ExpectedMaxAge.TOKENIZED_CSS_STYLE_SHEETS
	};

	private static final ExpectedSendNoCache[] _EXPECTED_SEND_NO_CACHE = {
		null, null, ExpectedSendNoCache.CSS_STYLE_SHEETS,
		ExpectedSendNoCache.CSS_STYLE_SHEETS, null, null,
		ExpectedSendNoCache.CSS_STYLE_SHEETS,
		ExpectedSendNoCache.TOKENIZED_CSS_STYLE_SHEETS, null, null,
		ExpectedSendNoCache.CSS_STYLE_SHEETS,
		ExpectedSendNoCache.CSS_STYLE_SHEETS, ExpectedSendNoCache.FALSE,
		ExpectedSendNoCache.FALSE, ExpectedSendNoCache.CSS_STYLE_SHEETS,
		ExpectedSendNoCache.TOKENIZED_CSS_STYLE_SHEETS,
		ExpectedSendNoCache.FALSE,
		ExpectedSendNoCache.TOKENIZED_CSS_STYLE_SHEETS
	};

	private static final String _HASH =
		FrontendJSWebTestUtil.randomHashedFileHash();

	private static final boolean _SEND_NO_CACHE_FOR_CSS_STYLE_SHEETS = true;

	private static final boolean _SEND_NO_CACHE_FOR_TOKENIZED_CSS_STYLE_SHEETS =
		false;

	private static final String _THEME_ID = "classic_WAR_classictheme";

	private static final String _THEME_IMAGES_PATH = "/images";

	private static final String _THEME_STATIC_RESOURCE_PATH =
		"/o/classic-theme";

	private static final String _TOKENIZABLE_CSS_CONTENT =
		"body {background-image: url(@theme_image_path@/an_image.png)}";

	private static final long _TOKENIZED_CSS_STYLE_SHEETS_MAX_AGE = 2000;

	private static final String _VALID_HASHED_URL =
		"/o/frontend-js-web/css/main.(" + _HASH + ").css";

	private static final String _VALID_URL = "/o/frontend-js-web/css/main.css";

	private enum ExpectedCacheKey {

		LAST_MODIFIED, LAST_MODIFIED_TOKENIZED, URL, URL_TOKENIZED

	}

	private enum ExpectedContent {

		CSS, TOKENIZABLE_CSS, TOKENIZED_CSS

	}

	private enum ExpectedETag {

		HASH, HASH_PLUS_TOKENS_HASH, NULL,

	}

	private enum ExpectedMaxAge {

		CSS_STYLE_SHEETS, INFINITE, TOKENIZED_CSS_STYLE_SHEETS

	}

	private enum ExpectedSendNoCache {

		CSS_STYLE_SHEETS, FALSE, TOKENIZED_CSS_STYLE_SHEETS

	}

}