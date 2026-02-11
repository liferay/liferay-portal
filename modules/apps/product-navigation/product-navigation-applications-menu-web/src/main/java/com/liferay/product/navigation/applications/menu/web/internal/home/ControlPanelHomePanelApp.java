/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.home;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.product.navigation.applications.menu.web.internal.constants.ControlPanelHomePortletKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Locale;


/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"panel.app.order:Integer=1000",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_HOME
	},
	service = PanelApp.class
)
public class ControlPanelHomePanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ControlPanelHomePortletKeys.CONTROL_PANEL_HOME;
	}

    @Override
	public String getLabel(Locale locale) {
		return "Home";
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ControlPanelHomePortletKeys.CONTROL_PANEL_HOME + ")"
	)
	private Portlet _portlet;

}