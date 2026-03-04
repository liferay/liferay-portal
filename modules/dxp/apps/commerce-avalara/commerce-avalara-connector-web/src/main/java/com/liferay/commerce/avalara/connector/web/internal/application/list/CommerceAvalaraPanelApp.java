/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.connector.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.commerce.application.list.constants.CommercePanelCategoryKeys;
import com.liferay.commerce.avalara.connector.web.internal.constants.CommerceAvalaraPortletKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Katie Nesterovich
 */
@Component(
	property = {
		"panel.app.order:Integer=400",
		"panel.category.key=" + CommercePanelCategoryKeys.COMMERCE_SETTINGS
	},
	service = PanelApp.class
)
public class CommerceAvalaraPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "plug";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CommerceAvalaraPortletKeys.COMMERCE_AVALARA;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + CommerceAvalaraPortletKeys.COMMERCE_AVALARA + ")"
	)
	private Portlet _portlet;

}