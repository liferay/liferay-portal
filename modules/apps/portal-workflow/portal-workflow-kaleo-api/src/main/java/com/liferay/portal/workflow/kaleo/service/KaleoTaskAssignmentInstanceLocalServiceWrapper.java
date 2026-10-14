/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignmentInstance;

/**
 * Provides a wrapper for {@link KaleoTaskAssignmentInstanceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskAssignmentInstanceLocalService
 * @generated
 */
public class KaleoTaskAssignmentInstanceLocalServiceWrapper
	implements KaleoTaskAssignmentInstanceLocalService,
			   ServiceWrapper<KaleoTaskAssignmentInstanceLocalService> {

	public KaleoTaskAssignmentInstanceLocalServiceWrapper() {
		this(null);
	}

	public KaleoTaskAssignmentInstanceLocalServiceWrapper(
		KaleoTaskAssignmentInstanceLocalService
			kaleoTaskAssignmentInstanceLocalService) {

		_kaleoTaskAssignmentInstanceLocalService =
			kaleoTaskAssignmentInstanceLocalService;
	}

	/**
	 * Adds the kaleo task assignment instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskAssignmentInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskAssignmentInstance the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was added
	 */
	@Override
	public KaleoTaskAssignmentInstance addKaleoTaskAssignmentInstance(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		return _kaleoTaskAssignmentInstanceLocalService.
			addKaleoTaskAssignmentInstance(kaleoTaskAssignmentInstance);
	}

	@Override
	public KaleoTaskAssignmentInstance addKaleoTaskAssignmentInstance(
			long groupId,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String assigneeClassName, long assigneeClassPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.
			addKaleoTaskAssignmentInstance(
				groupId, kaleoTaskInstanceToken, assigneeClassName,
				assigneeClassPK, serviceContext);
	}

	@Override
	public java.util.List<KaleoTaskAssignmentInstance>
			addTaskAssignmentInstances(
				com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
					kaleoTaskInstanceToken,
				java.util.Collection
					<com.liferay.portal.workflow.kaleo.model.
						KaleoTaskAssignment> kaleoTaskAssignments,
				java.util.Map<String, java.io.Serializable> workflowContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.
			addTaskAssignmentInstances(
				kaleoTaskInstanceToken, kaleoTaskAssignments, workflowContext,
				serviceContext);
	}

	@Override
	public KaleoTaskAssignmentInstance assignKaleoTaskAssignmentInstance(
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			String assigneeClassName, long assigneeClassPK,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.
			assignKaleoTaskAssignmentInstance(
				kaleoTaskInstanceToken, assigneeClassName, assigneeClassPK,
				serviceContext);
	}

	@Override
	public java.util.List<KaleoTaskAssignmentInstance>
			assignKaleoTaskAssignmentInstances(
				com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
					kaleoTaskInstanceToken,
				java.util.Collection
					<com.liferay.portal.workflow.kaleo.model.
						KaleoTaskAssignment> kaleoTaskAssignments,
				java.util.Map<String, java.io.Serializable> workflowContext,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.
			assignKaleoTaskAssignmentInstances(
				kaleoTaskInstanceToken, kaleoTaskAssignments, workflowContext,
				serviceContext);
	}

	@Override
	public KaleoTaskAssignmentInstance completeKaleoTaskInstanceToken(
			long kaleoTaskInstanceTokenId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.
			completeKaleoTaskInstanceToken(
				kaleoTaskInstanceTokenId, serviceContext);
	}

	/**
	 * Creates a new kaleo task assignment instance with the primary key. Does not add the kaleo task assignment instance to the database.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key for the new kaleo task assignment instance
	 * @return the new kaleo task assignment instance
	 */
	@Override
	public KaleoTaskAssignmentInstance createKaleoTaskAssignmentInstance(
		long kaleoTaskAssignmentInstanceId) {

		return _kaleoTaskAssignmentInstanceLocalService.
			createKaleoTaskAssignmentInstance(kaleoTaskAssignmentInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCompanyKaleoTaskAssignmentInstances(long companyId) {
		_kaleoTaskAssignmentInstanceLocalService.
			deleteCompanyKaleoTaskAssignmentInstances(companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoTaskAssignmentInstances(
		long kaleoDefinitionId) {

		_kaleoTaskAssignmentInstanceLocalService.
			deleteKaleoDefinitionVersionKaleoTaskAssignmentInstances(
				kaleoDefinitionId);
	}

	@Override
	public void deleteKaleoInstanceKaleoTaskAssignmentInstances(
		long kaleoInstanceId) {

		_kaleoTaskAssignmentInstanceLocalService.
			deleteKaleoInstanceKaleoTaskAssignmentInstances(kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo task assignment instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskAssignmentInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskAssignmentInstance the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was removed
	 */
	@Override
	public KaleoTaskAssignmentInstance deleteKaleoTaskAssignmentInstance(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		return _kaleoTaskAssignmentInstanceLocalService.
			deleteKaleoTaskAssignmentInstance(kaleoTaskAssignmentInstance);
	}

	/**
	 * Deletes the kaleo task assignment instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskAssignmentInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was removed
	 * @throws PortalException if a kaleo task assignment instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance deleteKaleoTaskAssignmentInstance(
			long kaleoTaskAssignmentInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.
			deleteKaleoTaskAssignmentInstance(kaleoTaskAssignmentInstanceId);
	}

	@Override
	public void deleteKaleoTaskAssignmentInstances(
		com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
			kaleoTaskInstanceToken) {

		_kaleoTaskAssignmentInstanceLocalService.
			deleteKaleoTaskAssignmentInstances(kaleoTaskInstanceToken);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoTaskAssignmentInstanceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoTaskAssignmentInstanceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoTaskAssignmentInstanceLocalService.dynamicQuery();
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

		return _kaleoTaskAssignmentInstanceLocalService.dynamicQuery(
			dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
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

		return _kaleoTaskAssignmentInstanceLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
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

		return _kaleoTaskAssignmentInstanceLocalService.dynamicQuery(
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

		return _kaleoTaskAssignmentInstanceLocalService.dynamicQueryCount(
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

		return _kaleoTaskAssignmentInstanceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoTaskAssignmentInstance fetchFirstKaleoTaskAssignmentInstance(
		long kaleoTaskInstanceTokenId,
		com.liferay.portal.kernel.util.OrderByComparator
			<KaleoTaskAssignmentInstance> orderByComparator) {

		return _kaleoTaskAssignmentInstanceLocalService.
			fetchFirstKaleoTaskAssignmentInstance(
				kaleoTaskInstanceTokenId, orderByComparator);
	}

	@Override
	public KaleoTaskAssignmentInstance fetchKaleoTaskAssignmentInstance(
		long kaleoTaskAssignmentInstanceId) {

		return _kaleoTaskAssignmentInstanceLocalService.
			fetchKaleoTaskAssignmentInstance(kaleoTaskAssignmentInstanceId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoTaskAssignmentInstanceLocalService.
			getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoTaskAssignmentInstanceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo task assignment instance with the primary key.
	 *
	 * @param kaleoTaskAssignmentInstanceId the primary key of the kaleo task assignment instance
	 * @return the kaleo task assignment instance
	 * @throws PortalException if a kaleo task assignment instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskAssignmentInstance getKaleoTaskAssignmentInstance(
			long kaleoTaskAssignmentInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.
			getKaleoTaskAssignmentInstance(kaleoTaskAssignmentInstanceId);
	}

	/**
	 * Returns a range of all the kaleo task assignment instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskAssignmentInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo task assignment instances
	 * @param end the upper bound of the range of kaleo task assignment instances (not inclusive)
	 * @return the range of kaleo task assignment instances
	 */
	@Override
	public java.util.List<KaleoTaskAssignmentInstance>
		getKaleoTaskAssignmentInstances(int start, int end) {

		return _kaleoTaskAssignmentInstanceLocalService.
			getKaleoTaskAssignmentInstances(start, end);
	}

	@Override
	public java.util.List<KaleoTaskAssignmentInstance>
		getKaleoTaskAssignmentInstances(long kaleoTaskInstanceTokenId) {

		return _kaleoTaskAssignmentInstanceLocalService.
			getKaleoTaskAssignmentInstances(kaleoTaskInstanceTokenId);
	}

	/**
	 * Returns the number of kaleo task assignment instances.
	 *
	 * @return the number of kaleo task assignment instances
	 */
	@Override
	public int getKaleoTaskAssignmentInstancesCount() {
		return _kaleoTaskAssignmentInstanceLocalService.
			getKaleoTaskAssignmentInstancesCount();
	}

	@Override
	public int getKaleoTaskAssignmentInstancesCount(
		long kaleoTaskInstanceTokenId) {

		return _kaleoTaskAssignmentInstanceLocalService.
			getKaleoTaskAssignmentInstancesCount(kaleoTaskInstanceTokenId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoTaskAssignmentInstanceLocalService.
			getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskAssignmentInstanceLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the kaleo task assignment instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskAssignmentInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskAssignmentInstance the kaleo task assignment instance
	 * @return the kaleo task assignment instance that was updated
	 */
	@Override
	public KaleoTaskAssignmentInstance updateKaleoTaskAssignmentInstance(
		KaleoTaskAssignmentInstance kaleoTaskAssignmentInstance) {

		return _kaleoTaskAssignmentInstanceLocalService.
			updateKaleoTaskAssignmentInstance(kaleoTaskAssignmentInstance);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoTaskAssignmentInstanceLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoTaskAssignmentInstance> getCTPersistence() {
		return _kaleoTaskAssignmentInstanceLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoTaskAssignmentInstance> getModelClass() {
		return _kaleoTaskAssignmentInstanceLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoTaskAssignmentInstance>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoTaskAssignmentInstanceLocalService.
			updateWithUnsafeFunction(updateUnsafeFunction);
	}

	@Override
	public KaleoTaskAssignmentInstanceLocalService getWrappedService() {
		return _kaleoTaskAssignmentInstanceLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoTaskAssignmentInstanceLocalService
			kaleoTaskAssignmentInstanceLocalService) {

		_kaleoTaskAssignmentInstanceLocalService =
			kaleoTaskAssignmentInstanceLocalService;
	}

	private KaleoTaskAssignmentInstanceLocalService
		_kaleoTaskAssignmentInstanceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1788565281