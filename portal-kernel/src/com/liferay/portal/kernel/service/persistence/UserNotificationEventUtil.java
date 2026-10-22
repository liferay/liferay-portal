/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the user notification event service. This utility wraps <code>com.liferay.portal.service.persistence.impl.UserNotificationEventPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see UserNotificationEventPersistence
 * @generated
 */
public class UserNotificationEventUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<UserNotificationEvent> userNotificationEvents) {

		getPersistence().cacheResult(userNotificationEvents);
	}

	/**
	 * @see BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		UserNotificationEvent userNotificationEvent) {

		getPersistence().cacheResult(userNotificationEvent);
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
	public static void clearCache(UserNotificationEvent userNotificationEvent) {
		getPersistence().clearCache(userNotificationEvent);
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
	public static Map<Serializable, UserNotificationEvent> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<UserNotificationEvent> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<UserNotificationEvent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<UserNotificationEvent> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static UserNotificationEvent update(
		UserNotificationEvent userNotificationEvent) {

		return getPersistence().update(userNotificationEvent);
	}

	/**
	 * @see BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static UserNotificationEvent update(
		UserNotificationEvent userNotificationEvent,
		ServiceContext serviceContext) {

		return getPersistence().update(userNotificationEvent, serviceContext);
	}

	/**
	 * Returns an ordered range of all the user notification events where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByUuid_First(
			String uuid,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByUuid_First(
		String uuid,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the user notification events where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of user notification events where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching user notification events
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the user notification events where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching user notification events
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUserId(
		long userId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByUserId_First(
			long userId,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByUserId_First(userId, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByUserId_First(
		long userId,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByUserId_First(userId, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public static void removeByUserId(long userId) {
		getPersistence().removeByUserId(userId);
	}

	/**
	 * Returns the number of user notification events where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching user notification events
	 */
	public static int countByUserId(long userId) {
		return getPersistence().countByUserId(userId);
	}

	/**
	 * Returns an ordered range of all the user notification events where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByType(
		String type, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByType(
			type, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByType_First(
			String type,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByType_First(type, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByType_First(
		String type,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByType_First(type, orderByComparator);
	}

	/**
	 * Removes all the user notification events where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public static void removeByType(String type) {
		getPersistence().removeByType(type);
	}

	/**
	 * Returns the number of user notification events where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching user notification events
	 */
	public static int countByType(String type) {
		return getPersistence().countByType(type);
	}

	/**
	 * Returns all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered) {

		return getPersistence().findByU_T_GteT_D(
			userId, type, timestamp, delivered);
	}

	/**
	 * Returns a range of all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end) {

		return getPersistence().findByU_T_GteT_D(
			userId, type, timestamp, delivered, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end, OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByU_T_GteT_D(
			userId, type, timestamp, delivered, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end, OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_T_GteT_D(
			userId, type, timestamp, delivered, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByU_T_GteT_D_First(
			long userId, String type, long timestamp, boolean delivered,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByU_T_GteT_D_First(
			userId, type, timestamp, delivered, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByU_T_GteT_D_First(
		long userId, String type, long timestamp, boolean delivered,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByU_T_GteT_D_First(
			userId, type, timestamp, delivered, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 */
	public static void removeByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered) {

		getPersistence().removeByU_T_GteT_D(userId, type, timestamp, delivered);
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @return the number of matching user notification events
	 */
	public static int countByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered) {

		return getPersistence().countByU_T_GteT_D(
			userId, type, timestamp, delivered);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByU_DT_D_AR_First(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByU_DT_D_AR_First(
			userId, deliveryType, delivered, actionRequired, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByU_DT_D_AR_First(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByU_DT_D_AR_First(
			userId, deliveryType, delivered, actionRequired, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 */
	public static void removeByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		getPersistence().removeByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired);
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @return the number of matching user notification events
	 */
	public static int countByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		return getPersistence().countByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived,
		int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_DT_D_A(
			userId, deliveryType, delivered, archived, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByU_DT_D_A_First(
			long userId, int deliveryType, boolean delivered, boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByU_DT_D_A_First(
			userId, deliveryType, delivered, archived, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByU_DT_D_A_First(
		long userId, int deliveryType, boolean delivered, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByU_DT_D_A_First(
			userId, deliveryType, delivered, archived, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 */
	public static void removeByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		getPersistence().removeByU_DT_D_A(
			userId, deliveryType, delivered, archived);
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	public static int countByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return getPersistence().countByU_DT_D_A(
			userId, deliveryType, delivered, archived);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_D_AR_A(
			userId, delivered, actionRequired, archived, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByU_D_AR_A_First(
			long userId, boolean delivered, boolean actionRequired,
			boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByU_D_AR_A_First(
			userId, delivered, actionRequired, archived, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByU_D_AR_A_First(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByU_D_AR_A_First(
			userId, delivered, actionRequired, archived, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 */
	public static void removeByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived) {

		getPersistence().removeByU_D_AR_A(
			userId, delivered, actionRequired, archived);
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	public static int countByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived) {

		return getPersistence().countByU_D_AR_A(
			userId, delivered, actionRequired, archived);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByU_T_DT_D_A_First(
			long userId, String type, int deliveryType, boolean delivered,
			boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByU_T_DT_D_A_First(
			userId, type, deliveryType, delivered, archived, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByU_T_DT_D_A_First(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByU_T_DT_D_A_First(
			userId, type, deliveryType, delivered, archived, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 */
	public static void removeByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		getPersistence().removeByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived);
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	public static int countByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		return getPersistence().countByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public static UserNotificationEvent findByU_DT_D_AR_A_First(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByU_DT_D_AR_A_First(
			userId, deliveryType, delivered, actionRequired, archived,
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public static UserNotificationEvent fetchByU_DT_D_AR_A_First(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().fetchByU_DT_D_AR_A_First(
			userId, deliveryType, delivered, actionRequired, archived,
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 */
	public static void removeByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		getPersistence().removeByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived);
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	public static int countByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		return getPersistence().countByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived);
	}

	/**
	 * Creates a new user notification event with the primary key. Does not add the user notification event to the database.
	 *
	 * @param userNotificationEventId the primary key for the new user notification event
	 * @return the new user notification event
	 */
	public static UserNotificationEvent create(long userNotificationEventId) {
		return getPersistence().create(userNotificationEventId);
	}

	/**
	 * Removes the user notification event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event that was removed
	 * @throws NoSuchUserNotificationEventException if a user notification event with the primary key could not be found
	 */
	public static UserNotificationEvent remove(long userNotificationEventId)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().remove(userNotificationEventId);
	}

	public static UserNotificationEvent updateImpl(
		UserNotificationEvent userNotificationEvent) {

		return getPersistence().updateImpl(userNotificationEvent);
	}

	/**
	 * Returns the user notification event with the primary key or throws a <code>NoSuchUserNotificationEventException</code> if it could not be found.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event
	 * @throws NoSuchUserNotificationEventException if a user notification event with the primary key could not be found
	 */
	public static UserNotificationEvent findByPrimaryKey(
			long userNotificationEventId)
		throws com.liferay.portal.kernel.exception.
			NoSuchUserNotificationEventException {

		return getPersistence().findByPrimaryKey(userNotificationEventId);
	}

	/**
	 * Returns the user notification event with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event, or <code>null</code> if a user notification event with the primary key could not be found
	 */
	public static UserNotificationEvent fetchByPrimaryKey(
		long userNotificationEventId) {

		return getPersistence().fetchByPrimaryKey(userNotificationEventId);
	}

	/**
	 * Returns all the user notification events where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the user notification events where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUuid(
		String uuid, int start, int end) {

		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId) {

		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the user notification events where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByUserId(long userId) {
		return getPersistence().findByUserId(userId);
	}

	/**
	 * Returns a range of all the user notification events where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUserId(
		long userId, int start, int end) {

		return getPersistence().findByUserId(userId, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByUserId(
		long userId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByUserId(
			userId, start, end, orderByComparator);
	}

	/**
	 * Returns all the user notification events where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByType(String type) {
		return getPersistence().findByType(type);
	}

	/**
	 * Returns a range of all the user notification events where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByType(
		String type, int start, int end) {

		return getPersistence().findByType(type, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByType(
		String type, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByType(type, start, end, orderByComparator);
	}

	/**
	 * Returns all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		return getPersistence().findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired);
	}

	/**
	 * Returns a range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end) {

		return getPersistence().findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return getPersistence().findByU_DT_D_A(
			userId, deliveryType, delivered, archived);
	}

	/**
	 * Returns a range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived,
		int start, int end) {

		return getPersistence().findByU_DT_D_A(
			userId, deliveryType, delivered, archived, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived,
		int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByU_DT_D_A(
			userId, deliveryType, delivered, archived, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived) {

		return getPersistence().findByU_D_AR_A(
			userId, delivered, actionRequired, archived);
	}

	/**
	 * Returns a range of all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived, int start, int end) {

		return getPersistence().findByU_D_AR_A(
			userId, delivered, actionRequired, archived, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByU_D_AR_A(
			userId, delivered, actionRequired, archived, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		return getPersistence().findByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived);
	}

	/**
	 * Returns a range of all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived, int start, int end) {

		return getPersistence().findByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived, start, end);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived, start, end,
			orderByComparator);
	}

	/**
	 * Returns all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @return the matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		return getPersistence().findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived);
	}

	/**
	 * Returns a range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @return the range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end) {

		return getPersistence().findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching user notification events
	 */
	public static List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return getPersistence().findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end, orderByComparator);
	}

	public static UserNotificationEventPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		UserNotificationEventPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile UserNotificationEventPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-466031580