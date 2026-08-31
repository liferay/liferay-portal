/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.test.util;

import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.randomizerbumpers.TikaRandomizerBumper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author Adolfo Pérez
 */
public class DLTestUtil {

	public static DLFileEntry addDLFileEntry(long dlFolderId)
		throws PortalException {

		DLFolder dlFolder = DLFolderLocalServiceUtil.fetchDLFolder(dlFolderId);

		byte[] bytes = TestDataConstants.TEST_BYTE_ARRAY;

		return DLFileEntryLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), dlFolder.getGroupId(),
			dlFolder.getRepositoryId(), dlFolder.getFolderId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(bytes), bytes.length, null, null,
			null,
			ServiceContextTestUtil.getServiceContext(dlFolder.getGroupId()));
	}

	public static DLFolder addDLFolder(long groupId) throws PortalException {
		return addDLFolder(
			groupId, ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static DLFolder addDLFolder(
			long groupId, boolean deleteExisting, ServiceContext serviceContext)
		throws PortalException {

		return addDLFolder(
			groupId, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, deleteExisting,
			serviceContext);
	}

	public static DLFolder addDLFolder(
			long groupId, long parentFolderId, boolean deleteExisting,
			ServiceContext serviceContext)
		throws PortalException {

		String name = RandomTestUtil.randomString();

		if (deleteExisting) {
			try {
				DLFolder folder = DLFolderLocalServiceUtil.getFolder(
					groupId, parentFolderId, name);

				DLFolderLocalServiceUtil.deleteFolder(folder.getFolderId());
			}
			catch (NoSuchFolderException noSuchFolderException) {
				if (_log.isDebugEnabled()) {
					_log.debug(noSuchFolderException);
				}
			}
		}

		return DLFolderLocalServiceUtil.addFolder(
			null, TestPropsValues.getUserId(), groupId, groupId, false,
			parentFolderId, name, StringPool.BLANK, false, serviceContext);
	}

	public static DLFolder addDLFolder(
			long groupId, ServiceContext serviceContext)
		throws PortalException {

		return addDLFolder(groupId, true, serviceContext);
	}

	public static byte[] getImageBytes(String suffix) throws IOException {
		BufferedImage bufferedImage = new BufferedImage(
			1, 1, BufferedImage.TYPE_INT_RGB);

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		ImageIO.write(bufferedImage, suffix, byteArrayOutputStream);

		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] randomTextFileBytes() {
		return randomTextFileBytes(8);
	}

	public static byte[] randomTextFileBytes(int length) {
		String string = RandomTestUtil.randomString(
			length, TikaRandomizerBumper.INSTANCE);

		return string.getBytes();
	}

	private static final Log _log = LogFactoryUtil.getLog(DLTestUtil.class);

}