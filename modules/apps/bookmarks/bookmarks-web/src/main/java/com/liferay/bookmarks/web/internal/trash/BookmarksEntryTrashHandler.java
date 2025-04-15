/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.trash;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
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
import com.liferay.trash.TrashHelper;
import com.liferay.trash.constants.TrashActionKeys;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Represents the trash handler for bookmarks entries entity.
 *
 * @author Levente Hudák
 * @author Zsolt Berentey
 */
@Component(
	property = "model.class.name=com.liferay.bookmarks.model.BookmarksEntry",
	service = TrashHandler.class
)
public class BookmarksEntryTrashHandler extends BaseBookmarksTrashHandler {

	@Override
	public void deleteTrashEntry(long classPK) throws PortalException {
		_bookmarksEntryLocalService.deleteEntry(classPK);
	}

	@Override
	public String getClassName() {
		return BookmarksEntry.class.getName();
	}

	@Override
	public ContainerModel getParentContainerModel(long classPK)
		throws PortalException {

		BookmarksEntry entry = _bookmarksEntryLocalService.getEntry(classPK);

		long parentFolderId = entry.getFolderId();

		if (parentFolderId <= 0) {
			return null;
		}

		return getContainerModel(parentFolderId);
	}

	@Override
	public ContainerModel getParentContainerModel(TrashedModel trashedModel)
		throws PortalException {

		BookmarksEntry entry = (BookmarksEntry)trashedModel;

		return getContainerModel(entry.getFolderId());
	}

	@Override
	public String getRestoreContainerModelLink(
			PortletRequest portletRequest, long classPK)
		throws PortalException {

		BookmarksEntry entry = _bookmarksEntryLocalService.getEntry(classPK);

		return BookmarksUtil.getControlPanelLink(
			portletRequest, entry.getFolderId());
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException {

		BookmarksEntry entry = _bookmarksEntryLocalService.getEntry(classPK);

		return BookmarksUtil.getAbsolutePath(
			portletRequest, entry.getFolderId());
	}

	@Override
	public TrashedModel getTrashedModel(long classPK) {
		return _bookmarksEntryLocalService.fetchBookmarksEntry(classPK);
	}

	@Override
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException {

		if (trashActionId.equals(TrashActionKeys.MOVE)) {
			return ModelResourcePermissionUtil.contains(
				_bookmarksFolderModelResourcePermission, permissionChecker,
				groupId, classPK, ActionKeys.ADD_ENTRY);
		}

		return super.hasTrashPermission(
			permissionChecker, groupId, classPK, trashActionId);
	}

	@Override
	public boolean isMovable(long classPK) throws PortalException {
		BookmarksEntry entry = _bookmarksEntryLocalService.getEntry(classPK);

		if (entry.getFolderId() > 0) {
			BookmarksFolder parentFolder =
				_bookmarksFolderLocalService.fetchBookmarksFolder(
					entry.getFolderId());

			if ((parentFolder == null) || parentFolder.isInTrash()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRestorable(long classPK) throws PortalException {
		BookmarksEntry entry = _bookmarksEntryLocalService.getEntry(classPK);

		if (entry.getFolderId() > 0) {
			BookmarksFolder bookmarksFolder =
				_bookmarksFolderLocalService.fetchBookmarksFolder(
					entry.getFolderId());

			if (bookmarksFolder == null) {
				return false;
			}
		}

		if (!hasTrashPermission(
				PermissionThreadLocal.getPermissionChecker(),
				entry.getGroupId(), classPK, TrashActionKeys.RESTORE)) {

			return false;
		}

		return !_trashHelper.isInTrashContainer(entry);
	}

	@Override
	public void moveEntry(
			long userId, long classPK, long containerModelId,
			ServiceContext serviceContext)
		throws PortalException {

		_bookmarksEntryLocalService.moveEntry(classPK, containerModelId);
	}

	@Override
	public void moveTrashEntry(
			long userId, long classPK, long containerId,
			ServiceContext serviceContext)
		throws PortalException {

		_bookmarksEntryLocalService.moveEntryFromTrash(
			userId, classPK, containerId);
	}

	@Override
	public void restoreTrashEntry(long userId, long classPK)
		throws PortalException {

		_bookmarksEntryLocalService.restoreEntryFromTrash(userId, classPK);
	}

	@Override
	protected long getGroupId(long classPK) throws PortalException {
		BookmarksEntry entry = _bookmarksEntryLocalService.getEntry(classPK);

		return entry.getGroupId();
	}

	@Override
	protected boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _bookmarksEntryModelResourcePermission.contains(
			permissionChecker, _bookmarksEntryLocalService.getEntry(classPK),
			actionId);
	}

	@Reference
	private BookmarksEntryLocalService _bookmarksEntryLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksEntry)"
	)
	private ModelResourcePermission<BookmarksEntry>
		_bookmarksEntryModelResourcePermission;

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