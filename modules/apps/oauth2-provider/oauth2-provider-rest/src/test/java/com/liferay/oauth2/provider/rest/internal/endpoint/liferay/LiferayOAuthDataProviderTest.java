/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.liferay;

import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.rest.internal.endpoint.jwks.LiferayJWKSService;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.core.Response;

import java.lang.reflect.Method;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Farias
 * @author Pedro Victor Silvestre
 */
public class LiferayOAuthDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_liferayOAuthDataProvider, "_oAuth2AuthorizationLocalService",
			_oAuth2AuthorizationLocalService);
	}

	@Test
	public void testProcessJwtAccessToken() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

		keyPairGenerator.initialize(2048);

		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		JsonWebKey jsonWebKey = JwkUtils.fromRSAPrivateKey(
			(RSAPrivateKey)keyPair.getPrivate(), "RS256");

		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));

		SecretResolver secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			secretResolver.resolve(CompanyConstants.SYSTEM, keyReferenceString)
		).thenReturn(
			JwkUtils.jwkKeyToJson(jsonWebKey)
		);

		ReflectionTestUtil.setFieldValue(
			_liferayOAuthDataProvider, "_secretResolver", secretResolver);

		_liferayOAuthDataProvider.activate(
			HashMapBuilder.<String, Object>put(
				"oauth2.authorization.server.jwt.access.token.signing.json." +
					"web.key",
				keyReferenceString
			).build());

		JwtClaims jwtClaims = new JwtClaims();

		jwtClaims.setSubject(RandomTestUtil.randomString());

		JwsJwtCompactConsumer jwsJwtCompactConsumer = new JwsJwtCompactConsumer(
			_liferayOAuthDataProvider.processJwtAccessToken(jwtClaims));

		LiferayJWKSService liferayJWKSService = new LiferayJWKSService();

		ReflectionTestUtil.setFieldValue(
			liferayJWKSService, "_secretResolver", secretResolver);

		Method method = ReflectionUtil.getDeclaredMethod(
			LiferayJWKSService.class, "activate", Map.class);

		method.invoke(
			liferayJWKSService,
			HashMapBuilder.<String, Object>put(
				"oauth2.authorization.server.jwt.access.token.signing.json." +
					"web.key",
				keyReferenceString
			).build());

		Response response = liferayJWKSService.jwks();

		JsonWebKeys jsonWebKeys = JwkUtils.readJwkSet(
			(String)response.getEntity());

		List<JsonWebKey> jsonWebKeysList = jsonWebKeys.getKeys();

		Assert.assertTrue(
			jwsJwtCompactConsumer.verifySignatureWith(
				JwsUtils.getSignatureVerifier(jsonWebKeysList.get(0))));
	}

	@Test
	public void testSaveRefreshToken() throws Exception {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"FIPS_ENABLED", true)) {

			String accessToken = RandomTestUtil.randomString();

			Mockito.when(
				_oAuth2AuthorizationLocalService.
					fetchOAuth2AuthorizationByAccessTokenContent(accessToken)
			).thenReturn(
				_oAuth2Authorization
			);

			String refreshTokenKey = RandomTestUtil.randomString();

			RefreshToken refreshToken = new RefreshToken(
				new Client(), refreshTokenKey, 3600,
				System.currentTimeMillis());

			refreshToken.setAccessTokens(
				Collections.singletonList(accessToken));

			_liferayOAuthDataProvider.saveRefreshToken(refreshToken);

			Mockito.verify(
				_oAuth2Authorization
			).setRefreshTokenContent(
				DigesterUtil.digestHex(DigesterUtil.SHA_256, refreshTokenKey)
			);

			Mockito.verify(
				_oAuth2AuthorizationLocalService
			).updateOAuth2Authorization(
				_oAuth2Authorization
			);
		}
	}

	private final LiferayOAuthDataProvider _liferayOAuthDataProvider =
		new LiferayOAuthDataProvider();
	private final OAuth2Authorization _oAuth2Authorization = Mockito.mock(
		OAuth2Authorization.class);
	private final OAuth2AuthorizationLocalService
		_oAuth2AuthorizationLocalService = Mockito.mock(
			OAuth2AuthorizationLocalService.class);

}