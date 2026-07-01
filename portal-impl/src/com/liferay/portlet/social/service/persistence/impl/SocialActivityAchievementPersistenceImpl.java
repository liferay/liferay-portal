/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portlet.social.model.impl.SocialActivityAchievementImpl;
import com.liferay.portlet.social.model.impl.SocialActivityAchievementModelImpl;
import com.liferay.social.kernel.exception.NoSuchActivityAchievementException;
import com.liferay.social.kernel.model.SocialActivityAchievement;
import com.liferay.social.kernel.model.SocialActivityAchievementTable;
import com.liferay.social.kernel.service.persistence.SocialActivityAchievementPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivityAchievementUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the social activity achievement service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SocialActivityAchievementPersistenceImpl
	extends BasePersistenceImpl
		<SocialActivityAchievement, NoSuchActivityAchievementException>
	implements SocialActivityAchievementPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SocialActivityAchievementUtil</code> to access the social activity achievement persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SocialActivityAchievementImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<SocialActivityAchievement, NoSuchActivityAchievementException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity achievements
	 */
	@Override
	public List<SocialActivityAchievement> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement findByGroupId_First(
			long groupId,
			OrderByComparator<SocialActivityAchievement> orderByComparator)
		throws NoSuchActivityAchievementException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement fetchByGroupId_First(
		long groupId,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the social activity achievements where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching social activity achievements
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<SocialActivityAchievement, NoSuchActivityAchievementException>
			_collectionPersistenceFinderByG_U;

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityAchievementModelImpl</code>.
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
	@Override
	public List<SocialActivityAchievement> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			start, end, orderByComparator, useFinderCache);
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
	@Override
	public SocialActivityAchievement findByG_U_First(
			long groupId, long userId,
			OrderByComparator<SocialActivityAchievement> orderByComparator)
		throws NoSuchActivityAchievementException {

		return _collectionPersistenceFinderByG_U.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			orderByComparator);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return _collectionPersistenceFinderByG_U.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			orderByComparator);
	}

	/**
	 * Removes all the social activity achievements where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		_collectionPersistenceFinderByG_U.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching social activity achievements
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId});
	}

	private CollectionPersistenceFinder
		<SocialActivityAchievement, NoSuchActivityAchievementException>
			_collectionPersistenceFinderByG_N;

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityAchievementModelImpl</code>.
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
	@Override
	public List<SocialActivityAchievement> findByG_N(
		long groupId, String name, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_N.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			start, end, orderByComparator, useFinderCache);
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
	@Override
	public SocialActivityAchievement findByG_N_First(
			long groupId, String name,
			OrderByComparator<SocialActivityAchievement> orderByComparator)
		throws NoSuchActivityAchievementException {

		return _collectionPersistenceFinderByG_N.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			orderByComparator);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement fetchByG_N_First(
		long groupId, String name,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return _collectionPersistenceFinderByG_N.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name},
			orderByComparator);
	}

	/**
	 * Removes all the social activity achievements where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 */
	@Override
	public void removeByG_N(long groupId, String name) {
		_collectionPersistenceFinderByG_N.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name});
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching social activity achievements
	 */
	@Override
	public int countByG_N(long groupId, String name) {
		return _collectionPersistenceFinderByG_N.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, name});
	}

	private CollectionPersistenceFinder
		<SocialActivityAchievement, NoSuchActivityAchievementException>
			_collectionPersistenceFinderByG_F;

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63; and firstInGroup = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param firstInGroup the first in group
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity achievements
	 */
	@Override
	public List<SocialActivityAchievement> findByG_F(
		long groupId, boolean firstInGroup, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, firstInGroup}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and firstInGroup = &#63;.
	 *
	 * @param groupId the group ID
	 * @param firstInGroup the first in group
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement findByG_F_First(
			long groupId, boolean firstInGroup,
			OrderByComparator<SocialActivityAchievement> orderByComparator)
		throws NoSuchActivityAchievementException {

		return _collectionPersistenceFinderByG_F.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, firstInGroup}, orderByComparator);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and firstInGroup = &#63;.
	 *
	 * @param groupId the group ID
	 * @param firstInGroup the first in group
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement fetchByG_F_First(
		long groupId, boolean firstInGroup,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return _collectionPersistenceFinderByG_F.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, firstInGroup}, orderByComparator);
	}

	/**
	 * Removes all the social activity achievements where groupId = &#63; and firstInGroup = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param firstInGroup the first in group
	 */
	@Override
	public void removeByG_F(long groupId, boolean firstInGroup) {
		_collectionPersistenceFinderByG_F.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, firstInGroup});
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and firstInGroup = &#63;.
	 *
	 * @param groupId the group ID
	 * @param firstInGroup the first in group
	 * @return the number of matching social activity achievements
	 */
	@Override
	public int countByG_F(long groupId, boolean firstInGroup) {
		return _collectionPersistenceFinderByG_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, firstInGroup});
	}

	private UniquePersistenceFinder
		<SocialActivityAchievement, NoSuchActivityAchievementException>
			_uniquePersistenceFinderByG_U_N;

	/**
	 * Returns the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; or throws a <code>NoSuchActivityAchievementException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement findByG_U_N(
			long groupId, long userId, String name)
		throws NoSuchActivityAchievementException {

		return _uniquePersistenceFinderByG_U_N.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, name});
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
	@Override
	public SocialActivityAchievement fetchByG_U_N(
		long groupId, long userId, String name, boolean useFinderCache) {

		return _uniquePersistenceFinderByG_U_N.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, name}, useFinderCache);
	}

	/**
	 * Removes the social activity achievement where groupId = &#63; and userId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the social activity achievement that was removed
	 */
	@Override
	public SocialActivityAchievement removeByG_U_N(
			long groupId, long userId, String name)
		throws NoSuchActivityAchievementException {

		SocialActivityAchievement socialActivityAchievement = findByG_U_N(
			groupId, userId, name);

		return remove(socialActivityAchievement);
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and userId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param name the name
	 * @return the number of matching social activity achievements
	 */
	@Override
	public int countByG_U_N(long groupId, long userId, String name) {
		return _uniquePersistenceFinderByG_U_N.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, name});
	}

	private CollectionPersistenceFinder
		<SocialActivityAchievement, NoSuchActivityAchievementException>
			_collectionPersistenceFinderByG_U_F;

	/**
	 * Returns an ordered range of all the social activity achievements where groupId = &#63; and userId = &#63; and firstInGroup = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SocialActivityAchievementModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param firstInGroup the first in group
	 * @param start the lower bound of the range of social activity achievements
	 * @param end the upper bound of the range of social activity achievements (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching social activity achievements
	 */
	@Override
	public List<SocialActivityAchievement> findByG_U_F(
		long groupId, long userId, boolean firstInGroup, int start, int end,
		OrderByComparator<SocialActivityAchievement> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U_F.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, firstInGroup}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and userId = &#63; and firstInGroup = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param firstInGroup the first in group
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement
	 * @throws NoSuchActivityAchievementException if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement findByG_U_F_First(
			long groupId, long userId, boolean firstInGroup,
			OrderByComparator<SocialActivityAchievement> orderByComparator)
		throws NoSuchActivityAchievementException {

		return _collectionPersistenceFinderByG_U_F.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, firstInGroup}, orderByComparator);
	}

	/**
	 * Returns the first social activity achievement in the ordered set where groupId = &#63; and userId = &#63; and firstInGroup = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param firstInGroup the first in group
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching social activity achievement, or <code>null</code> if a matching social activity achievement could not be found
	 */
	@Override
	public SocialActivityAchievement fetchByG_U_F_First(
		long groupId, long userId, boolean firstInGroup,
		OrderByComparator<SocialActivityAchievement> orderByComparator) {

		return _collectionPersistenceFinderByG_U_F.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, firstInGroup}, orderByComparator);
	}

	/**
	 * Removes all the social activity achievements where groupId = &#63; and userId = &#63; and firstInGroup = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param firstInGroup the first in group
	 */
	@Override
	public void removeByG_U_F(long groupId, long userId, boolean firstInGroup) {
		_collectionPersistenceFinderByG_U_F.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, firstInGroup});
	}

	/**
	 * Returns the number of social activity achievements where groupId = &#63; and userId = &#63; and firstInGroup = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param firstInGroup the first in group
	 * @return the number of matching social activity achievements
	 */
	@Override
	public int countByG_U_F(long groupId, long userId, boolean firstInGroup) {
		return _collectionPersistenceFinderByG_U_F.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {groupId, userId, firstInGroup});
	}

	public SocialActivityAchievementPersistenceImpl() {
		setModelClass(SocialActivityAchievement.class);

		setModelImplClass(SocialActivityAchievementImpl.class);
		setModelPKClass(long.class);

		setTable(SocialActivityAchievementTable.INSTANCE);
	}

	/**
	 * Creates a new social activity achievement with the primary key. Does not add the social activity achievement to the database.
	 *
	 * @param activityAchievementId the primary key for the new social activity achievement
	 * @return the new social activity achievement
	 */
	@Override
	public SocialActivityAchievement create(long activityAchievementId) {
		SocialActivityAchievement socialActivityAchievement =
			new SocialActivityAchievementImpl();

		socialActivityAchievement.setNew(true);
		socialActivityAchievement.setPrimaryKey(activityAchievementId);

		socialActivityAchievement.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return socialActivityAchievement;
	}

	/**
	 * Removes the social activity achievement with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement that was removed
	 * @throws NoSuchActivityAchievementException if a social activity achievement with the primary key could not be found
	 */
	@Override
	public SocialActivityAchievement remove(long activityAchievementId)
		throws NoSuchActivityAchievementException {

		return remove((Serializable)activityAchievementId);
	}

	@Override
	protected SocialActivityAchievement removeImpl(
		SocialActivityAchievement socialActivityAchievement) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(socialActivityAchievement)) {
				socialActivityAchievement =
					(SocialActivityAchievement)session.get(
						SocialActivityAchievementImpl.class,
						socialActivityAchievement.getPrimaryKeyObj());
			}

			if ((socialActivityAchievement != null) &&
				CTPersistenceHelperUtil.isRemove(socialActivityAchievement)) {

				session.delete(socialActivityAchievement);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (socialActivityAchievement != null) {
			clearCache(socialActivityAchievement);
		}

		return socialActivityAchievement;
	}

	@Override
	public SocialActivityAchievement updateImpl(
		SocialActivityAchievement socialActivityAchievement) {

		boolean isNew = socialActivityAchievement.isNew();

		if (!(socialActivityAchievement instanceof
				SocialActivityAchievementModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(socialActivityAchievement.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					socialActivityAchievement);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in socialActivityAchievement proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom SocialActivityAchievement implementation " +
					socialActivityAchievement.getClass());
		}

		SocialActivityAchievementModelImpl socialActivityAchievementModelImpl =
			(SocialActivityAchievementModelImpl)socialActivityAchievement;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(socialActivityAchievement)) {
				if (!isNew) {
					session.evict(
						SocialActivityAchievementImpl.class,
						socialActivityAchievement.getPrimaryKeyObj());
				}

				session.save(socialActivityAchievement);
			}
			else {
				socialActivityAchievement =
					(SocialActivityAchievement)session.merge(
						socialActivityAchievement);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(socialActivityAchievement, false);

		if (isNew) {
			socialActivityAchievement.setNew(false);
		}

		socialActivityAchievement.resetOriginalValues();

		return socialActivityAchievement;
	}

	/**
	 * Returns the social activity achievement with the primary key or throws a <code>NoSuchActivityAchievementException</code> if it could not be found.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement
	 * @throws NoSuchActivityAchievementException if a social activity achievement with the primary key could not be found
	 */
	@Override
	public SocialActivityAchievement findByPrimaryKey(
			long activityAchievementId)
		throws NoSuchActivityAchievementException {

		return findByPrimaryKey((Serializable)activityAchievementId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the social activity achievement with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param activityAchievementId the primary key of the social activity achievement
	 * @return the social activity achievement, or <code>null</code> if a social activity achievement with the primary key could not be found
	 */
	@Override
	public SocialActivityAchievement fetchByPrimaryKey(
		long activityAchievementId) {

		return fetchByPrimaryKey((Serializable)activityAchievementId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "activityAchievementId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SOCIALACTIVITYACHIEVEMENT;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return SocialActivityAchievementModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "SocialActivityAchievement";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("createDate");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("firstInGroup");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("activityAchievementId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"groupId", "userId", "name"});
	}

	/**
	 * Initializes the social activity achievement persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByGroupId",
					new String[] {Long.class.getName()},
					new String[] {"groupId"}, false),
				_SQL_SELECT_SOCIALACTIVITYACHIEVEMENT_WHERE,
				_SQL_COUNT_SOCIALACTIVITYACHIEVEMENT_WHERE,
				SocialActivityAchievementModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "", null,
				new FinderColumn<>(
					"socialActivityAchievement.", "groupId",
					FinderColumn.Type.LONG, "=", true, true,
					SocialActivityAchievement::getGroupId));

		_collectionPersistenceFinderByG_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "userId"}, false),
			_SQL_SELECT_SOCIALACTIVITYACHIEVEMENT_WHERE,
			_SQL_COUNT_SOCIALACTIVITYACHIEVEMENT_WHERE,
			SocialActivityAchievementModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"socialActivityAchievement.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityAchievement::getGroupId),
			new FinderColumn<>(
				"socialActivityAchievement.", "userId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityAchievement::getUserId));

		_collectionPersistenceFinderByG_N = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "name"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_N",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"groupId", "name"}, 0, 2, false, null),
			_SQL_SELECT_SOCIALACTIVITYACHIEVEMENT_WHERE,
			_SQL_COUNT_SOCIALACTIVITYACHIEVEMENT_WHERE,
			SocialActivityAchievementModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"socialActivityAchievement.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityAchievement::getGroupId),
			new FinderColumn<>(
				"socialActivityAchievement.", "name", FinderColumn.Type.STRING,
				"=", true, true, SocialActivityAchievement::getName));

		_collectionPersistenceFinderByG_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_F",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "firstInGroup"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_F",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "firstInGroup"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_F",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"groupId", "firstInGroup"}, false),
			_SQL_SELECT_SOCIALACTIVITYACHIEVEMENT_WHERE,
			_SQL_COUNT_SOCIALACTIVITYACHIEVEMENT_WHERE,
			SocialActivityAchievementModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"socialActivityAchievement.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityAchievement::getGroupId),
			new FinderColumn<>(
				"socialActivityAchievement.", "firstInGroup",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				SocialActivityAchievement::isFirstInGroup));

		_uniquePersistenceFinderByG_U_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByG_U_N",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"groupId", "userId", "name"}, 0, 4, false,
				SocialActivityAchievement::getGroupId,
				SocialActivityAchievement::getUserId,
				convertNullFunction(SocialActivityAchievement::getName)),
			_SQL_SELECT_SOCIALACTIVITYACHIEVEMENT_WHERE, "",
			new FinderColumn<>(
				"socialActivityAchievement.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityAchievement::getGroupId),
			new FinderColumn<>(
				"socialActivityAchievement.", "userId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityAchievement::getUserId),
			new FinderColumn<>(
				"socialActivityAchievement.", "name", FinderColumn.Type.STRING,
				"=", true, true, SocialActivityAchievement::getName));

		_collectionPersistenceFinderByG_U_F = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"groupId", "userId", "firstInGroup"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "userId", "firstInGroup"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U_F",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"groupId", "userId", "firstInGroup"}, false),
			_SQL_SELECT_SOCIALACTIVITYACHIEVEMENT_WHERE,
			_SQL_COUNT_SOCIALACTIVITYACHIEVEMENT_WHERE,
			SocialActivityAchievementModelImpl.ORDER_BY_JPQL,
			_ENTITY_ALIAS_PREFIX, "", "", null,
			new FinderColumn<>(
				"socialActivityAchievement.", "groupId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityAchievement::getGroupId),
			new FinderColumn<>(
				"socialActivityAchievement.", "userId", FinderColumn.Type.LONG,
				"=", true, true, SocialActivityAchievement::getUserId),
			new FinderColumn<>(
				"socialActivityAchievement.", "firstInGroup",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				SocialActivityAchievement::isFirstInGroup));

		SocialActivityAchievementUtil.setPersistence(this);
	}

	public void destroy() {
		SocialActivityAchievementUtil.setPersistence(null);

		EntityCacheUtil.removeCache(
			SocialActivityAchievementImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SocialActivityAchievementModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SOCIALACTIVITYACHIEVEMENT =
		"SELECT socialActivityAchievement FROM SocialActivityAchievement socialActivityAchievement";

	private static final String _SQL_SELECT_SOCIALACTIVITYACHIEVEMENT_WHERE =
		"SELECT socialActivityAchievement FROM SocialActivityAchievement socialActivityAchievement WHERE ";

	private static final String _SQL_COUNT_SOCIALACTIVITYACHIEVEMENT_WHERE =
		"SELECT COUNT(socialActivityAchievement) FROM SocialActivityAchievement socialActivityAchievement WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2053757869