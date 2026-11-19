/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lock.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.lock.model.Lock;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the lock service. This utility wraps <code>com.liferay.portal.lock.service.persistence.impl.LockPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LockPersistence
 * @generated
 */
public class LockUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(List<Lock> locks) {
		getPersistence().cacheResult(locks);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(Lock lock) {
		getPersistence().cacheResult(lock);
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
	public static void clearCache(Lock lock) {
		getPersistence().clearCache(lock);
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
	public static Map<Serializable, Lock> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<Lock> findWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<Lock> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<Lock> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<Lock> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static Lock update(Lock lock) {
		return getPersistence().update(lock);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static Lock update(Lock lock, ServiceContext serviceContext) {
		return getPersistence().update(lock, serviceContext);
	}

	/**
	 * Returns an ordered range of all the locks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid(
			uuid, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	public static Lock findByUuid_First(
			String uuid, OrderByComparator<Lock> orderByComparator)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().findByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	public static Lock fetchByUuid_First(
		String uuid, OrderByComparator<Lock> orderByComparator) {

		return getPersistence().fetchByUuid_First(uuid, orderByComparator);
	}

	/**
	 * Removes all the locks where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	public static void removeByUuid(String uuid) {
		getPersistence().removeByUuid(uuid);
	}

	/**
	 * Returns the number of locks where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching locks
	 */
	public static int countByUuid(String uuid) {
		return getPersistence().countByUuid(uuid);
	}

	/**
	 * Returns an ordered range of all the locks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	public static Lock findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Lock> orderByComparator)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().findByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	public static Lock fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Lock> orderByComparator) {

		return getPersistence().fetchByUuid_C_First(
			uuid, companyId, orderByComparator);
	}

	/**
	 * Removes all the locks where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	public static void removeByUuid_C(String uuid, long companyId) {
		getPersistence().removeByUuid_C(uuid, companyId);
	}

	/**
	 * Returns the number of locks where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching locks
	 */
	public static int countByUuid_C(String uuid, long companyId) {
		return getPersistence().countByUuid_C(uuid, companyId);
	}

	/**
	 * Returns an ordered range of all the locks where className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param className the class name
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByClassName(
		String className, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByClassName(
			className, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where className = &#63;.
	 *
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	public static Lock findByClassName_First(
			String className, OrderByComparator<Lock> orderByComparator)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().findByClassName_First(
			className, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where className = &#63;.
	 *
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	public static Lock fetchByClassName_First(
		String className, OrderByComparator<Lock> orderByComparator) {

		return getPersistence().fetchByClassName_First(
			className, orderByComparator);
	}

	/**
	 * Removes all the locks where className = &#63; from the database.
	 *
	 * @param className the class name
	 */
	public static void removeByClassName(String className) {
		getPersistence().removeByClassName(className);
	}

	/**
	 * Returns the number of locks where className = &#63;.
	 *
	 * @param className the class name
	 * @return the number of matching locks
	 */
	public static int countByClassName(String className) {
		return getPersistence().countByClassName(className);
	}

	/**
	 * Returns all the locks where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching locks
	 */
	public static List<Lock> findByLtExpirationDate(Date expirationDate) {
		return getPersistence().findByLtExpirationDate(expirationDate);
	}

	/**
	 * Returns a range of all the locks where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @return the range of matching locks
	 */
	public static List<Lock> findByLtExpirationDate(
		Date expirationDate, int start, int end) {

		return getPersistence().findByLtExpirationDate(
			expirationDate, start, end);
	}

	/**
	 * Returns an ordered range of all the locks where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<Lock> orderByComparator) {

		return getPersistence().findByLtExpirationDate(
			expirationDate, start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the locks where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByLtExpirationDate(
			expirationDate, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	public static Lock findByLtExpirationDate_First(
			Date expirationDate, OrderByComparator<Lock> orderByComparator)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().findByLtExpirationDate_First(
			expirationDate, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	public static Lock fetchByLtExpirationDate_First(
		Date expirationDate, OrderByComparator<Lock> orderByComparator) {

		return getPersistence().fetchByLtExpirationDate_First(
			expirationDate, orderByComparator);
	}

	/**
	 * Removes all the locks where expirationDate &lt; &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	public static void removeByLtExpirationDate(Date expirationDate) {
		getPersistence().removeByLtExpirationDate(expirationDate);
	}

	/**
	 * Returns the number of locks where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching locks
	 */
	public static int countByLtExpirationDate(Date expirationDate) {
		return getPersistence().countByLtExpirationDate(expirationDate);
	}

	/**
	 * Returns an ordered range of all the locks where companyId = &#63; and className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByC_C(
		long companyId, String className, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return getPersistence().findByC_C(
			companyId, className, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	public static Lock findByC_C_First(
			long companyId, String className,
			OrderByComparator<Lock> orderByComparator)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().findByC_C_First(
			companyId, className, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	public static Lock fetchByC_C_First(
		long companyId, String className,
		OrderByComparator<Lock> orderByComparator) {

		return getPersistence().fetchByC_C_First(
			companyId, className, orderByComparator);
	}

	/**
	 * Removes all the locks where companyId = &#63; and className = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 */
	public static void removeByC_C(long companyId, String className) {
		getPersistence().removeByC_C(companyId, className);
	}

	/**
	 * Returns the number of locks where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the number of matching locks
	 */
	public static int countByC_C(long companyId, String className) {
		return getPersistence().countByC_C(companyId, className);
	}

	/**
	 * Returns the lock where className = &#63; and key = &#63; or throws a <code>NoSuchLockException</code> if it could not be found.
	 *
	 * @param className the class name
	 * @param key the key
	 * @return the matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	public static Lock findByC_K(String className, String key)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().findByC_K(className, key);
	}

	/**
	 * Returns the lock where className = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param className the class name
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lock, or <code>null</code> if a matching lock could not be found
	 */
	public static Lock fetchByC_K(
		String className, String key, boolean useFinderCache) {

		return getPersistence().fetchByC_K(className, key, useFinderCache);
	}

	/**
	 * Removes the lock where className = &#63; and key = &#63; from the database.
	 *
	 * @param className the class name
	 * @param key the key
	 * @return the lock that was removed
	 */
	public static Lock removeByC_K(String className, String key)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().removeByC_K(className, key);
	}

	/**
	 * Returns the number of locks where className = &#63; and key = &#63;.
	 *
	 * @param className the class name
	 * @param key the key
	 * @return the number of matching locks
	 */
	public static int countByC_K(String className, String key) {
		return getPersistence().countByC_K(className, key);
	}

	/**
	 * Creates a new lock with the primary key. Does not add the lock to the database.
	 *
	 * @param lockId the primary key for the new lock
	 * @return the new lock
	 */
	public static Lock create(long lockId) {
		return getPersistence().create(lockId);
	}

	/**
	 * Removes the lock with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock that was removed
	 * @throws NoSuchLockException if a lock with the primary key could not be found
	 */
	public static Lock remove(long lockId)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().remove(lockId);
	}

	public static Lock updateImpl(Lock lock) {
		return getPersistence().updateImpl(lock);
	}

	/**
	 * Returns the lock with the primary key or throws a <code>NoSuchLockException</code> if it could not be found.
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock
	 * @throws NoSuchLockException if a lock with the primary key could not be found
	 */
	public static Lock findByPrimaryKey(long lockId)
		throws com.liferay.portal.lock.exception.NoSuchLockException {

		return getPersistence().findByPrimaryKey(lockId);
	}

	/**
	 * Returns the lock with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock, or <code>null</code> if a lock with the primary key could not be found
	 */
	public static Lock fetchByPrimaryKey(long lockId) {
		return getPersistence().fetchByPrimaryKey(lockId);
	}

	/**
	 * Returns the lock where className = &#63; and key = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param className the class name
	 * @param key the key
	 * @return the matching lock, or <code>null</code> if a matching lock could not be found
	 */
	public static Lock fetchByC_K(String className, String key) {
		return getPersistence().fetchByC_K(className, key);
	}

	/**
	 * Returns all the locks where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the matching locks
	 */
	public static List<Lock> findByUuid(String uuid) {
		return getPersistence().findByUuid(uuid);
	}

	/**
	 * Returns a range of all the locks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @return the range of matching locks
	 */
	public static List<Lock> findByUuid(String uuid, int start, int end) {
		return getPersistence().findByUuid(uuid, start, end);
	}

	/**
	 * Returns an ordered range of all the locks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Lock> orderByComparator) {

		return getPersistence().findByUuid(uuid, start, end, orderByComparator);
	}

	/**
	 * Returns all the locks where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the matching locks
	 */
	public static List<Lock> findByUuid_C(String uuid, long companyId) {
		return getPersistence().findByUuid_C(uuid, companyId);
	}

	/**
	 * Returns a range of all the locks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @return the range of matching locks
	 */
	public static List<Lock> findByUuid_C(
		String uuid, long companyId, int start, int end) {

		return getPersistence().findByUuid_C(uuid, companyId, start, end);
	}

	/**
	 * Returns an ordered range of all the locks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Lock> orderByComparator) {

		return getPersistence().findByUuid_C(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns all the locks where className = &#63;.
	 *
	 * @param className the class name
	 * @return the matching locks
	 */
	public static List<Lock> findByClassName(String className) {
		return getPersistence().findByClassName(className);
	}

	/**
	 * Returns a range of all the locks where className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param className the class name
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @return the range of matching locks
	 */
	public static List<Lock> findByClassName(
		String className, int start, int end) {

		return getPersistence().findByClassName(className, start, end);
	}

	/**
	 * Returns an ordered range of all the locks where className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param className the class name
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByClassName(
		String className, int start, int end,
		OrderByComparator<Lock> orderByComparator) {

		return getPersistence().findByClassName(
			className, start, end, orderByComparator);
	}

	/**
	 * Returns all the locks where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the matching locks
	 */
	public static List<Lock> findByC_C(long companyId, String className) {
		return getPersistence().findByC_C(companyId, className);
	}

	/**
	 * Returns a range of all the locks where companyId = &#63; and className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @return the range of matching locks
	 */
	public static List<Lock> findByC_C(
		long companyId, String className, int start, int end) {

		return getPersistence().findByC_C(companyId, className, start, end);
	}

	/**
	 * Returns an ordered range of all the locks where companyId = &#63; and className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching locks
	 */
	public static List<Lock> findByC_C(
		long companyId, String className, int start, int end,
		OrderByComparator<Lock> orderByComparator) {

		return getPersistence().findByC_C(
			companyId, className, start, end, orderByComparator);
	}

	public static LockPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(LockPersistence persistence) {
		_persistence = persistence;
	}

	private static volatile LockPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:-934250034