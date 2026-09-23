/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.outlook.auth.connector.provider.internal.token.provider;

import com.liferay.mail.kernel.auth.token.provider.MailAuthTokenProvider;
import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.outlook.auth.connector.provider.internal.configuration.MailOutlookAuthConnectorCompanyConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.key.secret.SecretResolver;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"mail.server.name=outlook.office365.com",
		"mail.server.name=smtp.office365.com"
	},
	service = MailAuthTokenProvider.class
)
public class MailOutlookMailAuthTokenProvider implements MailAuthTokenProvider {

	@Override
	public String getAccessToken(long companyId) {
		try {
			MailOutlookAuthConnectorCompanyConfiguration
				mailOutlookAuthConnectorCompanyConfiguration =
					_configurationProvider.getCompanyConfiguration(
						MailOutlookAuthConnectorCompanyConfiguration.class,
						companyId);

			ConfidentialClientApplication confidentialClientApplication =
				ConfidentialClientApplication.builder(
					mailOutlookAuthConnectorCompanyConfiguration.clientId(),
					ClientCredentialFactory.createFromSecret(
						_secretResolver.resolve(
							companyId,
							mailOutlookAuthConnectorCompanyConfiguration.
								clientSecret()))
				).authority(
					String.format(
						"https://login.microsoftonline.com/%s/",
						mailOutlookAuthConnectorCompanyConfiguration.tenantId())
				).build();

			ClientCredentialParameters clientCredentialParam =
				ClientCredentialParameters.builder(
					Collections.singleton(
						"https://outlook.office365.com/.default")
				).build();

			CompletableFuture<IAuthenticationResult> completableFuture =
				confidentialClientApplication.acquireToken(
					clientCredentialParam);

			IAuthenticationResult iAuthenticationResult =
				completableFuture.get();

			return iAuthenticationResult.accessToken();
		}
		catch (Exception exception) {
			_log.error("Unable to acquire access token", exception);
		}

		return StringPool.BLANK;
	}

	@Override
	public boolean isProtocolSupported(long companyId, String protocol) {
		try {
			MailOutlookAuthConnectorCompanyConfiguration
				mailOutlookAuthConnectorCompanyConfiguration =
					_configurationProvider.getCompanyConfiguration(
						MailOutlookAuthConnectorCompanyConfiguration.class,
						companyId);

			if (Objects.equals(Account.PROTOCOL_POPS, protocol)) {
				return mailOutlookAuthConnectorCompanyConfiguration.
					pop3ConnectionEnabled();
			}

			if (Objects.equals(Account.PROTOCOL_SMTP, protocol)) {
				return mailOutlookAuthConnectorCompanyConfiguration.
					smtpConnectionEnabled();
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				protocol + " protocol is not supported for company ID " +
					companyId);
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MailOutlookMailAuthTokenProvider.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private SecretResolver _secretResolver;

}