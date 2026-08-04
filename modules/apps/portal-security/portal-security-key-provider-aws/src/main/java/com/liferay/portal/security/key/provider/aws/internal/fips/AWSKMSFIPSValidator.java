/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.fips;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.key.ServiceIndicator;
import com.liferay.portal.security.key.crypto.exception.CryptoException;

import java.util.Set;

/**
 * @author Christopher Kian
 */
public class AWSKMSFIPSValidator {

	public AWSKMSFIPSValidator(String cipherMode, boolean fipsEnforced) {
		_cipherMode = cipherMode;
		_fipsEnforced = fipsEnforced;
	}

	public boolean isFIPSApprovedKeyOrigin(String keyOrigin) {
		return _approvedKeyOrigins.contains(keyOrigin);
	}

	public boolean isFIPSEnforced() {
		return _fipsEnforced;
	}

	public ServiceIndicator toServiceIndicator(
			boolean fipsApproved, String securityFunctionName)
		throws CryptoException {

		if (_fipsEnforced && !fipsApproved) {
			throw new CryptoException(
				"Security function \"" + securityFunctionName +
					"\" is not FIPS approved under strict mode");
		}

		return new ServiceIndicator(fipsApproved, securityFunctionName);
	}

	public ServiceIndicator toServiceIndicator(
			String keyOrigin, String securityFunctionName)
		throws CryptoException {

		return toServiceIndicator(
			isFIPSApprovedKeyOrigin(keyOrigin), securityFunctionName);
	}

	public void validateCipherMode() throws CryptoException {
		if (_fipsEnforced &&
			(Validator.isNull(_cipherMode) ||
			 !_aeadCipherModes.contains(StringUtil.toUpperCase(_cipherMode)))) {

			throw new CryptoException(
				"FIPS enforcement requires an AEAD cipher mode but \"" +
					_cipherMode + "\" is not AEAD");
		}
	}

	public void validateKeyOrigin(String keyOrigin) throws CryptoException {
		if (_fipsEnforced && !isFIPSApprovedKeyOrigin(keyOrigin)) {
			throw new CryptoException(
				StringBundler.concat(
					"FIPS enforcement requires a CMK within the AWS FIPS ",
					"boundary (origin AWS_CLOUDHSM or AWS_KMS) but the key ",
					"origin is \"", keyOrigin, "\""));
		}
	}

	private static final Set<String> _aeadCipherModes = SetUtil.fromArray(
		"AES_256_GCM", "AES_GCM", "SYMMETRIC_DEFAULT");
	private static final Set<String> _approvedKeyOrigins = SetUtil.fromArray(
		"AWS_CLOUDHSM", "AWS_KMS");

	private final String _cipherMode;
	private final boolean _fipsEnforced;

}