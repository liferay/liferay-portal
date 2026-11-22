/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ratings.kernel.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.ratings.kernel.model.RatingsStats;

/**
 * Provides a wrapper for {@link RatingsStatsLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RatingsStatsLocalService
 * @generated
 */
public class RatingsStatsLocalServiceWrapper
	implements RatingsStatsLocalService,
			   ServiceWrapper<RatingsStatsLocalService> {

	public RatingsStatsLocalServiceWrapper() {
		this(null);
	}

	public RatingsStatsLocalServiceWrapper(
		RatingsStatsLocalService ratingsStatsLocalService) {

		_ratingsStatsLocalService = ratingsStatsLocalService;
	}

	/**
	 * Adds the ratings stats to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RatingsStatsLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ratingsStats the ratings stats
	 * @return the ratings stats that was added
	 */
	@Override
	public RatingsStats addRatingsStats(RatingsStats ratingsStats) {
		return _ratingsStatsLocalService.addRatingsStats(ratingsStats);
	}

	@Override
	public RatingsStats addStats(long classNameId, long classPK) {
		return _ratingsStatsLocalService.addStats(classNameId, classPK);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ratingsStatsLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new ratings stats with the primary key. Does not add the ratings stats to the database.
	 *
	 * @param statsId the primary key for the new ratings stats
	 * @return the new ratings stats
	 */
	@Override
	public RatingsStats createRatingsStats(long statsId) {
		return _ratingsStatsLocalService.createRatingsStats(statsId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ratingsStatsLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the ratings stats with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RatingsStatsLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param statsId the primary key of the ratings stats
	 * @return the ratings stats that was removed
	 * @throws PortalException if a ratings stats with the primary key could not be found
	 */
	@Override
	public RatingsStats deleteRatingsStats(long statsId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ratingsStatsLocalService.deleteRatingsStats(statsId);
	}

	/**
	 * Deletes the ratings stats from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RatingsStatsLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ratingsStats the ratings stats
	 * @return the ratings stats that was removed
	 */
	@Override
	public RatingsStats deleteRatingsStats(RatingsStats ratingsStats) {
		return _ratingsStatsLocalService.deleteRatingsStats(ratingsStats);
	}

	@Override
	public void deleteStats(String className, long classPK) {
		_ratingsStatsLocalService.deleteStats(className, classPK);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ratingsStatsLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ratingsStatsLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ratingsStatsLocalService.dynamicQuery();
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

		return _ratingsStatsLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.ratings.model.impl.RatingsStatsModelImpl</code>.
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

		return _ratingsStatsLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.ratings.model.impl.RatingsStatsModelImpl</code>.
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

		return _ratingsStatsLocalService.dynamicQuery(
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

		return _ratingsStatsLocalService.dynamicQueryCount(dynamicQuery);
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

		return _ratingsStatsLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public RatingsStats fetchRatingsStats(long statsId) {
		return _ratingsStatsLocalService.fetchRatingsStats(statsId);
	}

	@Override
	public RatingsStats fetchStats(String className, long classPK) {
		return _ratingsStatsLocalService.fetchStats(className, classPK);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ratingsStatsLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ratingsStatsLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ratingsStatsLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ratingsStatsLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the ratings stats with the primary key.
	 *
	 * @param statsId the primary key of the ratings stats
	 * @return the ratings stats
	 * @throws PortalException if a ratings stats with the primary key could not be found
	 */
	@Override
	public RatingsStats getRatingsStats(long statsId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ratingsStatsLocalService.getRatingsStats(statsId);
	}

	/**
	 * Returns a range of all the ratings statses.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.ratings.model.impl.RatingsStatsModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ratings statses
	 * @param end the upper bound of the range of ratings statses (not inclusive)
	 * @return the range of ratings statses
	 */
	@Override
	public java.util.List<RatingsStats> getRatingsStatses(int start, int end) {
		return _ratingsStatsLocalService.getRatingsStatses(start, end);
	}

	/**
	 * Returns the number of ratings statses.
	 *
	 * @return the number of ratings statses
	 */
	@Override
	public int getRatingsStatsesCount() {
		return _ratingsStatsLocalService.getRatingsStatsesCount();
	}

	@Override
	public RatingsStats getStats(String className, long classPK)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ratingsStatsLocalService.getStats(className, classPK);
	}

	@Override
	public java.util.Map<Long, RatingsStats> getStats(
		String className, long[] classPKs) {

		return _ratingsStatsLocalService.getStats(className, classPKs);
	}

	/**
	 * Updates the ratings stats in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RatingsStatsLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ratingsStats the ratings stats
	 * @return the ratings stats that was updated
	 */
	@Override
	public RatingsStats updateRatingsStats(RatingsStats ratingsStats) {
		return _ratingsStatsLocalService.updateRatingsStats(ratingsStats);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _ratingsStatsLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<RatingsStats> getCTPersistence() {
		return _ratingsStatsLocalService.getCTPersistence();
	}

	@Override
	public Class<RatingsStats> getModelClass() {
		return _ratingsStatsLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<RatingsStats>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ratingsStatsLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public RatingsStatsLocalService getWrappedService() {
		return _ratingsStatsLocalService;
	}

	@Override
	public void setWrappedService(
		RatingsStatsLocalService ratingsStatsLocalService) {

		_ratingsStatsLocalService = ratingsStatsLocalService;
	}

	private RatingsStatsLocalService _ratingsStatsLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2048491459