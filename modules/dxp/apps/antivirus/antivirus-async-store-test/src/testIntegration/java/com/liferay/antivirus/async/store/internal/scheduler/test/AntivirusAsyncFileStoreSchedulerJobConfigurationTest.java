/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.antivirus.async.store.internal.scheduler.test;

import com.liferay.antivirus.async.store.configuration.AntivirusAsyncConfiguration;
import com.liferay.antivirus.async.store.constants.AntivirusAsyncDestinationNames;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.antivirus.AntivirusScanner;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.document.library.kernel.store.DLStore;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayInputStream;

import java.util.Dictionary;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Istvan Sajtos
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class AntivirusAsyncFileStoreSchedulerJobConfigurationTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule integrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_antivirusScannerServiceRegistration = _bundleContext.registerService(
			AntivirusScanner.class,
			(AntivirusScanner)ProxyUtil.newProxyInstance(
				AntivirusScanner.class.getClassLoader(),
				new Class<?>[] {AntivirusScanner.class},
				(proxy, method, args) -> method.getDefaultValue()),
			null);

		_configuration = _configurationAdmin.getConfiguration(
			AntivirusAsyncConfiguration.class.getName(), "?");

		_properties = _configuration.getProperties();

		_configuration.update(
			HashMapDictionaryBuilder.<String, Object>put(
				"batchScanCronExpression", "0 0 23 * * ?"
			).put(
				"maximumQueueSize", 0
			).put(
				"retryCronExpression", "0 0/5 * * * ?"
			).build());

		_group = GroupTestUtil.addGroup();
		_schedulerJobConfiguration = _getService(
			SchedulerJobConfiguration.class.getName(),
			"com.liferay.antivirus.async.store.internal.scheduler." +
				"AntivirusAsyncFileStoreSchedulerJobConfiguration");
	}

	@After
	public void tearDown() throws Exception {
		_antivirusScannerServiceRegistration.unregister();

		_configuration.update(_properties);
	}

	@Test(expected = Test.None.class)
	public void testRun() throws Exception {
		UnsafeRunnable<Exception> jobExecutorUnsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		jobExecutorUnsafeRunnable.run();
	}

	@Test
	public void testScan() throws Exception {
		_testScan();

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"DL_STORE_IMPL", _CLASS_NAME_DB_STORE);
			SafeCloseable safeCloseable2 =
				_updateAntivirusAsyncDLStoreWithSafeCloseable()) {

			_testScan();
		}
	}

	private void _assertFileScanned(String fileName) throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(1);

		TestMessageListener testMessageListener = new TestMessageListener(
			countDownLatch, fileName);

		ServiceRegistration<MessageListener> serviceRegistration =
			_bundleContext.registerService(
				MessageListener.class, testMessageListener,
				HashMapDictionaryBuilder.<String, Object>put(
					"destination.name", AntivirusAsyncDestinationNames.ANTIVIRUS
				).put(
					"service.ranking", Integer.MAX_VALUE
				).build());

		UnsafeRunnable<Exception> jobExecutorUnsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		try {
			jobExecutorUnsafeRunnable.run();

			countDownLatch.await(10, TimeUnit.SECONDS);

			Assert.assertTrue(testMessageListener._fileScanned);
		}
		finally {
			serviceRegistration.unregister();
		}
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

	private void _testScan() throws Exception {
		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			dlFolder.getRepositoryId(), dlFolder.getFolderId(),
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_assertFileScanned(dlFileEntry.getName());

		dlFileEntry = DLFileEntryLocalServiceUtil.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new ByteArrayInputStream(TestDataConstants.TEST_BYTE_ARRAY),
			TestDataConstants.TEST_BYTE_ARRAY.length, null, null, null,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		_assertFileScanned(dlFileEntry.getName());
	}

	private SafeCloseable _updateAntivirusAsyncDLStoreWithSafeCloseable()
		throws Exception {

		DLStore dlStore = _getService(
			DLStore.class.getName(),
			"com.liferay.antivirus.async.store.internal.AntivirusAsyncDLStore");

		Store store = ReflectionTestUtil.getAndSetFieldValue(
			dlStore, "_store", _dbStore);

		return () -> ReflectionTestUtil.setFieldValue(dlStore, "_store", store);
	}

	private static final String _CLASS_NAME_DB_STORE =
		"com.liferay.portal.store.db.DBStore";

	private static final int _SLEEP_TIME = 2000;

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private ServiceRegistration<AntivirusScanner>
		_antivirusScannerServiceRegistration;
	private Configuration _configuration;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject(filter = "store.type=" + _CLASS_NAME_DB_STORE)
	private Store _dbStore;

	@DeleteAfterTestRun
	private Group _group;

	private Dictionary<String, Object> _properties;
	private SchedulerJobConfiguration _schedulerJobConfiguration;

	private static class TestMessageListener implements MessageListener {

		public TestMessageListener(
			CountDownLatch countDownLatch, String fileName) {

			_countDownLatch = countDownLatch;
			_fileName = fileName;
		}

		@Override
		public void receive(Message message) throws MessageListenerException {
			String fileName = (String)message.get("fileName");

			if (fileName.equals(_fileName)) {
				_fileScanned = true;
				_countDownLatch.countDown();
			}
		}

		private final CountDownLatch _countDownLatch;
		private final String _fileName;
		private boolean _fileScanned;

	}

}