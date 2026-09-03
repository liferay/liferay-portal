/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = {
		"panel.app.order:Integer=1300",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS
	},
	service = PanelApp.class
)
public class LaunchPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "rocket";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return LaunchPortletKeys.LAUNCH;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-72278")) {

			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + LaunchPortletKeys.LAUNCH + ")"
	)
	private Portlet _portlet;

}