/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.internal.web.cache;

import com.liferay.ai.hub.cell.configuration.AIHubCellConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.webcache.WebCacheItem;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.HttpURLConnection;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author José Abelenda
 */
public class AIHubCellAccessTokenWebCacheItemTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_secretResolverSnapshot = ReflectionTestUtil.getFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot");

		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			new Snapshot<SecretResolver>(
				SecretResolverUtil.class, SecretResolver.class) {

				@Override
				public SecretResolver get() {
					return (companyId, value) -> value;
				}

			});
	}

	@After
	public void tearDown() {
		ReflectionTestUtil.setFieldValue(
			SecretResolverUtil.class, "_secretResolverSnapshot",
			_secretResolverSnapshot);
	}

	@Test
	public void testConvertWhenAccessTokenIsJWT() throws Exception {
		long startTime = System.currentTimeMillis();

		long expirationTime =
			((startTime + Time.HOUR) / Time.SECOND) * Time.SECOND;

		try (MockedStatic<HttpUtil> httpUtilMockedStatic = _mockHttpUtil(
				JSONUtil.put(
					"access_token", _createAccessToken(expirationTime)
				).put(
					"expires_in", RandomTestUtil.randomLong()
				).toString())) {

			AIHubCellAccessTokenWebCacheItem aiHubCellAccessTokenWebCacheItem =
				new AIHubCellAccessTokenWebCacheItem(
					_getAIHubCellConfiguration(), CompanyConstants.SYSTEM);

			aiHubCellAccessTokenWebCacheItem.convert(StringPool.BLANK);

			long refreshTime =
				aiHubCellAccessTokenWebCacheItem.getRefreshTime();

			Assert.assertTrue(refreshTime > 0);
			Assert.assertTrue(
				refreshTime <= (long)((expirationTime - startTime) * 0.8));
		}
	}

	@Test
	public void testConvertWhenAccessTokenIsMissing() throws Exception {
		try (MockedStatic<HttpUtil> httpUtilMockedStatic = _mockHttpUtil(
				JSONUtil.put(
					"error", RandomTestUtil.randomString()
				).toString())) {

			AIHubCellAccessTokenWebCacheItem aiHubCellAccessTokenWebCacheItem =
				new AIHubCellAccessTokenWebCacheItem(
					_getAIHubCellConfiguration(), CompanyConstants.SYSTEM);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					AIHubCellAccessTokenWebCacheItem.class.getName(),
					LoggerTestUtil.OFF)) {

				Assert.assertNull(
					aiHubCellAccessTokenWebCacheItem.convert(StringPool.BLANK));
			}
		}
	}

	@Test
	public void testConvertWhenAccessTokenIsOpaque() throws Exception {
		try (MockedStatic<HttpUtil> httpUtilMockedStatic = _mockHttpUtil(
				JSONUtil.put(
					"access_token", RandomTestUtil.randomString()
				).put(
					"expires_in", RandomTestUtil.randomLong()
				).toString())) {

			AIHubCellAccessTokenWebCacheItem aiHubCellAccessTokenWebCacheItem =
				new AIHubCellAccessTokenWebCacheItem(
					_getAIHubCellConfiguration(), CompanyConstants.SYSTEM);

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					AIHubCellAccessTokenWebCacheItem.class.getName(),
					LoggerTestUtil.OFF)) {

				Assert.assertNull(
					aiHubCellAccessTokenWebCacheItem.convert(StringPool.BLANK));
			}
		}
	}

	@Test
	public void testGetWhenCachedAccessTokenIsExpired() throws Exception {
		try (MockedStatic<WebCachePoolUtil> webCachePoolUtilMockedStatic =
				Mockito.mockStatic(WebCachePoolUtil.class)) {

			JSONObject expiredJSONObject = JSONUtil.put(
				"access_token",
				_createAccessToken(System.currentTimeMillis() - Time.MINUTE));
			JSONObject freshJSONObject = JSONUtil.put(
				"access_token",
				_createAccessToken(System.currentTimeMillis() + Time.HOUR));

			webCachePoolUtilMockedStatic.when(
				() -> WebCachePoolUtil.get(
					Mockito.anyString(), Mockito.any(WebCacheItem.class))
			).thenReturn(
				expiredJSONObject, freshJSONObject
			);

			Assert.assertSame(
				freshJSONObject,
				AIHubCellAccessTokenWebCacheItem.get(
					_getAIHubCellConfiguration(), RandomTestUtil.randomLong()));

			webCachePoolUtilMockedStatic.verify(
				() -> WebCachePoolUtil.remove(Mockito.anyString()));
		}
	}

	@Test
	public void testGetWhenCachedAccessTokenIsNotExpired() throws Exception {
		try (MockedStatic<WebCachePoolUtil> webCachePoolUtilMockedStatic =
				Mockito.mockStatic(WebCachePoolUtil.class)) {

			JSONObject freshJSONObject = JSONUtil.put(
				"access_token",
				_createAccessToken(System.currentTimeMillis() + Time.HOUR));

			webCachePoolUtilMockedStatic.when(
				() -> WebCachePoolUtil.get(
					Mockito.anyString(), Mockito.any(WebCacheItem.class))
			).thenReturn(
				freshJSONObject
			);

			Assert.assertSame(
				freshJSONObject,
				AIHubCellAccessTokenWebCacheItem.get(
					_getAIHubCellConfiguration(), RandomTestUtil.randomLong()));

			webCachePoolUtilMockedStatic.verify(
				() -> WebCachePoolUtil.remove(Mockito.anyString()),
				Mockito.never());
		}
	}

	private String _createAccessToken(long expirationTime) throws Exception {
		SignedJWT signedJWT = new SignedJWT(
			new JWSHeader(JWSAlgorithm.HS256),
			new JWTClaimsSet.Builder(
			).expirationTime(
				new Date(expirationTime)
			).build());

		signedJWT.sign(new MACSigner(new byte[32]));

		return signedJWT.serialize();
	}

	private AIHubCellConfiguration _getAIHubCellConfiguration() {
		AIHubCellConfiguration aiHubCellConfiguration = Mockito.mock(
			AIHubCellConfiguration.class);

		Mockito.when(
			aiHubCellConfiguration.clientId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			aiHubCellConfiguration.clientSecret()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			aiHubCellConfiguration.serviceURL()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return aiHubCellConfiguration;
	}

	private MockedStatic<HttpUtil> _mockHttpUtil(String responseJSON) {
		MockedStatic<HttpUtil> httpUtilMockedStatic = Mockito.mockStatic(
			HttpUtil.class);

		httpUtilMockedStatic.when(
			() -> HttpUtil.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocationOnMock -> {
				Http.Options options = invocationOnMock.getArgument(0);

				Http.Response response = new Http.Response();

				response.setResponseCode(HttpURLConnection.HTTP_OK);

				options.setResponse(response);

				return responseJSON;
			}
		);

		return httpUtilMockedStatic;
	}

	private Snapshot<SecretResolver> _secretResolverSnapshot;

}