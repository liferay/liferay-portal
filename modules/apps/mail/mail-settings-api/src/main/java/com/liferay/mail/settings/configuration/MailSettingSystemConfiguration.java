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
	category = "email", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.mail.settings.configuration.MailSettingSystemConfiguration",
	localization = "content/Language",
	name = "mail-settings-system-configuration-name"
)
public interface MailSettingSystemConfiguration {

	@Meta.AD(
		deflt = "", description = "audit-trail-description",
		name = "audit-trail", required = false
	)
	public String auditTrail();

	@Meta.AD(
		deflt = "0", description = "batch-size-description",
		name = "batch-size", required = false
	)
	public String batchSize();

	@Meta.AD(
		deflt = "", description = "jndi-name-description", name = "jndi-name",
		required = false
	)
	public String jndiName();

	@Meta.AD(
		deflt = "events", description = "pop-server-subdomain-description",
		name = "pop-server-subdomain", required = false
	)
	public String popServerSubdomain();

	@Meta.AD(
		deflt = "noreply@liferay.com|test@liferay.com|noreply@domain.invalid|test@domain.invalid",
		description = "send-blacklist-description", name = "send-blacklist",
		required = false
	)
	public String[] sendBlacklist();

	@Meta.AD(
		deflt = "false",
		description = "throws-exception-on-failure-description",
		name = "throws-exception-on-failure", required = false
	)
	public boolean throwsExceptionOnFailure();

}