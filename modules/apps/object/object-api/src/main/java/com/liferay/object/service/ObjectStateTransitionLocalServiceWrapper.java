/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link ObjectStateTransitionLocalService}.
 *
 * @author Marco Leo
 * @see ObjectStateTransitionLocalService
 * @generated
 */
public class ObjectStateTransitionLocalServiceWrapper
	implements ObjectStateTransitionLocalService,
			   ServiceWrapper<ObjectStateTransitionLocalService> {

	public ObjectStateTransitionLocalServiceWrapper() {
		this(null);
	}

	public ObjectStateTransitionLocalServiceWrapper(
		ObjectStateTransitionLocalService objectStateTransitionLocalService) {

		_objectStateTransitionLocalService = objectStateTransitionLocalService;
	}

	@Override
	public com.liferay.object.model.ObjectStateTransition
			addObjectStateTransition(
				long userId, long objectStateFlowId, long sourceObjectStateId,
				long targetObjectStateId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectStateTransitionLocalService.addObjectStateTransition(
			userId, objectStateFlowId, sourceObjectStateId,
			targetObjectStateId);
	}

	/**
	 * Adds the object state transition to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectStateTransitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectStateTransition the object state transition
	 * @return the object state transition that was added
	 */
	@Override
	public com.liferay.object.model.ObjectStateTransition
		addObjectStateTransition(
			com.liferay.object.model.ObjectStateTransition
				objectStateTransition) {

		return _objectStateTransitionLocalService.addObjectStateTransition(
			objectStateTransition);
	}

	/**
	 * Creates a new object state transition with the primary key. Does not add the object state transition to the database.
	 *
	 * @param objectStateTransitionId the primary key for the new object state transition
	 * @return the new object state transition
	 */
	@Override
	public com.liferay.object.model.ObjectStateTransition
		createObjectStateTransition(long objectStateTransitionId) {

		return _objectStateTransitionLocalService.createObjectStateTransition(
			objectStateTransitionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectStateTransitionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteObjectStateFlowObjectStateTransitions(
		long objectStateFlowId) {

		_objectStateTransitionLocalService.
			deleteObjectStateFlowObjectStateTransitions(objectStateFlowId);
	}

	@Override
	public void deleteObjectStateObjectStateTransitions(long objectStateId) {
		_objectStateTransitionLocalService.
			deleteObjectStateObjectStateTransitions(objectStateId);
	}

	/**
	 * Deletes the object state transition with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectStateTransitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectStateTransitionId the primary key of the object state transition
	 * @return the object state transition that was removed
	 * @throws PortalException if a object state transition with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectStateTransition
			deleteObjectStateTransition(long objectStateTransitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectStateTransitionLocalService.deleteObjectStateTransition(
			objectStateTransitionId);
	}

	/**
	 * Deletes the object state transition from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectStateTransitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectStateTransition the object state transition
	 * @return the object state transition that was removed
	 */
	@Override
	public com.liferay.object.model.ObjectStateTransition
		deleteObjectStateTransition(
			com.liferay.object.model.ObjectStateTransition
				objectStateTransition) {

		return _objectStateTransitionLocalService.deleteObjectStateTransition(
			objectStateTransition);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectStateTransitionLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _objectStateTransitionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _objectStateTransitionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _objectStateTransitionLocalService.dynamicQuery();
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

		return _objectStateTransitionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectStateTransitionModelImpl</code>.
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

		return _objectStateTransitionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectStateTransitionModelImpl</code>.
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

		return _objectStateTransitionLocalService.dynamicQuery(
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

		return _objectStateTransitionLocalService.dynamicQueryCount(
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

		return _objectStateTransitionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.object.model.ObjectStateTransition
		fetchObjectStateTransition(long objectStateTransitionId) {

		return _objectStateTransitionLocalService.fetchObjectStateTransition(
			objectStateTransitionId);
	}

	/**
	 * Returns the object state transition with the matching UUID and company.
	 *
	 * @param uuid the object state transition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object state transition, or <code>null</code> if a matching object state transition could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectStateTransition
		fetchObjectStateTransitionByUuidAndCompanyId(
			String uuid, long companyId) {

		return _objectStateTransitionLocalService.
			fetchObjectStateTransitionByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _objectStateTransitionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return _objectStateTransitionLocalService.
			getExportActionableDynamicQuery(portletDataContext);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _objectStateTransitionLocalService.
			getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<com.liferay.object.model.ObjectStateTransition>
		getObjectStateFlowObjectStateTransitions(long objectStateFlowId) {

		return _objectStateTransitionLocalService.
			getObjectStateFlowObjectStateTransitions(objectStateFlowId);
	}

	/**
	 * Returns the object state transition with the primary key.
	 *
	 * @param objectStateTransitionId the primary key of the object state transition
	 * @return the object state transition
	 * @throws PortalException if a object state transition with the primary key could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectStateTransition
			getObjectStateTransition(long objectStateTransitionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectStateTransitionLocalService.getObjectStateTransition(
			objectStateTransitionId);
	}

	/**
	 * Returns the object state transition with the matching UUID and company.
	 *
	 * @param uuid the object state transition's UUID
	 * @param companyId the primary key of the company
	 * @return the matching object state transition
	 * @throws PortalException if a matching object state transition could not be found
	 */
	@Override
	public com.liferay.object.model.ObjectStateTransition
			getObjectStateTransitionByUuidAndCompanyId(
				String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectStateTransitionLocalService.
			getObjectStateTransitionByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the object state transitions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.object.model.impl.ObjectStateTransitionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of object state transitions
	 * @param end the upper bound of the range of object state transitions (not inclusive)
	 * @return the range of object state transitions
	 */
	@Override
	public java.util.List<com.liferay.object.model.ObjectStateTransition>
		getObjectStateTransitions(int start, int end) {

		return _objectStateTransitionLocalService.getObjectStateTransitions(
			start, end);
	}

	/**
	 * Returns the number of object state transitions.
	 *
	 * @return the number of object state transitions
	 */
	@Override
	public int getObjectStateTransitionsCount() {
		return _objectStateTransitionLocalService.
			getObjectStateTransitionsCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _objectStateTransitionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _objectStateTransitionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the object state transition in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect ObjectStateTransitionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param objectStateTransition the object state transition
	 * @return the object state transition that was updated
	 */
	@Override
	public com.liferay.object.model.ObjectStateTransition
		updateObjectStateTransition(
			com.liferay.object.model.ObjectStateTransition
				objectStateTransition) {

		return _objectStateTransitionLocalService.updateObjectStateTransition(
			objectStateTransition);
	}

	@Override
	public void updateObjectStateTransitions(
			com.liferay.object.model.ObjectStateFlow objectStateFlow)
		throws com.liferay.portal.kernel.exception.PortalException {

		_objectStateTransitionLocalService.updateObjectStateTransitions(
			objectStateFlow);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _objectStateTransitionLocalService.getBasePersistence();
	}

	@Override
	public ObjectStateTransitionLocalService getWrappedService() {
		return _objectStateTransitionLocalService;
	}

	@Override
	public void setWrappedService(
		ObjectStateTransitionLocalService objectStateTransitionLocalService) {

		_objectStateTransitionLocalService = objectStateTransitionLocalService;
	}

	private ObjectStateTransitionLocalService
		_objectStateTransitionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1272698798