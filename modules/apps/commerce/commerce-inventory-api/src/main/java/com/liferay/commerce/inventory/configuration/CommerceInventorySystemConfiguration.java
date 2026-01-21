/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Luca Pellizzon
 */
@ExtendedObjectClassDefinition(
	category = "inventory", scope = ExtendedObjectClassDefinition.Scope.SYSTEM
)
@Meta.OCD(
	id = "com.liferay.commerce.inventory.configuration.CommerceInventorySystemConfiguration",
	localization = "content/Language",
	name = "commerce-inventory-system-configuration-name"
)
public interface CommerceInventorySystemConfiguration {

	@Meta.AD(
		deflt = "60", min = "1", name = "check-inventory-audit-interval",
		required = false
	)
	public int checkCommerceInventoryAuditQuantityInterval();

	@Meta.AD(
		deflt = "60",
		description = "check-temporary-booked-quantity-interval-description",
		min = "1", name = "check-temporary-booked-quantity-interval",
		required = false
	)
	public int checkCommerceInventoryTemporaryBookedQuantityInterval();

	@Meta.AD(
		deflt = "12", name = "delete-inventory-audit-month-interval",
		required = false
	)
	public int deleteAuditMonthInterval();

}