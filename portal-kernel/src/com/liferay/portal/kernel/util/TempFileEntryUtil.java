/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.LocalRepository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesCapability;
import com.liferay.portal.kernel.repository.capabilities.TemporaryFileEntriesScope;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Sergio González
 * @author Matthew Kong
 * @author Alexander Chow
 * @author Iván Zaera
 */
public class TempFileEntryUtil {

	public static final String TEMP_RANDOM_SUFFIX = "--tempRandomSuffix--";

	public static FileEntry addTempFileEntry(
			long groupId, long userId, String folderName, String fileName,
			File file, String mimeType)
		throws PortalException {

		try (InputStream inputStream = new FileInputStream(file)) {
			return addTempFileEntry(
				groupId, userId, folderName, fileName, inputStream, mimeType);
		}
		catch (IOException ioException) {
			throw new PortalException(ioException);
		}
	}

	public static FileEntry addTempFileEntry(
			long groupId, long userId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException {

		boolean dlAppHelperEnabled = DLAppHelperThreadLocal.isEnabled();

		try {
			DLAppHelperThreadLocal.setEnabled(false);

			TemporaryFileEntriesCapability temporaryFileEntriesCapability =
				_getTemporaryFileEntriesCapability(groupId);

			return temporaryFileEntriesCapability.addTemporaryFileEntry(
				new TemporaryFileEntriesScope(_UUID, userId, folderName),
				fileName, mimeType, inputStream);
		}
		finally {
			DLAppHelperThreadLocal.setEnabled(dlAppHelperEnabled);
		}
	}

	public static FileEntry addTempFileEntry(
			String externalReferenceCode, long groupId, long userId,
			String folderName, String fileName, InputStream inputStream,
			String mimeType)
		throws PortalException {

		boolean dlAppHelperEnabled = DLAppHelperThreadLocal.isEnabled();

		try {
			DLAppHelperThreadLocal.setEnabled(false);

			TemporaryFileEntriesCapability temporaryFileEntriesCapability =
				_getTemporaryFileEntriesCapability(groupId);

			return temporaryFileEntriesCapability.addTemporaryFileEntry(
				new TemporaryFileEntriesScope(_UUID, userId, folderName),
				externalReferenceCode, fileName, mimeType, inputStream);
		}
		finally {
			DLAppHelperThreadLocal.setEnabled(dlAppHelperEnabled);
		}
	}

	public static void deleteTempFileEntry(long fileEntryId)
		throws PortalException {

		LocalRepository localRepository =
			RepositoryProviderUtil.getFileEntryLocalRepository(fileEntryId);

		FileEntry fileEntry = localRepository.getFileEntry(fileEntryId);

		Folder folder = fileEntry.getFolder();

		deleteTempFileEntry(
			fileEntry.getGroupId(), fileEntry.getUserId(), folder.getName(),
			fileEntry.getTitle());
	}

	public static void deleteTempFileEntry(
			long groupId, long userId, String folderName, String fileName)
		throws PortalException {

		boolean dlAppHelperEnabled = DLAppHelperThreadLocal.isEnabled();

		try {
			DLAppHelperThreadLocal.setEnabled(false);

			TemporaryFileEntriesCapability temporaryFileEntriesCapability =
				_getTemporaryFileEntriesCapability(groupId);

			temporaryFileEntriesCapability.deleteTemporaryFileEntry(
				new TemporaryFileEntriesScope(_UUID, userId, folderName),
				fileName);
		}
		finally {
			DLAppHelperThreadLocal.setEnabled(dlAppHelperEnabled);
		}
	}

	public static String getOriginalTempFileName(String tempFileName) {
		String extension = FileUtil.getExtension(tempFileName);

		int pos = tempFileName.lastIndexOf(TEMP_RANDOM_SUFFIX);

		if (pos != -1) {
			tempFileName = tempFileName.substring(0, pos);

			if (Validator.isNotNull(extension)) {
				tempFileName = tempFileName + StringPool.PERIOD + extension;
			}
		}

		return tempFileName;
	}

	public static FileEntry getTempFileEntry(
			long groupId, long userId, String folderName, String fileName)
		throws PortalException {

		TemporaryFileEntriesCapability temporaryFileEntriesCapability =
			_getTemporaryFileEntriesCapability(groupId);

		return temporaryFileEntriesCapability.getTemporaryFileEntry(
			new TemporaryFileEntriesScope(_UUID, userId, folderName), fileName);
	}

	public static String getTempFileName(String originalFileName) {
		StringBundler sb = new StringBundler(5);

		sb.append(FileUtil.stripExtension(originalFileName));
		sb.append(TEMP_RANDOM_SUFFIX);
		sb.append(StringUtil.randomString());

		String extension = FileUtil.getExtension(originalFileName);

		if (Validator.isNotNull(extension)) {
			sb.append(StringPool.PERIOD);
			sb.append(extension);
		}

		return sb.toString();
	}

	public static String[] getTempFileNames(
			long groupId, long userId, String folderName)
		throws PortalException {

		Repository repository = RepositoryLocalServiceUtil.fetchRepository(
			groupId, TempFileEntryUtil.class.getName(),
			TempFileEntryUtil.class.getName());

		if (repository == null) {
			return StringPool.EMPTY_ARRAY;
		}

		LocalRepository localRepository =
			RepositoryProviderUtil.getLocalRepository(
				repository.getRepositoryId());

		TemporaryFileEntriesCapability temporaryFileEntriesCapability =
			localRepository.getCapability(TemporaryFileEntriesCapability.class);

		List<FileEntry> fileEntries =
			temporaryFileEntriesCapability.getTemporaryFileEntries(
				new TemporaryFileEntriesScope(_UUID, userId, folderName));

		List<String> fileNames = new ArrayList<>();

		for (FileEntry fileEntry : fileEntries) {
			fileNames.add(fileEntry.getFileName());
		}

		return ArrayUtil.toStringArray(fileNames);
	}

	private static LocalRepository _addPortletRepository(
			long groupId, ServiceContext serviceContext)
		throws PortalException {

		Repository repository = RepositoryLocalServiceUtil.fetchRepository(
			groupId, TempFileEntryUtil.class.getName(),
			TempFileEntryUtil.class.getName());

		if (repository != null) {
			return RepositoryProviderUtil.getLocalRepository(
				repository.getRepositoryId());
		}

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		User user = UserLocalServiceUtil.getGuestUser(group.getCompanyId());

		long classNameId = PortalUtil.getClassNameId(
			"com.liferay.portal.repository.temporaryrepository." +
				"TemporaryFileEntryRepository");

		UnicodeProperties typeSettingsUnicodeProperties =
			new UnicodeProperties();

		boolean dlAppHelperEnabled = DLAppHelperThreadLocal.isEnabled();

		try {
			DLAppHelperThreadLocal.setEnabled(false);

			repository = RepositoryLocalServiceUtil.addRepository(
				null, user.getUserId(), groupId, classNameId,
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				TempFileEntryUtil.class.getName(), StringPool.BLANK,
				TempFileEntryUtil.class.getName(),
				typeSettingsUnicodeProperties, true, serviceContext);

			return RepositoryProviderUtil.getLocalRepository(
				repository.getRepositoryId());
		}
		finally {
			DLAppHelperThreadLocal.setEnabled(dlAppHelperEnabled);
		}
	}

	private static TemporaryFileEntriesCapability
			_getTemporaryFileEntriesCapability(long groupId)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		LocalRepository localRepository = _addPortletRepository(
			groupId, serviceContext);

		return localRepository.getCapability(
			TemporaryFileEntriesCapability.class);
	}

	private static final UUID _UUID = UUID.fromString(
		"00EF1134-B3EE-432A-BABD-367CEFA44DE1");

}