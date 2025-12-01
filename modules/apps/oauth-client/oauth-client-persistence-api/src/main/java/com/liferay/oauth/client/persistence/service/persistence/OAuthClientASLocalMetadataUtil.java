/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence;

import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the o auth client as local metadata service. This utility wraps <code>com.liferay.oauth.client.persistence.service.persistence.impl.OAuthClientASLocalMetadataPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientASLocalMetadataPersistence
 * @generated
 */
public class OAuthClientASLocalMetadataUtil {

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
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		getPersistence().clearCache(oAuthClientASLocalMetadata);
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
	public static Map<Serializable, OAuthClientASLocalMetadata>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<OAuthClientASLocalMetadata> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<OAuthClientASLocalMetadata> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<OAuthClientASLocalMetadata> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static OAuthClientASLocalMetadata update(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		return getPersistence().update(oAuthClientASLocalMetadata);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static OAuthClientASLocalMetadata update(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata,
		ServiceContext serviceContext) {

		return getPersistence().update(
			oAuthClientASLocalMetadata, serviceContext);
	}

	/**
	 * Returns all the o auth client as local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client as local metadatas
	 */
	public static List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId) {

		return getPersistence().findByCompanyId(companyId);
	}

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
	public static List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

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
	public static List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

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
	public static List<OAuthClientASLocalMetadata> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata findByCompanyId_First(
			long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByCompanyId_First(
		long companyId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata findByCompanyId_Last(
			long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the last o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByCompanyId_Last(
		long companyId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().fetchByCompanyId_Last(
			companyId, orderByComparator);
	}

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set where companyId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public static OAuthClientASLocalMetadata[] findByCompanyId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long companyId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByCompanyId_PrevAndNext(
			oAuthClientASLocalMetadataId, companyId, orderByComparator);
	}

	/**
	 * Returns all the o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching o auth client as local metadatas that the user has permission to view
	 */
	public static List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId) {

		return getPersistence().filterFindByCompanyId(companyId);
	}

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
	public static List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().filterFindByCompanyId(companyId, start, end);
	}

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
	public static List<OAuthClientASLocalMetadata> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().filterFindByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set of o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public static OAuthClientASLocalMetadata[]
			filterFindByCompanyId_PrevAndNext(
				long oAuthClientASLocalMetadataId, long companyId,
				OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().filterFindByCompanyId_PrevAndNext(
			oAuthClientASLocalMetadataId, companyId, orderByComparator);
	}

	/**
	 * Removes all the o auth client as local metadatas where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of o auth client as local metadatas where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	public static int filterCountByCompanyId(long companyId) {
		return getPersistence().filterCountByCompanyId(companyId);
	}

	/**
	 * Returns all the o auth client as local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client as local metadatas
	 */
	public static List<OAuthClientASLocalMetadata> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

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
	public static List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

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
	public static List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

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
	public static List<OAuthClientASLocalMetadata> findByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata findByUserId_First(
			long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByUserId_First(
		long userId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the last o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata findByUserId_Last(
			long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByUserId_Last(userId, orderByComparator);
	}

	/**
	 * Returns the last o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByUserId_Last(
		long userId,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().fetchByUserId_Last(userId, orderByComparator);
	}

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set where userId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public static OAuthClientASLocalMetadata[] findByUserId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByUserId_PrevAndNext(
			oAuthClientASLocalMetadataId, userId, orderByComparator);
	}

	/**
	 * Returns all the o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching o auth client as local metadatas that the user has permission to view
	 */
	public static List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId) {

		return getPersistence().filterFindByUserId(userId);
	}

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
	public static List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId, int start, int end) {

		return getPersistence().filterFindByUserId(userId, start, end);
	}

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
	public static List<OAuthClientASLocalMetadata> filterFindByUserId(
		long userId, int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().filterFindByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns the o auth client as local metadatas before and after the current o auth client as local metadata in the ordered set of o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the current o auth client as local metadata
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public static OAuthClientASLocalMetadata[] filterFindByUserId_PrevAndNext(
			long oAuthClientASLocalMetadataId, long userId,
			OrderByComparator<OAuthClientASLocalMetadata> orderByComparator)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().filterFindByUserId_PrevAndNext(
			oAuthClientASLocalMetadataId, userId, orderByComparator);
	}

