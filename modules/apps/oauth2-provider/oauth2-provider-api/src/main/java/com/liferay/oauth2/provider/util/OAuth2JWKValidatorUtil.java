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

	public static void validateJWK(String json) {
		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		JSONObject jsonObject = null;

		try {
			jsonObject = JSONFactoryUtil.createJSONObject(json);
		}
		catch (JSONException jsonException) {
			throw new SecurityException("Unable to parse JWK", jsonException);
		}

		_validateJWK(jsonObject);
	}

	public static void validateJWKS(String json) {
		if (!PropsValues.FIPS_ENABLED) {
			return;
		}

		JSONObject jsonObject = null;

		try {
			jsonObject = JSONFactoryUtil.createJSONObject(json);
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

	private static int _decodeBase64URLBitLength(String value) {
		try {
			byte[] bytes = Base64.decodeFromURL(value);

			BigInteger bigInteger = new BigInteger(1, bytes);

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

		if (keyType.equals("RSA")) {
			int bits = _decodeBase64URLBitLength(jsonObject.getString("n"));

			if (bits < _MIN_RSA_KEY_BITS) {
				throw new SecurityException(
					StringBundler.concat(
						"RSA key of ", bits,
						" bits is not allowed in FIPS mode"));
			}
		}
		else if (keyType.equals("oct")) {
			int bits = _decodeBase64URLBitLength(jsonObject.getString("k"));

			if (bits < _MIN_HMAC_KEY_BITS) {
				throw new SecurityException(
					StringBundler.concat(
						"HMAC key of ", bits,
						" bits is not allowed in FIPS mode"));
			}
		}
	}

	private static final int _MIN_HMAC_KEY_BITS = 112;

	private static final int _MIN_RSA_KEY_BITS = 2048;

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2JWKValidatorUtil.class);

	private static final Set<String> _allowedJWSAlgorithms = Set.of(
		"ES256", "ES384", "ES512", "HS256", "HS384", "HS512", "PS256", "PS384",
		"PS512", "RS256", "RS384", "RS512");

}