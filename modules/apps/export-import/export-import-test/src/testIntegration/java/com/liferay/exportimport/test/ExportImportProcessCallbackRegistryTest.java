/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.lar.ExportImportProcessCallbackRegistry;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleManagerUtil;
import com.liferay.exportimport.kernel.lifecycle.constants.ExportImportLifecycleConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 */
@RunWith(Arquillian.class)
public class ExportImportProcessCallbackRegistryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_processId = RandomTestUtil.randomString();
	}

	@Test
	public void testMultiThreadedParallelNestedProcessRegisterCallable()
		throws Exception {

		MockExportImportProcessCallable callable =
			new MockExportImportProcessCallable();

		startProcess(_processId);

		Thread thread1 = new Thread(getFullProcessRunnable());

		thread1.start();

		Thread thread2 = new Thread(getFullProcessRunnable());

		thread2.start();

		_exportImportProcessCallbackRegistry.registerCallback(
			_processId, callable);

		Assert.assertFalse(callable.hasBeenCalled.get());

		stopProcess(_processId);

		Assert.assertTrue(callable.hasBeenCalled.get());

		thread1.join();
		thread2.join();
	}

	@Test
	public void testMultiThreadedParallelProcessRegisterCallable()
		throws Exception {

		Thread thread1 = new Thread(getFullProcessRunnable());
		Thread thread2 = new Thread(getFullProcessRunnable());

		thread1.start();
		thread2.start();

		thread1.join();
		thread2.join();
	}

	@Test
	public void testMultiThreadedSynchronizedNestedProcessRegisterCallable()
		throws Exception {

		startProcess(_processId);

		MockExportImportProcessCallable callable1 =
			new MockExportImportProcessCallable();

		_exportImportProcessCallbackRegistry.registerCallback(
			_processId, callable1);

		CountDownLatch countDownLatch = new CountDownLatch(1);

		Thread thread = new Thread(getFullProcessRunnable(countDownLatch));

		thread.start();

		countDownLatch.await(1, TimeUnit.MINUTES);

		Assert.assertFalse(callable1.hasBeenCalled.get());

		stopProcess(_processId);

		Assert.assertTrue(callable1.hasBeenCalled.get());
	}

	@Test
	public void testMultiThreadedSynchronizedProcessRegisterCallable()
		throws Exception {

		MockExportImportProcessCallable callable =
			new MockExportImportProcessCallable();

		CountDownLatch startedLatch = new CountDownLatch(1);
		CountDownLatch registeredLatch = new CountDownLatch(1);
		CountDownLatch stoppedLatch = new CountDownLatch(1);

		Thread starterThread = new Thread(
			() -> {
				startProcess(_processId);

				startedLatch.countDown();
			});

		Thread stopperThread = new Thread(
			() -> {
				stopProcess(_processId);

				stoppedLatch.countDown();
			});

		Thread registrationThread = new Thread(
			() -> {
				_exportImportProcessCallbackRegistry.registerCallback(
					_processId, callable);

				registeredLatch.countDown();
			});

		starterThread.start();

		startedLatch.await(1, TimeUnit.MINUTES);

		Assert.assertFalse(callable.hasBeenCalled.get());

		registrationThread.start();

		registeredLatch.await(1, TimeUnit.MINUTES);

		Assert.assertFalse(callable.hasBeenCalled.get());

		stopperThread.start();

		stoppedLatch.await(1, TimeUnit.MINUTES);

		Assert.assertTrue(callable.hasBeenCalled.get());
	}

	@Test
	public void testMultiThreadedSynchronizedSelfNestedProcessRegisterCallable()
		throws Exception {

		startProcess(_processId);

		MockExportImportProcessCallable callable1 =
			new MockExportImportProcessCallable();

		_exportImportProcessCallbackRegistry.registerCallback(
			_processId, callable1);

		CountDownLatch countDownLatch = new CountDownLatch(1);

		Thread thread = new Thread(
			getFullProcessRunnable(_processId, countDownLatch));

		thread.start();

		countDownLatch.await(1, TimeUnit.MINUTES);

		Assert.assertFalse(callable1.hasBeenCalled.get());

		stopProcess(_processId);

		Assert.assertTrue(callable1.hasBeenCalled.get());
	}

	@Test
	public void testSingleThreadedNestedProcessRegisterCallable() {
		startProcess(_processId);

		MockExportImportProcessCallable callable =
			new MockExportImportProcessCallable();

		_exportImportProcessCallbackRegistry.registerCallback(
			_processId, callable);

		String nestedProcessId = RandomTestUtil.randomString();

		startProcess(nestedProcessId);

		MockExportImportProcessCallable callableNested =
			new MockExportImportProcessCallable();

		_exportImportProcessCallbackRegistry.registerCallback(
			nestedProcessId, callableNested);

		Assert.assertFalse(callableNested.hasBeenCalled.get());

		stopProcess(nestedProcessId);

		Assert.assertTrue(callableNested.hasBeenCalled.get());
		Assert.assertFalse(callable.hasBeenCalled.get());

		stopProcess(_processId);

		Assert.assertTrue(callable.hasBeenCalled.get());
	}

	@Test
	public void testSingleThreadedProcessRegisterMultipleCallables() {
		startProcess(_processId);

		MockExportImportProcessCallable callable1 =
			new MockExportImportProcessCallable();

		MockExportImportProcessCallable callable2 =
			new MockExportImportProcessCallable();

		_exportImportProcessCallbackRegistry.registerCallback(
			_processId, callable1);

		_exportImportProcessCallbackRegistry.registerCallback(
			_processId, callable2);

		Assert.assertFalse(callable1.hasBeenCalled.get());
		Assert.assertFalse(callable2.hasBeenCalled.get());

		stopProcess(_processId);

		Assert.assertTrue(callable1.hasBeenCalled.get());
		Assert.assertTrue(callable2.hasBeenCalled.get());
	}

	@Test
	public void testSingleThreadedSelfNestedProcessRegisterCallable() {
		startProcess(_processId);

		MockExportImportProcessCallable callable =
			new MockExportImportProcessCallable();

		_exportImportProcessCallbackRegistry.registerCallback(
			_processId, callable);

		startProcess(_processId);

		MockExportImportProcessCallable callableNested =
			new MockExportImportProcessCallable();

		_exportImportProcessCallbackRegistry.registerCallback(
			_processId, callableNested);

		Assert.assertFalse(callableNested.hasBeenCalled.get());

		stopProcess(_processId);

		Assert.assertTrue(callableNested.hasBeenCalled.get());
		Assert.assertFalse(callable.hasBeenCalled.get());

		stopProcess(_processId);

		Assert.assertTrue(callable.hasBeenCalled.get());
	}

	protected Runnable getFullProcessRunnable() {
		return getFullProcessRunnable(RandomTestUtil.randomString(), null);
	}

	protected Runnable getFullProcessRunnable(CountDownLatch countDownLatch) {
		return getFullProcessRunnable(
			RandomTestUtil.randomString(), countDownLatch);
	}

	protected Runnable getFullProcessRunnable(
		String processId, CountDownLatch countDownLatch) {

		return () -> {
			startProcess(processId);

			MockExportImportProcessCallable callable =
				new MockExportImportProcessCallable();

			_exportImportProcessCallbackRegistry.registerCallback(
				processId, callable);

			Assert.assertFalse(callable.hasBeenCalled.get());

			stopProcess(processId);

			Assert.assertTrue(callable.hasBeenCalled.get());

			if (countDownLatch != null) {
				countDownLatch.countDown();
			}
		};
	}

	protected void startProcess(String processId) {
		ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
			ExportImportLifecycleConstants.
				EVENT_PUBLICATION_LAYOUT_LOCAL_STARTED,
			ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_STAGING_IN_PROCESS,
			processId);
	}

	protected void stopProcess(String processId) {
		ExportImportLifecycleManagerUtil.fireExportImportLifecycleEvent(
			ExportImportLifecycleConstants.
				EVENT_PUBLICATION_LAYOUT_LOCAL_SUCCEEDED,
			ExportImportLifecycleConstants.
				PROCESS_FLAG_LAYOUT_STAGING_IN_PROCESS,
			processId);
	}

	@Inject
	private ExportImportProcessCallbackRegistry
		_exportImportProcessCallbackRegistry;

	private String _processId;

	private class MockExportImportProcessCallable implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			Assert.assertFalse(hasBeenCalled.get());

			hasBeenCalled.set(Boolean.TRUE);

			return null;
		}

		public void reset() {
			hasBeenCalled.set(Boolean.FALSE);
		}

		public AtomicBoolean hasBeenCalled = new AtomicBoolean(false);

	}

}