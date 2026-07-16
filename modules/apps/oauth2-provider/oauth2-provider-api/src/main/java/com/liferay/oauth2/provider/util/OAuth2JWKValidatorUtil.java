/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.PropsValues;

import java.math.BigInteger;

import java.util.Set;

/**
 * @author Pedro Victor Silvestre
 */
public class OAuth2JWKValidatorUtil {

	public static void validateJWK(String jwk) {
		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		JSONObject jsonObject = null;

		try {
			jsonObject = JSONFactoryUtil.createJSONObject(jwk);
		}
		catch (JSONException jsonException) {
			throw new SecurityException("Unable to parse JWK", jsonException);
		}

		_validateJWK(jsonObject);
	}

	public static void validateJWKS(String jwks) {
		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		JSONObject jsonObject = null;

		try {
			jsonObject = JSONFactoryUtil.createJSONObject(jwks);
		}
		catch (JSONException jsonException) {
			throw new SecurityException("Unable to parse JWKS", jsonException);
		}

		JSONArray keysJSONArray = jsonObject.getJSONArray("keys");

		if (keysJSONArray == null) {
			return;
		}

		for (int i = 0; i < keysJSONArray.length(); i++) {
			_validateJWK(keysJSONArray.getJSONObject(i));
		}
	}

	public static void validateJWSAlgorithm(String algorithm) {
		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		if ((algorithm == null) || !_allowedJWSAlgorithms.contains(algorithm)) {
			throw new SecurityException(
				"JWS algorithm \"" + algorithm +
					"\" is not allowed in FIPS mode");
		}
	}

	private static int _decodeBase64URLBitLength(String base64) {
		try {
			BigInteger bigInteger = new BigInteger(
				1, Base64.decodeFromURL(base64));

			return bigInteger.bitLength();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to decode JWK value", illegalArgumentException);
			}

			return 0;
		}
	}

	private static void _validateJWK(JSONObject jsonObject) {
		if (jsonObject == null) {
			throw new SecurityException("Unable to read JWK");
		}

		validateJWSAlgorithm(jsonObject.getString("alg"));

		String keyType = jsonObject.getString("kty");

		if (keyType.equals("EC")) {
			String curve = jsonObject.getString("crv");

			if (!_allowedECCurves.contains(curve)) {
				throw new SecurityException(
					"EC curve \"" + curve + "\" is not allowed in FIPS mode");
			}
		}
		else if (keyType.equals("RSA")) {
			int bits = _decodeBase64URLBitLength(jsonObject.getString("n"));

			if (bits < _MIN_RSA_KEY_BITS) {
				throw new SecurityException(
					StringBundler.concat(
						"RSA key of ", bits,
						" bits is not allowed in FIPS mode"));
			}
		}
		else {
			throw new SecurityException(
				"JWK key type \"" + keyType + "\" is not allowed in FIPS mode");
		}
	}

	private static final int _MIN_RSA_KEY_BITS = 2048;

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2JWKValidatorUtil.class);

	private static final Set<String> _allowedECCurves = Set.of(
		"P-256", "P-384", "P-521");
	private static final Set<String> _allowedJWSAlgorithms = Set.of(
		"ES256", "ES384", "ES512", "PS256", "PS384", "PS512", "RS256", "RS384",
		"RS512");

}