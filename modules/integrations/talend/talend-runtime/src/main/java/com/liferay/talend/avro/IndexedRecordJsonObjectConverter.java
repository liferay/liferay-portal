/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.avro;

import com.liferay.talend.avro.exception.ConverterException;
import com.liferay.talend.common.oas.OASExtensions;
import com.liferay.talend.common.schema.SchemaUtils;

import java.io.IOException;
import java.io.StringReader;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.talend.components.api.component.runtime.Result;
import org.talend.daikon.avro.AvroUtils;

/**
 * @author Igor Beslic
 */
public class IndexedRecordJsonObjectConverter extends RejectHandler {

	public IndexedRecordJsonObjectConverter(
		Boolean dieOnError, Schema schema, Schema rejectSchema, Result result) {

		super(dieOnError, new ArrayList<IndexedRecord>(), rejectSchema, result);

		_schema = schema;
	}

	public JsonValue toJsonValue(IndexedRecord indexedRecord)
		throws IOException {

		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

		Map<String, JsonObjectBuilder> nestedJsonObjectBuilders =
			new HashMap<>();

		for (Schema.Field field : _schema.getFields()) {
			String fieldName = field.name();

			Schema.Field dataField = SchemaUtils.getField(
				fieldName, indexedRecord.getSchema());

			if ((dataField == null) ||
				(indexedRecord.get(dataField.pos()) == null)) {

				if (!AvroUtils.isNullable(field.schema())) {
					reject(
						indexedRecord,
						new ConverterException(
							"Missing required field " + fieldName));
				}

				continue;
			}

			JsonObjectBuilder currentJsonObjectBuilder = jsonObjectBuilder;

			if (_isNestedFieldName(fieldName)) {
				String[] nameParts = _getNameParts(fieldName);

				if (nameParts.length > 2) {
					if (_logger.isWarnEnabled()) {
						_logger.warn(
							"Unable to map more than one child object {}",
							fieldName);
					}

					continue;
				}

				if (!nestedJsonObjectBuilders.containsKey(nameParts[0])) {
					nestedJsonObjectBuilders.put(
						nameParts[0], Json.createObjectBuilder());
				}

				currentJsonObjectBuilder = nestedJsonObjectBuilders.get(
					nameParts[0]);

				fieldName = nameParts[1];
			}

			Schema fieldSchema = AvroUtils.unwrapIfNullable(field.schema());

			int fieldPos = dataField.pos();

			if (AvroUtils.isSameType(fieldSchema, AvroUtils._boolean())) {
				currentJsonObjectBuilder.add(
					fieldName, (boolean)indexedRecord.get(fieldPos));
			}
			else if (AvroUtils.isSameType(fieldSchema, AvroUtils._bytes())) {
				Base64.Encoder encoder = Base64.getEncoder();

				currentJsonObjectBuilder.add(
					fieldName,
					encoder.encodeToString(
						(byte[])indexedRecord.get(fieldPos)));
			}
			else if (AvroUtils.isSameType(fieldSchema, AvroUtils._decimal())) {
				currentJsonObjectBuilder.add(
					fieldName, (BigDecimal)indexedRecord.get(fieldPos));
			}
			else if (AvroUtils.isSameType(fieldSchema, AvroUtils._double())) {
				currentJsonObjectBuilder.add(
					fieldName, (double)indexedRecord.get(fieldPos));
			}
			else if (AvroUtils.isSameType(fieldSchema, AvroUtils._float())) {
				currentJsonObjectBuilder.add(
					fieldName, (float)indexedRecord.get(fieldPos));
			}
			else if (AvroUtils.isSameType(fieldSchema, AvroUtils._int())) {
				currentJsonObjectBuilder.add(
					fieldName, (int)indexedRecord.get(fieldPos));
			}
			else if (AvroUtils.isSameType(fieldSchema, AvroUtils._long())) {
				if ((fieldSchema.getLogicalType() != null) &&
					(fieldSchema.getLogicalType() ==
						LogicalTypes.timestampMillis())) {

					currentJsonObjectBuilder.add(
						fieldName,
						_asISO8601String((long)indexedRecord.get(fieldPos)));

					continue;
				}

				currentJsonObjectBuilder.add(
					fieldName, (long)indexedRecord.get(fieldPos));
			}
			else if (AvroUtils.isSameType(fieldSchema, AvroUtils._string())) {
				String stringFieldValue = (String)indexedRecord.get(fieldPos);

				if (Objects.equals(field.getProp("oas.dictionary"), "true") ||
					Objects.equals(fieldSchema.getName(), "Dictionary") ||
					_isJsonObjectFormattedString(stringFieldValue) ||
					_isJsonArrayFormattedString(stringFieldValue)) {

					StringReader stringReader = new StringReader(
						stringFieldValue);

					JsonReader jsonReader = Json.createReader(stringReader);

					currentJsonObjectBuilder.add(
						fieldName, jsonReader.readValue());

					jsonReader.close();

					continue;
				}

				currentJsonObjectBuilder.add(fieldName, stringFieldValue);
			}
			else {
				reject(
					indexedRecord,
					new ConverterException(
						String.format(
							"Unable to convert field %s of type %s", fieldName,
							fieldSchema.getType())));
			}
		}

		for (Map.Entry<String, JsonObjectBuilder> nestedJsonObjectBuilder :
				nestedJsonObjectBuilders.entrySet()) {

			jsonObjectBuilder.add(
				nestedJsonObjectBuilder.getKey(),
				nestedJsonObjectBuilder.getValue());
		}

		if (!_isIterable()) {
			return jsonObjectBuilder.build();
		}

		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

		jsonArrayBuilder.add(jsonObjectBuilder);

		return jsonArrayBuilder.build();
	}

