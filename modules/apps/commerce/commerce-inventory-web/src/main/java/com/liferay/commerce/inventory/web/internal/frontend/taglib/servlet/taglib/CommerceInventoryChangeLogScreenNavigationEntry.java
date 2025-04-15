/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.inventory.constants.CommerceInventoryActionKeys;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class CommerceInventoryChangeLogScreenNavigationEntry
	extends CommerceInventoryChangeLogScreenNavigationCategory
	implements ScreenNavigationEntry<CommerceInventoryReplenishmentItem> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(
		User user, CommerceInventoryReplenishmentItem context) {

		PortletResourcePermission portletResourcePermission =
			_commerceInventoryWarehouseModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			PermissionThreadLocal.getPermissionChecker(), null,
			CommerceInventoryActionKeys.MANAGE_INVENTORY);
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		_jspRenderer.renderJSP(
			httpServletRequest, httpServletResponse, "/details/changelog.jsp");
	}

	@Reference(
		target = "(model.class.name=com.liferay.commerce.inventory.model.CommerceInventoryWarehouse)"
	)
	private ModelResourcePermission<CommerceInventoryWarehouse>
		_commerceInventoryWarehouseModelResourcePermission;

	@Reference
	private JSPRenderer _jspRenderer;

}