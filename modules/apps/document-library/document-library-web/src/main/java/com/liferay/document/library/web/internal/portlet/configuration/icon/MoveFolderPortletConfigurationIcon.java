/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.configuration.icon;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.web.internal.portlet.action.ActionUtil;
import com.liferay.document.library.web.internal.util.DLFolderUtil;
import com.liferay.document.library.web.internal.util.DLPortletConfigurationIconUtil;
import com.liferay.document.library.web.internal.util.FolderItemSelectorURLProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

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
		"path=/document_library/view_folder"
	},
	service = PortletConfigurationIcon.class
)
public class MoveFolderPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getIconCssClass() {
		return "move-folder";
	}

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "move");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		try {
			LiferayPortletResponse liferayPortletResponse =
				_portal.getLiferayPortletResponse(portletResponse);
			Folder folder = ActionUtil.getFolder(portletRequest);
			FolderItemSelectorURLProvider folderItemSelectorURLProvider =
				new FolderItemSelectorURLProvider(
					_portal.getHttpServletRequest(portletRequest),
					(ItemSelector)portletRequest.getAttribute(
						ItemSelector.class.getName()));

			return StringBundler.concat(
				"javascript: ", liferayPortletResponse.getNamespace(),
				"move(1, 'rowIdsFolder', ", folder.getFolderId(), ", '",
				HtmlUtil.escapeJS(
					folderItemSelectorURLProvider.getSelectMoveToFolderURL(
						folder.getRepositoryId(), folder.getParentFolderId(),
						folder.getFolderId())),
				"');");
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public double getWeight() {
		return 140;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		return DLPortletConfigurationIconUtil.runWithDefaultValueOnError(
			false,
			() -> {
				Folder folder = ActionUtil.getFolder(portletRequest);

				if (DLFolderUtil.isRepositoryRoot(folder)) {
					return false;
				}

				ThemeDisplay themeDisplay =
					(ThemeDisplay)portletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return ModelResourcePermissionUtil.contains(
					_folderModelResourcePermission,
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroupId(), folder.getFolderId(),
					ActionKeys.UPDATE);
			});
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _folderModelResourcePermission;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}