/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.service.BookmarksFolderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import jakarta.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alexander Chow
 */
@Component(
	property = "jakarta.portlet.name=" + BookmarksPortletKeys.BOOKMARKS,
	service = AssetRendererFactory.class
)
public class BookmarksFolderAssetRendererFactory
	extends BaseAssetRendererFactory<BookmarksFolder> {

	public static final String TYPE = "bookmark_folder";

	public BookmarksFolderAssetRendererFactory() {
		setCategorizable(false);
		setClassName(BookmarksFolder.class.getName());
		setPortletId(BookmarksPortletKeys.BOOKMARKS);
		setSearchable(true);
	}

	@Override
	public AssetRenderer<BookmarksFolder> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		BookmarksFolder folder =
			_bookmarksFolderLocalService.fetchBookmarksFolder(classPK);

		if (folder == null) {
			return new BookmarksRootFolderAssetRenderer(
				_groupLocalService.getGroup(classPK));
		}

		BookmarksFolderAssetRenderer bookmarksFolderAssetRenderer =
			new BookmarksFolderAssetRenderer(
				folder, _trashHelper, _bookmarksFolderModelResourcePermission);

		bookmarksFolderAssetRenderer.setAssetRendererType(type);
		bookmarksFolderAssetRenderer.setServletContext(_servletContext);

		return bookmarksFolderAssetRenderer;
	}

	@Override
	public String getClassName() {
		return BookmarksFolder.class.getName();
	}

	@Override
	public String getIconCssClass() {
		return "folder";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLView(
		LiferayPortletResponse liferayPortletResponse,
		WindowState windowState) {

		LiferayPortletURL liferayPortletURL =
			liferayPortletResponse.createLiferayPortletURL(
				BookmarksPortletKeys.BOOKMARKS, PortletRequest.RENDER_PHASE);

		try {
			liferayPortletURL.setWindowState(windowState);
		}
		catch (WindowStateException windowStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(windowStateException);
			}
		}

		return liferayPortletURL;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, long classPK, String actionId)
		throws PortalException {

		return _bookmarksFolderModelResourcePermission.contains(
			permissionChecker, classPK, actionId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BookmarksFolderAssetRendererFactory.class);

	@Reference
	private BookmarksFolderLocalService _bookmarksFolderLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.bookmarks.model.BookmarksFolder)"
	)
	private ModelResourcePermission<BookmarksFolder>
		_bookmarksFolderModelResourcePermission;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.bookmarks.web)")
	private ServletContext _servletContext;

	@Reference
	private TrashHelper _trashHelper;

}