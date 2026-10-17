/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.applications.menu.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.product.navigation.applications.menu.web.internal.constants.ProductNavigationApplicationsMenuPortletKeys;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Leandro
 */
@Component(
	property = {
		"panel.app.order:Integer=1000",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL
	},
	service = PanelApp.class
)
public class ControlPanelHomePanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "home";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "home");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ProductNavigationApplicationsMenuPortletKeys.CONTROL_PANEL_HOME;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ProductNavigationApplicationsMenuPortletKeys.CONTROL_PANEL_HOME + ")"
	)
	private Portlet _portlet;

}