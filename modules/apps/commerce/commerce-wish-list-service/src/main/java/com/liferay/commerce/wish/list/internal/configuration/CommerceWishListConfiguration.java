/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.wish.list.internal.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alessio Antonio Rendina
 */
@ExtendedObjectClassDefinition(category = "wish-lists")
@Meta.OCD(
	id = "com.liferay.commerce.wish.list.internal.configuration.CommerceWishListConfiguration",
	localization = "content/Language",
	name = "commerce-wish-list-configuration-name"
)
public interface CommerceWishListConfiguration {

	@Meta.AD(
		deflt = "15", description = "wish-list-check-interval-description",
		min = "1", name = "wish-list-check-interval", required = false
	)
	public int checkInterval();

	@Meta.AD(
		deflt = "43200", name = "wish-list-delete-interval", required = false
	)
	public int deleteInterval();

	@Meta.AD(
		deflt = "1000",
		name = "commerce-wish-list-guest-wish-list-item-max-allowed",
		required = false
	)
	public int guestWishListItemMaxAllowed();

	@Meta.AD(
		deflt = "10000",
		name = "commerce-wish-list-guest-wish-list-max-allowed",
		required = false
	)
	public int guestWishListMaxAllowed();

}