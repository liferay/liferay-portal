/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.settings.internal.upgrade.v1_0_0;

import com.liferay.mail.settings.configuration.MailSettingCompanyConfiguration;
import com.liferay.portal.configuration.upgrade.PrefsPropsToConfigurationUpgradeHelper;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.KeyValuePair;

/**
 * @author Jiefeng Wu
 */
public class MailSettingCompanyConfigurationUpgradeProcess
	extends UpgradeProcess {

	public MailSettingCompanyConfigurationUpgradeProcess(
		PrefsPropsToConfigurationUpgradeHelper
			prefsPropsToConfigurationUpgradeHelper) {

		_prefsPropsToConfigurationUpgradeHelper =
			prefsPropsToConfigurationUpgradeHelper;
	}

	@Override
	protected void doUpgrade() throws Exception {
		KeyValuePair[] keyValuePairs = {
			new KeyValuePair(
				"mail.session.mail.advanced.properties",
				"additionalJavaMailProperties"),
			new KeyValuePair(
				"mail.session.mail.pop3.host", "incomingPOPServer"),
			new KeyValuePair("mail.session.mail.pop3.password", "popPassword"),
			new KeyValuePair("mail.session.mail.pop3.port", "incomingPOPPort"),
			new KeyValuePair("mail.session.mail.pop3.user", "popUserName"),
			new KeyValuePair(
				"mail.session.mail.smtp.host", "outgoingSMTPServer"),
			new KeyValuePair("mail.session.mail.smtp.password", "smtpPassword"),
			new KeyValuePair("mail.session.mail.smtp.port", "outgoingSMTPPort"),
			new KeyValuePair(
				"mail.session.mail.smtp.starttls.enable", "enableStartTLS"),
			new KeyValuePair("mail.session.mail.smtp.user", "smtpUserName"),
			new KeyValuePair(
				"mail.session.mail.store.protocol", "storeProtocol"),
			new KeyValuePair(
				"mail.session.mail.transport.protocol", "transportProtocol"),
			new KeyValuePair(
				"pop.server.notifications.enabled",
				"enablePOPServerNotifications")
		};

		_prefsPropsToConfigurationUpgradeHelper.mapConfigurations(
			MailSettingCompanyConfiguration.class, keyValuePairs);

		CompanyLocalServiceUtil.forEachCompanyId(
			companyId ->
				_prefsPropsToConfigurationUpgradeHelper.mapConfigurations(
					companyId, MailSettingCompanyConfiguration.class,
					keyValuePairs));
	}

	private final PrefsPropsToConfigurationUpgradeHelper
		_prefsPropsToConfigurationUpgradeHelper;

}