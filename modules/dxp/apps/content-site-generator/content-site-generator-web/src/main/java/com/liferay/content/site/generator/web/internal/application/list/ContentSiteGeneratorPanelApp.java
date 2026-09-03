/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.site.generator.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.content.site.generator.web.internal.constants.ContentSiteGeneratorPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mylena Monte
 */
@Component(
	property = {
		"panel.app.order:Integer=1400",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS
	},
	service = PanelApp.class
)
public class ContentSiteGeneratorPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "stars";
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ContentSiteGeneratorPortletKeys.CONTENT_SITE_GENERATOR;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-62272")) {

			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ContentSiteGeneratorPortletKeys.CONTENT_SITE_GENERATOR + ")"
	)
	private Portlet _portlet;

}