/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.reader;

import com.liferay.batch.engine.exception.BatchEngineImportTaskExecutorException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ivica Cardic
 */
public class XLSBatchEngineImportTaskItemReaderImplTest
	extends BaseBatchEngineImportTaskItemReaderImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testColumnMapping() throws Exception {
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						new String[] {
							"createDate1", "underscore_field1", "id1",
							"space name1_i18n_en", "space name1_i18n_hr"
						},
						new Object[][] {
							{
								createDateString, "sample description", 1,
								"sample name", "naziv"
							}
						})) {

			validate(
				createDateString, "sample description", 1L,
				HashMapBuilder.put(
					"createDate1", "createDate"
				).put(
					"id1", "id"
				).put(
					"space name1_i18n_en", "name"
				).put(
					"space name1_i18n_hr", "name"
				).put(
					"underscore_field1", "description"
				).build(),
				xlsBatchEngineImportTaskItemReaderImpl.read(),
				HashMapBuilder.put(
					"en", "sample name"
				).put(
					"hr", "naziv"
				).build());
		}
	}

	@Test
	public void testColumnMappingWitUndefinedColumn() throws Exception {
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						new String[] {
							"createDate1", "description1", "id1",
							"name1_i18n_en", "name1_i18n_hr"
						},
						new Object[][] {
							{
								createDateString, "sample description", 1,
								"sample name", "naziv"
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
				xlsBatchEngineImportTaskItemReaderImpl.read(), null);
		}
	}

	@Test
	public void testColumnMappingWitUndefinedTargetColumn() throws Exception {
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						new String[] {
							"createDate1", "description1", "id1",
							"name1_i18n_en", "name1_i18n_hr"
						},
						new Object[][] {
							{
								createDateString, "sample description", 1,
								"sample name", "naziv"
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
				xlsBatchEngineImportTaskItemReaderImpl.read(), null);
		}
	}

	@Test
	public void testInvalidColumnMapping() throws Exception {
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						new String[] {
							"createDate1", "description1", "id1",
							"name1_i18n_en", "name1_i18n_hr"
						},
						new Object[][] {
							{
								createDateString, "sample description", 1,
								"sample name", "naziv"
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
					xlsBatchEngineImportTaskItemReaderImpl.read(),
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
	public void testReadInvalidRow() throws Exception {
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						FIELD_NAMES,
						new Object[][] {
							{
								new Date(), "sample description", 1L,
								"sample name", "naziv", "unknown column"
							}
						})) {

			try {
				xlsBatchEngineImportTaskItemReaderImpl.read();

				Assert.fail();
			}
			catch (ArrayIndexOutOfBoundsException
						arrayIndexOutOfBoundsException) {
			}
		}
	}

	@Test
	public void testReadMultipleRows() throws Exception {
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						FIELD_NAMES,
						new Object[][] {
							{
								createDate, "sample description 1", 1L,
								"sample name 1", "naziv 1"
							},
							{
								createDate, "sample description 2", 2L,
								"sample name 2", "naziv 2"
							}
						})) {

			for (int i = 1; i < 3; i++) {
				long rowCount = i;

				validate(
					createDateString, "sample description " + rowCount,
					rowCount,
					HashMapBuilder.put(
						"createDate", "createDate"
					).put(
						"description", "description"
					).put(
						"id", "id"
					).put(
						"name_i18n_en", "name"
					).put(
						"name_i18n_hr", "name"
					).build(),
					xlsBatchEngineImportTaskItemReaderImpl.read(),
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
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						FIELD_NAMES,
						new Object[][] {
							{
								createDate, "hey, here is comma inside", 1L,
								"sample name", "naziv"
							}
						})) {

			validate(
				createDateString, "hey, here is comma inside", 1L,
				HashMapBuilder.put(
					"createDate", "createDate"
				).put(
					"description", "description"
				).put(
					"id", "id"
				).put(
					"name_i18n_en", "name"
				).put(
					"name_i18n_hr", "name"
				).build(),
				xlsBatchEngineImportTaskItemReaderImpl.read(),
				HashMapBuilder.put(
					"en", "sample name"
				).put(
					"hr", "naziv"
				).build());
		}
	}

	@Test
	public void testReadRowsWithLessValues() throws Exception {
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						FIELD_NAMES, new Object[][] {{null, null, 1}})) {

			validate(
				null, null, 1L, Collections.emptyMap(),
				xlsBatchEngineImportTaskItemReaderImpl.read(), null);
		}
	}

	@Test
	public void testReadRowsWithNullValues() throws Exception {
		try (XLSBatchEngineImportTaskItemReaderImpl
				xlsBatchEngineImportTaskItemReaderImpl =
					_getXLSBatchEngineImportTaskItemReader(
						FIELD_NAMES,
						new Object[][] {
							{createDate, null, 1L, null, "naziv"},
							{
								createDate, "sample description 2", 2L,
								"sample name 2", "naziv 2"
							}
						})) {

			validate(
				createDateString, null, 1L,
				HashMapBuilder.put(
					"createDate", "createDate"
				).put(
					"description", "description"
				).put(
					"id", "id"
				).put(
					"name_i18n_en", "name"
				).put(
					"name_i18n_hr", "name"
				).build(),
				xlsBatchEngineImportTaskItemReaderImpl.read(),
				new HashMap<String, String>() {
					{
						put("en", null);
						put("hr", "naziv");
					}
				});

			validate(
				createDateString, "sample description 2", 2L,
				HashMapBuilder.put(
					"createDate", "createDate"
				).put(
					"description", "description"
				).put(
					"id", "id"
				).put(
					"name_i18n_en", "name"
				).put(
					"name_i18n_hr", "name"
				).build(),
				xlsBatchEngineImportTaskItemReaderImpl.read(),
				HashMapBuilder.put(
					"en", "sample name 2"
				).put(
					"hr", "naziv 2"
				).build());
		}
	}

	private byte[] _getContent(String[] cellNames, Object[][] rowValues)
		throws IOException {

		try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook()) {
			Sheet sheet = xssfWorkbook.createSheet();

			_populateRow(sheet.createRow(0), xssfWorkbook, (Object[])cellNames);

			for (int i = 0; i < rowValues.length; i++) {
				_populateRow(
					sheet.createRow(i + 1), xssfWorkbook, rowValues[i]);
			}

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			xssfWorkbook.write(byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		}
	}

	private XLSBatchEngineImportTaskItemReaderImpl
			_getXLSBatchEngineImportTaskItemReader(
				String[] cellNames, Object[][] rowValues)
		throws IOException {

		return new XLSBatchEngineImportTaskItemReaderImpl(
			Collections.emptyList(),
			new ByteArrayInputStream(_getContent(cellNames, rowValues)));
	}

	private void _populateRow(
		Row row, XSSFWorkbook xssfWorkbook, Object... cellValues) {

		for (int i = 0; i < cellValues.length; i++) {
			Cell cell = row.createCell(i);

			if (cellValues[i] instanceof Boolean) {
				cell.setCellValue((Boolean)cellValues[i]);
			}
			else if (cellValues[i] instanceof Date) {
				CellStyle cellStyle = xssfWorkbook.createCellStyle();

				CreationHelper creationHelper =
					xssfWorkbook.getCreationHelper();

				DataFormat dataFormat = creationHelper.createDataFormat();

				cellStyle.setDataFormat(
					dataFormat.getFormat("yyyy-mm-dd hh:mm:ss"));

				cell.setCellStyle(cellStyle);

				cell.setCellValue((Date)cellValues[i]);
			}
			else if (cellValues[i] instanceof Number) {
				Number value = (Number)cellValues[i];

				cell.setCellValue(value.doubleValue());
			}
			else {
				cell.setCellValue((String)cellValues[i]);
			}
		}
	}

}