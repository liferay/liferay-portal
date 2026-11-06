/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for DLFileEntryType. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryTypeLocalServiceUtil
 * @generated
 */
@CTAware
@OSGiBeanProperties(
	property = {
		"model.class.name=com.liferay.document.library.kernel.model.DLFileEntryType"
	}
)
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface DLFileEntryTypeLocalService
	extends BaseLocalService, CTService<DLFileEntryType>,
			PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFileEntryTypeLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the document library file entry type local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link DLFileEntryTypeLocalServiceUtil} if injection and service tracking are not available.
	 */
	public void addDDMStructureLinks(
		long fileEntryTypeId, Set<Long> ddmStructureIds);

	/**
	 * Adds the document library file entry type to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntryType the document library file entry type
	 * @return the document library file entry type that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DLFileEntryType addDLFileEntryType(DLFileEntryType dlFileEntryType);

	public boolean addDLFolderDLFileEntryType(
		long folderId, DLFileEntryType dlFileEntryType);

	public boolean addDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId);

	public boolean addDLFolderDLFileEntryTypes(
		long folderId, List<DLFileEntryType> dlFileEntryTypes);

	public boolean addDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds);

	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int scope, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addFileEntryType(String, long, long, long, String, Map, Map, int,
	 ServiceContext)}
	 */
	@Deprecated
	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			ServiceContext serviceContext)
		throws PortalException;

	public void cascadeFileEntryTypes(long userId, DLFolder dlFolder)
		throws PortalException;

	public void clearDLFolderDLFileEntryTypes(long folderId);

	public DLFileEntryType createBasicDocumentDLFileEntryType();

	/**
	 * Creates a new document library file entry type with the primary key. Does not add the document library file entry type to the database.
	 *
	 * @param fileEntryTypeId the primary key for the new document library file entry type
	 * @return the new document library file entry type
	 */
	@Transactional(enabled = false)
	public DLFileEntryType createDLFileEntryType(long fileEntryTypeId);

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Deletes the document library file entry type from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntryType the document library file entry type
	 * @return the document library file entry type that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	public DLFileEntryType deleteDLFileEntryType(
		DLFileEntryType dlFileEntryType);

	/**
	 * Deletes the document library file entry type with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param fileEntryTypeId the primary key of the document library file entry type
	 * @return the document library file entry type that was removed
	 * @throws PortalException if a document library file entry type with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public DLFileEntryType deleteDLFileEntryType(long fileEntryTypeId)
		throws PortalException;

	public void deleteDLFolderDLFileEntryType(
		long folderId, DLFileEntryType dlFileEntryType);

	public void deleteDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId);

	public void deleteDLFolderDLFileEntryTypes(
		long folderId, List<DLFileEntryType> dlFileEntryTypes);

	public void deleteDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds);

	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(
		action = SystemEventConstants.ACTION_SKIP,
		type = SystemEventConstants.TYPE_DELETE
	)
	public DLFileEntryType deleteFileEntryType(DLFileEntryType dlFileEntryType)
		throws PortalException;

	public void deleteFileEntryType(long fileEntryTypeId)
		throws PortalException;

	public void deleteFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	public void deleteFileEntryTypes(long groupId) throws PortalException;

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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl</code>.
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
	public DLFileEntryType fetchDataDefinitionFileEntryType(
		long groupId, long dataDefinitionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType fetchDLFileEntryType(long fileEntryTypeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType fetchDLFileEntryTypeByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the document library file entry type matching the UUID and group.
	 *
	 * @param uuid the document library file entry type's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType fetchDLFileEntryTypeByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType fetchFileEntryType(long fileEntryTypeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType fetchFileEntryType(
		long groupId, String fileEntryTypeKey);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType getBasicDocumentDLFileEntryType()
		throws NoSuchFileEntryTypeException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long getDefaultFileEntryTypeId(long folderId) throws PortalException;

	/**
	 * Returns the document library file entry type with the primary key.
	 *
	 * @param fileEntryTypeId the primary key of the document library file entry type
	 * @return the document library file entry type
	 * @throws PortalException if a document library file entry type with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType getDLFileEntryType(long fileEntryTypeId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType getDLFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the document library file entry type matching the UUID and group.
	 *
	 * @param uuid the document library file entry type's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry type
	 * @throws PortalException if a matching document library file entry type could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType getDLFileEntryTypeByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns a range of all the document library file entry types.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.documentlibrary.model.impl.DLFileEntryTypeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @return the range of document library file entry types
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getDLFileEntryTypes(int start, int end);

	/**
	 * Returns all the document library file entry types matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entry types
	 * @param companyId the primary key of the company
	 * @return the matching document library file entry types, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getDLFileEntryTypesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of document library file entry types matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entry types
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of document library file entry types
	 * @param end the upper bound of the range of document library file entry types (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching document library file entry types, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getDLFileEntryTypesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator);

	/**
	 * Returns the number of document library file entry types.
	 *
	 * @return the number of document library file entry types
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDLFileEntryTypesCount();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getDLFolderDLFileEntryTypes(long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getDLFolderDLFileEntryTypes(
		long folderId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getDLFolderDLFileEntryTypes(
		long folderId, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getDLFolderDLFileEntryTypesCount(long folderId);

	/**
	 * Returns the folderIds of the document library folders associated with the document library file entry type.
	 *
	 * @param fileEntryTypeId the fileEntryTypeId of the document library file entry type
	 * @return long[] the folderIds of document library folders associated with the document library file entry type
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long[] getDLFolderPrimaryKeys(long fileEntryTypeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType getFileEntryType(long fileEntryTypeId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DLFileEntryType getFileEntryType(
			long groupId, String fileEntryTypeKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getFileEntryTypes(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getFileEntryTypesByCompanyId(long companyId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> getFolderFileEntryTypes(
			long[] groupIds, long folderId, boolean inherited)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

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
	public boolean hasDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasDLFolderDLFileEntryTypes(long folderId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<DLFileEntryType> search(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int searchCount(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType);

	public void setDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds);

	public void unsetFolderFileEntryTypes(long folderId);

	public void updateDDMStructureLinks(
			long fileEntryTypeId, Set<Long> ddmStructureIds)
		throws PortalException;

	/**
	 * Updates the document library file entry type in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DLFileEntryTypeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param dlFileEntryType the document library file entry type
	 * @return the document library file entry type that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public DLFileEntryType updateDLFileEntryType(
		DLFileEntryType dlFileEntryType);

	public DLFileEntry updateFileEntryFileEntryType(
			DLFileEntry dlFileEntry, ServiceContext serviceContext)
		throws PortalException;

	public DLFileEntryType updateFileEntryType(
			long fileEntryTypeId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap)
		throws PortalException;

	public void updateFolderFileEntryTypes(
		DLFolder dlFolder, List<Long> fileEntryTypeIds,
		long defaultFileEntryTypeId, ServiceContext serviceContext);

	@Override
	@Transactional(enabled = false)
	public CTPersistence<DLFileEntryType> getCTPersistence();

	@Override
	@Transactional(enabled = false)
	public Class<DLFileEntryType> getModelClass();

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFileEntryType>, R, E>
				updateUnsafeFunction)
		throws E;

}
// LIFERAY-SERVICE-BUILDER-HASH:-751307731