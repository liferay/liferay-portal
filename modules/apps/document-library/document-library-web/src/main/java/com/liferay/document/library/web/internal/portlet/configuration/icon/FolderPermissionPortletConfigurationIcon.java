/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.configuration.icon;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.web.internal.portlet.action.ActionUtil;
import com.liferay.document.library.web.internal.util.DLPortletConfigurationIconUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.RepositoryUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
		"path=/document_library/edit_folder",
		"path=/document_library/view_folder"
	},
	service = PortletConfigurationIcon.class
)
public class FolderPermissionPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "password-policies";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "permissions");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Folder folder = ActionUtil.getFolder(portletRequest);

			if (folder != null) {
				return PermissionsURLTag.doTag(
					null, DLFolderConstants.getClassName(),
					HtmlUtil.unescape(folder.getName()), null,
					String.valueOf(folder.getFolderId()),
					LiferayWindowState.POP_UP.toString(), null,
					themeDisplay.getRequest());
			}

			return PermissionsURLTag.doTag(
				null, "com.liferay.document.library",
				HtmlUtil.unescape(themeDisplay.getScopeGroupName()), null,
				String.valueOf(themeDisplay.getScopeGroupId()),
				LiferayWindowState.POP_UP.toString(), null,
				themeDisplay.getRequest());
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public double getWeight() {
		return 130;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		return DLPortletConfigurationIconUtil.runWithDefaultValueOnError(
			false,
			() -> {
				Folder folder = ActionUtil.getFolder(portletRequest);

				if ((folder == null) ||
					(!folder.isMountPoint() &&
					 RepositoryUtil.isExternalRepository(
						 folder.getRepositoryId()))) {

					return false;
				}

				ThemeDisplay themeDisplay =
					(ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return ModelResourcePermissionUtil.contains(
					_folderModelResourcePermission,
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroupId(), folder.getFolderId(),
					ActionKeys.PERMISSIONS);
			});
	}

	@Override
	public boolean isUseDialog() {
		return true;
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private Language _language;

}