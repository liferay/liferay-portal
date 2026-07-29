/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.portlet.action;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.constants.DesignLibraryAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Leandro
 * @author Thiago Buarque
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN,
		"mvc.command.name=/design_library/edit_design_library"
	},
	service = MVCRenderCommand.class
)
public class EditDesignLibraryMVCRenderCommand
	extends BaseDesignLibraryMVCRenderCommand {

	@Override
	protected void checkPermissions(
			PermissionChecker permissionChecker, DepotEntry depotEntry)
		throws PortalException {

		_depotEntryModelResourcePermission.check(
			permissionChecker, depotEntry, ActionKeys.UPDATE);
	}

	@Override
	protected String getPath() {
		return "/edit_design_library.jsp";
	}

	@Reference(target = "(model.class.name=com.liferay.depot.model.DepotEntry)")
	private ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;

}