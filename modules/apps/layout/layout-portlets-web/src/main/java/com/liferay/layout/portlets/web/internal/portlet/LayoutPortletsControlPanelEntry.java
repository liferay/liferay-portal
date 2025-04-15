/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.portlets.web.internal.portlet;

import com.liferay.layout.portlets.web.internal.constants.LayoutsPortletsPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = "jakarta.portlet.name=" + LayoutsPortletsPortletKeys.LAYOUT_PORTLETS,
	service = ControlPanelEntry.class
)
public class LayoutPortletsControlPanelEntry extends BaseControlPanelEntry {

	@Override
	public boolean hasAccessPermissionDenied(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		if (group.isLayout()) {
			return true;
		}

		return super.hasAccessPermissionDenied(
			permissionChecker, group, portlet);
	}

}