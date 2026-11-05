/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.encryptor;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.CompanyKeyException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.PropsValues;

import java.security.Key;

/**
 * @author Christopher Kian
 */
public class CompanyKeyResolverUtil {

	public static String generateKey(long companyId) throws EncryptorException {
		String keyString = wrapKey(companyId, EncryptorUtil.generateKey());

		if (!PropsValues.FIPS_ENABLED) {
			return keyString;
		}

		return StringBundler.concat(
			_FIPS_KEY_PREFIX, keyString, StringPool.CLOSE_CURLY_BRACE);
	}

	public static boolean isFIPSKey(String keyString) {
		if ((keyString != null) && keyString.startsWith(_FIPS_KEY_PREFIX) &&
			keyString.endsWith(StringPool.CLOSE_CURLY_BRACE)) {

			return true;
		}

		return false;
	}

	public static boolean isWrappedKey(String keyString) {
		keyString = _getInnerKeyString(keyString);

		if ((keyString != null) && keyString.startsWith("${wrappedKey:")) {
			return true;
		}

		return false;
	}

	public static Key unwrapKey(long companyId, String keyString) {
		if (PropsValues.FIPS_ENABLED && !isFIPSKey(keyString)) {
			throw new CompanyKeyException(
				StringBundler.concat(
					"The key of company ", companyId,
					" was not generated in FIPS mode and is refused until a ",
					"FIPS start rotates it"));
		}

		keyString = _getInnerKeyString(keyString);

		if (!isWrappedKey(keyString)) {
			return EncryptorUtil.deserializeKey(keyString);
		}

		CompanyKeyResolver companyKeyResolver =
			_companyKeyResolverSnapshot.get();

		if (companyKeyResolver == null) {
			throw new CompanyKeyException(
				"Key resolver is not available for company " + companyId);
		}

		return companyKeyResolver.unwrapKey(companyId, keyString);
	}

	public static String wrapKey(long companyId, Key key) {
		CompanyKeyResolver companyKeyResolver =
			_companyKeyResolverSnapshot.get();

		if ((companyKeyResolver == null) ||
			!companyKeyResolver.isEnabled(companyId)) {

			return EncryptorUtil.serializeKey(key);
		}

		return companyKeyResolver.wrapKey(companyId, key);
	}

	private static String _getInnerKeyString(String keyString) {
		if (!isFIPSKey(keyString)) {
			return keyString;
		}

		return keyString.substring(
			_FIPS_KEY_PREFIX.length(), keyString.length() - 1);
	}

	private static final String _FIPS_KEY_PREFIX = "${fipsKey:";

	private static final Snapshot<CompanyKeyResolver>
		_companyKeyResolverSnapshot = new Snapshot<>(
			CompanyKeyResolverUtil.class, CompanyKeyResolver.class, null, true);

}