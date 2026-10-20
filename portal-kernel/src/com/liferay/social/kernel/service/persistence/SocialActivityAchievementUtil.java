/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.social.kernel.model.SocialActivityAchievement;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the social activity achievement service. This utility wraps <code>com.liferay.portlet.social.service.persistence.impl.SocialActivityAchievementPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SocialActivityAchievementPersistence
 * @generated
 */
public class SocialActivityAchievementUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<SocialActivityAchievement> socialActivityAchievements) {

		getPersistence().cacheResult(socialActivityAchievements);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		SocialActivityAchievement socialActivityAchievement) {

		getPersistence().cacheResult(socialActivityAchievement);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(
		SocialActivityAchievement socialActivityAchievement) {

		getPersistence().clearCache(socialActivityAchievement);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, SocialActivityAchievement>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SocialActivityAchievement> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SocialActivityAchievement> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SocialActivityAchievement> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SocialActivityAchievement update(
		SocialActivityAchievement socialActivityAchievement) {

		return getPersistence().update(socialActivityAchievement);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SocialActivityAchievement update(
		SocialActivityAchievement socialActivityAchievement,
		ServiceContext serviceContext) {

		return getPersistence().update(
			socialActivityAchievement, serviceContext);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity achievements
	 */
	public static List<SocialActivityAchievement> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U(
			groupId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	public static SocialActivityAchievement findByG_U_First(
			long groupId, long userId,
			OrderByComparator<SocialActivityAchievement> orderByComparator)
		throws com.liferay.social.kernel.exception.
			NoSuchActivityAchievementException {

		return getPersistence().findByG_U_First(
			groupId, userId, orderByComparator);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	public static SocialActivityAchievement fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return getPersistence().fetchByG_U_First(
			groupId, userId, orderByComparator);
	}

	/**
	 * Removes all the social activity achievements where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	public static void removeByG_U(long groupId, long userId) {
		getPersistence().removeByG_U(groupId, userId);
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching social activity achievements
	 */
	public static int countByG_U(long groupId, long userId) {
		return getPersistence().countByG_U(groupId, userId);
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
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity achievements
	 */
	public static List<SocialActivityAchievement> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_N(
			groupId, name, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	public static SocialActivityAchievement findByG_N_First(
			long groupId, String name,
			OrderByComparator<SocialActivityAchievement> orderByComparator)
		throws com.liferay.social.kernel.exception.
			NoSuchActivityAchievementException {

		return getPersistence().findByG_N_First(
			groupId, name, orderByComparator);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	public static SocialActivityAchievement fetchByG_N_First(
		long groupId, String name,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return getPersistence().fetchByG_N_First(
			groupId, name, orderByComparator);
	}

	/**
	 * Removes all the social activity achievements where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	public static void removeByG_N(long groupId, String name) {
		getPersistence().removeByG_N(groupId, name);
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching social activity achievements
	 */
	public static int countByG_N(long groupId, String name) {
		return getPersistence().countByG_N(groupId, name);
	}

	/**
	 * Returns the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; or throws a <code>NoSuchActivityAchievementException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	public static SocialActivityAchievement findByG_U_N(
			long groupId, long userId, String name)
		throws com.liferay.social.kernel.exception.
			NoSuchActivityAchievementException {

		return getPersistence().findByG_U_N(groupId, userId, name);
	}

	/**
	 * Returns the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	public static SocialActivityAchievement fetchByG_U_N(
		long groupId, long userId, String name, boolean useFinderCache) {

		return getPersistence().fetchByG_U_N(
			groupId, userId, name, useFinderCache);
	}

	/**
	 * Removes the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the social activity achievement that was removed
	 */
	public static SocialActivityAchievement removeByG_U_N(
			long groupId, long userId, String name)
		throws com.liferay.social.kernel.exception.
			NoSuchActivityAchievementException {

		return getPersistence().removeByG_U_N(groupId, userId, name);
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and userId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the number of matching social activity achievements
	 */
	public static int countByG_U_N(long groupId, long userId, String name) {
		return getPersistence().countByG_U_N(groupId, userId, name);
	}

	/**
	 * Creates a new social activity achievement with the primary key. Does not add the social activity achievement to the database.
	 *
	 * @param activityAchievementId the primary key for the new social activity achievement
	 * @return the new social activity achievement
	 */
	public static SocialActivityAchievement create(long activityAchievementId) {
		return getPersistence().create(activityAchievementId);
	}

	/**
	 * Removes the social activity achievement with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement that was removed
	 * @throws NoSuchActivityAchievementException if a social activity achievement with the primary key could not be found
	 */
	public static SocialActivityAchievement remove(long activityAchievementId)
		throws com.liferay.social.kernel.exception.
			NoSuchActivityAchievementException {

		return getPersistence().remove(activityAchievementId);
	}

	public static SocialActivityAchievement updateImpl(
		SocialActivityAchievement socialActivityAchievement) {

		return getPersistence().updateImpl(socialActivityAchievement);
	}

	/**
	 * Returns the social activity achievement with the primary key or throws a <code>NoSuchActivityAchievementException</code> if it could not be found.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement
	 * @throws NoSuchActivityAchievementException if a social activity achievement with the primary key could not be found
	 */
	public static SocialActivityAchievement findByPrimaryKey(
			long activityAchievementId)
		throws com.liferay.social.kernel.exception.
			NoSuchActivityAchievementException {

		return getPersistence().findByPrimaryKey(activityAchievementId);
	}

	/**
	 * Returns the social activity achievement with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement, or <code>null</code> if a social activity achievement with the primary key could not be found
	 */
	public static SocialActivityAchievement fetchByPrimaryKey(
		long activityAchievementId) {

		return getPersistence().fetchByPrimaryKey(activityAchievementId);
	}

	/**
	 * Returns the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	public static SocialActivityAchievement fetchByG_U_N(
		long groupId, long userId, String name) {

		return getPersistence().fetchByG_U_N(groupId, userId, name);
	}

	/**
	 * Returns all the social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching social activity achievements
	 */
	public static List<SocialActivityAchievement> findByG_U(
		long groupId, long userId) {

		return getPersistence().findByG_U(groupId, userId);
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
	public static List<SocialActivityAchievement> findByG_U(
		long groupId, long userId, int start, int end) {

		return getPersistence().findByG_U(groupId, userId, start, end);
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
	public static List<SocialActivityAchievement> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return getPersistence().findByG_U(
			groupId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching social activity achievements
	 */
	public static List<SocialActivityAchievement> findByG_N(
		long groupId, String name) {

		return getPersistence().findByG_N(groupId, name);
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
	public static List<SocialActivityAchievement> findByG_N(
		long groupId, String name, int start, int end) {

		return getPersistence().findByG_N(groupId, name, start, end);
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
	public static List<SocialActivityAchievement> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return getPersistence().findByG_N(
			groupId, name, start, end, orderByComparator);
	}

	public static SocialActivityAchievementPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		SocialActivityAchievementPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile SocialActivityAchievementPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1895467357