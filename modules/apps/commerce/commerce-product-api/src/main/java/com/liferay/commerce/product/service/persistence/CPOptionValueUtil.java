/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cp option value service. This utility wraps <code>com.liferay.commerce.product.service.persistence.impl.CPOptionValuePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPOptionValuePersistence
 * @generated
 */
public class CPOptionValueUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<CPOptionValue> cpOptionValues) {
		getPersistence().cacheResult(cpOptionValues);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(CPOptionValue cpOptionValue) {
		getPersistence().cacheResult(cpOptionValue);
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
	public static void clearCache(CPOptionValue cpOptionValue) {
		getPersistence().clearCache(cpOptionValue);
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
	public static Map<Serializable, CPOptionValue> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CPOptionValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CPOptionValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CPOptionValue> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CPOptionValue update(CPOptionValue cpOptionValue) {
		return getPersistence().update(cpOptionValue);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CPOptionValue update(
		CPOptionValue cpOptionValue, ServiceContext serviceContext) {

		return getPersistence().update(cpOptionValue, serviceContext);
	}

	/**
	 * Returns an ordered range of all the cp option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp option values
	 */
	public static List<CPOptionValue> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp option value in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public static CPOptionValue findByUuid_First(
			String uuid, OrderByComparator<CPOptionValue> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first cp option value in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public static CPOptionValue fetchByUuid_First(
		String uuid, OrderByComparator<CPOptionValue> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the cp option values where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of cp option values where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp option values
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp option values
	 */
	public static List<CPOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp option value in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public static CPOptionValue findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPOptionValue> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first cp option value in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public static CPOptionValue fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPOptionValue> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the cp option values where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp option values
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the cp option values where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp option values
	 */
	public static List<CPOptionValue> findByCPOptionId(
		long CPOptionId, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPOptionId(
			CPOptionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp option value in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public static CPOptionValue findByCPOptionId_First(
			long CPOptionId, OrderByComparator<CPOptionValue> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().findByCPOptionId_First(
			CPOptionId, orderByComparator);
	}

	/**
	 * Returns the first cp option value in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public static CPOptionValue fetchByCPOptionId_First(
		long CPOptionId, OrderByComparator<CPOptionValue> orderByComparator) {

		return getPersistence().fetchByCPOptionId_First(
			CPOptionId, orderByComparator);
	}

	/**
	 * Removes all the cp option values where CPOptionId = &#63; from the database.
	 *
	 * @param CPOptionId the cp option ID
	 */
	public static void removeByCPOptionId(long CPOptionId) {
		getPersistence().removeByCPOptionId(CPOptionId);
	}

	/**
	 * Returns the number of cp option values where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the number of matching cp option values
	 */
	public static int countByCPOptionId(long CPOptionId) {
		return getPersistence().countByCPOptionId(CPOptionId);
	}

	/**
	 * Returns the cp option value where CPOptionId = &#63; and key = &#63; or throws a <code>NoSuchCPOptionValueException</code> if it could not be found.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @return the matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public static CPOptionValue findByC_K(long CPOptionId, String key)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().findByC_K(CPOptionId, key);
	}

	/**
	 * Returns the cp option value where CPOptionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public static CPOptionValue fetchByC_K(
		long CPOptionId, String key, boolean useFinderCache) {

		return getPersistence().fetchByC_K(CPOptionId, key, useFinderCache);
	}

	/**
	 * Removes the cp option value where CPOptionId = &#63; and key = &#63; from the database.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @return the cp option value that was removed
	 */
	public static CPOptionValue removeByC_K(long CPOptionId, String key)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().removeByC_K(CPOptionId, key);
	}

	/**
	 * Returns the number of cp option values where CPOptionId = &#63; and key = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @return the number of matching cp option values
	 */
	public static int countByC_K(long CPOptionId, String key) {
		return getPersistence().countByC_K(CPOptionId, key);
	}

	/**
	 * Returns the cp option value where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPOptionValueException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp option value
	 * @throws NoSuchCPOptionValueException if a matching cp option value could not be found
	 */
	public static CPOptionValue findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp option value where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public static CPOptionValue fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the cp option value where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp option value that was removed
	 */
	public static CPOptionValue removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of cp option values where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp option values
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new cp option value with the primary key. Does not add the cp option value to the database.
	 *
	 * @param CPOptionValueId the primary key for the new cp option value
	 * @return the new cp option value
	 */
	public static CPOptionValue create(long CPOptionValueId) {
		return getPersistence().create(CPOptionValueId);
	}

	/**
	 * Removes the cp option value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value that was removed
	 * @throws NoSuchCPOptionValueException if a cp option value with the primary key could not be found
	 */
	public static CPOptionValue remove(long CPOptionValueId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().remove(CPOptionValueId);
	}

	public static CPOptionValue updateImpl(CPOptionValue cpOptionValue) {
		return getPersistence().updateImpl(cpOptionValue);
	}

	/**
	 * Returns the cp option value with the primary key or throws a <code>NoSuchCPOptionValueException</code> if it could not be found.
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value
	 * @throws NoSuchCPOptionValueException if a cp option value with the primary key could not be found
	 */
	public static CPOptionValue findByPrimaryKey(long CPOptionValueId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPOptionValueException {

		return getPersistence().findByPrimaryKey(CPOptionValueId);
	}

	/**
	 * Returns the cp option value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPOptionValueId the primary key of the cp option value
	 * @return the cp option value, or <code>null</code> if a cp option value with the primary key could not be found
	 */
	public static CPOptionValue fetchByPrimaryKey(long CPOptionValueId) {
		return getPersistence().fetchByPrimaryKey(CPOptionValueId);
	}

	/**
	 * Returns the cp option value where CPOptionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPOptionId the cp option ID
	 * @param key the key
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public static CPOptionValue fetchByC_K(long CPOptionId, String key) {
		return getPersistence().fetchByC_K(CPOptionId, key);
	}

	/**
	 * Returns the cp option value where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp option value, or <code>null</code> if a matching cp option value could not be found
	 */
	public static CPOptionValue fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the cp option values where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp option values
	 */
	public static List<CPOptionValue> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the cp option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @return the range of matching cp option values
	 */
	public static List<CPOptionValue> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cp option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp option values
	 */
	public static List<CPOptionValue> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp option values
	 */
	public static List<CPOptionValue> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @return the range of matching cp option values
	 */
	public static List<CPOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp option values
	 */
	public static List<CPOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp option values where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the matching cp option values
	 */
	public static List<CPOptionValue> findByCPOptionId(long CPOptionId) {
		return getPersistence().findByCPOptionId(CPOptionId);
	}

	/**
	 * Returns a range of all the cp option values where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @return the range of matching cp option values
	 */
	public static List<CPOptionValue> findByCPOptionId(
		long CPOptionId, int start, int end) {

		return getPersistence().findByCPOptionId(CPOptionId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp option values where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp option values
	 * @param end the upper bound of the range of cp option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp option values
	 */
	public static List<CPOptionValue> findByCPOptionId(
		long CPOptionId, int start, int end,
		OrderByComparator<CPOptionValue> orderByComparator) {

		return getPersistence().findByCPOptionId(
			CPOptionId, start, end, orderByComparator);
	}

	public static CPOptionValuePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CPOptionValuePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CPOptionValuePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:575727457