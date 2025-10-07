/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.reader;

import com.liferay.talend.avro.JsonObjectIndexedRecordConverter;
import com.liferay.talend.common.headless.HeadlessUtil;
import com.liferay.talend.properties.input.LiferayInputProperties;
import com.liferay.talend.runtime.LiferaySource;
import com.liferay.talend.runtime.client.exception.ResponseContentClientException;

import java.io.IOException;

import java.net.URI;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.avro.generic.IndexedRecord;

import org.joda.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.components.api.component.runtime.Reader;
import org.talend.components.api.component.runtime.Result;
import org.talend.components.api.component.runtime.Source;
import org.talend.components.api.exception.ComponentException;

/**
 * @author Zoltán Takács
 * @author Igor Beslic
 */
public class LiferayReader implements Reader<IndexedRecord> {

	public LiferayReader(
		LiferaySource liferaySource,
		LiferayInputProperties liferayInputProperties) {

		_liferaySource = liferaySource;
		_liferayInputProperties = liferayInputProperties;

		_jsonObjectIndexedRecordConverter =
			new JsonObjectIndexedRecordConverter(
				liferayInputProperties.getOutboundSchema());
	}

	@Override
	public boolean advance() throws IOException {
		if (!_started) {
			throw new IllegalStateException("Reader was not started");
		}

		_currentItemIndex++;

		if (_currentItemIndex < _itemsJsonArray.size()) {
			_dataCount++;
			_hasMore = true;

			return true;
		}

		if (_currentPage >= _lastPage) {
			_hasMore = false;

			return false;
		}

		_currentPage++;

		try {
			_readEndpointJsonObject();
		}
		catch (ResponseContentClientException responseContentClientException) {
			if (responseContentClientException.getHttpStatus() == 404) {
				return false;
			}

			throw responseContentClientException;
		}

		if (_itemsJsonArray.size() <= 0) {
			_hasMore = false;

			return false;
		}

		_hasMore = true;

		_dataCount++;

		return true;
	}

	@Override
	public void close() {
	}

	@Override
	public IndexedRecord getCurrent() throws NoSuchElementException {
		if (!_started) {
			throw new NoSuchElementException("Reader was not started");
		}

		if (!_hasMore) {
			throw new NoSuchElementException(
				"Resource does not have more elements");
		}

		try {
			JsonValue currentJsonValue = getCurrentJsonValue();

			if (!(currentJsonValue instanceof JsonObject)) {
				throw new ComponentException(
					new IllegalArgumentException(
						"Expected json object instead of " +
							currentJsonValue.getClass()));
			}

			return _jsonObjectIndexedRecordConverter.toIndexedRecord(
				currentJsonValue.asJsonObject());
		}
		catch (Exception exception) {
			throw new ComponentException(exception);
		}
	}

	public JsonValue getCurrentJsonValue() throws NoSuchElementException {
		if (_currentItemIndex < _itemsJsonArray.size()) {
			return _itemsJsonArray.get(_currentItemIndex);
		}

		throw new NoSuchElementException(
			"JSON array does not have more elements");
	}

	@Override
	public Source getCurrentSource() {
		return _liferaySource;
	}

	@Override
	public Instant getCurrentTimestamp() throws NoSuchElementException {
		return _currentTimestamp;
	}

	@Override
	public Map<String, Object> getReturnValues() {
		Result result = new Result();

		result.totalCount = _dataCount;

		return result.toMap();
	}

	@Override
	public boolean start() throws IOException {
		if (_started) {
			throw new IllegalStateException("Reader has already started");
		}

		_currentPage = 1;
		_lastPage = Integer.MIN_VALUE;

		_readEndpointJsonObject();

		if (_itemsJsonArray.isEmpty()) {
			return false;
		}

		_dataCount = 0;
		_hasMore = true;
		_started = true;

		return true;
	}

	private Map<String, String> _getPageQueryParameters() {
		Map<String, String> parameters = new HashMap<>();

		parameters.put("page", String.valueOf(_currentPage));
		parameters.put(
			"pageSize",
			String.valueOf(_liferayInputProperties.getItemsPerPage()));

		return parameters;
	}

	private void _readEndpointJsonObject() throws IOException {
		URI resourceURI = HeadlessUtil.updateWithQueryParameters(
			_liferayInputProperties.getEndpointUrl(),
			_getPageQueryParameters());

		_currentItemIndex = 0;

		_currentTimestamp = Instant.now();

		LiferaySource liferaySource = (LiferaySource)getCurrentSource();

		if (_logger.isDebugEnabled()) {
			_logger.debug(
				"Started to process resources at entry point: " +
					resourceURI.toString());
		}

		JsonObject jsonObject = liferaySource.doGetRequest(
			resourceURI.toString());

		if (jsonObject == null) {
			throw new IOException(
				"Unable to get JSON object for resource at " +
					resourceURI.toASCIIString());
		}

		if (jsonObject.containsKey("page")) {
			if (jsonObject.containsKey("items")) {
				_itemsJsonArray = jsonObject.getJsonArray("items");

				if (jsonObject.containsKey("lastPage")) {
					_lastPage = jsonObject.getInt("lastPage");
				}

				return;
			}

			JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

			_itemsJsonArray = jsonArrayBuilder.build();

			_lastPage = 1;

			return;
		}

		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

		jsonArrayBuilder.add(jsonObject);

		_itemsJsonArray = jsonArrayBuilder.build();

		_lastPage = 1;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		LiferayReader.class);

	private transient int _currentItemIndex;
	private int _currentPage;
	private Instant _currentTimestamp = Instant.now();
	private int _dataCount;
	private boolean _hasMore;
	private transient JsonArray _itemsJsonArray;
	private final JsonObjectIndexedRecordConverter
		_jsonObjectIndexedRecordConverter;
	private int _lastPage;
	private final LiferayInputProperties _liferayInputProperties;
	private final Source _liferaySource;
	private boolean _started;

}