/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.asset.model;

import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.web.internal.security.permission.resource.JournalFolderPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
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

import java.util.Locale;

/**
 * @author Alexander Chow
 */
public class JournalFolderAssetRenderer
	extends BaseJSPAssetRenderer<JournalFolder> implements TrashRenderer {

	public static final String TYPE = "folder";

	public JournalFolderAssetRenderer(
		JournalFolder folder, TrashHelper trashHelper) {

		_folder = folder;
		_trashHelper = trashHelper;
	}

	@Override
	public JournalFolder getAssetObject() {
		return _folder;
	}

	@Override
	public String getClassName() {
		return JournalFolder.class.getName();
	}

	@Override
	public long getClassPK() {
		return _folder.getFolderId();
	}

	@Override
	public long getGroupId() {
		return _folder.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		if (!template.equals(TEMPLATE_FULL_CONTENT)) {
			return null;
		}

		httpServletRequest.setAttribute(WebKeys.JOURNAL_FOLDER, _folder);

		return "/asset/folder_" + template + ".jsp";
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<JournalFolder> assetRendererFactory =
			getAssetRendererFactory();

		return assetRendererFactory.getPortletId();
	}

	@Override
	public int getStatus() {
		return _folder.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return _folder.getDescription();
	}

	@Override
	public String getTitle(Locale locale) {
		if (_trashHelper == null) {
			return _folder.getName();
		}

		return _trashHelper.getOriginalTitle(_folder.getName());
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public PortletURL getURLEdit(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		Group group = GroupLocalServiceUtil.fetchGroup(_folder.getGroupId());

		if (group.isCompany()) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			group = themeDisplay.getScopeGroup();
		}

		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				liferayPortletRequest, group, JournalPortletKeys.JOURNAL, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_folder.jsp"
		).setParameter(
			"folderId", _folder.getFolderId()
		).buildPortletURL();
	}

	@Override
	public String getURLView(
			LiferayPortletResponse liferayPortletResponse,
			WindowState windowState)
		throws Exception {

		AssetRendererFactory<JournalFolder> assetRendererFactory =
			getAssetRendererFactory();

		return PortletURLBuilder.create(
			assetRendererFactory.getURLView(liferayPortletResponse, windowState)
		).setMVCPath(
			"/asset/folder_full_content.jsp"
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

		try {
			return getURLView(liferayPortletResponse, WindowState.MAXIMIZED);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return noSuchEntryRedirect;
		}
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

		return JournalFolderPermission.contains(
			permissionChecker, _folder, ActionKeys.UPDATE);
	}

	@Override
	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException {

		return JournalFolderPermission.contains(
			permissionChecker, _folder, ActionKeys.VIEW);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalFolderAssetRenderer.class);

	private final JournalFolder _folder;
	private final TrashHelper _trashHelper;

}