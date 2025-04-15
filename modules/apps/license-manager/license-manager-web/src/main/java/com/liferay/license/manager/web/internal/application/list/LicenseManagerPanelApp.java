/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.license.manager.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.license.manager.web.internal.constants.LicenseManagerPortletKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"panel.app.order:Integer=300",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_APPS
	},
	service = PanelApp.class
)
public class LicenseManagerPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return LicenseManagerPortletKeys.LICENSE_MANAGER;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + LicenseManagerPortletKeys.LICENSE_MANAGER + ")"
	)
	private Portlet _portlet;

}