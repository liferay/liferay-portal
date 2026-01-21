/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Pei-Jung Lan
 */
@ExtendedObjectClassDefinition(
	category = "accounts", scope = ExtendedObjectClassDefinition.Scope.COMPANY,
	strictScope = true
)
@Meta.OCD(
	id = "com.liferay.account.configuration.AccountEntryEmailDomainsConfiguration",
	localization = "content/Language",
	name = "account-entry-email-domains-configuration-name"
)
public interface AccountEntryEmailDomainsConfiguration {

	@Meta.AD(
		description = "blocked-email-domains-help",
		name = "blocked-email-domains", required = false
	)
	public String blockedEmailDomains();

	@Meta.AD(
		description = "custom-tlds-description", name = "custom-tlds-name",
		required = false
	)
	public String[] customTLDs();

	@Meta.AD(
		deflt = "false", description = "enable-email-domain-validation-help",
		name = "enable-email-domain-validation", required = false
	)
	public boolean enableEmailDomainValidation();

}