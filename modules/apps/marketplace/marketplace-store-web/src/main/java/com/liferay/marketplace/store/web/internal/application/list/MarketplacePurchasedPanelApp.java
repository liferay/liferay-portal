/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.store.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.marketplace.constants.MarketplaceStorePortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ryan Park
 * @author Joan Kim
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_APPS
	},
	service = PanelApp.class
)
public class MarketplacePurchasedPanelApp extends BasePanelApp {

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return MarketplaceStorePortletKeys.MARKETPLACE_PURCHASED;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (group.getCompanyId() != _portal.getDefaultCompanyId()) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(jakarta.portlet.name=" + MarketplaceStorePortletKeys.MARKETPLACE_PURCHASED + ")"
	)
	private Portlet _portlet;

}