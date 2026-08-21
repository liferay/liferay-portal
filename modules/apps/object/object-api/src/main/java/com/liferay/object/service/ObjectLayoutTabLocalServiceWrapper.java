/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectLayoutTabLocalService}.
 *
 * @author Marco Leo
 * @see ObjectLayoutTabLocalService
 * @generated
 */
public class ObjectLayoutTabLocalServiceWrapper
	implements ObjectLayoutTabLocalService,
			   ServiceWrapper<ObjectLayoutTabLocalService> {

	public ObjectLayoutTabLocalServiceWrapper() {
		this(null);
	}

	public ObjectLayoutTabLocalServiceWrapper(
		ObjectLayoutTabLocalService objectLayoutTabLocalService) {

		_objectLayoutTabLocalService = objectLayoutTabLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectLayoutTab addObjectLayoutTab(
			long userId, long objectLayoutId, long objectRelationshipId,
			java.util.Map<java.util.Locale, String> nameMap, int priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutTabLocalService.addObjectLayoutTab(
			userId, objectLayoutId, objectRelationshipId, nameMap, priority);
	}

	/**
	 * Adds the object layout tab to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutTabLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutTab the object layout tab
	 * @return the object layout tab that was added
	 */
	@Override
	public com.liferay.object.model.ObjectLayoutTab addObjectLayoutTab(
		com.liferay.object.model.ObjectLayoutTab objectLayoutTab) {

		return _objectLayoutTabLocalService.addObjectLayoutTab(objectLayoutTab);
	}

	/**
	 * Creates a new object layout tab with the primary key. Does not add the object layout tab to the database.
	 *
	 * @param objectLayoutTabId the primary key for the new object layout tab
	 * @return the new object layout tab
	 */
	@Override
	public com.liferay.object.model.ObjectLayoutTab createObjectLayoutTab(
		long objectLayoutTabId) {

		return _objectLayoutTabLocalService.createObjectLayoutTab(
			objectLayoutTabId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutTabLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteObjectLayoutObjectLayoutTabs(long objectLayoutId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectLayoutTabLocalService.deleteObjectLayoutObjectLayoutTabs(
			objectLayoutId);
	}

	/**
	 * Deletes the object layout tab with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutTabLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutTabId the primary key of the object layout tab
	 * @return the object layout tab that was removed
	 * @throws PortalException if a object layout tab with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectLayoutTab deleteObjectLayoutTab(
			long objectLayoutTabId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutTabLocalService.deleteObjectLayoutTab(
			objectLayoutTabId);
	}

	/**
	 * Deletes the object layout tab from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutTabLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutTab the object layout tab
	 * @return the object layout tab that was removed
	 */
	@Override
	public com.liferay.object.model.ObjectLayoutTab deleteObjectLayoutTab(
		com.liferay.object.model.ObjectLayoutTab objectLayoutTab) {

		return _objectLayoutTabLocalService.deleteObjectLayoutTab(
			objectLayoutTab);
	}

	@Override
	public void deleteObjectRelationshipObjectLayoutTabs(
			long objectRelationshipId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectLayoutTabLocalService.deleteObjectRelationshipObjectLayoutTabs(
			objectRelationshipId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutTabLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectLayoutTabLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectLayoutTabLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectLayoutTabLocalService.dynamicQuery();
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

		return _objectLayoutTabLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutTabModelImpl</code>.
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

		return _objectLayoutTabLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutTabModelImpl</code>.
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

		return _objectLayoutTabLocalService.dynamicQuery(
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

		return _objectLayoutTabLocalService.dynamicQueryCount(dynamicQuery);
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

		return _objectLayoutTabLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectLayoutTab fetchObjectLayoutTab(
		long objectLayoutTabId) {

		return _objectLayoutTabLocalService.fetchObjectLayoutTab(
			objectLayoutTabId);
	}

	/**
	 * Returns the object layout tab with the matching UUID and company.
	 *
	 * @param uuid the object layout tab's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout tab, or <code>null</code> if a matching object layout tab could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectLayoutTab
		fetchObjectLayoutTabByUuidAndCompanyId(String uuid, long companyId) {

		return _objectLayoutTabLocalService.
			fetchObjectLayoutTabByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectLayoutTabLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectLayoutTabLocalService.getExportActionableDynamicQuery(
			portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectLayoutTabLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectLayoutTab>
		getObjectLayoutObjectLayoutTabs(long objectLayoutId) {

		return _objectLayoutTabLocalService.getObjectLayoutObjectLayoutTabs(
			objectLayoutId);
	}

	/**
	 * Returns the object layout tab with the primary key.
	 *
	 * @param objectLayoutTabId the primary key of the object layout tab
	 * @return the object layout tab
	 * @throws PortalException if a object layout tab with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectLayoutTab getObjectLayoutTab(
			long objectLayoutTabId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutTabLocalService.getObjectLayoutTab(
			objectLayoutTabId);
	}

	/**
	 * Returns the object layout tab with the matching UUID and company.
	 *
	 * @param uuid the object layout tab's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object layout tab
	 * @throws PortalException if a matching object layout tab could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectLayoutTab
			getObjectLayoutTabByUuidAndCompanyId(String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutTabLocalService.
			getObjectLayoutTabByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object layout tabs.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectLayoutTabModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object layout tabs
	 * @param end the upper bound of the range of object layout tabs (not inclusive)
	 * @return the range of object layout tabs
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectLayoutTab>
		getObjectLayoutTabs(int start, int end) {

		return _objectLayoutTabLocalService.getObjectLayoutTabs(start, end);
	}

	/**
	 * Returns the number of object layout tabs.
	 *
	 * @return the number of object layout tabs
	 */
	@Override
	public int getObjectLayoutTabsCount() {
		return _objectLayoutTabLocalService.getObjectLayoutTabsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectLayoutTabLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectLayoutTabLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public void registerObjectLayoutTabScreenNavigationCategories(
		com.liferay.object.model.ObjectDefinition objectDefinition,
		java.util.List<com.liferay.object.model.ObjectLayoutTab>
			objectLayoutTabs) {

		_objectLayoutTabLocalService.
			registerObjectLayoutTabScreenNavigationCategories(
				objectDefinition, objectLayoutTabs);
	}

	@Override
	public void unregisterObjectLayoutTabScreenNavigationCategories(
		com.liferay.object.model.ObjectDefinition objectDefinition) {

		_objectLayoutTabLocalService.
			unregisterObjectLayoutTabScreenNavigationCategories(
				objectDefinition);
	}

	/**
	 * Updates the object layout tab in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectLayoutTabLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectLayoutTab the object layout tab
	 * @return the object layout tab that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectLayoutTab updateObjectLayoutTab(
		com.liferay.object.model.ObjectLayoutTab objectLayoutTab) {

		return _objectLayoutTabLocalService.updateObjectLayoutTab(
			objectLayoutTab);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectLayoutTabLocalService.getBasePersistence();
	}

	@Override
	public ObjectLayoutTabLocalService getWrappedService() {
		return _objectLayoutTabLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectLayoutTabLocalService objectLayoutTabLocalService) {

		_objectLayoutTabLocalService = objectLayoutTabLocalService;
	}

	private ObjectLayoutTabLocalService _objectLayoutTabLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-673590415