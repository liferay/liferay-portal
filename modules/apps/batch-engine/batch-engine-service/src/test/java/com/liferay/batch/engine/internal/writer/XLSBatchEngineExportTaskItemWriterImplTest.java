/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.writer;

import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.CSVUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Ivica Cardic
 */
public class XLSBatchEngineExportTaskItemWriterImplTest
	extends BaseBatchEngineExportTaskItemWriterImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testWriteRowsWithDefinedFieldNames1() throws Exception {
		_testWriteRows(Arrays.asList("createDate", "description", "id"));
	}

	@Test
	public void testWriteRowsWithDefinedFieldNames2() throws Exception {
		_testWriteRows(
			Arrays.asList("createDate", "description", "id", "name"));
	}

	@Test
	public void testWriteRowsWithDefinedFieldNames3() throws Exception {
		_testWriteRows(Arrays.asList("createDate", "id", "name"));
	}

	@Test
	public void testWriteRowsWithDefinedFieldNames4() throws Exception {
		_testWriteRows(
			Arrays.asList("id", "name", "description", "createDate"));
	}

	@Test
	public void testWriteRowsWithEmptyFieldNames() throws Exception {
		try {
			_testWriteRows(Collections.emptyList());

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}
	}

	private byte[] _getExpectedContent(
			List<String> fieldNames, List<Item> items)
		throws Exception {

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet();

			_populateRow(sheet.createRow(0), fieldNames);

			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);

				List<Object> values = new ArrayList<>();

				for (String fieldName : fieldNames) {
					int index = fieldName.indexOf(CharPool.UNDERLINE);

					if (index == -1) {
						ObjectValuePair<Field, Method> objectValuePair =
							fieldNameObjectValuePairs.get(fieldName);

						Method method = objectValuePair.getValue();

						values.add(method.invoke(item));
					}
					else {
						ObjectValuePair<Field, Method> objectValuePair =
							fieldNameObjectValuePairs.get(
								fieldName.substring(0, index));

						Method method = objectValuePair.getValue();

						Map<?, ?> valueMap = (Map<?, ?>)method.invoke(item);

						values.add(
							valueMap.get(fieldName.substring(index + 1)));
					}
				}

				_populateRow(sheet.createRow(i + 1), values);
			}

			ByteArrayOutputStream byteArrayOutputStream =
				new ByteArrayOutputStream();

			workbook.write(byteArrayOutputStream);

			return byteArrayOutputStream.toByteArray();
		}
	}

	private Iterator<Row> _getExpectedRowIterator(List<String> fieldNames)
		throws Exception {

		Workbook expectedWorkbook = new XSSFWorkbook(
			new ByteArrayInputStream(
				_getExpectedContent(fieldNames, getItems())));

		Sheet expectedSheet = expectedWorkbook.getSheetAt(0);

		return expectedSheet.rowIterator();
	}

	private void _populateRow(Row row, List<?> cellValues) {
		for (int i = 0; i < cellValues.size(); i++) {
			Object value = cellValues.get(i);

			Cell cell = row.createCell(i);

			if (value instanceof Boolean) {
				cell.setCellValue((Boolean)value);
			}
			else if (value instanceof Date) {
				cell.setCellValue(dateFormat.format(value));
			}
			else if (value instanceof Map) {
				Map<?, ?> map = (Map<?, ?>)value;

				StringBundler sb = new StringBundler(map.size() * 3);

				Set<? extends Map.Entry<?, ?>> entries = map.entrySet();

				Iterator<? extends Map.Entry<?, ?>> iterator =
					entries.iterator();

				while (iterator.hasNext()) {
					Map.Entry<?, ?> entry = iterator.next();

					sb.append(CSVUtil.encode(entry.getKey()));

					sb.append(StringPool.COLON);

					if (entry.getValue() != null) {
						sb.append(CSVUtil.encode(entry.getValue()));
					}
					else {
						sb.append(StringPool.BLANK);
					}

					if (!iterator.hasNext()) {
						continue;
					}

					sb.append(StringPool.COMMA_AND_SPACE);
				}

				cell.setCellValue(sb.toString());
			}
			else if (value instanceof Number) {
				Number cellValue = (Number)value;

				cell.setCellValue(cellValue.doubleValue());
			}
			else {
				if (value == null) {
					cell.setCellValue(StringPool.BLANK);

					continue;
				}

				cell.setCellValue(String.valueOf(value));
			}
		}
	}

	private void _testWriteRows(List<String> fieldNames) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (XLSBatchEngineExportTaskItemWriterImpl
				xlsBatchEngineExportTaskItemWriterImpl =
					new XLSBatchEngineExportTaskItemWriterImpl(
						null, 0, fieldNameObjectValuePairs, fieldNames,
						unsyncByteArrayOutputStream, null)) {

			for (Item[] items : getItemGroups()) {
				xlsBatchEngineExportTaskItemWriterImpl.write(
					Arrays.asList(items));
			}
		}

		Workbook actualWorkbook = new XSSFWorkbook(
			new ByteArrayInputStream(
				unsyncByteArrayOutputStream.toByteArray()));

		Sheet actualSheet = actualWorkbook.getSheetAt(0);

		Iterator<Row> actualRowIterator = actualSheet.rowIterator();

		Iterator<Row> expectedRowIterator = _getExpectedRowIterator(fieldNames);

		while (expectedRowIterator.hasNext()) {
			Row actualRow = actualRowIterator.next();
			Row expectedRow = expectedRowIterator.next();

			for (int i = 0; i < expectedRow.getLastCellNum(); i++) {
				Cell actualCell = actualRow.getCell(i);

				Cell expectedCell = expectedRow.getCell(i);

				CellType cellType = expectedCell.getCellType();

				if (cellType == CellType.BOOLEAN) {
					Assert.assertEquals(
						expectedCell.getBooleanCellValue(),
						actualCell.getBooleanCellValue());
				}
				else if (cellType == CellType.NUMERIC) {
					Assert.assertEquals(
						expectedCell.getNumericCellValue(),
						actualCell.getNumericCellValue(), 0);
				}
				else {
					Assert.assertEquals(
						expectedCell.getStringCellValue(),
						actualCell.getStringCellValue());
				}
			}
		}
	}

}