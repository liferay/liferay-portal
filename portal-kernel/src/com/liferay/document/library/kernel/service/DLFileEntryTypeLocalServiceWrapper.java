/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DLFileEntryTypeLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryTypeLocalService
 * @generated
 */
public class DLFileEntryTypeLocalServiceWrapper
	implements DLFileEntryTypeLocalService,
			   ServiceWrapper<DLFileEntryTypeLocalService> {

	public DLFileEntryTypeLocalServiceWrapper() {
		this(null);
	}

	public DLFileEntryTypeLocalServiceWrapper(
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService) {

		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
	}

	@Override
	public void addDDMStructureLinks(
		long fileEntryTypeId, java.util.Set<Long> ddmStructureIds) {

		_dlFileEntryTypeLocalService.addDDMStructureLinks(
			fileEntryTypeId, ddmStructureIds);
	}

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
	@Override
	public DLFileEntryType addDLFileEntryType(DLFileEntryType dlFileEntryType) {
		return _dlFileEntryTypeLocalService.addDLFileEntryType(dlFileEntryType);
	}

	@Override
	public boolean addDLFolderDLFileEntryType(
		long folderId, DLFileEntryType dlFileEntryType) {

		return _dlFileEntryTypeLocalService.addDLFolderDLFileEntryType(
			folderId, dlFileEntryType);
	}

	@Override
	public boolean addDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId) {

		return _dlFileEntryTypeLocalService.addDLFolderDLFileEntryType(
			folderId, fileEntryTypeId);
	}

	@Override
	public boolean addDLFolderDLFileEntryTypes(
		long folderId, java.util.List<DLFileEntryType> dlFileEntryTypes) {

		return _dlFileEntryTypeLocalService.addDLFolderDLFileEntryTypes(
			folderId, dlFileEntryTypes);
	}

	@Override
	public boolean addDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds) {

		return _dlFileEntryTypeLocalService.addDLFolderDLFileEntryTypes(
			folderId, fileEntryTypeIds);
	}

	@Override
	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int scope,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.addFileEntryType(
			externalReferenceCode, userId, groupId, dataDefinitionId,
			fileEntryTypeKey, nameMap, descriptionMap, scope, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addFileEntryType(String, long, long, long, String, Map, Map, int,
	 ServiceContext)}
	 */
	@Deprecated
	@Override
	public DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.addFileEntryType(
			externalReferenceCode, userId, groupId, dataDefinitionId,
			fileEntryTypeKey, nameMap, descriptionMap, serviceContext);
	}

	@Override
	public void cascadeFileEntryTypes(
			long userId,
			com.liferay.document.library.kernel.model.DLFolder dlFolder)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryTypeLocalService.cascadeFileEntryTypes(userId, dlFolder);
	}

	@Override
	public void clearDLFolderDLFileEntryTypes(long folderId) {
		_dlFileEntryTypeLocalService.clearDLFolderDLFileEntryTypes(folderId);
	}

	@Override
	public DLFileEntryType createBasicDocumentDLFileEntryType() {
		return _dlFileEntryTypeLocalService.
			createBasicDocumentDLFileEntryType();
	}

	/**
	 * Creates a new document library file entry type with the primary key. Does not add the document library file entry type to the database.
	 *
	 * @param fileEntryTypeId the primary key for the new document library file entry type
	 * @return the new document library file entry type
	 */
	@Override
	public DLFileEntryType createDLFileEntryType(long fileEntryTypeId) {
		return _dlFileEntryTypeLocalService.createDLFileEntryType(
			fileEntryTypeId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.createPersistedModel(primaryKeyObj);
	}

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
	@Override
	public DLFileEntryType deleteDLFileEntryType(
		DLFileEntryType dlFileEntryType) {

		return _dlFileEntryTypeLocalService.deleteDLFileEntryType(
			dlFileEntryType);
	}

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
	@Override
	public DLFileEntryType deleteDLFileEntryType(long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.deleteDLFileEntryType(
			fileEntryTypeId);
	}

	@Override
	public void deleteDLFolderDLFileEntryType(
		long folderId, DLFileEntryType dlFileEntryType) {

		_dlFileEntryTypeLocalService.deleteDLFolderDLFileEntryType(
			folderId, dlFileEntryType);
	}

	@Override
	public void deleteDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId) {

		_dlFileEntryTypeLocalService.deleteDLFolderDLFileEntryType(
			folderId, fileEntryTypeId);
	}

	@Override
	public void deleteDLFolderDLFileEntryTypes(
		long folderId, java.util.List<DLFileEntryType> dlFileEntryTypes) {

		_dlFileEntryTypeLocalService.deleteDLFolderDLFileEntryTypes(
			folderId, dlFileEntryTypes);
	}

	@Override
	public void deleteDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds) {

		_dlFileEntryTypeLocalService.deleteDLFolderDLFileEntryTypes(
			folderId, fileEntryTypeIds);
	}

	@Override
	public DLFileEntryType deleteFileEntryType(DLFileEntryType dlFileEntryType)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.deleteFileEntryType(
			dlFileEntryType);
	}

	@Override
	public void deleteFileEntryType(long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryTypeLocalService.deleteFileEntryType(fileEntryTypeId);
	}

	@Override
	public void deleteFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryTypeLocalService.deleteFileEntryTypeByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public void deleteFileEntryTypes(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryTypeLocalService.deleteFileEntryTypes(groupId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _dlFileEntryTypeLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _dlFileEntryTypeLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _dlFileEntryTypeLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _dlFileEntryTypeLocalService.dynamicQuery(dynamicQuery);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _dlFileEntryTypeLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _dlFileEntryTypeLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _dlFileEntryTypeLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _dlFileEntryTypeLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DLFileEntryType fetchDataDefinitionFileEntryType(
		long groupId, long dataDefinitionId) {

		return _dlFileEntryTypeLocalService.fetchDataDefinitionFileEntryType(
			groupId, dataDefinitionId);
	}

	@Override
	public DLFileEntryType fetchDLFileEntryType(long fileEntryTypeId) {
		return _dlFileEntryTypeLocalService.fetchDLFileEntryType(
			fileEntryTypeId);
	}

	@Override
	public DLFileEntryType fetchDLFileEntryTypeByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return _dlFileEntryTypeLocalService.
			fetchDLFileEntryTypeByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file entry type matching the UUID and group.
	 *
	 * @param uuid the document library file entry type's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType fetchDLFileEntryTypeByUuidAndGroupId(
		String uuid, long groupId) {

		return _dlFileEntryTypeLocalService.
			fetchDLFileEntryTypeByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public DLFileEntryType fetchFileEntryType(long fileEntryTypeId) {
		return _dlFileEntryTypeLocalService.fetchFileEntryType(fileEntryTypeId);
	}

	@Override
	public DLFileEntryType fetchFileEntryType(
		long groupId, String fileEntryTypeKey) {

		return _dlFileEntryTypeLocalService.fetchFileEntryType(
			groupId, fileEntryTypeKey);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _dlFileEntryTypeLocalService.getActionableDynamicQuery();
	}

	@Override
	public DLFileEntryType getBasicDocumentDLFileEntryType()
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryTypeException {

		return _dlFileEntryTypeLocalService.getBasicDocumentDLFileEntryType();
	}

	@Override
	public long getDefaultFileEntryTypeId(long folderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.getDefaultFileEntryTypeId(folderId);
	}

	/**
	 * Returns the document library file entry type with the primary key.
	 *
	 * @param fileEntryTypeId the primary key of the document library file entry type
	 * @return the document library file entry type
	 * @throws PortalException if a document library file entry type with the primary key could not be found
	 */
	@Override
	public DLFileEntryType getDLFileEntryType(long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.getDLFileEntryType(fileEntryTypeId);
	}

	@Override
	public DLFileEntryType getDLFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.
			getDLFileEntryTypeByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file entry type matching the UUID and group.
	 *
	 * @param uuid the document library file entry type's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry type
	 * @throws PortalException if a matching document library file entry type could not be found
	 */
	@Override
	public DLFileEntryType getDLFileEntryTypeByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.getDLFileEntryTypeByUuidAndGroupId(
			uuid, groupId);
	}

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
	@Override
	public java.util.List<DLFileEntryType> getDLFileEntryTypes(
		int start, int end) {

		return _dlFileEntryTypeLocalService.getDLFileEntryTypes(start, end);
	}

	/**
	 * Returns all the document library file entry types matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entry types
	 * @param companyId the primary key of the company
	 * @return the matching document library file entry types, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<DLFileEntryType>
		getDLFileEntryTypesByUuidAndCompanyId(String uuid, long companyId) {

		return _dlFileEntryTypeLocalService.
			getDLFileEntryTypesByUuidAndCompanyId(uuid, companyId);
	}

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
	@Override
	public java.util.List<DLFileEntryType>
		getDLFileEntryTypesByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<DLFileEntryType>
				orderByComparator) {

		return _dlFileEntryTypeLocalService.
			getDLFileEntryTypesByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file entry types.
	 *
	 * @return the number of document library file entry types
	 */
	@Override
	public int getDLFileEntryTypesCount() {
		return _dlFileEntryTypeLocalService.getDLFileEntryTypesCount();
	}

	@Override
	public java.util.List<DLFileEntryType> getDLFolderDLFileEntryTypes(
		long folderId) {

		return _dlFileEntryTypeLocalService.getDLFolderDLFileEntryTypes(
			folderId);
	}

	@Override
	public java.util.List<DLFileEntryType> getDLFolderDLFileEntryTypes(
		long folderId, int start, int end) {

		return _dlFileEntryTypeLocalService.getDLFolderDLFileEntryTypes(
			folderId, start, end);
	}

	@Override
	public java.util.List<DLFileEntryType> getDLFolderDLFileEntryTypes(
		long folderId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntryType>
			orderByComparator) {

		return _dlFileEntryTypeLocalService.getDLFolderDLFileEntryTypes(
			folderId, start, end, orderByComparator);
	}

	@Override
	public int getDLFolderDLFileEntryTypesCount(long folderId) {
		return _dlFileEntryTypeLocalService.getDLFolderDLFileEntryTypesCount(
			folderId);
	}

	/**
	 * Returns the folderIds of the document library folders associated with the document library file entry type.
	 *
	 * @param fileEntryTypeId the fileEntryTypeId of the document library file entry type
	 * @return long[] the folderIds of document library folders associated with the document library file entry type
	 */
	@Override
	public long[] getDLFolderPrimaryKeys(long fileEntryTypeId) {
		return _dlFileEntryTypeLocalService.getDLFolderPrimaryKeys(
			fileEntryTypeId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _dlFileEntryTypeLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public DLFileEntryType getFileEntryType(long fileEntryTypeId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.getFileEntryType(fileEntryTypeId);
	}

	@Override
	public DLFileEntryType getFileEntryType(
			long groupId, String fileEntryTypeKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.getFileEntryType(
			groupId, fileEntryTypeKey);
	}

	@Override
	public java.util.List<DLFileEntryType> getFileEntryTypes(long[] groupIds) {
		return _dlFileEntryTypeLocalService.getFileEntryTypes(groupIds);
	}

	@Override
	public java.util.List<DLFileEntryType> getFileEntryTypesByCompanyId(
		long companyId) {

		return _dlFileEntryTypeLocalService.getFileEntryTypesByCompanyId(
			companyId);
	}

	@Override
	public java.util.List<DLFileEntryType> getFolderFileEntryTypes(
			long[] groupIds, long folderId, boolean inherited)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.getFolderFileEntryTypes(
			groupIds, folderId, inherited);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _dlFileEntryTypeLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _dlFileEntryTypeLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId) {

		return _dlFileEntryTypeLocalService.hasDLFolderDLFileEntryType(
			folderId, fileEntryTypeId);
	}

	@Override
	public boolean hasDLFolderDLFileEntryTypes(long folderId) {
		return _dlFileEntryTypeLocalService.hasDLFolderDLFileEntryTypes(
			folderId);
	}

	@Override
	public java.util.List<DLFileEntryType> search(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DLFileEntryType>
			orderByComparator) {

		return _dlFileEntryTypeLocalService.search(
			companyId, groupIds, keywords, includeBasicFileEntryType, start,
			end, orderByComparator);
	}

	@Override
	public int searchCount(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType) {

		return _dlFileEntryTypeLocalService.searchCount(
			companyId, groupIds, keywords, includeBasicFileEntryType);
	}

	@Override
	public void setDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds) {

		_dlFileEntryTypeLocalService.setDLFolderDLFileEntryTypes(
			folderId, fileEntryTypeIds);
	}

	@Override
	public void unsetFolderFileEntryTypes(long folderId) {
		_dlFileEntryTypeLocalService.unsetFolderFileEntryTypes(folderId);
	}

	@Override
	public void updateDDMStructureLinks(
			long fileEntryTypeId, java.util.Set<Long> ddmStructureIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_dlFileEntryTypeLocalService.updateDDMStructureLinks(
			fileEntryTypeId, ddmStructureIds);
	}

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
	@Override
	public DLFileEntryType updateDLFileEntryType(
		DLFileEntryType dlFileEntryType) {

		return _dlFileEntryTypeLocalService.updateDLFileEntryType(
			dlFileEntryType);
	}

	@Override
	public com.liferay.document.library.kernel.model.DLFileEntry
			updateFileEntryFileEntryType(
				com.liferay.document.library.kernel.model.DLFileEntry
					dlFileEntry,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.updateFileEntryFileEntryType(
			dlFileEntry, serviceContext);
	}

	@Override
	public DLFileEntryType updateFileEntryType(
			long fileEntryTypeId,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _dlFileEntryTypeLocalService.updateFileEntryType(
			fileEntryTypeId, nameMap, descriptionMap);
	}

	@Override
	public void updateFolderFileEntryTypes(
		com.liferay.document.library.kernel.model.DLFolder dlFolder,
		java.util.List<Long> fileEntryTypeIds, long defaultFileEntryTypeId,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		_dlFileEntryTypeLocalService.updateFolderFileEntryTypes(
			dlFolder, fileEntryTypeIds, defaultFileEntryTypeId, serviceContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _dlFileEntryTypeLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<DLFileEntryType> getCTPersistence() {
		return _dlFileEntryTypeLocalService.getCTPersistence();
	}

	@Override
	public Class<DLFileEntryType> getModelClass() {
		return _dlFileEntryTypeLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DLFileEntryType>, R, E>
				updateUnsafeFunction)
		throws E {

		return _dlFileEntryTypeLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DLFileEntryTypeLocalService getWrappedService() {
		return _dlFileEntryTypeLocalService;
	}

	@Override
	public void setWrappedService(
		DLFileEntryTypeLocalService dlFileEntryTypeLocalService) {

		_dlFileEntryTypeLocalService = dlFileEntryTypeLocalService;
	}

	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1584263654