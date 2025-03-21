/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.simulation.device.internal.application.list;

import com.liferay.application.list.BaseJSPPanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.product.navigation.simulation.constants.ProductNavigationSimulationConstants;
import com.liferay.product.navigation.simulation.constants.ProductNavigationSimulationPortletKeys;

import jakarta.servlet.ServletContext;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + ProductNavigationSimulationConstants.SIMULATION_PANEL_CATEGORY_KEY
	},
	service = PanelApp.class
)
public class DevicePreviewPanelApp extends BaseJSPPanelApp {

	@Override
	public String getJspPath() {
		return "/simulation_device.jsp";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "screen-size");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ProductNavigationSimulationPortletKeys.
			PRODUCT_NAVIGATION_SIMULATION;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (group.isControlPanel() ||
			!_hasPreviewInDevicePermission(permissionChecker, group)) {

			return false;
		}

		return true;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private boolean _hasPreviewInDevicePermission(
			PermissionChecker permissionChecker, Group group)
		throws PortalException {

		return GroupPermissionUtil.contains(
			permissionChecker, group, ActionKeys.PREVIEW_IN_DEVICE);
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + ProductNavigationSimulationPortletKeys.PRODUCT_NAVIGATION_SIMULATION + ")"
	)
	private Portlet _portlet;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.product.navigation.simulation.device)"
	)
	private ServletContext _servletContext;

}