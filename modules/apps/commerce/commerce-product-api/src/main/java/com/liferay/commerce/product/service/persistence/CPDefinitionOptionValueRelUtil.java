/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cp definition option value rel service. This utility wraps <code>com.liferay.commerce.product.service.persistence.impl.CPDefinitionOptionValueRelPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinitionOptionValueRelPersistence
 * @generated
 */
public class CPDefinitionOptionValueRelUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels) {

		getPersistence().cacheResult(cpDefinitionOptionValueRels);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		getPersistence().cacheResult(cpDefinitionOptionValueRel);
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
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		getPersistence().clearCache(cpDefinitionOptionValueRel);
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
	public static Map<Serializable, CPDefinitionOptionValueRel>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CPDefinitionOptionValueRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CPDefinitionOptionValueRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CPDefinitionOptionValueRel> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CPDefinitionOptionValueRel update(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		return getPersistence().update(cpDefinitionOptionValueRel);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CPDefinitionOptionValueRel update(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel,
		ServiceContext serviceContext) {

		return getPersistence().update(
			cpDefinitionOptionValueRel, serviceContext);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByUuid_First(
			String uuid,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByUuid_First(
		String uuid,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of cp definition option value rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the cp definition option value rel where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp definition option value rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the cp definition option value rel where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition option value rel that was removed
	 */
	public static CPDefinitionOptionValueRel removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of cp definition option value rels where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByGroupId_First(
			long groupId,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByGroupId_First(
		long groupId,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of cp definition option value rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByCompanyId_First(
			long companyId,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByCompanyId_First(
		long companyId,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of cp definition option value rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel>
		findByCPDefinitionOptionRelId(
			long CPDefinitionOptionRelId, int start, int end,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
			boolean useFinderCache) {

		return getPersistence().findByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel
			findByCPDefinitionOptionRelId_First(
				long CPDefinitionOptionRelId,
				OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByCPDefinitionOptionRelId_First(
			CPDefinitionOptionRelId, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel
		fetchByCPDefinitionOptionRelId_First(
			long CPDefinitionOptionRelId,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().fetchByCPDefinitionOptionRelId_First(
			CPDefinitionOptionRelId, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where CPDefinitionOptionRelId = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 */
	public static void removeByCPDefinitionOptionRelId(
		long CPDefinitionOptionRelId) {

		getPersistence().removeByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId);
	}

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByCPDefinitionOptionRelId(
		long CPDefinitionOptionRelId) {

		return getPersistence().countByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPInstanceUuid(
			CPInstanceUuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByCPInstanceUuid_First(
			String CPInstanceUuid,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByCPInstanceUuid_First(
			CPInstanceUuid, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByCPInstanceUuid_First(
		String CPInstanceUuid,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().fetchByCPInstanceUuid_First(
			CPInstanceUuid, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where CPInstanceUuid = &#63; from the database.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 */
	public static void removeByCPInstanceUuid(String CPInstanceUuid) {
		getPersistence().removeByCPInstanceUuid(CPInstanceUuid);
	}

	/**
	 * Returns the number of cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByCPInstanceUuid(String CPInstanceUuid) {
		return getPersistence().countByCPInstanceUuid(CPInstanceUuid);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByKey(
		String key, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByKey(
			key, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByKey_First(
			String key,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByKey_First(key, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where key = &#63;.
	 *
	 * @param key the key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByKey_First(
		String key,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().fetchByKey_First(key, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where key = &#63; from the database.
	 *
	 * @param key the key
	 */
	public static void removeByKey(String key) {
		getPersistence().removeByKey(key);
	}

	/**
	 * Returns the number of cp definition option value rels where key = &#63;.
	 *
	 * @param key the key
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByKey(String key) {
		return getPersistence().countByKey(key);
	}

	/**
	 * Returns the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByC_K(
			long CPDefinitionOptionRelId, String key)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByC_K(CPDefinitionOptionRelId, key);
	}

	/**
	 * Returns the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByC_K(
		long CPDefinitionOptionRelId, String key, boolean useFinderCache) {

		return getPersistence().fetchByC_K(
			CPDefinitionOptionRelId, key, useFinderCache);
	}

	/**
	 * Removes the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the cp definition option value rel that was removed
	 */
	public static CPDefinitionOptionValueRel removeByC_K(
			long CPDefinitionOptionRelId, String key)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().removeByC_K(CPDefinitionOptionRelId, key);
	}

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63; and key = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByC_K(long CPDefinitionOptionRelId, String key) {
		return getPersistence().countByC_K(CPDefinitionOptionRelId, key);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCDORI_P(
			CPDefinitionOptionRelId, preselected, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByCDORI_P_First(
			long CPDefinitionOptionRelId, boolean preselected,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByCDORI_P_First(
			CPDefinitionOptionRelId, preselected, orderByComparator);
	}

	/**
	 * Returns the first cp definition option value rel in the ordered set where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByCDORI_P_First(
		long CPDefinitionOptionRelId, boolean preselected,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().fetchByCDORI_P_First(
			CPDefinitionOptionRelId, preselected, orderByComparator);
	}

	/**
	 * Removes all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63; from the database.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 */
	public static void removeByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected) {

		getPersistence().removeByCDORI_P(CPDefinitionOptionRelId, preselected);
	}

	/**
	 * Returns the number of cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected) {

		return getPersistence().countByCDORI_P(
			CPDefinitionOptionRelId, preselected);
	}

	/**
	 * Returns the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel findByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByERC_C(
		String externalReferenceCode, long companyId, boolean useFinderCache) {

		return getPersistence().fetchByERC_C(
			externalReferenceCode, companyId, useFinderCache);
	}

	/**
	 * Removes the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the cp definition option value rel that was removed
	 */
	public static CPDefinitionOptionValueRel removeByERC_C(
			String externalReferenceCode, long companyId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().removeByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns the number of cp definition option value rels where externalReferenceCode = &#63; and companyId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the number of matching cp definition option value rels
	 */
	public static int countByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().countByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Creates a new cp definition option value rel with the primary key. Does not add the cp definition option value rel to the database.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key for the new cp definition option value rel
	 * @return the new cp definition option value rel
	 */
	public static CPDefinitionOptionValueRel create(
		long CPDefinitionOptionValueRelId) {

		return getPersistence().create(CPDefinitionOptionValueRelId);
	}

	/**
	 * Removes the cp definition option value rel with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel that was removed
	 * @throws NoSuchCPDefinitionOptionValueRelException if a cp definition option value rel with the primary key could not be found
	 */
	public static CPDefinitionOptionValueRel remove(
			long CPDefinitionOptionValueRelId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().remove(CPDefinitionOptionValueRelId);
	}

	public static CPDefinitionOptionValueRel updateImpl(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		return getPersistence().updateImpl(cpDefinitionOptionValueRel);
	}

	/**
	 * Returns the cp definition option value rel with the primary key or throws a <code>NoSuchCPDefinitionOptionValueRelException</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel
	 * @throws NoSuchCPDefinitionOptionValueRelException if a cp definition option value rel with the primary key could not be found
	 */
	public static CPDefinitionOptionValueRel findByPrimaryKey(
			long CPDefinitionOptionValueRelId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionOptionValueRelException {

		return getPersistence().findByPrimaryKey(CPDefinitionOptionValueRelId);
	}

	/**
	 * Returns the cp definition option value rel with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionOptionValueRelId the primary key of the cp definition option value rel
	 * @return the cp definition option value rel, or <code>null</code> if a cp definition option value rel with the primary key could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByPrimaryKey(
		long CPDefinitionOptionValueRelId) {

		return getPersistence().fetchByPrimaryKey(CPDefinitionOptionValueRelId);
	}

	/**
	 * Returns the cp definition option value rel where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp definition option value rel where CPDefinitionOptionRelId = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param key the key
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByC_K(
		long CPDefinitionOptionRelId, String key) {

		return getPersistence().fetchByC_K(CPDefinitionOptionRelId, key);
	}

	/**
	 * Returns the cp definition option value rel where externalReferenceCode = &#63; and companyId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param companyId the company ID
	 * @return the matching cp definition option value rel, or <code>null</code> if a matching cp definition option value rel could not be found
	 */
	public static CPDefinitionOptionValueRel fetchByERC_C(
		String externalReferenceCode, long companyId) {

		return getPersistence().fetchByERC_C(externalReferenceCode, companyId);
	}

	/**
	 * Returns all the cp definition option value rels where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the cp definition option value rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option value rels where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the cp definition option value rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option value rels where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId) {

		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the cp definition option value rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @return the matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel>
		findByCPDefinitionOptionRelId(long CPDefinitionOptionRelId) {

		return getPersistence().findByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId);
	}

	/**
	 * Returns a range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel>
		findByCPDefinitionOptionRelId(
			long CPDefinitionOptionRelId, int start, int end) {

		return getPersistence().findByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel>
		findByCPDefinitionOptionRelId(
			long CPDefinitionOptionRelId, int start, int end,
			OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findByCPDefinitionOptionRelId(
			CPDefinitionOptionRelId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @return the matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCPInstanceUuid(
		String CPInstanceUuid) {

		return getPersistence().findByCPInstanceUuid(CPInstanceUuid);
	}

	/**
	 * Returns a range of all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end) {

		return getPersistence().findByCPInstanceUuid(
			CPInstanceUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPInstanceUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPInstanceUuid the cp instance uuid
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCPInstanceUuid(
		String CPInstanceUuid, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findByCPInstanceUuid(
			CPInstanceUuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option value rels where key = &#63;.
	 *
	 * @param key the key
	 * @return the matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByKey(String key) {
		return getPersistence().findByKey(key);
	}

	/**
	 * Returns a range of all the cp definition option value rels where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByKey(
		String key, int start, int end) {

		return getPersistence().findByKey(key, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where key = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param key the key
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByKey(
		String key, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findByKey(key, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @return the matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected) {

		return getPersistence().findByCDORI_P(
			CPDefinitionOptionRelId, preselected);
	}

	/**
	 * Returns a range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @return the range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected, int start, int end) {

		return getPersistence().findByCDORI_P(
			CPDefinitionOptionRelId, preselected, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition option value rels where CPDefinitionOptionRelId = &#63; and preselected = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionOptionValueRelModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionOptionRelId the cp definition option rel ID
	 * @param preselected the preselected
	 * @param start the lower bound of the range of cp definition option value rels
	 * @param end the upper bound of the range of cp definition option value rels (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition option value rels
	 */
	public static List<CPDefinitionOptionValueRel> findByCDORI_P(
		long CPDefinitionOptionRelId, boolean preselected, int start, int end,
		OrderByComparator<CPDefinitionOptionValueRel> orderByComparator) {

		return getPersistence().findByCDORI_P(
			CPDefinitionOptionRelId, preselected, start, end,
			orderByComparator);
	}

	public static CPDefinitionOptionValueRelPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		CPDefinitionOptionValueRelPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile CPDefinitionOptionValueRelPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1949180773