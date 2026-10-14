/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.service.persistence;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the blogs entry service. This utility wraps <code>com.liferay.blogs.service.persistence.impl.BlogsEntryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see BlogsEntryPersistence
 * @generated
 */
public class BlogsEntryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<BlogsEntry> blogsEntries) {
		getPersistence().cacheResult(blogsEntries);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(BlogsEntry blogsEntry) {
		getPersistence().cacheResult(blogsEntry);
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
	public static void clearCache(BlogsEntry blogsEntry) {
		getPersistence().clearCache(blogsEntry);
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
	public static Map<Serializable, BlogsEntry> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<BlogsEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<BlogsEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<BlogsEntry> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static BlogsEntry update(BlogsEntry blogsEntry) {
		return getPersistence().update(blogsEntry);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static BlogsEntry update(
		BlogsEntry blogsEntry, ServiceContext serviceContext) {

		return getPersistence().update(blogsEntry, serviceContext);
	}

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByUuid_First(
			String uuid, OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByUuid_First(
		String uuid, OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of blogs entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching blogs entries
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the blogs entry where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByUUID_G(String uuid, long groupId)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the blogs entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the blogs entry where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the blogs entry that was removed
	 */
	public static BlogsEntry removeByUUID_G(String uuid, long groupId)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of blogs entries where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching blogs entries
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByGroupId_First(
			long groupId, OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByGroupId_First(
		long groupId, OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByCompanyId_First(
			long companyId, OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByCompanyId_First(
		long companyId, OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching blogs entries
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns the blogs entry where groupId = &#63; and urlTitle = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_UT(long groupId, String urlTitle)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns the blogs entry where groupId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_UT(
		long groupId, String urlTitle, boolean useFinderCache) {

		return getPersistence().fetchByG_UT(groupId, urlTitle, useFinderCache);
	}

	/**
	 * Removes the blogs entry where groupId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the blogs entry that was removed
	 */
	public static BlogsEntry removeByG_UT(long groupId, String urlTitle)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().removeByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the number of matching blogs entries
	 */
	public static int countByG_UT(long groupId, String urlTitle) {
		return getPersistence().countByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD(long groupId, Date displayDate) {
		return getPersistence().findByG_LtD(groupId, displayDate);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end) {

		return getPersistence().findByG_LtD(groupId, displayDate, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_LtD(
			groupId, displayDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LtD(
			groupId, displayDate, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_LtD_First(
			long groupId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_LtD_First(
			groupId, displayDate, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_LtD_First(
		long groupId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_LtD_First(
			groupId, displayDate, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD(
		long groupId, Date displayDate) {

		return getPersistence().filterFindByG_LtD(groupId, displayDate);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD(
		long groupId, Date displayDate, int start, int end) {

		return getPersistence().filterFindByG_LtD(
			groupId, displayDate, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD(
		long groupId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_LtD(
			groupId, displayDate, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 */
	public static void removeByG_LtD(long groupId, Date displayDate) {
		getPersistence().removeByG_LtD(groupId, displayDate);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	public static int countByG_LtD(long groupId, Date displayDate) {
		return getPersistence().countByG_LtD(groupId, displayDate);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_LtD(long groupId, Date displayDate) {
		return getPersistence().filterCountByG_LtD(groupId, displayDate);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_S_First(
			long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_S(long groupId, int status) {
		return getPersistence().filterCountByG_S(groupId, status);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_NotS(long groupId, int status) {
		return getPersistence().findByG_NotS(groupId, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_NotS(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_NotS(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_NotS(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_NotS_First(
			long groupId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_NotS_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_NotS_First(
		long groupId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_NotS_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_NotS(
		long groupId, int status) {

		return getPersistence().filterFindByG_NotS(groupId, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_NotS(
		long groupId, int status, int start, int end) {

		return getPersistence().filterFindByG_NotS(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_NotS(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_NotS(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_NotS(long groupId, int status) {
		getPersistence().removeByG_NotS(groupId, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_NotS(long groupId, int status) {
		return getPersistence().countByG_NotS(groupId, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_NotS(long groupId, int status) {
		return getPersistence().filterCountByG_NotS(groupId, status);
	}

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate) {

		return getPersistence().findByC_LtD(companyId, displayDate);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end) {

		return getPersistence().findByC_LtD(companyId, displayDate, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByC_LtD(
			companyId, displayDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD(
		long companyId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_LtD(
			companyId, displayDate, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByC_LtD_First(
			long companyId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByC_LtD_First(
			companyId, displayDate, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByC_LtD_First(
		long companyId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByC_LtD_First(
			companyId, displayDate, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 */
	public static void removeByC_LtD(long companyId, Date displayDate) {
		getPersistence().removeByC_LtD(companyId, displayDate);
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	public static int countByC_LtD(long companyId, Date displayDate) {
		return getPersistence().countByC_LtD(companyId, displayDate);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByC_S_First(
			long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public static void removeByC_S(long companyId, int status) {
		getPersistence().removeByC_S(companyId, status);
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByC_S(long companyId, int status) {
		return getPersistence().countByC_S(companyId, status);
	}

	/**
	 * Returns all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByC_NotS(long companyId, int status) {
		return getPersistence().findByC_NotS(companyId, status);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end) {

		return getPersistence().findByC_NotS(companyId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByC_NotS(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_NotS(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_NotS(
			companyId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByC_NotS_First(
			long companyId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByC_NotS_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByC_NotS_First(
		long companyId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByC_NotS_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public static void removeByC_NotS(long companyId, int status) {
		getPersistence().removeByC_NotS(companyId, status);
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByC_NotS(long companyId, int status) {
		return getPersistence().countByC_NotS(companyId, status);
	}

	/**
	 * Returns all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByLtD_S(Date displayDate, int status) {
		return getPersistence().findByLtD_S(displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return getPersistence().findByLtD_S(displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByLtD_S(Date displayDate, int status) {
		getPersistence().removeByLtD_S(displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByLtD_S(Date displayDate, int status) {
		return getPersistence().countByLtD_S(displayDate, status);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return getPersistence().findByG_U_LtD(groupId, userId, displayDate);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end) {

		return getPersistence().findByG_U_LtD(
			groupId, userId, displayDate, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_U_LtD(
			groupId, userId, displayDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_LtD(
			groupId, userId, displayDate, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_U_LtD_First(
			long groupId, long userId, Date displayDate,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_U_LtD_First(
			groupId, userId, displayDate, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_U_LtD_First(
		long groupId, long userId, Date displayDate,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_U_LtD_First(
			groupId, userId, displayDate, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return getPersistence().filterFindByG_U_LtD(
			groupId, userId, displayDate);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end) {

		return getPersistence().filterFindByG_U_LtD(
			groupId, userId, displayDate, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD(
		long groupId, long userId, Date displayDate, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_U_LtD(
			groupId, userId, displayDate, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 */
	public static void removeByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		getPersistence().removeByG_U_LtD(groupId, userId, displayDate);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries
	 */
	public static int countByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return getPersistence().countByG_U_LtD(groupId, userId, displayDate);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_U_LtD(
		long groupId, long userId, Date displayDate) {

		return getPersistence().filterCountByG_U_LtD(
			groupId, userId, displayDate);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_S(
			groupId, userId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_U_S_First(
			groupId, userId, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_U_S_First(
			groupId, userId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_U_S(
			groupId, userId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_U_S(
			groupId, userId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_S(
			groupId, userId, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	public static void removeByG_U_S(long groupId, long userId, int status) {
		getPersistence().removeByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_U_S(long groupId, long userId, int status) {
		return getPersistence().countByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching blogs entries
	 */
	public static int countByG_U_S(long groupId, long userId, int[] statuses) {
		return getPersistence().countByG_U_S(groupId, userId, statuses);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_U_S(
		long groupId, long userId, int status) {

		return getPersistence().filterCountByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_U_S(
		long groupId, long userId, int[] statuses) {

		return getPersistence().filterCountByG_U_S(groupId, userId, statuses);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status) {

		return getPersistence().findByG_U_NotS(groupId, userId, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return getPersistence().findByG_U_NotS(
			groupId, userId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_U_NotS(
			groupId, userId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_NotS(
			groupId, userId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_U_NotS_First(
			long groupId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_U_NotS_First(
			groupId, userId, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_U_NotS_First(
		long groupId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_U_NotS_First(
			groupId, userId, status, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status) {

		return getPersistence().filterFindByG_U_NotS(groupId, userId, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end) {

		return getPersistence().filterFindByG_U_NotS(
			groupId, userId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_NotS(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_U_NotS(
			groupId, userId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	public static void removeByG_U_NotS(long groupId, long userId, int status) {
		getPersistence().removeByG_U_NotS(groupId, userId, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_U_NotS(long groupId, long userId, int status) {
		return getPersistence().countByG_U_NotS(groupId, userId, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_U_NotS(
		long groupId, long userId, int status) {

		return getPersistence().filterCountByG_U_NotS(groupId, userId, status);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_D_S(
			groupId, displayDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_D_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_D_S_First(
			groupId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_D_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_D_S_First(
			groupId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_D_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_D_S(
			groupId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByG_D_S(
		long groupId, Date displayDate, int status) {

		getPersistence().removeByG_D_S(groupId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_D_S(long groupId, Date displayDate, int status) {
		return getPersistence().countByG_D_S(groupId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_D_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().filterCountByG_D_S(
			groupId, displayDate, status);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().findByG_GtD_S(groupId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return getPersistence().findByG_GtD_S(
			groupId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_GtD_S(
			groupId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_GtD_S(
			groupId, displayDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_GtD_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_GtD_S_First(
			groupId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_GtD_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_GtD_S_First(
			groupId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().filterFindByG_GtD_S(
			groupId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return getPersistence().filterFindByG_GtD_S(
			groupId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_GtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_GtD_S(
			groupId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByG_GtD_S(
		long groupId, Date displayDate, int status) {

		getPersistence().removeByG_GtD_S(groupId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().countByG_GtD_S(groupId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &gt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_GtD_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().filterCountByG_GtD_S(
			groupId, displayDate, status);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().findByG_LtD_S(groupId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return getPersistence().findByG_LtD_S(
			groupId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_LtD_S(
			groupId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LtD_S(
			groupId, displayDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_LtD_S_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_LtD_S_First(
			groupId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_LtD_S_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_LtD_S_First(
			groupId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().filterFindByG_LtD_S(
			groupId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return getPersistence().filterFindByG_LtD_S(
			groupId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_LtD_S(
			groupId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByG_LtD_S(
		long groupId, Date displayDate, int status) {

		getPersistence().removeByG_LtD_S(groupId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().countByG_LtD_S(groupId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_LtD_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().filterCountByG_LtD_S(
			groupId, displayDate, status);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return getPersistence().findByG_LtD_NotS(groupId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end) {

		return getPersistence().findByG_LtD_NotS(
			groupId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_LtD_NotS(
			groupId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LtD_NotS(
			groupId, displayDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_LtD_NotS_First(
			long groupId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_LtD_NotS_First(
			groupId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_LtD_NotS_First(
		long groupId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_LtD_NotS_First(
			groupId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return getPersistence().filterFindByG_LtD_NotS(
			groupId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end) {

		return getPersistence().filterFindByG_LtD_NotS(
			groupId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_LtD_NotS(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_LtD_NotS(
			groupId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		getPersistence().removeByG_LtD_NotS(groupId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return getPersistence().countByG_LtD_NotS(groupId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_LtD_NotS(
		long groupId, Date displayDate, int status) {

		return getPersistence().filterCountByG_LtD_NotS(
			groupId, displayDate, status);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U_S(
			companyId, userId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByC_U_S_First(
			long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByC_U_S_First(
			companyId, userId, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByC_U_S_First(
		long companyId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByC_U_S_First(
			companyId, userId, status, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 */
	public static void removeByC_U_S(long companyId, long userId, int status) {
		getPersistence().removeByC_U_S(companyId, userId, status);
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByC_U_S(long companyId, long userId, int status) {
		return getPersistence().countByC_U_S(companyId, userId, status);
	}

	/**
	 * Returns all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status) {

		return getPersistence().findByC_U_NotS(companyId, userId, status);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end) {

		return getPersistence().findByC_U_NotS(
			companyId, userId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByC_U_NotS(
			companyId, userId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_U_NotS(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_U_NotS(
			companyId, userId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByC_U_NotS_First(
			long companyId, long userId, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByC_U_NotS_First(
			companyId, userId, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByC_U_NotS_First(
		long companyId, long userId, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByC_U_NotS_First(
			companyId, userId, status, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 */
	public static void removeByC_U_NotS(
		long companyId, long userId, int status) {

		getPersistence().removeByC_U_NotS(companyId, userId, status);
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and userId = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByC_U_NotS(long companyId, long userId, int status) {
		return getPersistence().countByC_U_NotS(companyId, userId, status);
	}

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status) {

		return getPersistence().findByC_LtD_S(companyId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end) {

		return getPersistence().findByC_LtD_S(
			companyId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByC_LtD_S(
			companyId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD_S(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_LtD_S(
			companyId, displayDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByC_LtD_S_First(
			long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByC_LtD_S_First(
			companyId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByC_LtD_S_First(
		long companyId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByC_LtD_S_First(
			companyId, displayDate, status, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByC_LtD_S(
		long companyId, Date displayDate, int status) {

		getPersistence().removeByC_LtD_S(companyId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByC_LtD_S(
		long companyId, Date displayDate, int status) {

		return getPersistence().countByC_LtD_S(companyId, displayDate, status);
	}

	/**
	 * Returns all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status) {

		return getPersistence().findByC_LtD_NotS(
			companyId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end) {

		return getPersistence().findByC_LtD_NotS(
			companyId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByC_LtD_NotS(
			companyId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_LtD_NotS(
		long companyId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_LtD_NotS(
			companyId, displayDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByC_LtD_NotS_First(
			long companyId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByC_LtD_NotS_First(
			companyId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByC_LtD_NotS_First(
		long companyId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByC_LtD_NotS_First(
			companyId, displayDate, status, orderByComparator);
	}

	/**
	 * Removes all the blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByC_LtD_NotS(
		long companyId, Date displayDate, int status) {

		getPersistence().removeByC_LtD_NotS(companyId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where companyId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByC_LtD_NotS(
		long companyId, Date displayDate, int status) {

		return getPersistence().countByC_LtD_NotS(
			companyId, displayDate, status);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return getPersistence().findByG_U_LtD_S(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return getPersistence().findByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_U_LtD_S_First(
			long groupId, long userId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_U_LtD_S_First(
			groupId, userId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_U_LtD_S_First(
		long groupId, long userId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_U_LtD_S_First(
			groupId, userId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return getPersistence().filterFindByG_U_LtD_S(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return getPersistence().filterFindByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_U_LtD_S(
			groupId, userId, displayDate, status, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		getPersistence().removeByG_U_LtD_S(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return getPersistence().countByG_U_LtD_S(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_U_LtD_S(
		long groupId, long userId, Date displayDate, int status) {

		return getPersistence().filterCountByG_U_LtD_S(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return getPersistence().findByG_U_LtD_NotS(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return getPersistence().findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByG_U_LtD_NotS_First(
			long groupId, long userId, Date displayDate, int status,
			OrderByComparator<BlogsEntry> orderByComparator)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByG_U_LtD_NotS_First(
			groupId, userId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first blogs entry in the ordered set where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_U_LtD_NotS_First(
		long groupId, long userId, Date displayDate, int status,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().fetchByG_U_LtD_NotS_First(
			groupId, userId, displayDate, status, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return getPersistence().filterFindByG_U_LtD_NotS(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end) {

		return getPersistence().filterFindByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries that the user has permissions to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status, int start,
		int end, OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().filterFindByG_U_LtD_NotS(
			groupId, userId, displayDate, status, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		getPersistence().removeByG_U_LtD_NotS(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries
	 */
	public static int countByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return getPersistence().countByG_U_LtD_NotS(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns the number of blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and displayDate &lt; &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching blogs entries that the user has permission to view
	 */
	public static int filterCountByG_U_LtD_NotS(
		long groupId, long userId, Date displayDate, int status) {

		return getPersistence().filterCountByG_U_LtD_NotS(
			groupId, userId, displayDate, status);
	}

	/**
	 * Returns the blogs entry where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching blogs entry
	 * @throws NoSuchEntryException if a matching blogs entry could not be found
	 */
	public static BlogsEntry findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the blogs entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the blogs entry where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the blogs entry that was removed
	 */
	public static BlogsEntry removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of blogs entries where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching blogs entries
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new blogs entry with the primary key. Does not add the blogs entry to the database.
	 *
	 * @param entryId the primary key for the new blogs entry
	 * @return the new blogs entry
	 */
	public static BlogsEntry create(long entryId) {
		return getPersistence().create(entryId);
	}

	/**
	 * Removes the blogs entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry that was removed
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	public static BlogsEntry remove(long entryId)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().remove(entryId);
	}

	public static BlogsEntry updateImpl(BlogsEntry blogsEntry) {
		return getPersistence().updateImpl(blogsEntry);
	}

	/**
	 * Returns the blogs entry with the primary key or throws a <code>NoSuchEntryException</code> if it could not be found.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry
	 * @throws NoSuchEntryException if a blogs entry with the primary key could not be found
	 */
	public static BlogsEntry findByPrimaryKey(long entryId)
		throws com.liferay.blogs.exception.NoSuchEntryException {

		return getPersistence().findByPrimaryKey(entryId);
	}

	/**
	 * Returns the blogs entry with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param entryId the primary key of the blogs entry
	 * @return the blogs entry, or <code>null</code> if a blogs entry with the primary key could not be found
	 */
	public static BlogsEntry fetchByPrimaryKey(long entryId) {
		return getPersistence().fetchByPrimaryKey(entryId);
	}

	/**
	 * Returns the blogs entry where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the blogs entry where groupId = &#63; and urlTitle = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByG_UT(long groupId, String urlTitle) {
		return getPersistence().fetchByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns the blogs entry where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching blogs entry, or <code>null</code> if a matching blogs entry could not be found
	 */
	public static BlogsEntry fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the blogs entries where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the blogs entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the blogs entries where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_S(long groupId, int status) {
		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_S(long groupId, int status) {
		return getPersistence().filterFindByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().filterFindByG_S(groupId, status, start, end);
	}

	/**
	 * Returns all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByC_S(long companyId, int status) {
		return getPersistence().findByC_S(companyId, status);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_S(
		long companyId, int status, int start, int end) {

		return getPersistence().findByC_S(companyId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int status) {

		return getPersistence().findByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return getPersistence().findByG_U_S(
			groupId, userId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_U_S(
			groupId, userId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses) {

		return getPersistence().findByG_U_S(groupId, userId, statuses);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return getPersistence().findByG_U_S(
			groupId, userId, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_U_S(
			groupId, userId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int status) {

		return getPersistence().filterFindByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return getPersistence().filterFindByG_U_S(
			groupId, userId, status, start, end);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int[] statuses) {

		return getPersistence().filterFindByG_U_S(groupId, userId, statuses);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and userId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_U_S(
		long groupId, long userId, int[] statuses, int start, int end) {

		return getPersistence().filterFindByG_U_S(
			groupId, userId, statuses, start, end);
	}

	/**
	 * Returns all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().findByG_D_S(groupId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return getPersistence().findByG_D_S(
			groupId, displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByG_D_S(
		long groupId, Date displayDate, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByG_D_S(
			groupId, displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the blogs entries that the user has permission to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_D_S(
		long groupId, Date displayDate, int status) {

		return getPersistence().filterFindByG_D_S(groupId, displayDate, status);
	}

	/**
	 * Returns a range of all the blogs entries that the user has permission to view where groupId = &#63; and displayDate = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries that the user has permission to view
	 */
	public static List<BlogsEntry> filterFindByG_D_S(
		long groupId, Date displayDate, int status, int start, int end) {

		return getPersistence().filterFindByG_D_S(
			groupId, displayDate, status, start, end);
	}

	/**
	 * Returns all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching blogs entries
	 */
	public static List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status) {

		return getPersistence().findByC_U_S(companyId, userId, status);
	}

	/**
	 * Returns a range of all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @return the range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status, int start, int end) {

		return getPersistence().findByC_U_S(
			companyId, userId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the blogs entries where companyId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.blogs.model.impl.BlogsEntryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of blogs entries
	 * @param end the upper bound of the range of blogs entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching blogs entries
	 */
	public static List<BlogsEntry> findByC_U_S(
		long companyId, long userId, int status, int start, int end,
		OrderByComparator<BlogsEntry> orderByComparator) {

		return getPersistence().findByC_U_S(
			companyId, userId, status, start, end, orderByComparator);
	}

	public static BlogsEntryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(BlogsEntryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile BlogsEntryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:770097156