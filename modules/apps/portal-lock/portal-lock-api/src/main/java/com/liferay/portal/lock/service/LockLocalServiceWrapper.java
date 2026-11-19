/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lock.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

/**
 * Provides a wrapper for {@link LockLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see LockLocalService
 * @generated
 */
public class LockLocalServiceWrapper
	implements LockLocalService, ServiceWrapper<LockLocalService> {

	public LockLocalServiceWrapper() {
		this(null);
	}

	public LockLocalServiceWrapper(LockLocalService lockLocalService) {
		_lockLocalService = lockLocalService;
	}

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
	@Override
	public com.liferay.portal.lock.model.Lock addLock(
		com.liferay.portal.lock.model.Lock lock) {

		return _lockLocalService.addLock(lock);
	}

	@Override
	public void clear() {
		_lockLocalService.clear();
	}

	/**
	 * Creates a new lock with the primary key. Does not add the lock to the database.
	 *
	 * @param lockId the primary key for the new lock
	 * @return the new lock
	 */
	@Override
	public com.liferay.portal.lock.model.Lock createLock(long lockId) {
		return _lockLocalService.createLock(lockId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.createPersistedModel(primaryKeyObj);
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
	@Override
	public com.liferay.portal.lock.model.Lock deleteLock(
		com.liferay.portal.lock.model.Lock lock) {

		return _lockLocalService.deleteLock(lock);
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
	@Override
	public com.liferay.portal.lock.model.Lock deleteLock(long lockId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.deleteLock(lockId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.deletePersistedModel(persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _lockLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _lockLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _lockLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _lockLocalService.dynamicQuery(dynamicQuery);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _lockLocalService.dynamicQuery(dynamicQuery, start, end);
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
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _lockLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _lockLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _lockLocalService.dynamicQueryCount(dynamicQuery, projection);
	}

	@Override
	public com.liferay.portal.lock.model.Lock fetchLock(long lockId) {
		return _lockLocalService.fetchLock(lockId);
	}

	@Override
	public com.liferay.portal.lock.model.Lock fetchLock(
		String className, long key) {

		return _lockLocalService.fetchLock(className, key);
	}

	@Override
	public com.liferay.portal.lock.model.Lock fetchLock(
		String className, String key) {

		return _lockLocalService.fetchLock(className, key);
	}

	/**
	 * Returns the lock with the matching UUID and company.
	 *
	 * @param uuid the lock's UUID
	 * @param companyId the primary key of the company
	 * @return the matching lock, or <code>null</code> if a matching lock could not be found
	 */
	@Override
	public com.liferay.portal.lock.model.Lock fetchLockByUuidAndCompanyId(
		String uuid, long companyId) {

		return _lockLocalService.fetchLockByUuidAndCompanyId(uuid, companyId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _lockLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _lockLocalService.getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the lock with the primary key.
	 *
	 * @param lockId the primary key of the lock
	 * @return the lock
	 * @throws PortalException if a lock with the primary key could not be found
	 */
	@Override
	public com.liferay.portal.lock.model.Lock getLock(long lockId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.getLock(lockId);
	}

	@Override
	public com.liferay.portal.lock.model.Lock getLock(
			String className, long key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.getLock(className, key);
	}

	@Override
	public com.liferay.portal.lock.model.Lock getLock(
			String className, String key)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.getLock(className, key);
	}

	/**
	 * Returns the lock with the matching UUID and company.
	 *
	 * @param uuid the lock's UUID
	 * @param companyId the primary key of the company
	 * @return the matching lock
	 * @throws PortalException if a matching lock could not be found
	 */
	@Override
	public com.liferay.portal.lock.model.Lock getLockByUuidAndCompanyId(
			String uuid, long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.getLockByUuidAndCompanyId(uuid, companyId);
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
	@Override
	public java.util.List<com.liferay.portal.lock.model.Lock> getLocks(
		int start, int end) {

		return _lockLocalService.getLocks(start, end);
	}

	@Override
	public java.util.List<com.liferay.portal.lock.model.Lock> getLocks(
		long companyId, String className) {

		return _lockLocalService.getLocks(companyId, className);
	}

	/**
	 * Returns the number of locks.
	 *
	 * @return the number of locks
	 */
	@Override
	public int getLocksCount() {
		return _lockLocalService.getLocksCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _lockLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public boolean hasLock(long userId, String className, long key) {
		return _lockLocalService.hasLock(userId, className, key);
	}

	@Override
	public boolean hasLock(long userId, String className, String key) {
		return _lockLocalService.hasLock(userId, className, key);
	}

	@Override
	public boolean isLocked(String className, long key) {
		return _lockLocalService.isLocked(className, key);
	}

	@Override
	public boolean isLocked(String className, String key) {
		return _lockLocalService.isLocked(className, key);
	}

	@Override
	public com.liferay.portal.lock.model.Lock lock(
			long userId, String className, long key, String owner,
			boolean inheritable, long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.lock(
			userId, className, key, owner, inheritable, expirationTime);
	}

	@Override
	public com.liferay.portal.lock.model.Lock lock(
			long userId, String className, long key, String owner,
			boolean inheritable, long expirationTime, boolean renew)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.lock(
			userId, className, key, owner, inheritable, expirationTime, renew);
	}

	@Override
	public com.liferay.portal.lock.model.Lock lock(
			long userId, String className, String key, String owner,
			boolean inheritable, long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.lock(
			userId, className, key, owner, inheritable, expirationTime);
	}

	@Override
	public com.liferay.portal.lock.model.Lock lock(
			long userId, String className, String key, String owner,
			boolean inheritable, long expirationTime, boolean renew)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.lock(
			userId, className, key, owner, inheritable, expirationTime, renew);
	}

	@Override
	public com.liferay.portal.lock.model.Lock lock(
		String className, String key, String owner) {

		return _lockLocalService.lock(className, key, owner);
	}

	@Override
	public com.liferay.portal.lock.model.Lock lock(
		String className, String key, String expectedOwner,
		String updatedOwner) {

		return _lockLocalService.lock(
			className, key, expectedOwner, updatedOwner);
	}

	@Override
	public com.liferay.portal.lock.model.Lock refresh(
			String uuid, long companyId, long expirationTime)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _lockLocalService.refresh(uuid, companyId, expirationTime);
	}

	@Override
	public void unlock(String className, long key) {
		_lockLocalService.unlock(className, key);
	}

	@Override
	public void unlock(String className, String key) {
		_lockLocalService.unlock(className, key);
	}

	@Override
	public void unlock(String className, String key, String owner) {
		_lockLocalService.unlock(className, key, owner);
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
	@Override
	public com.liferay.portal.lock.model.Lock updateLock(
		com.liferay.portal.lock.model.Lock lock) {

		return _lockLocalService.updateLock(lock);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _lockLocalService.getBasePersistence();
	}

	@Override
	public LockLocalService getWrappedService() {
		return _lockLocalService;
	}

	@Override
	public void setWrappedService(LockLocalService lockLocalService) {
		_lockLocalService = lockLocalService;
	}

	private LockLocalService _lockLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:760683853