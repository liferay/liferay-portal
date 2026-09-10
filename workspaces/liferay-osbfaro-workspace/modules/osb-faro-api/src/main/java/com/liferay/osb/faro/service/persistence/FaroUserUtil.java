/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.service.persistence;

import com.liferay.osb.faro.model.FaroUser;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the faro user service. This utility wraps <code>com.liferay.osb.faro.service.persistence.impl.FaroUserPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Matthew Kong
 * @see FaroUserPersistence
 * @generated
 */
public class FaroUserUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<FaroUser> faroUsers) {
		getPersistence().cacheResult(faroUsers);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(FaroUser faroUser) {
		getPersistence().cacheResult(faroUser);
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
	public static void clearCache(FaroUser faroUser) {
		getPersistence().clearCache(faroUser);
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
	public static Map<Serializable, FaroUser> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<FaroUser> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<FaroUser> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<FaroUser> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static FaroUser update(FaroUser faroUser) {
		return getPersistence().update(faroUser);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static FaroUser update(
		FaroUser faroUser, ServiceContext serviceContext) {

		return getPersistence().update(faroUser, serviceContext);
	}

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByGroupId_First(
			long groupId, OrderByComparator<FaroUser> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByGroupId_First(
		long groupId, OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the faro users where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of faro users where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching faro users
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the faro users where liveUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param liveUserId the live user ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByLiveUserId(
		long liveUserId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByLiveUserId(
			liveUserId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where liveUserId = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByLiveUserId_First(
			long liveUserId, OrderByComparator<FaroUser> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByLiveUserId_First(
			liveUserId, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where liveUserId = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByLiveUserId_First(
		long liveUserId, OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().fetchByLiveUserId_First(
			liveUserId, orderByComparator);
	}

	/**
	 * Removes all the faro users where liveUserId = &#63; from the database.
	 *
	 * @param liveUserId the live user ID
	 */
	public static void removeByLiveUserId(long liveUserId) {
		getPersistence().removeByLiveUserId(liveUserId);
	}

	/**
	 * Returns the number of faro users where liveUserId = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @return the number of matching faro users
	 */
	public static int countByLiveUserId(long liveUserId) {
		return getPersistence().countByLiveUserId(liveUserId);
	}

	/**
	 * Returns the faro user where key = &#63; or throws a <code>NoSuchFaroUserException</code> if it could not be found.
	 *
	 * @param key the key
	 * @return the matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByKey(String key)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByKey(key);
	}

	/**
	 * Returns the faro user where key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByKey(String key, boolean useFinderCache) {
		return getPersistence().fetchByKey(key, useFinderCache);
	}

	/**
	 * Removes the faro user where key = &#63; from the database.
	 *
	 * @param key the key
	 * @return the faro user that was removed
	 */
	public static FaroUser removeByKey(String key)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().removeByKey(key);
	}

	/**
	 * Returns the number of faro users where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching faro users
	 */
	public static int countByKey(String key) {
		return getPersistence().countByKey(key);
	}

	/**
	 * Returns the faro user where groupId = &#63; and liveUserId = &#63; or throws a <code>NoSuchFaroUserException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @return the matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByG_L(long groupId, long liveUserId)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByG_L(groupId, liveUserId);
	}

	/**
	 * Returns the faro user where groupId = &#63; and liveUserId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByG_L(
		long groupId, long liveUserId, boolean useFinderCache) {

		return getPersistence().fetchByG_L(groupId, liveUserId, useFinderCache);
	}

	/**
	 * Removes the faro user where groupId = &#63; and liveUserId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @return the faro user that was removed
	 */
	public static FaroUser removeByG_L(long groupId, long liveUserId)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().removeByG_L(groupId, liveUserId);
	}

	/**
	 * Returns the number of faro users where groupId = &#63; and liveUserId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @return the number of matching faro users
	 */
	public static int countByG_L(long groupId, long liveUserId) {
		return getPersistence().countByG_L(groupId, liveUserId);
	}

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByG_R(
		long groupId, long roleId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_R(
			groupId, roleId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByG_R_First(
			long groupId, long roleId,
			OrderByComparator<FaroUser> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByG_R_First(
			groupId, roleId, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByG_R_First(
		long groupId, long roleId,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().fetchByG_R_First(
			groupId, roleId, orderByComparator);
	}

	/**
	 * Removes all the faro users where groupId = &#63; and roleId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 */
	public static void removeByG_R(long groupId, long roleId) {
		getPersistence().removeByG_R(groupId, roleId);
	}

	/**
	 * Returns the number of faro users where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the number of matching faro users
	 */
	public static int countByG_R(long groupId, long roleId) {
		return getPersistence().countByG_R(groupId, roleId);
	}

	/**
	 * Returns the faro user where groupId = &#63; and emailAddress = &#63; or throws a <code>NoSuchFaroUserException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @return the matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByG_E(long groupId, String emailAddress)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByG_E(groupId, emailAddress);
	}

	/**
	 * Returns the faro user where groupId = &#63; and emailAddress = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByG_E(
		long groupId, String emailAddress, boolean useFinderCache) {

		return getPersistence().fetchByG_E(
			groupId, emailAddress, useFinderCache);
	}

	/**
	 * Removes the faro user where groupId = &#63; and emailAddress = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @return the faro user that was removed
	 */
	public static FaroUser removeByG_E(long groupId, String emailAddress)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().removeByG_E(groupId, emailAddress);
	}

	/**
	 * Returns the number of faro users where groupId = &#63; and emailAddress = &#63;.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @return the number of matching faro users
	 */
	public static int countByG_E(long groupId, String emailAddress) {
		return getPersistence().countByG_E(groupId, emailAddress);
	}

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByG_S_First(
			long groupId, int status,
			OrderByComparator<FaroUser> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Removes all the faro users where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of faro users where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching faro users
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns an ordered range of all the faro users where liveUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByL_S(
		long liveUserId, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByL_S(
			liveUserId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where liveUserId = &#63; and status = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByL_S_First(
			long liveUserId, int status,
			OrderByComparator<FaroUser> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByL_S_First(
			liveUserId, status, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where liveUserId = &#63; and status = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByL_S_First(
		long liveUserId, int status,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().fetchByL_S_First(
			liveUserId, status, orderByComparator);
	}

	/**
	 * Removes all the faro users where liveUserId = &#63; and status = &#63; from the database.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 */
	public static void removeByL_S(long liveUserId, int status) {
		getPersistence().removeByL_S(liveUserId, status);
	}

	/**
	 * Returns the number of faro users where liveUserId = &#63; and status = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @return the number of matching faro users
	 */
	public static int countByL_S(long liveUserId, int status) {
		return getPersistence().countByL_S(liveUserId, status);
	}

	/**
	 * Returns an ordered range of all the faro users where emailAddress = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByE_S(
		String emailAddress, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByE_S(
			emailAddress, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first faro user in the ordered set where emailAddress = &#63; and status = &#63;.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user
	 * @throws NoSuchFaroUserException if a matching faro user could not be found
	 */
	public static FaroUser findByE_S_First(
			String emailAddress, int status,
			OrderByComparator<FaroUser> orderByComparator)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByE_S_First(
			emailAddress, status, orderByComparator);
	}

	/**
	 * Returns the first faro user in the ordered set where emailAddress = &#63; and status = &#63;.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByE_S_First(
		String emailAddress, int status,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().fetchByE_S_First(
			emailAddress, status, orderByComparator);
	}

	/**
	 * Removes all the faro users where emailAddress = &#63; and status = &#63; from the database.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 */
	public static void removeByE_S(String emailAddress, int status) {
		getPersistence().removeByE_S(emailAddress, status);
	}

	/**
	 * Returns the number of faro users where emailAddress = &#63; and status = &#63;.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @return the number of matching faro users
	 */
	public static int countByE_S(String emailAddress, int status) {
		return getPersistence().countByE_S(emailAddress, status);
	}

	/**
	 * Creates a new faro user with the primary key. Does not add the faro user to the database.
	 *
	 * @param faroUserId the primary key for the new faro user
	 * @return the new faro user
	 */
	public static FaroUser create(long faroUserId) {
		return getPersistence().create(faroUserId);
	}

	/**
	 * Removes the faro user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param faroUserId the primary key of the faro user
	 * @return the faro user that was removed
	 * @throws NoSuchFaroUserException if a faro user with the primary key could not be found
	 */
	public static FaroUser remove(long faroUserId)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().remove(faroUserId);
	}

	public static FaroUser updateImpl(FaroUser faroUser) {
		return getPersistence().updateImpl(faroUser);
	}

	/**
	 * Returns the faro user with the primary key or throws a <code>NoSuchFaroUserException</code> if it could not be found.
	 *
	 * @param faroUserId the primary key of the faro user
	 * @return the faro user
	 * @throws NoSuchFaroUserException if a faro user with the primary key could not be found
	 */
	public static FaroUser findByPrimaryKey(long faroUserId)
		throws com.liferay.osb.faro.exception.NoSuchFaroUserException {

		return getPersistence().findByPrimaryKey(faroUserId);
	}

	/**
	 * Returns the faro user with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param faroUserId the primary key of the faro user
	 * @return the faro user, or <code>null</code> if a faro user with the primary key could not be found
	 */
	public static FaroUser fetchByPrimaryKey(long faroUserId) {
		return getPersistence().fetchByPrimaryKey(faroUserId);
	}

	/**
	 * Returns the faro user where key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param key the key
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByKey(String key) {
		return getPersistence().fetchByKey(key);
	}

	/**
	 * Returns the faro user where groupId = &#63; and liveUserId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param liveUserId the live user ID
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByG_L(long groupId, long liveUserId) {
		return getPersistence().fetchByG_L(groupId, liveUserId);
	}

	/**
	 * Returns the faro user where groupId = &#63; and emailAddress = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param emailAddress the email address
	 * @return the matching faro user, or <code>null</code> if a matching faro user could not be found
	 */
	public static FaroUser fetchByG_E(long groupId, String emailAddress) {
		return getPersistence().fetchByG_E(groupId, emailAddress);
	}

	/**
	 * Returns all the faro users where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching faro users
	 */
	public static List<FaroUser> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the faro users where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @return the range of matching faro users
	 */
	public static List<FaroUser> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the faro users where liveUserId = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @return the matching faro users
	 */
	public static List<FaroUser> findByLiveUserId(long liveUserId) {
		return getPersistence().findByLiveUserId(liveUserId);
	}

	/**
	 * Returns a range of all the faro users where liveUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param liveUserId the live user ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @return the range of matching faro users
	 */
	public static List<FaroUser> findByLiveUserId(
		long liveUserId, int start, int end) {

		return getPersistence().findByLiveUserId(liveUserId, start, end);
	}

	/**
	 * Returns an ordered range of all the faro users where liveUserId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param liveUserId the live user ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByLiveUserId(
		long liveUserId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().findByLiveUserId(
			liveUserId, start, end, orderByComparator);
	}

	/**
	 * Returns all the faro users where groupId = &#63; and roleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @return the matching faro users
	 */
	public static List<FaroUser> findByG_R(long groupId, long roleId) {
		return getPersistence().findByG_R(groupId, roleId);
	}

	/**
	 * Returns a range of all the faro users where groupId = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @return the range of matching faro users
	 */
	public static List<FaroUser> findByG_R(
		long groupId, long roleId, int start, int end) {

		return getPersistence().findByG_R(groupId, roleId, start, end);
	}

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63; and roleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param roleId the role ID
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByG_R(
		long groupId, long roleId, int start, int end,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().findByG_R(
			groupId, roleId, start, end, orderByComparator);
	}

	/**
	 * Returns all the faro users where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching faro users
	 */
	public static List<FaroUser> findByG_S(long groupId, int status) {
		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the faro users where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @return the range of matching faro users
	 */
	public static List<FaroUser> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the faro users where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the faro users where liveUserId = &#63; and status = &#63;.
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @return the matching faro users
	 */
	public static List<FaroUser> findByL_S(long liveUserId, int status) {
		return getPersistence().findByL_S(liveUserId, status);
	}

	/**
	 * Returns a range of all the faro users where liveUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @return the range of matching faro users
	 */
	public static List<FaroUser> findByL_S(
		long liveUserId, int status, int start, int end) {

		return getPersistence().findByL_S(liveUserId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the faro users where liveUserId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param liveUserId the live user ID
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByL_S(
		long liveUserId, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().findByL_S(
			liveUserId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the faro users where emailAddress = &#63; and status = &#63;.
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @return the matching faro users
	 */
	public static List<FaroUser> findByE_S(String emailAddress, int status) {
		return getPersistence().findByE_S(emailAddress, status);
	}

	/**
	 * Returns a range of all the faro users where emailAddress = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @return the range of matching faro users
	 */
	public static List<FaroUser> findByE_S(
		String emailAddress, int status, int start, int end) {

		return getPersistence().findByE_S(emailAddress, status, start, end);
	}

	/**
	 * Returns an ordered range of all the faro users where emailAddress = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.osb.faro.model.impl.FaroUserModelImpl</code>.
	 * </p>
	 *
	 * @param emailAddress the email address
	 * @param status the status
	 * @param start the lower bound of the range of faro users
	 * @param end the upper bound of the range of faro users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching faro users
	 */
	public static List<FaroUser> findByE_S(
		String emailAddress, int status, int start, int end,
		OrderByComparator<FaroUser> orderByComparator) {

		return getPersistence().findByE_S(
			emailAddress, status, start, end, orderByComparator);
	}

	public static FaroUserPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(FaroUserPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile FaroUserPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:166202172