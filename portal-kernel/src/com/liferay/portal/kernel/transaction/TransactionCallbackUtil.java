/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.transaction;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.spring.orm.LastSessionRecorderHelperUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Registers callbacks against the current transaction's lifecycle. Commit
 * callbacks run after the transaction commits, rollback callbacks run after it
 * rolls back, and completion callbacks run after either outcome, ordered after
 * the outcome specific callbacks. The listener recognizes savepoint backed
 * nested propagation scopes and keeps their callbacks in savepoint frames: a
 * scope that commits promotes its callbacks to the enclosing scope, while a
 * scope that rolls back runs its rollback and completion callbacks and
 * discards its commit callbacks.
 *
 * @author Shuyang Zhou
 */
public class TransactionCallbackUtil {

	public static final TransactionLifecycleListener
		TRANSACTION_LIFECYCLE_LISTENER = new TransactionLifecycleListener() {

			@Override
			public void committed(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				if (transactionAttribute.getPropagation() ==
						Propagation.NESTED) {

					if (transactionStatus.isNewTransaction()) {
						_committed();
					}
					else {
						_savepointReleased();
					}
				}
				else if (transactionStatus.isNewTransaction()) {
					_committed();
				}
			}

			@Override
			public void created(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus) {

				if (transactionAttribute.getPropagation() ==
						Propagation.NESTED) {

					if (transactionStatus.isNewTransaction()) {
						_created();
					}
					else {
						_savepointCreated();
					}
				}
				else if (transactionStatus.isNewTransaction()) {
					_created();
				}
			}

			@Override
			public void rollbacked(
				TransactionAttribute transactionAttribute,
				TransactionStatus transactionStatus, Throwable throwable) {

				if (transactionAttribute.getPropagation() ==
						Propagation.NESTED) {

					if (transactionStatus.isNewTransaction()) {
						_rollbacked();
					}
					else {
						_savepointRollbacked();
					}
				}
				else if (transactionStatus.isNewTransaction()) {
					_rollbacked();
				}
			}

		};

	public static void registerCommitCallback(Callable<?> callable) {
		Callbacks callbacks = _getCallbacks();

		if (callbacks == null) {
			_call(callable);

			return;
		}

		callbacks._commitCallables.add(_wrapCallable(callable));
	}

	public static void registerCompletionCallback(Callable<?> callable) {
		Callbacks callbacks = _getCallbacks();

		if (callbacks == null) {
			_call(callable);

			return;
		}

		callbacks._completionCallables.add(_wrapCallable(callable));
	}

	public static void registerRollbackCallback(Callable<?> callable) {
		Callbacks callbacks = _getCallbacks();

		if (callbacks == null) {

			// Without a transaction nothing can roll back

			return;
		}

		callbacks._rollbackCallables.add(_wrapCallable(callable));
	}

