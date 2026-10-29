/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.cbom.builder;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.cyclonedx.model.component.crypto.enums.Primitive;

/**
 * @author Manuele Castro
 */
public class CryptoPrimitiveUtil {

	public static Primitive getPrimitive(
		String algorithm, String jcaClassName) {

		Primitive primitive = _primitives.get(jcaClassName);

		if (primitive != null) {
			return primitive;
		}

		if (algorithm == null) {
			return Primitive.UNKNOWN;
		}

		String[] algorithmParts = StringUtil.split(
			StringUtil.toUpperCase(algorithm), CharPool.SLASH);

		String name = algorithmParts[0];

		if (name.startsWith("HMAC")) {
			return Primitive.MAC;
		}

		if (name.startsWith("PBE") || name.startsWith("PBKDF2")) {
			return Primitive.KDF;
		}

		if (name.equals("CHACHA20-POLY1305")) {
			return Primitive.AE;
		}

		if (_streamCipherNames.contains(name)) {
			return Primitive.STREAM_CIPHER;
		}

		if (name.startsWith("AES") || _blockCipherNames.contains(name)) {
			if (Objects.equals(jcaClassName, "Cipher") &&
				(algorithmParts.length > 1) &&
				_aeModeNames.contains(algorithmParts[1])) {

				return Primitive.AE;
			}

			return Primitive.BLOCK_CIPHER;
		}

		if (name.equals("RSA") && Objects.equals(jcaClassName, "Cipher")) {
			return Primitive.PKE;
		}

		if (_keyAgreementNames.contains(name)) {
			return Primitive.KEY_AGREE;
		}

		if (_signatureNames.contains(name)) {
			return Primitive.SIGNATURE;
		}

		return Primitive.UNKNOWN;
	}

	private static final Set<String> _aeModeNames = SetUtil.fromArray(
		"CCM", "GCM");
	private static final Set<String> _blockCipherNames = SetUtil.fromArray(
		"ARIA", "BLOWFISH", "CAMELLIA", "DES", "DESEDE", "RC2", "SM4",
		"TRIPLEDES");
	private static final Set<String> _keyAgreementNames = SetUtil.fromArray(
		"DH", "DIFFIEHELLMAN", "ECDH", "X25519", "X448", "XDH");
	private static final Map<String, Primitive> _primitives =
		HashMapBuilder.put(
			"BCrypt", Primitive.KDF
		).put(
			"Crypt", Primitive.HASH
		).put(
			"KeyAgreement", Primitive.KEY_AGREE
		).put(
			"KeyManagerFactory", Primitive.OTHER
		).put(
			"KeyStore", Primitive.OTHER
		).put(
			"Mac", Primitive.MAC
		).put(
			"MessageDigest", Primitive.HASH
		).put(
			"SecureRandom", Primitive.DRBG
		).put(
			"Signature", Primitive.SIGNATURE
		).put(
			"SSLContext", Primitive.OTHER
		).put(
			"TrustManagerFactory", Primitive.OTHER
		).build();
	private static final Set<String> _signatureNames = SetUtil.fromArray(
		"DSA", "ECDSA", "ED25519", "ED448", "EDDSA");
	private static final Set<String> _streamCipherNames = SetUtil.fromArray(
		"ARCFOUR", "CHACHA20", "RC4");

}