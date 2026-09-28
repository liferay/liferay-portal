/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.sanitizer;

import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.configuration.BatchEngineTaskCompanyConfiguration;
import com.liferay.batch.engine.internal.reader.BatchEngineImportTaskItemReader;
import com.liferay.batch.engine.internal.reader.CSVBatchEngineImportTaskItemReaderImpl;
import com.liferay.batch.engine.internal.reader.JSONBatchEngineImportTaskItemReaderImpl;
import com.liferay.batch.engine.internal.reader.JSONLBatchEngineImportTaskItemReaderImpl;
import com.liferay.batch.engine.internal.reader.XLSBatchEngineImportTaskItemReaderImpl;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.batch.engine.model.impl.BatchEngineImportTaskImpl;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class BatchEngineTaskContentSanitizerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSanitizeCSV() throws Exception {
		String login = StringUtil.randomString();
		String password = StringUtil.randomString();

		byte[] bytes = _sanitize(
			BatchEngineTaskContentType.CSV, Collections.emptyMap(),
			StringBundler.concat(
				"login,password\n", login, StringPool.COMMA, password,
				StringPool.NEW_LINE),
			Collections.emptyMap());

		_assertBatchEngineImportTaskItemReaderResult(
			List.of(
				HashMapBuilder.<String, Object>put(
					"login", login
				).build()),
			new CSVBatchEngineImportTaskItemReaderImpl(
				StringPool.COMMA, new UnsyncByteArrayInputStream(bytes),
				Collections.emptyMap()));
	}

	@Test
	public void testSanitizeCSVWithFieldNameMapping() throws Exception {
		String login = StringUtil.randomString();
		String password = StringUtil.randomString();

		byte[] bytes = _sanitize(
			BatchEngineTaskContentType.CSV,
			HashMapBuilder.<String, Serializable>put(
				"name", "login"
			).put(
				"secret", "password"
			).build(),
			StringBundler.concat(
				"name;secret\n", login, StringPool.SEMICOLON, password,
				StringPool.NEW_LINE),
			HashMapBuilder.<String, Serializable>put(
				"delimiter", StringPool.SEMICOLON
			).build());

		_assertBatchEngineImportTaskItemReaderResult(
			List.of(
				HashMapBuilder.<String, Object>put(
					"name", login
				).build()),
			new CSVBatchEngineImportTaskItemReaderImpl(
				StringPool.COMMA, new UnsyncByteArrayInputStream(bytes),
				HashMapBuilder.<String, Serializable>put(
					"delimiter", StringPool.SEMICOLON
				).build()));
	}

	@Test
	public void testSanitizeCSVWithoutHeaders() throws Exception {
		Map<String, Serializable> parameters =
			HashMapBuilder.<String, Serializable>put(
				"containsHeaders", "false"
			).build();

		String login = StringUtil.randomString();
		String password = StringUtil.randomString();

		byte[] bytes = _sanitize(
			BatchEngineTaskContentType.CSV,
			HashMapBuilder.<String, Serializable>put(
				"0", "login"
			).put(
				"1", "password"
			).build(),
			StringBundler.concat(
				login, StringPool.COMMA, password, StringPool.NEW_LINE),
			parameters);

		_assertBatchEngineImportTaskItemReaderResult(
			List.of(
				HashMapBuilder.<String, Object>put(
					"0", login
				).build()),
			new CSVBatchEngineImportTaskItemReaderImpl(
				StringPool.COMMA, new UnsyncByteArrayInputStream(bytes),
				parameters));
	}

	@Test
	public void testSanitizeJSON() throws Exception {
		String login = StringUtil.randomString();
		String password = StringUtil.randomString();

		byte[] bytes = _sanitize(
			BatchEngineTaskContentType.JSON, Collections.emptyMap(),
			StringBundler.concat(
				"[{\"credential\": {\"login\": \"", login,
				"\", \"password\": \"", password, "\"}, \"login\": \"", login,
				"\", \"password\": \"", password, "\"}]"),
			Collections.emptyMap());

		_assertBatchEngineImportTaskItemReaderResult(
			List.of(
				HashMapBuilder.<String, Object>put(
					"credential", Collections.singletonMap("login", login)
				).put(
					"login", login
				).build()),
			new JSONBatchEngineImportTaskItemReaderImpl(
				Collections.emptyList(),
				new UnsyncByteArrayInputStream(bytes)));
	}

	@Test
	public void testSanitizeJSONL() throws Exception {
		String login1 = StringUtil.randomString();
		String login2 = StringUtil.randomString();
		String password = StringUtil.randomString();

		byte[] bytes = _sanitize(
			BatchEngineTaskContentType.JSONL, Collections.emptyMap(),
			StringBundler.concat(
				"{\"login\": \"", login1, "\", \"password\": \"", password,
				"\"}\n{\"login\": \"", login2, "\", \"password\": \"", password,
				"\"}\n"),
			Collections.emptyMap());

		_assertBatchEngineImportTaskItemReaderResult(
			List.of(
				Collections.singletonMap("login", login1),
				Collections.singletonMap("login", login2)),
			new JSONLBatchEngineImportTaskItemReaderImpl(
				Collections.emptyList(),
				new UnsyncByteArrayInputStream(bytes)));
	}

	@Test
	public void testSanitizeJSONWithFieldNameMapping() throws Exception {
		Map<String, Serializable> fieldNameMapping =
			HashMapBuilder.<String, Serializable>put(
				"account", "credential"
			).put(
				"name", "login"
			).put(
				"secret", "password"
			).build();

		String login = StringUtil.randomString();
		String password = StringUtil.randomString();

		byte[] bytes = _sanitize(
			BatchEngineTaskContentType.JSON, fieldNameMapping,
			StringBundler.concat(
				"[{\"account\": {\"login\": \"", login, "\", \"password\": \"",
				password, "\"}, \"name\": \"", login, "\", \"secret\": \"",
				password, "\"}]"),
			Collections.emptyMap());

		_assertBatchEngineImportTaskItemReaderResult(
			List.of(
				HashMapBuilder.<String, Object>put(
					"account", Collections.singletonMap("login", login)
				).put(
					"name", login
				).build()),
			new JSONBatchEngineImportTaskItemReaderImpl(
				new ArrayList<>(fieldNameMapping.keySet()),
				new UnsyncByteArrayInputStream(bytes)));
	}

	@Test
	public void testSanitizeMalformedCSV() throws Exception {
		Assert.assertThrows(
			Exception.class,
			() -> _sanitize(
				BatchEngineTaskContentType.CSV, Collections.emptyMap(),
				StringBundler.concat(
					"login,password\n", StringUtil.randomString(), ",\"",
					StringUtil.randomString()),
				Collections.emptyMap()));
	}

	@Test
	public void testSanitizeMalformedJSON() throws Exception {
		Assert.assertThrows(
			Exception.class,
			() -> _sanitize(
				BatchEngineTaskContentType.JSON, Collections.emptyMap(),
				"[{\"login\": \"" + StringUtil.randomString() +
					"\", \"password\": ",
				Collections.emptyMap()));
		Assert.assertThrows(
			Exception.class,
			() -> _sanitize(
				BatchEngineTaskContentType.JSON, Collections.emptyMap(),
				"{\"password\": \"" + StringUtil.randomString() + "\"}",
				Collections.emptyMap()));
	}

	@Test
	public void testSanitizeMalformedJSONL() throws Exception {
		Assert.assertThrows(
			Exception.class,
			() -> _sanitize(
				BatchEngineTaskContentType.JSONL, Collections.emptyMap(),
				"{\"login\": \"" + StringUtil.randomString() +
					"\", \"password\": \n",
				Collections.emptyMap()));
	}

	@Test
	public void testSanitizeXLS() throws Exception {
		String login = StringUtil.randomString();
		String password = StringUtil.randomString();

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook()) {
			Sheet sheet = xssfWorkbook.createSheet();

			_createRow(sheet, 0, "login", "password");
			_createRow(sheet, 1, login, password);

			xssfWorkbook.write(unsyncByteArrayOutputStream);
		}

		byte[] bytes = _sanitize(
			BatchEngineTaskContentType.XLSX, Collections.emptyMap(),
			unsyncByteArrayOutputStream.toByteArray(), Collections.emptyMap());

		_assertBatchEngineImportTaskItemReaderResult(
			List.of(
				HashMapBuilder.<String, Object>put(
					"login", login
				).build()),
			new XLSBatchEngineImportTaskItemReaderImpl(
				Collections.emptyList(),
				new UnsyncByteArrayInputStream(bytes)));

		try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(
				new UnsyncByteArrayInputStream(bytes))) {

			SharedStrings sharedStrings = xssfWorkbook.getSharedStringSource();

			for (int i = 0; i < sharedStrings.getUniqueCount(); i++) {
				RichTextString richTextString = sharedStrings.getItemAt(i);

				Assert.assertNotEquals(password, richTextString.getString());
			}
		}
	}

	private void _assertBatchEngineImportTaskItemReaderResult(
			List<Map<String, Object>> expectedList,
			BatchEngineImportTaskItemReader batchEngineImportTaskItemReader)
		throws Exception {

		List<Map<String, Object>> list = new ArrayList<>();

		try {
			Map<String, Object> map = null;

			while ((map = batchEngineImportTaskItemReader.read()) != null) {
				list.add(_normalize(map));
			}
		}
		finally {
			batchEngineImportTaskItemReader.close();
		}

		Assert.assertEquals(expectedList, list);
	}

	private BatchEngineImportTask _createBatchEngineImportTask(
		BatchEngineTaskContentType batchEngineTaskContentType,
		Map<String, Serializable> fieldNameMapping,
		Map<String, Serializable> parameters) {

		BatchEngineImportTask batchEngineImportTask =
			new BatchEngineImportTaskImpl();

		batchEngineImportTask.setContentType(batchEngineTaskContentType.name());
		batchEngineImportTask.setFieldNameMapping(fieldNameMapping);
		batchEngineImportTask.setParameters(parameters);

		return batchEngineImportTask;
	}

	private ConfigurationProvider _createConfigurationProvider() {
		try {
			BatchEngineTaskCompanyConfiguration
				batchEngineTaskCompanyConfiguration = Mockito.mock(
					BatchEngineTaskCompanyConfiguration.class);

			Mockito.doReturn(
				StringPool.COMMA
			).when(
				batchEngineTaskCompanyConfiguration
			).csvFileColumnDelimiter();

			ConfigurationProvider configurationProvider = Mockito.mock(
				ConfigurationProvider.class);

			Mockito.doReturn(
				batchEngineTaskCompanyConfiguration
			).when(
				configurationProvider
			).getCompanyConfiguration(
				Mockito.eq(BatchEngineTaskCompanyConfiguration.class),
				Mockito.anyLong()
			);

			return configurationProvider;
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private void _createRow(Sheet sheet, int rowNum, String... values) {
		Row row = sheet.createRow(rowNum);

		for (int i = 0; i < values.length; i++) {
			Cell cell = row.createCell(i);

			cell.setCellValue(values[i]);
		}
	}

	private Map<String, Object> _normalize(Map<String, Object> map) {
		Map<String, Object> normalizedMap = new HashMap<>();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();

			if (value == null) {
				continue;
			}

			normalizedMap.put(entry.getKey(), value);
		}

		return normalizedMap;
	}

	private byte[] _sanitize(
			BatchEngineTaskContentType batchEngineTaskContentType,
			Map<String, Serializable> fieldNameMapping, byte[] content,
			Map<String, Serializable> parameters)
		throws Exception {

		BatchEngineTaskContentSanitizer batchEngineTaskContentSanitizer =
			new BatchEngineTaskContentSanitizer(
				_createBatchEngineImportTask(
					batchEngineTaskContentType, fieldNameMapping, parameters),
				_configurationProvider, _passwordFieldNames);

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		batchEngineTaskContentSanitizer.sanitize(
			new UnsyncByteArrayInputStream(_zip(content)),
			unsyncByteArrayOutputStream);

		try (ZipInputStream zipInputStream = new ZipInputStream(
				new UnsyncByteArrayInputStream(
					unsyncByteArrayOutputStream.toByteArray()))) {

			ZipEntry zipEntry = zipInputStream.getNextEntry();

			Assert.assertEquals(_ZIP_ENTRY_NAME, zipEntry.getName());

			return zipInputStream.readAllBytes();
		}
	}

	private byte[] _sanitize(
			BatchEngineTaskContentType batchEngineTaskContentType,
			Map<String, Serializable> fieldNameMapping, String content,
			Map<String, Serializable> parameters)
		throws Exception {

		return _sanitize(
			batchEngineTaskContentType, fieldNameMapping,
			content.getBytes(StandardCharsets.UTF_8), parameters);
	}

	private byte[] _zip(byte[] bytes) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				unsyncByteArrayOutputStream)) {

			zipOutputStream.putNextEntry(new ZipEntry(_ZIP_ENTRY_NAME));

			zipOutputStream.write(bytes);

			zipOutputStream.closeEntry();
		}

		return unsyncByteArrayOutputStream.toByteArray();
	}

	private static final String _ZIP_ENTRY_NAME = "import.data";

	private static final Set<String> _passwordFieldNames = Set.of(
		"credential.password", "password");

	private final ConfigurationProvider _configurationProvider =
		_createConfigurationProvider();

}