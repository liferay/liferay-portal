/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token.grant.handler;

import com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration;
import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.oauth2.provider.rest.internal.endpoint.liferay.LiferayOAuthDataProvider;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;

import java.util.Collections;
import java.util.Map;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.jose.common.JoseType;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactProducer;
import org.apache.cxf.rs.security.jose.jws.JwsSignatureVerifier;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtConstants;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.jwt.Constants;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Allen Ziegenfus
 */
public class LiferayJWTBearerGrantHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

		keyPairGenerator.initialize(2048);

		_keyPair = keyPairGenerator.generateKeyPair();

		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getPortalURL(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			"https://" + _HOST
		);

		portalUtil.setPortal(portal);
	}

	@Before
	public void setUp() {
		Mockito.when(
			_liferayOAuthDataProvider.createAccessToken(
				Mockito.any(AccessTokenRegistration.class))
		).thenReturn(
			_serverAccessToken
		);

		ReflectionTestUtil.setFieldValue(
			_liferayJWTBearerGrantHandler, "_liferayOAuthDataProvider",
			_liferayOAuthDataProvider);

		OAuth2ProviderConfiguration oAuth2ProviderConfiguration = Mockito.mock(
			OAuth2ProviderConfiguration.class);

		Mockito.when(
			oAuth2ProviderConfiguration.allowJWTBearerGrant()
		).thenReturn(
			true
		);

		ReflectionTestUtil.setFieldValue(
			_liferayJWTBearerGrantHandler, "_oAuth2ProviderConfiguration",
			oAuth2ProviderConfiguration);

		Map<Long, Map<String, Map<String, JwsSignatureVerifier>>>
			jwsSignatureVerifiers = ReflectionTestUtil.getFieldValue(
				_liferayJWTBearerGrantHandler, "_jwsSignatureVerifiers");

		jwsSignatureVerifiers.put(
			_COMPANY_ID,
			Collections.singletonMap(
				_ISSUER,
				Collections.singletonMap(
					_KEY_ID,
					JwsUtils.getSignatureVerifier(
						JwkUtils.fromRSAPublicKey(
							(RSAPublicKey)_keyPair.getPublic(), "RS256",
							_KEY_ID)))));

		Map<Long, Map<String, String>> userAuthTypes =
			ReflectionTestUtil.getFieldValue(
				_liferayJWTBearerGrantHandler, "_userAuthTypes");

		userAuthTypes.put(
			_COMPANY_ID,
			Collections.singletonMap(_ISSUER, CompanyConstants.AUTH_TYPE_EA));

		UserLocalService userLocalService = Mockito.mock(
			UserLocalService.class);

		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getScreenName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			user.getUserId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			userLocalService.fetchUserByEmailAddress(_COMPANY_ID, _SUBJECT)
		).thenReturn(
			user
		);

		_liferayJWTBearerGrantHandler.userLocalService = userLocalService;
	}

	@After
	public void tearDown() {
		ThreadLocal<Message> threadLocal = _getCurrentMessageThreadLocal();

		threadLocal.remove();
	}

	@Test
	public void testCreateAccessToken() {
		Assert.assertSame(
			_serverAccessToken,
			_createAccessToken(
				_getJwtClaims("https://" + _HOST + _TOKEN_PATH)));

		Message message = PhaseInterceptorChain.getCurrentMessage();

		Assert.assertNull(message.get(JwtConstants.EXPECTED_CLAIM_AUDIENCE));
	}

	@Test
	public void testCreateAccessTokenWithRequestURLAudience() {
		Assert.assertSame(
			_serverAccessToken,
			_createAccessToken(_getJwtClaims("http://" + _HOST + _TOKEN_PATH)));
	}

	@Test
	public void testCreateAccessTokenWithWrongAudience() {
		Assert.assertThrows(
			OAuthServiceException.class,
			() -> _createAccessToken(
				_getJwtClaims(
					"https://" + RandomTestUtil.randomString() + _TOKEN_PATH)));
	}

	private ServerAccessToken _createAccessToken(JwtClaims jwtClaims) {
		Message message = new MessageImpl();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getRequestURI()
		).thenReturn(
			_TOKEN_PATH
		);

		message.put(AbstractHTTPDestination.HTTP_REQUEST, httpServletRequest);

		message.put(Message.REQUEST_URL, "http://" + _HOST + _TOKEN_PATH);

		ThreadLocal<Message> threadLocal = _getCurrentMessageThreadLocal();

		threadLocal.set(message);

		Client client = new Client(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true);

		client.setProperties(
			Collections.singletonMap(
				OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_COMPANY_ID,
				String.valueOf(_COMPANY_ID)));

		JwsHeaders jwsHeaders = new JwsHeaders(
			JoseType.JWT, SignatureAlgorithm.RS256);

		jwsHeaders.setKeyId(_KEY_ID);

		JwsJwtCompactProducer jwsJwtCompactProducer = new JwsJwtCompactProducer(
			new JwtToken(jwsHeaders, jwtClaims));

		MultivaluedMap<String, String> multivaluedMap =
			new MultivaluedHashMap<>();

		multivaluedMap.putSingle(
			Constants.CLIENT_GRANT_ASSERTION_PARAM,
			jwsJwtCompactProducer.signWith(_keyPair.getPrivate()));

		return _liferayJWTBearerGrantHandler.createAccessToken(
			client, multivaluedMap);
	}

	private ThreadLocal<Message> _getCurrentMessageThreadLocal() {
		return ReflectionTestUtil.getFieldValue(
			PhaseInterceptorChain.class, "CURRENT_MESSAGE");
	}

	private JwtClaims _getJwtClaims(String audience) {
		JwtClaims jwtClaims = new JwtClaims();

		jwtClaims.setAudience(audience);
		jwtClaims.setIssuedAt(OAuthUtils.getIssuedAt());
		jwtClaims.setExpiryTime(jwtClaims.getIssuedAt() + 3600);
		jwtClaims.setIssuer(_ISSUER);
		jwtClaims.setSubject(_SUBJECT);

		return jwtClaims;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _HOST = RandomTestUtil.randomString();

	private static final String _ISSUER = RandomTestUtil.randomString();

	private static final String _KEY_ID = RandomTestUtil.randomString();

	private static final String _SUBJECT = RandomTestUtil.randomString();

	private static final String _TOKEN_PATH = "/o/oauth2/token";

	private static KeyPair _keyPair;

	private final LiferayJWTBearerGrantHandler _liferayJWTBearerGrantHandler =
		new LiferayJWTBearerGrantHandler();
	private final LiferayOAuthDataProvider _liferayOAuthDataProvider =
		Mockito.mock(LiferayOAuthDataProvider.class);
	private final ServerAccessToken _serverAccessToken = Mockito.mock(
		ServerAccessToken.class);

}