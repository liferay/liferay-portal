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
import com.nimbusds.openid.connect.sdk.claims.LogoutTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;

import jakarta.servlet.http.HttpServletResponse;

import java.net.URL;

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
import org.springframework.mock.web.MockHttpServletResponse;
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
			new URL("http://mocked.jwks.uri/key-set.json")
		).when(
			_openIdConnectBackchannelLogoutServlet
		).getJWKSURL(
			Mockito.any(OpenIdConnectSession.class)
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

		JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder(
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
			RandomTestUtil.randomString()
		);

		if (logoutToken) {
			jwtClaimsSetBuilder.claim(
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
			jwtClaimsSetBuilder.claim("typ", "ID");
		}

		JWSHeader jwsHeader = new JWSHeader.Builder(
			JWSAlgorithm.RS256
		).keyID(
			_KEY_ID
		).build();

		SignedJWT signedJWT = new SignedJWT(
			jwsHeader, jwtClaimsSetBuilder.build());

		RSAKey rsaKey = new RSAKeyGenerator(
			2048
		).keyID(
			_KEY_ID
		).generate();

		signedJWT.sign(new RSASSASigner(rsaKey.toPrivateKey()));

		return signedJWT;
	}

	private OpenIdConnectSession _mockOpenIdConnectSession(SignedJWT signedJWT)
		throws Exception {

		OpenIdConnectSession openIdConnectSession = Mockito.mock(
			OpenIdConnectSession.class);

		Mockito.when(
			_openIdConnectSessionLocalService.getOpenIdConnectSession(
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

	private void _testDoPostWithInvalidToken(String logoutToken) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("logout_token", logoutToken);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_openIdConnectBackchannelLogoutServlet.doPost(
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_BAD_REQUEST,
			mockHttpServletResponse.getStatus());
	}

	private static final String _CLIENT_ID = RandomTestUtil.randomString();

	private static final String _ISSUER_URL = RandomTestUtil.randomString();

	private static final String _KEY_ID = RandomTestUtil.randomString();

	private static final String _SESSION_ID = RandomTestUtil.randomString();

	private final OpenIdConnectBackchannelLogoutServlet
		_openIdConnectBackchannelLogoutServlet = Mockito.spy(
			new OpenIdConnectBackchannelLogoutServlet());
	private final OpenIdConnectSessionLocalService
		_openIdConnectSessionLocalService = Mockito.mock(
			OpenIdConnectSessionLocalService.class);

}