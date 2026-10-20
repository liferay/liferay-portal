/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource.handler;

import com.liferay.frontend.js.web.internal.resource.FrontendResource;
import com.liferay.frontend.js.web.test.util.FrontendJSWebTestUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.PortletConfigFactory;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URL;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Iván Zaera Avellón
 */
@RunWith(Parameterized.class)
public class JavaScriptFrontendResourceRequestHandlerTest
	extends BaseFrontendResourceRequestHandlerTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Parameterized.Parameters(
		name = "{0}: deployHashed={1},  deployLanguageJSON={2}, deployTranslatable={3}, requestURL={4}"
	)
	public static Collection<Object[]> data() {
		return Arrays.asList(
			new Object[][] {
				{
					0, false, false, false,
					"/o/frontend-js-web/__liferay__/index.nojs"
				},
				{1, false, false, false, "/nonsense/request/index.js"},
				{2, false, false, false, _VALID_BUNDLE_HASHED_URL},
				{3, false, false, false, _VALID_HASHED_URL},
				{4, false, false, false, _VALID_URL},
				{5, false, false, true, _VALID_BUNDLE_HASHED_URL},
				{6, false, false, true, _VALID_HASHED_URL},
				{7, false, false, true, _VALID_URL},
				{8, false, true, false, _VALID_BUNDLE_HASHED_URL},
				{9, false, true, false, _VALID_HASHED_URL},
				{10, false, true, false, _VALID_URL},
				{11, false, true, true, _VALID_BUNDLE_HASHED_URL},
				{12, false, true, true, _VALID_HASHED_URL},
				{13, false, true, true, _VALID_URL},
				{14, true, false, false, _VALID_BUNDLE_HASHED_URL},
				{15, true, false, false, _VALID_HASHED_URL},
				{16, true, false, false, _VALID_URL},
				{17, true, false, true, _VALID_BUNDLE_HASHED_URL},
				{18, true, false, true, _VALID_HASHED_URL},
				{19, true, false, true, _VALID_URL},
				{20, true, true, false, _VALID_BUNDLE_HASHED_URL},
				{21, true, true, false, _VALID_HASHED_URL},
				{22, true, true, false, _VALID_URL},
				{23, true, true, true, _VALID_BUNDLE_HASHED_URL},
				{24, true, true, true, _VALID_HASHED_URL},
				{25, true, true, true, _VALID_URL}
			});
	}

	@Before
	public void setUp() {
		super.setUp();

		_localeUtilMockedStatic = Mockito.mockStatic(LocaleUtil.class);

		_localeUtilMockedStatic.when(
			() -> LocaleUtil.fromLanguageId(Mockito.anyString())
		).thenAnswer(
			(Answer<Locale>)invocationOnMock -> new Locale(
				invocationOnMock.getArgument(0))
		);
	}

	@After
	public void tearDown() {
		if (_localeUtilMockedStatic != null) {
			_localeUtilMockedStatic.close();

			_localeUtilMockedStatic = null;
		}
	}

	@Test
	public void test() throws Exception {
		HashedFilesRegistry hashedFilesRegistry = Mockito.mock(
			HashedFilesRegistry.class);

		Mockito.when(
			hashedFilesRegistry.getServletContextHash(
				Mockito.eq(_SERVLET_CONTEXT_NAME))
		).thenReturn(
			_SERVLET_CONTEXT_HASH
		);

		mockDeployedFile(
			hashedFilesRegistry, deployHashed ? _VALID_HASHED_URL : _VALID_URL,
			deployTranslatable ? _TRANSLATABLE_JS_CONTENT : _JS_CONTENT);

		if (deployLanguageJSON) {
			mockDeployedFile(
				hashedFilesRegistry,
				"/o/" + _SERVLET_CONTEXT_NAME + "/language.json", "{}");
		}

		JavaScriptFrontendResourceRequestHandler
			javaScriptFrontendResourceRequestHandler =
				new JavaScriptFrontendResourceRequestHandler(
					mockConfigurationProvider(
						HashMapBuilder.<String, Object>put(
							"jsFilesMaxAge", _JS_FILES_MAX_AGE
						).put(
							"sendNoCacheForJSFiles", _SEND_NO_CACHE_FOR_JS_FILES
						).put(
							"sendNoCacheForTranslatedJSFiles",
							_SEND_NO_CACHE_FOR_TRANSLATED_JS_FILES
						).put(
							"translatedJSFilesMaxAge",
							_TRANSLATED_JS_FILES_MAX_AGE
						).build()),
					hashedFilesRegistry, _mockLanguage(), mockPortal(),
					Mockito.mock(PortletConfigFactory.class));

		HttpServletRequest httpServletRequest = mockHttpServletRequest(
			requestURL, null);

		Assert.assertEquals(
			_EXPECTED_CAN_HANDLE_REQUEST[index],
			javaScriptFrontendResourceRequestHandler.canHandleRequest(
				httpServletRequest));

		FrontendResource frontendResource =
			javaScriptFrontendResourceRequestHandler.handleRequest(
				httpServletRequest);

		if (!_EXPECTED_FRONTEND_RESOURCE[index]) {
			Assert.assertNull(frontendResource);

			return;
		}

		Assert.assertNotNull(frontendResource);
		Assert.assertEquals(
			ContentTypes.APPLICATION_JAVASCRIPT,
			frontendResource.getContentType());

		URL url = hashedFilesRegistry.getResource(
			deployHashed ? _VALID_HASHED_URL : _VALID_URL);

		if (_EXPECTED_CACHE_KEY[index] == ExpectedCacheKey.LAST_MODIFIED) {
			Assert.assertEquals(
				url + "#" + LAST_MODIFIED, frontendResource.getCacheKey());
		}
		else if (_EXPECTED_CACHE_KEY[index] == ExpectedCacheKey.NULL) {
			Assert.assertNull(frontendResource.getCacheKey());
		}
		else if (_EXPECTED_CACHE_KEY[index] == ExpectedCacheKey.URL) {
			Assert.assertEquals(url.toString(), frontendResource.getCacheKey());
		}

		if (_EXPECTED_E_TAG[index] == ExpectedETag.HASH) {
			Assert.assertEquals(_HASH, frontendResource.getETag());
		}
		else if (_EXPECTED_E_TAG[index] == ExpectedETag.NULL) {
			Assert.assertNull(frontendResource.getETag());
		}

		Assert.assertEquals(
			_EXPECTED_IMMUTABLE[index], frontendResource.isImmutable());

		if (_EXPECTED_MAX_AGE[index] == ExpectedMaxAge.JS_FILES) {
			Assert.assertEquals(
				_JS_FILES_MAX_AGE, frontendResource.getMaxAge());
		}
		else if (_EXPECTED_MAX_AGE[index] == ExpectedMaxAge.INFINITE) {
			Assert.assertEquals(31536000L, frontendResource.getMaxAge());
		}
		else if (_EXPECTED_MAX_AGE[index] ==
					ExpectedMaxAge.TRANSLATED_JS_FILES) {

			Assert.assertEquals(
				_TRANSLATED_JS_FILES_MAX_AGE, frontendResource.getMaxAge());
		}

		Assert.assertEquals(
			_EXPECTED_PRIVATE[index], frontendResource.isPrivate());

		if (_EXPECTED_SEND_NO_CACHE[index] == ExpectedSendNoCache.JS_FILES) {
			Assert.assertEquals(
				_SEND_NO_CACHE_FOR_JS_FILES, frontendResource.isSendNoCache());
		}
		else if (_EXPECTED_SEND_NO_CACHE[index] == ExpectedSendNoCache.FALSE) {
			Assert.assertFalse(frontendResource.isSendNoCache());
		}
		else if (_EXPECTED_SEND_NO_CACHE[index] ==
					ExpectedSendNoCache.TRANSLATED_JS_FILES) {

			Assert.assertEquals(
				_SEND_NO_CACHE_FOR_TRANSLATED_JS_FILES,
				frontendResource.isSendNoCache());
		}

		if (_EXPECTED_CONTENT[index] == ExpectedContent.JS) {
			Assert.assertEquals(
				_JS_CONTENT,
				StreamUtil.toString(frontendResource.getInputStream()));
		}
		else if (_EXPECTED_CONTENT[index] == ExpectedContent.TRANSLATABLE_JS) {
			Assert.assertEquals(
				_TRANSLATABLE_JS_CONTENT,
				StreamUtil.toString(frontendResource.getInputStream()));
		}
		else if (_EXPECTED_CONTENT[index] == ExpectedContent.TRANSLATED_JS) {
			Assert.assertEquals(
				_TRANSLATED_JS_CONTENT,
				StreamUtil.toString(frontendResource.getInputStream()));
		}
	}

	@Parameterized.Parameter(1)
	public boolean deployHashed;

	@Parameterized.Parameter(2)
	public boolean deployLanguageJSON;

	@Parameterized.Parameter(3)
	public boolean deployTranslatable;

	@Parameterized.Parameter
	public int index;

	@Parameterized.Parameter(4)
	public String requestURL;

	private Language _mockLanguage() {
		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.getLanguageId(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			_LANGUAGE_ID
		);

		Mockito.when(
			language.process(
				Mockito.any(), Mockito.eq(new Locale(_LANGUAGE_ID)),
				Mockito.eq(_TRANSLATABLE_JS_CONTENT))
		).thenReturn(
			_TRANSLATED_JS_CONTENT
		);

		return language;
	}

	private static final ExpectedCacheKey[] _EXPECTED_CACHE_KEY = {
		null, null, null, null, ExpectedCacheKey.LAST_MODIFIED, null, null,
		ExpectedCacheKey.NULL, null, null, ExpectedCacheKey.LAST_MODIFIED, null,
		null, ExpectedCacheKey.LAST_MODIFIED, ExpectedCacheKey.URL,
		ExpectedCacheKey.URL, ExpectedCacheKey.URL, ExpectedCacheKey.NULL,
		ExpectedCacheKey.NULL, ExpectedCacheKey.NULL, ExpectedCacheKey.URL,
		ExpectedCacheKey.URL, ExpectedCacheKey.URL, ExpectedCacheKey.URL,
		ExpectedCacheKey.URL, ExpectedCacheKey.URL
	};

	private static final boolean[] _EXPECTED_CAN_HANDLE_REQUEST = {
		false, false, false, false, true, false, false, true, false, false,
		true, false, false, true, true, true, true, true, true, true, true,
		true, true, true, true, true
	};

	private static final ExpectedContent[] _EXPECTED_CONTENT = {
		null, null, null, null, ExpectedContent.JS, null, null,
		ExpectedContent.TRANSLATED_JS, null, null, ExpectedContent.JS, null,
		null, ExpectedContent.TRANSLATABLE_JS, ExpectedContent.JS,
		ExpectedContent.JS, ExpectedContent.JS, ExpectedContent.TRANSLATED_JS,
		ExpectedContent.TRANSLATED_JS, ExpectedContent.TRANSLATED_JS,
		ExpectedContent.JS, ExpectedContent.JS, ExpectedContent.JS,
		ExpectedContent.TRANSLATABLE_JS, ExpectedContent.TRANSLATABLE_JS,
		ExpectedContent.TRANSLATABLE_JS
	};

	private static final ExpectedETag[] _EXPECTED_E_TAG = {
		null, null, null, null, ExpectedETag.NULL, null, null,
		ExpectedETag.NULL, null, null, ExpectedETag.NULL, null, null,
		ExpectedETag.NULL, ExpectedETag.HASH, ExpectedETag.HASH,
		ExpectedETag.HASH, ExpectedETag.NULL, ExpectedETag.NULL,
		ExpectedETag.NULL, ExpectedETag.HASH, ExpectedETag.HASH,
		ExpectedETag.HASH, ExpectedETag.HASH, ExpectedETag.HASH,
		ExpectedETag.HASH
	};

	private static final boolean[] _EXPECTED_FRONTEND_RESOURCE = {
		false, false, false, false, true, false, false, true, false, false,
		true, false, false, true, true, true, true, true, true, true, true,
		true, true, true, true, true
	};

	private static final boolean[] _EXPECTED_IMMUTABLE = {
		false, false, false, false, false, false, false, false, false, false,
		false, false, false, false, true, true, false, false, false, false,
		true, true, false, true, true, false
	};

	private static final ExpectedMaxAge[] _EXPECTED_MAX_AGE = {
		null, null, null, null, ExpectedMaxAge.JS_FILES, null, null,
		ExpectedMaxAge.TRANSLATED_JS_FILES, null, null, ExpectedMaxAge.JS_FILES,
		null, null, ExpectedMaxAge.JS_FILES, ExpectedMaxAge.INFINITE,
		ExpectedMaxAge.INFINITE, ExpectedMaxAge.JS_FILES,
		ExpectedMaxAge.TRANSLATED_JS_FILES, ExpectedMaxAge.TRANSLATED_JS_FILES,
		ExpectedMaxAge.TRANSLATED_JS_FILES, ExpectedMaxAge.INFINITE,
		ExpectedMaxAge.INFINITE, ExpectedMaxAge.JS_FILES,
		ExpectedMaxAge.INFINITE, ExpectedMaxAge.INFINITE,
		ExpectedMaxAge.JS_FILES
	};

	private static final boolean[] _EXPECTED_PRIVATE = {
		false, false, false, false, false, false, false, true, false, false,
		false, false, false, false, false, false, false, true, true, true,
		false, false, false, false, false, false
	};

	private static final ExpectedSendNoCache[] _EXPECTED_SEND_NO_CACHE = {
		null, null, null, null, ExpectedSendNoCache.JS_FILES, null, null,
		ExpectedSendNoCache.TRANSLATED_JS_FILES, null, null,
		ExpectedSendNoCache.JS_FILES, null, null, ExpectedSendNoCache.JS_FILES,
		ExpectedSendNoCache.FALSE, ExpectedSendNoCache.FALSE,
		ExpectedSendNoCache.JS_FILES, ExpectedSendNoCache.TRANSLATED_JS_FILES,
		ExpectedSendNoCache.TRANSLATED_JS_FILES,
		ExpectedSendNoCache.TRANSLATED_JS_FILES, ExpectedSendNoCache.FALSE,
		ExpectedSendNoCache.FALSE, ExpectedSendNoCache.JS_FILES,
		ExpectedSendNoCache.FALSE, ExpectedSendNoCache.FALSE,
		ExpectedSendNoCache.JS_FILES
	};

	private static final String _HASH =
		FrontendJSWebTestUtil.randomHashedFileHash();

	private static final String _JS_CONTENT =
		"export default f() {return 'Hi there!';}";

	private static final long _JS_FILES_MAX_AGE = 1000;

	private static final String _LANGUAGE_ID = "en";

	private static final boolean _SEND_NO_CACHE_FOR_JS_FILES =
		RandomTestUtil.randomBoolean();

	private static final boolean _SEND_NO_CACHE_FOR_TRANSLATED_JS_FILES =
		RandomTestUtil.randomBoolean();

	private static final String _SERVLET_CONTEXT_HASH =
		FrontendJSWebTestUtil.randomHashedFileHash();

	private static final String _SERVLET_CONTEXT_NAME = "frontend-js-web";

	private static final String _TRANSLATABLE_JS_CONTENT =
		"export default f() {return Liferay.Language.get('portlet');}";

	private static final String _TRANSLATED_JS_CONTENT =
		"export default f() {return 'Portlet';}";

	private static final long _TRANSLATED_JS_FILES_MAX_AGE = 2000;

	private static final String _VALID_BUNDLE_HASHED_URL = StringBundler.concat(
		"/o/js/-/", _SERVLET_CONTEXT_NAME, StringPool.OPEN_PARENTHESIS,
		_SERVLET_CONTEXT_HASH, ")/__liferay__/index.js");

	private static final String _VALID_HASHED_URL = StringBundler.concat(
		"/o/", _SERVLET_CONTEXT_NAME, "/__liferay__/index.(", _HASH, ").js");

	private static final String _VALID_URL =
		"/o/" + _SERVLET_CONTEXT_NAME + "/__liferay__/index.js";

	private MockedStatic<LocaleUtil> _localeUtilMockedStatic;

	private enum ExpectedCacheKey {

		LAST_MODIFIED, NULL, URL

	}

	private enum ExpectedContent {

		JS, TRANSLATABLE_JS, TRANSLATED_JS

	}

	private enum ExpectedETag {

		HASH, NULL

	}

	private enum ExpectedMaxAge {

		INFINITE, JS_FILES, TRANSLATED_JS_FILES

	}

	private enum ExpectedSendNoCache {

		FALSE, JS_FILES, TRANSLATED_JS_FILES

	}

}