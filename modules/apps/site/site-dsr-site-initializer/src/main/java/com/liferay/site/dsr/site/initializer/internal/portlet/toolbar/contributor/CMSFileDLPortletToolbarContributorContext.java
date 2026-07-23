/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.portlet.toolbar.contributor;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.portlet.toolbar.contributor.DLPortletToolbarContributorContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.JavaScriptMenuItem;
import com.liferay.portal.kernel.servlet.taglib.ui.MenuItem;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = DLPortletToolbarContributorContext.class)
public class CMSFileDLPortletToolbarContributorContext
	implements DLPortletToolbarContributorContext {

	@Override
	public void updatePortletTitleMenuItems(
		List<MenuItem> menuItems, Folder folder, ThemeDisplay themeDisplay,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", themeDisplay.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		Group group = themeDisplay.getScopeGroup();

		if (!Objects.equals(
				group.getClassName(), objectDefinition.getClassName())) {

			return;
		}

		try {
			long folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

			if (folder != null) {
				folderId = folder.getFolderId();
			}

			if (!ModelResourcePermissionUtil.contains(
					_modelResourcePermission,
					themeDisplay.getPermissionChecker(),
					themeDisplay.getScopeGroupId(), folderId,
					ActionKeys.ADD_DOCUMENT)) {

				return;
			}

			JavaScriptMenuItem javaScriptMenuItem = new JavaScriptMenuItem();

			long repositoryId = themeDisplay.getScopeGroupId();

			if (folder != null) {
				repositoryId = folder.getRepositoryId();
			}

			javaScriptMenuItem.setData(
				HashMapBuilder.<String, Object>put(
					"action", "openCMSFileSelector"
				).put(
					"folderId", folderId
				).put(
					"groupId", themeDisplay.getScopeGroupId()
				).put(
					"redirect",
					_portal.getCurrentURL(
						_portal.getHttpServletRequest(portletRequest))
				).put(
					"repositoryId", repositoryId
				).build());

			javaScriptMenuItem.setIcon("catalog");
			javaScriptMenuItem.setKey("#cms-files");
			javaScriptMenuItem.setLabel(
				_language.get(themeDisplay.getLocale(), "cms-files"));

			menuItems.add(javaScriptMenuItem);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSFileDLPortletToolbarContributorContext.class);

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.Folder)"
	)
	private ModelResourcePermission<Folder> _modelResourcePermission;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private Portal _portal;

}