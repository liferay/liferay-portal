/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.search.experiences.constants.SXPPanelCategoryKeys;
import com.liferay.search.experiences.constants.SXPPortletKeys;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	enabled = false,
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + SXPPanelCategoryKeys.CONTROL_PANEL_SEARCH_EXPERIENCES
	},
	service = PanelApp.class
)
public class SXPBlueprintAdminPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return SXPPortletKeys.SXP_BLUEPRINT_ADMIN;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (Objects.equals(searchEngineInformation.getVendorString(), "Solr")) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference
	protected SearchEngineInformation searchEngineInformation;

	@Reference(
		target = "(jakarta.portlet.name=" + SXPPortletKeys.SXP_BLUEPRINT_ADMIN + ")"
	)
	private Portlet _portlet;

}