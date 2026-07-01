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
import com.liferay.portal.kernel.exception.NoSuchSubscriptionException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Subscription;
import com.liferay.portal.kernel.model.SubscriptionTable;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.SubscriptionPersistence;
import com.liferay.portal.kernel.service.persistence.SubscriptionUtil;
import com.liferay.portal.kernel.service.persistence.impl.ArrayableFinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.model.impl.SubscriptionImpl;
import com.liferay.portal.model.impl.SubscriptionModelImpl;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The persistence implementation for the subscription service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @deprecated
 * @generated
 */
@Deprecated
public class SubscriptionPersistenceImpl
	extends BasePersistenceImpl<Subscription, NoSuchSubscriptionException>
	implements SubscriptionPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>SubscriptionUtil</code> to access the subscription persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		SubscriptionImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<Subscription, NoSuchSubscriptionException>
			_collectionPersistenceFinderByUserId;

	/**
	 * Returns an ordered range of all the subscriptions where userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SubscriptionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param start the lower bound of the range of subscriptions
	 * @param end the upper bound of the range of subscriptions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching subscriptions
	 */
	@Override
	public List<Subscription> findByUserId(
		long userId, int start, int end,
		OrderByComparator<Subscription> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByUserId.find(
			FinderCacheUtil.getFinderCache(), new Object[] {userId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first subscription in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching subscription
	 * @throws NoSuchSubscriptionException if a matching subscription could not be found
	 */
	@Override
	public Subscription findByUserId_First(
			long userId, OrderByComparator<Subscription> orderByComparator)
		throws NoSuchSubscriptionException {

		return _collectionPersistenceFinderByUserId.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Returns the first subscription in the ordered set where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching subscription, or <code>null</code> if a matching subscription could not be found
	 */
	@Override
	public Subscription fetchByUserId_First(
		long userId, OrderByComparator<Subscription> orderByComparator) {

		return _collectionPersistenceFinderByUserId.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {userId},
			orderByComparator);
	}

	/**
	 * Removes all the subscriptions where userId = &#63; from the database.
	 *
	 * @param userId the user ID
	 */
	@Override
	public void removeByUserId(long userId) {
		_collectionPersistenceFinderByUserId.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	/**
	 * Returns the number of subscriptions where userId = &#63;.
	 *
	 * @param userId the user ID
	 * @return the number of matching subscriptions
	 */
	@Override
	public int countByUserId(long userId) {
		return _collectionPersistenceFinderByUserId.count(
			FinderCacheUtil.getFinderCache(), new Object[] {userId});
	}

	private CollectionPersistenceFinder
		<Subscription, NoSuchSubscriptionException>
			_collectionPersistenceFinderByG_U;

	/**
	 * Returns an ordered range of all the subscriptions where groupId = &#63; and userId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SubscriptionModelImpl</code>.
	 * </p>
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param start the lower bound of the range of subscriptions
	 * @param end the upper bound of the range of subscriptions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching subscriptions
	 */
	@Override
	public List<Subscription> findByG_U(
		long groupId, long userId, int start, int end,
		OrderByComparator<Subscription> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByG_U.find(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first subscription in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching subscription
	 * @throws NoSuchSubscriptionException if a matching subscription could not be found
	 */
	@Override
	public Subscription findByG_U_First(
			long groupId, long userId,
			OrderByComparator<Subscription> orderByComparator)
		throws NoSuchSubscriptionException {

		return _collectionPersistenceFinderByG_U.findFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			orderByComparator);
	}

	/**
	 * Returns the first subscription in the ordered set where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching subscription, or <code>null</code> if a matching subscription could not be found
	 */
	@Override
	public Subscription fetchByG_U_First(
		long groupId, long userId,
		OrderByComparator<Subscription> orderByComparator) {

		return _collectionPersistenceFinderByG_U.fetchFirst(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId},
			orderByComparator);
	}

	/**
	 * Removes all the subscriptions where groupId = &#63; and userId = &#63; from the database.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 */
	@Override
	public void removeByG_U(long groupId, long userId) {
		_collectionPersistenceFinderByG_U.remove(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId});
	}

	/**
	 * Returns the number of subscriptions where groupId = &#63; and userId = &#63;.
	 *
	 * @param groupId the group ID
	 * @param userId the user ID
	 * @return the number of matching subscriptions
	 */
	@Override
	public int countByG_U(long groupId, long userId) {
		return _collectionPersistenceFinderByG_U.count(
			FinderCacheUtil.getFinderCache(), new Object[] {groupId, userId});
	}

	private CollectionPersistenceFinder
		<Subscription, NoSuchSubscriptionException>
			_collectionPersistenceFinderByU_C;

	/**
	 * Returns an ordered range of all the subscriptions where userId = &#63; and classNameId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SubscriptionModelImpl</code>.
	 * </p>
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param start the lower bound of the range of subscriptions
	 * @param end the upper bound of the range of subscriptions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching subscriptions
	 */
	@Override
	public List<Subscription> findByU_C(
		long userId, long classNameId, int start, int end,
		OrderByComparator<Subscription> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByU_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first subscription in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching subscription
	 * @throws NoSuchSubscriptionException if a matching subscription could not be found
	 */
	@Override
	public Subscription findByU_C_First(
			long userId, long classNameId,
			OrderByComparator<Subscription> orderByComparator)
		throws NoSuchSubscriptionException {

		return _collectionPersistenceFinderByU_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId}, orderByComparator);
	}

	/**
	 * Returns the first subscription in the ordered set where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching subscription, or <code>null</code> if a matching subscription could not be found
	 */
	@Override
	public Subscription fetchByU_C_First(
		long userId, long classNameId,
		OrderByComparator<Subscription> orderByComparator) {

		return _collectionPersistenceFinderByU_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId}, orderByComparator);
	}

	/**
	 * Removes all the subscriptions where userId = &#63; and classNameId = &#63; from the database.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 */
	@Override
	public void removeByU_C(long userId, long classNameId) {
		_collectionPersistenceFinderByU_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId});
	}

	/**
	 * Returns the number of subscriptions where userId = &#63; and classNameId = &#63;.
	 *
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @return the number of matching subscriptions
	 */
	@Override
	public int countByU_C(long userId, long classNameId) {
		return _collectionPersistenceFinderByU_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {userId, classNameId});
	}

	private CollectionPersistenceFinder
		<Subscription, NoSuchSubscriptionException>
			_collectionPersistenceFinderByC_C_C;

	/**
	 * Returns an ordered range of all the subscriptions where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SubscriptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param start the lower bound of the range of subscriptions
	 * @param end the upper bound of the range of subscriptions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching subscriptions
	 */
	@Override
	public List<Subscription> findByC_C_C(
		long companyId, long classNameId, long classPK, int start, int end,
		OrderByComparator<Subscription> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first subscription in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching subscription
	 * @throws NoSuchSubscriptionException if a matching subscription could not be found
	 */
	@Override
	public Subscription findByC_C_C_First(
			long companyId, long classNameId, long classPK,
			OrderByComparator<Subscription> orderByComparator)
		throws NoSuchSubscriptionException {

		return _collectionPersistenceFinderByC_C_C.findFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, orderByComparator);
	}

	/**
	 * Returns the first subscription in the ordered set where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching subscription, or <code>null</code> if a matching subscription could not be found
	 */
	@Override
	public Subscription fetchByC_C_C_First(
		long companyId, long classNameId, long classPK,
		OrderByComparator<Subscription> orderByComparator) {

		return _collectionPersistenceFinderByC_C_C.fetchFirst(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK}, orderByComparator);
	}

	/**
	 * Removes all the subscriptions where companyId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 */
	@Override
	public void removeByC_C_C(long companyId, long classNameId, long classPK) {
		_collectionPersistenceFinderByC_C_C.remove(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	/**
	 * Returns the number of subscriptions where companyId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching subscriptions
	 */
	@Override
	public int countByC_C_C(long companyId, long classNameId, long classPK) {
		return _collectionPersistenceFinderByC_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, classNameId, classPK});
	}

	private CollectionPersistenceFinder
		<Subscription, NoSuchSubscriptionException>
			_collectionPersistenceFinderByC_U_C_C;
	private UniquePersistenceFinder<Subscription, NoSuchSubscriptionException>
		_uniquePersistenceFinderByC_U_C_C;

	/**
	 * Returns an ordered range of all the subscriptions where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63;, optionally using the finder cache.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>SubscriptionModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @param start the lower bound of the range of subscriptions
	 * @param end the upper bound of the range of subscriptions (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching subscriptions
	 */
	@Override
	public List<Subscription> findByC_U_C_C(
		long companyId, long userId, long classNameId, long[] classPKs,
		int start, int end, OrderByComparator<Subscription> orderByComparator,
		boolean useFinderCache) {

		classPKs = ArrayUtil.sortedUnique(classPKs);

		if (classPKs.length == 1) {
			Subscription subscription = fetchByC_U_C_C(
				companyId, userId, classNameId, classPKs[0], useFinderCache);

			if (subscription == null) {
				return Collections.emptyList();
			}
			else {
				List<Subscription> list = new ArrayList<Subscription>(1);

				list.add(subscription);

				return list;
			}
		}

		return _collectionPersistenceFinderByC_U_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, userId, classNameId, classPKs}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the subscription where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; or throws a <code>NoSuchSubscriptionException</code> if it could not be found.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the matching subscription
	 * @throws NoSuchSubscriptionException if a matching subscription could not be found
	 */
	@Override
	public Subscription findByC_U_C_C(
			long companyId, long userId, long classNameId, long classPK)
		throws NoSuchSubscriptionException {

		return _uniquePersistenceFinderByC_U_C_C.find(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, userId, classNameId, classPK});
	}

	/**
	 * Returns the subscription where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching subscription, or <code>null</code> if a matching subscription could not be found
	 */
	@Override
	public Subscription fetchByC_U_C_C(
		long companyId, long userId, long classNameId, long classPK,
		boolean useFinderCache) {

		return _uniquePersistenceFinderByC_U_C_C.fetch(
			FinderCacheUtil.getFinderCache(),
			new Object[] {companyId, userId, classNameId, classPK},
			useFinderCache);
	}

	/**
	 * Removes the subscription where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the subscription that was removed
	 */
	@Override
	public Subscription removeByC_U_C_C(
			long companyId, long userId, long classNameId, long classPK)
		throws NoSuchSubscriptionException {

		Subscription subscription = findByC_U_C_C(
			companyId, userId, classNameId, classPK);

		return remove(subscription);
	}

	/**
	 * Returns the number of subscriptions where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPK the class pk
	 * @return the number of matching subscriptions
	 */
	@Override
	public int countByC_U_C_C(
		long companyId, long userId, long classNameId, long classPK) {

		return _collectionPersistenceFinderByC_U_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, userId, classNameId, new long[] {classPK}
			});
	}

	/**
	 * Returns the number of subscriptions where companyId = &#63; and userId = &#63; and classNameId = &#63; and classPK = any &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param classNameId the class name ID
	 * @param classPKs the class pks
	 * @return the number of matching subscriptions
	 */
	@Override
	public int countByC_U_C_C(
		long companyId, long userId, long classNameId, long[] classPKs) {

		return _collectionPersistenceFinderByC_U_C_C.count(
			FinderCacheUtil.getFinderCache(),
			new Object[] {
				companyId, userId, classNameId, ArrayUtil.sortedUnique(classPKs)
			});
	}

	public SubscriptionPersistenceImpl() {
		setModelClass(Subscription.class);

		setModelImplClass(SubscriptionImpl.class);
		setModelPKClass(long.class);

		setTable(SubscriptionTable.INSTANCE);
	}

	/**
	 * Creates a new subscription with the primary key. Does not add the subscription to the database.
	 *
	 * @param subscriptionId the primary key for the new subscription
	 * @return the new subscription
	 */
	@Override
	public Subscription create(long subscriptionId) {
		Subscription subscription = new SubscriptionImpl();

		subscription.setNew(true);
		subscription.setPrimaryKey(subscriptionId);

		subscription.setCompanyId(CompanyThreadLocal.getCompanyId());

		return subscription;
	}

	/**
	 * Removes the subscription with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param subscriptionId the primary key of the subscription
	 * @return the subscription that was removed
	 * @throws NoSuchSubscriptionException if a subscription with the primary key could not be found
	 */
	@Override
	public Subscription remove(long subscriptionId)
		throws NoSuchSubscriptionException {

		return remove((Serializable)subscriptionId);
	}

	@Override
	protected Subscription removeImpl(Subscription subscription) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(subscription)) {
				subscription = (Subscription)session.get(
					SubscriptionImpl.class, subscription.getPrimaryKeyObj());
			}

			if (subscription != null) {
				session.delete(subscription);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (subscription != null) {
			clearCache(subscription);
		}

		return subscription;
	}

	@Override
	public Subscription updateImpl(Subscription subscription) {
		boolean isNew = subscription.isNew();

		if (!(subscription instanceof SubscriptionModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(subscription.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					subscription);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in subscription proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Subscription implementation " +
					subscription.getClass());
		}

		SubscriptionModelImpl subscriptionModelImpl =
			(SubscriptionModelImpl)subscription;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (subscription.getCreateDate() == null)) {
			if (serviceContext == null) {
				subscription.setCreateDate(date);
			}
			else {
				subscription.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		if (!subscriptionModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				subscription.setModifiedDate(date);
			}
			else {
				subscription.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(subscription);
			}
			else {
				subscription = (Subscription)session.merge(subscription);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(subscription, false);

		if (isNew) {
			subscription.setNew(false);
		}

		subscription.resetOriginalValues();

		return subscription;
	}

	/**
	 * Returns the subscription with the primary key or throws a <code>NoSuchSubscriptionException</code> if it could not be found.
	 *
	 * @param subscriptionId the primary key of the subscription
	 * @return the subscription
	 * @throws NoSuchSubscriptionException if a subscription with the primary key could not be found
	 */
	@Override
	public Subscription findByPrimaryKey(long subscriptionId)
		throws NoSuchSubscriptionException {

		return findByPrimaryKey((Serializable)subscriptionId);
	}

	/**
	 * Returns the subscription with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param subscriptionId the primary key of the subscription
	 * @return the subscription, or <code>null</code> if a subscription with the primary key could not be found
	 */
	@Override
	public Subscription fetchByPrimaryKey(long subscriptionId) {
		return fetchByPrimaryKey((Serializable)subscriptionId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return EntityCacheUtil.getEntityCache();
	}

	@Override
	protected String getPKDBName() {
		return "subscriptionId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_SUBSCRIPTION;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return SubscriptionModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the subscription persistence.
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
				_SQL_SELECT_SUBSCRIPTION_WHERE, _SQL_COUNT_SUBSCRIPTION_WHERE,
				SubscriptionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"subscription.", "userId", FinderColumn.Type.LONG, "=",
					true, true, Subscription::getUserId));

		_collectionPersistenceFinderByG_U = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByG_U",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"groupId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByG_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "userId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByG_U",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"groupId", "userId"}, false),
			_SQL_SELECT_SUBSCRIPTION_WHERE, _SQL_COUNT_SUBSCRIPTION_WHERE,
			SubscriptionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"subscription.", "groupId", FinderColumn.Type.LONG, "=", true,
				true, Subscription::getGroupId),
			new FinderColumn<>(
				"subscription.", "userId", FinderColumn.Type.LONG, "=", true,
				true, Subscription::getUserId));

		_collectionPersistenceFinderByU_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByU_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"userId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "classNameId"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByU_C",
				new String[] {Long.class.getName(), Long.class.getName()},
				new String[] {"userId", "classNameId"}, false),
			_SQL_SELECT_SUBSCRIPTION_WHERE, _SQL_COUNT_SUBSCRIPTION_WHERE,
			SubscriptionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"subscription.", "userId", FinderColumn.Type.LONG, "=", true,
				true, Subscription::getUserId),
			new FinderColumn<>(
				"subscription.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, Subscription::getClassNameId));

		_collectionPersistenceFinderByC_C_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName()
				},
				new String[] {"companyId", "classNameId", "classPK"}, false),
			_SQL_SELECT_SUBSCRIPTION_WHERE, _SQL_COUNT_SUBSCRIPTION_WHERE,
			SubscriptionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"subscription.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Subscription::getCompanyId),
			new FinderColumn<>(
				"subscription.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, Subscription::getClassNameId),
			new FinderColumn<>(
				"subscription.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, Subscription::getClassPK));

		_collectionPersistenceFinderByC_U_C_C =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName(),
						Integer.class.getName(), Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {
						"companyId", "userId", "classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"companyId", "userId", "classNameId", "classPK"
					},
					true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "countByC_U_C_C",
					new String[] {
						Long.class.getName(), Long.class.getName(),
						Long.class.getName(), Long.class.getName()
					},
					new String[] {
						"companyId", "userId", "classNameId", "classPK"
					},
					false),
				_SQL_SELECT_SUBSCRIPTION_WHERE, _SQL_COUNT_SUBSCRIPTION_WHERE,
				SubscriptionModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "",
				"",
				new FinderColumn<>(
					"subscription.", "companyId", FinderColumn.Type.LONG, "=",
					true, true, Subscription::getCompanyId),
				new FinderColumn<>(
					"subscription.", "userId", FinderColumn.Type.LONG, "=",
					true, true, Subscription::getUserId),
				new FinderColumn<>(
					"subscription.", "classNameId", FinderColumn.Type.LONG, "=",
					true, true, Subscription::getClassNameId),
				new ArrayableFinderColumn<>(
					"subscription.", "classPK", FinderColumn.Type.LONG, "=",
					false, true, true, Subscription::getClassPK));

		_uniquePersistenceFinderByC_U_C_C = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_U_C_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					Long.class.getName(), Long.class.getName()
				},
				new String[] {"companyId", "userId", "classNameId", "classPK"},
				0, 0, false, Subscription::getCompanyId,
				Subscription::getUserId, Subscription::getClassNameId,
				Subscription::getClassPK),
			_SQL_SELECT_SUBSCRIPTION_WHERE, "",
			new FinderColumn<>(
				"subscription.", "companyId", FinderColumn.Type.LONG, "=", true,
				true, Subscription::getCompanyId),
			new FinderColumn<>(
				"subscription.", "userId", FinderColumn.Type.LONG, "=", true,
				true, Subscription::getUserId),
			new FinderColumn<>(
				"subscription.", "classNameId", FinderColumn.Type.LONG, "=",
				true, true, Subscription::getClassNameId),
			new FinderColumn<>(
				"subscription.", "classPK", FinderColumn.Type.LONG, "=", true,
				true, Subscription::getClassPK));

		SubscriptionUtil.setPersistence(this);
	}

	public void destroy() {
		SubscriptionUtil.setPersistence(null);

		EntityCacheUtil.removeCache(SubscriptionImpl.class.getName());
	}

	private static final String _ENTITY_ALIAS_PREFIX =
		SubscriptionModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_SUBSCRIPTION =
		"SELECT subscription FROM Subscription subscription";

	private static final String _SQL_SELECT_SUBSCRIPTION_WHERE =
		"SELECT subscription FROM Subscription subscription WHERE ";

	private static final String _SQL_COUNT_SUBSCRIPTION_WHERE =
		"SELECT COUNT(subscription) FROM Subscription subscription WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Subscription exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		SubscriptionPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return FinderCacheUtil.getFinderCache();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-502711599