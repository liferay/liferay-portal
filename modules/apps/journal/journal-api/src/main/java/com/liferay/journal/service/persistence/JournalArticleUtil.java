/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence;

import com.liferay.journal.model.JournalArticle;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the journal article service. This utility wraps <code>com.liferay.journal.service.persistence.impl.JournalArticlePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see JournalArticlePersistence
 * @generated
 */
public class JournalArticleUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<JournalArticle> journalArticles) {
		getPersistence().cacheResult(journalArticles);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(JournalArticle journalArticle) {
		getPersistence().cacheResult(journalArticle);
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
	public static void clearCache(JournalArticle journalArticle) {
		getPersistence().clearCache(journalArticle);
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
	public static Map<Serializable, JournalArticle> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<JournalArticle> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<JournalArticle> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<JournalArticle> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static JournalArticle update(JournalArticle journalArticle) {
		return getPersistence().update(journalArticle);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static JournalArticle update(
		JournalArticle journalArticle, ServiceContext serviceContext) {

		return getPersistence().update(journalArticle, serviceContext);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByResourcePrimKey_First(
			long resourcePrimKey,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByResourcePrimKey_First(
			resourcePrimKey, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByResourcePrimKey_First(
		long resourcePrimKey,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByResourcePrimKey_First(
			resourcePrimKey, orderByComparator);
	}

	/**
	 * Removes all the journal articles where resourcePrimKey = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 */
	public static void removeByResourcePrimKey(long resourcePrimKey) {
		getPersistence().removeByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the number of matching journal articles
	 */
	public static int countByResourcePrimKey(long resourcePrimKey) {
		return getPersistence().countByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns an ordered range of all the journal articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByUuid_First(
			String uuid, OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByUuid_First(
		String uuid, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the journal articles where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of journal articles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching journal articles
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the journal article where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByUUID_G(String uuid, long groupId)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the journal article where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the journal article where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the journal article that was removed
	 */
	public static JournalArticle removeByUUID_G(String uuid, long groupId)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of journal articles where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching journal articles
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the journal articles where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of journal articles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching journal articles
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByGroupId_First(
			long groupId, OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByGroupId_First(
		long groupId, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal articles
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByCompanyId_First(
			long companyId, OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByCompanyId_First(
		long companyId, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of journal articles where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching journal articles
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the journal articles where DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByDDMStructureId(
		long DDMStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByDDMStructureId(
			DDMStructureId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByDDMStructureId_First(
			long DDMStructureId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByDDMStructureId_First(
			DDMStructureId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByDDMStructureId_First(
		long DDMStructureId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByDDMStructureId_First(
			DDMStructureId, orderByComparator);
	}

	/**
	 * Removes all the journal articles where DDMStructureId = &#63; from the database.
	 *
	 * @param DDMStructureId the ddm structure ID
	 */
	public static void removeByDDMStructureId(long DDMStructureId) {
		getPersistence().removeByDDMStructureId(DDMStructureId);
	}

	/**
	 * Returns the number of journal articles where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching journal articles
	 */
	public static int countByDDMStructureId(long DDMStructureId) {
		return getPersistence().countByDDMStructureId(DDMStructureId);
	}

	/**
	 * Returns an ordered range of all the journal articles where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findBySmallImageId(
		long smallImageId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findBySmallImageId(
			smallImageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findBySmallImageId_First(
			long smallImageId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findBySmallImageId_First(
			smallImageId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchBySmallImageId_First(
		long smallImageId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchBySmallImageId_First(
			smallImageId, orderByComparator);
	}

	/**
	 * Removes all the journal articles where smallImageId = &#63; from the database.
	 *
	 * @param smallImageId the small image ID
	 */
	public static void removeBySmallImageId(long smallImageId) {
		getPersistence().removeBySmallImageId(smallImageId);
	}

	/**
	 * Returns the number of journal articles where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the number of matching journal articles
	 */
	public static int countBySmallImageId(long smallImageId) {
		return getPersistence().countBySmallImageId(smallImageId);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I(
		long resourcePrimKey, boolean indexable, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_I(
			resourcePrimKey, indexable, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByR_I_First(
			long resourcePrimKey, boolean indexable,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByR_I_First(
			resourcePrimKey, indexable, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByR_I_First(
		long resourcePrimKey, boolean indexable,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByR_I_First(
			resourcePrimKey, indexable, orderByComparator);
	}

	/**
	 * Removes all the journal articles where resourcePrimKey = &#63; and indexable = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 */
	public static void removeByR_I(long resourcePrimKey, boolean indexable) {
		getPersistence().removeByR_I(resourcePrimKey, indexable);
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @return the number of matching journal articles
	 */
	public static int countByR_I(long resourcePrimKey, boolean indexable) {
		return getPersistence().countByR_I(resourcePrimKey, indexable);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_ST(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_ST(
			resourcePrimKey, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByR_ST_First(
			long resourcePrimKey, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByR_ST_First(
			resourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByR_ST_First(
		long resourcePrimKey, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByR_ST_First(
			resourcePrimKey, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_ST(
		long resourcePrimKey, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_ST(
			resourcePrimKey, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the journal articles where resourcePrimKey = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 */
	public static void removeByR_ST(long resourcePrimKey, int status) {
		getPersistence().removeByR_ST(resourcePrimKey, status);
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByR_ST(long resourcePrimKey, int status) {
		return getPersistence().countByR_ST(resourcePrimKey, status);
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	public static int countByR_ST(long resourcePrimKey, int[] statuses) {
		return getPersistence().countByR_ST(resourcePrimKey, statuses);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_ERC(
			groupId, externalReferenceCode, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_ERC_First(
			long groupId, String externalReferenceCode,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_First(
			groupId, externalReferenceCode, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_ERC_First(
		long groupId, String externalReferenceCode,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_ERC_First(
			groupId, externalReferenceCode, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_ERC(
			groupId, externalReferenceCode, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and externalReferenceCode = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 */
	public static void removeByG_ERC(
		long groupId, String externalReferenceCode) {

		getPersistence().removeByG_ERC(groupId, externalReferenceCode);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching journal articles
	 */
	public static int countByG_ERC(long groupId, String externalReferenceCode) {
		return getPersistence().countByG_ERC(groupId, externalReferenceCode);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_ERC(
		long groupId, String externalReferenceCode) {

		return getPersistence().filterCountByG_ERC(
			groupId, externalReferenceCode);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F(
			groupId, folderId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_F_First(
			long groupId, long folderId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_F_First(
			groupId, folderId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_F_First(
		long groupId, long folderId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_F_First(
			groupId, folderId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_F(
			groupId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_F(
			groupId, folderIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F(
			groupId, folderIds, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and folderId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 */
	public static void removeByG_F(long groupId, long folderId) {
		getPersistence().removeByG_F(groupId, folderId);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching journal articles
	 */
	public static int countByG_F(long groupId, long folderId) {
		return getPersistence().countByG_F(groupId, folderId);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching journal articles
	 */
	public static int countByG_F(long groupId, long[] folderIds) {
		return getPersistence().countByG_F(groupId, folderIds);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_F(long groupId, long folderId) {
		return getPersistence().filterCountByG_F(groupId, folderId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_F(long groupId, long[] folderIds) {
		return getPersistence().filterCountByG_F(groupId, folderIds);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_A(
			groupId, articleId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_A_First(
			long groupId, String articleId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_A_First(
			groupId, articleId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_A_First(
		long groupId, String articleId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_A_First(
			groupId, articleId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and articleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_A(
			groupId, articleId, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and articleId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 */
	public static void removeByG_A(long groupId, String articleId) {
		getPersistence().removeByG_A(groupId, articleId);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the number of matching journal articles
	 */
	public static int countByG_A(long groupId, String articleId) {
		return getPersistence().countByG_A(groupId, articleId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_A(long groupId, String articleId) {
		return getPersistence().filterCountByG_A(groupId, articleId);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_UT(
		long groupId, String urlTitle, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_UT(
			groupId, urlTitle, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_UT_First(
			long groupId, String urlTitle,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_UT_First(
			groupId, urlTitle, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_UT_First(
		long groupId, String urlTitle,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_UT_First(
			groupId, urlTitle, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_UT(
		long groupId, String urlTitle, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_UT(
			groupId, urlTitle, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and urlTitle = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 */
	public static void removeByG_UT(long groupId, String urlTitle) {
		getPersistence().removeByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the number of matching journal articles
	 */
	public static int countByG_UT(long groupId, String urlTitle) {
		return getPersistence().countByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_UT(long groupId, String urlTitle) {
		return getPersistence().filterCountByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_DDMSI(
		long groupId, long DDMStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_DDMSI(
			groupId, DDMStructureId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_DDMSI_First(
			long groupId, long DDMStructureId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_DDMSI_First(
			groupId, DDMStructureId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_DDMSI_First(
		long groupId, long DDMStructureId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_DDMSI_First(
			groupId, DDMStructureId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_DDMSI(
		long groupId, long DDMStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_DDMSI(
			groupId, DDMStructureId, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and DDMStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 */
	public static void removeByG_DDMSI(long groupId, long DDMStructureId) {
		getPersistence().removeByG_DDMSI(groupId, DDMStructureId);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching journal articles
	 */
	public static int countByG_DDMSI(long groupId, long DDMStructureId) {
		return getPersistence().countByG_DDMSI(groupId, DDMStructureId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_DDMSI(long groupId, long DDMStructureId) {
		return getPersistence().filterCountByG_DDMSI(groupId, DDMStructureId);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_L(
		long groupId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_L(
			groupId, layoutUuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_L_First(
			long groupId, String layoutUuid,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_L_First(
			groupId, layoutUuid, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_L_First(
		long groupId, String layoutUuid,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_L_First(
			groupId, layoutUuid, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_L(
		long groupId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_L(
			groupId, layoutUuid, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and layoutUuid = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 */
	public static void removeByG_L(long groupId, String layoutUuid) {
		getPersistence().removeByG_L(groupId, layoutUuid);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles
	 */
	public static int countByG_L(long groupId, String layoutUuid) {
		return getPersistence().countByG_L(groupId, layoutUuid);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_L(long groupId, String layoutUuid) {
		return getPersistence().filterCountByG_L(groupId, layoutUuid);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByC_V(
		long companyId, double version, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_V(
			companyId, version, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByC_V_First(
			long companyId, double version,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByC_V_First(
			companyId, version, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByC_V_First(
		long companyId, double version,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByC_V_First(
			companyId, version, orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; and version = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 */
	public static void removeByC_V(long companyId, double version) {
		getPersistence().removeByC_V(companyId, version);
	}

	/**
	 * Returns the number of journal articles where companyId = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @return the number of matching journal articles
	 */
	public static int countByC_V(long companyId, double version) {
		return getPersistence().countByC_V(companyId, version);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByC_ST(
		long companyId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_ST(
			companyId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByC_ST_First(
			long companyId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByC_ST_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByC_ST_First(
		long companyId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByC_ST_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public static void removeByC_ST(long companyId, int status) {
		getPersistence().removeByC_ST(companyId, status);
	}

	/**
	 * Returns the number of journal articles where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByC_ST(long companyId, int status) {
		return getPersistence().countByC_ST(companyId, status);
	}

	/**
	 * Returns all the journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByLtD_S(
		Date displayDate, int status) {

		return getPersistence().findByLtD_S(displayDate, status);
	}

	/**
	 * Returns a range of all the journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByLtD_S(
		Date displayDate, int status, int start, int end) {

		return getPersistence().findByLtD_S(displayDate, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByLtD_S(
		Date displayDate, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByLtD_S(
			displayDate, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByLtD_S_First(
			Date displayDate, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByLtD_S_First(
		Date displayDate, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByLtD_S_First(
			displayDate, status, orderByComparator);
	}

	/**
	 * Removes all the journal articles where displayDate &lt; &#63; and status = &#63; from the database.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 */
	public static void removeByLtD_S(Date displayDate, int status) {
		getPersistence().removeByLtD_S(displayDate, status);
	}

	/**
	 * Returns the number of journal articles where displayDate &lt; &#63; and status = &#63;.
	 *
	 * @param displayDate the display date
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByLtD_S(Date displayDate, int status) {
		return getPersistence().countByLtD_S(displayDate, status);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_I_S(
			resourcePrimKey, indexable, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByR_I_S_First(
			long resourcePrimKey, boolean indexable, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByR_I_S_First(
			resourcePrimKey, indexable, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByR_I_S_First(
		long resourcePrimKey, boolean indexable, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByR_I_S_First(
			resourcePrimKey, indexable, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int[] statuses, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByR_I_S(
			resourcePrimKey, indexable, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63; from the database.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 */
	public static void removeByR_I_S(
		long resourcePrimKey, boolean indexable, int status) {

		getPersistence().removeByR_I_S(resourcePrimKey, indexable, status);
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByR_I_S(
		long resourcePrimKey, boolean indexable, int status) {

		return getPersistence().countByR_I_S(
			resourcePrimKey, indexable, status);
	}

	/**
	 * Returns the number of journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	public static int countByR_I_S(
		long resourcePrimKey, boolean indexable, int[] statuses) {

		return getPersistence().countByR_I_S(
			resourcePrimKey, indexable, statuses);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_U_C(
		long groupId, long userId, long classNameId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_C(
			groupId, userId, classNameId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_U_C_First(
			long groupId, long userId, long classNameId,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_U_C_First(
			groupId, userId, classNameId, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_U_C_First(
		long groupId, long userId, long classNameId,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_U_C_First(
			groupId, userId, classNameId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_U_C(
		long groupId, long userId, long classNameId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_U_C(
			groupId, userId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 */
	public static void removeByG_U_C(
		long groupId, long userId, long classNameId) {

		getPersistence().removeByG_U_C(groupId, userId, classNameId);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching journal articles
	 */
	public static int countByG_U_C(
		long groupId, long userId, long classNameId) {

		return getPersistence().countByG_U_C(groupId, userId, classNameId);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_U_C(
		long groupId, long userId, long classNameId) {

		return getPersistence().filterCountByG_U_C(
			groupId, userId, classNameId);
	}

	/**
	 * Returns the journal article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_ERC_V(
			long groupId, String externalReferenceCode, double version)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	/**
	 * Returns the journal article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_ERC_V(
		long groupId, String externalReferenceCode, double version,
		boolean useFinderCache) {

		return getPersistence().fetchByG_ERC_V(
			groupId, externalReferenceCode, version, useFinderCache);
	}

	/**
	 * Removes the journal article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the journal article that was removed
	 */
	public static JournalArticle removeByG_ERC_V(
			long groupId, String externalReferenceCode, double version)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().removeByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and externalReferenceCode = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the number of matching journal articles
	 */
	public static int countByG_ERC_V(
		long groupId, String externalReferenceCode, double version) {

		return getPersistence().countByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_ERC_ST(
			groupId, externalReferenceCode, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_ERC_ST_First(
			long groupId, String externalReferenceCode, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_ERC_ST_First(
			groupId, externalReferenceCode, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_ERC_ST_First(
		long groupId, String externalReferenceCode, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_ERC_ST_First(
			groupId, externalReferenceCode, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC_ST(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_ERC_ST(
			groupId, externalReferenceCode, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_ERC_ST(
			groupId, externalReferenceCode, statuses, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_ERC_ST(
			groupId, externalReferenceCode, statuses, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 */
	public static void removeByG_ERC_ST(
		long groupId, String externalReferenceCode, int status) {

		getPersistence().removeByG_ERC_ST(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByG_ERC_ST(
		long groupId, String externalReferenceCode, int status) {

		return getPersistence().countByG_ERC_ST(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	public static int countByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses) {

		return getPersistence().countByG_ERC_ST(
			groupId, externalReferenceCode, statuses);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_ERC_ST(
		long groupId, String externalReferenceCode, int status) {

		return getPersistence().filterCountByG_ERC_ST(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses) {

		return getPersistence().filterCountByG_ERC_ST(
			groupId, externalReferenceCode, statuses);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F_ST(
			groupId, folderId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_F_ST_First(
			long groupId, long folderId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_F_ST_First(
			groupId, folderId, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_F_ST_First(
		long groupId, long folderId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_F_ST_First(
			groupId, folderId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_ST(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_F_ST(
			groupId, folderId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_ST(
		long groupId, long folderId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_F_ST(
			groupId, folderId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F_ST(
			groupId, folderId, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 */
	public static void removeByG_F_ST(long groupId, long folderId, int status) {
		getPersistence().removeByG_F_ST(groupId, folderId, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByG_F_ST(long groupId, long folderId, int status) {
		return getPersistence().countByG_F_ST(groupId, folderId, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	public static int countByG_F_ST(
		long groupId, long folderId, int[] statuses) {

		return getPersistence().countByG_F_ST(groupId, folderId, statuses);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_F_ST(
		long groupId, long folderId, int status) {

		return getPersistence().filterCountByG_F_ST(groupId, folderId, status);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_F_ST(
		long groupId, long folderId, int[] statuses) {

		return getPersistence().filterCountByG_F_ST(
			groupId, folderId, statuses);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_C(
			groupId, classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_C_C_First(
			long groupId, long classNameId, long classPK,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_C_C_First(
			groupId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_C_C_First(
		long groupId, long classNameId, long classPK,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_C_C_First(
			groupId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_C_C(
			groupId, classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByG_C_C(
		long groupId, long classNameId, long classPK) {

		getPersistence().removeByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching journal articles
	 */
	public static int countByG_C_C(
		long groupId, long classNameId, long classPK) {

		return getPersistence().countByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_C_C(
		long groupId, long classNameId, long classPK) {

		return getPersistence().filterCountByG_C_C(
			groupId, classNameId, classPK);
	}

	/**
	 * Returns the journal article where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_C_DDMSI(
			long groupId, long classNameId, long DDMStructureId)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_C_DDMSI(
			groupId, classNameId, DDMStructureId);
	}

	/**
	 * Returns the journal article where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_C_DDMSI(
		long groupId, long classNameId, long DDMStructureId,
		boolean useFinderCache) {

		return getPersistence().fetchByG_C_DDMSI(
			groupId, classNameId, DDMStructureId, useFinderCache);
	}

	/**
	 * Removes the journal article where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the journal article that was removed
	 */
	public static JournalArticle removeByG_C_DDMSI(
			long groupId, long classNameId, long DDMStructureId)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().removeByG_C_DDMSI(
			groupId, classNameId, DDMStructureId);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the number of matching journal articles
	 */
	public static int countByG_C_DDMSI(
		long groupId, long classNameId, long DDMStructureId) {

		return getPersistence().countByG_C_DDMSI(
			groupId, classNameId, DDMStructureId);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		return getPersistence().findByG_C_NotL(
			groupId, classNameId, layoutUuid);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end) {

		return getPersistence().findByG_C_NotL(
			groupId, classNameId, layoutUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_C_NotL(
			groupId, classNameId, layoutUuid, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_NotL(
			groupId, classNameId, layoutUuid, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_C_NotL_First(
			long groupId, long classNameId, String layoutUuid,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_C_NotL_First(
			groupId, classNameId, layoutUuid, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_C_NotL_First(
		long groupId, long classNameId, String layoutUuid,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_C_NotL_First(
			groupId, classNameId, layoutUuid, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		return getPersistence().filterFindByG_C_NotL(
			groupId, classNameId, layoutUuid);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end) {

		return getPersistence().filterFindByG_C_NotL(
			groupId, classNameId, layoutUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_C_NotL(
			groupId, classNameId, layoutUuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids) {

		return getPersistence().filterFindByG_C_NotL(
			groupId, classNameId, layoutUuids);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end) {

		return getPersistence().filterFindByG_C_NotL(
			groupId, classNameId, layoutUuids, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_C_NotL(
			groupId, classNameId, layoutUuids, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids) {

		return getPersistence().findByG_C_NotL(
			groupId, classNameId, layoutUuids);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end) {

		return getPersistence().findByG_C_NotL(
			groupId, classNameId, layoutUuids, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_C_NotL(
			groupId, classNameId, layoutUuids, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_NotL(
			groupId, classNameId, layoutUuids, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 */
	public static void removeByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		getPersistence().removeByG_C_NotL(groupId, classNameId, layoutUuid);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles
	 */
	public static int countByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		return getPersistence().countByG_C_NotL(
			groupId, classNameId, layoutUuid);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @return the number of matching journal articles
	 */
	public static int countByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids) {

		return getPersistence().countByG_C_NotL(
			groupId, classNameId, layoutUuids);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuid the layout uuid
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_C_NotL(
		long groupId, long classNameId, String layoutUuid) {

		return getPersistence().filterCountByG_C_NotL(
			groupId, classNameId, layoutUuid);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and layoutUuid &ne; all &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param layoutUuids the layout uuids
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_C_NotL(
		long groupId, long classNameId, String[] layoutUuids) {

		return getPersistence().filterCountByG_C_NotL(
			groupId, classNameId, layoutUuids);
	}

	/**
	 * Returns the journal article where groupId = &#63; and articleId = &#63; and version = &#63; or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @return the matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_A_V(
			long groupId, String articleId, double version)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_A_V(groupId, articleId, version);
	}

	/**
	 * Returns the journal article where groupId = &#63; and articleId = &#63; and version = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_A_V(
		long groupId, String articleId, double version,
		boolean useFinderCache) {

		return getPersistence().fetchByG_A_V(
			groupId, articleId, version, useFinderCache);
	}

	/**
	 * Removes the journal article where groupId = &#63; and articleId = &#63; and version = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @return the journal article that was removed
	 */
	public static JournalArticle removeByG_A_V(
			long groupId, String articleId, double version)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().removeByG_A_V(groupId, articleId, version);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63; and version = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @return the number of matching journal articles
	 */
	public static int countByG_A_V(
		long groupId, String articleId, double version) {

		return getPersistence().countByG_A_V(groupId, articleId, version);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_A_ST(
			groupId, articleId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_A_ST_First(
			long groupId, String articleId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_A_ST_First(
			groupId, articleId, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_A_ST_First(
		long groupId, String articleId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_A_ST_First(
			groupId, articleId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_ST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_A_ST(
			groupId, articleId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_ST(
		long groupId, String articleId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_A_ST(
			groupId, articleId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_A_ST(
			groupId, articleId, statuses, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 */
	public static void removeByG_A_ST(
		long groupId, String articleId, int status) {

		getPersistence().removeByG_A_ST(groupId, articleId, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByG_A_ST(
		long groupId, String articleId, int status) {

		return getPersistence().countByG_A_ST(groupId, articleId, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @return the number of matching journal articles
	 */
	public static int countByG_A_ST(
		long groupId, String articleId, int[] statuses) {

		return getPersistence().countByG_A_ST(groupId, articleId, statuses);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_A_ST(
		long groupId, String articleId, int status) {

		return getPersistence().filterCountByG_A_ST(groupId, articleId, status);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_A_ST(
		long groupId, String articleId, int[] statuses) {

		return getPersistence().filterCountByG_A_ST(
			groupId, articleId, statuses);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_A_NotST(
		long groupId, String articleId, int status) {

		return getPersistence().findByG_A_NotST(groupId, articleId, status);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_NotST(
		long groupId, String articleId, int status, int start, int end) {

		return getPersistence().findByG_A_NotST(
			groupId, articleId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_NotST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_A_NotST(
			groupId, articleId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_NotST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_A_NotST(
			groupId, articleId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_A_NotST_First(
			long groupId, String articleId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_A_NotST_First(
			groupId, articleId, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_A_NotST_First(
		long groupId, String articleId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_A_NotST_First(
			groupId, articleId, status, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_NotST(
		long groupId, String articleId, int status) {

		return getPersistence().filterFindByG_A_NotST(
			groupId, articleId, status);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_NotST(
		long groupId, String articleId, int status, int start, int end) {

		return getPersistence().filterFindByG_A_NotST(
			groupId, articleId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_NotST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_A_NotST(
			groupId, articleId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 */
	public static void removeByG_A_NotST(
		long groupId, String articleId, int status) {

		getPersistence().removeByG_A_NotST(groupId, articleId, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByG_A_NotST(
		long groupId, String articleId, int status) {

		return getPersistence().countByG_A_NotST(groupId, articleId, status);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_A_NotST(
		long groupId, String articleId, int status) {

		return getPersistence().filterCountByG_A_NotST(
			groupId, articleId, status);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_UT_ST(
		long groupId, String urlTitle, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_UT_ST(
			groupId, urlTitle, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_UT_ST_First(
			long groupId, String urlTitle, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_UT_ST_First(
			groupId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_UT_ST_First(
		long groupId, String urlTitle, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_UT_ST_First(
			groupId, urlTitle, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_UT_ST(
		long groupId, String urlTitle, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_UT_ST(
			groupId, urlTitle, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 */
	public static void removeByG_UT_ST(
		long groupId, String urlTitle, int status) {

		getPersistence().removeByG_UT_ST(groupId, urlTitle, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByG_UT_ST(
		long groupId, String urlTitle, int status) {

		return getPersistence().countByG_UT_ST(groupId, urlTitle, status);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_UT_ST(
		long groupId, String urlTitle, int status) {

		return getPersistence().filterCountByG_UT_ST(groupId, urlTitle, status);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByC_V_ST(
		long companyId, double version, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_V_ST(
			companyId, version, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByC_V_ST_First(
			long companyId, double version, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByC_V_ST_First(
			companyId, version, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByC_V_ST_First(
		long companyId, double version, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByC_V_ST_First(
			companyId, version, status, orderByComparator);
	}

	/**
	 * Removes all the journal articles where companyId = &#63; and version = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 */
	public static void removeByC_V_ST(
		long companyId, double version, int status) {

		getPersistence().removeByC_V_ST(companyId, version, status);
	}

	/**
	 * Returns the number of journal articles where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByC_V_ST(
		long companyId, double version, int status) {

		return getPersistence().countByC_V_ST(companyId, version, status);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		return getPersistence().findByG_F_C_NotST(
			groupId, folderId, classNameId, status);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end) {

		return getPersistence().findByG_F_C_NotST(
			groupId, folderId, classNameId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_F_C_NotST(
			groupId, folderId, classNameId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_F_C_NotST(
			groupId, folderId, classNameId, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article
	 * @throws NoSuchArticleException if a matching journal article could not be found
	 */
	public static JournalArticle findByG_F_C_NotST_First(
			long groupId, long folderId, long classNameId, int status,
			OrderByComparator<JournalArticle> orderByComparator)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByG_F_C_NotST_First(
			groupId, folderId, classNameId, status, orderByComparator);
	}

	/**
	 * Returns the first journal article in the ordered set where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_F_C_NotST_First(
		long groupId, long folderId, long classNameId, int status,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().fetchByG_F_C_NotST_First(
			groupId, folderId, classNameId, status, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		return getPersistence().filterFindByG_F_C_NotST(
			groupId, folderId, classNameId, status);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end) {

		return getPersistence().filterFindByG_F_C_NotST(
			groupId, folderId, classNameId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles that the user has permissions to view where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().filterFindByG_F_C_NotST(
			groupId, folderId, classNameId, status, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 */
	public static void removeByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		getPersistence().removeByG_F_C_NotST(
			groupId, folderId, classNameId, status);
	}

	/**
	 * Returns the number of journal articles where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the number of matching journal articles
	 */
	public static int countByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		return getPersistence().countByG_F_C_NotST(
			groupId, folderId, classNameId, status);
	}

	/**
	 * Returns the number of journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and classNameId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param classNameId the class name ID
	 * @param status the status
	 * @return the number of matching journal articles that the user has permission to view
	 */
	public static int filterCountByG_F_C_NotST(
		long groupId, long folderId, long classNameId, int status) {

		return getPersistence().filterCountByG_F_C_NotST(
			groupId, folderId, classNameId, status);
	}

	/**
	 * Creates a new journal article with the primary key. Does not add the journal article to the database.
	 *
	 * @param id the primary key for the new journal article
	 * @return the new journal article
	 */
	public static JournalArticle create(long id) {
		return getPersistence().create(id);
	}

	/**
	 * Removes the journal article with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param id the primary key of the journal article
	 * @return the journal article that was removed
	 * @throws NoSuchArticleException if a journal article with the primary key could not be found
	 */
	public static JournalArticle remove(long id)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().remove(id);
	}

	public static JournalArticle updateImpl(JournalArticle journalArticle) {
		return getPersistence().updateImpl(journalArticle);
	}

	/**
	 * Returns the journal article with the primary key or throws a <code>NoSuchArticleException</code> if it could not be found.
	 *
	 * @param id the primary key of the journal article
	 * @return the journal article
	 * @throws NoSuchArticleException if a journal article with the primary key could not be found
	 */
	public static JournalArticle findByPrimaryKey(long id)
		throws com.liferay.journal.exception.NoSuchArticleException {

		return getPersistence().findByPrimaryKey(id);
	}

	/**
	 * Returns the journal article with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param id the primary key of the journal article
	 * @return the journal article, or <code>null</code> if a journal article with the primary key could not be found
	 */
	public static JournalArticle fetchByPrimaryKey(long id) {
		return getPersistence().fetchByPrimaryKey(id);
	}

	/**
	 * Returns the journal article where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the journal article where groupId = &#63; and externalReferenceCode = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param version the version
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_ERC_V(
		long groupId, String externalReferenceCode, double version) {

		return getPersistence().fetchByG_ERC_V(
			groupId, externalReferenceCode, version);
	}

	/**
	 * Returns the journal article where groupId = &#63; and classNameId = &#63; and DDMStructureId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_C_DDMSI(
		long groupId, long classNameId, long DDMStructureId) {

		return getPersistence().fetchByG_C_DDMSI(
			groupId, classNameId, DDMStructureId);
	}

	/**
	 * Returns the journal article where groupId = &#63; and articleId = &#63; and version = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param version the version
	 * @return the matching journal article, or <code>null</code> if a matching journal article could not be found
	 */
	public static JournalArticle fetchByG_A_V(
		long groupId, String articleId, double version) {

		return getPersistence().fetchByG_A_V(groupId, articleId, version);
	}

	/**
	 * Returns all the journal articles where resourcePrimKey = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByResourcePrimKey(
		long resourcePrimKey) {

		return getPersistence().findByResourcePrimKey(resourcePrimKey);
	}

	/**
	 * Returns a range of all the journal articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByResourcePrimKey(
		long resourcePrimKey, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByResourcePrimKey(
			resourcePrimKey, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the journal articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the journal articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the journal articles where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the journal articles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where DDMStructureId = &#63;.
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByDDMStructureId(
		long DDMStructureId) {

		return getPersistence().findByDDMStructureId(DDMStructureId);
	}

	/**
	 * Returns a range of all the journal articles where DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByDDMStructureId(
		long DDMStructureId, int start, int end) {

		return getPersistence().findByDDMStructureId(
			DDMStructureId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByDDMStructureId(
		long DDMStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByDDMStructureId(
			DDMStructureId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where smallImageId = &#63;.
	 *
	 * @param smallImageId the small image ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findBySmallImageId(long smallImageId) {
		return getPersistence().findBySmallImageId(smallImageId);
	}

	/**
	 * Returns a range of all the journal articles where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findBySmallImageId(
		long smallImageId, int start, int end) {

		return getPersistence().findBySmallImageId(smallImageId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where smallImageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param smallImageId the small image ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findBySmallImageId(
		long smallImageId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findBySmallImageId(
			smallImageId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByR_I(
		long resourcePrimKey, boolean indexable) {

		return getPersistence().findByR_I(resourcePrimKey, indexable);
	}

	/**
	 * Returns a range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I(
		long resourcePrimKey, boolean indexable, int start, int end) {

		return getPersistence().findByR_I(
			resourcePrimKey, indexable, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I(
		long resourcePrimKey, boolean indexable, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByR_I(
			resourcePrimKey, indexable, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByR_ST(
		long resourcePrimKey, int status) {

		return getPersistence().findByR_ST(resourcePrimKey, status);
	}

	/**
	 * Returns a range of all the journal articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByR_ST(
		long resourcePrimKey, int status, int start, int end) {

		return getPersistence().findByR_ST(resourcePrimKey, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_ST(
		long resourcePrimKey, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByR_ST(
			resourcePrimKey, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where resourcePrimKey = &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param statuses the statuses
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByR_ST(
		long resourcePrimKey, int[] statuses) {

		return getPersistence().findByR_ST(resourcePrimKey, statuses);
	}

	/**
	 * Returns a range of all the journal articles where resourcePrimKey = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByR_ST(
		long resourcePrimKey, int[] statuses, int start, int end) {

		return getPersistence().findByR_ST(
			resourcePrimKey, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_ST(
		long resourcePrimKey, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByR_ST(
			resourcePrimKey, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC(
		long groupId, String externalReferenceCode) {

		return getPersistence().findByG_ERC(groupId, externalReferenceCode);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end) {

		return getPersistence().findByG_ERC(
			groupId, externalReferenceCode, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC(
		long groupId, String externalReferenceCode, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_ERC(
			groupId, externalReferenceCode, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode) {

		return getPersistence().filterFindByG_ERC(
			groupId, externalReferenceCode);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC(
		long groupId, String externalReferenceCode, int start, int end) {

		return getPersistence().filterFindByG_ERC(
			groupId, externalReferenceCode, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_F(long groupId, long folderId) {
		return getPersistence().findByG_F(groupId, folderId);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F(
		long groupId, long folderId, int start, int end) {

		return getPersistence().findByG_F(groupId, folderId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F(
		long groupId, long folderId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_F(
			groupId, folderId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_F(
		long groupId, long[] folderIds) {

		return getPersistence().findByG_F(groupId, folderIds);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F(
		long groupId, long[] folderIds, int start, int end) {

		return getPersistence().findByG_F(groupId, folderIds, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F(
		long groupId, long[] folderIds, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_F(
			groupId, folderIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F(
		long groupId, long folderId) {

		return getPersistence().filterFindByG_F(groupId, folderId);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F(
		long groupId, long folderId, int start, int end) {

		return getPersistence().filterFindByG_F(groupId, folderId, start, end);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F(
		long groupId, long[] folderIds) {

		return getPersistence().filterFindByG_F(groupId, folderIds);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderIds the folder IDs
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F(
		long groupId, long[] folderIds, int start, int end) {

		return getPersistence().filterFindByG_F(groupId, folderIds, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_A(
		long groupId, String articleId) {

		return getPersistence().findByG_A(groupId, articleId);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and articleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A(
		long groupId, String articleId, int start, int end) {

		return getPersistence().findByG_A(groupId, articleId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A(
		long groupId, String articleId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_A(
			groupId, articleId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A(
		long groupId, String articleId) {

		return getPersistence().filterFindByG_A(groupId, articleId);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A(
		long groupId, String articleId, int start, int end) {

		return getPersistence().filterFindByG_A(groupId, articleId, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_UT(
		long groupId, String urlTitle) {

		return getPersistence().findByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_UT(
		long groupId, String urlTitle, int start, int end) {

		return getPersistence().findByG_UT(groupId, urlTitle, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_UT(
		long groupId, String urlTitle, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_UT(
			groupId, urlTitle, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and urlTitle = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_UT(
		long groupId, String urlTitle) {

		return getPersistence().filterFindByG_UT(groupId, urlTitle);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and urlTitle = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_UT(
		long groupId, String urlTitle, int start, int end) {

		return getPersistence().filterFindByG_UT(groupId, urlTitle, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_DDMSI(
		long groupId, long DDMStructureId) {

		return getPersistence().findByG_DDMSI(groupId, DDMStructureId);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_DDMSI(
		long groupId, long DDMStructureId, int start, int end) {

		return getPersistence().findByG_DDMSI(
			groupId, DDMStructureId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_DDMSI(
		long groupId, long DDMStructureId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_DDMSI(
			groupId, DDMStructureId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_DDMSI(
		long groupId, long DDMStructureId) {

		return getPersistence().filterFindByG_DDMSI(groupId, DDMStructureId);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and DDMStructureId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param DDMStructureId the ddm structure ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_DDMSI(
		long groupId, long DDMStructureId, int start, int end) {

		return getPersistence().filterFindByG_DDMSI(
			groupId, DDMStructureId, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_L(
		long groupId, String layoutUuid) {

		return getPersistence().findByG_L(groupId, layoutUuid);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_L(
		long groupId, String layoutUuid, int start, int end) {

		return getPersistence().findByG_L(groupId, layoutUuid, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_L(
		long groupId, String layoutUuid, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_L(
			groupId, layoutUuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_L(
		long groupId, String layoutUuid) {

		return getPersistence().filterFindByG_L(groupId, layoutUuid);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and layoutUuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param layoutUuid the layout uuid
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_L(
		long groupId, String layoutUuid, int start, int end) {

		return getPersistence().filterFindByG_L(
			groupId, layoutUuid, start, end);
	}

	/**
	 * Returns all the journal articles where companyId = &#63; and version = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByC_V(
		long companyId, double version) {

		return getPersistence().findByC_V(companyId, version);
	}

	/**
	 * Returns a range of all the journal articles where companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByC_V(
		long companyId, double version, int start, int end) {

		return getPersistence().findByC_V(companyId, version, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and version = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByC_V(
		long companyId, double version, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByC_V(
			companyId, version, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByC_ST(long companyId, int status) {
		return getPersistence().findByC_ST(companyId, status);
	}

	/**
	 * Returns a range of all the journal articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByC_ST(
		long companyId, int status, int start, int end) {

		return getPersistence().findByC_ST(companyId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByC_ST(
		long companyId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByC_ST(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int status) {

		return getPersistence().findByR_I_S(resourcePrimKey, indexable, status);
	}

	/**
	 * Returns a range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int status, int start,
		int end) {

		return getPersistence().findByR_I_S(
			resourcePrimKey, indexable, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByR_I_S(
			resourcePrimKey, indexable, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = any &#63;.
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param statuses the statuses
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int[] statuses) {

		return getPersistence().findByR_I_S(
			resourcePrimKey, indexable, statuses);
	}

	/**
	 * Returns a range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int[] statuses, int start,
		int end) {

		return getPersistence().findByR_I_S(
			resourcePrimKey, indexable, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where resourcePrimKey = &#63; and indexable = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param resourcePrimKey the resource prim key
	 * @param indexable the indexable
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByR_I_S(
		long resourcePrimKey, boolean indexable, int[] statuses, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByR_I_S(
			resourcePrimKey, indexable, statuses, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_U_C(
		long groupId, long userId, long classNameId) {

		return getPersistence().findByG_U_C(groupId, userId, classNameId);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_U_C(
		long groupId, long userId, long classNameId, int start, int end) {

		return getPersistence().findByG_U_C(
			groupId, userId, classNameId, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_U_C(
		long groupId, long userId, long classNameId, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_U_C(
			groupId, userId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_U_C(
		long groupId, long userId, long classNameId) {

		return getPersistence().filterFindByG_U_C(groupId, userId, classNameId);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_U_C(
		long groupId, long userId, long classNameId, int start, int end) {

		return getPersistence().filterFindByG_U_C(
			groupId, userId, classNameId, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int status) {

		return getPersistence().findByG_ERC_ST(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int status, int start,
		int end) {

		return getPersistence().findByG_ERC_ST(
			groupId, externalReferenceCode, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int status, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_ERC_ST(
			groupId, externalReferenceCode, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses) {

		return getPersistence().findByG_ERC_ST(
			groupId, externalReferenceCode, statuses);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses, int start,
		int end) {

		return getPersistence().findByG_ERC_ST(
			groupId, externalReferenceCode, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses, int start,
		int end, OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_ERC_ST(
			groupId, externalReferenceCode, statuses, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC_ST(
		long groupId, String externalReferenceCode, int status) {

		return getPersistence().filterFindByG_ERC_ST(
			groupId, externalReferenceCode, status);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC_ST(
		long groupId, String externalReferenceCode, int status, int start,
		int end) {

		return getPersistence().filterFindByG_ERC_ST(
			groupId, externalReferenceCode, status, start, end);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses) {

		return getPersistence().filterFindByG_ERC_ST(
			groupId, externalReferenceCode, statuses);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and externalReferenceCode = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param externalReferenceCode the external reference code
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_ERC_ST(
		long groupId, String externalReferenceCode, int[] statuses, int start,
		int end) {

		return getPersistence().filterFindByG_ERC_ST(
			groupId, externalReferenceCode, statuses, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int status) {

		return getPersistence().findByG_F_ST(groupId, folderId, status);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int status, int start, int end) {

		return getPersistence().findByG_F_ST(
			groupId, folderId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_F_ST(
			groupId, folderId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int[] statuses) {

		return getPersistence().findByG_F_ST(groupId, folderId, statuses);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int[] statuses, int start, int end) {

		return getPersistence().findByG_F_ST(
			groupId, folderId, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_F_ST(
		long groupId, long folderId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_F_ST(
			groupId, folderId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_ST(
		long groupId, long folderId, int status) {

		return getPersistence().filterFindByG_F_ST(groupId, folderId, status);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_ST(
		long groupId, long folderId, int status, int start, int end) {

		return getPersistence().filterFindByG_F_ST(
			groupId, folderId, status, start, end);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_ST(
		long groupId, long folderId, int[] statuses) {

		return getPersistence().filterFindByG_F_ST(groupId, folderId, statuses);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and folderId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param folderId the folder ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_F_ST(
		long groupId, long folderId, int[] statuses, int start, int end) {

		return getPersistence().filterFindByG_F_ST(
			groupId, folderId, statuses, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_C_C(
		long groupId, long classNameId, long classPK) {

		return getPersistence().findByG_C_C(groupId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end) {

		return getPersistence().findByG_C_C(
			groupId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_C_C(
			groupId, classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_C(
		long groupId, long classNameId, long classPK) {

		return getPersistence().filterFindByG_C_C(
			groupId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_C_C(
		long groupId, long classNameId, long classPK, int start, int end) {

		return getPersistence().filterFindByG_C_C(
			groupId, classNameId, classPK, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int status) {

		return getPersistence().findByG_A_ST(groupId, articleId, status);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int status, int start, int end) {

		return getPersistence().findByG_A_ST(
			groupId, articleId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_A_ST(
			groupId, articleId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int[] statuses) {

		return getPersistence().findByG_A_ST(groupId, articleId, statuses);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int[] statuses, int start, int end) {

		return getPersistence().findByG_A_ST(
			groupId, articleId, statuses, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_A_ST(
		long groupId, String articleId, int[] statuses, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_A_ST(
			groupId, articleId, statuses, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_ST(
		long groupId, String articleId, int status) {

		return getPersistence().filterFindByG_A_ST(groupId, articleId, status);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_ST(
		long groupId, String articleId, int status, int start, int end) {

		return getPersistence().filterFindByG_A_ST(
			groupId, articleId, status, start, end);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_ST(
		long groupId, String articleId, int[] statuses) {

		return getPersistence().filterFindByG_A_ST(
			groupId, articleId, statuses);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and articleId = &#63; and status = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param articleId the article ID
	 * @param statuses the statuses
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_A_ST(
		long groupId, String articleId, int[] statuses, int start, int end) {

		return getPersistence().filterFindByG_A_ST(
			groupId, articleId, statuses, start, end);
	}

	/**
	 * Returns all the journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByG_UT_ST(
		long groupId, String urlTitle, int status) {

		return getPersistence().findByG_UT_ST(groupId, urlTitle, status);
	}

	/**
	 * Returns a range of all the journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByG_UT_ST(
		long groupId, String urlTitle, int status, int start, int end) {

		return getPersistence().findByG_UT_ST(
			groupId, urlTitle, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByG_UT_ST(
		long groupId, String urlTitle, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByG_UT_ST(
			groupId, urlTitle, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the journal articles that the user has permission to view where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @return the matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_UT_ST(
		long groupId, String urlTitle, int status) {

		return getPersistence().filterFindByG_UT_ST(groupId, urlTitle, status);
	}

	/**
	 * Returns a range of all the journal articles that the user has permission to view where groupId = &#63; and urlTitle = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param urlTitle the url title
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles that the user has permission to view
	 */
	public static List<JournalArticle> filterFindByG_UT_ST(
		long groupId, String urlTitle, int status, int start, int end) {

		return getPersistence().filterFindByG_UT_ST(
			groupId, urlTitle, status, start, end);
	}

	/**
	 * Returns all the journal articles where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @return the matching journal articles
	 */
	public static List<JournalArticle> findByC_V_ST(
		long companyId, double version, int status) {

		return getPersistence().findByC_V_ST(companyId, version, status);
	}

	/**
	 * Returns a range of all the journal articles where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @return the range of matching journal articles
	 */
	public static List<JournalArticle> findByC_V_ST(
		long companyId, double version, int status, int start, int end) {

		return getPersistence().findByC_V_ST(
			companyId, version, status, start, end);
	}

	/**
	 * Returns an ordered range of all the journal articles where companyId = &#63; and version = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.journal.model.impl.JournalArticleModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param version the version
	 * @param status the status
	 * @param start the lower bound of the range of journal articles
	 * @param end the upper bound of the range of journal articles (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching journal articles
	 */
	public static List<JournalArticle> findByC_V_ST(
		long companyId, double version, int status, int start, int end,
		OrderByComparator<JournalArticle> orderByComparator) {

		return getPersistence().findByC_V_ST(
			companyId, version, status, start, end, orderByComparator);
	}

	public static JournalArticlePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(JournalArticlePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile JournalArticlePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:775166851