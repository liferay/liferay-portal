/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.RememberMeToken;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the remember me token service. This utility wraps <code>com.liferay.portal.service.persistence.impl.RememberMeTokenPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see RememberMeTokenPersistence
 * @generated
 */
public class RememberMeTokenUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<RememberMeToken> rememberMeTokens) {
		getPersistence().cacheResult(rememberMeTokens);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(RememberMeToken rememberMeToken) {
		getPersistence().cacheResult(rememberMeToken);
	}

	/**
	 * @see BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(RememberMeToken rememberMeToken) {
		getPersistence().clearCache(rememberMeToken);
	}

	/**
	 * @see BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, RememberMeToken> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<RememberMeToken> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<RememberMeToken> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<RememberMeToken> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<RememberMeToken> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static RememberMeToken update(RememberMeToken rememberMeToken) {
		return getPersistence().update(rememberMeToken);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static RememberMeToken update(
		RememberMeToken rememberMeToken, ServiceContext serviceContext) {

		return getPersistence().update(rememberMeToken, serviceContext);
	}

	/**
	 * Returns an ordered range of all the remember me tokens where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching remember me tokens
	 */
	public static List<RememberMeToken> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RememberMeToken> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first remember me token in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching remember me token
	 * @throws NoSuchRememberMeTokenException if a matching remember me token could not be found
	 */
	public static RememberMeToken findByUserId_First(
			long userId, OrderByComparator<RememberMeToken> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchRememberMeTokenException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first remember me token in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching remember me token, or <code>null</code> if a matching remember me token could not be found
	 */
	public static RememberMeToken fetchByUserId_First(
		long userId, OrderByComparator<RememberMeToken> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the remember me tokens where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of remember me tokens where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching remember me tokens
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns all the remember me tokens where expirationDate &le; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching remember me tokens
	 */
	public static List<RememberMeToken> findByLteExpirationDate(
		Date expirationDate) {

		return getPersistence().findByLteExpirationDate(expirationDate);
	}

	/**
	 * Returns a range of all the remember me tokens where expirationDate &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @return the range of matching remember me tokens
	 */
	public static List<RememberMeToken> findByLteExpirationDate(
		Date expirationDate, int start, int end) {

		return getPersistence().findByLteExpirationDate(
			expirationDate, start, end);
	}

	/**
	 * Returns an ordered range of all the remember me tokens where expirationDate &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching remember me tokens
	 */
	public static List<RememberMeToken> findByLteExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<RememberMeToken> orderByComparator) {

		return getPersistence().findByLteExpirationDate(
			expirationDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the remember me tokens where expirationDate &le; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching remember me tokens
	 */
	public static List<RememberMeToken> findByLteExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<RememberMeToken> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLteExpirationDate(
			expirationDate, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first remember me token in the ordered set where expirationDate &le; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching remember me token
	 * @throws NoSuchRememberMeTokenException if a matching remember me token could not be found
	 */
	public static RememberMeToken findByLteExpirationDate_First(
			Date expirationDate,
			OrderByComparator<RememberMeToken> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchRememberMeTokenException {

		return getPersistence().findByLteExpirationDate_First(
			expirationDate, orderByComparator);
	}

	/**
	 * Returns the first remember me token in the ordered set where expirationDate &le; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching remember me token, or <code>null</code> if a matching remember me token could not be found
	 */
	public static RememberMeToken fetchByLteExpirationDate_First(
		Date expirationDate,
		OrderByComparator<RememberMeToken> orderByComparator) {

		return getPersistence().fetchByLteExpirationDate_First(
			expirationDate, orderByComparator);
	}

	/**
	 * Removes all the remember me tokens where expirationDate &le; &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	public static void removeByLteExpirationDate(Date expirationDate) {
		getPersistence().removeByLteExpirationDate(expirationDate);
	}

	/**
	 * Returns the number of remember me tokens where expirationDate &le; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching remember me tokens
	 */
	public static int countByLteExpirationDate(Date expirationDate) {
		return getPersistence().countByLteExpirationDate(expirationDate);
	}

	/**
	 * Creates a new remember me token with the primary key. Does not add the remember me token to the database.
	 *
	 * @param rememberMeTokenId the primary key for the new remember me token
	 * @return the new remember me token
	 */
	public static RememberMeToken create(long rememberMeTokenId) {
		return getPersistence().create(rememberMeTokenId);
	}

	/**
	 * Removes the remember me token with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token that was removed
	 * @throws NoSuchRememberMeTokenException if a remember me token with the primary key could not be found
	 */
	public static RememberMeToken remove(long rememberMeTokenId)
		throws com.liferay.portal.kernel.exception.
			NoSuchRememberMeTokenException {

		return getPersistence().remove(rememberMeTokenId);
	}

	public static RememberMeToken updateImpl(RememberMeToken rememberMeToken) {
		return getPersistence().updateImpl(rememberMeToken);
	}

	/**
	 * Returns the remember me token with the primary key or throws a <code>NoSuchRememberMeTokenException</code> if it could not be found.
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token
	 * @throws NoSuchRememberMeTokenException if a remember me token with the primary key could not be found
	 */
	public static RememberMeToken findByPrimaryKey(long rememberMeTokenId)
		throws com.liferay.portal.kernel.exception.
			NoSuchRememberMeTokenException {

		return getPersistence().findByPrimaryKey(rememberMeTokenId);
	}

	/**
	 * Returns the remember me token with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param rememberMeTokenId the primary key of the remember me token
	 * @return the remember me token, or <code>null</code> if a remember me token with the primary key could not be found
	 */
	public static RememberMeToken fetchByPrimaryKey(long rememberMeTokenId) {
		return getPersistence().fetchByPrimaryKey(rememberMeTokenId);
	}

	/**
	 * Returns all the remember me tokens where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching remember me tokens
	 */
	public static List<RememberMeToken> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the remember me tokens where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @return the range of matching remember me tokens
	 */
	public static List<RememberMeToken> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the remember me tokens where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.RememberMeTokenModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of remember me tokens
	 * @param end the upper bound of the range of remember me tokens (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching remember me tokens
	 */
	public static List<RememberMeToken> findByUserId(
		long userId, int start, int end,
		OrderByComparator<RememberMeToken> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	public static RememberMeTokenPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(RememberMeTokenPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile RememberMeTokenPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:2133147552