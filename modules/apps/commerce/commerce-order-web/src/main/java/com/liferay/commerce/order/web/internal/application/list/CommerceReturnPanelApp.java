/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.commerce.application.list.constants.CommercePanelCategoryKeys;
import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"panel.app.order:Integer=500",
		"panel.category.key=" + CommercePanelCategoryKeys.COMMERCE_ORDER_MANAGEMENT
	},
	service = PanelApp.class
)
public class CommerceReturnPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CommercePortletKeys.COMMERCE_RETURN;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled("LPD-10562")) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_RETURN + ")"
	)
	private Portlet _portlet;

}