/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lock.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.lock.exception.DuplicateLockException;
import com.liferay.portal.lock.model.Lock;
import com.liferay.portal.lock.service.LockLocalServiceUtil;
import com.liferay.portal.test.rule.ExpectedDBType;
import com.liferay.portal.test.rule.ExpectedLog;
import com.liferay.portal.test.rule.ExpectedLogs;
import com.liferay.portal.test.rule.ExpectedMultipleLogs;
import com.liferay.portal.test.rule.ExpectedType;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.persistence.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class LockLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		LockLocalServiceUtil.unlock("className", "key");
	}

	@ExpectedMultipleLogs(
		expectedMultipleLogs = {
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedDBType = ExpectedDBType.DB2,
						expectedLog = "Error for batch element",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.DB2,
						expectedLog = "Batch failure.",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.HYPERSONIC,
						expectedLog = "integrity constraint violation: unique constraint or index violation: IX_228562AD",
						expectedType = ExpectedType.EXACT
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MARIADB,
						expectedLog = "Deadlock found when trying to get lock; try restarting transaction",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MARIADB,
						expectedLog = "Duplicate entry",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MYSQL,
						expectedLog = "Deadlock found when trying to get lock; try restarting transaction",
						expectedType = ExpectedType.EXACT
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MYSQL,
						expectedLog = "Duplicate entry",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.ORACLE,
						expectedLog = "ORA-00001: unique constraint",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.POSTGRESQL,
						expectedLog = "Batch entry 0 insert into Lock_ ",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.POSTGRESQL,
						expectedLog = "ERROR: duplicate key value violates unique constraint ",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.SQLSERVER,
						expectedLog = "Cannot insert duplicate key row in object",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = SqlExceptionHelper.class
			),
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedDBType = ExpectedDBType.NONE,
						expectedLog = "HHH000315: Exception executing batch [java.sql.BatchUpdateException",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = BatchingBatch.class
			)
		}
	)
	@Test
	public void testLock() throws Exception {
		final long userId = TestPropsValues.getUserId();

		// Lock is new

		Lock lock = LockLocalServiceUtil.lock(
			userId, "className", "key", "owner", false, Time.DAY, false);

		Assert.assertTrue(lock.isNew());

		Date expirationDate = lock.getExpirationDate();

		// Lock already exist, don't renew

		lock = LockLocalServiceUtil.lock(
			userId, "className", "key", "owner", false, Time.DAY, false);

		Assert.assertFalse(lock.isNew());
		Assert.assertEquals(expirationDate, lock.getExpirationDate());

		// Lock already exist, renew

		lock = LockLocalServiceUtil.lock(
			userId, "className", "key", "owner", false, 2 * Time.DAY, true);

		Assert.assertFalse(lock.isNew());
		Assert.assertTrue(expirationDate.before(lock.getExpirationDate()));

		// Duplicate lock exception

		try {
			LockLocalServiceUtil.lock(
				0, "className", "key", "owner", false, Time.DAY, false);

			Assert.fail();
		}
		catch (DuplicateLockException duplicateLockException) {
		}

		// Set lock to be expired

		expirationDate = new Date(System.currentTimeMillis() - (10 * Time.DAY));

		lock.setExpirationDate(expirationDate);

		lock = LockLocalServiceUtil.updateLock(lock);

		Assert.assertEquals(expirationDate, lock.getExpirationDate());

		// Lock with auto expiration

		lock = LockLocalServiceUtil.lock(
			userId, "className", "key", "owner", false, Time.DAY, false);

		Assert.assertTrue(lock.isNew());
		Assert.assertTrue(expirationDate.before(lock.getExpirationDate()));

		// Remove lock

		LockLocalServiceUtil.unlock("className", "key");

		// Concurrent locking

		Bundle bundle = FrameworkUtil.getBundle(LockLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		final Thread testThread = Thread.currentThread();

		final CountDownLatch createdCountDownLatch = new CountDownLatch(1);
		final CountDownLatch continueCountDownLatch = new CountDownLatch(1);

		ServiceRegistration<ModelListener<Lock>> serviceRegistration =
			bundleContext.registerService(
				(Class<ModelListener<Lock>>)(Class<?>)ModelListener.class,
				new BaseModelListener<Lock>() {

					@Override
					public void onAfterCreate(Lock model) {
						if (Thread.currentThread() != testThread) {
							createdCountDownLatch.countDown();

							try {
								continueCountDownLatch.await();
							}
							catch (InterruptedException interruptedException) {
								ReflectionUtil.throwException(
									interruptedException);
							}
						}
					}

				},
				null);

		FutureTask<Lock> futureTask = new FutureTask<>(
			new Callable<Lock>() {

				@Override
				public Lock call() throws PortalException {
					return LockLocalServiceUtil.lock(
						userId, "className", "key", "owner", false, Time.DAY,
						false);
				}

			});

		Thread lockCreateThread = new Thread(futureTask, "Lock Create Thread");

		lockCreateThread.start();

		createdCountDownLatch.await();

		serviceRegistration.unregister();

		lock = LockLocalServiceUtil.lock(
			userId, "className", "key", "owner", false, Time.DAY, false);

		Assert.assertTrue(lock.isNew());

		continueCountDownLatch.countDown();

		try {
			futureTask.get();

			Assert.fail();
		}
		catch (ExecutionException executionException) {
			Throwable throwable1 = executionException.getCause();

			Assert.assertSame(
				PersistenceException.class, throwable1.getClass());

			Throwable throwable2 = throwable1.getCause();

			Assert.assertSame(
				ConstraintViolationException.class, throwable2.getClass());
		}
	}

	@ExpectedMultipleLogs(
		expectedMultipleLogs = {
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedDBType = ExpectedDBType.DB2,
						expectedLog = "Error for batch element",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.DB2,
						expectedLog = "Batch failure.",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.HYPERSONIC,
						expectedLog = "integrity constraint violation: unique constraint or index violation: IX_228562AD",
						expectedType = ExpectedType.EXACT
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MARIADB,
						expectedLog = "Deadlock found when trying to get lock; try restarting transaction",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MARIADB,
						expectedLog = "Duplicate entry",
						expectedType = ExpectedType.CONTAINS
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MYSQL,
						expectedLog = "Deadlock found when trying to get lock; try restarting transaction",
						expectedType = ExpectedType.EXACT
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.MYSQL,
						expectedLog = "Duplicate entry",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.ORACLE,
						expectedLog = "ORA-00001: unique constraint",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.POSTGRESQL,
						expectedLog = "Batch entry 0 insert into Lock_ ",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.POSTGRESQL,
						expectedLog = "ERROR: duplicate key value violates unique constraint ",
						expectedType = ExpectedType.PREFIX
					),
					@ExpectedLog(
						expectedDBType = ExpectedDBType.SQLSERVER,
						expectedLog = "Cannot insert duplicate key row in object",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = SqlExceptionHelper.class
			),
			@ExpectedLogs(
				expectedLogs = {
					@ExpectedLog(
						expectedDBType = ExpectedDBType.NONE,
						expectedLog = "HHH000315: Exception executing batch [java.sql.BatchUpdateException",
						expectedType = ExpectedType.PREFIX
					)
				},
				level = "ERROR", loggerClass = BatchingBatch.class
			)
		}
	)
	@Test
	public void testMutualExcludeLockingParallel() throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(10);

		List<Future<Void>> futures = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			LockingJob lockingJob = new LockingJob(
				"className", "key", "owner-" + i, 10);

			futures.add(executorService.submit(lockingJob));
		}

		executorService.shutdown();

		Assert.assertTrue(
			executorService.awaitTermination(600, TimeUnit.SECONDS));

		for (Future<Void> future : futures) {
			future.get();
		}

		Assert.assertFalse(LockLocalServiceUtil.isLocked("className", "key"));
	}

	@Test
	public void testMutualExcludeLockingSerial() throws Exception {
		String className = "testClassName";
		String key = "testKey";
		String owner1 = "testOwner1";

		Lock lock1 = LockLocalServiceUtil.lock(className, key, owner1);

		Assert.assertEquals(owner1, lock1.getOwner());

		Assert.assertTrue(lock1.isNew());

		String owner2 = "owner2";

		Lock lock2 = LockLocalServiceUtil.lock(className, key, owner2);

		Assert.assertEquals(owner1, lock2.getOwner());
		Assert.assertFalse(lock2.isNew());

		LockLocalServiceUtil.unlock(className, key, owner1);

		lock2 = LockLocalServiceUtil.lock(className, key, owner2);

		Assert.assertEquals(owner2, lock2.getOwner());
		Assert.assertTrue(lock2.isNew());

		LockLocalServiceUtil.unlock(className, key, owner2);
	}

	private static class LockingJob implements Callable<Void> {

		@Override
		public Void call() {
			int count = 0;

			while (true) {
				try {
					Lock lock = LockLocalServiceUtil.lock(
						_className, _key, _owner);

					if (lock.isNew()) {

						// The lock creator is responsible for unlocking. Try to
						// unlock many times because some databases like SQL
						// Server may randomly choke and rollback an unlock.

						while (true) {
							try {
								LockLocalServiceUtil.unlock(
									_className, _key, _owner);

								if (++count >= _requiredSuccessCount) {
									return null;
								}

								break;
							}
							catch (RuntimeException runtimeException) {
								throw runtimeException;
							}
						}
					}
				}
				catch (RuntimeException runtimeException) {
					throw runtimeException;
				}
			}
		}

		private LockingJob(
			String className, String key, String owner,
			int requiredSuccessCount) {

			_className = className;
			_key = key;
			_owner = owner;
			_requiredSuccessCount = requiredSuccessCount;
		}

		private final String _className;
		private final String _key;
		private final String _owner;
		private final int _requiredSuccessCount;

	}

}