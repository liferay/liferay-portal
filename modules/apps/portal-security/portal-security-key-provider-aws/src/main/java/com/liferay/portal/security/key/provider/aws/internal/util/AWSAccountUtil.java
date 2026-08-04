/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.provider.aws.internal.util;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Christopher Kian
 */
public class AWSAccountUtil {

	public static String getAccountId() {
		AWSSecurityTokenService awsSecurityTokenService =
			_getAWSSecurityTokenService();

		try {
			GetCallerIdentityResult getCallerIdentityResult =
				awsSecurityTokenService.getCallerIdentity(
					new GetCallerIdentityRequest());

			return getCallerIdentityResult.getAccount();
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to get AWS account ID via STS", exception);
			}
		}
		finally {
			awsSecurityTokenService.shutdown();
		}

		return null;
	}

	private static AWSSecurityTokenService _getAWSSecurityTokenService() {
		AWSSecurityTokenServiceClientBuilder
			awsSecurityTokenServiceClientBuilder =
				AWSSecurityTokenServiceClientBuilder.standard(
				).withCredentials(
					DefaultAWSCredentialsProviderChain.getInstance()
				);

		return awsSecurityTokenServiceClientBuilder.build();
	}

	private static final Log _log = LogFactoryUtil.getLog(AWSAccountUtil.class);

}