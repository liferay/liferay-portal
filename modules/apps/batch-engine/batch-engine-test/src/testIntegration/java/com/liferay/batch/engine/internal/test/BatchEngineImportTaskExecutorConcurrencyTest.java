/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Allen Ziegenfus
 */
@RunWith(Arquillian.class)
public class BatchEngineImportTaskExecutorConcurrencyTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			BatchEngineImportTaskExecutorConcurrencyTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			BatchEngineTaskItemDelegate.class.getName(),
			new PrototypeServiceFactory<TestBatchEngineTaskItemDelegate>() {

				@Override
				public TestBatchEngineTaskItemDelegate getService(
					Bundle bundle,
					ServiceRegistration<TestBatchEngineTaskItemDelegate>
						serviceRegistration) {

					return new TestBatchEngineTaskItemDelegate();
				}

				@Override
				public void ungetService(
					Bundle bundle,
					ServiceRegistration<TestBatchEngineTaskItemDelegate>
						serviceRegistration,
					TestBatchEngineTaskItemDelegate
						testBatchEngineTaskItemDelegate) {
				}

			},
			HashMapDictionaryBuilder.<String, Object>put(
				"batch.engine.task.item.delegate.name", _TASK_ITEM_DELEGATE_NAME
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testConcurrentImportTasksKeepTheirOwnContextUser()
		throws Exception {

		User user1 = TestPropsValues.getUser();

		_user = UserTestUtil.addUser();

		BatchEngineImportTask batchEngineImportTask1 =
			_addBatchEngineImportTask(
				"[{\"headline\": \"A1\"}, {\"headline\": \"A2\"}]", user1);
		BatchEngineImportTask batchEngineImportTask2 =
			_addBatchEngineImportTask("[{\"headline\": \"B1\"}]", _user);

		ExecutorService executorService = Executors.newFixedThreadPool(2);

		try {
			Future<?> future1 = executorService.submit(
				() -> _batchEngineImportTaskExecutor.execute(
					batchEngineImportTask1));

			Assert.assertTrue(
				_aStartedCountDownLatch.await(30, TimeUnit.SECONDS));

			Future<?> future2 = executorService.submit(
				() -> _batchEngineImportTaskExecutor.execute(
					batchEngineImportTask2));

			future2.get(60, TimeUnit.SECONDS);

			future1.get(60, TimeUnit.SECONDS);
		}
		finally {
			executorService.shutdownNow();
		}

		Assert.assertEquals(
			Long.valueOf(user1.getUserId()), _contextUserIds.get("A1"));
		Assert.assertEquals(
			Long.valueOf(_user.getUserId()), _contextUserIds.get("B1"));
		Assert.assertEquals(
			"Import task A processed its second item with the context user " +
				"of import task B",
			Long.valueOf(user1.getUserId()), _contextUserIds.get("A2"));
	}

	public class TestBatchEngineTaskItemDelegate
		extends BaseBatchEngineTaskItemDelegate<BlogPosting> {

		@Override
		public BlogPosting createItem(
				BlogPosting blogPosting, Map<String, Serializable> parameters)
			throws Exception {

			String headline = blogPosting.getHeadline();

			_contextUserIds.put(headline, contextUser.getUserId());

			if (headline.equals("A1")) {
				_aStartedCountDownLatch.countDown();

				_bStartedCountDownLatch.await(30, TimeUnit.SECONDS);
			}
			else if (headline.equals("B1")) {
				_bStartedCountDownLatch.countDown();
			}

			return blogPosting;
		}

		@Override
		public Page<BlogPosting> read(
				Filter filter, Pagination pagination, Sort[] sorts,
				Map<String, Serializable> parameters, String search)
			throws Exception {

			return null;
		}

	}

	private BatchEngineImportTask _addBatchEngineImportTask(
			String json, User user)
		throws Exception {

		return _batchEngineImportTaskLocalService.addBatchEngineImportTask(
			null, TestPropsValues.getCompanyId(), user.getUserId(), 1, null,
			BlogPosting.class.getName(), _compressContent(json.getBytes()),
			"JSON", BatchEngineTaskExecuteStatus.INITIAL.name(),
			Collections.emptyMap(),
			BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_CONTINUE,
			BatchEngineTaskOperation.CREATE.name(), new HashMap<>(),
			_TASK_ITEM_DELEGATE_NAME);
	}

	private byte[] _compressContent(byte[] content) throws Exception {
		try (ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream()) {

			try (ZipOutputStream zipOutputStream = new ZipOutputStream(
					byteArrayOutputStream)) {

				zipOutputStream.putNextEntry(new ZipEntry("import.json"));

				zipOutputStream.write(content, 0, content.length);
			}

			return byteArrayOutputStream.toByteArray();
		}
	}

	private static final String _TASK_ITEM_DELEGATE_NAME = "concurrency-test";

	private final CountDownLatch _aStartedCountDownLatch = new CountDownLatch(
		1);
	private final CountDownLatch _bStartedCountDownLatch = new CountDownLatch(
		1);

	@Inject
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	private final Map<String, Long> _contextUserIds = new ConcurrentHashMap<>();
	private ServiceRegistration<?> _serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

}