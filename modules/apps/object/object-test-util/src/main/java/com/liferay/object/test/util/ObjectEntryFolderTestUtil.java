/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.test.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntryFolderTestUtil {

	public static ObjectEntryFolder addObjectEntryFolder() throws Exception {
		return addObjectEntryFolder(
			TestPropsValues.getGroupId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
	}

	public static ObjectEntryFolder addObjectEntryFolder(long groupId)
		throws Exception {

		return addObjectEntryFolder(
			groupId,
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT);
	}

	public static ObjectEntryFolder addObjectEntryFolder(
			long groupId, long parentObjectEntryFolderId)
		throws Exception {

		return addObjectEntryFolder(
			groupId, TestPropsValues.getUserId(), parentObjectEntryFolderId);
	}

	public static ObjectEntryFolder addObjectEntryFolder(
			long groupId, long userId, long parentObjectEntryFolderId)
		throws Exception {

		return ObjectEntryFolderLocalServiceUtil.addObjectEntryFolder(
			null, groupId, userId, parentObjectEntryFolderId,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(StringUtil.randomString()),
			StringUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

}