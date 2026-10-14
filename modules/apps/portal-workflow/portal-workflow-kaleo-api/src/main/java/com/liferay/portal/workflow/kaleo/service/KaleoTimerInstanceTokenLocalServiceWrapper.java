/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoTimerInstanceToken;

/**
 * Provides a wrapper for {@link KaleoTimerInstanceTokenLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoTimerInstanceTokenLocalService
 * @generated
 */
public class KaleoTimerInstanceTokenLocalServiceWrapper
	implements KaleoTimerInstanceTokenLocalService,
			   ServiceWrapper<KaleoTimerInstanceTokenLocalService> {

	public KaleoTimerInstanceTokenLocalServiceWrapper() {
		this(null);
	}

	public KaleoTimerInstanceTokenLocalServiceWrapper(
		KaleoTimerInstanceTokenLocalService
			kaleoTimerInstanceTokenLocalService) {

		_kaleoTimerInstanceTokenLocalService =
			kaleoTimerInstanceTokenLocalService;
	}

	/**
	 * Adds the kaleo timer instance token to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTimerInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTimerInstanceToken the kaleo timer instance token
	 * @return the kaleo timer instance token that was added
	 */
	@Override
	public KaleoTimerInstanceToken addKaleoTimerInstanceToken(
		KaleoTimerInstanceToken kaleoTimerInstanceToken) {

		return _kaleoTimerInstanceTokenLocalService.addKaleoTimerInstanceToken(
			kaleoTimerInstanceToken);
	}

	@Override
	public KaleoTimerInstanceToken addKaleoTimerInstanceToken(
			long kaleoInstanceTokenId, long kaleoTaskInstanceTokenId,
			long kaleoTimerId, String kaleoTimerName,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerInstanceTokenLocalService.addKaleoTimerInstanceToken(
			kaleoInstanceTokenId, kaleoTaskInstanceTokenId, kaleoTimerId,
			kaleoTimerName, workflowContext, serviceContext);
	}

	@Override
	public java.util.List<KaleoTimerInstanceToken> addKaleoTimerInstanceTokens(
			com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken
				kaleoInstanceToken,
			com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken
				kaleoTaskInstanceToken,
			java.util.Collection
				<com.liferay.portal.workflow.kaleo.model.KaleoTimer>
					kaleoTimers,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerInstanceTokenLocalService.addKaleoTimerInstanceTokens(
			kaleoInstanceToken, kaleoTaskInstanceToken, kaleoTimers,
			workflowContext, serviceContext);
	}

	@Override
	public KaleoTimerInstanceToken completeKaleoTimerInstanceToken(
			long kaleoTimerInstanceTokenId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerInstanceTokenLocalService.
			completeKaleoTimerInstanceToken(
				kaleoTimerInstanceTokenId, serviceContext);
	}

	@Override
	public void completeKaleoTimerInstanceTokens(
			java.util.List<KaleoTimerInstanceToken> kaleoTimerInstanceTokens,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kaleoTimerInstanceTokenLocalService.completeKaleoTimerInstanceTokens(
			kaleoTimerInstanceTokens, serviceContext);
	}

	@Override
	public void completeKaleoTimerInstanceTokens(
			long kaleoInstanceTokenId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_kaleoTimerInstanceTokenLocalService.completeKaleoTimerInstanceTokens(
			kaleoInstanceTokenId, serviceContext);
	}

	/**
	 * Creates a new kaleo timer instance token with the primary key. Does not add the kaleo timer instance token to the database.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key for the new kaleo timer instance token
	 * @return the new kaleo timer instance token
	 */
	@Override
	public KaleoTimerInstanceToken createKaleoTimerInstanceToken(
		long kaleoTimerInstanceTokenId) {

		return _kaleoTimerInstanceTokenLocalService.
			createKaleoTimerInstanceToken(kaleoTimerInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerInstanceTokenLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Deletes the kaleo timer instance token from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTimerInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTimerInstanceToken the kaleo timer instance token
	 * @return the kaleo timer instance token that was removed
	 */
	@Override
	public KaleoTimerInstanceToken deleteKaleoTimerInstanceToken(
		KaleoTimerInstanceToken kaleoTimerInstanceToken) {

		return _kaleoTimerInstanceTokenLocalService.
			deleteKaleoTimerInstanceToken(kaleoTimerInstanceToken);
	}

	/**
	 * Deletes the kaleo timer instance token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTimerInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTimerInstanceTokenId the primary key of the kaleo timer instance token
	 * @return the kaleo timer instance token that was removed
	 * @throws PortalException if a kaleo timer instance token with the primary key could not be found
	 */
	@Override
	public KaleoTimerInstanceToken deleteKaleoTimerInstanceToken(
			long kaleoTimerInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerInstanceTokenLocalService.
			deleteKaleoTimerInstanceToken(kaleoTimerInstanceTokenId);
	}

	@Override
	public void deleteKaleoTimerInstanceTokens(long kaleoInstanceId) {
		_kaleoTimerInstanceTokenLocalService.deleteKaleoTimerInstanceTokens(
			kaleoInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerInstanceTokenLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoTimerInstanceTokenLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoTimerInstanceTokenLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoTimerInstanceTokenLocalService.dynamicQuery();
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

		return _kaleoTimerInstanceTokenLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
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

		return _kaleoTimerInstanceTokenLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
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

		return _kaleoTimerInstanceTokenLocalService.dynamicQuery(
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

		return _kaleoTimerInstanceTokenLocalService.dynamicQueryCount(
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

		return _kaleoTimerInstanceTokenLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoTimerInstanceToken fetchKaleoTimerInstanceToken(
		long kaleoTimerInstanceTokenId) {

		return _kaleoTimerInstanceTokenLocalService.
			fetchKaleoTimerInstanceToken(kaleoTimerInstanceTokenId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoTimerInstanceTokenLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoTimerInstanceTokenLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo timer instance token with the primary key.
	 *
	 * @param kaleoTimerInstanceTokenId the primary key of the kaleo timer instance token
	 * @return the kaleo timer instance token
	 * @throws PortalException if a kaleo timer instance token with the primary key could not be found
	 */
	@Override
	public KaleoTimerInstanceToken getKaleoTimerInstanceToken(
			long kaleoTimerInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerInstanceTokenLocalService.getKaleoTimerInstanceToken(
			kaleoTimerInstanceTokenId);
	}

	/**
	 * Returns a range of all the kaleo timer instance tokens.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoTimerInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo timer instance tokens
	 * @param end the upper bound of the range of kaleo timer instance tokens (not inclusive)
	 * @return the range of kaleo timer instance tokens
	 */
	@Override
	public java.util.List<KaleoTimerInstanceToken> getKaleoTimerInstanceTokens(
		int start, int end) {

		return _kaleoTimerInstanceTokenLocalService.getKaleoTimerInstanceTokens(
			start, end);
	}

	@Override
	public java.util.List<KaleoTimerInstanceToken> getKaleoTimerInstanceTokens(
		long kaleoInstanceTokenId, boolean blocking, boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTimerInstanceTokenLocalService.getKaleoTimerInstanceTokens(
			kaleoInstanceTokenId, blocking, completed, serviceContext);
	}

	/**
	 * Returns the number of kaleo timer instance tokens.
	 *
	 * @return the number of kaleo timer instance tokens
	 */
	@Override
	public int getKaleoTimerInstanceTokensCount() {
		return _kaleoTimerInstanceTokenLocalService.
			getKaleoTimerInstanceTokensCount();
	}

	@Override
	public int getKaleoTimerInstanceTokensCount(
		long kaleoInstanceTokenId, boolean blocking, boolean completed,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoTimerInstanceTokenLocalService.
			getKaleoTimerInstanceTokensCount(
				kaleoInstanceTokenId, blocking, completed, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoTimerInstanceTokenLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoTimerInstanceTokenLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Updates the kaleo timer instance token in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoTimerInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoTimerInstanceToken the kaleo timer instance token
	 * @return the kaleo timer instance token that was updated
	 */
	@Override
	public KaleoTimerInstanceToken updateKaleoTimerInstanceToken(
		KaleoTimerInstanceToken kaleoTimerInstanceToken) {

		return _kaleoTimerInstanceTokenLocalService.
			updateKaleoTimerInstanceToken(kaleoTimerInstanceToken);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoTimerInstanceTokenLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoTimerInstanceToken> getCTPersistence() {
		return _kaleoTimerInstanceTokenLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoTimerInstanceToken> getModelClass() {
		return _kaleoTimerInstanceTokenLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoTimerInstanceToken>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoTimerInstanceTokenLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoTimerInstanceTokenLocalService getWrappedService() {
		return _kaleoTimerInstanceTokenLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoTimerInstanceTokenLocalService
			kaleoTimerInstanceTokenLocalService) {

		_kaleoTimerInstanceTokenLocalService =
			kaleoTimerInstanceTokenLocalService;
	}

	private KaleoTimerInstanceTokenLocalService
		_kaleoTimerInstanceTokenLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-808784199