/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Mátyás Wollner
 */
@ExtendedObjectClassDefinition(
	category = "accounts", scope = ExtendedObjectClassDefinition.Scope.COMPANY,
	strictScope = true
)
@Meta.OCD(
	id = "com.liferay.account.configuration.AccountEntryAddressSubtypeConfiguration",
	localization = "content/Language",
	name = "account-entry-address-subtype-configuration-name"
)
public interface AccountEntryAddressSubtypeConfiguration {

	@Meta.AD(
		description = "billing-address-subtype-list-type-definition-external-reference-code-description",
		name = "billing-address-subtype-list-type-definition-external-reference-code",
		required = false
	)
	public String
		billingAddressSubtypeListTypeDefinitionExternalReferenceCode();

	@Meta.AD(
		description = "billing-and-shipping-address-subtype-list-type-definition-external-reference-code-description",
		name = "billing-and-shipping-address-subtype-list-type-definition-external-reference-code",
		required = false
	)
	public String
		billingAndShippingAddressSubtypeListTypeDefinitionExternalReferenceCode();

	@Meta.AD(
		description = "shipping-address-subtype-list-type-definition-external-reference-code-description",
		name = "shipping-address-subtype-list-type-definition-external-reference-code",
		required = false
	)
	public String
		shippingAddressSubtypeListTypeDefinitionExternalReferenceCode();

}