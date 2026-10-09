/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.engine.internal.writer;

import com.liferay.batch.engine.unit.BatchEngineUnitConfiguration;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Igor Beslic
 */
public class JSONTBatchEngineExportTaskItemWriterImplTest
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
		_testWriteRows(Collections.emptyList());
	}

	private String _getExpectedContent(
		BatchEngineUnitConfiguration batchEngineUnitConfiguration,
		List<String> fieldNames, List<Item> items) {

		StringBundler sb = new StringBundler();

		sb.append("{\"actions\":\n{\"createBatch\": {\"href\": \"");

		String endpoint =
			"/o/headless-batch-engine/v1.0/import-task/" +
				batchEngineUnitConfiguration.getClassName();

		sb.append(endpoint);

		sb.append("\", \"method\": \"POST\"}, \"deleteBatch\": {\"href\": \"");
		sb.append(endpoint);
		sb.append(
			"\", \"method\": \"DELETE\"}, \"updateBatch\": {\"href\": \"");
		sb.append(endpoint);
		sb.append("\", \"method\": \"PUT\"}},\n\"configuration\":\n");
		sb.append("{\"callbackURL\": null, \"className\": \"");
		sb.append(batchEngineUnitConfiguration.getClassName());
		sb.append("\",\n\"companyId\": ");
		sb.append(batchEngineUnitConfiguration.getCompanyId());
		sb.append(",\n\"multiCompany\": ");
		sb.append(batchEngineUnitConfiguration.isMultiCompany());
		sb.append(",\n\"userId\": ");
		sb.append(batchEngineUnitConfiguration.getUserId());
		sb.append(",\n\"version\": \"");
		sb.append(batchEngineUnitConfiguration.getVersion());
		sb.append("\"\n},");

		if (fieldNames.isEmpty()) {
			fieldNames = jsonFieldNames;
		}

		sb.append("\"items\": [\n");

		Iterator<Item> iterator = items.iterator();

		while (iterator.hasNext()) {
			Item item = iterator.next();

			sb.append(getItemJSONContent(fieldNames, item));

			if (!iterator.hasNext()) {
				continue;
			}

			sb.append(",\n");
		}

		sb.append("\n]\n}");

		return sb.toString();
	}

	private void _testWriteRows(List<String> fieldNames) throws Exception {
		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		BatchEngineUnitConfiguration batchEngineUnitConfiguration =
			new BatchEngineUnitConfiguration();

		batchEngineUnitConfiguration.setClassName(Item.class.getName());
		batchEngineUnitConfiguration.setVersion("v1.0");

		try (JSONTBatchEngineExportTaskItemWriterImpl
				jsontBatchEngineExportTaskItemWriterImpl =
					new JSONTBatchEngineExportTaskItemWriterImpl(
						batchEngineUnitConfiguration, fieldNames,
						unsyncByteArrayOutputStream)) {

			for (Item[] items : getItemGroups()) {
				jsontBatchEngineExportTaskItemWriterImpl.write(
					Arrays.asList(items));
			}
		}

		JSONAssert.assertEquals(
			_getExpectedContent(
				batchEngineUnitConfiguration, fieldNames, getItems()),
			unsyncByteArrayOutputStream.toString(), true);
	}

}