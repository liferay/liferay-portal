/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.product.navigation.control.panel;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "jakarta.portlet.name=" + ProductNavigationProductMenuPortletKeys.PRODUCT_NAVIGATION_PRODUCT_MENU,
	service = ControlPanelEntry.class
)
public class ProductMenuProductNavigationControlPanelEntry
	extends BaseControlPanelEntry {

	@Override
	protected boolean hasPermissionImplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		User user = permissionChecker.getUser();

		if (permissionChecker.isSignedIn() &&
			(user.isSetupComplete() || _isImpersonated())) {

			return true;
		}

		return false;
	}

	private boolean _isImpersonated() {
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return false;
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (themeDisplay == null) {
			return false;
		}

		return themeDisplay.isImpersonated();
	}

}