/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.encryptor;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.CompanyKeyException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.fips.FIPSApplicationStateMachineUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.security.Key;

import java.util.Objects;

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

	public static String regenerateKey(long companyId, String keyString)
		throws EncryptorException {

		String regeneratedKeyString = generateKey(companyId);

		if (isWrappedKey(keyString) && !isWrappedKey(regeneratedKeyString)) {
			throw new CompanyKeyException(
				"Unable to wrap the regenerated key for company " + companyId);
		}

		return regeneratedKeyString;
	}

	public static void rotateKey(
		Company company,
		UnsafeFunction<String, Boolean, Throwable> unsafeFunction) {

		if (!_isRefusedKey(company.getKey())) {
			return;
		}

		long companyId = company.getCompanyId();

		try {
			String keyString = regenerateKey(companyId, company.getKey());

			if (unsafeFunction.apply(keyString)) {
				if (_log.isInfoEnabled()) {
					_log.info("Rotated the key for company " + companyId);
				}
			}
			else if (_log.isInfoEnabled()) {
				_log.info(
					"Another node already rotated the key for company " +
						companyId);
			}
		}
		catch (Throwable throwable) {
			if (Objects.equals(
					company.getWebId(), PropsValues.COMPANY_DEFAULT_WEB_ID)) {

				FIPSApplicationStateMachineUtil.error(
					"Default company key rotation",
					new CompanyKeyException(
						"Unable to rotate the key of the default company " +
							companyId,
						throwable));
			}

			_log.error(
				StringBundler.concat(
					"Unable to rotate the key for company ", companyId,
					". The key is refused until a later start rotates it."),
				throwable);
		}
	}

	public static Key unwrapKey(long companyId, String keyString) {
		if (_isRefusedKey(keyString)) {
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

	private static boolean _isRefusedKey(String keyString) {
		if (PropsValues.FIPS_ENABLED && !isFIPSKey(keyString)) {
			return true;
		}

		return false;
	}

	private static final String _FIPS_KEY_PREFIX = "${fipsKey:";

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyKeyResolverUtil.class);

	private static final Snapshot<CompanyKeyResolver>
		_companyKeyResolverSnapshot = new Snapshot<>(
			CompanyKeyResolverUtil.class, CompanyKeyResolver.class, null, true);

}