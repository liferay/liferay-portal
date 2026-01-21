/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mail.outlook.auth.connector.provider.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Rafael Praxedes
 */
@ExtendedObjectClassDefinition(
	category = "email", scope = ExtendedObjectClassDefinition.Scope.COMPANY
)
@Meta.OCD(
	id = "com.liferay.mail.outlook.auth.connector.provider.internal.configuration.MailOutlookAuthConnectorCompanyConfiguration",
	localization = "content/Language",
	name = "outlook-auth-connector-configuration-name"
)
public interface MailOutlookAuthConnectorCompanyConfiguration {

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		description = "outlook-auth-connector-client-id-description",
		name = "client-id", required = false
	)
	public String clientId();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		description = "outlook-auth-connector-client-secret-description",
		name = "client-secret", required = false, type = Meta.Type.Password
	)
	public String clientSecret();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "false",
		description = "outlook-auth-connector-pop3-connection-enabled-description",
		name = "outlook-auth-connector-pop3-connection-enabled",
		required = false
	)
	public boolean pop3ConnectionEnabled();

	@ExtendedAttributeDefinition(requiredInput = true)
	@Meta.AD(
		deflt = "false",
		description = "outlook-auth-connector-smtp-connection-enabled-description",
		name = "outlook-auth-connector-smtp-connection-enabled",
		required = false
	)
	public boolean smtpConnectionEnabled();

	@ExtendedAttributeDefinition(
		descriptionArguments = "https://learn.microsoft.com/en-us/azure/active-directory/fundamentals/active-directory-how-to-find-tenant",
		requiredInput = true
	)
	@Meta.AD(
		description = "outlook-auth-connector-tenant-id-description",
		name = "tenant-id", required = false
	)
	public String tenantId();

}