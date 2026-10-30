/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.wiki.model.WikiNode;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the wiki node service. This utility wraps <code>com.liferay.wiki.service.persistence.impl.WikiNodePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see WikiNodePersistence
 * @generated
 */
public class WikiNodeUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<WikiNode> wikiNodes) {
		getPersistence().cacheResult(wikiNodes);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(WikiNode wikiNode) {
		getPersistence().cacheResult(wikiNode);
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
	public static void clearCache(WikiNode wikiNode) {
		getPersistence().clearCache(wikiNode);
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
	public static Map<Serializable, WikiNode> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<WikiNode> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<WikiNode> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<WikiNode> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static WikiNode update(WikiNode wikiNode) {
		return getPersistence().update(wikiNode);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static WikiNode update(
		WikiNode wikiNode, ServiceContext serviceContext) {

		return getPersistence().update(wikiNode, serviceContext);
	}

	/**
	 * Returns an ordered range of all the wiki nodes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki nodes
	 */
	public static List<WikiNode> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WikiNode> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki node in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki node
	 * @throws NoSuchNodeException if a matching wiki node could not be found
	 */
	public static WikiNode findByUuid_First(
			String uuid, OrderByComparator<WikiNode> orderByComparator)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first wiki node in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByUuid_First(
		String uuid, OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the wiki nodes where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of wiki nodes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching wiki nodes
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the wiki node where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchNodeException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching wiki node
	 * @throws NoSuchNodeException if a matching wiki node could not be found
	 */
	public static WikiNode findByUUID_G(String uuid, long groupId)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the wiki node where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the wiki node where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the wiki node that was removed
	 */
	public static WikiNode removeByUUID_G(String uuid, long groupId)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of wiki nodes where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching wiki nodes
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the wiki nodes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki nodes
	 */
	public static List<WikiNode> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiNode> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki node in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki node
	 * @throws NoSuchNodeException if a matching wiki node could not be found
	 */
	public static WikiNode findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<WikiNode> orderByComparator)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first wiki node in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the wiki nodes where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of wiki nodes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching wiki nodes
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the wiki nodes where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki nodes
	 */
	public static List<WikiNode> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<WikiNode> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki node in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki node
	 * @throws NoSuchNodeException if a matching wiki node could not be found
	 */
	public static WikiNode findByGroupId_First(
			long groupId, OrderByComparator<WikiNode> orderByComparator)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first wiki node in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByGroupId_First(
		long groupId, OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki nodes that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki nodes that the user has permission to view
	 */
	public static List<WikiNode> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the wiki nodes where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of wiki nodes where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching wiki nodes
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of wiki nodes that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching wiki nodes that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns the wiki node where groupId = &#63; and name = &#63; or throws a <code>NoSuchNodeException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching wiki node
	 * @throws NoSuchNodeException if a matching wiki node could not be found
	 */
	public static WikiNode findByG_N(long groupId, String name)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().findByG_N(groupId, name);
	}

	/**
	 * Returns the wiki node where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByG_N(
		long groupId, String name, boolean useFinderCache) {

		return getPersistence().fetchByG_N(groupId, name, useFinderCache);
	}

	/**
	 * Removes the wiki node where groupId = &#63; and name = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the wiki node that was removed
	 */
	public static WikiNode removeByG_N(long groupId, String name)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().removeByG_N(groupId, name);
	}

	/**
	 * Returns the number of wiki nodes where groupId = &#63; and name = &#63;.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the number of matching wiki nodes
	 */
	public static int countByG_N(long groupId, String name) {
		return getPersistence().countByG_N(groupId, name);
	}

	/**
	 * Returns an ordered range of all the wiki nodes where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching wiki nodes
	 */
	public static List<WikiNode> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<WikiNode> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first wiki node in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki node
	 * @throws NoSuchNodeException if a matching wiki node could not be found
	 */
	public static WikiNode findByG_S_First(
			long groupId, int status,
			OrderByComparator<WikiNode> orderByComparator)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first wiki node in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the wiki nodes that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki nodes that the user has permission to view
	 */
	public static List<WikiNode> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().filterFindByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the wiki nodes where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of wiki nodes where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching wiki nodes
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns the number of wiki nodes that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching wiki nodes that the user has permission to view
	 */
	public static int filterCountByG_S(long groupId, int status) {
		return getPersistence().filterCountByG_S(groupId, status);
	}

	/**
	 * Returns the wiki node where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchNodeException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching wiki node
	 * @throws NoSuchNodeException if a matching wiki node could not be found
	 */
	public static WikiNode findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the wiki node where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the wiki node where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the wiki node that was removed
	 */
	public static WikiNode removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of wiki nodes where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching wiki nodes
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new wiki node with the primary key. Does not add the wiki node to the database.
	 *
	 * @param nodeId the primary key for the new wiki node
	 * @return the new wiki node
	 */
	public static WikiNode create(long nodeId) {
		return getPersistence().create(nodeId);
	}

	/**
	 * Removes the wiki node with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param nodeId the primary key of the wiki node
	 * @return the wiki node that was removed
	 * @throws NoSuchNodeException if a wiki node with the primary key could not be found
	 */
	public static WikiNode remove(long nodeId)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().remove(nodeId);
	}

	public static WikiNode updateImpl(WikiNode wikiNode) {
		return getPersistence().updateImpl(wikiNode);
	}

	/**
	 * Returns the wiki node with the primary key or throws a <code>NoSuchNodeException</code> if it could not be found.
	 *
	 * @param nodeId the primary key of the wiki node
	 * @return the wiki node
	 * @throws NoSuchNodeException if a wiki node with the primary key could not be found
	 */
	public static WikiNode findByPrimaryKey(long nodeId)
		throws com.liferay.wiki.exception.NoSuchNodeException {

		return getPersistence().findByPrimaryKey(nodeId);
	}

	/**
	 * Returns the wiki node with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param nodeId the primary key of the wiki node
	 * @return the wiki node, or <code>null</code> if a wiki node with the primary key could not be found
	 */
	public static WikiNode fetchByPrimaryKey(long nodeId) {
		return getPersistence().fetchByPrimaryKey(nodeId);
	}

	/**
	 * Returns the wiki node where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the wiki node where groupId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param name the name
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByG_N(long groupId, String name) {
		return getPersistence().fetchByG_N(groupId, name);
	}

	/**
	 * Returns the wiki node where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching wiki node, or <code>null</code> if a matching wiki node could not be found
	 */
	public static WikiNode fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the wiki nodes where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching wiki nodes
	 */
	public static List<WikiNode> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the wiki nodes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @return the range of matching wiki nodes
	 */
	public static List<WikiNode> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the wiki nodes where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki nodes
	 */
	public static List<WikiNode> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the wiki nodes where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching wiki nodes
	 */
	public static List<WikiNode> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the wiki nodes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @return the range of matching wiki nodes
	 */
	public static List<WikiNode> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the wiki nodes where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki nodes
	 */
	public static List<WikiNode> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the wiki nodes where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching wiki nodes
	 */
	public static List<WikiNode> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the wiki nodes where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @return the range of matching wiki nodes
	 */
	public static List<WikiNode> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the wiki nodes where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki nodes
	 */
	public static List<WikiNode> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the wiki nodes that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching wiki nodes that the user has permission to view
	 */
	public static List<WikiNode> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the wiki nodes that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @return the range of matching wiki nodes that the user has permission to view
	 */
	public static List<WikiNode> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the wiki nodes where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching wiki nodes
	 */
	public static List<WikiNode> findByG_S(long groupId, int status) {
		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the wiki nodes where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @return the range of matching wiki nodes
	 */
	public static List<WikiNode> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the wiki nodes where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching wiki nodes
	 */
	public static List<WikiNode> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<WikiNode> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the wiki nodes that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching wiki nodes that the user has permission to view
	 */
	public static List<WikiNode> filterFindByG_S(long groupId, int status) {
		return getPersistence().filterFindByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the wiki nodes that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.wiki.model.impl.WikiNodeModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of wiki nodes
	 * @param end the upper bound of the range of wiki nodes (not inclusive)
	 * @return the range of matching wiki nodes that the user has permission to view
	 */
	public static List<WikiNode> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().filterFindByG_S(groupId, status, start, end);
	}

	public static WikiNodePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(WikiNodePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile WikiNodePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-881485415