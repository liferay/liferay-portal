/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.writer;

import com.liferay.talend.avro.IndexedRecordJsonObjectConverter;
import com.liferay.talend.avro.exception.ConverterException;
import com.liferay.talend.common.schema.SchemaUtils;
import com.liferay.talend.common.schema.constants.BatchSchemaConstants;
import com.liferay.talend.properties.batch.LiferayBatchFileProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.Arrays;
import java.util.Collections;

import javax.json.JsonArray;
import javax.json.JsonValue;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.WriterWithFeedback;
import org.talend.components.api.container.RuntimeContainer;

/**
 * @author Igor Beslic
 */
public class LiferayBatchFileWriter
	implements WriterWithFeedback<Result, IndexedRecord, IndexedRecord> {

	public LiferayBatchFileWriter(
		WriteOperation<Result> writeOperation,
		RuntimeContainer runtimeContainer) {

		_writeOperation = writeOperation;

		Object componentData = runtimeContainer.getComponentData(
			runtimeContainer.getCurrentComponentId(),
			"COMPONENT_RUNTIME_PROPERTIES");

		if (!(componentData instanceof LiferayBatchFileProperties)) {
			throw new IllegalArgumentException(
				String.format(
					"Unable to locate %s in given runtime container",
					LiferayBatchFileProperties.class));
		}

		_liferayBatchFileProperties = (LiferayBatchFileProperties)componentData;
	}

	@Override
	public void cleanWrites() {
		_indexedRecordJsonObjectConverter.clearFailedIndexedRecords();
	}

	@Override
	public Result close() throws IOException {
		if (_outputStreamWriter != null) {
			_outputStreamWriter.flush();

			_outputStreamWriter.close();
		}

		return _result;
	}

	@Override
	public Iterable<IndexedRecord> getRejectedWrites() {
		return Collections.unmodifiableCollection(
			_indexedRecordJsonObjectConverter.getFailedIndexedRecords());
	}

	@Override
	public Iterable<IndexedRecord> getSuccessfulWrites() {
		return Collections.unmodifiableCollection(
			Arrays.asList(
				BatchSchemaConstants.asBatchSchemaIndexedRecord(
					_liferayBatchFileProperties.getBatchFilePath(),
					_liferayBatchFileProperties.getEntityClassName(),
					"unavailable")));
	}

	@Override
	public WriteOperation<Result> getWriteOperation() {
		return _writeOperation;
	}

	@Override
	public void open(String uId) throws IOException {
		_uId = uId;

		_result = new Result(_uId);

		Schema entitySchema = _liferayBatchFileProperties.getEntitySchema();

		_indexedRecordJsonObjectConverter =
			new IndexedRecordJsonObjectConverter(
				false, entitySchema,
				SchemaUtils.createRejectSchema(entitySchema), _result);

		_outputStreamWriter = new OutputStreamWriter(
			new FileOutputStream(
				new File(_liferayBatchFileProperties.getBatchFilePath())));
	}

	@Override
	public void write(Object object) throws IOException {
		if (!(object instanceof IndexedRecord)) {
			_result.rejectCount++;

			return;
		}

		IndexedRecord indexedRecord = (IndexedRecord)object;

		try {
			JsonValue jsonValue = _indexedRecordJsonObjectConverter.toJsonValue(
				indexedRecord);

			if (jsonValue instanceof JsonArray) {
				_result.rejectCount++;

				return;
			}

			_outputStreamWriter.write(jsonValue.toString());

			_outputStreamWriter.write(System.lineSeparator());

			_result.successCount++;

			if ((_result.successCount % _FLUSH_TRIGGER_WRITES) == 0) {
				_outputStreamWriter.flush();
			}
		}
		catch (ConverterException converterException) {
			_indexedRecordJsonObjectConverter.reject(
				indexedRecord, converterException);
		}

		_result.totalCount++;
	}

	private static final int _FLUSH_TRIGGER_WRITES = 20;

	private IndexedRecordJsonObjectConverter _indexedRecordJsonObjectConverter;
	private final LiferayBatchFileProperties _liferayBatchFileProperties;
	private OutputStreamWriter _outputStreamWriter;
	private Result _result;
	private String _uId;
	private final WriteOperation<Result> _writeOperation;

}