/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Pei-Jung Lan
 */
@ExtendedObjectClassDefinition(
	category = "accounts", scope = ExtendedObjectClassDefinition.Scope.COMPANY,
	strictScope = true
)
@Meta.OCD(
	id = "com.liferay.account.configuration.AccountEntryEmailConfiguration",
	localization = "content/Language",
	name = "account-entry-email-configuration-name"
)
public interface AccountEntryEmailConfiguration {

	@Meta.AD(
		deflt = "${resource:com/liferay/account/dependencies/account_entry_invite_user_body.tmpl}",
		description = "invitation-email-body-description",
		name = "invitation-email-body", required = false
	)
	public LocalizedValuesMap invitationEmailBody();

	@Meta.AD(
		description = "invitation-email-sender-email-address-description",
		name = "invitation-email-sender-email-address", required = false
	)
	public String invitationEmailSenderEmailAddress();

	@Meta.AD(
		description = "invitation-email-sender-name-description",
		name = "invitation-email-sender-name", required = false
	)
	public String invitationEmailSenderName();

	@Meta.AD(
		deflt = "${resource:com/liferay/account/dependencies/account_entry_invite_user_subject.tmpl}",
		description = "invitation-email-subject-description",
		name = "invitation-email-subject", required = false
	)
	public LocalizedValuesMap invitationEmailSubject();

	@Meta.AD(
		deflt = "48",
		description = "invitation-token-expiration-time-description",
		name = "invitation-token-expiration-time", required = false
	)
	public int invitationTokenExpirationTime();

}