	private String _asISO8601String(long timeMills) {
		DateFormat dateFormat = ISO8601DateFormat._dateFormat;

		return dateFormat.format(new Date(timeMills));
	}

	private String[] _getNameParts(String fieldName) {
		String parentNamePart = fieldName;
		String i18nFieldName = null;

		if (_oasExtensions.isI18nFieldNameNested(fieldName)) {
			i18nFieldName = _oasExtensions.getI18nFieldName(fieldName);

			parentNamePart = fieldName.substring(
				0, fieldName.indexOf(i18nFieldName));
		}

		String[] nameParts = parentNamePart.split("_");

		if (i18nFieldName == null) {
			return nameParts;
		}

		nameParts = Arrays.copyOf(nameParts, nameParts.length + 1);

		nameParts[nameParts.length - 1] = i18nFieldName;

		return nameParts;
	}

	private boolean _isIterable() {
		Object iterablePropObject = _schema.getObjectProp("iterable");

		if (iterablePropObject == null) {
			return false;
		}

		return (Boolean)iterablePropObject;
	}

	private boolean _isJsonArrayFormattedString(String value) {
		if (value.startsWith("[") && value.endsWith("]")) {
			return true;
		}

		return false;
	}

	private boolean _isJsonObjectFormattedString(String value) {
		if (value.startsWith("{") && value.endsWith("}")) {
			return true;
		}

		return false;
	}

	private boolean _isNestedFieldName(String fieldName) {
		if ((!_oasExtensions.isI18nFieldNameNested(fieldName) &&
			 _oasExtensions.isI18nFieldName(fieldName)) ||
			_oasExtensions.isObjectDefinitionReferenceFieldName(fieldName) ||
			!fieldName.contains("_")) {

			return false;
		}

		return true;
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		IndexedRecordJsonObjectConverter.class);

	private static final OASExtensions _oasExtensions = new OASExtensions();

	private final Schema _schema;

	private static class ISO8601DateFormat {

		private static final DateFormat _dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'hh:mm:ss'Z'") {

			{
				setTimeZone(TimeZone.getTimeZone("UTC"));
			}
		};

	}

}