	/**
	 * Removes all the o auth client as local metadatas where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of o auth client as local metadatas where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client as local metadatas
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns the number of o auth client as local metadatas that the user has permission to view where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching o auth client as local metadatas that the user has permission to view
	 */
	public static int filterCountByUserId(long userId) {
		return getPersistence().filterCountByUserId(userId);
	}

	/**
	 * Returns the o auth client as local metadata where issuer = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param issuer the issuer
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata findByIssuer(String issuer)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByIssuer(issuer);
	}

	/**
	 * Returns the o auth client as local metadata where issuer = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param issuer the issuer
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByIssuer(String issuer) {
		return getPersistence().fetchByIssuer(issuer);
	}

	/**
	 * Returns the o auth client as local metadata where issuer = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param issuer the issuer
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByIssuer(
		String issuer, boolean useFinderCache) {

		return getPersistence().fetchByIssuer(issuer, useFinderCache);
	}

	/**
	 * Removes the o auth client as local metadata where issuer = &#63; from the database.
	 *
	 * @param issuer the issuer
	 * @return the o auth client as local metadata that was removed
	 */
	public static OAuthClientASLocalMetadata removeByIssuer(String issuer)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().removeByIssuer(issuer);
	}

	/**
	 * Returns the number of o auth client as local metadatas where issuer = &#63;.
	 *
	 * @param issuer the issuer
	 * @return the number of matching o auth client as local metadatas
	 */
	public static int countByIssuer(String issuer) {
		return getPersistence().countByIssuer(issuer);
	}

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOAS = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata findByLocalWellKnownURIOAS(
			String localWellKnownURIOAS)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByLocalWellKnownURIOAS(
			localWellKnownURIOAS);
	}

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOAS = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByLocalWellKnownURIOAS(
		String localWellKnownURIOAS) {

		return getPersistence().fetchByLocalWellKnownURIOAS(
			localWellKnownURIOAS);
	}

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOAS = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByLocalWellKnownURIOAS(
		String localWellKnownURIOAS, boolean useFinderCache) {

		return getPersistence().fetchByLocalWellKnownURIOAS(
			localWellKnownURIOAS, useFinderCache);
	}

	/**
	 * Removes the o auth client as local metadata where localWellKnownURIOAS = &#63; from the database.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @return the o auth client as local metadata that was removed
	 */
	public static OAuthClientASLocalMetadata removeByLocalWellKnownURIOAS(
			String localWellKnownURIOAS)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().removeByLocalWellKnownURIOAS(
			localWellKnownURIOAS);
	}

	/**
	 * Returns the number of o auth client as local metadatas where localWellKnownURIOAS = &#63;.
	 *
	 * @param localWellKnownURIOAS the local well known urioas
	 * @return the number of matching o auth client as local metadatas
	 */
	public static int countByLocalWellKnownURIOAS(String localWellKnownURIOAS) {
		return getPersistence().countByLocalWellKnownURIOAS(
			localWellKnownURIOAS);
	}

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOIC = &#63; or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @return the matching o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata findByLocalWellKnownURIOIC(
			String localWellKnownURIOIC)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByLocalWellKnownURIOIC(
			localWellKnownURIOIC);
	}

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOIC = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByLocalWellKnownURIOIC(
		String localWellKnownURIOIC) {

		return getPersistence().fetchByLocalWellKnownURIOIC(
			localWellKnownURIOIC);
	}

	/**
	 * Returns the o auth client as local metadata where localWellKnownURIOIC = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching o auth client as local metadata, or <code>null</code> if a matching o auth client as local metadata could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByLocalWellKnownURIOIC(
		String localWellKnownURIOIC, boolean useFinderCache) {

		return getPersistence().fetchByLocalWellKnownURIOIC(
			localWellKnownURIOIC, useFinderCache);
	}

	/**
	 * Removes the o auth client as local metadata where localWellKnownURIOIC = &#63; from the database.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @return the o auth client as local metadata that was removed
	 */
	public static OAuthClientASLocalMetadata removeByLocalWellKnownURIOIC(
			String localWellKnownURIOIC)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().removeByLocalWellKnownURIOIC(
			localWellKnownURIOIC);
	}

	/**
	 * Returns the number of o auth client as local metadatas where localWellKnownURIOIC = &#63;.
	 *
	 * @param localWellKnownURIOIC the local well known urioic
	 * @return the number of matching o auth client as local metadatas
	 */
	public static int countByLocalWellKnownURIOIC(String localWellKnownURIOIC) {
		return getPersistence().countByLocalWellKnownURIOIC(
			localWellKnownURIOIC);
	}

	/**
	 * Caches the o auth client as local metadata in the entity cache if it is enabled.
	 *
	 * @param oAuthClientASLocalMetadata the o auth client as local metadata
	 */
	public static void cacheResult(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		getPersistence().cacheResult(oAuthClientASLocalMetadata);
	}

	/**
	 * Caches the o auth client as local metadatas in the entity cache if it is enabled.
	 *
	 * @param oAuthClientASLocalMetadatas the o auth client as local metadatas
	 */
	public static void cacheResult(
		List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas) {

		getPersistence().cacheResult(oAuthClientASLocalMetadatas);
	}

	/**
	 * Creates a new o auth client as local metadata with the primary key. Does not add the o auth client as local metadata to the database.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key for the new o auth client as local metadata
	 * @return the new o auth client as local metadata
	 */
	public static OAuthClientASLocalMetadata create(
		long oAuthClientASLocalMetadataId) {

		return getPersistence().create(oAuthClientASLocalMetadataId);
	}

	/**
	 * Removes the o auth client as local metadata with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata that was removed
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public static OAuthClientASLocalMetadata remove(
			long oAuthClientASLocalMetadataId)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().remove(oAuthClientASLocalMetadataId);
	}

	public static OAuthClientASLocalMetadata updateImpl(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		return getPersistence().updateImpl(oAuthClientASLocalMetadata);
	}

	/**
	 * Returns the o auth client as local metadata with the primary key or throws a <code>NoSuchOAuthClientASLocalMetadataException</code> if it could not be found.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata
	 * @throws NoSuchOAuthClientASLocalMetadataException if a o auth client as local metadata with the primary key could not be found
	 */
	public static OAuthClientASLocalMetadata findByPrimaryKey(
			long oAuthClientASLocalMetadataId)
		throws com.liferay.oauth.client.persistence.exception.
			NoSuchOAuthClientASLocalMetadataException {

		return getPersistence().findByPrimaryKey(oAuthClientASLocalMetadataId);
	}

	/**
	 * Returns the o auth client as local metadata with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param oAuthClientASLocalMetadataId the primary key of the o auth client as local metadata
	 * @return the o auth client as local metadata, or <code>null</code> if a o auth client as local metadata with the primary key could not be found
	 */
	public static OAuthClientASLocalMetadata fetchByPrimaryKey(
		long oAuthClientASLocalMetadataId) {

		return getPersistence().fetchByPrimaryKey(oAuthClientASLocalMetadataId);
	}

	/**
	 * Returns all the o auth client as local metadatas.
	 *
	 * @return the o auth client as local metadatas
	 */
	public static List<OAuthClientASLocalMetadata> findAll() {
		return getPersistence().findAll();
	}

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
	public static List<OAuthClientASLocalMetadata> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

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
	public static List<OAuthClientASLocalMetadata> findAll(
		int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

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
	public static List<OAuthClientASLocalMetadata> findAll(
		int start, int end,
		OrderByComparator<OAuthClientASLocalMetadata> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the o auth client as local metadatas from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of o auth client as local metadatas.
	 *
	 * @return the number of o auth client as local metadatas
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static OAuthClientASLocalMetadataPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		OAuthClientASLocalMetadataPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile OAuthClientASLocalMetadataPersistence _persistence;

}