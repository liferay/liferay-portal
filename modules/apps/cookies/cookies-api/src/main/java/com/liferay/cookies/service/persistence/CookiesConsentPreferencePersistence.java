/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.service.persistence;

import com.liferay.cookies.exception.NoSuchCookiesConsentPreferenceException;
import com.liferay.cookies.model.CookiesConsentPreference;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the cookies consent preference service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Christopher Kian
 * @see CookiesConsentPreferenceUtil
 * @generated
 */
@ProviderType
public interface CookiesConsentPreferencePersistence
	extends BasePersistence<CookiesConsentPreference> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link CookiesConsentPreferenceUtil} to access the cookies consent preference persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the cookies consent preferences where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching cookies consent preferences
	 */
	public java.util.List<CookiesConsentPreference> findByUserId(long userId);

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
	public java.util.List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end);

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
	public java.util.List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator);

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
	public java.util.List<CookiesConsentPreference> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException;

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator);

	/**
	 * Removes all the cookies consent preferences where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of cookies consent preferences where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching cookies consent preferences
	 */
	public int countByUserId(long userId);

	/**
	 * Returns all the cookies consent preferences where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching cookies consent preferences
	 */
	public java.util.List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate);

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
	public java.util.List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end);

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
	public java.util.List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator);

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
	public java.util.List<CookiesConsentPreference> findByExpirationDate(
		Date expirationDate, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cookies consent preference in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference findByExpirationDate_First(
			Date expirationDate,
			com.liferay.portal.kernel.util.OrderByComparator
				<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException;

	/**
	 * Returns the first cookies consent preference in the ordered set where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference fetchByExpirationDate_First(
		Date expirationDate,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator);

	/**
	 * Removes all the cookies consent preferences where expirationDate = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	public void removeByExpirationDate(Date expirationDate);

	/**
	 * Returns the number of cookies consent preferences where expirationDate = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching cookies consent preferences
	 */
	public int countByExpirationDate(Date expirationDate);

	/**
	 * Returns all the cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @return the matching cookies consent preferences
	 */
	public java.util.List<CookiesConsentPreference> findByU_D(
		long userId, String domain);

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
	public java.util.List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end);

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
	public java.util.List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator);

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
	public java.util.List<CookiesConsentPreference> findByU_D(
		long userId, String domain, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference findByU_D_First(
			long userId, String domain,
			com.liferay.portal.kernel.util.OrderByComparator
				<CookiesConsentPreference> orderByComparator)
		throws NoSuchCookiesConsentPreferenceException;

	/**
	 * Returns the first cookies consent preference in the ordered set where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference fetchByU_D_First(
		long userId, String domain,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator);

	/**
	 * Removes all the cookies consent preferences where userId = &#63; and domain = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 */
	public void removeByU_D(long userId, String domain);

	/**
	 * Returns the number of cookies consent preferences where userId = &#63; and domain = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @return the number of matching cookies consent preferences
	 */
	public int countByU_D(long userId, String domain);

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or throws a <code>NoSuchCookiesConsentPreferenceException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the matching cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference findByU_D_N(
			long userId, String domain, String name)
		throws NoSuchCookiesConsentPreferenceException;

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference fetchByU_D_N(
		long userId, String domain, String name);

	/**
	 * Returns the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cookies consent preference, or <code>null</code> if a matching cookies consent preference could not be found
	 */
	public CookiesConsentPreference fetchByU_D_N(
		long userId, String domain, String name, boolean useFinderCache);

	/**
	 * Removes the cookies consent preference where userId = &#63; and domain = &#63; and name = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the cookies consent preference that was removed
	 */
	public CookiesConsentPreference removeByU_D_N(
			long userId, String domain, String name)
		throws NoSuchCookiesConsentPreferenceException;

	/**
	 * Returns the number of cookies consent preferences where userId = &#63; and domain = &#63; and name = &#63;.
	 *
	 * @param userId the user ID
	 * @param domain the domain
	 * @param name the name
	 * @return the number of matching cookies consent preferences
	 */
	public int countByU_D_N(long userId, String domain, String name);

	/**
	 * Caches the cookies consent preference in the entity cache if it is enabled.
	 *
	 * @param cookiesConsentPreference the cookies consent preference
	 */
	public void cacheResult(CookiesConsentPreference cookiesConsentPreference);

	/**
	 * Caches the cookies consent preferences in the entity cache if it is enabled.
	 *
	 * @param cookiesConsentPreferences the cookies consent preferences
	 */
	public void cacheResult(
		java.util.List<CookiesConsentPreference> cookiesConsentPreferences);

	/**
	 * Creates a new cookies consent preference with the primary key. Does not add the cookies consent preference to the database.
	 *
	 * @param cookiesConsentPreferenceId the primary key for the new cookies consent preference
	 * @return the new cookies consent preference
	 */
	public CookiesConsentPreference create(long cookiesConsentPreferenceId);

	/**
	 * Removes the cookies consent preference with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference that was removed
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	public CookiesConsentPreference remove(long cookiesConsentPreferenceId)
		throws NoSuchCookiesConsentPreferenceException;

	public CookiesConsentPreference updateImpl(
		CookiesConsentPreference cookiesConsentPreference);

	/**
	 * Returns the cookies consent preference with the primary key or throws a <code>NoSuchCookiesConsentPreferenceException</code> if it could not be found.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference
	 * @throws NoSuchCookiesConsentPreferenceException if a cookies consent preference with the primary key could not be found
	 */
	public CookiesConsentPreference findByPrimaryKey(
			long cookiesConsentPreferenceId)
		throws NoSuchCookiesConsentPreferenceException;

	/**
	 * Returns the cookies consent preference with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param cookiesConsentPreferenceId the primary key of the cookies consent preference
	 * @return the cookies consent preference, or <code>null</code> if a cookies consent preference with the primary key could not be found
	 */
	public CookiesConsentPreference fetchByPrimaryKey(
		long cookiesConsentPreferenceId);

	/**
	 * Returns all the cookies consent preferences.
	 *
	 * @return the cookies consent preferences
	 */
	public java.util.List<CookiesConsentPreference> findAll();

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
	public java.util.List<CookiesConsentPreference> findAll(int start, int end);

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
	public java.util.List<CookiesConsentPreference> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator);

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
	public java.util.List<CookiesConsentPreference> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<CookiesConsentPreference> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the cookies consent preferences from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of cookies consent preferences.
	 *
	 * @return the number of cookies consent preferences
	 */
	public int countAll();

}
// LIFERAY-SERVICE-BUILDER-HASH:-1278218230