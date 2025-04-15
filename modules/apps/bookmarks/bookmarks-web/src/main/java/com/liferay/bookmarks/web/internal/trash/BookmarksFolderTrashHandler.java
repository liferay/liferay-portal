/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.trash;

import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.bookmarks.web.internal.asset.model.BookmarksFolderAssetRenderer;
import com.liferay.bookmarks.web.internal.util.BookmarksUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Represents the trash handler for bookmarks folder entity.
 *
 * @author Eudaldo Alonso
 */
@Component(
	property = "model.class.name=com.liferay.bookmarks.model.BookmarksFolder",
	service = TrashHandler.class
)
public class BookmarksFolderTrashHandler extends BaseBookmarksTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_bookmarksFolderLocalService.deleteFolder(classPK, false);
	}

	@Override
	public String getClassName() {
		return BookmarksFolder.class.getName();
	}

	@Override
	public String getDeleteMessage() {
		return "found-in-deleted-folder-x";
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		BookmarksFolder folder = _getBookmarksFolder(classPK);

		long parentFolderId = folder.getParentFolderId();

		if (parentFolderId <= 0) {
			return null;
		}

		return getContainerModel(parentFolderId);
	}

	@Override
	public String getRestoreContainedModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		BookmarksFolder folder = _getBookmarksFolder(classPK);

		return BookmarksUtil.getControlPanelLink(
			portletRequest, folder.getFolderId());
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		BookmarksFolder folder = _getBookmarksFolder(classPK);

		return BookmarksUtil.getControlPanelLink(
			portletRequest, folder.getParentFolderId());
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		BookmarksFolder folder = _getBookmarksFolder(classPK);

		return BookmarksUtil.getAbsolutePath(
			portletRequest, folder.getParentFolderId());
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _bookmarksFolderLocalService.fetchBookmarksFolder(classPK);
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK) throws PortalException {
		BookmarksFolder folder = _getBookmarksFolder(classPK);

		return new BookmarksFolderAssetRenderer(
			folder, _trashHelper, _bookmarksFolderModelResourcePermission);
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_bookmarksFolderModelResourcePermission, permissionChecker,
				groupId, classPK, ActionKeys.ADD_FOLDER);
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isContainerModel() {
		return true;
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		BookmarksFolder folder = _getBookmarksFolder(classPK);

		if (folder.getParentFolderId() > 0) {
			BookmarksFolder parentFolder =
				_bookmarksFolderLocalService.fetchBookmarksFolder(
					folder.getParentFolderId());

			if ((parentFolder == null) || parentFolder.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		BookmarksFolder folder = _getBookmarksFolder(classPK);

		if (folder.getParentFolderId() > 0) {
			BookmarksFolder parentFolder =
				_bookmarksFolderLocalService.fetchBookmarksFolder(
					folder.getParentFolderId());

			if (parentFolder == null) {
				return false;
			}
		}

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				folder.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(folder);
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_bookmarksFolderLocalService.moveFolder(classPK, containerModelId);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerId,
			ServiceContext serviceContext)
		throws PortalException {

		_bookmarksFolderLocalService.moveFolderFromTrash(
			userId, classPK, containerId);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_bookmarksFolderLocalService.restoreFolderFromTrash(userId, classPK);
	}

	@Override
	protected long getGroupId(long classPK) throws PortalException {
		BookmarksFolder folder = _getBookmarksFolder(classPK);

		return folder.getGroupId();
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		BookmarksFolder folder = _getBookmarksFolder(classPK);

		return _bookmarksFolderModelResourcePermission.contains(
			permissionChecker, folder, actionId);
	}

	private BookmarksFolder _getBookmarksFolder(long classPK)
		throws PortalException {

		return _bookmarksFolderLocalService.getFolder(classPK);
	}

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksFolder)"
	)
	private ModelResourcePermission<BookmarksFolder>
		_bookmarksFolderModelResourcePermission;

	@Reference
	private TrashHelper _trashHelper;

}