/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
@ExtendedObjectClassDefinition(category = "catalog")
@Meta.OCD(
	id = "com.liferay.commerce.product.configuration.CPOptionConfiguration",
	localization = "content/Language", name = "cp-option-configuration-name"
)
public interface CPOptionConfiguration {

	@Meta.AD(
		deflt = "select|select_date|radio|date|checkbox|checkbox_multiple|document_library|numeric|text",
		name = "product-option-form-field-types-allowed", required = false
	)
	public String[] allowedCommerceOptionTypes();

}