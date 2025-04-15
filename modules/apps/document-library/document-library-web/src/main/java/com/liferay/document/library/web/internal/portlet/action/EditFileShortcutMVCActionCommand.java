/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.exception.FileShortcutPermissionException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFileShortcutException;
import com.liferay.document.library.kernel.model.DLFileShortcutConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLTrashService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

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
	service = MVCActionCommand.class
)
public class EditFileShortcutMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortalException {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateFileShortcut(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteFileShortcut(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteFileShortcut(actionRequest, true);
			}

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			if (Validator.isNotNull(portletResource)) {
				hideDefaultSuccessMessage(actionRequest);

				MultiSessionMessages.add(
					actionRequest, portletResource + "requestProcessed");
			}
		}
		catch (NoSuchFileShortcutException | PrincipalException exception) {
			SessionErrors.add(actionRequest, exception.getClass());

			actionResponse.setRenderParameter(
				"mvcPath", "/document_library/error.jsp");
		}
		catch (FileShortcutPermissionException | NoSuchFileEntryException
					exception) {

			SessionErrors.add(actionRequest, exception.getClass());
		}
	}

	private void _deleteFileShortcut(
			ActionRequest actionRequest, boolean moveToTrash)
		throws PortalException {

		long fileShortcutId = ParamUtil.getLong(
			actionRequest, "fileShortcutId");

		if (moveToTrash) {
			FileShortcut fileShortcut = _dlTrashService.moveFileShortcutToTrash(
				fileShortcutId);

			if (!(fileShortcut.getModel() instanceof TrashedModel)) {
				hideDefaultSuccessMessage(actionRequest);

				return;
			}

			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels",
					ListUtil.fromArray((TrashedModel)fileShortcut.getModel())
				).build());
		}
		else {
			_dlAppService.deleteFileShortcut(fileShortcutId);
		}
	}

	private void _updateFileShortcut(ActionRequest actionRequest)
		throws PortalException {

		long fileShortcutId = ParamUtil.getLong(
			actionRequest, "fileShortcutId");

		long folderId = ParamUtil.getLong(actionRequest, "folderId");
		long toFileEntryId = ParamUtil.getLong(actionRequest, "toFileEntryId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DLFileShortcutConstants.getClassName(), actionRequest);

		if (fileShortcutId <= 0) {

			// Add file shortcut

			long repositoryId = ParamUtil.getLong(
				actionRequest, "repositoryId");

			_dlAppService.addFileShortcut(
				null, repositoryId, folderId, toFileEntryId, serviceContext);
		}
		else {

			// Update file shortcut

			_dlAppService.updateFileShortcut(
				fileShortcutId, folderId, toFileEntryId, serviceContext);
		}
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLTrashService _dlTrashService;

}