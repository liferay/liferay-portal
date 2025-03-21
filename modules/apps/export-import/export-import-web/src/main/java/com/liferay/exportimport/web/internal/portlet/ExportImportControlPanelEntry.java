/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;

import org.osgi.service.component.annotations.Component;

/**
 * @author Julio Camarero
 */
@Component(
	property = "jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT_IMPORT,
	service = ControlPanelEntry.class
)
public class ExportImportControlPanelEntry extends BaseControlPanelEntry {

	@Override
	protected boolean hasAccessPermissionDenied(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		if (group.isLayoutPrototype()) {
			return true;
		}

		return super.hasAccessPermissionDenied(
			permissionChecker, group, portlet);
	}

	@Override
	protected boolean hasAccessPermissionExplicitlyGranted(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws PortalException {

		if (GroupPermissionUtil.contains(
				permissionChecker, group, ActionKeys.EXPORT_IMPORT_LAYOUTS)) {

			return true;
		}

		return super.hasAccessPermissionExplicitlyGranted(
			permissionChecker, group, portlet);
	}

}