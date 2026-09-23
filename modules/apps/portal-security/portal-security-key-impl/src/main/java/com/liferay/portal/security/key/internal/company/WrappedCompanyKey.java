/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.company;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.CompanyKeyResolverUtil;
import com.liferay.portal.kernel.exception.CompanyKeyResolutionException;
import com.liferay.portal.security.key.KeyReference;

import java.util.Arrays;
import java.util.Base64;

/**
 * @author Christopher Kian
 */
public class WrappedCompanyKey {

	public static WrappedCompanyKey parse(long companyId, String wrappedKey) {
		if (!CompanyKeyResolverUtil.isWrappedKey(wrappedKey) ||
			!wrappedKey.endsWith(StringPool.CLOSE_CURLY_BRACE)) {

			throw new CompanyKeyResolutionException(
				"Wrapped key is malformed for company " + companyId);
		}

		String body = wrappedKey.substring(
			CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX.length(),
			wrappedKey.length() - 1);

		int versionIndex = body.indexOf(CharPool.COLON);

		if (versionIndex <= 0) {
			throw new CompanyKeyResolutionException(
				"Wrapped key is malformed for company " + companyId);
		}

		String version = body.substring(0, versionIndex);

		if (!version.equals(CompanyKeyResolverUtil.WRAPPED_KEY_VERSION)) {
			throw new CompanyKeyResolutionException(
				StringBundler.concat(
					"Wrapped key version ", version,
					" is not supported for company ", companyId));
		}

		body = body.substring(versionIndex + 1);

		int colonIndex = body.indexOf(CharPool.COLON);
		int pipeIndex = body.indexOf(CharPool.PIPE);

		if ((colonIndex <= 0) || (pipeIndex <= (colonIndex + 1)) ||
			(pipeIndex >= (body.length() - 1))) {

			throw new CompanyKeyResolutionException(
				"Wrapped key is malformed for company " + companyId);
		}

		byte[] ciphertext = null;

		try {
			Base64.Decoder decoder = Base64.getDecoder();

			ciphertext = decoder.decode(body.substring(pipeIndex + 1));
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new CompanyKeyResolutionException(
				"Wrapped key ciphertext is not valid Base64 for company " +
					companyId,
				illegalArgumentException);
		}

		try {
			return new WrappedCompanyKey(
				ciphertext,
				new KeyReference(
					body.substring(colonIndex + 1, pipeIndex),
					body.substring(0, colonIndex), KeyReference.Type.CRYPTO));
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new CompanyKeyResolutionException(
				"Wrapped key is malformed for company " + companyId,
				illegalArgumentException);
		}
	}

	public WrappedCompanyKey(byte[] ciphertext, KeyReference keyReference) {
		if (ciphertext == null) {
			throw new IllegalArgumentException("Ciphertext is null");
		}

		if (keyReference == null) {
			throw new IllegalArgumentException("Key reference is null");
		}

		String identifier = keyReference.getIdentifier();

		if ((identifier.indexOf(CharPool.CLOSE_CURLY_BRACE) != -1) ||
			(identifier.indexOf(CharPool.PIPE) != -1)) {

			throw new IllegalArgumentException(
				"Identifier contains a reserved character");
		}

		String providerId = keyReference.getProviderId();

		if (providerId.indexOf(CharPool.PIPE) != -1) {
			throw new IllegalArgumentException(
				"Provider ID contains a reserved character");
		}

		_ciphertext = Arrays.copyOf(ciphertext, ciphertext.length);
		_keyReference = keyReference;
	}

	public byte[] getCiphertext() {
		return Arrays.copyOf(_ciphertext, _ciphertext.length);
	}

	public KeyReference getKeyReference() {
		return _keyReference;
	}

	public String toWrappedKey() {
		Base64.Encoder encoder = Base64.getEncoder();

		return StringBundler.concat(
			CompanyKeyResolverUtil.WRAPPED_KEY_PREFIX,
			CompanyKeyResolverUtil.WRAPPED_KEY_VERSION, StringPool.COLON,
			_keyReference.getProviderId(), StringPool.COLON,
			_keyReference.getIdentifier(), StringPool.PIPE,
			encoder.encodeToString(_ciphertext), StringPool.CLOSE_CURLY_BRACE);
	}

	private final byte[] _ciphertext;
	private final KeyReference _keyReference;

}