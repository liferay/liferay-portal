/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.endpoint.jwks;

import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.KeyReferenceUtil;
import com.liferay.portal.security.key.secret.SecretResolver;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.core.Response;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;

import java.util.List;

import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKeys;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Farias
 */
public class LiferayJWKSServiceTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testJWKS() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");

		keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));

		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		_testJWKS(
			JwkUtils.fromECPrivateKey(
				(ECPrivateKey)keyPair.getPrivate(), JsonWebKey.EC_CURVE_P256),
			JwkUtils.fromECPublicKey(
				(ECPublicKey)keyPair.getPublic(), JsonWebKey.EC_CURVE_P256,
				RandomTestUtil.randomString()));

		keyPairGenerator = KeyPairGenerator.getInstance("RSA");

		keyPairGenerator.initialize(2048);

		keyPair = keyPairGenerator.generateKeyPair();

		_testJWKS(
			JwkUtils.fromRSAPrivateKey(
				(RSAPrivateKey)keyPair.getPrivate(), "RS256"),
			JwkUtils.fromRSAPublicKey(
				(RSAPublicKey)keyPair.getPublic(), "RS256",
				RandomTestUtil.randomString()));
	}

	private void _testJWKS(
			JsonWebKey privateJsonWebKey, JsonWebKey publicJsonWebKey)
		throws Exception {

		JsonWebKey signingJsonWebKey = new JsonWebKey(
			HashMapBuilder.<String, Object>putAll(
				publicJsonWebKey.asMap()
			).putAll(
				privateJsonWebKey.asMap()
			).build());

		String keyReferenceString = KeyReferenceUtil.toKeyReferenceString(
			new KeyReference(
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				KeyReference.Type.SECRET));

		SecretResolver secretResolver = Mockito.mock(SecretResolver.class);

		Mockito.when(
			secretResolver.resolve(CompanyConstants.SYSTEM, keyReferenceString)
		).thenReturn(
			JwkUtils.jwkKeyToJson(signingJsonWebKey)
		);

		LiferayJWKSService liferayJWKSService = new LiferayJWKSService();

		ReflectionTestUtil.setFieldValue(
			liferayJWKSService, "_secretResolver", secretResolver);

		liferayJWKSService.activate(
			HashMapBuilder.<String, Object>put(
				"oauth2.authorization.server.jwt.access.token.signing.json." +
					"web.key",
				keyReferenceString
			).build());

		Response response = liferayJWKSService.jwks();

		JsonWebKeys jsonWebKeys = JwkUtils.readJwkSet(
			(String)response.getEntity());

		List<JsonWebKey> jsonWebKeysList = jsonWebKeys.getKeys();

		Assert.assertEquals(
			jsonWebKeysList.toString(), 1, jsonWebKeysList.size());

		JsonWebKey jsonWebKey = jsonWebKeysList.get(0);

		Assert.assertEquals(publicJsonWebKey.asMap(), jsonWebKey.asMap());
	}

}