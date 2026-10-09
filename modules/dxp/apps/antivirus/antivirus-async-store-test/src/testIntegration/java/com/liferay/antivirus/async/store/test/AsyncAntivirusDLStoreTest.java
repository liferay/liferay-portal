/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.test;

import com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration;
import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.antivirus.async.store.event.AntivirusAsyncEvent;
import com.liferay.antivirus.async.store.event.AntivirusAsyncEventListener;
import com.liferay.antivirus.async.store.jmx.AntivirusAsyncStatisticsManagerMBean;
import com.liferay.antivirus.async.store.retry.AntivirusAsyncRetryScheduler;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.antivirus.AntivirusScanner;
import com.liferay.document.library.kernel.antivirus.AntivirusScannerException;
import com.liferay.document.library.kernel.antivirus.AntivirusVirusFoundException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.petra.concurrent.NoticeableThreadPoolExecutor;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.DynamicMBean;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Raymond Augé
 * @author Jorge Garcia
 * @author Alvaro Saugar
 */
@RunWith(Arquillian.class)
public class AsyncAntivirusDLStoreTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() {
		Bundle bundle = FrameworkUtil.getBundle(
			AsyncAntivirusDLStoreTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Test
	public void testEventMissing() throws Exception {
		AtomicBoolean calledScan = new AtomicBoolean();

		AtomicBoolean firedEventMissing = new AtomicBoolean();

		_registerService(
			AntivirusAsyncEventListener.class,
			_create(
				HashMapBuilder.<AntivirusAsyncEvent, Runnable>put(
					AntivirusAsyncEvent.MISSING,
					() -> firedEventMissing.set(true)
				).build()),
			null);

		_registerService(
			AntivirusScanner.class,
			new MockAntivirusScanner(() -> calledScan.set(true)), null);

		_withAsyncAntivirusConfiguration(
			"0 0/1 * * * ?", 1, true,
			() -> {
				_messageBus.sendMessage(
					AntivirusAsyncDestinationNames.ANTIVIRUS,
					new Message() {
						{
							put("companyId", 0);
							put("fileName", RandomTestUtil.randomString());
							put("repositoryId", 0);
							put("versionLabel", RandomTestUtil.randomString());
						}
					});

				Assert.assertFalse(calledScan.get());
				Assert.assertTrue(firedEventMissing.get());
			});
	}

	@Test
	public void testEventProcessingError() throws Exception {
		AtomicBoolean calledSchedule = new AtomicBoolean();

		AtomicBoolean firedEventPrepare = new AtomicBoolean();
		AtomicBoolean firedEventProcessingError = new AtomicBoolean();

		_registerService(
			AntivirusAsyncEventListener.class,
			_create(
				HashMapBuilder.<AntivirusAsyncEvent, Runnable>put(
					AntivirusAsyncEvent.PREPARE,
					() -> firedEventPrepare.set(true)
				).put(
					AntivirusAsyncEvent.PROCESSING_ERROR,
					() -> firedEventProcessingError.set(true)
				).build()),
			MapUtil.singletonDictionary(Constants.SERVICE_RANKING, -100));

		_registerService(
			AntivirusAsyncRetryScheduler.class,
			new AntivirusAsyncRetryScheduler() {

				@Override
				public void schedule(Message message) {
					calledSchedule.set(true);
				}

				@Override
				public void unschedule(Message message) {
				}

			},
			MapUtil.singletonDictionary(Constants.SERVICE_RANKING, 100));
		_registerService(
			AntivirusScanner.class,
			new MockAntivirusScanner(
				() -> {
					throw new AntivirusScannerException(
						AntivirusScannerException.PROCESS_FAILURE);
				}),
			null);

		_withAsyncAntivirusConfiguration(
			"0 0/1 * * * ?", 1, true,
			() -> {
				DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

				DLTestUtil.addDLFileEntry(dlFolder.getFolderId());

				Assert.assertTrue(calledSchedule.get());
				Assert.assertTrue(firedEventPrepare.get());
				Assert.assertTrue(firedEventProcessingError.get());
			});
	}

	@Test
	public void testEventSizeExceeded() throws Exception {
		AtomicBoolean calledScan = new AtomicBoolean();

		AtomicBoolean firedEventPrepare = new AtomicBoolean();
		AtomicBoolean firedEventSizeExceeded = new AtomicBoolean();

		_registerService(
			AntivirusAsyncEventListener.class,
			_create(
				HashMapBuilder.<AntivirusAsyncEvent, Runnable>put(
					AntivirusAsyncEvent.PREPARE,
					() -> firedEventPrepare.set(true)
				).put(
					AntivirusAsyncEvent.SIZE_EXCEEDED,
					() -> firedEventSizeExceeded.set(true)
				).build()),
			null);

		_registerService(
			AntivirusScanner.class,
			new MockAntivirusScanner(
				() -> {
					calledScan.set(true);

					throw new AntivirusScannerException(
						AntivirusScannerException.SIZE_LIMIT_EXCEEDED);
				}),
			null);

		_withAsyncAntivirusConfiguration(
			"0 0/1 * * * ?", 1, true,
			() -> {
				DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

				DLTestUtil.addDLFileEntry(dlFolder.getFolderId());

				Assert.assertTrue(calledScan.get());
				Assert.assertTrue(firedEventPrepare.get());
				Assert.assertTrue(firedEventSizeExceeded.get());
			});
	}

	@Test
	public void testEventSuccess() throws Exception {
		AtomicBoolean calledScan = new AtomicBoolean();

		AtomicBoolean firedEventPrepare = new AtomicBoolean();
		AtomicBoolean firedEventSuccess = new AtomicBoolean();

		_registerService(
			AntivirusAsyncEventListener.class,
			_create(
				HashMapBuilder.<AntivirusAsyncEvent, Runnable>put(
					AntivirusAsyncEvent.PREPARE,
					() -> firedEventPrepare.set(true)
				).put(
					AntivirusAsyncEvent.SUCCESS,
					() -> firedEventSuccess.set(true)
				).build()),
			null);

		_registerService(
			AntivirusScanner.class,
			new MockAntivirusScanner(() -> calledScan.set(true)), null);

		_withAsyncAntivirusConfiguration(
			"0 0/1 * * * ?", 1, true,
			() -> {
				DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

				DLTestUtil.addDLFileEntry(dlFolder.getFolderId());

				Assert.assertTrue(calledScan.get());
				Assert.assertTrue(firedEventPrepare.get());
				Assert.assertTrue(firedEventSuccess.get());
			});
	}

	@Test
	public void testEventVirusFound() throws Exception {
		AtomicBoolean calledScan = new AtomicBoolean();

		AtomicBoolean firedEventPrepare = new AtomicBoolean();
		AtomicBoolean firedEventVirusFound = new AtomicBoolean();

		_registerService(
			AntivirusAsyncEventListener.class,
			_create(
				HashMapBuilder.<AntivirusAsyncEvent, Runnable>put(
					AntivirusAsyncEvent.PREPARE,
					() -> firedEventPrepare.set(true)
				).put(
					AntivirusAsyncEvent.VIRUS_FOUND,
					() -> firedEventVirusFound.set(true)
				).build()),
			null);

		_registerService(
			AntivirusScanner.class,
			new MockAntivirusScanner(
				() -> {
					calledScan.set(true);

					throw new AntivirusVirusFoundException(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString());
				}),
			null);

		_withAsyncAntivirusConfiguration(
			"0 0/1 * * * ?", 1, true,
			() -> {
				DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

				DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
					dlFolder.getFolderId());

				Assert.assertTrue(calledScan.get());
				Assert.assertTrue(firedEventPrepare.get());
				Assert.assertTrue(firedEventVirusFound.get());
				Assert.assertNull(
					_dlFileEntryLocalService.fetchDLFileEntry(
						dlFileEntry.getFileEntryId()));
				Assert.assertTrue(
					ArrayUtil.isEmpty(
						_store.getFileVersions(
							dlFileEntry.getCompanyId(),
							dlFileEntry.getDataRepositoryId(),
							dlFileEntry.getName())));
			});
	}

