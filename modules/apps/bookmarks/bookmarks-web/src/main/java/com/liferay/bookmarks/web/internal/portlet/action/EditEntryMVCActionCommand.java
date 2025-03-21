/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.portlet.action;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.exception.AssetTagException;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.exception.EntryURLException;
import com.liferay.bookmarks.exception.NoSuchEntryException;
import com.liferay.bookmarks.exception.NoSuchFolderException;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryService;
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
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
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
 * @author Levente Hudák
 */
@Component(
	property = {
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
		"jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS_ADMIN,
		"mvc.command.name=/bookmarks/edit_entry",
		"mvc.command.name=/bookmarks/move_entry"
	},
	service = MVCActionCommand.class
)
public class EditEntryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			BookmarksEntry entry = null;

			if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
				entry = _updateEntry(actionRequest);
			}
			else if (cmd.equals(Constants.DELETE)) {
				_deleteEntry(actionRequest, false);
			}
			else if (cmd.equals(Constants.MOVE)) {
				_moveEntries(actionRequest);
			}
			else if (cmd.equals(Constants.MOVE_TO_TRASH)) {
				_deleteEntry(actionRequest, true);
			}
			else if (cmd.equals(Constants.RESTORE)) {
				restoreTrashEntries(actionRequest);
			}
			else if (cmd.equals(Constants.SUBSCRIBE)) {
				_subscribeEntry(actionRequest);
			}
			else if (cmd.equals(Constants.UNSUBSCRIBE)) {
				_unsubscribeEntry(actionRequest);
			}

			String portletResource = ParamUtil.getString(
				actionRequest, "portletResource");

			if (Validator.isNotNull(portletResource)) {
				hideDefaultSuccessMessage(actionRequest);

				MultiSessionMessages.add(
					actionRequest, portletResource + "requestProcessed");
			}

			String redirect = _portal.escapeRedirect(
				ParamUtil.getString(actionRequest, "redirect"));

			if (Validator.isNotNull(redirect)) {
				if (cmd.equals(Constants.ADD) && (entry != null)) {
					String portletId = HttpComponentsUtil.getParameter(
						redirect, "portletResource", false);

					String namespace = _portal.getPortletNamespace(portletId);

					if (Validator.isNotNull(portletId)) {
						redirect = HttpComponentsUtil.addParameter(
							redirect, namespace + "className",
							BookmarksEntry.class.getName());
						redirect = HttpComponentsUtil.addParameter(
							redirect, namespace + "classPK",
							entry.getEntryId());
					}
				}

				actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
			}
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchEntryException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				actionResponse.setRenderParameter(
					"mvcPath", "/bookmarks/error.jsp");
			}
			else if (exception instanceof EntryURLException ||
					 exception instanceof NoSuchFolderException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof AssetCategoryException ||
					 exception instanceof AssetTagException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
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

	private void _deleteEntry(ActionRequest actionRequest, boolean moveToTrash)
		throws Exception {

		long[] deleteEntryIds = null;

		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		if (entryId > 0) {
			deleteEntryIds = new long[] {entryId};
		}
		else {
			deleteEntryIds = ParamUtil.getLongValues(
				actionRequest, "rowIdsBookmarksEntry");
		}

		List<TrashedModel> trashedModels = new ArrayList<>();

		for (long deleteEntryId : deleteEntryIds) {
			if (moveToTrash) {
				BookmarksEntry entry = _bookmarksEntryService.moveEntryToTrash(
					deleteEntryId);

				trashedModels.add(entry);
			}
			else {
				_bookmarksEntryService.deleteEntry(deleteEntryId);
			}
		}

		long[] deleteFolderIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsBookmarksFolder");

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

	private void _moveEntries(ActionRequest actionRequest) throws Exception {
		long newFolderId = ParamUtil.getLong(actionRequest, "newFolderId");

		long[] folderIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsBookmarksFolder");

		for (long folderId : folderIds) {
			_bookmarksFolderService.moveFolder(folderId, newFolderId);
		}

		long[] entryIds = ParamUtil.getLongValues(
			actionRequest, "rowIdsBookmarksEntry");

		for (long entryId : entryIds) {
			_bookmarksEntryService.moveEntry(entryId, newFolderId);
		}
	}

	private void _subscribeEntry(ActionRequest actionRequest) throws Exception {
		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		_bookmarksEntryService.subscribeEntry(entryId);
	}

	private void _unsubscribeEntry(ActionRequest actionRequest)
		throws Exception {

		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		_bookmarksEntryService.unsubscribeEntry(entryId);
	}

	private BookmarksEntry _updateEntry(ActionRequest actionRequest)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long entryId = ParamUtil.getLong(actionRequest, "entryId");

		long groupId = themeDisplay.getScopeGroupId();
		String name = ParamUtil.getString(actionRequest, "name");
		String url = ParamUtil.getString(actionRequest, "url");
		String description = ParamUtil.getString(actionRequest, "description");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			BookmarksEntry.class.getName(), actionRequest);

		BookmarksEntry entry = null;

		if (entryId <= 0) {

			// Add entry

			long folderId = ParamUtil.getLong(actionRequest, "folderId");

			entry = _bookmarksEntryService.addEntry(
				groupId, folderId, name, url, description, serviceContext);
		}
		else {

			// Update entry

			long newFolderId = ParamUtil.getLong(actionRequest, "newFolderId");

			entry = _bookmarksEntryService.updateEntry(
				entryId, groupId, newFolderId, name, url, description,
				serviceContext);
		}

		return entry;
	}

	@Reference
	private BookmarksEntryService _bookmarksEntryService;

	@Reference
	private BookmarksFolderService _bookmarksFolderService;

	@Reference
	private Portal _portal;

	@Reference
	private TrashEntryService _trashEntryService;

}