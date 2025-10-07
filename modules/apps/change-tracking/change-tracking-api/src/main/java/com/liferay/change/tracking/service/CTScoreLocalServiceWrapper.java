/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link CTScoreLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see CTScoreLocalService
 * @generated
 */
public class CTScoreLocalServiceWrapper
	implements CTScoreLocalService, ServiceWrapper<CTScoreLocalService> {

	public CTScoreLocalServiceWrapper() {
		this(null);
	}

	public CTScoreLocalServiceWrapper(CTScoreLocalService ctScoreLocalService) {
		_ctScoreLocalService = ctScoreLocalService;
	}

	/**
	 * Adds the ct score to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTScoreLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctScore the ct score
	 * @return the ct score that was added
	 */
	@Override
	public com.liferay.change.tracking.model.CTScore addCTScore(
		com.liferay.change.tracking.model.CTScore ctScore) {

		return _ctScoreLocalService.addCTScore(ctScore);
	}

	@Override
	public com.liferay.change.tracking.model.CTScore addCTScore(
		long ctCollectionId) {

		return _ctScoreLocalService.addCTScore(ctCollectionId);
	}

	/**
	 * Creates a new ct score with the primary key. Does not add the ct score to the database.
	 *
	 * @param ctScoreId the primary key for the new ct score
	 * @return the new ct score
	 */
	@Override
	public com.liferay.change.tracking.model.CTScore createCTScore(
		long ctScoreId) {

		return _ctScoreLocalService.createCTScore(ctScoreId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctScoreLocalService.createPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.change.tracking.model.CTScore decrementScore(
		long ctCollectionId, int score) {

		return _ctScoreLocalService.decrementScore(ctCollectionId, score);
	}

	/**
	 * Deletes the ct score from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTScoreLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctScore the ct score
	 * @return the ct score that was removed
	 */
	@Override
	public com.liferay.change.tracking.model.CTScore deleteCTScore(
		com.liferay.change.tracking.model.CTScore ctScore) {

		return _ctScoreLocalService.deleteCTScore(ctScore);
	}

	/**
	 * Deletes the ct score with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTScoreLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctScoreId the primary key of the ct score
	 * @return the ct score that was removed
	 * @throws PortalException if a ct score with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTScore deleteCTScore(
			long ctScoreId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctScoreLocalService.deleteCTScore(ctScoreId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctScoreLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ctScoreLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ctScoreLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ctScoreLocalService.dynamicQuery();
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

		return _ctScoreLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTScoreModelImpl</code>.
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

		return _ctScoreLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTScoreModelImpl</code>.
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

		return _ctScoreLocalService.dynamicQuery(
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

		return _ctScoreLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ctScoreLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.change.tracking.model.CTScore fetchCTScore(
		long ctScoreId) {

		return _ctScoreLocalService.fetchCTScore(ctScoreId);
	}

	@Override
	public com.liferay.change.tracking.model.CTScore
		fetchCTScoreByCTCollectionId(long ctCollectionId) {

		return _ctScoreLocalService.fetchCTScoreByCTCollectionId(
			ctCollectionId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ctScoreLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ct score with the primary key.
	 *
	 * @param ctScoreId the primary key of the ct score
	 * @return the ct score
	 * @throws PortalException if a ct score with the primary key could not be found
	 */
	@Override
	public com.liferay.change.tracking.model.CTScore getCTScore(long ctScoreId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctScoreLocalService.getCTScore(ctScoreId);
	}

	/**
	 * Returns a range of all the ct scores.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.change.tracking.model.impl.CTScoreModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ct scores
	 * @param end the upper bound of the range of ct scores (not inclusive)
	 * @return the range of ct scores
	 */
	@Override
	public java.util.List<com.liferay.change.tracking.model.CTScore>
		getCTScores(int start, int end) {

		return _ctScoreLocalService.getCTScores(start, end);
	}

	/**
	 * Returns the number of ct scores.
	 *
	 * @return the number of ct scores
	 */
	@Override
	public int getCTScoresCount() {
		return _ctScoreLocalService.getCTScoresCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ctScoreLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ctScoreLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ctScoreLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public com.liferay.change.tracking.model.CTScore incrementScore(
		long ctCollectionId, int score) {

		return _ctScoreLocalService.incrementScore(ctCollectionId, score);
	}

	/**
	 * Updates the ct score in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect CTScoreLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ctScore the ct score
	 * @return the ct score that was updated
	 */
	@Override
	public com.liferay.change.tracking.model.CTScore updateCTScore(
		com.liferay.change.tracking.model.CTScore ctScore) {

		return _ctScoreLocalService.updateCTScore(ctScore);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ctScoreLocalService.getBasePersistence();
	}

	@Override
	public CTScoreLocalService getWrappedService() {
		return _ctScoreLocalService;
	}

	@Override
	public void setWrappedService(CTScoreLocalService ctScoreLocalService) {
		_ctScoreLocalService = ctScoreLocalService;
	}

	private CTScoreLocalService _ctScoreLocalService;

}