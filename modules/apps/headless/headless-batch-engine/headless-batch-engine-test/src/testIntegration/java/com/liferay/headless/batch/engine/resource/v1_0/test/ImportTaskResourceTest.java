/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BaseBatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.pagination.Page;
import com.liferay.batch.engine.pagination.Pagination;
import com.liferay.headless.batch.engine.client.dto.v1_0.FailedItem;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.entity.TestEntity;
import com.liferay.headless.batch.engine.util.ExportImportTaskUtil;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Map;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alberto Javier Moreno Lage
 * @author Vendel Toreki
 */
@RunWith(Arquillian.class)
public class ImportTaskResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testPostImportTask() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			ImportTask importTask = ExportImportTaskUtil.postImportTask(
				JSONUtil.putAll(
					JSONUtil.put("textValue", "test")
				).toString(),
				TestEntity.class.getName(), "FAILED",
				HashMapBuilder.put(
					"createStrategy", "INSERT"
				).put(
					"taskItemDelegateName",
					"export-import-task-resource-exception"
				).build());

			Assert.assertEquals(
				"Modified error message for TestEntity 'test'",
				importTask.getErrorMessage());
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal." +
					"BatchEngineImportTaskExecutorImpl",
				LoggerTestUtil.ERROR)) {

			JSONArray bodyJSONArray = JSONUtil.putAll(
				JSONUtil.put(
					"intValue", RandomTestUtil.randomInt()
				).put(
					"textValue", RandomTestUtil.randomString()
				),
				JSONUtil.put(
					"intValue", RandomTestUtil.randomInt()
				).put(
					"textValue", RandomTestUtil.randomString()
				),
				JSONUtil.put(
					"intValue", RandomTestUtil.randomInt()
				).put(
					"textValue", RandomTestUtil.randomString()
				));

			ImportTask importTask = ExportImportTaskUtil.postImportTask(
				bodyJSONArray.toString(), TestEntity.class.getName(),
				"COMPLETED",
				HashMapBuilder.put(
					"createStrategy", "INSERT"
				).put(
					"importStrategy", "ON_ERROR_CONTINUE"
				).put(
					"taskItemDelegateName",
					"export-import-task-resource-exception"
				).build());

			Assert.assertEquals(3, (int)importTask.getProcessedItemsCount());

			FailedItem[] failedItems = importTask.getFailedItems();

			Assert.assertEquals(
				Arrays.toString(failedItems), 3, failedItems.length);

			for (FailedItem failedItem : failedItems) {
				JSONObject jsonObject = (JSONObject)bodyJSONArray.get(
					failedItem.getItemIndex() - 1);

				Assert.assertEquals(
					"Modified error message for TestEntity '" +
						jsonObject.getString("textValue") + "'",
					failedItem.getMessage());
			}
		}
	}

	@Test
	public void testPostImportTaskWithExternalReferenceCodes()
		throws Exception {

		_testPostImportTaskWithExternalReferenceCodes(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			(batchExternalReferenceCode, externalReferenceCode, importTask) -> {
				Assert.assertEquals(
					batchExternalReferenceCode, _externalReferenceCode);
				Assert.assertEquals(
					externalReferenceCode,
					importTask.getExternalReferenceCode());
			});
		_testPostImportTaskWithExternalReferenceCodes(
			RandomTestUtil.randomString(), null,
			(batchExternalReferenceCode, externalReferenceCode, importTask) -> {
				Assert.assertEquals(
					batchExternalReferenceCode, _externalReferenceCode);
				Assert.assertNotEquals(
					batchExternalReferenceCode,
					importTask.getExternalReferenceCode());
			});
		_testPostImportTaskWithExternalReferenceCodes(
			null, RandomTestUtil.randomString(),
			(batchExternalReferenceCode, externalReferenceCode, importTask) -> {
				Assert.assertEquals(
					externalReferenceCode, _externalReferenceCode);
				Assert.assertEquals(
					externalReferenceCode,
					importTask.getExternalReferenceCode());
			});
		_testPostImportTaskWithExternalReferenceCodes(
			null, null,
			(batchExternalReferenceCode, externalReferenceCode, importTask) -> {
				Assert.assertNull(_externalReferenceCode);
				Assert.assertNotNull(importTask.getExternalReferenceCode());
			});
	}

	private void _testPostImportTaskWithExternalReferenceCodes(
			String batchExternalReferenceCode, String externalReferenceCode,
			UnsafeTriConsumer<String, String, ImportTask, Exception>
				unsafeTriConsumer)
		throws Exception {

		ImportTask importTask;

		try (BatchEngineTaskItemDelegateAutoCloseable
				batchEngineTaskItemDelegateAutoCloseable =
					new BatchEngineTaskItemDelegateAutoCloseable()) {

			importTask = ExportImportTaskUtil.postImportTask(
				JSONFactoryUtil.createJSONArray(
				).put(
					JSONUtil.put(
						"intValue", String.valueOf(RandomTestUtil.nextInt())
					).put(
						"textValue",
						StringUtil.getTitleCase(
							RandomTestUtil.randomString(
								8, UniqueStringRandomizerBumper.INSTANCE),
							true, "")
					)
				).toString(),
				"com.liferay.headless.batch.engine.entity.TestEntity",
				"COMPLETED",
				HashMapBuilder.put(
					"batchExternalReferenceCode", batchExternalReferenceCode
				).put(
					"createStrategy", "INSERT"
				).put(
					"externalReferenceCode", externalReferenceCode
				).put(
					"taskItemDelegateName",
					batchEngineTaskItemDelegateAutoCloseable.
						getTaskItemDelegateName()
				).build());
		}

		unsafeTriConsumer.accept(
			batchExternalReferenceCode, externalReferenceCode, importTask);
	}

	private String _externalReferenceCode;

	private class BatchEngineTaskItemDelegateAutoCloseable
		implements AutoCloseable {

		public BatchEngineTaskItemDelegateAutoCloseable() throws Exception {
			BundleContext bundleContext = SystemBundleUtil.getBundleContext();

			BatchEngineTaskItemDelegate<TestEntity>
				batchEngineTaskItemDelegate =
					new BaseBatchEngineTaskItemDelegate<>() {

						@Override
						public TestEntity createItem(
							TestEntity testEntity,
							Map<String, Serializable> parameters) {

							_externalReferenceCode = (String)parameters.get(
								"externalReferenceCode");

							return new TestEntity();
						}

						@Override
						public Page<TestEntity> read(
							Filter filter, Pagination pagination, Sort[] sorts,
							Map<String, Serializable> parameters,
							String search) {

							return null;
						}

					};

			_serviceRegistration = bundleContext.registerService(
				BatchEngineTaskItemDelegate.class, batchEngineTaskItemDelegate,
				HashMapDictionaryBuilder.<String, Object>put(
					"batch.engine.task.item.delegate.name",
					_TASK_ITEM_DELEGATE_NAME
				).build());
		}

		@Override
		public void close() {
			_serviceRegistration.unregister();
		}

		public String getTaskItemDelegateName() {
			return _TASK_ITEM_DELEGATE_NAME;
		}

		private static final String _TASK_ITEM_DELEGATE_NAME =
			RandomTestUtil.randomString();

		private final ServiceRegistration<?> _serviceRegistration;

	}

}