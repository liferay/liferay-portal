/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.increment.BufferedIncrement;
import com.liferay.portal.kernel.increment.DateOverrideIncrement;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.change.tracking.CTService;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.kernel.spring.osgi.OSGiBeanProperties;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DLFolder. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFolderLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.document.library.kernel.model.DLFolder"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DLFolderLocalService
	extends BaseLocalService, CTService<DLFolder>, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFolderLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the document library folder local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DLFolderLocalServiceUtil} if injection and service tracking are not available.
	 */
	public boolean addDLFileEntryTypeDLFolder(
		long fileEntryTypeId, DLFolder dlFolder);

	public boolean addDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId);

	public boolean addDLFileEntryTypeDLFolders(
		long fileEntryTypeId, List<DLFolder> dlFolders);

	public boolean addDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds);

	/**
	 * Adds the document library folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DLFolder addDLFolder(DLFolder dlFolder);

	public DLFolder addFolder(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, boolean mountPoint, long parentFolderId,
			String name, String description, boolean hidden,
			ServiceContext serviceContext)
		throws PortalException;

	public void clearDLFileEntryTypeDLFolders(long fileEntryTypeId);

	/**
	 * Creates a new document library folder with the primary key. Does not add the document library folder to the database.
	 *
	 * @param folderId the primary key for the new document library folder
	 * @return the new document library folder
	 */
	@Transactional(enabled = false)
	public DLFolder createDLFolder(long folderId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	public void deleteAllByGroup(long groupId) throws PortalException;

	public void deleteAllByRepository(long repositoryId) throws PortalException;

	public void deleteDLFileEntryTypeDLFolder(
		long fileEntryTypeId, DLFolder dlFolder);

	public void deleteDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId);

	public void deleteDLFileEntryTypeDLFolders(
		long fileEntryTypeId, List<DLFolder> dlFolders);

	public void deleteDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds);

	/**
	 * Deletes the document library folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DLFolder deleteDLFolder(DLFolder dlFolder);

	/**
	 * Deletes the document library folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder that was removed
	 * @throws PortalException if a document library folder with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DLFolder deleteDLFolder(long folderId) throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DLFolder deleteFolder(DLFolder dlFolder) throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DLFolder deleteFolder(
			DLFolder dlFolder, boolean includeTrashedEntries)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public DLFolder deleteFolder(long folderId) throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public DLFolder deleteFolder(long folderId, boolean includeTrashedEntries)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public DLFolder deleteFolder(
			long userId, long folderId, boolean includeTrashedEntries)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder fetchDLFolder(long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder fetchDLFolderByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the document library folder matching the UUID and group.
	 *
	 * @param uuid the document library folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library folder, or <code>null</code> if a matching document library folder could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder fetchDLFolderByUuidAndGroupId(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder fetchFolder(long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder fetchFolder(long groupId, long parentFolderId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder fetchFolder(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getCompanyFolders(long companyId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanyFoldersCount(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getDLFileEntryTypeDLFolders(long fileEntryTypeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getDLFileEntryTypeDLFolders(
		long fileEntryTypeId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getDLFileEntryTypeDLFolders(
		long fileEntryTypeId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDLFileEntryTypeDLFoldersCount(long fileEntryTypeId);

	/**
	 * Returns the fileEntryTypeIds of the document library file entry types associated with the document library folder.
	 *
	 * @param folderId the folderId of the document library folder
	 * @return long[] the fileEntryTypeIds of document library file entry types associated with the document library folder
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getDLFileEntryTypePrimaryKeys(long folderId);

	/**
	 * Returns the document library folder with the primary key.
	 *
	 * @param folderId the primary key of the document library folder
	 * @return the document library folder
	 * @throws PortalException if a document library folder with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getDLFolder(long folderId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getDLFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the document library folder matching the UUID and group.
	 *
	 * @param uuid the document library folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library folder
	 * @throws PortalException if a matching document library folder could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getDLFolderByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the document library folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @return the range of document library folders
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getDLFolders(int start, int end);

	/**
	 * Returns all the document library folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library folders
	 * @param companyId the primary key of the company
	 * @return the matching document library folders, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getDLFoldersByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of document library folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of document library folders
	 * @param end the upper bound of the range of document library folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching document library folders, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getDLFoldersByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator);

	/**
	 * Returns the number of document library folders.
	 *
	 * @return the number of document library folders
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDLFoldersCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFileEntriesAndFileShortcuts(
		long groupId, long folderId, QueryDefinition<?> queryDefinition);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesAndFileShortcutsCount(
		long groupId, long folderId, QueryDefinition<?> queryDefinition);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getFolder(long folderId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getFolder(long groupId, long parentFolderId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getFolderId(long companyId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(
		long groupId, boolean mountPoint, String treePath, boolean hidden);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(long groupId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status, int start, int end,
		OrderByComparator<DLFolder> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int start, int end, OrderByComparator<DLFolder> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getFolders(long classNameId, String treePath);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getFoldersAndFileEntriesAndFileShortcuts(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersAndFileEntriesAndFileShortcutsCount(
		long groupId, long folderId, String[] mimeTypes,
		boolean includeMountFolders, QueryDefinition<?> queryDefinition);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(long groupId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(
		long groupId, long parentFolderId, boolean includeMountfolders,
		int status);

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 #getFoldersCount(long, long, boolean, int)}
	 */
	@Deprecated
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFoldersCount(
		long groupId, long parentFolderId, int status,
		boolean includeMountfolders);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getFolderSize(long companyId, long groupId, String treePath);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getGroupFolderIds(long groupId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void getGroupSubfolderIds(
		List<Long> folderIds, long groupId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFolder getMountFolder(long repositoryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getMountFolders(
		long groupId, long parentFolderId, int start, int end,
		OrderByComparator<DLFolder> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getMountFoldersCount(long groupId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getNoAssetFolders();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getNotInTrashFolders(
		long groupId, boolean mountPoint, String treePath, boolean hidden);

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Long> getRepositoryFolderIds(
		long repositoryId, long parentFolderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFolder> getRepositoryFolders(
		long repositoryId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRepositoryFoldersCount(long repositoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void getRepositorySubfolderIds(
		List<Long> folderIds, long repositoryId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getUniqueFolderName(
		String uuid, long groupId, long parentFolderId, String name, int count);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasDLFileEntryTypeDLFolder(
		long fileEntryTypeId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasDLFileEntryTypeDLFolders(long fileEntryTypeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasFolderLock(long userId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasInheritableLock(long folderId);

	public Lock lockFolder(long userId, long folderId) throws PortalException;

	public Lock lockFolder(
			long userId, long folderId, String owner, boolean inheritable,
			long expirationTime)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DLFolder moveFolder(
			long userId, long folderId, long parentFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	public void rebuildTree(long companyId) throws PortalException;

	public void rebuildTree(
			long companyId, long parentFolderId, String parentTreePath,
			boolean reindex)
		throws PortalException;

	public void setDLFileEntryTypeDLFolders(
		long fileEntryTypeId, long[] folderIds);

	public void unlockFolder(
			long groupId, long parentFolderId, String name, String lockUuid)
		throws PortalException;

	public void unlockFolder(long folderId, String lockUuid)
		throws PortalException;

	/**
	 * Updates the document library folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFolder the document library folder
	 * @return the document library folder that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DLFolder updateDLFolder(DLFolder dlFolder);

	@Indexable(type = IndexableType.REINDEX)
	public DLFolder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType, ServiceContext serviceContext)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DLFolder updateFolder(
			long folderId, String name, String description,
			long defaultFileEntryTypeId, List<Long> fileEntryTypeIds,
			int restrictionType, ServiceContext serviceContext)
		throws PortalException;

	public DLFolder updateFolderAndFileEntryTypes(
			long userId, long folderId, long parentFolderId, String name,
			String description, long defaultFileEntryTypeId,
			List<Long> fileEntryTypeIds, int restrictionType,
			ServiceContext serviceContext)
		throws PortalException;

	@BufferedIncrement(
		configuration = "DLFolderEntry",
		incrementClass = DateOverrideIncrement.class
	)
	public void updateLastPostDate(long folderId, Date lastPostDate)
		throws PortalException;

	public DLFolder updateStatus(
			long userId, long folderId, int status,
			Map<String, Serializable> workflowContext,
			ServiceContext serviceContext)
		throws PortalException;

	public boolean verifyInheritableLock(long folderId, String lockUuid)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DLFolder> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DLFolder> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFolder>, R, E> updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2121924971