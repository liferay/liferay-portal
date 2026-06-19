/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.pwd;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PwdEncryptorException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 * @author Scott Lee
 * @author Tomas Polesovsky
 * @author Michael C. Han
 */
public class PasswordEncryptorUtil {

	public static String encrypt(String plainTextPassword)
		throws PwdEncryptorException {

		return encrypt(plainTextPassword, null);
	}

	public static String encrypt(
			String plainTextPassword, String encryptedPassword)
		throws PwdEncryptorException {

		long startTime = 0;

		if (_log.isDebugEnabled()) {
			startTime = System.currentTimeMillis();
		}

		try {
			return encrypt(
				_PASSWORDS_ENCRYPTION_ALGORITHM, plainTextPassword,
				encryptedPassword);
		}
		finally {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Password encrypted in " +
						(System.currentTimeMillis() - startTime) + "ms");
			}
		}
	}

	public static String encrypt(
			String plainTextPassword, String encryptedPassword,
			boolean upgradeHashSecurity)
		throws PwdEncryptorException {

		if (upgradeHashSecurity) {
			encryptedPassword = null;
		}

		return _encrypt(
			null, plainTextPassword, encryptedPassword, upgradeHashSecurity);
	}

	public static String encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword)
		throws PwdEncryptorException {

		return _encrypt(algorithm, plainTextPassword, encryptedPassword, false);
	}

	public static String getEncryptedPasswordAlgorithmSettings(
			String encryptedPassword)
		throws PwdEncryptorException {

		String encryptedPasswordAlgorithm = _getEncryptedPasswordAlgorithm(
			encryptedPassword);

		if (Validator.isNull(encryptedPasswordAlgorithm)) {
			return null;
		}

		PasswordEncryptor passwordEncryptor = _getPasswordEncryptor(
			encryptedPasswordAlgorithm);

		return passwordEncryptor.getEncryptedPasswordAlgorithmSettings(
			encryptedPassword);
	}

	private static String _encrypt(
			String algorithm, String plainTextPassword,
			String encryptedPassword, boolean upgradeHashSecurity)
		throws PwdEncryptorException {

		if (Validator.isNull(plainTextPassword)) {
			throw new PwdEncryptorException.PwdMustNotBeNull(
				"Unable to _encrypt blank password");
		}

		boolean prependAlgorithm = true;

		if (upgradeHashSecurity) {
			algorithm = _PASSWORDS_ENCRYPTION_ALGORITHM;
			encryptedPassword = null;
		}
		else {
			String encryptedPasswordAlgorithm = _getEncryptedPasswordAlgorithm(
				encryptedPassword);

			if (Validator.isNotNull(encryptedPasswordAlgorithm)) {
				algorithm = encryptedPasswordAlgorithm;
			}

			if (Validator.isNotNull(encryptedPassword) &&
				(encryptedPassword.charAt(0) != CharPool.OPEN_CURLY_BRACE)) {

				prependAlgorithm = false;
			}
			else if (Validator.isNotNull(encryptedPassword) &&
					 (encryptedPassword.charAt(0) ==
						 CharPool.OPEN_CURLY_BRACE)) {

				int index = encryptedPassword.indexOf(
					CharPool.CLOSE_CURLY_BRACE);

				if (index > 0) {
					encryptedPassword = encryptedPassword.substring(index + 1);
				}
			}

			if (Validator.isNull(algorithm)) {
				algorithm = _PASSWORDS_ENCRYPTION_ALGORITHM;
			}
		}

		if (PropsValues.FIPS_ENABLED && Validator.isNull(encryptedPassword) &&
			!_isFIPSApprovedAlgorithm(algorithm)) {

			throw new PwdEncryptorException.InvalidAlgorithm(
				StringBundler.concat(
					"Algorithm \"", algorithm, "\" is not allowed"),
				null);
		}

		PasswordEncryptor passwordEncryptor = _getPasswordEncryptor(algorithm);

		String newEncryptedPassword = passwordEncryptor.encrypt(
			algorithm, plainTextPassword, encryptedPassword, false);

		if (!prependAlgorithm) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Generated password without algorithm prefix using " +
						algorithm);
			}

			return newEncryptedPassword;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Generated password with algorithm prefix using " + algorithm);
		}

		return StringBundler.concat(
			StringPool.OPEN_CURLY_BRACE, _getAlgorithmName(algorithm),
			StringPool.CLOSE_CURLY_BRACE, newEncryptedPassword);
	}

	private static String _getAlgorithmName(String algorithm) {
		int index = algorithm.indexOf(CharPool.SLASH);

		if (index > 0) {
			return algorithm.substring(0, index);
		}

		return algorithm;
	}

	private static String _getEncryptedPasswordAlgorithm(
			String encryptedPassword)
		throws PwdEncryptorException {

		String legacyAlgorithm = GetterUtil.getString(
			PropsUtil.get(PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY));

		if (_log.isDebugEnabled() && Validator.isNotNull(legacyAlgorithm)) {
			if (Validator.isNull(encryptedPassword)) {
				_log.debug(
					StringBundler.concat(
						"Using legacy detection scheme for algorithm ",
						legacyAlgorithm, " with empty password"));
			}
			else {
				_log.debug(
					StringBundler.concat(
						"Using legacy detection scheme for algorithm ",
						legacyAlgorithm, " with provided password"));
			}
		}

		if (Validator.isNotNull(encryptedPassword) &&
			(encryptedPassword.charAt(0) != CharPool.OPEN_CURLY_BRACE)) {

			if (_log.isDebugEnabled()) {
				_log.debug("Using legacy algorithm " + legacyAlgorithm);
			}

			if (Validator.isNotNull(legacyAlgorithm)) {
				return legacyAlgorithm;
			}

			if (_log.isWarnEnabled()) {
				_log.warn(
					"Property \"" +
						PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM_LEGACY +
							"\" is not set");
			}

			throw new PwdEncryptorException.MustSetLegacyAlgorithmProperty();
		}
		else if (Validator.isNotNull(encryptedPassword) &&
				 (encryptedPassword.charAt(0) == CharPool.OPEN_CURLY_BRACE)) {

			int index = encryptedPassword.indexOf(CharPool.CLOSE_CURLY_BRACE);

			if (index > 0) {
				String algorithm = encryptedPassword.substring(1, index);

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Upgraded password to use algorithm " + algorithm);
				}

				return algorithm;
			}
		}

		return null;
	}

	private static PasswordEncryptor _getPasswordEncryptor(String algorithm) {
		if (Validator.isNull(algorithm)) {
			throw new IllegalArgumentException("Invalid algorithm");
		}

		PasswordEncryptor passwordEncryptor = null;

		if (algorithm.startsWith(PasswordEncryptor.TYPE_BCRYPT)) {
			passwordEncryptor = _serviceTrackerMap.getService(
				PasswordEncryptor.TYPE_BCRYPT);
		}
		else if (algorithm.startsWith(PasswordEncryptor.TYPE_PBKDF2)) {
			passwordEncryptor = _serviceTrackerMap.getService(
				PasswordEncryptor.TYPE_PBKDF2);
		}
		else if (algorithm.indexOf(CharPool.SLASH) > 0) {
			passwordEncryptor = _serviceTrackerMap.getService(
				algorithm.substring(0, algorithm.indexOf(CharPool.SLASH)));
		}
		else {
			passwordEncryptor = _serviceTrackerMap.getService(algorithm);
		}

		if (PropsValues.FIPS_ENABLED &&
			(algorithm.startsWith(PasswordEncryptor.TYPE_BCRYPT) ||
			 algorithm.startsWith(PasswordEncryptor.TYPE_UFC_CRYPT))) {

			throw new SecurityException(
				StringBundler.concat(
					"Algorithm ", algorithm, " is not allowed in FIPS mode"));
		}

		if (passwordEncryptor == null) {
			if (_log.isDebugEnabled()) {
				_log.debug("No password encryptor found for " + algorithm);
			}

			passwordEncryptor = _serviceTrackerMap.getService(
				PasswordEncryptor.TYPE_DEFAULT);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Found ", ClassUtil.getClassName(passwordEncryptor),
					" to _encrypt password using ", algorithm));
		}

		return passwordEncryptor;
	}

	private static boolean _isFIPSApprovedAlgorithm(String algorithm) {
		if (Validator.isNull(algorithm)) {
			return false;
		}

		String upperCaseAlgorithm = StringUtil.toUpperCase(algorithm);

		if (upperCaseAlgorithm.startsWith(PasswordEncryptor.TYPE_PBKDF2) &&
			upperCaseAlgorithm.contains("SHA256")) {

			return true;
		}

		return false;
	}

	private static final String _PASSWORDS_ENCRYPTION_ALGORITHM =
		StringUtil.toUpperCase(
			GetterUtil.getString(
				PropsUtil.get(PropsKeys.PASSWORDS_ENCRYPTION_ALGORITHM)));

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordEncryptorUtil.class);

	private static final ServiceTrackerMap<String, PasswordEncryptor>
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			SystemBundleUtil.getBundleContext(), PasswordEncryptor.class,
			"type");

}