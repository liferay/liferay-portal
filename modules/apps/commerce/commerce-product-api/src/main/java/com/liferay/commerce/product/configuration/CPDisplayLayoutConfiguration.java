/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alec Sloan
 */
@ExtendedObjectClassDefinition(
	category = "channel", scope = ExtendedObjectClassDefinition.Scope.GROUP
)
@Meta.OCD(
	id = "com.liferay.commerce.product.configuration.CPDisplayLayoutConfiguration",
	localization = "content/Language",
	name = "cp-display-layout-configuration-name"
)
public interface CPDisplayLayoutConfiguration {

	@Meta.AD(
		description = "asset-category-layout-uuid-help",
		name = "asset-category-layout-uuid", required = false
	)
	public String assetCategoryLayoutUuid();

	@Meta.AD(
		description = "product-layout-uuid-help", name = "product-layout-uuid",
		required = false
	)
	public String productLayoutUuid();

}