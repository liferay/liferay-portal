/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lock.service.impl;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.LockListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.lock.exception.DuplicateLockException;
import com.liferay.portal.lock.exception.ExpiredLockException;
import com.liferay.portal.lock.exception.NoSuchLockException;
import com.liferay.portal.lock.model.Lock;
import com.liferay.portal.lock.service.base.LockLocalServiceBaseImpl;

import jakarta.persistence.PersistenceException;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.LockAcquisitionException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@Component(
	property = "model.class.name=com.liferay.portal.lock.model.Lock",
	service = AopService.class
)
@CTAware
public class LockLocalServiceImpl extends LockLocalServiceBaseImpl {

	@Override
	public void clear() {
		lockPersistence.removeByLtExpirationDate(new Date());
	}

	@Override
	public Lock fetchLock(String className, long key) {
		return fetchLock(className, String.valueOf(key));
	}

	@Override
	public Lock fetchLock(String className, String key) {
		if (lockPersistence.countByClassName(className) == 0) {
			return null;
		}

		Lock lock = lockPersistence.fetchByC_K(className, key);

		if ((lock != null) && lock.isExpired()) {
			_expireLock(lock);

			lock = null;
		}

		return lock;
	}

	@Override
	public Lock getLock(String className, long key) throws PortalException {
		return getLock(className, String.valueOf(key));
	}

	@Override
	public Lock getLock(String className, String key) throws PortalException {
		if (lockPersistence.countByClassName(className) == 0) {
			throw new NoSuchLockException(
				StringBundler.concat(
					"No Lock exists with the key {className=", className,
					", key=", key, "}"));
		}

		Lock lock = lockPersistence.findByC_K(className, key);

		if (lock.isExpired()) {
			_expireLock(lock);

			throw new ExpiredLockException();
		}

		return lock;
	}

	@Override
	public List<Lock> getLocks(long companyId, long userId, String className) {
		return lockPersistence.findByC_U_C(companyId, userId, className);
	}

	@Override
	public List<Lock> getLocks(long companyId, String className) {
		return lockPersistence.findByC_C(companyId, className);
	}

	@Override
	public boolean hasLock(long userId, String className, long key) {
		return hasLock(userId, className, String.valueOf(key));
	}

