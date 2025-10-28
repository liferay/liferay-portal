/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.security.sso.openid.connect.persistence.exception.NoSuchUserException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the open ID connect user service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Arthur Chan
 * @see OpenIdConnectUserUtil
 * @generated
 */
@ProviderType
public interface OpenIdConnectUserPersistence
	extends BasePersistence<OpenIdConnectUser> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link OpenIdConnectUserUtil} to access the open ID connect user persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the matching open ID connect users
	 */
	public java.util.List<OpenIdConnectUser> findByC_U(
		long companyId, long userId);

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
	public java.util.List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end);

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
	public java.util.List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
			orderByComparator);

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
	public java.util.List<OpenIdConnectUser> findByC_U(
		long companyId, long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	public OpenIdConnectUser findByC_U_First(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
				orderByComparator)
		throws NoSuchUserException;

	/**
	 * Returns the first open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	public OpenIdConnectUser fetchByC_U_First(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
			orderByComparator);

	/**
	 * Returns the last open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	public OpenIdConnectUser findByC_U_Last(
			long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
				orderByComparator)
		throws NoSuchUserException;

	/**
	 * Returns the last open ID connect user in the ordered set where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	public OpenIdConnectUser fetchByC_U_Last(
		long companyId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
			orderByComparator);

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
	public OpenIdConnectUser[] findByC_U_PrevAndNext(
			long openIdConnectUserId, long companyId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
				orderByComparator)
		throws NoSuchUserException;

	/**
	 * Removes all the open ID connect users where companyId = &#63; and userId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 */
	public void removeByC_U(long companyId, long userId);

	/**
	 * Returns the number of open ID connect users where companyId = &#63; and userId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @return the number of matching open ID connect users
	 */
	public int countByC_U(long companyId, long userId);

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or throws a <code>NoSuchUserException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the matching open ID connect user
	 * @throws NoSuchUserException if a matching open ID connect user could not be found
	 */
	public OpenIdConnectUser findByC_I_S(
			long companyId, String issuer, String subject)
		throws NoSuchUserException;

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	public OpenIdConnectUser fetchByC_I_S(
		long companyId, String issuer, String subject);

	/**
	 * Returns the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching open ID connect user, or <code>null</code> if a matching open ID connect user could not be found
	 */
	public OpenIdConnectUser fetchByC_I_S(
		long companyId, String issuer, String subject, boolean useFinderCache);

	/**
	 * Removes the open ID connect user where companyId = &#63; and issuer = &#63; and subject = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the open ID connect user that was removed
	 */
	public OpenIdConnectUser removeByC_I_S(
			long companyId, String issuer, String subject)
		throws NoSuchUserException;

	/**
	 * Returns the number of open ID connect users where companyId = &#63; and issuer = &#63; and subject = &#63;.
	 *
	 * @param companyId the company ID
	 * @param issuer the issuer
	 * @param subject the subject
	 * @return the number of matching open ID connect users
	 */
	public int countByC_I_S(long companyId, String issuer, String subject);

	/**
	 * Caches the open ID connect user in the entity cache if it is enabled.
	 *
	 * @param openIdConnectUser the open ID connect user
	 */
	public void cacheResult(OpenIdConnectUser openIdConnectUser);

	/**
	 * Caches the open ID connect users in the entity cache if it is enabled.
	 *
	 * @param openIdConnectUsers the open ID connect users
	 */
	public void cacheResult(
		java.util.List<OpenIdConnectUser> openIdConnectUsers);

	/**
	 * Creates a new open ID connect user with the primary key. Does not add the open ID connect user to the database.
	 *
	 * @param openIdConnectUserId the primary key for the new open ID connect user
	 * @return the new open ID connect user
	 */
	public OpenIdConnectUser create(long openIdConnectUserId);

	/**
	 * Removes the open ID connect user with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user that was removed
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	public OpenIdConnectUser remove(long openIdConnectUserId)
		throws NoSuchUserException;

	public OpenIdConnectUser updateImpl(OpenIdConnectUser openIdConnectUser);

	/**
	 * Returns the open ID connect user with the primary key or throws a <code>NoSuchUserException</code> if it could not be found.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user
	 * @throws NoSuchUserException if a open ID connect user with the primary key could not be found
	 */
	public OpenIdConnectUser findByPrimaryKey(long openIdConnectUserId)
		throws NoSuchUserException;

	/**
	 * Returns the open ID connect user with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param openIdConnectUserId the primary key of the open ID connect user
	 * @return the open ID connect user, or <code>null</code> if a open ID connect user with the primary key could not be found
	 */
	public OpenIdConnectUser fetchByPrimaryKey(long openIdConnectUserId);

	/**
	 * Returns all the open ID connect users.
	 *
	 * @return the open ID connect users
	 */
	public java.util.List<OpenIdConnectUser> findAll();

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
	public java.util.List<OpenIdConnectUser> findAll(int start, int end);

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
	public java.util.List<OpenIdConnectUser> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
			orderByComparator);

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
	public java.util.List<OpenIdConnectUser> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OpenIdConnectUser>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the open ID connect users from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of open ID connect users.
	 *
	 * @return the number of open ID connect users
	 */
	public int countAll();

}