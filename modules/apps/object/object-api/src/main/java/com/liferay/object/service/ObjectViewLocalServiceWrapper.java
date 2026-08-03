/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectViewLocalService}.
 *
 * @author Marco Leo
 * @see ObjectViewLocalService
 * @generated
 */
public class ObjectViewLocalServiceWrapper
	implements ObjectViewLocalService, ServiceWrapper<ObjectViewLocalService> {

	public ObjectViewLocalServiceWrapper() {
		this(null);
	}

	public ObjectViewLocalServiceWrapper(
		ObjectViewLocalService objectViewLocalService) {

		_objectViewLocalService = objectViewLocalService;
	}

	/**
	 * Adds the object view to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectView the object view
	 * @return the object view that was added
	 */
	@Override
	public com.liferay.object.model.ObjectView addObjectView(
		com.liferay.object.model.ObjectView objectView) {

		return _objectViewLocalService.addObjectView(objectView);
	}

	@Override
	public com.liferay.object.model.ObjectView addObjectView(
			String externalReferenceCode, long userId, long objectDefinitionId,
			boolean defaultObjectView,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.List<com.liferay.object.model.ObjectViewColumn>
				objectViewColumns,
			java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
				objectViewFilterColumns,
			java.util.List<com.liferay.object.model.ObjectViewSortColumn>
				objectViewSortColumns)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewLocalService.addObjectView(
			externalReferenceCode, userId, objectDefinitionId,
			defaultObjectView, nameMap, objectViewColumns,
			objectViewFilterColumns, objectViewSortColumns);
	}

	/**
	 * Creates a new object view with the primary key. Does not add the object view to the database.
	 *
	 * @param objectViewId the primary key for the new object view
	 * @return the new object view
	 */
	@Override
	public com.liferay.object.model.ObjectView createObjectView(
		long objectViewId) {

		return _objectViewLocalService.createObjectView(objectViewId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the object view with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectViewId the primary key of the object view
	 * @return the object view that was removed
	 * @throws PortalException if a object view with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectView deleteObjectView(
			long objectViewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewLocalService.deleteObjectView(objectViewId);
	}

	/**
	 * Deletes the object view from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectView the object view
	 * @return the object view that was removed
	 */
	@Override
	public com.liferay.object.model.ObjectView deleteObjectView(
		com.liferay.object.model.ObjectView objectView) {

		return _objectViewLocalService.deleteObjectView(objectView);
	}

	@Override
	public void deleteObjectViews(long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectViewLocalService.deleteObjectViews(objectDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectViewLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectViewLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectViewLocalService.dynamicQuery();
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

		return _objectViewLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewModelImpl</code>.
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

		return _objectViewLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewModelImpl</code>.
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

		return _objectViewLocalService.dynamicQuery(
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

		return _objectViewLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectViewLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectView fetchDefaultObjectView(
		long objectDefinitionId) {

		return _objectViewLocalService.fetchDefaultObjectView(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectView fetchObjectView(
		long objectViewId) {

		return _objectViewLocalService.fetchObjectView(objectViewId);
	}

	@Override
	public com.liferay.object.model.ObjectView fetchObjectView(
		String externalReferenceCode, long objectDefinitionId) {

		return _objectViewLocalService.fetchObjectView(
			externalReferenceCode, objectDefinitionId);
	}

	/**
	 * Returns the object view with the matching UUID and company.
	 *
	 * @param uuid the object view's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object view, or <code>null</code> if a matching object view could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectView
		fetchObjectViewByUuidAndCompanyId(String uuid, long companyId) {

		return _objectViewLocalService.fetchObjectViewByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectViewLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectViewLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectViewLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object view with the primary key.
	 *
	 * @param objectViewId the primary key of the object view
	 * @return the object view
	 * @throws PortalException if a object view with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectView getObjectView(long objectViewId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewLocalService.getObjectView(objectViewId);
	}

	/**
	 * Returns the object view with the matching UUID and company.
	 *
	 * @param uuid the object view's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object view
	 * @throws PortalException if a matching object view could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectView getObjectViewByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewLocalService.getObjectViewByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object views.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectViewModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object views
	 * @param end the upper bound of the range of object views (not inclusive)
	 * @return the range of object views
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectView> getObjectViews(
		int start, int end) {

		return _objectViewLocalService.getObjectViews(start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectView> getObjectViews(
		long objectDefinitionId) {

		return _objectViewLocalService.getObjectViews(objectDefinitionId);
	}

	/**
	 * Returns the number of object views.
	 *
	 * @return the number of object views
	 */
	@Override
	public int getObjectViewsCount() {
		return _objectViewLocalService.getObjectViewsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectViewLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void unassociateObjectField(
		com.liferay.object.model.ObjectField objectField) {

		_objectViewLocalService.unassociateObjectField(objectField);
	}

	/**
	 * Updates the object view in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectViewLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectView the object view
	 * @return the object view that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectView updateObjectView(
		com.liferay.object.model.ObjectView objectView) {

		return _objectViewLocalService.updateObjectView(objectView);
	}

	@Override
	public com.liferay.object.model.ObjectView updateObjectView(
			String externalReferenceCode, long objectViewId,
			boolean defaultObjectView,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.List<com.liferay.object.model.ObjectViewColumn>
				objectViewColumns,
			java.util.List<com.liferay.object.model.ObjectViewFilterColumn>
				objectViewFilterColumns,
			java.util.List<com.liferay.object.model.ObjectViewSortColumn>
				objectViewSortColumns)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectViewLocalService.updateObjectView(
			externalReferenceCode, objectViewId, defaultObjectView, nameMap,
			objectViewColumns, objectViewFilterColumns, objectViewSortColumns);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectViewLocalService.getBasePersistence();
	}

	@Override
	public ObjectViewLocalService getWrappedService() {
		return _objectViewLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectViewLocalService objectViewLocalService) {

		_objectViewLocalService = objectViewLocalService;
	}

	private ObjectViewLocalService _objectViewLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-185423022