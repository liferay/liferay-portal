/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.staging.StagingGroupHelper;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Vendel Toreki
 */
@Component(
	property = {
		"panel.app.order:Integer=1400",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_BATCH_PLANNER
	},
	service = PanelApp.class
)
public class CompanyImportPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "import";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ExportImportPortletKeys.COMPANY_IMPORT;
	}

	@Override
	protected Group getGroup(HttpServletRequest httpServletRequest) {
		return _stagingGroupHelper.fetchCompanyGroup(
			_portal.getCompanyId(httpServletRequest));
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(jakarta.portlet.name=" + ExportImportPortletKeys.COMPANY_IMPORT + ")"
	)
	private Portlet _portlet;

	@Reference
	private StagingGroupHelper _stagingGroupHelper;

}