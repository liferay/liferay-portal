/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectEntryFolderLocalService}.
 *
 * @author Marco Leo
 * @see ObjectEntryFolderLocalService
 * @generated
 */
public class ObjectEntryFolderLocalServiceWrapper
	implements ObjectEntryFolderLocalService,
			   ServiceWrapper<ObjectEntryFolderLocalService> {

	public ObjectEntryFolderLocalServiceWrapper() {
		this(null);
	}

	public ObjectEntryFolderLocalServiceWrapper(
		ObjectEntryFolderLocalService objectEntryFolderLocalService) {

		_objectEntryFolderLocalService = objectEntryFolderLocalService;
	}

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
	@Override
	public com.liferay.object.model.ObjectEntryFolder addObjectEntryFolder(
		com.liferay.object.model.ObjectEntryFolder objectEntryFolder) {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			objectEntryFolder);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder addObjectEntryFolder(
			String externalReferenceCode, long groupId, long userId,
			long parentObjectEntryFolderId, String description,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.addObjectEntryFolder(
			externalReferenceCode, groupId, userId, parentObjectEntryFolderId,
			description, labelMap, name, serviceContext);
	}

	/**
	 * Creates a new object entry folder with the primary key. Does not add the object entry folder to the database.
	 *
	 * @param objectEntryFolderId the primary key for the new object entry folder
	 * @return the new object entry folder
	 */
	@Override
	public com.liferay.object.model.ObjectEntryFolder createObjectEntryFolder(
		long objectEntryFolderId) {

		return _objectEntryFolderLocalService.createObjectEntryFolder(
			objectEntryFolderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.createPersistedModel(
			primaryKeyObj);
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
	@Override
	public com.liferay.object.model.ObjectEntryFolder deleteObjectEntryFolder(
			long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolderId);
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
	@Override
	public com.liferay.object.model.ObjectEntryFolder deleteObjectEntryFolder(
			com.liferay.object.model.ObjectEntryFolder objectEntryFolder)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.deleteObjectEntryFolder(
			objectEntryFolder);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			deleteObjectEntryFolderByExternalReferenceCode(
				String externalReferenceCode, long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.
			deleteObjectEntryFolderByExternalReferenceCode(
				externalReferenceCode, groupId, companyId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectEntryFolderLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectEntryFolderLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectEntryFolderLocalService.dynamicQuery();
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

		return _objectEntryFolderLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _objectEntryFolderLocalService.dynamicQuery(
			dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _objectEntryFolderLocalService.dynamicQuery(
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

		return _objectEntryFolderLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectEntryFolderLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder fetchObjectEntryFolder(
		long objectEntryFolderId) {

		return _objectEntryFolderLocalService.fetchObjectEntryFolder(
			objectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
		fetchObjectEntryFolderByExternalReferenceCode(
			String externalReferenceCode, long groupId, long companyId) {

		return _objectEntryFolderLocalService.
			fetchObjectEntryFolderByExternalReferenceCode(
				externalReferenceCode, groupId, companyId);
	}

	/**
	 * Returns the object entry folder matching the UUID and group.
	 *
	 * @param uuid the object entry folder's UUID
	 * @param groupId the primary key of the group
	 * @return the matching object entry folder, or <code>null</code> if a matching object entry folder could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntryFolder
		fetchObjectEntryFolderByUuidAndGroupId(String uuid, long groupId) {

		return _objectEntryFolderLocalService.
			fetchObjectEntryFolderByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectEntryFolderLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectEntryFolderLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectEntryFolderLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object entry folder with the primary key.
	 *
	 * @param objectEntryFolderId the primary key of the object entry folder
	 * @return the object entry folder
	 * @throws PortalException if a object entry folder with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectEntryFolder getObjectEntryFolder(
			long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.getObjectEntryFolder(
			objectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			getObjectEntryFolderByExternalReferenceCode(
				String externalReferenceCode, long groupId, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.
			getObjectEntryFolderByExternalReferenceCode(
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
	@Override
	public com.liferay.object.model.ObjectEntryFolder
			getObjectEntryFolderByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.
			getObjectEntryFolderByUuidAndGroupId(uuid, groupId);
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
	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryFolder>
		getObjectEntryFolders(int start, int end) {

		return _objectEntryFolderLocalService.getObjectEntryFolders(start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryFolder>
		getObjectEntryFolders(
			long groupId, long companyId, long parentObjectEntryFolderId,
			int start, int end) {

		return _objectEntryFolderLocalService.getObjectEntryFolders(
			groupId, companyId, parentObjectEntryFolderId, start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryFolder>
		getObjectEntryFoldersByExternalReferenceCode(
			String externalReferenceCode, java.util.List<Long> groupIds,
			long companyId) {

		return _objectEntryFolderLocalService.
			getObjectEntryFoldersByExternalReferenceCode(
				externalReferenceCode, groupIds, companyId);
	}

	/**
	 * Returns all the object entry folders matching the UUID and company.
	 *
	 * @param uuid the UUID of the object entry folders
	 * @param companyId the primary key of the company
	 * @return the matching object entry folders, or an empty list if no matches were found
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryFolder>
		getObjectEntryFoldersByUuidAndCompanyId(String uuid, long companyId) {

		return _objectEntryFolderLocalService.
			getObjectEntryFoldersByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.object.model.ObjectEntryFolder>
		getObjectEntryFoldersByUuidAndCompanyId(
			String uuid, long companyId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.object.model.ObjectEntryFolder>
					orderByComparator) {

		return _objectEntryFolderLocalService.
			getObjectEntryFoldersByUuidAndCompanyId(
				uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of object entry folders.
	 *
	 * @return the number of object entry folders
	 */
	@Override
	public int getObjectEntryFoldersCount() {
		return _objectEntryFolderLocalService.getObjectEntryFoldersCount();
	}

	@Override
	public int getObjectEntryFoldersCount(
		long groupId, long companyId, long parentObjectEntryFolderId) {

		return _objectEntryFolderLocalService.getObjectEntryFoldersCount(
			groupId, companyId, parentObjectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			getOrAddEmptyObjectEntryFolder(
				String externalReferenceCode, long groupId, long companyId,
				long userId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.getOrAddEmptyObjectEntryFolder(
			externalReferenceCode, groupId, companyId, userId, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectEntryFolderLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void moveObjectEntryFoldersToTrash(
			long userId,
			com.liferay.object.model.ObjectEntryFolder parentObjectEntryFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryFolderLocalService.moveObjectEntryFoldersToTrash(
			userId, parentObjectEntryFolder, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			moveObjectEntryFolderToTrash(
				long userId,
				com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.moveObjectEntryFolderToTrash(
			userId, objectEntryFolder, serviceContext);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder
			restoreObjectEntryFolderFromTrash(
				long userId,
				com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.restoreObjectEntryFolderFromTrash(
			userId, objectEntryFolder, serviceContext);
	}

	@Override
	public void restoreObjectEntryFoldersFromTrash(
			long userId,
			com.liferay.object.model.ObjectEntryFolder parentObjectEntryFolder,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryFolderLocalService.restoreObjectEntryFoldersFromTrash(
			userId, parentObjectEntryFolder, serviceContext);
	}

	@Override
	public void subscribeObjectEntryFolder(
			long userId, long groupId, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryFolderLocalService.subscribeObjectEntryFolder(
			userId, groupId, objectEntryFolderId);
	}

	@Override
	public void unsubscribeObjectEntryFolder(
			long userId, long groupId, long objectEntryFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectEntryFolderLocalService.unsubscribeObjectEntryFolder(
			userId, groupId, objectEntryFolderId);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder updateObjectEntryFolder(
			long userId, long objectEntryFolderId,
			long parentObjectEntryFolderId, String description,
			java.util.Map<java.util.Locale, String> labelMap, String name,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.updateObjectEntryFolder(
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
	@Override
	public com.liferay.object.model.ObjectEntryFolder updateObjectEntryFolder(
		com.liferay.object.model.ObjectEntryFolder objectEntryFolder) {

		return _objectEntryFolderLocalService.updateObjectEntryFolder(
			objectEntryFolder);
	}

	@Override
	public com.liferay.object.model.ObjectEntryFolder updateStatus(
			com.liferay.object.model.ObjectEntryFolder objectEntryFolder,
			int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectEntryFolderLocalService.updateStatus(
			objectEntryFolder, status);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectEntryFolderLocalService.getBasePersistence();
	}

	@Override
	public ObjectEntryFolderLocalService getWrappedService() {
		return _objectEntryFolderLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectEntryFolderLocalService objectEntryFolderLocalService) {

		_objectEntryFolderLocalService = objectEntryFolderLocalService;
	}

	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}