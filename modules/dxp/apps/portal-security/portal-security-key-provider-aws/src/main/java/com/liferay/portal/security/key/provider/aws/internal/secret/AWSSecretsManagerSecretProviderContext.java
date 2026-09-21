/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.secret;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;

import com.liferay.portal.security.key.provider.aws.internal.fips.AWSSecretsManagerFIPSValidator;
import com.liferay.portal.security.key.provider.aws.internal.util.AWSClientManager;

/**
 * @author Christopher Kian
 */
public class AWSSecretsManagerSecretProviderContext {

	public AWSSecretsManagerSecretProviderContext(
		String awsAccountId,
		AWSClientManager<AWSSecretsManager> awsClientManager, String awsRegion,
		AWSSecretsManagerFIPSValidator awsSecretsManagerFIPSValidator,
		boolean enabled, long recoveryWindowInDays, String secretARNTemplate) {

		_awsAccountId = awsAccountId;
		_awsClientManager = awsClientManager;
		_awsRegion = awsRegion;
		_awsSecretsManagerFIPSValidator = awsSecretsManagerFIPSValidator;
		_enabled = enabled;
		_recoveryWindowInDays = recoveryWindowInDays;
		_secretARNTemplate = secretARNTemplate;
	}

	public String getAWSAccountId() {
		return _awsAccountId;
	}

	public AWSClientManager<AWSSecretsManager> getAWSClientManager() {
		return _awsClientManager;
	}

	public String getAWSRegion() {
		return _awsRegion;
	}

	public AWSSecretsManagerFIPSValidator getAWSSecretsManagerFIPSValidator() {
		return _awsSecretsManagerFIPSValidator;
	}

	public long getRecoveryWindowInDays() {
		return _recoveryWindowInDays;
	}

	public String getSecretARNTemplate() {
		return _secretARNTemplate;
	}

	public boolean isEnabled() {
		return _enabled;
	}

	private final String _awsAccountId;
	private final AWSClientManager<AWSSecretsManager> _awsClientManager;
	private final String _awsRegion;
	private final AWSSecretsManagerFIPSValidator
		_awsSecretsManagerFIPSValidator;
	private final boolean _enabled;
	private final long _recoveryWindowInDays;
	private final String _secretARNTemplate;

}