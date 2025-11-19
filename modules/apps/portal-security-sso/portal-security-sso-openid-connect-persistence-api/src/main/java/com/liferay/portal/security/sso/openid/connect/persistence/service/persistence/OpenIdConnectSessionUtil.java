/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the open ID connect session service. This utility wraps <code>com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.impl.OpenIdConnectSessionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @see OpenIdConnectSessionPersistence
 * @generated
 */
public class OpenIdConnectSessionUtil {

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
	public static void clearCache(OpenIdConnectSession openIdConnectSession) {
		getPersistence().clearCache(openIdConnectSession);
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
	public static Map<Serializable, OpenIdConnectSession> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<OpenIdConnectSession> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<OpenIdConnectSession> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<OpenIdConnectSession> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static OpenIdConnectSession update(
		OpenIdConnectSession openIdConnectSession) {

		return getPersistence().update(openIdConnectSession);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static OpenIdConnectSession update(
		OpenIdConnectSession openIdConnectSession,
		ServiceContext serviceContext) {

		return getPersistence().update(openIdConnectSession, serviceContext);
	}

	/**
	 * Returns all the open ID connect sessions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the open ID connect sessions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession findByUserId_First(
			long userId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByUserId_First(
		long userId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the last open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession findByUserId_Last(
			long userId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByUserId_Last(userId, orderByComparator);
	}

	/**
	 * Returns the last open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByUserId_Last(
		long userId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().fetchByUserId_Last(userId, orderByComparator);
	}

	/**
	 * Returns the open ID connect sessions before and after the current open ID connect session in the ordered set where userId = &#63;.
	 *
	 * @param openIdConnectSessionId the primary key of the current open ID connect session
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	public static OpenIdConnectSession[] findByUserId_PrevAndNext(
			long openIdConnectSessionId, long userId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByUserId_PrevAndNext(
			openIdConnectSessionId, userId, orderByComparator);
	}

	/**
	 * Removes all the open ID connect sessions where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of open ID connect sessions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching open ID connect sessions
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @return the matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		return getPersistence().findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate);
	}

	/**
	 * Returns a range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end) {

		return getPersistence().findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, start, end);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate, int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtAccessTokenExpirationDate(
			accessTokenExpirationDate, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession findByLtAccessTokenExpirationDate_First(
			Date accessTokenExpirationDate,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByLtAccessTokenExpirationDate_First(
			accessTokenExpirationDate, orderByComparator);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByLtAccessTokenExpirationDate_First(
		Date accessTokenExpirationDate,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().fetchByLtAccessTokenExpirationDate_First(
			accessTokenExpirationDate, orderByComparator);
	}

	/**
	 * Returns the last open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession findByLtAccessTokenExpirationDate_Last(
			Date accessTokenExpirationDate,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByLtAccessTokenExpirationDate_Last(
			accessTokenExpirationDate, orderByComparator);
	}

	/**
	 * Returns the last open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByLtAccessTokenExpirationDate_Last(
		Date accessTokenExpirationDate,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().fetchByLtAccessTokenExpirationDate_Last(
			accessTokenExpirationDate, orderByComparator);
	}

	/**
	 * Returns the open ID connect sessions before and after the current open ID connect session in the ordered set where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param openIdConnectSessionId the primary key of the current open ID connect session
	 * @param accessTokenExpirationDate the access token expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	public static OpenIdConnectSession[]
			findByLtAccessTokenExpirationDate_PrevAndNext(
				long openIdConnectSessionId, Date accessTokenExpirationDate,
				OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByLtAccessTokenExpirationDate_PrevAndNext(
			openIdConnectSessionId, accessTokenExpirationDate,
			orderByComparator);
	}

	/**
	 * Removes all the open ID connect sessions where accessTokenExpirationDate &lt; &#63; from the database.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 */
	public static void removeByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		getPersistence().removeByLtAccessTokenExpirationDate(
			accessTokenExpirationDate);
	}

	/**
	 * Returns the number of open ID connect sessions where accessTokenExpirationDate &lt; &#63;.
	 *
	 * @param accessTokenExpirationDate the access token expiration date
	 * @return the number of matching open ID connect sessions
	 */
	public static int countByLtAccessTokenExpirationDate(
		Date accessTokenExpirationDate) {

		return getPersistence().countByLtAccessTokenExpirationDate(
			accessTokenExpirationDate);
	}

	/**
	 * Returns the open ID connect session where authServerWellKnownURI = &#63; and sid = &#63; or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param sid the sid
	 * @return the matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession findByA_S(
			String authServerWellKnownURI, String sid)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByA_S(authServerWellKnownURI, sid);
	}

	/**
	 * Returns the open ID connect session where authServerWellKnownURI = &#63; and sid = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param sid the sid
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByA_S(
		String authServerWellKnownURI, String sid) {

		return getPersistence().fetchByA_S(authServerWellKnownURI, sid);
	}

	/**
	 * Returns the open ID connect session where authServerWellKnownURI = &#63; and sid = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param sid the sid
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByA_S(
		String authServerWellKnownURI, String sid, boolean useFinderCache) {

		return getPersistence().fetchByA_S(
			authServerWellKnownURI, sid, useFinderCache);
	}

	/**
	 * Removes the open ID connect session where authServerWellKnownURI = &#63; and sid = &#63; from the database.
	 *
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param sid the sid
	 * @return the open ID connect session that was removed
	 */
	public static OpenIdConnectSession removeByA_S(
			String authServerWellKnownURI, String sid)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().removeByA_S(authServerWellKnownURI, sid);
	}

