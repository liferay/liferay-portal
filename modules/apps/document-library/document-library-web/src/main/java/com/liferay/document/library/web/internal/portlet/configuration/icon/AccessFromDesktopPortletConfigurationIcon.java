/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.configuration.icon;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.web.internal.portlet.action.ActionUtil;
import com.liferay.document.library.web.internal.util.DLPortletConfigurationIconUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BaseJSPPortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"path=-", "path=/document_library/view",
		"path=/document_library/view_folder"
	},
	service = PortletConfigurationIcon.class
)
public class AccessFromDesktopPortletConfigurationIcon
	extends BaseJSPPortletConfigurationIcon {

	@Override
	public Map<String, Object> getContext(PortletRequest portletRequest) {
		return HashMapBuilder.<String, Object>put(
			"action", getNamespace(portletRequest) + "accessFromDesktop"
		).put(
			"globalAction", true
		).build();
	}

	@Override
	public String getJspPath() {
		return "/document_library/access_from_desktop.jsp";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "access-from-desktop");
	}

	@Override
	public double getWeight() {
		return 190;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		return DLPortletConfigurationIconUtil.runWithDefaultValueOnError(
			false,
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

				Folder folder = ActionUtil.getFolder(portletRequest);

				if (folder != null) {
					folderId = folder.getFolderId();
				}

				if (ModelResourcePermissionUtil.contains(
						_folderModelResourcePermission,
						themeDisplay.getPermissionChecker(),
						themeDisplay.getScopeGroupId(), folderId,
						ActionKeys.VIEW) &&
					portletDisplay.isWebDAVEnabled() &&
					((folder == null) ||
					 (folder.getRepositoryId() ==
						 themeDisplay.getScopeGroupId()))) {

					return true;
				}

				return false;
			});
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.web)"
	)
	private ServletContext _servletContext;

}