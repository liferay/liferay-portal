/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence;

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the layout page template collection service. This utility wraps <code>com.liferay.layout.page.template.service.persistence.impl.LayoutPageTemplateCollectionPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateCollectionPersistence
 * @generated
 */
public class LayoutPageTemplateCollectionUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<LayoutPageTemplateCollection> layoutPageTemplateCollections) {

		getPersistence().cacheResult(layoutPageTemplateCollections);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		getPersistence().cacheResult(layoutPageTemplateCollection);
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
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		getPersistence().clearCache(layoutPageTemplateCollection);
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
	public static Map<Serializable, LayoutPageTemplateCollection>
		fetchByPrimaryKeys(Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<LayoutPageTemplateCollection> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<LayoutPageTemplateCollection> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<LayoutPageTemplateCollection> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static LayoutPageTemplateCollection update(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		return getPersistence().update(layoutPageTemplateCollection);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static LayoutPageTemplateCollection update(
		LayoutPageTemplateCollection layoutPageTemplateCollection,
		ServiceContext serviceContext) {

		return getPersistence().update(
			layoutPageTemplateCollection, serviceContext);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByUuid_First(
			String uuid,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByUuid_First(
		String uuid,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of layout page template collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching layout page template collections
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByUUID_G(
			String uuid, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the layout page template collection where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the layout page template collection that was removed
	 */
	public static LayoutPageTemplateCollection removeByUUID_G(
			String uuid, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of layout page template collections where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching layout page template collections
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByGroupId_First(
			long groupId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByGroupId_First(
		long groupId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByG_P_First(
			long groupId, long parentLayoutPageTemplateCollectionId,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByG_P_First(
			groupId, parentLayoutPageTemplateCollectionId, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByG_P_First(
		long groupId, long parentLayoutPageTemplateCollectionId,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().fetchByG_P_First(
			groupId, parentLayoutPageTemplateCollectionId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().filterFindByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 */
	public static void removeByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		getPersistence().removeByG_P(
			groupId, parentLayoutPageTemplateCollectionId);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the number of matching layout page template collections
	 */
	public static int countByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return getPersistence().countByG_P(
			groupId, parentLayoutPageTemplateCollectionId);
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public static int filterCountByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return getPersistence().filterCountByG_P(
			groupId, parentLayoutPageTemplateCollectionId);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_T(
			groupId, type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByG_T_First(
			long groupId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByG_T_First(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByG_T_First(
		long groupId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().fetchByG_T_First(
			groupId, type, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().filterFindByG_T(
			groupId, type, start, end, orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 */
	public static void removeByG_T(long groupId, int type) {
		getPersistence().removeByG_T(groupId, type);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public static int countByG_T(long groupId, int type) {
		return getPersistence().countByG_T(groupId, type);
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public static int filterCountByG_T(long groupId, int type) {
		return getPersistence().filterCountByG_T(groupId, type);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByG_P_T_First(
			long groupId, long parentLayoutPageTemplateCollectionId, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByG_P_T_First(
			groupId, parentLayoutPageTemplateCollectionId, type,
			orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByG_P_T_First(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().fetchByG_P_T_First(
			groupId, parentLayoutPageTemplateCollectionId, type,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().filterFindByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 */
	public static void removeByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		getPersistence().removeByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public static int countByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return getPersistence().countByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public static int filterCountByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return getPersistence().filterCountByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByG_LPTCK_T(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByG_LPTCK_T(
			groupId, layoutPageTemplateCollectionKey, type);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type,
		boolean useFinderCache) {

		return getPersistence().fetchByG_LPTCK_T(
			groupId, layoutPageTemplateCollectionKey, type, useFinderCache);
	}

	/**
	 * Removes the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the layout page template collection that was removed
	 */
	public static LayoutPageTemplateCollection removeByG_LPTCK_T(
			long groupId, String layoutPageTemplateCollectionKey, int type)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().removeByG_LPTCK_T(
			groupId, layoutPageTemplateCollectionKey, type);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public static int countByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type) {

		return getPersistence().countByG_LPTCK_T(
			groupId, layoutPageTemplateCollectionKey, type);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type) {

		return getPersistence().findByG_LikeN_T(groupId, name, type);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end) {

		return getPersistence().findByG_LikeN_T(
			groupId, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().findByG_LikeN_T(
			groupId, name, type, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeN_T(
			groupId, name, type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByG_LikeN_T_First(
			long groupId, String name, int type,
			OrderByComparator<LayoutPageTemplateCollection> orderByComparator)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByG_LikeN_T_First(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns the first layout page template collection in the ordered set where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByG_LikeN_T_First(
		long groupId, String name, int type,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().fetchByG_LikeN_T_First(
			groupId, name, type, orderByComparator);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type) {

		return getPersistence().filterFindByG_LikeN_T(groupId, name, type);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type, int start, int end) {

		return getPersistence().filterFindByG_LikeN_T(
			groupId, name, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template collections that the user has permissions to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_LikeN_T(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().filterFindByG_LikeN_T(
			groupId, name, type, start, end, orderByComparator);
	}

	/**
	 * Removes all the layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 */
	public static void removeByG_LikeN_T(long groupId, String name, int type) {
		getPersistence().removeByG_LikeN_T(groupId, name, type);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public static int countByG_LikeN_T(long groupId, String name, int type) {
		return getPersistence().countByG_LikeN_T(groupId, name, type);
	}

	/**
	 * Returns the number of layout page template collections that the user has permission to view where groupId = &#63; and name LIKE &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections that the user has permission to view
	 */
	public static int filterCountByG_LikeN_T(
		long groupId, String name, int type) {

		return getPersistence().filterCountByG_LikeN_T(groupId, name, type);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByG_P_N_T(
			long groupId, long parentLayoutPageTemplateCollectionId,
			String name, int type)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByG_P_N_T(
			groupId, parentLayoutPageTemplateCollectionId, name, type);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type, boolean useFinderCache) {

		return getPersistence().fetchByG_P_N_T(
			groupId, parentLayoutPageTemplateCollectionId, name, type,
			useFinderCache);
	}

	/**
	 * Removes the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the layout page template collection that was removed
	 */
	public static LayoutPageTemplateCollection removeByG_P_N_T(
			long groupId, long parentLayoutPageTemplateCollectionId,
			String name, int type)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().removeByG_P_N_T(
			groupId, parentLayoutPageTemplateCollectionId, name, type);
	}

	/**
	 * Returns the number of layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the number of matching layout page template collections
	 */
	public static int countByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type) {

		return getPersistence().countByG_P_N_T(
			groupId, parentLayoutPageTemplateCollectionId, name, type);
	}

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the layout page template collection that was removed
	 */
	public static LayoutPageTemplateCollection removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of layout page template collections where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching layout page template collections
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new layout page template collection with the primary key. Does not add the layout page template collection to the database.
	 *
	 * @param layoutPageTemplateCollectionId the primary key for the new layout page template collection
	 * @return the new layout page template collection
	 */
	public static LayoutPageTemplateCollection create(
		long layoutPageTemplateCollectionId) {

		return getPersistence().create(layoutPageTemplateCollectionId);
	}

	/**
	 * Removes the layout page template collection with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection that was removed
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	public static LayoutPageTemplateCollection remove(
			long layoutPageTemplateCollectionId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().remove(layoutPageTemplateCollectionId);
	}

	public static LayoutPageTemplateCollection updateImpl(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		return getPersistence().updateImpl(layoutPageTemplateCollection);
	}

	/**
	 * Returns the layout page template collection with the primary key or throws a <code>NoSuchPageTemplateCollectionException</code> if it could not be found.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection
	 * @throws NoSuchPageTemplateCollectionException if a layout page template collection with the primary key could not be found
	 */
	public static LayoutPageTemplateCollection findByPrimaryKey(
			long layoutPageTemplateCollectionId)
		throws com.liferay.layout.page.template.exception.
			NoSuchPageTemplateCollectionException {

		return getPersistence().findByPrimaryKey(
			layoutPageTemplateCollectionId);
	}

	/**
	 * Returns the layout page template collection with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param layoutPageTemplateCollectionId the primary key of the layout page template collection
	 * @return the layout page template collection, or <code>null</code> if a layout page template collection with the primary key could not be found
	 */
	public static LayoutPageTemplateCollection fetchByPrimaryKey(
		long layoutPageTemplateCollectionId) {

		return getPersistence().fetchByPrimaryKey(
			layoutPageTemplateCollectionId);
	}

	/**
	 * Returns the layout page template collection where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByUUID_G(
		String uuid, long groupId) {

		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and layoutPageTemplateCollectionKey = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param layoutPageTemplateCollectionKey the layout page template collection key
	 * @param type the type
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByG_LPTCK_T(
		long groupId, String layoutPageTemplateCollectionKey, int type) {

		return getPersistence().fetchByG_LPTCK_T(
			groupId, layoutPageTemplateCollectionKey, type);
	}

	/**
	 * Returns the layout page template collection where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and name = &#63; and type = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param name the name
	 * @param type the type
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByG_P_N_T(
		long groupId, long parentLayoutPageTemplateCollectionId, String name,
		int type) {

		return getPersistence().fetchByG_P_N_T(
			groupId, parentLayoutPageTemplateCollectionId, name, type);
	}

	/**
	 * Returns the layout page template collection where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching layout page template collection, or <code>null</code> if a matching layout page template collection could not be found
	 */
	public static LayoutPageTemplateCollection fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the layout page template collections where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByGroupId(
		long groupId) {

		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId) {

		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return getPersistence().findByG_P(
			groupId, parentLayoutPageTemplateCollectionId);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end) {

		return getPersistence().findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().findByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId) {

		return getPersistence().filterFindByG_P(
			groupId, parentLayoutPageTemplateCollectionId);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_P(
		long groupId, long parentLayoutPageTemplateCollectionId, int start,
		int end) {

		return getPersistence().filterFindByG_P(
			groupId, parentLayoutPageTemplateCollectionId, start, end);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type) {

		return getPersistence().findByG_T(groupId, type);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end) {

		return getPersistence().findByG_T(groupId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_T(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().findByG_T(
			groupId, type, start, end, orderByComparator);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type) {

		return getPersistence().filterFindByG_T(groupId, type);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_T(
		long groupId, int type, int start, int end) {

		return getPersistence().filterFindByG_T(groupId, type, start, end);
	}

	/**
	 * Returns all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return getPersistence().findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns a range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end) {

		return getPersistence().findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end);
	}

	/**
	 * Returns an ordered range of all the layout page template collections where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching layout page template collections
	 */
	public static List<LayoutPageTemplateCollection> findByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateCollection> orderByComparator) {

		return getPersistence().findByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @return the matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type) {

		return getPersistence().filterFindByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type);
	}

	/**
	 * Returns a range of all the layout page template collections that the user has permission to view where groupId = &#63; and parentLayoutPageTemplateCollectionId = &#63; and type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.layout.page.template.model.impl.LayoutPageTemplateCollectionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentLayoutPageTemplateCollectionId the parent layout page template collection ID
	 * @param type the type
	 * @param start the lower bound of the range of layout page template collections
	 * @param end the upper bound of the range of layout page template collections (not inclusive)
	 * @return the range of matching layout page template collections that the user has permission to view
	 */
	public static List<LayoutPageTemplateCollection> filterFindByG_P_T(
		long groupId, long parentLayoutPageTemplateCollectionId, int type,
		int start, int end) {

		return getPersistence().filterFindByG_P_T(
			groupId, parentLayoutPageTemplateCollectionId, type, start, end);
	}

	public static LayoutPageTemplateCollectionPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		LayoutPageTemplateCollectionPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile LayoutPageTemplateCollectionPersistence
		_persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-779200104