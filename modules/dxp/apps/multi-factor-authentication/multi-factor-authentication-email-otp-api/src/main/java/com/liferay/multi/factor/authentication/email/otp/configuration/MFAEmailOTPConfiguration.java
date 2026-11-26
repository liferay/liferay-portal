/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.email.otp.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;

/**
 * @author Tomas Polesovsky
 * @author Marta Medio
 */
@ExtendedObjectClassDefinition(
	category = "multi-factor-authentication",
	scope = ExtendedObjectClassDefinition.Scope.COMPANY, strictScope = true,
	visibilityControllerKey = "multi-factor-authentication"
)
@Meta.OCD(
	id = "com.liferay.multi.factor.authentication.email.otp.configuration.MFAEmailOTPConfiguration",
	localization = "content/Language", name = "mfa-email-otp-configuration-name"
)
public interface MFAEmailOTPConfiguration {

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/admin.email.from.address}",
		description = "set-the-sender-address-on-the-one-time-password-email",
		name = "email-from-field[template]", required = false
	)
	public String emailFromAddress();

	@Meta.AD(
		deflt = "${server-property://com.liferay.portal/admin.email.from.name}",
		name = "email-from-name", required = false
	)
	public String emailFromName();

	@Meta.AD(
		deflt = "${resource:com/liferay/multi/factor/authentication/email/otp/configuration/dependencies/email_otp_sent_body.tmpl}",
		name = "email-otp-sent-body", required = false
	)
	public LocalizedValuesMap emailOTPSentBody();

	@Meta.AD(
		deflt = "${resource:com/liferay/multi/factor/authentication/email/otp/configuration/dependencies/email_otp_sent_subject.tmpl}",
		name = "email-otp-sent-subject", required = false
	)
	public LocalizedValuesMap emailOTPSentSubject();

	@Meta.AD(
		deflt = "false", description = "mfa-email-otp-enabled-description",
		name = "enabled", required = false
	)
	public boolean enabled();

	@Meta.AD(
		deflt = "-1", description = "failed-attempts-allowed-description",
		name = "failed-attempts-allowed", required = false
	)
	public int failedAttemptsAllowed();

	@Meta.AD(
		deflt = "10", description = "order-description", id = "service.ranking",
		name = "order", required = false
	)
	public int order();

	@Meta.AD(
		deflt = "6", description = "otp-size-description", name = "otp-size",
		required = false
	)
	public int otpSize();

	@Meta.AD(
		deflt = "30", description = "resend-email-timeout-description",
		name = "resend-email-timeout", required = false
	)
	public long resendEmailTimeout();

	@Meta.AD(
		deflt = "-1", description = "retry-timeout-description",
		name = "retry-timeout", required = false
	)
	public long retryTimeout();

}