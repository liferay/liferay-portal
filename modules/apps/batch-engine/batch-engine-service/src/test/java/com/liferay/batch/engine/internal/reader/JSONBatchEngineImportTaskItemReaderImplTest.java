/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.reader;

import com.liferay.batch.engine.exception.BatchEngineImportTaskExecutorException;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ivica Cardic
 */
public class JSONBatchEngineImportTaskItemReaderImplTest
	extends BaseBatchEngineImportTaskItemReaderImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testColumnMapping() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						new String[] {
							"createDate1", "description1", "id1", "name1"
						},
						new Object[][] {
							{
								"\"" + createDateString + "\"",
								"\"sample description\"", 1,
								"{\"en\": \"sample name\", \"hr\": \"naziv\"}"
							}
						})) {

			validate(
				createDateString, "sample description", 1L,
				HashMapBuilder.put(
					"createDate1", "createDate"
				).put(
					"description1", "description"
				).put(
					"id1", "id"
				).put(
					"name1", "name"
				).build(),
				jsonBatchEngineImportTaskItemReaderImpl.read(),
				HashMapBuilder.put(
					"en", "sample name"
				).put(
					"hr", "naziv"
				).build());
		}
	}

	@Test
	public void testColumnMappingWithUndefinedColumn() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						new String[] {
							"createDate1", "description1", "id1", "name1"
						},
						new Object[][] {
							{
								"\"" + createDateString + "\"",
								"\"sample description\"", 1,
								"{\"en\": \"sample name\", \"hr\": \"naziv\"}"
							}
						})) {

			validate(
				createDateString, "sample description", 1L,
				HashMapBuilder.put(
					"createDate1", "createDate"
				).put(
					"description1", "description"
				).put(
					"id1", "id"
				).build(),
				jsonBatchEngineImportTaskItemReaderImpl.read(), null);
		}
	}

	@Test
	public void testColumnMappingWithUndefinedTargetColumn() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						new String[] {
							"createDate1", "description1", "id1", "name1"
						},
						new Object[][] {
							{
								"\"" + createDateString + "\"",
								"\"sample description\"", 1,
								"{\"en\": \"sample name\", \"hr\": \"naziv\"}"
							}
						})) {

			validate(
				createDateString, "sample description", 1L,
				new HashMap<String, String>() {
					{
						put("createDate1", "createDate");
						put("description1", "description");
						put("id1", "id");
						put("name1", null);
					}
				},
				jsonBatchEngineImportTaskItemReaderImpl.read(), null);
		}
	}

	@Test
	public void testInvalidColumnMapping() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						new String[] {
							"createDate1", "description1", "id1", "name1"
						},
						new Object[][] {
							{
								"\"" + createDateString + "\"",
								"\"sample description\"", 1,
								"{\"en\": \"sample name\", \"hr\": \"naziv\"}"
							}
						})) {

			try {
				validate(
					createDateString, "sample description", null,
					HashMapBuilder.put(
						"createDate1", "description"
					).put(
						"description1", "createDate"
					).put(
						"id1", "id"
					).put(
						"name1", "name"
					).build(),
					jsonBatchEngineImportTaskItemReaderImpl.read(),
					HashMapBuilder.put(
						"en", "sample name"
					).put(
						"hr", "naziv"
					).build());

				Assert.fail();
			}
			catch (BatchEngineImportTaskExecutorException
						batchEngineImportTaskExecutorException) {
			}
		}
	}

	@Test
	public void testJSONStringColumnMapping() throws Exception {

		// As JSON object

		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						new String[] {"jsonString1"},
						new Object[][] {{"{\"key\": \"value\"}"}})) {

			Item item = getItem(
				HashMapBuilder.put(
					"jsonString1", "jsonString"
				).build(),
				jsonBatchEngineImportTaskItemReaderImpl.read());

			Assert.assertEquals("{\"key\":\"value\"}", item.getJsonString());
		}

		// As JSON string

		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						new String[] {"jsonString1"},
						new Object[][] {
							{"\"{\\\"key\\\": \\\"value\\\"}\""}
						})) {

			Item item = getItem(
				HashMapBuilder.put(
					"jsonString1", "jsonString"
				).build(),
				jsonBatchEngineImportTaskItemReaderImpl.read());

			Assert.assertEquals("{\"key\": \"value\"}", item.getJsonString());
		}
	}

	@Test
	public void testReadInvalidRow() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						_FIELD_NAMES,
						new Object[][] {
							{
								null, "\"sample description\"", 1,
								"{\"en\": \"sample name\", \"hr\": \"naziv\"}",
								"\"unknown column\""
							}
						})) {

			try {
				validate(
					createDateString, "sample description", null,
					Collections.emptyMap(),
					jsonBatchEngineImportTaskItemReaderImpl.read(),
					HashMapBuilder.put(
						"en", "sample name"
					).put(
						"hr", "naziv"
					).build());

				Assert.fail();
			}
			catch (BatchEngineImportTaskExecutorException
						batchEngineImportTaskExecutorException) {
			}
		}
	}

	@Test
	public void testReadMultipleRows() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						_FIELD_NAMES,
						new Object[][] {
							{
								"\"" + createDateString + "\"",
								"\"sample description 1\"", 1,
								"{\"en\": \"sample name 1\", \"hr\": \"naziv " +
									"1\"}"
							},
							{
								"\"" + createDateString + "\"",
								"\"sample description 2\"", 2,
								"{\"en\": \"sample name 2\", \"hr\": \"naziv " +
									"2\"}"
							}
						})) {

			for (int i = 1; i < 3; i++) {
				long rowCount = i;

				validate(
					createDateString, "sample description " + rowCount,
					rowCount, Collections.emptyMap(),
					jsonBatchEngineImportTaskItemReaderImpl.read(),
					HashMapBuilder.put(
						"en", "sample name " + rowCount
					).put(
						"hr", "naziv " + rowCount
					).build());
			}
		}
	}

	@Test
	public void testReadRowsWithCommaInsideQuotes() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						_FIELD_NAMES,
						new Object[][] {
							{
								"\"" + createDateString + "\"",
								"\"hey, here is comma inside\"", 1,
								"{\"en\": \"sample name\", \"hr\": \"naziv\"}"
							}
						})) {

			validate(
				createDateString, "hey, here is comma inside", 1L,
				Collections.emptyMap(),
				jsonBatchEngineImportTaskItemReaderImpl.read(),
				HashMapBuilder.put(
					"en", "sample name"
				).put(
					"hr", "naziv"
				).build());
		}
	}

	@Test
	public void testReadRowsWithLessValues() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						_FIELD_NAMES, new Object[][] {{"null", "null", 1}})) {

			validate(
				null, null, 1L, Collections.emptyMap(),
				jsonBatchEngineImportTaskItemReaderImpl.read(), null);
		}
	}

	@Test
	public void testReadRowsWithNullValues() throws Exception {
		try (JSONBatchEngineImportTaskItemReaderImpl
				jsonBatchEngineImportTaskItemReaderImpl =
					_getJSONBatchEngineImportTaskItemReader(
						_FIELD_NAMES,
						new Object[][] {
							{
								"\"" + createDateString + "\"", "null", 1,
								"{\"hr\": \"naziv 1\"}"
							},
							{
								"\"" + createDateString + "\"",
								"\"sample description 2\"", 2,
								"{\"en\": \"sample name 2\", \"hr\": \"naziv " +
									"2\"}"
							}
						})) {

			validate(
				createDateString, null, 1L, Collections.emptyMap(),
				jsonBatchEngineImportTaskItemReaderImpl.read(),
				HashMapBuilder.put(
					"hr", "naziv 1"
				).build());

			validate(
				createDateString, "sample description 2", 2L,
				Collections.emptyMap(),
				jsonBatchEngineImportTaskItemReaderImpl.read(),
				HashMapBuilder.put(
					"en", "sample name 2"
				).put(
					"hr", "naziv 2"
				).build());
		}
	}

	private byte[] _getContent(String[] columnNames, Object[][] rowValues) {
		StringBundler sb = new StringBundler();

		sb.append("[");

		for (Object[] singleRowValues : rowValues) {
			sb.append("{");

			for (int j = 0; j < singleRowValues.length; j++) {
				if (singleRowValues[j] != null) {
					sb.append("\"");
					sb.append(columnNames[j]);
					sb.append("\": ");
					sb.append(singleRowValues[j]);
					sb.append(",");
				}
			}

			sb.setIndex(sb.index() - 1);
			sb.append("},");
		}

		sb.setIndex(sb.index() - 1);
		sb.append("}]");

		String content = sb.toString();

		return content.getBytes();
	}

	private JSONBatchEngineImportTaskItemReaderImpl
			_getJSONBatchEngineImportTaskItemReader(
				String[] columnNames, Object[][] rowValues)
		throws IOException {

		return new JSONBatchEngineImportTaskItemReaderImpl(
			Collections.emptyList(),
			new ByteArrayInputStream(_getContent(columnNames, rowValues)));
	}

	private static final String[] _FIELD_NAMES = {
		"createDate", "description", "id", "name", "unknownColumn"
	};

}