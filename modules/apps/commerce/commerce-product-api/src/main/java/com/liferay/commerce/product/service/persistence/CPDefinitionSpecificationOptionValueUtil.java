/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cp definition specification option value service. This utility wraps <code>com.liferay.commerce.product.service.persistence.impl.CPDefinitionSpecificationOptionValuePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinitionSpecificationOptionValuePersistence
 * @generated
 */
public class CPDefinitionSpecificationOptionValueUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CPDefinitionSpecificationOptionValue>
			cpDefinitionSpecificationOptionValues) {

		getPersistence().cacheResult(cpDefinitionSpecificationOptionValues);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue) {

		getPersistence().cacheResult(cpDefinitionSpecificationOptionValue);
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
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue) {

		getPersistence().clearCache(cpDefinitionSpecificationOptionValue);
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
	public static Map<Serializable, CPDefinitionSpecificationOptionValue>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findWithDynamicQuery(DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findWithDynamicQuery(DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findWithDynamicQuery(
			DynamicQuery dynamicQuery, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CPDefinitionSpecificationOptionValue update(
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue) {

		return getPersistence().update(cpDefinitionSpecificationOptionValue);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CPDefinitionSpecificationOptionValue update(
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue,
		ServiceContext serviceContext) {

		return getPersistence().update(
			cpDefinitionSpecificationOptionValue, serviceContext);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByUuid_First(
			String uuid,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the cp definition specification option values where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of cp definition specification option values where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the cp definition specification option value where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionSpecificationOptionValueException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp definition specification option value where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the cp definition specification option value where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition specification option value that was removed
	 */
	public static CPDefinitionSpecificationOptionValue removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of cp definition specification option values where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the cp definition specification option values where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of cp definition specification option values where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPDefinitionId(
			long CPDefinitionId, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
			findByCPDefinitionId_First(
				long CPDefinitionId,
				OrderByComparator<CPDefinitionSpecificationOptionValue>
					orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
		fetchByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getPersistence().fetchByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the cp definition specification option values where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	public static void removeByCPDefinitionId(long CPDefinitionId) {
		getPersistence().removeByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns the number of cp definition specification option values where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByCPDefinitionId(long CPDefinitionId) {
		return getPersistence().countByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPSpecificationOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPSpecificationOptionId(
			long CPSpecificationOptionId, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByCPSpecificationOptionId(
			CPSpecificationOptionId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPSpecificationOptionId = &#63;.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
			findByCPSpecificationOptionId_First(
				long CPSpecificationOptionId,
				OrderByComparator<CPDefinitionSpecificationOptionValue>
					orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByCPSpecificationOptionId_First(
			CPSpecificationOptionId, orderByComparator);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPSpecificationOptionId = &#63;.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
		fetchByCPSpecificationOptionId_First(
			long CPSpecificationOptionId,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getPersistence().fetchByCPSpecificationOptionId_First(
			CPSpecificationOptionId, orderByComparator);
	}

	/**
	 * Removes all the cp definition specification option values where CPSpecificationOptionId = &#63; from the database.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 */
	public static void removeByCPSpecificationOptionId(
		long CPSpecificationOptionId) {

		getPersistence().removeByCPSpecificationOptionId(
			CPSpecificationOptionId);
	}

	/**
	 * Returns the number of cp definition specification option values where CPSpecificationOptionId = &#63;.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByCPSpecificationOptionId(
		long CPSpecificationOptionId) {

		return getPersistence().countByCPSpecificationOptionId(
			CPSpecificationOptionId);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPOptionCategoryId(
			long CPOptionCategoryId, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByCPOptionCategoryId(
			CPOptionCategoryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
			findByCPOptionCategoryId_First(
				long CPOptionCategoryId,
				OrderByComparator<CPDefinitionSpecificationOptionValue>
					orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByCPOptionCategoryId_First(
			CPOptionCategoryId, orderByComparator);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue
		fetchByCPOptionCategoryId_First(
			long CPOptionCategoryId,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getPersistence().fetchByCPOptionCategoryId_First(
			CPOptionCategoryId, orderByComparator);
	}

	/**
	 * Removes all the cp definition specification option values where CPOptionCategoryId = &#63; from the database.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 */
	public static void removeByCPOptionCategoryId(long CPOptionCategoryId) {
		getPersistence().removeByCPOptionCategoryId(CPOptionCategoryId);
	}

	/**
	 * Returns the number of cp definition specification option values where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByCPOptionCategoryId(long CPOptionCategoryId) {
		return getPersistence().countByCPOptionCategoryId(CPOptionCategoryId);
	}

	/**
	 * Returns the cp definition specification option value where CPDefinitionSpecificationOptionValueId = &#63; and CPDefinitionId = &#63; or throws a <code>NoSuchCPDefinitionSpecificationOptionValueException</code> if it could not be found.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the cp definition specification option value ID
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByC_CSOVI(
			long CPDefinitionSpecificationOptionValueId, long CPDefinitionId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByC_CSOVI(
			CPDefinitionSpecificationOptionValueId, CPDefinitionId);
	}

	/**
	 * Returns the cp definition specification option value where CPDefinitionSpecificationOptionValueId = &#63; and CPDefinitionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the cp definition specification option value ID
	 * @param CPDefinitionId the cp definition ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByC_CSOVI(
		long CPDefinitionSpecificationOptionValueId, long CPDefinitionId,
		boolean useFinderCache) {

		return getPersistence().fetchByC_CSOVI(
			CPDefinitionSpecificationOptionValueId, CPDefinitionId,
			useFinderCache);
	}

	/**
	 * Removes the cp definition specification option value where CPDefinitionSpecificationOptionValueId = &#63; and CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the cp definition specification option value ID
	 * @param CPDefinitionId the cp definition ID
	 * @return the cp definition specification option value that was removed
	 */
	public static CPDefinitionSpecificationOptionValue removeByC_CSOVI(
			long CPDefinitionSpecificationOptionValueId, long CPDefinitionId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().removeByC_CSOVI(
			CPDefinitionSpecificationOptionValueId, CPDefinitionId);
	}

	/**
	 * Returns the number of cp definition specification option values where CPDefinitionSpecificationOptionValueId = &#63; and CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the cp definition specification option value ID
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByC_CSOVI(
		long CPDefinitionSpecificationOptionValueId, long CPDefinitionId) {

		return getPersistence().countByC_CSOVI(
			CPDefinitionSpecificationOptionValueId, CPDefinitionId);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPDefinitionId = &#63; and CPSpecificationOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByC_CSO(
		long CPDefinitionId, long CPSpecificationOptionId, int start, int end,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_CSO(
			CPDefinitionId, CPSpecificationOptionId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPDefinitionId = &#63; and CPSpecificationOptionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByC_CSO_First(
			long CPDefinitionId, long CPSpecificationOptionId,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByC_CSO_First(
			CPDefinitionId, CPSpecificationOptionId, orderByComparator);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPDefinitionId = &#63; and CPSpecificationOptionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByC_CSO_First(
		long CPDefinitionId, long CPSpecificationOptionId,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator) {

		return getPersistence().fetchByC_CSO_First(
			CPDefinitionId, CPSpecificationOptionId, orderByComparator);
	}

	/**
	 * Removes all the cp definition specification option values where CPDefinitionId = &#63; and CPSpecificationOptionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPSpecificationOptionId the cp specification option ID
	 */
	public static void removeByC_CSO(
		long CPDefinitionId, long CPSpecificationOptionId) {

		getPersistence().removeByC_CSO(CPDefinitionId, CPSpecificationOptionId);
	}

	/**
	 * Returns the number of cp definition specification option values where CPDefinitionId = &#63; and CPSpecificationOptionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByC_CSO(
		long CPDefinitionId, long CPSpecificationOptionId) {

		return getPersistence().countByC_CSO(
			CPDefinitionId, CPSpecificationOptionId);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPDefinitionId = &#63; and CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByC_COC(
		long CPDefinitionId, long CPOptionCategoryId, int start, int end,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_COC(
			CPDefinitionId, CPOptionCategoryId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPDefinitionId = &#63; and CPOptionCategoryId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByC_COC_First(
			long CPDefinitionId, long CPOptionCategoryId,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByC_COC_First(
			CPDefinitionId, CPOptionCategoryId, orderByComparator);
	}

	/**
	 * Returns the first cp definition specification option value in the ordered set where CPDefinitionId = &#63; and CPOptionCategoryId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionCategoryId the cp option category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByC_COC_First(
		long CPDefinitionId, long CPOptionCategoryId,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator) {

		return getPersistence().fetchByC_COC_First(
			CPDefinitionId, CPOptionCategoryId, orderByComparator);
	}

	/**
	 * Removes all the cp definition specification option values where CPDefinitionId = &#63; and CPOptionCategoryId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionCategoryId the cp option category ID
	 */
	public static void removeByC_COC(
		long CPDefinitionId, long CPOptionCategoryId) {

		getPersistence().removeByC_COC(CPDefinitionId, CPOptionCategoryId);
	}

	/**
	 * Returns the number of cp definition specification option values where CPDefinitionId = &#63; and CPOptionCategoryId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByC_COC(
		long CPDefinitionId, long CPOptionCategoryId) {

		return getPersistence().countByC_COC(
			CPDefinitionId, CPOptionCategoryId);
	}

	/**
	 * Returns the cp definition specification option value where CPDefinitionId = &#63; and key = &#63; or throws a <code>NoSuchCPDefinitionSpecificationOptionValueException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByC_K(
			long CPDefinitionId, String key)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByC_K(CPDefinitionId, key);
	}

	/**
	 * Returns the cp definition specification option value where CPDefinitionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByC_K(
		long CPDefinitionId, String key, boolean useFinderCache) {

		return getPersistence().fetchByC_K(CPDefinitionId, key, useFinderCache);
	}

	/**
	 * Removes the cp definition specification option value where CPDefinitionId = &#63; and key = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the cp definition specification option value that was removed
	 */
	public static CPDefinitionSpecificationOptionValue removeByC_K(
			long CPDefinitionId, String key)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().removeByC_K(CPDefinitionId, key);
	}

	/**
	 * Returns the number of cp definition specification option values where CPDefinitionId = &#63; and key = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByC_K(long CPDefinitionId, String key) {
		return getPersistence().countByC_K(CPDefinitionId, key);
	}

	/**
	 * Returns the cp definition specification option value where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPDefinitionSpecificationOptionValueException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition specification option value where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the cp definition specification option value where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp definition specification option value that was removed
	 */
	public static CPDefinitionSpecificationOptionValue removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of cp definition specification option values where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp definition specification option values
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new cp definition specification option value with the primary key. Does not add the cp definition specification option value to the database.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key for the new cp definition specification option value
	 * @return the new cp definition specification option value
	 */
	public static CPDefinitionSpecificationOptionValue create(
		long CPDefinitionSpecificationOptionValueId) {

		return getPersistence().create(CPDefinitionSpecificationOptionValueId);
	}

	/**
	 * Removes the cp definition specification option value with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value that was removed
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a cp definition specification option value with the primary key could not be found
	 */
	public static CPDefinitionSpecificationOptionValue remove(
			long CPDefinitionSpecificationOptionValueId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().remove(CPDefinitionSpecificationOptionValueId);
	}

	public static CPDefinitionSpecificationOptionValue updateImpl(
		CPDefinitionSpecificationOptionValue
			cpDefinitionSpecificationOptionValue) {

		return getPersistence().updateImpl(
			cpDefinitionSpecificationOptionValue);
	}

	/**
	 * Returns the cp definition specification option value with the primary key or throws a <code>NoSuchCPDefinitionSpecificationOptionValueException</code> if it could not be found.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value
	 * @throws NoSuchCPDefinitionSpecificationOptionValueException if a cp definition specification option value with the primary key could not be found
	 */
	public static CPDefinitionSpecificationOptionValue findByPrimaryKey(
			long CPDefinitionSpecificationOptionValueId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionSpecificationOptionValueException {

		return getPersistence().findByPrimaryKey(
			CPDefinitionSpecificationOptionValueId);
	}

	/**
	 * Returns the cp definition specification option value with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the primary key of the cp definition specification option value
	 * @return the cp definition specification option value, or <code>null</code> if a cp definition specification option value with the primary key could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByPrimaryKey(
		long CPDefinitionSpecificationOptionValueId) {

		return getPersistence().fetchByPrimaryKey(
			CPDefinitionSpecificationOptionValueId);
	}

	/**
	 * Returns the cp definition specification option value where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp definition specification option value where CPDefinitionSpecificationOptionValueId = &#63; and CPDefinitionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionSpecificationOptionValueId the cp definition specification option value ID
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByC_CSOVI(
		long CPDefinitionSpecificationOptionValueId, long CPDefinitionId) {

		return getPersistence().fetchByC_CSOVI(
			CPDefinitionSpecificationOptionValueId, CPDefinitionId);
	}

	/**
	 * Returns the cp definition specification option value where CPDefinitionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByC_K(
		long CPDefinitionId, String key) {

		return getPersistence().fetchByC_K(CPDefinitionId, key);
	}

	/**
	 * Returns the cp definition specification option value where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition specification option value, or <code>null</code> if a matching cp definition specification option value could not be found
	 */
	public static CPDefinitionSpecificationOptionValue fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the cp definition specification option values where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByUuid(
		String uuid) {

		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the cp definition specification option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition specification option values where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp definition specification option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition specification option values where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPDefinitionId(long CPDefinitionId) {

		return getPersistence().findByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns a range of all the cp definition specification option values where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPDefinitionId(long CPDefinitionId, int start, int end) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPDefinitionId(
			long CPDefinitionId, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition specification option values where CPSpecificationOptionId = &#63;.
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @return the matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPSpecificationOptionId(long CPSpecificationOptionId) {

		return getPersistence().findByCPSpecificationOptionId(
			CPSpecificationOptionId);
	}

	/**
	 * Returns a range of all the cp definition specification option values where CPSpecificationOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPSpecificationOptionId(
			long CPSpecificationOptionId, int start, int end) {

		return getPersistence().findByCPSpecificationOptionId(
			CPSpecificationOptionId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPSpecificationOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPSpecificationOptionId(
			long CPSpecificationOptionId, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getPersistence().findByCPSpecificationOptionId(
			CPSpecificationOptionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition specification option values where CPOptionCategoryId = &#63;.
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPOptionCategoryId(long CPOptionCategoryId) {

		return getPersistence().findByCPOptionCategoryId(CPOptionCategoryId);
	}

	/**
	 * Returns a range of all the cp definition specification option values where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPOptionCategoryId(long CPOptionCategoryId, int start, int end) {

		return getPersistence().findByCPOptionCategoryId(
			CPOptionCategoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue>
		findByCPOptionCategoryId(
			long CPOptionCategoryId, int start, int end,
			OrderByComparator<CPDefinitionSpecificationOptionValue>
				orderByComparator) {

		return getPersistence().findByCPOptionCategoryId(
			CPOptionCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition specification option values where CPDefinitionId = &#63; and CPSpecificationOptionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @return the matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByC_CSO(
		long CPDefinitionId, long CPSpecificationOptionId) {

		return getPersistence().findByC_CSO(
			CPDefinitionId, CPSpecificationOptionId);
	}

	/**
	 * Returns a range of all the cp definition specification option values where CPDefinitionId = &#63; and CPSpecificationOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByC_CSO(
		long CPDefinitionId, long CPSpecificationOptionId, int start, int end) {

		return getPersistence().findByC_CSO(
			CPDefinitionId, CPSpecificationOptionId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPDefinitionId = &#63; and CPSpecificationOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPSpecificationOptionId the cp specification option ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByC_CSO(
		long CPDefinitionId, long CPSpecificationOptionId, int start, int end,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator) {

		return getPersistence().findByC_CSO(
			CPDefinitionId, CPSpecificationOptionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the cp definition specification option values where CPDefinitionId = &#63; and CPOptionCategoryId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionCategoryId the cp option category ID
	 * @return the matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByC_COC(
		long CPDefinitionId, long CPOptionCategoryId) {

		return getPersistence().findByC_COC(CPDefinitionId, CPOptionCategoryId);
	}

	/**
	 * Returns a range of all the cp definition specification option values where CPDefinitionId = &#63; and CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @return the range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByC_COC(
		long CPDefinitionId, long CPOptionCategoryId, int start, int end) {

		return getPersistence().findByC_COC(
			CPDefinitionId, CPOptionCategoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition specification option values where CPDefinitionId = &#63; and CPOptionCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionSpecificationOptionValueModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionCategoryId the cp option category ID
	 * @param start the lower bound of the range of cp definition specification option values
	 * @param end the upper bound of the range of cp definition specification option values (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition specification option values
	 */
	public static List<CPDefinitionSpecificationOptionValue> findByC_COC(
		long CPDefinitionId, long CPOptionCategoryId, int start, int end,
		OrderByComparator<CPDefinitionSpecificationOptionValue>
			orderByComparator) {

		return getPersistence().findByC_COC(
			CPDefinitionId, CPOptionCategoryId, start, end, orderByComparator);
	}

	public static CPDefinitionSpecificationOptionValuePersistence
		getPersistence() {

		return _persistence;
	}

	public static void setPersistence(
		CPDefinitionSpecificationOptionValuePersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CPDefinitionSpecificationOptionValuePersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1900998260