/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.trash;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ContainerModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.BaseTrashHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the trash handler for bookmarks folder entity.
 *
 * @author Eudaldo Alonso
 */
public abstract class BaseBookmarksTrashHandler extends BaseTrashHandler {

	@Override
	public ContainerModel getContainerModel(long containerModelId)
		throws PortalException {

		return BookmarksFolderLocalServiceUtil.getFolder(containerModelId);
	}

	@Override
	public String getContainerModelClassName(long classPK) {
		return BookmarksFolder.class.getName();
	}

	@Override
	public List<ContainerModel> getContainerModels(
			long classPK, long parentContainerModelId, int start, int end)
		throws PortalException {

		return TransformUtil.transform(
			BookmarksFolderLocalServiceUtil.getFolders(
				getGroupId(classPK), parentContainerModelId, start, end),
			folder -> folder);
	}

	@Override
	public int getContainerModelsCount(
			long classPK, long parentContainerModelId)
		throws PortalException {

		return BookmarksFolderLocalServiceUtil.getFoldersCount(
			getGroupId(classPK), parentContainerModelId);
	}

	@Override
	public List<ContainerModel> getParentContainerModels(long classPK)
		throws PortalException {

		List<ContainerModel> containerModels = new ArrayList<>();

		ContainerModel containerModel = getParentContainerModel(classPK);

		if (containerModel == null) {
			return containerModels;
		}

		containerModels.add(containerModel);

		while (containerModel.getParentContainerModelId() > 0) {
			containerModel = getContainerModel(
				containerModel.getParentContainerModelId());

			if (containerModel == null) {
				break;
			}

			containerModels.add(containerModel);
		}

		return containerModels;
	}

	@Override
	public String getRootContainerModelName() {
		return "home";
	}

	@Override
	public String getSubcontainerModelName() {
		return "folder";
	}

	@Override
	public String getTrashContainedModelName() {
		return "bookmarks";
	}

	@Override
	public int getTrashContainedModelsCount(long classPK)
		throws PortalException {

		BookmarksFolder folder = BookmarksFolderLocalServiceUtil.getFolder(
			classPK);

		return BookmarksEntryLocalServiceUtil.getEntriesCount(
			folder.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public String getTrashContainerModelName() {
		return "folders";
	}

	@Override
	public int getTrashContainerModelsCount(long classPK)
		throws PortalException {

		BookmarksFolder folder = BookmarksFolderLocalServiceUtil.getFolder(
			classPK);

		return BookmarksFolderLocalServiceUtil.getFoldersCount(
			folder.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	@Override
	public List<TrashedModel> getTrashModelTrashedModels(
			long classPK, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException {

		BookmarksFolder folder = BookmarksFolderLocalServiceUtil.getFolder(
			classPK);

		return TransformUtil.transform(
			BookmarksFolderLocalServiceUtil.getFoldersAndEntries(
				folder.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH,
				start, end, orderByComparator),
			folderOrEntry -> {
				if (folderOrEntry instanceof BookmarksFolder) {
					return (BookmarksFolder)folderOrEntry;
				}

				return (BookmarksEntry)folderOrEntry;
			});
	}

	@Override
	public int getTrashModelsCount(long classPK) throws PortalException {
		BookmarksFolder folder = BookmarksFolderLocalServiceUtil.getFolder(
			classPK);

		return BookmarksFolderLocalServiceUtil.getFoldersAndEntriesCount(
			folder.getGroupId(), classPK, WorkflowConstants.STATUS_IN_TRASH);
	}

	protected abstract long getGroupId(long classPK) throws PortalException;

}