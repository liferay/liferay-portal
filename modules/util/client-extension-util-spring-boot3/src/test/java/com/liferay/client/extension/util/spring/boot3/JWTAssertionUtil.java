/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.util.spring.boot3;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.time.Instant;

import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author Arthur Chan
 * @author Gregory Amerson
 */
public class JWTAssertionUtil {

	public static final String JWKS = _readJWKS();

	public static String getJWTWithClientId(String clientId) throws Exception {
		return _getJWT(
			_getJWTClaimsSetBuilder(
				"localhost"
			).claim(
				"client_id", clientId
			).build());
	}

	public static String getJWTWithExternalReferenceCode(
			String externalReferenceCode, String issuer)
		throws Exception {

		return _getJWT(
			_getJWTClaimsSetBuilder(
				issuer
			).claim(
				"client_id", String.valueOf(UUID.randomUUID())
			).claim(
				"external_reference_code", externalReferenceCode
			).build());
	}

	private static String _getJWT(JWTClaimsSet jwtClaimsSet) throws Exception {
		JWSHeader jwsHeader = new JWSHeader.Builder(
			JWSAlgorithm.RS256
		).keyID(
			_KEY_ID
		).type(
			new JOSEObjectType("at+jwt")
		).build();

		SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);

		JWKSet jwkSet = JWKSet.parse(JWKS);

		RSAKey rsaKey = (RSAKey)jwkSet.getKeyByKeyId(_KEY_ID);

		signedJWT.sign(new RSASSASigner(rsaKey));

		return signedJWT.serialize();
	}

	private static JWTClaimsSet.Builder _getJWTClaimsSetBuilder(String issuer) {
		Instant instant = Instant.now();

		return new JWTClaimsSet.Builder(
		).audience(
			"localhost"
		).expirationTime(
			Date.from(instant.plusSeconds(3600))
		).issueTime(
			Date.from(instant)
		).issuer(
			issuer
		).subject(
			"none"
		);
	}

	private static String _readJWKS() {
		try (Scanner scanner = new Scanner(
				JWTAssertionUtil.class.getResourceAsStream(
					"dependencies/jwks.json"),
				"UTF-8")) {

			scanner.useDelimiter("\\A");

			return scanner.next();
		}
	}

	private JWTAssertionUtil() {
	}

	private static final String _KEY_ID = "_createTestRSAKeyPairJSONWebKey01";

}