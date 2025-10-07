/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.entry.folder.subscription.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.subscription.service.SubscriptionLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryFolderSubscriptionUtil {

	public static boolean isSubscribedToObjectEntryFolder(
			long companyId, long groupId, long objectEntryFolderId, long userId)
		throws PortalException {

		List<Long> classPKs = new ArrayList<>();

		classPKs.add(groupId);

		if (objectEntryFolderId !=
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			ObjectEntryFolder objectEntryFolder =
				ObjectEntryFolderLocalServiceUtil.fetchObjectEntryFolder(
					objectEntryFolderId);

			if (objectEntryFolder != null) {
				classPKs.add(objectEntryFolder.getObjectEntryFolderId());
				classPKs.addAll(
					objectEntryFolder.getAncestorObjectEntryFolderIds());
			}
		}

		return SubscriptionLocalServiceUtil.isSubscribed(
			companyId, userId, ObjectEntryFolder.class.getName(),
			ArrayUtil.toLongArray(classPKs));
	}

}