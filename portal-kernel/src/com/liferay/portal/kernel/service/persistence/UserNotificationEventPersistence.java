/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.exception.NoSuchUserNotificationEventException;
import com.liferay.portal.kernel.model.UserNotificationEvent;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the user notification event service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see UserNotificationEventUtil
 * @generated
 */
@ProviderType
public interface UserNotificationEventPersistence
	extends BasePersistence<UserNotificationEvent> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link UserNotificationEventUtil} to access the user notification event persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

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
	public java.util.List<UserNotificationEvent> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public UserNotificationEvent findByUuid_First(
			String uuid,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public UserNotificationEvent fetchByUuid_First(
		String uuid,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public void removeByUuid(String uuid);

	/**
	 * Returns the number of user notification events where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching user notification events
	 */
	public int countByUuid(String uuid);

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
	public java.util.List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public UserNotificationEvent findByUuid_C_First(
			String uuid, long companyId,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public UserNotificationEvent fetchByUuid_C_First(
		String uuid, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public void removeByUuid_C(String uuid, long companyId);

	/**
	 * Returns the number of user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching user notification events
	 */
	public int countByUuid_C(String uuid, long companyId);

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
	public java.util.List<UserNotificationEvent> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public UserNotificationEvent findByUserId_First(
			long userId,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public UserNotificationEvent fetchByUserId_First(
		long userId,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	public void removeByUserId(long userId);

	/**
	 * Returns the number of user notification events where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching user notification events
	 */
	public int countByUserId(long userId);

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
	public java.util.List<UserNotificationEvent> findByType(
		String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first user notification event in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	public UserNotificationEvent findByType_First(
			String type,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

	/**
	 * Returns the first user notification event in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	public UserNotificationEvent fetchByType_First(
		String type,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	public void removeByType(String type);

	/**
	 * Returns the number of user notification events where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching user notification events
	 */
	public int countByType(String type);

	/**
	 * Returns all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @return the matching user notification events
	 */
	public java.util.List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered);

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
	public java.util.List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end);

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
	public java.util.List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

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
	public java.util.List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

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
	public UserNotificationEvent findByU_T_GteT_D_First(
			long userId, String type, long timestamp, boolean delivered,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

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
	public UserNotificationEvent fetchByU_T_GteT_D_First(
		long userId, String type, long timestamp, boolean delivered,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 */
	public void removeByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered);

	/**
	 * Returns the number of user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @return the number of matching user notification events
	 */
	public int countByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered);

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
	public java.util.List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

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
	public UserNotificationEvent findByU_DT_D_AR_First(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

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
	public UserNotificationEvent fetchByU_DT_D_AR_First(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 */
	public void removeByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired);

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @return the number of matching user notification events
	 */
	public int countByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired);

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
	public java.util.List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

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
	public UserNotificationEvent findByU_DT_D_A_First(
			long userId, int deliveryType, boolean delivered, boolean archived,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

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
	public UserNotificationEvent fetchByU_DT_D_A_First(
		long userId, int deliveryType, boolean delivered, boolean archived,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 */
	public void removeByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived);

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	public int countByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived);

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
	public java.util.List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

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
	public UserNotificationEvent findByU_D_AR_A_First(
			long userId, boolean delivered, boolean actionRequired,
			boolean archived,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

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
	public UserNotificationEvent fetchByU_D_AR_A_First(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 */
	public void removeByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived);

	/**
	 * Returns the number of user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	public int countByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived);

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
	public java.util.List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

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
	public UserNotificationEvent findByU_T_DT_D_A_First(
			long userId, String type, int deliveryType, boolean delivered,
			boolean archived,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

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
	public UserNotificationEvent fetchByU_T_DT_D_A_First(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 */
	public void removeByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived);

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
	public int countByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived);

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
	public java.util.List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator,
		boolean useFinderCache);

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
	public UserNotificationEvent findByU_DT_D_AR_A_First(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, boolean archived,
			com.liferay.portal.kernel.util.OrderByComparator
				<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException;

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
	public UserNotificationEvent fetchByU_DT_D_AR_A_First(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator);

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 */
	public void removeByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived);

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
	public int countByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived);

	/**
	 * Creates a new user notification event with the primary key. Does not add the user notification event to the database.
	 *
	 * @param userNotificationEventId the primary key for the new user notification event
	 * @return the new user notification event
	 */
	public UserNotificationEvent create(long userNotificationEventId);

	/**
	 * Removes the user notification event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event that was removed
	 * @throws NoSuchUserNotificationEventException if a user notification event with the primary key could not be found
	 */
	public UserNotificationEvent remove(long userNotificationEventId)
		throws NoSuchUserNotificationEventException;

	public UserNotificationEvent updateImpl(
		UserNotificationEvent userNotificationEvent);

	/**
	 * Returns the user notification event with the primary key or throws a <code>NoSuchUserNotificationEventException</code> if it could not be found.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event
	 * @throws NoSuchUserNotificationEventException if a user notification event with the primary key could not be found
	 */
	public UserNotificationEvent findByPrimaryKey(long userNotificationEventId)
		throws NoSuchUserNotificationEventException;

	/**
	 * Returns the user notification event with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event, or <code>null</code> if a user notification event with the primary key could not be found
	 */
	public UserNotificationEvent fetchByPrimaryKey(
		long userNotificationEventId);

	/**
	 * Returns all the user notification events where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching user notification events
	 */
	public default java.util.List<UserNotificationEvent> findByUuid(
		String uuid) {

		return findByUuid(
			uuid, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByUuid(
		String uuid, int start, int end) {

		return findByUuid(uuid, start, end, null, true);
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
	public default java.util.List<UserNotificationEvent> findByUuid(
		String uuid, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByUuid(uuid, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching user notification events
	 */
	public default java.util.List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId) {

		return findByUuid_C(
			uuid, companyId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return findByUuid_C(uuid, companyId, start, end, null, true);
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
	public default java.util.List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByUuid_C(
			uuid, companyId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the user notification events where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the matching user notification events
	 */
	public default java.util.List<UserNotificationEvent> findByUserId(
		long userId) {

		return findByUserId(
			userId, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByUserId(
		long userId, int start, int end) {

		return findByUserId(userId, start, end, null, true);
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
	public default java.util.List<UserNotificationEvent> findByUserId(
		long userId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByUserId(userId, start, end, orderByComparator, true);
	}

	/**
	 * Returns all the user notification events where type = &#63;.
	 *
	 * @param type the type
	 * @return the matching user notification events
	 */
	public default java.util.List<UserNotificationEvent> findByType(
		String type) {

		return findByType(
			type, com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByType(
		String type, int start, int end) {

		return findByType(type, start, end, null, true);
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
	public default java.util.List<UserNotificationEvent> findByType(
		String type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByType(type, start, end, orderByComparator, true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		return findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end) {

		return findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired, start, end, null,
			true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByU_DT_D_AR(
			userId, deliveryType, delivered, actionRequired, start, end,
			orderByComparator, true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return findByU_DT_D_A(
			userId, deliveryType, delivered, archived,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived,
		int start, int end) {

		return findByU_DT_D_A(
			userId, deliveryType, delivered, archived, start, end, null, true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByU_DT_D_A(
			userId, deliveryType, delivered, archived, start, end,
			orderByComparator, true);
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
	public default java.util.List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived) {

		return findByU_D_AR_A(
			userId, delivered, actionRequired, archived,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived, int start, int end) {

		return findByU_D_AR_A(
			userId, delivered, actionRequired, archived, start, end, null,
			true);
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
	public default java.util.List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByU_D_AR_A(
			userId, delivered, actionRequired, archived, start, end,
			orderByComparator, true);
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
	public default java.util.List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		return findByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived, int start, int end) {

		return findByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived, start, end, null,
			true);
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
	public default java.util.List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByU_T_DT_D_A(
			userId, type, deliveryType, delivered, archived, start, end,
			orderByComparator, true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		return findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end) {

		return findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end, null, true);
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
	public default java.util.List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<UserNotificationEvent>
			orderByComparator) {

		return findByU_DT_D_AR_A(
			userId, deliveryType, delivered, actionRequired, archived, start,
			end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1021605113