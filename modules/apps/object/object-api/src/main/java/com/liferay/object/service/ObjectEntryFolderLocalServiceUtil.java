/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

/**
 * Provides the local service utility for ObjectEntryFolder. This utility wraps
 * <code>com.liferay.object.service.impl.ObjectEntryFolderLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Marco Leo
 * @see ObjectEntryFolderLocalService
 * @generated
 */
public class ObjectEntryFolderLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.object.service.impl.ObjectEntryFolderLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the object entry folder to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryFolder the object entry folder
	 * @return the object entry folder that was added
	 */
	public static ObjectEntryFolder addObjectEntryFolder(
		ObjectEntryFolder objectEntryFolder) {

		return getService().addObjectEntryFolder(objectEntryFolder);
	}

	public static ObjectEntryFolder addObjectEntryFolder(
			String externalReferenceCode, long groupId, long userId,
			long parentObjectEntryFolderId, String description,
			Map<java.util.Locale, String> labelMap, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addObjectEntryFolder(
			externalReferenceCode, groupId, userId, parentObjectEntryFolderId,
			description, labelMap, name, serviceContext);
	}

	/**
	 * Creates a new object entry folder with the primary key. Does not add the object entry folder to the database.
	 *
	 * @param objectEntryFolderId the primary key for the new object entry folder
	 * @return the new object entry folder
	 */
	public static ObjectEntryFolder createObjectEntryFolder(
		long objectEntryFolderId) {

		return getService().createObjectEntryFolder(objectEntryFolderId);
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
	 * Deletes the object entry folder with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder that was removed
	 * @throws PortalException if a object entry folder with the primary key could not be found
	 */
	public static ObjectEntryFolder deleteObjectEntryFolder(
			long objectEntryFolderId)
		throws PortalException {

		return getService().deleteObjectEntryFolder(objectEntryFolderId);
	}

	/**
	 * Deletes the object entry folder from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryFolder the object entry folder
	 * @return the object entry folder that was removed
	 * @throws PortalException
	 */
	public static ObjectEntryFolder deleteObjectEntryFolder(
			ObjectEntryFolder objectEntryFolder)
		throws PortalException {

		return getService().deleteObjectEntryFolder(objectEntryFolder);
	}

	public static ObjectEntryFolder
			deleteObjectEntryFolderByExternalReferenceCode(
				String externalReferenceCode, long groupId, long companyId)
		throws PortalException {

		return getService().deleteObjectEntryFolderByExternalReferenceCode(
			externalReferenceCode, groupId, companyId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
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

	public static ObjectEntryFolder fetchObjectEntryFolder(
		long objectEntryFolderId) {

		return getService().fetchObjectEntryFolder(objectEntryFolderId);
	}

	public static ObjectEntryFolder
		fetchObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId) {

		return getService().fetchObjectEntryFolderByExternalReferenceCode(
			externalReferenceCode, groupId, companyId);
	}

	/**
	 * Returns the object entry folder matching the UUID and group.
	 *
	 * @param uuid the object entry folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder fetchObjectEntryFolderByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchObjectEntryFolderByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object entry folder with the primary key.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder
	 * @throws PortalException if a object entry folder with the primary key could not be found
	 */
	public static ObjectEntryFolder getObjectEntryFolder(
			long objectEntryFolderId)
		throws PortalException {

		return getService().getObjectEntryFolder(objectEntryFolderId);
	}

	public static ObjectEntryFolder getObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId)
		throws PortalException {

		return getService().getObjectEntryFolderByExternalReferenceCode(
			externalReferenceCode, groupId, companyId);
	}

	/**
	 * Returns the object entry folder matching the UUID and group.
	 *
	 * @param uuid the object entry folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry folder
	 * @throws PortalException if a matching object entry folder could not be found
	 */
	public static ObjectEntryFolder getObjectEntryFolderByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getObjectEntryFolderByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns a range of all the object entry folders.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectEntryFolderModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @return the range of object entry folders
	 */
	public static List<ObjectEntryFolder> getObjectEntryFolders(
		int start, int end) {

		return getService().getObjectEntryFolders(start, end);
	}

	public static List<ObjectEntryFolder> getObjectEntryFolders(
		long groupId, long companyId, long parentObjectEntryFolderId, int start,
		int end) {

		return getService().getObjectEntryFolders(
			groupId, companyId, parentObjectEntryFolderId, start, end);
	}

	public static List<ObjectEntryFolder>
		getObjectEntryFoldersByExternalReferenceCode(
			String externalReferenceCode, List<Long> groupIds, long companyId) {

		return getService().getObjectEntryFoldersByExternalReferenceCode(
			externalReferenceCode, groupIds, companyId);
	}

	/**
	 * Returns all the object entry folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entry folders
	 * @param companyId the primary key of the company
	 * @return the matching object entry folders, or an empty list if no matches were found
	 */
	public static List<ObjectEntryFolder>
		getObjectEntryFoldersByUuidAndCompanyId(String uuid, long companyId) {

		return getService().getObjectEntryFoldersByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of object entry folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entry folders
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of object entry folders
	 * @param end the upper bound of the range of object entry folders (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching object entry folders, or an empty list if no matches were found
	 */
	public static List<ObjectEntryFolder>
		getObjectEntryFoldersByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			OrderByComparator<ObjectEntryFolder> orderByComparator) {

		return getService().getObjectEntryFoldersByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of object entry folders.
	 *
	 * @return the number of object entry folders
	 */
	public static int getObjectEntryFoldersCount() {
		return getService().getObjectEntryFoldersCount();
	}

	public static int getObjectEntryFoldersCount(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return getService().getObjectEntryFoldersCount(
			groupId, companyId, parentObjectEntryFolderId);
	}

	public static ObjectEntryFolder getOrAddEmptyObjectEntryFolder(
			String externalReferenceCode, long groupId, long companyId,
			long userId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().getOrAddEmptyObjectEntryFolder(
			externalReferenceCode, groupId, companyId, userId, serviceContext);
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

	public static void moveObjectEntryFoldersToTrash(
			long userId, ObjectEntryFolder parentObjectEntryFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().moveObjectEntryFoldersToTrash(
			userId, parentObjectEntryFolder, serviceContext);
	}

	public static ObjectEntryFolder moveObjectEntryFolderToTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().moveObjectEntryFolderToTrash(
			userId, objectEntryFolder, serviceContext);
	}

	public static ObjectEntryFolder restoreObjectEntryFolderFromTrash(
			long userId, ObjectEntryFolder objectEntryFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().restoreObjectEntryFolderFromTrash(
			userId, objectEntryFolder, serviceContext);
	}

	public static void restoreObjectEntryFoldersFromTrash(
			long userId, ObjectEntryFolder parentObjectEntryFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		getService().restoreObjectEntryFoldersFromTrash(
			userId, parentObjectEntryFolder, serviceContext);
	}

	public static void subscribeObjectEntryFolder(
			long userId, long groupId, long objectEntryFolderId)
		throws PortalException {

		getService().subscribeObjectEntryFolder(
			userId, groupId, objectEntryFolderId);
	}

	public static void unsubscribeObjectEntryFolder(
			long userId, long groupId, long objectEntryFolderId)
		throws PortalException {

		getService().unsubscribeObjectEntryFolder(
			userId, groupId, objectEntryFolderId);
	}

	public static ObjectEntryFolder updateObjectEntryFolder(
			long userId, long objectEntryFolderId,
			long parentObjectEntryFolderId, String description,
			Map<java.util.Locale, String> labelMap, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateObjectEntryFolder(
			userId, objectEntryFolderId, parentObjectEntryFolderId, description,
			labelMap, name, serviceContext);
	}

	/**
	 * Updates the object entry folder in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectEntryFolderLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectEntryFolder the object entry folder
	 * @return the object entry folder that was updated
	 */
	public static ObjectEntryFolder updateObjectEntryFolder(
		ObjectEntryFolder objectEntryFolder) {

		return getService().updateObjectEntryFolder(objectEntryFolder);
	}

	public static ObjectEntryFolder updateStatus(
			ObjectEntryFolder objectEntryFolder, int status)
		throws PortalException {

		return getService().updateStatus(objectEntryFolder, status);
	}

	public static ObjectEntryFolderLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<ObjectEntryFolderLocalService>
		_serviceSnapshot = new Snapshot<>(
			ObjectEntryFolderLocalServiceUtil.class,
			ObjectEntryFolderLocalService.class);

}