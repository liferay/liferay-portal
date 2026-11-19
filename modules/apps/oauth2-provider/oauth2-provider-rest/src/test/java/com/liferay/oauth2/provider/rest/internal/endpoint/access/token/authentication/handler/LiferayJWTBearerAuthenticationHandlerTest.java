/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.access.token.authentication.handler;

import com.liferay.oauth2.provider.rest.internal.endpoint.constants.OAuth2ProviderRESTEndpointConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.UriInfo;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

import java.util.Collections;

import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.rs.security.jose.common.JoseType;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactProducer;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtConstants;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.grants.jwt.Constants;
import org.apache.cxf.rs.security.oauth2.provider.ClientRegistrationProvider;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
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
public class LiferayJWTBearerAuthenticationHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		_keyPair = _createKeyPair();

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
		ClientRegistrationProvider clientRegistrationProvider = Mockito.mock(
			ClientRegistrationProvider.class);

		Client client = new Client(_CLIENT_ID, null, false);

		client.setProperties(
			Collections.singletonMap(
				OAuth2ProviderRESTEndpointConstants.PROPERTY_KEY_CLIENT_JWKS,
				JwkUtils.jwkSetToJson(
					new JsonWebKeys(
						JwkUtils.fromRSAPublicKey(
							(RSAPublicKey)_keyPair.getPublic(), "RS256",
							_KEY_ID)))));
		client.setTokenEndpointAuthMethod("private_key_jwt");

		Mockito.when(
			clientRegistrationProvider.getClient(_CLIENT_ID)
		).thenReturn(
			client
		);

		_liferayJWTBearerAuthenticationHandler.setClientRegistrationProvider(
			clientRegistrationProvider);
	}

	@After
	public void tearDown() {
		ThreadLocal<Message> threadLocal = _getCurrentMessageThreadLocal();

		threadLocal.remove();
	}

	@Test
	public void testFilter() throws Exception {
		Message message = _filter(
			_getAssertion(_keyPair.getPrivate(), _getJwtClaims(_CLIENT_ID)));

		Assert.assertEquals(_CLIENT_ID, message.get(OAuthConstants.CLIENT_ID));
		Assert.assertNull(message.get(JwtConstants.EXPECTED_CLAIM_AUDIENCE));
	}

	@Test
	public void testFilterWithExpiredJWTAssertion() throws Exception {
		JwtClaims jwtClaims = _getJwtClaims(_CLIENT_ID);

		jwtClaims.setIssuedAt(jwtClaims.getIssuedAt() - 120);
		jwtClaims.setExpiryTime(jwtClaims.getIssuedAt() + 60);

		_assertInvalidClient(_getAssertion(_keyPair.getPrivate(), jwtClaims));
	}

	@Test
	public void testFilterWithMalformedJWTAssertion() throws Exception {
		_assertInvalidClient("a..b");
		_assertInvalidClient("a.b.c");
		_assertInvalidClient("eyJhbGciOiJSUzI1NiJ9.!!!.abc");
	}

	@Test
	public void testFilterWithRequestURLAudience() throws Exception {
		JwtClaims jwtClaims = _getJwtClaims(_CLIENT_ID);

		jwtClaims.setAudience("http://" + _HOST + _TOKEN_PATH);

		Message message = _filter(
			_getAssertion(_keyPair.getPrivate(), jwtClaims));

		Assert.assertEquals(_CLIENT_ID, message.get(OAuthConstants.CLIENT_ID));
	}

	@Test
	public void testFilterWithUnknownClientId() throws Exception {
		_assertInvalidClient(
			_getAssertion(
				_keyPair.getPrivate(),
				_getJwtClaims(RandomTestUtil.randomString())));
	}

	@Test
	public void testFilterWithWrongAudience() throws Exception {
		JwtClaims jwtClaims = _getJwtClaims(_CLIENT_ID);

		jwtClaims.setAudience(
			"https://" + RandomTestUtil.randomString() + _TOKEN_PATH);

		_assertInvalidClient(_getAssertion(_keyPair.getPrivate(), jwtClaims));
	}

	@Test
	public void testFilterWithWrongKey() throws Exception {
		KeyPair keyPair = _createKeyPair();

		_assertInvalidClient(
			_getAssertion(keyPair.getPrivate(), _getJwtClaims(_CLIENT_ID)));
	}

	@Test
	public void testFilterWithoutExpirationTime() throws Exception {
		JwtClaims jwtClaims = _getJwtClaims(_CLIENT_ID);

		jwtClaims.removeProperty(JwtConstants.CLAIM_EXPIRY);

		_assertInvalidClient(_getAssertion(_keyPair.getPrivate(), jwtClaims));
	}

	private static KeyPair _createKeyPair() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

		keyPairGenerator.initialize(2048);

		return keyPairGenerator.generateKeyPair();
	}

	private void _assertInvalidClient(String assertion) throws Exception {
		try {
			_filter(assertion);

			Assert.fail();
		}
		catch (NotAuthorizedException notAuthorizedException) {
			Assert.assertEquals(
				Collections.singletonList(OAuthConstants.INVALID_CLIENT),
				notAuthorizedException.getChallenges());
		}
	}

	private Message _filter(String assertion) throws Exception {
		Message message = new MessageImpl();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getParameter(
				Constants.CLIENT_AUTH_ASSERTION_PARAM)
		).thenReturn(
			assertion
		);

		Mockito.when(
			httpServletRequest.getParameter(
				Constants.CLIENT_AUTH_ASSERTION_TYPE)
		).thenReturn(
			Constants.CLIENT_AUTH_JWT_BEARER
		);

		Mockito.when(
			httpServletRequest.getRequestURI()
		).thenReturn(
			_TOKEN_PATH
		);

		message.put(AbstractHTTPDestination.HTTP_REQUEST, httpServletRequest);

		message.put(Message.REQUEST_URL, "http://" + _HOST + _TOKEN_PATH);

		ThreadLocal<Message> threadLocal = _getCurrentMessageThreadLocal();

		threadLocal.set(message);

		ContainerRequestContext containerRequestContext = Mockito.mock(
			ContainerRequestContext.class);

		UriInfo uriInfo = Mockito.mock(UriInfo.class);

		Mockito.when(
			uriInfo.getPath()
		).thenReturn(
			"token"
		);

		Mockito.when(
			containerRequestContext.getUriInfo()
		).thenReturn(
			uriInfo
		);

		_liferayJWTBearerAuthenticationHandler.filter(containerRequestContext);

		return message;
	}

	private String _getAssertion(PrivateKey privateKey, JwtClaims jwtClaims) {
		JwsHeaders jwsHeaders = new JwsHeaders(
			JoseType.JWT, SignatureAlgorithm.RS256);

		jwsHeaders.setKeyId(_KEY_ID);

		JwsJwtCompactProducer jwsJwtCompactProducer = new JwsJwtCompactProducer(
			new JwtToken(jwsHeaders, jwtClaims));

		return jwsJwtCompactProducer.signWith(privateKey);
	}

	private ThreadLocal<Message> _getCurrentMessageThreadLocal() {
		return ReflectionTestUtil.getFieldValue(
			PhaseInterceptorChain.class, "CURRENT_MESSAGE");
	}

	private JwtClaims _getJwtClaims(String clientId) {
		JwtClaims jwtClaims = new JwtClaims();

		jwtClaims.setAudience("https://" + _HOST + _TOKEN_PATH);
		jwtClaims.setIssuedAt(OAuthUtils.getIssuedAt());
		jwtClaims.setExpiryTime(jwtClaims.getIssuedAt() + 3600);
		jwtClaims.setIssuer(clientId);
		jwtClaims.setSubject(clientId);

		return jwtClaims;
	}

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	private static final String _HOST = RandomTestUtil.randomString();

	private static final String _KEY_ID = RandomTestUtil.randomString();

	private static final String _TOKEN_PATH = "/o/oauth2/token";

	private static KeyPair _keyPair;

	private final LiferayJWTBearerAuthenticationHandler
		_liferayJWTBearerAuthenticationHandler =
			new LiferayJWTBearerAuthenticationHandler();

}