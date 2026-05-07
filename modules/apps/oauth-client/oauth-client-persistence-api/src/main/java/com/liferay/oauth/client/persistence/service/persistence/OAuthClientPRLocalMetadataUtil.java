/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence;

import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the o auth client pr local metadata service. This utility wraps <code>com.liferay.oauth.client.persistence.service.persistence.impl.OAuthClientPRLocalMetadataPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataPersistence
 * @generated
 */
public class OAuthClientPRLocalMetadataUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<OAuthClientPRLocalMetadata> oAuthClientPRLocalMetadatas) {

		getPersistence().cacheResult(oAuthClientPRLocalMetadatas);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		getPersistence().cacheResult(oAuthClientPRLocalMetadata);
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
	public static void clearCache(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		getPersistence().clearCache(oAuthClientPRLocalMetadata);
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
	public static Map<Serializable, OAuthClientPRLocalMetadata>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<OAuthClientPRLocalMetadata> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<OAuthClientPRLocalMetadata> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<OAuthClientPRLocalMetadata> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static OAuthClientPRLocalMetadata update(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		return getPersistence().update(oAuthClientPRLocalMetadata);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static OAuthClientPRLocalMetadata update(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata,
		ServiceContext serviceContext) {

		return getPersistence().update(
			oAuthClientPRLocalMetadata, serviceContext);
	}

	/**
	 * Returns all the o auth client pr local metadatas where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching o auth client pr local metadatas
	 */
	public static List<OAuthClientPRLocalMetadata> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata findByUuid_First(
			String uuid,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByUuid_First(
		String uuid,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching o auth client pr local metadatas
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadatas
	 */
	public static List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the o auth client pr local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadatas
	 */
	public static List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId) {

		return getPersistence().findByCompanyId(companyId);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata findByCompanyId_First(
			long companyId,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByCompanyId_First(
		long companyId,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns all the o auth client pr local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client pr local metadatas
	 */
	public static List<OAuthClientPRLocalMetadata> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata findByUserId_First(
			long userId,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByUserId_First(
		long userId,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @return the matching o auth client pr local metadatas
	 */
	public static List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled) {

		return getPersistence().findByC_L(companyId, localWellKnownEnabled);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end) {

		return getPersistence().findByC_L(
			companyId, localWellKnownEnabled, start, end);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().findByC_L(
			companyId, localWellKnownEnabled, start, end, orderByComparator);
	}

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
	public static List<OAuthClientPRLocalMetadata> findByC_L(
		long companyId, boolean localWellKnownEnabled, int start, int end,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_L(
			companyId, localWellKnownEnabled, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata findByC_L_First(
			long companyId, boolean localWellKnownEnabled,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByC_L_First(
			companyId, localWellKnownEnabled, orderByComparator);
	}

	/**
	 * Returns the first o auth client pr local metadata in the ordered set where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByC_L_First(
		long companyId, boolean localWellKnownEnabled,
		OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator) {

		return getPersistence().fetchByC_L_First(
			companyId, localWellKnownEnabled, orderByComparator);
	}

	/**
	 * Removes all the o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 */
	public static void removeByC_L(
		long companyId, boolean localWellKnownEnabled) {

		getPersistence().removeByC_L(companyId, localWellKnownEnabled);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and localWellKnownEnabled = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownEnabled the local well known enabled
	 * @return the number of matching o auth client pr local metadatas
	 */
	public static int countByC_L(
		long companyId, boolean localWellKnownEnabled) {

		return getPersistence().countByC_L(companyId, localWellKnownEnabled);
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata findByC_LWKURI(
			long companyId, String localWellKnownURI)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByC_LWKURI(companyId, localWellKnownURI);
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByC_LWKURI(
		long companyId, String localWellKnownURI) {

		return getPersistence().fetchByC_LWKURI(companyId, localWellKnownURI);
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByC_LWKURI(
		long companyId, String localWellKnownURI, boolean useFinderCache) {

		return getPersistence().fetchByC_LWKURI(
			companyId, localWellKnownURI, useFinderCache);
	}

	/**
	 * Removes the o auth client pr local metadata where companyId = &#63; and localWellKnownURI = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the o auth client pr local metadata that was removed
	 */
	public static OAuthClientPRLocalMetadata removeByC_LWKURI(
			long companyId, String localWellKnownURI)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().removeByC_LWKURI(companyId, localWellKnownURI);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and localWellKnownURI = &#63;.
	 *
	 * @param companyId the company ID
	 * @param localWellKnownURI the local well known uri
	 * @return the number of matching o auth client pr local metadatas
	 */
	public static int countByC_LWKURI(
		long companyId, String localWellKnownURI) {

		return getPersistence().countByC_LWKURI(companyId, localWellKnownURI);
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata findByC_R(
			long companyId, String resource)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByC_R(companyId, resource);
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByC_R(
		long companyId, String resource) {

		return getPersistence().fetchByC_R(companyId, resource);
	}

	/**
	 * Returns the o auth client pr local metadata where companyId = &#63; and resource = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByC_R(
		long companyId, String resource, boolean useFinderCache) {

		return getPersistence().fetchByC_R(companyId, resource, useFinderCache);
	}

	/**
	 * Removes the o auth client pr local metadata where companyId = &#63; and resource = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the o auth client pr local metadata that was removed
	 */
	public static OAuthClientPRLocalMetadata removeByC_R(
			long companyId, String resource)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().removeByC_R(companyId, resource);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where companyId = &#63; and resource = &#63;.
	 *
	 * @param companyId the company ID
	 * @param resource the resource
	 * @return the number of matching o auth client pr local metadatas
	 */
	public static int countByC_R(long companyId, String resource) {
		return getPersistence().countByC_R(companyId, resource);
	}

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client pr local metadata, or <code>null</code> if a matching o auth client pr local metadata could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the o auth client pr local metadata where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the o auth client pr local metadata that was removed
	 */
	public static OAuthClientPRLocalMetadata removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of o auth client pr local metadatas where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching o auth client pr local metadatas
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new o auth client pr local metadata with the primary key. Does not add the o auth client pr local metadata to the database.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key for the new o auth client pr local metadata
	 * @return the new o auth client pr local metadata
	 */
	public static OAuthClientPRLocalMetadata create(
		long oAuthClientPRLocalMetadataId) {

		return getPersistence().create(oAuthClientPRLocalMetadataId);
	}

	/**
	 * Removes the o auth client pr local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata that was removed
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a o auth client pr local metadata with the primary key could not be found
	 */
	public static OAuthClientPRLocalMetadata remove(
			long oAuthClientPRLocalMetadataId)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().remove(oAuthClientPRLocalMetadataId);
	}

	public static OAuthClientPRLocalMetadata updateImpl(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		return getPersistence().updateImpl(oAuthClientPRLocalMetadata);
	}

	/**
	 * Returns the o auth client pr local metadata with the primary key or throws a <code>NoSuchOAuthClientPRLocalMetadataException</code> if it could not be found.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata
	 * @throws NoSuchOAuthClientPRLocalMetadataException if a o auth client pr local metadata with the primary key could not be found
	 */
	public static OAuthClientPRLocalMetadata findByPrimaryKey(
			long oAuthClientPRLocalMetadataId)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientPRLocalMetadataException {

		return getPersistence().findByPrimaryKey(oAuthClientPRLocalMetadataId);
	}

	/**
	 * Returns the o auth client pr local metadata with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientPRLocalMetadataId the primary key of the o auth client pr local metadata
	 * @return the o auth client pr local metadata, or <code>null</code> if a o auth client pr local metadata with the primary key could not be found
	 */
	public static OAuthClientPRLocalMetadata fetchByPrimaryKey(
		long oAuthClientPRLocalMetadataId) {

		return getPersistence().fetchByPrimaryKey(oAuthClientPRLocalMetadataId);
	}

	public static OAuthClientPRLocalMetadataPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		OAuthClientPRLocalMetadataPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile OAuthClientPRLocalMetadataPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-851637142