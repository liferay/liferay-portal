/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectViewFilterColumnLocalService}.
 *
 * @author Marco Leo
 * @see ObjectViewFilterColumnLocalService
 * @generated
 */
public class ObjectViewFilterColumnLocalServiceWrapper
	implements ObjectViewFilterColumnLocalService,
			   ServiceWrapper<ObjectViewFilterColumnLocalService> {

	public ObjectViewFilterColumnLocalServiceWrapper() {
		this(null);
	}

	public ObjectViewFilterColumnLocalServiceWrapper(
		ObjectViewFilterColumnLocalService objectViewFilterColumnLocalService) {

		_objectViewFilterColumnLocalService =
			objectViewFilterColumnLocalService;
	}

	/**
	 * Adds the object view filter column to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewFilterColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewFilterColumn the object view filter column
	 * @return the object view filter column that was added
	 */
	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
		addObjectViewFilterColumn(
			com.liferay.object.model.ObjectViewFilterColumn
				objectViewFilterColumn) {

		return _objectViewFilterColumnLocalService.addObjectViewFilterColumn(
			objectViewFilterColumn);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
			addObjectViewFilterColumns(
				com.liferay.portal.kernel.model.User user,
				com.liferay.object.model.ObjectView objectView,
				java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
					objectViewFilterColumns)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewFilterColumnLocalService.addObjectViewFilterColumns(
			user, objectView, objectViewFilterColumns);
	}

	/**
	 * Creates a new object view filter column with the primary key. Does not add the object view filter column to the database.
	 *
	 * @param objectViewFilterColumnId the primary key for the new object view filter column
	 * @return the new object view filter column
	 */
	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
		createObjectViewFilterColumn(long objectViewFilterColumnId) {

		return _objectViewFilterColumnLocalService.createObjectViewFilterColumn(
			objectViewFilterColumnId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewFilterColumnLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the object view filter column with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewFilterColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewFilterColumnId the primary key of the object view filter column
	 * @return the object view filter column that was removed
	 * @throws PortalException if a object view filter column with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
			deleteObjectViewFilterColumn(long objectViewFilterColumnId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewFilterColumnLocalService.deleteObjectViewFilterColumn(
			objectViewFilterColumnId);
	}

	/**
	 * Deletes the object view filter column from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewFilterColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewFilterColumn the object view filter column
	 * @return the object view filter column that was removed
	 */
	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
		deleteObjectViewFilterColumn(
			com.liferay.object.model.ObjectViewFilterColumn
				objectViewFilterColumn) {

		return _objectViewFilterColumnLocalService.deleteObjectViewFilterColumn(
			objectViewFilterColumn);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewFilterColumnLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectViewFilterColumnLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectViewFilterColumnLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectViewFilterColumnLocalService.dynamicQuery();
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

		return _objectViewFilterColumnLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewFilterColumnModelImpl</code>.
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

		return _objectViewFilterColumnLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewFilterColumnModelImpl</code>.
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

		return _objectViewFilterColumnLocalService.dynamicQuery(
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

		return _objectViewFilterColumnLocalService.dynamicQueryCount(
			dynamicQuery);
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

		return _objectViewFilterColumnLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
		fetchObjectViewFilterColumn(long objectViewFilterColumnId) {

		return _objectViewFilterColumnLocalService.fetchObjectViewFilterColumn(
			objectViewFilterColumnId);
	}

	/**
	 * Returns the object view filter column with the matching UUID and company.
	 *
	 * @param uuid the object view filter column's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object view filter column, or <code>null</code> if a matching object view filter column could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
		fetchObjectViewFilterColumnByUuidAndCompanyId(
			String uuid, long companyId) {

		return _objectViewFilterColumnLocalService.
			fetchObjectViewFilterColumnByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectViewFilterColumnLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectViewFilterColumnLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectViewFilterColumnLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object view filter column with the primary key.
	 *
	 * @param objectViewFilterColumnId the primary key of the object view filter column
	 * @return the object view filter column
	 * @throws PortalException if a object view filter column with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
			getObjectViewFilterColumn(long objectViewFilterColumnId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewFilterColumnLocalService.getObjectViewFilterColumn(
			objectViewFilterColumnId);
	}

	/**
	 * Returns the object view filter column with the matching UUID and company.
	 *
	 * @param uuid the object view filter column's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object view filter column
	 * @throws PortalException if a matching object view filter column could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
			getObjectViewFilterColumnByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewFilterColumnLocalService.
			getObjectViewFilterColumnByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object view filter columns.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewFilterColumnModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object view filter columns
	 * @param end the upper bound of the range of object view filter columns (not inclusive)
	 * @return the range of object view filter columns
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
		getObjectViewFilterColumns(int start, int end) {

		return _objectViewFilterColumnLocalService.getObjectViewFilterColumns(
			start, end);
	}

	/**
	 * Returns the number of object view filter columns.
	 *
	 * @return the number of object view filter columns
	 */
	@Override
	public int getObjectViewFilterColumnsCount() {
		return _objectViewFilterColumnLocalService.
			getObjectViewFilterColumnsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectViewFilterColumnLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewFilterColumnLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the object view filter column in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewFilterColumnLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewFilterColumn the object view filter column
	 * @return the object view filter column that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectViewFilterColumn
		updateObjectViewFilterColumn(
			com.liferay.object.model.ObjectViewFilterColumn
				objectViewFilterColumn) {

		return _objectViewFilterColumnLocalService.updateObjectViewFilterColumn(
			objectViewFilterColumn);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectViewFilterColumnLocalService.getBasePersistence();
	}

	@Override
	public ObjectViewFilterColumnLocalService getWrappedService() {
		return _objectViewFilterColumnLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectViewFilterColumnLocalService objectViewFilterColumnLocalService) {

		_objectViewFilterColumnLocalService =
			objectViewFilterColumnLocalService;
	}

	private ObjectViewFilterColumnLocalService
		_objectViewFilterColumnLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1224884773