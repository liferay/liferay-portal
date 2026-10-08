/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.liferay;

import com.liferay.oauth2.provider.rest.internal.endpoint.jwks.LiferayJWKSService;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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

import java.util.List;
import java.util.Map;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactConsumer;
import org.apache.cxf.rs.security.jose.jws.JwsUtils;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class LiferayOAuthDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

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

		LiferayOAuthDataProvider liferayOAuthDataProvider =
			new LiferayOAuthDataProvider();

		ReflectionTestUtil.setFieldValue(
			liferayOAuthDataProvider, "_secretResolver", secretResolver);

		liferayOAuthDataProvider.activate(
			HashMapBuilder.<String, Object>put(
				"oauth2.authorization.server.jwt.access.token.signing.json." +
					"web.key",
				keyReferenceString
			).build());

		JwtClaims jwtClaims = new JwtClaims();

		jwtClaims.setSubject(RandomTestUtil.randomString());

		JwsJwtCompactConsumer jwsJwtCompactConsumer = new JwsJwtCompactConsumer(
			liferayOAuthDataProvider.processJwtAccessToken(jwtClaims));

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

}