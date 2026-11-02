/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.writer;

import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Ivica Cardic
 */
public class JSONLBatchEngineExportTaskItemWriterImplTest
	extends BaseBatchEngineExportTaskItemWriterImplTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testWriteRowsToFailingOutputStream() throws Exception {
		try {
			JSONLBatchEngineExportTaskItemWriterImpl
				jsonlBatchEngineExportTaskItemWriterImpl =
					new JSONLBatchEngineExportTaskItemWriterImpl(
						Collections.emptyList(),
						new OutputStream() {

							@Override
							public void write(int b) throws IOException {
								throw new IOException("Disk is full");
							}

						});

			for (Item[] items : getItemGroups()) {
				jsonlBatchEngineExportTaskItemWriterImpl.write(
					Arrays.asList(items));
			}

			jsonlBatchEngineExportTaskItemWriterImpl.close();

			Assert.fail();
		}
		catch (IOException ioException) {
			Assert.assertEquals("Disk is full", ioException.getMessage());
		}
	}

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
		_testWriteRows(Collections.emptyList());
	}

	private String _getExpectedContent(
		List<String> fieldNames, List<Item> items) {

		StringBundler sb = new StringBundler();

		if (fieldNames.isEmpty()) {
			fieldNames = jsonFieldNames;
		}

		for (Item item : items) {
			sb.append(getItemJSONContent(fieldNames, item));
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private void _testWriteRows(List<String> fieldNames) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		try (JSONLBatchEngineExportTaskItemWriterImpl
				jsonlBatchEngineExportTaskItemWriterImpl =
					new JSONLBatchEngineExportTaskItemWriterImpl(
						fieldNames, unsyncByteArrayOutputStream)) {

			for (Item[] items : getItemGroups()) {
				jsonlBatchEngineExportTaskItemWriterImpl.write(
					Arrays.asList(items));
			}
		}

		String content = unsyncByteArrayOutputStream.toString();

		JSONAssert.assertEquals(
			_getExpectedContent(fieldNames, getItems()), content, true);
	}

}