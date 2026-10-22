/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link UserTrackerLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see UserTrackerLocalService
 * @generated
 */
public class UserTrackerLocalServiceWrapper
	implements ServiceWrapper<UserTrackerLocalService>,
			   UserTrackerLocalService {

	public UserTrackerLocalServiceWrapper() {
		this(null);
	}

	public UserTrackerLocalServiceWrapper(
		UserTrackerLocalService userTrackerLocalService) {

		_userTrackerLocalService = userTrackerLocalService;
	}

	@Override
	public com.liferay.portal.kernel.model.UserTracker addUserTracker(
		long companyId, long userId, java.util.Date modifiedDate,
		String sessionId, String remoteAddr, String remoteHost,
		String userAgent,
		java.util.List<com.liferay.portal.kernel.model.UserTrackerPath>
			userTrackerPaths) {

		return _userTrackerLocalService.addUserTracker(
			companyId, userId, modifiedDate, sessionId, remoteAddr, remoteHost,
			userAgent, userTrackerPaths);
	}

	/**
	 * Adds the user tracker to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserTrackerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userTracker the user tracker
	 * @return the user tracker that was added
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTracker addUserTracker(
		com.liferay.portal.kernel.model.UserTracker userTracker) {

		return _userTrackerLocalService.addUserTracker(userTracker);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new user tracker with the primary key. Does not add the user tracker to the database.
	 *
	 * @param userTrackerId the primary key for the new user tracker
	 * @return the new user tracker
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTracker createUserTracker(
		long userTrackerId) {

		return _userTrackerLocalService.createUserTracker(userTrackerId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerLocalService.deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the user tracker with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserTrackerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userTrackerId the primary key of the user tracker
	 * @return the user tracker that was removed
	 * @throws PortalException if a user tracker with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTracker deleteUserTracker(
			long userTrackerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerLocalService.deleteUserTracker(userTrackerId);
	}

	/**
	 * Deletes the user tracker from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserTrackerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userTracker the user tracker
	 * @return the user tracker that was removed
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTracker deleteUserTracker(
		com.liferay.portal.kernel.model.UserTracker userTracker) {

		return _userTrackerLocalService.deleteUserTracker(userTracker);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _userTrackerLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _userTrackerLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _userTrackerLocalService.dynamicQuery();
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

		return _userTrackerLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerModelImpl</code>.
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

		return _userTrackerLocalService.dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerModelImpl</code>.
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

		return _userTrackerLocalService.dynamicQuery(
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

		return _userTrackerLocalService.dynamicQueryCount(dynamicQuery);
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

		return _userTrackerLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.kernel.model.UserTracker fetchUserTracker(
		long userTrackerId) {

		return _userTrackerLocalService.fetchUserTracker(userTrackerId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _userTrackerLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _userTrackerLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _userTrackerLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerLocalService.getPersistedModel(primaryKeyObj);
	}

	/**
	 * Returns the user tracker with the primary key.
	 *
	 * @param userTrackerId the primary key of the user tracker
	 * @return the user tracker
	 * @throws PortalException if a user tracker with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTracker getUserTracker(
			long userTrackerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _userTrackerLocalService.getUserTracker(userTrackerId);
	}

	/**
	 * Returns a range of all the user trackers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of user trackers
	 * @param end the upper bound of the range of user trackers (not inclusive)
	 * @return the range of user trackers
	 */
	@Override
	public java.util.List<com.liferay.portal.kernel.model.UserTracker>
		getUserTrackers(int start, int end) {

		return _userTrackerLocalService.getUserTrackers(start, end);
	}

	/**
	 * Returns the number of user trackers.
	 *
	 * @return the number of user trackers
	 */
	@Override
	public int getUserTrackersCount() {
		return _userTrackerLocalService.getUserTrackersCount();
	}

	/**
	 * Updates the user tracker in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect UserTrackerLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param userTracker the user tracker
	 * @return the user tracker that was updated
	 */
	@Override
	public com.liferay.portal.kernel.model.UserTracker updateUserTracker(
		com.liferay.portal.kernel.model.UserTracker userTracker) {

		return _userTrackerLocalService.updateUserTracker(userTracker);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _userTrackerLocalService.getBasePersistence();
	}

	@Override
	public UserTrackerLocalService getWrappedService() {
		return _userTrackerLocalService;
	}

	@Override
	public void setWrappedService(
		UserTrackerLocalService userTrackerLocalService) {

		_userTrackerLocalService = userTrackerLocalService;
	}

	private UserTrackerLocalService _userTrackerLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:2111857098