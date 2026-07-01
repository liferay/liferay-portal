/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout friendly url service. This utility wraps <code>com.liferay.portal.service.persistence.impl.LayoutFriendlyURLPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutFriendlyURLPersistence
 * @generated
 */
public class LayoutFriendlyURLUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<LayoutFriendlyURL> layoutFriendlyURLs) {
		getPersistence().cacheResult(layoutFriendlyURLs);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(LayoutFriendlyURL layoutFriendlyURL) {
		getPersistence().cacheResult(layoutFriendlyURL);
	}

	/**
	 * @see BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(LayoutFriendlyURL layoutFriendlyURL) {
		getPersistence().clearCache(layoutFriendlyURL);
	}

	/**
	 * @see BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, LayoutFriendlyURL> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutFriendlyURL> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutFriendlyURL> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutFriendlyURL> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutFriendlyURL update(
		LayoutFriendlyURL layoutFriendlyURL) {

		return getPersistence().update(layoutFriendlyURL);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutFriendlyURL update(
		LayoutFriendlyURL layoutFriendlyURL, ServiceContext serviceContext) {

		return getPersistence().update(layoutFriendlyURL, serviceContext);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByUuid_First(
			String uuid, OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByUuid_First(
		String uuid, OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of layout friendly urls where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout friendly urls
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the layout friendly url where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchLayoutFriendlyURLException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByUUID_G(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout friendly url where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the layout friendly url where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout friendly url that was removed
	 */
	public static LayoutFriendlyURL removeByUUID_G(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of layout friendly urls where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout friendly urls
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of layout friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout friendly urls
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByGroupId_First(
		long groupId, OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of layout friendly urls where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout friendly urls
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByCompanyId_First(
			long companyId,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByCompanyId_First(
		long companyId,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of layout friendly urls where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching layout friendly urls
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByPlid(
			plid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByPlid_First(
			long plid, OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByPlid_First(plid, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where plid = &#63;.
	 *
	 * @param plid the plid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByPlid_First(
		long plid, OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().fetchByPlid_First(plid, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where plid = &#63; from the database.
	 *
	 * @param plid the plid
	 */
	public static void removeByPlid(long plid) {
		getPersistence().removeByPlid(plid);
	}

	/**
	 * Returns the number of layout friendly urls where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the number of matching layout friendly urls
	 */
	public static int countByPlid(long plid) {
		return getPersistence().countByPlid(plid);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByC_F(
		long companyId, String friendlyURL, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_F(
			companyId, friendlyURL, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByC_F_First(
			long companyId, String friendlyURL,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByC_F_First(
			companyId, friendlyURL, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByC_F_First(
		long companyId, String friendlyURL,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().fetchByC_F_First(
			companyId, friendlyURL, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where companyId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 */
	public static void removeByC_F(long companyId, String friendlyURL) {
		getPersistence().removeByC_F(companyId, friendlyURL);
	}

	/**
	 * Returns the number of layout friendly urls where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching layout friendly urls
	 */
	public static int countByC_F(long companyId, String friendlyURL) {
		return getPersistence().countByC_F(companyId, friendlyURL);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByP_F(
		long plid, String friendlyURL, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_F(
			plid, friendlyURL, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where plid = &#63; and friendlyURL = &#63;.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByP_F_First(
			long plid, String friendlyURL,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByP_F_First(
			plid, friendlyURL, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where plid = &#63; and friendlyURL = &#63;.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByP_F_First(
		long plid, String friendlyURL,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().fetchByP_F_First(
			plid, friendlyURL, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where plid = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 */
	public static void removeByP_F(long plid, String friendlyURL) {
		getPersistence().removeByP_F(plid, friendlyURL);
	}

	/**
	 * Returns the number of layout friendly urls where plid = &#63; and friendlyURL = &#63;.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @return the number of matching layout friendly urls
	 */
	public static int countByP_F(long plid, String friendlyURL) {
		return getPersistence().countByP_F(plid, friendlyURL);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = &#63; and languageId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plids the plids
	 * @param languageId the language ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByP_L(
		long[] plids, String languageId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L(
			plids, languageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the layout friendly url where plid = &#63; and languageId = &#63; or throws a <code>NoSuchLayoutFriendlyURLException</code> if it could not be found.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @return the matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByP_L(long plid, String languageId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByP_L(plid, languageId);
	}

	/**
	 * Returns the layout friendly url where plid = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByP_L(
		long plid, String languageId, boolean useFinderCache) {

		return getPersistence().fetchByP_L(plid, languageId, useFinderCache);
	}

	/**
	 * Removes the layout friendly url where plid = &#63; and languageId = &#63; from the database.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @return the layout friendly url that was removed
	 */
	public static LayoutFriendlyURL removeByP_L(long plid, String languageId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().removeByP_L(plid, languageId);
	}

	/**
	 * Returns the number of layout friendly urls where plid = &#63; and languageId = &#63;.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @return the number of matching layout friendly urls
	 */
	public static int countByP_L(long plid, String languageId) {
		return getPersistence().countByP_L(plid, languageId);
	}

	/**
	 * Returns the number of layout friendly urls where plid = any &#63; and languageId = &#63;.
	 *
	 * @param plids the plids
	 * @param languageId the language ID
	 * @return the number of matching layout friendly urls
	 */
	public static int countByP_L(long[] plids, String languageId) {
		return getPersistence().countByP_L(plids, languageId);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL, int start,
		int end, OrderByComparator<LayoutFriendlyURL> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_F(
			groupId, privateLayout, friendlyURL, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByG_P_F_First(
			long groupId, boolean privateLayout, String friendlyURL,
			OrderByComparator<LayoutFriendlyURL> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByG_P_F_First(
			groupId, privateLayout, friendlyURL, orderByComparator);
	}

	/**
	 * Returns the first layout friendly url in the ordered set where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByG_P_F_First(
		long groupId, boolean privateLayout, String friendlyURL,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().fetchByG_P_F_First(
			groupId, privateLayout, friendlyURL, orderByComparator);
	}

	/**
	 * Removes all the layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 */
	public static void removeByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		getPersistence().removeByG_P_F(groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns the number of layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the number of matching layout friendly urls
	 */
	public static int countByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		return getPersistence().countByG_P_F(
			groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns the layout friendly url where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63; or throws a <code>NoSuchLayoutFriendlyURLException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the matching layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL findByG_P_F_L(
			long groupId, boolean privateLayout, String friendlyURL,
			String languageId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByG_P_F_L(
			groupId, privateLayout, friendlyURL, languageId);
	}

	/**
	 * Returns the layout friendly url where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByG_P_F_L(
		long groupId, boolean privateLayout, String friendlyURL,
		String languageId, boolean useFinderCache) {

		return getPersistence().fetchByG_P_F_L(
			groupId, privateLayout, friendlyURL, languageId, useFinderCache);
	}

	/**
	 * Removes the layout friendly url where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the layout friendly url that was removed
	 */
	public static LayoutFriendlyURL removeByG_P_F_L(
			long groupId, boolean privateLayout, String friendlyURL,
			String languageId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().removeByG_P_F_L(
			groupId, privateLayout, friendlyURL, languageId);
	}

	/**
	 * Returns the number of layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the number of matching layout friendly urls
	 */
	public static int countByG_P_F_L(
		long groupId, boolean privateLayout, String friendlyURL,
		String languageId) {

		return getPersistence().countByG_P_F_L(
			groupId, privateLayout, friendlyURL, languageId);
	}

	/**
	 * Creates a new layout friendly url with the primary key. Does not add the layout friendly url to the database.
	 *
	 * @param layoutFriendlyURLId the primary key for the new layout friendly url
	 * @return the new layout friendly url
	 */
	public static LayoutFriendlyURL create(long layoutFriendlyURLId) {
		return getPersistence().create(layoutFriendlyURLId);
	}

	/**
	 * Removes the layout friendly url with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url that was removed
	 * @throws NoSuchLayoutFriendlyURLException if a layout friendly url with the primary key could not be found
	 */
	public static LayoutFriendlyURL remove(long layoutFriendlyURLId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().remove(layoutFriendlyURLId);
	}

	public static LayoutFriendlyURL updateImpl(
		LayoutFriendlyURL layoutFriendlyURL) {

		return getPersistence().updateImpl(layoutFriendlyURL);
	}

	/**
	 * Returns the layout friendly url with the primary key or throws a <code>NoSuchLayoutFriendlyURLException</code> if it could not be found.
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url
	 * @throws NoSuchLayoutFriendlyURLException if a layout friendly url with the primary key could not be found
	 */
	public static LayoutFriendlyURL findByPrimaryKey(long layoutFriendlyURLId)
		throws com.liferay.portal.kernel.exception.
			NoSuchLayoutFriendlyURLException {

		return getPersistence().findByPrimaryKey(layoutFriendlyURLId);
	}

	/**
	 * Returns the layout friendly url with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutFriendlyURLId the primary key of the layout friendly url
	 * @return the layout friendly url, or <code>null</code> if a layout friendly url with the primary key could not be found
	 */
	public static LayoutFriendlyURL fetchByPrimaryKey(
		long layoutFriendlyURLId) {

		return getPersistence().fetchByPrimaryKey(layoutFriendlyURLId);
	}

	/**
	 * Returns the layout friendly url where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout friendly url where plid = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param plid the plid
	 * @param languageId the language ID
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByP_L(long plid, String languageId) {
		return getPersistence().fetchByP_L(plid, languageId);
	}

	/**
	 * Returns the layout friendly url where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63; and languageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param languageId the language ID
	 * @return the matching layout friendly url, or <code>null</code> if a matching layout friendly url could not be found
	 */
	public static LayoutFriendlyURL fetchByG_P_F_L(
		long groupId, boolean privateLayout, String friendlyURL,
		String languageId) {

		return getPersistence().fetchByG_P_F_L(
			groupId, privateLayout, friendlyURL, languageId);
	}

	/**
	 * Returns all the layout friendly urls where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the layout friendly urls where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout friendly urls where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the layout friendly urls where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout friendly urls where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the layout friendly urls where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout friendly urls where plid = &#63;.
	 *
	 * @param plid the plid
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByPlid(long plid) {
		return getPersistence().findByPlid(plid);
	}

	/**
	 * Returns a range of all the layout friendly urls where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByPlid(
		long plid, int start, int end) {

		return getPersistence().findByPlid(plid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByPlid(
		long plid, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByPlid(plid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout friendly urls where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByC_F(
		long companyId, String friendlyURL) {

		return getPersistence().findByC_F(companyId, friendlyURL);
	}

	/**
	 * Returns a range of all the layout friendly urls where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByC_F(
		long companyId, String friendlyURL, int start, int end) {

		return getPersistence().findByC_F(companyId, friendlyURL, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where companyId = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByC_F(
		long companyId, String friendlyURL, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByC_F(
			companyId, friendlyURL, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout friendly urls where plid = &#63; and friendlyURL = &#63;.
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByP_F(
		long plid, String friendlyURL) {

		return getPersistence().findByP_F(plid, friendlyURL);
	}

	/**
	 * Returns a range of all the layout friendly urls where plid = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByP_F(
		long plid, String friendlyURL, int start, int end) {

		return getPersistence().findByP_F(plid, friendlyURL, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plid the plid
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByP_F(
		long plid, String friendlyURL, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByP_F(
			plid, friendlyURL, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout friendly urls where plid = any &#63; and languageId = &#63;.
	 *
	 * @param plids the plids
	 * @param languageId the language ID
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByP_L(
		long[] plids, String languageId) {

		return getPersistence().findByP_L(plids, languageId);
	}

	/**
	 * Returns a range of all the layout friendly urls where plid = any &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plids the plids
	 * @param languageId the language ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByP_L(
		long[] plids, String languageId, int start, int end) {

		return getPersistence().findByP_L(plids, languageId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where plid = any &#63; and languageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param plids the plids
	 * @param languageId the language ID
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByP_L(
		long[] plids, String languageId, int start, int end,
		OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByP_L(
			plids, languageId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @return the matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL) {

		return getPersistence().findByG_P_F(
			groupId, privateLayout, friendlyURL);
	}

	/**
	 * Returns a range of all the layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @return the range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL, int start,
		int end) {

		return getPersistence().findByG_P_F(
			groupId, privateLayout, friendlyURL, start, end);
	}

	/**
	 * Returns an ordered range of all the layout friendly urls where groupId = &#63; and privateLayout = &#63; and friendlyURL = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutFriendlyURLModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param privateLayout the private layout
	 * @param friendlyURL the friendly url
	 * @param start the lower bound of the range of layout friendly urls
	 * @param end the upper bound of the range of layout friendly urls (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout friendly urls
	 */
	public static List<LayoutFriendlyURL> findByG_P_F(
		long groupId, boolean privateLayout, String friendlyURL, int start,
		int end, OrderByComparator<LayoutFriendlyURL> orderByComparator) {

		return getPersistence().findByG_P_F(
			groupId, privateLayout, friendlyURL, start, end, orderByComparator);
	}

	public static LayoutFriendlyURLPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		LayoutFriendlyURLPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile LayoutFriendlyURLPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-184087854