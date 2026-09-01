/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.site.model.SiteSitemapRegenerationEntry;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the site sitemap regeneration entry service. This utility wraps <code>com.liferay.site.service.persistence.impl.SiteSitemapRegenerationEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see SiteSitemapRegenerationEntryPersistence
 * @generated
 */
public class SiteSitemapRegenerationEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries) {

		getPersistence().cacheResult(siteSitemapRegenerationEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		getPersistence().cacheResult(siteSitemapRegenerationEntry);
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
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		getPersistence().clearCache(siteSitemapRegenerationEntry);
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
	public static Map<Serializable, SiteSitemapRegenerationEntry>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<SiteSitemapRegenerationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<SiteSitemapRegenerationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<SiteSitemapRegenerationEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static SiteSitemapRegenerationEntry update(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		return getPersistence().update(siteSitemapRegenerationEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static SiteSitemapRegenerationEntry update(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry,
		ServiceContext serviceContext) {

		return getPersistence().update(
			siteSitemapRegenerationEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	public static List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a matching site sitemap regeneration entry could not be found
	 */
	public static SiteSitemapRegenerationEntry findByCompanyId_First(
			long companyId,
			OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator)
		throws com.liferay.site.exception.
			NoSuchSitemapRegenerationEntryException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry, or <code>null</code> if a matching site sitemap regeneration entry could not be found
	 */
	public static SiteSitemapRegenerationEntry fetchByCompanyId_First(
		long companyId,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the site sitemap regeneration entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of site sitemap regeneration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching site sitemap regeneration entries
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	public static List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey, int start, int end,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_A(
			groupId, companyId, assetTypeKey, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a matching site sitemap regeneration entry could not be found
	 */
	public static SiteSitemapRegenerationEntry findByG_C_A_First(
			long groupId, long companyId, String assetTypeKey,
			OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator)
		throws com.liferay.site.exception.
			NoSuchSitemapRegenerationEntryException {

		return getPersistence().findByG_C_A_First(
			groupId, companyId, assetTypeKey, orderByComparator);
	}

	/**
	 * Returns the first site sitemap regeneration entry in the ordered set where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching site sitemap regeneration entry, or <code>null</code> if a matching site sitemap regeneration entry could not be found
	 */
	public static SiteSitemapRegenerationEntry fetchByG_C_A_First(
		long groupId, long companyId, String assetTypeKey,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator) {

		return getPersistence().fetchByG_C_A_First(
			groupId, companyId, assetTypeKey, orderByComparator);
	}

	/**
	 * Removes all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 */
	public static void removeByG_C_A(
		long groupId, long companyId, String assetTypeKey) {

		getPersistence().removeByG_C_A(groupId, companyId, assetTypeKey);
	}

	/**
	 * Returns the number of site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @return the number of matching site sitemap regeneration entries
	 */
	public static int countByG_C_A(
		long groupId, long companyId, String assetTypeKey) {

		return getPersistence().countByG_C_A(groupId, companyId, assetTypeKey);
	}

	/**
	 * Creates a new site sitemap regeneration entry with the primary key. Does not add the site sitemap regeneration entry to the database.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key for the new site sitemap regeneration entry
	 * @return the new site sitemap regeneration entry
	 */
	public static SiteSitemapRegenerationEntry create(
		long siteSitemapRegenerationEntryId) {

		return getPersistence().create(siteSitemapRegenerationEntryId);
	}

	/**
	 * Removes the site sitemap regeneration entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry that was removed
	 * @throws NoSuchSitemapRegenerationEntryException if a site sitemap regeneration entry with the primary key could not be found
	 */
	public static SiteSitemapRegenerationEntry remove(
			long siteSitemapRegenerationEntryId)
		throws com.liferay.site.exception.
			NoSuchSitemapRegenerationEntryException {

		return getPersistence().remove(siteSitemapRegenerationEntryId);
	}

	public static SiteSitemapRegenerationEntry updateImpl(
		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

		return getPersistence().updateImpl(siteSitemapRegenerationEntry);
	}

	/**
	 * Returns the site sitemap regeneration entry with the primary key or throws a <code>NoSuchSitemapRegenerationEntryException</code> if it could not be found.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry
	 * @throws NoSuchSitemapRegenerationEntryException if a site sitemap regeneration entry with the primary key could not be found
	 */
	public static SiteSitemapRegenerationEntry findByPrimaryKey(
			long siteSitemapRegenerationEntryId)
		throws com.liferay.site.exception.
			NoSuchSitemapRegenerationEntryException {

		return getPersistence().findByPrimaryKey(
			siteSitemapRegenerationEntryId);
	}

	/**
	 * Returns the site sitemap regeneration entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param siteSitemapRegenerationEntryId the primary key of the site sitemap regeneration entry
	 * @return the site sitemap regeneration entry, or <code>null</code> if a site sitemap regeneration entry with the primary key could not be found
	 */
	public static SiteSitemapRegenerationEntry fetchByPrimaryKey(
		long siteSitemapRegenerationEntryId) {

		return getPersistence().fetchByPrimaryKey(
			siteSitemapRegenerationEntryId);
	}

	/**
	 * Returns all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching site sitemap regeneration entries
	 */
	public static List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId) {

		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @return the range of matching site sitemap regeneration entries
	 */
	public static List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	public static List<SiteSitemapRegenerationEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @return the matching site sitemap regeneration entries
	 */
	public static List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey) {

		return getPersistence().findByG_C_A(groupId, companyId, assetTypeKey);
	}

	/**
	 * Returns a range of all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @return the range of matching site sitemap regeneration entries
	 */
	public static List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey, int start, int end) {

		return getPersistence().findByG_C_A(
			groupId, companyId, assetTypeKey, start, end);
	}

	/**
	 * Returns an ordered range of all the site sitemap regeneration entries where groupId = &#63; and companyId = &#63; and assetTypeKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.site.model.impl.SiteSitemapRegenerationEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param companyId the company ID
	 * @param assetTypeKey the asset type key
	 * @param start the lower bound of the range of site sitemap regeneration entries
	 * @param end the upper bound of the range of site sitemap regeneration entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching site sitemap regeneration entries
	 */
	public static List<SiteSitemapRegenerationEntry> findByG_C_A(
		long groupId, long companyId, String assetTypeKey, int start, int end,
		OrderByComparator<SiteSitemapRegenerationEntry> orderByComparator) {

		return getPersistence().findByG_C_A(
			groupId, companyId, assetTypeKey, start, end, orderByComparator);
	}

	public static SiteSitemapRegenerationEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		SiteSitemapRegenerationEntryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile SiteSitemapRegenerationEntryPersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1326494489