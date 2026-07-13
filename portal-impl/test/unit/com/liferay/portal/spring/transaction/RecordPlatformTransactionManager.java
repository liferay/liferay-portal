/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.transaction;

import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Objects;

import org.junit.Assert;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * @author Shuyang Zhou
 */
public class RecordPlatformTransactionManager
	implements PlatformTransactionManager {

	public static final TransactionStatus TRANSACTION_STATUS =
		(TransactionStatus)ProxyUtil.newProxyInstance(
			TransactionStatus.class.getClassLoader(),
			new Class<?>[] {TransactionStatus.class},
			new InvocationHandler() {

				@Override
				public Object invoke(
					Object proxy, Method method, Object[] args) {

					if (Objects.equals(method.getName(), "isNewTransaction")) {
						return true;
					}

					throw new UnsupportedOperationException(method.toString());
				}

			});

	@Override
	public void commit(TransactionStatus transactionStatus) {
		_commitTransactionStatus = transactionStatus;
	}

	@Override
	public TransactionStatus getTransaction(
		TransactionDefinition transactionDefinition) {

		_transactionDefinition = transactionDefinition;

		return TRANSACTION_STATUS;
	}

	@Override
	public void rollback(TransactionStatus transactionStatus) {
		_rollbackTransactionStatus = transactionStatus;
	}

	public void setCommitTransactionStatus(
		TransactionStatus commitTransactionStatus) {

		_commitTransactionStatus = commitTransactionStatus;
	}

	public void setRollbackTransactionStatus(
		TransactionStatus rollbackTransactionStatus) {

		_rollbackTransactionStatus = rollbackTransactionStatus;
	}

	public void verify(
		TransactionDefinition transactionDefinition,
		TransactionStatus commitTransactionStatus,
		TransactionStatus rollbackTransactionStatus) {

		Assert.assertSame(transactionDefinition, _transactionDefinition);
		Assert.assertSame(commitTransactionStatus, _commitTransactionStatus);
		Assert.assertSame(
			rollbackTransactionStatus, _rollbackTransactionStatus);
	}

	private TransactionStatus _commitTransactionStatus;
	private TransactionStatus _rollbackTransactionStatus;
	private TransactionDefinition _transactionDefinition;

}