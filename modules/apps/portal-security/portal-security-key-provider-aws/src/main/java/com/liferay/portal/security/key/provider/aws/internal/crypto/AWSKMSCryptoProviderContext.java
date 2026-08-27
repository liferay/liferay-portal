/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.crypto;

import com.amazonaws.services.kms.AWSKMS;

import com.liferay.portal.security.key.provider.aws.internal.fips.AWSKMSFIPSValidator;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSClientManager;

/**
 * @author Christopher Kian
 */
public class AWSKMSCryptoProviderContext {

	public AWSKMSCryptoProviderContext(
		String awsAccountId, AWSClientManager<AWSKMS> awsClientManager,
		AWSKMSFIPSValidator awsKMSFIPSValidator, String awsRegion,
		boolean enabled, String keyARNTemplate, int pendingWindowInDays,
		boolean useFIPSEndpoint) {

		_awsAccountId = awsAccountId;
		_awsClientManager = awsClientManager;
		_awsKMSFIPSValidator = awsKMSFIPSValidator;
		_awsRegion = awsRegion;
		_enabled = enabled;
		_keyARNTemplate = keyARNTemplate;
		_pendingWindowInDays = pendingWindowInDays;
		_useFIPSEndpoint = useFIPSEndpoint;
	}

	public String getAWSAccountId() {
		return _awsAccountId;
	}

	public AWSClientManager<AWSKMS> getAWSClientManager() {
		return _awsClientManager;
	}

	public AWSKMSFIPSValidator getAWSKMSFIPSValidator() {
		return _awsKMSFIPSValidator;
	}

	public String getAWSRegion() {
		return _awsRegion;
	}

	public String getKeyARNTemplate() {
		return _keyARNTemplate;
	}

	public int getPendingWindowInDays() {
		return _pendingWindowInDays;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public boolean isUseFIPSEndpoint() {
		return _useFIPSEndpoint;
	}

	private final String _awsAccountId;
	private final AWSClientManager<AWSKMS> _awsClientManager;
	private final AWSKMSFIPSValidator _awsKMSFIPSValidator;
	private final String _awsRegion;
	private final boolean _enabled;
	private final String _keyARNTemplate;
	private final int _pendingWindowInDays;
	private final boolean _useFIPSEndpoint;

}