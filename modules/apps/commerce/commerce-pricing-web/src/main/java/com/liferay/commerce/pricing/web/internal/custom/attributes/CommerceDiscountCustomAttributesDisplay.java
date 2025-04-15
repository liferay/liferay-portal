/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.web.internal.custom.attributes;

import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.pricing.constants.CommercePricingPortletKeys;
import com.liferay.expando.kernel.model.BaseCustomAttributesDisplay;
import com.liferay.expando.kernel.model.CustomAttributesDisplay;

import org.osgi.service.component.annotations.Component;

/**
 * @author Riccardo Alberti
 */
@Component(
	property = "jakarta.portlet.name=" + CommercePricingPortletKeys.COMMERCE_DISCOUNT,
	service = CustomAttributesDisplay.class
)
public class CommerceDiscountCustomAttributesDisplay
	extends BaseCustomAttributesDisplay {

	@Override
	public String getClassName() {
		return CommerceDiscount.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "tag";
	}

}