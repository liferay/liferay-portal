/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.settings.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Jiefeng Wu
 */
@ExtendedObjectClassDefinition(
	category = "email", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.mail.settings.configuration.MailSettingCompanyConfiguration",
	localization = "content/Language",
	name = "mail-settings-company-configuration-name"
)
public interface MailSettingCompanyConfiguration {

	@Meta.AD(
		deflt = "false", name = "enable-pop-server-notifications",
		required = false
	)
	public boolean enablePOPServerNotifications();

	@Meta.AD(
		deflt = "localhost", name = "incoming-pop-server", required = false
	)
	public String incomingPOPServer();

	@Meta.AD(deflt = "110", name = "incoming-port", required = false)
	public String incomingPOPPort();

	@Meta.AD(deflt = "pop3", name = "store-protocol", required = false)
	public String storeProtocol();

	@Meta.AD(deflt = "smtp", name = "transport-protocol", required = false)
	public String transportProtocol();

	@Meta.AD(deflt = "", name = "user-name", required = false)
	public String popUserName();

	@Meta.AD(
		deflt = "", name = "password", required = false,
		type = Meta.Type.Password
	)
	public String popPassword();

	@Meta.AD(
		deflt = "localhost", name = "outgoing-smtp-server", required = false
	)
	public String outgoingSMTPServer();

	@Meta.AD(deflt = "25", name = "outgoing-port", required = false)
	public String outgoingSMTPPort();

	@Meta.AD(deflt = "true", name = "enable-starttls", required = false)
	public boolean enableStartTLS();

	@Meta.AD(deflt = "", name = "user-name", required = false)
	public String smtpUserName();

	@Meta.AD(
		deflt = "", name = "password", required = false,
		type = Meta.Type.Password
	)
	public String smtpPassword();

	@Meta.AD(
		deflt = "",
		name = "manually-specify-additional-javamail-properties-to-override-the-above-configuration",
		required = false
	)
	public String additionalJavaMailProperties();

}