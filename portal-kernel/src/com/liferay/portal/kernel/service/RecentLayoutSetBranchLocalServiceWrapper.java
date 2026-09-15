/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link RecentLayoutSetBranchLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutSetBranchLocalService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class RecentLayoutSetBranchLocalServiceWrapper
	implements RecentLayoutSetBranchLocalService,
			   ServiceWrapper<RecentLayoutSetBranchLocalService> {

	public RecentLayoutSetBranchLocalServiceWrapper() {
		this(null);
	}

	public RecentLayoutSetBranchLocalServiceWrapper(
		RecentLayoutSetBranchLocalService recentLayoutSetBranchLocalService) {

		_recentLayoutSetBranchLocalService = recentLayoutSetBranchLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
			addRecentLayoutSetBranch(
				long userId, long layoutSetBranchId, long layoutSetId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutSetBranchLocalService.addRecentLayoutSetBranch(
			userId, layoutSetBranchId, layoutSetId);
	}

	/**
	 * Adds the recent layout set branch to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutSetBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutSetBranch the recent layout set branch
	 * @return the recent layout set branch that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
		addRecentLayoutSetBranch(
			com.liferay.portal.kernel.model.RecentLayoutSetBranch
				recentLayoutSetBranch) {

		return _recentLayoutSetBranchLocalService.addRecentLayoutSetBranch(
			recentLayoutSetBranch);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutSetBranchLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new recent layout set branch with the primary key. Does not add the recent layout set branch to the database.
	 *
	 * @param recentLayoutSetBranchId the primary key for the new recent layout set branch
	 * @return the new recent layout set branch
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
		createRecentLayoutSetBranch(long recentLayoutSetBranchId) {

		return _recentLayoutSetBranchLocalService.createRecentLayoutSetBranch(
			recentLayoutSetBranchId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutSetBranchLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the recent layout set branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutSetBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch that was removed
	 * @throws PortalException if a recent layout set branch with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
			deleteRecentLayoutSetBranch(long recentLayoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutSetBranchLocalService.deleteRecentLayoutSetBranch(
			recentLayoutSetBranchId);
	}

	/**
	 * Deletes the recent layout set branch from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutSetBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutSetBranch the recent layout set branch
	 * @return the recent layout set branch that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
		deleteRecentLayoutSetBranch(
			com.liferay.portal.kernel.model.RecentLayoutSetBranch
				recentLayoutSetBranch) {

		return _recentLayoutSetBranchLocalService.deleteRecentLayoutSetBranch(
			recentLayoutSetBranch);
	}

	@Override
	public void deleteRecentLayoutSetBranches(long layoutSetBranchId) {
		_recentLayoutSetBranchLocalService.deleteRecentLayoutSetBranches(
			layoutSetBranchId);
	}

	@Override
	public void deleteUserRecentLayoutSetBranches(long userId) {
		_recentLayoutSetBranchLocalService.deleteUserRecentLayoutSetBranches(
			userId);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _recentLayoutSetBranchLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _recentLayoutSetBranchLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _recentLayoutSetBranchLocalService.dynamicQuery();
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

		return _recentLayoutSetBranchLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
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

		return _recentLayoutSetBranchLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
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

		return _recentLayoutSetBranchLocalService.dynamicQuery(
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

		return _recentLayoutSetBranchLocalService.dynamicQueryCount(
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

		return _recentLayoutSetBranchLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
		fetchRecentLayoutSetBranch(long recentLayoutSetBranchId) {

		return _recentLayoutSetBranchLocalService.fetchRecentLayoutSetBranch(
			recentLayoutSetBranchId);
	}

	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
		fetchRecentLayoutSetBranch(long userId, long layoutSetId) {

		return _recentLayoutSetBranchLocalService.fetchRecentLayoutSetBranch(
			userId, layoutSetId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _recentLayoutSetBranchLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _recentLayoutSetBranchLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _recentLayoutSetBranchLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutSetBranchLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns the recent layout set branch with the primary key.
	 *
	 * @param recentLayoutSetBranchId the primary key of the recent layout set branch
	 * @return the recent layout set branch
	 * @throws PortalException if a recent layout set branch with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
			getRecentLayoutSetBranch(long recentLayoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _recentLayoutSetBranchLocalService.getRecentLayoutSetBranch(
			recentLayoutSetBranchId);
	}

	/**
	 * Returns a range of all the recent layout set branches.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RecentLayoutSetBranchModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of recent layout set branches
	 * @param end the upper bound of the range of recent layout set branches (not inclusive)
	 * @return the range of recent layout set branches
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.RecentLayoutSetBranch>
		getRecentLayoutSetBranchs(int start, int end) {

		return _recentLayoutSetBranchLocalService.getRecentLayoutSetBranchs(
			start, end);
	}

	/**
	 * Returns the number of recent layout set branches.
	 *
	 * @return the number of recent layout set branches
	 */
	@Override
	public int getRecentLayoutSetBranchsCount() {
		return _recentLayoutSetBranchLocalService.
			getRecentLayoutSetBranchsCount();
	}

	/**
	 * Updates the recent layout set branch in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect RecentLayoutSetBranchLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param recentLayoutSetBranch the recent layout set branch
	 * @return the recent layout set branch that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.RecentLayoutSetBranch
		updateRecentLayoutSetBranch(
			com.liferay.portal.kernel.model.RecentLayoutSetBranch
				recentLayoutSetBranch) {

		return _recentLayoutSetBranchLocalService.updateRecentLayoutSetBranch(
			recentLayoutSetBranch);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _recentLayoutSetBranchLocalService.getBasePersistence();
	}

	@Override
	public RecentLayoutSetBranchLocalService getWrappedService() {
		return _recentLayoutSetBranchLocalService;
	}

	@Override
	public void setWrappedService(
		RecentLayoutSetBranchLocalService recentLayoutSetBranchLocalService) {

		_recentLayoutSetBranchLocalService = recentLayoutSetBranchLocalService;
	}

	private RecentLayoutSetBranchLocalService
		_recentLayoutSetBranchLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1619890726