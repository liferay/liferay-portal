/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the message-boards message service. This utility wraps <code>com.liferay.message.boards.service.persistence.impl.MBMessagePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see MBMessagePersistence
 * @generated
 */
public class MBMessageUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<MBMessage> mbMessages) {
		getPersistence().cacheResult(mbMessages);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(MBMessage mbMessage) {
		getPersistence().cacheResult(mbMessage);
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
	public static void clearCache(MBMessage mbMessage) {
		getPersistence().clearCache(mbMessage);
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
	public static Map<Serializable, MBMessage> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<MBMessage> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<MBMessage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<MBMessage> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static MBMessage update(MBMessage mbMessage) {
		return getPersistence().update(mbMessage);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static MBMessage update(
		MBMessage mbMessage, ServiceContext serviceContext) {

		return getPersistence().update(mbMessage, serviceContext);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByUuid_First(
			String uuid, OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByUuid_First(
		String uuid, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching message-boards messages
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns the message-boards message where uuid = &#63; and groupId = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByUUID_G(String uuid, long groupId)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the message-boards message where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByUUID_G(
		String uuid, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByUUID_G(uuid, groupId, useFinderCache);
	}

	/**
	 * Removes the message-boards message where uuid = &#63; and groupId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the message-boards message that was removed
	 */
	public static MBMessage removeByUUID_G(String uuid, long groupId)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().removeByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63; and groupId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByUUID_G(String uuid, long groupId) {
		return getPersistence().countByUUID_G(uuid, groupId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByGroupId_First(
			long groupId, OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByGroupId_First(groupId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByGroupId_First(
		long groupId, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByGroupId_First(
			groupId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 */
	public static void removeByGroupId(long groupId) {
		getPersistence().removeByGroupId(groupId);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByGroupId(long groupId) {
		return getPersistence().countByGroupId(groupId);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByGroupId(long groupId) {
		return getPersistence().filterCountByGroupId(groupId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByCompanyId_First(
			long companyId, OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByCompanyId_First(
		long companyId, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByCompanyId_First(
			companyId, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where companyId = &#63; from the database.
	 *
	 * @param companyId the company ID
	 */
	public static void removeByCompanyId(long companyId) {
		getPersistence().removeByCompanyId(companyId);
	}

	/**
	 * Returns the number of message-boards messages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByCompanyId(long companyId) {
		return getPersistence().countByCompanyId(companyId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByUserId_First(
			long userId, OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByUserId_First(
		long userId, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByThreadId(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByThreadId(
			threadId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByThreadId_First(
			long threadId, OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByThreadId_First(
			threadId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByThreadId_First(
		long threadId, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByThreadId_First(
			threadId, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 */
	public static void removeByThreadId(long threadId) {
		getPersistence().removeByThreadId(threadId);
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByThreadId(long threadId) {
		return getPersistence().countByThreadId(threadId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByThreadIdReplies(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByThreadIdReplies(
			threadId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByThreadIdReplies_First(
			long threadId, OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByThreadIdReplies_First(
			threadId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByThreadIdReplies_First(
		long threadId, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByThreadIdReplies_First(
			threadId, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 */
	public static void removeByThreadIdReplies(long threadId) {
		getPersistence().removeByThreadIdReplies(threadId);
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByThreadIdReplies(long threadId) {
		return getPersistence().countByThreadIdReplies(threadId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByParentMessageId(
		long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByParentMessageId(
			parentMessageId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByParentMessageId_First(
			long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByParentMessageId_First(
			parentMessageId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByParentMessageId_First(
		long parentMessageId, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByParentMessageId_First(
			parentMessageId, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where parentMessageId = &#63; from the database.
	 *
	 * @param parentMessageId the parent message ID
	 */
	public static void removeByParentMessageId(long parentMessageId) {
		getPersistence().removeByParentMessageId(parentMessageId);
	}

	/**
	 * Returns the number of message-boards messages where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByParentMessageId(long parentMessageId) {
		return getPersistence().countByParentMessageId(parentMessageId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U(
			groupId, userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_U_First(
			long groupId, long userId,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_U_First(
			groupId, userId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByG_U_First(
			groupId, userId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByG_U(
			groupId, userId, start, end, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	public static void removeByG_U(long groupId, long userId) {
		getPersistence().removeByG_U(groupId, userId);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_U(long groupId, long userId) {
		return getPersistence().countByG_U(groupId, userId);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByG_U(long groupId, long userId) {
		return getPersistence().filterCountByG_U(groupId, userId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C(
			groupId, categoryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_C_First(
			long groupId, long categoryId,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_C_First(
			groupId, categoryId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_C_First(
		long groupId, long categoryId,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByG_C_First(
			groupId, categoryId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByG_C(
			groupId, categoryId, start, end, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 */
	public static void removeByG_C(long groupId, long categoryId) {
		getPersistence().removeByG_C(groupId, categoryId);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_C(long groupId, long categoryId) {
		return getPersistence().countByG_C(groupId, categoryId);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByG_C(long groupId, long categoryId) {
		return getPersistence().filterCountByG_C(groupId, categoryId);
	}

	/**
	 * Returns the message-boards message where groupId = &#63; and urlSubject = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_US(long groupId, String urlSubject)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_US(groupId, urlSubject);
	}

	/**
	 * Returns the message-boards message where groupId = &#63; and urlSubject = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_US(
		long groupId, String urlSubject, boolean useFinderCache) {

		return getPersistence().fetchByG_US(
			groupId, urlSubject, useFinderCache);
	}

	/**
	 * Removes the message-boards message where groupId = &#63; and urlSubject = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the message-boards message that was removed
	 */
	public static MBMessage removeByG_US(long groupId, String urlSubject)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().removeByG_US(groupId, urlSubject);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and urlSubject = &#63;.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_US(long groupId, String urlSubject) {
		return getPersistence().countByG_US(groupId, urlSubject);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_S_First(
			long groupId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_S_First(
		long groupId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByG_S_First(
			groupId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 */
	public static void removeByG_S(long groupId, int status) {
		getPersistence().removeByG_S(groupId, status);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_S(long groupId, int status) {
		return getPersistence().countByG_S(groupId, status);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByG_S(long groupId, int status) {
		return getPersistence().filterCountByG_S(groupId, status);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByC_S_First(
			long companyId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByC_S_First(
		long companyId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByC_S_First(
			companyId, status, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where companyId = &#63; and status = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 */
	public static void removeByC_S(long companyId, int status) {
		getPersistence().removeByC_S(companyId, status);
	}

	/**
	 * Returns the number of message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByC_S(long companyId, int status) {
		return getPersistence().countByC_S(companyId, status);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByC_C_First(
			long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByC_C_First(
		long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByC_C(long classNameId, long classPK) {
		getPersistence().removeByC_C(classNameId, classPK);
	}

	/**
	 * Returns the number of message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching message-boards messages
	 */
	public static int countByC_C(long classNameId, long classPK) {
		return getPersistence().countByC_C(classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_P(
		long threadId, long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByT_P(
			threadId, parentMessageId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByT_P_First(
			long threadId, long parentMessageId,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByT_P_First(
			threadId, parentMessageId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByT_P_First(
		long threadId, long parentMessageId,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByT_P_First(
			threadId, parentMessageId, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and parentMessageId = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 */
	public static void removeByT_P(long threadId, long parentMessageId) {
		getPersistence().removeByT_P(threadId, parentMessageId);
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByT_P(long threadId, long parentMessageId) {
		return getPersistence().countByT_P(threadId, parentMessageId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_S(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByT_S(
			threadId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByT_S_First(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByT_S_First(
			threadId, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByT_S_First(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByT_S_First(
			threadId, status, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and status = &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 */
	public static void removeByT_S(long threadId, int status) {
		getPersistence().removeByT_S(threadId, status);
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByT_S(long threadId, int status) {
		return getPersistence().countByT_S(threadId, status);
	}

	/**
	 * Returns all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByT_NotS(long threadId, int status) {
		return getPersistence().findByT_NotS(threadId, status);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end) {

		return getPersistence().findByT_NotS(threadId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByT_NotS(
			threadId, status, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_NotS(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByT_NotS(
			threadId, status, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByT_NotS_First(
			long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByT_NotS_First(
			threadId, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByT_NotS_First(
		long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByT_NotS_First(
			threadId, status, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where threadId = &#63; and status &ne; &#63; from the database.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 */
	public static void removeByT_NotS(long threadId, int status) {
		getPersistence().removeByT_NotS(threadId, status);
	}

	/**
	 * Returns the number of message-boards messages where threadId = &#63; and status &ne; &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByT_NotS(long threadId, int status) {
		return getPersistence().countByT_NotS(threadId, status);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByP_S(
		long parentMessageId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByP_S(
			parentMessageId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByP_S_First(
			long parentMessageId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByP_S_First(
			parentMessageId, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByP_S_First(
		long parentMessageId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByP_S_First(
			parentMessageId, status, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where parentMessageId = &#63; and status = &#63; from the database.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 */
	public static void removeByP_S(long parentMessageId, int status) {
		getPersistence().removeByP_S(parentMessageId, status);
	}

	/**
	 * Returns the number of message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByP_S(long parentMessageId, int status) {
		return getPersistence().countByP_S(parentMessageId, status);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_U_S(
			groupId, userId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_U_S_First(
			long groupId, long userId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_U_S_First(
			groupId, userId, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_U_S_First(
		long groupId, long userId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByG_U_S_First(
			groupId, userId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByG_U_S(
			groupId, userId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 */
	public static void removeByG_U_S(long groupId, long userId, int status) {
		getPersistence().removeByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_U_S(long groupId, long userId, int status) {
		return getPersistence().countByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByG_U_S(
		long groupId, long userId, int status) {

		return getPersistence().filterCountByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_T(
			groupId, categoryId, threadId, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_C_T_First(
			long groupId, long categoryId, long threadId,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_C_T_First(
			groupId, categoryId, threadId, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_C_T_First(
		long groupId, long categoryId, long threadId,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByG_C_T_First(
			groupId, categoryId, threadId, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByG_C_T(
			groupId, categoryId, threadId, start, end, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 */
	public static void removeByG_C_T(
		long groupId, long categoryId, long threadId) {

		getPersistence().removeByG_C_T(groupId, categoryId, threadId);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_C_T(
		long groupId, long categoryId, long threadId) {

		return getPersistence().countByG_C_T(groupId, categoryId, threadId);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByG_C_T(
		long groupId, long categoryId, long threadId) {

		return getPersistence().filterCountByG_C_T(
			groupId, categoryId, threadId);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_C_S_First(
			long groupId, long categoryId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_C_S_First(
			groupId, categoryId, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_C_S_First(
		long groupId, long categoryId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByG_C_S_First(
			groupId, categoryId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63; from the database.
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
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_C_S(long groupId, long categoryId, int status) {
		return getPersistence().countByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByG_C_S(
		long groupId, long categoryId, int status) {

		return getPersistence().filterCountByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_C_C(
			userId, classNameId, classPK, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByU_C_C_First(
			long userId, long classNameId, long classPK,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByU_C_C_First(
			userId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByU_C_C_First(
		long userId, long classNameId, long classPK,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByU_C_C_First(
			userId, classNameId, classPK, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	public static void removeByU_C_C(
		long userId, long classNameId, long classPK) {

		getPersistence().removeByU_C_C(userId, classNameId, classPK);
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching message-boards messages
	 */
	public static int countByU_C_C(
		long userId, long classNameId, long classPK) {

		return getPersistence().countByU_C_C(userId, classNameId, classPK);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByC_C_S_First(
			long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByC_C_S_First(
			classNameId, classPK, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByC_C_S_First(
		long classNameId, long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByC_C_S_First(
			classNameId, classPK, status, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	public static void removeByC_C_S(
		long classNameId, long classPK, int status) {

		getPersistence().removeByC_C_S(classNameId, classPK, status);
	}

	/**
	 * Returns the number of message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByC_C_S(long classNameId, long classPK, int status) {
		return getPersistence().countByC_C_S(classNameId, classPK, status);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_T_A(
			groupId, categoryId, threadId, answer, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_C_T_A_First(
			long groupId, long categoryId, long threadId, boolean answer,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_C_T_A_First(
			groupId, categoryId, threadId, answer, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_C_T_A_First(
		long groupId, long categoryId, long threadId, boolean answer,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByG_C_T_A_First(
			groupId, categoryId, threadId, answer, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByG_C_T_A(
			groupId, categoryId, threadId, answer, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 */
	public static void removeByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		getPersistence().removeByG_C_T_A(groupId, categoryId, threadId, answer);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		return getPersistence().countByG_C_T_A(
			groupId, categoryId, threadId, answer);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		return getPersistence().filterCountByG_C_T_A(
			groupId, categoryId, threadId, answer);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByG_C_T_S(
			groupId, categoryId, threadId, status, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByG_C_T_S_First(
			long groupId, long categoryId, long threadId, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByG_C_T_S_First(
			groupId, categoryId, threadId, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_C_T_S_First(
		long groupId, long categoryId, long threadId, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByG_C_T_S_First(
			groupId, categoryId, threadId, status, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the message-boards messages that the user has permissions to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().filterFindByG_C_T_S(
			groupId, categoryId, threadId, status, start, end,
			orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 */
	public static void removeByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		getPersistence().removeByG_C_T_S(groupId, categoryId, threadId, status);
	}

	/**
	 * Returns the number of message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		return getPersistence().countByG_C_T_S(
			groupId, categoryId, threadId, status);
	}

	/**
	 * Returns the number of message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the number of matching message-boards messages that the user has permission to view
	 */
	public static int filterCountByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		return getPersistence().filterCountByG_C_T_S(
			groupId, categoryId, threadId, status);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_C_C_S(
			userId, classNameId, classPK, status, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByU_C_C_S_First(
			long userId, long classNameId, long classPK, int status,
			OrderByComparator<MBMessage> orderByComparator)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByU_C_C_S_First(
			userId, classNameId, classPK, status, orderByComparator);
	}

	/**
	 * Returns the first message-boards message in the ordered set where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByU_C_C_S_First(
		long userId, long classNameId, long classPK, int status,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().fetchByU_C_C_S_First(
			userId, classNameId, classPK, status, orderByComparator);
	}

	/**
	 * Removes all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 */
	public static void removeByU_C_C_S(
		long userId, long classNameId, long classPK, int status) {

		getPersistence().removeByU_C_C_S(userId, classNameId, classPK, status);
	}

	/**
	 * Returns the number of message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the number of matching message-boards messages
	 */
	public static int countByU_C_C_S(
		long userId, long classNameId, long classPK, int status) {

		return getPersistence().countByU_C_C_S(
			userId, classNameId, classPK, status);
	}

	/**
	 * Returns the message-boards message where externalReferenceCode = &#63; and groupId = &#63; or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message-boards message
	 * @throws NoSuchMessageException if a matching message-boards message could not be found
	 */
	public static MBMessage findByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the message-boards message where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByERC_G(
		String externalReferenceCode, long groupId, boolean useFinderCache) {

		return getPersistence().fetchByERC_G(
			externalReferenceCode, groupId, useFinderCache);
	}

	/**
	 * Removes the message-boards message where externalReferenceCode = &#63; and groupId = &#63; from the database.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the message-boards message that was removed
	 */
	public static MBMessage removeByERC_G(
			String externalReferenceCode, long groupId)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().removeByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns the number of message-boards messages where externalReferenceCode = &#63; and groupId = &#63;.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the number of matching message-boards messages
	 */
	public static int countByERC_G(String externalReferenceCode, long groupId) {
		return getPersistence().countByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Creates a new message-boards message with the primary key. Does not add the message-boards message to the database.
	 *
	 * @param messageId the primary key for the new message-boards message
	 * @return the new message-boards message
	 */
	public static MBMessage create(long messageId) {
		return getPersistence().create(messageId);
	}

	/**
	 * Removes the message-boards message with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message that was removed
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	public static MBMessage remove(long messageId)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().remove(messageId);
	}

	public static MBMessage updateImpl(MBMessage mbMessage) {
		return getPersistence().updateImpl(mbMessage);
	}

	/**
	 * Returns the message-boards message with the primary key or throws a <code>NoSuchMessageException</code> if it could not be found.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message
	 * @throws NoSuchMessageException if a message-boards message with the primary key could not be found
	 */
	public static MBMessage findByPrimaryKey(long messageId)
		throws com.liferay.message.boards.exception.NoSuchMessageException {

		return getPersistence().findByPrimaryKey(messageId);
	}

	/**
	 * Returns the message-boards message with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param messageId the primary key of the message-boards message
	 * @return the message-boards message, or <code>null</code> if a message-boards message with the primary key could not be found
	 */
	public static MBMessage fetchByPrimaryKey(long messageId) {
		return getPersistence().fetchByPrimaryKey(messageId);
	}

	/**
	 * Returns the message-boards message where uuid = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param uuid the uuid
	 * @param groupId the group ID
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByUUID_G(String uuid, long groupId) {
		return getPersistence().fetchByUUID_G(uuid, groupId);
	}

	/**
	 * Returns the message-boards message where groupId = &#63; and urlSubject = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param groupId the group ID
	 * @param urlSubject the url subject
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByG_US(long groupId, String urlSubject) {
		return getPersistence().fetchByG_US(groupId, urlSubject);
	}

	/**
	 * Returns the message-boards message where externalReferenceCode = &#63; and groupId = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param externalReferenceCode the external reference code
	 * @param groupId the group ID
	 * @return the matching message-boards message, or <code>null</code> if a matching message-boards message could not be found
	 */
	public static MBMessage fetchByERC_G(
		String externalReferenceCode, long groupId) {

		return getPersistence().fetchByERC_G(externalReferenceCode, groupId);
	}

	/**
	 * Returns all the message-boards messages where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the message-boards messages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByGroupId(long groupId) {
		return getPersistence().findByGroupId(groupId);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByGroupId(
		long groupId, int start, int end) {

		return getPersistence().findByGroupId(groupId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByGroupId(
		long groupId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByGroupId(
			groupId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63;.
	 *
	 * @param groupId the group ID
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByGroupId(long groupId) {
		return getPersistence().filterFindByGroupId(groupId);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByGroupId(
		long groupId, int start, int end) {

		return getPersistence().filterFindByGroupId(groupId, start, end);
	}

	/**
	 * Returns all the message-boards messages where companyId = &#63;.
	 *
	 * @param companyId the company ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByCompanyId(long companyId) {
		return getPersistence().findByCompanyId(companyId);
	}

	/**
	 * Returns a range of all the message-boards messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByCompanyId(
		long companyId, int start, int end) {

		return getPersistence().findByCompanyId(companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByCompanyId(
		long companyId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByUserId(
		long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByThreadId(long threadId) {
		return getPersistence().findByThreadId(threadId);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByThreadId(
		long threadId, int start, int end) {

		return getPersistence().findByThreadId(threadId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByThreadId(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByThreadId(
			threadId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where threadId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByThreadIdReplies(long threadId) {
		return getPersistence().findByThreadIdReplies(threadId);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByThreadIdReplies(
		long threadId, int start, int end) {

		return getPersistence().findByThreadIdReplies(threadId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByThreadIdReplies(
		long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByThreadIdReplies(
			threadId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where parentMessageId = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByParentMessageId(long parentMessageId) {
		return getPersistence().findByParentMessageId(parentMessageId);
	}

	/**
	 * Returns a range of all the message-boards messages where parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByParentMessageId(
		long parentMessageId, int start, int end) {

		return getPersistence().findByParentMessageId(
			parentMessageId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByParentMessageId(
		long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByParentMessageId(
			parentMessageId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByG_U(long groupId, long userId) {
		return getPersistence().findByG_U(groupId, userId);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_U(
		long groupId, long userId, int start, int end) {

		return getPersistence().findByG_U(groupId, userId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByG_U(
			groupId, userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_U(long groupId, long userId) {
		return getPersistence().filterFindByG_U(groupId, userId);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_U(
		long groupId, long userId, int start, int end) {

		return getPersistence().filterFindByG_U(groupId, userId, start, end);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByG_C(long groupId, long categoryId) {
		return getPersistence().findByG_C(groupId, categoryId);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C(
		long groupId, long categoryId, int start, int end) {

		return getPersistence().findByG_C(groupId, categoryId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C(
		long groupId, long categoryId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByG_C(
			groupId, categoryId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C(
		long groupId, long categoryId) {

		return getPersistence().filterFindByG_C(groupId, categoryId);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C(
		long groupId, long categoryId, int start, int end) {

		return getPersistence().filterFindByG_C(
			groupId, categoryId, start, end);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByG_S(long groupId, int status) {
		return getPersistence().findByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().findByG_S(groupId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_S(
		long groupId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByG_S(
			groupId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_S(long groupId, int status) {
		return getPersistence().filterFindByG_S(groupId, status);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_S(
		long groupId, int status, int start, int end) {

		return getPersistence().filterFindByG_S(groupId, status, start, end);
	}

	/**
	 * Returns all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByC_S(long companyId, int status) {
		return getPersistence().findByC_S(companyId, status);
	}

	/**
	 * Returns a range of all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_S(
		long companyId, int status, int start, int end) {

		return getPersistence().findByC_S(companyId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where companyId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_S(
		long companyId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByC_S(
			companyId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByC_C(long classNameId, long classPK) {
		return getPersistence().findByC_C(classNameId, classPK);
	}

	/**
	 * Returns a range of all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_C(
		long classNameId, long classPK, int start, int end) {

		return getPersistence().findByC_C(classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_C(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByC_C(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByT_P(
		long threadId, long parentMessageId) {

		return getPersistence().findByT_P(threadId, parentMessageId);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_P(
		long threadId, long parentMessageId, int start, int end) {

		return getPersistence().findByT_P(
			threadId, parentMessageId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and parentMessageId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param parentMessageId the parent message ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_P(
		long threadId, long parentMessageId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByT_P(
			threadId, parentMessageId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByT_S(long threadId, int status) {
		return getPersistence().findByT_S(threadId, status);
	}

	/**
	 * Returns a range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_S(
		long threadId, int status, int start, int end) {

		return getPersistence().findByT_S(threadId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByT_S(
		long threadId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByT_S(
			threadId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByP_S(long parentMessageId, int status) {
		return getPersistence().findByP_S(parentMessageId, status);
	}

	/**
	 * Returns a range of all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByP_S(
		long parentMessageId, int status, int start, int end) {

		return getPersistence().findByP_S(parentMessageId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where parentMessageId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param parentMessageId the parent message ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByP_S(
		long parentMessageId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByP_S(
			parentMessageId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByG_U_S(
		long groupId, long userId, int status) {

		return getPersistence().findByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return getPersistence().findByG_U_S(
			groupId, userId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_U_S(
		long groupId, long userId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByG_U_S(
			groupId, userId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_U_S(
		long groupId, long userId, int status) {

		return getPersistence().filterFindByG_U_S(groupId, userId, status);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and userId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_U_S(
		long groupId, long userId, int status, int start, int end) {

		return getPersistence().filterFindByG_U_S(
			groupId, userId, status, start, end);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId) {

		return getPersistence().findByG_C_T(groupId, categoryId, threadId);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end) {

		return getPersistence().findByG_C_T(
			groupId, categoryId, threadId, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByG_C_T(
			groupId, categoryId, threadId, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T(
		long groupId, long categoryId, long threadId) {

		return getPersistence().filterFindByG_C_T(
			groupId, categoryId, threadId);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T(
		long groupId, long categoryId, long threadId, int start, int end) {

		return getPersistence().filterFindByG_C_T(
			groupId, categoryId, threadId, start, end);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status) {

		return getPersistence().findByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end) {

		return getPersistence().findByG_C_S(
			groupId, categoryId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_S(
		long groupId, long categoryId, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByG_C_S(
			groupId, categoryId, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_S(
		long groupId, long categoryId, int status) {

		return getPersistence().filterFindByG_C_S(groupId, categoryId, status);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_S(
		long groupId, long categoryId, int status, int start, int end) {

		return getPersistence().filterFindByG_C_S(
			groupId, categoryId, status, start, end);
	}

	/**
	 * Returns all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK) {

		return getPersistence().findByU_C_C(userId, classNameId, classPK);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end) {

		return getPersistence().findByU_C_C(
			userId, classNameId, classPK, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByU_C_C(
		long userId, long classNameId, long classPK, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByU_C_C(
			userId, classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status) {

		return getPersistence().findByC_C_S(classNameId, classPK, status);
	}

	/**
	 * Returns a range of all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByC_C_S(
		long classNameId, long classPK, int status, int start, int end,
		OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByC_C_S(
			classNameId, classPK, status, start, end, orderByComparator);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		return getPersistence().findByG_C_T_A(
			groupId, categoryId, threadId, answer);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end) {

		return getPersistence().findByG_C_T_A(
			groupId, categoryId, threadId, answer, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByG_C_T_A(
			groupId, categoryId, threadId, answer, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer) {

		return getPersistence().filterFindByG_C_T_A(
			groupId, categoryId, threadId, answer);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and answer = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param answer the answer
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T_A(
		long groupId, long categoryId, long threadId, boolean answer, int start,
		int end) {

		return getPersistence().filterFindByG_C_T_A(
			groupId, categoryId, threadId, answer, start, end);
	}

	/**
	 * Returns all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		return getPersistence().findByG_C_T_S(
			groupId, categoryId, threadId, status);
	}

	/**
	 * Returns a range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end) {

		return getPersistence().findByG_C_T_S(
			groupId, categoryId, threadId, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByG_C_T_S(
			groupId, categoryId, threadId, status, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @return the matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T_S(
		long groupId, long categoryId, long threadId, int status) {

		return getPersistence().filterFindByG_C_T_S(
			groupId, categoryId, threadId, status);
	}

	/**
	 * Returns a range of all the message-boards messages that the user has permission to view where groupId = &#63; and categoryId = &#63; and threadId = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param categoryId the category ID
	 * @param threadId the thread ID
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages that the user has permission to view
	 */
	public static List<MBMessage> filterFindByG_C_T_S(
		long groupId, long categoryId, long threadId, int status, int start,
		int end) {

		return getPersistence().filterFindByG_C_T_S(
			groupId, categoryId, threadId, status, start, end);
	}

	/**
	 * Returns all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @return the matching message-boards messages
	 */
	public static List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status) {

		return getPersistence().findByU_C_C_S(
			userId, classNameId, classPK, status);
	}

	/**
	 * Returns a range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @return the range of matching message-boards messages
	 */
	public static List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status, int start,
		int end) {

		return getPersistence().findByU_C_C_S(
			userId, classNameId, classPK, status, start, end);
	}

	/**
	 * Returns an ordered range of all the message-boards messages where userId = &#63; and classNameId = &#63; and classPK = &#63; and status = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.message.boards.model.impl.MBMessageModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param status the status
	 * @param start the lower bound of the range of message-boards messages
	 * @param end the upper bound of the range of message-boards messages (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching message-boards messages
	 */
	public static List<MBMessage> findByU_C_C_S(
		long userId, long classNameId, long classPK, int status, int start,
		int end, OrderByComparator<MBMessage> orderByComparator) {

		return getPersistence().findByU_C_C_S(
			userId, classNameId, classPK, status, start, end,
			orderByComparator);
	}

	public static MBMessagePersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(MBMessagePersistence persistence) {
		_persistence = persistence;
	}

	private static volatile MBMessagePersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1179540785