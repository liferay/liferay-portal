/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;

import java.io.InputStream;

/**
 * Provides the local service utility for DLApp. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLAppLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLAppLocalService
 * @generated
 */
public class DLAppLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLAppLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds a file entry and associated metadata based on a {@link File} object.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param userId the primary key of the file entry's creator/owner
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param sourceFileName the original file's name
	 * @param mimeType the file's MIME type
	 * @param title the name to be assigned to the file (optionally
	 <code>null </code>)
	 * @param description the file's description
	 * @param changeLog the file's version change log
	 * @param file the file's data (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it
	 may include:  <ul> <li> fileEntryTypeId - ID for a custom
	 file entry type </li> <li> fieldsMap - mapping for fields
	 associated with a custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addFileEntry(String, long, long, long, String, String,
	 String, String, String, String, File, Date, Date
	 expirationDate, Date, ServiceContext)}
	 */
	@Deprecated
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				long userId, long repositoryId, long folderId,
				String sourceFileName, String mimeType, String title,
				String description, String changeLog, java.io.File file,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileEntry(
			userId, repositoryId, folderId, sourceFileName, mimeType, title,
			description, changeLog, file, serviceContext);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				String externalReferenceCode, long userId, long repositoryId,
				long folderId, String sourceFileName, String mimeType,
				byte[] bytes, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileEntry(
			externalReferenceCode, userId, repositoryId, folderId,
			sourceFileName, mimeType, bytes, displayDate, expirationDate,
			reviewDate, serviceContext);
	}

	/**
	 * Adds a file entry and associated metadata based on a byte array.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param externalReferenceCode the file entry's external reference code
	 * @param userId the primary key of the file entry's creator/owner
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param sourceFileName the original file's name
	 * @param mimeType the file's MIME type
	 * @param title the name to be assigned to the file (optionally <code>null
	 </code>)
	 * @param description the file's description
	 * @param changeLog the file's version change log
	 * @param bytes the file's data (optionally <code>null</code>)
	 * @param displayDate the file's display date (optionally
	 <code>null</code>)
	 * @param expirationDate the file's expiration date (optionally <code>null
	 </code>)
	 * @param reviewDate the file's review Date (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				String externalReferenceCode, long userId, long repositoryId,
				long folderId, String sourceFileName, String mimeType,
				String title, String urlTitle, String description,
				String changeLog, byte[] bytes, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileEntry(
			externalReferenceCode, userId, repositoryId, folderId,
			sourceFileName, mimeType, title, urlTitle, description, changeLog,
			bytes, displayDate, expirationDate, reviewDate, serviceContext);
	}

	/**
	 * Adds a file entry and associated metadata based on a {@link File} object.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param externalReferenceCode the file entry's external reference code
	 * @param userId the primary key of the file entry's creator/owner
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param sourceFileName the original file's name
	 * @param mimeType the file's MIME type
	 * @param title the name to be assigned to the file (optionally <code>null
	 </code>)
	 * @param description the file's description
	 * @param changeLog the file's version change log
	 * @param file the file's data (optionally <code>null</code>)
	 * @param displayDate the file's display date (optionally
	 <code>null</code>)
	 * @param expirationDate the file's expiration date (optionally
	 <code>null</code>)
	 * @param reviewDate the file's review Date (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				String externalReferenceCode, long userId, long repositoryId,
				long folderId, String sourceFileName, String mimeType,
				String title, String urlTitle, String description,
				String changeLog, java.io.File file, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileEntry(
			externalReferenceCode, userId, repositoryId, folderId,
			sourceFileName, mimeType, title, urlTitle, description, changeLog,
			file, displayDate, expirationDate, reviewDate, serviceContext);
	}

	/**
	 * Adds a file entry and associated metadata based on an {@link InputStream}
	 * object.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal. If it is <code>null</code>, the <code>
	 * sourceFileName</code> will be used.
	 * </p>
	 *
	 * @param externalReferenceCode the file entry's external reference code
	 * @param userId the primary key of the file entry's creator/owner
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param sourceFileName the original file's name
	 * @param mimeType the file's MIME type
	 * @param title the name to be assigned to the file (optionally <code>null
	 </code>)
	 * @param description the file's description
	 * @param changeLog the file's version change log
	 * @param inputStream the file's data (optionally <code>null</code>)
	 * @param size the file's size (optionally <code>0</code>)
	 * @param displayDate the file's display date (optionally
	 <code>null</code>)
	 * @param expirationDate the file's expiration date (optionally <code>null
	 </code>)
	 * @param reviewDate the file's review Date (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			addFileEntry(
				String externalReferenceCode, long userId, long repositoryId,
				long folderId, String sourceFileName, String mimeType,
				String title, String urlTitle, String description,
				String changeLog, InputStream inputStream, long size,
				java.util.Date displayDate, java.util.Date expirationDate,
				java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileEntry(
			externalReferenceCode, userId, repositoryId, folderId,
			sourceFileName, mimeType, title, urlTitle, description, changeLog,
			inputStream, size, displayDate, expirationDate, reviewDate,
			serviceContext);
	}

	/**
	 * Adds the file shortcut to the existing file entry. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param userId the primary key of the file shortcut's creator/owner
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file shortcut's parent folder
	 * @param toFileEntryId the primary key of the file entry to point to
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry.
	 * @return the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileShortcut
			addFileShortcut(
				String externalReferenceCode, long userId, long repositoryId,
				long folderId, long toFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileShortcut(
			externalReferenceCode, userId, repositoryId, folderId,
			toFileEntryId, serviceContext);
	}

	/**
	 * Adds a folder.
	 *
	 * @param userId the primary key of the folder's creator/owner
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param name the folder's name
	 * @param description the folder's description
	 * @param serviceContext the service context to be applied. In a Liferay
	 repository, it may include mountPoint which is a boolean
	 specifying whether the folder is a facade for mounting a
	 third-party repository
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder addFolder(
			String externalReferenceCode, long userId, long repositoryId,
			long parentFolderId, String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFolder(
			externalReferenceCode, userId, repositoryId, parentFolderId, name,
			description, serviceContext);
	}

	/**
	 * Delete all data associated to the given repository. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param repositoryId the primary key of the data's repository
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteAll(long repositoryId) throws PortalException {
		getService().deleteAll(repositoryId);
	}

	public static void deleteAllRepositories(long groupId)
		throws PortalException {

		getService().deleteAllRepositories(groupId);
	}

	/**
	 * Deletes the file entry.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileEntry(long fileEntryId)
		throws PortalException {

		getService().deleteFileEntry(fileEntryId);
	}

	public static void deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		getService().deleteFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Deletes the file shortcut. This method is only supported by the Liferay
	 * repository.
	 *
	 * @param fileShortcut the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileShortcut(
			com.liferay.portal.kernel.repository.model.FileShortcut
				fileShortcut)
		throws PortalException {

		getService().deleteFileShortcut(fileShortcut);
	}

	/**
	 * Deletes the file shortcut. This method is only supported by the Liferay
	 * repository.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileShortcut(long fileShortcutId)
		throws PortalException {

		getService().deleteFileShortcut(fileShortcutId);
	}

	/**
	 * Deletes all file shortcuts associated to the file entry. This method is
	 * only supported by the Liferay repository.
	 *
	 * @param toFileEntryId the primary key of the associated file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileShortcuts(long toFileEntryId)
		throws PortalException {

		getService().deleteFileShortcuts(toFileEntryId);
	}

	/**
	 * Deletes the file version. File versions can only be deleted if it is
	 * approved and there are other approved file versions available.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFileVersion(long fileVersionId)
		throws PortalException {

		getService().deleteFileVersion(fileVersionId);
	}

	/**
	 * Deletes the folder and all of its subfolders and file entries.
	 *
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static void deleteFolder(long folderId) throws PortalException {
		getService().deleteFolder(folderId);
	}

	public static com.liferay.portal.kernel.repository.model.FileEntry
			fetchFileEntry(long fileEntryId)
		throws PortalException {

		return getService().fetchFileEntry(fileEntryId);
	}

	/**
	 * Returns the document library file entry with the matching external
	 * reference code and group.
	 *
	 * @param groupId the primary key of the file entry's group
	 * @param externalReferenceCode the file entry's external reference code
	 * @return the matching document library file entry, or <code>null</code> if
	 a matching document library file entry could not be found
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			fetchFileEntryByExternalReferenceCode(
				long groupId, String externalReferenceCode)
		throws PortalException {

		return getService().fetchFileEntryByExternalReferenceCode(
			groupId, externalReferenceCode);
	}

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			fetchFileShortcut(long fileShortcutId)
		throws PortalException {

		return getService().fetchFileShortcut(fileShortcutId);
	}

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			fetchFileShortcutByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchFileShortcutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static com.liferay.portal.kernel.repository.model.Folder
			fetchFolderByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().fetchFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the file entry with the primary key.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @return the file entry with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntry(long fileEntryId)
		throws PortalException {

		return getService().getFileEntry(fileEntryId);
	}

	/**
	 * Returns the file entry with the title in the folder.
	 *
	 * @param groupId the primary key of the file entry's group
	 * @param folderId the primary key of the file entry's folder
	 * @param title the file entry's title
	 * @return the file entry with the title in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntry(long groupId, long folderId, String title)
		throws PortalException {

		return getService().getFileEntry(groupId, folderId, title);
	}

	/**
	 * Returns the file entry with the external reference code.
	 *
	 * @param groupId the primary key of the file entry's group
	 * @param externalReferenceCode the file entry's external reference code
	 * @return the file entry with the external reference code
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFileEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the file entry with the file name in the folder.
	 *
	 * @param groupId the primary key of the file entry's group
	 * @param folderId the primary key of the file entry's folder
	 * @param fileName the file entry's file name
	 * @return the file entry with the file name in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntryByFileName(long groupId, long folderId, String fileName)
		throws PortalException {

		return getService().getFileEntryByFileName(groupId, folderId, fileName);
	}

	/**
	 * Returns the file entry with the UUID and group.
	 *
	 * @param uuid the file entry's UUID
	 * @param groupId the primary key of the file entry's group
	 * @return the file entry with the UUID and group
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException {

		return getService().getFileEntryByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the file shortcut with the primary key. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 * @return the file shortcut with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileShortcut
			getFileShortcut(long fileShortcutId)
		throws PortalException {

		return getService().getFileShortcut(fileShortcutId);
	}

	public static com.liferay.portal.kernel.repository.model.FileShortcut
			getFileShortcutByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFileShortcutByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the file version with the primary key.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @return the file version with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileVersion
			getFileVersion(long fileVersionId)
		throws PortalException {

		return getService().getFileVersion(fileVersionId);
	}

	/**
	 * Returns the folder with the primary key.
	 *
	 * @param folderId the primary key of the folder
	 * @return the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder getFolder(
			long folderId)
		throws PortalException {

		return getService().getFolder(folderId);
	}

	/**
	 * Returns the folder with the name in the parent folder.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param name the folder's name
	 * @return the folder with the name in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder getFolder(
			long repositoryId, long parentFolderId, String name)
		throws PortalException {

		return getService().getFolder(repositoryId, parentFolderId, name);
	}

	public static com.liferay.portal.kernel.repository.model.Folder
			getFolderByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFolderByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the mount folder of the repository with the primary key. This
	 * method is only supported by the Liferay repository.
	 *
	 * @param repositoryId the primary key of the repository
	 * @return the folder used for mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder
			getMountFolder(long repositoryId)
		throws PortalException {

		return getService().getMountFolder(repositoryId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * Moves the file entry to the new folder.
	 *
	 * @param userId the primary key of the user
	 * @param fileEntryId the primary key of the file entry
	 * @param newFolderId the primary key of the new folder
	 * @param serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			moveFileEntry(
				long userId, long fileEntryId, long newFolderId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFileEntry(
			userId, fileEntryId, newFolderId, serviceContext);
	}

	public static com.liferay.portal.kernel.repository.model.Folder moveFolder(
			long userId, long folderId, long parentFolderId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveFolder(
			userId, folderId, parentFolderId, serviceContext);
	}

	public static void subscribeFileEntry(
			long userId, long groupId, long fileEntryId)
		throws PortalException {

		getService().subscribeFileEntry(userId, groupId, fileEntryId);
	}

	/**
	 * Subscribe the user to changes in documents of the file entry type. This
	 * method is only supported by the Liferay repository.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the file entry type's group
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @throws PortalException if a portal exception occurred
	 */
	public static void subscribeFileEntryType(
			long userId, long groupId, long fileEntryTypeId)
		throws PortalException {

		getService().subscribeFileEntryType(userId, groupId, fileEntryTypeId);
	}

	/**
	 * Subscribe the user to document changes in the folder. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the folder's group
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static void subscribeFolder(long userId, long groupId, long folderId)
		throws PortalException {

		getService().subscribeFolder(userId, groupId, folderId);
	}

	public static void unsubscribeFileEntry(
			long userId, long groupId, long fileEntryId)
		throws PortalException {

		getService().unsubscribeFileEntry(userId, groupId, fileEntryId);
	}

	/**
	 * Unsubscribe the user from changes in documents of the file entry type.
	 * This method is only supported by the Liferay repository.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the file entry type's group
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @throws PortalException if a portal exception occurred
	 */
	public static void unsubscribeFileEntryType(
			long userId, long groupId, long fileEntryTypeId)
		throws PortalException {

		getService().unsubscribeFileEntryType(userId, groupId, fileEntryTypeId);
	}

	/**
	 * Unsubscribe the user from document changes in the folder. This method is
	 * only supported by the Liferay repository.
	 *
	 * @param userId the primary key of the user
	 * @param groupId the primary key of the folder's group
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static void unsubscribeFolder(
			long userId, long groupId, long folderId)
		throws PortalException {

		getService().unsubscribeFolder(userId, groupId, folderId);
	}

	/**
	 * Updates the file entry's asset replacing its asset categories, tags, and
	 * links.
	 *
	 * @param userId the primary key of the user
	 * @param fileEntry the file entry to update
	 * @param fileVersion the file version to update
	 * @param assetCategoryIds the primary keys of the new asset categories
	 * @param assetTagNames the new asset tag names
	 * @param assetLinkEntryIds the primary keys of the new asset link entries
	 * @throws PortalException if a portal exception occurred
	 */
	public static void updateAsset(
			long userId,
			com.liferay.portal.kernel.repository.model.FileEntry fileEntry,
			com.liferay.portal.kernel.repository.model.FileVersion fileVersion,
			long[] assetCategoryIds, String[] assetTagNames,
			long[] assetLinkEntryIds)
		throws PortalException {

		getService().updateAsset(
			userId, fileEntry, fileVersion, assetCategoryIds, assetTagNames,
			assetLinkEntryIds);
	}

	/**
	 * Updates a file entry and associated metadata based on a byte array
	 * object. If the file data is <code>null</code>, then only the associated
	 * metadata (i.e., <code>title</code>, <code>description</code>, and
	 * parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param fileEntryId the primary key of the file entry
	 * @param sourceFileName the original file's name (optionally
	 <code>null</code>)
	 * @param mimeType the file's MIME type (optionally <code>null</code>)
	 * @param title the new name to be assigned to the file (optionally <code>
	 <code>null</code></code>)
	 * @param description the file's new description
	 * @param changeLog the file's version change log (optionally
	 <code>null</code>)
	 * @param dlVersionNumberIncrease the kind of version number increase to
	 apply for these changes.
	 * @param bytes the file's data (optionally <code>null</code>)
	 * @param displayDate the file's display date (optionally <code>null
	 </code>)
	 * @param expirationDate the file's expiration date (optionally <code>null
	 </code>)
	 * @param reviewDate the file's review date (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long userId, long fileEntryId, String sourceFileName,
				String mimeType, String title, String urlTitle,
				String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				byte[] bytes, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, bytes, displayDate,
			expirationDate, reviewDate, serviceContext);
	}

	/**
	 * Updates a file entry and associated metadata based on a {@link File}
	 * object. If the file data is <code>null</code>, then only the associated
	 * metadata (i.e., <code>title</code>, <code>description</code>, and
	 * parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param fileEntryId the primary key of the file entry
	 * @param sourceFileName the original file's name (optionally
	 <code>null</code>)
	 * @param mimeType the file's MIME type (optionally <code>null</code>)
	 * @param title the new name to be assigned to the file (optionally <code>
	 <code>null</code></code>)
	 * @param description the file's new description
	 * @param changeLog the file's version change log (optionally
	 <code>null</code>)
	 * @param dlVersionNumberIncrease the kind of version number increase to
	 apply for these changes.
	 * @param file the file's data (optionally <code>null</code>)
	 * @param displayDate the file's display date (optionally <code>null
	 </code>)
	 * @param expirationDate the file's expiration date (optionally <code>null
	 </code>)
	 * @param reviewDate the file's review date (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long userId, long fileEntryId, String sourceFileName,
				String mimeType, String title, String urlTitle,
				String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				java.io.File file, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, file, displayDate,
			expirationDate, reviewDate, serviceContext);
	}

	/**
	 * Updates a file entry and associated metadata based on an {@link
	 * InputStream} object. If the file data is <code>null</code>, then only the
	 * associated metadata (i.e., <code>title</code>, <code>description</code>,
	 * and parameters in the <code>serviceContext</code>) will be updated.
	 *
	 * <p>
	 * This method takes two file names, the <code>sourceFileName</code> and the
	 * <code>title</code>. The <code>sourceFileName</code> corresponds to the
	 * name of the actual file being uploaded. The <code>title</code>
	 * corresponds to a name the client wishes to assign this file after it has
	 * been uploaded to the portal.
	 * </p>
	 *
	 * @param userId the primary key of the user
	 * @param fileEntryId the primary key of the file entry
	 * @param sourceFileName the original file's name (optionally
	 <code>null</code>)
	 * @param mimeType the file's MIME type (optionally <code>null</code>)
	 * @param title the new name to be assigned to the file (optionally <code>
	 <code>null</code></code>)
	 * @param description the file's new description
	 * @param changeLog the file's version change log (optionally
	 <code>null</code>)
	 * @param dlVersionNumberIncrease the kind of version number increase to
	 apply for these changes.
	 * @param inputStream the file's data (optionally <code>null</code>)
	 * @param size the file's size (optionally <code>0</code>)
	 * @param displayDate the file's displaydate (optionally <code>null
	 </code>)
	 * @param expirationDate the file's expiration date (optionally <code>null
	 </code>)
	 * @param reviewDate the file's review date (optionally <code>null</code>)
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry. In a Liferay repository, it may
	 include:  <ul> <li> fileEntryTypeId - ID for a custom file entry
	 type </li> <li> fieldsMap - mapping for fields associated with a
	 custom file entry type </li> </ul>
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileEntry
			updateFileEntry(
				long userId, long fileEntryId, String sourceFileName,
				String mimeType, String title, String urlTitle,
				String description, String changeLog,
				com.liferay.document.library.kernel.model.
					DLVersionNumberIncrease dlVersionNumberIncrease,
				InputStream inputStream, long size, java.util.Date displayDate,
				java.util.Date expirationDate, java.util.Date reviewDate,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFileEntry(
			userId, fileEntryId, sourceFileName, mimeType, title, urlTitle,
			description, changeLog, dlVersionNumberIncrease, inputStream, size,
			displayDate, expirationDate, reviewDate, serviceContext);
	}

	/**
	 * Updates a file shortcut to the existing file entry. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param userId the primary key of the file shortcut's creator/owner
	 * @param fileShortcutId the primary key of the file shortcut
	 * @param folderId the primary key of the file shortcut's parent folder
	 * @param toFileEntryId the primary key of the file shortcut's file entry
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry.
	 * @return the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.FileShortcut
			updateFileShortcut(
				long userId, long fileShortcutId, long folderId,
				long toFileEntryId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFileShortcut(
			userId, fileShortcutId, folderId, toFileEntryId, serviceContext);
	}

	/**
	 * Updates all file shortcuts to the existing file entry to the new file
	 * entry. This method is only supported by the Liferay repository.
	 *
	 * @param oldToFileEntryId the primary key of the old file entry pointed to
	 * @param newToFileEntryId the primary key of the new file entry to point
	 to
	 * @throws PortalException if a portal exception occurred
	 */
	public static void updateFileShortcuts(
			long oldToFileEntryId, long newToFileEntryId)
		throws PortalException {

		getService().updateFileShortcuts(oldToFileEntryId, newToFileEntryId);
	}

	/**
	 * Updates the folder.
	 *
	 * @param folderId the primary key of the folder
	 * @param parentFolderId the primary key of the folder's new parent folder
	 * @param name the folder's new name
	 * @param description the folder's new description
	 * @param serviceContext the service context to be applied. In a Liferay
	 repository, it may include:  <ul> <li> defaultFileEntryTypeId -
	 the file entry type to default all Liferay file entries to </li>
	 <li> dlFileEntryTypesSearchContainerPrimaryKeys - a
	 comma-delimited list of file entry type primary keys allowed in
	 the given folder and all descendants </li> <li> restrictionType -
	 specifying restriction type of file entry types allowed </li>
	 <li> workflowDefinitionXYZ - the workflow definition name
	 specified per file entry type. The parameter name must be the
	 string <code>workflowDefinition</code> appended by the
	 <code>fileEntryTypeId</code> (optionally <code>0</code>).</li>
	 </ul>
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public static com.liferay.portal.kernel.repository.model.Folder
			updateFolder(
				long folderId, long parentFolderId, String name,
				String description,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFolder(
			folderId, parentFolderId, name, description, serviceContext);
	}

	public static DLAppLocalService getService() {
		return _service;
	}

	public static void setService(DLAppLocalService service) {
		_service = service;
	}

	private static volatile DLAppLocalService _service;

}