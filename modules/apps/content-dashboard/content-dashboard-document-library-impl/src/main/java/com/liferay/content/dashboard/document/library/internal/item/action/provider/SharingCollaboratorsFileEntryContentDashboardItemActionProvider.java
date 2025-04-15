/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.action.provider;

import com.liferay.content.dashboard.document.library.internal.item.action.SharingCollaboratorsFileEntryContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemActionProvider;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.sharing.configuration.SharingConfiguration;
import com.liferay.sharing.configuration.SharingConfigurationFactory;
import com.liferay.sharing.security.permission.SharingPermission;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemActionProvider.class)
public class SharingCollaboratorsFileEntryContentDashboardItemActionProvider
	implements ContentDashboardItemActionProvider<FileEntry> {

	@Override
	public ContentDashboardItemAction getContentDashboardItemAction(
		FileEntry fileEntry, HttpServletRequest httpServletRequest) {

		if (!isShow(fileEntry, httpServletRequest)) {
			return null;
		}

		return new SharingCollaboratorsFileEntryContentDashboardItemAction(
			fileEntry, httpServletRequest, _language, _portal);
	}

	@Override
	public String getKey() {
		return "share";
	}

	@Override
	public ContentDashboardItemAction.Type getType() {
		return ContentDashboardItemAction.Type.SHARING_COLLABORATORS;
	}

	@Override
	public boolean isShow(
		FileEntry fileEntry, HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (_isSharingEnabled(themeDisplay.getScopeGroupId()) &&
				_sharingPermission.containsSharePermission(
					themeDisplay.getPermissionChecker(),
					_classNameLocalService.getClassNameId(
						DLFileEntry.class.getName()),
					fileEntry.getFileEntryId(),
					themeDisplay.getScopeGroupId())) {

				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}

		return false;
	}

	private boolean _isSharingEnabled(long groupId) throws PortalException {
		SharingConfiguration sharingConfiguration =
			_sharingConfigurationFactory.getGroupSharingConfiguration(
				_groupLocalService.getGroup(groupId));

		return sharingConfiguration.isEnabled();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SharingCollaboratorsFileEntryContentDashboardItemActionProvider.class);

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SharingConfigurationFactory _sharingConfigurationFactory;

	@Reference
	private SharingPermission _sharingPermission;

}