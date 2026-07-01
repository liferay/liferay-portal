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
import com.liferay.portal.kernel.exception.NoSuchUserGroupGroupRoleException;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupGroupRoleTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.UserGroupGroupRolePersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupGroupRoleUtil;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelperUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.UserGroupGroupRoleImpl;
import com.liferay.portal.model.impl.UserGroupGroupRoleModelImpl;

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
 * The persistence implementation for the user group group role service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserGroupGroupRolePersistenceImpl
	extends BasePersistenceImpl
		<UserGroupGroupRole, NoSuchUserGroupGroupRoleException>
	implements UserGroupGroupRolePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserGroupGroupRoleUtil</code> to access the user group group role persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserGroupGroupRoleImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<UserGroupGroupRole, NoSuchUserGroupGroupRoleException>
			_collectionPersistenceFinderByUserGroupId;

	/**
	 * Returns an ordered range of all the user group group roles where userGroupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param start the lower bound of the range of user group group roles
	 * @param end the upper bound of the range of user group group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group group roles
	 */
	@Override
	public List<UserGroupGroupRole> findByUserGroupId(
		long userGroupId, int start, int end,
		OrderByComparator<UserGroupGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userGroupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group group role in the ordered set where userGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role
	 * @throws NoSuchUserGroupGroupRoleException if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole findByUserGroupId_First(
			long userGroupId,
			OrderByComparator<UserGroupGroupRole> orderByComparator)
		throws NoSuchUserGroupGroupRoleException {

		return _collectionPersistenceFinderByUserGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userGroupId},
			orderByComparator);
	}

	/**
	 * Returns the first user group group role in the ordered set where userGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role, or <code>null</code> if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole fetchByUserGroupId_First(
		long userGroupId,
		OrderByComparator<UserGroupGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByUserGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userGroupId},
			orderByComparator);
	}

	/**
	 * Removes all the user group group roles where userGroupId = &#63; from the database.
	 *
	 * @param userGroupId the user group ID
	 */
	@Override
	public void removeByUserGroupId(long userGroupId) {
		_collectionPersistenceFinderByUserGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userGroupId});
	}

	/**
	 * Returns the number of user group group roles where userGroupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @return the number of matching user group group roles
	 */
	@Override
	public int countByUserGroupId(long userGroupId) {
		return _collectionPersistenceFinderByUserGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userGroupId});
	}

	private CollectionPersistenceFinder
		<UserGroupGroupRole, NoSuchUserGroupGroupRoleException>
			_collectionPersistenceFinderByGroupId;

	/**
	 * Returns an ordered range of all the user group group roles where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of user group group roles
	 * @param end the upper bound of the range of user group group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group group roles
	 */
	@Override
	public List<UserGroupGroupRole> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<UserGroupGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByGroupId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group group role in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role
	 * @throws NoSuchUserGroupGroupRoleException if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole findByGroupId_First(
			long groupId,
			OrderByComparator<UserGroupGroupRole> orderByComparator)
		throws NoSuchUserGroupGroupRoleException {

		return _collectionPersistenceFinderByGroupId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Returns the first user group group role in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role, or <code>null</code> if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole fetchByGroupId_First(
		long groupId, OrderByComparator<UserGroupGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByGroupId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId},
			orderByComparator);
	}

	/**
	 * Removes all the user group group roles where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	@Override
	public void removeByGroupId(long groupId) {
		_collectionPersistenceFinderByGroupId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	/**
	 * Returns the number of user group group roles where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching user group group roles
	 */
	@Override
	public int countByGroupId(long groupId) {
		return _collectionPersistenceFinderByGroupId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId});
	}

	private CollectionPersistenceFinder
		<UserGroupGroupRole, NoSuchUserGroupGroupRoleException>
			_collectionPersistenceFinderByRoleId;

	/**
	 * Returns an ordered range of all the user group group roles where roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param roleId the role ID
	 * @param start the lower bound of the range of user group group roles
	 * @param end the upper bound of the range of user group group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group group roles
	 */
	@Override
	public List<UserGroupGroupRole> findByRoleId(
		long roleId, int start, int end,
		OrderByComparator<UserGroupGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByRoleId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group group role in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role
	 * @throws NoSuchUserGroupGroupRoleException if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole findByRoleId_First(
			long roleId,
			OrderByComparator<UserGroupGroupRole> orderByComparator)
		throws NoSuchUserGroupGroupRoleException {

		return _collectionPersistenceFinderByRoleId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId},
			orderByComparator);
	}

	/**
	 * Returns the first user group group role in the ordered set where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role, or <code>null</code> if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole fetchByRoleId_First(
		long roleId, OrderByComparator<UserGroupGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByRoleId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId},
			orderByComparator);
	}

	/**
	 * Removes all the user group group roles where roleId = &#63; from the database.
	 *
	 * @param roleId the role ID
	 */
	@Override
	public void removeByRoleId(long roleId) {
		_collectionPersistenceFinderByRoleId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId});
	}

	/**
	 * Returns the number of user group group roles where roleId = &#63;.
	 *
	 * @param roleId the role ID
	 * @return the number of matching user group group roles
	 */
	@Override
	public int countByRoleId(long roleId) {
		return _collectionPersistenceFinderByRoleId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {roleId});
	}

	private CollectionPersistenceFinder
		<UserGroupGroupRole, NoSuchUserGroupGroupRoleException>
			_collectionPersistenceFinderByU_G;

	/**
	 * Returns an ordered range of all the user group group roles where userGroupId = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 * @param start the lower bound of the range of user group group roles
	 * @param end the upper bound of the range of user group group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group group roles
	 */
	@Override
	public List<UserGroupGroupRole> findByU_G(
		long userGroupId, long groupId, int start, int end,
		OrderByComparator<UserGroupGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_G.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, groupId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first user group group role in the ordered set where userGroupId = &#63; and groupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role
	 * @throws NoSuchUserGroupGroupRoleException if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole findByU_G_First(
			long userGroupId, long groupId,
			OrderByComparator<UserGroupGroupRole> orderByComparator)
		throws NoSuchUserGroupGroupRoleException {

		return _collectionPersistenceFinderByU_G.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, groupId}, orderByComparator);
	}

	/**
	 * Returns the first user group group role in the ordered set where userGroupId = &#63; and groupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role, or <code>null</code> if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole fetchByU_G_First(
		long userGroupId, long groupId,
		OrderByComparator<UserGroupGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByU_G.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, groupId}, orderByComparator);
	}

	/**
	 * Removes all the user group group roles where userGroupId = &#63; and groupId = &#63; from the database.
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 */
	@Override
	public void removeByU_G(long userGroupId, long groupId) {
		_collectionPersistenceFinderByU_G.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, groupId});
	}

	/**
	 * Returns the number of user group group roles where userGroupId = &#63; and groupId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 * @return the number of matching user group group roles
	 */
	@Override
	public int countByU_G(long userGroupId, long groupId) {
		return _collectionPersistenceFinderByU_G.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, groupId});
	}

	private CollectionPersistenceFinder
		<UserGroupGroupRole, NoSuchUserGroupGroupRoleException>
			_collectionPersistenceFinderByG_R;

	/**
	 * Returns an ordered range of all the user group group roles where groupId = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserGroupGroupRoleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param start the lower bound of the range of user group group roles
	 * @param end the upper bound of the range of user group group roles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user group group roles
	 */
	@Override
	public List<UserGroupGroupRole> findByG_R(
		long groupId, long roleId, int start, int end,
		OrderByComparator<UserGroupGroupRole> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_R.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user group group role in the ordered set where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role
	 * @throws NoSuchUserGroupGroupRoleException if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole findByG_R_First(
			long groupId, long roleId,
			OrderByComparator<UserGroupGroupRole> orderByComparator)
		throws NoSuchUserGroupGroupRoleException {

		return _collectionPersistenceFinderByG_R.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId},
			orderByComparator);
	}

	/**
	 * Returns the first user group group role in the ordered set where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user group group role, or <code>null</code> if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole fetchByG_R_First(
		long groupId, long roleId,
		OrderByComparator<UserGroupGroupRole> orderByComparator) {

		return _collectionPersistenceFinderByG_R.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId},
			orderByComparator);
	}

	/**
	 * Removes all the user group group roles where groupId = &#63; and roleId = &#63; from the database.
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
	 * Returns the number of user group group roles where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the number of matching user group group roles
	 */
	@Override
	public int countByG_R(long groupId, long roleId) {
		return _collectionPersistenceFinderByG_R.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, roleId});
	}

	private UniquePersistenceFinder
		<UserGroupGroupRole, NoSuchUserGroupGroupRoleException>
			_uniquePersistenceFinderByU_G_R;

	/**
	 * Returns the user group group role where userGroupId = &#63; and groupId = &#63; and roleId = &#63; or throws a <code>NoSuchUserGroupGroupRoleException</code> if it could not be found.
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the matching user group group role
	 * @throws NoSuchUserGroupGroupRoleException if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole findByU_G_R(
			long userGroupId, long groupId, long roleId)
		throws NoSuchUserGroupGroupRoleException {

		return _uniquePersistenceFinderByU_G_R.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, groupId, roleId});
	}

	/**
	 * Returns the user group group role where userGroupId = &#63; and groupId = &#63; and roleId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user group group role, or <code>null</code> if a matching user group group role could not be found
	 */
	@Override
	public UserGroupGroupRole fetchByU_G_R(
		long userGroupId, long groupId, long roleId, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_G_R.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, groupId, roleId}, useFinderCache);
	}

	/**
	 * Removes the user group group role where userGroupId = &#63; and groupId = &#63; and roleId = &#63; from the database.
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the user group group role that was removed
	 */
	@Override
	public UserGroupGroupRole removeByU_G_R(
			long userGroupId, long groupId, long roleId)
		throws NoSuchUserGroupGroupRoleException {

		UserGroupGroupRole userGroupGroupRole = findByU_G_R(
			userGroupId, groupId, roleId);

		return remove(userGroupGroupRole);
	}

	/**
	 * Returns the number of user group group roles where userGroupId = &#63; and groupId = &#63; and roleId = &#63;.
	 *
	 * @param userGroupId the user group ID
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the number of matching user group group roles
	 */
	@Override
	public int countByU_G_R(long userGroupId, long groupId, long roleId) {
		return _uniquePersistenceFinderByU_G_R.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userGroupId, groupId, roleId});
	}

	public UserGroupGroupRolePersistenceImpl() {
		setModelClass(UserGroupGroupRole.class);

		setModelImplClass(UserGroupGroupRoleImpl.class);
		setModelPKClass(long.class);

		setTable(UserGroupGroupRoleTable.INSTANCE);
	}

	/**
	 * Creates a new user group group role with the primary key. Does not add the user group group role to the database.
	 *
	 * @param userGroupGroupRoleId the primary key for the new user group group role
	 * @return the new user group group role
	 */
	@Override
	public UserGroupGroupRole create(long userGroupGroupRoleId) {
		UserGroupGroupRole userGroupGroupRole = new UserGroupGroupRoleImpl();

		userGroupGroupRole.setNew(true);
		userGroupGroupRole.setPrimaryKey(userGroupGroupRoleId);

		userGroupGroupRole.setCompanyId(CompanyThreadLocal.getCompanyId());

		return userGroupGroupRole;
	}

	/**
	 * Removes the user group group role with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userGroupGroupRoleId the primary key of the user group group role
	 * @return the user group group role that was removed
	 * @throws NoSuchUserGroupGroupRoleException if a user group group role with the primary key could not be found
	 */
	@Override
	public UserGroupGroupRole remove(long userGroupGroupRoleId)
		throws NoSuchUserGroupGroupRoleException {

		return remove((Serializable)userGroupGroupRoleId);
	}

	@Override
	protected UserGroupGroupRole removeImpl(
		UserGroupGroupRole userGroupGroupRole) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userGroupGroupRole)) {
				userGroupGroupRole = (UserGroupGroupRole)session.get(
					UserGroupGroupRoleImpl.class,
					userGroupGroupRole.getPrimaryKeyObj());
			}

			if ((userGroupGroupRole != null) &&
				CTPersistenceHelperUtil.isRemove(userGroupGroupRole)) {

				session.delete(userGroupGroupRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userGroupGroupRole != null) {
			clearCache(userGroupGroupRole);
		}

		return userGroupGroupRole;
	}

	@Override
	public UserGroupGroupRole updateImpl(
		UserGroupGroupRole userGroupGroupRole) {

		boolean isNew = userGroupGroupRole.isNew();

		if (!(userGroupGroupRole instanceof UserGroupGroupRoleModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userGroupGroupRole.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					userGroupGroupRole);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userGroupGroupRole proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserGroupGroupRole implementation " +
					userGroupGroupRole.getClass());
		}

		UserGroupGroupRoleModelImpl userGroupGroupRoleModelImpl =
			(UserGroupGroupRoleModelImpl)userGroupGroupRole;

		Session session = null;

		try {
			session = openSession();

			if (CTPersistenceHelperUtil.isInsert(userGroupGroupRole)) {
				if (!isNew) {
					session.evict(
						UserGroupGroupRoleImpl.class,
						userGroupGroupRole.getPrimaryKeyObj());
				}

				session.save(userGroupGroupRole);
			}
			else {
				userGroupGroupRole = (UserGroupGroupRole)session.merge(
					userGroupGroupRole);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(userGroupGroupRole, false);

		if (isNew) {
			userGroupGroupRole.setNew(false);
		}

		userGroupGroupRole.resetOriginalValues();

		return userGroupGroupRole;
	}

	/**
	 * Returns the user group group role with the primary key or throws a <code>NoSuchUserGroupGroupRoleException</code> if it could not be found.
	 *
	 * @param userGroupGroupRoleId the primary key of the user group group role
	 * @return the user group group role
	 * @throws NoSuchUserGroupGroupRoleException if a user group group role with the primary key could not be found
	 */
	@Override
	public UserGroupGroupRole findByPrimaryKey(long userGroupGroupRoleId)
		throws NoSuchUserGroupGroupRoleException {

		return findByPrimaryKey((Serializable)userGroupGroupRoleId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return CTPersistenceHelperUtil.getCTPersistenceHelper();
	}

	/**
	 * Returns the user group group role with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userGroupGroupRoleId the primary key of the user group group role
	 * @return the user group group role, or <code>null</code> if a user group group role with the primary key could not be found
	 */
	@Override
	public UserGroupGroupRole fetchByPrimaryKey(long userGroupGroupRoleId) {
		return fetchByPrimaryKey((Serializable)userGroupGroupRoleId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "userGroupGroupRoleId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERGROUPGROUPROLE;
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
		return UserGroupGroupRoleModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "UserGroupGroupRole";
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
		ctMergeColumnNames.add("userGroupId");
		ctStrictColumnNames.add("groupId");
		ctMergeColumnNames.add("roleId");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("userGroupGroupRoleId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {"userGroupId", "groupId", "roleId"});
	}

	/**
	 * Initializes the user group group role persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByUserGroupId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserGroupId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByUserGroupId", new String[] {Long.class.getName()},
					new String[] {"userGroupId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByUserGroupId", new String[] {Long.class.getName()},
					new String[] {"userGroupId"}, false),
				_SQL_SELECT_USERGROUPGROUPROLE_WHERE,
				_SQL_COUNT_USERGROUPGROUPROLE_WHERE,
				UserGroupGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"userGroupGroupRole.", "userGroupId",
					FinderColumn.Type.LONG, "=", true, true,
					UserGroupGroupRole::getUserGroupId));

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
				_SQL_SELECT_USERGROUPGROUPROLE_WHERE,
				_SQL_COUNT_USERGROUPGROUPROLE_WHERE,
				UserGroupGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"userGroupGroupRole.", "groupId", FinderColumn.Type.LONG,
					"=", true, true, UserGroupGroupRole::getGroupId));

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
				_SQL_SELECT_USERGROUPGROUPROLE_WHERE,
				_SQL_COUNT_USERGROUPGROUPROLE_WHERE,
				UserGroupGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
				"", "", null,
				new FinderColumn<>(
					"userGroupGroupRole.", "roleId", FinderColumn.Type.LONG,
					"=", true, true, UserGroupGroupRole::getRoleId));

		_collectionPersistenceFinderByU_G = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_G",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userGroupId", "groupId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_G",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userGroupId", "groupId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_G",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userGroupId", "groupId"}, false),
			_SQL_SELECT_USERGROUPGROUPROLE_WHERE,
			_SQL_COUNT_USERGROUPGROUPROLE_WHERE,
			UserGroupGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"userGroupGroupRole.", "userGroupId", FinderColumn.Type.LONG,
				"=", true, true, UserGroupGroupRole::getUserGroupId),
			new FinderColumn<>(
				"userGroupGroupRole.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, UserGroupGroupRole::getGroupId));

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
			_SQL_SELECT_USERGROUPGROUPROLE_WHERE,
			_SQL_COUNT_USERGROUPGROUPROLE_WHERE,
			UserGroupGroupRoleModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
			"", null,
			new FinderColumn<>(
				"userGroupGroupRole.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, UserGroupGroupRole::getGroupId),
			new FinderColumn<>(
				"userGroupGroupRole.", "roleId", FinderColumn.Type.LONG, "=",
				true, true, UserGroupGroupRole::getRoleId));

		_uniquePersistenceFinderByU_G_R = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_G_R",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"userGroupId", "groupId", "roleId"}, 0, 0, false,
				UserGroupGroupRole::getUserGroupId,
				UserGroupGroupRole::getGroupId, UserGroupGroupRole::getRoleId),
			_SQL_SELECT_USERGROUPGROUPROLE_WHERE, "",
			new FinderColumn<>(
				"userGroupGroupRole.", "userGroupId", FinderColumn.Type.LONG,
				"=", true, true, UserGroupGroupRole::getUserGroupId),
			new FinderColumn<>(
				"userGroupGroupRole.", "groupId", FinderColumn.Type.LONG, "=",
				true, true, UserGroupGroupRole::getGroupId),
			new FinderColumn<>(
				"userGroupGroupRole.", "roleId", FinderColumn.Type.LONG, "=",
				true, true, UserGroupGroupRole::getRoleId));

		UserGroupGroupRoleUtil.setPersistence(this);
	}

	public void destroy() {
		UserGroupGroupRoleUtil.setPersistence(null);

		EntityCacheUtil.removeCache(UserGroupGroupRoleImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		UserGroupGroupRoleModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_USERGROUPGROUPROLE =
		"SELECT userGroupGroupRole FROM UserGroupGroupRole userGroupGroupRole";

	private static final String _SQL_SELECT_USERGROUPGROUPROLE_WHERE =
		"SELECT userGroupGroupRole FROM UserGroupGroupRole userGroupGroupRole WHERE ";

	private static final String _SQL_COUNT_USERGROUPGROUPROLE_WHERE =
		"SELECT COUNT(userGroupGroupRole) FROM UserGroupGroupRole userGroupGroupRole WHERE ";

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2117441271