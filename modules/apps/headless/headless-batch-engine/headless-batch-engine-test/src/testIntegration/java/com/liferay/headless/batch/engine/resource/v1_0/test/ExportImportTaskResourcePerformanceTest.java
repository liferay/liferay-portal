/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskItemDelegateRegistry;
import com.liferay.headless.batch.engine.batch.engine.TestEntityBatchEngineTaskItemDelegate;
import com.liferay.headless.batch.engine.client.dto.v1_0.ExportTask;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.util.ExportImportTaskUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.test.performance.PerformanceTimer;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Closeable;
import java.io.InputStream;

import java.nio.file.Path;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vendel Toreki
 */
@RunWith(Arquillian.class)
public class ExportImportTaskResourcePerformanceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_jsons = LinkedHashMapBuilder.put(
			"com.liferay.headless.admin.user.dto.v1_0.UserAccount",
			JSONUtil.put(
				"additionalName", ""
			).put(
				"alternateName", "[$ALTERNATE_NAME$]"
			).put(
				"birthDate", "1977-01-01T00:00:00Z"
			).put(
				"customFields", JSONFactoryUtil.createJSONArray()
			).put(
				"dashboardURL", ""
			).put(
				"dateCreated", "2021-05-19T16:04:46Z"
			).put(
				"dateModified", "2021-05-19T16:04:46Z"
			).put(
				"emailAddress", "[$EMAIL_ADDRESS$]"
			).put(
				"familyName", "[$FAMILY_NAME$]"
			).put(
				"givenName", "[$GIVEN_NAME$]"
			).put(
				"jobTitle", ""
			).put(
				"keywords", JSONFactoryUtil.createJSONArray()
			).put(
				"name", "[$GIVEN_NAME$] [$FAMILY_NAME$]"
			).put(
				"organizationBriefs", JSONFactoryUtil.createJSONArray()
			).put(
				"profileURL", ""
			).put(
				"roleBriefs",
				JSONUtil.put(
					JSONUtil.put(
						"id", 20113
					).put(
						"name", "User"
					))
			).put(
				"siteBriefs",
				JSONUtil.put(
					JSONUtil.merge(
						JSONUtil.put(
							"id", 20127
						).put(
							"name", "Global"
						),
						JSONUtil.put(
							"id", 20125
						).put(
							"name", "Guest"
						)))
			).put(
				"userAccountContactInformation",
				JSONUtil.put(
					"emailAddresses", JSONFactoryUtil.createJSONArray()
				).put(
					"facebook", ""
				).put(
					"postalAddresses", JSONFactoryUtil.createJSONArray()
				).put(
					"skype", ""
				).put(
					"sms", ""
				).put(
					"telephones", JSONFactoryUtil.createJSONArray()
				).put(
					"twitter", ""
				).put(
					"webUrls", JSONFactoryUtil.createJSONArray()
				)
			).toString()
		).put(
			"com.liferay.headless.batch.engine.entity.TestEntity",
			JSONUtil.put(
				"intValue", "[$INT_VALUE$]"
			).put(
				"textValue", "[$TEXT_VALUE$]"
			).toString()
		).build();

		Class<?> clazz = ExportImportTaskResourcePerformanceTest.class;

		_properties = PropertiesUtil.load(
			clazz.getResourceAsStream(
				"dependencies/export-import-task-resource-performance." +
					"properties"),
			"UTF-8");
	}

	@Test
	public void testPostExportTaskWithTestEntity() throws Exception {
		TestEntityBatchEngineTaskItemDelegate
			testEntityBatchEngineTaskItemDelegate =
				_getTestEntityBatchEngineTaskItemDelegate();

		int count = GetterUtil.getInteger(
			_properties.getProperty("test.entities.count"));

		testEntityBatchEngineTaskItemDelegate.generate(count);

		_testPostExportTask(
			"com.liferay.headless.batch.engine.entity.TestEntity#" +
				"export-import-task-resource-performance-test-entities",
			count,
			GetterUtil.getLong(
				_properties.getProperty("test.entities.export.max.time")),
			GetterUtil.getLong(
				_properties.getProperty("test.entities.download.max.time")));
	}

	@Test
	public void testPostExportTaskWithUserAccount() throws Exception {
		int count = GetterUtil.getInteger(
			_properties.getProperty("user.accounts.count"));

		for (int i = 0; i < count; ++i) {
			UserTestUtil.addUser();
		}

		_testPostExportTask(
			"com.liferay.headless.admin.user.dto.v1_0.UserAccount", count,
			GetterUtil.getLong(
				_properties.getProperty("user.accounts.export.max.time")),
			GetterUtil.getLong(
				_properties.getProperty("user.accounts.download.max.time")));
	}

	@Test
	public void testPostImportTaskWithTestEntity() throws Exception {
		_testPostImportTask(
			"com.liferay.headless.batch.engine.entity.TestEntity#" +
				"export-import-task-resource-performance-test-entities",
			GetterUtil.getInteger(
				_properties.getProperty("test.entities.count")),
			GetterUtil.getLong(
				_properties.getProperty("test.entities.import.max.time")));
	}

	@Test
	public void testPostImportTaskWithUserAccount() throws Exception {
		_testPostImportTask(
			"com.liferay.headless.admin.user.dto.v1_0.UserAccount",
			GetterUtil.getInteger(
				_properties.getProperty("user.accounts.count")),
			GetterUtil.getLong(
				_properties.getProperty("user.accounts.import.max.time")));
	}

	private String _createBatchJSON(String className, int count) {
		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_BRACKET);

		for (int i = 0; i < count; i++) {
			String alternateName = RandomTestUtil.randomString(
				8, UniqueStringRandomizerBumper.INSTANCE);

			String json = StringUtil.replace(
				_jsons.get(className), "[$ALTERNATE_NAME$]", alternateName);

			json = StringUtil.replace(
				json, "[$EMAIL_ADDRESS$]",
				StringBundler.concat(
					alternateName, "@", RandomTestUtil.randomString(), ".com"));
			json = StringUtil.replace(
				json, "[$FAMILY_NAME$]",
				StringUtil.getTitleCase(
					RandomTestUtil.randomString(
						8, UniqueStringRandomizerBumper.INSTANCE),
					true, ""));
			json = StringUtil.replace(
				json, "[$GIVEN_NAME$]",
				StringUtil.getTitleCase(
					RandomTestUtil.randomString(
						8, UniqueStringRandomizerBumper.INSTANCE),
					true, ""));
			json = StringUtil.replace(
				json, "\"[$INT_VALUE$]\"",
				String.valueOf(RandomTestUtil.nextInt()));
			json = StringUtil.replace(
				json, "[$TEXT_VALUE$]",
				StringUtil.getTitleCase(
					RandomTestUtil.randomString(
						8, UniqueStringRandomizerBumper.INSTANCE),
					true, ""));

			sb.append(json);

			if (i < (count - 1)) {
				sb.append(StringPool.COMMA);
			}
		}

		sb.append(StringPool.CLOSE_BRACKET);

		return sb.toString();
	}

	private TestEntityBatchEngineTaskItemDelegate
		_getTestEntityBatchEngineTaskItemDelegate() {

		return (TestEntityBatchEngineTaskItemDelegate)
			_batchEngineTaskItemDelegateRegistry.getBatchEngineTaskItemDelegate(
				0, "com.liferay.headless.batch.engine.entity.TestEntity",
				"export-import-task-resource-performance-test-entities");
	}

	private Map<String, String> _splitClassName(String className) {
		Map<String, String> classNamePartsMap = new HashMap<>();

		if (className.contains("#")) {
			String[] classNameParts = className.split("#");

			classNamePartsMap.put("className", classNameParts[0]);
			classNamePartsMap.put("taskItemDelegateName", classNameParts[1]);
		}
		else {
			classNamePartsMap.put("className", className);
		}

		return classNamePartsMap;
	}

	private void _testPostExportTask(
			String className, int count, long maxExportTime,
			long maxDownloadTime)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Class name: " + className);
		}

		String externalReferenceCode = null;

		Map<String, String> classNamePartsMap = _splitClassName(className);

		try (TestEntityPerformanceTimer itemCountPerformanceTimer =
				new TestEntityPerformanceTimer(
					count, maxExportTime, className + "#export")) {

			ExportTask exportTask = ExportImportTaskUtil.postExportTask(
				classNamePartsMap.get("className"), "COMPLETED",
				HashMapBuilder.put(
					"createStrategy", "INSERT"
				).put(
					"taskItemDelegateName",
					() -> {
						if (!classNamePartsMap.containsKey(
								"taskItemDelegateName")) {

							return null;
						}

						return classNamePartsMap.get("taskItemDelegateName");
					}
				).build());

			externalReferenceCode = exportTask.getExternalReferenceCode();
		}

		try (TestEntityPerformanceTimer itemCountPerformanceTimer =
				new TestEntityPerformanceTimer(
					count, maxDownloadTime, className + "#download")) {

			try (InputStream inputStream = HTTPTestUtil.invokeToInputStream(
					null,
					StringBundler.concat(
						"headless-batch-engine/v1.0/export-task",
						"/by-external-reference-code/", externalReferenceCode,
						"/content"),
					Http.Method.GET)) {

				ZipInputStream zipInputStream = new ZipInputStream(inputStream);

				zipInputStream.getNextEntry();

				StringUtil.read(zipInputStream);
			}
		}
	}

	private void _testPostImportTask(String className, int count, long maxTime)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Class name: " + className);
		}

		Map<String, String> classNamePartsMap = _splitClassName(className);

		String json = _createBatchJSON(
			classNamePartsMap.get("className"), count);

		try (Closeable closeable = new TestEntityPerformanceTimer(
				count, maxTime, className)) {

			ImportTask importTask = ExportImportTaskUtil.postImportTask(
				json, classNamePartsMap.get("className"), "COMPLETED",
				HashMapBuilder.put(
					"createStrategy", "INSERT"
				).put(
					"taskItemDelegateName",
					() -> {
						if (!classNamePartsMap.containsKey(
								"taskItemDelegateName")) {

							return null;
						}

						return classNamePartsMap.get("taskItemDelegateName");
					}
				).build());

			Date endDate = importTask.getEndTime();
			Date startDate = importTask.getStartTime();

			_log.info(
				"Import task duration: " +
					(endDate.getTime() - startDate.getTime()) + " ms");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportImportTaskResourcePerformanceTest.class);

	private static Map<String, String> _jsons;
	private static Properties _properties;

	@Inject
	private BatchEngineTaskItemDelegateRegistry
		_batchEngineTaskItemDelegateRegistry;

	@Inject
	private UserService _userService;

	private class TestEntityPerformanceTimer extends PerformanceTimer {

		public TestEntityPerformanceTimer(
			int count, long maxTime, String name) {

			this(
				count, null, maxTime, getInvokerName(null, name),
				System.currentTimeMillis());
		}

		public TestEntityPerformanceTimer(
			int count, Path logFilePath, long maxTime, String name) {

			this(
				count, logFilePath, maxTime, getInvokerName(null, name),
				System.currentTimeMillis());
		}

		@Override
		public void close() {
			long delta = System.currentTimeMillis() - startTime;

			double speed =
				(delta > 0) ? (double)(_count * 1000) / (double)delta :
					Double.NaN;

			log(
				StringBundler.concat(
					"Completed ", name, " in ", delta, " ms, speed: ",
					String.format("%.2f", speed), " items per second"));

			Assert.assertTrue(
				StringBundler.concat(
					"Completed in ", delta,
					"ms, but the expected completion time should be less than ",
					maxTime, "ms"),
				delta < maxTime);
		}

		protected TestEntityPerformanceTimer(
			int count, Path logFilePath, long maxTime, String name,
			long startTime) {

			super(logFilePath, maxTime, name, startTime);

			_count = count;
		}

		private int _count;

	}

}