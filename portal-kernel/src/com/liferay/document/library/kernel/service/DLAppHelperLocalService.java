/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.async.Async;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DLAppHelper. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLAppHelperLocalServiceUtil
 * @generated
 */
@CTAware
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DLAppHelperLocalService extends BaseLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLAppHelperLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the dl app helper local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DLAppHelperLocalServiceUtil} if injection and service tracking are not available.
	 */
	public void addFolder(
			long userId, Folder folder, ServiceContext serviceContext)
		throws PortalException;

	public void cancelCheckOut(
			long userId, FileEntry fileEntry, FileVersion sourceFileVersion,
			FileVersion destinationFileVersion, FileVersion draftFileVersion,
			ServiceContext serviceContext)
		throws PortalException;

	public void cancelCheckOuts(long groupId) throws PortalException;

	public void checkAssetEntry(
			long userId, FileEntry fileEntry, FileVersion fileVersion)
		throws PortalException;

	public void deleteFileEntry(FileEntry fileEntry) throws PortalException;

	public void deleteFolder(Folder folder) throws PortalException;

	public void deleteRepositoryFileEntries(long repositoryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getCheckedOutFileEntriesCount(long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void getFileAsStream(
		long userId, FileEntry fileEntry, boolean incrementCounter);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileShortcut> getGroupFileShortcuts(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<FileEntry> getNoAssetFileEntries();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public void moveDependentsToTrash(DLFolder dlFolder) throws PortalException;

	public FileEntry moveFileEntryFromTrash(
			long userId, FileEntry fileEntry, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Moves the file entry to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the file entry
	 * @param fileEntry the file entry to be moved
	 * @return the moved file entry
	 */
	public FileEntry moveFileEntryToTrash(long userId, FileEntry fileEntry)
		throws PortalException;

	public FileShortcut moveFileShortcutFromTrash(
			long userId, FileShortcut fileShortcut, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Moves the file shortcut to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the file shortcut
	 * @param fileShortcut the file shortcut to be moved
	 * @return the moved file shortcut
	 */
	public FileShortcut moveFileShortcutToTrash(
			long userId, FileShortcut fileShortcut)
		throws PortalException;

	public Folder moveFolderFromTrash(
			long userId, Folder folder, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Moves the folder to the recycle bin.
	 *
	 * @param userId the primary key of the user moving the folder
	 * @param folder the folder to be moved
	 * @return the moved folder
	 */
	public Folder moveFolderToTrash(long userId, Folder folder)
		throws PortalException;

	@Async
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void reindex(long companyId, List<Long> dlFileEntryIds)
		throws PortalException;

	public void restoreDependentsFromTrash(DLFolder dlFolder)
		throws PortalException;

	public void restoreFileEntryFromTrash(long userId, FileEntry fileEntry)
		throws PortalException;

	public void restoreFileEntryFromTrash(
			long userId, long newFolderId, FileEntry fileEntry)
		throws PortalException;

	public void restoreFileShortcutFromTrash(
			long userId, FileShortcut fileShortcut)
		throws PortalException;

	public void restoreFolderFromTrash(long userId, Folder folder)
		throws PortalException;

	public AssetEntry updateAsset(
			long userId, FileEntry fileEntry, FileVersion fileVersion,
			long assetClassPK)
		throws PortalException;

	public AssetEntry updateAsset(
			long userId, FileEntry fileEntry, FileVersion fileVersion,
			ServiceContext serviceContext)
		throws PortalException;

	public AssetEntry updateAsset(
			long userId, Folder folder, long[] assetCategoryIds,
			String[] assetTagNames, long[] assetLinkEntryIds)
		throws PortalException;

	public void updateFileEntry(
			long userId, FileEntry fileEntry, FileVersion sourceFileVersion,
			FileVersion destinationFileVersion, long assetClassPK)
		throws PortalException;

	public void updateFileEntry(
			long userId, FileEntry fileEntry, FileVersion sourceFileVersion,
			FileVersion destinationFileVersion, ServiceContext serviceContext)
		throws PortalException;

	public void updateFolder(
			long userId, Folder folder, ServiceContext serviceContext)
		throws PortalException;

	public void updateStatus(
			long userId, FileEntry fileEntry, FileVersion latestFileVersion,
			int oldStatus, int newStatus, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:394497898