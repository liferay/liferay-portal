/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.upgrade.v1_2_0;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.upgrade.BasePortletPreferencesUpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Marco Galluzzi
 */
public class UpgradePortletPreferences
	extends BasePortletPreferencesUpgradeProcess {

	public UpgradePortletPreferences(
		DLAppLocalService dlAppLocalService,
		GroupLocalService groupLocalService,
		RepositoryLocalService repositoryLocalService) {

		_dlAppLocalService = dlAppLocalService;
		_groupLocalService = groupLocalService;
		_repositoryLocalService = repositoryLocalService;
	}

	@Override
	protected String[] getPortletIds() {
		return new String[] {
			DLPortletKeys.MEDIA_GALLERY_DISPLAY + "_INSTANCE_%"
		};
	}

	@Override
	protected String upgradePreferences(
			long companyId, long ownerId, int ownerType, long plid,
			String portletId, String xml)
		throws Exception {

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				companyId, ownerId, ownerType, plid, portletId, xml);

		long rootFolderId = GetterUtil.getLong(
			portletPreferences.getValue("rootFolderId", "-1"));

		if (rootFolderId == -1) {
			return _toXML(portletPreferences);
		}

		String rootFolderExternalReferenceCode = StringPool.BLANK;

		if (rootFolderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			try {
				Folder folder = _dlAppLocalService.getFolder(rootFolderId);

				rootFolderExternalReferenceCode =
					folder.getExternalReferenceCode();
			}
			catch (PortalException portalException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Unable to get root folder ID ", rootFolderId, ": ",
							portalException));
				}

				return _toXML(portletPreferences);
			}
		}

		long selectedRepositoryId = GetterUtil.getLong(
			portletPreferences.getValue("selectedRepositoryId", "-1"));

		if (selectedRepositoryId == -1) {
			return _toXML(portletPreferences);
		}

		Repository selectedRepository = _repositoryLocalService.fetchRepository(
			selectedRepositoryId);

		Group selectedGroup = null;
		String selectedRepositoryExternalReferenceCode = StringPool.BLANK;

		if (selectedRepository != null) {
			selectedGroup = _groupLocalService.getGroup(
				selectedRepository.getGroupId());

			selectedRepositoryExternalReferenceCode =
				selectedRepository.getExternalReferenceCode();
		}
		else {
			selectedGroup = _groupLocalService.fetchGroup(selectedRepositoryId);

			if (selectedGroup == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to get selected repository ID " +
							selectedRepositoryId);
				}

				return _toXML(portletPreferences);
			}
		}

		portletPreferences.setValue(
			"rootFolderExternalReferenceCode", rootFolderExternalReferenceCode);
		portletPreferences.setValue(
			"selectedGroupExternalReferenceCode",
			selectedGroup.getExternalReferenceCode());
		portletPreferences.setValue(
			"selectedRepositoryExternalReferenceCode",
			selectedRepositoryExternalReferenceCode);

		return _toXML(portletPreferences);
	}

	private String _toXML(PortletPreferences portletPreferences)
		throws Exception {

		portletPreferences.reset("rootFolderId");
		portletPreferences.reset("selectedRepositoryId");

		return PortletPreferencesFactoryUtil.toXML(portletPreferences);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePortletPreferences.class);

	private final DLAppLocalService _dlAppLocalService;
	private final GroupLocalService _groupLocalService;
	private final RepositoryLocalService _repositoryLocalService;

}