/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence;

import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the cp definition link service. This utility wraps <code>com.liferay.commerce.product.service.persistence.impl.CPDefinitionLinkPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Marco Leo
 * @see CPDefinitionLinkPersistence
 * @generated
 */
public class CPDefinitionLinkUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<CPDefinitionLink> cpDefinitionLinks) {
		getPersistence().cacheResult(cpDefinitionLinks);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(CPDefinitionLink cpDefinitionLink) {
		getPersistence().cacheResult(cpDefinitionLink);
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
	public static void clearCache(CPDefinitionLink cpDefinitionLink) {
		getPersistence().clearCache(cpDefinitionLink);
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
	public static Map<Serializable, CPDefinitionLink> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<CPDefinitionLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<CPDefinitionLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<CPDefinitionLink> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static CPDefinitionLink update(CPDefinitionLink cpDefinitionLink) {
		return getPersistence().update(cpDefinitionLink);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static CPDefinitionLink update(
		CPDefinitionLink cpDefinitionLink, ServiceContext serviceContext) {

		return getPersistence().update(cpDefinitionLink, serviceContext);
	}

	/**
	 * Returns an ordered range of all the cp definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByUuid_First(
			String uuid, OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByUuid_First(
		String uuid, OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of cp definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching cp definition links
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the cp definition link where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCPDefinitionLinkException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the cp definition link where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the cp definition link that was removed
	 */
	public static CPDefinitionLink removeByUUID_G(String uuid, long groupId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of cp definition links where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching cp definition links
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the cp definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of cp definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching cp definition links
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByCPDefinitionId_First(
			long CPDefinitionId,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByCPDefinitionId_First(
		long CPDefinitionId,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCPDefinitionId_First(
			CPDefinitionId, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CPDefinitionId = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 */
	public static void removeByCPDefinitionId(long CPDefinitionId) {
		getPersistence().removeByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the number of matching cp definition links
	 */
	public static int countByCPDefinitionId(long CPDefinitionId) {
		return getPersistence().countByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCProductId(
			CProductId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByCProductId_First(
			long CProductId,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByCProductId_First(
			CProductId, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByCProductId_First(
		long CProductId,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCProductId_First(
			CProductId, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CProductId = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 */
	public static void removeByCProductId(long CProductId) {
		getPersistence().removeByCProductId(CProductId);
	}

	/**
	 * Returns the number of cp definition links where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the number of matching cp definition links
	 */
	public static int countByCProductId(long CProductId) {
		return getPersistence().countByCProductId(CProductId);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_T(
		long CPDefinitionId, String type, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPD_T(
			CPDefinitionId, type, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByCPD_T_First(
			long CPDefinitionId, String type,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByCPD_T_First(
			CPDefinitionId, type, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByCPD_T_First(
		long CPDefinitionId, String type,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCPD_T_First(
			CPDefinitionId, type, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CPDefinitionId = &#63; and type = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 */
	public static void removeByCPD_T(long CPDefinitionId, String type) {
		getPersistence().removeByCPD_T(CPDefinitionId, type);
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @return the number of matching cp definition links
	 */
	public static int countByCPD_T(long CPDefinitionId, String type) {
		return getPersistence().countByCPD_T(CPDefinitionId, type);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_S(
		long CPDefinitionId, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPD_S(
			CPDefinitionId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByCPD_S_First(
			long CPDefinitionId, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByCPD_S_First(
			CPDefinitionId, status, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByCPD_S_First(
		long CPDefinitionId, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCPD_S_First(
			CPDefinitionId, status, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CPDefinitionId = &#63; and status = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 */
	public static void removeByCPD_S(long CPDefinitionId, int status) {
		getPersistence().removeByCPD_S(CPDefinitionId, status);
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	public static int countByCPD_S(long CPDefinitionId, int status) {
		return getPersistence().countByCPD_S(CPDefinitionId, status);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCP_S(
		long CProductId, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCP_S(
			CProductId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByCP_S_First(
			long CProductId, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByCP_S_First(
			CProductId, status, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByCP_S_First(
		long CProductId, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCP_S_First(
			CProductId, status, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CProductId = &#63; and status = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 */
	public static void removeByCP_S(long CProductId, int status) {
		getPersistence().removeByCP_S(CProductId, status);
	}

	/**
	 * Returns the number of cp definition links where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	public static int countByCP_S(long CProductId, int status) {
		return getPersistence().countByCP_S(CProductId, status);
	}

	/**
	 * Returns all the cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByLtD_S(
		Date displayDate, int status) {

		return getPersistence().findByLtD_S(displayDate, status);
	}

	/**
	 * Returns a range of all the cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return getPersistence().findByLtD_S(displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByLtD_S(Date displayDate, int status) {
		getPersistence().removeByLtD_S(displayDate, status);
	}

	/**
	 * Returns the number of cp definition links where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	public static int countByLtD_S(Date displayDate, int status) {
		return getPersistence().countByLtD_S(displayDate, status);
	}

	/**
	 * Returns all the cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByLtE_S(
		Date expirationDate, int status) {

		return getPersistence().findByLtE_S(expirationDate, status);
	}

	/**
	 * Returns a range of all the cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByLtE_S(
		Date expirationDate, int status, int start, int end) {

		return getPersistence().findByLtE_S(expirationDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByLtE_S(
			expirationDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByLtE_S(
		Date expirationDate, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtE_S(
			expirationDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByLtE_S_First(
			Date expirationDate, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByLtE_S_First(
			expirationDate, status, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByLtE_S_First(
		Date expirationDate, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByLtE_S_First(
			expirationDate, status, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where expirationDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 */
	public static void removeByLtE_S(Date expirationDate, int status) {
		getPersistence().removeByLtE_S(expirationDate, status);
	}

	/**
	 * Returns the number of cp definition links where expirationDate &lt; &#63; and status = &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	public static int countByLtE_S(Date expirationDate, int status) {
		return getPersistence().countByLtE_S(expirationDate, status);
	}

	/**
	 * Returns the cp definition link where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63; or throws a <code>NoSuchCPDefinitionLinkException</code> if it could not be found.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @return the matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByC_C_T(
			long CPDefinitionId, long CProductId, String type)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByC_C_T(CPDefinitionId, CProductId, type);
	}

	/**
	 * Returns the cp definition link where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByC_C_T(
		long CPDefinitionId, long CProductId, String type,
		boolean useFinderCache) {

		return getPersistence().fetchByC_C_T(
			CPDefinitionId, CProductId, type, useFinderCache);
	}

	/**
	 * Removes the cp definition link where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @return the cp definition link that was removed
	 */
	public static CPDefinitionLink removeByC_C_T(
			long CPDefinitionId, long CProductId, String type)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().removeByC_C_T(CPDefinitionId, CProductId, type);
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @return the number of matching cp definition links
	 */
	public static int countByC_C_T(
		long CPDefinitionId, long CProductId, String type) {

		return getPersistence().countByC_C_T(CPDefinitionId, CProductId, type);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_T_S(
		long CPDefinitionId, String type, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCPD_T_S(
			CPDefinitionId, type, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByCPD_T_S_First(
			long CPDefinitionId, String type, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByCPD_T_S_First(
			CPDefinitionId, type, status, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByCPD_T_S_First(
		long CPDefinitionId, String type, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCPD_T_S_First(
			CPDefinitionId, type, status, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByCPD_T_S(
		long CPDefinitionId, String type, int status) {

		getPersistence().removeByCPD_T_S(CPDefinitionId, type, status);
	}

	/**
	 * Returns the number of cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	public static int countByCPD_T_S(
		long CPDefinitionId, String type, int status) {

		return getPersistence().countByCPD_T_S(CPDefinitionId, type, status);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCP_T_S(
		long CProductId, String type, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCP_T_S(
			CProductId, type, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink findByCP_T_S_First(
			long CProductId, String type, int status,
			OrderByComparator<CPDefinitionLink> orderByComparator)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByCP_T_S_First(
			CProductId, type, status, orderByComparator);
	}

	/**
	 * Returns the first cp definition link in the ordered set where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByCP_T_S_First(
		long CProductId, String type, int status,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().fetchByCP_T_S_First(
			CProductId, type, status, orderByComparator);
	}

	/**
	 * Removes all the cp definition links where CProductId = &#63; and type = &#63; and status = &#63; from the database.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 */
	public static void removeByCP_T_S(
		long CProductId, String type, int status) {

		getPersistence().removeByCP_T_S(CProductId, type, status);
	}

	/**
	 * Returns the number of cp definition links where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @return the number of matching cp definition links
	 */
	public static int countByCP_T_S(long CProductId, String type, int status) {
		return getPersistence().countByCP_T_S(CProductId, type, status);
	}

	/**
	 * Creates a new cp definition link with the primary key. Does not add the cp definition link to the database.
	 *
	 * @param CPDefinitionLinkId the primary key for the new cp definition link
	 * @return the new cp definition link
	 */
	public static CPDefinitionLink create(long CPDefinitionLinkId) {
		return getPersistence().create(CPDefinitionLinkId);
	}

	/**
	 * Removes the cp definition link with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link that was removed
	 * @throws NoSuchCPDefinitionLinkException if a cp definition link with the primary key could not be found
	 */
	public static CPDefinitionLink remove(long CPDefinitionLinkId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().remove(CPDefinitionLinkId);
	}

	public static CPDefinitionLink updateImpl(
		CPDefinitionLink cpDefinitionLink) {

		return getPersistence().updateImpl(cpDefinitionLink);
	}

	/**
	 * Returns the cp definition link with the primary key or throws a <code>NoSuchCPDefinitionLinkException</code> if it could not be found.
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link
	 * @throws NoSuchCPDefinitionLinkException if a cp definition link with the primary key could not be found
	 */
	public static CPDefinitionLink findByPrimaryKey(long CPDefinitionLinkId)
		throws com.liferay.commerce.product.exception.
			NoSuchCPDefinitionLinkException {

		return getPersistence().findByPrimaryKey(CPDefinitionLinkId);
	}

	/**
	 * Returns the cp definition link with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param CPDefinitionLinkId the primary key of the cp definition link
	 * @return the cp definition link, or <code>null</code> if a cp definition link with the primary key could not be found
	 */
	public static CPDefinitionLink fetchByPrimaryKey(long CPDefinitionLinkId) {
		return getPersistence().fetchByPrimaryKey(CPDefinitionLinkId);
	}

	/**
	 * Returns the cp definition link where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the cp definition link where CPDefinitionId = &#63; and CProductId = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param CProductId the c product ID
	 * @param type the type
	 * @return the matching cp definition link, or <code>null</code> if a matching cp definition link could not be found
	 */
	public static CPDefinitionLink fetchByC_C_T(
		long CPDefinitionId, long CProductId, String type) {

		return getPersistence().fetchByC_C_T(CPDefinitionId, CProductId, type);
	}

	/**
	 * Returns all the cp definition links where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the cp definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the cp definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links where CPDefinitionId = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPDefinitionId(
		long CPDefinitionId) {

		return getPersistence().findByCPDefinitionId(CPDefinitionId);
	}

	/**
	 * Returns a range of all the cp definition links where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPDefinitionId(
		long CPDefinitionId, int start, int end) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPDefinitionId(
		long CPDefinitionId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByCPDefinitionId(
			CPDefinitionId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links where CProductId = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCProductId(long CProductId) {
		return getPersistence().findByCProductId(CProductId);
	}

	/**
	 * Returns a range of all the cp definition links where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCProductId(
		long CProductId, int start, int end) {

		return getPersistence().findByCProductId(CProductId, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCProductId(
		long CProductId, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByCProductId(
			CProductId, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_T(
		long CPDefinitionId, String type) {

		return getPersistence().findByCPD_T(CPDefinitionId, type);
	}

	/**
	 * Returns a range of all the cp definition links where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_T(
		long CPDefinitionId, String type, int start, int end) {

		return getPersistence().findByCPD_T(CPDefinitionId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_T(
		long CPDefinitionId, String type, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByCPD_T(
			CPDefinitionId, type, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_S(
		long CPDefinitionId, int status) {

		return getPersistence().findByCPD_S(CPDefinitionId, status);
	}

	/**
	 * Returns a range of all the cp definition links where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_S(
		long CPDefinitionId, int status, int start, int end) {

		return getPersistence().findByCPD_S(CPDefinitionId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_S(
		long CPDefinitionId, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByCPD_S(
			CPDefinitionId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links where CProductId = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCP_S(
		long CProductId, int status) {

		return getPersistence().findByCP_S(CProductId, status);
	}

	/**
	 * Returns a range of all the cp definition links where CProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCP_S(
		long CProductId, int status, int start, int end) {

		return getPersistence().findByCP_S(CProductId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCP_S(
		long CProductId, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByCP_S(
			CProductId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_T_S(
		long CPDefinitionId, String type, int status) {

		return getPersistence().findByCPD_T_S(CPDefinitionId, type, status);
	}

	/**
	 * Returns a range of all the cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_T_S(
		long CPDefinitionId, String type, int status, int start, int end) {

		return getPersistence().findByCPD_T_S(
			CPDefinitionId, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CPDefinitionId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CPDefinitionId the cp definition ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCPD_T_S(
		long CPDefinitionId, String type, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByCPD_T_S(
			CPDefinitionId, type, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the cp definition links where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @return the matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCP_T_S(
		long CProductId, String type, int status) {

		return getPersistence().findByCP_T_S(CProductId, type, status);
	}

	/**
	 * Returns a range of all the cp definition links where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @return the range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCP_T_S(
		long CProductId, String type, int status, int start, int end) {

		return getPersistence().findByCP_T_S(
			CProductId, type, status, start, end);
	}

	/**
	 * Returns an ordered range of all the cp definition links where CProductId = &#63; and type = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.commerce.product.model.impl.CPDefinitionLinkModelImpl</code>.
	 * </p>
	 *
	 * @param CProductId the c product ID
	 * @param type the type
	 * @param status the status
	 * @param start the lower bound of the range of cp definition links
	 * @param end the upper bound of the range of cp definition links (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching cp definition links
	 */
	public static List<CPDefinitionLink> findByCP_T_S(
		long CProductId, String type, int status, int start, int end,
		OrderByComparator<CPDefinitionLink> orderByComparator) {

		return getPersistence().findByCP_T_S(
			CProductId, type, status, start, end, orderByComparator);
	}

	public static CPDefinitionLinkPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(CPDefinitionLinkPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile CPDefinitionLinkPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2106824522