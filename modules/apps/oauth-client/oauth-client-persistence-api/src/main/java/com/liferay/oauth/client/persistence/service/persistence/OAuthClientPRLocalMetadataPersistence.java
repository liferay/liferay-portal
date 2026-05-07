/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence;

import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientPRLocalMetadataException;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the o auth client pr local metadata service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataUtil
 * @generated
 */
@ProviderType
public interface OAuthClientPRLocalMetadataPersistence
	extends BasePersistence<OAuthClientPRLocalMetadata> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link OAuthClientPRLocalMetadataUtil} to access the o auth client pr local metadata persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUuid(String uuid);

	/**
	 * Returns a range of all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Removes all the o auth client pr local metadatas where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of o auth client pr local metadatas where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching o auth client pr local metadatas
	 */
	public int countByUuid(String uuid);

	/**
	 * Returns all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId);

	/**
	 * Returns a range of all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Removes all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	public int countByUuid_C(String uuid, long companyId);

	/**
	 * Returns all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId);

	/**
	 * Returns a range of all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata findByCompanyId_First(
			long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByCompanyId_First(
		long companyId,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Removes all the o auth client pr local metadatas where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public void removeByCompanyId(long companyId);

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	public int countByCompanyId(long companyId);

	/**
	 * Returns all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUserId(long userId);

	/**
	 * Returns a range of all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Removes all the o auth client pr local metadatas where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of o auth client pr local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	public int countByUserId(long userId);

	/**
	 * Returns all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @return the matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled);

	/**
	 * Returns a range of all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @return the range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Returns an ordered range of all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.oauth.client.persistence.model.impl.OAuthClientPRLocalMetadataModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param start the lower bound of the range of o auth client pr local metadatas
	 * @param end the upper bound of the range of o auth client pr local metadatas (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching o auth client pr local metadatas
	 */
	public java.util.List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata findByC_L_First(
			long companyId, boolean localWellKnownEnabled,
			com.liferay.portal.kernel.util.OrderByComparator
				<OAuthClientPRLocalMetadata> orderByComparator)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByC_L_First(
		long companyId, boolean localWellKnownEnabled,
		com.liferay.portal.kernel.util.OrderByComparator
			<OAuthClientPRLocalMetadata> orderByComparator);

	/**
	 * Removes all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 */
	public void removeByC_L(long companyId, boolean localWellKnownEnabled);

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @return the number of matching o auth client pr local metadatas
	 */
	public int countByC_L(long companyId, boolean localWellKnownEnabled);

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata findByC_LWKURI(
			long companyId, String localWellKnownURI)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByC_LWKURI(
		long companyId, String localWellKnownURI);

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByC_LWKURI(
		long companyId, String localWellKnownURI, boolean useFinderCache);

	/**
	 * Removes the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the o auth client pr local metadata that was removed
	 */
	public OAuthClientPRLocalMetadata removeByC_LWKURI(
			long companyId, String localWellKnownURI)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and localWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the number of matching o auth client pr local metadatas
	 */
	public int countByC_LWKURI(long companyId, String localWellKnownURI);

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata findByC_R(long companyId, String resource)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByC_R(
		long companyId, String resource);

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByC_R(
		long companyId, String resource, boolean useFinderCache);

	/**
	 * Removes the o auth client pr local metadata where companyId = &#63; and resource = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the o auth client pr local metadata that was removed
	 */
	public OAuthClientPRLocalMetadata removeByC_R(
			long companyId, String resource)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and resource = &#63;.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the number of matching o auth client pr local metadatas
	 */
	public int countByC_R(long companyId, String resource);

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata findByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByERC_C(
		String externalReferenceCode, long companyId);

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache);

	/**
	 * Removes the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the o auth client pr local metadata that was removed
	 */
	public OAuthClientPRLocalMetadata removeByERC_C(
			String externalReferenceCode, long companyId)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the number of o auth client pr local metadatas where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	public int countByERC_C(String externalReferenceCode, long companyId);

	/**
	 * Creates a new o auth client pr local metadata with the primary key. Does not add the o auth client pr local metadata to the database.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key for the new o auth client pr local metadata
	 * @return the new o auth client pr local metadata
	 */
	public OAuthClientPRLocalMetadata create(long oAuthClientPRLocalMetadataId);

	/**
	 * Removes the o auth client pr local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata that was removed
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a o auth client pr local metadata with the primary key could not be found
	 */
	public OAuthClientPRLocalMetadata remove(long oAuthClientPRLocalMetadataId)
		throws NoSuchOAuthClientPRLocalMetadataException;

	public OAuthClientPRLocalMetadata updateImpl(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata);

	/**
	 * Returns the o auth client pr local metadata with the primary key or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a o auth client pr local metadata with the primary key could not be found
	 */
	public OAuthClientPRLocalMetadata findByPrimaryKey(
			long oAuthClientPRLocalMetadataId)
		throws NoSuchOAuthClientPRLocalMetadataException;

	/**
	 * Returns the o auth client pr local metadata with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata, or <code>null</code> if a o auth client pr local metadata with the primary key could not be found
	 */
	public OAuthClientPRLocalMetadata fetchByPrimaryKey(
		long oAuthClientPRLocalMetadataId);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1759327034