	/**
	 * Returns the number of open ID connect sessions where authServerWellKnownURI = &#63; and sid = &#63;.
	 *
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param sid the sid
	 * @return the number of matching open ID connect sessions
	 */
	public static int countByA_S(String authServerWellKnownURI, String sid) {
		return getPersistence().countByA_S(authServerWellKnownURI, sid);
	}

	/**
	 * Returns all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		return getPersistence().findByC_A_C(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns a range of all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		int start, int end) {

		return getPersistence().findByC_A_C(
			companyId, authServerWellKnownURI, clientId, start, end);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().findByC_A_C(
			companyId, authServerWellKnownURI, clientId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId,
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_A_C(
			companyId, authServerWellKnownURI, clientId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession findByC_A_C_First(
			long companyId, String authServerWellKnownURI, String clientId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByC_A_C_First(
			companyId, authServerWellKnownURI, clientId, orderByComparator);
	}

	/**
	 * Returns the first open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByC_A_C_First(
		long companyId, String authServerWellKnownURI, String clientId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().fetchByC_A_C_First(
			companyId, authServerWellKnownURI, clientId, orderByComparator);
	}

	/**
	 * Returns the last open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession findByC_A_C_Last(
			long companyId, String authServerWellKnownURI, String clientId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByC_A_C_Last(
			companyId, authServerWellKnownURI, clientId, orderByComparator);
	}

	/**
	 * Returns the last open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByC_A_C_Last(
		long companyId, String authServerWellKnownURI, String clientId,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().fetchByC_A_C_Last(
			companyId, authServerWellKnownURI, clientId, orderByComparator);
	}

	/**
	 * Returns the open ID connect sessions before and after the current open ID connect session in the ordered set where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param openIdConnectSessionId the primary key of the current open ID connect session
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	public static OpenIdConnectSession[] findByC_A_C_PrevAndNext(
			long openIdConnectSessionId, long companyId,
			String authServerWellKnownURI, String clientId,
			OrderByComparator<OpenIdConnectSession> orderByComparator)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByC_A_C_PrevAndNext(
			openIdConnectSessionId, companyId, authServerWellKnownURI, clientId,
			orderByComparator);
	}

	/**
	 * Removes all the open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 */
	public static void removeByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		getPersistence().removeByC_A_C(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns the number of open ID connect sessions where companyId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the number of matching open ID connect sessions
	 */
	public static int countByC_A_C(
		long companyId, String authServerWellKnownURI, String clientId) {

		return getPersistence().countByC_A_C(
			companyId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching open ID connect session
	 * @throws NoSuchSessionException if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession findByU_A_C(
			long userId, String authServerWellKnownURI, String clientId)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByU_A_C(
			userId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByU_A_C(
		long userId, String authServerWellKnownURI, String clientId) {

		return getPersistence().fetchByU_A_C(
			userId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect session, or <code>null</code> if a matching open ID connect session could not be found
	 */
	public static OpenIdConnectSession fetchByU_A_C(
		long userId, String authServerWellKnownURI, String clientId,
		boolean useFinderCache) {

		return getPersistence().fetchByU_A_C(
			userId, authServerWellKnownURI, clientId, useFinderCache);
	}

	/**
	 * Removes the open ID connect session where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the open ID connect session that was removed
	 */
	public static OpenIdConnectSession removeByU_A_C(
			long userId, String authServerWellKnownURI, String clientId)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().removeByU_A_C(
			userId, authServerWellKnownURI, clientId);
	}

	/**
	 * Returns the number of open ID connect sessions where userId = &#63; and authServerWellKnownURI = &#63; and clientId = &#63;.
	 *
	 * @param userId the user ID
	 * @param authServerWellKnownURI the auth server well known uri
	 * @param clientId the client ID
	 * @return the number of matching open ID connect sessions
	 */
	public static int countByU_A_C(
		long userId, String authServerWellKnownURI, String clientId) {

		return getPersistence().countByU_A_C(
			userId, authServerWellKnownURI, clientId);
	}

	/**
	 * Caches the open ID connect session in the entity cache if it is enabled.
	 *
	 * @param openIdConnectSession the open ID connect session
	 */
	public static void cacheResult(OpenIdConnectSession openIdConnectSession) {
		getPersistence().cacheResult(openIdConnectSession);
	}

	/**
	 * Caches the open ID connect sessions in the entity cache if it is enabled.
	 *
	 * @param openIdConnectSessions the open ID connect sessions
	 */
	public static void cacheResult(
		List<OpenIdConnectSession> openIdConnectSessions) {

		getPersistence().cacheResult(openIdConnectSessions);
	}

	/**
	 * Creates a new open ID connect session with the primary key. Does not add the open ID connect session to the database.
	 *
	 * @param openIdConnectSessionId the primary key for the new open ID connect session
	 * @return the new open ID connect session
	 */
	public static OpenIdConnectSession create(long openIdConnectSessionId) {
		return getPersistence().create(openIdConnectSessionId);
	}

	/**
	 * Removes the open ID connect session with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session that was removed
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	public static OpenIdConnectSession remove(long openIdConnectSessionId)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().remove(openIdConnectSessionId);
	}

	public static OpenIdConnectSession updateImpl(
		OpenIdConnectSession openIdConnectSession) {

		return getPersistence().updateImpl(openIdConnectSession);
	}

	/**
	 * Returns the open ID connect session with the primary key or throws a <code>NoSuchSessionException</code> if it could not be found.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session
	 * @throws NoSuchSessionException if a open ID connect session with the primary key could not be found
	 */
	public static OpenIdConnectSession findByPrimaryKey(
			long openIdConnectSessionId)
		throws com.liferay.portal.security.sso.openid.connect.persistence.
			exception.NoSuchSessionException {

		return getPersistence().findByPrimaryKey(openIdConnectSessionId);
	}

	/**
	 * Returns the open ID connect session with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param openIdConnectSessionId the primary key of the open ID connect session
	 * @return the open ID connect session, or <code>null</code> if a open ID connect session with the primary key could not be found
	 */
	public static OpenIdConnectSession fetchByPrimaryKey(
		long openIdConnectSessionId) {

		return getPersistence().fetchByPrimaryKey(openIdConnectSessionId);
	}

	/**
	 * Returns all the open ID connect sessions.
	 *
	 * @return the open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the open ID connect sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @return the range of open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findAll(
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the open ID connect sessions.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OpenIdConnectSessionModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of open ID connect sessions
	 * @param end the upper bound of the range of open ID connect sessions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of open ID connect sessions
	 */
	public static List<OpenIdConnectSession> findAll(
		int start, int end,
		OrderByComparator<OpenIdConnectSession> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the open ID connect sessions from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of open ID connect sessions.
	 *
	 * @return the number of open ID connect sessions
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static OpenIdConnectSessionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		OpenIdConnectSessionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile OpenIdConnectSessionPersistence _persistence;

}