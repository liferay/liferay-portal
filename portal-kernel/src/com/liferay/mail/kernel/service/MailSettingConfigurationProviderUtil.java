/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.kernel.service;

import com.liferay.portal.kernel.module.service.Snapshot;

/**
 * @author Jiefeng Wu
 */
public class MailSettingConfigurationProviderUtil {

	public static String getAdditionalJavaMailProperties() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.
			getAdditionalJavaMailProperties();
	}

	public static boolean getEnablePOPServerNotifications() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.
			getEnablePOPServerNotifications();
	}

	public static boolean getEnableStartTLS() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getEnableStartTLS();
	}

	public static String getIncomingPOPPort() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getIncomingPOPPort();
	}

	public static String getIncomingPOPServer() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getIncomingPOPServer();
	}

	public static String getOutgoingSMTPPort() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getOutgoingSMTPPort();
	}

	public static String getOutgoingSMTPServer() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getOutgoingSMTPServer();
	}

	public static String getPOPPassword() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getPOPUserName();
	}

	public static String getPOPUserName() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getPOPUserName();
	}

	public static String getSMTPPassword() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getSMTPUserName();
	}

	public static String getSMTPUserName() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.getSMTPUserName();
	}

	public static boolean getUseASecureNetworkConnectionForPOP() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.
			getUseASecureNetworkConnectionForPOP();
	}

	public static boolean getUseASecureNetworkConnectionForSMTP() {
		MailSettingConfigurationProvider mailSettingConfigurationProvider =
			_mailSettingConfigurationProviderSnapshot.get();

		return mailSettingConfigurationProvider.
			getUseASecureNetworkConnectionForSMTP();
	}

	private static final Snapshot<MailSettingConfigurationProvider>
		_mailSettingConfigurationProviderSnapshot = new Snapshot<>(
			MailSettingConfigurationProviderUtil.class,
			MailSettingConfigurationProvider.class);

}