/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link RecentLayoutBranchLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutBranchLocalService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class RecentLayoutBranchLocalServiceWrapper
	implements RecentLayoutBranchLocalService,
			   ServiceWrapper<RecentLayoutBranchLocalService> {

	public RecentLayoutBranchLocalServiceWrapper() {
		this(null);
	}

	public RecentLayoutBranchLocalServiceWrapper(
		RecentLayoutBranchLocalService recentLayoutBranchLocalService) {

		_recentLayoutBranchLocalService = recentLayoutBranchLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
			addRecentLayoutBranch(
				long userId, long layoutBranchId, long layoutSetBranchId,
				long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutBranchLocalService.addRecentLayoutBranch(
			userId, layoutBranchId, layoutSetBranchId, plid);
	}

	/**
	 * Adds the recent layout branch to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutBranch the recent layout branch
	 * @return the recent layout branch that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
		addRecentLayoutBranch(
			com.liferay.portal.kernel.model.RecentLayoutBranch
				recentLayoutBranch) {

		return _recentLayoutBranchLocalService.addRecentLayoutBranch(
			recentLayoutBranch);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutBranchLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new recent layout branch with the primary key. Does not add the recent layout branch to the database.
	 *
	 * @param recentLayoutBranchId the primary key for the new recent layout branch
	 * @return the new recent layout branch
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
		createRecentLayoutBranch(long recentLayoutBranchId) {

		return _recentLayoutBranchLocalService.createRecentLayoutBranch(
			recentLayoutBranchId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutBranchLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the recent layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch that was removed
	 * @throws PortalException if a recent layout branch with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
			deleteRecentLayoutBranch(long recentLayoutBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutBranchLocalService.deleteRecentLayoutBranch(
			recentLayoutBranchId);
	}

	/**
	 * Deletes the recent layout branch from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutBranch the recent layout branch
	 * @return the recent layout branch that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
		deleteRecentLayoutBranch(
			com.liferay.portal.kernel.model.RecentLayoutBranch
				recentLayoutBranch) {

		return _recentLayoutBranchLocalService.deleteRecentLayoutBranch(
			recentLayoutBranch);
	}

	@Override
	public void deleteRecentLayoutBranches(long layoutBranchId) {
		_recentLayoutBranchLocalService.deleteRecentLayoutBranches(
			layoutBranchId);
	}

	@Override
	public void deleteUserRecentLayoutBranches(long userId) {
		_recentLayoutBranchLocalService.deleteUserRecentLayoutBranches(userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _recentLayoutBranchLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _recentLayoutBranchLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _recentLayoutBranchLocalService.dynamicQuery();
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

		return _recentLayoutBranchLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
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

		return _recentLayoutBranchLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
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

		return _recentLayoutBranchLocalService.dynamicQuery(
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

		return _recentLayoutBranchLocalService.dynamicQueryCount(dynamicQuery);
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

		return _recentLayoutBranchLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
		fetchRecentLayoutBranch(long recentLayoutBranchId) {

		return _recentLayoutBranchLocalService.fetchRecentLayoutBranch(
			recentLayoutBranchId);
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
		fetchRecentLayoutBranch(
			long userId, long layoutSetBranchId, long plid) {

		return _recentLayoutBranchLocalService.fetchRecentLayoutBranch(
			userId, layoutSetBranchId, plid);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _recentLayoutBranchLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _recentLayoutBranchLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _recentLayoutBranchLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutBranchLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the recent layout branch with the primary key.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch
	 * @throws PortalException if a recent layout branch with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
			getRecentLayoutBranch(long recentLayoutBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutBranchLocalService.getRecentLayoutBranch(
			recentLayoutBranchId);
	}

	/**
	 * Returns a range of all the recent layout branches.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of recent layout branches
	 * @param end the upper bound of the range of recent layout branches (not inclusive)
	 * @return the range of recent layout branches
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.RecentLayoutBranch>
		getRecentLayoutBranchs(int start, int end) {

		return _recentLayoutBranchLocalService.getRecentLayoutBranchs(
			start, end);
	}

	/**
	 * Returns the number of recent layout branches.
	 *
	 * @return the number of recent layout branches
	 */
	@Override
	public int getRecentLayoutBranchsCount() {
		return _recentLayoutBranchLocalService.getRecentLayoutBranchsCount();
	}

	/**
	 * Updates the recent layout branch in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutBranch the recent layout branch
	 * @return the recent layout branch that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutBranch
		updateRecentLayoutBranch(
			com.liferay.portal.kernel.model.RecentLayoutBranch
				recentLayoutBranch) {

		return _recentLayoutBranchLocalService.updateRecentLayoutBranch(
			recentLayoutBranch);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _recentLayoutBranchLocalService.getBasePersistence();
	}

	@Override
	public RecentLayoutBranchLocalService getWrappedService() {
		return _recentLayoutBranchLocalService;
	}

	@Override
	public void setWrappedService(
		RecentLayoutBranchLocalService recentLayoutBranchLocalService) {

		_recentLayoutBranchLocalService = recentLayoutBranchLocalService;
	}

	private RecentLayoutBranchLocalService _recentLayoutBranchLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-811876044