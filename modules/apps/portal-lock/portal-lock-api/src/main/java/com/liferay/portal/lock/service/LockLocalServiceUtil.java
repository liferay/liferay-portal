/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lock.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.lock.model.Lock;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for Lock. This utility wraps
 * <code>com.liferay.portal.lock.service.impl.LockLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LockLocalService
 * @generated
 */
public class LockLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.lock.service.impl.LockLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the lock to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LockLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lock the lock
	 * @return the lock that was added
	 */
	public static Lock addLock(Lock lock) {
		return getService().addLock(lock);
	}

	public static void clear() {
		getService().clear();
	}

	/**
	 * Creates a new lock with the primary key. Does not add the lock to the database.
	 *
	 * @param lockId the primary key for the new lock
	 * @return the new lock
	 */
	public static Lock createLock(long lockId) {
		return getService().createLock(lockId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the lock from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LockLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lock the lock
	 * @return the lock that was removed
	 */
	public static Lock deleteLock(Lock lock) {
		return getService().deleteLock(lock);
	}

	/**
	 * Deletes the lock with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LockLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock that was removed
	 * @throws PortalException if a lock with the primary key could not be found
	 */
	public static Lock deleteLock(long lockId) throws PortalException {
		return getService().deleteLock(lockId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static Lock fetchLock(long lockId) {
		return getService().fetchLock(lockId);
	}

	public static Lock fetchLock(String className, long key) {
		return getService().fetchLock(className, key);
	}

	public static Lock fetchLock(String className, String key) {
		return getService().fetchLock(className, key);
	}

	/**
	 * Returns the lock with the matching UUID and company.
	 *
	 * @param uuid the lock's UUID
	 * @param companyId the primary key of the company
	 * @return the matching lock, or <code>null</code> if a matching lock could not be found
	 */
	public static Lock fetchLockByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().fetchLockByUuidAndCompanyId(uuid, companyId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the lock with the primary key.
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock
	 * @throws PortalException if a lock with the primary key could not be found
	 */
	public static Lock getLock(long lockId) throws PortalException {
		return getService().getLock(lockId);
	}

	public static Lock getLock(String className, long key)
		throws PortalException {

		return getService().getLock(className, key);
	}

	public static Lock getLock(String className, String key)
		throws PortalException {

		return getService().getLock(className, key);
	}

	/**
	 * Returns the lock with the matching UUID and company.
	 *
	 * @param uuid the lock's UUID
	 * @param companyId the primary key of the company
	 * @return the matching lock
	 * @throws PortalException if a matching lock could not be found
	 */
	public static Lock getLockByUuidAndCompanyId(String uuid, long companyId)
		throws PortalException {

		return getService().getLockByUuidAndCompanyId(uuid, companyId);
	}

	/**
	 * Returns a range of all the locks.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.lock.model.impl.LockModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of locks
	 * @param end the upper bound of the range of locks (not inclusive)
	 * @return the range of locks
	 */
	public static List<Lock> getLocks(int start, int end) {
		return getService().getLocks(start, end);
	}

	public static List<Lock> getLocks(long companyId, String className) {
		return getService().getLocks(companyId, className);
	}

	/**
	 * Returns the number of locks.
	 *
	 * @return the number of locks
	 */
	public static int getLocksCount() {
		return getService().getLocksCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean hasLock(long userId, String className, long key) {
		return getService().hasLock(userId, className, key);
	}

	public static boolean hasLock(long userId, String className, String key) {
		return getService().hasLock(userId, className, key);
	}

	public static boolean isLocked(String className, long key) {
		return getService().isLocked(className, key);
	}

	public static boolean isLocked(String className, String key) {
		return getService().isLocked(className, key);
	}

	public static Lock lock(
			long userId, String className, long key, String owner,
			boolean inheritable, long expirationTime)
		throws PortalException {

		return getService().lock(
			userId, className, key, owner, inheritable, expirationTime);
	}

	public static Lock lock(
			long userId, String className, long key, String owner,
			boolean inheritable, long expirationTime, boolean renew)
		throws PortalException {

		return getService().lock(
			userId, className, key, owner, inheritable, expirationTime, renew);
	}

	public static Lock lock(
			long userId, String className, String key, String owner,
			boolean inheritable, long expirationTime)
		throws PortalException {

		return getService().lock(
			userId, className, key, owner, inheritable, expirationTime);
	}

	public static Lock lock(
			long userId, String className, String key, String owner,
			boolean inheritable, long expirationTime, boolean renew)
		throws PortalException {

		return getService().lock(
			userId, className, key, owner, inheritable, expirationTime, renew);
	}

	public static Lock lock(String className, String key, String owner) {
		return getService().lock(className, key, owner);
	}

	public static Lock lock(
		String className, String key, String expectedOwner,
		String updatedOwner) {

		return getService().lock(className, key, expectedOwner, updatedOwner);
	}

	public static Lock refresh(String uuid, long companyId, long expirationTime)
		throws PortalException {

		return getService().refresh(uuid, companyId, expirationTime);
	}

	public static void unlock(String className, long key) {
		getService().unlock(className, key);
	}

	public static void unlock(String className, String key) {
		getService().unlock(className, key);
	}

	public static void unlock(String className, String key, String owner) {
		getService().unlock(className, key, owner);
	}

	/**
	 * Updates the lock in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect LockLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param lock the lock
	 * @return the lock that was updated
	 */
	public static Lock updateLock(Lock lock) {
		return getService().updateLock(lock);
	}

	public static LockLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<LockLocalService> _serviceSnapshot =
		new Snapshot<>(LockLocalServiceUtil.class, LockLocalService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-1126021849