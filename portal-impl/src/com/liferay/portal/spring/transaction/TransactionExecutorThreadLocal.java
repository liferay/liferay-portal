/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.transaction;

import com.liferay.petra.lang.CentralizedThreadLocal;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Preston Crary
 */
public class TransactionExecutorThreadLocal {

	public static TransactionExecutor getCurrentTransactionExecutor() {
		Deque<TransactionExecutor> transactionExecutorDeque =
			_transactionExecutorDeque.get();

		return transactionExecutorDeque.peek();
	}

	protected static TransactionExecutor popTransactionExecutor() {
		Deque<TransactionExecutor> transactionExecutorDeque =
			_transactionExecutorDeque.get();

		return transactionExecutorDeque.pop();
	}

	protected static void pushTransactionExecutor(
		TransactionExecutor transactionExecutor) {

		Deque<TransactionExecutor> transactionExecutorDeque =
			_transactionExecutorDeque.get();

		transactionExecutorDeque.push(transactionExecutor);
	}

	private TransactionExecutorThreadLocal() {
	}

	private static final ThreadLocal<Deque<TransactionExecutor>>
		_transactionExecutorDeque = new CentralizedThreadLocal<>(
			TransactionExecutorThreadLocal.class + "._transactionExecutorDeque",
			ArrayDeque::new, false);

}