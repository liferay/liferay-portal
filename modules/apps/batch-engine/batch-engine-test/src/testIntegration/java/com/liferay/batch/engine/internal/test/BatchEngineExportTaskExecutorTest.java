/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.model.BatchEngineExportTask;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.petra.io.unsync.UnsyncBufferedReader;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import java.sql.Blob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.ZipInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ivica Cardic
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class BatchEngineExportTaskExecutorTest
	extends BaseBatchEngineTaskExecutorTest {

	@BeforeClass
	public static void setUpClass() {
		_objectMapper.addMixIn(BlogPosting.class, BlogPostingMixin.class);
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_parameters = HashMapBuilder.<String, Serializable>put(
			"siteId", TestPropsValues.getGroupId()
		).build();
	}

	@Test
	public void testExportBlogPostingsToCSVFileWithEmptyFieldNames()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_EXPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_testExportBlogPostingsToCSVFile(
				Collections.emptyList(), line -> new Object[0], _parameters,
				false);

			_assertEmptyFieldNames(logCapture);
		}
	}

	@Test
	public void testExportBlogPostingsToCSVFileWithFieldNames()
		throws Exception {

		_testExportBlogPostingsToCSVFile(
			Arrays.asList("articleBody", "datePublished", "headline", "id"),
			_csvFilterFunction, _parameters, true);
	}

	@Test
	public void testExportBlogPostingsToCSVFileWithFilterParameter()
		throws Exception {

		_parameters.put("filter", "headline eq 'headline1'");

		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		List<String> fieldNames = Arrays.asList(
			"articleBody", "datePublished", "headline", "id");

		_exportBlogPostings("CSV", fieldNames, _parameters);

		List<Object[]> rowValuesList = _readRowValuesList(
			_csvFilterFunction,
			_batchEngineExportTaskLocalService.getBatchEngineExportTask(
				_batchEngineExportTask.getBatchEngineExportTaskId()));

		Assert.assertEquals(rowValuesList.toString(), 1, rowValuesList.size());

		_assertExportedValues(
			blogsEntries.get(1), fieldNames, rowValuesList.get(0));
	}

	@Test
	public void testExportBlogPostingsToCSVFileWithSortParameter()
		throws Exception {

		_parameters.put("sort", "headline:desc'");

		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		List<String> fieldNames = Arrays.asList(
			"articleBody", "datePublished", "headline", "id");

		_exportBlogPostings("CSV", fieldNames, _parameters);

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskLocalService.getBatchEngineExportTask(
				_batchEngineExportTask.getBatchEngineExportTaskId());

		blogsEntries.sort(
			Comparator.comparing(
				BlogsEntry::getEntryId,
				(entryId1, entryId2) -> -entryId1.compareTo(entryId2)));

		_assertExportedValues(
			blogsEntries, fieldNames,
			_readRowValuesList(_csvFilterFunction, batchEngineExportTask));
	}

	@Test
	public void testExportBlogPostingsToJSONFileWithEmptyFieldNames()
		throws Exception {

		_testExportBlogPostingsToJSONFile(
			Collections.emptyList(),
			blogPosting -> new Object[] {
				blogPosting.getAlternativeHeadline(),
				blogPosting.getArticleBody(), blogPosting.getDatePublished(),
				blogPosting.getHeadline(), blogPosting.getId(),
				blogPosting.getSiteId()
			},
			_parameters);
	}

	@Test
	public void testExportBlogPostingsToJSONFileWithFieldNames()
		throws Exception {

		_testExportBlogPostingsToJSONFile(
			Arrays.asList(
				"alternativeHeadline", "datePublished", "headline", "id"),
			blogPosting -> new Object[] {
				blogPosting.getAlternativeHeadline(),
				blogPosting.getDatePublished(), blogPosting.getHeadline(),
				blogPosting.getId()
			},
			_parameters);
	}

	@Test
	public void testExportBlogPostingsToJSONLFileWithEmptyFieldNames()
		throws Exception {

		_testExportBlogPostingsToJSONLFile(
			Collections.emptyList(),
			blogPosting -> new Object[] {
				blogPosting.getAlternativeHeadline(),
				blogPosting.getArticleBody(), blogPosting.getDatePublished(),
				blogPosting.getHeadline(), blogPosting.getId(),
				blogPosting.getSiteId()
			},
			_parameters);
	}

	@Test
	public void testExportBlogPostingsToJSONLFileWithFieldNames()
		throws Exception {

		_testExportBlogPostingsToJSONLFile(
			Arrays.asList(
				"alternativeHeadline", "datePublished", "headline", "id"),
			blogPosting -> new Object[] {
				blogPosting.getAlternativeHeadline(),
				blogPosting.getDatePublished(), blogPosting.getHeadline(),
				blogPosting.getId()
			},
			HashMapBuilder.<String, Serializable>put(
				"siteId", TestPropsValues.getGroupId()
			).build());
	}

	@Test
	public void testExportBlogPostingsToXLSFileWithEmptyFieldNames()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_BATCH_ENGINE_EXPORT_TASK_EXECUTOR_IMPL,
				LoggerTestUtil.ERROR)) {

			_testExportBlogPostingsToXLSFile(
				Collections.emptyList(), rowValues -> new Object[0],
				HashMapBuilder.<String, Serializable>put(
					"siteId", TestPropsValues.getGroupId()
				).build(),
				false);
		}
	}

	@Test
	public void testExportBlogPostingsToXLSFileWithFieldNames()
		throws Exception {

		_testExportBlogPostingsToXLSFile(
			Arrays.asList("articleBody", "datePublished", "headline", "id"),
			rowValues -> new Object[] {
				rowValues[0], rowValues[1], rowValues[2], rowValues[3]
			},
			HashMapBuilder.<String, Serializable>put(
				"siteId", TestPropsValues.getGroupId()
			).build(),
			true);
	}

	@Test
	@TestInfo("LPD-65748")
	public void testExportBlogPostingsWithMaxItems() throws Throwable {
		List<BlogsEntry> blogsEntries = addBlogsEntries();

		_batchEngineExportTask =
			_batchEngineExportTaskLocalService.createBatchEngineExportTask(
				RandomTestUtil.randomLong(), null, user.getCompanyId(),
				user.getUserId(), null, BlogPosting.class.getName(), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null, _parameters,
				null);

		int maxItems = Math.floorDiv(ROWS_COUNT, 2);

		TransactionInvokerUtil.invoke(
			TransactionConfig.Factory.create(
				Propagation.REQUIRED, new Class<?>[] {Exception.class}),
			() -> {
				BatchEngineExportTaskExecutor.Result result =
					_batchEngineExportTaskExecutor.execute(
						_batchEngineExportTask,
						new BatchEngineExportTaskExecutor.Settings() {

							@Override
							public int getMaxItems() {
								return maxItems;
							}

							@Override
							public boolean isCompressContent() {
								return false;
							}

							@Override
							public boolean isPersist() {
								return false;
							}

						});

				JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
					StringUtil.read(result.getInputStream()));

				Assert.assertEquals(maxItems, jsonArray.length());

				BatchEngineExportTask resultBatchEngineExportTask =
					result.getBatchEngineExportTask();

				Assert.assertEquals(
					BatchEngineTaskExecuteStatus.COMPLETED.toString(),
					resultBatchEngineExportTask.getExecuteStatus());
				Assert.assertEquals(
					maxItems,
					resultBatchEngineExportTask.getProcessedItemsCount());
				Assert.assertEquals(
					blogsEntries.size(),
					resultBatchEngineExportTask.getTotalItemsCount());

				return null;
			});
	}

	@Test
	@TestInfo("LPD-50699")
	public void testExportBlogPostingsWithoutPersistingContent()
		throws Throwable {

		List<BlogsEntry> blogsEntries = addBlogsEntries();

		int batchEngineExportTasksCount =
			_batchEngineExportTaskLocalService.getBatchEngineExportTasksCount();

		_batchEngineExportTask =
			_batchEngineExportTaskLocalService.createBatchEngineExportTask(
				RandomTestUtil.randomLong(), null, user.getCompanyId(),
				user.getUserId(), null, BlogPosting.class.getName(), "JSON",
				BatchEngineTaskExecuteStatus.INITIAL.name(), null, _parameters,
				null);

		TransactionInvokerUtil.invoke(
			TransactionConfig.Factory.create(
				Propagation.REQUIRED, new Class<?>[] {Exception.class}),
			() -> {
				BatchEngineExportTaskExecutor.Result result =
					_batchEngineExportTaskExecutor.execute(
						_batchEngineExportTask,
						new BatchEngineExportTaskExecutor.Settings() {

							@Override
							public boolean isCompressContent() {
								return false;
							}

							@Override
							public boolean isPersist() {
								return false;
							}

						});

				JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
					StringUtil.read(result.getInputStream()));

				Assert.assertTrue(jsonArray.length() >= blogsEntries.size());

				Assert.assertEquals(
					batchEngineExportTasksCount,
					_batchEngineExportTaskLocalService.
						getBatchEngineExportTasksCount());

				BatchEngineExportTask resultBatchEngineExportTask =
					result.getBatchEngineExportTask();

				Assert.assertEquals(
					BatchEngineTaskExecuteStatus.COMPLETED.toString(),
					resultBatchEngineExportTask.getExecuteStatus());
				Assert.assertEquals(
					blogsEntries.size(),
					resultBatchEngineExportTask.getProcessedItemsCount());
				Assert.assertEquals(
					blogsEntries.size(),
					resultBatchEngineExportTask.getTotalItemsCount());

				Blob content = resultBatchEngineExportTask.getContent();

				Assert.assertEquals(0, content.length());

				return null;
			});
	}

	@Test
	public void testExportBlogPostingsWithUncompressedContent() {
		AssertUtils.assertFailure(
			IllegalArgumentException.class,
			"Uncompressed content cannot be stored in the database",
			() -> _batchEngineExportTaskExecutor.execute(
				_batchEngineExportTaskLocalService.addBatchEngineExportTask(
					null, user.getCompanyId(), user.getUserId(), null,
					BlogPosting.class.getName(), "JSON",
					BatchEngineTaskExecuteStatus.INITIAL.name(), null,
					_parameters, null),
				new BatchEngineExportTaskExecutor.Settings() {

					@Override
					public boolean isCompressContent() {
						return false;
					}

					@Override
					public boolean isPersist() {
						return true;
					}

				}));
	}

	public abstract class BlogPostingMixin {

		@JsonProperty(access = JsonProperty.Access.READ_WRITE)
		public Long id;

		@JsonProperty(access = JsonProperty.Access.READ_WRITE)
		public Long siteId;

		@JsonProperty(access = JsonProperty.Access.READ_WRITE)
		protected Date dateCreated;

	}

	private void _assertEmptyFieldNames(LogCapture logCapture) {
		List<LogEntry> logEntries = logCapture.getLogEntries();

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		Assert.assertEquals(LoggerTestUtil.ERROR, logEntry.getPriority());

		String message = logEntry.getMessage();

		Assert.assertTrue(
			message.startsWith("Unable to update batch engine export task"));
	}

	private void _assertExportedValues(
			BlogsEntry blogsEntry, List<String> fieldNames, Object[] rowValues)
		throws Exception {

		int index = 0;

		if (fieldNames.isEmpty() || fieldNames.contains(FIELD_NAMES[0])) {
			Assert.assertEquals(blogsEntry.getSubtitle(), rowValues[index++]);
		}

		if (fieldNames.isEmpty() || fieldNames.contains(FIELD_NAMES[1])) {
			Assert.assertEquals(blogsEntry.getContent(), rowValues[index++]);
		}

		if (fieldNames.isEmpty() || fieldNames.contains(FIELD_NAMES[2])) {
			Object value = rowValues[index++];

			if (value instanceof String) {
				value = dateFormat.parse((String)value);
			}

			Assert.assertEquals(blogsEntry.getDisplayDate(), value);
		}

		if (fieldNames.isEmpty() || fieldNames.contains(FIELD_NAMES[3])) {
			Assert.assertEquals(blogsEntry.getTitle(), rowValues[index++]);
		}

		if (fieldNames.isEmpty() || fieldNames.contains("id")) {
			Object value = rowValues[index++];

			if (value instanceof String) {
				value = GetterUtil.getLong(value);
			}

			if (value instanceof Double) {
				Double doubleValue = (Double)value;

				value = doubleValue.longValue();
			}

			Assert.assertEquals(blogsEntry.getEntryId(), value);
		}

		if (fieldNames.isEmpty() || fieldNames.contains("siteId")) {
			Object value = rowValues[index];

			if (value instanceof String) {
				value = GetterUtil.getLong(value);
			}

			if (value instanceof Double) {
				Double doubleValue = (Double)value;

				value = doubleValue.longValue();
			}

			Assert.assertEquals(blogsEntry.getGroupId(), value);
		}
	}

	private void _assertExportedValues(
			List<BlogsEntry> blogsEntries, List<String> fieldNames,
			List<Object[]> rowValuesList)
		throws Exception {

		blogsEntries.sort(Comparator.comparing(BlogsEntry::getEntryId));

		if (fieldNames.contains("id")) {
			rowValuesList.sort(
				Comparator.comparing(
					rowValues -> GetterUtil.getLong(
						rowValues[fieldNames.indexOf("id")])));
		}
		else {
			rowValuesList.sort(
				Comparator.comparing(rowValues -> (Long)rowValues[4]));
		}

		for (int i = 0; i < blogsEntries.size(); i++) {
			_assertExportedValues(
				blogsEntries.get(i), fieldNames,
				rowValuesList.get(i + initialCount));
		}
	}

	private void _assertFailedTask(
		BatchEngineExportTask batchEngineExportTask) {

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.FAILED.toString(),
			batchEngineExportTask.getExecuteStatus());
		Assert.assertEquals(0, batchEngineExportTask.getProcessedItemsCount());
		Assert.assertEquals(0, batchEngineExportTask.getTotalItemsCount());
	}

	private void _assertSuccessTask(
		BatchEngineExportTask batchEngineExportTask) {

		Assert.assertEquals(
			BatchEngineTaskExecuteStatus.COMPLETED.toString(),
			batchEngineExportTask.getExecuteStatus());

		Assert.assertEquals(
			initialCount + ROWS_COUNT,
			batchEngineExportTask.getProcessedItemsCount());
		Assert.assertEquals(
			initialCount + ROWS_COUNT,
			batchEngineExportTask.getTotalItemsCount());
	}

	private void _exportBlogPostings(
			String contentType, List<String> fieldNames,
			Map<String, Serializable> parameters)
		throws Exception {

		parameters.put("siteId", TestPropsValues.getGroupId());

		_batchEngineExportTask =
			_batchEngineExportTaskLocalService.addBatchEngineExportTask(
				null, user.getCompanyId(), user.getUserId(), null,
				BlogPosting.class.getName(), contentType,
				BatchEngineTaskExecuteStatus.INITIAL.name(), fieldNames,
				parameters, null);

		_batchEngineExportTaskExecutor.execute(_batchEngineExportTask);
	}

	private ZipInputStream _getZipInputStream(InputStream inputStream)
		throws Exception {

		ZipInputStream zipInputStream = new ZipInputStream(inputStream);

		zipInputStream.getNextEntry();

		return zipInputStream;
	}

	private List<Object[]> _readRowValuesList(
			Function<String, Object[]> filterFunction,
			BatchEngineExportTask batchEngineExportTask)
		throws Exception {

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(
				_getZipInputStream(
					_batchEngineExportTaskLocalService.openContentInputStream(
						batchEngineExportTask.getBatchEngineExportTaskId()))));

		unsyncBufferedReader.readLine();

		String line = null;
		List<Object[]> rowValues = new ArrayList<>();

		while ((line = unsyncBufferedReader.readLine()) != null) {
			rowValues.add(filterFunction.apply(line));
		}

		return rowValues;
	}

	private void _testExportBlogPostingsToCSVFile(
			List<String> fieldNames, Function<String, Object[]> filterFunction,
			Map<String, Serializable> parameters, boolean success)
		throws Exception {

		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_exportBlogPostings("CSV", fieldNames, parameters);

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskLocalService.getBatchEngineExportTask(
				_batchEngineExportTask.getBatchEngineExportTaskId());

		if (!success) {
			_assertFailedTask(batchEngineExportTask);

			return;
		}

		_assertSuccessTask(batchEngineExportTask);

		_assertExportedValues(
			blogsEntries, fieldNames,
			_readRowValuesList(filterFunction, batchEngineExportTask));
	}

	private void _testExportBlogPostingsToJSONFile(
			List<String> fieldNames,
			Function<BlogPosting, Object[]> filterFunction,
			Map<String, Serializable> parameters)
		throws Exception {

		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_exportBlogPostings("JSON", fieldNames, parameters);

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskLocalService.getBatchEngineExportTask(
				_batchEngineExportTask.getBatchEngineExportTaskId());

		_assertSuccessTask(batchEngineExportTask);

		List<BlogPosting> blogPostings = _objectMapper.readValue(
			_getZipInputStream(
				_batchEngineExportTaskLocalService.openContentInputStream(
					batchEngineExportTask.getBatchEngineExportTaskId())),
			new TypeReference<List<BlogPosting>>() {
			});

		List<Object[]> rowValues = new ArrayList<>();

		for (BlogPosting blogPosting : blogPostings) {
			rowValues.add(filterFunction.apply(blogPosting));
		}

		_assertExportedValues(blogsEntries, fieldNames, rowValues);
	}

	private void _testExportBlogPostingsToJSONLFile(
			List<String> fieldNames,
			Function<BlogPosting, Object[]> filterFunction,
			Map<String, Serializable> parameters)
		throws Exception {

		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_exportBlogPostings("JSONL", fieldNames, parameters);

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskLocalService.getBatchEngineExportTask(
				_batchEngineExportTask.getBatchEngineExportTaskId());

		_assertSuccessTask(batchEngineExportTask);

		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new InputStreamReader(
				_getZipInputStream(
					_batchEngineExportTaskLocalService.openContentInputStream(
						batchEngineExportTask.getBatchEngineExportTaskId()))));

		List<BlogPosting> blogPostings = new ArrayList<>();
		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			blogPostings.add(
				_objectMapper.readValue(
					line,
					new TypeReference<BlogPosting>() {
					}));
		}

		List<Object[]> rowValues = new ArrayList<>();

		for (BlogPosting blogPosting : blogPostings) {
			rowValues.add(filterFunction.apply(blogPosting));
		}

		_assertExportedValues(blogsEntries, fieldNames, rowValues);
	}

	private void _testExportBlogPostingsToXLSFile(
			List<String> fieldNames,
			Function<Object[], Object[]> filterFunction,
			Map<String, Serializable> parameters, boolean success)
		throws Exception {

		List<BlogsEntry> blogsEntries = addBlogsEntries();

		assertBlogsEntriesCount();

		_exportBlogPostings("XLS", fieldNames, parameters);

		BatchEngineExportTask batchEngineExportTask =
			_batchEngineExportTaskLocalService.getBatchEngineExportTask(
				_batchEngineExportTask.getBatchEngineExportTaskId());

		if (!success) {
			_assertFailedTask(batchEngineExportTask);

			return;
		}

		_assertSuccessTask(batchEngineExportTask);

		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(
			_getZipInputStream(
				_batchEngineExportTaskLocalService.openContentInputStream(
					batchEngineExportTask.getBatchEngineExportTaskId())));

		Sheet sheet = xssfWorkbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheet.iterator();

		rowIterator.next();

		List<Object[]> rowValues = new ArrayList<>();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			List<Object> values = new ArrayList<>();

			for (Cell cell : row) {
				if (CellType.BOOLEAN == cell.getCellType()) {
					values.add(cell.getBooleanCellValue());
				}
				else if (CellType.NUMERIC == cell.getCellType()) {
					if (DateUtil.isCellDateFormatted(cell)) {
						values.add(cell.getDateCellValue());
					}
					else {
						values.add(cell.getNumericCellValue());
					}
				}
				else {
					values.add(cell.getStringCellValue());
				}
			}

			rowValues.add(filterFunction.apply(values.toArray()));
		}

		_assertExportedValues(blogsEntries, fieldNames, rowValues);
	}

	private static final String
		_CLASS_NAME_BATCH_ENGINE_EXPORT_TASK_EXECUTOR_IMPL =
			"com.liferay.batch.engine.internal." +
				"BatchEngineExportTaskExecutorImpl";

	private static final ObjectMapper _objectMapper = new ObjectMapper();

	private BatchEngineExportTask _batchEngineExportTask;

	@Inject
	private BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;

	@Inject
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	private final Function<String, Object[]> _csvFilterFunction = line -> {
		String[] values = StringUtil.split(line, CharPool.COMMA);

		return new Object[] {values[0], values[1], values[2], values[3]};
	};

	private Map<String, Serializable> _parameters;

}