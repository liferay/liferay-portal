/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.custom.attributes;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.expando.kernel.model.BaseCustomAttributesDisplay;
import com.liferay.expando.kernel.model.CustomAttributesDisplay;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ethan Bustad
 */
@Component(
	property = "jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER,
	service = CustomAttributesDisplay.class
)
public class CommerceOrderCustomAttributesDisplay
	extends BaseCustomAttributesDisplay {

	@Override
	public String getClassName() {
		return CommerceOrder.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "tag";
	}

}