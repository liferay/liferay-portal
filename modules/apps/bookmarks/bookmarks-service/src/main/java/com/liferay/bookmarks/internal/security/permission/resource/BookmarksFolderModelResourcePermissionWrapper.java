/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.internal.security.permission.resource;

import com.liferay.bookmarks.constants.BookmarksConstants;
import com.liferay.bookmarks.constants.BookmarksFolderConstants;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.exportimport.kernel.staging.permission.StagingPermission;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.DynamicInheritancePermissionLogic;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.StagedModelPermissionLogic;
import com.liferay.portal.kernel.util.PropsValues;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Preston Crary
 */
@Component(
	property = "model.class.name=com.liferay.bookmarks.model.BookmarksFolder",
	service = ModelResourcePermission.class
)
public class BookmarksFolderModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<BookmarksFolder> {

	@Override
	protected ModelResourcePermission<BookmarksFolder>
		doGetModelResourcePermission() {

		return ModelResourcePermissionFactory.create(
			BookmarksFolder.class, BookmarksFolder::getFolderId,
			_bookmarksFolderLocalService::getFolder, _portletResourcePermission,
			(modelResourcePermission, consumer) -> {
				consumer.accept(
					new StagedModelPermissionLogic<>(
						_stagingPermission, BookmarksPortletKeys.BOOKMARKS,
						BookmarksFolder::getFolderId));

				if (PropsValues.PERMISSIONS_VIEW_DYNAMIC_INHERITANCE) {
					consumer.accept(
						new DynamicInheritancePermissionLogic<>(
							modelResourcePermission, _getFetchParentFunction(),
							true));
				}
			},
			actionId -> {
				if (ActionKeys.ADD_FOLDER.equals(actionId)) {
					return ActionKeys.ADD_SUBFOLDER;
				}

				return actionId;
			});
	}

	private UnsafeFunction<BookmarksFolder, BookmarksFolder, PortalException>
		_getFetchParentFunction() {

		return folder -> {
			long folderId = folder.getParentFolderId();

			if (BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID == folderId) {
				return null;
			}

			if (folder.isInTrash()) {
				return _bookmarksFolderLocalService.fetchBookmarksFolder(
					folderId);
			}

			return _bookmarksFolderLocalService.getFolder(folderId);
		};
	}

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

	@Reference(
		target = "(resource.name=" + BookmarksConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private StagingPermission _stagingPermission;

}