/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model.impl;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Leo
 */
public class ObjectEntryFolderImpl extends ObjectEntryFolderBaseImpl {

	@Override
	public List<Long> getAncestorObjectEntryFolderIds() throws PortalException {
		List<Long> ancestorObjectEntryFolderIds = new ArrayList<>();

		ObjectEntryFolder objectEntryFolder = this;

		while (!objectEntryFolder.isRoot()) {
			objectEntryFolder =
				ObjectEntryFolderLocalServiceUtil.getObjectEntryFolder(
					objectEntryFolder.getParentObjectEntryFolderId());

			ancestorObjectEntryFolderIds.add(
				objectEntryFolder.getObjectEntryFolderId());
		}

		return ancestorObjectEntryFolderIds;
	}

	@Override
	public boolean isRoot() {
		if (getParentObjectEntryFolderId() ==
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT) {

			return true;
		}

		return false;
	}

}