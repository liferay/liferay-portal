/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.servlet;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnect;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalService;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.claims.LogoutTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Lucas Miranda
 */
public class OpenIdConnectBackchannelLogoutServletTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_openIdConnectBackchannelLogoutServlet = Mockito.spy(
			new OpenIdConnectBackchannelLogoutServlet());

		OpenIdConnect openIdConnect = Mockito.mock(OpenIdConnect.class);

		Mockito.when(
			openIdConnect.isEnabled(Mockito.anyLong())
		).thenReturn(
			true
		);

		ReflectionTestUtils.setField(
			_openIdConnectBackchannelLogoutServlet, "_openIdConnect",
			openIdConnect);

		ReflectionTestUtils.setField(
			_openIdConnectBackchannelLogoutServlet,
			"_openIdConnectSessionLocalService",
			_openIdConnectSessionLocalService);

		Mockito.doReturn(
			"http://mocked.jwks.uri/key-set.json"
		).when(
			_openIdConnectBackchannelLogoutServlet
		).getJWKSURI(
			new Issuer(_ISSUER_URL)
		);
	}

	@Test
	public void testDoPost() throws Exception {
		OpenIdConnectSession openIdConnectSession = _mockOpenIdConnectSession(
			_createSignedJWT(false));

		LogoutTokenClaimsSet logoutTokenClaimsSet = new LogoutTokenClaimsSet(
			new JWTClaimsSet.Builder(
			).audience(
				Collections.singletonList(_CLIENT_ID)
			).claim(
				"events",
				HashMapBuilder.put(
					"http://schemas.openid.net/event/backchannel-logout",
					Collections.emptyMap()
				).build()
			).claim(
				"sid", _SESSION_ID
			).expirationTime(
				new Date(System.currentTimeMillis() + 60000)
			).issuer(
				_ISSUER_URL
			).issueTime(
				new Date()
			).jwtID(
				String.valueOf(UUID.randomUUID())
			).build());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		SignedJWT signedJWT = _createSignedJWT(true);

		mockHttpServletRequest.setParameter(
			"logout_token", signedJWT.serialize());

		HttpServletResponse httpServletResponse = Mockito.mock(
			HttpServletResponse.class);

		try (MockedConstruction<LogoutTokenValidator> mockedConstruction =
				Mockito.mockConstruction(
					LogoutTokenValidator.class,
					(mock, context) -> Mockito.when(
						mock.validate(Mockito.any(JWT.class))
					).thenReturn(
						logoutTokenClaimsSet
					))) {

			_openIdConnectBackchannelLogoutServlet.doPost(
				mockHttpServletRequest, httpServletResponse);

			List<LogoutTokenValidator> logoutTokenValidators =
				mockedConstruction.constructed();

			Mockito.verify(
				logoutTokenValidators.get(0)
			).validate(
				Mockito.any(JWT.class)
			);
		}

		Mockito.verify(
			_openIdConnectSessionLocalService
		).deleteOpenIdConnectSession(
			Mockito.eq(openIdConnectSession)
		);

		Mockito.verify(
			httpServletResponse
		).setStatus(
			HttpServletResponse.SC_OK
		);
	}

	@Test
	public void testDoPostWithInvalidToken() throws Exception {
		_testDoPostWithInvalidToken(RandomTestUtil.randomString());

		_mockOpenIdConnectSession(_createSignedJWT(false));

		SignedJWT signedJWT = _createSignedJWT(true);

		_testDoPostWithInvalidToken(signedJWT.serialize());
	}

	private SignedJWT _createSignedJWT(boolean logoutToken) throws Exception {
		Date now = new Date();

		JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder(
		).audience(
			_CLIENT_ID
		).claim(
			"sid", _SESSION_ID
		).expirationTime(
			new Date(now.getTime() + 60_000)
		).issuer(
			_ISSUER_URL
		).issueTime(
			now
		).subject(
			_SUBJECT
		);

		if (logoutToken) {
			claimsBuilder.claim(
				"events",
				JSONUtil.put(
					"http://schemas.openid.net/event/backchannel-logout", "{}")
			).claim(
				"typ", "Logout"
			).jwtID(
				String.valueOf(UUID.randomUUID())
			);
		}
		else {
			claimsBuilder.claim("typ", "ID");
		}

		JWSHeader jwsHeader = new JWSHeader.Builder(
			_JWS_ALGORITHM
		).keyID(
			_KEY_ID
		).build();

		SignedJWT token = new SignedJWT(jwsHeader, claimsBuilder.build());

		RSAKey rsaKey = new RSAKeyGenerator(
			2048
		).keyID(
			_KEY_ID
		).generate();

		token.sign(new RSASSASigner(rsaKey.toPrivateKey()));

		return token;
	}

	private OpenIdConnectSession _mockOpenIdConnectSession(
		SignedJWT signedJWT) {

		OpenIdConnectSession openIdConnectSession = Mockito.mock(
			OpenIdConnectSession.class);

		Mockito.when(
			_openIdConnectSessionLocalService.fetchOpenIdConnectSession(
				Mockito.any(), Mockito.eq(_SESSION_ID))
		).thenReturn(
			openIdConnectSession
		);

		Mockito.when(
			openIdConnectSession.getIdToken()
		).thenReturn(
			signedJWT.serialize()
		);

		Mockito.when(
			openIdConnectSession.getClientId()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return openIdConnectSession;
	}

	private void _testDoPostWithInvalidToken(String logoutToken)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("logout_token", logoutToken);

		HttpServletResponse httpServletResponse = Mockito.mock(
			HttpServletResponse.class);

		try {
			_openIdConnectBackchannelLogoutServlet.doPost(
				mockHttpServletRequest, httpServletResponse);

			Assert.fail();
		}
		catch (Exception exception) {
			Mockito.verify(
				httpServletResponse
			).setStatus(
				HttpServletResponse.SC_BAD_REQUEST
			);
		}
	}

	private static final String _CLIENT_ID = "liferay";

	private static final String _ISSUER_URL =
		"http://localhost:8180/realms/lucas";

	private static final JWSAlgorithm _JWS_ALGORITHM = JWSAlgorithm.RS256;

	private static final String _KEY_ID = "test-kid-logout";

	private static final String _SESSION_ID =
		"055bbd56-a07a-43a4-bb57-03bb42543e7d";

	private static final String _SUBJECT =
		"98259e32-a701-41fa-9dc5-719e00182326";

	private OpenIdConnectBackchannelLogoutServlet
		_openIdConnectBackchannelLogoutServlet;
	private final OpenIdConnectSessionLocalService
		_openIdConnectSessionLocalService = Mockito.mock(
			OpenIdConnectSessionLocalService.class);

}