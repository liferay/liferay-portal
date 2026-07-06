/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.transaction;

import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class TransactionCallbackUtilTest {

	@Test
	public void testCommitted() {
		_fireCreated(Propagation.REQUIRED, true);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("commit1"));
		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("commit2"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("completion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("rollback"));

		Assert.assertTrue(_records.toString(), _records.isEmpty());

		_fireCommitted(Propagation.REQUIRED, true);

		Assert.assertEquals(
			Arrays.asList("commit1", "commit2", "completion"), _records);
	}

	@Test
	public void testCommittedWhenCallableThrows() {
		_fireCreated(Propagation.REQUIRED, true);

		Exception exception = new Exception();

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				throw exception;
			});

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("commit"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				TransactionCallbackUtil.class.getName(),
				LoggerTestUtil.ERROR)) {

			_fireCommitted(Propagation.REQUIRED, true);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to execute transaction callback",
				logEntry.getMessage());
			Assert.assertSame(exception, logEntry.getThrowable());
		}

		Assert.assertEquals(Collections.singletonList("commit"), _records);
	}

	@Test
	public void testCommittedWithJoiningTransactionEvents() {
		_fireCreated(Propagation.REQUIRED, true);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("outerCommit"));

		_fireCreated(Propagation.REQUIRED, false);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("joinedCommit"));

		_fireCommitted(Propagation.REQUIRED, false);
		_fireRollbacked(Propagation.REQUIRED, false);

		Assert.assertTrue(_records.toString(), _records.isEmpty());

		_fireCommitted(Propagation.REQUIRED, true);

		Assert.assertEquals(
			Arrays.asList("outerCommit", "joinedCommit"), _records);
	}

	@Test
	public void testNestedNewTransaction() {
		_fireCreated(Propagation.NESTED, true);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("commit"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("completion"));

		_fireCommitted(Propagation.NESTED, true);

		Assert.assertEquals(Arrays.asList("commit", "completion"), _records);

		_records.clear();

		_fireCreated(Propagation.NESTED, true);

		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("rollback"));

		_fireRollbacked(Propagation.NESTED, true);

		Assert.assertEquals(Collections.singletonList("rollback"), _records);
	}

	@Test
	public void testRollbacked() {
		_fireCreated(Propagation.REQUIRED, true);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("commit"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("completion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("rollback1"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("rollback2"));

		Assert.assertTrue(_records.toString(), _records.isEmpty());

		_fireRollbacked(Propagation.REQUIRED, true);

		Assert.assertEquals(
			Arrays.asList("rollback1", "rollback2", "completion"), _records);
	}

	@Test
	public void testRollbackedWhenCallableThrows() {
		_fireCreated(Propagation.REQUIRED, true);

		Exception exception1 = new Exception();
		Exception exception2 = new Exception();

		TransactionCallbackUtil.registerRollbackCallback(
			() -> {
				throw exception1;
			});
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("rollback"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> {
				throw exception2;
			});
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("completion"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				TransactionCallbackUtil.class.getName(),
				LoggerTestUtil.ERROR)) {

			_fireRollbacked(Propagation.REQUIRED, true);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 2, logEntries.size());

			LogEntry logEntry1 = logEntries.get(0);

			Assert.assertEquals(
				"Unable to execute transaction callback",
				logEntry1.getMessage());
			Assert.assertSame(exception1, logEntry1.getThrowable());

			LogEntry logEntry2 = logEntries.get(1);

			Assert.assertEquals(
				"Unable to execute transaction callback",
				logEntry2.getMessage());
			Assert.assertSame(exception2, logEntry2.getThrowable());
		}

		Assert.assertEquals(Arrays.asList("rollback", "completion"), _records);
	}

	@Test
	public void testRollbackedWithOpenSavepointScopes() {
		_fireCreated(Propagation.REQUIRED, true);

		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("outerCompletion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("outerRollback"));

		_fireCreated(Propagation.NESTED, false);

		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("savepointCompletion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("savepointRollback"));

		_fireRollbacked(Propagation.REQUIRED, true);

		Assert.assertEquals(
			Arrays.asList(
				"savepointRollback", "savepointCompletion", "outerRollback",
				"outerCompletion"),
			_records);
	}

	@Test
	public void testSavepointScopeCommitted() {
		_fireCreated(Propagation.REQUIRED, true);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("outerCommit"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("outerCompletion"));

		_fireCreated(Propagation.NESTED, false);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("savepointCommit"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("savepointCompletion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("savepointRollback"));

		_fireCommitted(Propagation.NESTED, false);

		Assert.assertTrue(_records.toString(), _records.isEmpty());

		_fireCommitted(Propagation.REQUIRED, true);

		Assert.assertEquals(
			Arrays.asList(
				"outerCommit", "savepointCommit", "outerCompletion",
				"savepointCompletion"),
			_records);
	}

	@Test
	public void testSavepointScopeCommittedThenRollbacked() {
		_fireCreated(Propagation.REQUIRED, true);

		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("outerCompletion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("outerRollback"));

		_fireCreated(Propagation.NESTED, false);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("savepointCommit"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("savepointCompletion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("savepointRollback"));

		_fireCommitted(Propagation.NESTED, false);

		Assert.assertTrue(_records.toString(), _records.isEmpty());

		_fireRollbacked(Propagation.REQUIRED, true);

		Assert.assertEquals(
			Arrays.asList(
				"outerRollback", "savepointRollback", "outerCompletion",
				"savepointCompletion"),
			_records);
	}

	@Test
	public void testSavepointScopeRollbacked() {
		_fireCreated(Propagation.REQUIRED, true);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("outerCommit"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("outerCompletion"));

		_fireCreated(Propagation.NESTED, false);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("savepointCommit"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("savepointCompletion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("savepointRollback"));

		_fireRollbacked(Propagation.NESTED, false);

		Assert.assertEquals(
			Arrays.asList("savepointRollback", "savepointCompletion"),
			_records);

		_fireCommitted(Propagation.REQUIRED, true);

		Assert.assertEquals(
			Arrays.asList(
				"savepointRollback", "savepointCompletion", "outerCommit",
				"outerCompletion"),
			_records);
	}

	@Test
	public void testSavepointScopesNested() {
		_fireCreated(Propagation.REQUIRED, true);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("outerCommit"));

		_fireCreated(Propagation.NESTED, false);

		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("savepoint1Completion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("savepoint1Rollback"));

		_fireCreated(Propagation.NESTED, false);

		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("savepoint2Commit"));
		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("savepoint2Completion"));
		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("savepoint2Rollback"));

		_fireCommitted(Propagation.NESTED, false);

		Assert.assertTrue(_records.toString(), _records.isEmpty());

		_fireRollbacked(Propagation.NESTED, false);

		Assert.assertEquals(
			Arrays.asList(
				"savepoint1Rollback", "savepoint2Rollback",
				"savepoint1Completion", "savepoint2Completion"),
			_records);

		_fireCommitted(Propagation.REQUIRED, true);

		Assert.assertEquals(
			Arrays.asList(
				"savepoint1Rollback", "savepoint2Rollback",
				"savepoint1Completion", "savepoint2Completion", "outerCommit"),
			_records);
	}

	@Test
	public void testWithoutCallbacks() {
		_fireCreated(Propagation.REQUIRED, true);
		_fireCommitted(Propagation.REQUIRED, true);

		_fireCreated(Propagation.REQUIRED, true);
		_fireRollbacked(Propagation.REQUIRED, true);

		Assert.assertTrue(_records.toString(), _records.isEmpty());
	}

	@Test
	public void testWithoutTransaction() {
		TransactionCallbackUtil.registerCommitCallback(
			() -> _records.add("commit"));

		Assert.assertEquals(Collections.singletonList("commit"), _records);

		TransactionCallbackUtil.registerCompletionCallback(
			() -> _records.add("completion"));

		Assert.assertEquals(Arrays.asList("commit", "completion"), _records);

		TransactionCallbackUtil.registerRollbackCallback(
			() -> _records.add("rollback"));

		Assert.assertEquals(Arrays.asList("commit", "completion"), _records);
	}

	@Test
	public void testWithoutTransactionWhenCallableThrows() {
		Exception exception = new Exception();

		try {
			TransactionCallbackUtil.registerCommitCallback(
				() -> {
					throw exception;
				});

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Assert.assertSame(exception, runtimeException.getCause());
		}

		try {
			TransactionCallbackUtil.registerCompletionCallback(
				() -> {
					throw exception;
				});

			Assert.fail();
		}
		catch (RuntimeException runtimeException) {
			Assert.assertSame(exception, runtimeException.getCause());
		}
	}

	private TransactionAttribute _createTransactionAttribute(
		Propagation propagation) {

		TransactionAttribute.Builder builder =
			new TransactionAttribute.Builder();

		builder.setPropagation(propagation);

		return builder.build();
	}

	private TransactionStatus _createTransactionStatus(boolean newTransaction) {
		return new TransactionStatus() {

			@Override
			public boolean isCompleted() {
				return false;
			}

			@Override
			public boolean isNewTransaction() {
				return newTransaction;
			}

			@Override
			public boolean isRollbackOnly() {
				return false;
			}

			@Override
			public void suppressLifecycleListenerThrowable(
				Throwable throwable) {
			}

		};
	}

	private void _fireCommitted(
		Propagation propagation, boolean newTransaction) {

		TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER.committed(
			_createTransactionAttribute(propagation),
			_createTransactionStatus(newTransaction));
	}

	private void _fireCreated(Propagation propagation, boolean newTransaction) {
		TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER.created(
			_createTransactionAttribute(propagation),
			_createTransactionStatus(newTransaction));
	}

	private void _fireRollbacked(
		Propagation propagation, boolean newTransaction) {

		TransactionCallbackUtil.TRANSACTION_LIFECYCLE_LISTENER.rollbacked(
			_createTransactionAttribute(propagation),
			_createTransactionStatus(newTransaction), null);
	}

	private final List<String> _records = new ArrayList<>();

}