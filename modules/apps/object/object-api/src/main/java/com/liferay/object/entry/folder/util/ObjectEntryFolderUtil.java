/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.entry.folder.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryFolderUtil {

	public static long getObjectEntryFolderId(
		long currentObjectEntryFolderId, long originalObjectEntryFolderId) {

		if (originalObjectEntryFolderId ==
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			return originalObjectEntryFolderId;
		}

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderLocalServiceUtil.fetchObjectEntryFolder(
				originalObjectEntryFolderId);

		while ((objectEntryFolder != null) &&
			   (objectEntryFolder.getStatus() ==
				   WorkflowConstants.STATUS_IN_TRASH)) {

			objectEntryFolder =
				ObjectEntryFolderLocalServiceUtil.fetchObjectEntryFolder(
					objectEntryFolder.getParentObjectEntryFolderId());
		}

		if (objectEntryFolder == null) {
			return getRootObjectEntryFolderId(currentObjectEntryFolderId);
		}

		return objectEntryFolder.getObjectEntryFolderId();
	}

	public static long getRootObjectEntryFolderId(long objectEntryFolderId) {
		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderLocalServiceUtil.fetchObjectEntryFolder(
				objectEntryFolderId);

		if (objectEntryFolder == null) {
			return ObjectEntryFolderConstants.
				PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT;
		}

		List<String> parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		ObjectEntryFolder rootObjectEntryFolder =
			ObjectEntryFolderLocalServiceUtil.fetchObjectEntryFolder(
				GetterUtil.getLong(parts.get(0)));

		if ((rootObjectEntryFolder != null) &&
			(Objects.equals(
				rootObjectEntryFolder.getExternalReferenceCode(),
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS) ||
			 Objects.equals(
				 rootObjectEntryFolder.getExternalReferenceCode(),
				 ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES))) {

			return rootObjectEntryFolder.getObjectEntryFolderId();
		}

		return ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT;
	}

}