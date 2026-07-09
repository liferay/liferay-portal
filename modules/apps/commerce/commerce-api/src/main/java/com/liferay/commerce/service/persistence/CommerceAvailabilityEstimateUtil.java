/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence;

import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the commerce availability estimate service. This utility wraps <code>com.liferay.commerce.service.persistence.impl.CommerceAvailabilityEstimatePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Alessio Antonio Rendina
 * @see CommerceAvailabilityEstimatePersistence
 * @generated
 */
public class CommerceAvailabilityEstimateUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CommerceAvailabilityEstimate> commerceAvailabilityEstimates) {

		getPersistence().cacheResult(commerceAvailabilityEstimates);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		getPersistence().cacheResult(commerceAvailabilityEstimate);
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
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		getPersistence().clearCache(commerceAvailabilityEstimate);
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
	public static Map<Serializable, CommerceAvailabilityEstimate>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CommerceAvailabilityEstimate> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CommerceAvailabilityEstimate> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CommerceAvailabilityEstimate> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CommerceAvailabilityEstimate update(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		return getPersistence().update(commerceAvailabilityEstimate);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CommerceAvailabilityEstimate update(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate,
		ServiceContext serviceContext) {

		return getPersistence().update(
			commerceAvailabilityEstimate, serviceContext);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce availability estimate in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate findByUuid_First(
			String uuid,
			OrderByComparator<CommerceAvailabilityEstimate> orderByComparator)
		throws com.liferay.commerce.exception.
			NoSuchAvailabilityEstimateException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first commerce availability estimate in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate fetchByUuid_First(
		String uuid,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates that the user has permissions to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().filterFindByUuid(
			uuid, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce availability estimates where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of commerce availability estimates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce availability estimates
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the number of commerce availability estimates that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching commerce availability estimates that the user has permission to view
	 */
	public static int filterCountByUuid(String uuid) {
		return getPersistence().filterCountByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce availability estimate in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CommerceAvailabilityEstimate> orderByComparator)
		throws com.liferay.commerce.exception.
			NoSuchAvailabilityEstimateException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first commerce availability estimate in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates that the user has permissions to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().filterFindByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce availability estimates where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of commerce availability estimates that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates that the user has permission to view
	 */
	public static int filterCountByUuid_C(String uuid, long companyId) {
		return getPersistence().filterCountByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first commerce availability estimate in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate findByCompanyId_First(
			long companyId,
			OrderByComparator<CommerceAvailabilityEstimate> orderByComparator)
		throws com.liferay.commerce.exception.
			NoSuchAvailabilityEstimateException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first commerce availability estimate in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates that the user has permissions to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().filterFindByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Removes all the commerce availability estimates where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of commerce availability estimates where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the number of commerce availability estimates that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates that the user has permission to view
	 */
	public static int filterCountByCompanyId(long companyId) {
		return getPersistence().filterCountByCompanyId(companyId);
	}

	/**
	 * Returns the commerce availability estimate where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchAvailabilityEstimateException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.
			NoSuchAvailabilityEstimateException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the commerce availability estimate where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the commerce availability estimate where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the commerce availability estimate that was removed
	 */
	public static CommerceAvailabilityEstimate removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.exception.
			NoSuchAvailabilityEstimateException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of commerce availability estimates where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching commerce availability estimates
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new commerce availability estimate with the primary key. Does not add the commerce availability estimate to the database.
	 *
	 * @param commerceAvailabilityEstimateId the primary key for the new commerce availability estimate
	 * @return the new commerce availability estimate
	 */
	public static CommerceAvailabilityEstimate create(
		long commerceAvailabilityEstimateId) {

		return getPersistence().create(commerceAvailabilityEstimateId);
	}

	/**
	 * Removes the commerce availability estimate with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate that was removed
	 * @throws NoSuchAvailabilityEstimateException if a commerce availability estimate with the primary key could not be found
	 */
	public static CommerceAvailabilityEstimate remove(
			long commerceAvailabilityEstimateId)
		throws com.liferay.commerce.exception.
			NoSuchAvailabilityEstimateException {

		return getPersistence().remove(commerceAvailabilityEstimateId);
	}

	public static CommerceAvailabilityEstimate updateImpl(
		CommerceAvailabilityEstimate commerceAvailabilityEstimate) {

		return getPersistence().updateImpl(commerceAvailabilityEstimate);
	}

	/**
	 * Returns the commerce availability estimate with the primary key or throws a <code>NoSuchAvailabilityEstimateException</code> if it could not be found.
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate
	 * @throws NoSuchAvailabilityEstimateException if a commerce availability estimate with the primary key could not be found
	 */
	public static CommerceAvailabilityEstimate findByPrimaryKey(
			long commerceAvailabilityEstimateId)
		throws com.liferay.commerce.exception.
			NoSuchAvailabilityEstimateException {

		return getPersistence().findByPrimaryKey(
			commerceAvailabilityEstimateId);
	}

	/**
	 * Returns the commerce availability estimate with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param commerceAvailabilityEstimateId the primary key of the commerce availability estimate
	 * @return the commerce availability estimate, or <code>null</code> if a commerce availability estimate with the primary key could not be found
	 */
	public static CommerceAvailabilityEstimate fetchByPrimaryKey(
		long commerceAvailabilityEstimateId) {

		return getPersistence().fetchByPrimaryKey(
			commerceAvailabilityEstimateId);
	}

	/**
	 * Returns the commerce availability estimate where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching commerce availability estimate, or <code>null</code> if a matching commerce availability estimate could not be found
	 */
	public static CommerceAvailabilityEstimate fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the commerce availability estimates where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the commerce availability estimates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce availability estimates that the user has permission to view where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByUuid(
		String uuid) {

		return getPersistence().filterFindByUuid(uuid);
	}

	/**
	 * Returns a range of all the commerce availability estimates that the user has permission to view where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByUuid(
		String uuid, int start, int end) {

		return getPersistence().filterFindByUuid(uuid, start, end);
	}

	/**
	 * Returns all the commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce availability estimates that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByUuid_C(
		String uuid, long companyId) {

		return getPersistence().filterFindByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the commerce availability estimates that the user has permission to view where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().filterFindByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns all the commerce availability estimates where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByCompanyId(
		long companyId) {

		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the commerce availability estimates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the commerce availability estimates where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching commerce availability estimates
	 */
	public static List<CommerceAvailabilityEstimate> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CommerceAvailabilityEstimate> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the commerce availability estimates that the user has permission to view where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByCompanyId(
		long companyId) {

		return getPersistence().filterFindByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the commerce availability estimates that the user has permission to view where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.model.impl.CommerceAvailabilityEstimateModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of commerce availability estimates
	 * @param end the upper bound of the range of commerce availability estimates (not inclusive)
	 * @return the range of matching commerce availability estimates that the user has permission to view
	 */
	public static List<CommerceAvailabilityEstimate> filterFindByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().filterFindByCompanyId(companyId, start, end);
	}

	public static CommerceAvailabilityEstimatePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CommerceAvailabilityEstimatePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CommerceAvailabilityEstimatePersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1030639806