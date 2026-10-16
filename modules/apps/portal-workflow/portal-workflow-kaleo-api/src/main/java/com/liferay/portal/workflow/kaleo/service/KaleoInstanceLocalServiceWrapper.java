/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoInstance;

/**
 * Provides a wrapper for {@link KaleoInstanceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceLocalService
 * @generated
 */
public class KaleoInstanceLocalServiceWrapper
	implements KaleoInstanceLocalService,
			   ServiceWrapper<KaleoInstanceLocalService> {

	public KaleoInstanceLocalServiceWrapper() {
		this(null);
	}

	public KaleoInstanceLocalServiceWrapper(
		KaleoInstanceLocalService kaleoInstanceLocalService) {

		_kaleoInstanceLocalService = kaleoInstanceLocalService;
	}

	/**
	 * Adds the kaleo instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was added
	 */
	@Override
	public KaleoInstance addKaleoInstance(KaleoInstance kaleoInstance) {
		return _kaleoInstanceLocalService.addKaleoInstance(kaleoInstance);
	}

	@Override
	public KaleoInstance addKaleoInstance(
			long kaleoDefinitionId, long kaleoDefinitionVersionId,
			String kaleoDefinitionName, int kaleoDefinitionVersion,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.addKaleoInstance(
			kaleoDefinitionId, kaleoDefinitionVersionId, kaleoDefinitionName,
			kaleoDefinitionVersion, workflowContext, serviceContext);
	}

	@Override
	public KaleoInstance addKaleoInstance(
			String kaleoDefinitionName, Integer kaleoDefinitionVersion,
			String transitionName,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext,
			boolean waitForCompletion)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.addKaleoInstance(
			kaleoDefinitionName, kaleoDefinitionVersion, transitionName,
			workflowContext, serviceContext, waitForCompletion);
	}

	@Override
	public KaleoInstance completeKaleoInstance(long kaleoInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.completeKaleoInstance(
			kaleoInstanceId);
	}

	/**
	 * Creates a new kaleo instance with the primary key. Does not add the kaleo instance to the database.
	 *
	 * @param kaleoInstanceId the primary key for the new kaleo instance
	 * @return the new kaleo instance
	 */
	@Override
	public KaleoInstance createKaleoInstance(long kaleoInstanceId) {
		return _kaleoInstanceLocalService.createKaleoInstance(kaleoInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public void deleteCompanyKaleoInstances(long companyId) {
		_kaleoInstanceLocalService.deleteCompanyKaleoInstances(companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoInstances(
		long kaleoDefinitionVersionId) {

		_kaleoInstanceLocalService.deleteKaleoDefinitionVersionKaleoInstances(
			kaleoDefinitionVersionId);
	}

	/**
	 * Deletes the kaleo instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was removed
	 */
	@Override
	public KaleoInstance deleteKaleoInstance(KaleoInstance kaleoInstance) {
		return _kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstance);
	}

	/**
	 * Deletes the kaleo instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance that was removed
	 * @throws PortalException if a kaleo instance with the primary key could not be found
	 */
	@Override
	public KaleoInstance deleteKaleoInstance(long kaleoInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.deleteKaleoInstance(kaleoInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoInstanceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoInstanceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoInstanceLocalService.dynamicQuery();
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

		return _kaleoInstanceLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
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

		return _kaleoInstanceLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
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

		return _kaleoInstanceLocalService.dynamicQuery(
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

		return _kaleoInstanceLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kaleoInstanceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoInstance fetchKaleoInstance(long kaleoInstanceId) {
		return _kaleoInstanceLocalService.fetchKaleoInstance(kaleoInstanceId);
	}

	@Override
	public KaleoInstance fetchKaleoInstance(
		long kaleoInstanceId, long companyId, long userId) {

		return _kaleoInstanceLocalService.fetchKaleoInstance(
			kaleoInstanceId, companyId, userId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoInstanceLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoInstanceLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public int getKaleoDefinitionKaleoInstancesCount(
		long kaleoDefinitionId, boolean completed) {

		return _kaleoInstanceLocalService.getKaleoDefinitionKaleoInstancesCount(
			kaleoDefinitionId, completed);
	}

	/**
	 * Returns the kaleo instance with the primary key.
	 *
	 * @param kaleoInstanceId the primary key of the kaleo instance
	 * @return the kaleo instance
	 * @throws PortalException if a kaleo instance with the primary key could not be found
	 */
	@Override
	public KaleoInstance getKaleoInstance(long kaleoInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.getKaleoInstance(kaleoInstanceId);
	}

	/**
	 * Returns a range of all the kaleo instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo instances
	 * @param end the upper bound of the range of kaleo instances (not inclusive)
	 * @return the range of kaleo instances
	 */
	@Override
	public java.util.List<KaleoInstance> getKaleoInstances(int start, int end) {
		return _kaleoInstanceLocalService.getKaleoInstances(start, end);
	}

	@Override
	public java.util.List<KaleoInstance> getKaleoInstances(
			Long userId, String assetClassName, Long assetClassPK,
			Boolean completed, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
				orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.getKaleoInstances(
			userId, assetClassName, assetClassPK, completed, start, end,
			orderByComparator, serviceContext);
	}

	@Override
	public java.util.List<KaleoInstance> getKaleoInstances(
			Long userId, String[] assetClassNames, Boolean completed, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
				orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.getKaleoInstances(
			userId, assetClassNames, completed, start, end, orderByComparator,
			serviceContext);
	}

	@Override
	public java.util.List<KaleoInstance> getKaleoInstances(
			String kaleoDefinitionName, int kaleoDefinitionVersion,
			boolean completed, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
				orderByComparator,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.getKaleoInstances(
			kaleoDefinitionName, kaleoDefinitionVersion, completed, start, end,
			orderByComparator, serviceContext);
	}

	/**
	 * Returns the number of kaleo instances.
	 *
	 * @return the number of kaleo instances
	 */
	@Override
	public int getKaleoInstancesCount() {
		return _kaleoInstanceLocalService.getKaleoInstancesCount();
	}

	@Override
	public int getKaleoInstancesCount(
		long kaleoDefinitionVersionId, boolean completed) {

		return _kaleoInstanceLocalService.getKaleoInstancesCount(
			kaleoDefinitionVersionId, completed);
	}

	@Override
	public int getKaleoInstancesCount(
		Long userId, String assetClassName, Long assetClassPK,
		Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoInstanceLocalService.getKaleoInstancesCount(
			userId, assetClassName, assetClassPK, completed, serviceContext);
	}

	@Override
	public int getKaleoInstancesCount(
		Long userId, String[] assetClassNames, Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoInstanceLocalService.getKaleoInstancesCount(
			userId, assetClassNames, completed, serviceContext);
	}

	@Override
	public int getKaleoInstancesCount(
		String kaleoDefinitionName, int kaleoDefinitionVersion,
		boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoInstanceLocalService.getKaleoInstancesCount(
			kaleoDefinitionName, kaleoDefinitionVersion, completed,
			serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoInstanceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public java.util.List<KaleoInstance> search(
		Long userId, Boolean active, String assetClassName, String assetTitle,
		String assetDescription, String nodeName, String kaleoDefinitionName,
		Boolean completed, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
			orderByComparator,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoInstanceLocalService.search(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed, start, end,
			orderByComparator, serviceContext);
	}

	@Override
	public int searchCount(
		Long userId, Boolean active, String assetClassName, String assetTitle,
		String assetDescription, String nodeName, String kaleoDefinitionName,
		Boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoInstanceLocalService.searchCount(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed, serviceContext);
	}

	@Override
	public com.liferay.portal.kernel.search.BaseModelSearchResult<KaleoInstance>
			searchKaleoInstances(
				Long userId, Boolean active, String assetClassName,
				String assetTitle, String assetDescription, String nodeName,
				String kaleoDefinitionName, Boolean completed,
				boolean searchByActiveWorkflowHandlers, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator<KaleoInstance>
					orderByComparator,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.searchKaleoInstances(
			userId, active, assetClassName, assetTitle, assetDescription,
			nodeName, kaleoDefinitionName, completed,
			searchByActiveWorkflowHandlers, start, end, orderByComparator,
			serviceContext);
	}

	@Override
	public KaleoInstance updateActive(
			long userId, long kaleoInstanceId, boolean active)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.updateActive(
			userId, kaleoInstanceId, active);
	}

	/**
	 * Updates the kaleo instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstance the kaleo instance
	 * @return the kaleo instance that was updated
	 */
	@Override
	public KaleoInstance updateKaleoInstance(KaleoInstance kaleoInstance) {
		return _kaleoInstanceLocalService.updateKaleoInstance(kaleoInstance);
	}

	@Override
	public KaleoInstance updateKaleoInstance(
			long kaleoInstanceId, long rootKaleoInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.updateKaleoInstance(
			kaleoInstanceId, rootKaleoInstanceTokenId);
	}

	@Override
	public KaleoInstance updateKaleoInstance(
			long kaleoInstanceId,
			java.util.Map<String, java.io.Serializable> workflowContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceLocalService.updateKaleoInstance(
			kaleoInstanceId, workflowContext);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoInstanceLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoInstance> getCTPersistence() {
		return _kaleoInstanceLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoInstance> getModelClass() {
		return _kaleoInstanceLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoInstance>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoInstanceLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoInstanceLocalService getWrappedService() {
		return _kaleoInstanceLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoInstanceLocalService kaleoInstanceLocalService) {

		_kaleoInstanceLocalService = kaleoInstanceLocalService;
	}

	private KaleoInstanceLocalService _kaleoInstanceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1667521244