	private static void _call(Callable<?> callable) {
		try {
			callable.call();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static void _committed() {
		List<Callbacks> callbacksList = _popCallbacksList();

		if (callbacksList == _emptyCallbacksList) {
			return;
		}

		Callbacks callbacks = callbacksList.get(0);

		for (int i = 1; i < callbacksList.size(); i++) {
			callbacks._merge(callbacksList.get(i));
		}

		_invoke(callbacks._commitCallables);
		_invoke(callbacks._completionCallables);
	}

	private static void _created() {
		List<List<Callbacks>> callbacksLists = _callbacksLists.get();

		callbacksLists.add(_emptyCallbacksList);
	}

	private static Callbacks _getCallbacks() {
		List<Callbacks> callbacksList = _getWritableCallbacksList();

		if (callbacksList == null) {
			return null;
		}

		return callbacksList.get(callbacksList.size() - 1);
	}

	private static List<Callbacks> _getWritableCallbacksList() {
		List<List<Callbacks>> callbacksLists = _callbacksLists.get();

		if (callbacksLists.isEmpty()) {
			return null;
		}

		int index = callbacksLists.size() - 1;

		List<Callbacks> callbacksList = callbacksLists.get(index);

		if (callbacksList == _emptyCallbacksList) {
			callbacksList = new ArrayList<>();

			callbacksList.add(new Callbacks());

			callbacksLists.set(index, callbacksList);
		}

		return callbacksList;
	}

	private static void _invoke(List<Callable<?>> callables) {
		for (Callable<?> callable : callables) {
			try {
				callable.call();
			}
			catch (Exception exception) {
				_log.error("Unable to execute transaction callback", exception);
			}
		}
	}

	private static List<Callbacks> _popCallbacksList() {
		List<List<Callbacks>> callbacksLists = _callbacksLists.get();

		return callbacksLists.remove(callbacksLists.size() - 1);
	}

	private static void _rollbacked() {
		List<Callbacks> callbacksList = _popCallbacksList();

		if (callbacksList == _emptyCallbacksList) {
			return;
		}

		for (int i = callbacksList.size() - 1; i >= 0; i--) {
			Callbacks callbacks = callbacksList.get(i);

			_invoke(callbacks._rollbackCallables);
			_invoke(callbacks._completionCallables);
		}
	}

	private static void _savepointCreated() {
		List<Callbacks> callbacksList = _getWritableCallbacksList();

		if (callbacksList == null) {
			return;
		}

		callbacksList.add(new Callbacks());
	}

	private static void _savepointReleased() {
		List<Callbacks> callbacksList = _getWritableCallbacksList();

		if ((callbacksList == null) || (callbacksList.size() < 2)) {
			return;
		}

		Callbacks callbacks = callbacksList.remove(callbacksList.size() - 1);

		Callbacks parentCallbacks = callbacksList.get(callbacksList.size() - 1);

		parentCallbacks._merge(callbacks);
	}

	private static void _savepointRollbacked() {
		List<Callbacks> callbacksList = _getWritableCallbacksList();

		if ((callbacksList == null) || (callbacksList.size() < 2)) {
			return;
		}

		Callbacks callbacks = callbacksList.remove(callbacksList.size() - 1);

		_invoke(callbacks._rollbackCallables);
		_invoke(callbacks._completionCallables);
	}

	private static Callable<?> _wrapCallable(Callable<?> callable) {
		long companyId = CompanyThreadLocal.getCompanyId();

		return () -> {
			if (companyId == CompanyThreadLocal.getCompanyId()) {
				return callable.call();
			}

			// A callback runs under the company scope it was registered in.
			// Company scoped entities stamp the live company thread local
			// into new rows, so a callback running under whatever scope is
			// current when the transaction completes would mislabel the data
			// it creates.

			if (PropsValues.DATABASE_PARTITION_ENABLED) {

				// Statement routing reads the live company thread local, so
				// session state left pending under the current scope must
				// flush before the switch, and state created under the
				// captured scope must flush before the restore, or it would
				// execute against the wrong company's partition

				LastSessionRecorderHelperUtil.syncLastSessionState(false);

				try (SafeCloseable safeCloseable =
						CompanyThreadLocal.setRawCompanyIdWithSafeCloseable(
							companyId)) {

					try {
						return callable.call();
					}
					finally {
						LastSessionRecorderHelperUtil.syncLastSessionState(
							false);
					}
				}
			}

			// Without partitions every statement runs against the same
			// schema, so only the company id itself needs to be restored

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setRawCompanyIdWithSafeCloseable(
						companyId)) {

				return callable.call();
			}
		};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TransactionCallbackUtil.class);

	private static final ThreadLocal<List<List<Callbacks>>> _callbacksLists =
		new CentralizedThreadLocal<>(
			TransactionCallbackUtil.class + "._callbacksLists", ArrayList::new);
	private static final List<Callbacks> _emptyCallbacksList =
		Collections.emptyList();

	private static class Callbacks {

		private void _merge(Callbacks callbacks) {
			_commitCallables.addAll(callbacks._commitCallables);
			_completionCallables.addAll(callbacks._completionCallables);
			_rollbackCallables.addAll(callbacks._rollbackCallables);
		}

		private final List<Callable<?>> _commitCallables = new ArrayList<>();
		private final List<Callable<?>> _completionCallables =
			new ArrayList<>();
		private final List<Callable<?>> _rollbackCallables = new ArrayList<>();

	}

}