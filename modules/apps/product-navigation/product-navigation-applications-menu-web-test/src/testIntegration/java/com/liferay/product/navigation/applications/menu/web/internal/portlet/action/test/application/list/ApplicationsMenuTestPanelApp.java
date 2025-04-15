/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.portlet.action.test.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.product.navigation.applications.menu.web.internal.portlet.action.test.constants.ApplicationsMenuTestPanelCategoryKeys;
import com.liferay.product.navigation.applications.menu.web.internal.portlet.action.test.constants.ApplicationsMenuTestPortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + ApplicationsMenuTestPanelCategoryKeys.APPLICATIONS_MENU_TEST_PANEL_CATEGORY
	},
	service = PanelApp.class
)
public class ApplicationsMenuTestPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ApplicationsMenuTestPortletKeys.APPLICATIONS_MENU_TEST_PORTLET;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ApplicationsMenuTestPortletKeys.APPLICATIONS_MENU_TEST_PORTLET + ")"
	)
	private Portlet _portlet;

}