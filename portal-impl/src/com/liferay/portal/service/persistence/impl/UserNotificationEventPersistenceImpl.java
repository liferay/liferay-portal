/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.impl;

import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchUserNotificationEventException;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.model.UserNotificationEventTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.UserNotificationEventPersistence;
import com.liferay.portal.kernel.service.persistence.UserNotificationEventUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.model.impl.UserNotificationEventImpl;
import com.liferay.portal.model.impl.UserNotificationEventModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence implementation for the user notification event service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserNotificationEventPersistenceImpl
	extends BasePersistenceImpl
		<UserNotificationEvent, NoSuchUserNotificationEventException>
	implements UserNotificationEventPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserNotificationEventUtil</code> to access the user notification event persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserNotificationEventImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the user notification events where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByUuid_First(
			String uuid,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByUuid.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByUuid_First(
		String uuid,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	/**
	 * Returns the number of user notification events where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			start, end, orderByComparator, useFinderCache);
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
	@Override
	public UserNotificationEvent findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of user notification events where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			FinderCacheUtil.getFinderCache(), new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByUserId(
		long userId, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByUserId_First(
			long userId,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByUserId_First(
		long userId,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of user notification events where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByType;

	/**
	 * Returns an ordered range of all the user notification events where type = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param type the type
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByType(
		String type, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByType.find(
			FinderCacheUtil.getFinderCache(), new Object[] {type}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByType_First(
			String type,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByType.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where type = &#63;.
	 *
	 * @param type the type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByType_First(
		String type,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByType.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {type},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where type = &#63; from the database.
	 *
	 * @param type the type
	 */
	@Override
	public void removeByType(String type) {
		_collectionPersistenceFinderByType.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {type});
	}

	/**
	 * Returns the number of user notification events where type = &#63;.
	 *
	 * @param type the type
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByType(String type) {
		return _collectionPersistenceFinderByType.count(
			FinderCacheUtil.getFinderCache(), new Object[] {type});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_DT;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_DT(
		long userId, int deliveryType, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_DT.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_DT_First(
			long userId, int deliveryType,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_DT.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType}, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_DT_First(
		long userId, int deliveryType,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_DT.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType}, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 */
	@Override
	public void removeByU_DT(long userId, int deliveryType) {
		_collectionPersistenceFinderByU_DT.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_DT(long userId, int deliveryType) {
		return _collectionPersistenceFinderByU_DT.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_D;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_D(
		long userId, boolean delivered, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_D.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, delivered},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_D_First(
			long userId, boolean delivered,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_D.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, delivered},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_D_First(
		long userId, boolean delivered,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_D.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, delivered},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and delivered = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 */
	@Override
	public void removeByU_D(long userId, boolean delivered) {
		_collectionPersistenceFinderByU_D.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, delivered});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_D(long userId, boolean delivered) {
		return _collectionPersistenceFinderByU_D.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, delivered});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_A(
		long userId, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_A.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, archived},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_A_First(
			long userId, boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_A.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, archived},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_A_First(
		long userId, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_A.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, archived},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param archived the archived
	 */
	@Override
	public void removeByU_A(long userId, boolean archived) {
		_collectionPersistenceFinderByU_A.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, archived});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_A(long userId, boolean archived) {
		return _collectionPersistenceFinderByU_A.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId, archived});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_DT_D;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_DT_D(
		long userId, int deliveryType, boolean delivered, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_DT_D.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_DT_D_First(
			long userId, int deliveryType, boolean delivered,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_DT_D.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered}, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_DT_D_First(
		long userId, int deliveryType, boolean delivered,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_DT_D.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered}, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 */
	@Override
	public void removeByU_DT_D(
		long userId, int deliveryType, boolean delivered) {

		_collectionPersistenceFinderByU_DT_D.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_DT_D(long userId, int deliveryType, boolean delivered) {
		return _collectionPersistenceFinderByU_DT_D.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_DT_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_DT_A(
		long userId, int deliveryType, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_DT_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, archived}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_DT_A_First(
			long userId, int deliveryType, boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_DT_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, archived}, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_DT_A_First(
		long userId, int deliveryType, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_DT_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, archived}, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param archived the archived
	 */
	@Override
	public void removeByU_DT_A(
		long userId, int deliveryType, boolean archived) {

		_collectionPersistenceFinderByU_DT_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, archived});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_DT_A(long userId, int deliveryType, boolean archived) {
		return _collectionPersistenceFinderByU_DT_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, archived});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_D_AR;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_D_AR(
		long userId, boolean delivered, boolean actionRequired, int start,
		int end, OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_D_AR.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_D_AR_First(
			long userId, boolean delivered, boolean actionRequired,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_D_AR.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_D_AR_First(
		long userId, boolean delivered, boolean actionRequired,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_D_AR.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 */
	@Override
	public void removeByU_D_AR(
		long userId, boolean delivered, boolean actionRequired) {

		_collectionPersistenceFinderByU_D_AR.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_D_AR(
		long userId, boolean delivered, boolean actionRequired) {

		return _collectionPersistenceFinderByU_D_AR.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_D_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_D_A(
		long userId, boolean delivered, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_D_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, archived}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_D_A_First(
			long userId, boolean delivered, boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_D_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, archived}, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_D_A_First(
		long userId, boolean delivered, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_D_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, archived}, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and delivered = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param archived the archived
	 */
	@Override
	public void removeByU_D_A(
		long userId, boolean delivered, boolean archived) {

		_collectionPersistenceFinderByU_D_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, archived});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_D_A(long userId, boolean delivered, boolean archived) {
		return _collectionPersistenceFinderByU_D_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, archived});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_AR_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_AR_A(
		long userId, boolean actionRequired, boolean archived, int start,
		int end, OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_AR_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, actionRequired, archived}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_AR_A_First(
			long userId, boolean actionRequired, boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_AR_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, actionRequired, archived}, orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_AR_A_First(
		long userId, boolean actionRequired, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_AR_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, actionRequired, archived}, orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and actionRequired = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param actionRequired the action required
	 * @param archived the archived
	 */
	@Override
	public void removeByU_AR_A(
		long userId, boolean actionRequired, boolean archived) {

		_collectionPersistenceFinderByU_AR_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, actionRequired, archived});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_AR_A(
		long userId, boolean actionRequired, boolean archived) {

		return _collectionPersistenceFinderByU_AR_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, actionRequired, archived});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_T_GteT_D;

	/**
	 * Returns all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 * @return the matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered) {

		return findByU_T_GteT_D(
			userId, type, timestamp, delivered, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end) {

		return findByU_T_GteT_D(
			userId, type, timestamp, delivered, start, end, null);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end, OrderByComparator<UserNotificationEvent> orderByComparator) {

		return findByU_T_GteT_D(
			userId, type, timestamp, delivered, start, end, orderByComparator,
			true);
	}

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered, int start,
		int end, OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_T_GteT_D.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, timestamp, delivered}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public UserNotificationEvent findByU_T_GteT_D_First(
			long userId, String type, long timestamp, boolean delivered,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_T_GteT_D.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, timestamp, delivered},
			orderByComparator);
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
	@Override
	public UserNotificationEvent fetchByU_T_GteT_D_First(
		long userId, String type, long timestamp, boolean delivered,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_T_GteT_D.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, timestamp, delivered},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and type = &#63; and timestamp &ge; &#63; and delivered = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param timestamp the timestamp
	 * @param delivered the delivered
	 */
	@Override
	public void removeByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered) {

		_collectionPersistenceFinderByU_T_GteT_D.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, timestamp, delivered});
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
	@Override
	public int countByU_T_GteT_D(
		long userId, String type, long timestamp, boolean delivered) {

		return _collectionPersistenceFinderByU_T_GteT_D.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, timestamp, delivered});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_T_DT_D;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_T_DT_D(
		long userId, String type, int deliveryType, boolean delivered,
		int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_T_DT_D.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_T_DT_D_First(
			long userId, String type, int deliveryType, boolean delivered,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_T_DT_D.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_T_DT_D_First(
		long userId, String type, int deliveryType, boolean delivered,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_T_DT_D.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 */
	@Override
	public void removeByU_T_DT_D(
		long userId, String type, int deliveryType, boolean delivered) {

		_collectionPersistenceFinderByU_T_DT_D.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63;.
	 *
	 * @param userId the user ID
	 * @param type the type
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_T_DT_D(
		long userId, String type, int deliveryType, boolean delivered) {

		return _collectionPersistenceFinderByU_T_DT_D.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_DT_D_AR;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_DT_D_AR.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, actionRequired},
			start, end, orderByComparator, useFinderCache);
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
	@Override
	public UserNotificationEvent findByU_DT_D_AR_First(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_DT_D_AR.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, actionRequired},
			orderByComparator);
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
	@Override
	public UserNotificationEvent fetchByU_DT_D_AR_First(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_DT_D_AR.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, actionRequired},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 */
	@Override
	public void removeByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		_collectionPersistenceFinderByU_DT_D_AR.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, actionRequired});
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
	@Override
	public int countByU_DT_D_AR(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired) {

		return _collectionPersistenceFinderByU_DT_D_AR.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, actionRequired});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_DT_D_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived,
		int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_DT_D_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, archived}, start,
			end, orderByComparator, useFinderCache);
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
	@Override
	public UserNotificationEvent findByU_DT_D_A_First(
			long userId, int deliveryType, boolean delivered, boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_DT_D_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, archived},
			orderByComparator);
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
	@Override
	public UserNotificationEvent fetchByU_DT_D_A_First(
		long userId, int deliveryType, boolean delivered, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_DT_D_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, archived},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param delivered the delivered
	 * @param archived the archived
	 */
	@Override
	public void removeByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		_collectionPersistenceFinderByU_DT_D_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, archived});
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
	@Override
	public int countByU_DT_D_A(
		long userId, int deliveryType, boolean delivered, boolean archived) {

		return _collectionPersistenceFinderByU_DT_D_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, delivered, archived});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_DT_AR_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param start the lower bound of the range of user notification events
	 * @param end the upper bound of the range of user notification events (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification events
	 */
	@Override
	public List<UserNotificationEvent> findByU_DT_AR_A(
		long userId, int deliveryType, boolean actionRequired, boolean archived,
		int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_DT_AR_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, actionRequired, archived},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event
	 * @throws NoSuchUserNotificationEventException if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent findByU_DT_AR_A_First(
			long userId, int deliveryType, boolean actionRequired,
			boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_DT_AR_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, actionRequired, archived},
			orderByComparator);
	}

	/**
	 * Returns the first user notification event in the ordered set where userId = &#63; and deliveryType = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification event, or <code>null</code> if a matching user notification event could not be found
	 */
	@Override
	public UserNotificationEvent fetchByU_DT_AR_A_First(
		long userId, int deliveryType, boolean actionRequired, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_DT_AR_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, actionRequired, archived},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and deliveryType = &#63; and actionRequired = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param actionRequired the action required
	 * @param archived the archived
	 */
	@Override
	public void removeByU_DT_AR_A(
		long userId, int deliveryType, boolean actionRequired,
		boolean archived) {

		_collectionPersistenceFinderByU_DT_AR_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, actionRequired, archived});
	}

	/**
	 * Returns the number of user notification events where userId = &#63; and deliveryType = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * @param userId the user ID
	 * @param deliveryType the delivery type
	 * @param actionRequired the action required
	 * @param archived the archived
	 * @return the number of matching user notification events
	 */
	@Override
	public int countByU_DT_AR_A(
		long userId, int deliveryType, boolean actionRequired,
		boolean archived) {

		return _collectionPersistenceFinderByU_DT_AR_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, deliveryType, actionRequired, archived});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_D_AR_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_D_AR_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired, archived}, start,
			end, orderByComparator, useFinderCache);
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
	@Override
	public UserNotificationEvent findByU_D_AR_A_First(
			long userId, boolean delivered, boolean actionRequired,
			boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_D_AR_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired, archived},
			orderByComparator);
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
	@Override
	public UserNotificationEvent fetchByU_D_AR_A_First(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_D_AR_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired, archived},
			orderByComparator);
	}

	/**
	 * Removes all the user notification events where userId = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param delivered the delivered
	 * @param actionRequired the action required
	 * @param archived the archived
	 */
	@Override
	public void removeByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived) {

		_collectionPersistenceFinderByU_D_AR_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired, archived});
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
	@Override
	public int countByU_D_AR_A(
		long userId, boolean delivered, boolean actionRequired,
		boolean archived) {

		return _collectionPersistenceFinderByU_D_AR_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, delivered, actionRequired, archived});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_T_DT_D_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and type = &#63; and deliveryType = &#63; and delivered = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_T_DT_D_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered, archived},
			start, end, orderByComparator, useFinderCache);
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
	@Override
	public UserNotificationEvent findByU_T_DT_D_A_First(
			long userId, String type, int deliveryType, boolean delivered,
			boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_T_DT_D_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered, archived},
			orderByComparator);
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
	@Override
	public UserNotificationEvent fetchByU_T_DT_D_A_First(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_T_DT_D_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered, archived},
			orderByComparator);
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
	@Override
	public void removeByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		_collectionPersistenceFinderByU_T_DT_D_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered, archived});
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
	@Override
	public int countByU_T_DT_D_A(
		long userId, String type, int deliveryType, boolean delivered,
		boolean archived) {

		return _collectionPersistenceFinderByU_T_DT_D_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, type, deliveryType, delivered, archived});
	}

	private CollectionPersistenceFinder
		<UserNotificationEvent, NoSuchUserNotificationEventException>
			_collectionPersistenceFinderByU_DT_D_AR_A;

	/**
	 * Returns an ordered range of all the user notification events where userId = &#63; and deliveryType = &#63; and delivered = &#63; and actionRequired = &#63; and archived = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationEventModelImpl</code>.
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
	@Override
	public List<UserNotificationEvent> findByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived, int start, int end,
		OrderByComparator<UserNotificationEvent> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_DT_D_AR_A.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				userId, deliveryType, delivered, actionRequired, archived
			},
			start, end, orderByComparator, useFinderCache);
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
	@Override
	public UserNotificationEvent findByU_DT_D_AR_A_First(
			long userId, int deliveryType, boolean delivered,
			boolean actionRequired, boolean archived,
			OrderByComparator<UserNotificationEvent> orderByComparator)
		throws NoSuchUserNotificationEventException {

		return _collectionPersistenceFinderByU_DT_D_AR_A.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				userId, deliveryType, delivered, actionRequired, archived
			},
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
	@Override
	public UserNotificationEvent fetchByU_DT_D_AR_A_First(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived,
		OrderByComparator<UserNotificationEvent> orderByComparator) {

		return _collectionPersistenceFinderByU_DT_D_AR_A.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				userId, deliveryType, delivered, actionRequired, archived
			},
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
	@Override
	public void removeByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		_collectionPersistenceFinderByU_DT_D_AR_A.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				userId, deliveryType, delivered, actionRequired, archived
			});
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
	@Override
	public int countByU_DT_D_AR_A(
		long userId, int deliveryType, boolean delivered,
		boolean actionRequired, boolean archived) {

		return _collectionPersistenceFinderByU_DT_D_AR_A.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				userId, deliveryType, delivered, actionRequired, archived
			});
	}

	public UserNotificationEventPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("type", "type_");

		setDBColumnNames(dbColumnNames);

		setModelClass(UserNotificationEvent.class);

		setModelImplClass(UserNotificationEventImpl.class);
		setModelPKClass(long.class);

		setTable(UserNotificationEventTable.INSTANCE);
	}

	/**
	 * Creates a new user notification event with the primary key. Does not add the user notification event to the database.
	 *
	 * @param userNotificationEventId the primary key for the new user notification event
	 * @return the new user notification event
	 */
	@Override
	public UserNotificationEvent create(long userNotificationEventId) {
		UserNotificationEvent userNotificationEvent =
			new UserNotificationEventImpl();

		userNotificationEvent.setNew(true);
		userNotificationEvent.setPrimaryKey(userNotificationEventId);

		String uuid = PortalUUIDUtil.generate();

		userNotificationEvent.setUuid(uuid);

		userNotificationEvent.setCompanyId(CompanyThreadLocal.getCompanyId());

		return userNotificationEvent;
	}

	/**
	 * Removes the user notification event with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event that was removed
	 * @throws NoSuchUserNotificationEventException if a user notification event with the primary key could not be found
	 */
	@Override
	public UserNotificationEvent remove(long userNotificationEventId)
		throws NoSuchUserNotificationEventException {

		return remove((Serializable)userNotificationEventId);
	}

	@Override
	protected UserNotificationEvent removeImpl(
		UserNotificationEvent userNotificationEvent) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userNotificationEvent)) {
				userNotificationEvent = (UserNotificationEvent)session.get(
					UserNotificationEventImpl.class,
					userNotificationEvent.getPrimaryKeyObj());
			}

			if (userNotificationEvent != null) {
				session.delete(userNotificationEvent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userNotificationEvent != null) {
			clearCache(userNotificationEvent);
		}

		return userNotificationEvent;
	}

	@Override
	public UserNotificationEvent updateImpl(
		UserNotificationEvent userNotificationEvent) {

		boolean isNew = userNotificationEvent.isNew();

		if (!(userNotificationEvent instanceof
				UserNotificationEventModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userNotificationEvent.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					userNotificationEvent);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userNotificationEvent proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserNotificationEvent implementation " +
					userNotificationEvent.getClass());
		}

		UserNotificationEventModelImpl userNotificationEventModelImpl =
			(UserNotificationEventModelImpl)userNotificationEvent;

		if (Validator.isNull(userNotificationEvent.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			userNotificationEvent.setUuid(uuid);
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(userNotificationEvent);
			}
			else {
				userNotificationEvent = (UserNotificationEvent)session.merge(
					userNotificationEvent);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(userNotificationEvent, false);

		if (isNew) {
			userNotificationEvent.setNew(false);
		}

		userNotificationEvent.resetOriginalValues();

		return userNotificationEvent;
	}

	/**
	 * Returns the user notification event with the primary key or throws a <code>NoSuchUserNotificationEventException</code> if it could not be found.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event
	 * @throws NoSuchUserNotificationEventException if a user notification event with the primary key could not be found
	 */
	@Override
	public UserNotificationEvent findByPrimaryKey(long userNotificationEventId)
		throws NoSuchUserNotificationEventException {

		return findByPrimaryKey((Serializable)userNotificationEventId);
	}

	/**
	 * Returns the user notification event with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userNotificationEventId the primary key of the user notification event
	 * @return the user notification event, or <code>null</code> if a user notification event with the primary key could not be found
	 */
	@Override
	public UserNotificationEvent fetchByPrimaryKey(
		long userNotificationEventId) {

		return fetchByPrimaryKey((Serializable)userNotificationEventId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "userNotificationEventId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERNOTIFICATIONEVENT;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UserNotificationEventModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the user notification event persistence.
	 */
	public void afterPropertiesSet() {
		_collectionPersistenceFinderByUuid = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"uuid_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid",
				new String[] {String.class.getName()}, new String[] {"uuid_"},
				0, 1, false, null),
			_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
			_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
			UserNotificationEventModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"userNotificationEvent.", "uuid", "uuid_",
				FinderColumn.Type.STRING, "=", true, true,
				UserNotificationEvent::getUuid));

		_collectionPersistenceFinderByUuid_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUuid_C",
					new String[] {
						String.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"uuid_", "companyId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUuid_C",
					new String[] {String.class.getName(), Long.class.getName()},
					new String[] {"uuid_", "companyId"}, 0, 1, false, null),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "uuid", "uuid_",
					FinderColumn.Type.STRING, "=", true, true,
					UserNotificationEvent::getUuid),
				new FinderColumn<>(
					"userNotificationEvent.", "companyId",
					FinderColumn.Type.LONG, "=", true, true,
					UserNotificationEvent::getCompanyId));

		_collectionPersistenceFinderByUserId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByUserId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByUserId",
					new String[] {Long.class.getName()},
					new String[] {"userId"}, false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId));

		_collectionPersistenceFinderByType = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByType",
				new String[] {
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"type_"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByType",
				new String[] {String.class.getName()}, new String[] {"type_"},
				0, 1, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByType",
				new String[] {String.class.getName()}, new String[] {"type_"},
				0, 1, false, null),
			_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
			_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
			UserNotificationEventModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"userNotificationEvent.", "type", "type_",
				FinderColumn.Type.STRING, "=", true, true,
				UserNotificationEvent::getType));

		_collectionPersistenceFinderByU_DT = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_DT",
				new String[] {
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "deliveryType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_DT",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId", "deliveryType"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_DT",
				new String[] {Long.class.getName(), Integer.class.getName()},
				new String[] {"userId", "deliveryType"}, false),
			_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
			_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
			UserNotificationEventModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"userNotificationEvent.", "userId", FinderColumn.Type.LONG, "=",
				true, true, UserNotificationEvent::getUserId),
			new FinderColumn<>(
				"userNotificationEvent.", "deliveryType",
				FinderColumn.Type.INTEGER, "=", true, true,
				UserNotificationEvent::getDeliveryType));

		_collectionPersistenceFinderByU_D = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_D",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "delivered"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_D",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"userId", "delivered"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_D",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"userId", "delivered"}, false),
			_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
			_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
			UserNotificationEventModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"userNotificationEvent.", "userId", FinderColumn.Type.LONG, "=",
				true, true, UserNotificationEvent::getUserId),
			new FinderColumn<>(
				"userNotificationEvent.", "delivered",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				UserNotificationEvent::isDelivered));

		_collectionPersistenceFinderByU_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "archived"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"userId", "archived"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_A",
				new String[] {Long.class.getName(), Boolean.class.getName()},
				new String[] {"userId", "archived"}, false),
			_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
			_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
			UserNotificationEventModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"userNotificationEvent.", "userId", FinderColumn.Type.LONG, "=",
				true, true, UserNotificationEvent::getUserId),
			new FinderColumn<>(
				"userNotificationEvent.", "archived", FinderColumn.Type.BOOLEAN,
				"=", true, true, UserNotificationEvent::isArchived));

		_collectionPersistenceFinderByU_DT_D =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_DT_D",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "deliveryType", "delivered"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_DT_D",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"userId", "deliveryType", "delivered"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_DT_D",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"userId", "deliveryType", "delivered"},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "deliveryType",
					FinderColumn.Type.INTEGER, "=", true, true,
					UserNotificationEvent::getDeliveryType),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered));

		_collectionPersistenceFinderByU_DT_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_DT_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "deliveryType", "archived"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_DT_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"userId", "deliveryType", "archived"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_DT_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"userId", "deliveryType", "archived"}, false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "deliveryType",
					FinderColumn.Type.INTEGER, "=", true, true,
					UserNotificationEvent::getDeliveryType),
				new FinderColumn<>(
					"userNotificationEvent.", "archived",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isArchived));

		_collectionPersistenceFinderByU_D_AR =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_D_AR",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "delivered", "actionRequired"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_D_AR",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"userId", "delivered", "actionRequired"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_D_AR",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"userId", "delivered", "actionRequired"},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered),
				new FinderColumn<>(
					"userNotificationEvent.", "actionRequired",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isActionRequired));

		_collectionPersistenceFinderByU_D_A = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_D_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"userId", "delivered", "archived"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_D_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"userId", "delivered", "archived"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_D_A",
				new String[] {
					Long.class.getName(), Boolean.class.getName(),
					Boolean.class.getName()
				},
				new String[] {"userId", "delivered", "archived"}, false),
			_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
			_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
			UserNotificationEventModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX,
			"", "",
			new FinderColumn<>(
				"userNotificationEvent.", "userId", FinderColumn.Type.LONG, "=",
				true, true, UserNotificationEvent::getUserId),
			new FinderColumn<>(
				"userNotificationEvent.", "delivered",
				FinderColumn.Type.BOOLEAN, "=", true, true,
				UserNotificationEvent::isDelivered),
			new FinderColumn<>(
				"userNotificationEvent.", "archived", FinderColumn.Type.BOOLEAN,
				"=", true, true, UserNotificationEvent::isArchived));

		_collectionPersistenceFinderByU_AR_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_AR_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "actionRequired", "archived"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_AR_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"userId", "actionRequired", "archived"},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_AR_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {"userId", "actionRequired", "archived"},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "actionRequired",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isActionRequired),
				new FinderColumn<>(
					"userNotificationEvent.", "archived",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isArchived));

		_collectionPersistenceFinderByU_T_GteT_D =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_T_GteT_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"userId", "type_", "timestamp", "delivered"},
					true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByU_T_GteT_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Long.class.getName(), Boolean.class.getName()
					},
					new String[] {"userId", "type_", "timestamp", "delivered"},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					UserNotificationEvent::getType),
				new FinderColumn<>(
					"userNotificationEvent.", "timestamp",
					FinderColumn.Type.LONG, ">=", true, true,
					UserNotificationEvent::getTimestamp),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered));

		_collectionPersistenceFinderByU_T_DT_D =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_T_DT_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "type_", "deliveryType", "delivered"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_T_DT_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "type_", "deliveryType", "delivered"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByU_T_DT_D",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "type_", "deliveryType", "delivered"
					},
					0, 2, false, null),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					UserNotificationEvent::getType),
				new FinderColumn<>(
					"userNotificationEvent.", "deliveryType",
					FinderColumn.Type.INTEGER, "=", true, true,
					UserNotificationEvent::getDeliveryType),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered));

		_collectionPersistenceFinderByU_DT_D_AR =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_DT_D_AR",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "actionRequired"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByU_DT_D_AR",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "actionRequired"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByU_DT_D_AR",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "actionRequired"
					},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "deliveryType",
					FinderColumn.Type.INTEGER, "=", true, true,
					UserNotificationEvent::getDeliveryType),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered),
				new FinderColumn<>(
					"userNotificationEvent.", "actionRequired",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isActionRequired));

		_collectionPersistenceFinderByU_DT_D_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_DT_D_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_DT_D_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByU_DT_D_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "archived"
					},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "deliveryType",
					FinderColumn.Type.INTEGER, "=", true, true,
					UserNotificationEvent::getDeliveryType),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered),
				new FinderColumn<>(
					"userNotificationEvent.", "archived",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isArchived));

		_collectionPersistenceFinderByU_DT_AR_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_DT_AR_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "actionRequired", "archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByU_DT_AR_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "actionRequired", "archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByU_DT_AR_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "actionRequired", "archived"
					},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "deliveryType",
					FinderColumn.Type.INTEGER, "=", true, true,
					UserNotificationEvent::getDeliveryType),
				new FinderColumn<>(
					"userNotificationEvent.", "actionRequired",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isActionRequired),
				new FinderColumn<>(
					"userNotificationEvent.", "archived",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isArchived));

		_collectionPersistenceFinderByU_D_AR_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_D_AR_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "delivered", "actionRequired", "archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_D_AR_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "delivered", "actionRequired", "archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByU_D_AR_A",
					new String[] {
						Long.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Boolean.class.getName()
					},
					new String[] {
						"userId", "delivered", "actionRequired", "archived"
					},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered),
				new FinderColumn<>(
					"userNotificationEvent.", "actionRequired",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isActionRequired),
				new FinderColumn<>(
					"userNotificationEvent.", "archived",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isArchived));

		_collectionPersistenceFinderByU_T_DT_D_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_T_DT_D_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "type_", "deliveryType", "delivered",
						"archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByU_T_DT_D_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"userId", "type_", "deliveryType", "delivered",
						"archived"
					},
					0, 2, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByU_T_DT_D_A",
					new String[] {
						Long.class.getName(), String.class.getName(),
						Integer.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"userId", "type_", "deliveryType", "delivered",
						"archived"
					},
					0, 2, false, null),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "type", "type_",
					FinderColumn.Type.STRING, "=", true, true,
					UserNotificationEvent::getType),
				new FinderColumn<>(
					"userNotificationEvent.", "deliveryType",
					FinderColumn.Type.INTEGER, "=", true, true,
					UserNotificationEvent::getDeliveryType),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered),
				new FinderColumn<>(
					"userNotificationEvent.", "archived",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isArchived));

		_collectionPersistenceFinderByU_DT_D_AR_A =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_DT_D_AR_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Boolean.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "actionRequired",
						"archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByU_DT_D_AR_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "actionRequired",
						"archived"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByU_DT_D_AR_A",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Boolean.class.getName(), Boolean.class.getName(),
						Boolean.class.getName()
					},
					new String[] {
						"userId", "deliveryType", "delivered", "actionRequired",
						"archived"
					},
					false),
				_SQL_SELECT_USERNOTIFICATIONEVENT_WHERE,
				_SQL_COUNT_USERNOTIFICATIONEVENT_WHERE,
				UserNotificationEventModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationEvent.", "userId", FinderColumn.Type.LONG,
					"=", true, true, UserNotificationEvent::getUserId),
				new FinderColumn<>(
					"userNotificationEvent.", "deliveryType",
					FinderColumn.Type.INTEGER, "=", true, true,
					UserNotificationEvent::getDeliveryType),
				new FinderColumn<>(
					"userNotificationEvent.", "delivered",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isDelivered),
				new FinderColumn<>(
					"userNotificationEvent.", "actionRequired",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isActionRequired),
				new FinderColumn<>(
					"userNotificationEvent.", "archived",
					FinderColumn.Type.BOOLEAN, "=", true, true,
					UserNotificationEvent::isArchived));

		UserNotificationEventUtil.setPersistence(this);
	}

	public void destroy() {
		UserNotificationEventUtil.setPersistence(null);

		EntityCacheUtil.removeCache(UserNotificationEventImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		UserNotificationEventModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_USERNOTIFICATIONEVENT =
		"SELECT userNotificationEvent FROM UserNotificationEvent userNotificationEvent";

	private static final String _SQL_SELECT_USERNOTIFICATIONEVENT_WHERE =
		"SELECT userNotificationEvent FROM UserNotificationEvent userNotificationEvent WHERE ";

	private static final String _SQL_COUNT_USERNOTIFICATIONEVENT_WHERE =
		"SELECT COUNT(userNotificationEvent) FROM UserNotificationEvent userNotificationEvent WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No UserNotificationEvent exists with the key {";

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "type"});

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:343250698