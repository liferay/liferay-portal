/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alec Sloan
 */
@ExtendedObjectClassDefinition(
	category = "orders", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.commerce.configuration.CommerceOrderFieldsConfiguration",
	localization = "content/Language", name = "order-fields-configuration-name"
)
public interface CommerceOrderFieldsConfiguration {

	@Meta.AD(deflt = "0", name = "account-cart-max-allowed", required = false)
	public int accountCartMaxAllowed();

	@Meta.AD(deflt = "false", name = "request-quote-enabled", required = false)
	public boolean requestQuoteEnabled();

	@Meta.AD(
		deflt = "true", name = "show-purchase-order-number", required = false
	)
	public boolean showPurchaseOrderNumber();

}