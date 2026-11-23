/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;

/**
 * Provides a wrapper for {@link KaleoInstanceTokenLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoInstanceTokenLocalService
 * @generated
 */
public class KaleoInstanceTokenLocalServiceWrapper
	implements KaleoInstanceTokenLocalService,
			   ServiceWrapper<KaleoInstanceTokenLocalService> {

	public KaleoInstanceTokenLocalServiceWrapper() {
		this(null);
	}

	public KaleoInstanceTokenLocalServiceWrapper(
		KaleoInstanceTokenLocalService kaleoInstanceTokenLocalService) {

		_kaleoInstanceTokenLocalService = kaleoInstanceTokenLocalService;
	}

	/**
	 * Adds the kaleo instance token to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was added
	 */
	@Override
	public KaleoInstanceToken addKaleoInstanceToken(
		KaleoInstanceToken kaleoInstanceToken) {

		return _kaleoInstanceTokenLocalService.addKaleoInstanceToken(
			kaleoInstanceToken);
	}

	@Override
	public KaleoInstanceToken addKaleoInstanceToken(
			long currentKaleoNodeId, long kaleoDefinitionId,
			long kaleoDefinitionVersionId, long kaleoInstanceId,
			long parentKaleoInstanceTokenId,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.addKaleoInstanceToken(
			currentKaleoNodeId, kaleoDefinitionId, kaleoDefinitionVersionId,
			kaleoInstanceId, parentKaleoInstanceTokenId, workflowContext,
			serviceContext);
	}

	@Override
	public KaleoInstanceToken addKaleoInstanceToken(
			long parentKaleoInstanceTokenId,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.addKaleoInstanceToken(
			parentKaleoInstanceTokenId, workflowContext, serviceContext);
	}

	@Override
	public KaleoInstanceToken completeKaleoInstanceToken(
			long kaleoInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.completeKaleoInstanceToken(
			kaleoInstanceTokenId);
	}

	/**
	 * Creates a new kaleo instance token with the primary key. Does not add the kaleo instance token to the database.
	 *
	 * @param kaleoInstanceTokenId the primary key for the new kaleo instance token
	 * @return the new kaleo instance token
	 */
	@Override
	public KaleoInstanceToken createKaleoInstanceToken(
		long kaleoInstanceTokenId) {

		return _kaleoInstanceTokenLocalService.createKaleoInstanceToken(
			kaleoInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.createPersistedModel(
			primaryKeyObj);
	}

	@Override
	public void deleteCompanyKaleoInstanceTokens(long companyId) {
		_kaleoInstanceTokenLocalService.deleteCompanyKaleoInstanceTokens(
			companyId);
	}

	@Override
	public void deleteKaleoDefinitionVersionKaleoInstanceTokens(
		long kaleoDefinitionVersionId) {

		_kaleoInstanceTokenLocalService.
			deleteKaleoDefinitionVersionKaleoInstanceTokens(
				kaleoDefinitionVersionId);
	}

	@Override
	public void deleteKaleoInstanceKaleoInstanceTokens(long kaleoInstanceId) {
		_kaleoInstanceTokenLocalService.deleteKaleoInstanceKaleoInstanceTokens(
			kaleoInstanceId);
	}

	/**
	 * Deletes the kaleo instance token from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was removed
	 */
	@Override
	public KaleoInstanceToken deleteKaleoInstanceToken(
		KaleoInstanceToken kaleoInstanceToken) {

		return _kaleoInstanceTokenLocalService.deleteKaleoInstanceToken(
			kaleoInstanceToken);
	}

	/**
	 * Deletes the kaleo instance token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceTokenId the primary key of the kaleo instance token
	 * @return the kaleo instance token that was removed
	 * @throws PortalException if a kaleo instance token with the primary key could not be found
	 */
	@Override
	public KaleoInstanceToken deleteKaleoInstanceToken(
			long kaleoInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.deleteKaleoInstanceToken(
			kaleoInstanceTokenId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _kaleoInstanceTokenLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _kaleoInstanceTokenLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _kaleoInstanceTokenLocalService.dynamicQuery();
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

		return _kaleoInstanceTokenLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
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

		return _kaleoInstanceTokenLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
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

		return _kaleoInstanceTokenLocalService.dynamicQuery(
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

		return _kaleoInstanceTokenLocalService.dynamicQueryCount(dynamicQuery);
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

		return _kaleoInstanceTokenLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public KaleoInstanceToken fetchKaleoInstanceToken(
		long kaleoInstanceTokenId) {

		return _kaleoInstanceTokenLocalService.fetchKaleoInstanceToken(
			kaleoInstanceTokenId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _kaleoInstanceTokenLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _kaleoInstanceTokenLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the kaleo instance token with the primary key.
	 *
	 * @param kaleoInstanceTokenId the primary key of the kaleo instance token
	 * @return the kaleo instance token
	 * @throws PortalException if a kaleo instance token with the primary key could not be found
	 */
	@Override
	public KaleoInstanceToken getKaleoInstanceToken(long kaleoInstanceTokenId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.getKaleoInstanceToken(
			kaleoInstanceTokenId);
	}

	/**
	 * Returns a range of all the kaleo instance tokens.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.workflow.kaleo.model.impl.KaleoInstanceTokenModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kaleo instance tokens
	 * @param end the upper bound of the range of kaleo instance tokens (not inclusive)
	 * @return the range of kaleo instance tokens
	 */
	@Override
	public java.util.List<KaleoInstanceToken> getKaleoInstanceTokens(
		int start, int end) {

		return _kaleoInstanceTokenLocalService.getKaleoInstanceTokens(
			start, end);
	}

	@Override
	public java.util.List<KaleoInstanceToken> getKaleoInstanceTokens(
		long kaleoInstanceId) {

		return _kaleoInstanceTokenLocalService.getKaleoInstanceTokens(
			kaleoInstanceId);
	}

	@Override
	public java.util.List<KaleoInstanceToken> getKaleoInstanceTokens(
		long parentKaleoInstanceTokenId, java.util.Date completionDate,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoInstanceTokenLocalService.getKaleoInstanceTokens(
			parentKaleoInstanceTokenId, completionDate, serviceContext);
	}

	@Override
	public java.util.List<KaleoInstanceToken> getKaleoInstanceTokens(
		long parentKaleoInstanceTokenId,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoInstanceTokenLocalService.getKaleoInstanceTokens(
			parentKaleoInstanceTokenId, serviceContext);
	}

	/**
	 * Returns the number of kaleo instance tokens.
	 *
	 * @return the number of kaleo instance tokens
	 */
	@Override
	public int getKaleoInstanceTokensCount() {
		return _kaleoInstanceTokenLocalService.getKaleoInstanceTokensCount();
	}

	@Override
	public int getKaleoInstanceTokensCount(
		long parentKaleoInstanceTokenId, java.util.Date completionDate,
		com.liferay.portal.kernel.service.ServiceContext serviceContext) {

		return _kaleoInstanceTokenLocalService.getKaleoInstanceTokensCount(
			parentKaleoInstanceTokenId, completionDate, serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoInstanceTokenLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public KaleoInstanceToken getRootKaleoInstanceToken(
			long kaleoInstanceId,
			java.util.Map<String, java.io.Serializable> workflowContext,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.getRootKaleoInstanceToken(
			kaleoInstanceId, workflowContext, serviceContext);
	}

	/**
	 * Updates the kaleo instance token in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect KaleoInstanceTokenLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param kaleoInstanceToken the kaleo instance token
	 * @return the kaleo instance token that was updated
	 */
	@Override
	public KaleoInstanceToken updateKaleoInstanceToken(
		KaleoInstanceToken kaleoInstanceToken) {

		return _kaleoInstanceTokenLocalService.updateKaleoInstanceToken(
			kaleoInstanceToken);
	}

	@Override
	public KaleoInstanceToken updateKaleoInstanceToken(
			long kaleoInstanceTokenId, long currentKaleoNodeId,
			String currentKaleoNodeName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _kaleoInstanceTokenLocalService.updateKaleoInstanceToken(
			kaleoInstanceTokenId, currentKaleoNodeId, currentKaleoNodeName);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _kaleoInstanceTokenLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<KaleoInstanceToken> getCTPersistence() {
		return _kaleoInstanceTokenLocalService.getCTPersistence();
	}

	@Override
	public Class<KaleoInstanceToken> getModelClass() {
		return _kaleoInstanceTokenLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<KaleoInstanceToken>, R, E>
				updateUnsafeFunction)
		throws E {

		return _kaleoInstanceTokenLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public KaleoInstanceTokenLocalService getWrappedService() {
		return _kaleoInstanceTokenLocalService;
	}

	@Override
	public void setWrappedService(
		KaleoInstanceTokenLocalService kaleoInstanceTokenLocalService) {

		_kaleoInstanceTokenLocalService = kaleoInstanceTokenLocalService;
	}

	private KaleoInstanceTokenLocalService _kaleoInstanceTokenLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1946792462