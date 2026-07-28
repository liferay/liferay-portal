/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.spi.crypto;

import com.liferay.portal.security.key.crypto.CryptoKey;
import com.liferay.portal.security.key.crypto.CryptoServiceResult;
import com.liferay.portal.security.key.crypto.exception.CryptoException;
import com.liferay.portal.security.key.spi.ProviderStatus;

import java.security.Key;

import java.util.List;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
public interface CryptoProvider {

	public CryptoServiceResult<byte[]> decrypt(
			byte[] ciphertext, long companyId, String keyIdentifier)
		throws CryptoException;

	public void deleteKey(long companyId, String keyIdentifier)
		throws CryptoException;

	public CryptoServiceResult<byte[]> encrypt(
			long companyId, String keyIdentifier, byte[] plaintext)
		throws CryptoException;

	public CryptoServiceResult<Key> exportKey(
			long companyId, String keyIdentifier)
		throws CryptoException;

	public CryptoServiceResult<String> generateAsymmetricKeyIdentifier(
			String algorithm, long companyId, String keyIdentifier)
		throws CryptoException;

	public CryptoServiceResult<String> generateSecretKeyIdentifier(
			String algorithm, long companyId, String keyIdentifier)
		throws CryptoException;

	public CryptoKey getCryptoKey(long companyId, String keyIdentifier)
		throws CryptoException;

	public List<String> getKeyIdentifiers(long companyId)
		throws CryptoException;

	public ProviderStatus getProviderStatus();

	public CryptoServiceResult<String> importSecretKey(
			String algorithm, long companyId, byte[] keyBytes,
			String keyIdentifier)
		throws CryptoException;

	public boolean isAllowedCompany(long companyId);

	public CryptoServiceResult<String> unwrap(
			long companyId, String keyIdentifier, String masterKeyIdentifier,
			String wrappedKeyAlgorithm, byte[] wrappedKeyBytes,
			int wrappedKeyCipherType)
		throws CryptoException;

	public CryptoServiceResult<byte[]> wrap(
			long companyId, String keyIdentifier, String masterKeyIdentifier)
		throws CryptoException;

}