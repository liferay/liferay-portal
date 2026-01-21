/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.method.mercanet.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Luca Pellizzon
 */
@ExtendedObjectClassDefinition(
	category = "payment", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.commerce.payment.method.mercanet.internal.configuration.MercanetGroupServiceConfiguration",
	localization = "content/Language",
	name = "commerce-payment-method-mercanet-group-service-configuration-name"
)
public interface MercanetGroupServiceConfiguration {

	@Meta.AD(name = "environment", required = false)
	public String environment();

	@Meta.AD(name = "key-version", required = false)
	public String keyVersion();

	@Meta.AD(name = "merchant-id", required = false)
	public String merchantId();

	@Meta.AD(name = "secret-key", required = false, type = Meta.Type.Password)
	public String secretKey();

}