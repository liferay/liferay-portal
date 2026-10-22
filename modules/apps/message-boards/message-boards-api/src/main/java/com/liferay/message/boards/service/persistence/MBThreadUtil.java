/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence;

import com.liferay.message.boards.model.MBThread;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the message boards thread service. This utility wraps <code>com.liferay.message.boards.service.persistence.impl.MBThreadPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MBThreadPersistence
 * @generated
 */
public class MBThreadUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<MBThread> mbThreads) {
		getPersistence().cacheResult(mbThreads);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(MBThread mbThread) {
		getPersistence().cacheResult(mbThread);
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
	public static void clearCache(MBThread mbThread) {
		getPersistence().clearCache(mbThread);
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
	public static Map<Serializable, MBThread> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<MBThread> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<MBThread> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<MBThread> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static MBThread update(MBThread mbThread) {
		return getPersistence().update(mbThread);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static MBThread update(
		MBThread mbThread, ServiceContext serviceContext) {

		return getPersistence().update(mbThread, serviceContext);
	}

	/**
	 * Returns an ordered range of all the message boards threads where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByUuid_First(
			String uuid, OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByUuid_First(
		String uuid, OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the message boards threads where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of message boards threads where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message boards threads
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the message boards thread where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchThreadException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByUUID_G(String uuid, long groupId)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the message boards thread where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the message boards thread where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message boards thread that was removed
	 */
	public static MBThread removeByUUID_G(String uuid, long groupId)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of message boards threads where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message boards threads
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the message boards threads where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message boards threads
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByGroupId_First(
			long groupId, OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByGroupId_First(
		long groupId, OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the message boards threads where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards threads
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns the message boards thread where rootMessageId = &#63; or throws a <code>NoSuchThreadException</code> if it could not be found.
	 *
	 * @param rootMessageId the root message ID
	 * @return the matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByRootMessageId(long rootMessageId)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByRootMessageId(rootMessageId);
	}

	/**
	 * Returns the message boards thread where rootMessageId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param rootMessageId the root message ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByRootMessageId(
		long rootMessageId, boolean useFinderCache) {

		return getPersistence().fetchByRootMessageId(
			rootMessageId, useFinderCache);
	}

	/**
	 * Removes the message boards thread where rootMessageId = &#63; from the database.
	 *
	 * @param rootMessageId the root message ID
	 * @return the message boards thread that was removed
	 */
	public static MBThread removeByRootMessageId(long rootMessageId)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().removeByRootMessageId(rootMessageId);
	}

	/**
	 * Returns the number of message boards threads where rootMessageId = &#63;.
	 *
	 * @param rootMessageId the root message ID
	 * @return the number of matching message boards threads
	 */
	public static int countByRootMessageId(long rootMessageId) {
		return getPersistence().countByRootMessageId(rootMessageId);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_C(
			groupId, categoryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByG_C_First(
			long groupId, long categoryId,
			OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByG_C_First(
			groupId, categoryId, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByG_C_First(
		long groupId, long categoryId,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByG_C_First(
			groupId, categoryId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByG_C(
			groupId, categoryId, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C(
		long groupId, long[] categoryIds, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByG_C(
			groupId, categoryIds, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C(
		long groupId, long[] categoryIds, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_C(
			groupId, categoryIds, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the message boards threads where groupId = &#63; and categoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 */
	public static void removeByG_C(long groupId, long categoryId) {
		getPersistence().removeByG_C(groupId, categoryId);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message boards threads
	 */
	public static int countByG_C(long groupId, long categoryId) {
		return getPersistence().countByG_C(groupId, categoryId);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @return the number of matching message boards threads
	 */
	public static int countByG_C(long groupId, long[] categoryIds) {
		return getPersistence().countByG_C(groupId, categoryIds);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByG_C(long groupId, long categoryId) {
		return getPersistence().filterCountByG_C(groupId, categoryId);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByG_C(long groupId, long[] categoryIds) {
		return getPersistence().filterCountByG_C(groupId, categoryIds);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByG_S_First(
			long groupId, int status,
			OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the message boards threads where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByG_S(long groupId, int status) {
		return getPersistence().filterCountByG_S(groupId, status);
	}

	/**
	 * Returns an ordered range of all the message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByL_P(
		Date lastPostDate, double priority, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByL_P(
			lastPostDate, priority, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where lastPostDate = &#63; and priority = &#63;.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByL_P_First(
			Date lastPostDate, double priority,
			OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByL_P_First(
			lastPostDate, priority, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where lastPostDate = &#63; and priority = &#63;.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByL_P_First(
		Date lastPostDate, double priority,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByL_P_First(
			lastPostDate, priority, orderByComparator);
	}

	/**
	 * Removes all the message boards threads where lastPostDate = &#63; and priority = &#63; from the database.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 */
	public static void removeByL_P(Date lastPostDate, double priority) {
		getPersistence().removeByL_P(lastPostDate, priority);
	}

	/**
	 * Returns the number of message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @return the number of matching message boards threads
	 */
	public static int countByL_P(Date lastPostDate, double priority) {
		return getPersistence().countByL_P(lastPostDate, priority);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_C_L(
			groupId, categoryId, lastPostDate, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByG_C_L_First(
			long groupId, long categoryId, Date lastPostDate,
			OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByG_C_L_First(
			groupId, categoryId, lastPostDate, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByG_C_L_First(
		long groupId, long categoryId, Date lastPostDate,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByG_C_L_First(
			groupId, categoryId, lastPostDate, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByG_C_L(
			groupId, categoryId, lastPostDate, start, end, orderByComparator);
	}

	/**
	 * Removes all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 */
	public static void removeByG_C_L(
		long groupId, long categoryId, Date lastPostDate) {

		getPersistence().removeByG_C_L(groupId, categoryId, lastPostDate);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @return the number of matching message boards threads
	 */
	public static int countByG_C_L(
		long groupId, long categoryId, Date lastPostDate) {

		return getPersistence().countByG_C_L(groupId, categoryId, lastPostDate);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByG_C_L(
		long groupId, long categoryId, Date lastPostDate) {

		return getPersistence().filterCountByG_C_L(
			groupId, categoryId, lastPostDate);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByG_C_S_First(
			long groupId, long categoryId, int status,
			OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByG_C_S_First(
			groupId, categoryId, status, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByG_C_S_First(
		long groupId, long categoryId, int status,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByG_C_S_First(
			groupId, categoryId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByG_C_S(
			groupId, categoryIds, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_C_S(
			groupId, categoryIds, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 */
	public static void removeByG_C_S(
		long groupId, long categoryId, int status) {

		getPersistence().removeByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public static int countByG_C_S(long groupId, long categoryId, int status) {
		return getPersistence().countByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public static int countByG_C_S(
		long groupId, long[] categoryIds, int status) {

		return getPersistence().countByG_C_S(groupId, categoryIds, status);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByG_C_S(
		long groupId, long categoryId, int status) {

		return getPersistence().filterCountByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByG_C_S(
		long groupId, long[] categoryIds, int status) {

		return getPersistence().filterCountByG_C_S(
			groupId, categoryIds, status);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByG_C_NotS(
		long groupId, long categoryId, int status) {

		return getPersistence().findByG_C_NotS(groupId, categoryId, status);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end) {

		return getPersistence().findByG_C_NotS(
			groupId, categoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByG_C_NotS(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_C_NotS(
			groupId, categoryId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread
	 * @throws NoSuchThreadException if a matching message boards thread could not be found
	 */
	public static MBThread findByG_C_NotS_First(
			long groupId, long categoryId, int status,
			OrderByComparator<MBThread> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByG_C_NotS_First(
			groupId, categoryId, status, orderByComparator);
	}

	/**
	 * Returns the first message boards thread in the ordered set where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByG_C_NotS_First(
		long groupId, long categoryId, int status,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().fetchByG_C_NotS_First(
			groupId, categoryId, status, orderByComparator);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_NotS(
		long groupId, long categoryId, int status) {

		return getPersistence().filterFindByG_C_NotS(
			groupId, categoryId, status);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end) {

		return getPersistence().filterFindByG_C_NotS(
			groupId, categoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permissions to view where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_NotS(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByG_C_NotS(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_NotS(
		long groupId, long[] categoryIds, int status) {

		return getPersistence().filterFindByG_C_NotS(
			groupId, categoryIds, status);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end) {

		return getPersistence().filterFindByG_C_NotS(
			groupId, categoryIds, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().filterFindByG_C_NotS(
			groupId, categoryIds, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByG_C_NotS(
		long groupId, long[] categoryIds, int status) {

		return getPersistence().findByG_C_NotS(groupId, categoryIds, status);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end) {

		return getPersistence().findByG_C_NotS(
			groupId, categoryIds, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByG_C_NotS(
			groupId, categoryIds, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_NotS(
		long groupId, long[] categoryIds, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByG_C_NotS(
			groupId, categoryIds, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Removes all the message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 */
	public static void removeByG_C_NotS(
		long groupId, long categoryId, int status) {

		getPersistence().removeByG_C_NotS(groupId, categoryId, status);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public static int countByG_C_NotS(
		long groupId, long categoryId, int status) {

		return getPersistence().countByG_C_NotS(groupId, categoryId, status);
	}

	/**
	 * Returns the number of message boards threads where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the number of matching message boards threads
	 */
	public static int countByG_C_NotS(
		long groupId, long[] categoryIds, int status) {

		return getPersistence().countByG_C_NotS(groupId, categoryIds, status);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByG_C_NotS(
		long groupId, long categoryId, int status) {

		return getPersistence().filterCountByG_C_NotS(
			groupId, categoryId, status);
	}

	/**
	 * Returns the number of message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status &ne; &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the number of matching message boards threads that the user has permission to view
	 */
	public static int filterCountByG_C_NotS(
		long groupId, long[] categoryIds, int status) {

		return getPersistence().filterCountByG_C_NotS(
			groupId, categoryIds, status);
	}

	/**
	 * Creates a new message boards thread with the primary key. Does not add the message boards thread to the database.
	 *
	 * @param threadId the primary key for the new message boards thread
	 * @return the new message boards thread
	 */
	public static MBThread create(long threadId) {
		return getPersistence().create(threadId);
	}

	/**
	 * Removes the message boards thread with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param threadId the primary key of the message boards thread
	 * @return the message boards thread that was removed
	 * @throws NoSuchThreadException if a message boards thread with the primary key could not be found
	 */
	public static MBThread remove(long threadId)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().remove(threadId);
	}

	public static MBThread updateImpl(MBThread mbThread) {
		return getPersistence().updateImpl(mbThread);
	}

	/**
	 * Returns the message boards thread with the primary key or throws a <code>NoSuchThreadException</code> if it could not be found.
	 *
	 * @param threadId the primary key of the message boards thread
	 * @return the message boards thread
	 * @throws NoSuchThreadException if a message boards thread with the primary key could not be found
	 */
	public static MBThread findByPrimaryKey(long threadId)
		throws com.liferay.message.boards.exception.NoSuchThreadException {

		return getPersistence().findByPrimaryKey(threadId);
	}

	/**
	 * Returns the message boards thread with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param threadId the primary key of the message boards thread
	 * @return the message boards thread, or <code>null</code> if a message boards thread with the primary key could not be found
	 */
	public static MBThread fetchByPrimaryKey(long threadId) {
		return getPersistence().fetchByPrimaryKey(threadId);
	}

	/**
	 * Returns the message boards thread where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the message boards thread where rootMessageId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param rootMessageId the root message ID
	 * @return the matching message boards thread, or <code>null</code> if a matching message boards thread could not be found
	 */
	public static MBThread fetchByRootMessageId(long rootMessageId) {
		return getPersistence().fetchByRootMessageId(rootMessageId);
	}

	/**
	 * Returns all the message boards threads where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the message boards threads where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByG_C(long groupId, long categoryId) {
		return getPersistence().findByG_C(groupId, categoryId);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByG_C(
		long groupId, long categoryId, int start, int end) {

		return getPersistence().findByG_C(groupId, categoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByG_C(
			groupId, categoryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByG_C(long groupId, long[] categoryIds) {
		return getPersistence().findByG_C(groupId, categoryIds);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByG_C(
		long groupId, long[] categoryIds, int start, int end) {

		return getPersistence().findByG_C(groupId, categoryIds, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C(
		long groupId, long[] categoryIds, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByG_C(
			groupId, categoryIds, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C(
		long groupId, long categoryId) {

		return getPersistence().filterFindByG_C(groupId, categoryId);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C(
		long groupId, long categoryId, int start, int end) {

		return getPersistence().filterFindByG_C(
			groupId, categoryId, start, end);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C(
		long groupId, long[] categoryIds) {

		return getPersistence().filterFindByG_C(groupId, categoryIds);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C(
		long groupId, long[] categoryIds, int start, int end) {

		return getPersistence().filterFindByG_C(
			groupId, categoryIds, start, end);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByG_S(long groupId, int status) {
		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_S(long groupId, int status) {
		return getPersistence().filterFindByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().filterFindByG_S(groupId, status, start, end);
	}

	/**
	 * Returns all the message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByL_P(Date lastPostDate, double priority) {
		return getPersistence().findByL_P(lastPostDate, priority);
	}

	/**
	 * Returns a range of all the message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByL_P(
		Date lastPostDate, double priority, int start, int end) {

		return getPersistence().findByL_P(lastPostDate, priority, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where lastPostDate = &#63; and priority = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param lastPostDate the last post date
	 * @param priority the priority
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByL_P(
		Date lastPostDate, double priority, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByL_P(
			lastPostDate, priority, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByG_C_L(
		long groupId, long categoryId, Date lastPostDate) {

		return getPersistence().findByG_C_L(groupId, categoryId, lastPostDate);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end) {

		return getPersistence().findByG_C_L(
			groupId, categoryId, lastPostDate, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByG_C_L(
			groupId, categoryId, lastPostDate, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_L(
		long groupId, long categoryId, Date lastPostDate) {

		return getPersistence().filterFindByG_C_L(
			groupId, categoryId, lastPostDate);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and lastPostDate = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param lastPostDate the last post date
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_L(
		long groupId, long categoryId, Date lastPostDate, int start, int end) {

		return getPersistence().filterFindByG_C_L(
			groupId, categoryId, lastPostDate, start, end);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByG_C_S(
		long groupId, long categoryId, int status) {

		return getPersistence().findByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end) {

		return getPersistence().findByG_C_S(
			groupId, categoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the matching message boards threads
	 */
	public static List<MBThread> findByG_C_S(
		long groupId, long[] categoryIds, int status) {

		return getPersistence().findByG_C_S(groupId, categoryIds, status);
	}

	/**
	 * Returns a range of all the message boards threads where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end) {

		return getPersistence().findByG_C_S(
			groupId, categoryIds, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message boards threads where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message boards threads
	 */
	public static List<MBThread> findByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end,
		OrderByComparator<MBThread> orderByComparator) {

		return getPersistence().findByG_C_S(
			groupId, categoryIds, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_S(
		long groupId, long categoryId, int status) {

		return getPersistence().filterFindByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end) {

		return getPersistence().filterFindByG_C_S(
			groupId, categoryId, status, start, end);
	}

	/**
	 * Returns all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @return the matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_S(
		long groupId, long[] categoryIds, int status) {

		return getPersistence().filterFindByG_C_S(groupId, categoryIds, status);
	}

	/**
	 * Returns a range of all the message boards threads that the user has permission to view where groupId = &#63; and categoryId = any &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBThreadModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryIds the category IDs
	 * @param status the status
	 * @param start the lower bound of the range of message boards threads
	 * @param end the upper bound of the range of message boards threads (not inclusive)
	 * @return the range of matching message boards threads that the user has permission to view
	 */
	public static List<MBThread> filterFindByG_C_S(
		long groupId, long[] categoryIds, int status, int start, int end) {

		return getPersistence().filterFindByG_C_S(
			groupId, categoryIds, status, start, end);
	}

	public static MBThreadPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(MBThreadPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile MBThreadPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:2094093909