/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.commerce.application.list.constants.CommercePanelCategoryKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"panel.app.order:Integer=150",
		"panel.category.key=" + CommercePanelCategoryKeys.COMMERCE_ORDER_MANAGEMENT
	},
	service = PanelApp.class
)
public class CommerceOrderTypePanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "order-form-tag";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CommercePortletKeys.COMMERCE_ORDER_TYPE;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_ORDER_TYPE + ")"
	)
	private Portlet _portlet;

}