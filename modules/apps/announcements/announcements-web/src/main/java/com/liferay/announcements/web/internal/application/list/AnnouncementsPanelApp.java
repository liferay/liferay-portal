/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.application.list;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_COMMUNICATION
	},
	service = PanelApp.class
)
public class AnnouncementsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "megaphone";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN + ")"
	)
	private Portlet _portlet;

}