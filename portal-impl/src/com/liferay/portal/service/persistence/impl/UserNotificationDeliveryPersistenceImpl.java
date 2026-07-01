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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchUserNotificationDeliveryException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserNotificationDelivery;
import com.liferay.portal.kernel.model.UserNotificationDeliveryTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.persistence.UserNotificationDeliveryPersistence;
import com.liferay.portal.kernel.service.persistence.UserNotificationDeliveryUtil;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.UserNotificationDeliveryImpl;
import com.liferay.portal.model.impl.UserNotificationDeliveryModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the user notification delivery service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class UserNotificationDeliveryPersistenceImpl
	extends BasePersistenceImpl
		<UserNotificationDelivery, NoSuchUserNotificationDeliveryException>
	implements UserNotificationDeliveryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>UserNotificationDeliveryUtil</code> to access the user notification delivery persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		UserNotificationDeliveryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<UserNotificationDelivery, NoSuchUserNotificationDeliveryException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the user notification deliveries where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>UserNotificationDeliveryModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of user notification deliveries
	 * @param end the upper bound of the range of user notification deliveries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching user notification deliveries
	 */
	@Override
	public List<UserNotificationDelivery> findByUserId(
		long userId, int start, int end,
		OrderByComparator<UserNotificationDelivery> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first user notification delivery in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification delivery
	 * @throws NoSuchUserNotificationDeliveryException if a matching user notification delivery could not be found
	 */
	@Override
	public UserNotificationDelivery findByUserId_First(
			long userId,
			OrderByComparator<UserNotificationDelivery> orderByComparator)
		throws NoSuchUserNotificationDeliveryException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first user notification delivery in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching user notification delivery, or <code>null</code> if a matching user notification delivery could not be found
	 */
	@Override
	public UserNotificationDelivery fetchByUserId_First(
		long userId,
		OrderByComparator<UserNotificationDelivery> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the user notification deliveries where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of user notification deliveries where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching user notification deliveries
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private UniquePersistenceFinder
		<UserNotificationDelivery, NoSuchUserNotificationDeliveryException>
			_uniquePersistenceFinderByU_P_C_N_D;

	/**
	 * Returns the user notification delivery where userId = &#63; and portletId = &#63; and classNameId = &#63; and notificationType = &#63; and deliveryType = &#63; or throws a <code>NoSuchUserNotificationDeliveryException</code> if it could not be found.
	 *
	 * @param userId the user ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param notificationType the notification type
	 * @param deliveryType the delivery type
	 * @return the matching user notification delivery
	 * @throws NoSuchUserNotificationDeliveryException if a matching user notification delivery could not be found
	 */
	@Override
	public UserNotificationDelivery findByU_P_C_N_D(
			long userId, String portletId, long classNameId,
			int notificationType, int deliveryType)
		throws NoSuchUserNotificationDeliveryException {

		return _uniquePersistenceFinderByU_P_C_N_D.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				userId, portletId, classNameId, notificationType, deliveryType
			});
	}

	/**
	 * Returns the user notification delivery where userId = &#63; and portletId = &#63; and classNameId = &#63; and notificationType = &#63; and deliveryType = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param userId the user ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param notificationType the notification type
	 * @param deliveryType the delivery type
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching user notification delivery, or <code>null</code> if a matching user notification delivery could not be found
	 */
	@Override
	public UserNotificationDelivery fetchByU_P_C_N_D(
		long userId, String portletId, long classNameId, int notificationType,
		int deliveryType, boolean useFinderCache) {

		return _uniquePersistenceFinderByU_P_C_N_D.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				userId, portletId, classNameId, notificationType, deliveryType
			},
			useFinderCache);
	}

	/**
	 * Removes the user notification delivery where userId = &#63; and portletId = &#63; and classNameId = &#63; and notificationType = &#63; and deliveryType = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param notificationType the notification type
	 * @param deliveryType the delivery type
	 * @return the user notification delivery that was removed
	 */
	@Override
	public UserNotificationDelivery removeByU_P_C_N_D(
			long userId, String portletId, long classNameId,
			int notificationType, int deliveryType)
		throws NoSuchUserNotificationDeliveryException {

		UserNotificationDelivery userNotificationDelivery = findByU_P_C_N_D(
			userId, portletId, classNameId, notificationType, deliveryType);

		return remove(userNotificationDelivery);
	}

	/**
	 * Returns the number of user notification deliveries where userId = &#63; and portletId = &#63; and classNameId = &#63; and notificationType = &#63; and deliveryType = &#63;.
	 *
	 * @param userId the user ID
	 * @param portletId the portlet ID
	 * @param classNameId the class name ID
	 * @param notificationType the notification type
	 * @param deliveryType the delivery type
	 * @return the number of matching user notification deliveries
	 */
	@Override
	public int countByU_P_C_N_D(
		long userId, String portletId, long classNameId, int notificationType,
		int deliveryType) {

		return _uniquePersistenceFinderByU_P_C_N_D.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				userId, portletId, classNameId, notificationType, deliveryType
			});
	}

	public UserNotificationDeliveryPersistenceImpl() {
		setModelClass(UserNotificationDelivery.class);

		setModelImplClass(UserNotificationDeliveryImpl.class);
		setModelPKClass(long.class);

		setTable(UserNotificationDeliveryTable.INSTANCE);
	}

	/**
	 * Creates a new user notification delivery with the primary key. Does not add the user notification delivery to the database.
	 *
	 * @param userNotificationDeliveryId the primary key for the new user notification delivery
	 * @return the new user notification delivery
	 */
	@Override
	public UserNotificationDelivery create(long userNotificationDeliveryId) {
		UserNotificationDelivery userNotificationDelivery =
			new UserNotificationDeliveryImpl();

		userNotificationDelivery.setNew(true);
		userNotificationDelivery.setPrimaryKey(userNotificationDeliveryId);

		userNotificationDelivery.setCompanyId(
			CompanyThreadLocal.getCompanyId());

		return userNotificationDelivery;
	}

	/**
	 * Removes the user notification delivery with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param userNotificationDeliveryId the primary key of the user notification delivery
	 * @return the user notification delivery that was removed
	 * @throws NoSuchUserNotificationDeliveryException if a user notification delivery with the primary key could not be found
	 */
	@Override
	public UserNotificationDelivery remove(long userNotificationDeliveryId)
		throws NoSuchUserNotificationDeliveryException {

		return remove((Serializable)userNotificationDeliveryId);
	}

	@Override
	protected UserNotificationDelivery removeImpl(
		UserNotificationDelivery userNotificationDelivery) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(userNotificationDelivery)) {
				userNotificationDelivery =
					(UserNotificationDelivery)session.get(
						UserNotificationDeliveryImpl.class,
						userNotificationDelivery.getPrimaryKeyObj());
			}

			if (userNotificationDelivery != null) {
				session.delete(userNotificationDelivery);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (userNotificationDelivery != null) {
			clearCache(userNotificationDelivery);
		}

		return userNotificationDelivery;
	}

	@Override
	public UserNotificationDelivery updateImpl(
		UserNotificationDelivery userNotificationDelivery) {

		boolean isNew = userNotificationDelivery.isNew();

		if (!(userNotificationDelivery instanceof
				UserNotificationDeliveryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(userNotificationDelivery.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					userNotificationDelivery);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in userNotificationDelivery proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom UserNotificationDelivery implementation " +
					userNotificationDelivery.getClass());
		}

		UserNotificationDeliveryModelImpl userNotificationDeliveryModelImpl =
			(UserNotificationDeliveryModelImpl)userNotificationDelivery;

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(userNotificationDelivery);
			}
			else {
				userNotificationDelivery =
					(UserNotificationDelivery)session.merge(
						userNotificationDelivery);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(userNotificationDelivery, false);

		if (isNew) {
			userNotificationDelivery.setNew(false);
		}

		userNotificationDelivery.resetOriginalValues();

		return userNotificationDelivery;
	}

	/**
	 * Returns the user notification delivery with the primary key or throws a <code>NoSuchUserNotificationDeliveryException</code> if it could not be found.
	 *
	 * @param userNotificationDeliveryId the primary key of the user notification delivery
	 * @return the user notification delivery
	 * @throws NoSuchUserNotificationDeliveryException if a user notification delivery with the primary key could not be found
	 */
	@Override
	public UserNotificationDelivery findByPrimaryKey(
			long userNotificationDeliveryId)
		throws NoSuchUserNotificationDeliveryException {

		return findByPrimaryKey((Serializable)userNotificationDeliveryId);
	}

	/**
	 * Returns the user notification delivery with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param userNotificationDeliveryId the primary key of the user notification delivery
	 * @return the user notification delivery, or <code>null</code> if a user notification delivery with the primary key could not be found
	 */
	@Override
	public UserNotificationDelivery fetchByPrimaryKey(
		long userNotificationDeliveryId) {

		return fetchByPrimaryKey((Serializable)userNotificationDeliveryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "userNotificationDeliveryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_USERNOTIFICATIONDELIVERY;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return UserNotificationDeliveryModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the user notification delivery persistence.
	 */
	public void afterPropertiesSet() {
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
				_SQL_SELECT_USERNOTIFICATIONDELIVERY_WHERE,
				_SQL_COUNT_USERNOTIFICATIONDELIVERY_WHERE,
				UserNotificationDeliveryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"userNotificationDelivery.", "userId",
					FinderColumn.Type.LONG, "=", true, true,
					UserNotificationDelivery::getUserId));

		_uniquePersistenceFinderByU_P_C_N_D = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByU_P_C_N_D",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName()
				},
				new String[] {
					"userId", "portletId", "classNameId", "notificationType",
					"deliveryType"
				},
				0, 2, false, UserNotificationDelivery::getUserId,
				convertNullFunction(UserNotificationDelivery::getPortletId),
				UserNotificationDelivery::getClassNameId,
				UserNotificationDelivery::getNotificationType,
				UserNotificationDelivery::getDeliveryType),
			_SQL_SELECT_USERNOTIFICATIONDELIVERY_WHERE, "",
			new FinderColumn<>(
				"userNotificationDelivery.", "userId", FinderColumn.Type.LONG,
				"=", true, true, UserNotificationDelivery::getUserId),
			new FinderColumn<>(
				"userNotificationDelivery.", "portletId",
				FinderColumn.Type.STRING, "=", true, true,
				UserNotificationDelivery::getPortletId),
			new FinderColumn<>(
				"userNotificationDelivery.", "classNameId",
				FinderColumn.Type.LONG, "=", true, true,
				UserNotificationDelivery::getClassNameId),
			new FinderColumn<>(
				"userNotificationDelivery.", "notificationType",
				FinderColumn.Type.INTEGER, "=", true, true,
				UserNotificationDelivery::getNotificationType),
			new FinderColumn<>(
				"userNotificationDelivery.", "deliveryType",
				FinderColumn.Type.INTEGER, "=", true, true,
				UserNotificationDelivery::getDeliveryType));

		UserNotificationDeliveryUtil.setPersistence(this);
	}

	public void destroy() {
		UserNotificationDeliveryUtil.setPersistence(null);

		EntityCacheUtil.removeCache(
			UserNotificationDeliveryImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		UserNotificationDeliveryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_USERNOTIFICATIONDELIVERY =
		"SELECT userNotificationDelivery FROM UserNotificationDelivery userNotificationDelivery";

	private static final String _SQL_SELECT_USERNOTIFICATIONDELIVERY_WHERE =
		"SELECT userNotificationDelivery FROM UserNotificationDelivery userNotificationDelivery WHERE ";

	private static final String _SQL_COUNT_USERNOTIFICATIONDELIVERY_WHERE =
		"SELECT COUNT(userNotificationDelivery) FROM UserNotificationDelivery userNotificationDelivery WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No UserNotificationDelivery exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		UserNotificationDeliveryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1143784113