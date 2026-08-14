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
		String accountId, AWSClientManager<AWSKMS> awsClientManager,
		AWSKMSFIPSValidator awsKMSFIPSValidator, boolean enabled,
		String keyARNTemplate, int pendingWindowInDays, String region,
		boolean useFIPSEndpoint) {

		_accountId = accountId;
		_awsClientManager = awsClientManager;
		_awsKMSFIPSValidator = awsKMSFIPSValidator;
		_enabled = enabled;
		_keyARNTemplate = keyARNTemplate;
		_pendingWindowInDays = pendingWindowInDays;
		_region = region;
		_useFIPSEndpoint = useFIPSEndpoint;
	}

	public String getAccountId() {
		return _accountId;
	}

	public AWSClientManager<AWSKMS> getAWSClientManager() {
		return _awsClientManager;
	}

	public AWSKMSFIPSValidator getAWSKMSFIPSValidator() {
		return _awsKMSFIPSValidator;
	}

	public String getKeyARNTemplate() {
		return _keyARNTemplate;
	}

	public int getPendingWindowInDays() {
		return _pendingWindowInDays;
	}

	public String getRegion() {
		return _region;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	public boolean isUseFIPSEndpoint() {
		return _useFIPSEndpoint;
	}

	private final String _accountId;
	private final AWSClientManager<AWSKMS> _awsClientManager;
	private final AWSKMSFIPSValidator _awsKMSFIPSValidator;
	private final boolean _enabled;
	private final String _keyARNTemplate;
	private final int _pendingWindowInDays;
	private final String _region;
	private final boolean _useFIPSEndpoint;

}