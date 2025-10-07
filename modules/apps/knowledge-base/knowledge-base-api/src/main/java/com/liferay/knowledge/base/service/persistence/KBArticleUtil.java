/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.service.persistence;

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the kb article service. This utility wraps <code>com.liferay.knowledge.base.service.persistence.impl.KBArticlePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see KBArticlePersistence
 * @generated
 */
public class KBArticleUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(KBArticle kbArticle) {
		getPersistence().clearCache(kbArticle);
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
	public static Map<Serializable, KBArticle> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<KBArticle> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<KBArticle> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<KBArticle> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static KBArticle update(KBArticle kbArticle) {
		return getPersistence().update(kbArticle);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static KBArticle update(
		KBArticle kbArticle, ServiceContext serviceContext) {

		return getPersistence().update(kbArticle, serviceContext);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByResourcePrimKey(long resourcePrimKey) {
		return getPersistence().findByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByResourcePrimKey_First(
			long resourcePrimKey,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByResourcePrimKey_First(
			resourcePrimKey, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByResourcePrimKey_First(
		long resourcePrimKey, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByResourcePrimKey_First(
			resourcePrimKey, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByResourcePrimKey_Last(
			long resourcePrimKey,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByResourcePrimKey_Last(
			resourcePrimKey, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByResourcePrimKey_Last(
		long resourcePrimKey, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByResourcePrimKey_Last(
			resourcePrimKey, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByResourcePrimKey_PrevAndNext(
			long kbArticleId, long resourcePrimKey,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByResourcePrimKey_PrevAndNext(
			kbArticleId, resourcePrimKey, orderByComparator);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	public static void removeByResourcePrimKey(long resourcePrimKey) {
		getPersistence().removeByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching kb articles
	 */
	public static int countByResourcePrimKey(long resourcePrimKey) {
		return getPersistence().countByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns all the kb articles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the kb articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByUuid_First(
			String uuid, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByUuid_First(
		String uuid, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByUuid_Last(
			String uuid, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByUuid_Last(
		String uuid, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByUuid_Last(uuid, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where uuid = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByUuid_PrevAndNext(
			long kbArticleId, String uuid,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByUuid_PrevAndNext(
			kbArticleId, uuid, orderByComparator);
	}

	/**
	 * Removes all the kb articles where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of kb articles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching kb articles
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the kb article where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByUUID_G(String uuid, long groupId)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the kb article where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the kb article where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the kb article where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the kb article that was removed
	 */
	public static KBArticle removeByUUID_G(String uuid, long groupId)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of kb articles where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching kb articles
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByUuid_C_Last(
			String uuid, long companyId,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByUuid_C_Last(
		String uuid, long companyId,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByUuid_C_Last(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByUuid_C_PrevAndNext(
			long kbArticleId, String uuid, long companyId,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByUuid_C_PrevAndNext(
			kbArticleId, uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the kb articles where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of kb articles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching kb articles
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId) {

		return getPersistence().findByR_G(resourcePrimKey, groupId);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId, int start, int end) {

		return getPersistence().findByR_G(resourcePrimKey, groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G(
			resourcePrimKey, groupId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G(
		long resourcePrimKey, long groupId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G(
			resourcePrimKey, groupId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_First(
			long resourcePrimKey, long groupId,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_First(
			resourcePrimKey, groupId, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_First(
		long resourcePrimKey, long groupId,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_First(
			resourcePrimKey, groupId, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_Last(
			long resourcePrimKey, long groupId,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_Last(
			resourcePrimKey, groupId, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_Last(
		long resourcePrimKey, long groupId,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_Last(
			resourcePrimKey, groupId, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_G_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G(
		long resourcePrimKey, long groupId) {

		return getPersistence().filterFindByR_G(resourcePrimKey, groupId);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G(
		long resourcePrimKey, long groupId, int start, int end) {

		return getPersistence().filterFindByR_G(
			resourcePrimKey, groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G(
		long resourcePrimKey, long groupId, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G(
			resourcePrimKey, groupId, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByR_G_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByR_G_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, orderByComparator);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 */
	public static void removeByR_G(long resourcePrimKey, long groupId) {
		getPersistence().removeByR_G(resourcePrimKey, groupId);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the number of matching kb articles
	 */
	public static int countByR_G(long resourcePrimKey, long groupId) {
		return getPersistence().countByR_G(resourcePrimKey, groupId);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G(long resourcePrimKey, long groupId) {
		return getPersistence().filterCountByR_G(resourcePrimKey, groupId);
	}

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_V(long resourcePrimKey, int version)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_V(resourcePrimKey, version);
	}

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_V(long resourcePrimKey, int version) {
		return getPersistence().fetchByR_V(resourcePrimKey, version);
	}

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_V(
		long resourcePrimKey, int version, boolean useFinderCache) {

		return getPersistence().fetchByR_V(
			resourcePrimKey, version, useFinderCache);
	}

	/**
	 * Removes the kb article where resourcePrimKey = &#63; and version = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the kb article that was removed
	 */
	public static KBArticle removeByR_V(long resourcePrimKey, int version)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().removeByR_V(resourcePrimKey, version);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and version = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	public static int countByR_V(long resourcePrimKey, int version) {
		return getPersistence().countByR_V(resourcePrimKey, version);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest) {

		return getPersistence().findByR_L(resourcePrimKey, latest);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest, int start, int end) {

		return getPersistence().findByR_L(resourcePrimKey, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_L(
			resourcePrimKey, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_L(
		long resourcePrimKey, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_L(
			resourcePrimKey, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_L_First(
			long resourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_L_First(
			resourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_L_First(
		long resourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_L_First(
			resourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_L_Last(
			long resourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_L_Last(
			resourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_L_Last(
		long resourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_L_Last(
			resourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_L_PrevAndNext(
			long kbArticleId, long resourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_L_PrevAndNext(
			kbArticleId, resourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest) {

		return getPersistence().findByR_L(resourcePrimKeys, latest);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest, int start, int end) {

		return getPersistence().findByR_L(resourcePrimKeys, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_L(
			resourcePrimKeys, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_L(
		long[] resourcePrimKeys, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_L(
			resourcePrimKeys, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 */
	public static void removeByR_L(long resourcePrimKey, boolean latest) {
		getPersistence().removeByR_L(resourcePrimKey, latest);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByR_L(long resourcePrimKey, boolean latest) {
		return getPersistence().countByR_L(resourcePrimKey, latest);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByR_L(long[] resourcePrimKeys, boolean latest) {
		return getPersistence().countByR_L(resourcePrimKeys, latest);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main) {

		return getPersistence().findByR_M(resourcePrimKey, main);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main, int start, int end) {

		return getPersistence().findByR_M(resourcePrimKey, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_M(
			resourcePrimKey, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_M(
		long resourcePrimKey, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_M(
			resourcePrimKey, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_M_First(
			long resourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_M_First(
			resourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_M_First(
		long resourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_M_First(
			resourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_M_Last(
			long resourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_M_Last(
			resourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_M_Last(
		long resourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_M_Last(
			resourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_M_PrevAndNext(
			long kbArticleId, long resourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_M_PrevAndNext(
			kbArticleId, resourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main) {

		return getPersistence().findByR_M(resourcePrimKeys, main);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main, int start, int end) {

		return getPersistence().findByR_M(resourcePrimKeys, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_M(
			resourcePrimKeys, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_M(
		long[] resourcePrimKeys, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_M(
			resourcePrimKeys, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 */
	public static void removeByR_M(long resourcePrimKey, boolean main) {
		getPersistence().removeByR_M(resourcePrimKey, main);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByR_M(long resourcePrimKey, boolean main) {
		return getPersistence().countByR_M(resourcePrimKey, main);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByR_M(long[] resourcePrimKeys, boolean main) {
		return getPersistence().countByR_M(resourcePrimKeys, main);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_S(long resourcePrimKey, int status) {
		return getPersistence().findByR_S(resourcePrimKey, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_S(
		long resourcePrimKey, int status, int start, int end) {

		return getPersistence().findByR_S(resourcePrimKey, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_S(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_S(
			resourcePrimKey, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_S(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_S(
			resourcePrimKey, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_S_First(
			long resourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_S_First(
			resourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_S_First(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_S_First(
			resourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_S_Last(
			long resourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_S_Last(
			resourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_S_Last(
		long resourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_S_Last(
			resourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_S_PrevAndNext(
			long kbArticleId, long resourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_S_PrevAndNext(
			kbArticleId, resourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses) {

		return getPersistence().findByR_S(resourcePrimKeys, statuses);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses, int start, int end) {

		return getPersistence().findByR_S(
			resourcePrimKeys, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_S(
			resourcePrimKeys, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_S(
		long[] resourcePrimKeys, int[] statuses, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_S(
			resourcePrimKeys, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 */
	public static void removeByR_S(long resourcePrimKey, int status) {
		getPersistence().removeByR_S(resourcePrimKey, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_S(long resourcePrimKey, int status) {
		return getPersistence().countByR_S(resourcePrimKey, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param statuses the statuses
	 * @return the number of matching kb articles
	 */
	public static int countByR_S(long[] resourcePrimKeys, int[] statuses) {
		return getPersistence().countByR_S(resourcePrimKeys, statuses);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode) {

		return getPersistence().findByG_ERC(groupId, externalReferenceCode);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end) {

		return getPersistence().findByG_ERC(
			groupId, externalReferenceCode, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_ERC(
			groupId, externalReferenceCode, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_ERC(
			groupId, externalReferenceCode, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_ERC_First(
			long groupId, String externalReferenceCode,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_First(
			groupId, externalReferenceCode, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_ERC_First(
		long groupId, String externalReferenceCode,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_ERC_First(
			groupId, externalReferenceCode, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_ERC_Last(
			long groupId, String externalReferenceCode,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_Last(
			groupId, externalReferenceCode, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_ERC_Last(
		long groupId, String externalReferenceCode,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_ERC_Last(
			groupId, externalReferenceCode, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_ERC_PrevAndNext(
			long kbArticleId, long groupId, String externalReferenceCode,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_PrevAndNext(
			kbArticleId, groupId, externalReferenceCode, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode) {

		return getPersistence().filterFindByG_ERC(
			groupId, externalReferenceCode);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end) {

		return getPersistence().filterFindByG_ERC(
			groupId, externalReferenceCode, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_ERC(
			groupId, externalReferenceCode, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_ERC_PrevAndNext(
			long kbArticleId, long groupId, String externalReferenceCode,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_ERC_PrevAndNext(
			kbArticleId, groupId, externalReferenceCode, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and externalReferenceCode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 */
	public static void removeByG_ERC(
		long groupId, String externalReferenceCode) {

		getPersistence().removeByG_ERC(groupId, externalReferenceCode);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching kb articles
	 */
	public static int countByG_ERC(long groupId, String externalReferenceCode) {
		return getPersistence().countByG_ERC(groupId, externalReferenceCode);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_ERC(
		long groupId, String externalReferenceCode) {

		return getPersistence().filterCountByG_ERC(
			groupId, externalReferenceCode);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_L(long groupId, boolean latest) {
		return getPersistence().findByG_L(groupId, latest);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_L(
		long groupId, boolean latest, int start, int end) {

		return getPersistence().findByG_L(groupId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_L(
		long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_L(
			groupId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_L(
		long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_L(
			groupId, latest, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_L_First(
			long groupId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_L_First(
			groupId, latest, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_L_First(
		long groupId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_L_First(
			groupId, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_L_Last(
			long groupId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_L_Last(
			groupId, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_L_Last(
		long groupId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_L_Last(
			groupId, latest, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_L_PrevAndNext(
			long kbArticleId, long groupId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_L_PrevAndNext(
			kbArticleId, groupId, latest, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_L(
		long groupId, boolean latest) {

		return getPersistence().filterFindByG_L(groupId, latest);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_L(
		long groupId, boolean latest, int start, int end) {

		return getPersistence().filterFindByG_L(groupId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_L(
		long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_L(
			groupId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_L_PrevAndNext(
			long kbArticleId, long groupId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_L_PrevAndNext(
			kbArticleId, groupId, latest, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 */
	public static void removeByG_L(long groupId, boolean latest) {
		getPersistence().removeByG_L(groupId, latest);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByG_L(long groupId, boolean latest) {
		return getPersistence().countByG_L(groupId, latest);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_L(long groupId, boolean latest) {
		return getPersistence().filterCountByG_L(groupId, latest);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_M(long groupId, boolean main) {
		return getPersistence().findByG_M(groupId, main);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_M(
		long groupId, boolean main, int start, int end) {

		return getPersistence().findByG_M(groupId, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_M(
		long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_M(
			groupId, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_M(
		long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_M(
			groupId, main, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_M_First(
			long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_M_First(
			groupId, main, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_M_First(
		long groupId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_M_First(
			groupId, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_M_Last(
			long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_M_Last(
			groupId, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_M_Last(
		long groupId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_M_Last(
			groupId, main, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_M_PrevAndNext(
			long kbArticleId, long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_M_PrevAndNext(
			kbArticleId, groupId, main, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_M(long groupId, boolean main) {
		return getPersistence().filterFindByG_M(groupId, main);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_M(
		long groupId, boolean main, int start, int end) {

		return getPersistence().filterFindByG_M(groupId, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_M(
		long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_M(
			groupId, main, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_M_PrevAndNext(
			long kbArticleId, long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_M_PrevAndNext(
			kbArticleId, groupId, main, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 */
	public static void removeByG_M(long groupId, boolean main) {
		getPersistence().removeByG_M(groupId, main);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByG_M(long groupId, boolean main) {
		return getPersistence().countByG_M(groupId, main);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_M(long groupId, boolean main) {
		return getPersistence().filterCountByG_M(groupId, main);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_S(long groupId, int status) {
		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_S_First(
			long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_S_Last(
			long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_S_Last(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_S_Last(
		long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_S_Last(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_S_PrevAndNext(
			long kbArticleId, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_S_PrevAndNext(
			kbArticleId, groupId, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_S(long groupId, int status) {
		return getPersistence().filterFindByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().filterFindByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_S_PrevAndNext(
			long kbArticleId, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_S_PrevAndNext(
			kbArticleId, groupId, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_S(long groupId, int status) {
		return getPersistence().filterCountByG_S(groupId, status);
	}

	/**
	 * Returns all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByC_L(long companyId, boolean latest) {
		return getPersistence().findByC_L(companyId, latest);
	}

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByC_L(
		long companyId, boolean latest, int start, int end) {

		return getPersistence().findByC_L(companyId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_L(
		long companyId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByC_L(
			companyId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_L(
		long companyId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_L(
			companyId, latest, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_L_First(
			long companyId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_L_First(
			companyId, latest, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_L_First(
		long companyId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_L_First(
			companyId, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_L_Last(
			long companyId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_L_Last(
			companyId, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_L_Last(
		long companyId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_L_Last(
			companyId, latest, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByC_L_PrevAndNext(
			long kbArticleId, long companyId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_L_PrevAndNext(
			kbArticleId, companyId, latest, orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and latest = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 */
	public static void removeByC_L(long companyId, boolean latest) {
		getPersistence().removeByC_L(companyId, latest);
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and latest = &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByC_L(long companyId, boolean latest) {
		return getPersistence().countByC_L(companyId, latest);
	}

	/**
	 * Returns all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByC_M(long companyId, boolean main) {
		return getPersistence().findByC_M(companyId, main);
	}

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByC_M(
		long companyId, boolean main, int start, int end) {

		return getPersistence().findByC_M(companyId, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_M(
		long companyId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByC_M(
			companyId, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_M(
		long companyId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_M(
			companyId, main, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_M_First(
			long companyId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_M_First(
			companyId, main, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_M_First(
		long companyId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_M_First(
			companyId, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_M_Last(
			long companyId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_M_Last(
			companyId, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_M_Last(
		long companyId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_M_Last(
			companyId, main, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByC_M_PrevAndNext(
			long kbArticleId, long companyId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_M_PrevAndNext(
			kbArticleId, companyId, main, orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and main = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 */
	public static void removeByC_M(long companyId, boolean main) {
		getPersistence().removeByC_M(companyId, main);
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and main = &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByC_M(long companyId, boolean main) {
		return getPersistence().countByC_M(companyId, main);
	}

	/**
	 * Returns all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByC_S(long companyId, int status) {
		return getPersistence().findByC_S(companyId, status);
	}

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByC_S(
		long companyId, int status, int start, int end) {

		return getPersistence().findByC_S(companyId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_S_First(
			long companyId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_S_Last(
			long companyId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_S_Last(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_S_Last(
		long companyId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_S_Last(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByC_S_PrevAndNext(
			long kbArticleId, long companyId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_S_PrevAndNext(
			kbArticleId, companyId, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public static void removeByC_S(long companyId, int status) {
		getPersistence().removeByC_S(companyId, status);
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByC_S(long companyId, int status) {
		return getPersistence().countByC_S(companyId, status);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest) {

		return getPersistence().findByP_L(parentResourcePrimKey, latest);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest, int start, int end) {

		return getPersistence().findByP_L(
			parentResourcePrimKey, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_L(
			parentResourcePrimKey, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_L(
		long parentResourcePrimKey, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L(
			parentResourcePrimKey, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_L_First(
			long parentResourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_L_First(
			parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_L_First(
		long parentResourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_L_First(
			parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_L_Last(
			long parentResourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_L_Last(
			parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_L_Last(
		long parentResourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_L_Last(
			parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByP_L_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_L_PrevAndNext(
			kbArticleId, parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest) {

		return getPersistence().findByP_L(parentResourcePrimKeys, latest);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest, int start, int end) {

		return getPersistence().findByP_L(
			parentResourcePrimKeys, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_L(
			parentResourcePrimKeys, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_L(
		long[] parentResourcePrimKeys, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L(
			parentResourcePrimKeys, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 */
	public static void removeByP_L(long parentResourcePrimKey, boolean latest) {
		getPersistence().removeByP_L(parentResourcePrimKey, latest);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByP_L(long parentResourcePrimKey, boolean latest) {
		return getPersistence().countByP_L(parentResourcePrimKey, latest);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByP_L(
		long[] parentResourcePrimKeys, boolean latest) {

		return getPersistence().countByP_L(parentResourcePrimKeys, latest);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main) {

		return getPersistence().findByP_M(parentResourcePrimKey, main);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main, int start, int end) {

		return getPersistence().findByP_M(
			parentResourcePrimKey, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_M(
			parentResourcePrimKey, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_M(
		long parentResourcePrimKey, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_M(
			parentResourcePrimKey, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_M_First(
			long parentResourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_M_First(
			parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_M_First(
		long parentResourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_M_First(
			parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_M_Last(
			long parentResourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_M_Last(
			parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_M_Last(
		long parentResourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_M_Last(
			parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByP_M_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_M_PrevAndNext(
			kbArticleId, parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main) {

		return getPersistence().findByP_M(parentResourcePrimKeys, main);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main, int start, int end) {

		return getPersistence().findByP_M(
			parentResourcePrimKeys, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_M(
			parentResourcePrimKeys, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_M(
		long[] parentResourcePrimKeys, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_M(
			parentResourcePrimKeys, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 */
	public static void removeByP_M(long parentResourcePrimKey, boolean main) {
		getPersistence().removeByP_M(parentResourcePrimKey, main);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByP_M(long parentResourcePrimKey, boolean main) {
		return getPersistence().countByP_M(parentResourcePrimKey, main);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByP_M(long[] parentResourcePrimKeys, boolean main) {
		return getPersistence().countByP_M(parentResourcePrimKeys, main);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status) {

		return getPersistence().findByP_S(parentResourcePrimKey, status);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status, int start, int end) {

		return getPersistence().findByP_S(
			parentResourcePrimKey, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_S(
			parentResourcePrimKey, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_S(
		long parentResourcePrimKey, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_S(
			parentResourcePrimKey, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_S_First(
			long parentResourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_S_First(
			parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_S_First(
		long parentResourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_S_First(
			parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_S_Last(
			long parentResourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_S_Last(
			parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_S_Last(
		long parentResourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_S_Last(
			parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByP_S_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_S_PrevAndNext(
			kbArticleId, parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status) {

		return getPersistence().findByP_S(parentResourcePrimKeys, status);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status, int start, int end) {

		return getPersistence().findByP_S(
			parentResourcePrimKeys, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_S(
			parentResourcePrimKeys, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_S(
		long[] parentResourcePrimKeys, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_S(
			parentResourcePrimKeys, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 */
	public static void removeByP_S(long parentResourcePrimKey, int status) {
		getPersistence().removeByP_S(parentResourcePrimKey, status);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByP_S(long parentResourcePrimKey, int status) {
		return getPersistence().countByP_S(parentResourcePrimKey, status);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByP_S(long[] parentResourcePrimKeys, int status) {
		return getPersistence().countByP_S(parentResourcePrimKeys, status);
	}

	/**
	 * Returns all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByLtD_S(Date displayDate, int status) {
		return getPersistence().findByLtD_S(displayDate, status);
	}

	/**
	 * Returns a range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return getPersistence().findByLtD_S(displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByLtD_S_Last(
			Date displayDate, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByLtD_S_Last(
			displayDate, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByLtD_S_Last(
		Date displayDate, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByLtD_S_Last(
			displayDate, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByLtD_S_PrevAndNext(
			long kbArticleId, Date displayDate, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByLtD_S_PrevAndNext(
			kbArticleId, displayDate, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByLtD_S(Date displayDate, int status) {
		getPersistence().removeByLtD_S(displayDate, status);
	}

	/**
	 * Returns the number of kb articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByLtD_S(Date displayDate, int status) {
		return getPersistence().countByLtD_S(displayDate, status);
	}

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_V(
			long resourcePrimKey, long groupId, int version)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_V(resourcePrimKey, groupId, version);
	}

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_V(
		long resourcePrimKey, long groupId, int version) {

		return getPersistence().fetchByR_G_V(resourcePrimKey, groupId, version);
	}

	/**
	 * Returns the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_V(
		long resourcePrimKey, long groupId, int version,
		boolean useFinderCache) {

		return getPersistence().fetchByR_G_V(
			resourcePrimKey, groupId, version, useFinderCache);
	}

	/**
	 * Removes the kb article where resourcePrimKey = &#63; and groupId = &#63; and version = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the kb article that was removed
	 */
	public static KBArticle removeByR_G_V(
			long resourcePrimKey, long groupId, int version)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().removeByR_G_V(
			resourcePrimKey, groupId, version);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and version = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_V(
		long resourcePrimKey, long groupId, int version) {

		return getPersistence().countByR_G_V(resourcePrimKey, groupId, version);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest) {

		return getPersistence().findByR_G_L(resourcePrimKey, groupId, latest);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start,
		int end) {

		return getPersistence().findByR_G_L(
			resourcePrimKey, groupId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_L(
			resourcePrimKey, groupId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_L(
			resourcePrimKey, groupId, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_L_First(
			long resourcePrimKey, long groupId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_L_First(
			resourcePrimKey, groupId, latest, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_L_First(
		long resourcePrimKey, long groupId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_L_First(
			resourcePrimKey, groupId, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_L_Last(
			long resourcePrimKey, long groupId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_L_Last(
			resourcePrimKey, groupId, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_L_Last(
		long resourcePrimKey, long groupId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_L_Last(
			resourcePrimKey, groupId, latest, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_G_L_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			boolean latest, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_L_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, latest, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L(
		long resourcePrimKey, long groupId, boolean latest) {

		return getPersistence().filterFindByR_G_L(
			resourcePrimKey, groupId, latest);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start,
		int end) {

		return getPersistence().filterFindByR_G_L(
			resourcePrimKey, groupId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L(
		long resourcePrimKey, long groupId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_L(
			resourcePrimKey, groupId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByR_G_L_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			boolean latest, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByR_G_L_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, latest, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest) {

		return getPersistence().filterFindByR_G_L(
			resourcePrimKeys, groupId, latest);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end) {

		return getPersistence().filterFindByR_G_L(
			resourcePrimKeys, groupId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_L(
			resourcePrimKeys, groupId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest) {

		return getPersistence().findByR_G_L(resourcePrimKeys, groupId, latest);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end) {

		return getPersistence().findByR_G_L(
			resourcePrimKeys, groupId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_L(
			resourcePrimKeys, groupId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_L(
			resourcePrimKeys, groupId, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 */
	public static void removeByR_G_L(
		long resourcePrimKey, long groupId, boolean latest) {

		getPersistence().removeByR_G_L(resourcePrimKey, groupId, latest);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_L(
		long resourcePrimKey, long groupId, boolean latest) {

		return getPersistence().countByR_G_L(resourcePrimKey, groupId, latest);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest) {

		return getPersistence().countByR_G_L(resourcePrimKeys, groupId, latest);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_L(
		long resourcePrimKey, long groupId, boolean latest) {

		return getPersistence().filterCountByR_G_L(
			resourcePrimKey, groupId, latest);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_L(
		long[] resourcePrimKeys, long groupId, boolean latest) {

		return getPersistence().filterCountByR_G_L(
			resourcePrimKeys, groupId, latest);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main) {

		return getPersistence().findByR_G_M(resourcePrimKey, groupId, main);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end) {

		return getPersistence().findByR_G_M(
			resourcePrimKey, groupId, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_M(
			resourcePrimKey, groupId, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_M(
			resourcePrimKey, groupId, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_M_First(
			long resourcePrimKey, long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_M_First(
			resourcePrimKey, groupId, main, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_M_First(
		long resourcePrimKey, long groupId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_M_First(
			resourcePrimKey, groupId, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_M_Last(
			long resourcePrimKey, long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_M_Last(
			resourcePrimKey, groupId, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_M_Last(
		long resourcePrimKey, long groupId, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_M_Last(
			resourcePrimKey, groupId, main, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_G_M_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_M_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, main, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M(
		long resourcePrimKey, long groupId, boolean main) {

		return getPersistence().filterFindByR_G_M(
			resourcePrimKey, groupId, main);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end) {

		return getPersistence().filterFindByR_G_M(
			resourcePrimKey, groupId, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M(
		long resourcePrimKey, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_M(
			resourcePrimKey, groupId, main, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByR_G_M_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByR_G_M_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, main, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main) {

		return getPersistence().filterFindByR_G_M(
			resourcePrimKeys, groupId, main);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start,
		int end) {

		return getPersistence().filterFindByR_G_M(
			resourcePrimKeys, groupId, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_M(
			resourcePrimKeys, groupId, main, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main) {

		return getPersistence().findByR_G_M(resourcePrimKeys, groupId, main);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start,
		int end) {

		return getPersistence().findByR_G_M(
			resourcePrimKeys, groupId, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_M(
			resourcePrimKeys, groupId, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_M(
			resourcePrimKeys, groupId, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 */
	public static void removeByR_G_M(
		long resourcePrimKey, long groupId, boolean main) {

		getPersistence().removeByR_G_M(resourcePrimKey, groupId, main);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_M(
		long resourcePrimKey, long groupId, boolean main) {

		return getPersistence().countByR_G_M(resourcePrimKey, groupId, main);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main) {

		return getPersistence().countByR_G_M(resourcePrimKeys, groupId, main);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_M(
		long resourcePrimKey, long groupId, boolean main) {

		return getPersistence().filterCountByR_G_M(
			resourcePrimKey, groupId, main);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_M(
		long[] resourcePrimKeys, long groupId, boolean main) {

		return getPersistence().filterCountByR_G_M(
			resourcePrimKeys, groupId, main);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status) {

		return getPersistence().findByR_G_S(resourcePrimKey, groupId, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end) {

		return getPersistence().findByR_G_S(
			resourcePrimKey, groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_S(
			resourcePrimKey, groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_S(
			resourcePrimKey, groupId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_S_First(
			long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_S_First(
			resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_S_First(
		long resourcePrimKey, long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_S_First(
			resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_S_Last(
			long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_S_Last(
			resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_S_Last(
		long resourcePrimKey, long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_S_Last(
			resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_G_S_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_S_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_S(
		long resourcePrimKey, long groupId, int status) {

		return getPersistence().filterFindByR_G_S(
			resourcePrimKey, groupId, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end) {

		return getPersistence().filterFindByR_G_S(
			resourcePrimKey, groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_S(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_S(
			resourcePrimKey, groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByR_G_S_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByR_G_S_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_S(
		long[] resourcePrimKeys, long groupId, int status) {

		return getPersistence().filterFindByR_G_S(
			resourcePrimKeys, groupId, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end) {

		return getPersistence().filterFindByR_G_S(
			resourcePrimKeys, groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_S(
			resourcePrimKeys, groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status) {

		return getPersistence().findByR_G_S(resourcePrimKeys, groupId, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end) {

		return getPersistence().findByR_G_S(
			resourcePrimKeys, groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_S(
			resourcePrimKeys, groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_S(
		long[] resourcePrimKeys, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_S(
			resourcePrimKeys, groupId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByR_G_S(
		long resourcePrimKey, long groupId, int status) {

		getPersistence().removeByR_G_S(resourcePrimKey, groupId, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_S(
		long resourcePrimKey, long groupId, int status) {

		return getPersistence().countByR_G_S(resourcePrimKey, groupId, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_S(
		long[] resourcePrimKeys, long groupId, int status) {

		return getPersistence().countByR_G_S(resourcePrimKeys, groupId, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_S(
		long resourcePrimKey, long groupId, int status) {

		return getPersistence().filterCountByR_G_S(
			resourcePrimKey, groupId, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_S(
		long[] resourcePrimKeys, long groupId, int status) {

		return getPersistence().filterCountByR_G_S(
			resourcePrimKeys, groupId, status);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		return getPersistence().findByR_G_NotS(
			resourcePrimKey, groupId, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end) {

		return getPersistence().findByR_G_NotS(
			resourcePrimKey, groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_NotS(
			resourcePrimKey, groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_NotS(
			resourcePrimKey, groupId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_NotS_First(
			long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_NotS_First(
			resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_NotS_First(
		long resourcePrimKey, long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_NotS_First(
			resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_NotS_Last(
			long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_NotS_Last(
			resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_NotS_Last(
		long resourcePrimKey, long groupId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_NotS_Last(
			resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_G_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_NotS_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		return getPersistence().filterFindByR_G_NotS(
			resourcePrimKey, groupId, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end) {

		return getPersistence().filterFindByR_G_NotS(
			resourcePrimKey, groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_NotS(
		long resourcePrimKey, long groupId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_NotS(
			resourcePrimKey, groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByR_G_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByR_G_NotS_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		getPersistence().removeByR_G_NotS(resourcePrimKey, groupId, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		return getPersistence().countByR_G_NotS(
			resourcePrimKey, groupId, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_NotS(
		long resourcePrimKey, long groupId, int status) {

		return getPersistence().filterCountByR_G_NotS(
			resourcePrimKey, groupId, status);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status) {

		return getPersistence().findByR_L_NotS(resourcePrimKey, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end) {

		return getPersistence().findByR_L_NotS(
			resourcePrimKey, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_L_NotS(
			resourcePrimKey, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_L_NotS(
		long resourcePrimKey, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_L_NotS(
			resourcePrimKey, latest, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_L_NotS_First(
			long resourcePrimKey, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_L_NotS_First(
			resourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_L_NotS_First(
		long resourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_L_NotS_First(
			resourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_L_NotS_Last(
			long resourcePrimKey, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_L_NotS_Last(
			resourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_L_NotS_Last(
		long resourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_L_NotS_Last(
			resourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_L_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_L_NotS_PrevAndNext(
			kbArticleId, resourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status) {

		return getPersistence().findByR_L_NotS(
			resourcePrimKeys, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start,
		int end) {

		return getPersistence().findByR_L_NotS(
			resourcePrimKeys, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_L_NotS(
			resourcePrimKeys, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_L_NotS(
			resourcePrimKeys, latest, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByR_L_NotS(
		long resourcePrimKey, boolean latest, int status) {

		getPersistence().removeByR_L_NotS(resourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_L_NotS(
		long resourcePrimKey, boolean latest, int status) {

		return getPersistence().countByR_L_NotS(
			resourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_L_NotS(
		long[] resourcePrimKeys, boolean latest, int status) {

		return getPersistence().countByR_L_NotS(
			resourcePrimKeys, latest, status);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status) {

		return getPersistence().findByR_M_NotS(resourcePrimKey, main, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end) {

		return getPersistence().findByR_M_NotS(
			resourcePrimKey, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_M_NotS(
			resourcePrimKey, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_M_NotS(
		long resourcePrimKey, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_M_NotS(
			resourcePrimKey, main, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_M_NotS_First(
			long resourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_M_NotS_First(
			resourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_M_NotS_First(
		long resourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_M_NotS_First(
			resourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_M_NotS_Last(
			long resourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_M_NotS_Last(
			resourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_M_NotS_Last(
		long resourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_M_NotS_Last(
			resourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_M_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_M_NotS_PrevAndNext(
			kbArticleId, resourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status) {

		return getPersistence().findByR_M_NotS(resourcePrimKeys, main, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end) {

		return getPersistence().findByR_M_NotS(
			resourcePrimKeys, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_M_NotS(
			resourcePrimKeys, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_M_NotS(
			resourcePrimKeys, main, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 */
	public static void removeByR_M_NotS(
		long resourcePrimKey, boolean main, int status) {

		getPersistence().removeByR_M_NotS(resourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_M_NotS(
		long resourcePrimKey, boolean main, int status) {

		return getPersistence().countByR_M_NotS(resourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_M_NotS(
		long[] resourcePrimKeys, boolean main, int status) {

		return getPersistence().countByR_M_NotS(resourcePrimKeys, main, status);
	}

	/**
	 * Returns the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_ERC_V(
			long groupId, String externalReferenceCode, int version)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	/**
	 * Returns the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_ERC_V(
		long groupId, String externalReferenceCode, int version) {

		return getPersistence().fetchByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	/**
	 * Returns the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_ERC_V(
		long groupId, String externalReferenceCode, int version,
		boolean useFinderCache) {

		return getPersistence().fetchByG_ERC_V(
			groupId, externalReferenceCode, version, useFinderCache);
	}

	/**
	 * Removes the kb article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the kb article that was removed
	 */
	public static KBArticle removeByG_ERC_V(
			long groupId, String externalReferenceCode, int version)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().removeByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the number of matching kb articles
	 */
	public static int countByG_ERC_V(
		long groupId, String externalReferenceCode, int version) {

		return getPersistence().countByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status) {

		return getPersistence().findByG_ERC_S(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end) {

		return getPersistence().findByG_ERC_S(
			groupId, externalReferenceCode, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_ERC_S(
			groupId, externalReferenceCode, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_ERC_S(
			groupId, externalReferenceCode, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_ERC_S_First(
			long groupId, String externalReferenceCode, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_S_First(
			groupId, externalReferenceCode, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_ERC_S_First(
		long groupId, String externalReferenceCode, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_ERC_S_First(
			groupId, externalReferenceCode, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_ERC_S_Last(
			long groupId, String externalReferenceCode, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_S_Last(
			groupId, externalReferenceCode, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_ERC_S_Last(
		long groupId, String externalReferenceCode, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_ERC_S_Last(
			groupId, externalReferenceCode, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_ERC_S_PrevAndNext(
			long kbArticleId, long groupId, String externalReferenceCode,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_S_PrevAndNext(
			kbArticleId, groupId, externalReferenceCode, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_ERC_S(
		long groupId, String externalReferenceCode, int status) {

		return getPersistence().filterFindByG_ERC_S(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end) {

		return getPersistence().filterFindByG_ERC_S(
			groupId, externalReferenceCode, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_ERC_S(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_ERC_S(
			groupId, externalReferenceCode, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_ERC_S_PrevAndNext(
			long kbArticleId, long groupId, String externalReferenceCode,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_ERC_S_PrevAndNext(
			kbArticleId, groupId, externalReferenceCode, status,
			orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 */
	public static void removeByG_ERC_S(
		long groupId, String externalReferenceCode, int status) {

		getPersistence().removeByG_ERC_S(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_ERC_S(
		long groupId, String externalReferenceCode, int status) {

		return getPersistence().countByG_ERC_S(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_ERC_S(
		long groupId, String externalReferenceCode, int status) {

		return getPersistence().filterCountByG_ERC_S(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest) {

		return getPersistence().findByG_P_L(
			groupId, parentResourcePrimKey, latest);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end) {

		return getPersistence().findByG_P_L(
			groupId, parentResourcePrimKey, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_L(
			groupId, parentResourcePrimKey, latest, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_L(
			groupId, parentResourcePrimKey, latest, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_L_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_First(
			groupId, parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_L_First(
		long groupId, long parentResourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_L_First(
			groupId, parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_L_Last(
			long groupId, long parentResourcePrimKey, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_Last(
			groupId, parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_L_Last(
		long groupId, long parentResourcePrimKey, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_L_Last(
			groupId, parentResourcePrimKey, latest, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_P_L_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, latest,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest) {

		return getPersistence().filterFindByG_P_L(
			groupId, parentResourcePrimKey, latest);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end) {

		return getPersistence().filterFindByG_P_L(
			groupId, parentResourcePrimKey, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_L(
			groupId, parentResourcePrimKey, latest, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_P_L_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_P_L_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, latest,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest) {

		return getPersistence().filterFindByG_P_L(
			groupId, parentResourcePrimKeys, latest);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end) {

		return getPersistence().filterFindByG_P_L(
			groupId, parentResourcePrimKeys, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_L(
			groupId, parentResourcePrimKeys, latest, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest) {

		return getPersistence().findByG_P_L(
			groupId, parentResourcePrimKeys, latest);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end) {

		return getPersistence().findByG_P_L(
			groupId, parentResourcePrimKeys, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_L(
			groupId, parentResourcePrimKeys, latest, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_L(
			groupId, parentResourcePrimKeys, latest, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 */
	public static void removeByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest) {

		getPersistence().removeByG_P_L(groupId, parentResourcePrimKey, latest);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest) {

		return getPersistence().countByG_P_L(
			groupId, parentResourcePrimKey, latest);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest) {

		return getPersistence().countByG_P_L(
			groupId, parentResourcePrimKeys, latest);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_L(
		long groupId, long parentResourcePrimKey, boolean latest) {

		return getPersistence().filterCountByG_P_L(
			groupId, parentResourcePrimKey, latest);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_L(
		long groupId, long[] parentResourcePrimKeys, boolean latest) {

		return getPersistence().filterCountByG_P_L(
			groupId, parentResourcePrimKeys, latest);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main) {

		return getPersistence().findByG_P_M(
			groupId, parentResourcePrimKey, main);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end) {

		return getPersistence().findByG_P_M(
			groupId, parentResourcePrimKey, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_M(
			groupId, parentResourcePrimKey, main, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_M(
			groupId, parentResourcePrimKey, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_M_First(
			long groupId, long parentResourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_First(
			groupId, parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_M_First(
		long groupId, long parentResourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_M_First(
			groupId, parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_M_Last(
			long groupId, long parentResourcePrimKey, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_Last(
			groupId, parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_M_Last(
		long groupId, long parentResourcePrimKey, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_M_Last(
			groupId, parentResourcePrimKey, main, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_P_M_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, main,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main) {

		return getPersistence().filterFindByG_P_M(
			groupId, parentResourcePrimKey, main);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end) {

		return getPersistence().filterFindByG_P_M(
			groupId, parentResourcePrimKey, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_M(
			groupId, parentResourcePrimKey, main, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_P_M_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_P_M_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, main,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main) {

		return getPersistence().filterFindByG_P_M(
			groupId, parentResourcePrimKeys, main);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end) {

		return getPersistence().filterFindByG_P_M(
			groupId, parentResourcePrimKeys, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_M(
			groupId, parentResourcePrimKeys, main, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main) {

		return getPersistence().findByG_P_M(
			groupId, parentResourcePrimKeys, main);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end) {

		return getPersistence().findByG_P_M(
			groupId, parentResourcePrimKeys, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_M(
			groupId, parentResourcePrimKeys, main, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_M(
			groupId, parentResourcePrimKeys, main, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 */
	public static void removeByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main) {

		getPersistence().removeByG_P_M(groupId, parentResourcePrimKey, main);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main) {

		return getPersistence().countByG_P_M(
			groupId, parentResourcePrimKey, main);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main) {

		return getPersistence().countByG_P_M(
			groupId, parentResourcePrimKeys, main);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_M(
		long groupId, long parentResourcePrimKey, boolean main) {

		return getPersistence().filterCountByG_P_M(
			groupId, parentResourcePrimKey, main);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_M(
		long groupId, long[] parentResourcePrimKeys, boolean main) {

		return getPersistence().filterCountByG_P_M(
			groupId, parentResourcePrimKeys, main);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status) {

		return getPersistence().findByG_P_S(
			groupId, parentResourcePrimKey, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end) {

		return getPersistence().findByG_P_S(
			groupId, parentResourcePrimKey, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_S(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_S(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_S_First(
			long groupId, long parentResourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_S_First(
			groupId, parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_S_First(
		long groupId, long parentResourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_S_First(
			groupId, parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_S_Last(
			long groupId, long parentResourcePrimKey, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_S_Last(
			groupId, parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_S_Last(
		long groupId, long parentResourcePrimKey, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_S_Last(
			groupId, parentResourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_P_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_S_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_S(
		long groupId, long parentResourcePrimKey, int status) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentResourcePrimKey, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentResourcePrimKey, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_S(
		long groupId, long parentResourcePrimKey, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentResourcePrimKey, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_P_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_P_S_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentResourcePrimKeys, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentResourcePrimKeys, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_S(
			groupId, parentResourcePrimKeys, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status) {

		return getPersistence().findByG_P_S(
			groupId, parentResourcePrimKeys, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end) {

		return getPersistence().findByG_P_S(
			groupId, parentResourcePrimKeys, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_S(
			groupId, parentResourcePrimKeys, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_S(
			groupId, parentResourcePrimKeys, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 */
	public static void removeByG_P_S(
		long groupId, long parentResourcePrimKey, int status) {

		getPersistence().removeByG_P_S(groupId, parentResourcePrimKey, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_S(
		long groupId, long parentResourcePrimKey, int status) {

		return getPersistence().countByG_P_S(
			groupId, parentResourcePrimKey, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status) {

		return getPersistence().countByG_P_S(
			groupId, parentResourcePrimKeys, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_S(
		long groupId, long parentResourcePrimKey, int status) {

		return getPersistence().filterCountByG_P_S(
			groupId, parentResourcePrimKey, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_S(
		long groupId, long[] parentResourcePrimKeys, int status) {

		return getPersistence().filterCountByG_P_S(
			groupId, parentResourcePrimKeys, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle) {

		return getPersistence().findByG_KBFI_UT(groupId, kbFolderId, urlTitle);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end) {

		return getPersistence().findByG_KBFI_UT(
			groupId, kbFolderId, urlTitle, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_KBFI_UT(
			groupId, kbFolderId, urlTitle, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_KBFI_UT(
			groupId, kbFolderId, urlTitle, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_UT_First(
			long groupId, long kbFolderId, String urlTitle,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_First(
			groupId, kbFolderId, urlTitle, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_UT_First(
		long groupId, long kbFolderId, String urlTitle,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_UT_First(
			groupId, kbFolderId, urlTitle, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_UT_Last(
			long groupId, long kbFolderId, String urlTitle,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_Last(
			groupId, kbFolderId, urlTitle, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_UT_Last(
		long groupId, long kbFolderId, String urlTitle,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_UT_Last(
			groupId, kbFolderId, urlTitle, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_KBFI_UT_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_PrevAndNext(
			kbArticleId, groupId, kbFolderId, urlTitle, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle) {

		return getPersistence().filterFindByG_KBFI_UT(
			groupId, kbFolderId, urlTitle);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end) {

		return getPersistence().filterFindByG_KBFI_UT(
			groupId, kbFolderId, urlTitle, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_KBFI_UT(
			groupId, kbFolderId, urlTitle, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_KBFI_UT_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_KBFI_UT_PrevAndNext(
			kbArticleId, groupId, kbFolderId, urlTitle, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 */
	public static void removeByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle) {

		getPersistence().removeByG_KBFI_UT(groupId, kbFolderId, urlTitle);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the number of matching kb articles
	 */
	public static int countByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle) {

		return getPersistence().countByG_KBFI_UT(groupId, kbFolderId, urlTitle);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_KBFI_UT(
		long groupId, long kbFolderId, String urlTitle) {

		return getPersistence().filterCountByG_KBFI_UT(
			groupId, kbFolderId, urlTitle);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest) {

		return getPersistence().findByG_KBFI_L(groupId, kbFolderId, latest);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end) {

		return getPersistence().findByG_KBFI_L(
			groupId, kbFolderId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_KBFI_L(
			groupId, kbFolderId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_KBFI_L(
			groupId, kbFolderId, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_L_First(
			long groupId, long kbFolderId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_L_First(
			groupId, kbFolderId, latest, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_L_First(
		long groupId, long kbFolderId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_L_First(
			groupId, kbFolderId, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_L_Last(
			long groupId, long kbFolderId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_L_Last(
			groupId, kbFolderId, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_L_Last(
		long groupId, long kbFolderId, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_L_Last(
			groupId, kbFolderId, latest, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_KBFI_L_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_L_PrevAndNext(
			kbArticleId, groupId, kbFolderId, latest, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest) {

		return getPersistence().filterFindByG_KBFI_L(
			groupId, kbFolderId, latest);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end) {

		return getPersistence().filterFindByG_KBFI_L(
			groupId, kbFolderId, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_KBFI_L(
			groupId, kbFolderId, latest, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_KBFI_L_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_KBFI_L_PrevAndNext(
			kbArticleId, groupId, kbFolderId, latest, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 */
	public static void removeByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest) {

		getPersistence().removeByG_KBFI_L(groupId, kbFolderId, latest);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest) {

		return getPersistence().countByG_KBFI_L(groupId, kbFolderId, latest);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_KBFI_L(
		long groupId, long kbFolderId, boolean latest) {

		return getPersistence().filterCountByG_KBFI_L(
			groupId, kbFolderId, latest);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status) {

		return getPersistence().findByG_KBFI_S(groupId, kbFolderId, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end) {

		return getPersistence().findByG_KBFI_S(
			groupId, kbFolderId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_KBFI_S(
			groupId, kbFolderId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_KBFI_S(
			groupId, kbFolderId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_S_First(
			long groupId, long kbFolderId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_S_First(
			groupId, kbFolderId, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_S_First(
		long groupId, long kbFolderId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_S_First(
			groupId, kbFolderId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_S_Last(
			long groupId, long kbFolderId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_S_Last(
			groupId, kbFolderId, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_S_Last(
		long groupId, long kbFolderId, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_S_Last(
			groupId, kbFolderId, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_KBFI_S_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_S_PrevAndNext(
			kbArticleId, groupId, kbFolderId, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_S(
		long groupId, long kbFolderId, int status) {

		return getPersistence().filterFindByG_KBFI_S(
			groupId, kbFolderId, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end) {

		return getPersistence().filterFindByG_KBFI_S(
			groupId, kbFolderId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_S(
		long groupId, long kbFolderId, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_KBFI_S(
			groupId, kbFolderId, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_KBFI_S_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_KBFI_S_PrevAndNext(
			kbArticleId, groupId, kbFolderId, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 */
	public static void removeByG_KBFI_S(
		long groupId, long kbFolderId, int status) {

		getPersistence().removeByG_KBFI_S(groupId, kbFolderId, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_KBFI_S(
		long groupId, long kbFolderId, int status) {

		return getPersistence().countByG_KBFI_S(groupId, kbFolderId, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_KBFI_S(
		long groupId, long kbFolderId, int status) {

		return getPersistence().filterCountByG_KBFI_S(
			groupId, kbFolderId, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		return getPersistence().findByG_LikeS_L(groupId, sections, latest);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end) {

		return getPersistence().findByG_LikeS_L(
			groupId, sections, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_L(
			groupId, sections, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_L(
			groupId, sections, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_L_First(
			long groupId, String sections, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_L_First(
			groupId, sections, latest, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_L_First(
		long groupId, String sections, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_L_First(
			groupId, sections, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_L_Last(
			long groupId, String sections, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_L_Last(
			groupId, sections, latest, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_L_Last(
		long groupId, String sections, boolean latest,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_L_Last(
			groupId, sections, latest, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_LikeS_L_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_L_PrevAndNext(
			kbArticleId, groupId, sections, latest, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		return getPersistence().filterFindByG_LikeS_L(
			groupId, sections, latest);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end) {

		return getPersistence().filterFindByG_LikeS_L(
			groupId, sections, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String sections, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_L(
			groupId, sections, latest, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_LikeS_L_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean latest,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_LikeS_L_PrevAndNext(
			kbArticleId, groupId, sections, latest, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest) {

		return getPersistence().filterFindByG_LikeS_L(
			groupId, sectionses, latest);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end) {

		return getPersistence().filterFindByG_LikeS_L(
			groupId, sectionses, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_L(
			groupId, sectionses, latest, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest) {

		return getPersistence().findByG_LikeS_L(groupId, sectionses, latest);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end) {

		return getPersistence().findByG_LikeS_L(
			groupId, sectionses, latest, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_L(
			groupId, sectionses, latest, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_L(
			groupId, sectionses, latest, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 */
	public static void removeByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		getPersistence().removeByG_LikeS_L(groupId, sections, latest);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		return getPersistence().countByG_LikeS_L(groupId, sections, latest);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest) {

		return getPersistence().countByG_LikeS_L(groupId, sectionses, latest);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_L(
		long groupId, String sections, boolean latest) {

		return getPersistence().filterCountByG_LikeS_L(
			groupId, sections, latest);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_L(
		long groupId, String[] sectionses, boolean latest) {

		return getPersistence().filterCountByG_LikeS_L(
			groupId, sectionses, latest);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main) {

		return getPersistence().findByG_LikeS_M(groupId, sections, main);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end) {

		return getPersistence().findByG_LikeS_M(
			groupId, sections, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_M(
			groupId, sections, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_M(
			groupId, sections, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_M_First(
			long groupId, String sections, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_M_First(
			groupId, sections, main, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_M_First(
		long groupId, String sections, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_M_First(
			groupId, sections, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_M_Last(
			long groupId, String sections, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_M_Last(
			groupId, sections, main, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_M_Last(
		long groupId, String sections, boolean main,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_M_Last(
			groupId, sections, main, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_LikeS_M_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_M_PrevAndNext(
			kbArticleId, groupId, sections, main, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main) {

		return getPersistence().filterFindByG_LikeS_M(groupId, sections, main);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end) {

		return getPersistence().filterFindByG_LikeS_M(
			groupId, sections, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String sections, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_M(
			groupId, sections, main, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_LikeS_M_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean main,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_LikeS_M_PrevAndNext(
			kbArticleId, groupId, sections, main, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main) {

		return getPersistence().filterFindByG_LikeS_M(
			groupId, sectionses, main);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end) {

		return getPersistence().filterFindByG_LikeS_M(
			groupId, sectionses, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_M(
			groupId, sectionses, main, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main) {

		return getPersistence().findByG_LikeS_M(groupId, sectionses, main);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end) {

		return getPersistence().findByG_LikeS_M(
			groupId, sectionses, main, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_M(
			groupId, sectionses, main, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M(
		long groupId, String[] sectionses, boolean main, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_M(
			groupId, sectionses, main, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 */
	public static void removeByG_LikeS_M(
		long groupId, String sections, boolean main) {

		getPersistence().removeByG_LikeS_M(groupId, sections, main);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_M(
		long groupId, String sections, boolean main) {

		return getPersistence().countByG_LikeS_M(groupId, sections, main);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_M(
		long groupId, String[] sectionses, boolean main) {

		return getPersistence().countByG_LikeS_M(groupId, sectionses, main);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_M(
		long groupId, String sections, boolean main) {

		return getPersistence().filterCountByG_LikeS_M(groupId, sections, main);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_M(
		long groupId, String[] sectionses, boolean main) {

		return getPersistence().filterCountByG_LikeS_M(
			groupId, sectionses, main);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status) {

		return getPersistence().findByG_LikeS_S(groupId, sections, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end) {

		return getPersistence().findByG_LikeS_S(
			groupId, sections, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_S(
			groupId, sections, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_S(
			groupId, sections, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_S_First(
			long groupId, String sections, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_S_First(
			groupId, sections, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_S_First(
		long groupId, String sections, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_S_First(
			groupId, sections, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_S_Last(
			long groupId, String sections, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_S_Last(
			groupId, sections, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_S_Last(
		long groupId, String sections, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_S_Last(
			groupId, sections, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_LikeS_S_PrevAndNext(
			long kbArticleId, long groupId, String sections, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_S_PrevAndNext(
			kbArticleId, groupId, sections, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status) {

		return getPersistence().filterFindByG_LikeS_S(
			groupId, sections, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status, int start, int end) {

		return getPersistence().filterFindByG_LikeS_S(
			groupId, sections, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String sections, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_S(
			groupId, sections, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_LikeS_S_PrevAndNext(
			long kbArticleId, long groupId, String sections, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_LikeS_S_PrevAndNext(
			kbArticleId, groupId, sections, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status) {

		return getPersistence().filterFindByG_LikeS_S(
			groupId, sectionses, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end) {

		return getPersistence().filterFindByG_LikeS_S(
			groupId, sectionses, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_S(
			groupId, sectionses, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status) {

		return getPersistence().findByG_LikeS_S(groupId, sectionses, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end) {

		return getPersistence().findByG_LikeS_S(
			groupId, sectionses, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_S(
			groupId, sectionses, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_S(
		long groupId, String[] sectionses, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_S(
			groupId, sectionses, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 */
	public static void removeByG_LikeS_S(
		long groupId, String sections, int status) {

		getPersistence().removeByG_LikeS_S(groupId, sections, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_S(
		long groupId, String sections, int status) {

		return getPersistence().countByG_LikeS_S(groupId, sections, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_S(
		long groupId, String[] sectionses, int status) {

		return getPersistence().countByG_LikeS_S(groupId, sectionses, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_S(
		long groupId, String sections, int status) {

		return getPersistence().filterCountByG_LikeS_S(
			groupId, sections, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_S(
		long groupId, String[] sectionses, int status) {

		return getPersistence().filterCountByG_LikeS_S(
			groupId, sectionses, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status) {

		return getPersistence().findByG_L_NotS(groupId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end) {

		return getPersistence().findByG_L_NotS(
			groupId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_L_NotS(
			groupId, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_L_NotS(
			groupId, latest, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_L_NotS_First(
			long groupId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_L_NotS_First(
			groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_L_NotS_First(
		long groupId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_L_NotS_First(
			groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_L_NotS_Last(
			long groupId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_L_NotS_Last(
			groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_L_NotS_Last(
		long groupId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_L_NotS_Last(
			groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_L_NotS_PrevAndNext(
			kbArticleId, groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status) {

		return getPersistence().filterFindByG_L_NotS(groupId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end) {

		return getPersistence().filterFindByG_L_NotS(
			groupId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_L_NotS(
		long groupId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_L_NotS(
			groupId, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_L_NotS_PrevAndNext(
			kbArticleId, groupId, latest, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByG_L_NotS(
		long groupId, boolean latest, int status) {

		getPersistence().removeByG_L_NotS(groupId, latest, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_L_NotS(
		long groupId, boolean latest, int status) {

		return getPersistence().countByG_L_NotS(groupId, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_L_NotS(
		long groupId, boolean latest, int status) {

		return getPersistence().filterCountByG_L_NotS(groupId, latest, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status) {

		return getPersistence().findByG_M_NotS(groupId, main, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end) {

		return getPersistence().findByG_M_NotS(
			groupId, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_M_NotS(
			groupId, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_M_NotS(
			groupId, main, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_M_NotS_First(
			long groupId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_M_NotS_First(
			groupId, main, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_M_NotS_First(
		long groupId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_M_NotS_First(
			groupId, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_M_NotS_Last(
			long groupId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_M_NotS_Last(
			groupId, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_M_NotS_Last(
		long groupId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_M_NotS_Last(
			groupId, main, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_M_NotS_PrevAndNext(
			kbArticleId, groupId, main, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status) {

		return getPersistence().filterFindByG_M_NotS(groupId, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status, int start, int end) {

		return getPersistence().filterFindByG_M_NotS(
			groupId, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_M_NotS(
		long groupId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_M_NotS(
			groupId, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_M_NotS_PrevAndNext(
			kbArticleId, groupId, main, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 */
	public static void removeByG_M_NotS(
		long groupId, boolean main, int status) {

		getPersistence().removeByG_M_NotS(groupId, main, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_M_NotS(long groupId, boolean main, int status) {
		return getPersistence().countByG_M_NotS(groupId, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_M_NotS(
		long groupId, boolean main, int status) {

		return getPersistence().filterCountByG_M_NotS(groupId, main, status);
	}

	/**
	 * Returns all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status) {

		return getPersistence().findByC_L_NotS(companyId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end) {

		return getPersistence().findByC_L_NotS(
			companyId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByC_L_NotS(
			companyId, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_L_NotS(
		long companyId, boolean latest, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_L_NotS(
			companyId, latest, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_L_NotS_First(
			long companyId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_L_NotS_First(
			companyId, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_L_NotS_First(
		long companyId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_L_NotS_First(
			companyId, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_L_NotS_Last(
			long companyId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_L_NotS_Last(
			companyId, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_L_NotS_Last(
		long companyId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_L_NotS_Last(
			companyId, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByC_L_NotS_PrevAndNext(
			long kbArticleId, long companyId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_L_NotS_PrevAndNext(
			kbArticleId, companyId, latest, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByC_L_NotS(
		long companyId, boolean latest, int status) {

		getPersistence().removeByC_L_NotS(companyId, latest, status);
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByC_L_NotS(
		long companyId, boolean latest, int status) {

		return getPersistence().countByC_L_NotS(companyId, latest, status);
	}

	/**
	 * Returns all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status) {

		return getPersistence().findByC_M_NotS(companyId, main, status);
	}

	/**
	 * Returns a range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end) {

		return getPersistence().findByC_M_NotS(
			companyId, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByC_M_NotS(
			companyId, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByC_M_NotS(
		long companyId, boolean main, int status, int start, int end,
		OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_M_NotS(
			companyId, main, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_M_NotS_First(
			long companyId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_M_NotS_First(
			companyId, main, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_M_NotS_First(
		long companyId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_M_NotS_First(
			companyId, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByC_M_NotS_Last(
			long companyId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_M_NotS_Last(
			companyId, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByC_M_NotS_Last(
		long companyId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByC_M_NotS_Last(
			companyId, main, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByC_M_NotS_PrevAndNext(
			long kbArticleId, long companyId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByC_M_NotS_PrevAndNext(
			kbArticleId, companyId, main, status, orderByComparator);
	}

	/**
	 * Removes all the kb articles where companyId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 */
	public static void removeByC_M_NotS(
		long companyId, boolean main, int status) {

		getPersistence().removeByC_M_NotS(companyId, main, status);
	}

	/**
	 * Returns the number of kb articles where companyId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param companyId the company ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByC_M_NotS(
		long companyId, boolean main, int status) {

		return getPersistence().countByC_M_NotS(companyId, main, status);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().findByP_L_NotS(
			parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end) {

		return getPersistence().findByP_L_NotS(
			parentResourcePrimKey, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_L_NotS(
			parentResourcePrimKey, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L_NotS(
			parentResourcePrimKey, latest, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_L_NotS_First(
			long parentResourcePrimKey, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_L_NotS_First(
			parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_L_NotS_First(
		long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_L_NotS_First(
			parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_L_NotS_Last(
			long parentResourcePrimKey, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_L_NotS_Last(
			parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_L_NotS_Last(
		long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_L_NotS_Last(
			parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByP_L_NotS_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_L_NotS_PrevAndNext(
			kbArticleId, parentResourcePrimKey, latest, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status) {

		return getPersistence().findByP_L_NotS(
			parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end) {

		return getPersistence().findByP_L_NotS(
			parentResourcePrimKeys, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_L_NotS(
			parentResourcePrimKeys, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_L_NotS(
			parentResourcePrimKeys, latest, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status) {

		getPersistence().removeByP_L_NotS(
			parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByP_L_NotS(
		long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().countByP_L_NotS(
			parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByP_L_NotS(
		long[] parentResourcePrimKeys, boolean latest, int status) {

		return getPersistence().countByP_L_NotS(
			parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().findByP_M_NotS(
			parentResourcePrimKey, main, status);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end) {

		return getPersistence().findByP_M_NotS(
			parentResourcePrimKey, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_M_NotS(
			parentResourcePrimKey, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_M_NotS(
			parentResourcePrimKey, main, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_M_NotS_First(
			long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_M_NotS_First(
			parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_M_NotS_First(
		long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_M_NotS_First(
			parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByP_M_NotS_Last(
			long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_M_NotS_Last(
			parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByP_M_NotS_Last(
		long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByP_M_NotS_Last(
			parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByP_M_NotS_PrevAndNext(
			long kbArticleId, long parentResourcePrimKey, boolean main,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByP_M_NotS_PrevAndNext(
			kbArticleId, parentResourcePrimKey, main, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().findByP_M_NotS(
			parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns a range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end) {

		return getPersistence().findByP_M_NotS(
			parentResourcePrimKeys, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByP_M_NotS(
			parentResourcePrimKeys, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_M_NotS(
			parentResourcePrimKeys, main, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	public static void removeByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status) {

		getPersistence().removeByP_M_NotS(parentResourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByP_M_NotS(
		long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().countByP_M_NotS(
			parentResourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles where parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByP_M_NotS(
		long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().countByP_M_NotS(
			parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		return getPersistence().findByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end) {

		return getPersistence().findByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_L_NotS_First(
			long resourcePrimKey, long groupId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_L_NotS_First(
			resourcePrimKey, groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_L_NotS_First(
		long resourcePrimKey, long groupId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_L_NotS_First(
			resourcePrimKey, groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_L_NotS_Last(
			long resourcePrimKey, long groupId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_L_NotS_Last(
			resourcePrimKey, groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_L_NotS_Last(
		long resourcePrimKey, long groupId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_L_NotS_Last(
			resourcePrimKey, groupId, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_G_L_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_L_NotS_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, latest, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		return getPersistence().filterFindByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end) {

		return getPersistence().filterFindByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByR_G_L_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId,
			boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByR_G_L_NotS_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, latest, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status) {

		return getPersistence().filterFindByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end) {

		return getPersistence().filterFindByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status) {

		return getPersistence().findByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end) {

		return getPersistence().findByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		getPersistence().removeByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		return getPersistence().countByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status) {

		return getPersistence().countByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_L_NotS(
		long resourcePrimKey, long groupId, boolean latest, int status) {

		return getPersistence().filterCountByR_G_L_NotS(
			resourcePrimKey, groupId, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_L_NotS(
		long[] resourcePrimKeys, long groupId, boolean latest, int status) {

		return getPersistence().filterCountByR_G_L_NotS(
			resourcePrimKeys, groupId, latest, status);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		return getPersistence().findByR_G_M_NotS(
			resourcePrimKey, groupId, main, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end) {

		return getPersistence().findByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_M_NotS_First(
			long resourcePrimKey, long groupId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_M_NotS_First(
			resourcePrimKey, groupId, main, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_M_NotS_First(
		long resourcePrimKey, long groupId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_M_NotS_First(
			resourcePrimKey, groupId, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByR_G_M_NotS_Last(
			long resourcePrimKey, long groupId, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_M_NotS_Last(
			resourcePrimKey, groupId, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByR_G_M_NotS_Last(
		long resourcePrimKey, long groupId, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByR_G_M_NotS_Last(
			resourcePrimKey, groupId, main, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByR_G_M_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, boolean main,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByR_G_M_NotS_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, main, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		return getPersistence().filterFindByR_G_M_NotS(
			resourcePrimKey, groupId, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end) {

		return getPersistence().filterFindByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_M_NotS(
			resourcePrimKey, groupId, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByR_G_M_NotS_PrevAndNext(
			long kbArticleId, long resourcePrimKey, long groupId, boolean main,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByR_G_M_NotS_PrevAndNext(
			kbArticleId, resourcePrimKey, groupId, main, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status) {

		return getPersistence().filterFindByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end) {

		return getPersistence().filterFindByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status) {

		return getPersistence().findByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status);
	}

	/**
	 * Returns a range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end) {

		return getPersistence().findByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 */
	public static void removeByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		getPersistence().removeByR_G_M_NotS(
			resourcePrimKey, groupId, main, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		return getPersistence().countByR_G_M_NotS(
			resourcePrimKey, groupId, main, status);
	}

	/**
	 * Returns the number of kb articles where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status) {

		return getPersistence().countByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_M_NotS(
		long resourcePrimKey, long groupId, boolean main, int status) {

		return getPersistence().filterCountByR_G_M_NotS(
			resourcePrimKey, groupId, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where resourcePrimKey = any &#63; and groupId = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param resourcePrimKeys the resource prim keys
	 * @param groupId the group ID
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByR_G_M_NotS(
		long[] resourcePrimKeys, long groupId, boolean main, int status) {

		return getPersistence().filterCountByR_G_M_NotS(
			resourcePrimKeys, groupId, main, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().findByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end) {

		return getPersistence().findByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_L_S_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_S_First(
			groupId, parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_L_S_First(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_L_S_First(
			groupId, parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_L_S_Last(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_S_Last(
			groupId, parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_L_S_Last(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_L_S_Last(
			groupId, parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_P_L_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_S_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, latest, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().filterFindByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end) {

		return getPersistence().filterFindByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_P_L_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_P_L_S_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, latest, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return getPersistence().filterFindByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end) {

		return getPersistence().filterFindByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return getPersistence().findByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end) {

		return getPersistence().findByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		getPersistence().removeByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().countByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return getPersistence().countByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_L_S(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().filterCountByG_P_L_S(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_L_S(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return getPersistence().filterCountByG_P_L_S(
			groupId, parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().findByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end) {

		return getPersistence().findByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_L_NotS_First(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_NotS_First(
			groupId, parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_L_NotS_First(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_L_NotS_First(
			groupId, parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_L_NotS_Last(
			long groupId, long parentResourcePrimKey, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_NotS_Last(
			groupId, parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_L_NotS_Last(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_L_NotS_Last(
			groupId, parentResourcePrimKey, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_P_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_L_NotS_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, latest, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end) {

		return getPersistence().filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_P_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_P_L_NotS_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, latest, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return getPersistence().filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end) {

		return getPersistence().filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return getPersistence().findByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end) {

		return getPersistence().findByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		getPersistence().removeByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().countByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return getPersistence().countByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_L_NotS(
		long groupId, long parentResourcePrimKey, boolean latest, int status) {

		return getPersistence().filterCountByG_P_L_NotS(
			groupId, parentResourcePrimKey, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_L_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean latest,
		int status) {

		return getPersistence().filterCountByG_P_L_NotS(
			groupId, parentResourcePrimKeys, latest, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().findByG_P_M_S(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end) {

		return getPersistence().findByG_P_M_S(
			groupId, parentResourcePrimKey, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_M_S(
			groupId, parentResourcePrimKey, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_M_S(
			groupId, parentResourcePrimKey, main, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_M_S_First(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_S_First(
			groupId, parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_M_S_First(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_M_S_First(
			groupId, parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_M_S_Last(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_S_Last(
			groupId, parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_M_S_Last(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_M_S_Last(
			groupId, parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_P_M_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_S_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, main, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().filterFindByG_P_M_S(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end) {

		return getPersistence().filterFindByG_P_M_S(
			groupId, parentResourcePrimKey, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_M_S(
			groupId, parentResourcePrimKey, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_P_M_S_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_P_M_S_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, main, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().filterFindByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end) {

		return getPersistence().filterFindByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().findByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end) {

		return getPersistence().findByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	public static void removeByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		getPersistence().removeByG_P_M_S(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().countByG_P_M_S(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().countByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_M_S(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().filterCountByG_P_M_S(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_M_S(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().filterCountByG_P_M_S(
			groupId, parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().findByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end) {

		return getPersistence().findByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_M_NotS_First(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_NotS_First(
			groupId, parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_M_NotS_First(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_M_NotS_First(
			groupId, parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_P_M_NotS_Last(
			long groupId, long parentResourcePrimKey, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_NotS_Last(
			groupId, parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_P_M_NotS_Last(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_P_M_NotS_Last(
			groupId, parentResourcePrimKey, main, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_P_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_P_M_NotS_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, main, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end) {

		return getPersistence().filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_P_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, long parentResourcePrimKey,
			boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_P_M_NotS_PrevAndNext(
			kbArticleId, groupId, parentResourcePrimKey, main, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end) {

		return getPersistence().filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().findByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end) {

		return getPersistence().findByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 */
	public static void removeByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		getPersistence().removeByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().countByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().countByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKey the parent resource prim key
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_M_NotS(
		long groupId, long parentResourcePrimKey, boolean main, int status) {

		return getPersistence().filterCountByG_P_M_NotS(
			groupId, parentResourcePrimKey, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and parentResourcePrimKey = any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param parentResourcePrimKeys the parent resource prim keys
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_P_M_NotS(
		long groupId, long[] parentResourcePrimKeys, boolean main, int status) {

		return getPersistence().filterCountByG_P_M_NotS(
			groupId, parentResourcePrimKeys, main, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getPersistence().findByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end) {

		return getPersistence().findByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_UT_S_First(
			long groupId, long kbFolderId, String urlTitle, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_S_First(
			groupId, kbFolderId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_UT_S_First(
		long groupId, long kbFolderId, String urlTitle, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_UT_S_First(
			groupId, kbFolderId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_UT_S_Last(
			long groupId, long kbFolderId, String urlTitle, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_S_Last(
			groupId, kbFolderId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_UT_S_Last(
		long groupId, long kbFolderId, String urlTitle, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_UT_S_Last(
			groupId, kbFolderId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_KBFI_UT_S_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_S_PrevAndNext(
			kbArticleId, groupId, kbFolderId, urlTitle, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getPersistence().filterFindByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end) {

		return getPersistence().filterFindByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_KBFI_UT_S_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_KBFI_UT_S_PrevAndNext(
			kbArticleId, groupId, kbFolderId, urlTitle, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses) {

		return getPersistence().filterFindByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end) {

		return getPersistence().filterFindByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses) {

		return getPersistence().findByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end) {

		return getPersistence().findByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 */
	public static void removeByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status) {

		getPersistence().removeByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getPersistence().countByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the number of matching kb articles
	 */
	public static int countByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses) {

		return getPersistence().countByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getPersistence().filterCountByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param statuses the statuses
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_KBFI_UT_S(
		long groupId, long kbFolderId, String urlTitle, int[] statuses) {

		return getPersistence().filterCountByG_KBFI_UT_S(
			groupId, kbFolderId, urlTitle, statuses);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getPersistence().findByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end) {

		return getPersistence().findByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_UT_NotS_First(
			long groupId, long kbFolderId, String urlTitle, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_NotS_First(
			groupId, kbFolderId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_UT_NotS_First(
		long groupId, long kbFolderId, String urlTitle, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_UT_NotS_First(
			groupId, kbFolderId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_UT_NotS_Last(
			long groupId, long kbFolderId, String urlTitle, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_NotS_Last(
			groupId, kbFolderId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_UT_NotS_Last(
		long groupId, long kbFolderId, String urlTitle, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_UT_NotS_Last(
			groupId, kbFolderId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_KBFI_UT_NotS_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_UT_NotS_PrevAndNext(
			kbArticleId, groupId, kbFolderId, urlTitle, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getPersistence().filterFindByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end) {

		return getPersistence().filterFindByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_KBFI_UT_NotS_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, String urlTitle,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_KBFI_UT_NotS_PrevAndNext(
			kbArticleId, groupId, kbFolderId, urlTitle, status,
			orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 */
	public static void removeByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		getPersistence().removeByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getPersistence().countByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and urlTitle = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_KBFI_UT_NotS(
		long groupId, long kbFolderId, String urlTitle, int status) {

		return getPersistence().filterCountByG_KBFI_UT_NotS(
			groupId, kbFolderId, urlTitle, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		return getPersistence().findByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end) {

		return getPersistence().findByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_L_NotS_First(
			long groupId, long kbFolderId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_L_NotS_First(
			groupId, kbFolderId, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_L_NotS_First(
		long groupId, long kbFolderId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_L_NotS_First(
			groupId, kbFolderId, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_KBFI_L_NotS_Last(
			long groupId, long kbFolderId, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_L_NotS_Last(
			groupId, kbFolderId, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_KBFI_L_NotS_Last(
		long groupId, long kbFolderId, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_KBFI_L_NotS_Last(
			groupId, kbFolderId, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_KBFI_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_KBFI_L_NotS_PrevAndNext(
			kbArticleId, groupId, kbFolderId, latest, status,
			orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		return getPersistence().filterFindByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end) {

		return getPersistence().filterFindByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_KBFI_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, long kbFolderId, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_KBFI_L_NotS_PrevAndNext(
			kbArticleId, groupId, kbFolderId, latest, status,
			orderByComparator);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		getPersistence().removeByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		return getPersistence().countByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and kbFolderId = &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param kbFolderId the kb folder ID
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_KBFI_L_NotS(
		long groupId, long kbFolderId, boolean latest, int status) {

		return getPersistence().filterCountByG_KBFI_L_NotS(
			groupId, kbFolderId, latest, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		return getPersistence().findByG_LikeS_L_NotS(
			groupId, sections, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end) {

		return getPersistence().findByG_LikeS_L_NotS(
			groupId, sections, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_L_NotS(
			groupId, sections, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_L_NotS(
			groupId, sections, latest, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_L_NotS_First(
			long groupId, String sections, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_L_NotS_First(
			groupId, sections, latest, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_L_NotS_First(
		long groupId, String sections, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_L_NotS_First(
			groupId, sections, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_L_NotS_Last(
			long groupId, String sections, boolean latest, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_L_NotS_Last(
			groupId, sections, latest, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_L_NotS_Last(
		long groupId, String sections, boolean latest, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_L_NotS_Last(
			groupId, sections, latest, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_LikeS_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_L_NotS_PrevAndNext(
			kbArticleId, groupId, sections, latest, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		return getPersistence().filterFindByG_LikeS_L_NotS(
			groupId, sections, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end) {

		return getPersistence().filterFindByG_LikeS_L_NotS(
			groupId, sections, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_L_NotS(
			groupId, sections, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_LikeS_L_NotS_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean latest,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_LikeS_L_NotS_PrevAndNext(
			kbArticleId, groupId, sections, latest, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status) {

		return getPersistence().filterFindByG_LikeS_L_NotS(
			groupId, sectionses, latest, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end) {

		return getPersistence().filterFindByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status) {

		return getPersistence().findByG_LikeS_L_NotS(
			groupId, sectionses, latest, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end) {

		return getPersistence().findByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status,
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_L_NotS(
			groupId, sectionses, latest, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 */
	public static void removeByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		getPersistence().removeByG_LikeS_L_NotS(
			groupId, sections, latest, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		return getPersistence().countByG_LikeS_L_NotS(
			groupId, sections, latest, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status) {

		return getPersistence().countByG_LikeS_L_NotS(
			groupId, sectionses, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_L_NotS(
		long groupId, String sections, boolean latest, int status) {

		return getPersistence().filterCountByG_LikeS_L_NotS(
			groupId, sections, latest, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and latest = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param latest the latest
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_L_NotS(
		long groupId, String[] sectionses, boolean latest, int status) {

		return getPersistence().filterCountByG_LikeS_L_NotS(
			groupId, sectionses, latest, status);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		return getPersistence().findByG_LikeS_M_NotS(
			groupId, sections, main, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end) {

		return getPersistence().findByG_LikeS_M_NotS(
			groupId, sections, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_M_NotS(
			groupId, sections, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_M_NotS(
			groupId, sections, main, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_M_NotS_First(
			long groupId, String sections, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_M_NotS_First(
			groupId, sections, main, status, orderByComparator);
	}

	/**
	 * Returns the first kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_M_NotS_First(
		long groupId, String sections, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_M_NotS_First(
			groupId, sections, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article
	 * @throws NoSuchArticleException if a matching kb article could not be found
	 */
	public static KBArticle findByG_LikeS_M_NotS_Last(
			long groupId, String sections, boolean main, int status,
			OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_M_NotS_Last(
			groupId, sections, main, status, orderByComparator);
	}

	/**
	 * Returns the last kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the last matching kb article, or <code>null</code> if a matching kb article could not be found
	 */
	public static KBArticle fetchByG_LikeS_M_NotS_Last(
		long groupId, String sections, boolean main, int status,
		OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().fetchByG_LikeS_M_NotS_Last(
			groupId, sections, main, status, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] findByG_LikeS_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean main,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByG_LikeS_M_NotS_PrevAndNext(
			kbArticleId, groupId, sections, main, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		return getPersistence().filterFindByG_LikeS_M_NotS(
			groupId, sections, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end) {

		return getPersistence().filterFindByG_LikeS_M_NotS(
			groupId, sections, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permissions to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_M_NotS(
			groupId, sections, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns the kb articles before and after the current kb article in the ordered set of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param kbArticleId the primary key of the current kb article
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the previous, current, and next kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle[] filterFindByG_LikeS_M_NotS_PrevAndNext(
			long kbArticleId, long groupId, String sections, boolean main,
			int status, OrderByComparator<KBArticle> orderByComparator)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().filterFindByG_LikeS_M_NotS_PrevAndNext(
			kbArticleId, groupId, sections, main, status, orderByComparator);
	}

	/**
	 * Returns all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status) {

		return getPersistence().filterFindByG_LikeS_M_NotS(
			groupId, sectionses, main, status);
	}

	/**
	 * Returns a range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end) {

		return getPersistence().filterFindByG_LikeS_M_NotS(
			groupId, sectionses, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles that the user has permission to view
	 */
	public static List<KBArticle> filterFindByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().filterFindByG_LikeS_M_NotS(
			groupId, sectionses, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status) {

		return getPersistence().findByG_LikeS_M_NotS(
			groupId, sectionses, main, status);
	}

	/**
	 * Returns a range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end) {

		return getPersistence().findByG_LikeS_M_NotS(
			groupId, sectionses, main, status, start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findByG_LikeS_M_NotS(
			groupId, sectionses, main, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching kb articles
	 */
	public static List<KBArticle> findByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status, int start,
		int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_LikeS_M_NotS(
			groupId, sectionses, main, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 */
	public static void removeByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		getPersistence().removeByG_LikeS_M_NotS(
			groupId, sections, main, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		return getPersistence().countByG_LikeS_M_NotS(
			groupId, sections, main, status);
	}

	/**
	 * Returns the number of kb articles where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles
	 */
	public static int countByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status) {

		return getPersistence().countByG_LikeS_M_NotS(
			groupId, sectionses, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sections the sections
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_M_NotS(
		long groupId, String sections, boolean main, int status) {

		return getPersistence().filterCountByG_LikeS_M_NotS(
			groupId, sections, main, status);
	}

	/**
	 * Returns the number of kb articles that the user has permission to view where groupId = &#63; and sections LIKE any &#63; and main = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param sectionses the sectionses
	 * @param main the main
	 * @param status the status
	 * @return the number of matching kb articles that the user has permission to view
	 */
	public static int filterCountByG_LikeS_M_NotS(
		long groupId, String[] sectionses, boolean main, int status) {

		return getPersistence().filterCountByG_LikeS_M_NotS(
			groupId, sectionses, main, status);
	}

	/**
	 * Caches the kb article in the entity cache if it is enabled.
	 *
	 * @param kbArticle the kb article
	 */
	public static void cacheResult(KBArticle kbArticle) {
		getPersistence().cacheResult(kbArticle);
	}

	/**
	 * Caches the kb articles in the entity cache if it is enabled.
	 *
	 * @param kbArticles the kb articles
	 */
	public static void cacheResult(List<KBArticle> kbArticles) {
		getPersistence().cacheResult(kbArticles);
	}

	/**
	 * Creates a new kb article with the primary key. Does not add the kb article to the database.
	 *
	 * @param kbArticleId the primary key for the new kb article
	 * @return the new kb article
	 */
	public static KBArticle create(long kbArticleId) {
		return getPersistence().create(kbArticleId);
	}

	/**
	 * Removes the kb article with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article that was removed
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle remove(long kbArticleId)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().remove(kbArticleId);
	}

	public static KBArticle updateImpl(KBArticle kbArticle) {
		return getPersistence().updateImpl(kbArticle);
	}

	/**
	 * Returns the kb article with the primary key or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article
	 * @throws NoSuchArticleException if a kb article with the primary key could not be found
	 */
	public static KBArticle findByPrimaryKey(long kbArticleId)
		throws com.liferay.knowledge.base.exception.NoSuchArticleException {

		return getPersistence().findByPrimaryKey(kbArticleId);
	}

	/**
	 * Returns the kb article with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param kbArticleId the primary key of the kb article
	 * @return the kb article, or <code>null</code> if a kb article with the primary key could not be found
	 */
	public static KBArticle fetchByPrimaryKey(long kbArticleId) {
		return getPersistence().fetchByPrimaryKey(kbArticleId);
	}

	/**
	 * Returns all the kb articles.
	 *
	 * @return the kb articles
	 */
	public static List<KBArticle> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the kb articles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @return the range of kb articles
	 */
	public static List<KBArticle> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the kb articles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of kb articles
	 */
	public static List<KBArticle> findAll(
		int start, int end, OrderByComparator<KBArticle> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the kb articles.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>KBArticleModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of kb articles
	 * @param end the upper bound of the range of kb articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of kb articles
	 */
	public static List<KBArticle> findAll(
		int start, int end, OrderByComparator<KBArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the kb articles from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of kb articles.
	 *
	 * @return the number of kb articles
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static KBArticlePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(KBArticlePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile KBArticlePersistence _persistence;

}