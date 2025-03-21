/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;

/**
 * @author Matija Petanjek
 */
@Component(
	property = "jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH,
	service = ControlPanelEntry.class
)
public class DispatchControlPanelEntry extends BaseControlPanelEntry {

	@Override
	public boolean hasAccessPermission(
		PermissionChecker permissionChecker, Group group, Portlet portlet) {

		return permissionChecker.isCompanyAdmin();
	}

}