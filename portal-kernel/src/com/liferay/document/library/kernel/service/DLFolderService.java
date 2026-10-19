/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for DLFolder. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see DLFolderServiceUtil
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
public interface DLFolderService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFolderServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the document library folder remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DLFolderServiceUtil} if injection and service tracking are not available.
	 */
	public DLFolder addFolder(
			String externalReferenceCode, long groupId, long repositoryId,
			boolean mountPoint, long parentFolderId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException;

	public void deleteFolder(long folderId) throws PortalException;

	public void deleteFolder(long folderId, boolean includeTrashedEntries)
		throws PortalException;

	public void deleteFolder(long groupId, long parentFolderId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getDLFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFileEntriesAndFileShortcuts(
			long groupId, long folderId, int status, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, String[] mimeTypes, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getFolder(long folderId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getFolder(long groupId, long parentFolderId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getFolderIds(long groupId, long folderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(
			long groupId, double score, int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(
			long groupId, long parentFolderId, boolean includeMountfolders,
			int status, int start, int end,
			OrderByComparator<DLFolder> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(
			long groupId, long parentFolderId, int start, int end,
			OrderByComparator<DLFolder> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, boolean includeMountFolders,
			int status, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders, int status, int start, int end,
			OrderByComparator<?> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders, QueryDefinition<?> queryDefinition)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
			long groupId, long folderId, String[] mimeTypes,
			long fileEntryTypeId, boolean includeMountFolders, int status,
			int start, int end, OrderByComparator<?> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, int status,
			boolean includeMountFolders)
		throws PortalException;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFoldersAndFileEntriesAndFileShortcutsCount(long, long,
	 String[], boolean, int)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, int status, String[] mimeTypes,
			boolean includeMountFolders)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, String[] mimeTypes,
			boolean includeMountFolders, QueryDefinition<?> queryDefinition)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
			long groupId, long folderId, String[] mimeTypes,
			long fileEntryTypeId, boolean includeMountFolders, int status)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(long groupId, double score)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(long groupId, long parentFolderId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(
			long groupId, long parentFolderId, boolean includeMountfolders,
			int status)
		throws PortalException;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFoldersCount(long, long, boolean, int)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(
			long groupId, long parentFolderId, int status,
			boolean includeMountfolders)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getMountFolders(
			long groupId, long parentFolderId, int start, int end,
			OrderByComparator<DLFolder> orderByComparator)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getMountFoldersCount(long groupId, long parentFolderId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void getSubfolderIds(
			List<Long> folderIds, long groupId, long folderId, boolean recurse)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getSubfolderIds(
			long groupId, long folderId, boolean recurse)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasFolderLock(long folderId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasInheritableLock(long folderId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isFolderLocked(long folderId);

	public Lock lockFolder(long folderId) throws PortalException;

	public Lock lockFolder(
			long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException;

	public DLFolder moveFolder(
			long folderId, long parentFolderId, ServiceContext serviceContext)
		throws PortalException;

	public Lock refreshFolderLock(
			String lockUuid, long companyId, long expirationTime)
		throws PortalException;

	public void unlockFolder(
			long groupId, long parentFolderId, String name, String lockUuid)
		throws PortalException;

	public void unlockFolder(long folderId, String lockUuid)
		throws PortalException;

	public DLFolder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType, ServiceContext serviceContext)
		throws PortalException;

	public DLFolder updateFolder(
			long folderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType, ServiceContext serviceContext)
		throws PortalException;

	public boolean verifyInheritableLock(long folderId, String lockUuid)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:2066661061