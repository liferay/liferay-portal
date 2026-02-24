/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.set.prototype.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"panel.app.order:Integer=200",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_SITES
	},
	service = PanelApp.class
)
public class SiteTemplatesPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "site-template";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE + ")"
	)
	private Portlet _portlet;

}