/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link UserTrackerPathLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserTrackerPathLocalService
 * @generated
 */
public class UserTrackerPathLocalServiceWrapper
	implements ServiceWrapper<UserTrackerPathLocalService>,
			   UserTrackerPathLocalService {

	public UserTrackerPathLocalServiceWrapper() {
		this(null);
	}

	public UserTrackerPathLocalServiceWrapper(
		UserTrackerPathLocalService userTrackerPathLocalService) {

		_userTrackerPathLocalService = userTrackerPathLocalService;
	}

	/**
	 * Adds the user tracker path to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserTrackerPathLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userTrackerPath the user tracker path
	 * @return the user tracker path that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTrackerPath addUserTrackerPath(
		com.liferay.portal.kernel.model.UserTrackerPath userTrackerPath) {

		return _userTrackerPathLocalService.addUserTrackerPath(userTrackerPath);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerPathLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new user tracker path with the primary key. Does not add the user tracker path to the database.
	 *
	 * @param userTrackerPathId the primary key for the new user tracker path
	 * @return the new user tracker path
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTrackerPath
		createUserTrackerPath(long userTrackerPathId) {

		return _userTrackerPathLocalService.createUserTrackerPath(
			userTrackerPathId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerPathLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the user tracker path with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserTrackerPathLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userTrackerPathId the primary key of the user tracker path
	 * @return the user tracker path that was removed
	 * @throws PortalException if a user tracker path with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTrackerPath
			deleteUserTrackerPath(long userTrackerPathId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerPathLocalService.deleteUserTrackerPath(
			userTrackerPathId);
	}

	/**
	 * Deletes the user tracker path from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserTrackerPathLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userTrackerPath the user tracker path
	 * @return the user tracker path that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTrackerPath
		deleteUserTrackerPath(
			com.liferay.portal.kernel.model.UserTrackerPath userTrackerPath) {

		return _userTrackerPathLocalService.deleteUserTrackerPath(
			userTrackerPath);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _userTrackerPathLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _userTrackerPathLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _userTrackerPathLocalService.dynamicQuery();
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

		return _userTrackerPathLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerPathModelImpl</code>.
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

		return _userTrackerPathLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerPathModelImpl</code>.
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

		return _userTrackerPathLocalService.dynamicQuery(
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

		return _userTrackerPathLocalService.dynamicQueryCount(dynamicQuery);
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

		return _userTrackerPathLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.UserTrackerPath fetchUserTrackerPath(
		long userTrackerPathId) {

		return _userTrackerPathLocalService.fetchUserTrackerPath(
			userTrackerPathId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _userTrackerPathLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _userTrackerPathLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userTrackerPathLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerPathLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the user tracker path with the primary key.
	 *
	 * @param userTrackerPathId the primary key of the user tracker path
	 * @return the user tracker path
	 * @throws PortalException if a user tracker path with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTrackerPath getUserTrackerPath(
			long userTrackerPathId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerPathLocalService.getUserTrackerPath(
			userTrackerPathId);
	}

	/**
	 * Returns a range of all the user tracker paths.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerPathModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user tracker paths
	 * @param end the upper bound of the range of user tracker paths (not inclusive)
	 * @return the range of user tracker paths
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserTrackerPath>
		getUserTrackerPaths(int start, int end) {

		return _userTrackerPathLocalService.getUserTrackerPaths(start, end);
	}

	/**
	 * Returns the number of user tracker paths.
	 *
	 * @return the number of user tracker paths
	 */
	@Override
	public int getUserTrackerPathsCount() {
		return _userTrackerPathLocalService.getUserTrackerPathsCount();
	}

	/**
	 * Updates the user tracker path in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserTrackerPathLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userTrackerPath the user tracker path
	 * @return the user tracker path that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTrackerPath
		updateUserTrackerPath(
			com.liferay.portal.kernel.model.UserTrackerPath userTrackerPath) {

		return _userTrackerPathLocalService.updateUserTrackerPath(
			userTrackerPath);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _userTrackerPathLocalService.getBasePersistence();
	}

	@Override
	public UserTrackerPathLocalService getWrappedService() {
		return _userTrackerPathLocalService;
	}

	@Override
	public void setWrappedService(
		UserTrackerPathLocalService userTrackerPathLocalService) {

		_userTrackerPathLocalService = userTrackerPathLocalService;
	}

	private UserTrackerPathLocalService _userTrackerPathLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:1726917870