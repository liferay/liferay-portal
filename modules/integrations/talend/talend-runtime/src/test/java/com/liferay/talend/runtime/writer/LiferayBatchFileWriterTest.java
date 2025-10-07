/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.writer;

import com.liferay.talend.BaseTestCase;
import com.liferay.talend.properties.batch.LiferayBatchFileProperties;
import com.liferay.talend.runtime.LiferayMockRuntimeContainer;

import java.io.File;

import java.net.URL;

import javax.json.JsonObject;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Igor Beslic
 */
public class LiferayBatchFileWriterTest extends BaseTestCase {

	@Before
	public void setUp() {
		Class<BaseTestCase> baseTestClass = BaseTestCase.class;

		URL systemResource = baseTestClass.getResource("");

		String testPath = systemResource.getPath();

		testPath = testPath.substring(0, testPath.indexOf("test-classes"));

		testPath = testPath + "temp";

		_testTemporaryDirectory = new File(testPath);

		if (!_testTemporaryDirectory.exists()) {
			_testTemporaryDirectory.mkdirs();
		}

		System.out.println(
			"Running test in:" + _testTemporaryDirectory.getAbsolutePath());
	}

	@Test
	public void testWrite() throws Exception {
		JsonObject oasJsonObject = readObject("openapi_data_types.json");

		Schema entitySchema = getEntitySchema("BigDecimal", oasJsonObject);

		LiferayBatchFileProperties batchFileProperties =
			new LiferayBatchFileProperties(
				_testTemporaryDirectory.getAbsolutePath() + "/testOutput.jsonl",
				entitySchema, "batchFileProperties", oasJsonObject);

		LiferayMockRuntimeContainer liferayMockRuntimeContainer =
			new LiferayMockRuntimeContainer(batchFileProperties);

		LiferayBatchFileWriter liferayBatchFileWriter =
			new LiferayBatchFileWriter(
				new LiferayBatchFileWriteOperation(null),
				liferayMockRuntimeContainer);

		liferayBatchFileWriter.open("testUid");

		IndexedRecord indexedRecord = createIndexedRecordFromFile(
			"bigdecimal_content.json", entitySchema);

		Schema.Field idField = entitySchema.getField("id");

		idField.pos();

		for (int i = 1; i < 10; i++) {
			indexedRecord.put(idField.pos(), Long.valueOf(i));

			liferayBatchFileWriter.write(indexedRecord);
		}

		liferayBatchFileWriter.close();
	}

	private File _testTemporaryDirectory;

}