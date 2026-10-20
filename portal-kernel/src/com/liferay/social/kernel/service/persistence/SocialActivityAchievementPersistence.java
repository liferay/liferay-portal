/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.social.kernel.exception.NoSuchActivityAchievementException;
import com.liferay.social.kernel.model.SocialActivityAchievement;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the social activity achievement service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityAchievementUtil
 * @generated
 */
@ProviderType
public interface SocialActivityAchievementPersistence
	extends BasePersistence<SocialActivityAchievement>,
			CTPersistence<SocialActivityAchievement> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link SocialActivityAchievementUtil} to access the social activity achievement persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity achievements
	 */
	public java.util.List<SocialActivityAchievement> findByG_U(
		long groupId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	public SocialActivityAchievement findByG_U_First(
			long groupId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<SocialActivityAchievement> orderByComparator)
		throws NoSuchActivityAchievementException;

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	public SocialActivityAchievement fetchByG_U_First(
		long groupId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator
			<SocialActivityAchievement> orderByComparator);

	/**
	 * Removes all the social activity achievements where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	public void removeByG_U(long groupId, long userId);

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching social activity achievements
	 */
	public int countByG_U(long groupId, long userId);

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity achievements
	 */
	public java.util.List<SocialActivityAchievement> findByG_N(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	public SocialActivityAchievement findByG_N_First(
			long groupId, String name,
			com.liferay.portal.kernel.util.OrderByComparator
				<SocialActivityAchievement> orderByComparator)
		throws NoSuchActivityAchievementException;

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	public SocialActivityAchievement fetchByG_N_First(
		long groupId, String name,
		com.liferay.portal.kernel.util.OrderByComparator
			<SocialActivityAchievement> orderByComparator);

	/**
	 * Removes all the social activity achievements where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	public void removeByG_N(long groupId, String name);

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching social activity achievements
	 */
	public int countByG_N(long groupId, String name);

	/**
	 * Returns the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; or throws a <code>NoSuchActivityAchievementException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	public SocialActivityAchievement findByG_U_N(
			long groupId, long userId, String name)
		throws NoSuchActivityAchievementException;

	/**
	 * Returns the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	public SocialActivityAchievement fetchByG_U_N(
		long groupId, long userId, String name, boolean useFinderCache);

	/**
	 * Removes the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the social activity achievement that was removed
	 */
	public SocialActivityAchievement removeByG_U_N(
			long groupId, long userId, String name)
		throws NoSuchActivityAchievementException;

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and userId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the number of matching social activity achievements
	 */
	public int countByG_U_N(long groupId, long userId, String name);

	/**
	 * Creates a new social activity achievement with the primary key. Does not add the social activity achievement to the database.
	 *
	 * @param activityAchievementId the primary key for the new social activity achievement
	 * @return the new social activity achievement
	 */
	public SocialActivityAchievement create(long activityAchievementId);

	/**
	 * Removes the social activity achievement with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement that was removed
	 * @throws NoSuchActivityAchievementException if a social activity achievement with the primary key could not be found
	 */
	public SocialActivityAchievement remove(long activityAchievementId)
		throws NoSuchActivityAchievementException;

	public SocialActivityAchievement updateImpl(
		SocialActivityAchievement socialActivityAchievement);

	/**
	 * Returns the social activity achievement with the primary key or throws a <code>NoSuchActivityAchievementException</code> if it could not be found.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement
	 * @throws NoSuchActivityAchievementException if a social activity achievement with the primary key could not be found
	 */
	public SocialActivityAchievement findByPrimaryKey(
			long activityAchievementId)
		throws NoSuchActivityAchievementException;

	/**
	 * Returns the social activity achievement with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement, or <code>null</code> if a social activity achievement with the primary key could not be found
	 */
	public SocialActivityAchievement fetchByPrimaryKey(
		long activityAchievementId);

	/**
	 * Returns the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	public default SocialActivityAchievement fetchByG_U_N(
		long groupId, long userId, String name) {

		return fetchByG_U_N(groupId, userId, name, true);
	}

	/**
	 * Returns all the social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching social activity achievements
	 */
	public default java.util.List<SocialActivityAchievement> findByG_U(
		long groupId, long userId) {

		return findByG_U(
			groupId, userId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @return the range of matching social activity achievements
	 */
	public default java.util.List<SocialActivityAchievement> findByG_U(
		long groupId, long userId, int start, int end) {

		return findByG_U(groupId, userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activity achievements
	 */
	public default java.util.List<SocialActivityAchievement> findByG_U(
		long groupId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SocialActivityAchievement> orderByComparator) {

		return findByG_U(groupId, userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching social activity achievements
	 */
	public default java.util.List<SocialActivityAchievement> findByG_N(
		long groupId, String name) {

		return findByG_N(
			groupId, name, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @return the range of matching social activity achievements
	 */
	public default java.util.List<SocialActivityAchievement> findByG_N(
		long groupId, String name, int start, int end) {

		return findByG_N(groupId, name, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching social activity achievements
	 */
	public default java.util.List<SocialActivityAchievement> findByG_N(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<SocialActivityAchievement> orderByComparator) {

		return findByG_N(groupId, name, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1008892698