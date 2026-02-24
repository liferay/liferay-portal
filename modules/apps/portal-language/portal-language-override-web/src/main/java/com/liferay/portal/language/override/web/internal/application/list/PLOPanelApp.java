/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.language.override.web.internal.constants.PLOPortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Drew Brokke
 */
@Component(
	property = {
		"panel.app.order:Integer=800",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_CONFIGURATION
	},
	service = PanelApp.class
)
public class PLOPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "automatic-translate";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return PLOPortletKeys.PORTAL_LANGUAGE_OVERRIDE;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + PLOPortletKeys.PORTAL_LANGUAGE_OVERRIDE + ")"
	)
	private Portlet _portlet;

}