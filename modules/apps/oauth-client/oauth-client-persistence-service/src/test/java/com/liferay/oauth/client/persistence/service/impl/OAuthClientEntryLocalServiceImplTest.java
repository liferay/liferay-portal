/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryAuthRequestParametersJSONException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryAuthServerWellKnownURIException;
import com.liferay.oauth.client.persistence.exception.OAuthClientEntryTokenRequestParametersJSONException;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.net.HttpURLConnection;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Rafael Praxedes
 * @author Alvaro Saugar
 */
public class OAuthClientEntryLocalServiceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testValidateAuthRequestParametersJSON() throws Exception {
		_validateAuthRequestParametersJSON("{}");

		Assert.assertThrows(
			OAuthClientEntryAuthRequestParametersJSONException.class,
			() -> _validateAuthRequestParametersJSON(
				JSONUtil.put(
					"allow_upstream_token_forwarding", true
				).toString()));
	}

	@Test
	public void testValidateAuthServerWellKnownURI() throws Exception {
		_testValidateAuthServerWellKnownURI();
		_testValidateAuthServerWellKnownURIWithCarrierGradeNATHost();
		_testValidateAuthServerWellKnownURIWithFileScheme();
		_testValidateAuthServerWellKnownURIWithHostAllowedByWildcard();
		_testValidateAuthServerWellKnownURIWithHostAllowedIgnoringPort();
		_testValidateAuthServerWellKnownURIWithHostNotAllowed();
		_testValidateAuthServerWellKnownURIWithLinkLocalHost();
		_testValidateAuthServerWellKnownURIWithLocalNetworkAccessDisabled();
		_testValidateAuthServerWellKnownURIWithLocalWellKnownURI();
		_testValidateAuthServerWellKnownURIWithMulticastHost();
		_testValidateAuthServerWellKnownURIWithRedirect();
		_testValidateAuthServerWellKnownURIWithRedirectToHostNotAllowed();
		_testValidateAuthServerWellKnownURIWithRelativeRedirect();
		_testValidateAuthServerWellKnownURIWithTooManyRedirects();
		_testValidateAuthServerWellKnownURIWithUniqueLocalHost();
	}

	@Test
	public void testValidateTokenRequestParametersJSON() throws Exception {
		_validateTokenRequestParametersJSON(
			JSONUtil.put(
				"allow_upstream_token_forwarding", false
			).toString());
		_validateTokenRequestParametersJSON(
			JSONUtil.put(
				"allow_upstream_token_forwarding", true
			).toString());
		_validateTokenRequestParametersJSON("{}");

		Assert.assertThrows(
			OAuthClientEntryTokenRequestParametersJSONException.class,
			() -> _validateTokenRequestParametersJSON(
				JSONUtil.put(
					"allow_upstream_token_forwarding",
					RandomTestUtil.randomString()
				).toString()));
	}

	private OAuthClientEntryLocalServiceImpl
			_createOAuthClientEntryLocalServiceImpl(
				String[] authServerHostsAllowed,
				boolean authServerLocalNetworkAccessEnabled, Http http)
		throws Exception {

		OAuthClientEntryLocalServiceImpl oAuthClientEntryLocalServiceImpl =
			new OAuthClientEntryLocalServiceImpl();

		OAuthClientCompanyConfiguration oAuthClientCompanyConfiguration =
			Mockito.mock(OAuthClientCompanyConfiguration.class);

		Mockito.when(
			oAuthClientCompanyConfiguration.authServerHostsAllowed()
		).thenReturn(
			authServerHostsAllowed
		);

		Mockito.when(
			oAuthClientCompanyConfiguration.
				authServerLocalNetworkAccessEnabled()
		).thenReturn(
			authServerLocalNetworkAccessEnabled
		);

		ConfigurationProvider configurationProvider = Mockito.mock(
			ConfigurationProvider.class);

		Mockito.when(
			configurationProvider.getCompanyConfiguration(
				Mockito.eq(OAuthClientCompanyConfiguration.class),
				Mockito.anyLong())
		).thenReturn(
			oAuthClientCompanyConfiguration
		);

		ReflectionTestUtil.setFieldValue(
			oAuthClientEntryLocalServiceImpl, "_configurationProvider",
			configurationProvider);

		ReflectionTestUtil.setFieldValue(
			oAuthClientEntryLocalServiceImpl, "_http", http);

		return oAuthClientEntryLocalServiceImpl;
	}

	private String _getRandomHost(int octetsCount, String prefix) {
		StringBundler sb = new StringBundler(octetsCount * 2);

		sb.append(prefix);

		for (int i = 0; i < octetsCount; i++) {
			if (i > 0) {
				sb.append(StringPool.PERIOD);
			}

			sb.append(RandomTestUtil.randomInt(0, 255));
		}

		return sb.toString();
	}

	private String _getWellKnownURI(String host) {
		return Http.HTTP_WITH_SLASH + host + _WELL_KNOWN_PATH;
	}

	private Http _mockHttp(Http.Response... httpResponses) throws Exception {
		Http http = Mockito.mock(Http.class);

		if (httpResponses.length == 0) {
			return http;
		}

		AtomicInteger atomicInteger = new AtomicInteger();

		Mockito.when(
			http.URLtoString(Mockito.any(Http.Options.class))
		).thenAnswer(
			invocation -> {
				Http.Options httpOptions = invocation.getArgument(0);

				int index = Math.min(
					atomicInteger.getAndIncrement(), httpResponses.length - 1);

				httpOptions.setResponse(httpResponses[index]);

				return "{}";
			}
		);

		return http;
	}

	private Http.Response _mockHttpResponse(String redirect, int responseCode) {
		Http.Response httpResponse = new Http.Response();

		httpResponse.setRedirect(redirect);
		httpResponse.setResponseCode(responseCode);

		return httpResponse;
	}

	private void _testValidateAuthServerWellKnownURI() throws Exception {
		Http http = _mockHttp(
			_mockHttpResponse(null, HttpURLConnection.HTTP_OK));

		_validateAuthServerWellKnownURI(
			_getWellKnownURI(_getRandomHost(3, _HOST_PREFIX_LOOPBACK)),
			_createOAuthClientEntryLocalServiceImpl(null, true, http));

		ArgumentCaptor<Http.Options> argumentCaptor = ArgumentCaptor.forClass(
			Http.Options.class);

		Mockito.verify(
			http
		).URLtoString(
			argumentCaptor.capture()
		);

		Http.Options httpOptions = argumentCaptor.getValue();

		Assert.assertEquals(
			Http.CookieSpec.STANDARD, httpOptions.getCookieSpec());
		Assert.assertFalse(httpOptions.isFollowRedirects());
	}

	private void _testValidateAuthServerWellKnownURIWithCarrierGradeNATHost()
		throws Exception {

		Http http = _mockHttp();

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				_getWellKnownURI(
					_getRandomHost(2, _HOST_PREFIX_CARRIER_GRADE_NAT)),
				_createOAuthClientEntryLocalServiceImpl(null, false, http)));

		Mockito.verifyNoInteractions(http);
	}

	private void _testValidateAuthServerWellKnownURIWithFileScheme()
		throws Exception {

		Http http = _mockHttp();

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				"file:///" + RandomTestUtil.randomString(),
				_createOAuthClientEntryLocalServiceImpl(null, true, http)));

		Mockito.verifyNoInteractions(http);
	}

	private void _testValidateAuthServerWellKnownURIWithHostAllowedByWildcard()
		throws Exception {

		Http http = _mockHttp(
			_mockHttpResponse(null, HttpURLConnection.HTTP_OK));

		_validateAuthServerWellKnownURI(
			_getWellKnownURI(_getRandomHost(3, _HOST_PREFIX_LOOPBACK)),
			_createOAuthClientEntryLocalServiceImpl(
				new String[] {StringPool.STAR}, true, http));

		Mockito.verify(
			http
		).URLtoString(
			Mockito.any(Http.Options.class)
		);
	}

	private void _testValidateAuthServerWellKnownURIWithHostAllowedIgnoringPort()
		throws Exception {

		String host = _getRandomHost(3, _HOST_PREFIX_LOOPBACK);
		Http http = _mockHttp(
			_mockHttpResponse(null, HttpURLConnection.HTTP_OK));

		_validateAuthServerWellKnownURI(
			StringBundler.concat(
				Http.HTTPS_WITH_SLASH, host, StringPool.COLON,
				RandomTestUtil.randomInt(32768, 65535), _WELL_KNOWN_PATH),
			_createOAuthClientEntryLocalServiceImpl(
				new String[] {
					StringBundler.concat(
						host, StringPool.COLON,
						RandomTestUtil.randomInt(1, 32767))
				},
				true, http));

		Mockito.verify(
			http
		).URLtoString(
			Mockito.any(Http.Options.class)
		);
	}

	private void _testValidateAuthServerWellKnownURIWithHostNotAllowed()
		throws Exception {

		Http http = _mockHttp();

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				_getWellKnownURI(_getRandomHost(3, _HOST_PREFIX_LOOPBACK)),
				_createOAuthClientEntryLocalServiceImpl(
					new String[] {_getRandomHost(3, _HOST_PREFIX_PRIVATE)},
					true, http)));

		Mockito.verifyNoInteractions(http);
	}

	private void _testValidateAuthServerWellKnownURIWithLinkLocalHost()
		throws Exception {

		Http http = _mockHttp();

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				_getWellKnownURI(_getRandomHost(2, _HOST_PREFIX_LINK_LOCAL)),
				_createOAuthClientEntryLocalServiceImpl(null, false, http)));

		Mockito.verifyNoInteractions(http);
	}

	private void _testValidateAuthServerWellKnownURIWithLocalNetworkAccessDisabled()
		throws Exception {

		String host = _getRandomHost(3, _HOST_PREFIX_LOOPBACK);
		Http http = _mockHttp();

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				_getWellKnownURI(host),
				_createOAuthClientEntryLocalServiceImpl(
					new String[] {host}, false, http)));

		Mockito.verifyNoInteractions(http);
	}

	private void _testValidateAuthServerWellKnownURIWithLocalWellKnownURI()
		throws Exception {

		Http http = _mockHttp();

		OAuthClientEntryLocalServiceImpl oAuthClientEntryLocalServiceImpl =
			_createOAuthClientEntryLocalServiceImpl(null, false, http);

		ReflectionTestUtil.setFieldValue(
			oAuthClientEntryLocalServiceImpl,
			"_oAuthClientASLocalMetadataLocalService",
			Mockito.mock(OAuthClientASLocalMetadataLocalService.class));

		_validateAuthServerWellKnownURI(
			_getWellKnownURI(_getRandomHost(3, _HOST_PREFIX_LOOPBACK)) +
				"/local",
			oAuthClientEntryLocalServiceImpl);

		Mockito.verifyNoInteractions(http);
	}

	private void _testValidateAuthServerWellKnownURIWithMulticastHost()
		throws Exception {

		Http http = _mockHttp();

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				_getWellKnownURI(_getRandomHost(3, _HOST_PREFIX_MULTICAST)),
				_createOAuthClientEntryLocalServiceImpl(null, true, http)));

		Mockito.verifyNoInteractions(http);
	}

	private void _testValidateAuthServerWellKnownURIWithRedirect()
		throws Exception {

		String host = _getRandomHost(3, _HOST_PREFIX_LOOPBACK);

		Http http = _mockHttp(
			_mockHttpResponse(
				_getWellKnownURI(host), HttpURLConnection.HTTP_MOVED_TEMP),
			_mockHttpResponse(null, HttpURLConnection.HTTP_OK));

		_validateAuthServerWellKnownURI(
			_getWellKnownURI(host),
			_createOAuthClientEntryLocalServiceImpl(
				new String[] {host}, true, http));

		Mockito.verify(
			http, Mockito.times(2)
		).URLtoString(
			Mockito.any(Http.Options.class)
		);
	}

	private void _testValidateAuthServerWellKnownURIWithRedirectToHostNotAllowed()
		throws Exception {

		String host = _getRandomHost(3, _HOST_PREFIX_LOOPBACK);
		Http http = _mockHttp(
			_mockHttpResponse(
				_getWellKnownURI(_getRandomHost(3, _HOST_PREFIX_PRIVATE)),
				HttpURLConnection.HTTP_MOVED_TEMP));

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				_getWellKnownURI(host),
				_createOAuthClientEntryLocalServiceImpl(
					new String[] {host}, true, http)));

		Mockito.verify(
			http
		).URLtoString(
			Mockito.any(Http.Options.class)
		);
	}

	private void _testValidateAuthServerWellKnownURIWithRelativeRedirect()
		throws Exception {

		String host = _getRandomHost(3, _HOST_PREFIX_LOOPBACK);
		Http http = _mockHttp(
			_mockHttpResponse(
				_WELL_KNOWN_PATH, HttpURLConnection.HTTP_MOVED_TEMP),
			_mockHttpResponse(null, HttpURLConnection.HTTP_OK));

		_validateAuthServerWellKnownURI(
			_getWellKnownURI(host),
			_createOAuthClientEntryLocalServiceImpl(
				new String[] {host}, true, http));

		Mockito.verify(
			http, Mockito.times(2)
		).URLtoString(
			Mockito.any(Http.Options.class)
		);
	}

	private void _testValidateAuthServerWellKnownURIWithTooManyRedirects()
		throws Exception {

		String host = _getRandomHost(3, _HOST_PREFIX_LOOPBACK);
		Http http = _mockHttp(
			_mockHttpResponse(
				_WELL_KNOWN_PATH, HttpURLConnection.HTTP_MOVED_TEMP));

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				_getWellKnownURI(host),
				_createOAuthClientEntryLocalServiceImpl(
					new String[] {host}, true, http)));

		Mockito.verify(
			http, Mockito.times(_MAXIMUM_REDIRECTS + 1)
		).URLtoString(
			Mockito.any(Http.Options.class)
		);
	}

	private void _testValidateAuthServerWellKnownURIWithUniqueLocalHost()
		throws Exception {

		Http http = _mockHttp();

		Assert.assertThrows(
			OAuthClientEntryAuthServerWellKnownURIException.class,
			() -> _validateAuthServerWellKnownURI(
				_getWellKnownURI(
					StringBundler.concat(
						"[fd",
						Integer.toHexString(RandomTestUtil.randomInt(16, 255)),
						StringPool.COLON,
						Integer.toHexString(
							RandomTestUtil.randomInt(16, 65535)),
						"::1]")),
				_createOAuthClientEntryLocalServiceImpl(null, false, http)));

		Mockito.verifyNoInteractions(http);
	}

	private void _validateAuthRequestParametersJSON(
			String authRequestParametersJSON)
		throws Exception {

		ReflectionTestUtil.invoke(
			new OAuthClientEntryLocalServiceImpl(),
			"_validateAuthRequestParametersJSON", new Class<?>[] {String.class},
			authRequestParametersJSON);
	}

	private void _validateAuthServerWellKnownURI(
		String authServerWellKnownURI,
		OAuthClientEntryLocalServiceImpl oAuthClientEntryLocalServiceImpl) {

		ReflectionTestUtil.invoke(
			oAuthClientEntryLocalServiceImpl, "_validateAuthServerWellKnownURI",
			new Class<?>[] {long.class, String.class},
			RandomTestUtil.randomLong(), authServerWellKnownURI);
	}

	private void _validateTokenRequestParametersJSON(
			String tokenRequestParametersJSON)
		throws Exception {

		ReflectionTestUtil.invoke(
			new OAuthClientEntryLocalServiceImpl(),
			"_validateTokenRequestParametersJSON",
			new Class<?>[] {String.class}, tokenRequestParametersJSON);
	}

	private static final String _HOST_PREFIX_CARRIER_GRADE_NAT = "100.64.";

	private static final String _HOST_PREFIX_LINK_LOCAL = "169.254.";

	private static final String _HOST_PREFIX_LOOPBACK = "127.";

	private static final String _HOST_PREFIX_MULTICAST = "224.";

	private static final String _HOST_PREFIX_PRIVATE = "10.";

	private static final int _MAXIMUM_REDIRECTS = 5;

	private static final String _WELL_KNOWN_PATH =
		"/.well-known/openid-configuration";

}