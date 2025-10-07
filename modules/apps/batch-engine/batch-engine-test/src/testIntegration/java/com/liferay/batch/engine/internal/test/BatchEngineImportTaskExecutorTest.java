/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineImportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskOperation;
import com.liferay.batch.engine.constants.BatchEngineImportTaskConstants;
import com.liferay.batch.engine.exception.BatchEngineImportTaskParametersException;
import com.liferay.batch.engine.exception.handler.BatchEngineImportTaskExceptionHandler;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.BatchEngineImportTaskError;
import com.liferay.batch.engine.service.BatchEngineImportTaskErrorLocalService;
import com.liferay.batch.engine.service.BatchEngineImportTaskLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Ivica Cardic
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class BatchEngineImportTaskExecutorTest
	extends BaseBatchEngineTaskExecutorTest {

	@BeforeClass
	public static void setUpClass() {
		_fieldNamesMappingMap = HashMapBuilder.put(
			"alternativeHeadline1", "alternativeHeadline"
		).put(
			"articleBody1", "articleBody"
		).put(
			"datePublished1", "datePublished"
		).put(
			"headline1", "headline"
		).put(
			"siteId1", "siteId"
		).build();
	}

	@Test
	public void testCreateBlogPostingsFromCSVFile() throws Exception {
		_importBlogPostings(
			BatchEngineTaskOperation.CREATE,
			_getBlogPostingsCSVCreateContent(
				TestPropsValues.getGroupId(), FIELD_NAMES),
			"CSV", null);

		_assertCreatedBlogPostings();
	}

	@Test
	public void testCreateBlogPostingsFromCSVFileWithFieldMappings()
		throws Exception {

		_importBlogPostings(
			BatchEngineTaskOperation.CREATE,
			_getBlogPostingsCSVCreateContent(
				TestPropsValues.getGroupId(), _ALTERNATE_FIELD_NAMES),
			"CSV", _fieldNamesMappingMap);

		_assertCreatedBlogPostings();
	}

	@Test
	public void testCreateBlogPostingsFromInvalidJSONFile() throws Exception {
		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_BRACKET);

		_createJSONRow(
			sb, FIELD_NAMES[0], _toJSONValue("alternativeHeadline"),
			FIELD_NAMES[1], _toJSONValue("articleBody"), FIELD_NAMES[2],
			_toJSONValue(dateFormat.format(new Date(baseDate.getTime()))),
			FIELD_NAMES[3], _toJSONValue("headline"), FIELD_NAMES[4],
			String.valueOf(TestPropsValues.getGroupId()), "unknownColumn",
			_toJSONValue("unknownValue"));

		sb.append(StringPool.CLOSE_BRACKET);

		String content = sb.toString();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_importBlogPostings(
				BatchEngineTaskOperation.CREATE,
				_compressContent(
					content.getBytes(StandardCharsets.UTF_8), "JSON"),
				"JSON", null);

			_assertInvalidFileImportWithOnErrorFailStrategy(1, 1, logCapture);
		}
	}

	@Test
	public void testCreateBlogPostingsFromInvalidJSONLFile() throws Exception {
		StringBundler sb = new StringBundler();

		_createJSONRow(
			sb, FIELD_NAMES[0], _toJSONValue("alternativeHeadline"),
			FIELD_NAMES[1], _toJSONValue("articleBody"), FIELD_NAMES[2],
			_toJSONValue(dateFormat.format(new Date(baseDate.getTime()))),
			FIELD_NAMES[3], _toJSONValue("headline"), FIELD_NAMES[4],
			String.valueOf(TestPropsValues.getGroupId()), "unknownColumn",
			_toJSONValue("unknownValue"));

		String content = sb.toString();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_importBlogPostings(
				BatchEngineTaskOperation.CREATE,
				_compressContent(
					content.getBytes(StandardCharsets.UTF_8), "JSONL"),
				"JSONL", null);

			_assertInvalidFileImportWithOnErrorFailStrategy(1, 1, logCapture);
		}
	}

	@Test
	public void testCreateBlogPostingsFromInvalidXLSFile() throws Exception {
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

		Sheet sheet = xssfWorkbook.createSheet();

		_createXLSRow(
			sheet.createRow(0), FIELD_NAMES[0], FIELD_NAMES[1], FIELD_NAMES[2],
			FIELD_NAMES[3], FIELD_NAMES[4], "unknownColumn");

		_createXLSRow(
			sheet.createRow(1), "alternativeHeadline", "articleBody",
			dateFormat.format(new Date(baseDate.getTime())), "headline",
			TestPropsValues.getGroupId(), "unknownValue");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_importBlogPostings(
				BatchEngineTaskOperation.CREATE, _toContent(xssfWorkbook),
				"XLS", null);

			_assertInvalidFileImportWithOnErrorFailStrategy(1, 1, logCapture);
		}
	}

	@Test
	public void testCreateBlogPostingsFromJSONFile() throws Exception {
		_importBlogPostings(
			BatchEngineTaskOperation.CREATE,
			_getBlogPostingsJSONCreateContent(
				TestPropsValues.getGroupId(), FIELD_NAMES),
			"JSON", null);

		_assertCreatedBlogPostings();
	}

	@Test
	public void testCreateBlogPostingsFromJSONFileWithFieldMappings()
		throws Exception {

		_importBlogPostings(
			BatchEngineTaskOperation.CREATE,
			_getBlogPostingsJSONCreateContent(
				TestPropsValues.getGroupId(), _ALTERNATE_FIELD_NAMES),
			"JSON", _fieldNamesMappingMap);

		_assertCreatedBlogPostings();
	}

	@Test
	public void testCreateBlogPostingsFromJSONLFile() throws Exception {
		_importBlogPostings(
			BatchEngineTaskOperation.CREATE,
			_getBlogPostingsJSONLCreateContent(
				TestPropsValues.getGroupId(), FIELD_NAMES),
			"JSONL", null);

		_assertCreatedBlogPostings();
	}

	@Test
	public void testCreateBlogPostingsFromJSONLFileWithFieldMappings()
		throws Exception {

		_importBlogPostings(
			BatchEngineTaskOperation.CREATE,
			_getBlogPostingsJSONLCreateContent(
				TestPropsValues.getGroupId(), _ALTERNATE_FIELD_NAMES),
			"JSONL", _fieldNamesMappingMap);

		_assertCreatedBlogPostings();
	}

	@Test
	public void testCreateBlogPostingsFromXLSFile() throws Exception {
		_importBlogPostings(
			BatchEngineTaskOperation.CREATE,
			_getBlogPostingsXLSCreateContent(
				TestPropsValues.getGroupId(), FIELD_NAMES),
			"XLS", null);

		_assertCreatedBlogPostings();
	}

	@Test
	public void testCreateBlogPostingsFromXLSFileWithFieldMappings()
		throws Exception {

		_importBlogPostings(
			BatchEngineTaskOperation.CREATE,
			_getBlogPostingsXLSCreateContent(
				TestPropsValues.getGroupId(), _ALTERNATE_FIELD_NAMES),
			"XLS", _fieldNamesMappingMap);

		_assertCreatedBlogPostings();
	}

	@Test
	public void testCreateBlogPostingsWithInvalidCSVFileAndOnErrorContinue()
		throws Exception {

		StringBundler sb = new StringBundler();

		_createCSVRow(sb, FIELD_NAMES);

		String[] blogPostingItem = {
			"alternativeHeadline", "articleBody",
			dateFormat.format(new Date(baseDate.getTime())), "headline",
			String.valueOf(TestPropsValues.getGroupId())
		};

		_createCSVRow(sb, blogPostingItem);

		String[] blogPostingItemWithUnknownColumn = {
			"alternativeHeadline", "articleBody",
			dateFormat.format(new Date(baseDate.getTime())), "headline",
			String.valueOf(TestPropsValues.getGroupId()), "unknownColumn"
		};

		int blogPostingItemWithUnknownColumnRowNumber = 2;

		_createCSVRow(sb, blogPostingItemWithUnknownColumn);

		String[] blogPostingItemWithInvalidValue = {
			"alternativeHeadline", null,
			dateFormat.format(new Date(baseDate.getTime())), "headline",
			String.valueOf(TestPropsValues.getGroupId())
		};

		int blogPostingItemWithInvalidValueRowNumber = 3;

		_createCSVRow(sb, blogPostingItemWithInvalidValue);

		String content = sb.toString();

		try (LogCapture logCapture1 = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal.strategy." +
					"OnErrorContinueBatchEngineImportStrategy",
				LoggerTestUtil.ERROR);
			LogCapture logCapture2 = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_importBlogPostings(
				BatchEngineTaskOperation.CREATE,
				_compressContent(
					content.getBytes(StandardCharsets.UTF_8), "CSV"),
				"CSV", null,
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_ON_ERROR_CONTINUE);
		}

		_assertInvalidFileImportWithOnErrorContinueStrategy(
			Arrays.asList(
				blogPostingItemWithUnknownColumnRowNumber,
				blogPostingItemWithInvalidValueRowNumber),
			3);
	}

	@Test
	public void testCreateBlogPostingsWithInvalidCSVFileAndOnErrorFail()
		throws Exception {

		StringBundler sb = new StringBundler();

		_createCSVRow(sb, FIELD_NAMES);

		String[] blogPostingItem1 = {
			"alternativeHeadline", "articleBody",
			dateFormat.format(new Date(baseDate.getTime())), "headline",
			String.valueOf(TestPropsValues.getGroupId())
		};

		_createCSVRow(sb, blogPostingItem1);

		String[] blogPostingItemWithUnknownColumn = {
			"alternativeHeadline", "articleBody",
			dateFormat.format(new Date(baseDate.getTime())), "headline",
			String.valueOf(TestPropsValues.getGroupId()), "unknownColumn"
		};

		int blogPostingItemWithUnknownColumnRowNumber = 2;

		_createCSVRow(sb, blogPostingItemWithUnknownColumn);

		String[] blogPostingItem3 = {
			"alternativeHeadline", "articleBody",
			dateFormat.format(new Date(baseDate.getTime())), "headline",
			String.valueOf(TestPropsValues.getGroupId())
		};

		_createCSVRow(sb, blogPostingItem3);

		String content = sb.toString();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_importBlogPostings(
				BatchEngineTaskOperation.CREATE,
				_compressContent(
					content.getBytes(StandardCharsets.UTF_8), "CSV"),
				"CSV", null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL);

			_assertInvalidFileImportWithOnErrorFailStrategy(
				blogPostingItemWithUnknownColumnRowNumber, 3, logCapture);
		}
	}

	@Test
	public void testDeleteBlogPostingsFromCSVFile() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_importBlogPostings(
			BatchEngineTaskOperation.DELETE,
			_getBlogPostingsCSVDeleteContent(blogsEntries), "CSV", null);

		_assertDeletedBlogPostings();
	}

	@Test
	public void testDeleteBlogPostingsFromJSONFile() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_importBlogPostings(
			BatchEngineTaskOperation.DELETE,
			_getBlogPostingsJSONDeleteContent(blogsEntries), "JSON", null);

		_assertDeletedBlogPostings();
	}

	@Test
	public void testDeleteBlogPostingsFromJSONLFile() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_importBlogPostings(
			BatchEngineTaskOperation.DELETE,
			_getBlogPostingsJSONLDeleteContent(blogsEntries), "JSONL", null);

		_assertDeletedBlogPostings();
	}

	@Test
	public void testDeleteBlogPostingsFromXLSFile() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_importBlogPostings(
			BatchEngineTaskOperation.DELETE,
			_getBlogPostingsXLSDeleteContent(blogsEntries), "XLS", null);

		_assertDeletedBlogPostings();
	}

	@Test
	public void testDeleteBlogPostingsWithImportStrategies() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_importBlogPostings(
				BatchEngineTaskOperation.DELETE,
				_getBlogPostingsCSVDeleteContent(blogsEntries, true), "CSV",
				null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL);
		}

		Assert.assertEquals(0, _batchEngineImportTask.getProcessedItemsCount());
		Assert.assertEquals(
			initialCount + blogsEntries.size(),
			blogsEntryLocalService.getGroupEntriesCount(
				TestPropsValues.getGroupId(),
				new QueryDefinition<>(WorkflowConstants.STATUS_ANY)));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.batch.engine.internal.strategy." +
					"OnErrorContinueBatchEngineImportStrategy",
				LoggerTestUtil.ERROR)) {

			_importBlogPostings(
				BatchEngineTaskOperation.DELETE,
				_getBlogPostingsCSVDeleteContent(blogsEntries, true), "CSV",
				null,
				BatchEngineImportTaskConstants.
					IMPORT_STRATEGY_ON_ERROR_CONTINUE);
		}

		Assert.assertEquals(
			ROWS_COUNT + 1, _batchEngineImportTask.getProcessedItemsCount());
		Assert.assertEquals(
			initialCount,
			blogsEntryLocalService.getGroupEntriesCount(
				TestPropsValues.getGroupId(),
				new QueryDefinition<>(WorkflowConstants.STATUS_ANY)));
	}

	@Test
	public void testImportAccountSystemObjectDefinitionsJSON()
		throws Exception {

		// Successful import

		AccountResource accountResource = AccountResource.builder(
		).authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).build();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "AccountEntry");

		String integerFieldName = "a" + RandomTestUtil.randomString();

		_objectFieldLocalService.addCustomObjectField(
			null, user.getUserId(), 0, objectDefinition.getObjectDefinitionId(),
			"Integer", "Integer", true, false, null,
			HashMapBuilder.put(
				LocaleUtil.fromLanguageId("en_US"), integerFieldName
			).build(),
			false, integerFieldName, null, null, true, false,
			Collections.emptyList());

		String textFieldName = "a" + RandomTestUtil.randomString();

		_objectFieldLocalService.addCustomObjectField(
			null, user.getUserId(), 0, objectDefinition.getObjectDefinitionId(),
			"Text", "String", true, false, "en_US",
			HashMapBuilder.put(
				LocaleUtil.fromLanguageId("en_US"), textFieldName
			).build(),
			false, textFieldName, null, null, false, false,
			Collections.emptyList());

		Account account1 = new Account() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				name = RandomTestUtil.randomString();
				type = Account.Type.PERSON;
			}
		};
		Account account2 = new Account() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				name = RandomTestUtil.randomString();
				type = Account.Type.PERSON;
			}
		};
		int integerFieldValue1 = RandomTestUtil.randomInt();
		int integerFieldValue2 = RandomTestUtil.randomInt();
		String textFieldValue1 = RandomTestUtil.randomString();
		String textFieldValue2 = RandomTestUtil.randomString();

		String json = JSONUtil.putAll(
			JSONFactoryUtil.createJSONObject(
				account1.toString()
			).put(
				integerFieldName, integerFieldValue1
			).put(
				textFieldName, textFieldValue1
			),
			JSONFactoryUtil.createJSONObject(
				account2.toString()
			).put(
				integerFieldName, integerFieldValue2
			).put(
				textFieldName, textFieldValue2
			)
		).toString();

		_batchEngineImportTask =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, TestPropsValues.getCompanyId(), user.getUserId(),
				_BATCH_SIZE, null,
				"com.liferay.headless.admin.user.dto.v1_0.Account",
				_compressContent(json.getBytes(), "JSON"), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null,
				BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL,
				BatchEngineTaskOperation.CREATE.toString(), new HashMap<>(),
				null);

		_batchEngineImportTaskExecutor.execute(_batchEngineImportTask);

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.COMPLETED.toString(),
			_batchEngineImportTask.getExecuteStatus());

		HttpInvoker.HttpResponse httpResponse =
			accountResource.getAccountByExternalReferenceCodeHttpResponse(
				account1.getExternalReferenceCode());

		JSONAssert.assertEquals(
			JSONUtil.put(
				integerFieldName, integerFieldValue1
			).put(
				textFieldName, textFieldValue1
			).put(
				"externalReferenceCode", account1.getExternalReferenceCode()
			).toString(),
			httpResponse.getContent(), JSONCompareMode.LENIENT);

		httpResponse =
			accountResource.getAccountByExternalReferenceCodeHttpResponse(
				account2.getExternalReferenceCode());

		JSONAssert.assertEquals(
			JSONUtil.put(
				integerFieldName, integerFieldValue2
			).put(
				textFieldName, textFieldValue2
			).put(
				"externalReferenceCode", account2.getExternalReferenceCode()
			).toString(),
			httpResponse.getContent(), JSONCompareMode.LENIENT);

		// Unsuccessful import

		account1 = new Account() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				name = RandomTestUtil.randomString();
				type = Account.Type.PERSON;
			}
		};

		json = JSONUtil.put(
			JSONFactoryUtil.createJSONObject(
				account1.toString()
			).put(
				textFieldName, RandomTestUtil.randomString()
			)
		).toString();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_batchEngineImportTask =
				_batchEngineImportTaskLocalService.addBatchEngineImportTask(
					null, TestPropsValues.getCompanyId(), user.getUserId(),
					_BATCH_SIZE, null,
					"com.liferay.headless.admin.user.dto.v1_0.Account",
					_compressContent(json.getBytes(), "JSON"), "JSON",
					BatchEngineTaskExecuteStatus.INITIAL.name(), null,
					BatchEngineImportTaskConstants.
						IMPORT_STRATEGY_ON_ERROR_FAIL,
					BatchEngineTaskOperation.CREATE.toString(), new HashMap<>(),
					null);

			_batchEngineImportTaskExecutor.execute(_batchEngineImportTask);
		}

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.FAILED.toString(),
			_batchEngineImportTask.getExecuteStatus());

		httpResponse =
			accountResource.getAccountByExternalReferenceCodeHttpResponse(
				account1.getExternalReferenceCode());

		Assert.assertEquals(404, httpResponse.getStatusCode());
	}

	@Test
	public void testImportTaskInvalidCreateAndUpdateStrategies() {
		BatchEngineTaskOperation batchEngineTaskOperation =
			BatchEngineTaskOperation.CREATE;
		Map<String, Serializable> parameters =
			HashMapBuilder.<String, Serializable>put(
				"createStrategy", "INVALID CREATE STRATEGY"
			).build();

		try {
			_batchEngineImportTask =
				_batchEngineImportTaskLocalService.addBatchEngineImportTask(
					null, TestPropsValues.getCompanyId(), user.getUserId(),
					_BATCH_SIZE, null, BlogPosting.class.getName(), null, "CSV",
					BatchEngineTaskExecuteStatus.INITIAL.name(),
					Collections.emptyMap(),
					BatchEngineImportTaskConstants.
						IMPORT_STRATEGY_ON_ERROR_FAIL,
					batchEngineTaskOperation.name(), parameters, null);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getClass(),
				BatchEngineImportTaskParametersException.class);

			String exceptionMessage = exception.getMessage();

			Assert.assertTrue(
				exceptionMessage.contains("INVALID CREATE STRATEGY"));
		}

		parameters = HashMapBuilder.<String, Serializable>put(
			"updateStrategy", "INVALID UPDATE STRATEGY"
		).build();

		try {
			_batchEngineImportTask =
				_batchEngineImportTaskLocalService.addBatchEngineImportTask(
					null, TestPropsValues.getCompanyId(), user.getUserId(),
					_BATCH_SIZE, null, BlogPosting.class.getName(), null, "CSV",
					BatchEngineTaskExecuteStatus.INITIAL.name(),
					Collections.emptyMap(),
					BatchEngineImportTaskConstants.
						IMPORT_STRATEGY_ON_ERROR_FAIL,
					batchEngineTaskOperation.name(), parameters, null);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertEquals(
				exception.getClass(),
				BatchEngineImportTaskParametersException.class);

			String exceptionMessage = exception.getMessage();

			Assert.assertTrue(
				exceptionMessage.contains("INVALID UPDATE STRATEGY"));
		}
	}

	@Test
	public void testUpdateBlogPostingsFromCSVFile() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_importBlogPostings(
			BatchEngineTaskOperation.UPDATE,
			_getBlogPostingsCSVUpdateContent(blogsEntries), "CSV", null);

		_assertUpdatedBlogPostings();
	}

	@Test
	public void testUpdateBlogPostingsFromJSONFile() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_importBlogPostings(
			BatchEngineTaskOperation.UPDATE,
			_getBlogPostingsJSONUpdateContent(blogsEntries), "JSON", null);

		_assertUpdatedBlogPostings();
	}

	@Test
	public void testUpdateBlogPostingsFromJSONLFile() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_importBlogPostings(
			BatchEngineTaskOperation.UPDATE,
			_getBlogPostingsJSONLUpdateContent(blogsEntries), "JSONL", null);

		_assertUpdatedBlogPostings();
	}

	@Test
	public void testUpdateBlogPostingsFromXLSFile() throws Exception {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_importBlogPostings(
			BatchEngineTaskOperation.UPDATE,
			_getBlogPostingsXLSUpdateContent(blogsEntries), "XLS", null);

		_assertUpdatedBlogPostings();
	}

	private void _assertCreatedBlogPostings() throws Exception {
		Assert.assertEquals(
			ROWS_COUNT, _batchEngineImportTask.getProcessedItemsCount());

		assertBlogsEntriesCount();

		List<BlogsEntry> blogsEntries = new ArrayList<>(
			blogsEntryLocalService.getGroupEntries(
				TestPropsValues.getGroupId(),
				new QueryDefinition<>(WorkflowConstants.STATUS_ANY)));

		blogsEntries.sort(Comparator.comparingLong(BlogsEntry::getEntryId));

		for (int i = 0; i < ROWS_COUNT; i++) {
			BlogsEntry blogsEntry = blogsEntries.get(i + initialCount);

			Assert.assertEquals(
				"alternativeHeadline" + i, blogsEntry.getSubtitle());
			Assert.assertEquals("articleBody" + i, blogsEntry.getContent());
			Assert.assertEquals(
				_toTime(baseDate, i - 1000),
				_toTime(blogsEntry.getDisplayDate(), 0));
			Assert.assertEquals("headline" + i, blogsEntry.getTitle());
		}
	}

	private void _assertDeletedBlogPostings() throws Exception {
		Assert.assertEquals(
			ROWS_COUNT, _batchEngineImportTask.getProcessedItemsCount());
		Assert.assertEquals(
			initialCount,
			blogsEntryLocalService.getGroupEntriesCount(
				TestPropsValues.getGroupId(),
				new QueryDefinition<>(WorkflowConstants.STATUS_ANY)));
	}

	private void _assertInvalidFileImportWithOnErrorContinueStrategy(
			List<Integer> invalidItemRowNumbers, int itemsCount)
		throws Exception {

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.COMPLETED.toString(),
			_batchEngineImportTask.getExecuteStatus());
		Assert.assertEquals(
			itemsCount, _batchEngineImportTask.getProcessedItemsCount());
		Assert.assertEquals(
			itemsCount, _batchEngineImportTask.getTotalItemsCount());
		Assert.assertEquals(
			initialCount + itemsCount - invalidItemRowNumbers.size(),
			getBlogEntriesCount());

		List<BatchEngineImportTaskError> batchEngineImportTaskErrors =
			_batchEngineImportTaskErrorLocalService.
				getBatchEngineImportTaskErrors(
					_batchEngineImportTask.getBatchEngineImportTaskId());

		Assert.assertEquals(
			batchEngineImportTaskErrors.toString(),
			invalidItemRowNumbers.size(), batchEngineImportTaskErrors.size());

		List<Integer> failedItemIndexes = new ArrayList<>();

		for (BatchEngineImportTaskError batchEngineImportTaskError :
				batchEngineImportTaskErrors) {

			failedItemIndexes.add(batchEngineImportTaskError.getItemIndex());
		}

		Assert.assertTrue(failedItemIndexes.containsAll(invalidItemRowNumbers));
	}

	private void _assertInvalidFileImportWithOnErrorFailStrategy(
			int invalidItemRowNumber, int itemsCount, LogCapture logCapture)
		throws Exception {

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.FAILED.toString(),
			_batchEngineImportTask.getExecuteStatus());
		Assert.assertEquals(0, _batchEngineImportTask.getProcessedItemsCount());
		Assert.assertEquals(
			itemsCount, _batchEngineImportTask.getTotalItemsCount());

		List<BatchEngineImportTaskError> batchEngineImportTaskErrors =
			_batchEngineImportTaskErrorLocalService.
				getBatchEngineImportTaskErrors(
					_batchEngineImportTask.getBatchEngineImportTaskId());

		Assert.assertEquals(
			batchEngineImportTaskErrors.toString(), 1,
			batchEngineImportTaskErrors.size());

		BatchEngineImportTaskError batchEngineImportTaskError =
			batchEngineImportTaskErrors.get(0);

		Assert.assertEquals(
			invalidItemRowNumber, batchEngineImportTaskError.getItemIndex());

		Assert.assertEquals(initialCount, getBlogEntriesCount());

		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());

		String message = logEntry.getMessage();

		Assert.assertTrue(
			message.startsWith("Unable to update batch engine import task"));
	}

	private void _assertUpdatedBlogPostings() throws Exception {
		Assert.assertEquals(
			ROWS_COUNT, _batchEngineImportTask.getProcessedItemsCount());

		assertBlogsEntriesCount();

		List<BlogsEntry> blogsEntries = new ArrayList<>(
			blogsEntryLocalService.getGroupEntries(
				TestPropsValues.getGroupId(),
				new QueryDefinition<>(WorkflowConstants.STATUS_ANY)));

		blogsEntries.sort(Comparator.comparingLong(BlogsEntry::getEntryId));

		for (int i = 0; i < ROWS_COUNT; i++) {
			BlogsEntry blogsEntry = blogsEntries.get(i + initialCount);

			Assert.assertEquals(
				"alternativeHeadline" + i + i, blogsEntry.getSubtitle());
			Assert.assertEquals("articleBody" + i + i, blogsEntry.getContent());
			Assert.assertEquals(
				_toTime(baseDate, i + 100),
				_toTime(blogsEntry.getDisplayDate(), 0));
			Assert.assertEquals("headline" + i + i, blogsEntry.getTitle());
		}
	}

	private byte[] _compressContent(byte[] content, String contentType)
		throws Exception {

		try (ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream()) {

			try (ZipOutputStream zipOutputStream = new ZipOutputStream(
					byteArrayOutputStream)) {

				ZipEntry zipEntry = new ZipEntry(
					"import." + StringUtil.toLowerCase(contentType));

				zipOutputStream.putNextEntry(zipEntry);

				zipOutputStream.write(content, 0, content.length);
			}

			return byteArrayOutputStream.toByteArray();
		}
	}

	private void _createCSVRow(StringBundler sb, String... values) {
		for (String value : values) {
			sb.append(value);
			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.NEW_LINE);
	}

	private void _createJSONRow(StringBundler sb, String... values) {
		sb.append(StringPool.OPEN_CURLY_BRACE);

		for (int i = 0; i < values.length; i = i + 2) {
			sb.append(StringPool.QUOTE);
			sb.append(values[i]);
			sb.append("\": ");
			sb.append(values[i + 1]);

			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_CURLY_BRACE);
	}

	private void _createXLSRow(Row row, Object... values) {
		for (int i = 0; i < values.length; i++) {
			Cell cell = row.createCell(i);

			if (values[i] instanceof Boolean) {
				cell.setCellValue((Boolean)values[i]);
			}
			else if (values[i] instanceof Date) {
				cell.setCellValue((Date)values[i]);
			}
			else if (values[i] instanceof Number) {
				Number value = (Number)values[i];

				cell.setCellValue(value.doubleValue());
			}
			else {
				cell.setCellValue((String)values[i]);
			}
		}
	}

	private byte[] _getBlogPostingsCSVCreateContent(
			long siteId, String[] fieldNames)
		throws Exception {

		StringBundler sb = new StringBundler();

		_createCSVRow(sb, fieldNames);

		for (int i = 0; i < ROWS_COUNT; i++) {
			_createCSVRow(
				sb, "alternativeHeadline" + i, "articleBody" + i,
				dateFormat.format(new Date(_toTime(baseDate, i - 1000))),
				"headline" + i, String.valueOf(siteId));
		}

		return _toContent("CSV", sb);
	}

	private byte[] _getBlogPostingsCSVDeleteContent(
			List<BlogsEntry> blogsEntries)
		throws Exception {

		return _getBlogPostingsCSVDeleteContent(blogsEntries, false);
	}

	private byte[] _getBlogPostingsCSVDeleteContent(
			List<BlogsEntry> blogsEntries, boolean addInvalidId)
		throws Exception {

		StringBundler sb = new StringBundler();

		_createCSVRow(sb, "id");

		if (addInvalidId) {
			_createCSVRow(sb, String.valueOf(-1));
		}

		for (BlogsEntry blogsEntry : blogsEntries) {
			_createCSVRow(sb, String.valueOf(blogsEntry.getEntryId()));
		}

		return _toContent("CSV", sb);
	}

	private byte[] _getBlogPostingsCSVUpdateContent(
			List<BlogsEntry> blogsEntries)
		throws Exception {

		StringBundler sb = new StringBundler();

		_createCSVRow(
			sb, "alternativeHeadline", "articleBody", "datePublished",
			"headline", "id");

		for (int i = 0; i < blogsEntries.size(); i++) {
			BlogsEntry blogsEntry = blogsEntries.get(i);

			_createCSVRow(
				sb, blogsEntry.getSubtitle() + i, blogsEntry.getContent() + i,
				dateFormat.format(
					new Date(_toTime(blogsEntry.getDisplayDate(), i + 100))),
				blogsEntry.getTitle() + i,
				String.valueOf(blogsEntry.getEntryId()));
		}

		return _toContent("CSV", sb);
	}

	private byte[] _getBlogPostingsJSONCreateContent(
			long siteId, String[] fieldNames)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_BRACKET);

		for (int i = 0; i < ROWS_COUNT; i++) {
			_createJSONRow(
				sb, fieldNames[0], _toJSONValue("alternativeHeadline" + i),
				fieldNames[1], _toJSONValue("articleBody" + i), fieldNames[2],
				_toJSONValue(
					dateFormat.format(new Date(_toTime(baseDate, i - 1000)))),
				fieldNames[3], _toJSONValue("headline" + i), fieldNames[4],
				String.valueOf(siteId));

			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_BRACKET);

		return _toContent("JSON", sb);
	}

	private byte[] _getBlogPostingsJSONDeleteContent(
			List<BlogsEntry> blogsEntries)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_BRACKET);

		for (BlogsEntry blogsEntry : blogsEntries) {
			_createJSONRow(sb, "id", String.valueOf(blogsEntry.getEntryId()));

			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_BRACKET);

		return _toContent("JSON", sb);
	}

	private byte[] _getBlogPostingsJSONLCreateContent(
			long siteId, String[] fieldNames)
		throws Exception {

		StringBundler sb = new StringBundler();

		for (int i = 0; i < ROWS_COUNT; i++) {
			_createJSONRow(
				sb, fieldNames[0], _toJSONValue("alternativeHeadline" + i),
				fieldNames[1], _toJSONValue("articleBody" + i), fieldNames[2],
				_toJSONValue(
					dateFormat.format(new Date(_toTime(baseDate, i - 1000)))),
				fieldNames[3], _toJSONValue("headline" + i), fieldNames[4],
				String.valueOf(siteId));

			sb.append(StringPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		return _toContent("JSONL", sb);
	}

	private byte[] _getBlogPostingsJSONLDeleteContent(
			List<BlogsEntry> blogsEntries)
		throws Exception {

		StringBundler sb = new StringBundler();

		for (BlogsEntry blogsEntry : blogsEntries) {
			_createJSONRow(sb, "id", String.valueOf(blogsEntry.getEntryId()));

			sb.append(StringPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		return _toContent("JSONL", sb);
	}

	private byte[] _getBlogPostingsJSONLUpdateContent(
			List<BlogsEntry> blogsEntries)
		throws Exception {

		StringBundler sb = new StringBundler();

		for (int i = 0; i < blogsEntries.size(); i++) {
			BlogsEntry blogsEntry = blogsEntries.get(i);

			_createJSONRow(
				sb, FIELD_NAMES[0], _toJSONValue(blogsEntry.getSubtitle() + i),
				FIELD_NAMES[1], _toJSONValue(blogsEntry.getContent() + i),
				FIELD_NAMES[2],
				_toJSONValue(
					dateFormat.format(
						new Date(
							_toTime(blogsEntry.getDisplayDate(), i + 100)))),
				FIELD_NAMES[3], _toJSONValue(blogsEntry.getTitle() + i), "id",
				String.valueOf(blogsEntry.getEntryId()));

			sb.append(StringPool.NEW_LINE);
		}

		sb.setIndex(sb.index() - 1);

		return _toContent("JSONL", sb);
	}

	private byte[] _getBlogPostingsJSONUpdateContent(
			List<BlogsEntry> blogsEntries)
		throws Exception {

		StringBundler sb = new StringBundler();

		sb.append(StringPool.OPEN_BRACKET);

		for (int i = 0; i < blogsEntries.size(); i++) {
			BlogsEntry blogsEntry = blogsEntries.get(i);

			_createJSONRow(
				sb, FIELD_NAMES[0], _toJSONValue(blogsEntry.getSubtitle() + i),
				FIELD_NAMES[1], _toJSONValue(blogsEntry.getContent() + i),
				FIELD_NAMES[2],
				_toJSONValue(
					dateFormat.format(
						new Date(
							_toTime(blogsEntry.getDisplayDate(), i + 100)))),
				FIELD_NAMES[3], _toJSONValue(blogsEntry.getTitle() + i), "id",
				String.valueOf(blogsEntry.getEntryId()));

			sb.append(StringPool.COMMA);
		}

		sb.setIndex(sb.index() - 1);

		sb.append(StringPool.CLOSE_BRACKET);

		return _toContent("JSON", sb);
	}

	private byte[] _getBlogPostingsXLSCreateContent(
			long siteId, String[] fieldNames)
		throws Exception {

		XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

		Sheet sheet = xssfWorkbook.createSheet();

		_createXLSRow(
			sheet.createRow(0), fieldNames[0], fieldNames[1], fieldNames[2],
			fieldNames[3], fieldNames[4]);

		for (int i = 0; i < ROWS_COUNT; i++) {
			_createXLSRow(
				sheet.createRow(i + 1), "alternativeHeadline" + i,
				"articleBody" + i,
				dateFormat.format(new Date(_toTime(baseDate, i - 1000))),
				"headline" + i, siteId);
		}

		return _toContent(xssfWorkbook);
	}

	private byte[] _getBlogPostingsXLSDeleteContent(
			List<BlogsEntry> blogsEntries)
		throws Exception {

		XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

		Sheet sheet = xssfWorkbook.createSheet();

		_createXLSRow(sheet.createRow(0), "id");

		for (int i = 0; i < blogsEntries.size(); i++) {
			BlogsEntry blogsEntry = blogsEntries.get(i);

			_createXLSRow(sheet.createRow(i + 1), blogsEntry.getEntryId());
		}

		return _toContent(xssfWorkbook);
	}

	private byte[] _getBlogPostingsXLSUpdateContent(
			List<BlogsEntry> blogsEntries)
		throws Exception {

		XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

		Sheet sheet = xssfWorkbook.createSheet();

		_createXLSRow(
			sheet.createRow(0), FIELD_NAMES[0], FIELD_NAMES[1], FIELD_NAMES[2],
			FIELD_NAMES[3], "id");

		for (int i = 0; i < blogsEntries.size(); i++) {
			BlogsEntry blogsEntry = blogsEntries.get(i);

			_createXLSRow(
				sheet.createRow(i + 1), blogsEntry.getSubtitle() + i,
				blogsEntry.getContent() + i,
				dateFormat.format(
					new Date(_toTime(blogsEntry.getDisplayDate(), i + 100))),
				blogsEntry.getTitle() + i, blogsEntry.getEntryId());
		}

		return _toContent(xssfWorkbook);
	}

	private void _importBlogPostings(
			BatchEngineTaskOperation batchEngineTaskOperation, byte[] content,
			String contentType, Map<String, String> fieldNameMappingMap)
		throws Exception {

		_importBlogPostings(
			batchEngineTaskOperation, content, contentType, fieldNameMappingMap,
			BatchEngineImportTaskConstants.IMPORT_STRATEGY_ON_ERROR_FAIL);
	}

	private void _importBlogPostings(
			BatchEngineTaskOperation batchEngineTaskOperation, byte[] content,
			String contentType, Map<String, String> fieldNameMappingMap,
			int importStrategy)
		throws Exception {

		Map<String, Serializable> parameters = new HashMap<>();

		if (batchEngineTaskOperation == BatchEngineTaskOperation.CREATE) {
			parameters = HashMapBuilder.<String, Serializable>put(
				"siteId",
				(Serializable)String.valueOf(TestPropsValues.getGroupId())
			).build();
		}

		_batchEngineImportTask =
			_batchEngineImportTaskLocalService.addBatchEngineImportTask(
				null, TestPropsValues.getCompanyId(), user.getUserId(),
				_BATCH_SIZE, null, BlogPosting.class.getName(), content,
				contentType, BatchEngineTaskExecuteStatus.INITIAL.name(),
				fieldNameMappingMap, importStrategy,
				batchEngineTaskOperation.name(), parameters, null);

		_batchEngineImportTaskExecutor.execute(_batchEngineImportTask);
	}

	private byte[] _toContent(String contentType, StringBundler sb)
		throws Exception {

		String content = sb.toString();

		return _compressContent(
			content.getBytes(StandardCharsets.UTF_8), contentType);
	}

	private byte[] _toContent(XSSFWorkbook xssfWorkbook) throws Exception {
		try (ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream()) {

			xssfWorkbook.write(byteArrayOutputStream);

			xssfWorkbook.close();

			return _compressContent(byteArrayOutputStream.toByteArray(), "XLS");
		}
	}

	private String _toJSONValue(String value) {
		return StringPool.QUOTE + value + StringPool.QUOTE;
	}

	private long _toTime(Date date, int index) {
		return date.getTime() + (index * Time.MINUTE);
	}

	private static final String[] _ALTERNATE_FIELD_NAMES = {
		"alternativeHeadline1", "articleBody1", "datePublished1", "headline1",
		"siteId1"
	};

	private static final int _BATCH_SIZE = 10;

	private static final String
		_CLASS_NAME_BATCH_ENGINE_IMPORT_TASK_EXECUTOR_IMPL =
			"com.liferay.batch.engine.internal." +
				"BatchEngineImportTaskExecutorImpl";

	private static Map<String, String> _fieldNamesMappingMap;

	@DeleteAfterTestRun
	private BatchEngineImportTask _batchEngineImportTask;

	@Inject
	private BatchEngineImportTaskErrorLocalService
		_batchEngineImportTaskErrorLocalService;

	@Inject
	private BatchEngineImportTaskExecutor _batchEngineImportTaskExecutor;

	@Inject
	private BatchEngineImportTaskLocalService
		_batchEngineImportTaskLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	private static class TestBatchEngineImportTaskExceptionHandler
		implements BatchEngineImportTaskExceptionHandler {

		@Override
		public void handle(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			Exception exception, Object item) {

			_batchEngineImportTask = batchEngineImportTask;
			_exception = exception;
			_item = item;
		}

		private BatchEngineImportTask _batchEngineImportTask;
		private Exception _exception;
		private Object _item;

	}

}