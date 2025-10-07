/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
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
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Hits;
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

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DLFileEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.document.library.kernel.model.DLFileEntry"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DLFileEntryLocalService
	extends BaseLocalService, CTService<DLFileEntry>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFileEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the document library file entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DLFileEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds the document library file entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntry the document library file entry
	 * @return the document library file entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DLFileEntry addDLFileEntry(DLFileEntry dlFileEntry);

	public DLFileEntry addFileEntry(
			String externalReferenceCode, long userId, long groupId,
			long repositoryId, long folderId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, long fileEntryTypeId,
			Map<String, DDMFormValues> ddmFormValuesMap, File file,
			InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException;

	public DLFileVersion cancelCheckOut(long userId, long fileEntryId)
		throws PortalException;

	public void checkFileEntries(long companyId, long checkInterval)
		throws PortalException;

	public void checkInFileEntry(
			long userId, long fileEntryId,
			DLVersionNumberIncrease dlVersionNumberIncrease, String changeLog,
			ServiceContext serviceContext)
		throws PortalException;

	public void checkInFileEntry(
			long userId, long fileEntryId, String lockUuid,
			ServiceContext serviceContext)
		throws PortalException;

	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, long fileEntryTypeId,
			ServiceContext serviceContext)
		throws PortalException;

	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, long fileEntryTypeId, String owner,
			long expirationTime, ServiceContext serviceContext)
		throws PortalException;

	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, ServiceContext serviceContext)
		throws PortalException;

	public DLFileEntry checkOutFileEntry(
			long userId, long fileEntryId, String owner, long expirationTime,
			ServiceContext serviceContext)
		throws PortalException;

	public void convertExtraSettings(String[] keys) throws PortalException;

	public DLFileEntry copyFileEntry(
			long userId, long groupId, long repositoryId,
			long sourceFileEntryId, long targetFolderId, String fileName,
			ServiceContext serviceContext)
		throws PortalException;

	public void copyFileEntryMetadata(
			long companyId, long fileEntryTypeId, long fileEntryId,
			long sourceFileVersionId, long targetFileVersionId,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Creates a new document library file entry with the primary key. Does not add the document library file entry to the database.
	 *
	 * @param fileEntryId the primary key for the new document library file entry
	 * @return the new document library file entry
	 */
	@Transactional(enabled = false)
	public DLFileEntry createDLFileEntry(long fileEntryId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the document library file entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntry the document library file entry
	 * @return the document library file entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DLFileEntry deleteDLFileEntry(DLFileEntry dlFileEntry);

	/**
	 * Deletes the document library file entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry that was removed
	 * @throws PortalException if a document library file entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DLFileEntry deleteDLFileEntry(long fileEntryId)
		throws PortalException;

	public void deleteFileEntries(long groupId, long folderId)
		throws PortalException;

	public void deleteFileEntries(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException;

	public void deleteFileEntries(
			long companyId, long classNameId, long classPK)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DLFileEntry deleteFileEntry(DLFileEntry dlFileEntry)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public DLFileEntry deleteFileEntry(long fileEntryId) throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public DLFileEntry deleteFileEntry(long userId, long fileEntryId)
		throws PortalException;

	@Indexable(type = IndexableType.DELETE)
	public DLFileEntry deleteFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DLFileEntry deleteFileVersion(
			long userId, long fileEntryId, String version)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	public void deleteRepositoryFileEntries(long repositoryId)
		throws PortalException;

	public void deleteRepositoryFileEntries(long repositoryId, long folderId)
		throws PortalException;

	public void deleteRepositoryFileEntries(
			long repositoryId, long folderId, boolean includeTrashedEntries)
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
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
	public DLFileEntry fetchDLFileEntry(long fileEntryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry fetchDLFileEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the document library file entry matching the UUID and group.
	 *
	 * @param uuid the document library file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry, or <code>null</code> if a matching document library file entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry fetchDLFileEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry fetchFileEntry(
		long groupId, long folderId, String title);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry fetchFileEntry(String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry fetchFileEntryByAnyImageId(long imageId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry fetchFileEntryByExternalReferenceCode(
		long groupId, String externalReferenceCode);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry fetchFileEntryByFileName(
		long groupId, long folderId, String fileName);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry fetchFileEntryByName(
		long groupId, long folderId, String name);

	public void forEachFileEntry(
			long companyId, Consumer<DLFileEntry> consumer, long maximumSize,
			String[] mimeTypes)
		throws PortalException;

	public void forEachFileEntry(
			long companyId, long classNameId, Consumer<DLFileEntry> consumer,
			long maximumSize, String[] mimeTypes)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getDDMStructureFileEntries(
		long groupId, long[] ddmStructureIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getDDMStructureFileEntries(long[] ddmStructureIds);

	/**
	 * Returns a range of all the document library file entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @return the range of document library file entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getDLFileEntries(int start, int end);

	/**
	 * Returns all the document library file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entries
	 * @param companyId the primary key of the company
	 * @return the matching document library file entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getDLFileEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of document library file entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of document library file entries
	 * @param end the upper bound of the range of document library file entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching document library file entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getDLFileEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator);

	/**
	 * Returns the number of document library file entries.
	 *
	 * @return the number of document library file entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDLFileEntriesCount();

	/**
	 * Returns the document library file entry with the primary key.
	 *
	 * @param fileEntryId the primary key of the document library file entry
	 * @return the document library file entry
	 * @throws PortalException if a document library file entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getDLFileEntry(long fileEntryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getDLFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the document library file entry matching the UUID and group.
	 *
	 * @param uuid the document library file entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry
	 * @throws PortalException if a matching document library file entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getDLFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getExtraSettingsFileEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getExtraSettingsFileEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public InputStream getFileAsStream(long fileEntryId, String version)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public InputStream getFileAsStream(
			long fileEntryId, String version, boolean incrementCounter,
			int increment)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getFileEntries(int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getFileEntries(long groupId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getFileEntries(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getFileEntries(
		long groupId, long folderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getFileEntries(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getFileEntries(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getFileEntries(long folderId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getFileEntriesByClassNameIdAndTreePath(
		long classNameId, String treePath);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesCount(long groupId, long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesCount(long groupId, long folderId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesCount(
		long groupId, long userId, List<Long> repositoryIds,
		List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFileEntriesCount(
		long groupId, long userId, List<Long> folderIds, String[] mimeTypes,
		QueryDefinition<DLFileEntry> queryDefinition);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getFileEntry(long fileEntryId) throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getFileEntry(long groupId, long folderId, String title)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getFileEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getFileEntryByFileName(
			long groupId, long folderId, String fileName)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getFileEntryByName(
			long groupId, long folderId, String name)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntry getFileEntryByUuidAndGroupId(String uuid, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Map<Long, Long> getFileEntryTypeIds(
		long companyId, long[] groupIds, String treePath);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, long rootFolderId, int start, int end,
		OrderByComparator<DLFileEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getGroupFileEntries(
		long groupId, long userId, long repositoryId, long rootFolderId,
		int start, int end, OrderByComparator<DLFileEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupFileEntriesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupFileEntriesCount(long groupId, long userId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntry> getNoAssetFileEntries();

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
	public List<DLFileEntry> getRepositoryFileEntries(
		long repositoryId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getRepositoryFileEntriesCount(long repositoryId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String getUniqueTitle(
			long groupId, long folderId, long fileEntryId, String title,
			String extension)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasExtraSettings();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasFileEntryLock(long userId, long fileEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasFileEntryLock(
		long userId, long fileEntryId, long folderId);

	@Transactional(enabled = false)
	public void incrementViewCounter(DLFileEntry dlFileEntry, int increment);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isFileEntryCheckedOut(long fileEntryId);

	public Lock lockFileEntry(long userId, long fileEntryId)
		throws PortalException;

	@Indexable(type = IndexableType.REINDEX)
	public DLFileEntry moveFileEntry(
			long userId, long fileEntryId, long newFolderId,
			ServiceContext serviceContext)
		throws PortalException;

	public void rebuildTree(long companyId) throws PortalException;

	public void revertFileEntry(
			long userId, long fileEntryId, String version,
			ServiceContext serviceContext)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
			long groupId, long userId, long creatorUserId, int status,
			int start, int end)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Hits search(
			long groupId, long userId, long creatorUserId, long folderId,
			String[] mimeTypes, int status, int start, int end)
		throws PortalException;

	public void setTreePaths(long folderId, String treePath, boolean reindex)
		throws PortalException;

	public void unlockFileEntry(long fileEntryId);

	/**
	 * Updates the document library file entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntry the document library file entry
	 * @return the document library file entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DLFileEntry updateDLFileEntry(DLFileEntry dlFileEntry);

	public DLFileEntry updateFileEntry(
			long userId, long fileEntryId, String sourceFileName,
			String mimeType, String title, String urlTitle, String description,
			String changeLog, DLVersionNumberIncrease dlVersionNumberIncrease,
			long fileEntryTypeId, Map<String, DDMFormValues> ddmFormValuesMap,
			File file, InputStream inputStream, long size, Date displayDate,
			Date expirationDate, Date reviewDate, ServiceContext serviceContext)
		throws PortalException;

	public DLFileEntry updateFileEntryType(
			long userId, long fileEntryId, long fileEntryTypeId,
			ServiceContext serviceContext)
		throws PortalException;

	public DLFileEntry updateStatus(
			long userId, DLFileEntry dlFileEntry, DLFileVersion dlFileVersion,
			int status, ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException;

	public DLFileEntry updateStatus(
			long userId, long fileVersionId, int status,
			ServiceContext serviceContext,
			Map<String, Serializable> workflowContext)
		throws PortalException;

	public void validateFile(
			long groupId, long folderId, long fileEntryId, String fileName,
			String title)
		throws PortalException;

	public boolean verifyFileEntryCheckOut(long fileEntryId, String lockUuid)
		throws PortalException;

	public boolean verifyFileEntryLock(long fileEntryId, String lockUuid)
		throws PortalException;

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DLFileEntry> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DLFileEntry> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFileEntry>, R, E>
				updateUnsafeFunction)
		throws E;

}