/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence;

import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientASLocalMetadataException;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the o auth client as local metadata service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientASLocalMetadataUtil
 * @generated
 */
@ProviderType
public interface OAuthClientASLocalMetadataPersistence
	extends BasePersistence<OAuthClientASLocalMetadata> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link OAuthClientASLocalMetadataUtil} to access the o auth client as local metadata persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId);

	/**
	 * Returns a range of all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of matching o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns the last o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata findByCompanyId_Last(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the last o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata fetchByCompanyId_Last(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public OAuthClientASLocalMetadata[] findByCompanyId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns all the o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client as local metadatas that the user has permission to view
	 */
	public java.util.List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId);

	/**
	 * Returns a range of all the o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of matching o auth client as local metadatas that the user has permission to view
	 */
	public java.util.List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	public java.util.List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set of o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public OAuthClientASLocalMetadata[] filterFindByCompanyId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Removes all the o auth client as local metadatas where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of o auth client as local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	public int filterCountByCompanyId(long companyId);

	/**
	 * Returns all the o auth client as local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findByUserId(long userId);

	/**
	 * Returns a range of all the o auth client as local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of matching o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns an ordered range of all the o auth client as local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the first o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns the last o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata findByUserId_Last(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the last o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata fetchByUserId_Last(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public OAuthClientASLocalMetadata[] findByUserId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns all the o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client as local metadatas that the user has permission to view
	 */
	public java.util.List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId);

	/**
	 * Returns a range of all the o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of matching o auth client as local metadatas that the user has permission to view
	 */
	public java.util.List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client as local metadatas that the user has permissions to view where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client as local metadatas that the user has permission to view
	 */
	public java.util.List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set of o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public OAuthClientASLocalMetadata[] filterFindByUserId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientASLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Removes all the o auth client as local metadatas where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of o auth client as local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client as local metadatas
	 */
	public int countByUserId(long userId);

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	public int filterCountByUserId(long userId);

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOAS = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata findByLocalWellKnownURIOAS(
			String localWellKnownURIOAS)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOAS = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata fetchByLocalWellKnownURIOAS(
		String localWellKnownURIOAS);

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOAS = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata fetchByLocalWellKnownURIOAS(
		String localWellKnownURIOAS, boolean useFinderCache);

	/**
	 * Removes the o auth client as local metadata where localWellKnownURIOAS = &#63; from the database.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @return the o auth client as local metadata that was removed
	 */
	public OAuthClientASLocalMetadata removeByLocalWellKnownURIOAS(
			String localWellKnownURIOAS)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the number of o auth client as local metadatas where localWellKnownURIOAS = &#63;.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @return the number of matching o auth client as local metadatas
	 */
	public int countByLocalWellKnownURIOAS(String localWellKnownURIOAS);

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOIC = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata findByLocalWellKnownURIOIC(
			String localWellKnownURIOIC)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOIC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata fetchByLocalWellKnownURIOIC(
		String localWellKnownURIOIC);

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOIC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public OAuthClientASLocalMetadata fetchByLocalWellKnownURIOIC(
		String localWellKnownURIOIC, boolean useFinderCache);

	/**
	 * Removes the o auth client as local metadata where localWellKnownURIOIC = &#63; from the database.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @return the o auth client as local metadata that was removed
	 */
	public OAuthClientASLocalMetadata removeByLocalWellKnownURIOIC(
			String localWellKnownURIOIC)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the number of o auth client as local metadatas where localWellKnownURIOIC = &#63;.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @return the number of matching o auth client as local metadatas
	 */
	public int countByLocalWellKnownURIOIC(String localWellKnownURIOIC);

	/**
	 * Caches the o auth client as local metadata in the entity cache if it is enabled.
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 */
	public void cacheResult(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata);

	/**
	 * Caches the o auth client as local metadatas in the entity cache if it is enabled.
	 *
	 * @param oAuthClientASLocalMetadatas the o auth client as local metadatas
	 */
	public void cacheResult(
		java.util.List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas);

	/**
	 * Creates a new o auth client as local metadata with the primary key. Does not add the o auth client as local metadata to the database.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key for the new o auth client as local metadata
	 * @return the new o auth client as local metadata
	 */
	public OAuthClientASLocalMetadata create(long oAuthClientASLocalMetadataId);

	/**
	 * Removes the o auth client as local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public OAuthClientASLocalMetadata remove(long oAuthClientASLocalMetadataId)
		throws NoSuchOAuthClientASLocalMetadataException;

	public OAuthClientASLocalMetadata updateImpl(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata);

	/**
	 * Returns the o auth client as local metadata with the primary key or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public OAuthClientASLocalMetadata findByPrimaryKey(
			long oAuthClientASLocalMetadataId)
		throws NoSuchOAuthClientASLocalMetadataException;

	/**
	 * Returns the o auth client as local metadata with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata, or <code>null</code> if a o auth client as local metadata with the primary key could not be found
	 */
	public OAuthClientASLocalMetadata fetchByPrimaryKey(
		long oAuthClientASLocalMetadataId);

	/**
	 * Returns all the o auth client as local metadatas.
	 *
	 * @return the o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findAll();

	/**
	 * Returns a range of all the o auth client as local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @return the range of o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findAll(
		int start, int end);

	/**
	 * Returns an ordered range of all the o auth client as local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator);

	/**
	 * Returns an ordered range of all the o auth client as local metadatas.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>OAuthClientASLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of o auth client as local metadatas
	 * @param end the upper bound of the range of o auth client as local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of o auth client as local metadatas
	 */
	public java.util.List<OAuthClientASLocalMetadata> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the o auth client as local metadatas from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of o auth client as local metadatas.
	 *
	 * @return the number of o auth client as local metadatas
	 */
	public int countAll();

}