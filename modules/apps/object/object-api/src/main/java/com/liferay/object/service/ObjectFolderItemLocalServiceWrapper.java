/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectFolderItemLocalService}.
 *
 * @author Marco Leo
 * @see ObjectFolderItemLocalService
 * @generated
 */
public class ObjectFolderItemLocalServiceWrapper
	implements ObjectFolderItemLocalService,
			   ServiceWrapper<ObjectFolderItemLocalService> {

	public ObjectFolderItemLocalServiceWrapper() {
		this(null);
	}

	public ObjectFolderItemLocalServiceWrapper(
		ObjectFolderItemLocalService objectFolderItemLocalService) {

		_objectFolderItemLocalService = objectFolderItemLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectFolderItem addObjectFolderItem(
			long userId, long objectDefinitionId, long objectFolderId,
			int positionX, int positionY)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.addObjectFolderItem(
			userId, objectDefinitionId, objectFolderId, positionX, positionY);
	}

	/**
	 * Adds the object folder item to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderItem the object folder item
	 * @return the object folder item that was added
	 */
	@Override
	public com.liferay.object.model.ObjectFolderItem addObjectFolderItem(
		com.liferay.object.model.ObjectFolderItem objectFolderItem) {

		return _objectFolderItemLocalService.addObjectFolderItem(
			objectFolderItem);
	}

	/**
	 * Creates a new object folder item with the primary key. Does not add the object folder item to the database.
	 *
	 * @param objectFolderItemId the primary key for the new object folder item
	 * @return the new object folder item
	 */
	@Override
	public com.liferay.object.model.ObjectFolderItem createObjectFolderItem(
		long objectFolderItemId) {

		return _objectFolderItemLocalService.createObjectFolderItem(
			objectFolderItemId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the object folder item with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderItemId the primary key of the object folder item
	 * @return the object folder item that was removed
	 * @throws PortalException if a object folder item with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFolderItem deleteObjectFolderItem(
			long objectFolderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.deleteObjectFolderItem(
			objectFolderItemId);
	}

	@Override
	public com.liferay.object.model.ObjectFolderItem deleteObjectFolderItem(
			long objectDefinitionId, long objectFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.deleteObjectFolderItem(
			objectDefinitionId, objectFolderId);
	}

	/**
	 * Deletes the object folder item from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderItem the object folder item
	 * @return the object folder item that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.object.model.ObjectFolderItem deleteObjectFolderItem(
			com.liferay.object.model.ObjectFolderItem objectFolderItem)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.deleteObjectFolderItem(
			objectFolderItem);
	}

	@Override
	public void deleteObjectFolderItemByObjectDefinitionId(
		long objectDefinitionId) {

		_objectFolderItemLocalService.
			deleteObjectFolderItemByObjectDefinitionId(objectDefinitionId);
	}

	@Override
	public void deleteObjectFolderItemByObjectFolderId(long objectFolderId) {
		_objectFolderItemLocalService.deleteObjectFolderItemByObjectFolderId(
			objectFolderId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectFolderItemLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectFolderItemLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectFolderItemLocalService.dynamicQuery();
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

		return _objectFolderItemLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderItemModelImpl</code>.
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

		return _objectFolderItemLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderItemModelImpl</code>.
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

		return _objectFolderItemLocalService.dynamicQuery(
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

		return _objectFolderItemLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectFolderItemLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectFolderItem fetchObjectFolderItem(
		long objectFolderItemId) {

		return _objectFolderItemLocalService.fetchObjectFolderItem(
			objectFolderItemId);
	}

	/**
	 * Returns the object folder item with the matching UUID and company.
	 *
	 * @param uuid the object folder item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object folder item, or <code>null</code> if a matching object folder item could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFolderItem
		fetchObjectFolderItemByUuidAndCompanyId(String uuid, long companyId) {

		return _objectFolderItemLocalService.
			fetchObjectFolderItemByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectFolderItemLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectFolderItemLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectFolderItemLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object folder item with the primary key.
	 *
	 * @param objectFolderItemId the primary key of the object folder item
	 * @return the object folder item
	 * @throws PortalException if a object folder item with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFolderItem getObjectFolderItem(
			long objectFolderItemId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.getObjectFolderItem(
			objectFolderItemId);
	}

	/**
	 * Returns the object folder item with the matching UUID and company.
	 *
	 * @param uuid the object folder item's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object folder item
	 * @throws PortalException if a matching object folder item could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFolderItem
			getObjectFolderItemByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.
			getObjectFolderItemByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object folder items.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFolderItemModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object folder items
	 * @param end the upper bound of the range of object folder items (not inclusive)
	 * @return the range of object folder items
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectFolderItem>
		getObjectFolderItems(int start, int end) {

		return _objectFolderItemLocalService.getObjectFolderItems(start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectFolderItem>
		getObjectFolderItemsByObjectFolderId(long objectFolderId) {

		return _objectFolderItemLocalService.
			getObjectFolderItemsByObjectFolderId(objectFolderId);
	}

	/**
	 * Returns the number of object folder items.
	 *
	 * @return the number of object folder items
	 */
	@Override
	public int getObjectFolderItemsCount() {
		return _objectFolderItemLocalService.getObjectFolderItemsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectFolderItemLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.object.model.ObjectFolderItem updateObjectFolderItem(
			long objectDefinitionId, long objectFolderId, int positionX,
			int positionY)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFolderItemLocalService.updateObjectFolderItem(
			objectDefinitionId, objectFolderId, positionX, positionY);
	}

	/**
	 * Updates the object folder item in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFolderItemLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFolderItem the object folder item
	 * @return the object folder item that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectFolderItem updateObjectFolderItem(
		com.liferay.object.model.ObjectFolderItem objectFolderItem) {

		return _objectFolderItemLocalService.updateObjectFolderItem(
			objectFolderItem);
	}

	@Override
	public void updateObjectFolderObjectFolderItem(
			long objectDefinitionId, long newObjectFolderId,
			long oldObjectFolderId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectFolderItemLocalService.updateObjectFolderObjectFolderItem(
			objectDefinitionId, newObjectFolderId, oldObjectFolderId);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectFolderItemLocalService.getBasePersistence();
	}

	@Override
	public ObjectFolderItemLocalService getWrappedService() {
		return _objectFolderItemLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectFolderItemLocalService objectFolderItemLocalService) {

		_objectFolderItemLocalService = objectFolderItemLocalService;
	}

	private ObjectFolderItemLocalService _objectFolderItemLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-635246931