	@Test
	public void testFileEntryUpdatedWithVirusThenFileVersionDeleted()
		throws Exception {

		_testScanUpdatedFileEntry();

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DL_STORE_IMPL", _CLASS_NAME_DB_STORE);
			SafeCloseable safeCloseable2 =
				_updateAntivirusAsyncDLStoreWithSafeCloseable()) {

			Thread.sleep(_SLEEP_TIME);

			_testScanUpdatedFileEntry();
		}
	}

	@Test
	public void testQueueOverflow() throws Exception {
		AtomicInteger calledSchedule = new AtomicInteger();

		AtomicInteger firedEventPrepare = new AtomicInteger();

		_registerService(
			AntivirusAsyncEventListener.class,
			_create(
				HashMapBuilder.<AntivirusAsyncEvent, Runnable>put(
					AntivirusAsyncEvent.PREPARE,
					firedEventPrepare::incrementAndGet
				).put(
					AntivirusAsyncEvent.SUCCESS,
					() -> {
					}
				).build()),
			MapUtil.singletonDictionary(Constants.SERVICE_RANKING, -100));

		_registerService(
			AntivirusAsyncRetryScheduler.class,
			new AntivirusAsyncRetryScheduler() {

				@Override
				public void schedule(Message message) {
					calledSchedule.incrementAndGet();
				}

				@Override
				public void unschedule(Message message) {
				}

			},
			MapUtil.singletonDictionary(Constants.SERVICE_RANKING, 100));
		_registerService(
			AntivirusScanner.class,
			new MockAntivirusScanner(
				() -> {
					try {
						Thread.sleep(Long.MAX_VALUE);
					}
					catch (InterruptedException interruptedException) {
					}
				}),
			null);

		_withAsyncAntivirusConfiguration(
			"0 0/10 * * * ?", 1, false,
			() -> {
				DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

				int count = 10;

				try (LogCapture logCapture =
						LoggerTestUtil.configureLog4JLogger(
							"com.liferay.antivirus.async.store.internal." +
								"messaging.AntivirusAsyncMessageListener",
							LoggerTestUtil.DEBUG)) {

					for (int i = count; i > 0; i--) {
						DLTestUtil.addDLFileEntry(dlFolder.getFolderId());
					}

					List<LogEntry> logEntries = logCapture.getLogEntries();

					for (LogEntry logEntry : logEntries) {
						String message = logEntry.getMessage();

						Assert.assertTrue(
							message.contains(
								"into persistent storage because the async " +
									"antivirus queue is overflowing"));
					}
				}

				Assert.assertTrue(
					String.valueOf(calledSchedule.get()),
					calledSchedule.get() > 0);
				Assert.assertEquals(count, firedEventPrepare.get());
			});

		String[] fileNames = FileUtil.listFiles(
			SystemProperties.get(SystemProperties.TMP_DIR));

		long count = fileNames.length;

		_withAsyncAntivirusConfiguration(
			"0 0/10 * * * ?", 0, false,
			() -> {
				DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

				for (int i = 10; i > 0; i--) {
					DLTestUtil.addDLFileEntry(dlFolder.getFolderId());
				}
			});

		fileNames = FileUtil.listFiles(
			SystemProperties.get(SystemProperties.TMP_DIR));

		Assert.assertEquals(
			Arrays.toString(fileNames), count, fileNames.length);
	}

	@Test
	public void testStatistics() throws Exception {
		AtomicInteger firedEventPrepare = new AtomicInteger();
		AtomicInteger firedEventProcessingError = new AtomicInteger();
		AtomicInteger firedEventSizeExceeded = new AtomicInteger();
		AtomicInteger firedEventSuccess = new AtomicInteger();
		AtomicInteger firedEventVirusFound = new AtomicInteger();

		Random random = new Random();

		_registerService(
			AntivirusAsyncEventListener.class,
			_create(
				HashMapBuilder.<AntivirusAsyncEvent, Runnable>put(
					AntivirusAsyncEvent.PREPARE,
					firedEventPrepare::incrementAndGet
				).put(
					AntivirusAsyncEvent.PROCESSING_ERROR,
					firedEventProcessingError::incrementAndGet
				).put(
					AntivirusAsyncEvent.SIZE_EXCEEDED,
					firedEventSizeExceeded::incrementAndGet
				).put(
					AntivirusAsyncEvent.SUCCESS,
					firedEventSuccess::incrementAndGet
				).put(
					AntivirusAsyncEvent.VIRUS_FOUND,
					firedEventVirusFound::incrementAndGet
				).build()),
			MapUtil.singletonDictionary(Constants.SERVICE_RANKING, -100));
		_registerService(
			AntivirusAsyncRetryScheduler.class,
			ProxyFactory.newDummyInstance(AntivirusAsyncRetryScheduler.class),
			MapUtil.singletonDictionary(Constants.SERVICE_RANKING, 100));
		_registerService(
			AntivirusScanner.class,
			new MockAntivirusScanner(
				() -> {
					int choice = random.nextInt(4);

					if (choice == 1) {
						throw new AntivirusVirusFoundException(
							RandomTestUtil.randomString(),
							RandomTestUtil.randomString());
					}
					else if (choice == 2) {
						throw new AntivirusScannerException(
							AntivirusScannerException.SIZE_LIMIT_EXCEEDED);
					}
					else if (choice == 3) {
						throw new AntivirusScannerException(
							AntivirusScannerException.PROCESS_FAILURE);
					}
				}),
			MapUtil.singletonDictionary(Constants.SERVICE_RANKING, 100));

		_withAsyncAntivirusConfiguration(
			"0 0/10 * * * ?", 5, true,
			() -> {
				ServiceReference<?>[] serviceReferences =
					_bundleContext.getServiceReferences(
						DynamicMBean.class.getName(),
						"(component.name=" +
							"com.liferay.antivirus.async.store.jmx." +
								"AntivirusAsyncStatisticsManager)");

				AntivirusAsyncStatisticsManagerMBean
					antivirusAsyncStatisticsManagerMBean =
						(AntivirusAsyncStatisticsManagerMBean)
							_bundleContext.getService(serviceReferences[0]);

				Assert.assertNotNull(antivirusAsyncStatisticsManagerMBean);

				antivirusAsyncStatisticsManagerMBean.refresh();

				int count = 100;

				DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

				for (int i = count; i > 0; i--) {
					DLTestUtil.addDLFileEntry(dlFolder.getFolderId());
				}

				Assert.assertEquals(count, firedEventPrepare.get());
				Assert.assertEquals(
					firedEventProcessingError.get(),
					antivirusAsyncStatisticsManagerMBean.
						getProcessingErrorCount());
				Assert.assertEquals(
					firedEventSizeExceeded.get(),
					antivirusAsyncStatisticsManagerMBean.
						getSizeExceededCount());
				Assert.assertEquals(
					firedEventSuccess.get() + firedEventVirusFound.get(),
					antivirusAsyncStatisticsManagerMBean.
						getTotalScannedCount());
				Assert.assertEquals(
					firedEventVirusFound.get(),
					antivirusAsyncStatisticsManagerMBean.getVirusFoundCount());
			});
	}

	private AntivirusAsyncEventListener _create(
		Map<AntivirusAsyncEvent, Runnable> runnables) {

		return message -> {
			AntivirusAsyncEvent antivirusAsyncEvent =
				(AntivirusAsyncEvent)message.get("antivirusAsyncEvent");

			Runnable runnable = runnables.get(antivirusAsyncEvent);

			runnable.run();
		};
	}

	private <T> T _getService(String className, String componentName)
		throws Exception {

		String filterString = "(component.name=" + componentName + ")";

		ServiceReference<T>[] serviceReferences = null;

		int waitTime = 0;

		while (ArrayUtil.isEmpty(serviceReferences)) {
			serviceReferences =
				(ServiceReference<T>[])_bundleContext.getServiceReferences(
					className, filterString);

			if (waitTime >= TestPropsValues.CI_TEST_TIMEOUT_TIME) {
				throw new IllegalStateException(
					StringBundler.concat(
						"Timed out while waiting for service ", className, " ",
						filterString));
			}

			if (serviceReferences != null) {
				continue;
			}

			Thread.sleep(_SLEEP_TIME);

			waitTime += _SLEEP_TIME;
		}

		return _bundleContext.getService(serviceReferences[0]);
	}

	private <S> SafeCloseable _registerService(
		Class<S> clazz, S service, Dictionary<String, ?> properties) {

		ServiceRegistration<?> serviceRegistration =
			_bundleContext.registerService(clazz, service, properties);

		_serviceRegistrations.add(serviceRegistration);

		return () -> {
			_serviceRegistrations.remove(serviceRegistration);

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		};
	}

	private SafeCloseable _sync() {
		Destination destination = MessageBusUtil.getDestination(
			AntivirusAsyncDestinationNames.ANTIVIRUS);

		Object originalNoticeableThreadPoolExecutor =
			ReflectionTestUtil.getAndSetFieldValue(
				destination, "_noticeableThreadPoolExecutor",
				_syncNoticeableThreadPoolExecutor);

		return new SafeCloseable() {

			@Override
			public void close() {
				ReflectionTestUtil.setFieldValue(
					destination, "_noticeableThreadPoolExecutor",
					originalNoticeableThreadPoolExecutor);
			}

		};
	}

	private void _testScanUpdatedFileEntry() throws Exception {
		AtomicBoolean firedEventPrepare = new AtomicBoolean();
		AtomicBoolean firedEventSuccess = new AtomicBoolean();
		AtomicBoolean firedEventVirusFound = new AtomicBoolean();
		AtomicInteger scanCount = new AtomicInteger(0);

		try (SafeCloseable closeable1 = _registerService(
				AntivirusAsyncEventListener.class,
				_create(
					HashMapBuilder.<AntivirusAsyncEvent, Runnable>put(
						AntivirusAsyncEvent.MISSING,
						() -> firedEventSuccess.set(true)
					).put(
						AntivirusAsyncEvent.PREPARE,
						() -> firedEventPrepare.set(true)
					).put(
						AntivirusAsyncEvent.PROCESSING_ERROR,
						() -> firedEventSuccess.set(true)
					).put(
						AntivirusAsyncEvent.SUCCESS,
						() -> firedEventSuccess.set(true)
					).put(
						AntivirusAsyncEvent.VIRUS_FOUND,
						() -> firedEventVirusFound.set(true)
					).build()),
				MapUtil.singletonDictionary(Constants.SERVICE_RANKING, -100));
			SafeCloseable closeable2 = _registerService(
				AntivirusAsyncRetryScheduler.class,
				ProxyFactory.newDummyInstance(
					AntivirusAsyncRetryScheduler.class),
				MapUtil.singletonDictionary(Constants.SERVICE_RANKING, 100));
			SafeCloseable closeable3 = _registerService(
				AntivirusScanner.class,
				new MockAntivirusScanner(
					() -> {
						int currentScan = scanCount.getAndIncrement();

						if (currentScan > 0) {
							throw new AntivirusVirusFoundException(
								RandomTestUtil.randomString(),
								RandomTestUtil.randomString());
						}
					}),
				MapUtil.singletonDictionary(Constants.SERVICE_RANKING, 100))) {

			_withAsyncAntivirusConfiguration(
				"0 0/1 * * * ?", 2, true,
				() -> {
					DLFolder dlFolder = DLTestUtil.addDLFolder(
						_group.getGroupId());

					DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
						dlFolder.getFolderId());

					dlFileEntry = _dlFileEntryLocalService.updateStatus(
						TestPropsValues.getUserId(), dlFileEntry,
						dlFileEntry.getLatestFileVersion(true),
						WorkflowConstants.STATUS_APPROVED,
						ServiceContextTestUtil.getServiceContext(
							_group, TestPropsValues.getUserId()),
						Collections.emptyMap());

					Assert.assertTrue(firedEventSuccess.get());
					Assert.assertFalse(firedEventVirusFound.get());

					_dlAppService.updateFileEntry(
						dlFileEntry.getFileEntryId(), dlFileEntry.getFileName(),
						dlFileEntry.getMimeType(), dlFileEntry.getTitle(),
						dlFileEntry.getTitle(), dlFileEntry.getDescription(),
						null, DLVersionNumberIncrease.MAJOR,
						TestDataConstants.TEST_BYTE_ARRAY, new Date(), null,
						null,
						ServiceContextTestUtil.getServiceContext(
							dlFolder.getGroupId()));

					Assert.assertTrue(firedEventVirusFound.get());

					DLFileEntry updatedFileEntry =
						_dlFileEntryLocalService.fetchDLFileEntry(
							dlFileEntry.getFileEntryId());

					Assert.assertNotNull(updatedFileEntry);
					Assert.assertEquals("1.0", updatedFileEntry.getVersion());

					Assert.assertFalse(
						ArrayUtil.isEmpty(
							_store.getFileVersions(
								dlFileEntry.getCompanyId(),
								dlFileEntry.getDataRepositoryId(),
								dlFileEntry.getName())));
				});
		}
	}

	private SafeCloseable _updateAntivirusAsyncDLStoreWithSafeCloseable()
		throws Exception {

		_configuration = _configurationAdmin.getConfiguration(
			AntivirusAsyncConfiguration.class.getName(), "?");

		_configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				"batchScanCronExpression", "0 0 * 5 * ?"
			).put(
				"maximumQueueSize", 1
			).put(
				"retryCronExpression", "0 0/5 * * * ?"
			).build());

		DLStore dlStore = _getService(
			DLStore.class.getName(),
			"com.liferay.antivirus.async.store.internal.AntivirusAsyncDLStore");

		Store store = ReflectionTestUtil.getAndSetFieldValue(
			dlStore, "_store", _dbStore);

		return () -> ReflectionTestUtil.setFieldValue(dlStore, "_store", store);
	}

	private void _withAsyncAntivirusConfiguration(
			String batchScanCronExpression, int maximumQueueSize, boolean sync,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					AntivirusAsyncConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"batch-scan-cron-expression", batchScanCronExpression
					).put(
						"maximumQueueSize", maximumQueueSize
					).build())) {

			if (sync) {
				try (SafeCloseable safeCloseable = _sync()) {
					unsafeRunnable.run();
				}
			}
			else {
				unsafeRunnable.run();
			}
		}
	}

	private static final String _CLASS_NAME_DB_STORE =
		"com.liferay.portal.store.db.DBStore";

	private static final int _SLEEP_TIME = 2000;

	private static BundleContext _bundleContext;

	private Configuration _configuration;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject(filter = "store.type=" + _CLASS_NAME_DB_STORE)
	private Store _dbStore;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MessageBus _messageBus;

	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

	@Inject
	private Store _store;

	private final NoticeableThreadPoolExecutor
		_syncNoticeableThreadPoolExecutor = new NoticeableThreadPoolExecutor(
			1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1),
			Executors.defaultThreadFactory(),
			new ThreadPoolExecutor.AbortPolicy(),
			new ThreadPoolHandlerAdapter()) {

			@Override
			public void execute(Runnable runnable) {
				runnable.run();
			}

		};

	private class MockAntivirusScanner implements AntivirusScanner {

		public MockAntivirusScanner(
			UnsafeRunnable<AntivirusScannerException> unsafeRunnable) {

			_unsafeRunnable = unsafeRunnable;
		}

		@Override
		public boolean isActive() {
			return true;
		}

		@Override
		public void scan(byte[] bytes) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void scan(File file) throws AntivirusScannerException {
			_unsafeRunnable.run();
		}

		@Override
		public void scan(InputStream inputStream)
			throws AntivirusScannerException {

			_unsafeRunnable.run();
		}

		private final UnsafeRunnable<AntivirusScannerException> _unsafeRunnable;

	}

}