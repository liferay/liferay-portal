/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.setting.internal.configuration;

import com.liferay.mail.kernel.service.MailSettingConfigurationProvider;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;

import java.util.Map;

import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Jiefeng Wu
 */
@Component(
	configurationPid = "com.liferay.mail.setting.internal.configuration.MailSettingConfiguration",
	service = MailSettingConfigurationProvider.class
)
public class MailSettingConfigurationProviderImpl
	implements MailSettingConfigurationProvider {

	@Override
	public String getAdditionalJavaMailProperties() {
		return _mailSettingConfiguration.additionalJavaMailProperties();
	}

	@Override
	public boolean getEnablePOPServerNotifications() {
		return _mailSettingConfiguration.enablePOPServerNotifications();
	}

	@Override
	public boolean getEnableStartTLS() {
		return _mailSettingConfiguration.enableStartTLS();
	}

	@Override
	public String getIncomingPOPPort() {
		return _mailSettingConfiguration.incomingPOPPort();
	}

	@Override
	public String getIncomingPOPServer() {
		return _mailSettingConfiguration.incomingPOPServer();
	}

	@Override
	public String getOutgoingSMTPPort() {
		return _mailSettingConfiguration.outgoingSMTPPort();
	}

	@Override
	public String getOutgoingSMTPServer() {
		return _mailSettingConfiguration.outgoingSMTPServer();
	}

	@Override
	public String getPOPPassword() {
		return _mailSettingConfiguration.popPassword();
	}

	@Override
	public String getPOPUserName() {
		return _mailSettingConfiguration.popUserName();
	}

	@Override
	public String getSMTPPassword() {
		return _mailSettingConfiguration.smtpPassword();
	}

	@Override
	public String getSMTPUserName() {
		return _mailSettingConfiguration.smtpUserName();
	}

	@Override
	public boolean getUseASecureNetworkConnectionForPOP() {
		return _mailSettingConfiguration.useASecureNetworkConnectionForPOP();
	}

	@Override
	public boolean getUseASecureNetworkConnectionForSMTP() {
		return _mailSettingConfiguration.useASecureNetworkConnectionForSMTP();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_mailSettingConfiguration = ConfigurableUtil.createConfigurable(
			MailSettingConfiguration.class, properties);


//		try {
//			if (companyId > CompanyConstants.SYSTEM) {
//				return _configurationProvider.getCompanyConfiguration(
//					FriendlyURLRedirectionConfiguration.class, companyId);
//			}
//
//			return _configurationProvider.getSystemConfiguration(
//				FriendlyURLRedirectionConfiguration.class);
//		}
//		catch (ConfigurationException configurationException) {
//			return ReflectionUtil.throwException(configurationException);
//		}
	}

	private volatile MailSettingConfiguration _mailSettingConfiguration;

}