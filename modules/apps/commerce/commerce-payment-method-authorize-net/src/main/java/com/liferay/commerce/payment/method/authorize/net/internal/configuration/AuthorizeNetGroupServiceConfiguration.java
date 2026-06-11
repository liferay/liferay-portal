/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.authorize.net.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Luca Pellizzon
 */
@ExtendedObjectClassDefinition(
	category = "payment", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.commerce.payment.method.authorize.net.internal.configuration.AuthorizeNetGroupServiceConfiguration",
	localization = "content/Language",
	name = "commerce-payment-method-authorize-net-group-service-configuration-name"
)
public interface AuthorizeNetGroupServiceConfiguration {

	@Meta.AD(name = "api-login-id", required = false)
	public String apiLoginId();

	@Meta.AD(name = "environment", required = false)
	public String environment();

	@Meta.AD(name = "require-captcha", required = false)
	public boolean requireCaptcha();

	@Meta.AD(name = "require-card-code-verification", required = false)
	public boolean requireCardCodeVerification();

	@Meta.AD(
		deflt = StringPool.TRUE, name = "show-bank-account", required = false
	)
	public boolean showBankAccount();

	@Meta.AD(
		deflt = StringPool.TRUE, name = "show-credit-card", required = false
	)
	public boolean showCreditCard();

	@Meta.AD(
		deflt = StringPool.TRUE, name = "show-store-name", required = false
	)
	public boolean showStoreName();

	@Meta.AD(name = "signature-key", required = false)
	public String signatureKey();

	@Meta.AD(name = "transaction-key", required = false)
	public String transactionKey();

}