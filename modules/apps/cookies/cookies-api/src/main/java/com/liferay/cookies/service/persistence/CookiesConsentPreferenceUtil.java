/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.service.persistence;

import com.liferay.cookies.model.CookiesConsentPreference;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cookies consent preference service. This utility wraps <code>com.liferay.cookies.service.persistence.impl.CookiesConsentPreferencePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Christopher Kian
 * @see CookiesConsentPreferencePersistence
 * @generated
 */
public class CookiesConsentPreferenceUtil {

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
	public static void clearCache(
		CookiesConsentPreference cookiesConsentPreference) {

		getPersistence().clearCache(cookiesConsentPreference);
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
	public static Map<Serializable, CookiesConsentPreference>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CookiesConsentPreference> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CookiesConsentPreference> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CookiesConsentPreference> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CookiesConsentPreference update(
		CookiesConsentPreference cookiesConsentPreference) {

		return getPersistence().update(cookiesConsentPreference);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CookiesConsentPreference update(
		CookiesConsentPreference cookiesConsentPreference,
		ServiceContext serviceContext) {

		return getPersistence().update(
			cookiesConsentPreference, serviceContext);
	}

	/**
	 * Returns all the cookies consent preferences where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the cookies consent preferences where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference findByUserId_First(
			long userId,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws com.liferay.cookies.exception.
			NoSuchCookiesConsentPreferenceException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference fetchByUserId_First(
		long userId,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the cookies consent preferences where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching cookies consent preferences
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate) {

		return getPersistence().findByExpirationDate(expirationDate);
	}

	/**
	 * Returns a range of all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end) {

		return getPersistence().findByExpirationDate(
			expirationDate, start, end);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return getPersistence().findByExpirationDate(
			expirationDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByExpirationDate(
			expirationDate, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference findByExpirationDate_First(
			Date expirationDate,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws com.liferay.cookies.exception.
			NoSuchCookiesConsentPreferenceException {

		return getPersistence().findByExpirationDate_First(
			expirationDate, orderByComparator);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference fetchByExpirationDate_First(
		Date expirationDate,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return getPersistence().fetchByExpirationDate_First(
			expirationDate, orderByComparator);
	}

	/**
	 * Removes all the cookies consent preferences where expirationDate = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	public static void removeByExpirationDate(Date expirationDate) {
		getPersistence().removeByExpirationDate(expirationDate);
	}

	/**
	 * Returns the number of cookies consent preferences where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching cookies consent preferences
	 */
	public static int countByExpirationDate(Date expirationDate) {
		return getPersistence().countByExpirationDate(expirationDate);
	}

	/**
	 * Returns all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @return the matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByU_D(
		long userId, String domain) {

		return getPersistence().findByU_D(userId, domain);
	}

	/**
	 * Returns a range of all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end) {

		return getPersistence().findByU_D(userId, domain, start, end);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return getPersistence().findByU_D(
			userId, domain, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_D(
			userId, domain, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference findByU_D_First(
			long userId, String domain,
			OrderByComparator<CookiesConsentPreference> orderByComparator)
		throws com.liferay.cookies.exception.
			NoSuchCookiesConsentPreferenceException {

		return getPersistence().findByU_D_First(
			userId, domain, orderByComparator);
	}

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference fetchByU_D_First(
		long userId, String domain,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return getPersistence().fetchByU_D_First(
			userId, domain, orderByComparator);
	}

	/**
	 * Removes all the cookies consent preferences where userId = &#63; and domain = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 */
	public static void removeByU_D(long userId, String domain) {
		getPersistence().removeByU_D(userId, domain);
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @return the number of matching cookies consent preferences
	 */
	public static int countByU_D(long userId, String domain) {
		return getPersistence().countByU_D(userId, domain);
	}

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or throws a <code>NoSuchCookiesConsentPreferenceException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference findByU_D_N(
			long userId, String domain, String name)
		throws com.liferay.cookies.exception.
			NoSuchCookiesConsentPreferenceException {

		return getPersistence().findByU_D_N(userId, domain, name);
	}

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference fetchByU_D_N(
		long userId, String domain, String name) {

		return getPersistence().fetchByU_D_N(userId, domain, name);
	}

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public static CookiesConsentPreference fetchByU_D_N(
		long userId, String domain, String name, boolean useFinderCache) {

		return getPersistence().fetchByU_D_N(
			userId, domain, name, useFinderCache);
	}

	/**
	 * Removes the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the cookies consent preference that was removed
	 */
	public static CookiesConsentPreference removeByU_D_N(
			long userId, String domain, String name)
		throws com.liferay.cookies.exception.
			NoSuchCookiesConsentPreferenceException {

		return getPersistence().removeByU_D_N(userId, domain, name);
	}

	/**
	 * Returns the number of cookies consent preferences where userId = &#63; and domain = &#63; and name = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the number of matching cookies consent preferences
	 */
	public static int countByU_D_N(long userId, String domain, String name) {
		return getPersistence().countByU_D_N(userId, domain, name);
	}

	/**
	 * Caches the cookies consent preference in the entity cache if it is enabled.
	 *
	 * @param cookiesConsentPreference the cookies consent preference
	 */
	public static void cacheResult(
		CookiesConsentPreference cookiesConsentPreference) {

		getPersistence().cacheResult(cookiesConsentPreference);
	}

	/**
	 * Caches the cookies consent preferences in the entity cache if it is enabled.
	 *
	 * @param cookiesConsentPreferences the cookies consent preferences
	 */
	public static void cacheResult(
		List<CookiesConsentPreference> cookiesConsentPreferences) {

		getPersistence().cacheResult(cookiesConsentPreferences);
	}

	/**
	 * Creates a new cookies consent preference with the primary key. Does not add the cookies consent preference to the database.
	 *
	 * @param cookiesConsentPreferenceId the primary key for the new cookies consent preference
	 * @return the new cookies consent preference
	 */
	public static CookiesConsentPreference create(
		long cookiesConsentPreferenceId) {

		return getPersistence().create(cookiesConsentPreferenceId);
	}

	/**
	 * Removes the cookies consent preference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference that was removed
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	public static CookiesConsentPreference remove(
			long cookiesConsentPreferenceId)
		throws com.liferay.cookies.exception.
			NoSuchCookiesConsentPreferenceException {

		return getPersistence().remove(cookiesConsentPreferenceId);
	}

	public static CookiesConsentPreference updateImpl(
		CookiesConsentPreference cookiesConsentPreference) {

		return getPersistence().updateImpl(cookiesConsentPreference);
	}

	/**
	 * Returns the cookies consent preference with the primary key or throws a <code>NoSuchCookiesConsentPreferenceException</code> if it could not be found.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	public static CookiesConsentPreference findByPrimaryKey(
			long cookiesConsentPreferenceId)
		throws com.liferay.cookies.exception.
			NoSuchCookiesConsentPreferenceException {

		return getPersistence().findByPrimaryKey(cookiesConsentPreferenceId);
	}

	/**
	 * Returns the cookies consent preference with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference, or <code>null</code> if a cookies consent preference with the primary key could not be found
	 */
	public static CookiesConsentPreference fetchByPrimaryKey(
		long cookiesConsentPreferenceId) {

		return getPersistence().fetchByPrimaryKey(cookiesConsentPreferenceId);
	}

	/**
	 * Returns all the cookies consent preferences.
	 *
	 * @return the cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the cookies consent preferences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @return the range of cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findAll(
		int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cookies consent preferences.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>CookiesConsentPreferenceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of cookies consent preferences
	 * @param end the upper bound of the range of cookies consent preferences (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of cookies consent preferences
	 */
	public static List<CookiesConsentPreference> findAll(
		int start, int end,
		OrderByComparator<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the cookies consent preferences from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of cookies consent preferences.
	 *
	 * @return the number of cookies consent preferences
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static CookiesConsentPreferencePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CookiesConsentPreferencePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CookiesConsentPreferencePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1526253959