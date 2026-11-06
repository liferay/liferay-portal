/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.kernel.service;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides the local service utility for DLFileEntryType. This utility wraps
 * <code>com.liferay.portlet.documentlibrary.service.impl.DLFileEntryTypeLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see DLFileEntryTypeLocalService
 * @generated
 */
public class DLFileEntryTypeLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portlet.documentlibrary.service.impl.DLFileEntryTypeLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static void addDDMStructureLinks(
		long fileEntryTypeId, Set<Long> ddmStructureIds) {

		getService().addDDMStructureLinks(fileEntryTypeId, ddmStructureIds);
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
	public static DLFileEntryType addDLFileEntryType(
		DLFileEntryType dlFileEntryType) {

		return getService().addDLFileEntryType(dlFileEntryType);
	}

	public static boolean addDLFolderDLFileEntryType(
		long folderId, DLFileEntryType dlFileEntryType) {

		return getService().addDLFolderDLFileEntryType(
			folderId, dlFileEntryType);
	}

	public static boolean addDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId) {

		return getService().addDLFolderDLFileEntryType(
			folderId, fileEntryTypeId);
	}

	public static boolean addDLFolderDLFileEntryTypes(
		long folderId, List<DLFileEntryType> dlFileEntryTypes) {

		return getService().addDLFolderDLFileEntryTypes(
			folderId, dlFileEntryTypes);
	}

	public static boolean addDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds) {

		return getService().addDLFolderDLFileEntryTypes(
			folderId, fileEntryTypeIds);
	}

	public static DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap, int scope,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileEntryType(
			externalReferenceCode, userId, groupId, dataDefinitionId,
			fileEntryTypeKey, nameMap, descriptionMap, scope, serviceContext);
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 #addFileEntryType(String, long, long, long, String, Map, Map, int,
	 ServiceContext)}
	 */
	@Deprecated
	public static DLFileEntryType addFileEntryType(
			String externalReferenceCode, long userId, long groupId,
			long dataDefinitionId, String fileEntryTypeKey,
			Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFileEntryType(
			externalReferenceCode, userId, groupId, dataDefinitionId,
			fileEntryTypeKey, nameMap, descriptionMap, serviceContext);
	}

	public static void cascadeFileEntryTypes(
			long userId,
			com.liferay.document.library.kernel.model.DLFolder dlFolder)
		throws PortalException {

		getService().cascadeFileEntryTypes(userId, dlFolder);
	}

	public static void clearDLFolderDLFileEntryTypes(long folderId) {
		getService().clearDLFolderDLFileEntryTypes(folderId);
	}

	public static DLFileEntryType createBasicDocumentDLFileEntryType() {
		return getService().createBasicDocumentDLFileEntryType();
	}

	/**
	 * Creates a new document library file entry type with the primary key. Does not add the document library file entry type to the database.
	 *
	 * @param fileEntryTypeId the primary key for the new document library file entry type
	 * @return the new document library file entry type
	 */
	public static DLFileEntryType createDLFileEntryType(long fileEntryTypeId) {
		return getService().createDLFileEntryType(fileEntryTypeId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
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
	public static DLFileEntryType deleteDLFileEntryType(
		DLFileEntryType dlFileEntryType) {

		return getService().deleteDLFileEntryType(dlFileEntryType);
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
	public static DLFileEntryType deleteDLFileEntryType(long fileEntryTypeId)
		throws PortalException {

		return getService().deleteDLFileEntryType(fileEntryTypeId);
	}

	public static void deleteDLFolderDLFileEntryType(
		long folderId, DLFileEntryType dlFileEntryType) {

		getService().deleteDLFolderDLFileEntryType(folderId, dlFileEntryType);
	}

	public static void deleteDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId) {

		getService().deleteDLFolderDLFileEntryType(folderId, fileEntryTypeId);
	}

	public static void deleteDLFolderDLFileEntryTypes(
		long folderId, List<DLFileEntryType> dlFileEntryTypes) {

		getService().deleteDLFolderDLFileEntryTypes(folderId, dlFileEntryTypes);
	}

	public static void deleteDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds) {

		getService().deleteDLFolderDLFileEntryTypes(folderId, fileEntryTypeIds);
	}

	public static DLFileEntryType deleteFileEntryType(
			DLFileEntryType dlFileEntryType)
		throws PortalException {

		return getService().deleteFileEntryType(dlFileEntryType);
	}

	public static void deleteFileEntryType(long fileEntryTypeId)
		throws PortalException {

		getService().deleteFileEntryType(fileEntryTypeId);
	}

	public static void deleteFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		getService().deleteFileEntryTypeByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static void deleteFileEntryTypes(long groupId)
		throws PortalException {

		getService().deleteFileEntryTypes(groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static DLFileEntryType fetchDataDefinitionFileEntryType(
		long groupId, long dataDefinitionId) {

		return getService().fetchDataDefinitionFileEntryType(
			groupId, dataDefinitionId);
	}

	public static DLFileEntryType fetchDLFileEntryType(long fileEntryTypeId) {
		return getService().fetchDLFileEntryType(fileEntryTypeId);
	}

	public static DLFileEntryType fetchDLFileEntryTypeByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchDLFileEntryTypeByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the document library file entry type matching the UUID and group.
	 *
	 * @param uuid the document library file entry type's UUID
	 * @param groupId the primary key of the group
	 * @return the matching document library file entry type, or <code>null</code> if a matching document library file entry type could not be found
	 */
	public static DLFileEntryType fetchDLFileEntryTypeByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchDLFileEntryTypeByUuidAndGroupId(uuid, groupId);
	}

	public static DLFileEntryType fetchFileEntryType(long fileEntryTypeId) {
		return getService().fetchFileEntryType(fileEntryTypeId);
	}

	public static DLFileEntryType fetchFileEntryType(
		long groupId, String fileEntryTypeKey) {

		return getService().fetchFileEntryType(groupId, fileEntryTypeKey);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static DLFileEntryType getBasicDocumentDLFileEntryType()
		throws com.liferay.document.library.kernel.exception.
			NoSuchFileEntryTypeException {

		return getService().getBasicDocumentDLFileEntryType();
	}

	public static long getDefaultFileEntryTypeId(long folderId)
		throws PortalException {

		return getService().getDefaultFileEntryTypeId(folderId);
	}

	/**
	 * Returns the document library file entry type with the primary key.
	 *
	 * @param fileEntryTypeId the primary key of the document library file entry type
	 * @return the document library file entry type
	 * @throws PortalException if a document library file entry type with the primary key could not be found
	 */
	public static DLFileEntryType getDLFileEntryType(long fileEntryTypeId)
		throws PortalException {

		return getService().getDLFileEntryType(fileEntryTypeId);
	}

	public static DLFileEntryType getDLFileEntryTypeByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getDLFileEntryTypeByExternalReferenceCode(
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
	public static DLFileEntryType getDLFileEntryTypeByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getDLFileEntryTypeByUuidAndGroupId(uuid, groupId);
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
	public static List<DLFileEntryType> getDLFileEntryTypes(
		int start, int end) {

		return getService().getDLFileEntryTypes(start, end);
	}

	/**
	 * Returns all the document library file entry types matching the UUID and company.
	 *
	 * @param uuid the UUID of the document library file entry types
	 * @param companyId the primary key of the company
	 * @return the matching document library file entry types, or an empty list if no matches were found
	 */
	public static List<DLFileEntryType> getDLFileEntryTypesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getDLFileEntryTypesByUuidAndCompanyId(
			uuid, companyId);
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
	public static List<DLFileEntryType> getDLFileEntryTypesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		return getService().getDLFileEntryTypesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of document library file entry types.
	 *
	 * @return the number of document library file entry types
	 */
	public static int getDLFileEntryTypesCount() {
		return getService().getDLFileEntryTypesCount();
	}

	public static List<DLFileEntryType> getDLFolderDLFileEntryTypes(
		long folderId) {

		return getService().getDLFolderDLFileEntryTypes(folderId);
	}

	public static List<DLFileEntryType> getDLFolderDLFileEntryTypes(
		long folderId, int start, int end) {

		return getService().getDLFolderDLFileEntryTypes(folderId, start, end);
	}

	public static List<DLFileEntryType> getDLFolderDLFileEntryTypes(
		long folderId, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		return getService().getDLFolderDLFileEntryTypes(
			folderId, start, end, orderByComparator);
	}

	public static int getDLFolderDLFileEntryTypesCount(long folderId) {
		return getService().getDLFolderDLFileEntryTypesCount(folderId);
	}

	/**
	 * Returns the folderIds of the document library folders associated with the document library file entry type.
	 *
	 * @param fileEntryTypeId the fileEntryTypeId of the document library file entry type
	 * @return long[] the folderIds of document library folders associated with the document library file entry type
	 */
	public static long[] getDLFolderPrimaryKeys(long fileEntryTypeId) {
		return getService().getDLFolderPrimaryKeys(fileEntryTypeId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static DLFileEntryType getFileEntryType(long fileEntryTypeId)
		throws PortalException {

		return getService().getFileEntryType(fileEntryTypeId);
	}

	public static DLFileEntryType getFileEntryType(
			long groupId, String fileEntryTypeKey)
		throws PortalException {

		return getService().getFileEntryType(groupId, fileEntryTypeKey);
	}

	public static List<DLFileEntryType> getFileEntryTypes(long[] groupIds) {
		return getService().getFileEntryTypes(groupIds);
	}

	public static List<DLFileEntryType> getFileEntryTypesByCompanyId(
		long companyId) {

		return getService().getFileEntryTypesByCompanyId(companyId);
	}

	public static List<DLFileEntryType> getFolderFileEntryTypes(
			long[] groupIds, long folderId, boolean inherited)
		throws PortalException {

		return getService().getFolderFileEntryTypes(
			groupIds, folderId, inherited);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
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
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasDLFolderDLFileEntryType(
		long folderId, long fileEntryTypeId) {

		return getService().hasDLFolderDLFileEntryType(
			folderId, fileEntryTypeId);
	}

	public static boolean hasDLFolderDLFileEntryTypes(long folderId) {
		return getService().hasDLFolderDLFileEntryTypes(folderId);
	}

	public static List<DLFileEntryType> search(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType, int start, int end,
		OrderByComparator<DLFileEntryType> orderByComparator) {

		return getService().search(
			companyId, groupIds, keywords, includeBasicFileEntryType, start,
			end, orderByComparator);
	}

	public static int searchCount(
		long companyId, long[] groupIds, String keywords,
		boolean includeBasicFileEntryType) {

		return getService().searchCount(
			companyId, groupIds, keywords, includeBasicFileEntryType);
	}

	public static void setDLFolderDLFileEntryTypes(
		long folderId, long[] fileEntryTypeIds) {

		getService().setDLFolderDLFileEntryTypes(folderId, fileEntryTypeIds);
	}

	public static void unsetFolderFileEntryTypes(long folderId) {
		getService().unsetFolderFileEntryTypes(folderId);
	}

	public static void updateDDMStructureLinks(
			long fileEntryTypeId, Set<Long> ddmStructureIds)
		throws PortalException {

		getService().updateDDMStructureLinks(fileEntryTypeId, ddmStructureIds);
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
	public static DLFileEntryType updateDLFileEntryType(
		DLFileEntryType dlFileEntryType) {

		return getService().updateDLFileEntryType(dlFileEntryType);
	}

	public static com.liferay.document.library.kernel.model.DLFileEntry
			updateFileEntryFileEntryType(
				com.liferay.document.library.kernel.model.DLFileEntry
					dlFileEntry,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateFileEntryFileEntryType(
			dlFileEntry, serviceContext);
	}

	public static DLFileEntryType updateFileEntryType(
			long fileEntryTypeId, Map<java.util.Locale, String> nameMap,
			Map<java.util.Locale, String> descriptionMap)
		throws PortalException {

		return getService().updateFileEntryType(
			fileEntryTypeId, nameMap, descriptionMap);
	}

	public static void updateFolderFileEntryTypes(
		com.liferay.document.library.kernel.model.DLFolder dlFolder,
		List<Long> fileEntryTypeIds, long defaultFileEntryTypeId,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		getService().updateFolderFileEntryTypes(
			dlFolder, fileEntryTypeIds, defaultFileEntryTypeId, serviceContext);
	}

	public static DLFileEntryTypeLocalService getService() {
		return _service;
	}

	public static void setService(DLFileEntryTypeLocalService service) {
		_service = service;
	}

	private static volatile DLFileEntryTypeLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:-663848710