/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lock.service.persistence.impl;

import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;
import com.liferay.portal.lock.exception.NoSuchLockException;
import com.liferay.portal.lock.model.Lock;
import com.liferay.portal.lock.model.LockTable;
import com.liferay.portal.lock.model.impl.LockImpl;
import com.liferay.portal.lock.model.impl.LockModelImpl;
import com.liferay.portal.lock.service.persistence.LockPersistence;
import com.liferay.portal.lock.service.persistence.LockUtil;
import com.liferay.portal.lock.service.persistence.impl.constants.LockPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the lock service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = LockPersistence.class)
public class LockPersistenceImpl
	extends BasePersistenceImpl<Lock, NoSuchLockException>
	implements LockPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>LockUtil</code> to access the lock persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		LockImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder<Lock, NoSuchLockException>
		_collectionPersistenceFinderByUuid;

	/**
	 * Returns an ordered range of all the locks where uuid = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LockModelImpl</code>.
	 * </p>
	 *
	 * @param uuid the uuid
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	@Override
	public List<Lock> findByUuid(
		String uuid, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid.find(
			finderCache, new Object[] {uuid}, start, end, orderByComparator,
			useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	@Override
	public Lock findByUuid_First(
			String uuid, OrderByComparator<Lock> orderByComparator)
		throws NoSuchLockException {

		return _collectionPersistenceFinderByUuid.findFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	@Override
	public Lock fetchByUuid_First(
		String uuid, OrderByComparator<Lock> orderByComparator) {

		return _collectionPersistenceFinderByUuid.fetchFirst(
			finderCache, new Object[] {uuid}, orderByComparator);
	}

	/**
	 * Removes all the locks where uuid = &#63; from the database.
	 *
	 * @param uuid the uuid
	 */
	@Override
	public void removeByUuid(String uuid) {
		_collectionPersistenceFinderByUuid.remove(
			finderCache, new Object[] {uuid});
	}

	/**
	 * Returns the number of locks where uuid = &#63;.
	 *
	 * @param uuid the uuid
	 * @return the number of matching locks
	 */
	@Override
	public int countByUuid(String uuid) {
		return _collectionPersistenceFinderByUuid.count(
			finderCache, new Object[] {uuid});
	}

	private CollectionPersistenceFinder<Lock, NoSuchLockException>
		_collectionPersistenceFinderByUuid_C;

	/**
	 * Returns an ordered range of all the locks where uuid = &#63; and companyId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LockModelImpl</code>.
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
	@Override
	public List<Lock> findByUuid_C(
		String uuid, long companyId, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByUuid_C.find(
			finderCache, new Object[] {uuid, companyId}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public Lock findByUuid_C_First(
			String uuid, long companyId,
			OrderByComparator<Lock> orderByComparator)
		throws NoSuchLockException {

		return _collectionPersistenceFinderByUuid_C.findFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	@Override
	public Lock fetchByUuid_C_First(
		String uuid, long companyId,
		OrderByComparator<Lock> orderByComparator) {

		return _collectionPersistenceFinderByUuid_C.fetchFirst(
			finderCache, new Object[] {uuid, companyId}, orderByComparator);
	}

	/**
	 * Removes all the locks where uuid = &#63; and companyId = &#63; from the database.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 */
	@Override
	public void removeByUuid_C(String uuid, long companyId) {
		_collectionPersistenceFinderByUuid_C.remove(
			finderCache, new Object[] {uuid, companyId});
	}

	/**
	 * Returns the number of locks where uuid = &#63; and companyId = &#63;.
	 *
	 * @param uuid the uuid
	 * @param companyId the company ID
	 * @return the number of matching locks
	 */
	@Override
	public int countByUuid_C(String uuid, long companyId) {
		return _collectionPersistenceFinderByUuid_C.count(
			finderCache, new Object[] {uuid, companyId});
	}

	private CollectionPersistenceFinder<Lock, NoSuchLockException>
		_collectionPersistenceFinderByClassName;

	/**
	 * Returns an ordered range of all the locks where className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LockModelImpl</code>.
	 * </p>
	 *
	 * @param className the class name
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	@Override
	public List<Lock> findByClassName(
		String className, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByClassName.find(
			finderCache, new Object[] {className}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where className = &#63;.
	 *
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	@Override
	public Lock findByClassName_First(
			String className, OrderByComparator<Lock> orderByComparator)
		throws NoSuchLockException {

		return _collectionPersistenceFinderByClassName.findFirst(
			finderCache, new Object[] {className}, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where className = &#63;.
	 *
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	@Override
	public Lock fetchByClassName_First(
		String className, OrderByComparator<Lock> orderByComparator) {

		return _collectionPersistenceFinderByClassName.fetchFirst(
			finderCache, new Object[] {className}, orderByComparator);
	}

	/**
	 * Removes all the locks where className = &#63; from the database.
	 *
	 * @param className the class name
	 */
	@Override
	public void removeByClassName(String className) {
		_collectionPersistenceFinderByClassName.remove(
			finderCache, new Object[] {className});
	}

	/**
	 * Returns the number of locks where className = &#63;.
	 *
	 * @param className the class name
	 * @return the number of matching locks
	 */
	@Override
	public int countByClassName(String className) {
		return _collectionPersistenceFinderByClassName.count(
			finderCache, new Object[] {className});
	}

	private CollectionPersistenceFinder<Lock, NoSuchLockException>
		_collectionPersistenceFinderByLtExpirationDate;

	/**
	 * Returns all the locks where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the matching locks
	 */
	@Override
	public List<Lock> findByLtExpirationDate(Date expirationDate) {
		return findByLtExpirationDate(
			expirationDate, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	/**
	 * Returns a range of all the locks where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LockModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @return the range of matching locks
	 */
	@Override
	public List<Lock> findByLtExpirationDate(
		Date expirationDate, int start, int end) {

		return findByLtExpirationDate(expirationDate, start, end, null);
	}

	/**
	 * Returns an ordered range of all the locks where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LockModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching locks
	 */
	@Override
	public List<Lock> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<Lock> orderByComparator) {

		return findByLtExpirationDate(
			expirationDate, start, end, orderByComparator, true);
	}

	/**
	 * Returns an ordered range of all the locks where expirationDate &lt; &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LockModelImpl</code>.
	 * </p>
	 *
	 * @param expirationDate the expiration date
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	@Override
	public List<Lock> findByLtExpirationDate(
		Date expirationDate, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByLtExpirationDate.find(
			finderCache, new Object[] {expirationDate}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	@Override
	public Lock findByLtExpirationDate_First(
			Date expirationDate, OrderByComparator<Lock> orderByComparator)
		throws NoSuchLockException {

		return _collectionPersistenceFinderByLtExpirationDate.findFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	@Override
	public Lock fetchByLtExpirationDate_First(
		Date expirationDate, OrderByComparator<Lock> orderByComparator) {

		return _collectionPersistenceFinderByLtExpirationDate.fetchFirst(
			finderCache, new Object[] {expirationDate}, orderByComparator);
	}

	/**
	 * Removes all the locks where expirationDate &lt; &#63; from the database.
	 *
	 * @param expirationDate the expiration date
	 */
	@Override
	public void removeByLtExpirationDate(Date expirationDate) {
		_collectionPersistenceFinderByLtExpirationDate.remove(
			finderCache, new Object[] {expirationDate});
	}

	/**
	 * Returns the number of locks where expirationDate &lt; &#63;.
	 *
	 * @param expirationDate the expiration date
	 * @return the number of matching locks
	 */
	@Override
	public int countByLtExpirationDate(Date expirationDate) {
		return _collectionPersistenceFinderByLtExpirationDate.count(
			finderCache, new Object[] {expirationDate});
	}

	private CollectionPersistenceFinder<Lock, NoSuchLockException>
		_collectionPersistenceFinderByC_C;

	/**
	 * Returns an ordered range of all the locks where companyId = &#63; and className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LockModelImpl</code>.
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
	@Override
	public List<Lock> findByC_C(
		long companyId, String className, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_C.find(
			finderCache, new Object[] {companyId, className}, start, end,
			orderByComparator, useFinderCache);
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
	@Override
	public Lock findByC_C_First(
			long companyId, String className,
			OrderByComparator<Lock> orderByComparator)
		throws NoSuchLockException {

		return _collectionPersistenceFinderByC_C.findFirst(
			finderCache, new Object[] {companyId, className},
			orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	@Override
	public Lock fetchByC_C_First(
		long companyId, String className,
		OrderByComparator<Lock> orderByComparator) {

		return _collectionPersistenceFinderByC_C.fetchFirst(
			finderCache, new Object[] {companyId, className},
			orderByComparator);
	}

	/**
	 * Removes all the locks where companyId = &#63; and className = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 */
	@Override
	public void removeByC_C(long companyId, String className) {
		_collectionPersistenceFinderByC_C.remove(
			finderCache, new Object[] {companyId, className});
	}

	/**
	 * Returns the number of locks where companyId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param className the class name
	 * @return the number of matching locks
	 */
	@Override
	public int countByC_C(long companyId, String className) {
		return _collectionPersistenceFinderByC_C.count(
			finderCache, new Object[] {companyId, className});
	}

	private UniquePersistenceFinder<Lock, NoSuchLockException>
		_uniquePersistenceFinderByC_K;

	/**
	 * Returns the lock where className = &#63; and key = &#63; or throws a <code>NoSuchLockException</code> if it could not be found.
	 *
	 * @param className the class name
	 * @param key the key
	 * @return the matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	@Override
	public Lock findByC_K(String className, String key)
		throws NoSuchLockException {

		return _uniquePersistenceFinderByC_K.find(
			finderCache, new Object[] {className, key});
	}

	/**
	 * Returns the lock where className = &#63; and key = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param className the class name
	 * @param key the key
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching lock, or <code>null</code> if a matching lock could not be found
	 */
	@Override
	public Lock fetchByC_K(
		String className, String key, boolean useFinderCache) {

		return _uniquePersistenceFinderByC_K.fetch(
			finderCache, new Object[] {className, key}, useFinderCache);
	}

	/**
	 * Removes the lock where className = &#63; and key = &#63; from the database.
	 *
	 * @param className the class name
	 * @param key the key
	 * @return the lock that was removed
	 */
	@Override
	public Lock removeByC_K(String className, String key)
		throws NoSuchLockException {

		Lock lock = findByC_K(className, key);

		return remove(lock);
	}

	/**
	 * Returns the number of locks where className = &#63; and key = &#63;.
	 *
	 * @param className the class name
	 * @param key the key
	 * @return the number of matching locks
	 */
	@Override
	public int countByC_K(String className, String key) {
		return _uniquePersistenceFinderByC_K.count(
			finderCache, new Object[] {className, key});
	}

	private CollectionPersistenceFinder<Lock, NoSuchLockException>
		_collectionPersistenceFinderByC_U_C;

	/**
	 * Returns an ordered range of all the locks where companyId = &#63; and userId = &#63; and className = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>LockModelImpl</code>.
	 * </p>
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param className the class name
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching locks
	 */
	@Override
	public List<Lock> findByC_U_C(
		long companyId, long userId, String className, int start, int end,
		OrderByComparator<Lock> orderByComparator, boolean useFinderCache) {

		return _collectionPersistenceFinderByC_U_C.find(
			finderCache, new Object[] {companyId, userId, className}, start,
			end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first lock in the ordered set where companyId = &#63; and userId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock
	 * @throws NoSuchLockException if a matching lock could not be found
	 */
	@Override
	public Lock findByC_U_C_First(
			long companyId, long userId, String className,
			OrderByComparator<Lock> orderByComparator)
		throws NoSuchLockException {

		return _collectionPersistenceFinderByC_U_C.findFirst(
			finderCache, new Object[] {companyId, userId, className},
			orderByComparator);
	}

	/**
	 * Returns the first lock in the ordered set where companyId = &#63; and userId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param className the class name
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching lock, or <code>null</code> if a matching lock could not be found
	 */
	@Override
	public Lock fetchByC_U_C_First(
		long companyId, long userId, String className,
		OrderByComparator<Lock> orderByComparator) {

		return _collectionPersistenceFinderByC_U_C.fetchFirst(
			finderCache, new Object[] {companyId, userId, className},
			orderByComparator);
	}

	/**
	 * Removes all the locks where companyId = &#63; and userId = &#63; and className = &#63; from the database.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param className the class name
	 */
	@Override
	public void removeByC_U_C(long companyId, long userId, String className) {
		_collectionPersistenceFinderByC_U_C.remove(
			finderCache, new Object[] {companyId, userId, className});
	}

	/**
	 * Returns the number of locks where companyId = &#63; and userId = &#63; and className = &#63;.
	 *
	 * @param companyId the company ID
	 * @param userId the user ID
	 * @param className the class name
	 * @return the number of matching locks
	 */
	@Override
	public int countByC_U_C(long companyId, long userId, String className) {
		return _collectionPersistenceFinderByC_U_C.count(
			finderCache, new Object[] {companyId, userId, className});
	}

	public LockPersistenceImpl() {
		Map<String, String> dbColumnNames = new HashMap<String, String>();

		dbColumnNames.put("uuid", "uuid_");
		dbColumnNames.put("key", "key_");

		setDBColumnNames(dbColumnNames);

		setModelClass(Lock.class);

		setModelImplClass(LockImpl.class);
		setModelPKClass(long.class);

		setTable(LockTable.INSTANCE);
	}

	/**
	 * Creates a new lock with the primary key. Does not add the lock to the database.
	 *
	 * @param lockId the primary key for the new lock
	 * @return the new lock
	 */
	@Override
	public Lock create(long lockId) {
		Lock lock = new LockImpl();

		lock.setNew(true);
		lock.setPrimaryKey(lockId);

		String uuid = PortalUUIDUtil.generate();

		lock.setUuid(uuid);

		lock.setCompanyId(CompanyThreadLocal.getCompanyId());

		return lock;
	}

	/**
	 * Removes the lock with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock that was removed
	 * @throws NoSuchLockException if a lock with the primary key could not be found
	 */
	@Override
	public Lock remove(long lockId) throws NoSuchLockException {
		return remove((Serializable)lockId);
	}

	@Override
	protected Lock removeImpl(Lock lock) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(lock)) {
				lock = (Lock)session.get(
					LockImpl.class, lock.getPrimaryKeyObj());
			}

			if (lock != null) {
				session.delete(lock);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (lock != null) {
			clearCache(lock);
		}

		return lock;
	}

	@Override
	public Lock updateImpl(Lock lock) {
		boolean isNew = lock.isNew();

		if (!(lock instanceof LockModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(lock.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(lock);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in lock proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom Lock implementation " +
					lock.getClass());
		}

		LockModelImpl lockModelImpl = (LockModelImpl)lock;

		if (Validator.isNull(lock.getUuid())) {
			String uuid = PortalUUIDUtil.generate();

			lock.setUuid(uuid);
		}

		if (isNew && (lock.getCreateDate() == null)) {
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			Date date = new Date();

			if (serviceContext == null) {
				lock.setCreateDate(date);
			}
			else {
				lock.setCreateDate(serviceContext.getCreateDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (isNew) {
				session.save(lock);
			}
			else {
				lock = (Lock)session.merge(lock);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(lock, false);

		if (isNew) {
			lock.setNew(false);
		}

		lock.resetOriginalValues();

		return lock;
	}

	/**
	 * Returns the lock with the primary key or throws a <code>NoSuchLockException</code> if it could not be found.
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock
	 * @throws NoSuchLockException if a lock with the primary key could not be found
	 */
	@Override
	public Lock findByPrimaryKey(long lockId) throws NoSuchLockException {
		return findByPrimaryKey((Serializable)lockId);
	}

	/**
	 * Returns the lock with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock, or <code>null</code> if a lock with the primary key could not be found
	 */
	@Override
	public Lock fetchByPrimaryKey(long lockId) {
		return fetchByPrimaryKey((Serializable)lockId);
	}

	@Override
	public Set<String> getBadColumnNames() {
		return _badColumnNames;
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "lockId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_LOCK_;
	}

	@Override
	protected Map<String, Integer> getTableColumnsMap() {
		return LockModelImpl.TABLE_COLUMNS_MAP;
	}

	/**
	 * Initializes the lock persistence.
	 */
	@Activate
	public void activate() {
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
			_SQL_SELECT_LOCK__WHERE, _SQL_COUNT_LOCK__WHERE,
			LockModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"lock_.", "uuid", "uuid_", FinderColumn.Type.STRING, "=", true,
				true, Lock::getUuid));

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
				_SQL_SELECT_LOCK__WHERE, _SQL_COUNT_LOCK__WHERE,
				LockModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lock_.", "uuid", "uuid_", FinderColumn.Type.STRING, "=",
					true, true, Lock::getUuid),
				new FinderColumn<>(
					"lock_.", "companyId", FinderColumn.Type.LONG, "=", true,
					true, Lock::getCompanyId));

		_collectionPersistenceFinderByClassName =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByClassName",
					new String[] {
						String.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"className"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByClassName", new String[] {String.class.getName()},
					new String[] {"className"}, 0, 1, true, null),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByClassName", new String[] {String.class.getName()},
					new String[] {"className"}, 0, 1, false, null),
				_SQL_SELECT_LOCK__WHERE, _SQL_COUNT_LOCK__WHERE,
				LockModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lock_.", "className", FinderColumn.Type.STRING, "=", true,
					true, Lock::getClassName));

		_collectionPersistenceFinderByLtExpirationDate =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByLtExpirationDate",
					new String[] {
						Date.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"expirationDate"}, true),
				null,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"countByLtExpirationDate",
					new String[] {Date.class.getName()},
					new String[] {"expirationDate"}, false),
				_SQL_SELECT_LOCK__WHERE, _SQL_COUNT_LOCK__WHERE,
				LockModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
				new FinderColumn<>(
					"lock_.", "expirationDate", FinderColumn.Type.DATE, "<",
					true, true, Lock::getExpirationDate));

		_collectionPersistenceFinderByC_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_C",
				new String[] {
					Long.class.getName(), String.class.getName(),
					Integer.class.getName(), Integer.class.getName(),
					OrderByComparator.class.getName()
				},
				new String[] {"companyId", "className"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_C",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "className"}, 0, 2, true, null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_C",
				new String[] {Long.class.getName(), String.class.getName()},
				new String[] {"companyId", "className"}, 0, 2, false, null),
			_SQL_SELECT_LOCK__WHERE, _SQL_COUNT_LOCK__WHERE,
			LockModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"lock_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Lock::getCompanyId),
			new FinderColumn<>(
				"lock_.", "className", FinderColumn.Type.STRING, "=", true,
				true, Lock::getClassName));

		_uniquePersistenceFinderByC_K = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchByC_K",
				new String[] {String.class.getName(), String.class.getName()},
				new String[] {"className", "key_"}, 0, 3, false,
				convertNullFunction(Lock::getClassName),
				convertNullFunction(Lock::getKey)),
			_SQL_SELECT_LOCK__WHERE, "",
			new FinderColumn<>(
				"lock_.", "className", FinderColumn.Type.STRING, "=", true,
				true, Lock::getClassName),
			new FinderColumn<>(
				"lock_.", "key", "key_", FinderColumn.Type.STRING, "=", true,
				true, Lock::getKey));

		_collectionPersistenceFinderByC_U_C = new CollectionPersistenceFinder<>(
			this,
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findByC_U_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName(), Integer.class.getName(),
					Integer.class.getName(), OrderByComparator.class.getName()
				},
				new String[] {"companyId", "userId", "className"}, true),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findByC_U_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "userId", "className"}, 0, 4, true,
				null),
			new FinderPath(
				FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByC_U_C",
				new String[] {
					Long.class.getName(), Long.class.getName(),
					String.class.getName()
				},
				new String[] {"companyId", "userId", "className"}, 0, 4, false,
				null),
			_SQL_SELECT_LOCK__WHERE, _SQL_COUNT_LOCK__WHERE,
			LockModelImpl.ORDER_BY_JPQL, _ENTITY_ALIAS_PREFIX, "", "",
			new FinderColumn<>(
				"lock_.", "companyId", FinderColumn.Type.LONG, "=", true, true,
				Lock::getCompanyId),
			new FinderColumn<>(
				"lock_.", "userId", FinderColumn.Type.LONG, "=", true, true,
				Lock::getUserId),
			new FinderColumn<>(
				"lock_.", "className", FinderColumn.Type.STRING, "=", true,
				true, Lock::getClassName));

		LockUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		LockUtil.setPersistence(null);

		entityCache.removeCache(LockImpl.class.getName());
	}

	@Override
	@Reference(
		target = LockPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = LockPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = LockPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		LockModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_LOCK_ =
		"SELECT lock_ FROM Lock lock_";

	private static final String _SQL_SELECT_LOCK__WHERE =
		"SELECT lock_ FROM Lock lock_ WHERE ";

	private static final String _SQL_COUNT_LOCK__WHERE =
		"SELECT COUNT(lock_) FROM Lock lock_ WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No Lock exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		LockPersistenceImpl.class);

	private static final Set<String> _badColumnNames = SetUtil.fromArray(
		new String[] {"uuid", "key"});

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1621144101