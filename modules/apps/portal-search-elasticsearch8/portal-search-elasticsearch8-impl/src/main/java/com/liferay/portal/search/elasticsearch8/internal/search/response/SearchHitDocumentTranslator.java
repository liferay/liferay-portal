/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.search.response;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch8.internal.util.JsonpUtil;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import java.util.Map;

/**
 * @author André de Oliveira
 */
public class SearchHitDocumentTranslator {

	public static final SearchHitDocumentTranslator INSTANCE =
		new SearchHitDocumentTranslator();

	public Document translate(Hit<JsonData> hit) {
		Document document = new DocumentImpl();

		Map<String, JsonData> jsonDatas = hit.fields();

		if (MapUtil.isNotEmpty(jsonDatas)) {
			for (String fieldName : jsonDatas.keySet()) {
				_addField(document, fieldName, jsonDatas);
			}
		}

		JsonData jsonData = hit.source();

		if (jsonData != null) {
			JsonValue jsonValue = jsonData.toJson(JsonpUtil.getJsonpMapper());

			JsonObject jsonObject = jsonValue.asJsonObject();

			jsonObject.keySet();

			for (String fieldName : jsonObject.keySet()) {
				if (document.getField(fieldName) != null) {
					continue;
				}

				_addFieldFromSource(document, fieldName, jsonObject);
			}
		}

		return document;
	}

	protected Field translate(String fieldName, JsonValue jsonValue) {
		JsonValue.ValueType valueType = jsonValue.getValueType();

		if (valueType == JsonValue.ValueType.ARRAY) {
			return new Field(
				fieldName, _toStringArray(jsonValue.asJsonArray()));
		}
		else if (valueType == JsonValue.ValueType.OBJECT) {
			JsonObject jsonObject = jsonValue.asJsonObject();

			return new Field(fieldName, new String[] {jsonObject.toString()});
		}
		else if (valueType == JsonValue.ValueType.STRING) {
			JsonString jsonString = (JsonString)jsonValue;

			return new Field(fieldName, jsonString.getString());
		}

		return new Field(fieldName, jsonValue.toString());
	}

	private SearchHitDocumentTranslator() {
	}

	private void _addField(
		Document document, String fieldName, Map<String, JsonData> jsonDatas) {

		Field field = _getField(fieldName, jsonDatas);

		if (field != null) {
			document.add(field);
		}
	}

	private void _addFieldFromSource(
		Document document, String fieldName, JsonObject jsonObject) {

		Field field = _getFieldFromSource(fieldName, jsonObject);

		if (field != null) {
			document.add(field);
		}
	}

	private Field _getField(String fieldName, Map<String, JsonData> jsonDatas) {
		if (_isInvalidFieldName(fieldName)) {
			return null;
		}

		JsonData jsonData = jsonDatas.get(fieldName);

		JsonValue jsonValue = jsonData.toJson(JsonpUtil.getJsonpMapper());

		if (jsonDatas.containsKey(fieldName.concat(".geopoint"))) {
			return _translateGeoPoint(fieldName, jsonValue);
		}

		return translate(fieldName, jsonValue);
	}

	private Field _getFieldFromSource(String fieldName, JsonObject jsonObject) {
		if (_isInvalidFieldName(fieldName)) {
			return null;
		}

		JsonValue jsonValue = jsonObject.get(fieldName);

		if (jsonObject.containsKey(fieldName.concat(".geopoint"))) {
			return _translateGeoPoint(fieldName, jsonValue);
		}

		return translate(fieldName, jsonValue);
	}

	private GeoLocationPoint _getGeoLocationPoint(JsonValue jsonValue) {
		JsonValue.ValueType valueType = jsonValue.getValueType();

		if (valueType == JsonValue.ValueType.OBJECT) {
			JsonObject jsonObject = jsonValue.asJsonObject();

			JsonArray jsonArray = jsonObject.getJsonArray("coordinates");

			JsonNumber latitudeJsonNumber = jsonArray.getJsonNumber(1);
			JsonNumber longitudeJsonNumber = jsonArray.getJsonNumber(0);

			return new GeoLocationPoint(
				latitudeJsonNumber.doubleValue(),
				longitudeJsonNumber.doubleValue());
		}

		String coordinates = jsonValue.toString();

		String[] coordinatesParts = coordinates.split(",");

		return new GeoLocationPoint(
			Double.valueOf(StringUtil.trim(coordinatesParts[0])),
			Double.valueOf(StringUtil.trim(coordinatesParts[1])));
	}

	private boolean _isInvalidFieldName(String fieldName) {
		if (fieldName.endsWith(".geopoint") || fieldName.equals("_ignored")) {
			return true;
		}

		return false;
	}

	private String[] _toStringArray(JsonArray jsonArray) {
		if (jsonArray == null) {
			return new String[0];
		}

		String[] values = new String[jsonArray.size()];

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonValue jsonValue = jsonArray.get(i);

			if (jsonValue.getValueType() == JsonValue.ValueType.NUMBER) {
				JsonNumber jsonNumber = (JsonNumber)jsonValue;

				values[i] = jsonNumber.toString();
			}
			else if (jsonValue.getValueType() == JsonValue.ValueType.STRING) {
				JsonString jsonString = (JsonString)jsonValue;

				values[i] = jsonString.getString();
			}
			else {
				values[i] = jsonValue.toString();
			}
		}

		return values;
	}

	private Field _translateGeoPoint(String fieldName, JsonValue jsonValue) {
		Field field = new Field(fieldName);

		JsonArray jsonArray = jsonValue.asJsonArray();

		field.setGeoLocationPoint(_getGeoLocationPoint(jsonArray.get(0)));

		return field;
	}

}