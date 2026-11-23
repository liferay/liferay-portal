/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;

/**
 * Provides a wrapper for {@link KaleoTimerLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTimerLocalService
 * @generated
 */
public class KaleoTimerLocalServiceWrapper
	implements KaleoTimerLocalService, ServiceWrapper<KaleoTimerLocalService> {

	public KaleoTimerLocalServiceWrapper() {
		this(null);
	}

	public KaleoTimerLocalServiceWrapper(
		KaleoTimerLocalService kaleoTimerLocalService) {

		_kaleoTimerLocalService = kaleoTimerLocalService;
	}

	/**
	 * Adds the kaleo timer to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTimerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTimer the kaleo timer
	 * @return the kaleo timer that was added
	 */
	@Override
	public KaleoTimer addKaleoTimer(KaleoTimer kaleoTimer) {
		return _kaleoTimerLocalService.addKaleoTimer(kaleoTimer);
	}

	@Override
	public KaleoTimer addKaleoTimer(
			String kaleoClassName, long kaleoClassPK, long kaleoDefinitionId,
			long kaleoDefinitionVersionId,
			com.liferay.portal.workflow.kaleo.definition.Timer timer,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerLocalService.addKaleoTimer(
			kaleoClassName, kaleoClassPK, kaleoDefinitionId,
			kaleoDefinitionVersionId, timer, serviceContext);
	}

	/**
	 * Creates a new kaleo timer with the primary key. Does not add the kaleo timer to the database.
	 *
	 * @param kaleoTimerId the primary key for the new kaleo timer
	 * @return the new kaleo timer
	 */
	@Override
	public KaleoTimer createKaleoTimer(long kaleoTimerId) {
		return _kaleoTimerLocalService.createKaleoTimer(kaleoTimerId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the kaleo timer from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTimerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTimer the kaleo timer
	 * @return the kaleo timer that was removed
	 */
	@Override
	public KaleoTimer deleteKaleoTimer(KaleoTimer kaleoTimer) {
		return _kaleoTimerLocalService.deleteKaleoTimer(kaleoTimer);
	}

	/**
	 * Deletes the kaleo timer with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTimerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTimerId the primary key of the kaleo timer
	 * @return the kaleo timer that was removed
	 * @throws PortalException if a kaleo timer with the primary key could not be found
	 */
	@Override
	public KaleoTimer deleteKaleoTimer(long kaleoTimerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerLocalService.deleteKaleoTimer(kaleoTimerId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoTimerLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoTimerLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoTimerLocalService.dynamicQuery();
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

		return _kaleoTimerLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
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

		return _kaleoTimerLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
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

		return _kaleoTimerLocalService.dynamicQuery(
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

		return _kaleoTimerLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kaleoTimerLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoTimer fetchKaleoTimer(long kaleoTimerId) {
		return _kaleoTimerLocalService.fetchKaleoTimer(kaleoTimerId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoTimerLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoTimerLocalService.getIndexableActionableDynamicQuery();
	}

	@Override
	public java.util.List<KaleoTimer> getKaleoDefinitionVersionKaleoTimers(
		String kaleoClassName, long kaleoDefinitionVersionId) {

		return _kaleoTimerLocalService.getKaleoDefinitionVersionKaleoTimers(
			kaleoClassName, kaleoDefinitionVersionId);
	}

	/**
	 * Returns the kaleo timer with the primary key.
	 *
	 * @param kaleoTimerId the primary key of the kaleo timer
	 * @return the kaleo timer
	 * @throws PortalException if a kaleo timer with the primary key could not be found
	 */
	@Override
	public KaleoTimer getKaleoTimer(long kaleoTimerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerLocalService.getKaleoTimer(kaleoTimerId);
	}

	/**
	 * Returns a range of all the kaleo timers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo timers
	 * @param end the upper bound of the range of kaleo timers (not inclusive)
	 * @return the range of kaleo timers
	 */
	@Override
	public java.util.List<KaleoTimer> getKaleoTimers(int start, int end) {
		return _kaleoTimerLocalService.getKaleoTimers(start, end);
	}

	@Override
	public java.util.List<KaleoTimer> getKaleoTimers(
		String kaleoClassName, long kaleoClassPK) {

		return _kaleoTimerLocalService.getKaleoTimers(
			kaleoClassName, kaleoClassPK);
	}

	/**
	 * Returns the number of kaleo timers.
	 *
	 * @return the number of kaleo timers
	 */
	@Override
	public int getKaleoTimersCount() {
		return _kaleoTimerLocalService.getKaleoTimersCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoTimerLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Updates the kaleo timer in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTimerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTimer the kaleo timer
	 * @return the kaleo timer that was updated
	 */
	@Override
	public KaleoTimer updateKaleoTimer(KaleoTimer kaleoTimer) {
		return _kaleoTimerLocalService.updateKaleoTimer(kaleoTimer);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoTimerLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoTimer> getCTPersistence() {
		return _kaleoTimerLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoTimer> getModelClass() {
		return _kaleoTimerLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoTimer>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoTimerLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoTimerLocalService getWrappedService() {
		return _kaleoTimerLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoTimerLocalService kaleoTimerLocalService) {

		_kaleoTimerLocalService = kaleoTimerLocalService;
	}

	private KaleoTimerLocalService _kaleoTimerLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1413337229