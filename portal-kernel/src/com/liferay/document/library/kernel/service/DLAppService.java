/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.File;
import java.io.InputStream;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for DLApp. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see DLAppServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DLAppService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLAppServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the dl app remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DLAppServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds a file entry and associated metadata. It is created based on a
	 * {@link File} object.
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
	 #addFileEntry(String, long, long, String, String, String,
	 String, String, String, File, Date, Date, Date,
	 ServiceContext)}
	 */
	@Deprecated
	public FileEntry addFileEntry(
			long repositoryId, long folderId, String sourceFileName,
			String mimeType, String title, String description, String changeLog,
			File file, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a file entry and associated metadata. It is created based on a byte
	 * array.
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
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param sourceFileName the original file's name
	 * @param mimeType the file's MIME type
	 * @param title the name to be assigned to the file (optionally <code>null
	 </code>)
	 * @param urlTitle the url title
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
	public FileEntry addFileEntry(
			String externalReferenceCode, long repositoryId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, byte[] bytes,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a file entry and associated metadata. It is created based on a
	 * {@link File} object.
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
	 * @param externalReferenceCode the file entry's external reference code.
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
	public FileEntry addFileEntry(
			String externalReferenceCode, long repositoryId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a file entry and associated metadata. It is created based on a
	 * {@link InputStream} object.
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
	 * @param externalReferenceCode the file entry's external reference code.
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
	public FileEntry addFileEntry(
			String externalReferenceCode, long repositoryId, long folderId,
			String sourceFileName, String mimeType, String title,
			String urlTitle, String description, String changeLog,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a file shortcut to the existing file entry. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file shortcut's parent folder
	 * @param toFileEntryId the primary key of the file shortcut's file entry
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry.
	 * @return the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public FileShortcut addFileShortcut(
			String externalReferenceCode, long repositoryId, long folderId,
			long toFileEntryId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param name the folder's name
	 * @param description the folder's description
	 * @param serviceContext the service context to be applied. In a Liferay
	 repository, it may include boolean mountPoint specifying whether
	 folder is a facade for mounting a third-party repository
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public Folder addFolder(
			String externalReferenceCode, long repositoryId,
			long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds a temporary file entry.
	 *
	 * <p>
	 * This allows a client to upload a file into a temporary location and
	 * manipulate its metadata prior to making it available for public usage.
	 * This is different from checking in and checking out a file entry.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param folderId the primary key of the folder where the file entry will
	 eventually reside
	 * @param folderName the temporary folder's name
	 * @param fileName the file's original name
	 * @param file the file's data (optionally <code>null</code>)
	 * @param mimeType the file's MIME type
	 * @return the temporary file entry
	 * @throws PortalException if a portal exception occurred
	 * @see TempFileEntryUtil
	 */
	public FileEntry addTempFileEntry(
			long groupId, long folderId, String folderName, String fileName,
			File file, String mimeType)
		throws PortalException;

	/**
	 * Adds a temporary file entry. It is created based on the {@link
	 * InputStream} object.
	 *
	 * <p>
	 * This allows a client to upload a file into a temporary location and
	 * manipulate its metadata prior to making it available for public usage.
	 * This is different from checking in and checking out a file entry.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param folderId the primary key of the folder where the file entry will
	 eventually reside
	 * @param folderName the temporary folder's name
	 * @param fileName the file's original name
	 * @param inputStream the file's data
	 * @param mimeType the file's MIME type
	 * @return the temporary file entry
	 * @throws PortalException if a portal exception occurred
	 * @see TempFileEntryUtil
	 */
	public FileEntry addTempFileEntry(
			long groupId, long folderId, String folderName, String fileName,
			InputStream inputStream, String mimeType)
		throws PortalException;

	/**
	 * Cancels the check out of the file entry. If a user has not checked out
	 * the specified file entry, invoking this method will result in no changes.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry to cancel the
	 checkout
	 * @throws PortalException if a portal exception occurred
	 * @see #checkInFileEntry(long, String, ServiceContext)
	 * @see #checkOutFileEntry(long, ServiceContext)
	 */
	public void cancelCheckOut(long fileEntryId) throws PortalException;

	/**
	 * Checks in the file entry. If a user has not checked out the specified
	 * file entry, invoking this method will result in no changes.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry to check in
	 * @param dlVersionNumberIncrease the kind of version number increase to
	 apply for these changes.
	 * @param changeLog the file's version change log
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see #cancelCheckOut(long)
	 * @see #checkOutFileEntry(long, ServiceContext)
	 */
	public void checkInFileEntry(
			long fileEntryId, DLVersionNumberIncrease dlVersionNumberIncrease,
			String changeLog, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Checks in the file entry using the lock's UUID. If a user has not checked
	 * out the specified file entry, invoking this method will result in no
	 * changes. This method is primarily used by WebDAV.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the file entry to check in
	 * @param lockUuid the lock's UUID
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see #cancelCheckOut(long)
	 * @see #checkOutFileEntry(long, String, long, ServiceContext)
	 */
	public void checkInFileEntry(
			long fileEntryId, String lockUuid, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Check out a file entry.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the file entry to check out
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 * @see #cancelCheckOut(long)
	 * @see #checkInFileEntry(long, String, ServiceContext)
	 */
	public void checkOutFileEntry(
			long fileEntryId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Checks out the file entry. This method is primarily used by WebDAV.
	 *
	 * <p>
	 * When a file entry is checked out, a PWC (private working copy) is created
	 * and the original file entry is locked. A client can make as many changes
	 * to the PWC as he desires without those changes being visible to other
	 * users. If the user is satisfied with the changes, he may elect to check
	 * in his changes, resulting in a new file version based on the PWC; the PWC
	 * will be removed and the file entry will be unlocked. If the user is not
	 * satisfied with the changes, he may elect to cancel his check out; this
	 * results in the deletion of the PWC and unlocking of the file entry.
	 * </p>
	 *
	 * @param fileEntryId the file entry to check out
	 * @param owner the owner string for the checkout (optionally
	 <code>null</code>)
	 * @param expirationTime the time in milliseconds before the lock expires.
	 If the value is <code>0</code>, the default expiration time will
	 be used from <code>portal.properties</code>.
	 * @param serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 * @see #cancelCheckOut(long)
	 * @see #checkInFileEntry(long, String, ServiceContext)
	 */
	public FileEntry checkOutFileEntry(
			long fileEntryId, String owner, long expirationTime,
			ServiceContext serviceContext)
		throws PortalException;

	public FileEntry copyFileEntry(
			long fileEntryId, long destinationFolderId,
			long destinationRepositoryId, long fileEntryTypeId, long[] groupIds,
			ServiceContext serviceContext)
		throws PortalException;

	public FileShortcut copyFileShortcut(
			long fileShortcutId, long destinationFolderId,
			long destinationRepositoryId, ServiceContext serviceContext)
		throws PortalException;

	public Folder copyFolder(
			long sourceRepositoryId, long sourceFolderId,
			long destinationRepositoryId, long destinationParentFolderId,
			Map<Long, Long> fileEntryTypeIds, long[] groupIds,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Performs a deep copy of the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param sourceFolderId the primary key of the folder to copy
	 * @param parentFolderId the primary key of the new folder's parent folder
	 * @param name the new folder's name
	 * @param description the new folder's description
	 * @param serviceContext the service context to be applied
	 * @return the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public Folder copyFolder(
			long repositoryId, long sourceFolderId, long parentFolderId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Deletes the file entry with the primary key.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteFileEntry(long fileEntryId) throws PortalException;

	public void deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Deletes the file entry with the title in the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the file entry's parent folder
	 * @param title the file entry's title
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteFileEntryByTitle(
			long repositoryId, long folderId, String title)
		throws PortalException;

	/**
	 * Deletes the file shortcut with the primary key. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteFileShortcut(long fileShortcutId) throws PortalException;

	public void deleteFileShortcutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Deletes the file version. File versions can only be deleted if it is
	 * approved and there are other approved file versions available.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteFileVersion(long fileVersionId) throws PortalException;

	/**
	 * Deletes the file version. File versions can only be deleted if it is
	 * approved and there are other approved file versions available. This
	 * method is only supported by the Liferay repository.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param version the version label of the file version
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteFileVersion(long fileEntryId, String version)
		throws PortalException;

	/**
	 * Deletes the folder with the primary key and all of its subfolders and
	 * file entries.
	 *
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteFolder(long folderId) throws PortalException;

	/**
	 * Deletes the folder with the name in the parent folder and all of its
	 * subfolders and file entries.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param name the folder's name
	 * @throws PortalException if a portal exception occurred
	 */
	public void deleteFolder(
			long repositoryId, long parentFolderId, String name)
		throws PortalException;

	/**
	 * Deletes the temporary file entry.
	 *
	 * @param groupId the primary key of the group
	 * @param folderId the primary key of the folder where the file entry was
	 eventually to reside
	 * @param folderName the temporary folder's name
	 * @param fileName the file's original name
	 * @throws PortalException if a portal exception occurred
	 * @see TempFileEntryUtil
	 */
	public void deleteTempFileEntry(
			long groupId, long folderId, String folderName, String fileName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry fetchFileEntry(long fileEntryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry fetchFileEntry(long groupId, long folderId, String title)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry fetchFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry fetchFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry fetchFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns all the file entries in the folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @return the file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFileEntries(long repositoryId, long folderId)
		throws PortalException;

	/**
	 * Returns a name-ordered range of all the file entries in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, int start, int end)
		throws PortalException;

	/**
	 * Returns an ordered range of all the file entries in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the file entries
	 (optionally <code>null</code>)
	 * @return the range of file entries in the folder ordered by comparator
	 <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	/**
	 * Returns the file entries with the file entry type in the folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @return the file entries with the file entry type in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, long fileEntryTypeId)
		throws PortalException;

	/**
	 * Returns a name-ordered range of all the file entries with the file entry
	 * type in the folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of the file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, long fileEntryTypeId, int start,
			int end)
		throws PortalException;

	/**
	 * Returns an ordered range of all the file entries with the file entry type
	 * in the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the results by
	 (optionally <code>null</code>)
	 * @return the range of file entries with the file entry type in the folder
	 ordered by <code>null</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, long fileEntryTypeId, int start,
			int end, OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, String[] mimeTypes)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getFileEntries(
			long repositoryId, long folderId, String[] mimeTypes, int start,
			int end, OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	/**
	 * Returns a range of all the file entries and shortcuts in the folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param status the workflow status
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status, int start, int end)
		throws PortalException;

	/**
	 * Returns the number of file entries and shortcuts in the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param status the workflow status
	 * @return the number of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status)
		throws PortalException;

	/**
	 * Returns the number of file entries and shortcuts in the folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param status the workflow status
	 * @param mimeTypes allowed media types
	 * @return the number of file entries and shortcuts in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes)
		throws PortalException;

	/**
	 * Returns the number of file entries in the folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @return the number of file entries in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesCount(long repositoryId, long folderId)
		throws PortalException;

	/**
	 * Returns the number of file entries with the file entry type in the
	 * folder.
	 *
	 * @param repositoryId the primary key of the file entry's repository
	 * @param folderId the primary key of the file entry's folder
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @return the number of file entries with the file entry type in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesCount(
			long repositoryId, long folderId, long fileEntryTypeId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesCount(
			long repositoryId, long folderId, String[] mimeTypes)
		throws PortalException;

	/**
	 * Returns the file entry with the primary key.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @return the file entry with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry getFileEntry(long fileEntryId) throws PortalException;

	/**
	 * Returns the file entry with the title in the folder.
	 *
	 * @param groupId the primary key of the file entry's group
	 * @param folderId the primary key of the file entry's folder
	 * @param title the file entry's title
	 * @return the file entry with the title in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry getFileEntry(long groupId, long folderId, String title)
		throws PortalException;

	/**
	 * Returns the file entry with the external reference code.
	 *
	 * @param groupId the primary key of the file entry's group
	 * @param externalReferenceCode the file entry's external reference code
	 * @return the file entry with the external reference code
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the file entry with the file name in the folder.
	 *
	 * @param groupId the primary key of the file entry's group
	 * @param folderId the primary key of the file entry's folder
	 * @param fileName the file entry's file name
	 * @return the file entry with the file name in the folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry getFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws PortalException;

	/**
	 * Returns the file entry with the UUID and group.
	 *
	 * @param uuid the file entry's UUID
	 * @param groupId the primary key of the file entry's group
	 * @return the file entry with the UUID and group
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileEntry getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns the file shortcut with the primary key. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 * @return the file shortcut with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileShortcut getFileShortcutByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the file version with the primary key.
	 *
	 * @param fileVersionId the primary key of the file version
	 * @return the file version with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public FileVersion getFileVersion(long fileVersionId)
		throws PortalException;

	/**
	 * Returns the folder with the primary key.
	 *
	 * @param folderId the primary key of the folder
	 * @return the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Folder getFolder(long folderId) throws PortalException;

	/**
	 * Returns the folder with the name in the parent folder.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param name the folder's name
	 * @return the folder with the name in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Folder getFolder(long repositoryId, long parentFolderId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Folder getFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns all immediate subfolders of the parent folder.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @return the immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getFolders(long repositoryId, long parentFolderId)
		throws PortalException;

	/**
	 * Returns all immediate subfolders of the parent folder, optionally
	 * including mount folders for third-party repositories.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @return the immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, boolean includeMountFolders)
		throws PortalException;

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder, optionally including mount folders for third-party
	 * repositories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, boolean includeMountFolders,
			int start, int end)
		throws PortalException;

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the folders (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 comparator <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, boolean includeMountFolders,
			int start, int end, OrderByComparator<Folder> orderByComparator)
		throws PortalException;

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the folders (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 comparator <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, int status,
			boolean includeMountFolders, int start, int end,
			OrderByComparator<Folder> orderByComparator)
		throws PortalException;

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, int start, int end)
		throws PortalException;

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the folders (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder ordered by
	 comparator <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getFolders(
			long repositoryId, long parentFolderId, int start, int end,
			OrderByComparator<Folder> orderByComparator)
		throws PortalException;

	/**
	 * Returns a name-ordered range of all the immediate subfolders, file
	 * entries, and file shortcuts in the parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders, file entries, and
	 file shortcuts in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status,
			boolean includeMountFolders, int start, int end)
		throws PortalException;

	/**
	 * Returns an ordered range of all the immediate subfolders, file entries,
	 * and file shortcuts in the parent folder.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the results (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders, file entries, and file
	 shortcuts in the parent folder ordered by comparator
	 <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status,
			boolean includeMountFolders, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, boolean includeOwner, int start,
			int end, OrderByComparator<?> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException;

	/**
	 * Returns the number of immediate subfolders, file entries, and file
	 * shortcuts in the parent folder.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @return the number of immediate subfolders, file entries, and file
	 shortcuts in the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status,
			boolean includeMountFolders)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long repositoryId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders, boolean includeOwner)
		throws PortalException;

	/**
	 * Returns the number of immediate subfolders of the parent folder.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(long repositoryId, long parentFolderId)
		throws PortalException;

	/**
	 * Returns the number of immediate subfolders of the parent folder,
	 * optionally including mount folders for third-party repositories.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(
			long repositoryId, long parentFolderId, boolean includeMountFolders)
		throws PortalException;

	/**
	 * Returns the number of immediate subfolders of the parent folder,
	 * optionally including mount folders for third-party repositories.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param status the workflow status
	 * @param includeMountFolders whether to include mount folders for
	 third-party repositories
	 * @return the number of immediate subfolders of the parent folder
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(
			long repositoryId, long parentFolderId, int status,
			boolean includeMountFolders)
		throws PortalException;

	/**
	 * Returns the number of immediate subfolders and file entries across the
	 * folders.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderIds the primary keys of folders from which to count
	 immediate subfolders and file entries
	 * @param status the workflow status
	 * @return the number of immediate subfolders and file entries across the
	 folders
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersFileEntriesCount(
			long repositoryId, List<Long> folderIds, int status)
		throws PortalException;

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the repository default parent folder that are stored within the Liferay
	 * repository. This method is primarily used to search for recently modified
	 * file entries. It can be limited to the file entries modified by a given
	 * user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching file entries ordered by date modified
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, int start, int end)
		throws PortalException;

	/**
	 * Returns an ordered range of all the file entries in the group that are
	 * stored within the Liferay repository. This method is primarily used to
	 * search for recently modified file entries. It can be limited to the file
	 * entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the file entries
	 (optionally <code>null</code>)
	 * @return the range of matching file entries ordered by comparator
	 <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the root folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the range of matching file entries ordered by date modified
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, int start, int end)
		throws PortalException;

	/**
	 * Returns an ordered range of all the file entries in the group starting at
	 * the root folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the file entries
	 (optionally <code>null</code>)
	 * @return the range of matching file entries ordered by comparator
	 <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getGroupFileEntries(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status, int start, int end,
			OrderByComparator<FileEntry> orderByComparator)
		throws PortalException;

	/**
	 * Returns the number of file entries in a group starting at the repository
	 * default parent folder that are stored within the Liferay repository. This
	 * method is primarily used to search for recently modified file entries. It
	 * can be limited to the file entries modified by a given user.
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @return the number of matching file entries
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupFileEntriesCount(long groupId, long userId)
		throws PortalException;

	/**
	 * Returns the number of file entries in a group starting at the root folder
	 * that are stored within the Liferay repository. This method is primarily
	 * used to search for recently modified file entries. It can be limited to
	 * the file entries modified by a given user.
	 *
	 * @param groupId the primary key of the group
	 * @param userId the primary key of the user who created the file
	 (optionally <code>0</code>)
	 * @param rootFolderId the primary key of the root folder to begin the
	 search
	 * @return the number of matching file entries
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupFileEntriesCount(
			long groupId, long userId, long rootFolderId, String[] mimeTypes,
			int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileShortcut> getGroupFileShortcuts(long groupId)
		throws PortalException;

	/**
	 * Returns all immediate subfolders of the parent folder that are used for
	 * mounting third-party repositories. This method is only supported by the
	 * Liferay repository.
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @return the immediate subfolders of the parent folder that are used for
	 mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getMountFolders(long repositoryId, long parentFolderId)
		throws PortalException;

	/**
	 * Returns a name-ordered range of all the immediate subfolders of the
	 * parent folder that are used for mounting third-party repositories. This
	 * method is only supported by the Liferay repository.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the parent folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @return the name-ordered range of immediate subfolders of the parent
	 folder that are used for mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getMountFolders(
			long repositoryId, long parentFolderId, int start, int end)
		throws PortalException;

	/**
	 * Returns an ordered range of all the immediate subfolders of the parent
	 * folder that are used for mounting third-party repositories. This method
	 * is only supported by the Liferay repository.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param repositoryId the primary key of the folder's repository
	 * @param parentFolderId the primary key of the folder's parent folder
	 * @param start the lower bound of the range of results
	 * @param end the upper bound of the range of results (not inclusive)
	 * @param orderByComparator the comparator to order the folders (optionally
	 <code>null</code>)
	 * @return the range of immediate subfolders of the parent folder that are
	 used for mounting third-party repositories ordered by comparator
	 <code>orderByComparator</code>
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Folder> getMountFolders(
			long repositoryId, long parentFolderId, int start, int end,
			OrderByComparator<Folder> orderByComparator)
		throws PortalException;

	/**
	 * Returns the number of immediate subfolders of the parent folder that are
	 * used for mounting third-party repositories. This method is only supported
	 * by the Liferay repository.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the parent folder
	 * @return the number of folders of the parent folder that are used for
	 mounting third-party repositories
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getMountFoldersCount(long repositoryId, long parentFolderId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void getSubfolderIds(
			long repositoryId, List<Long> folderIds, long folderId)
		throws PortalException;

	/**
	 * Returns all the descendant folders of the folder with the primary key.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @return the descendant folders of the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getSubfolderIds(long repositoryId, long folderId)
		throws PortalException;

	/**
	 * Returns descendant folders of the folder with the primary key, optionally
	 * limiting to one level deep.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param recurse whether to recurse through each subfolder
	 * @return the descendant folders of the folder with the primary key
	 * @throws PortalException if a portal exception occurred
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getSubfolderIds(
			long repositoryId, long folderId, boolean recurse)
		throws PortalException;

	/**
	 * Returns all the temporary file entry names.
	 *
	 * @param groupId the primary key of the group
	 * @param folderId the primary key of the folder where the file entry will
	 eventually reside
	 * @param folderName the temporary folder's name
	 * @return the temporary file entry names
	 * @throws PortalException if a portal exception occurred
	 * @see #addTempFileEntry(long, long, String, String, File, String)
	 * @see TempFileEntryUtil
	 */
	@CTAware(onProduction = true)
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String[] getTempFileNames(
			long groupId, long folderId, String folderName)
		throws PortalException;

	/**
	 * Locks the folder. This method is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	public Lock lockFolder(long repositoryId, long folderId)
		throws PortalException;

	/**
	 * Locks the folder. This method is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param owner the owner string for the checkout (optionally
	 <code>null</code>)
	 * @param inheritable whether the lock must propagate to descendants
	 * @param expirationTime the time in milliseconds before the lock expires.
	 If the value is <code>0</code>, the default expiration time will
	 be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	public Lock lockFolder(
			long repositoryId, long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException;

	/**
	 * Moves the file entry to the new folder.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param newFolderId the primary key of the new folder
	 * @param serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public FileEntry moveFileEntry(
			long fileEntryId, long newFolderId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Moves the folder to the new parent folder with the primary key.
	 *
	 * @param folderId the primary key of the folder
	 * @param parentFolderId the primary key of the new parent folder
	 * @param serviceContext the service context to be applied
	 * @return the file entry
	 * @throws PortalException if a portal exception occurred
	 */
	public Folder moveFolder(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Refreshes the lock for the file entry. This method is primarily used by
	 * WebDAV.
	 *
	 * @param lockUuid the lock's UUID
	 * @param companyId the primary key of the file entry's company
	 * @param expirationTime the time in milliseconds before the lock expires.
	 If the value is <code>0</code>, the default expiration time will
	 be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	public Lock refreshFileEntryLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException;

	/**
	 * Refreshes the lock for the folder. This method is primarily used by
	 * WebDAV.
	 *
	 * @param lockUuid the lock's UUID
	 * @param companyId the primary key of the file entry's company
	 * @param expirationTime the time in milliseconds before the lock expires.
	 If the value is <code>0</code>, the default expiration time will
	 be used from <code>portal.properties</code>.
	 * @return the lock object
	 * @throws PortalException if a portal exception occurred
	 */
	public Lock refreshFolderLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException;

	/**
	 * Reverts the file entry to a previous version. A new version will be
	 * created based on the previous version and metadata.
	 *
	 * @param fileEntryId the primary key of the file entry
	 * @param version the version to revert back to
	 * @param serviceContext the service context to be applied
	 * @throws PortalException if a portal exception occurred
	 */
	public void revertFileEntry(
			long fileEntryId, String version, ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
			long repositoryId, long creatorUserId, int status, int start,
			int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
			long repositoryId, long creatorUserId, long folderId,
			String[] mimeTypes, int status, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(long repositoryId, SearchContext searchContext)
		throws SearchException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
			long repositoryId, SearchContext searchContext, Query query)
		throws SearchException;

	public void subscribeFileEntry(long groupId, long fileEntryId)
		throws PortalException;

	/**
	 * Subscribe the user to changes in documents of the file entry type. This
	 * method is only supported by the Liferay repository.
	 *
	 * @param groupId the primary key of the file entry type's group
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @throws PortalException if a portal exception occurred
	 */
	public void subscribeFileEntryType(long groupId, long fileEntryTypeId)
		throws PortalException;

	/**
	 * Subscribe the user to document changes in the folder. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param groupId the primary key of the folder's group
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public void subscribeFolder(long groupId, long folderId)
		throws PortalException;

	/**
	 * Unlocks the folder. This method is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param folderId the primary key of the folder
	 * @param lockUuid the lock's UUID
	 * @throws PortalException if a portal exception occurred
	 */
	public void unlockFolder(long repositoryId, long folderId, String lockUuid)
		throws PortalException;

	/**
	 * Unlocks the folder. This method is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key of the repository
	 * @param parentFolderId the primary key of the parent folder
	 * @param name the folder's name
	 * @param lockUuid the lock's UUID
	 * @throws PortalException if a portal exception occurred
	 */
	public void unlockFolder(
			long repositoryId, long parentFolderId, String name,
			String lockUuid)
		throws PortalException;

	public void unsubscribeFileEntry(long groupId, long fileEntryId)
		throws PortalException;

	/**
	 * Unsubscribe the user from changes in documents of the file entry type.
	 * This method is only supported by the Liferay repository.
	 *
	 * @param groupId the primary key of the file entry type's group
	 * @param fileEntryTypeId the primary key of the file entry type
	 * @throws PortalException if a portal exception occurred
	 */
	public void unsubscribeFileEntryType(long groupId, long fileEntryTypeId)
		throws PortalException;

	/**
	 * Unsubscribe the user from document changes in the folder. This method is
	 * only supported by the Liferay repository.
	 *
	 * @param groupId the primary key of the folder's group
	 * @param folderId the primary key of the folder
	 * @throws PortalException if a portal exception occurred
	 */
	public void unsubscribeFolder(long groupId, long folderId)
		throws PortalException;

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
	public FileEntry updateFileEntry(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease, byte[] bytes,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException;

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
	public FileEntry updateFileEntry(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException;

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
	public FileEntry updateFileEntry(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException;

	public FileEntry updateFileEntryAndCheckIn(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease, File file,
			Date displayDate, Date expirationDate, Date reviewDate,
			ServiceContext serviceContext)
		throws PortalException;

	public FileEntry updateFileEntryAndCheckIn(
			long fileEntryId, String sourceFileName, String mimeType,
			String title, String urlTitle, String description, String changeLog,
			DLVersionNumberIncrease dlVersionNumberIncrease,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates a file shortcut to the existing file entry. This method is only
	 * supported by the Liferay repository.
	 *
	 * @param fileShortcutId the primary key of the file shortcut
	 * @param folderId the primary key of the file shortcut's parent folder
	 * @param toFileEntryId the primary key of the file shortcut's file entry
	 * @param serviceContext the service context to be applied. Can set the
	 asset category IDs, asset tag names, and expando bridge
	 attributes for the file entry.
	 * @return the file shortcut
	 * @throws PortalException if a portal exception occurred
	 */
	public FileShortcut updateFileShortcut(
			long fileShortcutId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the folder.
	 *
	 * @param folderId the primary key of the folder
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
	public Folder updateFolder(
			long folderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Returns <code>true</code> if the file entry is checked out. This method
	 * is primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key for the repository
	 * @param fileEntryId the primary key for the file entry
	 * @param lockUuid the lock's UUID
	 * @return <code>true</code> if the file entry is checked out;
	 <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	public boolean verifyFileEntryCheckOut(
			long repositoryId, long fileEntryId, String lockUuid)
		throws PortalException;

	public boolean verifyFileEntryLock(
			long repositoryId, long fileEntryId, String lockUuid)
		throws PortalException;

	/**
	 * Returns <code>true</code> if the inheritable lock exists. This method is
	 * primarily used by WebDAV.
	 *
	 * @param repositoryId the primary key for the repository
	 * @param folderId the primary key for the folder
	 * @param lockUuid the lock's UUID
	 * @return <code>true</code> if the inheritable lock exists;
	 <code>false</code> otherwise
	 * @throws PortalException if a portal exception occurred
	 */
	public boolean verifyInheritableLock(
			long repositoryId, long folderId, String lockUuid)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1359349339