	@Override
	public boolean hasLock(long userId, String className, String key) {
		Lock lock = fetchLock(className, key);

		if ((lock != null) && (lock.getUserId() == userId)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isLocked(String className, long key) {
		return isLocked(className, String.valueOf(key));
	}

	@Override
	public boolean isLocked(String className, String key) {
		Lock lock = fetchLock(className, key);

		if (lock == null) {
			return false;
		}

		return true;
	}

	@Override
	public Lock lock(
			long userId, String className, long key, String owner,
			boolean inheritable, long expirationTime)
		throws PortalException {

		return lock(
			userId, className, String.valueOf(key), owner, inheritable,
			expirationTime);
	}

	@Override
	public Lock lock(
			long userId, String className, long key, String owner,
			boolean inheritable, long expirationTime, boolean renew)
		throws PortalException {

		return lock(
			userId, className, String.valueOf(key), owner, inheritable,
			expirationTime, renew);
	}

	@Override
	public Lock lock(
			long userId, String className, String key, String owner,
			boolean inheritable, long expirationTime)
		throws PortalException {

		return lock(
			userId, className, key, owner, inheritable, expirationTime, true);
	}

	@Override
	public Lock lock(
			long userId, String className, String key, String owner,
			boolean inheritable, long expirationTime, boolean renew)
		throws PortalException {

		Lock lock = lockPersistence.fetchByC_K(className, key);

		if (lock != null) {
			if (lock.isExpired()) {
				_expireLock(lock);

				lock = null;
			}
			else if (lock.getUserId() != userId) {
				throw new DuplicateLockException(lock);
			}
		}

		boolean isNew = false;

		if (lock == null) {
			User user = _userLocalService.getUser(userId);

			long lockId = counterLocalService.increment();

			lock = lockPersistence.create(lockId);

			lock.setCompanyId(user.getCompanyId());
			lock.setUserId(user.getUserId());
			lock.setUserName(user.getFullName());
			lock.setClassName(className);
			lock.setKey(key);
			lock.setOwner(owner);
			lock.setInheritable(inheritable);

			isNew = true;
		}
		else if (!renew) {
			return lock;
		}

		Date date = new Date();

		lock.setCreateDate(date);

		if (expirationTime == 0) {
			lock.setExpirationDate(null);
		}
		else {
			lock.setExpirationDate(new Date(date.getTime() + expirationTime));
		}

		lock = lockPersistence.update(lock);

		if (isNew) {
			lock.setNew(true);
		}

		return lock;
	}

	@Override
	public Lock lock(String className, String key, String owner) {
		return lock(className, key, null, owner);
	}

	@Override
	public Lock lock(
		String className, String key, String expectedOwner,
		String updatedOwner) {

		while (true) {
			try {
				return TransactionInvokerUtil.invoke(
					_transactionConfig,
					() -> {
						Lock lock = lockPersistence.fetchByC_K(
							className, key, false);

						if (lock == null) {
							long lockId = counterLocalService.increment();

							lock = lockPersistence.create(lockId);

							lock.setCreateDate(new Date());
							lock.setClassName(className);
							lock.setKey(key);
							lock.setOwner(updatedOwner);

							lock = lockPersistence.update(lock);

							lock.setNew(true);
						}
						else if (Objects.equals(
									lock.getOwner(), expectedOwner)) {

							lock.setCreateDate(new Date());
							lock.setClassName(className);
							lock.setKey(key);
							lock.setOwner(updatedOwner);

							lock = lockPersistence.update(lock);

							lock.setNew(true);
						}

						return lock;
					});
			}
			catch (Throwable throwable) {
				Throwable causeThrowable = throwable;

				if (throwable instanceof ORMException ||
					throwable instanceof PersistenceException) {

					causeThrowable = throwable.getCause();
				}

				if (causeThrowable instanceof ConstraintViolationException ||
					causeThrowable instanceof LockAcquisitionException) {

					if (_log.isInfoEnabled()) {
						_log.info("Unable to acquire lock, retrying");
					}

					continue;
				}

				ReflectionUtil.throwException(throwable);
			}
		}
	}

	@Override
	public Lock refresh(String uuid, long companyId, long expirationTime)
		throws PortalException {

		List<Lock> locks = lockPersistence.findByUuid_C(uuid, companyId);

		if (locks.isEmpty()) {
			throw new NoSuchLockException(
				StringBundler.concat(
					"{uuid=", uuid, ", companyId=", companyId, "}"));
		}

		Lock lock = locks.get(0);

		LockListener lockListener = _getLockListener(lock.getClassName());

		String key = lock.getKey();

		if (lockListener != null) {
			lockListener.onBeforeRefresh(key);
		}

		try {
			Date date = new Date();

			lock.setCreateDate(date);

			if (expirationTime == 0) {
				lock.setExpirationDate(null);
			}
			else {
				lock.setExpirationDate(
					new Date(date.getTime() + expirationTime));
			}

			return lockPersistence.update(lock);
		}
		finally {
			if (lockListener != null) {
				lockListener.onAfterRefresh(key);
			}
		}
	}

	@Override
	public void unlock(String className, long key) {
		unlock(className, String.valueOf(key));
	}

	@Override
	public void unlock(String className, String key) {
		Lock lock = lockPersistence.fetchByC_K(className, key);

		if (lock != null) {
			lockPersistence.remove(lock);
		}
	}

	@Override
	public void unlock(String className, String key, String owner) {
		while (true) {
			try {
				TransactionInvokerUtil.invoke(
					_transactionConfig,
					() -> {
						Lock lock = lockPersistence.fetchByC_K(
							className, key, false);

						if (lock == null) {
							return null;
						}

						if (Objects.equals(lock.getOwner(), owner)) {
							lockPersistence.remove(lock);
						}

						return null;
					});

				return;
			}
			catch (Throwable throwable) {
				Throwable causeThrowable = throwable;

				if (throwable instanceof ORMException) {
					causeThrowable = throwable.getCause();
				}

				if (causeThrowable instanceof ConstraintViolationException ||
					causeThrowable instanceof LockAcquisitionException) {

					if (_log.isInfoEnabled()) {
						_log.info("Unable to remove lock, retrying");
					}

					continue;
				}

				ReflectionUtil.throwException(throwable);
			}
		}
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();

		if (_serviceTrackerMap != null) {
			_serviceTrackerMap.close();
		}
	}

	private void _expireLock(Lock lock) {
		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					LockListener lockListener = _getLockListener(
						lock.getClassName());

					String key = lock.getKey();

					if (lockListener != null) {
						lockListener.onBeforeExpire(key);
					}

					try {
						lockPersistence.remove(lock);

						lockPersistence.flush();
					}
					finally {
						if (lockListener != null) {
							lockListener.onAfterExpire(key);
						}
					}

					return null;
				});
		}
		catch (Throwable throwable) {
			_log.error("Unable to expire lock", throwable);

			ReflectionUtil.throwException(throwable);
		}
	}

	private LockListener _getLockListener(String className) {
		if (_serviceTrackerMap == null) {
			Bundle bundle = FrameworkUtil.getBundle(LockLocalServiceImpl.class);

			BundleContext bundleContext = bundle.getBundleContext();

			_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, LockListener.class, null,
				(serviceReference, emitter) -> {
					LockListener lockListener = bundleContext.getService(
						serviceReference);

					emitter.emit(lockListener.getClassName());
				});
		}

		return _serviceTrackerMap.getService(className);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LockLocalServiceImpl.class);

	private ServiceTrackerMap<String, LockListener> _serviceTrackerMap;
	private final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRES_NEW, new Class<?>[] {Exception.class});

	@Reference
	private UserLocalService _userLocalService;

}