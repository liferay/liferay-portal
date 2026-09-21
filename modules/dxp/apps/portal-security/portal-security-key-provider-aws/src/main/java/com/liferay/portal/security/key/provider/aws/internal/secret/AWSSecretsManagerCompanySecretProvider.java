/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.secret;

import com.liferay.portal.security.key.spi.secret.SecretProvider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christopher Kian
 */
@Component(
	configurationPid = "com.liferay.portal.security.key.provider.aws.internal.configuration.AWSSecretsManagerCompanySecretProviderConfiguration",
	property = "secret.provider.id=aws-company-secret",
	service = SecretProvider.class
)
public class AWSSecretsManagerCompanySecretProvider
	extends BaseAWSSecretsManagerSecretProvider {

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (companyId > 0) {
			return true;
		}

		return false;
	}

	@Override
	protected String getProviderId() {
		return "aws-company-secret";
	}

}