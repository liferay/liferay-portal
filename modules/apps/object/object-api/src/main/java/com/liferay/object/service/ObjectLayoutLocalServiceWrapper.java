/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectLayoutLocalService}.
 *
 * @author Marco Leo
 * @see ObjectLayoutLocalService
 * @generated
 */
public class ObjectLayoutLocalServiceWrapper
	implements ObjectLayoutLocalService,
			   ServiceWrapper<ObjectLayoutLocalService> {

	public ObjectLayoutLocalServiceWrapper() {
		this(null);
	}

	public ObjectLayoutLocalServiceWrapper(
		ObjectLayoutLocalService objectLayoutLocalService) {

		_objectLayoutLocalService = objectLayoutLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectLayout addObjectLayout(
			long userId, long objectDefinitionId, boolean defaultObjectLayout,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.List<com.liferay.object.model.ObjectLayoutTab>
				objectLayoutTabs)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.addObjectLayout(
			userId, objectDefinitionId, defaultObjectLayout, nameMap,
			objectLayoutTabs);
	}

	/**
	 * Adds the object layout to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayout the object layout
	 * @return the object layout that was added
	 */
	@Override
	public com.liferay.object.model.ObjectLayout addObjectLayout(
		com.liferay.object.model.ObjectLayout objectLayout) {

		return _objectLayoutLocalService.addObjectLayout(objectLayout);
	}

	/**
	 * Creates a new object layout with the primary key. Does not add the object layout to the database.
	 *
	 * @param objectLayoutId the primary key for the new object layout
	 * @return the new object layout
	 */
	@Override
	public com.liferay.object.model.ObjectLayout createObjectLayout(
		long objectLayoutId) {

		return _objectLayoutLocalService.createObjectLayout(objectLayoutId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the object layout with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout that was removed
	 * @throws PortalException if a object layout with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectLayout deleteObjectLayout(
			long objectLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.deleteObjectLayout(objectLayoutId);
	}

	/**
	 * Deletes the object layout from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayout the object layout
	 * @return the object layout that was removed
	 * @throws PortalException
	 */
	@Override
	public com.liferay.object.model.ObjectLayout deleteObjectLayout(
			com.liferay.object.model.ObjectLayout objectLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.deleteObjectLayout(objectLayout);
	}

	@Override
	public void deleteObjectLayouts(long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectLayoutLocalService.deleteObjectLayouts(objectDefinitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectLayoutLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectLayoutLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectLayoutLocalService.dynamicQuery();
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

		return _objectLayoutLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutModelImpl</code>.
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

		return _objectLayoutLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutModelImpl</code>.
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

		return _objectLayoutLocalService.dynamicQuery(
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

		return _objectLayoutLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectLayoutLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectLayout fetchDefaultObjectLayout(
		long objectDefinitionId) {

		return _objectLayoutLocalService.fetchDefaultObjectLayout(
			objectDefinitionId);
	}

	@Override
	public com.liferay.object.model.ObjectLayout fetchObjectLayout(
		long objectLayoutId) {

		return _objectLayoutLocalService.fetchObjectLayout(objectLayoutId);
	}

	/**
	 * Returns the object layout with the matching UUID and company.
	 *
	 * @param uuid the object layout's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout, or <code>null</code> if a matching object layout could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectLayout
		fetchObjectLayoutByUuidAndCompanyId(String uuid, long companyId) {

		return _objectLayoutLocalService.fetchObjectLayoutByUuidAndCompanyId(
			uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectLayoutLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.object.model.ObjectLayout getDefaultObjectLayout(
			long objectDefinitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.getDefaultObjectLayout(
			objectDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectLayout>
		getDefaultObjectLayouts(long companyId) {

		return _objectLayoutLocalService.getDefaultObjectLayouts(companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectLayoutLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectLayoutLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the object layout with the primary key.
	 *
	 * @param objectLayoutId the primary key of the object layout
	 * @return the object layout
	 * @throws PortalException if a object layout with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectLayout getObjectLayout(
			long objectLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.getObjectLayout(objectLayoutId);
	}

	/**
	 * Returns the object layout with the matching UUID and company.
	 *
	 * @param uuid the object layout's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout
	 * @throws PortalException if a matching object layout could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectLayout
			getObjectLayoutByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.getObjectLayoutByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of all the object layouts.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layouts
	 * @param end the upper bound of the range of object layouts (not inclusive)
	 * @return the range of object layouts
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectLayout>
		getObjectLayouts(int start, int end) {

		return _objectLayoutLocalService.getObjectLayouts(start, end);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectLayout>
		getObjectLayouts(long objectDefinitionId) {

		return _objectLayoutLocalService.getObjectLayouts(objectDefinitionId);
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectLayout>
		getObjectLayouts(long objectDefinitionId, int start, int end) {

		return _objectLayoutLocalService.getObjectLayouts(
			objectDefinitionId, start, end);
	}

	/**
	 * Returns the number of object layouts.
	 *
	 * @return the number of object layouts
	 */
	@Override
	public int getObjectLayoutsCount() {
		return _objectLayoutLocalService.getObjectLayoutsCount();
	}

	@Override
	public int getObjectLayoutsCount(long objectDefinitionId) {
		return _objectLayoutLocalService.getObjectLayoutsCount(
			objectDefinitionId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectLayoutLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.object.model.ObjectLayout updateObjectLayout(
			long objectLayoutId, boolean defaultObjectLayout,
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.List<com.liferay.object.model.ObjectLayoutTab>
				objectLayoutTabs)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutLocalService.updateObjectLayout(
			objectLayoutId, defaultObjectLayout, nameMap, objectLayoutTabs);
	}

	/**
	 * Updates the object layout in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayout the object layout
	 * @return the object layout that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectLayout updateObjectLayout(
		com.liferay.object.model.ObjectLayout objectLayout) {

		return _objectLayoutLocalService.updateObjectLayout(objectLayout);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectLayoutLocalService.getBasePersistence();
	}

	@Override
	public ObjectLayoutLocalService getWrappedService() {
		return _objectLayoutLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectLayoutLocalService objectLayoutLocalService) {

		_objectLayoutLocalService = objectLayoutLocalService;
	}

	private ObjectLayoutLocalService _objectLayoutLocalService;

}