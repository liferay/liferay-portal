/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchUserTrackerException;
import com.liferay.portal.kernel.model.UserTracker;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the user tracker service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see UserTrackerUtil
 * @generated
 */
@ProviderType
public interface UserTrackerPersistence extends BasePersistence<UserTracker> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link UserTrackerUtil} to access the user tracker persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the user trackers where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user trackers
	 * @param end the upper bound of the range of user trackers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user trackers
	 */
	public java.util.List<UserTracker> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserTracker>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first user tracker in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker
	 * @throws NoSuchUserTrackerException if a matching user tracker could not be found
	 */
	public UserTracker findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator<UserTracker>
				orderByComparator)
		throws NoSuchUserTrackerException;

	/**
	 * Returns the first user tracker in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user tracker, or <code>null</code> if a matching user tracker could not be found
	 */
	public UserTracker fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<UserTracker>
			orderByComparator);

	/**
	 * Removes all the user trackers where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of user trackers where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching user trackers
	 */
	public int countByUserId(long userId);

	/**
	 * Creates a new user tracker with the primary key. Does not add the user tracker to the database.
	 *
	 * @param userTrackerId the primary key for the new user tracker
	 * @return the new user tracker
	 */
	public UserTracker create(long userTrackerId);

	/**
	 * Removes the user tracker with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userTrackerId the primary key of the user tracker
	 * @return the user tracker that was removed
	 * @throws NoSuchUserTrackerException if a user tracker with the primary key could not be found
	 */
	public UserTracker remove(long userTrackerId)
		throws NoSuchUserTrackerException;

	public UserTracker updateImpl(UserTracker userTracker);

	/**
	 * Returns the user tracker with the primary key or throws a <code>NoSuchUserTrackerException</code> if it could not be found.
	 *
	 * @param userTrackerId the primary key of the user tracker
	 * @return the user tracker
	 * @throws NoSuchUserTrackerException if a user tracker with the primary key could not be found
	 */
	public UserTracker findByPrimaryKey(long userTrackerId)
		throws NoSuchUserTrackerException;

	/**
	 * Returns the user tracker with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userTrackerId the primary key of the user tracker
	 * @return the user tracker, or <code>null</code> if a user tracker with the primary key could not be found
	 */
	public UserTracker fetchByPrimaryKey(long userTrackerId);

	/**
	 * Returns all the user trackers where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching user trackers
	 */
	public default java.util.List<UserTracker> findByUserId(long userId) {
		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the user trackers where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user trackers
	 * @param end the upper bound of the range of user trackers (not inclusive)
	 * @return the range of matching user trackers
	 */
	public default java.util.List<UserTracker> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the user trackers where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user trackers
	 * @param end the upper bound of the range of user trackers (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user trackers
	 */
	public default java.util.List<UserTracker> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserTracker>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:389146486