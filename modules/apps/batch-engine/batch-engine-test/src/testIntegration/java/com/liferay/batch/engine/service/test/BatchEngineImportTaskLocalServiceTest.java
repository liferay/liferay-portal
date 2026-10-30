/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.service.test;

import com.fasterxml.jackson.core.io.JsonEOFException;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.security.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.KeyGenerator;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class BatchEngineImportTaskLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAddBatchEngineImportTask() throws Exception {
		_testAddBatchEngineImportTaskWithEncryptionKey();
		_testAddBatchEngineImportTaskWithoutTaskItemDelegate();
	}

	@Test
	public void testUpdateBatchEngineImportTask() throws Exception {
		_testUpdateBatchEngineImportTask(
			BatchEngineTaskExecuteStatus.COMPLETED);
		_testUpdateBatchEngineImportTask(BatchEngineTaskExecuteStatus.FAILED);
		_testUpdateBatchEngineImportTaskWithMalformedContent(
			BatchEngineTaskExecuteStatus.COMPLETED);
		_testUpdateBatchEngineImportTaskWithMalformedContent(
			BatchEngineTaskExecuteStatus.FAILED);
		_testUpdateBatchEngineImportTaskWithoutPasswordFields(
			BatchEngineTaskExecuteStatus.COMPLETED);
		_testUpdateBatchEngineImportTaskWithoutPasswordFields(
			BatchEngineTaskExecuteStatus.FAILED);
	}

	private BatchEngineImportTask _addBatchEngineImportTask(
			String className, String json)
		throws Exception {

		BatchEngineImportTask batchEngineImportTask =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), 10, null, className,
				_getContent(json), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null);

		_batchEngineImportTasks.add(batchEngineImportTask);

		return batchEngineImportTask;
	}

	private byte[] _getContent(String json) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				unsyncByteArrayOutputStream)) {

			zipOutputStream.putNextEntry(new ZipEntry("import.json"));

			zipOutputStream.write(json.getBytes(StandardCharsets.UTF_8));

			zipOutputStream.closeEntry();
		}

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private String _readContent(BatchEngineImportTask batchEngineImportTask)
		throws Exception {

		try (ZipInputStream zipInputStream = new ZipInputStream(
				_batchEngineImportTaskLocalService.openContentInputStream(
					batchEngineImportTask.getBatchEngineImportTaskId()))) {

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			Assert.assertEquals("import.json", zipEntry.getName());

			return StringUtil.read(zipInputStream);
		}
	}

	private void _testAddBatchEngineImportTaskWithEncryptionKey()
		throws Exception {

		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

		Key key = keyGenerator.generateKey();

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"BATCH_ENGINE_ENCRYPTION_KEY",
					Base64.encode(key.getEncoded()))) {

			String emailAddress =
				RandomTestUtil.randomString() + "@liferay.com";
			String password = RandomTestUtil.randomString();

			BatchEngineImportTask batchEngineImportTask =
				_addBatchEngineImportTask(
					_CLASS_NAME_USER_ACCOUNT,
					JSONUtil.putAll(
						JSONUtil.put(
							"emailAddress", emailAddress
						).put(
							"password", password
						),
						JSONUtil.put(
							"emailAddress",
							RandomTestUtil.randomString() + "@liferay.com"
						).put(
							"password", StringPool.BLANK
						)
					).toString());

			String content = _readContent(batchEngineImportTask);

			Assert.assertFalse(content.contains(password));

			JSONArray jsonArray = JSONFactoryUtil.createJSONArray(content);

			JSONObject jsonObject = jsonArray.getJSONObject(0);

			Assert.assertEquals(
				emailAddress, jsonObject.getString("emailAddress"));

			String encryptedPassword = jsonObject.getString("password");

			Assert.assertEquals(
				password,
				EncryptorUtil.decryptAuthenticated(key, encryptedPassword));

			jsonObject = jsonArray.getJSONObject(1);

			Assert.assertEquals(
				StringPool.BLANK, jsonObject.getString("password"));

			batchEngineImportTask = _updateExecuteStatus(
				batchEngineImportTask, BatchEngineTaskExecuteStatus.STARTED);

			Assert.assertEquals(content, _readContent(batchEngineImportTask));

			batchEngineImportTask = _updateExecuteStatus(
				batchEngineImportTask, BatchEngineTaskExecuteStatus.COMPLETED);

			jsonArray = JSONFactoryUtil.createJSONArray(
				_readContent(batchEngineImportTask));

			jsonObject = jsonArray.getJSONObject(0);

			Assert.assertEquals(
				emailAddress, jsonObject.getString("emailAddress"));
			Assert.assertFalse(jsonObject.has("password"));

			jsonObject = jsonArray.getJSONObject(1);

			Assert.assertFalse(jsonObject.has("password"));
		}
	}

	private void _testAddBatchEngineImportTaskWithoutTaskItemDelegate()
		throws Exception {

		String className = RandomTestUtil.randomString();

		AssertUtils.assertFailure(
			IllegalStateException.class,
			StringBundler.concat(
				"No batch engine task item delegate is available for class ",
				"name ", className, " and task item delegate name null"),
			() -> _batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), 10, null, className,
				_getContent("[]"), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.name(), new HashMap<>(), null));
	}

	private void _testUpdateBatchEngineImportTask(
			BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus)
		throws Exception {

		String emailAddress = RandomTestUtil.randomString() + "@liferay.com";
		String password = RandomTestUtil.randomString();

		BatchEngineImportTask batchEngineImportTask = _addBatchEngineImportTask(
			_CLASS_NAME_USER_ACCOUNT,
			JSONUtil.putAll(
				JSONUtil.put(
					"emailAddress", emailAddress
				).put(
					"password", password
				)
			).toString());

		batchEngineImportTask = _updateExecuteStatus(
			batchEngineImportTask, BatchEngineTaskExecuteStatus.INITIAL);

		String content = _readContent(batchEngineImportTask);

		Assert.assertTrue(content, content.contains(password));

		batchEngineImportTask = _updateExecuteStatus(
			batchEngineImportTask, BatchEngineTaskExecuteStatus.STARTED);

		content = _readContent(batchEngineImportTask);

		Assert.assertTrue(content, content.contains(password));

		batchEngineImportTask = _updateExecuteStatus(
			batchEngineImportTask, batchEngineTaskExecuteStatus);

		content = _readContent(batchEngineImportTask);

		Assert.assertTrue(content, content.contains(emailAddress));
		Assert.assertFalse(content, content.contains(password));
	}

	private void _testUpdateBatchEngineImportTaskWithMalformedContent(
			BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus)
		throws Exception {

		String password = RandomTestUtil.randomString();

		BatchEngineImportTask batchEngineImportTask = _addBatchEngineImportTask(
			_CLASS_NAME_USER_ACCOUNT,
			StringBundler.concat(
				"[{\"emailAddress\": \"", RandomTestUtil.randomString(),
				"@liferay.com\", \"password\": \"", password, "\""));

		batchEngineImportTask = _updateExecuteStatus(
			batchEngineImportTask, BatchEngineTaskExecuteStatus.STARTED);

		String content = _readContent(batchEngineImportTask);

		Assert.assertTrue(content, content.contains(password));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.service.impl." +
					"BatchEngineImportTaskLocalServiceImpl",
				LoggerTestUtil.ERROR)) {

			batchEngineImportTask = _updateExecuteStatus(
				batchEngineImportTask, batchEngineTaskExecuteStatus);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Unable to remove password fields from batch engine ",
					"import task ",
					batchEngineImportTask.getBatchEngineImportTaskId(), ": ",
					JsonEOFException.class.getName()),
				logEntry.getMessage());
		}

		try (InputStream inputStream =
				_batchEngineImportTaskLocalService.openContentInputStream(
					batchEngineImportTask.getBatchEngineImportTaskId())) {

			Assert.assertEquals(StringPool.BLANK, StringUtil.read(inputStream));
		}
	}

	private void _testUpdateBatchEngineImportTaskWithoutPasswordFields(
			BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus)
		throws Exception {

		String headline = RandomTestUtil.randomString();

		BatchEngineImportTask batchEngineImportTask = _addBatchEngineImportTask(
			"com.liferay.headless.delivery.dto.v1_0.BlogPosting",
			JSONUtil.putAll(
				JSONUtil.put("headline", headline)
			).toString());

		batchEngineImportTask =
			_batchEngineImportTaskLocalService.getBatchEngineImportTask(
				batchEngineImportTask.getBatchEngineImportTaskId());

		_updateExecuteStatus(
			batchEngineImportTask, batchEngineTaskExecuteStatus);

		Assert.assertNull(
			ReflectionTestUtil.getFieldValue(
				batchEngineImportTask, "_contentBlobModel"));

		String content = _readContent(batchEngineImportTask);

		Assert.assertTrue(content, content.contains(headline));
	}

	private BatchEngineImportTask _updateExecuteStatus(
		BatchEngineImportTask batchEngineImportTask,
		BatchEngineTaskExecuteStatus batchEngineTaskExecuteStatus) {

		batchEngineImportTask.setExecuteStatus(
			batchEngineTaskExecuteStatus.name());

		return _batchEngineImportTaskLocalService.updateBatchEngineImportTask(
			batchEngineImportTask);
	}

	private static final String _CLASS_NAME_USER_ACCOUNT =
		"com.liferay.headless.admin.user.dto.v1_0.UserAccount";

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@DeleteAfterTestRun
	private final List<BatchEngineImportTask> _batchEngineImportTasks =
		new ArrayList<>();

}