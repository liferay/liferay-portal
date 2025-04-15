/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.web.internal.constants.DLWebKeys;
import com.liferay.document.library.web.internal.display.context.DLEditFileShortcutDisplayContext;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Levente Hudák
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
		"mvc.command.name=/document_library/edit_file_shortcut"
	},
	service = MVCRenderCommand.class
)
public class EditFileShortcutMVCRenderCommand
	extends BaseFileShortcutMVCRenderCommand {

	@Override
	protected void checkPermissions(
			PermissionChecker permissionChecker, FileShortcut fileShortcut)
		throws PortalException {

		_fileShortcutModelResourcePermission.check(
			permissionChecker, fileShortcut, ActionKeys.UPDATE);
	}

	@Override
	protected String getPath() {
		return "/document_library/edit_file_shortcut.jsp";
	}

	@Override
	protected void setAttributes(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortalException {

		super.setAttributes(renderRequest, renderResponse);

		renderRequest.setAttribute(
			DLWebKeys.DOCUMENT_LIBRARY_EDIT_FILE_SHORTCUT_DISPLAY_CONTEXT,
			new DLEditFileShortcutDisplayContext(
				_dlAppService, _itemSelector, _language,
				_portal.getLiferayPortletRequest(renderRequest),
				_portal.getLiferayPortletResponse(renderResponse)));
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileShortcut)"
	)
	private volatile ModelResourcePermission<FileShortcut>
		_fileShortcutModelResourcePermission;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}