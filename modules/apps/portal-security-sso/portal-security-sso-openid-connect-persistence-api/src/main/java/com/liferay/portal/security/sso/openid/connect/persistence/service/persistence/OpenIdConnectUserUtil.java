/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the open ID connect user service. This utility wraps <code>com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl.OpenIdConnectUserPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @see OpenIdConnectUserPersistence
 * @generated
 */
public class OpenIdConnectUserUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(OpenIdConnectUser openIdConnectUser) {
		getPersistence().clearCache(openIdConnectUser);
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
	public static Map<Serializable, OpenIdConnectUser> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<OpenIdConnectUser> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<OpenIdConnectUser> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<OpenIdConnectUser> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static OpenIdConnectUser update(
		OpenIdConnectUser openIdConnectUser) {

		return getPersistence().update(openIdConnectUser);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static OpenIdConnectUser update(
		OpenIdConnectUser openIdConnectUser, ServiceContext serviceContext) {

		return getPersistence().update(openIdConnectUser, serviceContext);
	}

	/**
	 * Returns all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching open ID connect users
	 */
	public static List<OpenIdConnectUser> findByC_U(
		long companyId, long userId) {

		return getPersistence().findByC_U(companyId, userId);
	}

	/**
	 * Returns a range of all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @return the range of matching open ID connect users
	 */
	public static List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end) {

		return getPersistence().findByC_U(companyId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect users
	 */
	public static List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect users
	 */
	public static List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U(
			companyId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	public static OpenIdConnectUser findByC_U_First(
			long companyId, long userId,
			OrderByComparator<OpenIdConnectUser> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchUserException {

		return getPersistence().findByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the first open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	public static OpenIdConnectUser fetchByC_U_First(
		long companyId, long userId,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		return getPersistence().fetchByC_U_First(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the last open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	public static OpenIdConnectUser findByC_U_Last(
			long companyId, long userId,
			OrderByComparator<OpenIdConnectUser> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchUserException {

		return getPersistence().findByC_U_Last(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the last open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	public static OpenIdConnectUser fetchByC_U_Last(
		long companyId, long userId,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		return getPersistence().fetchByC_U_Last(
			companyId, userId, orderByComparator);
	}

	/**
	 * Returns the open ID connect users before and after the current open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param openIdConnectUserId the primary key of the current open ID connect user
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next open ID connect user
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	public static OpenIdConnectUser[] findByC_U_PrevAndNext(
			long openIdConnectUserId, long companyId, long userId,
			OrderByComparator<OpenIdConnectUser> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchUserException {

		return getPersistence().findByC_U_PrevAndNext(
			openIdConnectUserId, companyId, userId, orderByComparator);
	}

	/**
	 * Removes all the open ID connect users where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public static void removeByC_U(long companyId, long userId) {
		getPersistence().removeByC_U(companyId, userId);
	}

	/**
	 * Returns the number of open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching open ID connect users
	 */
	public static int countByC_U(long companyId, long userId) {
		return getPersistence().countByC_U(companyId, userId);
	}

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or throws a <code>NoSuchUserException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	public static OpenIdConnectUser findByC_I_S(
			long companyId, String issuer, String subject)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchUserException {

		return getPersistence().findByC_I_S(companyId, issuer, subject);
	}

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	public static OpenIdConnectUser fetchByC_I_S(
		long companyId, String issuer, String subject) {

		return getPersistence().fetchByC_I_S(companyId, issuer, subject);
	}

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	public static OpenIdConnectUser fetchByC_I_S(
		long companyId, String issuer, String subject, boolean useFinderCache) {

		return getPersistence().fetchByC_I_S(
			companyId, issuer, subject, useFinderCache);
	}

	/**
	 * Removes the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the open ID connect user that was removed
	 */
	public static OpenIdConnectUser removeByC_I_S(
			long companyId, String issuer, String subject)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchUserException {

		return getPersistence().removeByC_I_S(companyId, issuer, subject);
	}

	/**
	 * Returns the number of open ID connect users where companyId = &#63; and issuer = &#63; and subject = &#63;.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the number of matching open ID connect users
	 */
	public static int countByC_I_S(
		long companyId, String issuer, String subject) {

		return getPersistence().countByC_I_S(companyId, issuer, subject);
	}

	/**
	 * Caches the open ID connect user in the entity cache if it is enabled.
	 *
	 * @param openIdConnectUser the open ID connect user
	 */
	public static void cacheResult(OpenIdConnectUser openIdConnectUser) {
		getPersistence().cacheResult(openIdConnectUser);
	}

	/**
	 * Caches the open ID connect users in the entity cache if it is enabled.
	 *
	 * @param openIdConnectUsers the open ID connect users
	 */
	public static void cacheResult(List<OpenIdConnectUser> openIdConnectUsers) {
		getPersistence().cacheResult(openIdConnectUsers);
	}

	/**
	 * Creates a new open ID connect user with the primary key. Does not add the open ID connect user to the database.
	 *
	 * @param openIdConnectUserId the primary key for the new open ID connect user
	 * @return the new open ID connect user
	 */
	public static OpenIdConnectUser create(long openIdConnectUserId) {
		return getPersistence().create(openIdConnectUserId);
	}

	/**
	 * Removes the open ID connect user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user that was removed
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	public static OpenIdConnectUser remove(long openIdConnectUserId)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchUserException {

		return getPersistence().remove(openIdConnectUserId);
	}

	public static OpenIdConnectUser updateImpl(
		OpenIdConnectUser openIdConnectUser) {

		return getPersistence().updateImpl(openIdConnectUser);
	}

	/**
	 * Returns the open ID connect user with the primary key or throws a <code>NoSuchUserException</code> if it could not be found.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	public static OpenIdConnectUser findByPrimaryKey(long openIdConnectUserId)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchUserException {

		return getPersistence().findByPrimaryKey(openIdConnectUserId);
	}

	/**
	 * Returns the open ID connect user with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user, or <code>null</code> if a open ID connect user with the primary key could not be found
	 */
	public static OpenIdConnectUser fetchByPrimaryKey(
		long openIdConnectUserId) {

		return getPersistence().fetchByPrimaryKey(openIdConnectUserId);
	}

	/**
	 * Returns all the open ID connect users.
	 *
	 * @return the open ID connect users
	 */
	public static List<OpenIdConnectUser> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the open ID connect users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @return the range of open ID connect users
	 */
	public static List<OpenIdConnectUser> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the open ID connect users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of open ID connect users
	 */
	public static List<OpenIdConnectUser> findAll(
		int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the open ID connect users.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectUserModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect users
	 * @param end the upper bound of the range of open ID connect users (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of open ID connect users
	 */
	public static List<OpenIdConnectUser> findAll(
		int start, int end,
		OrderByComparator<OpenIdConnectUser> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the open ID connect users from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of open ID connect users.
	 *
	 * @return the number of open ID connect users
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static OpenIdConnectUserPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		OpenIdConnectUserPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile OpenIdConnectUserPersistence _persistence;

}