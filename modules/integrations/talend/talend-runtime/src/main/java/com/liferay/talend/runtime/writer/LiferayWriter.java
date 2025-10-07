/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.writer;

import com.liferay.talend.avro.IndexedRecordJsonObjectConverter;
import com.liferay.talend.avro.JsonObjectIndexedRecordConverter;
import com.liferay.talend.properties.output.LiferayOutputProperties;
import com.liferay.talend.properties.resource.Operation;
import com.liferay.talend.runtime.LiferaySink;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.json.JsonObject;

import org.apache.avro.generic.IndexedRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.WriteOperation;
import org.talend.components.api.component.runtime.WriterWithFeedback;
import org.talend.daikon.exception.TalendRuntimeException;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 */
public class LiferayWriter
	implements WriterWithFeedback<Result, IndexedRecord, IndexedRecord> {

	public LiferayWriter(
		LiferayWriteOperation liferayWriteOperation,
		LiferayOutputProperties liferayOutputProperties) {

		_liferayWriteOperation = liferayWriteOperation;
		_liferayOutputProperties = liferayOutputProperties;

		_dieOnError = liferayOutputProperties.getDieOnError();
		_finalEndpointUrl = _getFinalEndpointUrl(liferayOutputProperties);
		_liferaySink = liferayWriteOperation.getSink();

		_indexedRecordJsonObjectConverter =
			new IndexedRecordJsonObjectConverter(
				_dieOnError,
				_liferayOutputProperties.resource.inboundSchemaProperties.
					schema.getValue(),
				_liferayOutputProperties.resource.rejectSchemaProperties.schema.
					getValue(),
				_result);
		_jsonObjectIndexedRecordConverter =
			new JsonObjectIndexedRecordConverter(
				_liferayOutputProperties.resource.outboundSchemaProperties.
					schema.getValue());
	}

	@Override
	public void cleanWrites() {
		_successWrites.clear();
		_indexedRecordJsonObjectConverter.clearFailedIndexedRecords();
	}

	@Override
	public Result close() {
		return _result;
	}

	public void doDelete(IndexedRecord indexedRecord) {
		JsonObject jsonObject = _liferaySink.doDeleteRequest(_getEndpointUrl());

		if (jsonObject == null) {
			_handleSuccessRecord(indexedRecord);

			return;
		}

		_handleSuccessRecord(
			_jsonObjectIndexedRecordConverter.toIndexedRecord(jsonObject));
	}

	public void doInsert(IndexedRecord indexedRecord) throws IOException {
		JsonObject jsonObject = _liferaySink.doPostRequest(
			_getEndpointUrl(),
			_indexedRecordJsonObjectConverter.toJsonValue(indexedRecord));

		if (jsonObject == null) {
			_handleSuccessRecord(indexedRecord);

			return;
		}

		_handleSuccessRecord(
			_jsonObjectIndexedRecordConverter.toIndexedRecord(jsonObject));
	}

	public void doReplace(IndexedRecord indexedRecord) throws IOException {
		JsonObject jsonObject = _liferaySink.doPutRequest(
			_getEndpointUrl(),
			_indexedRecordJsonObjectConverter.toJsonValue(indexedRecord));

		if (jsonObject == null) {
			_handleSuccessRecord(indexedRecord);

			return;
		}

		_handleSuccessRecord(
			_jsonObjectIndexedRecordConverter.toIndexedRecord(jsonObject));
	}

	public void doUpdate(IndexedRecord indexedRecord) throws IOException {
		JsonObject jsonObject = _liferaySink.doPatchRequest(
			_getEndpointUrl(),
			_indexedRecordJsonObjectConverter.toJsonValue(indexedRecord));

		if (jsonObject == null) {
			_handleSuccessRecord(indexedRecord);

			return;
		}

		_handleSuccessRecord(
			_jsonObjectIndexedRecordConverter.toIndexedRecord(jsonObject));
	}

	@Override
	public Iterable<IndexedRecord> getRejectedWrites() {
		return Collections.unmodifiableCollection(
			_indexedRecordJsonObjectConverter.getFailedIndexedRecords());
	}

	@Override
	public Iterable<IndexedRecord> getSuccessfulWrites() {
		return Collections.unmodifiableCollection(_successWrites);
	}

	@Override
	public WriteOperation<Result> getWriteOperation() {
		return _liferayWriteOperation;
	}

	@Override
	public void open(String uId) throws IOException {
	}

	@Override
	public void write(Object object) throws IOException {
		if (!_isIndexedRecord(object)) {
			return;
		}

		IndexedRecord indexedRecord = (IndexedRecord)object;

		cleanWrites();

		Operation operation = _liferayOutputProperties.getOperation();

		try {
			if (Operation.Delete == operation) {
				doDelete(indexedRecord);
			}
			else if (Operation.Insert == operation) {
				doInsert(indexedRecord);
			}
			else if (Operation.Replace == operation) {
				doReplace(indexedRecord);
			}
			else if (Operation.Update == operation) {
				doUpdate(indexedRecord);
			}
			else {
				_indexedRecordJsonObjectConverter.reject(
					indexedRecord,
					TalendRuntimeException.createUnexpectedException(
						"Unsupported write operation " + operation));
			}

			_result.totalCount++;
		}
		catch (Exception exception) {
			_indexedRecordJsonObjectConverter.reject(indexedRecord, exception);
		}
	}

	private String _getEndpointUrl() {
		if (_finalEndpointUrl != null) {
			return _finalEndpointUrl;
		}

		return _liferayOutputProperties.getEndpointUrl();
	}

	private String _getFinalEndpointUrl(
		LiferayOutputProperties liferayOutputProperties) {

		try {
			return liferayOutputProperties.getEndpointUrl();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_logger.isWarnEnabled()) {
				_logger.warn("Endpoint URL will be resolved dynamically");
			}
		}

		return null;
	}

	private void _handleSuccessRecord(IndexedRecord indexedRecord) {
		_result.successCount++;

		_successWrites.add(indexedRecord);
	}

	private boolean _isIndexedRecord(Object object) throws IOException {
		if (object instanceof IndexedRecord) {
			return true;
		}

		IllegalArgumentException illegalArgumentException =
			new IllegalArgumentException("Indexed record is null");

		if (object != null) {
			illegalArgumentException = new IllegalArgumentException(
				String.format(
					"Expected record instance of %s but actual instance " +
						"passed was %s",
					IndexedRecord.class, object.getClass()));
		}

		if (_dieOnError) {
			throw new IOException(illegalArgumentException);
		}

		if (_logger.isWarnEnabled()) {
			_logger.warn("Unable to process record", illegalArgumentException);
		}

		return false;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferayWriter.class);

	private final boolean _dieOnError;
	private final String _finalEndpointUrl;
	private final IndexedRecordJsonObjectConverter
		_indexedRecordJsonObjectConverter;
	private final JsonObjectIndexedRecordConverter
		_jsonObjectIndexedRecordConverter;
	private final LiferayOutputProperties _liferayOutputProperties;
	private final LiferaySink _liferaySink;
	private final LiferayWriteOperation _liferayWriteOperation;
	private final Result _result = new Result();
	private final List<IndexedRecord> _successWrites = new ArrayList<>();

}