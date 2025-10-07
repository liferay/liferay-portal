/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Marco Leo
 */
@ExtendedObjectClassDefinition(category = "orders")
@Meta.OCD(
	id = "com.liferay.commerce.configuration.CommerceOrderConfiguration",
	localization = "content/Language", name = "orders-configuration-name"
)
public interface CommerceOrderConfiguration {

	@Meta.AD(
		deflt = "15", description = "order-check-interval-description",
		min = "1", name = "order-check-interval", required = false
	)
	public int checkInterval();

	@Meta.AD(deflt = "43200", name = "order-delete-interval", required = false)
	public int deleteInterval();

	@Meta.AD(deflt = "10000", name = "guest-cart-max-allowed", required = false)
	public int guestCartMaxAllowed();

	@Meta.AD(
		deflt = "1000", name = "guest-cart-item-max-allowed", required = false
	)
	public int guestCartItemMaxAllowed();

	@Meta.AD(
		deflt = "false", name = "order-selection-disabled", required = false
	)
	public boolean orderSelectionDisabled();

	@Meta.AD(
		deflt = "false", name = "slow-connection-order-flow-enabled",
		required = false
	)
	public boolean slowConnectionOrderFlowEnabled();

	@Meta.AD(
		deflt = "false", name = "undo-cart-item-deletion-disabled",
		required = false
	)
	public boolean undoCartItemDeletionDisabled();

}