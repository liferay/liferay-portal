/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.fips;

import com.liferay.portal.security.key.secret.exception.SecretException;

/**
 * @author Christopher Kian
 */
public class AWSSecretsManagerFIPSValidator {

	public AWSSecretsManagerFIPSValidator(
		boolean fipsEnforced, boolean useFIPSEndpoint) {

		_fipsEnforced = fipsEnforced;
		_useFIPSEndpoint = useFIPSEndpoint;
	}

	public void validateEndpoint() throws SecretException {
		if (_fipsEnforced && !_useFIPSEndpoint) {
			throw new SecretException(
				"FIPS is enforced but the AWS Secrets Manager provider is " +
					"not configured to use a FIPS endpoint");
		}
	}

	private final boolean _fipsEnforced;
	private final boolean _useFIPSEndpoint;

}