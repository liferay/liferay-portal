/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchUserGroupRoleException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.model.UserGroupRoleTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.UserGroupRolePersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupRoleUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.UserGroupRoleImpl;
import com.liferay.portal.model.impl.UserGroupRoleModelImpl;

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
 * The persistence implementation for the user group role service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserGroupRolePersistenceImpl
	extends BasePersistenceImpl<UserGroupRole, NoSuchUserGroupRoleException>
	implements UserGroupRolePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserGroupRoleUtil</code> to access the user group role persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserGroupRoleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<UserGroupRole, NoSuchUserGroupRoleException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the user group roles where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user group roles
	 * @param end the upper bound of the range of user group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group roles
	 */
	@Override
	public List<UserGroupRole> findByUserId(
		long userId, int start, int end,
		OrderByComparator<UserGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group role in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role
	 * @throws NoSuchUserGroupRoleException if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole findByUserId_First(
			long userId, OrderByComparator<UserGroupRole> orderByComparator)
		throws NoSuchUserGroupRoleException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first user group role in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role, or <code>null</code> if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole fetchByUserId_First(
		long userId, OrderByComparator<UserGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the user group roles where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of user group roles where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching user group roles
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<UserGroupRole, NoSuchUserGroupRoleException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the user group roles where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of user group roles
	 * @param end the upper bound of the range of user group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group roles
	 */
	@Override
	public List<UserGroupRole> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<UserGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group role in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role
	 * @throws NoSuchUserGroupRoleException if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole findByGroupId_First(
			long groupId, OrderByComparator<UserGroupRole> orderByComparator)
		throws NoSuchUserGroupRoleException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first user group role in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role, or <code>null</code> if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole fetchByGroupId_First(
		long groupId, OrderByComparator<UserGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the user group roles where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of user group roles where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching user group roles
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<UserGroupRole, NoSuchUserGroupRoleException>
			_collectionPersistenceFinderByRoleId;

	/**
	 * Returns an ordered range of all the user group roles where roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param roleId the role ID
	 * @param start the lower bound of the range of user group roles
	 * @param end the upper bound of the range of user group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group roles
	 */
	@Override
	public List<UserGroupRole> findByRoleId(
		long roleId, int start, int end,
		OrderByComparator<UserGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRoleId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group role in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role
	 * @throws NoSuchUserGroupRoleException if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole findByRoleId_First(
			long roleId, OrderByComparator<UserGroupRole> orderByComparator)
		throws NoSuchUserGroupRoleException {

		return _collectionPersistenceFinderByRoleId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId},
			orderByComparator);
	}

	/**
	 * Returns the first user group role in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role, or <code>null</code> if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole fetchByRoleId_First(
		long roleId, OrderByComparator<UserGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByRoleId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId},
			orderByComparator);
	}

	/**
	 * Removes all the user group roles where roleId = &#63; from the database.
	 *
	 * @param roleId the role ID
	 */
	@Override
	public void removeByRoleId(long roleId) {
		_collectionPersistenceFinderByRoleId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId});
	}

	/**
	 * Returns the number of user group roles where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the number of matching user group roles
	 */
	@Override
	public int countByRoleId(long roleId) {
		return _collectionPersistenceFinderByRoleId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId});
	}

	private CollectionPersistenceFinder
		<UserGroupRole, NoSuchUserGroupRoleException>
			_collectionPersistenceFinderByU_G;

	/**
	 * Returns an ordered range of all the user group roles where userId = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 * @param start the lower bound of the range of user group roles
	 * @param end the upper bound of the range of user group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group roles
	 */
	@Override
	public List<UserGroupRole> findByU_G(
		long userId, long groupId, int start, int end,
		OrderByComparator<UserGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_G.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, groupId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group role in the ordered set where userId = &#63; and groupId = &#63;.
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role
	 * @throws NoSuchUserGroupRoleException if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole findByU_G_First(
			long userId, long groupId,
			OrderByComparator<UserGroupRole> orderByComparator)
		throws NoSuchUserGroupRoleException {

		return _collectionPersistenceFinderByU_G.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, groupId},
			orderByComparator);
	}

	/**
	 * Returns the first user group role in the ordered set where userId = &#63; and groupId = &#63;.
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role, or <code>null</code> if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole fetchByU_G_First(
		long userId, long groupId,
		OrderByComparator<UserGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByU_G.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, groupId},
			orderByComparator);
	}

	/**
	 * Removes all the user group roles where userId = &#63; and groupId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 */
	@Override
	public void removeByU_G(long userId, long groupId) {
		_collectionPersistenceFinderByU_G.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, groupId});
	}

	/**
	 * Returns the number of user group roles where userId = &#63; and groupId = &#63;.
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 * @return the number of matching user group roles
	 */
	@Override
	public int countByU_G(long userId, long groupId) {
		return _collectionPersistenceFinderByU_G.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, groupId});
	}

	private CollectionPersistenceFinder
		<UserGroupRole, NoSuchUserGroupRoleException>
			_collectionPersistenceFinderByG_R;

	/**
	 * Returns an ordered range of all the user group roles where groupId = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param start the lower bound of the range of user group roles
	 * @param end the upper bound of the range of user group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group roles
	 */
	@Override
	public List<UserGroupRole> findByG_R(
		long groupId, long roleId, int start, int end,
		OrderByComparator<UserGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_R.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group role in the ordered set where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role
	 * @throws NoSuchUserGroupRoleException if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole findByG_R_First(
			long groupId, long roleId,
			OrderByComparator<UserGroupRole> orderByComparator)
		throws NoSuchUserGroupRoleException {

		return _collectionPersistenceFinderByG_R.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId},
			orderByComparator);
	}

	/**
	 * Returns the first user group role in the ordered set where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group role, or <code>null</code> if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole fetchByG_R_First(
		long groupId, long roleId,
		OrderByComparator<UserGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByG_R.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId},
			orderByComparator);
	}

	/**
	 * Removes all the user group roles where groupId = &#63; and roleId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 */
	@Override
	public void removeByG_R(long groupId, long roleId) {
		_collectionPersistenceFinderByG_R.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId});
	}

	/**
	 * Returns the number of user group roles where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the number of matching user group roles
	 */
	@Override
	public int countByG_R(long groupId, long roleId) {
		return _collectionPersistenceFinderByG_R.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId});
	}

	private UniquePersistenceFinder<UserGroupRole, NoSuchUserGroupRoleException>
		_uniquePersistenceFinderByU_G_R;

	/**
	 * Returns the user group role where userId = &#63; and groupId = &#63; and roleId = &#63; or throws a <code>NoSuchUserGroupRoleException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the matching user group role
	 * @throws NoSuchUserGroupRoleException if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole findByU_G_R(long userId, long groupId, long roleId)
		throws NoSuchUserGroupRoleException {

		return _uniquePersistenceFinderByU_G_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, groupId, roleId});
	}

	/**
	 * Returns the user group role where userId = &#63; and groupId = &#63; and roleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user group role, or <code>null</code> if a matching user group role could not be found
	 */
	@Override
	public UserGroupRole fetchByU_G_R(
		long userId, long groupId, long roleId, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_G_R.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, groupId, roleId}, useFinderCache);
	}

	/**
	 * Removes the user group role where userId = &#63; and groupId = &#63; and roleId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the user group role that was removed
	 */
	@Override
	public UserGroupRole removeByU_G_R(long userId, long groupId, long roleId)
		throws NoSuchUserGroupRoleException {

		UserGroupRole userGroupRole = findByU_G_R(userId, groupId, roleId);

		return remove(userGroupRole);
	}

	/**
	 * Returns the number of user group roles where userId = &#63; and groupId = &#63; and roleId = &#63;.
	 *
	 * @param userId the user ID
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the number of matching user group roles
	 */
	@Override
	public int countByU_G_R(long userId, long groupId, long roleId) {
		return _uniquePersistenceFinderByU_G_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, groupId, roleId});
	}

	public UserGroupRolePersistenceImpl() {
		setModelClass(UserGroupRole.class);

		setModelImplClass(UserGroupRoleImpl.class);
		setModelPKClass(long.class);

		setTable(UserGroupRoleTable.INSTANCE);
	}

	/**
	 * Creates a new user group role with the primary key. Does not add the user group role to the database.
	 *
	 * @param userGroupRoleId the primary key for the new user group role
	 * @return the new user group role
	 */
	@Override
	public UserGroupRole create(long userGroupRoleId) {
		UserGroupRole userGroupRole = new UserGroupRoleImpl();

		userGroupRole.setNew(true);
		userGroupRole.setPrimaryKey(userGroupRoleId);

		userGroupRole.setCompanyId(CompanyThreadLocal.getCompanyId());

		return userGroupRole;
	}

	/**
	 * Removes the user group role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userGroupRoleId the primary key of the user group role
	 * @return the user group role that was removed
	 * @throws NoSuchUserGroupRoleException if a user group role with the primary key could not be found
	 */
	@Override
	public UserGroupRole remove(long userGroupRoleId)
		throws NoSuchUserGroupRoleException {

		return remove((Serializable)userGroupRoleId);
	}

	@Override
	protected UserGroupRole removeImpl(UserGroupRole userGroupRole) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userGroupRole)) {
				userGroupRole = (UserGroupRole)session.get(
					UserGroupRoleImpl.class, userGroupRole.getPrimaryKeyObj());
			}

			if ((userGroupRole != null) &&
				CTPersistenceHelperUtil.isRemove(userGroupRole)) {

				session.delete(userGroupRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userGroupRole != null) {
			clearCache(userGroupRole);
		}

		return userGroupRole;
	}

	@Override
	public UserGroupRole updateImpl(UserGroupRole userGroupRole) {
		boolean isNew = userGroupRole.isNew();

		if (!(userGroupRole instanceof UserGroupRoleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userGroupRole.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					userGroupRole);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userGroupRole proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserGroupRole implementation " +
					userGroupRole.getClass());
		}

		UserGroupRoleModelImpl userGroupRoleModelImpl =
			(UserGroupRoleModelImpl)userGroupRole;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(userGroupRole)) {
				if (!isNew) {
					session.evict(
						UserGroupRoleImpl.class,
						userGroupRole.getPrimaryKeyObj());
				}

				session.save(userGroupRole);
			}
			else {
				userGroupRole = (UserGroupRole)session.merge(userGroupRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(userGroupRole, false);

		if (isNew) {
			userGroupRole.setNew(false);
		}

		userGroupRole.resetOriginalValues();

		return userGroupRole;
	}

	/**
	 * Returns the user group role with the primary key or throws a <code>NoSuchUserGroupRoleException</code> if it could not be found.
	 *
	 * @param userGroupRoleId the primary key of the user group role
	 * @return the user group role
	 * @throws NoSuchUserGroupRoleException if a user group role with the primary key could not be found
	 */
	@Override
	public UserGroupRole findByPrimaryKey(long userGroupRoleId)
		throws NoSuchUserGroupRoleException {

		return findByPrimaryKey((Serializable)userGroupRoleId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the user group role with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userGroupRoleId the primary key of the user group role
	 * @return the user group role, or <code>null</code> if a user group role with the primary key could not be found
	 */
	@Override
	public UserGroupRole fetchByPrimaryKey(long userGroupRoleId) {
		return fetchByPrimaryKey((Serializable)userGroupRoleId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "userGroupRoleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERGROUPROLE;
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
		return UserGroupRoleModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "UserGroupRole";
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
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("groupId");
		ctMergeColumnNames.add("roleId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("userGroupRoleId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"userId", "groupId", "roleId"});
	}

	/**
	 * Initializes the user group role persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_USERGROUPROLE_WHERE, _SQL_COUNT_USERGROUPROLE_WHERE,
				UserGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"userGroupRole.", "userId", FinderColumn.Type.LONG, "=",
					true, true, UserGroupRole::getUserId));

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
				_SQL_SELECT_USERGROUPROLE_WHERE, _SQL_COUNT_USERGROUPROLE_WHERE,
				UserGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"userGroupRole.", "groupId", FinderColumn.Type.LONG, "=",
					true, true, UserGroupRole::getGroupId));

		_collectionPersistenceFinderByRoleId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByRoleId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"roleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByRoleId",
					new String[] {Long.class.getName()},
					new String[] {"roleId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByRoleId",
					new String[] {Long.class.getName()},
					new String[] {"roleId"}, false),
				_SQL_SELECT_USERGROUPROLE_WHERE, _SQL_COUNT_USERGROUPROLE_WHERE,
				UserGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"userGroupRole.", "roleId", FinderColumn.Type.LONG, "=",
					true, true, UserGroupRole::getRoleId));

		_collectionPersistenceFinderByU_G = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_G",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "groupId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_G",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "groupId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_G",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "groupId"}, false),
			_SQL_SELECT_USERGROUPROLE_WHERE, _SQL_COUNT_USERGROUPROLE_WHERE,
			UserGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"userGroupRole.", "userId", FinderColumn.Type.LONG, "=", true,
				true, UserGroupRole::getUserId),
			new FinderColumn<>(
				"userGroupRole.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, UserGroupRole::getGroupId));

		_collectionPersistenceFinderByG_R = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "roleId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_R",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "roleId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_R",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "roleId"}, false),
			_SQL_SELECT_USERGROUPROLE_WHERE, _SQL_COUNT_USERGROUPROLE_WHERE,
			UserGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"userGroupRole.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, UserGroupRole::getGroupId),
			new FinderColumn<>(
				"userGroupRole.", "roleId", FinderColumn.Type.LONG, "=", true,
				true, UserGroupRole::getRoleId));

		_uniquePersistenceFinderByU_G_R = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_G_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"userId", "groupId", "roleId"}, 0, 0, false,
				UserGroupRole::getUserId, UserGroupRole::getGroupId,
				UserGroupRole::getRoleId),
			_SQL_SELECT_USERGROUPROLE_WHERE, "",
			new FinderColumn<>(
				"userGroupRole.", "userId", FinderColumn.Type.LONG, "=", true,
				true, UserGroupRole::getUserId),
			new FinderColumn<>(
				"userGroupRole.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, UserGroupRole::getGroupId),
			new FinderColumn<>(
				"userGroupRole.", "roleId", FinderColumn.Type.LONG, "=", true,
				true, UserGroupRole::getRoleId));

		UserGroupRoleUtil.setPersistence(this);
	}

	public void destroy() {
		UserGroupRoleUtil.setPersistence(null);

		EntityCacheUtil.removeCache(UserGroupRoleImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		UserGroupRoleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_USERGROUPROLE =
		"SELECT userGroupRole FROM UserGroupRole userGroupRole";

	private static final String _SQL_SELECT_USERGROUPROLE_WHERE =
		"SELECT userGroupRole FROM UserGroupRole userGroupRole WHERE ";

	private static final String _SQL_COUNT_USERGROUPROLE_WHERE =
		"SELECT COUNT(userGroupRole) FROM UserGroupRole userGroupRole WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No UserGroupRole exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupRolePersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-597425370