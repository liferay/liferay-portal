/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.action.provider;

import com.liferay.content.dashboard.document.library.internal.item.action.EditFileEntryContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.info.display.url.provider.InfoEditURLProviderRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = ContentDashboardItemActionProvider.class)
public class EditFileEntryContentDashboardItemActionProvider
	implements ContentDashboardItemActionProvider<FileEntry> {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
		FileEntry fileEntry, HttpServletRequest httpServletRequest) {

		if (!isShow(fileEntry, httpServletRequest)) {
			return null;
		}

		return new EditFileEntryContentDashboardItemAction(
			fileEntry, httpServletRequest,
			_infoEditURLProviderRegistry.getInfoEditURLProvider(
				FileEntry.class.getName()),
			_language, _portal, _portletLocalService);
	}

	@Override
	public String getKey() {
		return "edit";
	}

	@Override
	public ContentDashboardItemAction.Type getType() {
		return ContentDashboardItemAction.Type.EDIT;
	}

	@Override
	public boolean isShow(
		FileEntry fileEntry, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			return _modelResourcePermission.contains(
				themeDisplay.getPermissionChecker(), fileEntry,
				ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditFileEntryContentDashboardItemActionProvider.class);

	@Reference
	private InfoEditURLProviderRegistry _infoEditURLProviderRegistry;

	@Reference
	private Language _language;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.repository.model.FileEntry)"
	)
	private ModelResourcePermission<FileEntry> _modelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

}