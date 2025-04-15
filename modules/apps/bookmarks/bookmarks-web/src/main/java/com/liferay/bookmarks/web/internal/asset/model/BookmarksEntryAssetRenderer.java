/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bookmarks.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.bookmarks.constants.BookmarksPortletKeys;
import com.liferay.bookmarks.constants.BookmarksWebKeys;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Julio Camarero
 * @author Juan Fernández
 * @author Sergio González
 */
public class BookmarksEntryAssetRenderer
	extends BaseJSPAssetRenderer<BookmarksEntry> implements TrashRenderer {

	public BookmarksEntryAssetRenderer(
		BookmarksEntry entry,
		ModelResourcePermission<BookmarksEntry>
			bookmarksEntryModelResourcePermission) {

		_entry = entry;
		_bookmarksEntryModelResourcePermission =
			bookmarksEntryModelResourcePermission;
	}

	@Override
	public BookmarksEntry getAssetObject() {
		return _entry;
	}

	@Override
	public String getClassName() {
		return BookmarksEntry.class.getName();
	}

	@Override
	public long getClassPK() {
		return _entry.getEntryId();
	}

	@Override
	public long getGroupId() {
		return _entry.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_FULL_CONTENT)) {
			return "/bookmarks/asset/" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<BookmarksEntry> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public int getStatus() {
		return _entry.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _entry.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		return _entry.getName();
	}

	@Override
	public String getType() {
		return BookmarksEntryAssetRendererFactory.TYPE;
	}

	@Override
	public PortletURL getURLEdit(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		Group group = GroupLocalServiceUtil.fetchGroup(_entry.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group,
				BookmarksPortletKeys.BOOKMARKS_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/bookmarks/edit_entry"
		).setParameter(
			"entryId", _entry.getEntryId()
		).setParameter(
			"folderId", _entry.getFolderId()
		).setParameter(
			"showFolderSelector", true
		).buildPortletURL();
	}

	@Override
	public String getURLView(
		LiferayPortletResponse liferayPortletResponse,
		WindowState windowState) {

		return PortalUtil.getPathMain() + "/bookmarks/open_entry?entryId=" +
			_entry.getEntryId();
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		return getURLViewInContext(
			liferayPortletRequest, noSuchEntryRedirect, "/bookmarks/find_entry",
			"entryId", _entry.getEntryId());
	}

	@Override
	public String getURLViewInContext(
		ThemeDisplay themeDisplay, String noSuchEntryRedirect) {

		return getURLViewInContext(
			themeDisplay, noSuchEntryRedirect, "/bookmarks/find_entry",
			"entryId", _entry.getEntryId());
	}

	@Override
	public long getUserId() {
		return _entry.getUserId();
	}

	@Override
	public String getUserName() {
		return _entry.getUserName();
	}

	@Override
	public String getUuid() {
		return _entry.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker) {
		try {
			return _bookmarksEntryModelResourcePermission.contains(
				permissionChecker, _entry, ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker) {
		try {
			return _bookmarksEntryModelResourcePermission.contains(
				permissionChecker, _entry, ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			BookmarksWebKeys.BOOKMARKS_ENTRY, _entry);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isPrintable() {
		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BookmarksEntryAssetRenderer.class);

	private final ModelResourcePermission<BookmarksEntry>
		_bookmarksEntryModelResourcePermission;
	private final BookmarksEntry _entry;

}