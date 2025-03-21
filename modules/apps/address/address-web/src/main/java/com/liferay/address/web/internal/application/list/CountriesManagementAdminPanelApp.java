/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.address.web.internal.application.list;

import com.liferay.address.web.internal.constants.AddressPortletKeys;
import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.PortletCategoryKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"panel.app.order:Integer=1100",
		"panel.category.key=" + PortletCategoryKeys.CONTROL_PANEL_CONFIGURATION
	},
	service = PanelApp.class
)
public class CountriesManagementAdminPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return AddressPortletKeys.COUNTRIES_MANAGEMENT_ADMIN;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + AddressPortletKeys.COUNTRIES_MANAGEMENT_ADMIN + ")"
	)
	private Portlet _portlet;

}