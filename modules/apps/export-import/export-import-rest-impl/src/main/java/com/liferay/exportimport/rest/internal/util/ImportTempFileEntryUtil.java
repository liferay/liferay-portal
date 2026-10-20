/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.kernel.background.task.BackgroundTaskExecutorNames;
import com.liferay.exportimport.rest.resource.v1_0.ImportPreviewResource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.background.task.service.BackgroundTaskLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;

import jakarta.ws.rs.NotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Collections;
import java.util.List;

/**
 * @author Jaime León
 */
public class ImportTempFileEntryUtil {

	public static FileEntry addTempFileEntry(
			File file, String fileName, long groupId, String mimeType)
		throws Exception {

		try (InputStream inputStream = new FileInputStream(file)) {
			return LayoutServiceUtil.addTempFileEntry(
				groupId, _FOLDER_NAME,
				TempFileEntryUtil.getTempFileName(fileName), inputStream,
				mimeType);
		}
	}

	public static void deleteUnreservedTempFileEntries(
			long groupId, long userId)
		throws PortalException {

		for (FileEntry fileEntry :
				_getUnreservedTempFileEntries(groupId, userId)) {

			LayoutServiceUtil.deleteTempFileEntry(
				groupId, _FOLDER_NAME, fileEntry.getFileName());
		}
	}

	public static String getFileName(FileEntry fileEntry) {
		return TempFileEntryUtil.getOriginalTempFileName(
			fileEntry.getFileName());
	}

	public static FileEntry getTempFileEntry(long groupId, long userId)
		throws PortalException {

		List<FileEntry> fileEntries = _getUnreservedTempFileEntries(
			groupId, userId);

		if (ListUtil.isEmpty(fileEntries)) {
			throw new NotFoundException();
		}

		return fileEntries.get(0);
	}

	private static List<FileEntry> _getUnreservedTempFileEntries(
			long groupId, long userId)
		throws PortalException {

		String[] tempFileNames = LayoutServiceUtil.getTempFileNames(
			groupId, _FOLDER_NAME);

		if (ArrayUtil.isEmpty(tempFileNames)) {
			return Collections.emptyList();
		}

		long[] reservedFileEntryIds = TransformUtil.transformToLongArray(
			BackgroundTaskLocalServiceUtil.getBackgroundTasks(
				new long[] {groupId},
				new String[] {
					BackgroundTaskExecutorNames.
						LAYOUT_IMPORT_BACKGROUND_TASK_EXECUTOR,
					BackgroundTaskExecutorNames.
						PORTLET_IMPORT_BACKGROUND_TASK_EXECUTOR
				},
				false),
			backgroundTask -> MapUtil.getLong(
				backgroundTask.getTaskContextMap(), "tempFileEntryId"));

		return TransformUtil.unsafeTransformToList(
			tempFileNames,
			tempFileName -> {
				FileEntry fileEntry = TempFileEntryUtil.getTempFileEntry(
					groupId, userId,
					DigesterUtil.digestHex(DigesterUtil.SHA_256, _FOLDER_NAME),
					tempFileName);

				if (ArrayUtil.contains(
						reservedFileEntryIds, fileEntry.getFileEntryId())) {

					return null;
				}

				return fileEntry;
			});
	}

	private static final String _FOLDER_NAME =
		ImportPreviewResource.class.getName();

}