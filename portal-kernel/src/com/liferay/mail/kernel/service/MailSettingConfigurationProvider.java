/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.kernel.service;

/**
 * @author Jiefeng Wu
 */
public interface MailSettingConfigurationProvider {

	public String getAdditionalJavaMailProperties();

	public boolean getEnablePOPServerNotifications();

	public boolean getEnableStartTLS();

	public String getIncomingPOPPort();

	public String getIncomingPOPServer();

	public String getOutgoingSMTPPort();

	public String getOutgoingSMTPServer();

	public String getPOPPassword();

	public String getPOPUserName();

	public String getSMTPPassword();

	public String getSMTPUserName();

	public boolean getUseASecureNetworkConnectionForPOP();

	public boolean getUseASecureNetworkConnectionForSMTP();

}