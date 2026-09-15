/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.RecentLayoutBranch;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for RecentLayoutBranch. This utility wraps
 * <code>com.liferay.portal.service.impl.RecentLayoutBranchLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see RecentLayoutBranchLocalService
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 * @generated
 */
@Deprecated
public class RecentLayoutBranchLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.RecentLayoutBranchLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static RecentLayoutBranch addRecentLayoutBranch(
			long userId, long layoutBranchId, long layoutSetBranchId, long plid)
		throws PortalException {

		return getService().addRecentLayoutBranch(
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
	public static RecentLayoutBranch addRecentLayoutBranch(
		RecentLayoutBranch recentLayoutBranch) {

		return getService().addRecentLayoutBranch(recentLayoutBranch);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new recent layout branch with the primary key. Does not add the recent layout branch to the database.
	 *
	 * @param recentLayoutBranchId the primary key for the new recent layout branch
	 * @return the new recent layout branch
	 */
	public static RecentLayoutBranch createRecentLayoutBranch(
		long recentLayoutBranchId) {

		return getService().createRecentLayoutBranch(recentLayoutBranchId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
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
	public static RecentLayoutBranch deleteRecentLayoutBranch(
			long recentLayoutBranchId)
		throws PortalException {

		return getService().deleteRecentLayoutBranch(recentLayoutBranchId);
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
	public static RecentLayoutBranch deleteRecentLayoutBranch(
		RecentLayoutBranch recentLayoutBranch) {

		return getService().deleteRecentLayoutBranch(recentLayoutBranch);
	}

	public static void deleteRecentLayoutBranches(long layoutBranchId) {
		getService().deleteRecentLayoutBranches(layoutBranchId);
	}

	public static void deleteUserRecentLayoutBranches(long userId) {
		getService().deleteUserRecentLayoutBranches(userId);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static RecentLayoutBranch fetchRecentLayoutBranch(
		long recentLayoutBranchId) {

		return getService().fetchRecentLayoutBranch(recentLayoutBranchId);
	}

	public static RecentLayoutBranch fetchRecentLayoutBranch(
		long userId, long layoutSetBranchId, long plid) {

		return getService().fetchRecentLayoutBranch(
			userId, layoutSetBranchId, plid);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the recent layout branch with the primary key.
	 *
	 * @param recentLayoutBranchId the primary key of the recent layout branch
	 * @return the recent layout branch
	 * @throws PortalException if a recent layout branch with the primary key could not be found
	 */
	public static RecentLayoutBranch getRecentLayoutBranch(
			long recentLayoutBranchId)
		throws PortalException {

		return getService().getRecentLayoutBranch(recentLayoutBranchId);
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
	public static List<RecentLayoutBranch> getRecentLayoutBranchs(
		int start, int end) {

		return getService().getRecentLayoutBranchs(start, end);
	}

	/**
	 * Returns the number of recent layout branches.
	 *
	 * @return the number of recent layout branches
	 */
	public static int getRecentLayoutBranchsCount() {
		return getService().getRecentLayoutBranchsCount();
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
	public static RecentLayoutBranch updateRecentLayoutBranch(
		RecentLayoutBranch recentLayoutBranch) {

		return getService().updateRecentLayoutBranch(recentLayoutBranch);
	}

	public static RecentLayoutBranchLocalService getService() {
		return _service;
	}

	public static void setService(RecentLayoutBranchLocalService service) {
		_service = service;
	}

	private static volatile RecentLayoutBranchLocalService _service;

}
// LIFERAY-SERVICE-BUILDER-HASH:78867522