/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.transaction;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionLifecycleManager;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Michael C. Han
 * @author Shuyang Zhou
 */
public class DefaultTransactionExecutor implements TransactionExecutor {

	public DefaultTransactionExecutor(
		PlatformTransactionManager platformTransactionManager) {

		_platformTransactionManager = platformTransactionManager;
	}

	@Override
	public void commit(
		TransactionAttributeAdapter transactionAttributeAdapter,
		TransactionStatusAdapter transactionStatusAdapter) {

		Throwable transactionManagerThrowable = null;

		try {
			_platformTransactionManager.commit(
				transactionStatusAdapter.getTransactionStatus());
		}
		catch (Throwable throwable) {
			transactionManagerThrowable = throwable;

			throw throwable;
		}
		finally {
			if (transactionManagerThrowable == null) {
				TransactionLifecycleManager.fireTransactionCommittedEvent(
					transactionAttributeAdapter, transactionStatusAdapter);
			}
			else {
				TransactionLifecycleManager.fireTransactionRollbackedEvent(
					transactionAttributeAdapter, transactionStatusAdapter,
					transactionManagerThrowable);
			}

			TransactionExecutorThreadLocal.popTransactionExecutor();

			transactionStatusAdapter.reportLifecycleListenerThrowables(
				transactionManagerThrowable);
		}
	}

	@Override
	public <T> T execute(
			TransactionAttributeAdapter transactionAttributeAdapter,
			UnsafeSupplier<T, Throwable> unsafeSupplier)
		throws Throwable {

		TransactionStatusAdapter transactionStatusAdapter = start(
			transactionAttributeAdapter);

		T returnValue = null;

		try {
			if (transactionAttributeAdapter.getPropagation() ==
					Propagation.NESTED) {

				if (transactionStatusAdapter.isNewTransaction()) {
					returnValue = _invokeWithNewTransaction(unsafeSupplier);
				}
				else {
					returnValue = _invokeWithSavepoint(unsafeSupplier);
				}
			}
			else if (transactionStatusAdapter.isNewTransaction()) {
				returnValue = _invokeWithNewTransaction(unsafeSupplier);
			}
			else {
				returnValue = _invokeWithAmbientTransaction(unsafeSupplier);
			}
		}
		catch (Throwable throwable) {
			rollback(
				throwable, transactionAttributeAdapter,
				transactionStatusAdapter);
		}

		commit(transactionAttributeAdapter, transactionStatusAdapter);

		return returnValue;
	}

	@Override
	public PlatformTransactionManager getPlatformTransactionManager() {
		return _platformTransactionManager;
	}

	@Override
	public void rollback(
			Throwable throwable1,
			TransactionAttributeAdapter transactionAttributeAdapter,
			TransactionStatusAdapter transactionStatusAdapter)
		throws Throwable {

		boolean rollback = transactionAttributeAdapter.rollbackOn(throwable1);

		Throwable transactionManagerThrowable = null;

		try {
			if (rollback) {
				_platformTransactionManager.rollback(
					transactionStatusAdapter.getTransactionStatus());
			}
			else {
				_platformTransactionManager.commit(
					transactionStatusAdapter.getTransactionStatus());
			}

			throw throwable1;
		}
		catch (Throwable throwable2) {
			if (throwable2 != throwable1) {
				throwable2.addSuppressed(throwable1);

				transactionManagerThrowable = throwable2;
			}

			throw throwable2;
		}
		finally {
			if (rollback) {
				TransactionLifecycleManager.fireTransactionRollbackedEvent(
					transactionAttributeAdapter, transactionStatusAdapter,
					throwable1);
			}
			else if (transactionManagerThrowable == null) {
				TransactionLifecycleManager.fireTransactionCommittedEvent(
					transactionAttributeAdapter, transactionStatusAdapter);
			}
			else {
				TransactionLifecycleManager.fireTransactionRollbackedEvent(
					transactionAttributeAdapter, transactionStatusAdapter,
					transactionManagerThrowable);
			}

			TransactionExecutorThreadLocal.popTransactionExecutor();

			if (transactionManagerThrowable == null) {
				transactionStatusAdapter.reportLifecycleListenerThrowables(
					throwable1);
			}
			else {
				transactionStatusAdapter.reportLifecycleListenerThrowables(
					transactionManagerThrowable);
			}
		}
	}

	@Override
	public TransactionStatusAdapter start(
		TransactionAttributeAdapter transactionAttributeAdapter) {

		TransactionStatusAdapter transactionStatusAdapter =
			new TransactionStatusAdapter(
				_platformTransactionManager.getTransaction(
					transactionAttributeAdapter));

		TransactionExecutorThreadLocal.pushTransactionExecutor(this);

		TransactionLifecycleManager.fireTransactionCreatedEvent(
			transactionAttributeAdapter, transactionStatusAdapter);

		return transactionStatusAdapter;
	}

	private <T> T _invokeWithAmbientTransaction(
			UnsafeSupplier<T, Throwable> unsafeSupplier)
		throws Throwable {

		return unsafeSupplier.get();
	}

	private <T> T _invokeWithNewTransaction(
			UnsafeSupplier<T, Throwable> unsafeSupplier)
		throws Throwable {

		return unsafeSupplier.get();
	}

	private <T> T _invokeWithSavepoint(
			UnsafeSupplier<T, Throwable> unsafeSupplier)
		throws Throwable {

		return unsafeSupplier.get();
	}

	private final PlatformTransactionManager _platformTransactionManager;

}