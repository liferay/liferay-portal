/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.util.RepositoryUtil;

import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class DLFolderUtil {

	public static boolean isRepositoryRoot(Folder folder) {
		if (folder.isMountPoint() ||
			(folder.isRoot() &&
			 RepositoryUtil.isExternalRepository(folder.getRepositoryId()))) {

			return true;
		}

		return false;
	}

	public static void validateDepotFolder(
			long folderId, long groupId, long scopeGroupId)
		throws PortalException {

		if (groupId == scopeGroupId) {
			return;
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		if (!group.isDepot()) {
			return;
		}

		List<Long> groupConnectedDepotEntries = ListUtil.toList(
			DepotEntryLocalServiceUtil.getGroupConnectedDepotEntries(
				scopeGroupId, DepotConstants.TYPE_ANY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			DepotEntry::getGroupId);

		if (!groupConnectedDepotEntries.contains(groupId)) {
			throw new NoSuchFolderException("{folderId=" + folderId + "}");
		}
	}

}