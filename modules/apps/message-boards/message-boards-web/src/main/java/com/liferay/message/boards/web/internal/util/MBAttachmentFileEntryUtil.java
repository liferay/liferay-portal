/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.util;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ambrín Chaudhary
 */
public class MBAttachmentFileEntryUtil {

	public static List<MBAttachmentFileEntryReference>
			addMBAttachmentFileEntries(
				long groupId, long userId, long messageId, long folderId,
				List<FileEntry> tempFileEntries,
				UnsafeFunction<String, String, PortalException>
					uniqueFileNameUnsafeFunction)
		throws PortalException {

		return TransformUtil.transform(
			tempFileEntries,
			tempFileEntry -> {
				FileEntry mbFileEntry =
					PortletFileRepositoryUtil.addPortletFileEntry(
						null, groupId, userId, MBMessage.class.getName(),
						messageId, MBConstants.SERVICE_NAME, folderId,
						tempFileEntry.getContentStream(),
						uniqueFileNameUnsafeFunction.apply(
							tempFileEntry.getTitle()),
						tempFileEntry.getMimeType(), true);

				return new MBAttachmentFileEntryReference(
					tempFileEntry.getFileEntryId(), mbFileEntry);
			});
	}

	public static List<FileEntry> getTempMBAttachmentFileEntries(
			String content, long groupId, long userId)
		throws PortalException {

		List<FileEntry> tempMBAttachmentFileEntries = new ArrayList<>();

		Pattern pattern = Pattern.compile(
			EditorConstants.ATTRIBUTE_DATA_IMAGE_ID + "=.(\\d+)");

		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			long fileEntryId = GetterUtil.getLong(matcher.group(1));

			FileEntry tempFileEntry =
				PortletFileRepositoryUtil.getPortletFileEntry(fileEntryId);

			if (!_isTempMBAttachmentFileEntry(tempFileEntry, groupId, userId)) {
				continue;
			}

			tempMBAttachmentFileEntries.add(tempFileEntry);
		}

		return tempMBAttachmentFileEntries;
	}

	private static boolean _isTempMBAttachmentFileEntry(
			FileEntry fileEntry, long groupId, long userId)
		throws PortalException {

		try {
			FileEntry tempFileEntry = TempFileEntryUtil.getTempFileEntry(
				groupId, userId, MBMessageConstants.TEMP_FOLDER_NAME,
				fileEntry.getTitle());

			if (tempFileEntry.getFileEntryId() == fileEntry.getFileEntryId()) {
				return true;
			}

			return false;
		}
		catch (NoSuchFileEntryException | NoSuchFolderException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBAttachmentFileEntryUtil.class);

}