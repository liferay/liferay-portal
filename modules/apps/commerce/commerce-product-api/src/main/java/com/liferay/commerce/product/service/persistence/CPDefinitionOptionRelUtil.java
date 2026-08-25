/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cp definition option rel service. This utility wraps <code>com.liferay.commerce.product.service.persistence.impl.CPDefinitionOptionRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinitionOptionRelPersistence
 * @generated
 */
public class CPDefinitionOptionRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CPDefinitionOptionRel> cpDefinitionOptionRels) {

		getPersistence().cacheResult(cpDefinitionOptionRels);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		getPersistence().cacheResult(cpDefinitionOptionRel);
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
	public static void clearCache(CPDefinitionOptionRel cpDefinitionOptionRel) {
		getPersistence().clearCache(cpDefinitionOptionRel);
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
	public static Map<Serializable, CPDefinitionOptionRel> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CPDefinitionOptionRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CPDefinitionOptionRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CPDefinitionOptionRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CPDefinitionOptionRel update(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		return getPersistence().update(cpDefinitionOptionRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CPDefinitionOptionRel update(
		CPDefinitionOptionRel cpDefinitionOptionRel,
		ServiceContext serviceContext) {

		return getPersistence().update(cpDefinitionOptionRel, serviceContext);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByUuid_First(
			String uuid,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of cp definition option rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition option rels
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the cp definition option rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp definition option rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the cp definition option rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition option rel that was removed
	 */
	public static CPDefinitionOptionRel removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of cp definition option rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition option rels
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByGroupId_First(
			long groupId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByGroupId_First(
		long groupId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of cp definition option rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp definition option rels
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByCompanyId_First(
			long companyId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of cp definition option rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().fetchByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	public static void removeByCPDefinitionId(long CPDefinitionId) {
		getPersistence().removeByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition option rels
	 */
	public static int countByCPDefinitionId(long CPDefinitionId) {
		return getPersistence().countByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPOptionId(
			CPOptionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByCPOptionId_First(
			long CPOptionId,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByCPOptionId_First(
			CPOptionId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByCPOptionId_First(
		long CPOptionId,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().fetchByCPOptionId_First(
			CPOptionId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where CPOptionId = &#63; from the database.
	 *
	 * @param CPOptionId the cp option ID
	 */
	public static void removeByCPOptionId(long CPOptionId) {
		getPersistence().removeByCPOptionId(CPOptionId);
	}

	/**
	 * Returns the number of cp definition option rels where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the number of matching cp definition option rels
	 */
	public static int countByCPOptionId(long CPOptionId) {
		return getPersistence().countByCPOptionId(CPOptionId);
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByC_C(
			long CPDefinitionId, long CPOptionId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByC_C(CPDefinitionId, CPOptionId);
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByC_C(
		long CPDefinitionId, long CPOptionId, boolean useFinderCache) {

		return getPersistence().fetchByC_C(
			CPDefinitionId, CPOptionId, useFinderCache);
	}

	/**
	 * Removes the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the cp definition option rel that was removed
	 */
	public static CPDefinitionOptionRel removeByC_C(
			long CPDefinitionId, long CPOptionId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().removeByC_C(CPDefinitionId, CPOptionId);
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and CPOptionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the number of matching cp definition option rels
	 */
	public static int countByC_C(long CPDefinitionId, long CPOptionId) {
		return getPersistence().countByC_C(CPDefinitionId, CPOptionId);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPDI_R(
			CPDefinitionId, required, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByCPDI_R_First(
			long CPDefinitionId, boolean required,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByCPDI_R_First(
			CPDefinitionId, required, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByCPDI_R_First(
		long CPDefinitionId, boolean required,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().fetchByCPDI_R_First(
			CPDefinitionId, required, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; and required = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 */
	public static void removeByCPDI_R(long CPDefinitionId, boolean required) {
		getPersistence().removeByCPDI_R(CPDefinitionId, required);
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @return the number of matching cp definition option rels
	 */
	public static int countByCPDI_R(long CPDefinitionId, boolean required) {
		return getPersistence().countByCPDI_R(CPDefinitionId, required);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_SC(
			CPDefinitionId, skuContributor, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByC_SC_First(
			long CPDefinitionId, boolean skuContributor,
			OrderByComparator<CPDefinitionOptionRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByC_SC_First(
			CPDefinitionId, skuContributor, orderByComparator);
	}

	/**
	 * Returns the first cp definition option rel in the ordered set where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByC_SC_First(
		long CPDefinitionId, boolean skuContributor,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().fetchByC_SC_First(
			CPDefinitionId, skuContributor, orderByComparator);
	}

	/**
	 * Removes all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 */
	public static void removeByC_SC(
		long CPDefinitionId, boolean skuContributor) {

		getPersistence().removeByC_SC(CPDefinitionId, skuContributor);
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @return the number of matching cp definition option rels
	 */
	public static int countByC_SC(long CPDefinitionId, boolean skuContributor) {
		return getPersistence().countByC_SC(CPDefinitionId, skuContributor);
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and key = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByC_K(
			long CPDefinitionId, String key)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByC_K(CPDefinitionId, key);
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByC_K(
		long CPDefinitionId, String key, boolean useFinderCache) {

		return getPersistence().fetchByC_K(CPDefinitionId, key, useFinderCache);
	}

	/**
	 * Removes the cp definition option rel where CPDefinitionId = &#63; and key = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the cp definition option rel that was removed
	 */
	public static CPDefinitionOptionRel removeByC_K(
			long CPDefinitionId, String key)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().removeByC_K(CPDefinitionId, key);
	}

	/**
	 * Returns the number of cp definition option rels where CPDefinitionId = &#63; and key = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the number of matching cp definition option rels
	 */
	public static int countByC_K(long CPDefinitionId, String key) {
		return getPersistence().countByC_K(CPDefinitionId, key);
	}

	/**
	 * Returns the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp definition option rel that was removed
	 */
	public static CPDefinitionOptionRel removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of cp definition option rels where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp definition option rels
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new cp definition option rel with the primary key. Does not add the cp definition option rel to the database.
	 *
	 * @param CPDefinitionOptionRelId the primary key for the new cp definition option rel
	 * @return the new cp definition option rel
	 */
	public static CPDefinitionOptionRel create(long CPDefinitionOptionRelId) {
		return getPersistence().create(CPDefinitionOptionRelId);
	}

	/**
	 * Removes the cp definition option rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel that was removed
	 * @throws NoSuchCPDefinitionOptionRelException if a cp definition option rel with the primary key could not be found
	 */
	public static CPDefinitionOptionRel remove(long CPDefinitionOptionRelId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().remove(CPDefinitionOptionRelId);
	}

	public static CPDefinitionOptionRel updateImpl(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		return getPersistence().updateImpl(cpDefinitionOptionRel);
	}

	/**
	 * Returns the cp definition option rel with the primary key or throws a <code>NoSuchCPDefinitionOptionRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel
	 * @throws NoSuchCPDefinitionOptionRelException if a cp definition option rel with the primary key could not be found
	 */
	public static CPDefinitionOptionRel findByPrimaryKey(
			long CPDefinitionOptionRelId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionRelException {

		return getPersistence().findByPrimaryKey(CPDefinitionOptionRelId);
	}

	/**
	 * Returns the cp definition option rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the primary key of the cp definition option rel
	 * @return the cp definition option rel, or <code>null</code> if a cp definition option rel with the primary key could not be found
	 */
	public static CPDefinitionOptionRel fetchByPrimaryKey(
		long CPDefinitionOptionRelId) {

		return getPersistence().fetchByPrimaryKey(CPDefinitionOptionRelId);
	}

	/**
	 * Returns the cp definition option rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and CPOptionId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CPOptionId the cp option ID
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByC_C(
		long CPDefinitionId, long CPOptionId) {

		return getPersistence().fetchByC_C(CPDefinitionId, CPOptionId);
	}

	/**
	 * Returns the cp definition option rel where CPDefinitionId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param key the key
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByC_K(
		long CPDefinitionId, String key) {

		return getPersistence().fetchByC_K(CPDefinitionId, key);
	}

	/**
	 * Returns the cp definition option rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option rel, or <code>null</code> if a matching cp definition option rel could not be found
	 */
	public static CPDefinitionOptionRel fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the cp definition option rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the cp definition option rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the cp definition option rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the cp definition option rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId) {

		return getPersistence().findByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns a range of all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId, int start, int end) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * @param CPOptionId the cp option ID
	 * @return the matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId) {

		return getPersistence().findByCPOptionId(CPOptionId);
	}

	/**
	 * Returns a range of all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId, int start, int end) {

		return getPersistence().findByCPOptionId(CPOptionId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPOptionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPOptionId the cp option ID
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPOptionId(
		long CPOptionId, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findByCPOptionId(
			CPOptionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @return the matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required) {

		return getPersistence().findByCPDI_R(CPDefinitionId, required);
	}

	/**
	 * Returns a range of all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required, int start, int end) {

		return getPersistence().findByCPDI_R(
			CPDefinitionId, required, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and required = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param required the required
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByCPDI_R(
		long CPDefinitionId, boolean required, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findByCPDI_R(
			CPDefinitionId, required, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @return the matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor) {

		return getPersistence().findByC_SC(CPDefinitionId, skuContributor);
	}

	/**
	 * Returns a range of all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @return the range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor, int start, int end) {

		return getPersistence().findByC_SC(
			CPDefinitionId, skuContributor, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option rels where CPDefinitionId = &#63; and skuContributor = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param skuContributor the sku contributor
	 * @param start the lower bound of the range of cp definition option rels
	 * @param end the upper bound of the range of cp definition option rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option rels
	 */
	public static List<CPDefinitionOptionRel> findByC_SC(
		long CPDefinitionId, boolean skuContributor, int start, int end,
		OrderByComparator<CPDefinitionOptionRel> orderByComparator) {

		return getPersistence().findByC_SC(
			CPDefinitionId, skuContributor, start, end, orderByComparator);
	}

	public static CPDefinitionOptionRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CPDefinitionOptionRelPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CPDefinitionOptionRelPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1507636456