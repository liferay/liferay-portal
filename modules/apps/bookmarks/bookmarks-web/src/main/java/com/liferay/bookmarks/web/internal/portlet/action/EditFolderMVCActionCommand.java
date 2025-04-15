/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.exception.FolderNameException;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderService;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.service.TrashEntryService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=/bookmarks/edit_folder"
	},
	service = MVCActionCommand.class
)
public class EditFolderMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				_updateFolder(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteFolders(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteFolders(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				restoreTrashEntries(actionRequest);
			}
			else if (cmd.equals(Constants.SUBSCRIBE)) {
				_subscribeFolder(actionRequest);
			}
			else if (cmd.equals(Constants.UNSUBSCRIBE)) {
				_unsubscribeFolder(actionRequest);
			}

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			if (Validator.isNotNull(portletResource)) {
				hideDefaultSuccessMessage(actionRequest);

				MultiSessionMessages.add(
					actionRequest, portletResource + "requestProcessed");
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchFolderException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/bookmarks/error.jsp");
			}
			else if (exception instanceof FolderNameException) {
				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	protected void restoreTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long[] restoreTrashEntryIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "restoreTrashEntryIds"), 0L);

		for (long restoreTrashEntryId : restoreTrashEntryIds) {
			_trashEntryService.restoreEntry(restoreTrashEntryId);
		}
	}

	private void _deleteFolders(
			ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		long[] deleteFolderIds = null;

		long folderId = ParamUtil.getLong(actionRequest, "folderId");

		if (folderId > 0) {
			deleteFolderIds = new long[] {folderId};
		}
		else {
			deleteFolderIds = StringUtil.split(
				ParamUtil.getString(actionRequest, "folderIds"), 0L);
		}

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (long deleteFolderId : deleteFolderIds) {
			if (moveToTrash) {
				BookmarksFolder folder =
					_bookmarksFolderService.moveFolderToTrash(deleteFolderId);

				trashedModels.add(folder);
			}
			else {
				_bookmarksFolderService.deleteFolder(deleteFolderId);
			}
		}

		if (moveToTrash && !trashedModels.isEmpty()) {
			addDeleteSuccessData(
				actionRequest,
				HashMapBuilder.<String, Object>put(
					"trashedModels", trashedModels
				).build());
		}
	}

	private void _subscribeFolder(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long folderId = ParamUtil.getLong(actionRequest, "folderId");

		_bookmarksFolderService.subscribeFolder(
			themeDisplay.getScopeGroupId(), folderId);
	}

	private void _unsubscribeFolder(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long folderId = ParamUtil.getLong(actionRequest, "folderId");

		_bookmarksFolderService.unsubscribeFolder(
			themeDisplay.getScopeGroupId(), folderId);
	}

	private void _updateFolder(ActionRequest actionRequest) throws Exception {
		long folderId = ParamUtil.getLong(actionRequest, "folderId");

		long newFolderId = ParamUtil.getLong(actionRequest, "newFolderId");
		String name = ParamUtil.getString(actionRequest, "name");
		String description = ParamUtil.getString(actionRequest, "description");

		boolean mergeWithParentFolder = ParamUtil.getBoolean(
			actionRequest, "mergeWithParentFolder");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			BookmarksFolder.class.getName(), actionRequest);

		if (folderId <= 0) {
			_bookmarksFolderService.addFolder(
				ParamUtil.getLong(actionRequest, "parentFolderId"), name,
				description, serviceContext);
		}
		else if (mergeWithParentFolder) {
			_bookmarksFolderService.mergeFolders(folderId, newFolderId);
		}
		else {
			_bookmarksFolderService.updateFolder(
				folderId, newFolderId, name, description, serviceContext);
		}
	}

	@Reference
	private BookmarksFolderService _bookmarksFolderService;

	@Reference
	private TrashEntryService _trashEntryService;

}