/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Greenwald
 * @author Prathima Shreenath
 */
@Component(
	property = {
		"panel.category.key=" + PanelCategoryKeys.SITE_ADMINISTRATION_CONFIGURATION,
		"service.ranking:Integer=600"
	},
	service = PanelApp.class
)
public class ReportsEngineConsolePanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ReportsEngineConsolePortletKeys.REPORTS_ADMIN;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ReportsEngineConsolePortletKeys.REPORTS_ADMIN + ")"
	)
	private Portlet _portlet;

}