/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.uad.test.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.List;

/**
 * @author William Newbury
 */
public class DLFileEntryUADTestUtil {

	public static DLFileEntry addDLFileEntry(
			DLAppLocalService dlAppLocalService,
			DLFileEntryLocalService dlFileEntryLocalService,
			DLFolderLocalService dlFolderLocalService, long userId,
			long groupId)
		throws Exception {

		DLFolder dlFolder = dlFolderLocalService.addFolder(
			null, userId, groupId, groupId, false, 0L,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			ServiceContextTestUtil.getServiceContext());

		return addDLFileEntry(
			dlAppLocalService, dlFileEntryLocalService, dlFolder.getFolderId(),
			userId, groupId);
	}

	public static DLFileEntry addDLFileEntry(
			DLAppLocalService dlAppLocalService,
			DLFileEntryLocalService dlFileEntryLocalService, long dlFolderId,
			long userId, long groupId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		InputStream inputStream = new ByteArrayInputStream(bytes);

		FileEntry fileEntry = dlAppLocalService.addFileEntry(
			null, userId, groupId, dlFolderId, RandomTestUtil.randomString(),
			ContentTypes.TEXT_PLAIN, RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, inputStream,
			bytes.length, null, null, null, serviceContext);

		return dlFileEntryLocalService.getFileEntry(fileEntry.getFileEntryId());
	}

	public static void cleanUpDependencies(
			DLAppLocalService dlAppLocalService,
			DLFileEntryLocalService dlFileEntryLocalService,
			DLFolderLocalService dlFolderLocalService,
			List<DLFileEntry> dlFileEntries)
		throws Exception {

		for (DLFileEntry dlFileEntry : dlFileEntries) {
			DLFileEntry existingDLFileEntry =
				dlFileEntryLocalService.fetchDLFileEntry(
					dlFileEntry.getFileEntryId());

			if (existingDLFileEntry != null) {
				dlAppLocalService.deleteFileEntry(dlFileEntry.getFileEntryId());
			}

			long dlFolderId = dlFileEntry.getFolderId();

			if (dlFolderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
				continue;
			}

			dlFolderLocalService.deleteFolder(dlFileEntry.getFolderId());
		}
	}

}