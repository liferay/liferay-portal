/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.secret;

import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.security.key.spi.secret.SecretProvider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christopher Kian
 */
@Component(
	configurationPid = "com.liferay.portal.security.key.provider.aws.internal.configuration.AWSSecretsManagerSystemSecretProviderConfiguration",
	property = "secret.provider.id=aws-system-secret",
	service = SecretProvider.class
)
public class AWSSecretsManagerSystemSecretProvider
	extends BaseAWSSecretsManagerSecretProvider {

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (companyId == CompanyConstants.SYSTEM) {
			return true;
		}

		return false;
	}

	@Override
	protected String getProviderId() {
		return "aws-system-secret";
	}

}