/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.crypto;

import com.liferay.portal.security.key.KeyReference;
import com.liferay.portal.security.key.crypto.exception.CryptoException;

import java.security.Key;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
@ProviderType
public interface CryptoManager {

	public CryptoServiceResult<byte[]> decrypt(
			byte[] ciphertext, long companyId, KeyReference keyReference)
		throws CryptoException;

	public void deleteKey(long companyId, KeyReference keyReference)
		throws CryptoException;

	public CryptoServiceResult<byte[]> encrypt(
			long companyId, KeyReference keyReference, byte[] plaintext)
		throws CryptoException;

	public CryptoServiceResult<Key> exportKey(
			long companyId, KeyReference keyReference)
		throws CryptoException;

	public CryptoServiceResult<KeyReference> generateAsymmetricKeyReference(
			String algorithm, long companyId, KeyReference keyReference)
		throws CryptoException;

	public CryptoServiceResult<KeyReference> generateSecretKeyReference(
			String algorithm, long companyId, KeyReference keyReference)
		throws CryptoException;

	public CryptoKey getCryptoKey(long companyId, KeyReference keyReference)
		throws CryptoException;

	public List<String> getCryptoProviderIds(long companyId)
		throws CryptoException;

	public List<KeyReference> getKeyReferences(
			long companyId, String cryptoProviderId)
		throws CryptoException;

	public CryptoServiceResult<KeyReference> importSecretKey(
			String algorithm, long companyId, byte[] keyBytes,
			KeyReference keyReference)
		throws CryptoException;

	public CryptoServiceResult<KeyReference> unwrap(
			long companyId, KeyReference keyReference,
			KeyReference masterKeyReference, String wrappedKeyAlgorithm,
			byte[] wrappedKeyBytes, int wrappedKeyCipherType)
		throws CryptoException;

	public CryptoServiceResult<byte[]> wrap(
			long companyId, KeyReference keyReference,
			KeyReference masterKeyReference)
		throws CryptoException;

}