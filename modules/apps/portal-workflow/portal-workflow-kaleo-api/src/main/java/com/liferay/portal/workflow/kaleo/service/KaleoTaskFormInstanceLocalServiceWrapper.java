/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskFormInstance;

/**
 * Provides a wrapper for {@link KaleoTaskFormInstanceLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTaskFormInstanceLocalService
 * @generated
 */
public class KaleoTaskFormInstanceLocalServiceWrapper
	implements KaleoTaskFormInstanceLocalService,
			   ServiceWrapper<KaleoTaskFormInstanceLocalService> {

	public KaleoTaskFormInstanceLocalServiceWrapper() {
		this(null);
	}

	public KaleoTaskFormInstanceLocalServiceWrapper(
		KaleoTaskFormInstanceLocalService kaleoTaskFormInstanceLocalService) {

		_kaleoTaskFormInstanceLocalService = kaleoTaskFormInstanceLocalService;
	}

	/**
	 * Adds the kaleo task form instance to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskFormInstance the kaleo task form instance
	 * @return the kaleo task form instance that was added
	 */
	@Override
	public KaleoTaskFormInstance addKaleoTaskFormInstance(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		return _kaleoTaskFormInstanceLocalService.addKaleoTaskFormInstance(
			kaleoTaskFormInstance);
	}

	@Override
	public KaleoTaskFormInstance addKaleoTaskFormInstance(
			long groupId, long kaleoTaskFormId, String formValues,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskFormInstanceLocalService.addKaleoTaskFormInstance(
			groupId, kaleoTaskFormId, formValues, kaleoTaskInstanceToken,
			serviceContext);
	}

	/**
	 * Creates a new kaleo task form instance with the primary key. Does not add the kaleo task form instance to the database.
	 *
	 * @param kaleoTaskFormInstanceId the primary key for the new kaleo task form instance
	 * @return the new kaleo task form instance
	 */
	@Override
	public KaleoTaskFormInstance createKaleoTaskFormInstance(
		long kaleoTaskFormInstanceId) {

		return _kaleoTaskFormInstanceLocalService.createKaleoTaskFormInstance(
			kaleoTaskFormInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskFormInstanceLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCompanyKaleoTaskFormInstances(long companyId) {
		_kaleoTaskFormInstanceLocalService.deleteCompanyKaleoTaskFormInstances(
			companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoTaskFormInstances(
		long kaleoDefinitionVersionId) {

		_kaleoTaskFormInstanceLocalService.
			deleteKaleoDefinitionVersionKaleoTaskFormInstances(
				kaleoDefinitionVersionId);
	}

	@Override
	public void deleteKaleoInstanceKaleoTaskFormInstances(
		long kaleoInstanceId) {

		_kaleoTaskFormInstanceLocalService.
			deleteKaleoInstanceKaleoTaskFormInstances(kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo task form instance from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskFormInstance the kaleo task form instance
	 * @return the kaleo task form instance that was removed
	 */
	@Override
	public KaleoTaskFormInstance deleteKaleoTaskFormInstance(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		return _kaleoTaskFormInstanceLocalService.deleteKaleoTaskFormInstance(
			kaleoTaskFormInstance);
	}

	/**
	 * Deletes the kaleo task form instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance that was removed
	 * @throws PortalException if a kaleo task form instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskFormInstance deleteKaleoTaskFormInstance(
			long kaleoTaskFormInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskFormInstanceLocalService.deleteKaleoTaskFormInstance(
			kaleoTaskFormInstanceId);
	}

	@Override
	public void deleteKaleoTaskInstanceTokenKaleoTaskFormInstances(
		long kaleoTaskInstanceTokenId) {

		_kaleoTaskFormInstanceLocalService.
			deleteKaleoTaskInstanceTokenKaleoTaskFormInstances(
				kaleoTaskInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskFormInstanceLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoTaskFormInstanceLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoTaskFormInstanceLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoTaskFormInstanceLocalService.dynamicQuery();
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

		return _kaleoTaskFormInstanceLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
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

		return _kaleoTaskFormInstanceLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
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

		return _kaleoTaskFormInstanceLocalService.dynamicQuery(
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

		return _kaleoTaskFormInstanceLocalService.dynamicQueryCount(
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

		return _kaleoTaskFormInstanceLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoTaskFormInstance fetchKaleoTaskFormInstance(
		long kaleoTaskFormInstanceId) {

		return _kaleoTaskFormInstanceLocalService.fetchKaleoTaskFormInstance(
			kaleoTaskFormInstanceId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoTaskFormInstanceLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoTaskFormInstanceLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo task form instance with the primary key.
	 *
	 * @param kaleoTaskFormInstanceId the primary key of the kaleo task form instance
	 * @return the kaleo task form instance
	 * @throws PortalException if a kaleo task form instance with the primary key could not be found
	 */
	@Override
	public KaleoTaskFormInstance getKaleoTaskFormInstance(
			long kaleoTaskFormInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskFormInstanceLocalService.getKaleoTaskFormInstance(
			kaleoTaskFormInstanceId);
	}

	/**
	 * Returns a range of all the kaleo task form instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTaskFormInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo task form instances
	 * @param end the upper bound of the range of kaleo task form instances (not inclusive)
	 * @return the range of kaleo task form instances
	 */
	@Override
	public java.util.List<KaleoTaskFormInstance> getKaleoTaskFormInstances(
		int start, int end) {

		return _kaleoTaskFormInstanceLocalService.getKaleoTaskFormInstances(
			start, end);
	}

	/**
	 * Returns the number of kaleo task form instances.
	 *
	 * @return the number of kaleo task form instances
	 */
	@Override
	public int getKaleoTaskFormInstancesCount() {
		return _kaleoTaskFormInstanceLocalService.
			getKaleoTaskFormInstancesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoTaskFormInstanceLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTaskFormInstanceLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the kaleo task form instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTaskFormInstanceLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTaskFormInstance the kaleo task form instance
	 * @return the kaleo task form instance that was updated
	 */
	@Override
	public KaleoTaskFormInstance updateKaleoTaskFormInstance(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		return _kaleoTaskFormInstanceLocalService.updateKaleoTaskFormInstance(
			kaleoTaskFormInstance);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoTaskFormInstanceLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoTaskFormInstance> getCTPersistence() {
		return _kaleoTaskFormInstanceLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoTaskFormInstance> getModelClass() {
		return _kaleoTaskFormInstanceLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoTaskFormInstance>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoTaskFormInstanceLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoTaskFormInstanceLocalService getWrappedService() {
		return _kaleoTaskFormInstanceLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoTaskFormInstanceLocalService kaleoTaskFormInstanceLocalService) {

		_kaleoTaskFormInstanceLocalService = kaleoTaskFormInstanceLocalService;
	}

	private KaleoTaskFormInstanceLocalService
		_kaleoTaskFormInstanceLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-851586940