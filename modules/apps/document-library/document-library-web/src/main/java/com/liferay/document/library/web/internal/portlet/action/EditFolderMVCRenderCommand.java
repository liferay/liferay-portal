/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.web.internal.helper.DLTrashHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Sergio González
 * @author Levente Hudák
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/edit_folder"
	},
	service = MVCRenderCommand.class
)
public class EditFolderMVCRenderCommand extends BaseFolderMVCRenderCommand {

	@Override
	protected void checkPermissions(
			PermissionChecker permissionChecker, Folder folder)
		throws PortalException {

		if (!_folderModelResourcePermission.contains(
				permissionChecker, folder, ActionKeys.ADVANCED_UPDATE) &&
			!_folderModelResourcePermission.contains(
				permissionChecker, folder, ActionKeys.UPDATE)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker.getUserId());
		}
	}

	@Override
	protected DLTrashHelper getDLTrashHelper() {
		return _dlTrashHelper;
	}

	@Override
	protected String getPath() {
		return "/document_library/edit_folder.jsp";
	}

	@Reference
	private DLTrashHelper _dlTrashHelper;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private volatile ModelResourcePermission<Folder>
		_folderModelResourcePermission;

}