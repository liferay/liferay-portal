/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.crypto;

import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.security.key.spi.crypto.CryptoProvider;

import org.osgi.service.component.annotations.Component;

/**
 * @author Christopher Kian
 */
@Component(
	configurationPid = "com.liferay.portal.security.key.provider.aws.internal.configuration.AWSKMSCompanyCryptoProviderConfiguration",
	property = "crypto.provider.id=aws-company-crypto",
	service = CryptoProvider.class
)
public class AWSKMSCompanyCryptoProvider extends BaseAWSKMSCryptoProvider {

	@Override
	public boolean isAllowedCompany(long companyId) {
		if (companyId == CompanyConstants.SYSTEM) {
			return false;
		}

		return true;
	}

	@Override
	protected String getProviderId() {
		return "aws-company-crypto";
	}

}