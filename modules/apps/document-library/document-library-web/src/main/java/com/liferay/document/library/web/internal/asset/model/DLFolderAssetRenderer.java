/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.web.internal.security.permission.resource.DLFolderPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Alexander Chow
 */
public class DLFolderAssetRenderer
	extends BaseJSPAssetRenderer<Folder> implements TrashRenderer {

	public static final String TYPE = "folder";

	public DLFolderAssetRenderer(Folder folder, TrashHelper trashHelper) {
		_folder = folder;
		_trashHelper = trashHelper;
	}

	@Override
	public Folder getAssetObject() {
		return _folder;
	}

	@Override
	public String getClassName() {
		return DLFolder.class.getName();
	}

	@Override
	public long getClassPK() {
		return _folder.getPrimaryKey();
	}

	@Override
	public long getGroupId() {
		return _folder.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (template.equals(TEMPLATE_ABSTRACT) ||
			template.equals(TEMPLATE_FULL_CONTENT)) {

			return "/document_library/asset/folder_" + template + ".jsp";
		}

		return null;
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<Folder> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _folder.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		return _trashHelper.getOriginalTitle(_folder.getName());
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLEdit(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		Group group = GroupLocalServiceUtil.fetchGroup(_folder.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group,
				DLPortletKeys.DOCUMENT_LIBRARY_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_folder"
		).setParameter(
			"folderId", _folder.getFolderId()
		).buildPortletURL();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<Folder> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCRenderCommandName(
			"/document_library/view_folder"
		).setParameter(
			"folderId", _folder.getFolderId()
		).setWindowState(
			windowState
		).buildString();
	}

	@Override
	public String getURLViewInContext(
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		String noSuchEntryRedirect) {

		return getURLViewInContext(
			liferayPortletRequest, noSuchEntryRedirect,
			"/document_library/find_folder", "folderId", _folder.getFolderId());
	}

	@Override
	public String getURLViewInContext(
		ThemeDisplay themeDisplay, String noSuchEntryRedirect) {

		return getURLViewInContext(
			themeDisplay, noSuchEntryRedirect, "/document_library/find_folder",
			"folderId", _folder.getFolderId());
	}

	@Override
	public long getUserId() {
		return _folder.getUserId();
	}

	@Override
	public String getUserName() {
		return _folder.getUserName();
	}

	@Override
	public String getUuid() {
		return _folder.getUuid();
	}

	@Override
	public boolean hasEditPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return DLFolderPermission.contains(
			permissionChecker, _folder, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return DLFolderPermission.contains(
			permissionChecker, _folder, ActionKeys.VIEW);
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			WebKeys.DOCUMENT_LIBRARY_FOLDER, _folder);

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	@Override
	public boolean isDisplayable() {
		return !_folder.isMountPoint();
	}

	private final Folder _folder;
	private final TrashHelper _trashHelper;

}