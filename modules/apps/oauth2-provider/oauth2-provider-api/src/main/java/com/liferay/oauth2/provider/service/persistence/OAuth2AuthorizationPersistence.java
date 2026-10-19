/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.service.persistence;

import com.liferay.oauth2.provider.exception.NoSuchOAuth2AuthorizationException;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the o auth2 authorization service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuth2AuthorizationUtil
 * @generated
 */
@ProviderType
public interface OAuth2AuthorizationPersistence
	extends BasePersistence<OAuth2Authorization> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link OAuth2AuthorizationUtil} to access the o auth2 authorization persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns an ordered range of all the o auth2 authorizations where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public java.util.List<OAuth2Authorization> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException;

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator);

	/**
	 * Removes all the o auth2 authorizations where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of o auth2 authorizations where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth2 authorizations
	 */
	public int countByUserId(long userId);

	/**
	 * Returns an ordered range of all the o auth2 authorizations where oAuth2ApplicationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public java.util.List<OAuth2Authorization> findByOAuth2ApplicationId(
		long oAuth2ApplicationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth2 authorization in the ordered set where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization findByOAuth2ApplicationId_First(
			long oAuth2ApplicationId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException;

	/**
	 * Returns the first o auth2 authorization in the ordered set where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization fetchByOAuth2ApplicationId_First(
		long oAuth2ApplicationId,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator);

	/**
	 * Removes all the o auth2 authorizations where oAuth2ApplicationId = &#63; from the database.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 */
	public void removeByOAuth2ApplicationId(long oAuth2ApplicationId);

	/**
	 * Returns the number of o auth2 authorizations where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @return the number of matching o auth2 authorizations
	 */
	public int countByOAuth2ApplicationId(long oAuth2ApplicationId);

	/**
	 * Returns an ordered range of all the o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public java.util.List<OAuth2Authorization> findByC_ATCH(
		long companyId, long accessTokenContentHash, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth2 authorization in the ordered set where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization findByC_ATCH_First(
			long companyId, long accessTokenContentHash,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException;

	/**
	 * Returns the first o auth2 authorization in the ordered set where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization fetchByC_ATCH_First(
		long companyId, long accessTokenContentHash,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator);

	/**
	 * Removes all the o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 */
	public void removeByC_ATCH(long companyId, long accessTokenContentHash);

	/**
	 * Returns the number of o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @return the number of matching o auth2 authorizations
	 */
	public int countByC_ATCH(long companyId, long accessTokenContentHash);

	/**
	 * Returns an ordered range of all the o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public java.util.List<OAuth2Authorization> findByC_RTCH(
		long companyId, long refreshTokenContentHash, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth2 authorization in the ordered set where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization findByC_RTCH_First(
			long companyId, long refreshTokenContentHash,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException;

	/**
	 * Returns the first o auth2 authorization in the ordered set where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization fetchByC_RTCH_First(
		long companyId, long refreshTokenContentHash,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator);

	/**
	 * Removes all the o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 */
	public void removeByC_RTCH(long companyId, long refreshTokenContentHash);

	/**
	 * Returns the number of o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @return the number of matching o auth2 authorizations
	 */
	public int countByC_RTCH(long companyId, long refreshTokenContentHash);

	/**
	 * Returns an ordered range of all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public java.util.List<OAuth2Authorization> findByU_O(
		long userId, long oAuth2ApplicationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63; and oAuth2ApplicationId = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization findByU_O_First(
			long userId, long oAuth2ApplicationId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException;

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63; and oAuth2ApplicationId = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization fetchByU_O_First(
		long userId, long oAuth2ApplicationId,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator);

	/**
	 * Removes all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 */
	public void removeByU_O(long userId, long oAuth2ApplicationId);

	/**
	 * Returns the number of o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @return the number of matching o auth2 authorizations
	 */
	public int countByU_O(long userId, long oAuth2ApplicationId);

	/**
	 * Returns an ordered range of all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public java.util.List<OAuth2Authorization> findByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization findByU_O_R_First(
			long userId, long oAuth2ApplicationId, String rememberDeviceContent,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuth2Authorization> orderByComparator)
		throws NoSuchOAuth2AuthorizationException;

	/**
	 * Returns the first o auth2 authorization in the ordered set where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth2 authorization, or <code>null</code> if a matching o auth2 authorization could not be found
	 */
	public OAuth2Authorization fetchByU_O_R_First(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator);

	/**
	 * Removes all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 */
	public void removeByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent);

	/**
	 * Returns the number of o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @return the number of matching o auth2 authorizations
	 */
	public int countByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent);

	/**
	 * Creates a new o auth2 authorization with the primary key. Does not add the o auth2 authorization to the database.
	 *
	 * @param oAuth2AuthorizationId the primary key for the new o auth2 authorization
	 * @return the new o auth2 authorization
	 */
	public OAuth2Authorization create(long oAuth2AuthorizationId);

	/**
	 * Removes the o auth2 authorization with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization that was removed
	 * @throws NoSuchOAuth2AuthorizationException if a o auth2 authorization with the primary key could not be found
	 */
	public OAuth2Authorization remove(long oAuth2AuthorizationId)
		throws NoSuchOAuth2AuthorizationException;

	public OAuth2Authorization updateImpl(
		OAuth2Authorization oAuth2Authorization);

	/**
	 * Returns the o auth2 authorization with the primary key or throws a <code>NoSuchOAuth2AuthorizationException</code> if it could not be found.
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization
	 * @throws NoSuchOAuth2AuthorizationException if a o auth2 authorization with the primary key could not be found
	 */
	public OAuth2Authorization findByPrimaryKey(long oAuth2AuthorizationId)
		throws NoSuchOAuth2AuthorizationException;

	/**
	 * Returns the o auth2 authorization with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuth2AuthorizationId the primary key of the o auth2 authorization
	 * @return the o auth2 authorization, or <code>null</code> if a o auth2 authorization with the primary key could not be found
	 */
	public OAuth2Authorization fetchByPrimaryKey(long oAuth2AuthorizationId);

	/**
	 * Returns the primaryKeys of o auth2 scope grants associated with the o auth2 authorization.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @return long[] of the primaryKeys of o auth2 scope grants associated with the o auth2 authorization
	 */
	public long[] getOAuth2ScopeGrantPrimaryKeys(long pk);

	/**
	 * Returns all the o auth2 authorization associated with the o auth2 scope grant.
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @return the o auth2 authorizations associated with the o auth2 scope grant
	 */
	public java.util.List<OAuth2Authorization>
		getOAuth2ScopeGrantOAuth2Authorizations(long pk);

	/**
	 * Returns all the o auth2 authorization associated with the o auth2 scope grant.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param start the lower bound of the range of o auth2 scope grants
	 * @param end the upper bound of the range of o auth2 scope grants (not inclusive)
	 * @return the range of o auth2 authorizations associated with the o auth2 scope grant
	 */
	public java.util.List<OAuth2Authorization>
		getOAuth2ScopeGrantOAuth2Authorizations(long pk, int start, int end);

	/**
	 * Returns all the o auth2 authorization associated with the o auth2 scope grant.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param pk the primary key of the o auth2 scope grant
	 * @param start the lower bound of the range of o auth2 scope grants
	 * @param end the upper bound of the range of o auth2 scope grants (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of o auth2 authorizations associated with the o auth2 scope grant
	 */
	public java.util.List<OAuth2Authorization>
		getOAuth2ScopeGrantOAuth2Authorizations(
			long pk, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuth2Authorization> orderByComparator);

	/**
	 * Returns the number of o auth2 scope grants associated with the o auth2 authorization.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @return the number of o auth2 scope grants associated with the o auth2 authorization
	 */
	public int getOAuth2ScopeGrantsSize(long pk);

	/**
	 * Returns <code>true</code> if the o auth2 scope grant is associated with the o auth2 authorization.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPK the primary key of the o auth2 scope grant
	 * @return <code>true</code> if the o auth2 scope grant is associated with the o auth2 authorization; <code>false</code> otherwise
	 */
	public boolean containsOAuth2ScopeGrant(long pk, long oAuth2ScopeGrantPK);

	/**
	 * Returns <code>true</code> if the o auth2 authorization has any o auth2 scope grants associated with it.
	 *
	 * @param pk the primary key of the o auth2 authorization to check for associations with o auth2 scope grants
	 * @return <code>true</code> if the o auth2 authorization has any o auth2 scope grants associated with it; <code>false</code> otherwise
	 */
	public boolean containsOAuth2ScopeGrants(long pk);

	/**
	 * Adds an association between the o auth2 authorization and the o auth2 scope grant. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPK the primary key of the o auth2 scope grant
	 * @return <code>true</code> if an association between the o auth2 authorization and the o auth2 scope grant was added; <code>false</code> if they were already associated
	 */
	public boolean addOAuth2ScopeGrant(long pk, long oAuth2ScopeGrantPK);

	/**
	 * Adds an association between the o auth2 authorization and the o auth2 scope grant. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrant the o auth2 scope grant
	 * @return <code>true</code> if an association between the o auth2 authorization and the o auth2 scope grant was added; <code>false</code> if they were already associated
	 */
	public boolean addOAuth2ScopeGrant(
		long pk,
		com.liferay.oauth2.provider.model.OAuth2ScopeGrant oAuth2ScopeGrant);

	/**
	 * Adds an association between the o auth2 authorization and the o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPKs the primary keys of the o auth2 scope grants
	 * @return <code>true</code> if at least one association between the o auth2 authorization and the o auth2 scope grants was added; <code>false</code> if they were all already associated
	 */
	public boolean addOAuth2ScopeGrants(long pk, long[] oAuth2ScopeGrantPKs);

	/**
	 * Adds an association between the o auth2 authorization and the o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrants the o auth2 scope grants
	 * @return <code>true</code> if at least one association between the o auth2 authorization and the o auth2 scope grants was added; <code>false</code> if they were all already associated
	 */
	public boolean addOAuth2ScopeGrants(
		long pk,
		java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
			oAuth2ScopeGrants);

	/**
	 * Clears all associations between the o auth2 authorization and its o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization to clear the associated o auth2 scope grants from
	 */
	public void clearOAuth2ScopeGrants(long pk);

	/**
	 * Removes the association between the o auth2 authorization and the o auth2 scope grant. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPK the primary key of the o auth2 scope grant
	 */
	public void removeOAuth2ScopeGrant(long pk, long oAuth2ScopeGrantPK);

	/**
	 * Removes the association between the o auth2 authorization and the o auth2 scope grant. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrant the o auth2 scope grant
	 */
	public void removeOAuth2ScopeGrant(
		long pk,
		com.liferay.oauth2.provider.model.OAuth2ScopeGrant oAuth2ScopeGrant);

	/**
	 * Removes the association between the o auth2 authorization and the o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPKs the primary keys of the o auth2 scope grants
	 */
	public void removeOAuth2ScopeGrants(long pk, long[] oAuth2ScopeGrantPKs);

	/**
	 * Removes the association between the o auth2 authorization and the o auth2 scope grants. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrants the o auth2 scope grants
	 */
	public void removeOAuth2ScopeGrants(
		long pk,
		java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
			oAuth2ScopeGrants);

	/**
	 * Sets the o auth2 scope grants associated with the o auth2 authorization, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrantPKs the primary keys of the o auth2 scope grants to be associated with the o auth2 authorization
	 */
	public void setOAuth2ScopeGrants(long pk, long[] oAuth2ScopeGrantPKs);

	/**
	 * Sets the o auth2 scope grants associated with the o auth2 authorization, removing and adding associations as necessary. Also notifies the appropriate model listeners and clears the mapping table finder cache.
	 *
	 * @param pk the primary key of the o auth2 authorization
	 * @param oAuth2ScopeGrants the o auth2 scope grants to be associated with the o auth2 authorization
	 */
	public void setOAuth2ScopeGrants(
		long pk,
		java.util.List<com.liferay.oauth2.provider.model.OAuth2ScopeGrant>
			oAuth2ScopeGrants);

	/**
	 * Returns all the o auth2 authorizations where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByUserId(
		long userId) {

		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the o auth2 authorizations where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the o auth2 authorizations where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the o auth2 authorizations where oAuth2ApplicationId = &#63;.
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @return the matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization>
		findByOAuth2ApplicationId(long oAuth2ApplicationId) {

		return findByOAuth2ApplicationId(
			oAuth2ApplicationId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the o auth2 authorizations where oAuth2ApplicationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization>
		findByOAuth2ApplicationId(
			long oAuth2ApplicationId, int start, int end) {

		return findByOAuth2ApplicationId(
			oAuth2ApplicationId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the o auth2 authorizations where oAuth2ApplicationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization>
		findByOAuth2ApplicationId(
			long oAuth2ApplicationId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuth2Authorization> orderByComparator) {

		return findByOAuth2ApplicationId(
			oAuth2ApplicationId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @return the matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByC_ATCH(
		long companyId, long accessTokenContentHash) {

		return findByC_ATCH(
			companyId, accessTokenContentHash,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByC_ATCH(
		long companyId, long accessTokenContentHash, int start, int end) {

		return findByC_ATCH(
			companyId, accessTokenContentHash, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the o auth2 authorizations where companyId = &#63; and accessTokenContentHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param accessTokenContentHash the access token content hash
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByC_ATCH(
		long companyId, long accessTokenContentHash, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator) {

		return findByC_ATCH(
			companyId, accessTokenContentHash, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @return the matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByC_RTCH(
		long companyId, long refreshTokenContentHash) {

		return findByC_RTCH(
			companyId, refreshTokenContentHash,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByC_RTCH(
		long companyId, long refreshTokenContentHash, int start, int end) {

		return findByC_RTCH(
			companyId, refreshTokenContentHash, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the o auth2 authorizations where companyId = &#63; and refreshTokenContentHash = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param refreshTokenContentHash the refresh token content hash
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByC_RTCH(
		long companyId, long refreshTokenContentHash, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator) {

		return findByC_RTCH(
			companyId, refreshTokenContentHash, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @return the matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByU_O(
		long userId, long oAuth2ApplicationId) {

		return findByU_O(
			userId, oAuth2ApplicationId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByU_O(
		long userId, long oAuth2ApplicationId, int start, int end) {

		return findByU_O(userId, oAuth2ApplicationId, start, end, null, true);
	}

	/**
	 * Returns an ordered range of all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByU_O(
		long userId, long oAuth2ApplicationId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator) {

		return findByU_O(
			userId, oAuth2ApplicationId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @return the matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent) {

		return findByU_O_R(
			userId, oAuth2ApplicationId, rememberDeviceContent,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
	}

	/**
	 * Returns a range of all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @return the range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent,
		int start, int end) {

		return findByU_O_R(
			userId, oAuth2ApplicationId, rememberDeviceContent, start, end,
			null, true);
	}

	/**
	 * Returns an ordered range of all the o auth2 authorizations where userId = &#63; and oAuth2ApplicationId = &#63; and rememberDeviceContent = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth2.provider.model.impl.OAuth2AuthorizationModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param oAuth2ApplicationId the o auth2 application ID
	 * @param rememberDeviceContent the remember device content
	 * @param start the lower bound of the range of o auth2 authorizations
	 * @param end the upper bound of the range of o auth2 authorizations (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth2 authorizations
	 */
	public default java.util.List<OAuth2Authorization> findByU_O_R(
		long userId, long oAuth2ApplicationId, String rememberDeviceContent,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<OAuth2Authorization>
			orderByComparator) {

		return findByU_O_R(
			userId, oAuth2ApplicationId, rememberDeviceContent, start, end,
			orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:795489233