/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link RecentLayoutRevisionLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutRevisionLocalService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class RecentLayoutRevisionLocalServiceWrapper
	implements RecentLayoutRevisionLocalService,
			   ServiceWrapper<RecentLayoutRevisionLocalService> {

	public RecentLayoutRevisionLocalServiceWrapper() {
		this(null);
	}

	public RecentLayoutRevisionLocalServiceWrapper(
		RecentLayoutRevisionLocalService recentLayoutRevisionLocalService) {

		_recentLayoutRevisionLocalService = recentLayoutRevisionLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
			addRecentLayoutRevision(
				long userId, long layoutRevisionId, long layoutSetBranchId,
				long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutRevisionLocalService.addRecentLayoutRevision(
			userId, layoutRevisionId, layoutSetBranchId, plid);
	}

	/**
	 * Adds the recent layout revision to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutRevision the recent layout revision
	 * @return the recent layout revision that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
		addRecentLayoutRevision(
			com.liferay.portal.kernel.model.RecentLayoutRevision
				recentLayoutRevision) {

		return _recentLayoutRevisionLocalService.addRecentLayoutRevision(
			recentLayoutRevision);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutRevisionLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new recent layout revision with the primary key. Does not add the recent layout revision to the database.
	 *
	 * @param recentLayoutRevisionId the primary key for the new recent layout revision
	 * @return the new recent layout revision
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
		createRecentLayoutRevision(long recentLayoutRevisionId) {

		return _recentLayoutRevisionLocalService.createRecentLayoutRevision(
			recentLayoutRevisionId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutRevisionLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the recent layout revision with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutRevisionId the primary key of the recent layout revision
	 * @return the recent layout revision that was removed
	 * @throws PortalException if a recent layout revision with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
			deleteRecentLayoutRevision(long recentLayoutRevisionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutRevisionLocalService.deleteRecentLayoutRevision(
			recentLayoutRevisionId);
	}

	/**
	 * Deletes the recent layout revision from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutRevision the recent layout revision
	 * @return the recent layout revision that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
		deleteRecentLayoutRevision(
			com.liferay.portal.kernel.model.RecentLayoutRevision
				recentLayoutRevision) {

		return _recentLayoutRevisionLocalService.deleteRecentLayoutRevision(
			recentLayoutRevision);
	}

	@Override
	public void deleteRecentLayoutRevisions(long layoutRevisionId) {
		_recentLayoutRevisionLocalService.deleteRecentLayoutRevisions(
			layoutRevisionId);
	}

	@Override
	public void deleteUserRecentLayoutRevisions(long userId) {
		_recentLayoutRevisionLocalService.deleteUserRecentLayoutRevisions(
			userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _recentLayoutRevisionLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _recentLayoutRevisionLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _recentLayoutRevisionLocalService.dynamicQuery();
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

		return _recentLayoutRevisionLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutRevisionModelImpl</code>.
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

		return _recentLayoutRevisionLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutRevisionModelImpl</code>.
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

		return _recentLayoutRevisionLocalService.dynamicQuery(
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

		return _recentLayoutRevisionLocalService.dynamicQueryCount(
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

		return _recentLayoutRevisionLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
		fetchRecentLayoutRevision(long recentLayoutRevisionId) {

		return _recentLayoutRevisionLocalService.fetchRecentLayoutRevision(
			recentLayoutRevisionId);
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
		fetchRecentLayoutRevision(
			long userId, long layoutSetBranchId, long plid) {

		return _recentLayoutRevisionLocalService.fetchRecentLayoutRevision(
			userId, layoutSetBranchId, plid);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _recentLayoutRevisionLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _recentLayoutRevisionLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _recentLayoutRevisionLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutRevisionLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the recent layout revision with the primary key.
	 *
	 * @param recentLayoutRevisionId the primary key of the recent layout revision
	 * @return the recent layout revision
	 * @throws PortalException if a recent layout revision with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
			getRecentLayoutRevision(long recentLayoutRevisionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutRevisionLocalService.getRecentLayoutRevision(
			recentLayoutRevisionId);
	}

	/**
	 * Returns a range of all the recent layout revisions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutRevisionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of recent layout revisions
	 * @param end the upper bound of the range of recent layout revisions (not inclusive)
	 * @return the range of recent layout revisions
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.RecentLayoutRevision>
		getRecentLayoutRevisions(int start, int end) {

		return _recentLayoutRevisionLocalService.getRecentLayoutRevisions(
			start, end);
	}

	/**
	 * Returns the number of recent layout revisions.
	 *
	 * @return the number of recent layout revisions
	 */
	@Override
	public int getRecentLayoutRevisionsCount() {
		return _recentLayoutRevisionLocalService.
			getRecentLayoutRevisionsCount();
	}

	/**
	 * Updates the recent layout revision in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutRevisionLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutRevision the recent layout revision
	 * @return the recent layout revision that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutRevision
		updateRecentLayoutRevision(
			com.liferay.portal.kernel.model.RecentLayoutRevision
				recentLayoutRevision) {

		return _recentLayoutRevisionLocalService.updateRecentLayoutRevision(
			recentLayoutRevision);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _recentLayoutRevisionLocalService.getBasePersistence();
	}

	@Override
	public RecentLayoutRevisionLocalService getWrappedService() {
		return _recentLayoutRevisionLocalService;
	}

	@Override
	public void setWrappedService(
		RecentLayoutRevisionLocalService recentLayoutRevisionLocalService) {

		_recentLayoutRevisionLocalService = recentLayoutRevisionLocalService;
	}

	private RecentLayoutRevisionLocalService _recentLayoutRevisionLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1307076218