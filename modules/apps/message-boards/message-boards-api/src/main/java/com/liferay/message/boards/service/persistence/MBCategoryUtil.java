/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence;

import com.liferay.message.boards.model.MBCategory;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the message boards category service. This utility wraps <code>com.liferay.message.boards.service.persistence.impl.MBCategoryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MBCategoryPersistence
 * @generated
 */
public class MBCategoryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<MBCategory> mbCategories) {
		getPersistence().cacheResult(mbCategories);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(MBCategory mbCategory) {
		getPersistence().cacheResult(mbCategory);
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
	public static void clearCache(MBCategory mbCategory) {
		getPersistence().clearCache(mbCategory);
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
	public static Map<Serializable, MBCategory> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<MBCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<MBCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<MBCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static MBCategory update(MBCategory mbCategory) {
		return getPersistence().update(mbCategory);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static MBCategory update(
		MBCategory mbCategory, ServiceContext serviceContext) {

		return getPersistence().update(mbCategory, serviceContext);
	}

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByUuid_First(
			String uuid, OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByUuid_First(
		String uuid, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of message boards categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards categories
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the message boards category where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByUUID_G(String uuid, long groupId)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the message boards category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the message boards category where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards category that was removed
	 */
	public static MBCategory removeByUUID_G(String uuid, long groupId)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of message boards categories where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards categories
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByGroupId_First(
			long groupId, OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByGroupId_First(
		long groupId, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, parentCategoryId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByG_P_First(
			long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByG_P_First(
			groupId, parentCategoryId, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByG_P_First(
		long groupId, long parentCategoryId,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByG_P_First(
			groupId, parentCategoryId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByG_P(
			groupId, parentCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P(
		long groupId, long[] parentCategoryIds, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByG_P(
			groupId, parentCategoryIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P(
		long groupId, long[] parentCategoryIds, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, parentCategoryIds, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	public static void removeByG_P(long groupId, long parentCategoryId) {
		getPersistence().removeByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories
	 */
	public static int countByG_P(long groupId, long parentCategoryId) {
		return getPersistence().countByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories
	 */
	public static int countByG_P(long groupId, long[] parentCategoryIds) {
		return getPersistence().countByG_P(groupId, parentCategoryIds);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByG_P(long groupId, long parentCategoryId) {
		return getPersistence().filterCountByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByG_P(long groupId, long[] parentCategoryIds) {
		return getPersistence().filterCountByG_P(groupId, parentCategoryIds);
	}

	/**
	 * Returns the message boards category where groupId = &#63; and friendlyURL = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByG_F(long groupId, String friendlyURL)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByG_F(groupId, friendlyURL);
	}

	/**
	 * Returns the message boards category where groupId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByG_F(
		long groupId, String friendlyURL, boolean useFinderCache) {

		return getPersistence().fetchByG_F(
			groupId, friendlyURL, useFinderCache);
	}

	/**
	 * Removes the message boards category where groupId = &#63; and friendlyURL = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the message boards category that was removed
	 */
	public static MBCategory removeByG_F(long groupId, String friendlyURL)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().removeByG_F(groupId, friendlyURL);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and friendlyURL = &#63;.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the number of matching message boards categories
	 */
	public static int countByG_F(long groupId, String friendlyURL) {
		return getPersistence().countByG_F(groupId, friendlyURL);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByG_S_First(
			long groupId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByG_S(long groupId, int status) {
		return getPersistence().filterCountByG_S(groupId, status);
	}

	/**
	 * Returns an ordered range of all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByC_S_First(
			long companyId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Removes all the message boards categories where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public static void removeByC_S(long companyId, int status) {
		getPersistence().removeByC_S(companyId, status);
	}

	/**
	 * Returns the number of message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	public static int countByC_S(long companyId, int status) {
		return getPersistence().countByC_S(companyId, status);
	}

	/**
	 * Returns all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return getPersistence().findByNotC_G_P(
			categoryId, groupId, parentCategoryId);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end) {

		return getPersistence().findByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByNotC_G_P_First(
			long categoryId, long groupId, long parentCategoryId,
			OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByNotC_G_P_First(
			categoryId, groupId, parentCategoryId, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByNotC_G_P_First(
		long categoryId, long groupId, long parentCategoryId,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByNotC_G_P_First(
			categoryId, groupId, parentCategoryId, orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return getPersistence().filterFindByNotC_G_P(
			categoryId, groupId, parentCategoryId);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end) {

		return getPersistence().filterFindByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByNotC_G_P(
			categoryId, groupId, parentCategoryId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return getPersistence().filterFindByNotC_G_P(
			categoryIds, groupId, parentCategoryIds);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end) {

		return getPersistence().filterFindByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return getPersistence().findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end) {

		return getPersistence().findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int start,
		int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByNotC_G_P(
			categoryIds, groupId, parentCategoryIds, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 */
	public static void removeByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		getPersistence().removeByNotC_G_P(
			categoryId, groupId, parentCategoryId);
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories
	 */
	public static int countByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return getPersistence().countByNotC_G_P(
			categoryId, groupId, parentCategoryId);
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories
	 */
	public static int countByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return getPersistence().countByNotC_G_P(
			categoryIds, groupId, parentCategoryIds);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByNotC_G_P(
		long categoryId, long groupId, long parentCategoryId) {

		return getPersistence().filterCountByNotC_G_P(
			categoryId, groupId, parentCategoryId);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByNotC_G_P(
		long[] categoryIds, long groupId, long[] parentCategoryIds) {

		return getPersistence().filterCountByNotC_G_P(
			categoryIds, groupId, parentCategoryIds);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_S(
			groupId, parentCategoryId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByG_P_S_First(
			long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByG_P_S_First(
			groupId, parentCategoryId, status, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByG_P_S_First(
		long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByG_P_S_First(
			groupId, parentCategoryId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentCategoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentCategoryIds, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_S(
			groupId, parentCategoryIds, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	public static void removeByG_P_S(
		long groupId, long parentCategoryId, int status) {

		getPersistence().removeByG_P_S(groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	public static int countByG_P_S(
		long groupId, long parentCategoryId, int status) {

		return getPersistence().countByG_P_S(groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	public static int countByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		return getPersistence().countByG_P_S(
			groupId, parentCategoryIds, status);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByG_P_S(
		long groupId, long parentCategoryId, int status) {

		return getPersistence().filterCountByG_P_S(
			groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		return getPersistence().filterCountByG_P_S(
			groupId, parentCategoryIds, status);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return getPersistence().findByG_P_NotS(
			groupId, parentCategoryId, status);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return getPersistence().findByG_P_NotS(
			groupId, parentCategoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByG_P_NotS(
			groupId, parentCategoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_NotS(
			groupId, parentCategoryId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByG_P_NotS_First(
			long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByG_P_NotS_First(
			groupId, parentCategoryId, status, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByG_P_NotS_First(
		long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByG_P_NotS_First(
			groupId, parentCategoryId, status, orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return getPersistence().filterFindByG_P_NotS(
			groupId, parentCategoryId, status);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return getPersistence().filterFindByG_P_NotS(
			groupId, parentCategoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_NotS(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByG_P_NotS(
			groupId, parentCategoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return getPersistence().filterFindByG_P_NotS(
			groupId, parentCategoryIds, status);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return getPersistence().filterFindByG_P_NotS(
			groupId, parentCategoryIds, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return getPersistence().findByG_P_NotS(
			groupId, parentCategoryIds, status);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return getPersistence().findByG_P_NotS(
			groupId, parentCategoryIds, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_NotS(
			groupId, parentCategoryIds, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	public static void removeByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		getPersistence().removeByG_P_NotS(groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	public static int countByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return getPersistence().countByG_P_NotS(
			groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	public static int countByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return getPersistence().countByG_P_NotS(
			groupId, parentCategoryIds, status);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByG_P_NotS(
		long groupId, long parentCategoryId, int status) {

		return getPersistence().filterCountByG_P_NotS(
			groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByG_P_NotS(
		long groupId, long[] parentCategoryIds, int status) {

		return getPersistence().filterCountByG_P_NotS(
			groupId, parentCategoryIds, status);
	}

	/**
	 * Returns all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return getPersistence().findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end) {

		return getPersistence().findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByNotC_G_P_S_First(
			long categoryId, long groupId, long parentCategoryId, int status,
			OrderByComparator<MBCategory> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByNotC_G_P_S_First(
			categoryId, groupId, parentCategoryId, status, orderByComparator);
	}

	/**
	 * Returns the first message boards category in the ordered set where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByNotC_G_P_S_First(
		long categoryId, long groupId, long parentCategoryId, int status,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().fetchByNotC_G_P_S_First(
			categoryId, groupId, parentCategoryId, status, orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return getPersistence().filterFindByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end) {

		return getPersistence().filterFindByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permissions to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return getPersistence().filterFindByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end) {

		return getPersistence().filterFindByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().filterFindByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return getPersistence().findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status);
	}

	/**
	 * Returns a range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end) {

		return getPersistence().findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds, int status,
		int start, int end, OrderByComparator<MBCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63; from the database.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 */
	public static void removeByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		getPersistence().removeByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	public static int countByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return getPersistence().countByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories
	 */
	public static int countByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return getPersistence().countByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; &#63; and groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param categoryId the category ID
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByNotC_G_P_S(
		long categoryId, long groupId, long parentCategoryId, int status) {

		return getPersistence().filterCountByNotC_G_P_S(
			categoryId, groupId, parentCategoryId, status);
	}

	/**
	 * Returns the number of message boards categories that the user has permission to view where categoryId &ne; all &#63; and groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param categoryIds the category IDs
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the number of matching message boards categories that the user has permission to view
	 */
	public static int filterCountByNotC_G_P_S(
		long[] categoryIds, long groupId, long[] parentCategoryIds,
		int status) {

		return getPersistence().filterCountByNotC_G_P_S(
			categoryIds, groupId, parentCategoryIds, status);
	}

	/**
	 * Returns the message boards category where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message boards category
	 * @throws NoSuchCategoryException if a matching message boards category could not be found
	 */
	public static MBCategory findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the message boards category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the message boards category where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the message boards category that was removed
	 */
	public static MBCategory removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of message boards categories where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching message boards categories
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new message boards category with the primary key. Does not add the message boards category to the database.
	 *
	 * @param categoryId the primary key for the new message boards category
	 * @return the new message boards category
	 */
	public static MBCategory create(long categoryId) {
		return getPersistence().create(categoryId);
	}

	/**
	 * Removes the message boards category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category that was removed
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	public static MBCategory remove(long categoryId)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().remove(categoryId);
	}

	public static MBCategory updateImpl(MBCategory mbCategory) {
		return getPersistence().updateImpl(mbCategory);
	}

	/**
	 * Returns the message boards category with the primary key or throws a <code>NoSuchCategoryException</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category
	 * @throws NoSuchCategoryException if a message boards category with the primary key could not be found
	 */
	public static MBCategory findByPrimaryKey(long categoryId)
		throws com.liferay.message.boards.exception.NoSuchCategoryException {

		return getPersistence().findByPrimaryKey(categoryId);
	}

	/**
	 * Returns the message boards category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param categoryId the primary key of the message boards category
	 * @return the message boards category, or <code>null</code> if a message boards category with the primary key could not be found
	 */
	public static MBCategory fetchByPrimaryKey(long categoryId) {
		return getPersistence().fetchByPrimaryKey(categoryId);
	}

	/**
	 * Returns the message boards category where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the message boards category where groupId = &#63; and friendlyURL = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param friendlyURL the friendly url
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByG_F(long groupId, String friendlyURL) {
		return getPersistence().fetchByG_F(groupId, friendlyURL);
	}

	/**
	 * Returns the message boards category where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message boards category, or <code>null</code> if a matching message boards category could not be found
	 */
	public static MBCategory fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the message boards categories where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the message boards categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByG_P(
		long groupId, long parentCategoryId) {

		return getPersistence().findByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end) {

		return getPersistence().findByG_P(
			groupId, parentCategoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P(
		long groupId, long parentCategoryId, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, parentCategoryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByG_P(
		long groupId, long[] parentCategoryIds) {

		return getPersistence().findByG_P(groupId, parentCategoryIds);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P(
		long groupId, long[] parentCategoryIds, int start, int end) {

		return getPersistence().findByG_P(
			groupId, parentCategoryIds, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P(
		long groupId, long[] parentCategoryIds, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, parentCategoryIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P(
		long groupId, long parentCategoryId) {

		return getPersistence().filterFindByG_P(groupId, parentCategoryId);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P(
		long groupId, long parentCategoryId, int start, int end) {

		return getPersistence().filterFindByG_P(
			groupId, parentCategoryId, start, end);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P(
		long groupId, long[] parentCategoryIds) {

		return getPersistence().filterFindByG_P(groupId, parentCategoryIds);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P(
		long groupId, long[] parentCategoryIds, int start, int end) {

		return getPersistence().filterFindByG_P(
			groupId, parentCategoryIds, start, end);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByG_S(long groupId, int status) {
		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_S(long groupId, int status) {
		return getPersistence().filterFindByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().filterFindByG_S(groupId, status, start, end);
	}

	/**
	 * Returns all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByC_S(long companyId, int status) {
		return getPersistence().findByC_S(companyId, status);
	}

	/**
	 * Returns a range of all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByC_S(
		long companyId, int status, int start, int end) {

		return getPersistence().findByC_S(companyId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status) {

		return getPersistence().findByG_P_S(groupId, parentCategoryId, status);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return getPersistence().findByG_P_S(
			groupId, parentCategoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByG_P_S(
			groupId, parentCategoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories
	 */
	public static List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		return getPersistence().findByG_P_S(groupId, parentCategoryIds, status);
	}

	/**
	 * Returns a range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return getPersistence().findByG_P_S(
			groupId, parentCategoryIds, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards categories where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards categories
	 */
	public static List<MBCategory> findByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start, int end,
		OrderByComparator<MBCategory> orderByComparator) {

		return getPersistence().findByG_P_S(
			groupId, parentCategoryIds, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_S(
		long groupId, long parentCategoryId, int status) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentCategoryId, status);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryId the parent category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_S(
		long groupId, long parentCategoryId, int status, int start, int end) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentCategoryId, status, start, end);
	}

	/**
	 * Returns all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @return the matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_S(
		long groupId, long[] parentCategoryIds, int status) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentCategoryIds, status);
	}

	/**
	 * Returns a range of all the message boards categories that the user has permission to view where groupId = &#63; and parentCategoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentCategoryIds the parent category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards categories
	 * @param end the upper bound of the range of message boards categories (not inclusive)
	 * @return the range of matching message boards categories that the user has permission to view
	 */
	public static List<MBCategory> filterFindByG_P_S(
		long groupId, long[] parentCategoryIds, int status, int start,
		int end) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentCategoryIds, status, start, end);
	}

	public static MBCategoryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(MBCategoryPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile MBCategoryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-797228876