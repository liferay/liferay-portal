/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectFilterLocalService}.
 *
 * @author Marco Leo
 * @see ObjectFilterLocalService
 * @generated
 */
public class ObjectFilterLocalServiceWrapper
	implements ObjectFilterLocalService,
			   ServiceWrapper<ObjectFilterLocalService> {

	public ObjectFilterLocalServiceWrapper() {
		this(null);
	}

	public ObjectFilterLocalServiceWrapper(
		ObjectFilterLocalService objectFilterLocalService) {

		_objectFilterLocalService = objectFilterLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectFilter addObjectFilter(
			long userId, long objectFieldId, String filterBy, String filterType,
			String json)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFilterLocalService.addObjectFilter(
			userId, objectFieldId, filterBy, filterType, json);
	}

	/**
	 * Adds the object filter to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFilterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFilter the object filter
	 * @return the object filter that was added
	 */
	@Override
	public com.liferay.object.model.ObjectFilter addObjectFilter(
		com.liferay.object.model.ObjectFilter objectFilter) {

		return _objectFilterLocalService.addObjectFilter(objectFilter);
	}

	/**
	 * Creates a new object filter with the primary key. Does not add the object filter to the database.
	 *
	 * @param objectFilterId the primary key for the new object filter
	 * @return the new object filter
	 */
	@Override
	public com.liferay.object.model.ObjectFilter createObjectFilter(
		long objectFilterId) {

		return _objectFilterLocalService.createObjectFilter(objectFilterId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFilterLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteObjectFieldObjectFilter(long objectFieldId) {
		_objectFilterLocalService.deleteObjectFieldObjectFilter(objectFieldId);
	}

	/**
	 * Deletes the object filter with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFilterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFilterId the primary key of the object filter
	 * @return the object filter that was removed
	 * @throws PortalException if a object filter with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFilter deleteObjectFilter(
			long objectFilterId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFilterLocalService.deleteObjectFilter(objectFilterId);
	}

	/**
	 * Deletes the object filter from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFilterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFilter the object filter
	 * @return the object filter that was removed
	 */
	@Override
	public com.liferay.object.model.ObjectFilter deleteObjectFilter(
		com.liferay.object.model.ObjectFilter objectFilter) {

		return _objectFilterLocalService.deleteObjectFilter(objectFilter);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFilterLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectFilterLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectFilterLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectFilterLocalService.dynamicQuery();
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

		return _objectFilterLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFilterModelImpl</code>.
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

		return _objectFilterLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFilterModelImpl</code>.
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

		return _objectFilterLocalService.dynamicQuery(
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

		return _objectFilterLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectFilterLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectFilter fetchObjectFilter(
		long objectFilterId) {

		return _objectFilterLocalService.fetchObjectFilter(objectFilterId);
	}

	/**
	 * Returns the object filter with the matching UUID and company.
	 *
	 * @param uuid the object filter's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object filter, or <code>null</code> if a matching object filter could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFilter
		fetchObjectFilterByUuidAndCompanyId(String uuid, long companyId) {

		return _objectFilterLocalService.fetchObjectFilterByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectFilterLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectFilterLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectFilterLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object filter with the primary key.
	 *
	 * @param objectFilterId the primary key of the object filter
	 * @return the object filter
	 * @throws PortalException if a object filter with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFilter getObjectFilter(
			long objectFilterId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFilterLocalService.getObjectFilter(objectFilterId);
	}

	/**
	 * Returns the object filter with the matching UUID and company.
	 *
	 * @param uuid the object filter's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object filter
	 * @throws PortalException if a matching object filter could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectFilter
			getObjectFilterByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFilterLocalService.getObjectFilterByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object filters.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectFilterModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object filters
	 * @param end the upper bound of the range of object filters (not inclusive)
	 * @return the range of object filters
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectFilter>
		getObjectFilters(int start, int end) {

		return _objectFilterLocalService.getObjectFilters(start, end);
	}

	/**
	 * Returns the number of object filters.
	 *
	 * @return the number of object filters
	 */
	@Override
	public int getObjectFiltersCount() {
		return _objectFilterLocalService.getObjectFiltersCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectFilterLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectFilterLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the object filter in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectFilterLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectFilter the object filter
	 * @return the object filter that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectFilter updateObjectFilter(
		com.liferay.object.model.ObjectFilter objectFilter) {

		return _objectFilterLocalService.updateObjectFilter(objectFilter);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectFilterLocalService.getBasePersistence();
	}

	@Override
	public ObjectFilterLocalService getWrappedService() {
		return _objectFilterLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectFilterLocalService objectFilterLocalService) {

		_objectFilterLocalService = objectFilterLocalService;
	}

	private ObjectFilterLocalService _objectFilterLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1256786615